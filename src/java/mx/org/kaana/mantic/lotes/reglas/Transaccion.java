package mx.org.kaana.mantic.lotes.reglas;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import org.hibernate.Session;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import static mx.org.kaana.kajool.enums.ESql.DELETE;
import static mx.org.kaana.kajool.enums.ESql.INSERT;
import static mx.org.kaana.kajool.enums.ESql.SELECT;
import static mx.org.kaana.kajool.enums.ESql.UPDATE;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticLotesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticLotesDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticLotesDto;
import mx.org.kaana.mantic.db.dto.TcManticLotesEspecialesDto;
import mx.org.kaana.mantic.db.dto.TcManticLotesPromediosDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasDetallesDto;
import mx.org.kaana.mantic.lotes.beans.Porcentaje;
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
 
	protected Long idLote;	
	protected Lote orden;	
	private String messageError;
	private TcManticLotesBitacoraDto bitacora;
  private List<Porcentaje> porcentajes;
  
	public Transaccion(Lote orden) {
		this(-1L, orden);
	}
  
	public Transaccion(Long idLote, Lote orden) {
    this.idLote= idLote;
		this.orden = orden;
	}
  
	public Transaccion(List<Porcentaje> porcentajes) {
		this.porcentajes= porcentajes;
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
				case TRANSFORMACION:
          regresar= this.toPromedios(sesion);
					break;
				case GENERAR:
          regresar= this.toFraccionar(sesion);
					break;
				case COMPLETO:
          regresar= this.toAddAgrupado(sesion);
					break;
				case COMPLEMENTAR:
          regresar= this.toUpdateAgrupado(sesion);
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
      this.orden.setOriginal(this.toSumPartidas());
      DaoFactory.getInstance().insert(sesion, this.orden);
      this.toAddBitacora(sesion);
      this.toFillPromedios(sesion);
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
      this.toFillPromedios(sesion);
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
      DaoFactory.getInstance().updateAll(sesion, TcManticNotasDetallesDto.class, params);
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
      params.put("idLote", this.orden.getIdLote());
      for (Partida item: this.orden.getPartidas()) {
        item.setIdUsuario(JsfBase.getIdUsuario());
        item.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        params.put("idNotaDetalle", item.getIdNotaDetalle());
        switch(item.getSql()) {
          case SELECT:
            break;
          case UPDATE:
            // DaoFactory.getInstance().updateAll(sesion, TcManticNotasDetallesDto.class, params, "actualizar");
            // regresar= DaoFactory.getInstance().update(sesion, item)> 0L;
            break;
          case DELETE:
            DaoFactory.getInstance().updateAll(sesion, TcManticNotasDetallesDto.class, params, "eliminar");
            regresar= DaoFactory.getInstance().delete(sesion, item)> 0L;
            break;
          case INSERT:
            if(item.getCantidad()>= item.getOriginal()) 
              DaoFactory.getInstance().updateAll(sesion, TcManticNotasDetallesDto.class, params, "agregar");
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
  
  private Boolean toFillPromedios(Session sesion) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    StringBuilder sb          = new StringBuilder();
    try {      
      params.put("idLote", this.orden.getIdLote());
      for (Partida item: this.orden.getPartidas()) 
        sb.append(item.getIdNotaDetalle()).append(", ");
      if(sb.length()> 0) {
        sb.delete(sb.length()- 2, sb.length());
        params.put("sortOrder", "order by tc_mantic_notas_promedios.id_nota_calidad");
        params.put("idNotaDetalle", sb.toString());
        List<Entity> promedios= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "VistaLotesDto", "promedios", params);
        for (Entity item: promedios) {
          TcManticLotesPromediosDto promedio= new TcManticLotesPromediosDto(
            JsfBase.getIdUsuario(), // Long idUsuario, 
            this.orden.getIdLote(), // Long idLote, 
            -1L, // Long idLotePromedio, 
            item.toDouble("cantidad"), // Double cantidad, 
            item.toDouble("porcentaje"), // Double porcentaje, 
            this.orden.getIdArticulo(), // Long idArticulo, 
            item.toLong("idNotaCalidad") // Long idNotaCalidad
          );
          params.put("idArticulo", this.orden.getIdArticulo());
          params.put("idNotaCalidad", item.toLong("idNotaCalidad"));
          Entity exist= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcManticLotesPromediosDto", "identically", params);
          if(!Objects.equals(exist, null) && !exist.isEmpty()) {
            promedio.setIdLotePromedio(exist.toLong("idLotePromedio"));
            DaoFactory.getInstance().update(sesion, promedio);
          } // if  
          else
            DaoFactory.getInstance().insert(sesion, promedio);
        } // for
      } // if
      else
        DaoFactory.getInstance().deleteAll(sesion, TcManticLotesPromediosDto.class, params);
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
  
  private Boolean toPromedios(Session sesion) throws Exception {
    Boolean regresar= Boolean.FALSE;
    try {
      for (Porcentaje item: this.porcentajes) {
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
    return regresar;
  }
  
  private Boolean toFraccionar(Session sesion) throws Exception {
    Boolean regresar= Boolean.FALSE;
    try {      
      TcManticLotesDto lote= (TcManticLotesDto)DaoFactory.getInstance().findById(sesion, TcManticLotesDto.class, this.idLote);
      lote.setCantidad(lote.getCantidad()- this.orden.getCantidad());
      lote.setIdUsuario(JsfBase.getIdUsuario());
      lote.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
      DaoFactory.getInstance().update(sesion, lote);
      for (Partida item: this.orden.getPartidas()) {
        if(item.getCantidad()> 0D) {
          TcManticLotesDetallesDto detalle= (TcManticLotesDetallesDto)DaoFactory.getInstance().findById(sesion, TcManticLotesDetallesDto.class, item.getIdLoteDetalle());
          if(item.getCantidad()>= detalle.getCantidad()) 
            DaoFactory.getInstance().delete(sesion, detalle);
          else 
            if(item.getCantidad()> 0) {
              detalle.setIdUsuario(JsfBase.getIdUsuario());
              detalle.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
              detalle.setCantidad(detalle.getCantidad()- item.getCantidad());
              DaoFactory.getInstance().update(sesion, detalle);
            } // else  
        } // if
      } // for  
      regresar= this.toAddNuevo(sesion, lote.getConsecutivo());
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    return regresar;
  }

  private Boolean toAddNuevo(Session sesion, String folio) throws Exception {
    Boolean regresar     = Boolean.FALSE;
    Siguiente consecutivo= null;
    int index            = 0;
    try {      
      consecutivo= this.toSiguiente(sesion);
      this.orden.setConsecutivo(consecutivo.getConsecutivo());
      this.orden.setOrden(consecutivo.getOrden());
      this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
      this.orden.setIdUsuario(JsfBase.getIdUsuario());
      String observaciones= this.orden.getObservaciones();
      this.orden.setObservaciones((Objects.equals(observaciones, null) || observaciones.trim().length()== 0? "": ", ")+ "ESTE LOTE SE FORMO DEL LOTE "+ folio+ " EL "+ Global.format(EFormatoDinamicos.FECHA_HORA, new Timestamp(Calendar.getInstance().getTimeInMillis())));
      DaoFactory.getInstance().insert(sesion, this.orden);
      this.toAddBitacora(sesion);
      while(index< this.orden.getPartidas().size()) {
        Partida partida= this.orden.getPartidas().get(index);
        partida.setIdLote(-1L);
        partida.setIdLoteDetalle(-1L);
        if((partida.getCantidad()<= 0D))
          this.orden.getPartidas().remove(index);
        else
          index++;
      } // while  
      this.toFillPromedios(sesion);
      this.toFillPartidas(sesion);
      regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    return regresar;
  }
  
  private Boolean toAddAgrupado(Session sesion) throws Exception {
    Boolean regresar     = Boolean.FALSE;
    Siguiente consecutivo= null;
    StringBuilder sb     = new StringBuilder();
    try {      
      consecutivo= this.toSiguiente(sesion);
      this.orden.setConsecutivo(consecutivo.getConsecutivo());
      this.orden.setOrden(consecutivo.getOrden());
      this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
      this.orden.setIdUsuario(JsfBase.getIdUsuario());
      this.orden.setCantidad(this.toSumPartidas());
      this.orden.setOriginal(this.toSumPartidas());
      sb.append("SE FORMO POR LOS LOTES (");
      for (Partida item: this.orden.getPartidas()) 
        sb.append(item.getLote()).append(", ");
      sb.delete(sb.length()- 2, sb.length());
      sb.append(")");
      this.orden.setObservaciones(sb.toString());
      DaoFactory.getInstance().insert(sesion, this.orden);
      this.toAddBitacora(sesion);
      this.toFillPromedios(sesion);
      this.toCancelLotes(sesion);
      regresar= this.toFillPartidas(sesion);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    return regresar;
  }
  
  private Boolean toUpdateAgrupado(Session sesion) throws Exception {
    Boolean regresar= Boolean.FALSE;
    try {      
      this.orden.setIdUsuario(JsfBase.getIdUsuario());
      this.orden.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
      this.orden.setCantidad(this.toSumPartidas());
      DaoFactory.getInstance().update(sesion, this.orden);
      this.toAddBitacora(sesion);
      this.toFillPromedios(sesion);
      this.toCancelLotes(sesion);
      regresar= this.toFillPartidas(sesion);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    return regresar;
  }
  
  private Boolean toCancelLotes(Session sesion) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    Map<Long, String> lotes   = new HashMap<>();
    try {      
      // SE SACAN TODOS LOS LOTES Y SE AGRUPAN LOS LOTES DETALLES DEL LOTE
      for (Partida item: this.orden.getPartidas()) {
        params.put("idNotaDetalle", item.getIdNotaDetalle());
        DaoFactory.getInstance().updateAll(sesion, TcManticNotasDetallesDto.class, params, "eliminar");
        if(lotes.containsKey(item.getIdLote())) {
          String detalle= lotes.get(item.getIdLote()); 
          lotes.put(item.getIdLote(), detalle+ item.getIdLoteDetalle()+ ", "); 
        } // if  
        else 
          lotes.put(item.getIdLote(), item.getIdLoteDetalle()+ ", "); 
        item.setIdLoteDetalle(-1L);
      } // for
      // RECORRER LOS LOTES Y VERIFICAR SI AUN CUENTA CON ALGUN PRODUCTO EN SU DEFECTO CANCELAR Y DEPURAR EL DETALLE
      for (Long key: lotes.keySet()) {
        params.put("idLote", key);
        params.put("detalle", lotes.get(key).substring(0, lotes.get(key).length()- 2));
        params.put("observaciones", "SE CANCELO POR EL LOTE (".concat(this.orden.getConsecutivo()).concat(")"));
        Entity lote= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcManticLotesDto", "cuantos", params);
        if(Objects.equals(lote, null) || lote.isEmpty()) {
          DaoFactory.getInstance().updateAll(sesion, TcManticLotesDto.class, params);
          DaoFactory.getInstance().deleteAll(sesion, TcManticLotesPromediosDto.class, params);
          DaoFactory.getInstance().deleteAll(sesion, TcManticLotesDetallesDto.class, params);
        } // if
        else 
          DaoFactory.getInstance().deleteAll(sesion, TcManticLotesDetallesDto.class, "igual", params);
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
  
} 