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
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.lotes.beans.Articulo;
import mx.org.kaana.mantic.lotes.beans.Lote;
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
  
  private Lote orden;
  private Entity lote;
	protected EAccion accion;	
  private Articulo articulo;

  public Lote getOrden() {
    return orden;
  }

  public void setOrden(Lote orden) {
    this.orden = orden;
  }

  public Entity getLote() {
    return lote;  
  }

  public Articulo getArticulo() {
    return articulo;
  }

  public void setArticulo(Articulo articulo) {
    this.articulo = articulo;
  }
  
  @PostConstruct
  @Override
  protected void init() {
    if(JsfBase.getFlashAttribute("accion")== null)
      UIBackingUtilities.execute("janal.isPostBack('cancelar')");
    this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.COPIAR: (EAccion)JsfBase.getFlashAttribute("accion");
    this.attrs.put("idLote", JsfBase.getFlashAttribute("idLote")== null? -1L: JsfBase.getFlashAttribute("idLote"));
    this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
    this.attrs.put("terminado", 0D);
    this.attrs.put("restos", 0D);
    this.articulo= new Articulo(-1L);
    this.doLoad();    
    this.toLoadProductos();
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
      switch(this.accion) {
        case CONSULTAR:
        case COPIAR:
          this.orden= (Lote)DaoFactory.getInstance().toEntity(Lote.class, "TcManticLotesDto", "igual", params);
          this.orden.toLoadArticulos();
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
 
  private void toLoadProductos() {
		List<Columna> columns         = new ArrayList<>();    
    Map<String, Object> params    = new HashMap<>();
    List<UISelectEntity> productos= null;
    try {      
      params.put("idTipoClase", this.lote.toLong("idTipoClase"));
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("articulo", EFormatoDinamicos.MAYUSCULAS));
      productos= (List<UISelectEntity>) UIEntity.seleccione("TcManticArticulosDto", "clase", params, columns, "codigo");
			this.attrs.put("productos", productos);
      if(productos!= null && !productos.isEmpty()) 
        this.articulo.setIkArticulo(productos.get(0));
    } // try
    catch (Exception e) {
			throw e;
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally      
  }
  
  public void doLoadPorcentajes() {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("sortOrder", "order by tc_mantic_notas_limpios.id_nota_limpio");
      params.put("idLote", this.lote.toLong("idLote"));
      this.orden.setUnidades((List<Unidad>)DaoFactory.getInstance().toEntitySet(Unidad.class, "VistaLotesDto", "limpios", params));
      if(!Objects.equals(this.orden.getUnidades(), null)) {
        for (Unidad item: this.orden.getUnidades()) {
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
        this.orden.setUnidades(new ArrayList<>());  
      this.doLoadTotal();
      this.attrs.put("articulos", this.orden.getUnidades().size());
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

  public void doUpdatePorcentaje(Unidad item) {
    if(Objects.equals(item.getSql(), ESql.SELECT))
      item.setSql(ESql.UPDATE);
    this.doLoadTotal();
  }
  
  public void doLoadTotal() {
    Double suma= 0D;
    for (Unidad item: this.orden.getUnidades()) 
      suma+= item.getPorcentaje();
    this.attrs.put("total", "Total: <strong>"+ suma+ "%</strong>");
    this.attrs.put("merma", suma);
    suma= 0D;
    for (Articulo item: this.orden.getArticulos()) 
      suma+= item.getCantidad();
    this.orden.setTerminado(suma);
    Double total= this.lote.toDouble("merma")+ this.orden.getTerminado()+ this.orden.getRestos();
    this.attrs.put("nuevo", Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, this.lote.toDouble("kilos")- total));
  }
 
  public void doAdd() {
    List<UISelectEntity> productos= (List<UISelectEntity>)this.attrs.get("productos");
    try {
      int index= productos.indexOf(this.articulo.getIkArticulo());
      if(index== 0) {
        this.articulo.setIkArticulo(productos.get(index));
        this.articulo.setCodigo(null);
        this.articulo.setArticulo(null);
      } // else  
      else  
        if(index> 0) {
          this.articulo.setIkArticulo(productos.get(index));
          this.articulo.setCodigo(productos.get(index).toString("codigo"));
          this.articulo.setArticulo(productos.get(index).toString("articulo"));
        } // if  
      index= this.orden.getArticulos().indexOf(this.articulo);
      if(index< 0)
        this.orden.getArticulos().add(this.articulo);
      else
        JsfBase.addMessage("El producto terminado ya esta en la lista !", ETipoMensaje.INFORMACION);
      this.toNewArticulo(this.articulo);
      this.doLoadTotal();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  protected void toNewArticulo(Articulo item) {
    List<UISelectEntity> productos= (List<UISelectEntity>)this.attrs.get("productos");
    try {      
      if(Objects.equals(item, null)) {
        this.articulo= new Articulo(-1L);
        if(!Objects.equals(productos, null)) {
          this.articulo.setIkArticulo(productos.get(0));
          this.articulo.setCodigo(productos.get(0).toString("codigo"));
          this.articulo.setArticulo(productos.get(0).toString("articulo"));
        } // if  
      } // if
      else
        this.articulo= item.clone();
      this.doLoadTotal();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }  

  public void doDelete(Articulo item) {
    try {   
      if(Objects.equals(item.getSql(), ESql.INSERT))
        this.orden.getArticulos().remove(item);
      else
        item.setSql(ESql.DELETE);
      this.doLoadTotal();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doRecover(Articulo item) {
    try {   
      item.setSql(ESql.UPDATE);
      this.doLoadTotal();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

  public void doUpdateImporte(Articulo item) {
    try {   
      item.setSql(ESql.UPDATE);
      this.doLoadTotal();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
}