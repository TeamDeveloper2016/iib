package mx.org.kaana.keet.db.dto;

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
@Table(name="tc_keet_contratos_personal")
public class TcKeetContratosPersonalDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_persona")
  private Long idContratoPersona;
  @Column (name="id_usuario")
  private Long idUsuario;  
  @Column (name="id_empresa_persona")
  private Long idEmpresaPersona;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_vigente")
  private Long idVigente;
  @Column (name="registro")
  private Timestamp registro;

  public TcKeetContratosPersonalDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosPersonalDto(Long key) {
    this(new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKeetContratosPersonalDto(Long idContratoPersona, Long idUsuario, Long idEmpresaPersona, String observaciones, Long idVigente) {
    setIdContratoPersona(idContratoPersona);
    setIdUsuario(idUsuario);    
    setIdEmpresaPersona(idEmpresaPersona);
    setObservaciones(observaciones);
    setIdVigente(idVigente);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdContratoPersona(Long idContratoPersona) {
    this.idContratoPersona = idContratoPersona;
  }

  public Long getIdContratoPersona() {
    return idContratoPersona;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
	}

  public void setIdEmpresaPersona(Long idEmpresaPersona) {
    this.idEmpresaPersona = idEmpresaPersona;
  }

  public Long getIdEmpresaPersona() {
    return idEmpresaPersona;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdVigente(Long idVigente) {
    this.idVigente = idVigente;
  }

  public Long getIdVigente() {
    return idVigente;
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
  	return getIdContratoPersona();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoPersona = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdContratoPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);		
		regresar.append(getIdEmpresaPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdVigente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idContratoPersona", getIdContratoPersona());
		regresar.put("idUsuario", getIdUsuario());		
		regresar.put("idEmpresaPersona", getIdEmpresaPersona());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idVigente", getIdVigente());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
			getIdContratoPersona(), getIdUsuario(), getIdEmpresaPersona(), getObservaciones(), getIdVigente(), getRegistro()
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
    regresar.append("idContratoPersona~");
    regresar.append(getIdContratoPersona());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoPersona());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosPersonalDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoPersona()!= null && getIdContratoPersona()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosPersonalDto other = (TcKeetContratosPersonalDto) obj;
    if (getIdContratoPersona() != other.idContratoPersona && (getIdContratoPersona() == null || !getIdContratoPersona().equals(other.idContratoPersona))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoPersona() != null ? getIdContratoPersona().hashCode() : 0);
    return hash;
  }
}