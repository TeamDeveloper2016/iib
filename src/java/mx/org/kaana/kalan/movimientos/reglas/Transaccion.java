package mx.org.kaana.kalan.movimientos.reglas;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.kalan.db.dto.TcKalanMovimientosBitacoraDto;
import mx.org.kaana.kalan.movimientos.beans.Movimiento;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private Movimiento movimiento;	
  private TcKalanMovimientosBitacoraDto bitacora;  
	private String messageError;

	public Transaccion(Movimiento movimiento) {
		this.movimiento= movimiento;		
	} 
  
	public Transaccion(Movimiento movimiento, TcKalanMovimientosBitacoraDto bitacora) {
		this.movimiento= movimiento;		
    this.bitacora= bitacora;
	} 

	public String getMessageError() {
		return messageError;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar          = false;
    Map<String, Object> params= new HashMap<>();
		try {
			this.messageError= "Ocurrio un error al ".concat(accion.name().toLowerCase()).concat(" el registrar el gasto");
      this.movimiento.setIdUsuario(JsfBase.getIdUsuario());
      this.movimiento.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			switch(accion) {
				case AGREGAR:
          Siguiente consecutivo= this.toSiguiente(sesion);	
          this.movimiento.setConsecutivo(consecutivo.getConsecutivo());			
          this.movimiento.setEjercicio(Long.valueOf(Fecha.getAnioActual()));			
          this.movimiento.setOrden(consecutivo.getOrden());
          if(Objects.equals(this.movimiento.getIdAnticipo(), 2L)) 
            this.movimiento.setIdCliente(null);
          regresar= DaoFactory.getInstance().insert(sesion, this.movimiento)> 0L;
          this.bitacora= new TcKalanMovimientosBitacoraDto(
            -1L, // Long idMovimientoBitacora, 
            null, // String justificacion, 
            JsfBase.getIdUsuario(), // Long idUsuario, 
            this.movimiento.getIdMovimientoEstatus(), // Long idMovimientoEstatus, 
            this.movimiento.getIdEmpresaMovimiento() // Long idEmpresaMovimiento                  
          );
          regresar= DaoFactory.getInstance().insert(sesion, this.bitacora)> 0L;
					break;
				case MODIFICAR:
          if(Objects.equals(this.movimiento.getIdAnticipo(), 2L)) 
            this.movimiento.setIdCliente(null);
          regresar= DaoFactory.getInstance().update(sesion, this.movimiento)> 0L;
          this.bitacora= new TcKalanMovimientosBitacoraDto(
            -1L, // Long idMovimientoBitacora, 
            null, // String justificacion, 
            JsfBase.getIdUsuario(), // Long idUsuario, 
            this.movimiento.getIdMovimientoEstatus(), // Long idMovimientoEstatus, 
            this.movimiento.getIdEmpresaMovimiento() // Long idEmpresaMovimiento                  
          );
          regresar= DaoFactory.getInstance().insert(sesion, this.bitacora)> 0L;
					break;				
				case ELIMINAR:
          params.put("idEmpresaMovimiento", this.movimiento.getIdEmpresaMovimiento());
          DaoFactory.getInstance().deleteAll(sesion, TcKalanMovimientosBitacoraDto.class, params);
          regresar= DaoFactory.getInstance().delete(sesion, this.movimiento)> 0L;
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.movimiento.setIdMovimientoEstatus(this.bitacora.getIdMovimientoEstatus());
            if(Objects.equals(this.movimiento.getIdAnticipo(), 2L)) 
              this.movimiento.setIdCliente(null);
            regresar= DaoFactory.getInstance().update(sesion, this.movimiento)>= 1L;
					} // if
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
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

	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idTipoMovimiento", this.movimiento.getIdTipoMovimiento());			
			params.put("ejercicio", this.getCurrentYear());			
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKalanEmpresasMovimientosDto", "siguiente", params, "siguiente");
			if(next.getData()!= null)
				regresar= new Siguiente(next.toLong());
			else
				regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L); 
		} // try		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toSiguiente  
  
}