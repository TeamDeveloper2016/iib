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
@Table(name="tc_kalan_gastos_estatus")
public class TcKalanGastosEstatusDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_justificacion")
  private Long idJustificacion;
  @Column (name="estatus_asociado")
  private String estatusAsociado;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_gasto_estatus")
  private Long idGastoEstatus;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanGastosEstatusDto() {
    this(new Long(-1L));
  }

  public TcKalanGastosEstatusDto(Long key) {
    this(null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKalanGastosEstatusDto(String descripcion, Long idJustificacion, String estatusAsociado, Long idGastoEstatus, String nombre) {
    setDescripcion(descripcion);
    setIdJustificacion(idJustificacion);
    setEstatusAsociado(estatusAsociado);
    setIdGastoEstatus(idGastoEstatus);
    setNombre(nombre);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdJustificacion(Long idJustificacion) {
    this.idJustificacion = idJustificacion;
  }

  public Long getIdJustificacion() {
    return idJustificacion;
  }

  public void setEstatusAsociado(String estatusAsociado) {
    this.estatusAsociado = estatusAsociado;
  }

  public String getEstatusAsociado() {
    return estatusAsociado;
  }

  public void setIdGastoEstatus(Long idGastoEstatus) {
    this.idGastoEstatus = idGastoEstatus;
  }

  public Long getIdGastoEstatus() {
    return idGastoEstatus;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
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
  	return getIdGastoEstatus();
  }

  @Override
  public void setKey(Long key) {
  	this.idGastoEstatus = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEstatusAsociado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGastoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("idJustificacion", getIdJustificacion());
		regresar.put("estatusAsociado", getEstatusAsociado());
		regresar.put("idGastoEstatus", getIdGastoEstatus());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdJustificacion(), getEstatusAsociado(), getIdGastoEstatus(), getNombre(), getRegistro()
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
    regresar.append("idGastoEstatus~");
    regresar.append(getIdGastoEstatus());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdGastoEstatus());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanGastosEstatusDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdGastoEstatus()!= null && getIdGastoEstatus()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanGastosEstatusDto other = (TcKalanGastosEstatusDto) obj;
    if (getIdGastoEstatus() != other.idGastoEstatus && (getIdGastoEstatus() == null || !getIdGastoEstatus().equals(other.idGastoEstatus))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdGastoEstatus() != null ? getIdGastoEstatus().hashCode() : 0);
    return hash;
  }

}


