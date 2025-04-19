package mx.org.kaana.kalan.movimientos.backing;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kalan.db.dto.TcKalanMovimientosBitacoraDto;
import mx.org.kaana.kalan.movimientos.reglas.Transaccion;
import mx.org.kaana.kalan.movimientos.beans.Movimiento;
import mx.org.kaana.kalan.movimientos.reglas.AdminMovimiento;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.enums.ETipoMovimiento;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 4/09/2023
 *@time 10:51:21 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 601942711851969289L;

  protected Long idTipoMovimiento;
  protected String titulo;
  protected String pagina;
  
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("idEmpresaMovimientoProcess", JsfBase.getFlashAttribute("idEmpresaMovimientoProcess"));
			this.toLoadCatalog();
      if(this.attrs.get("idEmpresaMovimientoProcess")!= null) 
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
      params.put("idTipoMovimiento", this.idTipoMovimiento);
      params.put("sortOrder", "order by tc_kalan_empresas_movimientos.consecutivo desc");
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cuenta", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cliente", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("fechaAplicacion", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaEmpresasMovimientosDto", params, columns);
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
  } // doLoad

  public String doAccion(String accion) {
		String regresar    = "/Paginas/Kalan/Movimientos/accion";
		Entity seleccionado= (Entity) this.attrs.get("seleccionado");
    EAccion eaccion    = null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("pagina", this.pagina);		
			JsfBase.setFlashAttribute("titulo", this.titulo);		
			JsfBase.setFlashAttribute("idTipoMovimiento", this.idTipoMovimiento);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Kalan/Movimientos/".concat(this.pagina));		
			JsfBase.setFlashAttribute("idEmpresaMovimiento", eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR)? seleccionado.getKey(): -1L);
		} // try // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR);
  } // doAccion  
	
  public void doEliminar() {
		Transaccion transaccion = null;
		Entity seleccionado     = (Entity) this.attrs.get("seleccionado");
		try {
      AdminMovimiento admin= new AdminMovimiento(seleccionado.getKey());
      Movimiento movimiento= admin.getMovimiento();
			transaccion     = new Transaccion(movimiento);
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "El ".concat(this.pagina).concat(" se ha eliminado correctamente"), ETipoMensaje.ERROR);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar el ".concat(this.pagina), ETipoMensaje.ERROR);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
  } // doEliminar

	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idEmpresaMovimientoProcess")) && !this.attrs.get("idEmpresaMovimientoProcess").toString().equals("-1")) {
  		sb.append("(tc_kalan_empresas_movimientos.id_empresa_movimiento=").append(this.attrs.get("idEmpresaMovimientoProcess")).append(") and ");
      this.attrs.put("idEmpresaMovimientoProcess", null);
    } // if  
		if(!Cadena.isVacio(this.attrs.get("consecutivo")))
  		sb.append("(tc_kalan_empresas_movimientos.consecutivo= '").append(this.attrs.get("consecutivo")).append("') and ");
		if(!Cadena.isVacio(this.attrs.get("fecha")))
		  sb.append("(date_format(tc_kalan_empresas_movimientos.fecha, '%Y%m%d')= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fecha"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
		  sb.append("(date_format(tc_kalan_empresas_movimientos.registro, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
		  sb.append("(date_format(tc_kalan_empresas_movimientos.registro, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("idCliente")) && !this.attrs.get("idCliente").toString().equals("-1"))
  		sb.append("(tc_mantic_clientes.id_cliente= ").append(this.attrs.get("idCliente")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idTipoConcepto")) && !this.attrs.get("idTipoConcepto").toString().equals("-1"))
  		sb.append("(tc_kalan_empresas_movimientos.id_tipo_concepto= ").append(this.attrs.get("idTipoConcepto")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idEmpresaCuenta")) && !this.attrs.get("idEmpresaCuenta").toString().equals("-1"))
  		sb.append("(tc_kalan_empresas_movimientos.id_empresa_cuenta= ").append(this.attrs.get("idEmpresaCuenta")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idMovimientoEstatus")) && !this.attrs.get("idMovimientoEstatus").toString().equals("-1"))
  		sb.append("(tc_kalan_empresas_movimientos.id_movimiento_estatus= ").append(this.attrs.get("idMovimientoEstatus")).append(") and ");
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
	
	private void toLoadCatalog() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
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
			this.attrs.put("idCliente", new UISelectEntity("-1"));
			columns.remove(0);
      this.attrs.put("catalogo", (List<UISelectEntity>) UIEntity.build("TcKalanMovimientosEstatusDto", "row", params, columns));
			this.attrs.put("idMovimientoEstatus", new UISelectEntity("-1"));
      this.doLoadBancos();
      this.toLoadConceptos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}
  
	public void doLoadEstatus() {
		Entity seleccionado          = (Entity)this.attrs.get("seleccionado");
		Map<String, Object>params    = new HashMap<>();
		List<UISelectItem> allEstatus= null;
		try {
			params.put(Constantes.SQL_CONDICION, "id_movimiento_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcKalanMovimientosEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
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
		Transaccion transaccion          = null;
	  TcKalanMovimientosBitacoraDto bitacora= null;
		Entity seleccionado              = null;
		try {
			seleccionado    = (Entity)this.attrs.get("seleccionado");
      AdminMovimiento admin= new AdminMovimiento(seleccionado.getKey());
      Movimiento movimiento= admin.toMovimiento();
      bitacora= new TcKalanMovimientosBitacoraDto(
        -1L, // Long idMovimientoBitacora, 
        (String)this.attrs.get("justificacion"), // String justificacion, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        Long.valueOf(this.attrs.get("estatus").toString()), // Long idMovimientoEstatus, 
        seleccionado.getKey() // Long idEmpresaMovimiento                  
      );
			transaccion= new Transaccion(movimiento, bitacora);
			if(transaccion.ejecutar(EAccion.JUSTIFICAR))
				JsfBase.addMessage("Cambio estatus", "Se realizo el cambio de estatus de forma correcta", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Cambio estatus", "Ocurrio un error al realizar el cambio de estatus", ETipoMensaje.ERROR);
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
    ETipoMovimiento tipo= Objects.equals(this.idTipoMovimiento, 1L)? ETipoMovimiento.INGRESOS: ETipoMovimiento.EGRESOS;
		JsfBase.setFlashAttribute("tipo", tipo);
		JsfBase.setFlashAttribute(tipo.getIdKey(), ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("regreso", "/Paginas/Kalan/Movimientos/".concat(this.pagina));
		return "/Paginas/Mantic/Compras/Ordenes/movimientos".concat(Constantes.REDIRECIONAR);
	}

	public List<UISelectEntity> doCompleteCliente(String codigo) {
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
        this.attrs.put("clientes", UIEntity.build("TcManticClientesDto", "porCodigo", params, columns, 40L));
			else
        this.attrs.put("clientes", UIEntity.build("TcManticClientesDto", "porNombre", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("clientes");
	}	

  public char getGroupCliente(UISelectEntity item) {
    return item.toString("rfc").charAt(0);
  }  

  public void doLoadBancos() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put("idEmpresa", this.attrs.get("idEmpresa"));
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> bancos= UISelect.seleccione("TcKalanEmpresasCuentasDto", params, "idKey|nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("bancos", bancos);
      if(bancos!= null && !bancos.isEmpty()) 
        this.attrs.put("idBanco", bancos.get(0).getValue());
      else
        this.attrs.put("idBanco", -1L);
      this.doLoadCuentas();
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
  
  public void doLoadCuentas() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put("idEmpresa", this.attrs.get("idEmpresa"));
			params.put("idBanco", this.attrs.get("idBanco"));
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> cuentas= UISelect.seleccione("TcKalanEmpresasCuentasDto", "cuentas", params, "idKey|nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("cuentas", cuentas);
      if(cuentas!= null && !cuentas.isEmpty()) 
        this.attrs.put("idEmpresaCuenta", cuentas.get(0).getValue());
      else
        this.attrs.put("idEmpresaCuenta", -1L);
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

  private void toLoadConceptos() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put("idTipoMovimiento", this.idTipoMovimiento);
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> conceptos= UISelect.seleccione("TcKalanTiposConceptosDto", params, "idKey|descripcion", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("conceptos", conceptos);
      if(conceptos!= null && !conceptos.isEmpty()) 
        this.attrs.put("idTipoConcepto", conceptos.get(0).getValue());
      else
        this.attrs.put("idTipoConcepto", -1L);
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
  
  public String doColor(Entity row) {
    return Objects.equals(row.toLong("idMovimientoEstatus"), 3L)? "janal-tr-yellow": "";
  }
  
}
