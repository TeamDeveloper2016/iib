package mx.org.kaana.mantic.db.dto;

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
@Table(name="tc_mantic_articulos_promedios")
public class TcManticArticulosPromediosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_tipo_movimiento")
  private Long idTipoMovimiento;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="promedio")
  private Double promedio;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="observaciones")
  private String observaciones;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_articulo_promedio")
  private Long idArticuloPromedio;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="importe")
  private Double importe;
  @Column (name="registro")
  private Timestamp registro;

  public TcManticArticulosPromediosDto() {
    this(new Long(-1L));
  }

  public TcManticArticulosPromediosDto(Long key) {
    this(null, null, null, null, null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcManticArticulosPromediosDto(Long idTipoMovimiento, Long idUsuario, Double promedio, Double cantidad, String observaciones, Long idArticuloPromedio, Long idEmpresa, Long idArticulo, Double importe) {
    setIdTipoMovimiento(idTipoMovimiento);
    setIdUsuario(idUsuario);
    setPromedio(promedio);
    setCantidad(cantidad);
    setObservaciones(observaciones);
    setIdArticuloPromedio(idArticuloPromedio);
    setIdEmpresa(idEmpresa);
    setIdArticulo(idArticulo);
    setImporte(importe);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
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

  public void setPromedio(Double promedio) {
    this.promedio = promedio;
  }

  public Double getPromedio() {
    return promedio;
  }

  public void setCantidad(Double cantidad) {
    this.cantidad = cantidad;
  }

  public Double getCantidad() {
    return cantidad;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdArticuloPromedio(Long idArticuloPromedio) {
    this.idArticuloPromedio = idArticuloPromedio;
  }

  public Long getIdArticuloPromedio() {
    return idArticuloPromedio;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

  public void setIdArticulo(Long idArticulo) {
    this.idArticulo = idArticulo;
  }

  public Long getIdArticulo() {
    return idArticulo;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getImporte() {
    return importe;
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
  	return getIdArticuloPromedio();
  }

  @Override
  public void setKey(Long key) {
  	this.idArticuloPromedio = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdTipoMovimiento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPromedio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticuloPromedio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idTipoMovimiento", getIdTipoMovimiento());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("promedio", getPromedio());
		regresar.put("cantidad", getCantidad());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idArticuloPromedio", getIdArticuloPromedio());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("importe", getImporte());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getIdTipoMovimiento(), getIdUsuario(), getPromedio(), getCantidad(), getObservaciones(), getIdArticuloPromedio(), getIdEmpresa(), getIdArticulo(), getImporte(), getRegistro()
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
    regresar.append("idArticuloPromedio~");
    regresar.append(getIdArticuloPromedio());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdArticuloPromedio());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticArticulosPromediosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdArticuloPromedio()!= null && getIdArticuloPromedio()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticArticulosPromediosDto other = (TcManticArticulosPromediosDto) obj;
    if (getIdArticuloPromedio() != other.idArticuloPromedio && (getIdArticuloPromedio() == null || !getIdArticuloPromedio().equals(other.idArticuloPromedio))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdArticuloPromedio() != null ? getIdArticuloPromedio().hashCode() : 0);
    return hash;
  }

}


