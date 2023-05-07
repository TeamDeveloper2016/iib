package mx.org.kaana.mantic.incidentes.reglas;

import java.sql.Date;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.db.dto.TcKeetIncidentesBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetIncidentesDto;
import mx.org.kaana.keet.enums.ETiposIncidentes;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesDto;
import mx.org.kaana.mantic.db.dto.TrManticEmpresaPersonalDto;
import mx.org.kaana.mantic.enums.EEstatusIncidentes;
import mx.org.kaana.mantic.incidentes.beans.Incidente;
import mx.org.kaana.mantic.incidentes.beans.Repercusion;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	protected Incidente incidente;
	private String messageError;	
	private String observaciones;	
  private List<Repercusion> incidentes;
	private boolean estatus;

	public Transaccion(Incidente incidente) {
		this(incidente, "");
	}
	
	public Transaccion(Incidente incidente, String observaciones) {
		this.incidente    = incidente;
		this.observaciones= observaciones;
		this.estatus      = false;
	}

	public Transaccion(List<Repercusion> incidentes) {
    this.incidentes= incidentes;
  }
  
	protected Incidente getIncidente() {
		return incidente;
	}

	protected void setIncidente(Incidente incidente) {
		this.incidente = incidente;
	}	

	public boolean isEstatus() {
		return estatus;
	}

	public void setEstatus(boolean estatus) {
		this.estatus = estatus;
	}	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
		boolean regresar= false;
		try {
			switch(accion){
				case DESTRANSFORMACION:			
					regresar= this.registrarIncidente(sesion);					
					break;
				case COMPLEMENTAR:			
					regresar= this.registrarAnticipo(sesion);					
					break;
				case AGREGAR:			
					if(verificaExistente(sesion))						
						regresar= registrarIncidente(sesion);					
					break;
				case MODIFICAR:									
					if(verificaExistente(sesion))
						regresar= this.modificarIncidente(sesion, false);
					break;				
				case ASIGNAR:
					regresar= this.modificarIncidente(sesion, true);
					break;
				case MOVIMIENTOS:
					regresar= this.modificarAnticipo(sesion, true);
					break;
				case REPROCESAR:
					regresar= this.registrarInidencias(sesion);
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {			
			throw new Exception(this.messageError.concat("<br/>") + e);
		} // catch		
		return regresar;
	} // ejecutar		
	
	private boolean verificaExistente(Session sesion) throws Exception {
		boolean regresar  = true;
		this.messageError = "No es posible agregar una incidencia del mismo tipo al mismo empleado cuando hay una vigente.";
		int totalDias     = Fecha.diferenciasDias(Fecha.formatear(Fecha.FECHA_ESTANDAR, this.incidente.getVigenciaInicio()), Fecha.formatear(Fecha.FECHA_ESTANDAR, this.incidente.getVigenciaFin()));
		Calendar inicio   =  Calendar.getInstance();
    inicio.setTimeInMillis(this.incidente.getVigenciaInicio().getTime());
		if(totalDias== 0)
			totalDias= 1;
		for(int count=0; count< totalDias; count++) {
			Map<String, Object> params= new HashMap<>();
			params.put("idEmpresaPersona", this.incidente.getIdEmpresaPersona());
			params.put("idTipoIncidente", this.incidente.getIdTipoIncidente());
			params.put("estatus", this.toEstatus());
			params.put("fecha", Fecha.formatear(Fecha.FECHA_ESTANDAR, inicio));
			Entity registro= (Entity) DaoFactory.getInstance().toEntity(sesion, "TcManticIncidentesDto", "existente", params);
			if(registro!= null && registro.isValid()) {
				regresar= false;
				break;
			} // if
			else 
        inicio.add(Calendar.DATE, 1);
		} // for
		return regresar;
	} // verificaExistente
	
	private String toEstatus() {
		StringBuilder regresar= null;
		try {
			regresar= new StringBuilder("");
			regresar.append(EEstatusIncidentes.ACEPTADA.getIdEstatusInicidente());
			regresar.append(",");
			regresar.append(EEstatusIncidentes.APLICADA.getIdEstatusInicidente());
			regresar.append(",");
			regresar.append(EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente());		
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} // toEstatus
	
	private boolean registrarIncidente(Session sesion) throws Exception{
		boolean regresar         = false;
		TcManticIncidentesDto dto= null;
		Long key                 = -1L;
		Siguiente consecutivo    = null;
		Long idEstatus           = null;
		try {
			dto= new TcManticIncidentesDto();
			consecutivo= this.toSiguiente(sesion);			
			dto.setConsecutivo(consecutivo.getConsecutivo());			
			dto.setOrden(consecutivo.getOrden());			
			dto.setEjercicio(Long.valueOf(Fecha.getAnioActual()));			
			idEstatus= this.estatus ? (isProcesoEmpleado() ? EEstatusIncidentes.ACEPTADA.getIdEstatusInicidente() : this.incidente.getIdEmpresaPersona()) : 
							                  (isProcesoEmpleado() ? EEstatusIncidentes.ACEPTADA.getIdEstatusInicidente() : EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente());
			dto.setIdIncidenteEstatus(idEstatus);			
			dto.setIdEmpresaPersona(this.incidente.getIdEmpresaPersona());
			if(this.incidente.getCosto()!= null && this.incidente.getCosto()>0D)
				dto.setCosto(this.incidente.getCosto());
			dto.setIdTipoIncidente(this.incidente.getIdTipoIncidente());	
			dto.setIdUsuario(JsfBase.getIdUsuario());
			dto.setObservaciones(this.incidente.getObservaciones());
			dto.setInicio(this.incidente.getVigenciaInicio());
			dto.setTermino(this.incidente.getVigenciaFin());		
			if(this.incidente.getIdPrestamo()!= null && this.incidente.getIdPrestamo() > 0)
  			dto.setIdPrestamo(this.incidente.getIdPrestamo());		
			key= DaoFactory.getInstance().insert(sesion, dto);
			if(key>= 1L)
				regresar= registrarBitacora(sesion, key, this.estatus ? this.incidente.getIdEmpresaPersona() : EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch	
		return regresar;
	} // registrarIncidente
	
	private boolean registrarAnticipo(Session sesion) throws Exception{
		boolean regresar       = false;
		TcKeetIncidentesDto dto= null;
		Long key               = -1L;
		Siguiente consecutivo  = null;
		Long idEstatus         = null;
		try {
			dto= new TcKeetIncidentesDto();
			consecutivo= this.toContinuar(sesion);			
			dto.setConsecutivo(consecutivo.getConsecutivo());			
			dto.setOrden(consecutivo.getOrden());			
			dto.setEjercicio(Long.valueOf(Fecha.getAnioActual()));			
			idEstatus= this.estatus? (isProcesoEmpleado()? EEstatusIncidentes.ACEPTADA.getIdEstatusInicidente(): this.incidente.getIdEmpresaPersona()): 
							                 (isProcesoEmpleado()? EEstatusIncidentes.ACEPTADA.getIdEstatusInicidente(): EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente());
			dto.setIdIncidenteEstatus(idEstatus);			
			dto.setIdProveedor(this.incidente.getIdEmpresaPersona());
			if(this.incidente.getCosto()!= null && this.incidente.getCosto()> 0D)
				dto.setCosto(this.incidente.getCosto());
			dto.setIdTipoIncidente(this.incidente.getIdTipoIncidente());	
			dto.setIdUsuario(JsfBase.getIdUsuario());
			dto.setObservaciones(this.incidente.getObservaciones());
			dto.setInicio(this.incidente.getVigenciaInicio());
			dto.setTermino(this.incidente.getVigenciaFin());		
			if(this.incidente.getIdPrestamo()!= null && this.incidente.getIdPrestamo() > 0)
  			dto.setIdAnticipo(this.incidente.getIdPrestamo());		
			key= DaoFactory.getInstance().insert(sesion, dto);
			if(key>= 1L)
				regresar= this.bitacoraAnticipo(sesion, key, this.estatus? this.incidente.getIdEmpresaPersona(): EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch	
		return regresar;
	} // registrarAnticipo
	
	private boolean isProcesoEmpleado() {
		return ETiposIncidentes.ABONO_NOMINA.getKey().equals(this.incidente.getIdTipoIncidente()) || ETiposIncidentes.PRESTAMO_NOMINA.getKey().equals(this.incidente.getIdTipoIncidente()) || ETiposIncidentes.ALTA.getKey().equals(this.incidente.getIdTipoIncidente()) || ETiposIncidentes.REINGRESO.getKey().equals(this.incidente.getIdTipoIncidente()) || ETiposIncidentes.BAJA.getKey().equals(this.incidente.getIdTipoIncidente());
	} // isProcesoEmpleado 
	
	private boolean registrarBitacora(Session sesion, Long idIncidente, Long idEstatus) throws Exception{
    return this.registrarBitacora(sesion, idIncidente, idEstatus, this.incidente.getObservaciones());
  }
  
	private boolean registrarBitacora(Session sesion, Long idIncidente, Long idEstatus, String observaciones) throws Exception{
		boolean regresar                 = false;
		TcManticIncidentesBitacoraDto dto= null;
		try {
			dto= new TcManticIncidentesBitacoraDto();
			dto.setIdIncidente(idIncidente);
			dto.setIdIncidenteEstatus(idEstatus);
			dto.setIdUsuario(JsfBase.getIdUsuario());
			dto.setJustificacion(observaciones);
			regresar= DaoFactory.getInstance().insert(sesion, dto)>= 1L;
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		return regresar;
	} // registrarBitacora
	
	private boolean bitacoraAnticipo(Session sesion, Long idIncidente, Long idEstatus) throws Exception{
		boolean regresar                 = false;
		TcKeetIncidentesBitacoraDto dto= null;
		try {
			dto= new TcKeetIncidentesBitacoraDto();
			dto.setIdIncidente(idIncidente);
			dto.setIdIncidenteEstatus(idEstatus);
			dto.setIdUsuario(JsfBase.getIdUsuario());
			dto.setJustificacion(this.incidente.getObservaciones());
			regresar= DaoFactory.getInstance().insert(sesion, dto)>= 1L;
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		return regresar;
	} // bitacoraAnticipo
	
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());			
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticIncidentesDto", "siguiente", params, "siguiente");
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
	
	protected Siguiente toContinuar(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());			
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKeetIncidentesDto", "siguiente", params, "siguiente");
			if(next.getData()!= null)
				regresar= new Siguiente(next.toLong());
			else
				regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L); 
		} // try		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toContinuar
	
	private boolean modificarIncidente(Session sesion, boolean estatus) throws Exception{
		boolean regresar         = false;
		boolean actualizaEstatus = false;
		TcManticIncidentesDto dto= null;
		try {
			dto= (TcManticIncidentesDto) DaoFactory.getInstance().findById(sesion, TcManticIncidentesDto.class, this.incidente.getIdIncidente());
			actualizaEstatus= dto.getIdIncidenteEstatus().equals(EEstatusIncidentes.CANCELADA.getIdEstatusInicidente()) || this.incidente.getIdIncidenteEstatus().equals(EEstatusIncidentes.CANCELADA.getIdEstatusInicidente());
			if(estatus)
				dto.setIdIncidenteEstatus(this.incidente.getIdIncidenteEstatus());
			dto.setIdEmpresaPersona(this.incidente.getIdEmpresaPersona());
			dto.setIdTipoIncidente(this.incidente.getIdTipoIncidente());			
			dto.setObservaciones(this.incidente.getObservaciones());
			dto.setInicio(this.incidente.getVigenciaInicio());
			dto.setTermino(this.incidente.getVigenciaFin());		
			if(DaoFactory.getInstance().update(sesion, dto)>= 1L){
				this.incidente.setObservaciones(this.observaciones);
				if(this.registrarBitacora(sesion, this.incidente.getIdIncidente(), dto.getIdIncidenteEstatus())){
					regresar= this.actualizaEstatusEmpleado(sesion, dto.getIdIncidenteEstatus(), actualizaEstatus);
				} // if
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // modificarIncidente
	
	private boolean modificarAnticipo(Session sesion, boolean estatus) throws Exception{
		boolean regresar       = false;
		TcKeetIncidentesDto dto= null;
		try {
			dto= (TcKeetIncidentesDto) DaoFactory.getInstance().findById(sesion, TcKeetIncidentesDto.class, this.incidente.getIdIncidente());
			if(estatus)
				dto.setIdIncidenteEstatus(this.incidente.getIdIncidenteEstatus());
			dto.setIdProveedor(this.incidente.getIdEmpresaPersona());
			dto.setIdTipoIncidente(this.incidente.getIdTipoIncidente());			
			dto.setObservaciones(this.incidente.getObservaciones());
			dto.setInicio(this.incidente.getVigenciaInicio());
			dto.setTermino(this.incidente.getVigenciaFin());		
			if(DaoFactory.getInstance().update(sesion, dto)>= 1L) { 
				this.incidente.setObservaciones(this.observaciones);
				this.bitacoraAnticipo(sesion, this.incidente.getIdIncidente(), dto.getIdIncidenteEstatus());
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // modificarAnticipo
	
	private boolean actualizaEstatusEmpleado(Session sesion, Long idIncidenteEstatus, boolean actualizaEstatus) throws Exception{
		boolean regresar                  = true;
		TrManticEmpresaPersonalDto persona= null;
		try {
			if(actualizaEstatus && isProcesoEmpleado()){
				persona= (TrManticEmpresaPersonalDto) DaoFactory.getInstance().findById(sesion, TrManticEmpresaPersonalDto.class, this.incidente.getIdEmpresaPersona());
				switch(ETiposIncidentes.fromId(this.incidente.getIdTipoIncidente())){
					case ALTA:
					case REINGRESO:
						if(EEstatusIncidentes.CANCELADA.getIdEstatusInicidente().equals(idIncidenteEstatus)) {
              Calendar calendar= Calendar.getInstance();
              calendar.set(2999, 11, 31);
							persona.setIdActivo(2L);						
							persona.setIngreso(new Date(calendar.getTimeInMillis()));
						} // if
            else {
							persona.setIdActivo(1L);						
							persona.setIngreso(new Date(Calendar.getInstance().getTimeInMillis()));
						} // else
						break;
					case BAJA:
						if(EEstatusIncidentes.CANCELADA.getIdEstatusInicidente().equals(idIncidenteEstatus)) {
							persona.setIdActivo(1L);
							persona.setIngreso(new Date(Calendar.getInstance().getTimeInMillis()));
						} // if
            else {
              Calendar calendar= Calendar.getInstance();
              calendar.set(2999, 11, 31);
							persona.setIdActivo(2L);
							persona.setIngreso(new Date(calendar.getTimeInMillis()));
						} // else
						break;
				} // switch				
				regresar= DaoFactory.getInstance().update(sesion, persona)>= 1L;
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // actualizaEstatusEmpleado
	
	private boolean eliminarIncidente(Session sesion) throws Exception{
		boolean regresar= false;
		TcManticIncidentesDto dto= null;
		try {
			dto= (TcManticIncidentesDto) DaoFactory.getInstance().findById(sesion, TcManticIncidentesDto.class, this.incidente.getIdIncidente());
			dto.setIdIncidenteEstatus(EEstatusIncidentes.CANCELADA.getIdEstatusInicidente());
			if(DaoFactory.getInstance().update(sesion, dto)>= 1L) {
				this.incidente.setObservaciones("LA INCIDENCIA FUE CANCELADA");
				regresar= registrarBitacora(sesion, this.incidente.getIdIncidente(), dto.getIdIncidenteEstatus());
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		return regresar;
	} // eliminarIncidente
  
  private Boolean registrarInidencias(Session sesion) throws Exception {
    Boolean regresar     = Boolean.TRUE;
		Siguiente consecutivo= null;
    for (Repercusion item: this.incidentes) {
      switch(item.getSql()) {
        case SELECT:
        case UPDATE:
        case DELETE:
          break;
        case INSERT:
          consecutivo= this.toSiguiente(sesion);			
          item.setConsecutivo(consecutivo.getConsecutivo());			
          item.setOrden(consecutivo.getOrden());			
          item.setEjercicio(Long.valueOf(Fecha.getAnioActual()));			
          item.setIdIncidenteEstatus(EEstatusIncidentes.ACEPTADA.getIdEstatusInicidente());			
          regresar= DaoFactory.getInstance().insert(sesion, item)> 0L;
          if(regresar) 
            regresar= this.registrarBitacora(sesion, item.getIdIncidente(), item.getIdIncidenteEstatus(), "INCIDENCIA REGISTRADA DE FORMA MASIVA");
          break;
      } // switch
    } // for
    return regresar;
  }
  
}
