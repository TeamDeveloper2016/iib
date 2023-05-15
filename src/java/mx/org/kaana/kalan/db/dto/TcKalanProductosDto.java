package mx.org.kaana.kalan.db.dto;

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
@Table(name="tc_kalan_productos")
public class TcKalanProductosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="clave")
  private String clave;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_producto")
  private Long idProducto;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="precio1")
  private Double precio1;
  @Column (name="precio2")
  private Double precio2;
  @Column (name="precio3")
  private Double precio3;
  @Column (name="costo")
  private Double costo;
  @Column (name="actualizado")
  private Timestamp actualizado;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanProductosDto() {
    this(new Long(-1L));
  }

  public TcKalanProductosDto(Long key) {
    this(null, null, null, new Long(-1L), null, null, null, null, new Timestamp(Calendar.getInstance().getTimeInMillis()), 0D);
    setKey(key);
  }

  public TcKalanProductosDto(String descripcion, String clave, Long idUsuario, Long idProducto, Long idEmpresa, Double precio1, Double precio2, Double precio3, Timestamp actualizado, Double costo) {
    setDescripcion(descripcion);
    setClave(clave);
    setIdUsuario(idUsuario);
    setIdProducto(idProducto);
    setIdEmpresa(idEmpresa);
    setPrecio1(precio1);
    setPrecio2(precio2);
    setPrecio3(precio3);
    setActualizado(actualizado);
    setCosto(costo);
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

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdProducto(Long idProducto) {
    this.idProducto = idProducto;
  }

  public Long getIdProducto() {
    return idProducto;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

  public void setPrecio1(Double precio1) {
    this.precio1 = precio1;
  }

  public Double getPrecio1() {
    return precio1;
  }

  public void setPrecio2(Double precio2) {
    this.precio2 = precio2;
  }

  public Double getPrecio2() {
    return precio2;
  }

  public void setPrecio3(Double precio3) {
    this.precio3 = precio3;
  }

  public Double getPrecio3() {
    return precio3;
  }

  public void setActualizado(Timestamp actualizado) {
    this.actualizado = actualizado;
  }

  public Timestamp getActualizado() {
    return actualizado;
  }

  public Double getCosto() {
    return costo;
  }

  public void setCosto(Double costo) {
    this.costo = costo;
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
  	return getIdProducto();
  }

  @Override
  public void setKey(Long key) {
  	this.idProducto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProducto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPrecio1());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPrecio2());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPrecio3());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getActualizado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
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
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idProducto", getIdProducto());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("precio1", getPrecio1());
		regresar.put("precio2", getPrecio2());
		regresar.put("precio3", getPrecio3());
		regresar.put("costo", getCosto());
		regresar.put("actualizado", getActualizado());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getDescripcion(), getClave(), getIdUsuario(), getIdProducto(), getIdEmpresa(), getPrecio1(), getPrecio2(), getPrecio3(), getCosto(), getActualizado(), getRegistro()
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
    regresar.append("idProducto~");
    regresar.append(getIdProducto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdProducto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanProductosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdProducto()!= null && getIdProducto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) 
      return false;
    if (getClass() != obj.getClass()) 
      return false;
    final TcKalanProductosDto other = (TcKalanProductosDto) obj;
    if (getIdProducto() != other.idProducto && (getIdProducto() == null || !getIdProducto().equals(other.idProducto))) 
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdProducto() != null ? getIdProducto().hashCode() : 0);
    return hash;
  }

}


