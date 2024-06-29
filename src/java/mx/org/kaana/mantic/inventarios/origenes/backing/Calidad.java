package mx.org.kaana.mantic.inventarios.origenes.backing;

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
import mx.org.kaana.mantic.inventarios.entradas.reglas.Transaccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.inventarios.origenes.beans.Porcentaje;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 21/06/2024
 *@time 23:19:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticInventariosOrigenesCalidad")
@ViewScoped
public class Calidad extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID= 327393488565639361L;
  
  private Entity nota;
  private List<UISelectEntity> detalle;
	protected EAccion accion;	
  private List<Porcentaje> porcentajes;

  public Entity getNota() {
    return nota;  
  }

  public List<UISelectEntity> getDetalle() {
    return detalle;
  }

  public List<Porcentaje> getPorcentajes() {
    return porcentajes;
  }

  public void setPorcentajes(List<Porcentaje> porcentajes) {
    this.porcentajes = porcentajes;
  }

  @PostConstruct
  @Override
  protected void init() {
    if(JsfBase.getFlashAttribute("accion")== null)
      UIBackingUtilities.execute("janal.isPostBack('cancelar')");
    this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.TRANSFORMACION: (EAccion)JsfBase.getFlashAttribute("accion");
    this.attrs.put("idNotaEntrada", JsfBase.getFlashAttribute("idNotaEntrada")== null? -1L: JsfBase.getFlashAttribute("idNotaEntrada"));
    this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
    this.doLoad();    
  }
  
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {
      params.put("sortOrder", "order by tc_mantic_notas_entradas.registro");      
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      params.put("idNotaEntrada", this.attrs.get("idNotaEntrada"));      
      params.put(Constantes.SQL_CONDICION, "tc_mantic_notas_entradas.id_nota_entrada= "+ this.attrs.get("idNotaEntrada"));      
      this.nota   = (Entity)DaoFactory.getInstance().toEntity("VistaNotasEntradasDto", "lazy", params);
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("promedio", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("gastos", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      params.put("sortOrder", "order by tc_mantic_notas_detalles.registro");
      this.detalle= UIEntity.build("TcManticNotasDetallesDto", "igual", params, columns);
      this.attrs.put("idNotaDetalle", UIBackingUtilities.toFirstKeySelectEntity(this.detalle));
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
    UISelectEntity articulo   = (UISelectEntity)this.attrs.get("idNotaDetalle");
    try {      
      if(!Objects.equals(this.detalle, null) && !this.detalle.isEmpty()) {
        int index= this.detalle.indexOf(articulo);
        if(index>= 0)
          articulo= this.detalle.get(index);
        else
          articulo= this.detalle.get(0);
        params.put("idNotaDetalle", articulo.toLong("idNotaDetalle"));
        this.porcentajes= (List<Porcentaje>)DaoFactory.getInstance().toEntitySet(Porcentaje.class, "VistaNotasEntradasDto", "porcentajes", params);
        if(!Objects.equals(this.porcentajes, null)) {
          for (Porcentaje item: this.porcentajes) {
            if(Objects.equals(item.getIdNotaPromedio(), -1L)) {
              item.setIdNotaEntrada(this.nota.toLong("idNotaEntrada"));
              item.setIdNotaDetalle(articulo.toLong("idNotaDetalle"));
              item.setIdArticulo(articulo.toLong("idArticulo"));
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
        this.toLoadTotal();
      } // if  
      else
        this.porcentajes= new ArrayList<>();  
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
    try {			
			transaccion = new Transaccion(this.porcentajes);
			if (transaccion.ejecutar(this.accion)) {
        // regresar= this.doCancelar();
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
  	JsfBase.setFlashAttribute("idNotaEntrada", this.attrs.get("idNotaEntrada"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } 

  public void doUpdatePorcentaje(Porcentaje item) {
    if(Objects.equals(item.getSql(), ESql.SELECT))
      item.setSql(ESql.UPDATE);
    this.toLoadTotal();
  }
  
  private void toLoadTotal() {
    double suma= 0D;
    for (Porcentaje item: this.porcentajes) {  
      suma+= item.getPorcentaje();
    } // for
    this.attrs.put("total", "Total: <strong>"+ suma+ "%</strong>");
  }
  
}