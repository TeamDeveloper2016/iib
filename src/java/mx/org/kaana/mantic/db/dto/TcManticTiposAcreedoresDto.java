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
@Table(name="tc_mantic_tipos_acreedores")
public class TcManticTiposAcreedoresDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="nombre")
  private String nombre;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_tipo_acreedor")
  private Long idTipoAcreedor;
  @Column (name="registro")
  private Timestamp registro;

  public TcManticTiposAcreedoresDto() {
    this(new Long(-1L));
  }

  public TcManticTiposAcreedoresDto(Long key) {
    this(null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcManticTiposAcreedoresDto(String descripcion, Long idUsuario, String nombre, Long idTipoAcreedor) {
    setDescripcion(descripcion);
    setIdUsuario(idUsuario);
    setNombre(nombre);
    setIdTipoAcreedor(idTipoAcreedor);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setIdTipoAcreedor(Long idTipoAcreedor) {
    this.idTipoAcreedor = idTipoAcreedor;
  }

  public Long getIdTipoAcreedor() {
    return idTipoAcreedor;
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
  	return getIdTipoAcreedor();
  }

  @Override
  public void setKey(Long key) {
  	this.idTipoAcreedor = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoAcreedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("nombre", getNombre());
		regresar.put("idTipoAcreedor", getIdTipoAcreedor());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdUsuario(), getNombre(), getIdTipoAcreedor(), getRegistro()
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
    regresar.append("idTipoAcreedor~");
    regresar.append(getIdTipoAcreedor());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdTipoAcreedor());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticTiposAcreedoresDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdTipoAcreedor()!= null && getIdTipoAcreedor()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticTiposAcreedoresDto other = (TcManticTiposAcreedoresDto) obj;
    if (getIdTipoAcreedor() != other.idTipoAcreedor && (getIdTipoAcreedor() == null || !getIdTipoAcreedor().equals(other.idTipoAcreedor))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdTipoAcreedor() != null ? getIdTipoAcreedor().hashCode() : 0);
    return hash;
  }

}


