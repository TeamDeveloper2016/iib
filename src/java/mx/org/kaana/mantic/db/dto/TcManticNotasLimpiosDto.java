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
@Table(name="tc_mantic_notas_limpios")
public class TcManticNotasLimpiosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_usuarios")
  private Long idUsuarios;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nota_limpio")
  private Long idNotaLimpio;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="nombre")
  private String nombre;

  public TcManticNotasLimpiosDto() {
    this(new Long(-1L));
  }

  public TcManticNotasLimpiosDto(Long key) {
    this(null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcManticNotasLimpiosDto(String descripcion, Long idUsuarios, Long idNotaLimpio, String nombre) {
    setDescripcion(descripcion);
    setIdUsuarios(idUsuarios);
    setIdNotaLimpio(idNotaLimpio);
    setNombre(nombre);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdUsuarios(Long idUsuarios) {
    this.idUsuarios = idUsuarios;
  }

  public Long getIdUsuarios() {
    return idUsuarios;
  }

  public void setIdNotaLimpio(Long idNotaLimpio) {
    this.idNotaLimpio = idNotaLimpio;
  }

  public Long getIdNotaLimpio() {
    return idNotaLimpio;
  }

  public void setRegistro(Timestamp registro) {
    this.registro = registro;
  }

  public Timestamp getRegistro() {
    return registro;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdNotaLimpio();
  }

  @Override
  public void setKey(Long key) {
  	this.idNotaLimpio = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuarios());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaLimpio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("idUsuarios", getIdUsuarios());
		regresar.put("idNotaLimpio", getIdNotaLimpio());
		regresar.put("registro", getRegistro());
		regresar.put("nombre", getNombre());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdUsuarios(), getIdNotaLimpio(), getRegistro(), getNombre()
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
    regresar.append("idNotaLimpio~");
    regresar.append(getIdNotaLimpio());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNotaLimpio());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticNotasLimpiosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNotaLimpio()!= null && getIdNotaLimpio()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticNotasLimpiosDto other = (TcManticNotasLimpiosDto) obj;
    if (getIdNotaLimpio() != other.idNotaLimpio && (getIdNotaLimpio() == null || !getIdNotaLimpio().equals(other.idNotaLimpio))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNotaLimpio() != null ? getIdNotaLimpio().hashCode() : 0);
    return hash;
  }

}


