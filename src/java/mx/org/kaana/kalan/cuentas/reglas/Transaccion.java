package mx.org.kaana.kalan.cuentas.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kalan.db.dto.TcKalanCuentasBitacoraDto;
import mx.org.kaana.kalan.cuentas.beans.Cuenta;
import mx.org.kaana.kalan.db.dto.TcKalanCuentasMovimientosDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseCuenta implements Serializable {

  private static final Log LOG= LogFactory.getLog(Transaccion.class);
  private static final long serialVersionUID = 5752229334495124313L;
  
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
          regresar= super.addCuenta(sesion, this.cuenta);
          break;
        case MODIFICAR:
          regresar= super.updateCuenta(sesion, this.cuenta);
          break;
        case PROCESAR:
          regresar= this.insert(sesion);
          break;
        case REPROCESAR:
          regresar= this.update(sesion);
          break;
				case ELIMINAR:
          params.put("idCuentaMovimiento", this.cuenta.getIdCuentaMovimiento());            
          DaoFactory.getInstance().deleteAll(sesion, TcKalanCuentasBitacoraDto.class, params);
					regresar= DaoFactory.getInstance().delete(sesion, this.cuenta)>= 1L;
					break;
				case DEPURAR:
          params.put(Constantes.SQL_CONDICION, "tc_kalan_cuentas_movimientos.id_cuenta_movimiento= "+ this.cuenta.getIdEmpresaDestino());
          clone= (Cuenta)DaoFactory.getInstance().toEntity(sesion, Cuenta.class, "TcKalanCuentasMovimientosDto", params);
          params.put("cuentas", this.cuenta.getIdCuentaMovimiento()+ ", "+ this.cuenta.getIdEmpresaDestino());
          DaoFactory.getInstance().updateAll(sesion, TcKalanCuentasMovimientosDto.class, params);
          sesion.flush();
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
            this.cuenta.setIdBanco(null);
						this.cuenta.setIdCuentaEstatus(this.bitacora.getIdCuentaEstatus());
            DaoFactory.getInstance().update(sesion, this.cuenta);
            params.put(Constantes.SQL_CONDICION, "tc_kalan_cuentas_movimientos.id_cuenta_movimiento= "+ this.cuenta.getIdEmpresaDestino());
            clone= (Cuenta)DaoFactory.getInstance().toEntity(sesion, Cuenta.class, "TcKalanCuentasMovimientosDto", params);
            clone.setIdBanco(null);
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
	
  private Boolean insert(Session sesion) throws Exception {
    Boolean regresar= Boolean.TRUE;
    try {
      super.addCuenta(sesion, this.cuenta);
      Cuenta clone= this.cuenta.clone();
      clone.setIdEmpresaDestino(this.cuenta.getIdCuentaMovimiento());
      super.addCuenta(sesion, clone);
      sesion.flush();
      this.cuenta.setIdEmpresaDestino(clone.getIdCuentaMovimiento());
      regresar= DaoFactory.getInstance().update(sesion, this.cuenta)>= 0L;
		} // try
		catch (Exception e) {
			throw e;
		} // catch
    return regresar;
  }
  
  private Boolean update(Session sesion) throws Exception {
    Boolean regresar          = Boolean.TRUE;
    Map<String, Object> params= new HashMap<>();
    try {
      this.cuenta.setIdBanco(null);
      super.updateCuenta(sesion, this.cuenta);
      params.put(Constantes.SQL_CONDICION, "tc_kalan_cuentas_movimientos.id_cuenta_movimiento= "+ this.cuenta.getIdEmpresaDestino());
      Cuenta clone= (Cuenta)DaoFactory.getInstance().toEntity(sesion, Cuenta.class, "TcKalanCuentasMovimientosDto", params);
      clone.setIdBanco(null);
      clone.setIdEmpresa(this.cuenta.getIdDestino());
      clone.setIdEmpresaCuenta(this.cuenta.getIdDestinoCuenta());
      clone.setIdCuentaEstatus(this.cuenta.getIdCuentaEstatus());
      clone.setFechaAplicacion(this.cuenta.getFechaAplicacion());
      clone.setFechaPago(this.cuenta.getFechaPago());
      clone.setIdTipoMedioPago(this.cuenta.getIdTipoMedioPago());
      clone.setImporte(this.cuenta.getImporte());
      clone.setReferencia(this.cuenta.getReferencia());
      regresar= super.updateCuenta(sesion, clone);
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
