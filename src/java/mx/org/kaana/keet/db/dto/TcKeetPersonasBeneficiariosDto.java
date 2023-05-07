package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Date;
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
@Table(name="tc_keet_personas_beneficiarios")
public class TcKeetPersonasBeneficiariosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="materno")
  private String materno;
  @Column (name="id_empresa_persona")
  private Long idEmpresaPersona;
  @Column (name="paterno")
  private String paterno;
  @Column (name="id_tipo_parentesco")
  private Long idTipoParentesco;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_persona_beneficiario")
  private Long idPersonaBeneficiario;
  @Column (name="nombre")
  private String nombre;  
  @Column (name="registro")
  private Timestamp registro;
	@Column(name = "fecha_nacimiento")
  private Date fechaNacimiento;  
  @Column (name="contacto")
  private String contacto;  

  public TcKeetPersonasBeneficiariosDto() {
    this(new Long(-1L));
  }

  public TcKeetPersonasBeneficiariosDto(Long key) {
    this(null, null, null, null, null, null, new Long(-1L), null, new Date(Calendar.getInstance().getTimeInMillis()), null);
    setKey(key);
  }

  public TcKeetPersonasBeneficiariosDto(String materno, Long idEmpresaPersona, String paterno, Long idTipoParentesco, Long idUsuario, String observaciones, Long idPersonaBeneficiario, String nombre, Date fechaNacimiento, String contacto) {
    setMaterno(materno);
    setIdEmpresaPersona(idEmpresaPersona);
    setPaterno(paterno);
    setIdTipoParentesco(idTipoParentesco);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setIdPersonaBeneficiario(idPersonaBeneficiario);
    setNombre(nombre);    
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		setFechaNacimiento(fechaNacimiento);
    this.contacto= contacto;
  }
	
  public void setMaterno(String materno) {
    this.materno = materno;
  }

  public String getMaterno() {
    return materno;
  }

	public Long getIdEmpresaPersona() {
		return idEmpresaPersona;
	}

	public void setIdEmpresaPersona(Long idEmpresaPersona) {
		this.idEmpresaPersona=idEmpresaPersona;
	}

  public void setPaterno(String paterno) {
    this.paterno = paterno;
  }

  public String getPaterno() {
    return paterno;
  }

  public void setIdTipoParentesco(Long idTipoParentesco) {
    this.idTipoParentesco = idTipoParentesco;
  }

  public Long getIdTipoParentesco() {
    return idTipoParentesco;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdPersonaBeneficiario(Long idPersonaBeneficiario) {
    this.idPersonaBeneficiario = idPersonaBeneficiario;
  }

  public Long getIdPersonaBeneficiario() {
    return idPersonaBeneficiario;
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

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}	

  public String getContacto() {
    return contacto;
  }

  public void setContacto(String contacto) {
    this.contacto = contacto;
  }
	
  @Transient
  @Override
  public Long getKey() {
  	return getIdPersonaBeneficiario();
  }

  @Override
  public void setKey(Long key) {
  	this.idPersonaBeneficiario = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getMaterno());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPaterno());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoParentesco());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPersonaBeneficiario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);		
		regresar.append(getFechaNacimiento());
		regresar.append(Constantes.SEPARADOR);		
		regresar.append(getContacto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("materno", getMaterno());
		regresar.put("idPersona", getIdEmpresaPersona());
		regresar.put("paterno", getPaterno());
		regresar.put("idTipoParentesco", getIdTipoParentesco());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idPersonaBeneficiario", getIdPersonaBeneficiario());
		regresar.put("nombre", getNombre());
		regresar.put("fechaNacimiento", getFechaNacimiento());
		regresar.put("contacto", getContacto());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
			getMaterno(), getIdEmpresaPersona(), getPaterno(), getIdTipoParentesco(), getIdUsuario(), getObservaciones(), getIdPersonaBeneficiario(), getNombre(), getFechaNacimiento(), getContacto(), getRegistro()
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
    regresar.append("idPersonaBeneficiario~");
    regresar.append(getIdPersonaBeneficiario());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPersonaBeneficiario());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPersonasBeneficiariosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPersonaBeneficiario()!= null && getIdPersonaBeneficiario()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPersonasBeneficiariosDto other = (TcKeetPersonasBeneficiariosDto) obj;
    if (getIdPersonaBeneficiario() != other.idPersonaBeneficiario && (getIdPersonaBeneficiario() == null || !getIdPersonaBeneficiario().equals(other.idPersonaBeneficiario))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPersonaBeneficiario() != null ? getIdPersonaBeneficiario().hashCode() : 0);
    return hash;
  }
}