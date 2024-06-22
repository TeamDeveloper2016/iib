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
@Table(name="tc_mantic_lotes_limpios")
public class TcManticLotesLimpiosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_lote_limpio")
  private Long idLoteLimpio;
  @Column (name="id_lote")
  private Long idLote;
  @Column (name="id_nota_limpio")
  private Long idNotaLimpio;
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

  public TcManticLotesLimpiosDto() {
    this(new Long(-1L));
  }

  public TcManticLotesLimpiosDto(Long key) {
    this(null, new Long(-1L), null, null, null, null, null, null);
    setKey(key);
  }

  public TcManticLotesLimpiosDto(Long idUsuario, Long idLoteLimpio, Long idLote, Long idNotaLimpio, Long idLoteDetalle, Double cantidad, Double porcentaje, Long idArticulo) {
    setIdUsuario(idUsuario);
    setIdLoteLimpio(idLoteLimpio);
    setIdLote(idLote);
    setIdNotaLimpio(idNotaLimpio);
    setIdLoteDetalle(idLoteDetalle);
    setCantidad(cantidad);
    setPorcentaje(porcentaje);
    setIdArticulo(idArticulo);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdLoteLimpio(Long idLoteLimpio) {
    this.idLoteLimpio = idLoteLimpio;
  }

  public Long getIdLoteLimpio() {
    return idLoteLimpio;
  }

  public void setIdLote(Long idLote) {
    this.idLote = idLote;
  }

  public Long getIdLote() {
    return idLote;
  }

  public void setIdNotaLimpio(Long idNotaLimpio) {
    this.idNotaLimpio = idNotaLimpio;
  }

  public Long getIdNotaLimpio() {
    return idNotaLimpio;
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
  	return getIdLoteLimpio();
  }

  @Override
  public void setKey(Long key) {
  	this.idLoteLimpio = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLoteLimpio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaLimpio());
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
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idLoteLimpio", getIdLoteLimpio());
		regresar.put("idLote", getIdLote());
		regresar.put("idNotaLimpio", getIdNotaLimpio());
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
    getIdUsuario(), getIdLoteLimpio(), getIdLote(), getIdNotaLimpio(), getIdLoteDetalle(), getCantidad(), getPorcentaje(), getIdArticulo(), getRegistro()
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
    regresar.append("idLoteLimpio~");
    regresar.append(getIdLoteLimpio());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdLoteLimpio());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticLotesLimpiosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdLoteLimpio()!= null && getIdLoteLimpio()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticLotesLimpiosDto other = (TcManticLotesLimpiosDto) obj;
    if (getIdLoteLimpio() != other.idLoteLimpio && (getIdLoteLimpio() == null || !getIdLoteLimpio().equals(other.idLoteLimpio))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdLoteLimpio() != null ? getIdLoteLimpio().hashCode() : 0);
    return hash;
  }

}


