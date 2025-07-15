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
@Table(name="tr_mantic_acreedor_agente")
public class TrManticAcreedorAgenteDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_acreedor_agente")
  private Long idAcreedorAgente;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_principal")
  private Long idPrincipal;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_agente")
  private Long idAgente;
  @Column (name="id_acreedor")
  private Long idAcreedor;
  @Column (name="registro")
  private Timestamp registro;

  public TrManticAcreedorAgenteDto() {
    this(new Long(-1L));
  }

  public TrManticAcreedorAgenteDto(Long key) {
    this(new Long(-1L), null, null, null, null, null);
    setKey(key);
  }

  public TrManticAcreedorAgenteDto(Long idAcreedorAgente, Long idUsuario, Long idPrincipal, String observaciones, Long idAgente, Long idAcreedor) {
    setIdAcreedorAgente(idAcreedorAgente);
    setIdUsuario(idUsuario);
    setIdPrincipal(idPrincipal);
    setObservaciones(observaciones);
    setIdAgente(idAgente);
    setIdAcreedor(idAcreedor);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdAcreedorAgente(Long idAcreedorAgente) {
    this.idAcreedorAgente = idAcreedorAgente;
  }

  public Long getIdAcreedorAgente() {
    return idAcreedorAgente;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
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

  public void setIdAgente(Long idAgente) {
    this.idAgente = idAgente;
  }

  public Long getIdAgente() {
    return idAgente;
  }

  public void setIdAcreedor(Long idAcreedor) {
    this.idAcreedor = idAcreedor;
  }

  public Long getIdAcreedor() {
    return idAcreedor;
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
  	return getIdAcreedorAgente();
  }

  @Override
  public void setKey(Long key) {
  	this.idAcreedorAgente = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdAcreedorAgente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrincipal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAgente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAcreedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idAcreedorAgente", getIdAcreedorAgente());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idPrincipal", getIdPrincipal());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idAgente", getIdAgente());
		regresar.put("idAcreedor", getIdAcreedor());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdAcreedorAgente(), getIdUsuario(), getIdPrincipal(), getObservaciones(), getIdAgente(), getIdAcreedor(), getRegistro()
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
    regresar.append("idAcreedorAgente~");
    regresar.append(getIdAcreedorAgente());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAcreedorAgente());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TrManticAcreedorAgenteDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAcreedorAgente()!= null && getIdAcreedorAgente()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TrManticAcreedorAgenteDto other = (TrManticAcreedorAgenteDto) obj;
    if (getIdAcreedorAgente() != other.idAcreedorAgente && (getIdAcreedorAgente() == null || !getIdAcreedorAgente().equals(other.idAcreedorAgente))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAcreedorAgente() != null ? getIdAcreedorAgente().hashCode() : 0);
    return hash;
  }

}


