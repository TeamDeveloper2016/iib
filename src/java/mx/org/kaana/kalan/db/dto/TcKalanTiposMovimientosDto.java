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
@Table(name="tc_kalan_tipos_movimientos")
public class TcKalanTiposMovimientosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="clave")
  private String clave;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_tipo_movimiento")
  private Long idTipoMovimiento;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanTiposMovimientosDto() {
    this(new Long(-1L));
  }

  public TcKalanTiposMovimientosDto(Long key) {
    this(null, null, new Long(-1L));
    setKey(key);
  }

  public TcKalanTiposMovimientosDto(String descripcion, String clave, Long idTipoMovimiento) {
    setDescripcion(descripcion);
    setClave(clave);
    setIdTipoMovimiento(idTipoMovimiento);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setIdTipoMovimiento(Long idTipoMovimiento) {
    this.idTipoMovimiento = idTipoMovimiento;
  }

  public Long getIdTipoMovimiento() {
    return idTipoMovimiento;
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
  	return getIdTipoMovimiento();
  }

  @Override
  public void setKey(Long key) {
  	this.idTipoMovimiento = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMovimiento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("clave", getClave());
		regresar.put("idTipoMovimiento", getIdTipoMovimiento());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getClave(), getIdTipoMovimiento(), getRegistro()
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
    regresar.append("idTipoMovimiento~");
    regresar.append(getIdTipoMovimiento());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdTipoMovimiento());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanTiposMovimientosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdTipoMovimiento()!= null && getIdTipoMovimiento()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanTiposMovimientosDto other = (TcKalanTiposMovimientosDto) obj;
    if (getIdTipoMovimiento() != other.idTipoMovimiento && (getIdTipoMovimiento() == null || !getIdTipoMovimiento().equals(other.idTipoMovimiento))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdTipoMovimiento() != null ? getIdTipoMovimiento().hashCode() : 0);
    return hash;
  }

}


