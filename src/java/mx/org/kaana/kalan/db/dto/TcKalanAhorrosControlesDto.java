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
@Table(name="tc_kalan_ahorros_controles")
public class TcKalanAhorrosControlesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_ahorro_control")
  private Long idAhorroControl;
  @Column (name="descrpcion")
  private String descrpcion;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanAhorrosControlesDto() {
    this(new Long(-1L));
  }

  public TcKalanAhorrosControlesDto(Long key) {
    this(new Long(-1L), null, null);
    setKey(key);
  }

  public TcKalanAhorrosControlesDto(Long idAhorroControl, String descrpcion, String nombre) {
    setIdAhorroControl(idAhorroControl);
    setDescrpcion(descrpcion);
    setNombre(nombre);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdAhorroControl(Long idAhorroControl) {
    this.idAhorroControl = idAhorroControl;
  }

  public Long getIdAhorroControl() {
    return idAhorroControl;
  }

  public void setDescrpcion(String descrpcion) {
    this.descrpcion = descrpcion;
  }

  public String getDescrpcion() {
    return descrpcion;
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
  	return getIdAhorroControl();
  }

  @Override
  public void setKey(Long key) {
  	this.idAhorroControl = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdAhorroControl());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescrpcion());
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
		regresar.put("idAhorroControl", getIdAhorroControl());
		regresar.put("descrpcion", getDescrpcion());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdAhorroControl(), getDescrpcion(), getNombre(), getRegistro()
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
    regresar.append("idAhorroControl~");
    regresar.append(getIdAhorroControl());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAhorroControl());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanAhorrosControlesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAhorroControl()!= null && getIdAhorroControl()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanAhorrosControlesDto other = (TcKalanAhorrosControlesDto) obj;
    if (getIdAhorroControl() != other.idAhorroControl && (getIdAhorroControl() == null || !getIdAhorroControl().equals(other.idAhorroControl))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAhorroControl() != null ? getIdAhorroControl().hashCode() : 0);
    return hash;
  }

}


