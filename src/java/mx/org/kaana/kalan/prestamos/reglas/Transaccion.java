package mx.org.kaana.kalan.prestamos.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.kalan.cuentas.enums.ECuentasOrigenes;
import mx.org.kaana.kalan.cuentas.enums.EEstatusCuentas;
import mx.org.kaana.kalan.cuentas.enums.ETipoAfectacion;
import mx.org.kaana.kalan.cuentas.reglas.IBaseCuenta;
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

public class Transaccion extends IBaseCuenta implements Serializable {

  private static final Log LOG= LogFactory.getLog(Transaccion.class);
  private static final long serialVersionUID = -4642395161013171L;
  
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

	public Transaccion(Afectacion afectacion) throws Exception {
    Map<String, Object> params = new HashMap<>();
    try {      
      params.put(Constantes.SQL_CONDICION, "id_prestamo= "+ afectacion.getIdPrestamo());      
  		this.prestamo   = (Prestamo)DaoFactory.getInstance().toEntity(Prestamo.class, "TcKalanPrestamosDto", params);
  		this.afectacion= afectacion;
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
      this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" de prestamos ");
      switch(accion) {
        case AGREGAR:
          Siguiente consecutivo= this.toSiguiente(sesion);
          this.prestamo.setConsecutivo(consecutivo.getConsecutivo());
          this.prestamo.setEjercicio(consecutivo.getEjercicio());
          this.prestamo.setOrden(consecutivo.getOrden());
          this.prestamo.setIdUsuario(JsfBase.getIdUsuario());
          this.prestamo.setSaldo(this.prestamo.getImporte());
          regresar= DaoFactory.getInstance().insert(sesion, this.prestamo)>= 0L;
          this.toBitacora(sesion, this.prestamo.getIdPrestamoEstatus());
          // QUEDA PENDIENTE ACTUALIZAR LA CUENTA DE BANCO
          if(Objects.equals(this.prestamo.getIdPrestamoEstatus(), 2L))
            this.toControlCuentaCargo(sesion);
          break;
        case MODIFICAR:
          // QUEDA PENDIENTE ACTUALIZAR LA CUENTA DE BANCO
          if(Objects.equals(this.prestamo.getIdPrestamoEstatus(), 2L))
            this.toControlCuentaCargo(sesion);
          regresar= this.toCheckEstatus(sesion, Boolean.FALSE);
          break;
				case ELIMINAR:
          Map<String, Object> params = new HashMap<>();
          params.put("idPrestamo", this.prestamo.getIdPrestamo());            
          // QUEDA PENDIENTE ACTUALIZAR LA CUENTA DE BANCO
          this.toDeleteControlCuenta(sesion);
          DaoFactory.getInstance().deleteAll(sesion, TcKalanPrestamosBitacoraDto.class, params);
          DaoFactory.getInstance().deleteAll(sesion, TcKalanPrestamosPagosDto.class, params);
					regresar= DaoFactory.getInstance().delete(sesion, this.prestamo)>= 1L;
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.prestamo.setIdPrestamoEstatus(this.bitacora.getIdPrestamoEstatus());
            if(Objects.equals(this.prestamo.getIdPrestamoEstatus(), 2L))  // ACTIVO
              this.toControlCuentaCargo(sesion);
            if(Objects.equals(this.prestamo.getIdPrestamoEstatus(), 5L))  // CANCELADO
              this.toCancel(sesion);
            regresar= DaoFactory.getInstance().update(sesion, this.prestamo)>= 1L;
					} // if
					break;
        case COMPLEMENTAR:
          Siguiente siguiente= this.toContinuar(sesion);
          this.afectacion.setConsecutivo(siguiente.getConsecutivo());
          this.afectacion.setEjercicio(siguiente.getEjercicio());
          this.afectacion.setOrden(siguiente.getOrden());
          DaoFactory.getInstance().insert(sesion, this.afectacion);
          // QUEDA PENDIENTE ACTUALIZAR LA CUENTA DE BANCO
          this.toControlCuentaCargoPago(sesion, afectacion);
          regresar= this.toCheckEstatus(sesion, Boolean.FALSE);
					break;
        case DEPURAR:
          regresar= this.toDeletePago(sesion);
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
    this.toBitacora(sesion, idPrestamoEstatus, null);
  }
  
