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
@Table(name="tc_keet_deudores")
public class TcKeetDeudoresDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_deudor")
  private Long idDeudor;
  @Column (name="id_empresa_persona")
  private Long idEmpresaPersona;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="saldo")
  private Double saldo;
  @Column (name="limite")
  private Double limite;
  @Column (name="disponible")
  private Double disponible;
  @Column (name="registro")
  private Timestamp registro;

  public TcKeetDeudoresDto() {
    this(new Long(-1L));
  }

  public TcKeetDeudoresDto(Long key) {
    this(null, new Long(-1L), null, null, null, 0D, null);
    setKey(key);
  }

  public TcKeetDeudoresDto(Long idUsuario, Long idDeudor, Long idEmpresaPersona, String observaciones, Double saldo, Double limite, Double disponible) {
    setIdUsuario(idUsuario);
    setIdDeudor(idDeudor);
    setIdEmpresaPersona(idEmpresaPersona);
    setObservaciones(observaciones);
    setSaldo(saldo);
    setLimite(limite);
    setDisponible(disponible);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdDeudor(Long idDeudor) {
    this.idDeudor = idDeudor;
  }

  public Long getIdDeudor() {
    return idDeudor;
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

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setLimite(Double limite) {
    this.limite = limite;
  }

  public Double getLimite() {
    return limite;
  }

  public void setDisponible(Double disponible) {
    this.disponible = disponible;
  }

  public Double getDisponible() {
    return disponible;
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
  	return getIdDeudor();
  }

  @Override
  public void setKey(Long key) {
  	this.idDeudor = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDeudor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSaldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLimite());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDisponible());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idDeudor", getIdDeudor());
		regresar.put("idEmpresaPersona", getIdEmpresaPersona());
		regresar.put("observaciones", getObservaciones());
		regresar.put("saldo", getSaldo());
		regresar.put("limite", getLimite());
		regresar.put("disponible", getDisponible());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdUsuario(), getIdDeudor(), getIdEmpresaPersona(), getObservaciones(), getSaldo(), getLimite(), getDisponible(), getRegistro()
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
    regresar.append("idDeudor~");
    regresar.append(getIdDeudor());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdDeudor());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetDeudoresDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdDeudor()!= null && getIdDeudor()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetDeudoresDto other = (TcKeetDeudoresDto) obj;
    if (getIdDeudor() != other.idDeudor && (getIdDeudor() == null || !getIdDeudor().equals(other.idDeudor))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdDeudor() != null ? getIdDeudor().hashCode() : 0);
    return hash;
  }
  
}