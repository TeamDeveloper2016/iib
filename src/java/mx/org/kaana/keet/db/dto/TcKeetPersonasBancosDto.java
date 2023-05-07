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
@Table(name="tc_keet_personas_bancos")
public class TcKeetPersonasBancosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_empresa_persona")
  private Long idEmpresaPersona;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="cuenta")
  private String cuenta;
  @Column (name="id_principal")
  private Long idPrincipal;
  @Column (name="observaciones")
  private String observaciones;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_persona_banco")
  private Long idPersonaBanco;
  @Column (name="id_banco")
  private Long idBanco;
  @Column (name="clabe")
  private String clabe;
  @Column (name="registro")
  private Timestamp registro;

  public TcKeetPersonasBancosDto() {
    this(new Long(-1L));
  }

  public TcKeetPersonasBancosDto(Long key) {
    this(null, null, null, null, null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcKeetPersonasBancosDto(Long idEmpresaPersona, Long idUsuario, String cuenta, Long idPrincipal, String observaciones, Long idPersonaBanco, Long idBanco, String clabe) {
    setIdEmpresaPersona(idEmpresaPersona);
    setIdUsuario(idUsuario);
    setCuenta(cuenta);
    setIdPrincipal(idPrincipal);
    setObservaciones(observaciones);
    setIdPersonaBanco(idPersonaBanco);
    setIdBanco(idBanco);
    setClabe(clabe);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }

	public Long getIdEmpresaPersona() {
		return idEmpresaPersona;
	}

	public void setIdEmpresaPersona(Long idEmpresaPersona) {
		this.idEmpresaPersona=idEmpresaPersona;
	}
	
  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setCuenta(String cuenta) {
    this.cuenta = cuenta;
  }

  public String getCuenta() {
    return cuenta;
  }

  public void setIdPrincipal(Long idPrincipal) {
    this.idPrincipal = idPrincipal;
  }

  public Long getIdPrincipal() {
    return idPrincipal;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdPersonaBanco(Long idPersonaBanco) {
    this.idPersonaBanco = idPersonaBanco;
  }

  public Long getIdPersonaBanco() {
    return idPersonaBanco;
  }

  public void setIdBanco(Long idBanco) {
    this.idBanco = idBanco;
  }

  public Long getIdBanco() {
    return idBanco;
  }

  public void setClabe(String clabe) {
    this.clabe = clabe;
  }

  public String getClabe() {
    return clabe;
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
  	return getIdPersonaBanco();
  }

  @Override
  public void setKey(Long key) {
  	this.idPersonaBanco = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdEmpresaPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCuenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrincipal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPersonaBanco());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBanco());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClabe());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idPersona", getIdEmpresaPersona());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("cuenta", getCuenta());
		regresar.put("idPrincipal", getIdPrincipal());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idPersonaBanco", getIdPersonaBanco());
		regresar.put("idBanco", getIdBanco());
		regresar.put("clabe", getClabe());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getIdEmpresaPersona(), getIdUsuario(), getCuenta(), getIdPrincipal(), getObservaciones(), getIdPersonaBanco(), getIdBanco(), getClabe(), getRegistro()
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
    regresar.append("idPersonaBanco~");
    regresar.append(getIdPersonaBanco());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPersonaBanco());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPersonasBancosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPersonaBanco()!= null && getIdPersonaBanco()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) 
      return false;
    if (getClass() != obj.getClass()) 
      return false;
    final TcKeetPersonasBancosDto other = (TcKeetPersonasBancosDto) obj;
    if (getIdPersonaBanco() != other.idPersonaBanco && (getIdPersonaBanco() == null || !getIdPersonaBanco().equals(other.idPersonaBanco))) 
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPersonaBanco() != null ? getIdPersonaBanco().hashCode() : 0);
    return hash;
  }
  
}
