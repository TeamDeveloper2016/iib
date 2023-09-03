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
@Table(name="tc_kalan_gastos_clasificaciones")
public class TcKalanGastosClasificacionesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="clave")
  private String clave;
  @Column (name="id_activo")
  private Long idActivo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_gasto_clasificacion")
  private Long idGastoClasificacion;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanGastosClasificacionesDto() {
    this(new Long(-1L));
  }

  public TcKalanGastosClasificacionesDto(Long key) {
    this(null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKalanGastosClasificacionesDto(String descripcion, String clave, Long idActivo, Long idGastoClasificacion) {
    setDescripcion(descripcion);
    setClave(clave);
    setIdActivo(idActivo);
    setIdGastoClasificacion(idGastoClasificacion);
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

  public void setIdActivo(Long idActivo) {
    this.idActivo = idActivo;
  }

  public Long getIdActivo() {
    return idActivo;
  }

  public void setIdGastoClasificacion(Long idGastoClasificacion) {
    this.idGastoClasificacion = idGastoClasificacion;
  }

  public Long getIdGastoClasificacion() {
    return idGastoClasificacion;
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
  	return getIdGastoClasificacion();
  }

  @Override
  public void setKey(Long key) {
  	this.idGastoClasificacion = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdActivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGastoClasificacion());
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
		regresar.put("idActivo", getIdActivo());
		regresar.put("idGastoClasificacion", getIdGastoClasificacion());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getClave(), getIdActivo(), getIdGastoClasificacion(), getRegistro()
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
    regresar.append("idGastoClasificacion~");
    regresar.append(getIdGastoClasificacion());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdGastoClasificacion());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanGastosClasificacionesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdGastoClasificacion()!= null && getIdGastoClasificacion()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanGastosClasificacionesDto other = (TcKalanGastosClasificacionesDto) obj;
    if (getIdGastoClasificacion() != other.idGastoClasificacion && (getIdGastoClasificacion() == null || !getIdGastoClasificacion().equals(other.idGastoClasificacion))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdGastoClasificacion() != null ? getIdGastoClasificacion().hashCode() : 0);
    return hash;
  }

}


