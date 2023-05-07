package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 10/10/2016
 *@time 11:58:22 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Entity
@Table(name="tc_keet_nominas_estatus")
public class TcKeetNominasEstatusDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nomina_estatus")
  private Long idNominaEstatus;
  @Column (name="id_justificacion")
  private Long idJustificacion;
  @Column (name="estatus_asociados")
  private String estatusAsociados;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private Timestamp registro;

  public TcKeetNominasEstatusDto() {
    this(new Long(-1L));
  }

  public TcKeetNominasEstatusDto(Long key) {
    this(null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetNominasEstatusDto(String descripcion, Long idNominaEstatus, Long idJustificacion, String estatusAsociados, String nombre) {
    setDescripcion(descripcion);
    setIdNominaEstatus(idNominaEstatus);
    setIdJustificacion(idJustificacion);
    setEstatusAsociados(estatusAsociados);
    setNombre(nombre);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdNominaEstatus(Long idNominaEstatus) {
    this.idNominaEstatus = idNominaEstatus;
  }

  public Long getIdNominaEstatus() {
    return idNominaEstatus;
  }

  public void setIdJustificacion(Long idJustificacion) {
    this.idJustificacion = idJustificacion;
  }

  public Long getIdJustificacion() {
    return idJustificacion;
  }

  public void setEstatusAsociados(String estatusAsociados) {
    this.estatusAsociados = estatusAsociados;
  }

  public String getEstatusAsociados() {
    return estatusAsociados;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setRegistro(Timestamp registro) {
    this.registro = registro;
  }

  public Timestamp getRegistro() {
    return registro;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdNominaEstatus();
  }

  @Override
  public void setKey(Long key) {
  	this.idNominaEstatus = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEstatusAsociados());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("idNominaEstatus", getIdNominaEstatus());
		regresar.put("idJustificacion", getIdJustificacion());
		regresar.put("estatusAsociados", getEstatusAsociados());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdNominaEstatus(), getIdJustificacion(), getEstatusAsociados(), getNombre(), getRegistro()
    };
    return regresar;
  }

  @Override
  public Object toValue(String name) {
    return Methods.getValue(this, name);
  }

  @Override
  public String toAllKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("|");
    regresar.append("idNominaEstatus~");
    regresar.append(getIdNominaEstatus());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNominaEstatus());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNominasEstatusDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNominaEstatus()!= null && getIdNominaEstatus()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNominasEstatusDto other = (TcKeetNominasEstatusDto) obj;
    if (getIdNominaEstatus() != other.idNominaEstatus && (getIdNominaEstatus() == null || !getIdNominaEstatus().equals(other.idNominaEstatus))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNominaEstatus() != null ? getIdNominaEstatus().hashCode() : 0);
    return hash;
  }
}
