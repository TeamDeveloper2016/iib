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
@Table(name="tc_kalan_acreedores_estatus")
public class TcKalanAcreedoresEstatusDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_justificacion")
  private Long idJustificacion;
  @Column (name="estatus_asociados")
  private String estatusAsociados;
  @Column (name="nombre")
  private String nombre;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_acreedor_estatus")
  private Long idAcreedorEstatus;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanAcreedoresEstatusDto() {
    this(new Long(-1L));
  }

  public TcKalanAcreedoresEstatusDto(Long key) {
    this(null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKalanAcreedoresEstatusDto(String descripcion, Long idJustificacion, String estatusAsociados, String nombre, Long idAcreedorEstatus) {
    setDescripcion(descripcion);
    setIdJustificacion(idJustificacion);
    setEstatusAsociados(estatusAsociados);
    setNombre(nombre);
    setIdAcreedorEstatus(idAcreedorEstatus);
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

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setIdAcreedorEstatus(Long idAcreedorEstatus) {
    this.idAcreedorEstatus = idAcreedorEstatus;
  }

  public Long getIdAcreedorEstatus() {
    return idAcreedorEstatus;
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
  	return getIdAcreedorEstatus();
  }

  @Override
  public void setKey(Long key) {
  	this.idAcreedorEstatus = key;
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
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAcreedorEstatus());
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
		regresar.put("nombre", getNombre());
		regresar.put("idAcreedorEstatus", getIdAcreedorEstatus());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdJustificacion(), getEstatusAsociados(), getNombre(), getIdAcreedorEstatus(), getRegistro()
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
    regresar.append("idAcreedorEstatus~");
    regresar.append(getIdAcreedorEstatus());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAcreedorEstatus());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanAcreedoresEstatusDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAcreedorEstatus()!= null && getIdAcreedorEstatus()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanAcreedoresEstatusDto other = (TcKalanAcreedoresEstatusDto) obj;
    if (getIdAcreedorEstatus() != other.idAcreedorEstatus && (getIdAcreedorEstatus() == null || !getIdAcreedorEstatus().equals(other.idAcreedorEstatus))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAcreedorEstatus() != null ? getIdAcreedorEstatus().hashCode() : 0);
    return hash;
  }

}


