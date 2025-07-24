package mx.org.kaana.kalan.prestamos.reglas;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.kalan.prestamos.beans.Afectacion;
import mx.org.kaana.kalan.prestamos.beans.Prestamo;
import mx.org.kaana.kalan.db.dto.TcKalanPrestamosPagosDto;
import mx.org.kaana.kalan.db.dto.TcKalanPrestamosBitacoraDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

  private static final Log LOG= LogFactory.getLog(Transaccion.class);
	private Prestamo prestamo;
	private Afectacion afectacion;
	private TcKalanPrestamosBitacoraDto bitacora;
	private String messageError;
	
	public Transaccion(Prestamo prestamo) {
		this.prestamo= prestamo;
	}
	
	public Transaccion(Prestamo prestamo, Afectacion afectacion) {
		this.prestamo   = prestamo;
    this.afectacion= afectacion;
	}
	
	public Transaccion(Long idPrestamo) throws Exception {
    this(idPrestamo, new TcKalanPrestamosBitacoraDto());
	}
  
	public Transaccion(Long idPrestamo, TcKalanPrestamosBitacoraDto bitacora) throws Exception {
    Map<String, Object> params = new HashMap<>();
    try {      
      params.put(Constantes.SQL_CONDICION, "id_prestamo= "+ idPrestamo);      
  		this.prestamo = (Prestamo)DaoFactory.getInstance().toEntity(Prestamo.class, "TcKalanPrestamosDto", params);
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
    boolean regresar= Boolean.FALSE;
    try {
      switch(accion) {
        case AGREGAR:
        case PROCESAR:
          Siguiente consecutivo= this.toSiguiente(sesion);
          this.prestamo.setConsecutivo(consecutivo.getConsecutivo());
          this.prestamo.setEjercicio(consecutivo.getEjercicio());
          this.prestamo.setOrden(consecutivo.getOrden());
          this.prestamo.setIdUsuario(JsfBase.getIdUsuario());
          this.prestamo.setIdPrestamoEstatus(Objects.equals(accion, EAccion.AGREGAR)? 1L: 2L);
          this.prestamo.setSaldo(this.prestamo.getImporte());
          regresar= DaoFactory.getInstance().insert(sesion, this.prestamo)>= 0L;
          this.toBitacora(sesion, this.prestamo.getIdPrestamoEstatus());
          // QUEDA PENDIENTE ACTUALIZAR LA CUENTA DE BANCO
          break;
        case MODIFICAR:
          regresar= this.toCheckEstatus(sesion);
          break;
				case ELIMINAR:
          Map<String, Object> params = new HashMap<>();
          params.put("idPrestamo", this.prestamo.getIdPrestamo());            
          DaoFactory.getInstance().deleteAll(sesion, TcKalanPrestamosBitacoraDto.class, params);
          DaoFactory.getInstance().deleteAll(sesion, TcKalanPrestamosPagosDto.class, params);
          // QUEDA PENDIENTE ACTUALIZAR LA CUENTA DE BANCO
					regresar= DaoFactory.getInstance().delete(sesion, this.prestamo)>= 1L;
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.prestamo.setIdPrestamoEstatus(this.bitacora.getIdPrestamoEstatus());
            regresar= DaoFactory.getInstance().update(sesion, this.prestamo)>= 1L;
					} // if
					break;
        case COMPLEMENTAR:
          Siguiente siguiente= this.toContinuar(sesion);
          this.afectacion.setConsecutivo(siguiente.getConsecutivo());
          this.afectacion.setEjercicio(siguiente.getEjercicio());
          this.afectacion.setOrden(siguiente.getOrden());
          DaoFactory.getInstance().insert(sesion, this.afectacion);
          regresar= this.toCheckEstatus(sesion);
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
    return regresar;
  } 
	
  private void toBitacora(Session sesion, Long idPrestamoEstatus) throws Exception {
    try {
      this.bitacora= new TcKalanPrestamosBitacoraDto(
        this.prestamo.getIdPrestamo(), // Long idPrestamo
        -1L, // Long idPrestamoBitacora, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        null, // String justificacion, 
        idPrestamoEstatus // Long idPrestamoEstatus
      );
      DaoFactory.getInstance().insert(sesion, this.bitacora);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
  }

  private Boolean toCheckEstatus(Session sesion) throws Exception {
    Boolean regresar= Boolean.TRUE;
    try {
      this.prestamo.setSaldo(this.prestamo.getImporte()- this.toSaldo(sesion));
      if(this.prestamo.getSaldo()<= 0D)
        this.prestamo.setIdPrestamoEstatus(3L); // TERMINADO
      else
        if(Objects.equals(this.prestamo.getImporte(), this.prestamo.getSaldo()))
          this.prestamo.setIdPrestamoEstatus(2L); // ACTIVO
        else
          this.prestamo.setIdPrestamoEstatus(6L); // PARCIAL
      regresar= DaoFactory.getInstance().update(sesion, this.prestamo)>= 0L;
      this.toBitacora(sesion, this.prestamo.getIdPrestamoEstatus());
      // QUEDA PENDIENTE ACTUALIZAR LA CUENTA DE BANCO
		} // try
		catch (Exception e) {
			throw e;
		} // catch
    return regresar;
  }
  
	private Double toSaldo(Session sesion) throws Exception {
		Double regresar           = 0D;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idPrestamo", this.prestamo.getIdPrestamo());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKalanPrestamosPagosDto", "pagos", params, "total");
			if(next.getData()!= null)
			  regresar= next.toDouble();
		} // try
		catch (Exception e) {
			throw e;
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
			params.put("ejercicio", this.getCurrentYear());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKalanPrestamosDto", "siguiente", params, "siguiente");
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

	private Siguiente toContinuar(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("ejercicio", this.getCurrentYear());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKalanPrestamosPagosDto", "siguiente", params, "siguiente");
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

}
