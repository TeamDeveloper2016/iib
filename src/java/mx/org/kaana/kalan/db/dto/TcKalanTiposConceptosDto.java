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
@Table(name="tc_kalan_tipos_conceptos")
public class TcKalanTiposConceptosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_tipo_concepto")
  private Long idTipoConcepto;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_activo")
  private Long idActivo;
  @Column (name="id_tipo_movimiento")
  private Long idTipoMovimiento;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanTiposConceptosDto() {
    this(new Long(-1L));
  }

  public TcKalanTiposConceptosDto(Long key) {
    this(new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKalanTiposConceptosDto(Long idTipoConcepto, String descripcion, Long idActivo, Long idTipoMovimiento, Long idUsuario) {
    setIdTipoConcepto(idTipoConcepto);
    setDescripcion(descripcion);
    setIdActivo(idActivo);
    setIdTipoMovimiento(idTipoMovimiento);
    setIdUsuario(idUsuario);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdTipoConcepto(Long idTipoConcepto) {
    this.idTipoConcepto = idTipoConcepto;
  }

  public Long getIdTipoConcepto() {
    return idTipoConcepto;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdActivo(Long idActivo) {
    this.idActivo = idActivo;
  }

  public Long getIdActivo() {
    return idActivo;
  }

  public void setIdTipoMovimiento(Long idTipoMovimiento) {
    this.idTipoMovimiento = idTipoMovimiento;
  }

  public Long getIdTipoMovimiento() {
    return idTipoMovimiento;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
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
  	return getIdTipoConcepto();
  }

  @Override
  public void setKey(Long key) {
  	this.idTipoConcepto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdTipoConcepto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdActivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMovimiento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idTipoConcepto", getIdTipoConcepto());
		regresar.put("descripcion", getDescripcion());
		regresar.put("idActivo", getIdActivo());
		regresar.put("idTipoMovimiento", getIdTipoMovimiento());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdTipoConcepto(), getDescripcion(), getIdActivo(), getIdTipoMovimiento(), getIdUsuario(), getRegistro()
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
    regresar.append("idTipoConcepto~");
    regresar.append(getIdTipoConcepto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdTipoConcepto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanTiposConceptosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdTipoConcepto()!= null && getIdTipoConcepto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanTiposConceptosDto other = (TcKalanTiposConceptosDto) obj;
    if (getIdTipoConcepto() != other.idTipoConcepto && (getIdTipoConcepto() == null || !getIdTipoConcepto().equals(other.idTipoConcepto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdTipoConcepto() != null ? getIdTipoConcepto().hashCode() : 0);
    return hash;
  }

}


