package mx.org.kaana.mantic.inventarios.entradas.backing;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Cifrar;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEntrada;
import mx.org.kaana.mantic.inventarios.entradas.reglas.AdminNotas;
import mx.org.kaana.mantic.inventarios.entradas.reglas.Transaccion;
import mx.org.kaana.mantic.compras.ordenes.enums.EOrdenes;
import mx.org.kaana.mantic.comun.IBaseArticulos;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesComprasDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.db.dto.TcManticProveedoresDto;
import mx.org.kaana.mantic.db.dto.TrManticProveedorPagoDto;
import mx.org.kaana.mantic.inventarios.entradas.beans.Costo;
import mx.org.kaana.mantic.libs.factura.beans.Concepto;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.StreamedContent;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticInventariosEntradasAccion")
@ViewScoped
public class Accion extends IBaseArticulos implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 327393488565639367L;
	
	protected EAccion accion;	
	private EOrdenes tipoOrden;
	private boolean aplicar;
	private TcManticProveedoresDto proveedor;
	private Calendar fechaEstimada;
	private Costo costo;

	public String getValidacion() {
		return Objects.equals(this.tipoOrden, EOrdenes.NORMAL) || Objects.equals(this.tipoOrden, EOrdenes.ESPECIAL) || Objects.equals(this.tipoOrden, EOrdenes.TERMINADO)? "libre": "requerido";
	}
	
	public String getTitulo() {
		return Objects.equals(this.tipoOrden, EOrdenes.NORMAL)? "(DIRECTA)": Objects.equals(this.tipoOrden, EOrdenes.ESPECIAL)? "(ESPECIAL)":"";
	}

	public Boolean getIsDirecta() {
		return Objects.equals(this.tipoOrden, EOrdenes.NORMAL) || Objects.equals(this.tipoOrden, EOrdenes.ESPECIAL) || Objects.equals(this.tipoOrden, EOrdenes.TERMINADO);
	}

	public Boolean getIsAplicar() {
		Boolean regresar= true;
		try {
			regresar= JsfBase.isAdminEncuestaOrAdmin();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		return regresar;
	}
	
	public String getAgregar() {
		return this.accion.equals(EAccion.AGREGAR)? "none": "";
	}

	public String getConsultar() {
		return this.accion.equals(EAccion.CONSULTAR)? "none": "";
	}

	public TcManticProveedoresDto getProveedor() {
		return proveedor;
	}

	public Boolean getDiferente() {
	  return this.getEmisor()!= null && this.proveedor!= null &&	!this.getEmisor().getRfc().equals(this.proveedor.getRfc());
	}

  public Costo getCosto() {
    return costo;
  }

  public void setCosto(Costo costo) {
    this.costo = costo;
  }
	
  public List<Costo> getCostos() {
    return ((NotaEntrada)this.getAdminOrden().getOrden()).getCostos();
  }
  
  public void setCostos(List<Costo> costos) {
    ((NotaEntrada)this.getAdminOrden().getOrden()).setCostos(costos);
  }
  
	@PostConstruct
  @Override
  protected void init() {		
    try {
			this.attrs.put("idTipoComparacion", 1);
      this.attrs.put("idArticuloTipo", 4L);      
			this.aplicar= Boolean.FALSE;
			this.attrs.put("xcodigo", JsfBase.getFlashAttribute("xcodigo"));	
			if(JsfBase.getFlashAttribute("accion")== null && JsfBase.getParametro("zOyOxDwIvGuCt")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
			this.tipoOrden= JsfBase.getParametro("zOyOxDwIvGuCt")== null? EOrdenes.NORMAL: EOrdenes.valueOf(Cifrar.descifrar(JsfBase.getParametro("zOyOxDwIvGuCt")));
      this.accion   = JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("idNotaEntrada", JsfBase.getFlashAttribute("idNotaEntrada")== null? -1L: JsfBase.getFlashAttribute("idNotaEntrada"));
      this.attrs.put("idOrdenCompra", JsfBase.getFlashAttribute("idOrdenCompra")== null? -1L: JsfBase.getFlashAttribute("idOrdenCompra"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("isPesos", Boolean.FALSE);
			this.attrs.put("sinIva", Boolean.FALSE);
			this.attrs.put("buscaPorCodigo", Boolean.FALSE);
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_FACTURA);
      this.attrs.put("intercambiar", Boolean.FALSE);
			this.fechaEstimada= Calendar.getInstance();
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
          this.setAdminOrden(new AdminNotas(new NotaEntrada(-1L, (Long)this.attrs.get("idOrdenCompra")), this.tipoOrden));
          TcManticOrdenesComprasDto ordenCompra= this.attrs.get("idOrdenCompra").equals(-1L)? new TcManticOrdenesComprasDto(): (TcManticOrdenesComprasDto)DaoFactory.getInstance().findById(TcManticOrdenesComprasDto.class, (Long)this.attrs.get("idOrdenCompra"));
					if(Objects.equals(this.tipoOrden, EOrdenes.NORMAL) || Objects.equals(this.tipoOrden, EOrdenes.ESPECIAL)) {
						((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(new UISelectEntity(new Entity(-1L)));
						((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(new UISelectEntity(new Entity(-1L)));
					} // if
					else {
						((NotaEntrada)this.getAdminOrden().getOrden()).setIdEmpresa(ordenCompra.getIdEmpresa());
						((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(new UISelectEntity(new Entity(ordenCompra.getIdAlmacen())));
						((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(new UISelectEntity(new Entity(ordenCompra.getIdProveedor())));
						((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedorPago(new UISelectEntity(new Entity(ordenCompra.getIdProveedorPago())));
						this.fechaEstimada.setTimeInMillis(ordenCompra.getRegistro().getTime());
					} // else	
          break;
        case MODIFICAR:					
        case CONSULTAR:					
					NotaEntrada notaEntrada= (NotaEntrada)DaoFactory.getInstance().toEntity(NotaEntrada.class, "TcManticNotasEntradasDto", "detalle", this.attrs);
					this.tipoOrden         = Objects.equals(notaEntrada.getIdNotaTipo(), 1L)? EOrdenes.NORMAL: Objects.equals(notaEntrada.getIdNotaTipo(), 4L)? EOrdenes.ESPECIAL: Objects.equals(notaEntrada.getIdNotaTipo(), 6L)? EOrdenes.TERMINADO: EOrdenes.PROVEEDOR;
          this.setAdminOrden(new AdminNotas(notaEntrada, this.tipoOrden));
    			this.attrs.put("sinIva", this.getAdminOrden().getIdSinIva().equals(1L));
					
          // ESTO ES PARA CARGAR LOS ARTICULOS DE LA FACTURA CUANDO SE ENTRA POR LA OPCION DE MODIFICAR Y VUELVA A HACER LA COMPARACION DE LOS ARTICULOS
					this.doLoadFiles("TcManticNotasArchivosDto", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada(), "idNotaEntrada", (boolean)this.attrs.get("sinIva"), this.getAdminOrden().getTipoDeCambio());
					this.toPrepareDisponibles(false);
          break;
      } // switch
			this.attrs.put("paginator", this.getAdminOrden().getArticulos().size()> Constantes.REGISTROS_LOTE_TOPE);
			this.doResetDataTable();
			this.toLoadCatalogos();
			this.doFilterRows();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 

  public String doAplicar() {  
		this.aplicar= true;
		return this.doAceptar();
	}
	
  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    try {			
      if(Objects.equals(((NotaEntrada)this.getAdminOrden().getOrden()).getIdProveedor(), -1L))
        ((NotaEntrada)this.getAdminOrden().getOrden()).setIdProveedor(null);
      if(Objects.equals(((NotaEntrada)this.getAdminOrden().getOrden()).getIdProveedorPago(), -1L))
        ((NotaEntrada)this.getAdminOrden().getOrden()).setIdProveedorPago(null);
			((NotaEntrada)this.getAdminOrden().getOrden()).setDescuentos(this.getAdminOrden().getTotales().getDescuento());
			((NotaEntrada)this.getAdminOrden().getOrden()).setExcedentes(this.getAdminOrden().getTotales().getExtra());
			((NotaEntrada)this.getAdminOrden().getOrden()).setImpuestos(this.getAdminOrden().getTotales().getIva());
			((NotaEntrada)this.getAdminOrden().getOrden()).setSubTotal(this.getAdminOrden().getTotales().getSubTotal());
			((NotaEntrada)this.getAdminOrden().getOrden()).setTotal(this.getAdminOrden().getTotales().getTotal());
			this.getAdminOrden().toAdjustArticulos();
			transaccion = new Transaccion((NotaEntrada)this.getAdminOrden().getOrden(), this.getAdminOrden().getArticulos(), this.aplicar, this.getXml(), this.getPdf());
			if (transaccion.ejecutar(this.accion)) {
				if(Objects.equals(this.accion, EAccion.AGREGAR) || this.aplicar) 
  				regresar= this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);
				else
					this.getAdminOrden().toStartCalculate();
 				if(!this.accion.equals(EAccion.CONSULTAR)) 
  				JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR)? "agreg�": "modific�").concat(" la nota de entrada"), ETipoMensaje.INFORMACION);
 			  JsfBase.setFlashAttribute("retorno", this.attrs.get("retorno"));
  			JsfBase.setFlashAttribute("idNotaEntrada", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada());
        // ESTO ES PARA IRSE A LA PAGINA PARA CALENDARIZAR EL PAGO
        if(this.aplicar) {
          Long idEmpresaDeuda= this.toCheckCuentaPagar(((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada());
          if(!Objects.equals(idEmpresaDeuda, -1L) && !Objects.equals(((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaTipo(), 4L)) {
  			    regresar= "/Paginas/Mantic/Catalogos/Empresas/Cuentas/prorroga".concat(Constantes.REDIRECIONAR);
    			  JsfBase.setFlashAttribute("idEmpresaDeuda", idEmpresaDeuda);
          } // if
        } // if
			} // if
			else 
				JsfBase.addMessage("Ocurri� un error al registrar la nota de entrada", ETipoMensaje.ERROR);      			
      if(Objects.equals(((NotaEntrada)this.getAdminOrden().getOrden()).getIdProveedor(), null))
        ((NotaEntrada)this.getAdminOrden().getOrden()).setIdProveedor(-1L);
      if(Objects.equals(((NotaEntrada)this.getAdminOrden().getOrden()).getIdProveedorPago(), null))
        ((NotaEntrada)this.getAdminOrden().getOrden()).setIdProveedorPago(-1L);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idNotaEntrada", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada());
		JsfBase.setFlashAttribute("xcodigo", this.attrs.get("xcodigo"));	
		if(this.getAdminOrden()!= null && this.getAdminOrden().getOrden()!= null && ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada()<= 0L) {
			if(this.getXml()!= null && this.getXml().getRuta()!= null) {
			  File oldNameFile= new File(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.getXml().getRuta()).concat(this.getXml().getName()));
			  oldNameFile.delete();
			} // if	
			if(this.getPdf()!= null && this.getPdf().getRuta()!= null) {
			  File oldNameFile= new File(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.getPdf().getRuta()).concat(this.getPdf().getName()));
			  oldNameFile.delete();
			} // if	
		} // 
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar

	private void toLoadCatalogos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      this.toLoadEmpresas();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
		  params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			List<UISelectEntity> proveedores= UIEntity.seleccione("VistaOrdenesComprasDto", "moneda", params, columns, "clave");
      this.attrs.put("proveedores", proveedores);
			int index= 0;
			if(!proveedores.isEmpty()) {
				if(this.accion.equals(EAccion.AGREGAR) && ((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedor().getKey().equals(-1L)) {
			    ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(proveedores.get(0));
          ((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedor().getValue("clave").setData("TECLEAR PROVEEDOR");
        } // if  
				else {
				  index= proveedores.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedor());
				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(proveedores.get(index));
				} // else
        UISelectEntity temporal= proveedores.get(index);
				temporal.put("fechaEstimada", new Value("fechaEstimada", Global.format(EFormatoDinamicos.FECHA_CORTA, this.toCalculateFechaEstimada(this.fechaEstimada, temporal.toInteger("idTipoDia"), temporal.toInteger("dias")))));
		    this.attrs.put("proveedor", temporal);
			  this.proveedor= (TcManticProveedoresDto)DaoFactory.getInstance().findById(TcManticProveedoresDto.class, ((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedor().getKey());
  			if(this.proveedor!= null) 
  				this.toLoadCondiciones(new UISelectEntity(new Entity(this.proveedor.getIdProveedor())));
				if(this.accion.equals(EAccion.AGREGAR))
					this.doCalculateFechaPago();
			} // if	
			if(this.attrs.get("idOrdenCompra")!= null) {
        columns.add(new Columna("total", EFormatoDinamicos.MONEDA_SAT_DECIMALES));
        this.attrs.put("ordenes", UIEntity.build("VistaNotasEntradasDto", "ordenes", params, columns));
			  List<UISelectEntity> ordenes= (List<UISelectEntity>)this.attrs.get("ordenes");
			  if(!ordenes.isEmpty()) 
				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkOrdenCompra(ordenes.get(0));
			} // if	
      
      List<UISelectItem> costos= UISelect.build("TcManticTiposCostosDto", params, "descripcion", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("costos", costos);
      List<UISelectItem> contratistas= UISelect.seleccione("VistaOrdenesComprasDto", "moneda", params, "idKey|razonSocial", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("contratistas", contratistas);
      
      List<UISelectItem> productos= new ArrayList<>();
      productos.add(new UISelectItem(new Long("-1"), "SELECCIONE"));
      if(!Objects.equals(this.accion, EAccion.AGREGAR)) {
        for (Articulo articulo: this.getAdminOrden().getArticulos()) { 
          productos.add(new UISelectItem(articulo.getIdArticulo(), articulo.getCodigo().concat(" | ").concat(articulo.getNombre())));
        } // for
        if(productos.size()> 0)
          productos.remove(productos.size()- 1);
      } // if
      this.attrs.put("productos", productos);
      this.toNewCosto(null);
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

	public void doUpdateProveedor() {
		try {
			if(this.tipoOrden.equals(EOrdenes.PROVEEDOR)) {
				this.getAdminOrden().getArticulos().clear();
				this.getAdminOrden().toCalculate();
			} // if	
			List<UISelectEntity> proveedores= (List<UISelectEntity>)this.attrs.get("proveedores");
			UISelectEntity temporal= ((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedor();
			temporal= proveedores.get(proveedores.indexOf(temporal));
			temporal.put("fechaEstimada", new Value("fechaEstimada", this.toCalculateFechaEstimada(this.fechaEstimada, temporal.toInteger("idTipoDia"), temporal.toInteger("dias"))));
			this.attrs.put("proveedor", temporal);
			this.proveedor= (TcManticProveedoresDto)DaoFactory.getInstance().findById(TcManticProveedoresDto.class, temporal.getKey());
			this.toLoadCondiciones(proveedores.get(proveedores.indexOf((UISelectEntity)((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedor())));
			this.doUpdatePlazo();
			// this.doCalculateFechaPago();
			this.toCheckProveedor(true);
		}	
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	} 
	
	private String toCalculateFechaEstimada(Calendar fechaEstimada, int tipoDia, int dias) {
		fechaEstimada.set(Calendar.DATE, fechaEstimada.get(Calendar.DATE)+ dias);
		if(tipoDia== 2) {
			fechaEstimada.add(Calendar.DATE, ((int)(dias/5)* 2));
			int dia= fechaEstimada.get(Calendar.DAY_OF_WEEK);
			dias= dia== Calendar.SUNDAY? 1: dia== Calendar.SATURDAY? 2: 0;
			fechaEstimada.add(Calendar.DATE, dias);
		} // if
		return Global.format(EFormatoDinamicos.FECHA_CORTA, new Date(fechaEstimada.getTimeInMillis()));
	}

  private void toLoadCondiciones(UISelectEntity proveedor) {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
  		params.put("idProveedor", proveedor.getKey());
      this.toCheckCondicion(proveedor.getKey());
      this.attrs.put("condiciones", UIEntity.build("VistaOrdenesComprasDto", "condiciones", params, columns));
			List<UISelectEntity> condiciones= (List<UISelectEntity>) this.attrs.get("condiciones");
			if(!condiciones.isEmpty()) {
				if(this.accion.equals(EAccion.AGREGAR) && ((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago()== null)
				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(0));
				else {
					Entity entity= new UISelectEntity(new Entity(((NotaEntrada)this.getAdminOrden().getOrden()).getIdProveedorPago()));
					if(condiciones.indexOf(entity)>= 0)
				    ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(condiciones.indexOf(entity)));
 					else
  				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(0));
				} // if	
				((NotaEntrada)this.getAdminOrden().getOrden()).setDiasPlazo(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago().toLong("plazo")+ 1);
        ((NotaEntrada)this.getAdminOrden().getOrden()).setDescuento(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago().toString("descuento"));
				if(this.accion.equals(EAccion.AGREGAR))
          this.doUpdatePorcentaje();
			} // if
    } // try
    catch (Exception e) {
			Error.mensaje(e);
			//JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}

	public void doChangeAlmacen() {
		List<UISelectEntity> almacenes= (List<UISelectEntity>)this.attrs.get("almacenes");
		int index= almacenes.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkAlmacen());
		if(index>= 0) 
	  	((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(index));
		if(this.tipoOrden.equals(EOrdenes.ALMACEN)) {
  		this.getAdminOrden().getArticulos().clear();
			this.getAdminOrden().toCalculate();
		} // if	
	}
	
	public void doTabChange(TabChangeEvent event) {
		if(event.getTab().getTitle().equals("Importar") && this.attrs.get("faltantes")== null)
			this.doLoadFiles("TcManticNotasArchivosDto", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada(), "idNotaEntrada", (boolean)this.attrs.get("sinIva"), this.getAdminOrden().getTipoDeCambio());
	  else
  		if(event.getTab().getTitle().equals("Articulos")) {
				UIBackingUtilities.update("contenedorGrupos:sinIva");
				UIBackingUtilities.update("contenedorGrupos:paginator");
			} // if
      else
    		if(event.getTab().getTitle().equals("Gastos")) {
          this.doPrepare();
        } // if
	}
	
	public void doFileUpload(FileUploadEvent event) {
		this.attrs.put("relacionados", 0);
		if(this.proveedor!= null) {
			this.doFileUpload(event, ((NotaEntrada)this.getAdminOrden().getOrden()).getFechaFactura().getTime(), Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas"), this.proveedor.getClave(), (boolean)this.attrs.get("sinIva"), this.getAdminOrden().getTipoDeCambio());
			if(event.getFile().getFileName().toUpperCase().endsWith(EFormatos.XML.name())) {
				((NotaEntrada)this.getAdminOrden().getOrden()).setFactura(this.getFactura().getFolio());
				((NotaEntrada)this.getAdminOrden().getOrden()).setFechaFactura(Fecha.toDateDefault(this.getFactura().getFecha()));
				((NotaEntrada)this.getAdminOrden().getOrden()).setOriginal(Numero.toRedondearSat(Double.parseDouble(this.getFactura().getTotal())));
//				if(Objects.equals(this.tipoOrden, EOrdenes.NORMAL) || Objects.equals(this.tipoOrden, EOrdenes.ESPECIAL) || Objects.equals(this.tipoOrden, EOrdenes.TERMINADO)) {
//					int count= 0;
//					while(count< this.getAdminOrden().getArticulos().size() && this.getAdminOrden().getArticulos().size()> 1) {
//						if(this.getAdminOrden().getArticulos().get(count).getIdOrdenDetalle()== null)
//							this.getAdminOrden().getArticulos().remove(count);
//						count++;
//					} // while 
//				} // if
				this.toMoveSelectedProveedor();
				this.toPrepareDisponibles(true);
				this.doCheckFolio();
				this.doCalculatePagoFecha();
			} // if
		} // if
		else 
			JsfBase.addMessage("Se tiene que seleccionar un proveedor primero", ETipoMensaje.ALERTA);      			
	} // doFileUpload	
	
	private void toPrepareDisponibles(boolean checkItems) {
		List<Articulo> disponibles= new ArrayList<>();
		for (Articulo disponible : this.getAdminOrden().getArticulos()) {
			if(disponible.getIdOrdenDetalle()!= null && disponible.getIdOrdenDetalle()> 0L) {
			  disponible.setDisponible(true);
			  disponible.setOrigen("");
			} // if
			if(disponible.getIdArticulo()> -1L)
				disponibles.add(disponible);
		} // for
		Collections.sort(disponibles);
		this.attrs.put("disponibles", disponibles);
    this.toCheckArticulos(checkItems);
	}

	private boolean isEqualsItems(Integer idTipoComparacion, Articulo faltante, Articulo disponible) {
	  boolean regresar= false;
		switch(idTipoComparacion) {
			case 1: // COMPRAR CODIGO
				regresar= (faltante.getCodigo()!= null && disponible.getCodigo()!= null && faltante.getCodigo().length()> 0 && Cadena.toEqualsString(faltante.getCodigo(), disponible.getCodigo()));
				break;
			case 2: // COMPRAR AMBOS CODIGO Y NOMBRE
				regresar= (faltante.getCodigo()!= null && disponible.getCodigo()!= null && faltante.getCodigo().length()> 0 && Cadena.toEqualsString(faltante.getCodigo(), disponible.getCodigo())) || 
			            (faltante.getNombre()!= null && disponible.getOrigen()!= null && faltante.getNombre().length()> 0 && Cadena.toEqualsString(faltante.getNombre(), disponible.getOrigen()));
				break;
			case 3: // COMPRAR NOMBRE
				regresar= (faltante.getNombre()!= null && disponible.getOrigen()!= null && faltante.getNombre().length()> 0 && Cadena.toEqualsString(faltante.getNombre(), disponible.getOrigen()));
				break;
		} // switch
		return regresar;
	}
	
	private void toCheckArticulos(boolean checkItems) {
		Articulo faltante, disponible= null;
		int relacionados             = 0;
		Integer idTipoComparacion    = (Integer)this.attrs.get("idTipoComparacion");
		try {
		  List<Articulo> faltantes= (List<Articulo>)this.attrs.get("faltantes");
			int x= 0;
			while(faltantes!= null && x< faltantes.size()) {
				faltante= faltantes.get(x);
  		  List<Articulo> disponibles= (List<Articulo>)this.attrs.get("disponibles");
				int y= 0;
  			boolean found= false;
				while (y< disponibles.size()) {
					disponible= disponibles.get(y);
					if(this.isEqualsItems(idTipoComparacion, faltante, disponible)) {
						relacionados++;
  				  LOG.info(relacionados+ ".- Relacionados ["+ disponible.getCodigo()+ "] "+ disponible.getNombre());
						found= true;
      			faltantes.remove(faltante);
    			  disponibles.remove(disponible);
						if(checkItems)
    			    this.toMoveArticulo(disponible, faltante);
						disponible.setDisponible(false);
						break;
					} // if	
					else
					  y++;
				} // for
				// EL ARTICULO FUE BUSCADO POR CODIGO EN EL PROVEDOR
				if(!found) {
					x++;
   				LOG.info(x+ ".- NO ENCONTRADO ["+ faltante.getCodigo()+ "] "+ faltante.getNombre());
				} // if	
				// YA NO HAY MAS ARTICULOS QUE BUSCAR TODOS FUERON ASIGNADOS
				if(disponibles.isEmpty())
					break;
			} // for
		} // try
	  catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
		this.attrs.put("relacionados", relacionados);
	  this.toCheckProveedor(checkItems);
	}
	
	private void toCheckProveedor(boolean checkItems) {
		Articulo faltante         = null;
		int relacionados          = this.attrs.get("relacionados")== null? 0: (int)this.attrs.get("relacionados");
		Integer idTipoComparacion = (Integer)this.attrs.get("idTipoComparacion");
	  Map<String, Object> params= new HashMap<>();
		try {
			params.put("idProveedor", this.getAdminOrden().getIdProveedor());
		  List<Articulo> faltantes= (List<Articulo>)this.attrs.get("faltantes");
			int x= 0;
			while(faltantes!= null && x< faltantes.size()) {
				faltante= faltantes.get(x);
				switch(idTipoComparacion) {
			   case 1: // COMPRAR CODIGO
    			 params.put(Constantes.SQL_CONDICION, "upper(tc_mantic_articulos_codigos.codigo)= upper('"+ faltante.getCodigo()+"')");
					 break;
  			 case 2: // COMPRAR AMBOS CODIGO Y NOMBRE
    			 params.put(Constantes.SQL_CONDICION, "(upper(tc_mantic_articulos_codigos.codigo)= upper('"+ faltante.getCodigo()+"') or upper(tc_mantic_notas_detalles.origen)= upper('"+ faltante.getOrigen()+"'))");
					 break;
				 case 3: // COMPRAR NOMBRE
    			 params.put(Constantes.SQL_CONDICION, "upper(tc_mantic_notas_detalles.origen)= upper('"+ faltante.getOrigen()+"')");
					 break;
				 default:
				   params.put(Constantes.SQL_CONDICION, "tc_mantic_articulos_codigos.codigo= 'null'");
  			} // switch
				List<UISelectEntity> disponibles= UIEntity.build("VistaNotasEntradasDto", "proveedor", params, Collections.EMPTY_LIST); 
				if(disponibles!= null && !disponibles.isEmpty()) {
					relacionados++;
					faltantes.remove(faltante);
					if(checkItems) {
						disponibles.get(0).put("sat", new Value("sat", faltante.getSat()));
						disponibles.get(0).put("codigo", new Value("codigo", faltante.getCodigo()));
						disponibles.get(0).put("costo", new Value("costo", faltante.getCosto()));
						disponibles.get(0).put("cantidad", new Value("cantidad", faltante.getCantidad()));
						disponibles.get(0).put("descuento", new Value("descuento", faltante.getDescuento()));
						//disponibles.get(0).put("iva", new Value("iva", faltante.getIva()));
						disponibles.get(0).put("iva", new Value("iva", Numero.toRedondearSat(Constantes.PORCENTAJE_IVA* 100)));
						disponibles.get(0).put("unidadMedida", new Value("unidadMedida", faltante.getUnidadMedida()!= null? faltante.getUnidadMedida().toUpperCase(): ""));
						disponibles.get(0).put("origen", new Value("origen", faltante.getNombre()));
						disponibles.get(0).put("facturado", new Value("facturado", true));
						disponibles.get(0).put("disponible", new Value("disponible", false));
						disponibles.get(0).put("fabricante", new Value("fabricante", ""));
						this.attrs.put("encontrado", disponibles.get(0));
						this.attrs.put("omitirMensaje", disponibles.get(0).toLong("idArticulo"));
						this.doFindArticulo(this.getAdminOrden().getArticulos().size()- 1);
					} // if
				} // if	
				else
					x++;
			} // while
		} // try
	  catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch
		finally {
			Methods.clean(params);
		} // finally
		this.attrs.put("relacionados", relacionados);
	}
	
	@Override
	public void doFaltanteArticulo() {
		Articulo faltante  = (Articulo)this.attrs.get("faltante");
		Articulo disponible= (Articulo)this.attrs.get("disponible");
		try {
		  List<Articulo> faltantes= (List<Articulo>)this.attrs.get("faltantes");
   	  faltantes.remove(faltante);
		  List<Articulo> disponibles= (List<Articulo>)this.attrs.get("disponibles");
		  disponibles.remove(disponible);
			if(faltante!= null && disponible!= null)
			  this.toMoveArticulo(disponible, faltante);
		} // try
	  catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	}

	private void toMoveArticulo(Articulo disponible, Articulo faltante) {
 		disponible.setSat(faltante.getSat());
		disponible.setCodigo(faltante.getCodigo());
		disponible.setCosto(faltante.getCosto());
		disponible.setCantidad(faltante.getCantidad());
		disponible.setDescuento("0");
		disponible.setIva(faltante.getIva());
		disponible.setUnidadMedida(faltante.getUnidadMedida());
		disponible.setOrigen(faltante.getOrigen());
		disponible.setDisponible(false);
		this.getAdminOrden().toCalculate();
	}

	public void doCheckFolio() {
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("factura", ((NotaEntrada)this.getAdminOrden().getOrden()).getFactura());
			params.put("idProveedor", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdProveedor());
			params.put("idNotaEntrada", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada());
			int month= Calendar.getInstance().get(Calendar.MONTH);
			if(month<= 5) {
				params.put("inicio", Calendar.getInstance().get(Calendar.YEAR)+ "0101");
				params.put("termino", Calendar.getInstance().get(Calendar.YEAR)+ "0630");
			} // if
			else {
				params.put("inicio", Calendar.getInstance().get(Calendar.YEAR)+ "0701");
				params.put("termino", Calendar.getInstance().get(Calendar.YEAR)+ "1231");
			} // else
			Entity entity= (Entity)DaoFactory.getInstance().toEntity("TcManticNotasEntradasDto", "folio", params);
			if(entity!= null && entity.size()> 0) 
				UIBackingUtilities.execute("$('#contenedorGrupos\\\\:factura').val('');janal.show([{summary: 'Error:', detail: 'El folio ["+ ((NotaEntrada)this.getAdminOrden().getOrden()).getFactura()+ "] se registr� en la nota de entrada "+ entity.toString("consecutivo")+ ", el dia "+ Global.format(EFormatoDinamicos.FECHA_HORA, entity.toTimestamp("registro"))+ " hrs'}]);");
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	}
	
	public void doCalculateFechaPago() {
		Date fechaFactura= ((NotaEntrada)this.getAdminOrden().getOrden()).getFechaFactura();
		Calendar calendar= Calendar.getInstance();
		calendar.setTimeInMillis(fechaFactura.getTime());
		if(((NotaEntrada)this.getAdminOrden().getOrden()).getDiasPlazo()== null)
			((NotaEntrada)this.getAdminOrden().getOrden()).setDiasPlazo(1L);
		calendar.add(Calendar.DATE, ((NotaEntrada)this.getAdminOrden().getOrden()).getDiasPlazo().intValue()- 1);
		((NotaEntrada)this.getAdminOrden().getOrden()).setFechaPago(new Date(calendar.getTimeInMillis()));
	}

	public void doCalculatePagoFecha() {
		Date fechaFactura= ((NotaEntrada)this.getAdminOrden().getOrden()).getFechaFactura();
		Date fechaPago   = ((NotaEntrada)this.getAdminOrden().getOrden()).getFechaPago();
		((NotaEntrada)this.getAdminOrden().getOrden()).setDiasPlazo(new Long(Fecha.getBetweenDays(fechaFactura, fechaPago)));
	}

	public StreamedContent doFileDownload() {
		return this.doPdfFileDownload(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas"));
	}	
	
	public void doViewDocument() {
		this.doViewDocument(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas"));
	}

	public void doViewFile() {
		this.doViewFile(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas"));
	}

	@Override
	public void doUpdateArticulo(String codigo, Integer index) {
		super.doUpdateArticulo(codigo, index);
    this.doFilterRows();
	}
	
	@Override
	public void doDeleteArticulo(Integer index) {
    try {
      // QUITA DE LA LISTA EL PRODUCTO SELECCIONADO ANTES DE ELIMINARLO
      List<UISelectItem> productos= (List<UISelectItem>)this.attrs.get("productos");
      if(index< this.getAdminOrden().getArticulos().size()) {
        Articulo articulo= this.getAdminOrden().getArticulos().get(index);
        int x= 0;
        while(x< productos.size()) {
          UISelectItem item= productos.get(x);
          if(Objects.equals((Long)item.getValue(), articulo.getIdArticulo()))
            productos.remove(x);
          else
            x++;
        } // for
      } // if  
		  super.doDeleteArticulo(index);
      this.doFilterRows();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
	}

	@Override
  public void doFindArticulo(Integer index) {
    try {
		  super.doFindArticulo(index);
		  this.doFilterRows();
      // AGREGAR A LA LISTA EL PRODUCTO SELECCIONADO DESPUES DE AGREGARLO
      List<UISelectItem> productos= (List<UISelectItem>)this.attrs.get("productos");
      if(index< this.getAdminOrden().getArticulos().size()) {
        Articulo articulo= this.getAdminOrden().getArticulos().get(index);
        productos.add(new UISelectItem(articulo.getIdArticulo(), articulo.getPropio().concat(" | ").concat(articulo.getNombre())));
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
	}

	public void doUpdateRfc() {
		this.doUpdateRfc(this.proveedor);
	}

	public void doDblClickArticulo(SelectEvent event) {
		this.attrs.put("buscado", event.getObject());
		this.doFindOutArticulo();
	}
	
	public void doRowSelectArticulo(SelectEvent event) {
		this.attrs.put("buscado", event.getObject());
	}
	
	public void doFindOutArticulo() {
		Articulo faltante= (Articulo)this.attrs.get("faltante");
		Entity  buscado  = (Entity)this.attrs.get("buscado");
		if(faltante!= null) { 
			if(buscado== null) {
				FormatCustomLazy list= (FormatCustomLazy)this.attrs.get("lazyModel");
				List<Entity> items   = (List<Entity>)list.getWrappedData();
				if(items.size()> 0) {
					buscado= items.get(0);
					faltante.setIdArticulo(buscado.getKey());
				} // if	
			} // else
			else
				faltante.setIdArticulo(buscado.getKey());
      int position= this.getAdminOrden().getArticulos().indexOf(faltante);
			if(position< 0) {
  		  List<Articulo> faltantes= (List<Articulo>)this.attrs.get("faltantes");
     	  faltantes.remove(faltante);
			} // if
			if(buscado!= null) {
				buscado.put("sat", new Value("sat", faltante.getSat()));
				buscado.put("codigo", new Value("codigo", faltante.getCodigo()));
				buscado.put("costo", new Value("costo", faltante.getCosto()));
				buscado.put("cantidad", new Value("cantidad", faltante.getCantidad()));
				buscado.put("descuento", new Value("descuento", faltante.getDescuento()));
				//buscado.put("iva", new Value("iva", faltante.getIva()));
				buscado.put("iva", new Value("iva", Numero.toRedondearSat(Constantes.PORCENTAJE_IVA* 100)));
				buscado.put("unidadMedida", new Value("unidadMedida", faltante.getUnidadMedida()!= null? faltante.getUnidadMedida().toUpperCase(): ""));
				buscado.put("origen", new Value("origen", faltante.getNombre()));
				buscado.put("disponible", new Value("disponible", false));
				buscado.put("fabricante", new Value("fabricante", ""));
			} // if	
			this.attrs.put("encontrado", new UISelectEntity(buscado));
		} // if
	}

	private boolean toCheckCodigoBarras(Long idNotaEntrada) throws Exception {
		boolean regresar          = false;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idNotaEntrada", idNotaEntrada);
			Value value= DaoFactory.getInstance().toField("VistaNotasEntradasDto", "codigos", params, "total");
			if(value.getData()!= null)
			  regresar= value.toLong()> 0;
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	  return regresar;	
	}
	
	private Long toCheckCuentaPagar(Long idNotaEntrada) throws Exception {
		Long regresar             = -1L;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idNotaEntrada", idNotaEntrada);
			Value value= DaoFactory.getInstance().toField("TcManticEmpresasDeudasDto", "deuda", params, "idEmpresaDeuda");
			if(value.getData()!= null)
			  regresar= value.toLong();
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	  return regresar;	
	}
	
  public void doUpdatePlazo() {
		if(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago()!= null) {
			List<UISelectEntity> condiciones= (List<UISelectEntity>) this.attrs.get("condiciones");
			int index= condiciones.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago());
			if(index>= 0) {
        ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(index));
		  	((NotaEntrada)this.getAdminOrden().getOrden()).setDiasPlazo(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago().toLong("plazo")+ 1);
        this.doCalculateFechaPago();		
        ((NotaEntrada)this.getAdminOrden().getOrden()).setDescuento(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago().toString("descuento"));
			} // if
			else {
        ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedorPago(null);
		  	((NotaEntrada)this.getAdminOrden().getOrden()).setDiasPlazo(0L);
        ((NotaEntrada)this.getAdminOrden().getOrden()).setDescuento("0");
			} // else
			this.doUpdatePorcentaje();
		}
	}	
	
	public void doLoadXmlFile() {
		try {
			if(this.getXml()!= null) {
				String alias= Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.getXml().getRuta()).concat(this.getXml().getName());
				this.toReadFactura(new File(alias), (boolean)this.attrs.get("sinIva"), this.getAdminOrden().getTipoDeCambio());
				// VERIFICAR SI ES UNA NOTA DE ENTRADA DIRECTA Y CAMBIAR EL PROVEEDOR QUE SE TIENE POR EL QUE SE CARGANDO DE LA FACTURA 
				// EN CASO DE QUE NO EXISTA MANDAR UN MENSAJE DE QUE ESE PROVEEDOR NO EXISTE EN EL CATALGO DE PROVEEDORES PARA QUE SE AGREGUE
				this.toMoveSelectedProveedor();
				this.toPrepareDisponibles(true);
			} // if	
	  }	// try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
	}

	public void doAutoSaveOrden() {
	  this.toSaveRecord();	
	}
	
	@Override
	public void toSaveRecord() {
    Transaccion transaccion= null;
    try {			
			((NotaEntrada)this.getAdminOrden().getOrden()).setDescuentos(this.getAdminOrden().getTotales().getDescuento());
			((NotaEntrada)this.getAdminOrden().getOrden()).setExcedentes(this.getAdminOrden().getTotales().getExtra());
			((NotaEntrada)this.getAdminOrden().getOrden()).setImpuestos(this.getAdminOrden().getTotales().getIva());
			((NotaEntrada)this.getAdminOrden().getOrden()).setSubTotal(this.getAdminOrden().getTotales().getSubTotal());
			((NotaEntrada)this.getAdminOrden().getOrden()).setTotal(this.getAdminOrden().getTotales().getTotal());
			transaccion = new Transaccion(((NotaEntrada)this.getAdminOrden().getOrden()), this.getAdminOrden().getArticulos(), false, this.getXml(), this.getPdf());
			this.getAdminOrden().toAdjustArticulos();
			if (transaccion.ejecutar(this.accion)) {
 			  UIBackingUtilities.execute("jsArticulos.back('guard\\u00F3 la nota de entrada', '"+ ((NotaEntrada)this.getAdminOrden().getOrden()).getConsecutivo()+ "');");
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
		LOG.error("ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA ["+ isViewException+ "]");
		if(isViewException && this.getAdminOrden().getArticulos().size()> 0)
		  this.toSaveRecord();
    //UIBackingUtilities.execute("alert('ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA');");
	}
	
	private void doResetArticuloFromXmlFile(String origen) {
		Articulo item           = null;
		List<Articulo> faltantes= (List<Articulo>)this.attrs.get("faltantes");
		if(this.getFactura()!= null && faltantes!= null) {
			try {
				for (Concepto concepto: this.getFactura().getConceptos()) {
          if(origen.equals(concepto.getDescripcion())) {
						if(item== null) {
							item= new Articulo(
								(boolean)this.attrs.get("sinIva"), // sinIva
								this.getAdminOrden().getTipoDeCambio(), // tipoDeCambio
								concepto.getDescripcion(), // nombre
								concepto.getNoIdentificacion(), // codigo
								Numero.toRedondearSat((Double.parseDouble(concepto.getTraslado().getBase()))/ Double.parseDouble(concepto.getCantidad())), // costo
								"", // descuento,
								-1L, // idOrdenCompra
								"", // extras
								0D, // importe
								"", // propio
								Double.parseDouble(concepto.getTraslado().getTasaCuota())* 100, // iva
								0D, // totalImpuesto 
								Numero.toRedondearSat(Double.parseDouble(concepto.getTraslado().getBase())), // subTotal
								Double.parseDouble(concepto.getCantidad()), // cantidad
								-1L, // idOrdenDetalle 
								new Random().nextLong(), // idArticulo 
								0D, // totalDescuentos
								-1L, // idProveedor
								false, // ultimo
								false, // solicitado
								0D, // stock
								0D, // excedentes
								concepto.getClaveProdServ(), // sat
								concepto.getUnidad(), // unidadMedida
								2L, // idAplicar
								concepto.getDescripcion() // origen
							);
						} // if
						else {
							item.setCantidad(item.getCantidad()+ Double.parseDouble(concepto.getCantidad()));
							item.setCosto(Numero.toRedondearSat((item.getSubTotal()+ Double.parseDouble(concepto.getTraslado().getBase()))/ item.getCantidad()));
							item.setSubTotal(item.getSubTotal()+ Double.parseDouble(concepto.getTraslado().getBase()));
						} // else
					} // if
				} // for
				if(item!= null)
  				faltantes.add(0, item);
			} // try	
			catch (Exception e) {
				Error.mensaje(e);
				JsfBase.addMessageError(e);
			} // catch
	  } // if 
	}
	
  public void doDesasociarArticulo(Articulo row, Integer index) {
		if(!row.isDisponible()) {
 		  this.doResetArticuloFromXmlFile(row.getOrigen());
			if(row.getIdOrdenDetalle()!= null && row.getIdOrdenDetalle()> 0L) {
		    row.setDisponible(true);
		    row.setCodigo("");
		    row.setOrigen("");
			} // if
			else {
				this.doDeleteArticulo(index);
				UIBackingUtilities.execute("janal.reset();");
				UIBackingUtilities.execute("jsArticulos.move();");
			} // else	
		} // if
		else
			JsfBase.addMessage("El articulo aun no se encuentra asociado a una partida de la factura (XML) !", ETipoMensaje.ALERTA);
	}	

	@Override
	public void doSearchArticulo(Long idArticulo, Integer index) {
		this.attrs.put("idAlmacen", ((NotaEntrada)this.getAdminOrden().getOrden()).getIkAlmacen().getKey());
		super.doSearchArticulo(idArticulo, index);
	}

  private void toMoveSelectedProveedor() {
		UISelectEntity temporal   = (UISelectEntity)this.attrs.get("proveedor");
	  Map<String, Object> params= new HashMap<>();
		try {
			if(Objects.equals(this.tipoOrden, EOrdenes.NORMAL) || Objects.equals(this.tipoOrden, EOrdenes.ESPECIAL) || Objects.equals(this.tipoOrden, EOrdenes.TERMINADO)) {
			  this.getAdminOrden().toCalculate();
				if(temporal== null || !this.getEmisor().getRfc().equals(temporal.toString("rfc"))) {
					params.put("rfc", this.getEmisor().getRfc());
					params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
					TcManticProveedoresDto encontrado= (TcManticProveedoresDto)DaoFactory.getInstance().findFirst(TcManticProveedoresDto.class, "proveedor", params);
					if(encontrado!= null) {
						String newFileName= this.getXml().getRuta().replaceAll("/"+ (this.proveedor.getClave()!= null? this.proveedor.getClave().trim(): "NoDefinido")+ "/", "/"+ (encontrado.getClave()!= null? encontrado.getClave().trim(): "NoDefinido")+ "/");
						this.proveedor= encontrado;
						temporal= new UISelectEntity(new Entity(encontrado.getIdProveedor()));
						((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(temporal);
						List<UISelectEntity> proveedores= (List<UISelectEntity>)this.attrs.get("proveedores");						
            int index= proveedores.indexOf(temporal);
            if(index>= 0)
						  temporal= proveedores.get(index);
            else {
              proveedores= this.doCompleteProveedor(encontrado.getRfc());
              index= proveedores.indexOf(temporal);
              if(index>= 0)
                temporal= proveedores.get(index);
              else
                temporal= proveedores.get(0);
              ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(temporal);
            } // else  
						temporal.put("fechaEstimada", new Value("fechaEstimada", this.toCalculateFechaEstimada(this.fechaEstimada, temporal.toInteger("idTipoDia"), temporal.toInteger("dias"))));
						this.attrs.put("proveedor", temporal);
						this.toLoadCondiciones(temporal);
						this.doUpdatePlazo();
						this.toCheckProveedor(true);
						File oldFileName= new File(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.getXml().getRuta()).concat(this.getXml().getName()));
						FileInputStream source= new FileInputStream(oldFileName);
						File target= new File(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(newFileName).concat(this.getXml().getName()));
						Archivo.toWriteFile(target, source);
						oldFileName.delete();
						this.getXml().setRuta(newFileName);
					} // if	
					else
						JsfBase.addAlert("El proveedor no existe en el catalogo de proveedores,<br/>favor de agregarlo antes al cat�logo para generar la nota de entrada<br/><br/> RFC ["+ this.getEmisor().getRfc()+ "] ".concat(this.getEmisor().getNombre()).concat("<br/>"), ETipoMensaje.ALERTA);
				} // if
		  } // if
	  }	// try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally	
	}

	public void doPrepareDisponibles() {
		this.doLoadXmlFile();
	}

  @Override
	public List<UISelectEntity> doCompleteArticulo(String query) {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
		int buscarCodigoPor       = 2;
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			params.put("precioCliente", "menudeo");
			params.put("idAlmacen", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdAlmacen());
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getDependencias());
  		params.put("idProveedor", this.attrs.get("proveedor")== null? new UISelectEntity(new Entity(-1L)): ((UISelectEntity)this.attrs.get("proveedor")).getKey());
			params.put("idCliente", -1L);
			String search= query; 
			if(!Cadena.isVacio(search)) {
				if((boolean)this.attrs.get("buscaPorCodigo"))
			    buscarCodigoPor= 1;
				if(search.startsWith("."))
					buscarCodigoPor= 1;
				else 
					if(search.startsWith(":"))
						buscarCodigoPor= 0;
				if(search.startsWith(".") || search.startsWith(":"))
					search= search.trim().substring(1);				
				search= search.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*");
        if(Cadena.isVacio(search))
          search= ".*";
			} // if	
			else
				search= "WXYZ";
  		params.put("codigo", search);	
  		params.put("idArticuloTipo", this.attrs.get("idArticuloTipo"));	
			switch(buscarCodigoPor) {      
				case 0: 
					this.attrs.put("articulos", (List<UISelectEntity>) UIEntity.build("VistaPrecioClienteDto", "porCodigoIgual", params, columns, 20L));
					break;
				case 1: 
					this.attrs.put("articulos", (List<UISelectEntity>) UIEntity.build("VistaPrecioClienteDto", "porCodigo", params, columns, 20L));
					break;
				case 2:
          this.attrs.put("articulos", (List<UISelectEntity>) UIEntity.build("VistaPrecioClienteDto", "porNombre", params, columns, 20L));
          break;
			} // switch
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
		return (List<UISelectEntity>)this.attrs.get("articulos");
	}	

  public void doAdd() {
    List<UISelectItem> proveedores= (List<UISelectItem>)this.attrs.get("contratistas");
    try {
      this.costo.setIdNotaEntrada(((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada());
      int index= proveedores.indexOf(new UISelectItem(this.costo.getIdProveedor()));
      if(index== 0) {
        this.costo.setIdProveedor(null);
        this.costo.setProveedor(null);
      } // else  
      else  
        if(index> 0)
          this.costo.setProveedor(proveedores.get(index).getLabel());
      if(Objects.equals(costo.getIdCuenta(), 2L)) {
        this.costo.setIdProveedor(null);
        this.costo.setProveedor(null);
        this.costo.setIdGenerar(2L);
      } // if 
      else 
        this.costo.setIdGenerar(1L);
      if(Objects.equals(costo.getIdArticulo(), -1L)) {
        costo.setIdArticulo(null);
        costo.setArticulo(null);
      } // if
      else {
        List<UISelectItem> productos= (List<UISelectItem>)this.attrs.get("productos");
        int x= 0;
        while(x< productos.size()) {
          UISelectItem item= productos.get(x);
          if(Objects.equals((Long)item.getValue(), costo.getIdArticulo()))
            costo.setArticulo(item.getLabel());
          x++;
        } // while
      } // if  
      ((NotaEntrada)this.getAdminOrden().getOrden()).add(costo);   
      this.getAdminOrden().getTotales().setGastos(((NotaEntrada)this.getAdminOrden().getOrden()).getCostos().size());
      this.toNewCosto(costo);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

  public void doPrepare() {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idTipoCosto", this.costo.getIdTipoCosto());      
      Entity item= (Entity)DaoFactory.getInstance().toEntity("TcManticTiposCostosDto", "igual", params);
      if(!Objects.equals(item, null) && !item.isEmpty()) {
        this.costo.setClave(item.toString("clave"));
        this.costo.setNombre(item.toString("descripcion"));
        this.costo.setIdCuenta(item.toLong("idCuenta"));
        if(Objects.equals(this.costo.getIdCuenta(), 1L)) {
          UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:idContratista', {validaciones: 'libre', mascara: 'libre', grupo: 'costo'});");
          UIBackingUtilities.execute("PF('contratista').enable()");
        } // if
        else {
          UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:idContratista', {validaciones: 'libre', mascara: 'libre', grupo: 'costo'});");
          UIBackingUtilities.execute("PF('contratista').disable()");
        } // else
      } // if
      else {
        UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:idContratista', {validaciones: 'libre', mascara: 'libre', grupo: 'costo'});");
        UIBackingUtilities.execute("PF('contratista').enable()");
      } //   
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }  

  protected void toNewCosto(Costo item) {
    List<UISelectItem> costos      = (List<UISelectItem>)this.attrs.get("costos");
    List<UISelectItem> productos   = (List<UISelectItem>)this.attrs.get("productos");
    List<UISelectItem> contratistas= (List<UISelectItem>)this.attrs.get("contratistas");
    try {      
      if(Objects.equals(item, null)) {
        this.costo= new Costo(-1L);
        if(!Objects.equals(costos, null)) 
          this.costo.setIdTipoCosto((Long)costos.get(0).getValue());
        if(!Objects.equals(contratistas, null)) 
          this.costo.setProveedor((String)contratistas.get(0).getLabel());
        this.costo.setIdArticulo((Long)productos.get(0).getValue());
      } // if
      else
        this.costo= item.clone();
      this.doPrepare();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }  
 
  public void doDelete(Costo costo) {
    try {   
      ((NotaEntrada)this.getAdminOrden().getOrden()).remove(costo);
      this.costo.setIdTipoCosto(costo.getIdTipoCosto());
      this.costo.setIdProveedor(costo.getIdProveedor());
      this.costo.setImporte(costo.getImporte());
      this.costo.setIdArticulo(costo.getIdArticulo());
      this.getAdminOrden().getTotales().setGastos(((NotaEntrada)this.getAdminOrden().getOrden()).getCostos().size());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doRecover(Costo costo) {
    try {   
      ((NotaEntrada)this.getAdminOrden().getOrden()).recover(costo);
      this.getAdminOrden().getTotales().setGastos(((NotaEntrada)this.getAdminOrden().getOrden()).getCostos().size());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

  public void doParamExpress() {
    
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
			if(!empresas.isEmpty() && Objects.equals(this.accion, EAccion.AGREGAR)) 
				if(((NotaEntrada)this.getAdminOrden().getOrden()).getIdEmpresa()== null)
				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkEmpresa(empresas.get(0));
				else {
					int index= empresas.indexOf(new UISelectEntity(((NotaEntrada)this.getAdminOrden().getOrden()).getIdEmpresa()));
					if(index>= 0)
					  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkEmpresa(empresas.get(index));
					else
  				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkEmpresa(empresas.get(0));
				} // else	
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
  	  params.put("idEmpresa", ((NotaEntrada)this.getAdminOrden().getOrden()).getIkEmpresa().getKey());
 			List<UISelectEntity> almacenes= UIEntity.build("TcManticAlmacenesDto", "origen", params, columns);
      this.attrs.put("almacenes", almacenes);
			if(!almacenes.isEmpty() && this.accion.equals(EAccion.AGREGAR)) 
				if(((NotaEntrada)this.getAdminOrden().getOrden()).getIdAlmacen()== null)
				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(0));
				else {
					int index= almacenes.indexOf(new UISelectEntity(((NotaEntrada)this.getAdminOrden().getOrden()).getIdAlmacen()));
					if(index>= 0)
					  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(index));
					else
  				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(0));
				} // else	
      this.doChangeAlmacen();
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
 
  private void toCheckCondicion(Long idProveedor) {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {
      if(!Objects.equals(idProveedor, null) && !Objects.equals(idProveedor, -1L)) {
        params.put("idProveedor", idProveedor);
        Entity value= (Entity)DaoFactory.getInstance().toEntity("VistaOrdenesComprasDto", "condiciones", params);
        if(Objects.equals(value, null) || value.isEmpty()) {
          TrManticProveedorPagoDto condicion= new TrManticProveedorPagoDto(
            -1L, // Long idProveedorPago, 
            idProveedor, // Long idProveedor, 
            "TRANSFERENCIA", // String clave, 
            2L, // Long idTipoPago, 
            JsfBase.getIdUsuario(), // Long idUsuario, 
            "0.00", // String descuento, 
            null, // String observaciones, 
            30L // Long plazo      
          );
          DaoFactory.getInstance().insert(condicion);
        } // if
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
  }
 
	public List<UISelectEntity> doCompleteProveedor(String codigo) {
 		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getDependencias());
			if(!Cadena.isVacio(codigo)) {
  			codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim();
				codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*");
			} // if	
			else
				codigo= "WXYZ";
 		  // params.put("codigo", codigo);
      params.put(Constantes.SQL_CONDICION, "(upper(razon_social) regexp '.*"+ codigo+ ".*' or upper(rfc) regexp '.*"+ codigo+ ".*')");
      this.attrs.put("proveedores", UIEntity.build("VistaOrdenesComprasDto", "moneda", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("proveedores");
	}	
 
  public char doGroupProveedor(UISelectEntity item) {
    return item.toString("rfc").charAt(0);
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