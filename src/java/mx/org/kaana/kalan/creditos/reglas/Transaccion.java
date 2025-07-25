package mx.org.kaana.kalan.creditos.reglas;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.kalan.creditos.beans.Afectacion;
import mx.org.kaana.kalan.creditos.beans.Credito;
import mx.org.kaana.kalan.db.dto.TcKalanCreditosBitacoraDto;
import mx.org.kaana.kalan.db.dto.TcKalanCreditosPagosDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

  private static final Log LOG= LogFactory.getLog(Transaccion.class);
	private Credito credito;
	private Afectacion afectacion;
	private TcKalanCreditosBitacoraDto bitacora;
	private String messageError;
	
	public Transaccion(Credito credito) {
		this.credito= credito;
	}
	
	public Transaccion(Credito credito, Afectacion afectacion) {
		this.credito   = credito;
    this.afectacion= afectacion;
	}
	
	public Transaccion(Long idCredito) throws Exception {
    this(idCredito, new TcKalanCreditosBitacoraDto());
	}
  
	public Transaccion(Long idCredito, TcKalanCreditosBitacoraDto bitacora) throws Exception {
    Map<String, Object> params = new HashMap<>();
    try {      
      params.put(Constantes.SQL_CONDICION, "id_credito= "+ idCredito);      
  		this.credito = (Credito)DaoFactory.getInstance().toEntity(Credito.class, "TcKalanCreditosDto", params);
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
      params.put(Constantes.SQL_CONDICION, "id_credito= "+ afectacion.getIdCredito());      
  		this.credito   = (Credito)DaoFactory.getInstance().toEntity(Credito.class, "TcKalanCreditosDto", params);
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
          this.credito.setConsecutivo(consecutivo.getConsecutivo());
          this.credito.setEjercicio(consecutivo.getEjercicio());
          this.credito.setOrden(consecutivo.getOrden());
          this.credito.setIdUsuario(JsfBase.getIdUsuario());
          this.credito.setIdCreditoEstatus(Objects.equals(accion, EAccion.AGREGAR)? 1L: 2L);
          this.credito.setSaldo(this.credito.getImporte());
          regresar= DaoFactory.getInstance().insert(sesion, this.credito)>= 0L;
          this.toBitacora(sesion, this.credito.getIdCreditoEstatus());
          // QUEDA PENDIENTE ACTUALIZAR LA CUENTA DE BANCO
          break;
        case MODIFICAR:
          regresar= this.toCheckEstatus(sesion, Boolean.FALSE);
          break;
				case ELIMINAR:
          Map<String, Object> params = new HashMap<>();
          params.put("idCredito", this.credito.getIdCredito());            
          DaoFactory.getInstance().deleteAll(sesion, TcKalanCreditosBitacoraDto.class, params);
          DaoFactory.getInstance().deleteAll(sesion, TcKalanCreditosPagosDto.class, params);
          // QUEDA PENDIENTE ACTUALIZAR LA CUENTA DE BANCO
					regresar= DaoFactory.getInstance().delete(sesion, this.credito)>= 1L;
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.credito.setIdCreditoEstatus(this.bitacora.getIdCreditoEstatus());
            regresar= DaoFactory.getInstance().update(sesion, this.credito)>= 1L;
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
	
  private void toBitacora(Session sesion, Long idCreditoEstatus) throws Exception {
    this.toBitacora(sesion, idCreditoEstatus, null);
  }
  
  private void toBitacora(Session sesion, Long idCreditoEstatus, String justificacion) throws Exception {
    try {
      this.bitacora= new TcKalanCreditosBitacoraDto(
        idCreditoEstatus, // Long idCreditoEstatus
        -1L, // Long idCreditoBitacora, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        justificacion, // String justificacion, 
        this.credito.getIdCredito() // Long idCredito
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
      this.credito.setSaldo(this.credito.getImporte()- this.toSaldo(sesion));
      if(this.credito.getSaldo()<= 0D)
        this.credito.setIdCreditoEstatus(3L); // TERMINADO
      else
        if(Objects.equals(this.credito.getImporte(), this.credito.getSaldo()))
          this.credito.setIdCreditoEstatus(2L); // ACTIVO
        else
          this.credito.setIdCreditoEstatus(6L); // PARCIAL
      regresar= DaoFactory.getInstance().update(sesion, this.credito)>= 0L;
      if(Objects.equals(this.afectacion, null))
        this.toBitacora(sesion, this.credito.getIdCreditoEstatus());
      else
        if(depurar)
          this.toBitacora(sesion, this.credito.getIdCreditoEstatus(), "SE ELIMINO UN "+ (Objects.equals(this.afectacion.getIdTipoAfectacion(), 1L)? "CARGO": "ABONO")+ " POR "+ this.afectacion.getImporte());
        else
          this.toBitacora(sesion, this.credito.getIdCreditoEstatus(), "SE REGISTRO UN "+ (Objects.equals(this.afectacion.getIdTipoAfectacion(), 1L)? "CARGO": "ABONO")+ " POR "+ this.afectacion.getImporte());
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
			params.put("idCredito", this.credito.getIdCredito());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKalanCreditosPagosDto", "pagos", params, "total");
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
			Value next= DaoFactory.getInstance().toField(sesion, "TcKalanCreditosDto", "siguiente", params, "siguiente");
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
			Value next= DaoFactory.getInstance().toField(sesion, "TcKalanCreditosPagosDto", "siguiente", params, "siguiente");
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
			params.put("idCredito", this.afectacion.getIdCredito());
			params.put("idCreditoPago", this.afectacion.getIdCreditoPago());
			regresar= DaoFactory.getInstance().deleteAll(sesion, TcKalanCreditosPagosDto.class, "depurar", params)> 0L;
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
 
}
