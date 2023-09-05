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
@Table(name="tc_kalan_movimientos_estatus")
public class TcKalanMovimientosEstatusDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_justificacion")
  private Long idJustificacion;
  @Column (name="estatus_asociados")
  private String estatusAsociados;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_movimiento_estatus")
  private Long idMovimientoEstatus;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanMovimientosEstatusDto() {
    this(new Long(-1L));
  }

  public TcKalanMovimientosEstatusDto(Long key) {
    this(null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKalanMovimientosEstatusDto(String descripcion, Long idJustificacion, String estatusAsociados, Long idMovimientoEstatus, String nombre) {
    setDescripcion(descripcion);
    setIdJustificacion(idJustificacion);
    setEstatusAsociados(estatusAsociados);
    setIdMovimientoEstatus(idMovimientoEstatus);
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

  public void setEstatusAsociados(String estatusAsociados) {
    this.estatusAsociados = estatusAsociados;
  }

  public String getEstatusAsociados() {
    return estatusAsociados;
  }

  public void setIdMovimientoEstatus(Long idMovimientoEstatus) {
    this.idMovimientoEstatus = idMovimientoEstatus;
  }

  public Long getIdMovimientoEstatus() {
    return idMovimientoEstatus;
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
  	return getIdMovimientoEstatus();
  }

  @Override
  public void setKey(Long key) {
  	this.idMovimientoEstatus = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEstatusAsociados());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMovimientoEstatus());
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
		regresar.put("estatusAsociados", getEstatusAsociados());
		regresar.put("idMovimientoEstatus", getIdMovimientoEstatus());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdJustificacion(), getEstatusAsociados(), getIdMovimientoEstatus(), getNombre(), getRegistro()
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
    regresar.append("idMovimientoEstatus~");
    regresar.append(getIdMovimientoEstatus());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdMovimientoEstatus());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanMovimientosEstatusDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdMovimientoEstatus()!= null && getIdMovimientoEstatus()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanMovimientosEstatusDto other = (TcKalanMovimientosEstatusDto) obj;
    if (getIdMovimientoEstatus() != other.idMovimientoEstatus && (getIdMovimientoEstatus() == null || !getIdMovimientoEstatus().equals(other.idMovimientoEstatus))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdMovimientoEstatus() != null ? getIdMovimientoEstatus().hashCode() : 0);
    return hash;
  }

}


