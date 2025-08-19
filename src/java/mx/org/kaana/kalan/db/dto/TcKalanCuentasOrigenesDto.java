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
@Table(name="tc_kalan_cuentas_origenes")
public class TcKalanCuentasOrigenesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="clave")
  private String clave;
  @Column (name="nombre")
  private String nombre;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_cuenta_origen")
  private Long idCuentaOrigen;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanCuentasOrigenesDto() {
    this(new Long(-1L));
  }

  public TcKalanCuentasOrigenesDto(Long key) {
    this(null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKalanCuentasOrigenesDto(String descripcion, String clave, String nombre, Long idCuentaOrigen) {
    setDescripcion(descripcion);
    setClave(clave);
    setNombre(nombre);
    setIdCuentaOrigen(idCuentaOrigen);
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

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setIdCuentaOrigen(Long idCuentaOrigen) {
    this.idCuentaOrigen = idCuentaOrigen;
  }

  public Long getIdCuentaOrigen() {
    return idCuentaOrigen;
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
  	return getIdCuentaOrigen();
  }

  @Override
  public void setKey(Long key) {
  	this.idCuentaOrigen = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCuentaOrigen());
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
		regresar.put("nombre", getNombre());
		regresar.put("idCuentaOrigen", getIdCuentaOrigen());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getClave(), getNombre(), getIdCuentaOrigen(), getRegistro()
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
    regresar.append("idCuentaOrigen~");
    regresar.append(getIdCuentaOrigen());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdCuentaOrigen());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanCuentasOrigenesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdCuentaOrigen()!= null && getIdCuentaOrigen()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanCuentasOrigenesDto other = (TcKalanCuentasOrigenesDto) obj;
    if (getIdCuentaOrigen() != other.idCuentaOrigen && (getIdCuentaOrigen() == null || !getIdCuentaOrigen().equals(other.idCuentaOrigen))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdCuentaOrigen() != null ? getIdCuentaOrigen().hashCode() : 0);
    return hash;
  }

}


