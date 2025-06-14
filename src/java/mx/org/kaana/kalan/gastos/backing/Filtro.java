package mx.org.kaana.kalan.gastos.backing;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletContext;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.kalan.db.dto.TcKalanGastosBitacoraDto;
import mx.org.kaana.kalan.gastos.beans.Gasto;
import mx.org.kaana.kalan.gastos.enums.EGastos;
import mx.org.kaana.kalan.gastos.reglas.AdminGasto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.kalan.gastos.reglas.Transaccion;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.archivo.Xls;
import mx.org.kaana.libs.archivo.Zip;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETipoMovimiento;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "kalanGastosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

	private static final long serialVersionUID=1361701967796774746L;
  private static final String FILE_COLUMN_XLS= "EMPRESA,CONSECUTIVO,CLASIFICACION,SUBCLASIFICACION,CONCEPTO,FECHA_APLICACION,TOTAL,ESTATUS,FECHA_REFERENCIA,REFERENCIA,PROVEEDOR,OBSERVACIONES,REGISTRO";
  
  private FormatLazyModel lazyDetalle;
  private Reporte reporte;
	
	public Reporte getReporte() {
		return reporte;
	}	

  public FormatLazyModel getLazyDetalle() {
    return lazyDetalle;
  }

  public Boolean getIsAdmin() {
		try {
      return JsfBase.isEncargado();
		} // try
		catch (Exception e) {			
			Error.mensaje(e);			
		} // catch		
    return Boolean.FALSE;
  }
  
  public String getGeneral() {
    String total= Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("total"));
    return "Suma total: <strong>"+ total+ "</strong>";  
  }
  
  public String getTotal() {
    double sum  = 0D;
    String value= null;
		try {
      if(!Objects.equals(this.lazyDetalle, null))
        for(Entity item: (List<Entity>)this.lazyDetalle.getWrappedData()) {
          value= item.toString("total");
          sum= sum+ Double.valueOf(Cadena.eliminar(value, ','));
        } // for  
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return Numero.formatear(Numero.MONEDA_CON_DECIMALES, Numero.toRedondearSat(sum));
  }
  
	public StreamedContent getArchivo() {
		StreamedContent regresar  = null;
		Xls xls                   = null;
		String template           = "GASTOS";
		Map<String, Object> params= this.toPrepare();
    List<Columna> columns     = new ArrayList<>();
		try {
      params.put("sortOrder", "order by tc_kalan_empresas_gastos.id_empresa, tc_kalan_empresas_gastos.consecutivo desc");
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("proveedor", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("fechaAplicacion", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("fechaReferencia", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));
      Modelo modelo  = new Modelo(params, "VistaEmpresasGastosDto", "lazy", template);
			String salida  = EFormatos.XLS.toPath().concat(Archivo.toFormatNameFile(template).concat(".")).concat(EFormatos.XLS.name().toLowerCase());
  		String fileName= JsfBase.getRealPath("").concat(salida);
      xls= new Xls(fileName, modelo, FILE_COLUMN_XLS, columns);	
			if(xls.procesar()) {
				Zip zip       = new Zip();
				String zipName= Archivo.toFormatNameFile(template).concat(".").concat(EFormatos.ZIP.name().toLowerCase());
				zip.setEliminar(true);
				zip.compactar(JsfBase.getRealPath("").concat(EFormatos.XLS.toPath()).concat(zipName), JsfBase.getRealPath("").concat(EFormatos.XLS.toPath()), "*".concat(template.concat(".").concat(EFormatos.XLS.name().toLowerCase())));
		    String contentType= EFormatos.ZIP.getContent();
        InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(EFormatos.XLS.toPath().concat(zipName));  
		    regresar          = new DefaultStreamedContent(stream, contentType, Archivo.toFormatNameFile(template).concat(".").concat(EFormatos.ZIP.name().toLowerCase()));
			} // if
      else 
        JsfBase.addMessage("Exportar", "No existen registros que exportar !");
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
    finally {
			Methods.clean(params);
      Methods.clean(columns);
    } // finally		
    return regresar;
  }	
  
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("idEmpresaGastoProcess", JsfBase.getFlashAttribute("idEmpresaGastoProcess"));
      this.attrs.put("idFuente", 2L);
      this.attrs.put("general", this.toEmptyTotales());
			this.toLoadCatalogos();
      if(this.attrs.get("idEmpresaGastoProcess")!= null) 
			  this.doLoad();
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
      params.put("sortOrder", "order by tc_kalan_empresas_gastos.id_empresa, tc_kalan_empresas_gastos.consecutivo desc");
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("proveedor", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("fechaAplicacion", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("fechaReferencia", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaEmpresasGastosDto", params, columns);
      this.attrs.put("general", this.toTotales("VistaEmpresasGastosDto", "general", params));
      UIBackingUtilities.resetDataTable();
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
    String regresar= null;
    try {
      regresar= this.doAccion(accion, "GASTOS_ADMINISTRATIVOS");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch  
    return regresar;
  }
  
  public String doAccion(String accion, String gasto) {
		String regresar    = "/Paginas/Kalan/Gastos/accion";
		Entity seleccionado= (Entity) this.attrs.get("seleccionado");
    EAccion eaccion    = null;
    EGastos egasto     = null; 
    Long idEmpresaGasto= -1L;
    Map<String, Object> params= new HashMap<>();
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
      if(eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR)) {
  			JsfBase.setFlashAttribute("idEmpresaGastoProcess", seleccionado.getKey());
        if(Objects.equals(seleccionado.toLong("idFuente"), 2L)) {
          params.put("idEmpresaGasto", seleccionado.getKey());
          Entity principal= (Entity)DaoFactory.getInstance().toEntity("VistaEmpresasGastosDto", "fuente", params);
          if(!Objects.equals(principal, null) && !principal.isEmpty()) 
            idEmpresaGasto= principal.getKey();
          else 
            idEmpresaGasto= seleccionado.getKey();
        } // if
        else
          idEmpresaGasto= seleccionado.getKey();
	      egasto= EGastos.fromOrdinal(seleccionado.toLong("idGastoClasificacion"));
      } // if  
      else
	      egasto= EGastos.valueOf(gasto.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("egasto", egasto);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Kalan/Gastos/filtro");		
			JsfBase.setFlashAttribute("idEmpresaGasto", idEmpresaGasto);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
    finally {
      Methods.clean(params);
    } // finally
		return regresar.concat(Constantes.REDIRECIONAR);
  } 
	
  public void doEliminar() {
		Transaccion transaccion   = null;
		Entity seleccionado       = (Entity) this.attrs.get("seleccionado");
    Map<String, Object> params= new HashMap<>();
		try {
      Long idEmpresaGasto= seleccionado.getKey();
      if(Objects.equals(seleccionado.toLong("idFuente"), 2L)) {
        params.put("idEmpresaGasto", seleccionado.getKey());
        Entity principal= (Entity)DaoFactory.getInstance().toEntity("VistaEmpresasGastosDto", "fuente", params);
        if(!Objects.equals(principal, null) && !principal.isEmpty()) 
          idEmpresaGasto= principal.getKey();
      } // if
      AdminGasto admin= new AdminGasto(idEmpresaGasto);
      Gasto gasto     = admin.getGasto();
      gasto.getParcialidades().forEach((item) -> {
        item.setSql(ESql.DELETE);
      }); // for
			transaccion     = new Transaccion(gasto);
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "El gasto se ha eliminado correctamente", ETipoMensaje.ERROR);
			else
				JsfBase.addMessage("Eliminar", "Ocurri� un error al eliminar el gasto", ETipoMensaje.ERROR);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
    finally {
      Methods.clean(params);
    } // finally
  } 

	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idEmpresaGastoProcess")) && !this.attrs.get("idEmpresaGastoProcess").toString().equals("-1")) {
  		sb.append("(tc_kalan_empresas_gastos.id_empresa_gasto=").append(this.attrs.get("idEmpresaGastoProcess")).append(") and ");
      this.attrs.put("idEmpresaGastoProcess", null);
    } // if  
		if(!Cadena.isVacio(this.attrs.get("consecutivo")))
  		sb.append("(tc_kalan_empresas_gastos.consecutivo= '").append(this.attrs.get("consecutivo")).append("') and ");
		if(!Cadena.isVacio(this.attrs.get("referencia")))
  		sb.append("(tc_kalan_empresas_gastos.referencia like '%").append(this.attrs.get("referencia")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
		  sb.append("(date_format(tc_kalan_empresas_gastos.fecha_aplicacion, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
		  sb.append("(date_format(tc_kalan_empresas_gastos.fecha_aplicacion, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("idProveedor")) && !this.attrs.get("idProveedor").toString().equals("-1"))
  		sb.append("(tc_mantic_proveedores.id_proveedor= ").append(this.attrs.get("idProveedor")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idGastoClasificacion")) && !this.attrs.get("idGastoClasificacion").toString().equals("-1"))
  		sb.append("(tc_kalan_empresas_gastos.id_gasto_clasificacion= ").append(this.attrs.get("idGastoClasificacion")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idGastoSubclasificacion")) && !this.attrs.get("idGastoSubclasificacion").toString().equals("-1"))
  		sb.append("(tc_kalan_empresas_gastos.id_gasto_subclasificacion= ").append(this.attrs.get("idGastoSubclasificacion")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idGastoEstatus")) && !this.attrs.get("idGastoEstatus").toString().equals("-1"))
  		sb.append("(tc_kalan_empresas_gastos.id_gasto_estatus= ").append(this.attrs.get("idGastoEstatus")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
		  regresar.put("idEmpresa", this.attrs.get("idEmpresa"));
		else
		  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
    regresar.put("idFuente", this.attrs.get("idFuente"));
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;
	}
	
	private void toParameters(Map<String, Object> parameters) {
    String inicio          = " - ";
    String termino         = " - ";
    String clasificacion   = "TODAS";
    String subClasificacion= "TODOS";
    String sucursal        = "TODAS";
		if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
		  inicio= Fecha.formatear(Fecha.FECHA_CORTA, (Date)this.attrs.get("fechaInicio"));	
		if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
		  termino= Fecha.formatear(Fecha.FECHA_CORTA, (Date)this.attrs.get("fechaTermino"));	
    parameters.put("REPORTE_PERIODO", inicio.concat("  AL  ").concat(termino));
		if(!Cadena.isVacio(this.attrs.get("idGastoClasificacion")) && !Objects.equals((Long)this.attrs.get("idGastoClasificacion"), -1L)) {
      List<UISelectItem> clasificaciones= (List<UISelectItem>)this.attrs.get("clasificaciones");
  		int index= clasificaciones.indexOf(new UISelectItem((Long)this.attrs.get("idGastoClasificacion")));
      if(index>= 0)
        clasificacion= clasificaciones.get(index).getLabel();
    } // if  
    parameters.put("REPORTE_CLASIFICACION", clasificacion);
		if(!Cadena.isVacio(this.attrs.get("idGastoSubclasificacion")) && !Objects.equals((Long)this.attrs.get("idGastoSubclasificacion"), -1L)) {
      List<UISelectItem> subClasificaciones= (List<UISelectItem>)this.attrs.get("subclasificaciones");
  		int index= subClasificaciones.indexOf(new UISelectItem((Long)this.attrs.get("idGastoSubclasificacion")));
      if(index>= 0)
        subClasificacion= subClasificaciones.get(index).getLabel();
    } // if  
    parameters.put("REPORTE_SUBCLASIFICACION", subClasificacion);
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !Objects.equals(((UISelectEntity)this.attrs.get("idEmpresa")).getKey(), -1L)) {
      List<UISelectEntity> sucursales= (List<UISelectEntity>)this.attrs.get("empresas");
  		int index= sucursales.indexOf((UISelectEntity)this.attrs.get("idEmpresa"));
      if(index>= 0)
        sucursal= sucursales.get(index).toString("nombre");
    } // if  
    parameters.put("REPORTE_SUCURSAL", sucursal);
	}
	
	private void toLoadCatalogos() {
		List<Columna> columns        = new ArrayList<>();
    Map<String, Object> params   = new HashMap<>();
    List<UISelectEntity> empresas= null;
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        empresas= (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave");
      else
        empresas= (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns);
      this.attrs.put("empresas", empresas);
  	  this.attrs.put("idEmpresa", UIBackingUtilities.toFirstKeySelectEntity(empresas));
			this.attrs.put("idProveedor", new UISelectEntity("-1"));
			columns.remove(0);
      this.attrs.put("catalogo", (List<UISelectEntity>) UIEntity.build("TcKalanGastosEstatusDto", "row", params, columns));
			this.attrs.put("idGastoEstatus", new UISelectEntity("-1"));
      this.toLoadClasificaciones();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}
  
  public void doReporte() throws Exception {
    Parametros comunes           = new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
		Map<String, Object>params    = null;
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = EReportes.GASTOS_DETALLE;
		try {		
      params= this.toPrepare();
      params.put("sortOrder", "order by tc_kalan_empresas_gastos.id_empresa, tc_kalan_empresas_gastos.consecutivo desc");
      this.reporte= JsfBase.toReporte();	
      parametros= comunes.getComunes();
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));			
      this.toParameters(parametros);
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));		
      this.doVerificarReporte();
      this.reporte.doAceptar();			
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
  } 
	
	public void doVerificarReporte() {
		RequestContext rc= UIBackingUtilities.getCurrentInstance();
		if(this.reporte.getTotal()> 0L)
			rc.execute("start(" + this.reporte.getTotal() + ")");		
		else{
			rc.execute("generalHide();");		
			JsfBase.addMessage("Reporte", "No se encontraron registros para el reporte", ETipoMensaje.ERROR);
		} // else
	} 		

	public void doLoadEstatus() {
		Entity seleccionado          = (Entity)this.attrs.get("seleccionado");
		Map<String, Object>params    = new HashMap<>();
		List<UISelectItem> allEstatus= null;
		try {
			params.put(Constantes.SQL_CONDICION, "id_gasto_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcKalanGastosEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
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
	} 
	
  public void doLoadDetalle(Entity row) {
    this.attrs.put("seleccionado", row);
    this.doLoadEstatus();
  }
          
	public void doActualizarEstatus() {
		Transaccion transaccion          = null;
		TcKalanGastosBitacoraDto bitacora= null;
		Entity seleccionado              = null;
		try {
			seleccionado    = (Entity)this.attrs.get("seleccionado");
      AdminGasto admin= new AdminGasto(seleccionado.getKey());
      Gasto gasto     = admin.toGasto();
			bitacora= new TcKalanGastosBitacoraDto(
        JsfBase.getIdUsuario(), // Long idUsuario, 
        -1L, // Long idGastoBitacora, 
        (String)this.attrs.get("justificacion"), // String observaciones, 
        Long.valueOf(this.attrs.get("estatus").toString()), // Long idGastoEstatus, 
        seleccionado.getKey() // Long idEmpresaGasto
      );
			transaccion= new Transaccion(gasto, bitacora);
			if(transaccion.ejecutar(EAccion.JUSTIFICAR))
				JsfBase.addMessage("Cambio estatus", "Se realizo el cambio de estatus", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Cambio estatus", "Ocurrio un error al cambio de estatus", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			this.attrs.put("justificacion", "");
		} // finally
	}	// doActualizaEstatus
	
	public String doMovimientos() {
		JsfBase.setFlashAttribute("tipo", ETipoMovimiento.GASTOS);
		JsfBase.setFlashAttribute(ETipoMovimiento.GASTOS.getIdKey(), ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("regreso", "/Paginas/Kalan/Gastos/filtro");
		return "/Paginas/Mantic/Compras/Ordenes/movimientos".concat(Constantes.REDIRECIONAR);
	}

	public List<UISelectEntity> doCompleteProveedor(String codigo) {
 		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
		boolean buscaPorCodigo    = false;
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			params.put("idAlmacen", JsfBase.getAutentifica().getEmpresa().getIdAlmacen());
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(!Cadena.isVacio(codigo)) {
  			codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim();
				buscaPorCodigo= codigo.startsWith(".");
				if(buscaPorCodigo)
					codigo= codigo.trim().substring(1);
				codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*");
			} // if	
			else
				codigo= "WXYZ";
  		params.put("codigo", codigo);
			if(buscaPorCodigo)
        this.attrs.put("proveedores", UIEntity.build("TcManticProveedoresDto", "porCodigo", params, columns, 40L));
			else
        this.attrs.put("proveedores", UIEntity.build("TcManticProveedoresDto", "porNombre", params, columns, 40L));
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

  public char getGroupProveedor(UISelectEntity item) {
    return item.toString("rfc").charAt(0);
  }  

  private void toLoadClasificaciones() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> clasificaciones= UISelect.seleccione("TcKalanGastosClasificacionesDto", params, "idKey|descripcion", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("clasificaciones", clasificaciones);
      if(clasificaciones!= null && !clasificaciones.isEmpty()) 
        this.attrs.put("idGastoClasificacion", clasificaciones.get(0).getValue());
      else
        this.attrs.put("idGastoClasificacion", -1L);
      this.doLoadSubclasificaciones();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
      throw e;
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  
  
  public void doLoadSubclasificaciones() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put("idGastoClasificacion", this.attrs.get("idGastoClasificacion"));
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> subclasificaciones= UISelect.seleccione("TcKalanGastosSubclasificacionesDto", params, "idKey|descripcion", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("subclasificaciones", subclasificaciones);
      if(subclasificaciones!= null && !subclasificaciones.isEmpty()) 
        this.attrs.put("idGastoSubclasificacion", subclasificaciones.get(0).getValue());
      else
        this.attrs.put("idGastoSubclasificacion", -1L);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
      throw e;
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  

  public void doView(Entity row) {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= this.toPrepare();
    try {
      params.put("idEmpresaGasto", row.toLong("idEmpresaGasto"));
      params.put("sortOrder", "order by tc_kalan_empresas_gastos.consecutivo");
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("subtotal", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("ivaCalculado", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("ivaRetenido", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("iepsCalculado", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("fechaAplicacion", EFormatoDinamicos.FECHA_CORTA));
      this.lazyDetalle= new FormatCustomLazy("VistaEmpresasGastosDto", "control", params, columns);
      UIBackingUtilities.resetDataTable("detalle");
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
  
  public String doColor(Entity row) {
    return Objects.equals(row.toLong("idGastoEstatus"), 3L)? "janal-tr-yellow": "";
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
    regresar.put("total", new Value("total", 0D));
    return regresar;
  }
  
}
