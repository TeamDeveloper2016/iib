package mx.org.kaana.kalan.cuentas.backing;

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
import mx.org.kaana.kalan.cuentas.enums.ECuentasOrigenes;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.kalan.cuentas.reglas.Transaccion;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.kalan.db.dto.TcKalanCuentasBitacoraDto;
import mx.org.kaana.mantic.enums.ETipoMovimiento;

@Named(value = "kalanCuentasFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428871L;

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
    String cargos= Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("totales")).toDouble("cargos"));
    String abonos= Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("totales")).toDouble("abonos"));
    String saldo = Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("totales")).toDouble("saldo"));
    return "Suma cargos: <strong>"+ cargos+ "</strong> | abonos: <strong>"+ abonos+ "</strong> | saldo: <strong>"+ saldo+ "</strong>";  
  }
  
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("totales", this.toEmptyTotales());
      if(JsfBase.getFlashAttribute("idCuentaMovimientoProcess")!= null) {
        this.attrs.put("idCuentaMovimientoProcess", JsfBase.getFlashAttribute("idCuentaMovimientoProcess"));
        this.doLoad();
      } // if
      this.toLoadEmpresas();
      this.toLoadEstatus();
      this.toLoadTiposAfectaciones();
      this.toLoadCuentasOrigenes();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= null;
    try {
      params= this.toPrepare();	
      params.put("sortOrder", "order by tc_kalan_cuentas_movimientos.consecutivo desc");
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cuenta", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("usuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("tipo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("fechaAplicacion", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("fechaPago", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyModel= new FormatCustomLazy("VistaCuentasDto", params, columns);
      this.attrs.put("totales", this.toTotales("VistaCuentasDto", "totales", params));
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

  public String doAccion(String accion) {
    EAccion eaccion= null;
    String pagina  = "accion";
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
      JsfBase.setFlashAttribute("accion", Objects.equals(eaccion, EAccion.MODIFICAR) && !Objects.equals(((Entity)this.attrs.get("seleccionado")).toLong("idEmpresaDestino"), null)? EAccion.REPROCESAR: eaccion);      
      JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(accion.toUpperCase()));      
      JsfBase.setFlashAttribute("idCuentaMovimiento", (eaccion.equals(EAccion.MODIFICAR)||eaccion.equals(EAccion.CONSULTAR))? ((Entity)this.attrs.get("seleccionado")).getKey(): -1L);
      if(Objects.equals(eaccion, EAccion.PROCESAR) || Objects.equals(eaccion, EAccion.MODIFICAR) || Objects.equals(eaccion, EAccion.CONSULTAR))
        pagina= "transferencias";
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return pagina.concat(Constantes.REDIRECIONAR);
  } 

  public void doEliminar() {
    Entity seleccionado      = Objects.equals(this.attrs.get("seleccionado"), null)? (Entity)this.attrs.get("temporal"): (Entity)this.attrs.get("seleccionado");
    Transaccion transaccion  = null;
		Map<String, Object>params= null;
    try {
      params= this.toPrepare();	
      transaccion = new Transaccion(seleccionado.getKey());
      if(transaccion.ejecutar(Objects.equals(seleccionado.toLong("idCuentaOrigen"), ECuentasOrigenes.BANCOS_TRANSFERENCIAS.getIdCuentaOrigen())? EAccion.DEPURAR: EAccion.ELIMINAR)) {
        JsfBase.addMessage("Eliminar", "El movimiento se ha eliminado", ETipoMensaje.INFORMACION);
        this.attrs.put("totales", this.toTotales("VistaCuentasDto", "totales", params));
        UIBackingUtilities.resetDataTable();
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally		
  } 
	
	public List<UISelectEntity> doCompleteEmpleado(String codigo) {
 		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
		boolean buscaPorCodigo    = false;
    try {
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("curp", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(!Cadena.isVacio(codigo)) {
  			codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim();
				codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*");
			} // if	
			else
				codigo= "WXYZ";
  		params.put("codigo", codigo);
			if(buscaPorCodigo)
        this.attrs.put("empleados", UIEntity.build("VistaPrestamosDto", "porCodigo", params, columns, 40L));
			else
        this.attrs.put("empleados", UIEntity.build("VistaPrestamosDto", "porNombre", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("empleados");
	}	
	
	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar  = new HashMap<>();	
		StringBuilder sb              = new StringBuilder();
	  UISelectEntity empleado       = (UISelectEntity)this.attrs.get("idEmpresaPersona");
		List<UISelectEntity>empleados = (List<UISelectEntity>)this.attrs.get("empleados");
	  UISelectEntity origen         = (UISelectEntity)this.attrs.get("idCuentaOrigen");
		List<UISelectEntity>origenes  = (List<UISelectEntity>)this.attrs.get("origenes");
    try {
      if(!Cadena.isVacio(this.attrs.get("idCuentaMovimientoProcess")) && !Objects.equals((Long)this.attrs.get("idCuentaMovimientoProcess"), -1L)) {
        sb.append("(tc_kalan_cuentas_movimientos.id_cuenta_movimiento=").append(this.attrs.get("idCuentaMovimientoProcess")).append(") and ");
        this.attrs.put("idCuentaMovimientoProcess", null);
      } // if  
      if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !Objects.equals(((UISelectEntity)this.attrs.get("idEmpresa")).getKey(), -1L))
        sb.append("(tr_mantic_empresa_personal.id_empresa= ").append(this.attrs.get("idEmpresa")).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("idEmpresaCuenta")) && !Objects.equals(((UISelectEntity)this.attrs.get("idEmpresaCuenta")).getKey(), -1L))
        sb.append("(tc_kalan_cuentas_movimientos.id_empresa_cuenta= ").append(this.attrs.get("idEmpresaCuenta")).append(") and ");
      if(empleados!= null && empleado!= null && empleados.indexOf(empleado)>= 0) 
        sb.append("(tc_mantic_personas.id_persona= ").append(empleados.get(empleados.indexOf(empleado)).toLong("idPersona")).append(") and ");
      else
        if(!Cadena.isVacio(JsfBase.getParametro("empleado_input"))) {
          String codigo= JsfBase.getParametro("empleado_input");
          codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim();
          codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*");
          sb.append("(upper(concat(tc_mantic_personas.nombres, ' ', ifnull(tc_mantic_personas.paterno, ''), ' ', ifnull(tc_mantic_personas.materno, ''))) regexp '.*").append(codigo).append(".*') and ");
        } // if  
      if(!Cadena.isVacio(this.attrs.get("importeMenor")))
        sb.append("(date_format(tc_kalan_cuentas_movimientos.importe, '%Y%m%d')<= '").append(this.attrs.get("importeMenor")).append("') and ");	
      if(!Cadena.isVacio(this.attrs.get("importeMayor")))
        sb.append("(date_format(tc_kalan_cuentas_movimientos.importe, '%Y%m%d')>= '").append(this.attrs.get("importeMayor")).append("') and ");	
      if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
        sb.append("(date_format(tc_kalan_cuentas_movimientos.fecha_aplicacion, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");	
      if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
        sb.append("(date_format(tc_kalan_cuentas_movimientos.fecha_aplicacion, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");	
      if(!Cadena.isVacio(this.attrs.get("idTipoAfectacion")) && !Objects.equals(((UISelectEntity)this.attrs.get("idTipoAfectacion")).getKey(), -1L))
        sb.append("(tc_kalan_cuentas_movimientos.id_tipo_afectacion= ").append(this.attrs.get("idTipoAfectacion")).append(") and ");
      if(origenes!= null && origen!= null && origenes.indexOf(origen)>= 0 && !Objects.equals(origen.getKey(), -1L)) 
        sb.append("(tc_kalan_cuentas_origenes.clave like '").append(origenes.get(origenes.indexOf(origen)).toString("clave")).append("%') and ");
      if(!Cadena.isVacio(this.attrs.get("idCuentaEstatus")) && !Objects.equals(((UISelectEntity)this.attrs.get("idCuentaEstatus")).getKey(), -1L))
        if(Objects.equals(((UISelectEntity)this.attrs.get("idCuentaEstatus")).getKey(), Constantes.TOP_OF_ITEMS))
          sb.append("(tc_kalan_cuentas_movimientos.id_cuenta_estatus in (1, 2)) and ");
        else 
          sb.append("(tc_kalan_cuentas_movimientos.id_cuenta_estatus= ").append(this.attrs.get("idCuentaEstatus")).append(") and ");
      regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
      if(sb.length()== 0)
        regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      else	
        regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
		return regresar;		
	}

	private void toLoadEmpresas() {
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
      this.doLoadCuentas();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}
  
  public void doLoadCuentas() {
    List<UISelectEntity> empresaCuentas= null;
    Map<String, Object> params         = new HashMap<>();
    try {
			params.put("idEmpresa", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      if(Objects.equals(((UISelectEntity)this.attrs.get("idEmpresa")).getKey(), -1L))
        empresaCuentas= UIEntity.seleccione("TcKalanEmpresasCuentasDto", params, "banco");
      else
        empresaCuentas= UIEntity.build("TcKalanEmpresasCuentasDto", params);
      this.attrs.put("empresaCuentas", empresaCuentas);
      if(empresaCuentas!= null && !empresaCuentas.isEmpty()) 
    	  this.attrs.put("idEmpresaCuenta", UIBackingUtilities.toFirstKeySelectEntity(empresaCuentas));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  
  
	private void toLoadEstatus() {
		List<Columna> columns       = new ArrayList<>();
    Map<String, Object> params  = new HashMap<>();
    List<UISelectEntity> estatus= null;
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      estatus= (List<UISelectEntity>) UIEntity.seleccione("TcKalanCuentasEstatusDto", "row", params, columns, "nombre");
      this.attrs.put("catalogo", estatus);
      UISelectEntity pendiente= new UISelectEntity(Constantes.TOP_OF_ITEMS);
      pendiente.add("nombre", "PENDIENTES");
      estatus.add(pendiente);
  	  this.attrs.put("idCuentaEstatus", pendiente);
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}

	private void toLoadTiposAfectaciones() {
		List<Columna> columns        = new ArrayList<>();
    Map<String, Object> params   = new HashMap<>();
    List<UISelectEntity> afectaciones= null;
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      afectaciones= (List<UISelectEntity>) UIEntity.seleccione("TcKalanTiposAfectacionesDto", "row", params, columns, "nombre");
      this.attrs.put("afectaciones", afectaciones);
      if(afectaciones!= null && !afectaciones.isEmpty()) 
    	  this.attrs.put("idTipoAfectacion", UIBackingUtilities.toFirstKeySelectEntity(afectaciones));
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}

	private void toLoadCuentasOrigenes() {
		List<Columna> columns        = new ArrayList<>();
    Map<String, Object> params   = new HashMap<>();
    List<UISelectEntity> origenes= null;
    try {
			// params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			params.put(Constantes.SQL_CONDICION, "clave like '1%'");
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      origenes= (List<UISelectEntity>) UIEntity.seleccione("TcKalanCuentasOrigenesDto", "row", params, columns, "nombre");
      this.attrs.put("origenes", origenes);
      if(origenes!= null && !origenes.isEmpty()) 
    	  this.attrs.put("idCuentaOrigen", UIBackingUtilities.toFirstKeySelectEntity(origenes));
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}

	public void doLoadEstatus() {
		Entity seleccionado          = (Entity)this.attrs.get("seleccionado");
		Map<String, Object>params    = new HashMap<>();
		List<UISelectItem> allEstatus= null;
		try {
			params.put(Constantes.SQL_CONDICION, "id_cuenta_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcKalanCuentasEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
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
	
	public void doActualizarEstatus() {
		Transaccion transaccion            = null;
		TcKalanCuentasBitacoraDto bitacora= null;
		Entity seleccionado                = null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			bitacora    = new TcKalanCuentasBitacoraDto(
        -1L, // Long idCuentaBitacora, 
        seleccionado.getKey(), // Long idCuentaMovimiento
        (String)this.attrs.get("justificacion"), // String justificacion, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        Long.valueOf((String)this.attrs.get("estatus")) // Long idCuentaEstatus
      );
			transaccion= new Transaccion(seleccionado.getKey(), bitacora);
			if(transaccion.ejecutar(!Objects.equals(seleccionado.toLong("idEmpresaDestino"), null)? EAccion.DESACTIVAR: EAccion.JUSTIFICAR))
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
	}	
	
	public String doMovimientos() {
		JsfBase.setFlashAttribute("tipo", ETipoMovimiento.CUENTAS);
		JsfBase.setFlashAttribute(ETipoMovimiento.CUENTAS.getIdKey(), ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("regreso", "/Paginas/Kalan/Cuentas/filtro");
		return "/Paginas/Mantic/Compras/Ordenes/movimientos".concat(Constantes.REDIRECIONAR);
	}

  public void doSeleccionado(Entity row) {
    try {
      this.attrs.put("seleccionado", row);
      this.attrs.put("temporal", row);
      this.doSeleccionado();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch      
  }
  
  public void doSeleccionado() {
    Entity seleccionado       = (Entity)this.attrs.get("seleccionado");
    Map<String, Object> params= new HashMap<>();
    try {  
      if(!Objects.equals(seleccionado.toLong("idEmpresaDestino"), null)) {
        params.put(Constantes.SQL_CONDICION, "id_cuenta_movimiento= "+ seleccionado.toLong("idEmpresaDestino"));      
        this.attrs.put("detalle", (Entity)DaoFactory.getInstance().toEntity("TcKalanCuentasMovimientosDto", "detalle", params)); 
      } // if
      else
        this.attrs.put("detalle", null);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(params);
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
    regresar.put("cargos", new Value("cargos", 0D));
    regresar.put("abonos", new Value("abonos", 0D));
    regresar.put("saldo", new Value("saldo", 0D));
    return regresar;
  }
  
  public String doColor(Entity row) {
    // return !Objects.equals(row.toLong("idEmpresaDestino"), null)? "janal-tr-green": "";
    return !Objects.equals(row.toLong("idEmpresaDestino"), null)? "": "";
  }  

  public Boolean doView(Entity row) {
    return Objects.equals(row.toLong("idCuentaOrigen"), ECuentasOrigenes.BANCOS.getIdCuentaOrigen()) || 
           Objects.equals(row.toLong("idCuentaOrigen"), ECuentasOrigenes.BANCOS_CARGOS.getIdCuentaOrigen()) ||
           Objects.equals(row.toLong("idCuentaOrigen"), ECuentasOrigenes.BANCOS_ABONOS.getIdCuentaOrigen()) ||
           Objects.equals(row.toLong("idCuentaOrigen"), ECuentasOrigenes.BANCOS_TRANSFERENCIAS.getIdCuentaOrigen());
  }  
  
}
