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
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.lotes.beans.Lote;
import mx.org.kaana.mantic.lotes.beans.Porcentaje;
import mx.org.kaana.mantic.lotes.beans.Unidad;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 21/06/2024
 *@time 23:19:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticLotesTerminado")
@ViewScoped
public class Terminado extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID= 127393488565639361L;
  
  private Entity lote;
	protected EAccion accion;	
  private List<Unidad> porcentajes;

  public Entity getLote() {
    return lote;  
  }

  public List<Unidad> getPorcentajes() {
    return porcentajes;
  }

  public void setPorcentajes(List<Unidad> porcentajes) {
    this.porcentajes = porcentajes;
  }

  @PostConstruct
  @Override
  protected void init() {
    if(JsfBase.getFlashAttribute("accion")== null)
      UIBackingUtilities.execute("janal.isPostBack('cancelar')");
    this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.TRANSFORMACION: (EAccion)JsfBase.getFlashAttribute("accion");
    this.attrs.put("idLote", JsfBase.getFlashAttribute("idLote")== null? -1L: JsfBase.getFlashAttribute("idLote"));
    this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
    this.attrs.put("terminado", 0D);
    this.attrs.put("restos", 0D);
    this.doLoad();    
  }
  
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {
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
        this.attrs.put("terminado", this.lote.toDouble("terminado"));
        this.attrs.put("restos", this.lote.toDouble("restos"));
        UIBackingUtilities.toFormatEntity(this.lote, columns);
        this.doLoadPorcentajes();
      } // if  
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
 
  public void doLoadPorcentajes() {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("sortOrder", "order by tc_mantic_notas_limpios.id_nota_limpio");
      params.put("idLote", this.lote.toLong("idLote"));
      this.porcentajes= (List<Unidad>)DaoFactory.getInstance().toEntitySet(Unidad.class, "VistaLotesDto", "limpios", params);
      if(!Objects.equals(this.porcentajes, null)) {
        for (Unidad item: this.porcentajes) {
          if(Objects.equals(item.getIdLoteLimpio(), -1L)) {
            item.setIdLote(this.lote.toLong("idLote"));
            item.setIdArticulo(this.lote.toLong("idArticulo"));
            item.setCantidad(0D);
            item.setPorcentaje(0D);
            item.setSql(ESql.INSERT);
          } // if  
          else
            item.setSql(ESql.SELECT);
        } // for
      } // if  
      else
        this.porcentajes= new ArrayList<>();  
      this.doLoadTotal();
      this.attrs.put("articulos", this.porcentajes.size());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  public String doAceptar() {  
    String regresar        = null;
    Transaccion transaccion= null;
    Map<String, Object> params= new HashMap<>();
		try {
      params.put("idLote", this.attrs.get("idLote"));
      Lote orden= (Lote)DaoFactory.getInstance().toEntity(Lote.class, "TcManticLotesDto", "igual", params);
      if(Objects.equals(orden.getIdTipoClase(), -1L))
        orden.setIdTipoClase(null);
      orden.setTerminado((Double)this.attrs.get("terminado"));
      orden.setRestos((Double)this.attrs.get("restos"));
			transaccion = new Transaccion(this.porcentajes, orden);
			if (transaccion.ejecutar(this.accion)) {
        regresar= this.doCancelar();
        JsfBase.addMessage("Se actualizaron los porcentajes", ETipoMensaje.INFORMACION);
      } // if
      else 
				JsfBase.addMessage("Ocurri� un error en los porcentajes", ETipoMensaje.ERROR);      			
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

  public void doUpdatePorcentaje(Unidad item) {
    if(Objects.equals(item.getSql(), ESql.SELECT))
      item.setSql(ESql.UPDATE);
    this.doLoadTotal();
  }
  
  public void doLoadTotal() {
    Double suma= 0D;
    for (Unidad item: this.porcentajes) {  
      suma+= item.getPorcentaje();
    } // for
    this.attrs.put("total", "Total: <strong>"+ suma+ "%</strong>");
    this.attrs.put("merma", suma);
    Double total= this.lote.toDouble("merma")+ (Double)this.attrs.get("terminado")+ (Double)this.attrs.get("restos");
    this.attrs.put("nuevo", Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, this.lote.toDouble("kilos")- total));
  }
  
}