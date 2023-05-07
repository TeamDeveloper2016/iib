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
@Table(name="tc_keet_contratistas_departamentos")
public class TcKeetContratistasDepartamentosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_departamento")
  private Long idDepartamento;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_empresa_persona")
  private Long idEmpresaPersona;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contratista_departamento")
  private Long idContratistaDepartamento;
  @Column (name="registro")
  private Timestamp registro;

  public TcKeetContratistasDepartamentosDto() {
    this(new Long(-1L));
  }

  public TcKeetContratistasDepartamentosDto(Long key) {
    this(null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetContratistasDepartamentosDto(Long idDepartamento, Long idUsuario, Long idEmpresaPersona, Long idContratistaDepartamento) {
    setIdDepartamento(idDepartamento);
    setIdUsuario(idUsuario);
    setIdEmpresaPersona(idEmpresaPersona);
    setIdContratistaDepartamento(idContratistaDepartamento);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
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

  public void setIdEmpresaPersona(Long idEmpresaPersona) {
    this.idEmpresaPersona = idEmpresaPersona;
  }

  public Long getIdEmpresaPersona() {
    return idEmpresaPersona;
  }

  public void setIdContratistaDepartamento(Long idContratistaDepartamento) {
    this.idContratistaDepartamento = idContratistaDepartamento;
  }

  public Long getIdContratistaDepartamento() {
    return idContratistaDepartamento;
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
  	return getIdContratistaDepartamento();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratistaDepartamento = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdDepartamento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratistaDepartamento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idDepartamento", getIdDepartamento());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idEmpresaPersona", getIdEmpresaPersona());
		regresar.put("idContratistaDepartamento", getIdContratistaDepartamento());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdDepartamento(), getIdUsuario(), getIdEmpresaPersona(), getIdContratistaDepartamento(), getRegistro()
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
    regresar.append("idContratistaDepartamento~");
    regresar.append(getIdContratistaDepartamento());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratistaDepartamento());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratistasDepartamentosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratistaDepartamento()!= null && getIdContratistaDepartamento()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratistasDepartamentosDto other = (TcKeetContratistasDepartamentosDto) obj;
    if (getIdContratistaDepartamento() != other.idContratistaDepartamento && (getIdContratistaDepartamento() == null || !getIdContratistaDepartamento().equals(other.idContratistaDepartamento))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratistaDepartamento() != null ? getIdContratistaDepartamento().hashCode() : 0);
    return hash;
  }

}


