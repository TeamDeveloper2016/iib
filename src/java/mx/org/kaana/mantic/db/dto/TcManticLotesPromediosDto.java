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
@Table(name="tc_mantic_lotes_promedios")
public class TcManticLotesPromediosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="regitro")
  private Timestamp regitro;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_lote")
  private Long idLote;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_lote_promedio")
  private Long idLotePromedio;
  @Column (name="id_lote_detalle")
  private Long idLoteDetalle;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="porcentaje")
  private Double porcentaje;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="id_nota_merma")
  private Long idNotaMerma;

  public TcManticLotesPromediosDto() {
    this(new Long(-1L));
  }

  public TcManticLotesPromediosDto(Long key) {
    this(new Timestamp(Calendar.getInstance().getTimeInMillis()), null, null, new Long(-1L), null, null, null, null, null);
    setKey(key);
  }

  public TcManticLotesPromediosDto(Timestamp regitro, Long idUsuario, Long idLote, Long idLotePromedio, Long idLoteDetalle, Double cantidad, Double porcentaje, Long idArticulo, Long idNotaMerma) {
    setRegitro(regitro);
    setIdUsuario(idUsuario);
    setIdLote(idLote);
    setIdLotePromedio(idLotePromedio);
    setIdLoteDetalle(idLoteDetalle);
    setCantidad(cantidad);
    setPorcentaje(porcentaje);
    setIdArticulo(idArticulo);
    setIdNotaMerma(idNotaMerma);
  }
	
  public void setRegitro(Timestamp regitro) {
    this.regitro = regitro;
  }

  public Timestamp getRegitro() {
    return regitro;
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

  public void setIdLotePromedio(Long idLotePromedio) {
    this.idLotePromedio = idLotePromedio;
  }

  public Long getIdLotePromedio() {
    return idLotePromedio;
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

  public void setIdNotaMerma(Long idNotaMerma) {
    this.idNotaMerma = idNotaMerma;
  }

  public Long getIdNotaMerma() {
    return idNotaMerma;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdLotePromedio();
  }

  @Override
  public void setKey(Long key) {
  	this.idLotePromedio = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getRegitro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLotePromedio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLoteDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentaje());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaMerma());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("regitro", getRegitro());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idLote", getIdLote());
		regresar.put("idLotePromedio", getIdLotePromedio());
		regresar.put("idLoteDetalle", getIdLoteDetalle());
		regresar.put("cantidad", getCantidad());
		regresar.put("porcentaje", getPorcentaje());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("idNotaMerma", getIdNotaMerma());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getRegitro(), getIdUsuario(), getIdLote(), getIdLotePromedio(), getIdLoteDetalle(), getCantidad(), getPorcentaje(), getIdArticulo(), getIdNotaMerma()
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
    regresar.append("idLotePromedio~");
    regresar.append(getIdLotePromedio());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdLotePromedio());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticLotesPromediosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdLotePromedio()!= null && getIdLotePromedio()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticLotesPromediosDto other = (TcManticLotesPromediosDto) obj;
    if (getIdLotePromedio() != other.idLotePromedio && (getIdLotePromedio() == null || !getIdLotePromedio().equals(other.idLotePromedio))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdLotePromedio() != null ? getIdLotePromedio().hashCode() : 0);
    return hash;
  }

}


