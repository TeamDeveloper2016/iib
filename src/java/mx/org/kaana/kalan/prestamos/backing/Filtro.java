package mx.org.kaana.kalan.prestamos.backing;

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
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.kalan.prestamos.reglas.Transaccion;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.kalan.db.dto.TcKalanPrestamosBitacoraDto;
import mx.org.kaana.kalan.prestamos.beans.Afectacion;
import mx.org.kaana.mantic.enums.ETipoMovimiento;

@Named(value = "kalanPrestamosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428871L;

  private FormatLazyModel lazyDetalle;

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
    String saldo= Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("saldo"));
    return "Suma importe: <strong>"+ total+ "</strong> | saldo: <strong>"+ saldo+ "</strong>";  
  }
  
  public String getTotal() {
    double cargo= 0D;
    double abono= 0D;
    String value= null;
		try {
      if(!Objects.equals(this.lazyDetalle, null))
        for(Entity item: (List<Entity>)this.lazyDetalle.getWrappedData()) {
          value= item.toString("importe");
          if(Objects.equals(item.toLong("idTipoAfectacion"), 1L))
            cargo= cargo+ Double.valueOf(Cadena.eliminar(value, ','));
          if(Objects.equals(item.toLong("idTipoAfectacion"), 2L))
            abono= abono+ Double.valueOf(Cadena.eliminar(value, ','));
        } // for  
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    String cargos= Numero.formatear(Numero.MILES_CON_DECIMALES, Numero.toRedondearSat(cargo));
    String abonos= Numero.formatear(Numero.MILES_CON_DECIMALES, Numero.toRedondearSat(abono));
    return "Suma cargos: <strong>"+ cargos+ "</strong> | abonos: <strong>"+ abonos+ "</strong> | saldo: <strong>"+ Numero.formatear(Numero.MILES_CON_DECIMALES, Numero.toRedondearSat(abono- cargo))+ "</strong>";  
  }
  
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("general", this.toEmptyTotales());
      if(JsfBase.getFlashAttribute("idPrestamoProcess")!= null) {
        this.attrs.put("idPrestamoProcess", JsfBase.getFlashAttribute("idPrestamoProcess"));
        this.doLoad();
      } // if
      this.toLoadEmpresas();
      this.toLoadEstatus();
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
      params.put("sortOrder", "order by tc_kalan_prestamos.registro desc");
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("empleado", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("saldo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("disponible", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("fechaAplicacion", EFormatoDinamicos.FECHA_CORTA));
      this.lazyModel= new FormatCustomLazy("VistaPrestamosDto", params, columns);
      this.attrs.put("general", this.toTotales("VistaPrestamosDto", "general", params));
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
    EAccion eaccion = null;
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
      JsfBase.setFlashAttribute("accion", eaccion);      
      JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(accion.toUpperCase()));      
      JsfBase.setFlashAttribute("idPrestamo", (eaccion.equals(EAccion.MODIFICAR)||eaccion.equals(EAccion.CONSULTAR)) ? ((Entity) this.attrs.get("seleccionado")).getKey() : -1L);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return "accion".concat(Constantes.REDIRECIONAR);
  } // doAccion

  public void doEliminar() {
    Transaccion transaccion = null;
    try {
      transaccion = new Transaccion(((Entity)this.attrs.get("seleccionado")).getKey());
      transaccion.ejecutar(EAccion.ELIMINAR);
      JsfBase.addMessage("Eliminar", "El prestamo se ha eliminado", ETipoMensaje.INFORMACION);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
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
    if(!Cadena.isVacio(this.attrs.get("idPrestamoProcess")) && !Objects.equals((Long)this.attrs.get("idPrestamoProcess"), -1L)) {
      sb.append("(tc_kalan_prestamos.id_prestamo=").append(this.attrs.get("idPrestamoProcess")).append(") and ");
      this.attrs.put("idPrestamoProcess", null);
    } // if  
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !Objects.equals(((UISelectEntity)this.attrs.get("idEmpresa")).getKey(), -1L))
  		sb.append("(tr_mantic_empresa_personal.id_empresa= ").append(this.attrs.get("idEmpresa")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("nombre")))
			sb.append("(tc_kalan_prestamos.nombre like '%").append(this.attrs.get("nombre")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("rfc")))
			sb.append("(tc_mantic_personas.rfc like '%").append(this.attrs.get("rfc")).append("%') and ");
		if(empleados!= null && empleado!= null && empleados.indexOf(empleado)>= 0) 
			sb.append("(tr_mantic_empresa_personal.id_empresa_persona= ").append(empleado.getKey()).append(") and ");
		else
 		  if(!Cadena.isVacio(JsfBase.getParametro("empleado_input"))) {
        String codigo= JsfBase.getParametro("empleado_input");
        codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim();
				codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*");
			  sb.append("(upper(concat(tc_mantic_personas.nombres, ' ', ifnull(tc_mantic_personas.paterno, ''), ' ', ifnull(tc_mantic_personas.materno, ''))) regexp '.*").append(codigo).append(".*') and ");
      } // if  
		if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
		  sb.append("(date_format(tc_kalan_prestamos.fecha_aplicacion, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
		  sb.append("(date_format(tc_kalan_prestamos.fecha_aplicacion, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("idPrestamoEstatus")) && !Objects.equals(((UISelectEntity)this.attrs.get("idPrestamoEstatus")).getKey(), -1L))
      if(Objects.equals(((UISelectEntity)this.attrs.get("idPrestamoEstatus")).getKey(), Constantes.TOP_OF_ITEMS))
  		  sb.append("(tc_kalan_prestamos.id_prestamo_estatus in (1, 2, 6)) and ");
      else 
  		  sb.append("(tc_kalan_prestamos.id_prestamo_estatus= ").append(this.attrs.get("idPrestamoEstatus")).append(") and ");
	  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
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
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
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
      estatus= (List<UISelectEntity>) UIEntity.seleccione("TcKalanPrestamosEstatusDto", "row", params, columns, "nombre");
      this.attrs.put("catalogo", estatus);
      UISelectEntity pendiente= new UISelectEntity(Constantes.TOP_OF_ITEMS);
      pendiente.add("nombre", "PENDIENTES");
      estatus.add(pendiente);
  	  this.attrs.put("idPrestamoEstatus", pendiente);
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
			params.put(Constantes.SQL_CONDICION, "id_prestamo_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcKalanPrestamosEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
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
		TcKalanPrestamosBitacoraDto bitacora= null;
		Entity seleccionado                = null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			bitacora    = new TcKalanPrestamosBitacoraDto(
        seleccionado.getKey(), // Long idPrestamo, 
        -1L, // Long idPrestamoBitacora, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        (String)this.attrs.get("justificacion"), // String justificacion, 
        Long.valueOf((String)this.attrs.get("estatus")) // Long idPrestamoEstatus
      );
			transaccion= new Transaccion(seleccionado.getKey(), bitacora);
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
	}	
	
	public String doMovimientos() {
		JsfBase.setFlashAttribute("tipo", ETipoMovimiento.PRESTAMOS);
		JsfBase.setFlashAttribute(ETipoMovimiento.PRESTAMOS.getIdKey(), ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("regreso", "/Paginas/Kalan/Prestamos/filtro");
		return "/Paginas/Mantic/Compras/Ordenes/movimientos".concat(Constantes.REDIRECIONAR);
	}

	public String doAfectaciones() {
		JsfBase.setFlashAttribute("idPrestamo", ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("regreso", "/Paginas/Kalan/Prestamos/filtro");
		return "afectaciones".concat(Constantes.REDIRECIONAR);
	}

  public void doView(Entity row) {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= this.toPrepare();
    try {
      params.put("idPrestamo", row.toLong("idPrestamo"));
      params.put("sortOrder", "order by tc_kalan_prestamos_pagos.consecutivo desc");
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("fechaAplicacion", EFormatoDinamicos.FECHA_CORTA));
      this.lazyDetalle= new FormatCustomLazy("VistaPrestamosDto", "pagos", params, columns);
      UIBackingUtilities.resetDataTable("detalle");
      this.attrs.put("seleccionado", row);
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

  public void doSelectDetalle(Entity row) {
    this.attrs.put("detalle", row);
  }
  
  public void doDeletePago(Entity row) {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= this.toPrepare();
    Afectacion afectacion     = null;
    Transaccion transaccion   = null;
    try {
      params.put(Constantes.SQL_CONDICION, "id_prestamo_pago= "+ row.toLong("idPrestamoPago"));
      afectacion = (Afectacion)DaoFactory.getInstance().toEntity(Afectacion.class, "TcKalanPrestamosPagosDto", params);
      transaccion= new Transaccion(afectacion);
      transaccion.ejecutar(EAccion.DEPURAR);
      JsfBase.addMessage("Eliminar", "El movimiento se ha eliminado", ETipoMensaje.INFORMACION);
      this.attrs.put("idPrestamoProcess", row.toLong("idPrestamo"));
      this.doLoad();
      this.doView(row);
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
    regresar.put("saldo", new Value("saldo", 0D));
    return regresar;
  }
  
}
