package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
@Table(name="tc_keet_departamentos")
public class TcKeetDepartamentosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_departamento")
  private Long idDepartamento;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="nombre")
  private String nombre;
  @Column (name="id_especialidad")
  private Long idEspecialidad;
  @Column (name="id_oficina")
  private Long idOficina;
  @Column (name="id_tipo_gasto")
  private Long idTipoGasto;
  @Column (name="registro")
  private Timestamp registro;

  public TcKeetDepartamentosDto() {
    this(new Long(-1L));
  }

  public TcKeetDepartamentosDto(Long key) {
    this(null, new Long(-1L), null, null, 1L, 1L, 1L);
    setKey(key);
  }

  public TcKeetDepartamentosDto(String descripcion, Long idDepartamento, Long idUsuario, String nombre, Long idEspecialidad, Long idOficina, Long idTipoGasto) {
    setDescripcion(descripcion);
    setIdDepartamento(idDepartamento);
    setIdUsuario(idUsuario);
    setNombre(nombre);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    this.idEspecialidad= idEspecialidad;
    this.idOficina= idOficina;
    this.idTipoGasto= idTipoGasto;
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdDepartamento(Long idDepartamento) {
    this.idDepartamento = idDepartamento;
  }

  public Long getIdDepartamento() {
    return idDepartamento;
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

  public void setRegistro(Timestamp registro) {
    this.registro = registro;
  }

  public Timestamp getRegistro() {
    return registro;
  }

  public Long getIdEspecialidad() {
    return idEspecialidad;
  }

  public void setIdEspecialidad(Long idEspecialidad) {
    this.idEspecialidad = idEspecialidad;
  }

  public Long getIdOficina() {
    return idOficina;
  }

  public void setIdOficina(Long idOficina) {
    this.idOficina = idOficina;
  }

  public Long getIdTipoGasto() {
    return idTipoGasto;
  }

  public void setIdTipoGasto(Long idTipoGasto) {
    this.idTipoGasto = idTipoGasto;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdDepartamento();
  }

  @Override
  public void setKey(Long key) {
  	this.idDepartamento = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDepartamento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEspecialidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdOficina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoGasto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("idDepartamento", getIdDepartamento());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("nombre", getNombre());
		regresar.put("idEspecialidad", getIdEspecialidad());
		regresar.put("idOficina", getIdOficina());
		regresar.put("idTipoGasto", getIdTipoGasto());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getDescripcion(), getIdDepartamento(), getIdUsuario(), getNombre(), getIdEspecialidad(), getIdOficina(), getIdTipoGasto(), getRegistro()
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
    regresar.append("idDepartamento~");
    regresar.append(getIdDepartamento());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdDepartamento());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetDepartamentosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdDepartamento()!= null && getIdDepartamento()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetDepartamentosDto other = (TcKeetDepartamentosDto) obj;
    if (getIdDepartamento() != other.idDepartamento && (getIdDepartamento() == null || !getIdDepartamento().equals(other.idDepartamento))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdDepartamento() != null ? getIdDepartamento().hashCode() : 0);
    return hash;
  }
}
