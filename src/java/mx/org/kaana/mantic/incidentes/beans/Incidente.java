package mx.org.kaana.mantic.incidentes.beans;

import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesDto;

public class Incidente implements Serializable {

	private static final long serialVersionUID= 5491973442869961621L;	
	private Long idIncidente;
	private Long idEmpresaPersona;
	private Long idTipoIncidente;
	private Long idIncidenteEstatus;
	private Date vigenciaInicio;
	private Date vigenciaFin;
	private String observaciones;
	private String nombre;
	private String tipoIncidente;
	private String nombreUsuario;
	private String estatus;
	private String puesto;
	private ESql accion;
	private String estatusAsociados;
	private Double costo;
	private Long idPrestamo;

	public Incidente() {
		this(-1L, -1L, -1L, -1L, new Date(Calendar.getInstance().getTimeInMillis()), new Date(Calendar.getInstance().getTimeInMillis()), "", null, null, null, null, null, ESql.UPDATE, null, null, null);
	}

	public Incidente(TcManticIncidentesDto dto){
		this(dto.getIdIncidente(), dto.getIdEmpresaPersona(), dto.getIdTipoIncidente(), dto.getIdIncidenteEstatus(), dto.getInicio(), dto.getTermino(), dto.getObservaciones(), null, null, null, null, null, ESql.UPDATE, null, dto.getCosto(), dto.getIdPrestamo());
	}
	
	public Incidente(Long idIncidente, Long idEmpresaPersona, Long idTipoIncidente, Long idIncidenteEstatus, Date vigenciaInicio, Date vigenciaFin, String observaciones, String nombre, String tipoIncidente, String nombreUsuario, String estatus, String puesto, ESql accion, String estatusAsociados, Double costo, Long idPrestamo) {
		this.idIncidente       = idIncidente;
		this.idEmpresaPersona  = idEmpresaPersona;
		this.idTipoIncidente   = idTipoIncidente;
		this.idIncidenteEstatus= idIncidenteEstatus;
		this.vigenciaInicio    = vigenciaInicio;
		this.vigenciaFin       = vigenciaFin;
		this.observaciones     = observaciones;
		this.estatusAsociados	 = estatusAsociados;	
    this.idPrestamo        = idPrestamo;
	}	

	public Long getIdIncidente() {
		return idIncidente;
	}

	public void setIdIncidente(Long idIncidente) {
		this.idIncidente = idIncidente;
	}
	
	public Long getIdEmpresaPersona() {
		return idEmpresaPersona;
	}

	public void setIdEmpresaPersona(Long idEmpresaPersona) {
		this.idEmpresaPersona = idEmpresaPersona;
	}

	public Long getIdTipoIncidente() {
		return idTipoIncidente;
	}

	public void setIdTipoIncidente(Long idTipoIncidente) {
		this.idTipoIncidente = idTipoIncidente;
	}

	public Long getIdIncidenteEstatus() {
		return idIncidenteEstatus;
	}

	public void setIdIncidenteEstatus(Long idIncidenteEstatus) {
		this.idIncidenteEstatus = idIncidenteEstatus;
	}

	public Date getVigenciaInicio() {
		return vigenciaInicio;
	}

	public void setVigenciaInicio(Date vigenciaInicio) {
		this.vigenciaInicio = vigenciaInicio;
	}

	public Date getVigenciaFin() {
		return vigenciaFin;
	}

	public void setVigenciaFin(Date vigenciaFin) {
		this.vigenciaFin = vigenciaFin;
	}
	
	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}	

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTipoIncidente() {
		return tipoIncidente;
	}

	public void setTipoIncidente(String tipoIncidente) {
		this.tipoIncidente = tipoIncidente;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getEstatus() {
		return estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}	

	public ESql getAccion() {
		return accion;
	}

	public void setAccion(ESql accion) {
		this.accion = accion;
	}	

	public String getEstatusAsociados() {
		return estatusAsociados;
	}

	public void setEstatusAsociados(String estatusAsociados) {
		this.estatusAsociados = estatusAsociados;
	}	

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}

  public Long getIdPrestamo() {
    return idPrestamo;
  }

  public void setIdPrestamo(Long idPrestamo) {
    this.idPrestamo = idPrestamo;
  }

	@Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Incidente other = (Incidente) obj;
    if (getIdIncidente() != other.idIncidente && (getIdIncidente() == null || !getIdIncidente().equals(other.idIncidente))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdIncidente() != null ? getIdIncidente().hashCode() : 0);
    return hash;
  }
}