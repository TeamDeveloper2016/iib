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
@Table(name="tc_mantic_tipos_clases")
public class TcManticTiposClasesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_tipo_clase")
  private Long idTipoClase;
  @Column (name="clave")
  private String clave;
  @Column (name="sat")
  private String sat;
  @Column (name="registro")
  private Timestamp registro;

  public TcManticTiposClasesDto() {
    this(new Long(-1L));
  }

  public TcManticTiposClasesDto(Long key) {
    this(null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcManticTiposClasesDto(String descripcion, Long idTipoClase, String clave, String sat) {
    setDescripcion(descripcion);
    setIdTipoClase(idTipoClase);
    setClave(clave);
    setSat(sat);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdTipoClase(Long idTipoClase) {
    this.idTipoClase = idTipoClase;
  }

  public Long getIdTipoClase() {
    return idTipoClase;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setSat(String sat) {
    this.sat = sat;
  }

  public String getSat() {
    return sat;
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
  	return getIdTipoClase();
  }

  @Override
  public void setKey(Long key) {
  	this.idTipoClase = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoClase());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSat());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("idTipoClase", getIdTipoClase());
		regresar.put("clave", getClave());
		regresar.put("sat", getSat());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdTipoClase(), getClave(), getSat(), getRegistro()
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
    regresar.append("idTipoClase~");
    regresar.append(getIdTipoClase());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdTipoClase());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticTiposClasesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdTipoClase()!= null && getIdTipoClase()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticTiposClasesDto other = (TcManticTiposClasesDto) obj;
    if (getIdTipoClase() != other.idTipoClase && (getIdTipoClase() == null || !getIdTipoClase().equals(other.idTipoClase))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdTipoClase() != null ? getIdTipoClase().hashCode() : 0);
    return hash;
  }

}


