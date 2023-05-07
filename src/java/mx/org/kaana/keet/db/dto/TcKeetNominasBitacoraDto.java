package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
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
@Table(name="tc_keet_nominas_bitacora")
public class TcKeetNominasBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_nomina_estatus")
  private Long idNominaEstatus;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
  @Column (name="id_nomina_bitacora")
  private Long idNominaBitacora;
  @Column (name="id_nomina")
  private Long idNomina;
  @Column (name="registro")
  private Timestamp registro;

  public TcKeetNominasBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKeetNominasBitacoraDto(Long key) {
    this(null, null, null, null, null);
    setKey(key);
  }

  public TcKeetNominasBitacoraDto(String justificacion, Long idNominaEstatus, Long idUsuario, Long idNominaBitacora, Long idNomina) {
    setJustificacion(justificacion);
    setIdNominaEstatus(idNominaEstatus);
    setIdUsuario(idUsuario);
    setIdNominaBitacora(idNominaBitacora);
    setIdNomina(idNomina);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setJustificacion(String justificacion) {
    this.justificacion = justificacion;
  }

  public String getJustificacion() {
    return justificacion;
  }

  public void setIdNominaEstatus(Long idNominaEstatus) {
    this.idNominaEstatus = idNominaEstatus;
  }

  public Long getIdNominaEstatus() {
    return idNominaEstatus;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdNominaBitacora(Long idNominaBitacota) {
    this.idNominaBitacora = idNominaBitacota;
  }

  public Long getIdNominaBitacora() {
    return idNominaBitacora;
  }

  public void setIdNomina(Long idNomina) {
    this.idNomina = idNomina;
  }

  public Long getIdNomina() {
    return idNomina;
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
  	return getIdNominaBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idNominaBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("justificacion", getJustificacion());
		regresar.put("idNominaEstatus", getIdNominaEstatus());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idNominaBitacota", getIdNominaBitacora());
		regresar.put("idNomina", getIdNomina());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getJustificacion(), getIdNominaEstatus(), getIdUsuario(), getIdNominaBitacora(), getIdNomina(), getRegistro()
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
    regresar.append("idNominaBitacora~");
    regresar.append(this.getIdNominaBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNominaBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNominasBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNominaBitacora()!= null && getIdNominaBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNominasBitacoraDto other = (TcKeetNominasBitacoraDto) obj;
    if (getIdNominaBitacora() != other.idNominaBitacora && (getIdNominaBitacora() == null || !getIdNominaBitacora().equals(other.idNominaBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNominaBitacora() != null ? getIdNominaBitacora().hashCode() : 0);
    return hash;
  }
}
