package mx.org.kaana.keet.catalogos.contratos.personal.beans;

import java.sql.Date;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesArchivosDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 22/03/2020
 *@time 11:48:22 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class DocumentoIncidencia extends TcManticIncidentesArchivosDto {

	private static final long serialVersionUID = -3343835523414572416L;	
	private Long idArchivo;	
	private Date fechaInicio;
	private Date fechaFin;
	private Long ejercicio;
	private String consecutivo;
	private String estatus;
	private String tipoIncidente;

	public DocumentoIncidencia(Long idArchivo, Long idIncidente, String archivo, String ruta, Long tamanio, Long idUsuario, Long idTipoArchivo, String observaciones, String alias, Long idIncidenteArchivo, String nombre, Date fechaInicio, Date fechaFin, Long ejercicio, String consecutivo, String estatus, String tipoIncidente) {
		super(idIncidente, archivo, ruta, tamanio, idUsuario, idTipoArchivo, observaciones, alias, idIncidenteArchivo, nombre);
		this.idArchivo    = idArchivo;
		this.fechaInicio  = fechaInicio;
		this.fechaFin     = fechaFin;
		this.ejercicio    = ejercicio;
		this.consecutivo  = consecutivo;
		this.estatus      = estatus;
		this.tipoIncidente= tipoIncidente;
	}		

	public Long getIdArchivo() {
		return idArchivo;
	}

	public void setIdArchivo(Long idArchivo) {
		this.idArchivo = idArchivo;
	}		

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Long getEjercicio() {
		return ejercicio;
	}

	public void setEjercicio(Long ejercicio) {
		this.ejercicio = ejercicio;
	}

	public String getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(String consecutivo) {
		this.consecutivo = consecutivo;
	}

	public String getEstatus() {
		return estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}	

	public String getTipoIncidente() {
		return tipoIncidente;
	}

	public void setTipoIncidente(String tipoIncidente) {
		this.tipoIncidente = tipoIncidente;
	}	
  
}