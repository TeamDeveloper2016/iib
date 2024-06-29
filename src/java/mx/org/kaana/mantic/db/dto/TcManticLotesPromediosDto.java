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
@Table(name="tc_mantic_lotes_promedios")
public class TcManticLotesPromediosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_lote")
  private Long idLote;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_lote_promedio")
  private Long idLotePromedio;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="porcentaje")
  private Double porcentaje;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="id_nota_calidad")
  private Long idNotaCalidad;

  public TcManticLotesPromediosDto() {
    this(new Long(-1L));
  }

  public TcManticLotesPromediosDto(Long key) {
    this(null, null, new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcManticLotesPromediosDto(Long idUsuario, Long idLote, Long idLotePromedio, Double cantidad, Double porcentaje, Long idArticulo, Long idNotaCalidad) {
    setIdUsuario(idUsuario);
    setIdLote(idLote);
    setIdLotePromedio(idLotePromedio);
    setCantidad(cantidad);
    setPorcentaje(porcentaje);
    setIdArticulo(idArticulo);
    setIdNotaCalidad(idNotaCalidad);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setRegistro(Timestamp registro) {
    this.registro = registro;
  }

  public Timestamp getRegistro() {
    return registro;
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

  public void setIdNotaCalidad(Long idNotaCalidad) {
    this.idNotaCalidad = idNotaCalidad;
  }

  public Long getIdNotaCalidad() {
    return idNotaCalidad;
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
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLotePromedio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentaje());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaCalidad());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("registro", getRegistro());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idLote", getIdLote());
		regresar.put("idLotePromedio", getIdLotePromedio());
		regresar.put("cantidad", getCantidad());
		regresar.put("porcentaje", getPorcentaje());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("idNotaCalidad", getIdNotaCalidad());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getRegistro(), getIdUsuario(), getIdLote(), getIdLotePromedio(), getCantidad(), getPorcentaje(), getIdArticulo(), getIdNotaCalidad()
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


