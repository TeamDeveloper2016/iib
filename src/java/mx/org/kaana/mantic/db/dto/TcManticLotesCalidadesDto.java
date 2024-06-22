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
@Table(name="tc_mantic_lotes_calidades")
public class TcManticLotesCalidadesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_lote_calidad")
  private Long idLoteCalidad;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_lote")
  private Long idLote;
  @Column (name="id_nota_calidad")
  private Long idNotaCalidad;
  @Column (name="id_lote_detalle")
  private Long idLoteDetalle;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="porcentaje")
  private Double porcentaje;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="registro")
  private Timestamp registro;

  public TcManticLotesCalidadesDto() {
    this(new Long(-1L));
  }

  public TcManticLotesCalidadesDto(Long key) {
    this(new Long(-1L), null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcManticLotesCalidadesDto(Long idLoteCalidad, Long idUsuario, Long idLote, Long idNotaCalidad, Long idLoteDetalle, Double cantidad, Double porcentaje, Long idArticulo) {
    setIdLoteCalidad(idLoteCalidad);
    setIdUsuario(idUsuario);
    setIdLote(idLote);
    setIdNotaCalidad(idNotaCalidad);
    setIdLoteDetalle(idLoteDetalle);
    setCantidad(cantidad);
    setPorcentaje(porcentaje);
    setIdArticulo(idArticulo);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdLoteCalidad(Long idLoteCalidad) {
    this.idLoteCalidad = idLoteCalidad;
  }

  public Long getIdLoteCalidad() {
    return idLoteCalidad;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdLote(Long idLote) {
    this.idLote = idLote;
  }

  public Long getIdLote() {
    return idLote;
  }

  public void setIdNotaCalidad(Long idNotaCalidad) {
    this.idNotaCalidad = idNotaCalidad;
  }

  public Long getIdNotaCalidad() {
    return idNotaCalidad;
  }

  public void setIdLoteDetalle(Long idLoteDetalle) {
    this.idLoteDetalle = idLoteDetalle;
  }

  public Long getIdLoteDetalle() {
    return idLoteDetalle;
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

  public void setIdArticulo(Long idArticulo) {
    this.idArticulo = idArticulo;
  }

  public Long getIdArticulo() {
    return idArticulo;
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
  	return getIdLoteCalidad();
  }

  @Override
  public void setKey(Long key) {
  	this.idLoteCalidad = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdLoteCalidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaCalidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLoteDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentaje());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idLoteCalidad", getIdLoteCalidad());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idLote", getIdLote());
		regresar.put("idNotaCalidad", getIdNotaCalidad());
		regresar.put("idLoteDetalle", getIdLoteDetalle());
		regresar.put("cantidad", getCantidad());
		regresar.put("porcentaje", getPorcentaje());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdLoteCalidad(), getIdUsuario(), getIdLote(), getIdNotaCalidad(), getIdLoteDetalle(), getCantidad(), getPorcentaje(), getIdArticulo(), getRegistro()
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
    regresar.append("idLoteCalidad~");
    regresar.append(getIdLoteCalidad());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdLoteCalidad());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticLotesCalidadesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdLoteCalidad()!= null && getIdLoteCalidad()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticLotesCalidadesDto other = (TcManticLotesCalidadesDto) obj;
    if (getIdLoteCalidad() != other.idLoteCalidad && (getIdLoteCalidad() == null || !getIdLoteCalidad().equals(other.idLoteCalidad))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdLoteCalidad() != null ? getIdLoteCalidad().hashCode() : 0);
    return hash;
  }

}


