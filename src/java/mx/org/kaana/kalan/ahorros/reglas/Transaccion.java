package mx.org.kaana.kalan.ahorros.reglas;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.kalan.ahorros.beans.Afectacion;
import mx.org.kaana.kalan.ahorros.beans.Ahorro;
import mx.org.kaana.kalan.db.dto.TcKalanAhorrosPagosDto;
import mx.org.kaana.kalan.db.dto.TcKalanAhorrosBitacoraDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

  private static final Log LOG= LogFactory.getLog(Transaccion.class);
	private Ahorro ahorro;
	private Afectacion afectacion;
	private TcKalanAhorrosBitacoraDto bitacora;
	private String messageError;
	
	public Transaccion(Ahorro ahorro) {
		this.ahorro= ahorro;
	}
	
	public Transaccion(Ahorro ahorro, Afectacion afectacion) {
		this.ahorro   = ahorro;
    this.afectacion= afectacion;
	}
	
	public Transaccion(Long idAhorro) throws Exception {
    this(idAhorro, new TcKalanAhorrosBitacoraDto());
	}
  
	public Transaccion(Long idAhorro, TcKalanAhorrosBitacoraDto bitacora) throws Exception {
    Map<String, Object> params = new HashMap<>();
    try {      
      params.put(Constantes.SQL_CONDICION, "id_ahorro= "+ idAhorro);      
  		this.ahorro = (Ahorro)DaoFactory.getInstance().toEntity(Ahorro.class, "TcKalanAhorrosDto", params);
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
      params.put(Constantes.SQL_CONDICION, "id_ahorro= "+ afectacion.getIdAhorro());      
  		this.ahorro    = (Ahorro)DaoFactory.getInstance().toEntity(Ahorro.class, "TcKalanAhorrosDto", params);
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
      switch(accion) {
        case AGREGAR:
        case PROCESAR:
          Siguiente consecutivo= this.toSiguiente(sesion);
          this.ahorro.setConsecutivo(consecutivo.getConsecutivo());
          this.ahorro.setEjercicio(consecutivo.getEjercicio());
          this.ahorro.setOrden(consecutivo.getOrden());
          this.ahorro.setIdUsuario(JsfBase.getIdUsuario());
          this.ahorro.setIdAhorroEstatus(Objects.equals(accion, EAccion.AGREGAR)? 1L: 2L);
          this.ahorro.setSaldo(this.ahorro.getImporte());
          DaoFactory.getInstance().insert(sesion, this.ahorro);
          this.toBitacora(sesion, this.ahorro.getIdAhorroEstatus());
          regresar= this.toCuotas(sesion);
          // QUEDA PENDIENTE ACTUALIZAR LA CUENTA DE BANCO
          break;
        case MODIFICAR:
          this.toCheckEstatus(sesion, Boolean.FALSE);
          regresar= this.toCuotas(sesion);
          break;
				case ELIMINAR:
          Map<String, Object> params = new HashMap<>();
          params.put("idAhorro", this.ahorro.getIdAhorro());            
          DaoFactory.getInstance().deleteAll(sesion, TcKalanAhorrosBitacoraDto.class, params);
          DaoFactory.getInstance().deleteAll(sesion, TcKalanAhorrosPagosDto.class, params);
          // QUEDA PENDIENTE ACTUALIZAR LA CUENTA DE BANCO
					regresar= DaoFactory.getInstance().delete(sesion, this.ahorro)>= 1L;
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.ahorro.setIdAhorroEstatus(this.bitacora.getIdAhorroEstatus());
            regresar= DaoFactory.getInstance().update(sesion, this.ahorro)>= 1L;
					} // if
					break;
        case COMPLEMENTAR:
          Siguiente siguiente= this.toContinuar(sesion);
          this.afectacion.setConsecutivo(siguiente.getConsecutivo());
          this.afectacion.setEjercicio(siguiente.getEjercicio());
          this.afectacion.setOrden(siguiente.getOrden());
          DaoFactory.getInstance().insert(sesion, this.afectacion);
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
	
  private void toBitacora(Session sesion, Long idAhorroEstatus) throws Exception {
    this.toBitacora(sesion, idAhorroEstatus, null);
  }
  
  private void toBitacora(Session sesion, Long idAhorroEstatus, String justificacion) throws Exception {
    try {
      this.bitacora= new TcKalanAhorrosBitacoraDto(
        this.ahorro.getIdAhorro(), // Long idAhorro
        -1L, // Long idAhorroBitacora, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        justificacion, // String justificacion, 
        idAhorroEstatus // Long idAhorroEstatus
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
      this.ahorro.setSaldo(this.ahorro.getImporte()- this.toSaldo(sesion));
      if(this.ahorro.getSaldo()<= 0D)
        this.ahorro.setIdAhorroEstatus(3L); // TERMINADO
      else
        if(Objects.equals(this.ahorro.getImporte(), this.ahorro.getSaldo()))
          this.ahorro.setIdAhorroEstatus(2L); // ACTIVO
        else
          this.ahorro.setIdAhorroEstatus(6L); // PARCIAL
      regresar= DaoFactory.getInstance().update(sesion, this.ahorro)>= 0L;
      if(Objects.equals(this.afectacion, null))
        this.toBitacora(sesion, this.ahorro.getIdAhorroEstatus());
      else 
        if(depurar)
          this.toBitacora(sesion, this.ahorro.getIdAhorroEstatus(), "SE ELIMINO UN "+ (Objects.equals(this.afectacion.getIdTipoAfectacion(), 1L)? "CARGO": "ABONO")+ " POR "+ this.afectacion.getImporte());
        else
          this.toBitacora(sesion, this.ahorro.getIdAhorroEstatus(), "SE REGISTRO UN "+ (Objects.equals(this.afectacion.getIdTipoAfectacion(), 1L)? "CARGO": "ABONO")+ " POR "+ this.afectacion.getImporte());
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
			params.put("idAhorro", this.ahorro.getIdAhorro());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKalanAhorrosPagosDto", "pagos", params, "total");
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
			Value next= DaoFactory.getInstance().toField(sesion, "TcKalanAhorrosDto", "siguiente", params, "siguiente");
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
			Value next= DaoFactory.getInstance().toField(sesion, "TcKalanAhorrosPagosDto", "siguiente", params, "siguiente");
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
		Boolean regresar          = Boolean.FALSE;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idAhorro", this.afectacion.getIdAhorro());
			params.put("idAhorroPago", this.afectacion.getIdAhorroPago());
			regresar= DaoFactory.getInstance().deleteAll(sesion, TcKalanAhorrosPagosDto.class, "depurar", params)> 0L;
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

  private Boolean toCuotas(Session sesion) throws Exception {  
		Boolean regresar   = Boolean.FALSE;
    Siguiente siguiente= null;
		try {
      for (Afectacion item: this.ahorro.getCuotas()) {
        switch(item.getSql()) {
          case SELECT:
            break;
          case INSERT:
            siguiente= this.toContinuar(sesion);
            item.setConsecutivo(siguiente.getConsecutivo());
            item.setEjercicio(siguiente.getEjercicio());
            item.setOrden(siguiente.getOrden());
            item.setIdAhorro(this.ahorro.getIdAhorro());
            item.setIdEmpresa(this.ahorro.getIdEmpresa());
            item.setIdEmpresaCuenta(this.ahorro.getIdEmpresaCuenta());
            item.setIdTipoMedioPago(this.ahorro.getIkTipoMedioPago().getKey());
            item.setIdBanco(Objects.equals(this.ahorro.getIkBanco().getKey(), -1L)? null: this.ahorro.getIkBanco().getKey());
            item.setReferencia(this.ahorro.getReferencia());
            item.setIdTipoAfectacion(2L); // ABONO
            item.setIdUsuario(JsfBase.getIdUsuario());
            regresar= DaoFactory.getInstance().insert(sesion, item)> 0L;
            break;
          case UPDATE:
            item.setIdEmpresa(this.ahorro.getIdEmpresa());
            item.setIdEmpresaCuenta(this.ahorro.getIdEmpresaCuenta());
            item.setIdTipoMedioPago(this.ahorro.getIkTipoMedioPago().getKey());
            item.setIdBanco(Objects.equals(this.ahorro.getIkBanco().getKey(), -1L)? null: this.ahorro.getIkBanco().getKey());
            item.setReferencia(this.ahorro.getReferencia());
            regresar= DaoFactory.getInstance().update(sesion, item)> 0L;
            break;
          case DELETE:
            item.setIdAhorroControl(3L); // CANCELADO
            regresar= DaoFactory.getInstance().update(sesion, item)> 0L;
            break;
        } // switch
      } // for
      regresar= Boolean.TRUE;
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		return regresar;
  }

}
