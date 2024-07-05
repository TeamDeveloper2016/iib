package mx.org.kaana.mantic.lotes.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.mantic.lotes.reglas.Transaccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.lotes.beans.Lote;
import mx.org.kaana.mantic.lotes.beans.Partida;
import mx.org.kaana.mantic.lotes.enums.EEstatusLotes;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 21/06/2024
 *@time 23:19:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticLotesFraccionar")
@ViewScoped
public class Fraccionar extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID= 127393488565639361L;
  
  private Entity lote;
  private Lote orden;
	protected EAccion accion;	

  public Entity getLote() {
    return lote;  
  }

  public Lote getOrden() {
    return orden;
  }

  public void setOrden(Lote orden) {
    this.orden = orden;
  }
  
  @PostConstruct
  @Override
  protected void init() {
    if(JsfBase.getFlashAttribute("accion")== null)
      UIBackingUtilities.execute("janal.isPostBack('cancelar')");
    this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.GENERAR: (EAccion)JsfBase.getFlashAttribute("accion");
    this.attrs.put("idLote", JsfBase.getFlashAttribute("idLote")== null? -1L: JsfBase.getFlashAttribute("idLote"));
    this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
    this.doLoad();    
  }
  
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      params.put("sortOrder", "order by tc_mantic_lotes.registro");      
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      params.put("idLote", this.attrs.get("idLote"));      
      params.put(Constantes.SQL_CONDICION, "tc_mantic_lotes.id_lote= "+ this.attrs.get("idLote"));      
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));
      this.lote= (Entity)DaoFactory.getInstance().toEntity("VistaLotesDto", "lazy", params);
      if(!Objects.equals(this.lote, null)) {
        this.orden= new Lote(
          this.lote.toDouble("cantidad"), // Double original, 
          2L, // Long idLoteTipo, 
          null, // String nombre, 
          new Long(Fecha.getAnioActual()), // Long ejercicio, 
          this.lote.toString("consecutivo"), // String consecutivo, 
          JsfBase.getIdUsuario(), // Long idUsuario, 
          this.lote.toLong("idAlmacen"), // Long idAlmacen, 
          -1L, // Long idLote, 
          null, // String observaciones, 
          this.lote.toLong("idEmpresa"), // Long idEmpresa, 
          this.lote.toDouble("cantidad"), // Double cantidad, 
          1L,// Long orden, 
          this.lote.toLong("idArticulo"), // Long idArticulo
          EEstatusLotes.ELABORADO.getKey(), // Long idLoteEstatus     
          this.lote.toLong("idTipoArticulo"), // Long idTipoClase      
          0D, // merma
          0D, // terminado
          0D // restos
        );
        this.orden.setItArticulo(this.lote.toLong("idArticulo"));
        this.orden.setItTipoClase(this.lote.toLong("idTipoArticulo"));
        this.orden.setIkEmpresa(new UISelectEntity(this.lote.toLong("idEmpresa")));
        this.orden.setIkAlmacen(new UISelectEntity(this.lote.toLong("idAlmacen")));
        this.orden.setIkArticulo(new UISelectEntity(this.lote.toLong("idArticulo")));
        this.toLoadPartidas();
        UIBackingUtilities.toFormatEntity(this.lote, columns);
      } // if  
      else
        this.orden= new Lote(-1L);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
  } // doLoad
 
  public void toLoadPartidas() {
    try {      
      this.orden.toLoadPartidas();
      this.toLoadTotal();
      this.attrs.put("articulos", this.orden.getPartidas().size());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public String doAceptar() {  
    String regresar        = null;
    Transaccion transaccion= null;
    try {			
      this.orden.setCantidad((Double)this.attrs.get("importe"));
      this.orden.setOriginal((Double)this.attrs.get("importe"));
			transaccion = new Transaccion((Long)this.attrs.get("idLote"), this.orden);
			if (transaccion.ejecutar(this.accion)) {
        this.attrs.put("idLote", this.orden.getIdLote());
        regresar= this.doCancelar();
        JsfBase.addMessage("Se fraccionó el lote en uno nuevo", ETipoMensaje.INFORMACION);
      } // if
      else 
				JsfBase.addMessage("Ocurrió un error al fraccionar el lote", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    return regresar;
  } 

  public String doCancelar() {  
  	JsfBase.setFlashAttribute("idLote", this.attrs.get("idLote"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } 

  public void doUpdateCantidad(Partida item) {
    if(item.getCantidad()> item.getOriginal()) {
      item.setCantidad(item.getOriginal());
		  JsfBase.addMessage("La cantidad no puede ser mayor a "+ item.getOriginal(), ETipoMensaje.ERROR);      			
    } // if  
    this.toLoadTotal();
  }
  
  private void toLoadTotal() {
    double suma= 0D;
    for (Partida item: this.orden.getPartidas()) {  
      suma+= item.getCantidad();
    } // for
    this.attrs.put("total", "Total: <strong>"+ suma+ "</strong>");
    this.attrs.put("importe", suma);
    this.attrs.put("nuevo", Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, this.lote.toDouble("kilos")- suma));
  }
  
}