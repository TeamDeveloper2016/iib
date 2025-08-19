package mx.org.kaana.kalan.cuentas.reglas;

import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.kalan.db.dto.TcKalanCuentasBitacoraDto;
import mx.org.kaana.kalan.cuentas.beans.Cuenta;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

  private static final Log LOG= LogFactory.getLog(Transaccion.class);
	private Cuenta cuenta;
	private TcKalanCuentasBitacoraDto bitacora;
	private String messageError;
	
	public Transaccion(Cuenta cuenta) {
		this.cuenta= cuenta;
	}
	
	public Transaccion(Long idCuentaMovimiento) throws Exception {
    Map<String, Object> params = new HashMap<>();
    try {      
      params.put(Constantes.SQL_CONDICION, "id_cuenta_movimiento= "+ idCuentaMovimiento);      
  		this.cuenta = (Cuenta)DaoFactory.getInstance().toEntity(Cuenta.class, "TcKalanCuentasMovimientosDto", params);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
	}
	
	public Transaccion(Long idCuentaMovimiento, TcKalanCuentasBitacoraDto bitacora) throws Exception {
    Map<String, Object> params = new HashMap<>();
    try {      
      params.put(Constantes.SQL_CONDICION, "id_cuenta_movimiento= "+ idCuentaMovimiento);      
  		this.cuenta = (Cuenta)DaoFactory.getInstance().toEntity(Cuenta.class, "TcKalanCuentasMovimientosDto", params);
      this.bitacora= bitacora;
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
	}

  @Override
  protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    Cuenta clone              = null; 
    try {
      this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" el movimiento ");
      switch(accion) {
        case AGREGAR:
          regresar= this.toAgregar(sesion);
          break;
        case MODIFICAR:
          regresar= this.toModificar(sesion);
          break;
        case PROCESAR:
          regresar= this.toInsert(sesion);
          break;
        case REPROCESAR:
          regresar= this.toUpdate(sesion);
          break;
				case ELIMINAR:
          params.put("idCuentaMovimiento", this.cuenta.getIdCuentaMovimiento());            
          DaoFactory.getInstance().deleteAll(sesion, TcKalanCuentasBitacoraDto.class, params);
					regresar= DaoFactory.getInstance().delete(sesion, this.cuenta)>= 1L;
					break;
				case DEPURAR:
          params.put(Constantes.SQL_CONDICION, "tc_kalan_cuentas_movimientos.id_cuenta_movimiento= "+ this.cuenta.getIdEmpresaDestino());
          clone= (Cuenta)DaoFactory.getInstance().toEntity(sesion, Cuenta.class, "TcKalanCuentasMovimientosDto", params);
          
          params.put("idCuentaMovimiento", this.cuenta.getIdCuentaMovimiento());            
          DaoFactory.getInstance().deleteAll(sesion, TcKalanCuentasBitacoraDto.class, params);
					DaoFactory.getInstance().delete(sesion, this.cuenta);
          
          params.put("idCuentaMovimiento", clone.getIdCuentaMovimiento());
          DaoFactory.getInstance().deleteAll(sesion, TcKalanCuentasBitacoraDto.class, params);
					regresar= DaoFactory.getInstance().delete(sesion, clone)>= 1L;
          break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.cuenta.setIdCuentaEstatus(this.bitacora.getIdCuentaEstatus());
            regresar= DaoFactory.getInstance().update(sesion, this.cuenta)>= 1L;
					} // if
					break;
				case DESACTIVAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.cuenta.setIdCuentaEstatus(this.bitacora.getIdCuentaEstatus());
            DaoFactory.getInstance().update(sesion, this.cuenta);
            params.put(Constantes.SQL_CONDICION, "tc_kalan_cuentas_movimientos.id_cuenta_movimiento= "+ this.cuenta.getIdEmpresaDestino());
            clone= (Cuenta)DaoFactory.getInstance().toEntity(sesion, Cuenta.class, "TcKalanCuentasMovimientosDto", params);
						clone.setIdCuentaEstatus(this.bitacora.getIdCuentaEstatus());
            regresar= DaoFactory.getInstance().update(sesion, clone)>= 1L;
					} // if
					break;
      } // switch
      if (!regresar) 
        throw new Exception(this.messageError);
    } // try
    catch (Exception e) {
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
    } // catch		
		finally {
			Methods.clean(params);
		} // finally
    return regresar;
  } 
	
  private void toBitacora(Session sesion, Long idCuentaEstatus) throws Exception {
    this.toBitacora(sesion, idCuentaEstatus, null);
  }
  
  private void toBitacora(Session sesion, Long idCuentaEstatus, String justificacion) throws Exception {
    try {
      this.bitacora= new TcKalanCuentasBitacoraDto(
        -1L, // Long idCuentaBitacora, 
        this.cuenta.getIdCuentaMovimiento(), // Long idCuentaMovimiento
        justificacion, // String justificacion, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        idCuentaEstatus // Long idCuentaEstatus
      );
      DaoFactory.getInstance().insert(sesion, this.bitacora);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
  }

	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("ejercicio", this.getCurrentYear());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKalanCuentasMovimientosDto", "siguiente", params, "siguiente");
			if(next.getData()!= null)
			  regresar= new Siguiente(next.toLong());
			else
			  regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}  

  private Boolean toAgregar(Session sesion) throws Exception {
    Boolean regresar= Boolean.TRUE;
    try {
      Siguiente consecutivo= this.toSiguiente(sesion);
      this.cuenta.setConsecutivo(consecutivo.getConsecutivo());
      this.cuenta.setEjercicio(consecutivo.getEjercicio());
      this.cuenta.setOrden(consecutivo.getOrden());
      this.cuenta.setIdUsuario(JsfBase.getIdUsuario());
      regresar= DaoFactory.getInstance().insert(sesion, this.cuenta)>= 0L;
      this.toBitacora(sesion, this.cuenta.getIdCuentaEstatus());
		} // try
		catch (Exception e) {
			throw e;
		} // catch
    return regresar;
  }
  
  private Boolean toModificar(Session sesion) throws Exception {
    Boolean regresar= Boolean.TRUE;
    try {
      regresar= DaoFactory.getInstance().update(sesion, this.cuenta)>= 0L;
      this.toBitacora(sesion, this.cuenta.getIdCuentaEstatus());
		} // try
		catch (Exception e) {
			throw e;
		} // catch
    return regresar;
  }
  
  private Boolean toInsert(Session sesion) throws Exception {
    Boolean regresar= Boolean.TRUE;
    try {
      Siguiente consecutivo= this.toSiguiente(sesion);
      this.cuenta.setConsecutivo(consecutivo.getConsecutivo());
      this.cuenta.setEjercicio(consecutivo.getEjercicio());
      this.cuenta.setOrden(consecutivo.getOrden());
      this.cuenta.setIdUsuario(JsfBase.getIdUsuario());
      DaoFactory.getInstance().insert(sesion, this.cuenta);
      this.toBitacora(sesion, this.cuenta.getIdCuentaEstatus());
      
      Cuenta clone= this.cuenta.clone();
      consecutivo = this.toSiguiente(sesion);
      clone.setIdEmpresaDestino(this.cuenta.getIdCuentaMovimiento());
      clone.setConsecutivo(consecutivo.getConsecutivo());
      clone.setEjercicio(consecutivo.getEjercicio());
      clone.setOrden(consecutivo.getOrden());
      clone.setIdUsuario(JsfBase.getIdUsuario());
      clone.setIdBanco(null);
      DaoFactory.getInstance().insert(sesion, clone);
      this.toBitacora(sesion, clone.getIdCuentaEstatus());
      
      sesion.flush();
      this.cuenta.setIdEmpresaDestino(clone.getIdCuentaMovimiento());
      regresar= DaoFactory.getInstance().update(sesion, this.cuenta)>= 0L;
		} // try
		catch (Exception e) {
			throw e;
		} // catch
    return regresar;
  }
  
  private Boolean toUpdate(Session sesion) throws Exception {
    Boolean regresar          = Boolean.TRUE;
    Map<String, Object> params= new HashMap<>();
    try {
      this.cuenta.setIdBanco(null);
      DaoFactory.getInstance().update(sesion, this.cuenta);
      this.toBitacora(sesion, this.cuenta.getIdCuentaEstatus());
      params.put(Constantes.SQL_CONDICION, "tc_kalan_cuentas_movimientos.id_cuenta_movimiento= "+ this.cuenta.getIdEmpresaDestino());
      Cuenta clone= (Cuenta)DaoFactory.getInstance().toEntity(sesion, Cuenta.class, "TcKalanCuentasMovimientosDto", params);
      clone.setIdBanco(null);
      clone.setIdEmpresa(this.cuenta.getIdDestino());
      clone.setIdEmpresaCuenta(this.cuenta.getIdDestinoCuenta());
      clone.setFechaAplicacion(this.cuenta.getFechaAplicacion());
      clone.setFechaPago(this.cuenta.getFechaPago());
      clone.setIdTipoMedioPago(this.cuenta.getIdTipoMedioPago());
      clone.setImporte(this.cuenta.getImporte());
      clone.setReferencia(this.cuenta.getReferencia());
      regresar= DaoFactory.getInstance().update(sesion, clone)>= 0L;
      this.toBitacora(sesion, clone.getIdCuentaEstatus());
		} // try
		catch (Exception e) {
			throw e;
		} // catch
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
  
}
