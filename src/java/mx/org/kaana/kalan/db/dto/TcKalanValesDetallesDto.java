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
@Table(name="tc_kalan_vales_detalles")
public class TcKalanValesDetallesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="codigo")
  private String codigo;
  @Column (name="precio")
  private Double precio;
  @Column (name="costo")
  private Double costo;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="id_vale")
  private Long idVale;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="nombre")
  private String nombre;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_vale_detalle")
  private Long idValeDetalle;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanValesDetallesDto() {
    this(new Long(-1L));
  }

  public TcKalanValesDetallesDto(Long key) {
    this(null, null, null, null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKalanValesDetallesDto(String codigo, Double precio, Double costo, Double cantidad, Long idVale, Long idArticulo, String nombre, Long idValeDetalle) {
    setCodigo(codigo);
    setPrecio(precio);
    setCosto(costo);
    setCantidad(cantidad);
    setIdVale(idVale);
    setIdArticulo(idArticulo);
    setNombre(nombre);
    setIdValeDetalle(idValeDetalle);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setPrecio(Double precio) {
    this.precio = precio;
  }

  public Double getPrecio() {
    return precio;
  }

  public void setCosto(Double costo) {
    this.costo = costo;
  }

  public Double getCosto() {
    return costo;
  }

  public void setCantidad(Double cantidad) {
    this.cantidad = cantidad;
  }

  public Double getCantidad() {
    return cantidad;
  }

  public void setIdVale(Long idVale) {
    this.idVale = idVale;
  }

  public Long getIdVale() {
    return idVale;
  }

  public void setIdArticulo(Long idArticulo) {
    this.idArticulo = idArticulo;
  }

  public Long getIdArticulo() {
    return idArticulo;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setIdValeDetalle(Long idValeDetalle) {
    this.idValeDetalle = idValeDetalle;
  }

  public Long getIdValeDetalle() {
    return idValeDetalle;
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
  	return getIdValeDetalle();
  }

  @Override
  public void setKey(Long key) {
  	this.idValeDetalle = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPrecio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdVale());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdValeDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("codigo", getCodigo());
		regresar.put("precio", getPrecio());
		regresar.put("costo", getCosto());
		regresar.put("cantidad", getCantidad());
		regresar.put("idVale", getIdVale());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("nombre", getNombre());
		regresar.put("idValeDetalle", getIdValeDetalle());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getCodigo(), getPrecio(), getCosto(), getCantidad(), getIdVale(), getIdArticulo(), getNombre(), getIdValeDetalle(), getRegistro()
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
    regresar.append("idValeDetalle~");
    regresar.append(getIdValeDetalle());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdValeDetalle());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanValesDetallesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdValeDetalle()!= null && getIdValeDetalle()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanValesDetallesDto other = (TcKalanValesDetallesDto) obj;
    if (getIdValeDetalle() != other.idValeDetalle && (getIdValeDetalle() == null || !getIdValeDetalle().equals(other.idValeDetalle))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdValeDetalle() != null ? getIdValeDetalle().hashCode() : 0);
    return hash;
  }

}


