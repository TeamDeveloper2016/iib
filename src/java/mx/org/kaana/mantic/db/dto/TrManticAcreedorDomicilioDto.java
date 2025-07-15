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
@Table(name="tr_mantic_acreedor_domicilio")
public class TrManticAcreedorDomicilioDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_acreedor_domicilio")
  private Long idAcreedorDomicilio;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_tipo_domicilio")
  private Long idTipoDomicilio;
  @Column (name="id_domicilio")
  private Long idDomicilio;
  @Column (name="id_principal")
  private Long idPrincipal;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_acreedor")
  private Long idAcreedor;
  @Column (name="registro")
  private Timestamp registro;

  public TrManticAcreedorDomicilioDto() {
    this(new Long(-1L));
  }

  public TrManticAcreedorDomicilioDto(Long key) {
    this(new Long(-1L), null, null, null, null, null, null);
    setKey(key);
  }

  public TrManticAcreedorDomicilioDto(Long idAcreedorDomicilio, Long idAcreedor, Long idUsuario, Long idTipoDomicilio, Long idDomicilio, Long idPrincipal, String observaciones) {
    setIdAcreedorDomicilio(idAcreedorDomicilio);
    setIdUsuario(idUsuario);
    setIdTipoDomicilio(idTipoDomicilio);
    setIdDomicilio(idDomicilio);
    setIdPrincipal(idPrincipal);
    setObservaciones(observaciones);
    setIdAcreedor(idAcreedor);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdAcreedorDomicilio(Long idAcreedorDomicilio) {
    this.idAcreedorDomicilio = idAcreedorDomicilio;
  }

  public Long getIdAcreedorDomicilio() {
    return idAcreedorDomicilio;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdTipoDomicilio(Long idTipoDomicilio) {
    this.idTipoDomicilio = idTipoDomicilio;
  }

  public Long getIdTipoDomicilio() {
    return idTipoDomicilio;
  }

  public void setIdDomicilio(Long idDomicilio) {
    this.idDomicilio = idDomicilio;
  }

  public Long getIdDomicilio() {
    return idDomicilio;
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
  	return getIdAcreedorDomicilio();
  }

  @Override
  public void setKey(Long key) {
  	this.idAcreedorDomicilio = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdAcreedorDomicilio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoDomicilio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDomicilio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrincipal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
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
		regresar.put("idAcreedorDomicilio", getIdAcreedorDomicilio());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoDomicilio", getIdTipoDomicilio());
		regresar.put("idDomicilio", getIdDomicilio());
		regresar.put("idPrincipal", getIdPrincipal());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idAcreedor", getIdAcreedor());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdAcreedorDomicilio(), getIdUsuario(), getIdTipoDomicilio(), getIdDomicilio(), getIdPrincipal(), getObservaciones(), getIdAcreedor(), getRegistro()
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
    regresar.append("idAcreedorDomicilio~");
    regresar.append(getIdAcreedorDomicilio());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAcreedorDomicilio());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TrManticAcreedorDomicilioDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAcreedorDomicilio()!= null && getIdAcreedorDomicilio()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TrManticAcreedorDomicilioDto other = (TrManticAcreedorDomicilioDto) obj;
    if (getIdAcreedorDomicilio() != other.idAcreedorDomicilio && (getIdAcreedorDomicilio() == null || !getIdAcreedorDomicilio().equals(other.idAcreedorDomicilio))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAcreedorDomicilio() != null ? getIdAcreedorDomicilio().hashCode() : 0);
    return hash;
  }

}


