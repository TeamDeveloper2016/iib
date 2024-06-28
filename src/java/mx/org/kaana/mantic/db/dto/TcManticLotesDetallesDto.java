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
@Table(name="tc_mantic_lotes_detalles")
public class TcManticLotesDetallesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_nota_detalle")
  private Long idNotaDetalle;
  @Column (name="id_lote")
  private Long idLote;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_lote_detalle")
  private Long idLoteDetalle;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="saldo")
  private Double saldo;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="id_tipo_clase")
  private Long idTipoClase;
  @Column (name="registro")
  private Timestamp registro;

  public TcManticLotesDetallesDto() {
    this(new Long(-1L));
  }

  public TcManticLotesDetallesDto(Long key) {
    this(null, null, null, new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcManticLotesDetallesDto(Long idUsuario, Long idNotaDetalle, Long idLote, Long idLoteDetalle, Double cantidad, Double saldo, Long idArticulo, Long idTipoClase) {
    setIdUsuario(idUsuario);
    setIdNotaDetalle(idNotaDetalle);
    setIdLote(idLote);
    setIdLoteDetalle(idLoteDetalle);
    setCantidad(cantidad);
    setSaldo(saldo);
    setIdArticulo(idArticulo);
    setIdTipoClase(idTipoClase);
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

  public void setIdLote(Long idLote) {
    this.idLote = idLote;
  }

  public Long getIdLote() {
    return idLote;
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

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setIdArticulo(Long idArticulo) {
    this.idArticulo = idArticulo;
  }

  public Long getIdArticulo() {
    return idArticulo;
  }

  public Long getIdTipoClase() {
    return idTipoClase;
  }

  public void setIdTipoClase(Long idTipoClase) {
    this.idTipoClase = idTipoClase;
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
  	return getIdLoteDetalle();
  }

  @Override
  public void setKey(Long key) {
  	this.idLoteDetalle = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLoteDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSaldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoClase());
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
		regresar.put("idLote", getIdLote());
		regresar.put("idLoteDetalle", getIdLoteDetalle());
		regresar.put("cantidad", getCantidad());
		regresar.put("saldo", getSaldo());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("idTipoClase", getIdTipoClase());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getIdUsuario(), getIdNotaDetalle(), getIdLote(), getIdLoteDetalle(), getCantidad(), getSaldo(), getIdArticulo(), getIdTipoClase(), getRegistro()
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
    regresar.append("idLoteDetalle~");
    regresar.append(getIdLoteDetalle());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdLoteDetalle());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticLotesDetallesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdLoteDetalle()!= null && getIdLoteDetalle()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticLotesDetallesDto other = (TcManticLotesDetallesDto) obj;
    if (getIdLoteDetalle() != other.idLoteDetalle && (getIdLoteDetalle() == null || !getIdLoteDetalle().equals(other.idLoteDetalle))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdLoteDetalle() != null ? getIdLoteDetalle().hashCode() : 0);
    return hash;
  }

}


