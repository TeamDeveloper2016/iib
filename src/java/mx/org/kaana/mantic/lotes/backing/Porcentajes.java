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
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.lotes.beans.Lote;
import mx.org.kaana.mantic.lotes.beans.Porcentaje;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 21/06/2024
 *@time 23:19:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticLotesPorcentajes")
@ViewScoped
public class Porcentajes extends IBaseAttribute implements Serializable {

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
    // if(JsfBase.getFlashAttribute("accion")== null)
    //  UIBackingUtilities.execute("janal.isPostBack('cancelar')");
    this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.TRANSFORMACION: (EAccion)JsfBase.getFlashAttribute("accion");
    this.attrs.put("idLote", JsfBase.getFlashAttribute("idLote")== null? 13L: JsfBase.getFlashAttribute("idLote"));
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
        case TRANSFORMACION:
          this.orden= (Lote)DaoFactory.getInstance().toEntity(Lote.class, "TcManticLotesDto", "igual", params);
          break;
      } // switch
      if(!Objects.equals(this.lote, null)) 
        UIBackingUtilities.toFormatEntity(this.lote, columns);
      this.doLoadPorcentajes();
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
      params.put("sortOrder", "order by tc_mantic_notas_calidades.id_nota_calidad");
      params.put("idLote", this.lote.toLong("idLote"));
      this.orden.setPorcentajes((List<Porcentaje>)DaoFactory.getInstance().toEntitySet(Porcentaje.class, "VistaLotesDto", "porcentajes", params));
      if(!Objects.equals(this.orden.getPorcentajes(), null)) {
        for (Porcentaje item: this.orden.getPorcentajes()) {
          if(Objects.equals(item.getIdLotePromedio(), -1L)) {
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
        this.orden.setPorcentajes(new ArrayList<>());  
      this.toLoadTotal();
      this.attrs.put("articulos", this.orden.getPorcentajes().size());
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
			transaccion = new Transaccion(this.orden);
			if (transaccion.ejecutar(this.accion)) {
        regresar= this.doCancelar();
        JsfBase.addMessage("Se actualizaron los porcentajes", ETipoMensaje.INFORMACION);
      } // if
      else 
				JsfBase.addMessage("Ocurrió un error en los porcentajes", ETipoMensaje.ERROR);      			
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

  public void doUpdatePorcentaje(Porcentaje item) {
    if(Objects.equals(item.getSql(), ESql.SELECT))
      item.setSql(ESql.UPDATE);
    this.toLoadTotal();
  }
  
  private void toLoadTotal() {
    double suma= 0D;
    for (Porcentaje item: this.orden.getPorcentajes()) 
      suma+= item.getPorcentaje();
    this.attrs.put("total", "Total: <strong>"+ suma+ "%</strong>");
    this.attrs.put("porcentaje", suma);
  }
  
}