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
import mx.org.kaana.mantic.lotes.beans.Kilo;
import mx.org.kaana.mantic.lotes.beans.Lote;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 21/06/2024
 *@time 23:19:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticLotesCalidad")
@ViewScoped
public class Calidad extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID= 127393488565639361L;
  
  private Lote orden;
  private Entity lote;
	protected EAccion accion;	

  public Lote getOrden() {
    return orden;
  }

  public void setOrden(Lote orden) {
    this.orden = orden;
  }
  
  public Entity getLote() {
    return lote;  
  }

  @PostConstruct
  @Override
  protected void init() {
    if(JsfBase.getFlashAttribute("accion")== null)
      UIBackingUtilities.execute("janal.isPostBack('cancelar')");
    this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.RESTAURAR: (EAccion)JsfBase.getFlashAttribute("accion");
    this.attrs.put("idLote", JsfBase.getFlashAttribute("idLote")== null? -1L: JsfBase.getFlashAttribute("idLote"));
    this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
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
      switch(this.accion) {
        case CONSULTAR:
        case RESTAURAR:
          this.orden= (Lote)DaoFactory.getInstance().toEntity(Lote.class, "TcManticLotesDto", "igual", params);
          break;
      } // switch
      if(!Objects.equals(this.lote, null)) 
        UIBackingUtilities.toFormatEntity(this.lote, columns);
      this.doLoadCantidades();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
  } 
 
  public void doLoadCantidades() {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("sortOrder", "order by tc_mantic_notas_mermas.id_nota_merma");
      params.put("idLote", this.lote.toLong("idLote"));
      this.orden.setCantidades((List<Kilo>)DaoFactory.getInstance().toEntitySet(Kilo.class, "VistaLotesDto", "cantidades", params));
      if(!Objects.equals(this.orden.getCantidades(), null)) {
        for (Kilo item: this.orden.getCantidades()) {
          if(Objects.equals(item.getIdLoteCalidad(), -1L)) {
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
        this.orden.setCantidades(new ArrayList<>());  
      this.toLoadTotal();
      this.attrs.put("articulos", this.orden.getCantidades().size());
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
		try {
			transaccion = new Transaccion(orden);
			if (transaccion.ejecutar(this.accion)) {
        regresar= this.doCancelar();
        JsfBase.addMessage("Se actualizó la información", ETipoMensaje.INFORMACION);
      } // if
      else 
				JsfBase.addMessage("Ocurrió un error en la información", ETipoMensaje.ERROR);      			
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

  public void doUpdateCantidad(Kilo item) {
    if(Objects.equals(item.getSql(), ESql.SELECT))
      item.setSql(ESql.UPDATE);
    this.toLoadTotal();
  }
  
  private void toLoadTotal() {
    Double suma= 0D;
    for (Kilo item: this.orden.getCantidades()) 
      suma+= item.getCantidad();
    this.attrs.put("total", "Total: <strong>"+ suma+ "</strong>");
    this.orden.setMerma(suma);
    this.attrs.put("nuevo", Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, this.lote.toDouble("kilos")- this.orden.getMerma()));
  }
  
}