  private void toBitacora(Session sesion, Long idPrestamoEstatus, String justificacion) throws Exception {
    try {
      this.bitacora= new TcKalanPrestamosBitacoraDto(
        this.prestamo.getIdPrestamo(), // Long idPrestamo
        -1L, // Long idPrestamoBitacora, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        justificacion, // String justificacion, 
        idPrestamoEstatus // Long idPrestamoEstatus
      );
      DaoFactory.getInstance().insert(sesion, this.bitacora);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
  }

  private Boolean toCheckEstatus(Session sesion, Boolean depurar) throws Exception {
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
      if(Objects.equals(this.afectacion, null))
        this.toBitacora(sesion, this.prestamo.getIdPrestamoEstatus());
      else 
        if(depurar)
          this.toBitacora(sesion, this.prestamo.getIdPrestamoEstatus(), "SE ELIMINO UN "+ (Objects.equals(this.afectacion.getIdTipoAfectacion(), 1L)? "CARGO": "ABONO")+ " POR "+ this.afectacion.getImporte());
        else
          this.toBitacora(sesion, this.prestamo.getIdPrestamoEstatus(), "SE REGISTRO UN "+ (Objects.equals(this.afectacion.getIdTipoAfectacion(), 1L)? "CARGO": "ABONO")+ " POR "+ this.afectacion.getImporte());
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

  private Boolean toDeletePago(Session sesion) throws Exception {
		Boolean regresar          = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idPrestamo", this.afectacion.getIdPrestamo());
			params.put("idPrestamoPago", this.afectacion.getIdPrestamoPago());
      // QUEDA PENDIENTE ACTUALIZAR LA CUENTA DE BANCO
      Afectacion item= (Afectacion)DaoFactory.getInstance().toEntity(sesion, Afectacion.class, "TcKalanPrestamosPagosDto", "cuenta", params);
      this.toControlCuentaDeletePago(sesion, item);
			regresar= DaoFactory.getInstance().deleteAll(sesion, TcKalanPrestamosPagosDto.class, "cuentas", params)> 0L;
      if(regresar) 
        regresar= this.toCheckEstatus(sesion, Boolean.TRUE);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
  }

  private void toDeleteControlCuenta(Session sesion) throws Exception {
    try {      
      super.control(sesion, this.prestamo, Objects.equals(this.prestamo.getIdTipoAfectacion(), ETipoAfectacion.CARGO.getIdTipoAfectacion())? ECuentasOrigenes.PRESTAMOS_CARGOS: ECuentasOrigenes.PRESTAMOS_ABONOS, EEstatusCuentas.ELIMINADO.getIdEstatusCuenta());
      this.toControlCuentaDelete(sesion);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }

  private void toControlCuentaDelete(Session sesion) throws Exception {
    Map<String, Object> params = new HashMap<>();
    try {      
      params.put("idPrestamo", this.prestamo.getIdPrestamo());   
      List<Afectacion> items= (List<Afectacion>)DaoFactory.getInstance().toEntitySet(sesion, Afectacion.class, "TcKalanPrestamosPagosDto", "cuentas", params);
      for (Afectacion item: items) 
        this.toControlCuentaDeletePago(sesion, item);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  private void toControlCuentaDeletePago(Session sesion, Afectacion item) throws Exception {
    try {      
      super.control(sesion, item, Objects.equals(item.getIdTipoAfectacion(), ETipoAfectacion.CARGO.getIdTipoAfectacion())? ECuentasOrigenes.PRESTAMOS_CARGOS: ECuentasOrigenes.PRESTAMOS_ABONOS, EEstatusCuentas.ELIMINADO.getIdEstatusCuenta());
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }
  
  private void toControlCuentaCargo(Session sesion) throws Exception {
    try {
      super.control(sesion, this.prestamo, Objects.equals(this.prestamo.getIdTipoAfectacion(), ETipoAfectacion.CARGO.getIdTipoAfectacion())? ECuentasOrigenes.PRESTAMOS_CARGOS: ECuentasOrigenes.PRESTAMOS_ABONOS);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }

  private void toControlCuentaCargoPago(Session sesion, Afectacion item) throws Exception {
    try {
      super.control(sesion, item, Objects.equals(item.getIdTipoAfectacion(), ETipoAfectacion.CARGO.getIdTipoAfectacion())? ECuentasOrigenes.PRESTAMOS_CARGOS: ECuentasOrigenes.PRESTAMOS_ABONOS);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }

  private Boolean toCancel(Session sesion) throws Exception {
		Boolean regresar          = Boolean.FALSE;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idPrestamo", this.prestamo.getIdPrestamo());
      // QUEDA PENDIENTE ACTUALIZAR LA CUENTA DE BANCO
      super.control(sesion, this.prestamo, Objects.equals(this.prestamo.getIdTipoAfectacion(), ETipoAfectacion.CARGO.getIdTipoAfectacion())? ECuentasOrigenes.PRESTAMOS_CARGOS: ECuentasOrigenes.PRESTAMOS_ABONOS, EEstatusCuentas.CANCELADO.getIdEstatusCuenta());
      List<Afectacion> items= (List<Afectacion>)DaoFactory.getInstance().toEntitySet(sesion, Afectacion.class, "TcKalanPrestamosPagosDto", "cuentas", params);
      if(!Objects.equals(items, null))
        for (Afectacion item: items) 
          super.control(sesion, item, Objects.equals(item.getIdTipoAfectacion(), ETipoAfectacion.CARGO.getIdTipoAfectacion())? ECuentasOrigenes.PRESTAMOS_CARGOS: ECuentasOrigenes.PRESTAMOS_ABONOS, EEstatusCuentas.CANCELADO.getIdEstatusCuenta());
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
