package mx.org.kaana.mantic.ventas.caja.cierres.backing;

import java.io.Serializable;
import java.sql.Date;
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
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETipoMovimiento;
import mx.org.kaana.mantic.ventas.caja.cierres.beans.CorteCaja;
import mx.org.kaana.mantic.ventas.caja.cierres.reglas.CreateCorteCaja;
import org.primefaces.context.RequestContext;

@Named(value = "manticVentasCajaCierresFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428332L;
  
	private Reporte reporte;
  private FormatLazyModel lazyDetalle;

	public FormatLazyModel getLazyDetalle() {
		return lazyDetalle;
	}		
	
	public boolean getAdmin() {
		boolean regresar= false;
    try {
  	  regresar= JsfBase.isAdminEncuestaOrAdmin();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
    } // catch		
		return regresar;
	}
	
  public String getParticular() {
    String kilos= Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("particular")).toDouble("cantidad"));
    String total= Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("particular")).toDouble("importe"));
    return "Suma de kilos: <strong>"+ kilos+ "</strong> | importe: <strong>"+ total+ "</strong>";  
  }
  
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.attrs.put("idCierre", JsfBase.getFlashAttribute("idCierre"));
      this.attrs.put("idCierreAnterior", JsfBase.getFlashAttribute("idCierreAnterior"));
			this.toLoadCatalogos();
      if(this.attrs.get("idCierreAnterior")!= null && this.attrs.get("idCierre")!= null) 
			  this.doPrintCorte();
      if(this.attrs.get("idCierre")!= null) 
			  this.doLoad();
      this.attrs.put("particular", this.toEmptyTotales());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
 
  @Override
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= this.toPrepare();
    try {
      params.put("sortOrder", "order by tc_mantic_empresas.id_empresa, tc_mantic_cajas.id_caja, tc_mantic_cierres.registro desc ");
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombreEmpresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("usuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("caja", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("disponible", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("acumulado", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("saldo", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("capturado", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA));      
      this.lazyModel = new FormatCustomLazy("VistaCierresCajasDto", params, columns);
      UIBackingUtilities.resetDataTable();
			this.attrs.put("idCierre", null);
      this.lazyDetalle= null;
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

  public String doAccion(String accion) {
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("idCierre", ((Entity)this.attrs.get("seleccionado")).toLong("idCierre"));
			JsfBase.setFlashAttribute("idCaja", ((Entity)this.attrs.get("seleccionado")).toLong("idCaja"));
  		JsfBase.setFlashAttribute("idEmpresa", ((Entity)this.attrs.get("seleccionado")).toLong("idEmpresa"));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Mantic/Ventas/Caja/Cierres/accion".concat(Constantes.REDIRECIONAR);
  } // doAccion  
	
	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idCierre")) && !this.attrs.get("idCierre").toString().equals("-1"))
  		sb.append("(tc_mantic_cierres.id_cierre=").append(this.attrs.get("idCierre")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idDiferencia")) && !this.attrs.get("idDiferencia").toString().equals("-1"))
  		sb.append("(tc_mantic_cierres.id_diferencias =").append(this.attrs.get("consecutivo")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
		  sb.append("(date_format(tc_mantic_cierres.registro, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
		  sb.append("(date_format(tc_mantic_cierres.registro, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("idCaja")) && !this.attrs.get("idCaja").toString().equals("-1"))
  		sb.append("(tc_mantic_cajas.id_caja= ").append(this.attrs.get("idCaja")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idCierreEstatus")) && !this.attrs.get("idCierreEstatus").toString().equals("-1"))
  		sb.append("(tc_mantic_cierres.id_cierre_estatus= ").append(this.attrs.get("idCierreEstatus")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
		  regresar.put("idEmpresa", this.attrs.get("idEmpresa"));
		else
		  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	}
	
	private void toLoadCatalogos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("catalogo", (List<UISelectEntity>) UIEntity.build("TcManticCierresEstatusDto", "row", params, columns));
			this.attrs.put("idCierreEstatus", new UISelectEntity("-1"));
			this.toLoadEmpresas();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}
	
	private void toLoadEmpresas() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns));			
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
			this.doLoadCajas();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}
	
	public void doLoadCajas() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			params.put("idEmpresa", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
      this.attrs.put("cajas", (List<UISelectEntity>) UIEntity.build("TcManticCajasDto", "cajas", params, columns));
			this.attrs.put("idCaja", new UISelectEntity("-1"));
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}
	
	public void doReporte(String nombre) throws Exception{
    Parametros comunes           = null;
		Map<String, Object>params    = this.toPrepare();
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;
		try{		
      params.put("sortOrder", "order by tc_mantic_cajas.registro desc");
      reporteSeleccion= EReportes.valueOf(nombre);
      if(reporteSeleccion.equals(EReportes.CIERRES_CAJA))
        comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.reporte= JsfBase.toReporte();	
      parametros= comunes.getComunes();
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));			
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));		
      if(doVerificarReporte())
        this.reporte.doAceptar();			
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
  } // doReporte
  
  public boolean doVerificarReporte() {
    boolean regresar = Boolean.FALSE;
		RequestContext rc= UIBackingUtilities.getCurrentInstance();
		if(this.reporte.getTotal()> 0L){
			rc.execute("start(" + this.reporte.getTotal() + ")");		
      regresar = Boolean.TRUE;
    } // if
    else {
			rc.execute("generalHide();");		
			JsfBase.addMessage("Reporte", "No se encontraron registros para el reporte", ETipoMensaje.ERROR);
      regresar = Boolean.FALSE;
		} // else
    return regresar;
	} // doVerificarReporte	
	
	public void doLoadEstatus(){
		Entity seleccionado          = null;
		Map<String, Object>params    = new HashMap<>();
		List<UISelectItem> allEstatus= null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			params.put(Constantes.SQL_CONDICION, "id_cierre_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcManticCierresEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
			this.attrs.put("allEstatus", allEstatus);
			this.attrs.put("estatus", allEstatus.get(0));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadEstatus
	
	public String doMovimientos() {
		try {
			JsfBase.setFlashAttribute("tipo", ETipoMovimiento.CIERRES_CAJA);
			JsfBase.setFlashAttribute(ETipoMovimiento.CIERRES_CAJA.getIdKey(), ((Entity)this.attrs.get("seleccionado")).toLong("idCierre"));
			JsfBase.setFlashAttribute("regreso", "/Paginas/Mantic/Ventas/Caja/Cierres/filtro");
	  } // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		return "/Paginas/Mantic/Compras/Ordenes/movimientos".concat(Constantes.REDIRECIONAR);
	}

  public String doRetiros() {
		try {
			JsfBase.setFlashAttribute("idCierre", ((Entity)this.attrs.get("seleccionado")).toLong("idCierre"));
			JsfBase.setFlashAttribute("idEmpresa", ((Entity)this.attrs.get("seleccionado")).toLong("idEmpresa"));
			JsfBase.setFlashAttribute("idCaja", ((Entity)this.attrs.get("seleccionado")).toLong("idCaja"));
	  } // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		return "retiros".concat(Constantes.REDIRECIONAR);
	}	
	
  public String doFondoCaja() {
		try {
			JsfBase.setFlashAttribute("accion", EAccion.PROCESAR);
			JsfBase.setFlashAttribute("idCierre", ((Entity)this.attrs.get("seleccionado")).toLong("idCierre"));
			JsfBase.setFlashAttribute("idEmpresa", ((Entity)this.attrs.get("seleccionado")).toLong("idEmpresa"));
			JsfBase.setFlashAttribute("idCaja", ((Entity)this.attrs.get("seleccionado")).toLong("idCaja"));
	  } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
		} // catch
		return "fondo".concat(Constantes.REDIRECIONAR);
	}	

  public String doAperturarCaja() {
		try {
			JsfBase.setFlashAttribute("accion", EAccion.REGISTRAR);
	  } // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		return "apertura".concat(Constantes.REDIRECIONAR);
	}	
	
  public String doAbonos() {
		try {
			JsfBase.setFlashAttribute("idCierre", ((Entity)this.attrs.get("seleccionado")).toLong("idCierre"));
			JsfBase.setFlashAttribute("idEmpresa", ((Entity)this.attrs.get("seleccionado")).toLong("idEmpresa"));
			JsfBase.setFlashAttribute("idCaja", ((Entity)this.attrs.get("seleccionado")).toLong("idCaja"));
	  } // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		return "abonos".concat(Constantes.REDIRECIONAR);
	}	
	
  public String doVerAmbos() {
		try {
			JsfBase.setFlashAttribute("idCierre", ((Entity)this.attrs.get("seleccionado")).toLong("idCierre"));
			JsfBase.setFlashAttribute("idCierreEstatus", ((Entity)this.attrs.get("seleccionado")).toLong("idCierreEstatus"));
			JsfBase.setFlashAttribute("idEmpresa", ((Entity)this.attrs.get("seleccionado")).toLong("idEmpresa"));
			JsfBase.setFlashAttribute("idCaja", ((Entity)this.attrs.get("seleccionado")).toLong("idCaja"));
	  } // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		return "ambos".concat(Constantes.REDIRECIONAR);
	}	
	
	public String toColor(Entity row) {
		return row.toDouble("efectivo")> row.toDouble("limite")? "janal-tr-orange": "";
	} 
  
  public void doPrintCorte() {  
    CreateCorteCaja corte      = null;
    Long idCierre              = -1L;
    Long idCierreNuevo         = -1L;
    Map<String, Object> params = new HashMap<>();
    Entity seleccionado        = null;
    try {		
      if(this.attrs.get("seleccionado")!= null) {
        seleccionado= (Entity)this.attrs.get("seleccionado");
        idCierre    =  seleccionado.toLong("idCierre");
        params.put("idCaja", seleccionado.toLong("idCaja"));
        params.put("orden", seleccionado.toLong("orden"));
        idCierreNuevo = ((Entity)DaoFactory.getInstance().toEntity("VistaCierresCajasDto", "cierrePosterior", params)).toLong("idCierre");
      } // if
      else {
        idCierre     = (Long)this.attrs.get("idCierreAnterior"); // porque viene de regreso de fondo
        idCierreNuevo= (Long)this.attrs.get("idCierre");
      } // else
      CorteCaja corteCaja= new CorteCaja(idCierre, idCierreNuevo);			
      corte= new CreateCorteCaja(corteCaja);
      UIBackingUtilities.execute("jsTicket.imprimirTicket('" + corte.getPrincipal().getClave() + corte.getCorte().getResumenCorte().toString("consecutivo") +"','"  + corte.toHtml() + "');");
      UIBackingUtilities.execute("jsTicket.clicTicket();");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
			Methods.clean(params);
		} // finally
  } 

	public String doCorteCaja() {
		try {
			JsfBase.setFlashAttribute("idCierre", ((Entity)this.attrs.get("seleccionado")).toLong("idCierre"));
			JsfBase.setFlashAttribute("idEmpresa", ((Entity)this.attrs.get("seleccionado")).toLong("idEmpresa"));
			JsfBase.setFlashAttribute("idCaja", ((Entity)this.attrs.get("seleccionado")).toLong("idCaja"));
			JsfBase.setFlashAttribute("inicio", ((Entity)this.attrs.get("seleccionado")).toTimestamp("inicio"));
			JsfBase.setFlashAttribute("termino", ((Entity)this.attrs.get("seleccionado")).toTimestamp("termino"));
			JsfBase.setFlashAttribute("nombreEmpresa", ((Entity)this.attrs.get("seleccionado")).toString("nombreEmpresa"));
			JsfBase.setFlashAttribute("caja", ((Entity)this.attrs.get("seleccionado")).toString("caja"));
			JsfBase.setFlashAttribute("acumulado", ((Entity)this.attrs.get("seleccionado")).toDouble("acumulado"));
	  } // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		return "corte".concat(Constantes.REDIRECIONAR);
	}

  public void doConsultar() {
    this.doDetalle((Entity)this.attrs.get("seleccionado"));
  }
  
  public void doDetalle(Entity row) {
		Map<String, Object>params= new HashMap<>();
		List<Columna>columns     = new ArrayList<>();
		try {
			if(row!= null && !row.isEmpty()) {
        this.attrs.put("seleccionado", row);
        params.put("idCierre", row.toLong("idCierre"));
        params.put("sortOrder", "order by tc_mantic_ventas.registro desc");
        columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("ventas", EFormatoDinamicos.MILES_SIN_DECIMALES));
        columns.add(new Columna("saldo", EFormatoDinamicos.MILES_SIN_DECIMALES));
				columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
				this.lazyDetalle= new FormatLazyModel("VistaCierresCajasDto", "registro", params, columns);
				UIBackingUtilities.resetDataTable("tablaDetalle");
        this.attrs.put("particular", this.toTotales("VistaCierresCajasDto", "particular", params));
			} // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
  }

  private Entity toTotales(String proceso, String idXml, Map<String, Object> params) {
    Entity regresar= null;
    try {      
      regresar= (Entity)DaoFactory.getInstance().toEntity(proceso, idXml, params);
      if(Objects.equals(regresar, null) || regresar.isEmpty()) 
        regresar= this.toEmptyTotales();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return regresar;
  }
  
  private Entity toEmptyTotales() {
    Entity regresar= new Entity(-1L);
    regresar.put("cantidad", new Value("cantidad", 0D));
    regresar.put("importe", new Value("importe", 0D));
    regresar.put("ventas", new Value("ventas", 0D));
    return regresar;
  }

	public void doMoveSection(Entity row) {
		List<Columna> columns         = new ArrayList<>();
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> documento= null;
    try {
      this.attrs.put("seleccionado", row);
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cantidad", EFormatoDinamicos.NUMERO_CON_DECIMALES));
      columns.add(new Columna("impuestos", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("precio", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MONEDA_SAT_DECIMALES));
      columns.add(new Columna("fecha", EFormatoDinamicos.FECHA_HORA));
			params.put("idVenta", row.toLong("idVenta"));
			documento= (List<UISelectEntity>) UIEntity.build("VistaKardexDto", "venta", params, columns, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("documentos", documento);
			if(documento!= null && !documento.isEmpty()) {
				documento.get(0).put("articulos", new Value("articulos", documento.size()));
        this.attrs.put("documento", documento.get(0));
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
  
}
