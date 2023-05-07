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
@Table(name="tc_keet_nominas_desarrollos")
public class TcKeetNominasDesarrollosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="id_empresa_persona")
  private Long idEmpresaPersona;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nomina_desarrollo")
  private Long idNominaDesarrollo;
  @Column (name="id_nomina")
  private Long idNomina;
  @Column (name="registro")
  private Timestamp registro;

  public TcKeetNominasDesarrollosDto() {
    this(new Long(-1L));
  }

  public TcKeetNominasDesarrollosDto(Long key) {
    this(null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetNominasDesarrollosDto(Long idDesarrollo, Long idEmpresaPersona, Long idNominaDesarrollo, Long idNomina) {
    setIdDesarrollo(idDesarrollo);
    setIdEmpresaPersona(idEmpresaPersona);
    setIdNominaDesarrollo(idNominaDesarrollo);
    setIdNomina(idNomina);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdEmpresaPersona(Long idEmpresaPersona) {
    this.idEmpresaPersona = idEmpresaPersona;
  }

  public Long getIdEmpresaPersona() {
    return idEmpresaPersona;
  }

  public void setIdNominaDesarrollo(Long idNominaDesarrollo) {
    this.idNominaDesarrollo = idNominaDesarrollo;
  }

  public Long getIdNominaDesarrollo() {
    return idNominaDesarrollo;
  }

  public void setIdNomina(Long idNomina) {
    this.idNomina = idNomina;
  }

  public Long getIdNomina() {
    return idNomina;
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
  	return getIdNominaDesarrollo();
  }

  @Override
  public void setKey(Long key) {
  	this.idNominaDesarrollo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("idEmpresaPersona", getIdEmpresaPersona());
		regresar.put("idNominaDesarrollo", getIdNominaDesarrollo());
		regresar.put("idNomina", getIdNomina());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdDesarrollo(), getIdEmpresaPersona(), getIdNominaDesarrollo(), getIdNomina(), getRegistro()
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
    regresar.append("idNominaDesarrollo~");
    regresar.append(getIdNominaDesarrollo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNominaDesarrollo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNominasDesarrollosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNominaDesarrollo()!= null && getIdNominaDesarrollo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNominasDesarrollosDto other = (TcKeetNominasDesarrollosDto) obj;
    if (getIdNominaDesarrollo() != other.idNominaDesarrollo && (getIdNominaDesarrollo() == null || !getIdNominaDesarrollo().equals(other.idNominaDesarrollo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNominaDesarrollo() != null ? getIdNominaDesarrollo().hashCode() : 0);
    return hash;
  }

}


