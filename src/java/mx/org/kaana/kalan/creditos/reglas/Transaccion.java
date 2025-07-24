package mx.org.kaana.kalan.creditos.reglas;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.kalan.creditos.beans.Credito;
import mx.org.kaana.kalan.db.dto.TcKalanCreditosPagosDto;
import mx.org.kaana.kalan.db.dto.TcKalanCreditosBitacoraDto;
import mx.org.kaana.kalan.db.dto.TcKalanCreditosDto;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

  private static final Log LOG= LogFactory.getLog(Transaccion.class);
	private Credito credito;
	private TcKalanCreditosBitacoraDto bitacora;
	private String messageError;
	
	public Transaccion(Credito credito) {
		this.credito= credito;
	}
	
	public Transaccion(Long idCredito) throws Exception {
    this(idCredito, new TcKalanCreditosBitacoraDto());
	}
  
	public Transaccion(Long idCredito, TcKalanCreditosBitacoraDto bitacora) throws Exception {
    Map<String, Object> params = new HashMap<>();
    try {      
      params.put("idCredito", idCredito);      
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
          this.credito.setSaldo(this.credito.getImporte()- this.toSaldo(sesion));
          regresar= DaoFactory.getInstance().update(sesion, this.credito)>= 0L;
          this.toBitacora(sesion, this.credito.getIdCreditoEstatus());
          // QUEDA PENDIENTE ACTUALIZAR LA CUENTA DE BANCO
          break;
				case DEPURAR:
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
    try {
      this.bitacora= new TcKalanCreditosBitacoraDto(
        idCreditoEstatus, // Long idCreditoEstatus
        -1L, // Long idCreditoBitacora, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        null, // String justificacion, 
        this.credito.getIdCredito() // Long idCredito
      );
      DaoFactory.getInstance().insert(sesion, this.bitacora);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
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

}
