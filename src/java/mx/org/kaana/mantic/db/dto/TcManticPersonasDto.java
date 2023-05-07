package mx.org.kaana.mantic.db.dto;

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

@Entity
@Table(name = "tc_mantic_personas")
public class TcManticPersonasDto implements IBaseDto, Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
  @Column(name = "id_persona")
  private Long idPersona;
  @Column(name = "id_persona_titulo")
  private Long idPersonaTitulo;
  @Column(name = "id_tipo_persona")
  private Long idTipoPersona;
  @Column(name = "id_usuario")
  private Long idUsuario;
  @Column(name = "nombres")
  private String nombres;
  @Column(name = "paterno")
  private String paterno;
  @Column(name = "materno")
  private String materno;
  @Column(name = "curp")
  private String curp;
  @Column(name = "rfc")
  private String rfc;
  @Column(name = "id_tipo_sexo")
  private Long idTipoSexo;
  @Column(name = "contrasenia")
  private String contrasenia;
  @Column(name = "observaciones")
  private String observaciones;
  @Column(name = "cuenta")
  private String cuenta;
  @Column(name = "estilo")
  private String estilo;  
  @Column(name = "registro")
  private Timestamp registro;
  @Column(name = "fecha_nacimiento")
  private Date fechaNacimiento;
  @Column(name = "id_estado_civil")
  private Long idEstadoCivil;
  @Column(name = "id_lugar_nacimiento")
  private Long idLugarNacimiento;  
	@Column (name="apodo")
  private String apodo;
  

  public TcManticPersonasDto() {
    this(new Long(-1L));
  }

  public TcManticPersonasDto(Long key) {
    this(null, null, null, null, null, 1L, null, null, null, new Date(Calendar.getInstance().getTimeInMillis()), null);
    setKey(key);
  }

  public TcManticPersonasDto(Long idEmpleado, String nombres, String paterno, String materno, String curp, Long idTipoSexo,  String estilo, String cuenta, String contrasenia, Date fechaNacimiento, Long idUsuario) {
    this(idEmpleado, nombres, paterno, materno, curp, idTipoSexo, estilo, cuenta, contrasenia, fechaNacimiento, idUsuario, 1L, 1L, null);
	}
	
  public TcManticPersonasDto(Long idEmpleado, String nombres, String paterno, String materno, String curp, Long idTipoSexo,  String estilo, String cuenta, String contrasenia, Date fechaNacimiento, Long idUsuario, Long idEstadoCivil, Long idLugarNacimiento, String apodo) {
    setIdEmpleado(idEmpleado);
    setNombres(nombres);
		setPaterno(paterno);
		setMaterno(materno);    
    setIdTipoSexo(idTipoSexo);
    setCurp(curp);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setCuenta(cuenta);
    setContrasenia(contrasenia);   
    setEstilo(estilo);
		setFechaNacimiento(fechaNacimiento);
		setIdUsuario(idUsuario);
		this.idEstadoCivil= idEstadoCivil;
		this.idLugarNacimiento= idLugarNacimiento;
		this.apodo= apodo;
  }

  public Date getFechaNacimiento() {
    return fechaNacimiento;
  }

  public String getRfc() {
    return rfc;
  }

  public void setRfc(String rfc) {
    this.rfc = rfc;
  }
  
  public void setFechaNacimiento(Date fechaNacimiento) {
    this.fechaNacimiento = fechaNacimiento;
  }
  
  public String getContrasenia() {
    return contrasenia;
  }

  public void setContrasenia(String contrasenia) {
    this.contrasenia = contrasenia;
  }

  public String getCuenta() {
    return cuenta;
  }

  public void setCuenta(String cuenta) {
    this.cuenta = cuenta;
  }

  public String getEstilo() {
    return estilo;
  }

  public void setEstilo(String estilo) {
    this.estilo = estilo;
  }

  public Timestamp getRegistro() {
    return registro;
  }

  public void setRegistro(Timestamp registro) {
    this.registro = registro;
  }

  public Long getIdPersona() {
    return idPersona;
  }

  public void setIdEmpleado(Long idPersona) {
    this.idPersona = idPersona;
  }

  public String getNombres() {
    return nombres;
  }

  public void setNombres(String nombres) {
    this.nombres = nombres;
  }  

  public void setIdPersona(Long idPersona) {
    this.idPersona = idPersona;
  }

  public Long getIdPersonaTitulo() {
    return idPersonaTitulo;
  }

  public void setIdPersonaTitulo(Long idPersonaTitulo) {
    this.idPersonaTitulo = idPersonaTitulo;
  }

  public Long getIdTipoPersona() {
    return idTipoPersona;
  }

  public void setIdTipoPersona(Long idTipoPersona) {
    this.idTipoPersona = idTipoPersona;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdUsuario(Long idUSuario) {
    this.idUsuario = idUSuario;
  }

  public String getPaterno() {
    return paterno;
  }

  public void setPaterno(String paterno) {
    this.paterno = paterno;
  }

  public String getMaterno() {
    return materno;
  }

  public void setMaterno(String materno) {
    this.materno = materno;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }
 
  public String getCurp() {
    return curp;
  }

  public void setCurp(String curp) {
    this.curp = curp;
  }

  public Long getIdTipoSexo() {
    return idTipoSexo;
  }

  public void setIdTipoSexo(Long idTipoSexo) {
    this.idTipoSexo = idTipoSexo;
  }

	public Long getIdEstadoCivil() {
		return idEstadoCivil;
	}

	public void setIdEstadoCivil(Long idEstadoCivil) {
		this.idEstadoCivil=idEstadoCivil;
	}

  public Long getIdLugarNacimiento() {
    return idLugarNacimiento;
  }

  public void setIdLugarNacimiento(Long idLugarNacimiento) {
    this.idLugarNacimiento = idLugarNacimiento;
  }

  public String getApodo() {
    return apodo;
  }

  public void setApodo(String apodo) {
    this.apodo = apodo;
  }

  @Transient
  @Override
  public Long getKey() {
    return getIdPersona();
  }

  @Override
  public void setKey(Long key) {
    this.idPersona = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar = new StringBuilder();
    regresar.append("[");
    regresar.append(getIdPersona());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getNombres());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getPaterno());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getMaterno());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getRfc());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getCurp());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getIdTipoSexo());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getCuenta());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getContrasenia());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getEstilo());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getRegistro());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getFechaNacimiento());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getIdEstadoCivil());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getIdUsuario());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getIdLugarNacimiento());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getApodo());
    regresar.append("]");
    return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
    regresar.put("idPersona", getIdPersona());
    regresar.put("nombres", getNombres());
    regresar.put("paterno", getPaterno());
    regresar.put("materno", getMaterno());
    regresar.put("rfc", getRfc());
    regresar.put("curp", getCurp());
    regresar.put("idTipoSexo", getIdTipoSexo());
    regresar.put("cuenta", getCuenta());
    regresar.put("contrasenia", getContrasenia());   
    regresar.put("registro", getRegistro());
    regresar.put("estilo", getEstilo());
    regresar.put("fechaNacimiento", getFechaNacimiento());
    regresar.put("idEstadoCivil", getIdEstadoCivil());
    regresar.put("idUsuario", getIdUsuario());
    regresar.put("idLugarNacimiento", getIdLugarNacimiento());
    regresar.put("apodo", getApodo());
    return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getIdPersona(), getNombres(), getPaterno(), getMaterno(), getIdTipoSexo(), getCurp(), getCuenta(), getContrasenia(), getEstilo(), getFechaNacimiento(), getIdEstadoCivil(), getIdUsuario(), getIdLugarNacimiento(), getApodo()
		};
    return regresar;
  }

  @Override
  public Object toValue(String name) {
    return Methods.getValue(this, name);
  }

  @Override
  public String toAllKeys() {
    StringBuilder regresar = new StringBuilder();
    regresar.append("|");
    regresar.append("idPersona~");
    regresar.append(getIdPersona());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar = new StringBuilder();
    regresar.append(getIdPersona());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticPersonasDto.class;
  }

  @Override
  public boolean isValid() {
    return getIdPersona() != null && getIdPersona() != -1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticPersonasDto other = (TcManticPersonasDto) obj;
    if (getIdPersona()!= other.idPersona && (getIdPersona() == null || !getIdPersona().equals(other.idPersona))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPersona() != null ? getIdPersona().hashCode() : 0);
    return hash;
  }
  
}