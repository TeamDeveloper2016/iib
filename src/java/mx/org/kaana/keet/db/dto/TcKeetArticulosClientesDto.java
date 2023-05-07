package mx.org.kaana.keet.db.dto;

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
@Table(name="tc_keet_articulos_clientes")
public class TcKeetArticulosClientesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_articulo_cliente")
  private Long idArticuloCliente;
  @Column (name="id_cliente")
  private Long idCliente;
  @Column (name="limite_medio_mayoreo")
  private Double limiteMedioMayoreo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="mayoreo")
  private Double mayoreo;
  @Column (name="menudeo")
  private Double menudeo;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="limite_mayoreo")
  private Double limiteMayoreo;
  @Column (name="medio_mayoreo")
  private Double medioMayoreo;
  @Column (name="actualizado")
  private Timestamp actualizado;
  @Column (name="registro")
  private Timestamp registro;

  public TcKeetArticulosClientesDto() {
    this(new Long(-1L));
  }

  public TcKeetArticulosClientesDto(Long key) {
    this(new Long(-1L), -1L, null, -1L, null, null, -1L, null, null, new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setKey(key);
  }

  public TcKeetArticulosClientesDto(Long idArticuloCliente, Long idCliente, Double limiteMedioMayoreo, Long idUsuario, Double mayoreo, Double menudeo, Long idArticulo, Double limiteMayoreo, Double medioMayoreo, Timestamp actualizado) {
    setIdArticuloCliente(idArticuloCliente);
    setIdCliente(idCliente);
    setLimiteMedioMayoreo(limiteMedioMayoreo);
    setIdUsuario(idUsuario);
    setMayoreo(mayoreo);
    setMenudeo(menudeo);
    setIdArticulo(idArticulo);
    setLimiteMayoreo(limiteMayoreo);
    setMedioMayoreo(medioMayoreo);
    setActualizado(actualizado);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdArticuloCliente(Long idArticuloCliente) {
    this.idArticuloCliente = idArticuloCliente;
  }

  public Long getIdArticuloCliente() {
    return idArticuloCliente;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setLimiteMedioMayoreo(Double limiteMedioMayoreo) {
    this.limiteMedioMayoreo = limiteMedioMayoreo;
  }

  public Double getLimiteMedioMayoreo() {
    return limiteMedioMayoreo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setMayoreo(Double mayoreo) {
    this.mayoreo = mayoreo;
  }

  public Double getMayoreo() {
    return mayoreo;
  }

  public void setMenudeo(Double menudeo) {
    this.menudeo = menudeo;
  }

  public Double getMenudeo() {
    return menudeo;
  }

  public void setIdArticulo(Long idArticulo) {
    this.idArticulo = idArticulo;
  }

  public Long getIdArticulo() {
    return idArticulo;
  }

  public void setLimiteMayoreo(Double limiteMayoreo) {
    this.limiteMayoreo = limiteMayoreo;
  }

  public Double getLimiteMayoreo() {
    return limiteMayoreo;
  }

  public void setMedioMayoreo(Double medioMayoreo) {
    this.medioMayoreo = medioMayoreo;
  }

  public Double getMedioMayoreo() {
    return medioMayoreo;
  }

  public Timestamp getActualizado() {
    return actualizado;
  }

  public void setActualizado(Timestamp actualizado) {
    this.actualizado = actualizado;
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
  	return getIdArticuloCliente();
  }

  @Override
  public void setKey(Long key) {
  	this.idArticuloCliente = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdArticuloCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLimiteMedioMayoreo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getMayoreo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getMenudeo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLimiteMayoreo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getMedioMayoreo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getActualizado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idArticuloCliente", getIdArticuloCliente());
		regresar.put("idCliente", getIdCliente());
		regresar.put("limiteMedioMayoreo", getLimiteMedioMayoreo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("mayoreo", getMayoreo());
		regresar.put("menudeo", getMenudeo());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("limiteMayoreo", getLimiteMayoreo());
		regresar.put("medioMayoreo", getMedioMayoreo());
		regresar.put("actualizado", getActualizado());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getIdArticuloCliente(), getIdCliente(), getLimiteMedioMayoreo(), getIdUsuario(), getMayoreo(), getMenudeo(), getIdArticulo(), getLimiteMayoreo(), getMedioMayoreo(), getActualizado(), getRegistro()
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
    regresar.append("idArticuloCliente~");
    regresar.append(getIdArticuloCliente());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdArticuloCliente());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetArticulosClientesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdArticuloCliente()!= null && getIdArticuloCliente()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetArticulosClientesDto other = (TcKeetArticulosClientesDto) obj;
    if (getIdArticuloCliente() != other.idArticuloCliente && (getIdArticuloCliente() == null || !getIdArticuloCliente().equals(other.idArticuloCliente))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdArticuloCliente() != null ? getIdArticuloCliente().hashCode() : 0);
    return hash;
  }

}


