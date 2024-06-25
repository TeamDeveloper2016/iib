package mx.org.kaana.mantic.lotes.reglas;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import org.hibernate.Session;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import static mx.org.kaana.kajool.enums.ESql.DELETE;
import static mx.org.kaana.kajool.enums.ESql.INSERT;
import static mx.org.kaana.kajool.enums.ESql.SELECT;
import static mx.org.kaana.kajool.enums.ESql.UPDATE;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticLotesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticLotesDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticLotesEspecialesDto;
import mx.org.kaana.mantic.db.dto.TcManticLotesPromediosDto;
import mx.org.kaana.mantic.lotes.beans.Lote;
import mx.org.kaana.mantic.lotes.beans.Partida;
import org.apache.log4j.Logger;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Transaccion extends IBaseTnx implements Serializable {

  private static final Logger LOG = Logger.getLogger(Transaccion.class);
	private static final long serialVersionUID= 1069204157451117549L;
 
	protected Lote orden;	
	private String messageError;
	private TcManticLotesBitacoraDto bitacora;

	public Transaccion(Lote orden) {
		this.orden= orden;
	}
  
	protected void setMessageError(String messageError) {
		this.messageError=messageError;
	}

	public String getMessageError() {
		return messageError;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar          = false;
		try {
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" el lote");
			switch(accion) {
				case AGREGAR:
          regresar= this.toAddLote(sesion);
					break;
				case MODIFICAR:
          regresar= this.toUpdateLote(sesion);
          break;				
				case ELIMINAR:
          regresar= this.toDeleteLote(sesion);
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.orden.setIdLoteEstatus(this.bitacora.getIdLoteEstatus());
						regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
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
		return regresar;
	}	// ejecutar

  private Boolean toAddLote(Session sesion) throws Exception {
    Boolean regresar     = Boolean.FALSE;
    Siguiente consecutivo= null;
    try {      
      consecutivo= this.toSiguiente(sesion);
      this.orden.setConsecutivo(consecutivo.getConsecutivo());
      this.orden.setOrden(consecutivo.getOrden());
      this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
      this.orden.setIdUsuario(JsfBase.getIdUsuario());
      this.orden.setCantidad(this.toSumPartidas());
      DaoFactory.getInstance().insert(sesion, this.orden);
      this.toAddBitacora(sesion);
      regresar= this.toFillPartidas(sesion);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    return regresar;
  }
  
  private Boolean toUpdateLote(Session sesion) throws Exception {
    Boolean regresar= Boolean.FALSE;
    try {      
      this.orden.setIdUsuario(JsfBase.getIdUsuario());
      this.orden.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
      this.orden.setCantidad(this.toSumPartidas());
      DaoFactory.getInstance().update(sesion, this.orden);
      this.toAddBitacora(sesion);
      regresar= this.toFillPartidas(sesion);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    return regresar;
  }
  
  private Boolean toDeleteLote(Session sesion) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idLote", this.orden.getIdLote());
      DaoFactory.getInstance().deleteAll(sesion, TcManticLotesBitacoraDto.class, params);
      DaoFactory.getInstance().deleteAll(sesion, TcManticLotesPromediosDto.class, params);
      DaoFactory.getInstance().deleteAll(sesion, TcManticLotesEspecialesDto.class, params);
      DaoFactory.getInstance().deleteAll(sesion, TcManticLotesDetallesDto.class, params);
      regresar= DaoFactory.getInstance().delete(sesion, this.orden)> 0L;
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
  
  private Boolean toFillPartidas(Session sesion) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    try {      
      for (Partida item: this.orden.getPartidas()) {
        item.setIdUsuario(JsfBase.getIdUsuario());
        item.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        switch(item.getSql()) {
          case SELECT:
            break;
          case UPDATE:
            regresar= DaoFactory.getInstance().update(sesion, item)> 0L;
            break;
          case DELETE:
            regresar= DaoFactory.getInstance().delete(sesion, item)> 0L;
            break;
          case INSERT:
            item.setIdLote(this.orden.getIdLote());
            regresar= DaoFactory.getInstance().insert(sesion, item)> 0L;
            break;
        } // switch    
        item.setSql(ESql.SELECT);
      } // for  
      regresar= Boolean.TRUE;
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
			params.put("idEmpresa", this.orden.getIdEmpresa());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticLotesDto", "siguiente", params, "siguiente");
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
  
  private Double toSumPartidas() throws Exception {
    Double regresar= 0D;
    try {      
      for (Partida item: this.orden.getPartidas()) {
        switch(item.getSql()) {
          case SELECT:
            regresar+= item.getCantidad();
            break;
          case UPDATE:
            regresar+= item.getCantidad();
            break;
          case DELETE:
            break;
          case INSERT:
            regresar+= item.getCantidad();
            break;
        } // switch    
      } // for  
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    return regresar;
  }
  
  private Boolean toAddBitacora(Session sesion) throws Exception {
    Boolean regresar= Boolean.FALSE;
    try {      
      this.bitacora= new TcManticLotesBitacoraDto(
        null, // String justificacion, 
        this.orden.getIdLoteEstatus(), // Long idLoteEstatus, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        this.orden.getIdLote(), // Long idLote, 
        -1L // Long idLoteBitacora
      );
      regresar= DaoFactory.getInstance().insert(sesion, bitacora)> 0L;
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    return regresar;
  }
  
} 