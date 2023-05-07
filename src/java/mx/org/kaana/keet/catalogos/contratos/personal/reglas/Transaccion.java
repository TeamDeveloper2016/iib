package mx.org.kaana.keet.catalogos.contratos.personal.reglas;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.beans.SelectionItem;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.catalogos.contratos.personal.beans.DocumentoIncidencia;
import mx.org.kaana.keet.db.dto.TcKeetContratosPersonalDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesArchivosDto;
import mx.org.kaana.mantic.db.dto.TrManticEmpresaPersonalDto;
import mx.org.kaana.mantic.incidentes.beans.Incidente;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

public class Transaccion extends mx.org.kaana.mantic.incidentes.reglas.Transaccion {

	private static final Log LOG= LogFactory.getLog(Transaccion.class);
	private static final Long ADMINISTRATIVO_POR_DIA= 2L;
	private Long idDesarrollo;	
	private List<SelectionItem> empleados;
	private ScheduleModel eventModel;
	private List<DocumentoIncidencia> incidencias;
	private List<Incidente> incidenciasDelete;

	public Transaccion(Incidente incidente, String observaciones) {
		super(incidente, observaciones);
	}
	
	public Transaccion(List<DocumentoIncidencia> incidencias) {
		this(new Incidente());
		this.incidencias= incidencias;
	}	
	
	public Transaccion(ScheduleModel eventModel, List<Incidente> incidenciasDelete) {
		this(new Incidente());
		this.eventModel       = eventModel;
		this.incidenciasDelete= incidenciasDelete;
	}		
	
	public Transaccion(Incidente incidente) {
		this(incidente, -1L, new ArrayList<>());
	}
	
	public Transaccion(Long idDesarrollo, List<SelectionItem> empleados) {
		this(new Incidente(), idDesarrollo, empleados);
	}
	
