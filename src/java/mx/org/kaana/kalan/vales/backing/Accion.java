package mx.org.kaana.kalan.vales.backing;

import com.google.common.base.Objects;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kalan.vales.beans.Vale;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.kalan.vales.reglas.Transaccion;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.kalan.vales.reglas.AdminVales;
import mx.org.kaana.mantic.comun.IBaseArticulos;
import mx.org.kaana.mantic.comun.IBaseStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TabChangeEvent;


@Named(value= "kalanValesAccion")
@ViewScoped
public class Accion extends IBaseArticulos implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 327391488565639367L;
	private EAccion accion;
	
	public String getAgregar() {
		return this.accion.equals(EAccion.AGREGAR)? "none": "";
	}
	
	@PostConstruct
  @Override
  protected void init() {		
    try {
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "/Paginas/Kalan/Vales/filtro": JsfBase.getFlashAttribute("retorno"));
			this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));      
//      if(JsfBase.getFlashAttribute("retorno")== null)
//				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("idVale", JsfBase.getFlashAttribute("idVale")== null? -1L: JsfBase.getFlashAttribute("idVale"));
			this.attrs.put("buscaPorCodigo", false);
			this.attrs.put("seleccionado", null);
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
      switch (this.accion) {
        case AGREGAR:											
          this.setAdminOrden(new AdminVales(new Vale(-1L)));
    			this.attrs.put("sinIva", false);
          break;
        case MODIFICAR:			
        case CONSULTAR:											
          this.setAdminOrden(new AdminVales((Vale)DaoFactory.getInstance().toEntity(Vale.class, "TcKalanValesDto", "detalle", this.attrs)));
    			this.attrs.put("sinIva", this.getAdminOrden().getIdSinIva().equals(1L));
          break;
      } // switch
			this.attrs.put("paginator", this.getAdminOrden().getArticulos().size()> Constantes.REGISTROS_LOTE_TOPE);
			this.doResetDataTable();
			this.toLoadCatalog();
			this.attrs.put("before", this.getAdminOrden().getIdAlmacen());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    try {			
			transaccion = new Transaccion(((Vale)this.getAdminOrden().getOrden()), this.getAdminOrden().getArticulos());
			this.getAdminOrden().toAdjustArticulos();
			if (transaccion.ejecutar(this.accion)) {
				if(this.accion.equals(EAccion.AGREGAR)) {
 				  regresar = this.doCancelar();
    			UIBackingUtilities.execute("jsArticulos.back('gener\\u00F3 un vale de almacen', '"+ ((Vale)this.getAdminOrden().getOrden()).getConsecutivo()+ "');");
				} // if	
				else
					this.getAdminOrden().toStartCalculate();
 				if(!this.accion.equals(EAccion.CONSULTAR)) 
    			JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el vale de almacen"), ETipoMensaje.INFORMACION);
  			JsfBase.setFlashAttribute("idVale", ((Vale)this.getAdminOrden().getOrden()).getIdVale());
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el vale de almacen", ETipoMensaje.ALERTA);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idVale", ((Vale)this.getAdminOrden().getOrden()).getIdVale());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } 

	private void toLoadCatalog() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns));
 			List<UISelectEntity> empresas= (List<UISelectEntity>)this.attrs.get("empresas");
			if(!empresas.isEmpty()) {
				this.attrs.put("idPedidoSucursal", empresas.get(0));
				if(this.accion.equals(EAccion.AGREGAR))
  				((Vale)this.getAdminOrden().getOrden()).setIkEmpresa(empresas.get(0));
			  else 
				  ((Vale)this.getAdminOrden().getOrden()).setIkEmpresa(empresas.get(empresas.indexOf(((Vale)this.getAdminOrden().getOrden()).getIkEmpresa())));
			} // if	
  		params.put("sucursales", ((Vale)this.getAdminOrden().getOrden()).getIkEmpresa());
      this.attrs.put("almacenes", UIEntity.seleccione("TcManticAlmacenesDto", "almacenes", params, columns, "clave"));
 			List<UISelectEntity> almacenes= (List<UISelectEntity>)this.attrs.get("almacenes");
			if(!almacenes.isEmpty()) {
				if(this.accion.equals(EAccion.AGREGAR))
				  ((Vale)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(0));
			  else
				  ((Vale)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(almacenes.indexOf(((Vale)this.getAdminOrden().getOrden()).getIkAlmacen())));
			} // if
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

	public void doUpdateAlmacen() {
		this.attrs.put("idAlmacen", ((Vale)this.getAdminOrden().getOrden()).getIkAlmacen().getKey());
		this.doLoadArticulos();
	}

	public void doTabChange(TabChangeEvent event) {
		if(event.getTab().getTitle().equals("Articulos")) {
			UIBackingUtilities.update("contenedorGrupos:sinIva");
			UIBackingUtilities.update("contenedorGrupos:paginator");
		} // if	
		else 
			if(event.getTab().getTitle().equals("Faltantes almacen")) 
        this.doLoadFaltantes();
	}
  
	@Override
  public void doFindArticulo(Integer index) {
		super.doFindArticulo(index);
		List<UISelectEntity> articulos= (List<UISelectEntity>)this.attrs.get("articulos");
		if(articulos!= null) {
		  int position= articulos.indexOf((UISelectEntity)this.attrs.get("articulo"));
		  if(position>= 0)
        this.attrs.put("seleccionado", articulos.get(position));
		} // if	
	}

	public void doAutoSaveOrden() {
	  this.toSaveRecord();	
	}
	
	@Override
	public void toSaveRecord() {
    Transaccion transaccion= null;
    try {			
			transaccion = new Transaccion(((Vale)this.getAdminOrden().getOrden()), this.getAdminOrden().getArticulos());
			this.getAdminOrden().toAdjustArticulos();
			if (transaccion.ejecutar(EAccion.MOVIMIENTOS)) {
   			UIBackingUtilities.execute("jsArticulos.back('guard\\u00F3 orden de compra', '"+ ((Vale)this.getAdminOrden().getOrden()).getConsecutivo()+ "');");
				this.accion= EAccion.MODIFICAR;
				this.getAdminOrden().getArticulos().add(new Articulo(-1L));
				this.attrs.put("autoSave", Global.format(EFormatoDinamicos.FECHA_HORA, Fecha.getRegistro()));
			} // if	
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
	}

	public void doGlobalEvent(Boolean isViewException) {
		LOG.error("ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA");
		if(isViewException && this.getAdminOrden().getArticulos().size()> 0)
		  this.toSaveRecord();
    //UIBackingUtilities.execute("alert('ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA');");
	}
	
	public void doLoadAlmacenes() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			params.put("sucursales", ((Vale)this.getAdminOrden().getOrden()).getIdEmpresa());
      this.attrs.put("almacenes", UIEntity.seleccione("TcManticAlmacenesDto", "almacenes", params, columns, "clave"));
 			List<UISelectEntity> almacenes= (List<UISelectEntity>)this.attrs.get("almacenes");
			if(!almacenes.isEmpty()) 
			  ((Vale)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(0));
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

  public void toSearchCodigos() {
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idProveedor", this.getAdminOrden().getIdProveedor());
			for (Articulo articulo: this.getAdminOrden().getArticulos()) {
				params.put("idArticulo", articulo.getIdArticulo());
				Value codigo= (Value)DaoFactory.getInstance().toField("TcManticArticulosCodigosDto", "codigo", params, "codigo");
				articulo.setModificado(codigo!= null? !Objects.equal(codigo.toString(), articulo.getCodigo()): !Cadena.isVacio(articulo.getCodigo()));
				articulo.setCodigo(codigo== null? "": codigo.toString());
			} // for
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	}	

	@Override
	public void doSearchArticulo(Long idArticulo, Integer index) {
		this.attrs.put("idAlmacen", ((Vale)this.getAdminOrden().getOrden()).getIkAlmacen().getKey());
		super.doSearchArticulo(idArticulo, index);
	}
	
	public void doCleanLookForFaltantes() {
		this.attrs.put("lookForFaltantes", "");
		this.doLoadFaltantes();
	} 
	
	public void doLookForFaltantes() {
		this.doLoadFaltantes();
	} 
	
  public void doLoadArticulos() {
		Map<String, Object> params= new HashMap<>();
		try {
			this.getAdminOrden().getArticulos().clear();
			this.getAdminOrden().getArticulos().add(new Articulo(-1L));
			this.getAdminOrden().toCalculate();
			UIBackingUtilities.execute("jsArticulos.update("+ (this.getAdminOrden().getArticulos().size()- 1)+ ");");
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} 

}