package mx.org.kaana.kalan.cuentas.reglas;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.kalan.cuentas.beans.ICuenta;
import mx.org.kaana.kalan.cuentas.enums.ECuentasOrigenes;
import mx.org.kaana.kalan.cuentas.enums.EEstatusCuentas;
import mx.org.kaana.kalan.db.dto.TcKalanCuentasBitacoraDto;
import mx.org.kaana.kalan.db.dto.TcKalanCuentasMovimientosDto;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public abstract class IBaseCuenta extends IBaseTnx implements Serializable {

  private static final Log LOG= LogFactory.getLog(Transaccion.class);
  private static final long serialVersionUID = 5752229314495124313L;
  
  protected Boolean control(Session sesion, ICuenta cuenta, ECuentasOrigenes origen) throws Exception {
    return this.control(sesion, cuenta, origen, Boolean.FALSE);  
  }
  
  protected Boolean control(Session sesion, ICuenta cuenta, ECuentasOrigenes origen, Boolean delete) throws Exception {
    Boolean regresar          = Boolean.TRUE;
    Map<String, Object> params= new HashMap<>();
    try {
       params.put("idCuentaOrigen", origen.getIdCuentaOrigen());
       params.put("idPivote", cuenta.getKey());
       TcKalanCuentasMovimientosDto existe= (TcKalanCuentasMovimientosDto)DaoFactory.getInstance().toEntity(sesion, TcKalanCuentasMovimientosDto.class, "TcKalanCuentasMovimientosDto", "existe", params);    
       if(delete && !Objects.equals(existe, null)) {
         existe.setIdCuentaEstatus(EEstatusCuentas.ELIMINADO.getIdEstatusCuenta());
         this.updateCuenta(sesion, existe);
       } // if
       else
          if(Objects.equals(existe, null)) {
            existe= this.clone(cuenta, origen);
            this.addCuenta(sesion, existe);
          } // if
          else {
            existe.setIdCuentaEstatus(cuenta.getIdCuentaEstatus());
            this.updateCuenta(sesion, existe);
          } // else
		} // try
		catch (Exception e) {
			throw e;
		} // catch
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
  
  private TcKalanCuentasMovimientosDto clone(ICuenta cuenta, ECuentasOrigenes origen) {
    TcKalanCuentasMovimientosDto regresar= new TcKalanCuentasMovimientosDto(
      cuenta.getIdTipoAfectacion(), // Long idTipoAfectacion, 
      cuenta.getIdTipoMedioPago(), // Long idTipoMedioPago, 
      cuenta.getImporte(), // Double importe, 
      null, // Long idBanco, 
      null, // Long ejercicio, 
      cuenta.getFechaPago(), // Date fechaPago, 
      null, // String consecutivo, 
      cuenta.getFechaAplicacion(), // Date fechaAplicacion, 
      cuenta.getIdEmpresaCuenta(), // Long idEmpresaCuenta, 
      cuenta.getIdUsuario(), // Long idUsuario, 
      null, // Long idEmpresaDestino, 
      cuenta.getObservaciones(), // String observaciones, 
      null, // Long orden, 
      -1L, // Long idCuentaMovimiento, 
      cuenta.getReferencia(), // String referencia, 
      cuenta.getIdEmpresa(), // Long idEmpresa, 
      2L, // APLICADO Long idCuentaEstatus, 
      origen.getIdCuentaOrigen(), // Long idCuentaOrigen, 
      cuenta.getKey() // Long idPivote
    );
    return regresar;
  }
  
  protected Boolean addCuenta(Session sesion, TcKalanCuentasMovimientosDto cuenta) throws Exception {
    Boolean regresar= Boolean.TRUE;
    try {
      Siguiente consecutivo= this.siguiente(sesion);
      cuenta.setConsecutivo(consecutivo.getConsecutivo());
      cuenta.setEjercicio(consecutivo.getEjercicio());
      cuenta.setOrden(consecutivo.getOrden());
      regresar= DaoFactory.getInstance().insert(sesion, cuenta)>= 0L;
      this.bitacora(sesion, cuenta);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
    return regresar;
  }
  
  protected Boolean updateCuenta(Session sesion, TcKalanCuentasMovimientosDto cuenta) throws Exception {
    Boolean regresar= Boolean.TRUE;
    try {
      cuenta.setActualizado(new Timestamp(Calendar.getInstance().getTimeInMillis()));
      regresar= DaoFactory.getInstance().update(sesion, cuenta)>= 0L;
      this.bitacora(sesion, cuenta);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
    return regresar;
  }

	protected Siguiente siguiente(Session sesion) throws Exception {
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
  
  protected void bitacora(Session sesion, TcKalanCuentasMovimientosDto cuenta) throws Exception {
    this.bitacora(sesion, cuenta, null);
  }
  
  protected void bitacora(Session sesion, TcKalanCuentasMovimientosDto cuenta, String justificacion) throws Exception {
    try {
      TcKalanCuentasBitacoraDto bitacora= new TcKalanCuentasBitacoraDto(
        -1L, // Long idCuentaBitacora, 
        cuenta.getIdCuentaMovimiento(), // Long idCuentaMovimiento
        justificacion, // String justificacion, 
        cuenta.getIdUsuario(), // Long idUsuario, 
        cuenta.getIdCuentaEstatus() // Long idCuentaEstatus
      );
      DaoFactory.getInstance().insert(sesion, bitacora);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
  }
  
}
