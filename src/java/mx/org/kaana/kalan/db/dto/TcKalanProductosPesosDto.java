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
@Table(name="tc_kalan_productos_pesos")
public class TcKalanProductosPesosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="clave")
  private String clave;
  @Column (name="peso")
  private Double peso;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_producto_peso")
  private Long idProductoPeso;
  @Column (name="id_producto")
  private Long idProducto;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanProductosPesosDto() {
    this(new Long(-1L));
  }

  public TcKalanProductosPesosDto(Long key) {
    this(null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKalanProductosPesosDto(String descripcion, String clave, Double peso, Long idProductoPeso, Long idProducto) {
    setDescripcion(descripcion);
    setClave(clave);
    setPeso(peso);
    setIdProductoPeso(idProductoPeso);
    setIdProducto(idProducto);
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

  public void setPeso(Double peso) {
    this.peso = peso;
  }

  public Double getPeso() {
    return peso;
  }

  public void setIdProductoPeso(Long idProductoPeso) {
    this.idProductoPeso = idProductoPeso;
  }

  public Long getIdProductoPeso() {
    return idProductoPeso;
  }

  public void setIdProducto(Long idProducto) {
    this.idProducto = idProducto;
  }

  public Long getIdProducto() {
    return idProducto;
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
  	return getIdProductoPeso();
  }

  @Override
  public void setKey(Long key) {
  	this.idProductoPeso = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPeso());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProductoPeso());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProducto());
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
		regresar.put("peso", getPeso());
		regresar.put("idProductoPeso", getIdProductoPeso());
		regresar.put("idProducto", getIdProducto());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getClave(), getPeso(), getIdProductoPeso(), getIdProducto(), getRegistro()
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
    regresar.append("idProductoPeso~");
    regresar.append(getIdProductoPeso());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdProductoPeso());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanProductosPesosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdProductoPeso()!= null && getIdProductoPeso()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanProductosPesosDto other = (TcKalanProductosPesosDto) obj;
    if (getIdProductoPeso() != other.idProductoPeso && (getIdProductoPeso() == null || !getIdProductoPeso().equals(other.idProductoPeso))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdProductoPeso() != null ? getIdProductoPeso().hashCode() : 0);
    return hash;
  }

}