	public Transaccion(Incidente incidente, Long idDesarrollo, List<SelectionItem> empleados) {
		super(incidente);
		this.idDesarrollo= idDesarrollo;		
		this.empleados   = empleados;
	}	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar                  = true;
		Map<String, Object>params         = null;
		TcKeetContratosPersonalDto dto    = null;
		TrManticEmpresaPersonalDto persona= null;
		EAccion accionIncidente           = null;
		boolean procesaEmpleado           = true;
		Long numeroRegistros              = null;
		try {
			switch(accion) {
				case COMPLEMENTAR:									
					params= new HashMap<>();
					for(SelectionItem item: this.empleados) {
            params.put("idEmpresaPersona", item.getKey());
						dto= (TcKeetContratosPersonalDto)DaoFactory.getInstance().toEntity(sesion, TcKeetContratosPersonalDto.class, "TcKeetContratosPersonalDto", "existe", params);
						if(dto== null) {	
							dto= new TcKeetContratosPersonalDto();							
							dto.setIdEmpresaPersona(Long.valueOf(item.getKey()));
							dto.setIdUsuario(JsfBase.getIdUsuario());
							dto.setIdVigente(1L);
							dto.setObservaciones("ASIGNACION DE EMPLEADO AL DESARROLLO " + this.idDesarrollo);
							DaoFactory.getInstance().insert(sesion, dto);
						} // if
            else {
							dto.setObservaciones("SE REASIGNO EL EMPLEADO AL DESARROLLO " + this.idDesarrollo);
              dto.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
							DaoFactory.getInstance().update(sesion, dto);
            } // else
					} // for					
					break;				
				case PROCESAR:									
					params= new HashMap<>();
					for(SelectionItem item: this.empleados) {
						procesaEmpleado= true;
						persona= (TrManticEmpresaPersonalDto) DaoFactory.getInstance().findById(sesion, TrManticEmpresaPersonalDto.class, Long.valueOf(item.getKey()));
						if(persona.getIdDepartamento().equals(ADMINISTRATIVO_POR_DIA)) {
							params.clear();
							params.put("idEmpresaPersona", item.getKey());
							numeroRegistros= DaoFactory.getInstance().toField(sesion, "TcKeetContratosPersonalDto", "numeroRegistros", params, "total").toLong();
							procesaEmpleado= numeroRegistros.equals(0L);
						} // if
						if(procesaEmpleado) {	
							dto= new TcKeetContratosPersonalDto();							
							dto.setIdEmpresaPersona(Long.valueOf(item.getKey()));
							dto.setIdUsuario(JsfBase.getIdUsuario());
							dto.setIdVigente(1L);
							dto.setObservaciones("ASIGNACION DEL EMPLEADO AL DESARROLLO " + this.idDesarrollo);
							DaoFactory.getInstance().insert(sesion, dto);
						} // if
					} // for					
					break;				
				case DEPURAR:
					for(SelectionItem item: this.empleados) {
						if(verificacionLotes(sesion, Long.valueOf(item.getKey()))){
							params= new HashMap<>();
							params.put("idEmpresaPersona", Long.valueOf(item.getKey()));						
							DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcKeetContratosPersonalDto", "contratoPersona", params);					
						} // if
					} // for
					break;
				case REGISTRAR:
					if(eliminarIncidencias(sesion)) {
						for(ScheduleEvent event: this.eventModel.getEvents()) {
							Incidente incidencia= (Incidente) event.getData();
							switch(incidencia.getAccion()){
								case INSERT:
									incidencia.setIdIncidente(-1L);
									accionIncidente= EAccion.AGREGAR;								
									break;
								case UPDATE:
									accionIncidente= EAccion.ASIGNAR;								
									break;
							} // switch						
							this.setIncidente(incidencia);
							super.ejecutar(sesion, accionIncidente);
						} // for
					} // if
					break;
				case SUBIR:
					for(DocumentoIncidencia incidencia: this.incidencias) {
						if(DaoFactory.getInstance().insert(sesion, incidencia)>= 1L)
							this.toSaveFile(incidencia.getIdArchivo());
					} // for
					break;
				case JUSTIFICAR:
					this.eliminarIncidencias(sesion);
					break;
				default:
					super.ejecutar(sesion, accion);
					break;
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception((e!= null? e.getCause().toString(): ""));
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	}	// ejecutar
	
	private boolean eliminarIncidencias(Session sesion) throws Exception{
		boolean regresar                         = true;
		List<TcManticIncidentesArchivosDto> files= null;
		Map<String, Object>params                = null;
		File file                                = null;
		try {
			for(Incidente incidencia: this.incidenciasDelete){
				params= new HashMap<>();
				params.put("idIncidente", incidencia.getIdIncidente());
				files= DaoFactory.getInstance().toEntitySet(sesion, TcManticIncidentesArchivosDto.class, "TcManticIncidentesArchivosDto", "allIncidente", params, Constantes.SQL_TODOS_REGISTROS);
				if(DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcManticIncidentesBitacoraDto", "rows", params) > -1L){
					if(DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcManticIncidentesArchivosDto", "rows", params) > -1L){
						if(DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcManticIncidentesDto", "rows", params) > -1L){
							for(TcManticIncidentesArchivosDto fileDepuracion: files){
								file= new File(fileDepuracion.getAlias());
								try {
									if(file.exists())
										file.delete();																				
									else
										LOG.info("No se encontro el archivo: " + fileDepuracion.getAlias());
								} // try
								catch (Exception e) {					
									Error.mensaje(e);					
									LOG.info("Ocurrió un error al eliminar el archivo: " + fileDepuracion.getAlias());
								} // catch				
							} // for
						} // if
					} // if
				} // if
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // eliminarIncidencias
	
	private boolean verificacionLotes(Session sesion, Long idEmpresaPersona) throws Exception{
		boolean regresar         = false;
		Long countLotesPersona   = 0L;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idEmpresaPersona", idEmpresaPersona);
			countLotesPersona= DaoFactory.getInstance().toField(sesion, "TcKeetContratosLotesContratistasDto", "trabajoLotes", params, "total").toLong();
			regresar= countLotesPersona.equals(0L);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // clearPersonalAsignado
}