package mx.org.kaana.mantic.db.dto;

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
@Table(name="tc_mantic_notas_promedios")
public class TcManticNotasPromediosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_nota_detalle")
  private Long idNotaDetalle;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nota_promedio")
  private Long idNotaPromedio;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="porcentaje")
  private Double porcentaje;
  @Column (name="id_nota_entrada")
  private Long idNotaEntrada;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="id_nota_calidad")
  private Long idNotaCalidad;
  @Column (name="registro")
  private Timestamp registro;

  public TcManticNotasPromediosDto() {
    this(new Long(-1L));
  }

  public TcManticNotasPromediosDto(Long key) {
    this(null, null, new Long(-1L), null, null, null, null, null);
    setKey(key);
  }

  public TcManticNotasPromediosDto(Long idUsuario, Long idNotaDetalle, Long idNotaPromedio, Double cantidad, Double porcentaje, Long idNotaEntrada, Long idArticulo, Long idNotaCalidad) {
    setIdUsuario(idUsuario);
    setIdNotaDetalle(idNotaDetalle);
    setIdNotaPromedio(idNotaPromedio);
    setCantidad(cantidad);
    setPorcentaje(porcentaje);
    setIdNotaEntrada(idNotaEntrada);
    setIdArticulo(idArticulo);
    setIdNotaCalidad(idNotaCalidad);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdNotaDetalle(Long idNotaDetalle) {
    this.idNotaDetalle = idNotaDetalle;
  }

  public Long getIdNotaDetalle() {
    return idNotaDetalle;
  }

  public void setIdNotaPromedio(Long idNotaPromedio) {
    this.idNotaPromedio = idNotaPromedio;
  }

  public Long getIdNotaPromedio() {
    return idNotaPromedio;
  }

  public void setCantidad(Double cantidad) {
    this.cantidad = cantidad;
  }

  public Double getCantidad() {
    return cantidad;
  }

  public void setPorcentaje(Double porcentaje) {
    this.porcentaje = porcentaje;
  }

  public Double getPorcentaje() {
    return porcentaje;
  }

  public void setIdNotaEntrada(Long idNotaEntrada) {
    this.idNotaEntrada = idNotaEntrada;
  }

  public Long getIdNotaEntrada() {
    return idNotaEntrada;
  }

  public void setIdArticulo(Long idArticulo) {
    this.idArticulo = idArticulo;
  }

  public Long getIdArticulo() {
    return idArticulo;
  }

  public void setIdNotaCalidad(Long idNotaCalidad) {
    this.idNotaCalidad = idNotaCalidad;
  }

  public Long getIdNotaCalidad() {
    return idNotaCalidad;
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
  	return getIdNotaPromedio();
  }

  @Override
  public void setKey(Long key) {
  	this.idNotaPromedio = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaPromedio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentaje());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaEntrada());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaCalidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idNotaDetalle", getIdNotaDetalle());
		regresar.put("idNotaPromedio", getIdNotaPromedio());
		regresar.put("cantidad", getCantidad());
		regresar.put("porcentaje", getPorcentaje());
		regresar.put("idNotaEntrada", getIdNotaEntrada());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("idNotaCalidad", getIdNotaCalidad());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getIdUsuario(), getIdNotaDetalle(), getIdNotaPromedio(), getCantidad(), getPorcentaje(), getIdNotaEntrada(), getIdArticulo(), getIdNotaCalidad(), getRegistro()
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
    regresar.append("idNotaPromedio~");
    regresar.append(getIdNotaPromedio());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNotaPromedio());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticNotasPromediosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNotaPromedio()!= null && getIdNotaPromedio()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticNotasPromediosDto other = (TcManticNotasPromediosDto) obj;
    if (getIdNotaPromedio() != other.idNotaPromedio && (getIdNotaPromedio() == null || !getIdNotaPromedio().equals(other.idNotaPromedio))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNotaPromedio() != null ? getIdNotaPromedio().hashCode() : 0);
    return hash;
  }

}


