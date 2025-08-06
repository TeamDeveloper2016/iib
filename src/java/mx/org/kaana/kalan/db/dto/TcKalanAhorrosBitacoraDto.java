package mx.org.kaana.kalan.db.dto;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Lob;
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
@Table(name="tc_kalan_ahorros_bitacora")
public class TcKalanAhorrosBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_ahorro")
  private Long idAhorro;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="justificacion")
  private String justificacion;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_ahorro_bitacora")
  private Long idAhorroBitacora;
  @Column (name="id_ahorro_estatus")
  private Long idAhorroEstatus;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanAhorrosBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKalanAhorrosBitacoraDto(Long key) {
    this(null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKalanAhorrosBitacoraDto(Long idAhorro, Long idAhorroBitacora, Long idUsuario, String justificacion, Long idAhorroEstatus) {
    setIdAhorro(idAhorro);
    setIdUsuario(idUsuario);
    setJustificacion(justificacion);
    setIdAhorroBitacora(idAhorroBitacora);
    setIdAhorroEstatus(idAhorroEstatus);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdAhorro(Long idAhorro) {
    this.idAhorro = idAhorro;
  }

  public Long getIdAhorro() {
    return idAhorro;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setJustificacion(String justificacion) {
    this.justificacion = justificacion;
  }

  public String getJustificacion() {
    return justificacion;
  }

  public void setIdAhorroBitacora(Long idAhorroBitacora) {
    this.idAhorroBitacora = idAhorroBitacora;
  }

  public Long getIdAhorroBitacora() {
    return idAhorroBitacora;
  }

  public void setIdAhorroEstatus(Long idAhorroEstatus) {
    this.idAhorroEstatus = idAhorroEstatus;
  }

  public Long getIdAhorroEstatus() {
    return idAhorroEstatus;
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
  	return getIdAhorroBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idAhorroBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdAhorro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAhorroBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAhorroEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idAhorro", getIdAhorro());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("justificacion", getJustificacion());
		regresar.put("idAhorroBitacora", getIdAhorroBitacora());
		regresar.put("idAhorroEstatus", getIdAhorroEstatus());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getIdAhorro(), getIdUsuario(), getJustificacion(), getIdAhorroBitacora(), getIdAhorroEstatus(), getRegistro()
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
    regresar.append("idAhorroBitacora~");
    regresar.append(getIdAhorroBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAhorroBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanAhorrosBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAhorroBitacora()!= null && getIdAhorroBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanAhorrosBitacoraDto other = (TcKalanAhorrosBitacoraDto) obj;
    if (getIdAhorroBitacora() != other.idAhorroBitacora && (getIdAhorroBitacora() == null || !getIdAhorroBitacora().equals(other.idAhorroBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAhorroBitacora() != null ? getIdAhorroBitacora().hashCode() : 0);
    return hash;
  }

}


