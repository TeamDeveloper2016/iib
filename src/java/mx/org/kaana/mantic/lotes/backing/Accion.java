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
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.lotes.reglas.Transaccion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.lotes.beans.Partida;
import mx.org.kaana.mantic.lotes.reglas.AdminLotes;
import org.primefaces.event.TabChangeEvent;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticLotesAccion")
@ViewScoped
public class Accion extends IBaseFilter implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 317393488565639367L;
	
	protected AdminLotes orden;	
	protected EAccion accion;	

	public String getAgregar() {
		return this.accion.equals(EAccion.AGREGAR)? "none": "";
	}

	public String getConsultar() {
		return this.accion.equals(EAccion.CONSULTAR)? "none": "";
	}

  public AdminLotes getOrden() {
    return orden;
  }

  public void setOrden(AdminLotes orden) {
    this.orden = orden;
  }

	@PostConstruct
  @Override
  protected void init() {		
    try {
      this.attrs.put("idArticuloTipo", 4L);      
			if(JsfBase.getFlashAttribute("accion")== null)
			  UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion   = JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("idLote", JsfBase.getFlashAttribute("idLote")== null? -1L: JsfBase.getFlashAttribute("idLote"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	@Override
  public void doLoad() {
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      switch (this.accion) {
        case AGREGAR:											
          this.orden= new AdminLotes();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          this.orden= new AdminLotes((Long)this.attrs.get("idLote"));
          break;
      } // switch
			this.toLoadCatalogos();
      this.toSumPartidas();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    try {			
			transaccion = new Transaccion(this.orden.getLote());
			if (transaccion.ejecutar(this.accion)) {
        regresar= this.doCancelar();
			} // if
			else 
				JsfBase.addMessage("Ocurri� un error al registrar la nota de entrada", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idLote", this.orden.getLote().getIdLote());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar

	private void toLoadCatalogos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      this.toLoadEmpresas();
      } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}
    
  private void toLoadEmpresas() {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      if(JsfBase.isAdminEncuestaOrAdmin())
			  params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      else
			  params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
 			List<UISelectEntity> empresas= UIEntity.build("TcManticEmpresasDto", "empresas", params, columns);
      this.attrs.put("empresas", empresas);
			if(!Objects.equals(empresas, null) && !empresas.isEmpty()) {
				if(Objects.equals(this.accion, EAccion.AGREGAR))
				  this.orden.getLote().setIkEmpresa(empresas.get(0));
				else {
					int index= empresas.indexOf(new UISelectEntity(this.orden.getLote().getIdEmpresa()));
					if(index>= 0)
					  this.orden.getLote().setIkEmpresa(empresas.get(index));
					else
  				  this.orden.getLote().setIkEmpresa(empresas.get(0));
				} // else	
      } // if  
      else 
 			  this.orden.getLote().setIkEmpresa(new UISelectEntity(-1L));
      this.doUpdateAlmacenes();
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
  
  public void doUpdateAlmacenes() {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
  	  params.put("sucursales", this.orden.getLote().getIdEmpresa());
 			List<UISelectEntity> almacenes= UIEntity.seleccione("TcManticAlmacenesDto", "origen", params, columns, "clave");
      this.attrs.put("almacenes", almacenes);
			if(!Objects.equals(almacenes, null) && !almacenes.isEmpty()) {
				if(this.accion.equals(EAccion.AGREGAR))
				  this.orden.getLote().setIkAlmacen(almacenes.get(0));
				else {
					int index= almacenes.indexOf(new UISelectEntity(this.orden.getLote().getIdAlmacen()));
					if(index>= 0)
					  this.orden.getLote().setIkAlmacen(almacenes.get(index));
					else
  				  this.orden.getLote().setIkAlmacen(almacenes.get(0));
				} // else	
			} // if
      else
			  this.orden.getLote().setIkAlmacen(new UISelectEntity(-1L));
      this.doUpdateArticulos();
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
  
	public void doUpdateArticulos() {
    List<Columna> columns         = new ArrayList<>();    
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> almacenes= (List<UISelectEntity>)this.attrs.get("almacenes");
    try {
      int index= almacenes.indexOf(this.orden.getLote().getIkAlmacen());
      if(index>= 0) 
        this.orden.getLote().setIkAlmacen(almacenes.get(index));
  		params.put("idLote", this.orden.getLote().getIdLote());
      params.put("idAlmacen", this.orden.getLote().getIdAlmacen()); 
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("articulo", EFormatoDinamicos.MAYUSCULAS));
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
 			List<UISelectEntity> articulos= UIEntity.seleccione("VistaLotesDto", "partidas", params, columns, "codigo");
      this.attrs.put("articulos", articulos);
			if(!Objects.equals(articulos, null) && !articulos.isEmpty()) {
				if(this.accion.equals(EAccion.AGREGAR))
				  this.orden.getLote().setIkArticulo(articulos.get(0));
				else {
					index= articulos.indexOf(new UISelectEntity(this.orden.getLote().getIdArticulo()));
					if(index>= 0)
					  this.orden.getLote().setIkArticulo(articulos.get(index));
					else
  				  this.orden.getLote().setIkArticulo(articulos.get(0));
				} // else	
      } // if  
      else 
 			  this.orden.getLote().setIkEmpresa(new UISelectEntity(-1L));
      this.doUpdateProductos();
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
  
	public void doUpdateProductos() {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {
  		params.put("idLote", this.orden.getLote().getIdLote());
  		params.put("idArticulo", this.orden.getLote().getIdArticulo());
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("articulo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("kilos", EFormatoDinamicos.MILES_CON_DECIMALES));
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.lazyModel = new FormatCustomLazy("VistaLotesDto", "productos", params, columns);
      UIBackingUtilities.resetDataTable();
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
  
	public void doTabChange(TabChangeEvent event) {
		// if(event.getTab().getTitle().equals("Importar") && this.attrs.get("faltantes")== null)
  }    
  
  @Override
  public void toSaveRecord() {
    Transaccion transaccion= null;
    try {			
			if (transaccion.ejecutar(this.accion)) {
 			  // UIBackingUtilities.execute("jsArticulos.back('guard\\u00F3 la nota de entrada', '"+ this.orden.getLote().getConsecutivo()+ "');");
				this.accion= EAccion.MODIFICAR;
				this.attrs.put("autoSave", Global.format(EFormatoDinamicos.FECHA_HORA, Fecha.getRegistro()));
			} // if	
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch    
  }
 
	public String toColor(Entity row) {
    Partida partida= new Partida(
      row.toLong("idNotaDetalle"), // Long idNotaDetalle, 
      row.toLong("idArticulo") // Long idArticulo            
    );
    int index= this.orden.getLote().getPartidas().indexOf(partida);
		return index>= 0? "janal-display-none": "";
	} 
 
  public void doAdd(Entity row) {
    Partida partida= new Partida(
      JsfBase.getIdUsuario(), // Long idUsuario, 
      row.toLong("idNotaDetalle"), // Long idNotaDetalle, 
      this.orden.getLote().getIdLote(), // Long idLote, 
      -1L, // Long idLoteDetalle, 
      row.toDouble("cantidad"), // Double cantidad, 
      row.toDouble("cantidad"), // Double saldo, 
      row.toLong("idArticulo") // Long idArticulo            
    );
    partida.setConsecutivo(row.toString("consecutivo"));
    partida.setCodigo(row.toString("codigo"));
    partida.setArticulo(row.toString("articulo"));
    partida.setProveedor(row.toString("proveedor"));
    int index= this.orden.getLote().getPartidas().indexOf(partida);
    if(index< 0)
      this.orden.getLote().getPartidas().add(partida);
    else {
      Partida item= this.orden.getLote().getPartidas().get(index);
      if(!Objects.equals(ESql.INSERT, item.getSql()))
        item.setSql(ESql.SELECT);
      else
        JsfBase.addMessage("El producto ya existe en la lista !", ETipoMensaje.INFORMACION);
    } // if
    this.attrs.put("partidas", this.orden.getLote().getPartidas().size());
    this.toSumPartidas();
  }
  
  public void doDelete(Partida row) {
    int index= this.orden.getLote().getPartidas().indexOf(row);
    if(index>= 0) {
      if(Objects.equals(ESql.INSERT, row.getSql()))
        this.orden.getLote().getPartidas().remove(index);
      else
        row.setSql(ESql.DELETE);
    } // if
    this.attrs.put("partidas", this.orden.getLote().getPartidas().size());
    this.toSumPartidas();
  }
  
  public void doRecover(Partida row) {
    if(Objects.equals(ESql.DELETE, row.getSql()))
      row.setSql(ESql.SELECT);
    this.toSumPartidas();
  }
  
  private void toSumPartidas() {
    Double sum= 0D;
    for (Partida item: this.orden.getLote().getPartidas()) {
      if(!Objects.equals(ESql.DELETE, item.getSql()))
        sum+= item.getCantidad();
    }
    this.attrs.put("total", "Suma de kilos: <strong>"+ Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, sum)+ "</strong>");
  }
  
	@Override
	protected void finalize() throws Throwable {
		try {
			this.doCancelar();
		} // try
		finally {
			super.finalize();
		} // finally	
	}
  
}