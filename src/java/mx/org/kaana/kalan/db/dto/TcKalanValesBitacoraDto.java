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
@Table(name="tc_kalan_vales_bitacora")
public class TcKalanValesBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_vale_bitacora")
  private Long idValeBitacora;
  @Column (name="id_vale_estatus")
  private Long idValeEstatus;
  @Column (name="id_vale")
  private Long idVale;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanValesBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKalanValesBitacoraDto(Long key) {
    this(null, null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcKalanValesBitacoraDto(String justificacion, Long idUsuario, Long idValeBitacora, Long idValeEstatus, Long idVale) {
    setJustificacion(justificacion);
    setIdUsuario(idUsuario);
    setIdValeBitacora(idValeBitacora);
    setIdValeEstatus(idValeEstatus);
    setIdVale(idVale);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setJustificacion(String justificacion) {
    this.justificacion = justificacion;
  }

  public String getJustificacion() {
    return justificacion;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdValeBitacora(Long idValeBitacora) {
    this.idValeBitacora = idValeBitacora;
  }

  public Long getIdValeBitacora() {
    return idValeBitacora;
  }

  public void setIdValeEstatus(Long idValeEstatus) {
    this.idValeEstatus = idValeEstatus;
  }

  public Long getIdValeEstatus() {
    return idValeEstatus;
  }

  public void setIdVale(Long idVale) {
    this.idVale = idVale;
  }

  public Long getIdVale() {
    return idVale;
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
  	return getIdValeBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idValeBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdValeBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdValeEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdVale());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("justificacion", getJustificacion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idValeBitacora", getIdValeBitacora());
		regresar.put("idValeEstatus", getIdValeEstatus());
		regresar.put("idVale", getIdVale());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getJustificacion(), getIdUsuario(), getIdValeBitacora(), getIdValeEstatus(), getIdVale(), getRegistro()
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
    regresar.append("idValeBitacora~");
    regresar.append(getIdValeBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdValeBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanValesBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdValeBitacora()!= null && getIdValeBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanValesBitacoraDto other = (TcKalanValesBitacoraDto) obj;
    if (getIdValeBitacora() != other.idValeBitacora && (getIdValeBitacora() == null || !getIdValeBitacora().equals(other.idValeBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdValeBitacora() != null ? getIdValeBitacora().hashCode() : 0);
    return hash;
  }

}


