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
@Table(name="tc_mantic_acreedores_bancos")
public class TcManticAcreedoresBancosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="convenio_cuenta")
  private String convenioCuenta;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_tipo_cuenta")
  private Long idTipoCuenta;
  @Column (name="clabe_referencia")
  private String clabeReferencia;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_acreedor_banco")
  private Long idAcreedorBanco;
  @Column (name="id_banco")
  private Long idBanco;
  @Column (name="id_acreedor")
  private Long idAcreedor;
  @Column (name="registro")
  private Timestamp registro;

  public TcManticAcreedoresBancosDto() {
    this(new Long(-1L));
  }

  public TcManticAcreedoresBancosDto(Long key) {
    this(null, null, null, null, null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcManticAcreedoresBancosDto(String convenioCuenta, Long idUsuario, String observaciones, Long idTipoCuenta, String clabeReferencia, Long idAcreedorBanco, Long idBanco, Long idAcreedor) {
    setConvenioCuenta(convenioCuenta);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setIdTipoCuenta(idTipoCuenta);
    setClabeReferencia(clabeReferencia);
    setIdAcreedorBanco(idAcreedorBanco);
    setIdBanco(idBanco);
    setIdAcreedor(idAcreedor);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setConvenioCuenta(String convenioCuenta) {
    this.convenioCuenta = convenioCuenta;
  }

  public String getConvenioCuenta() {
    return convenioCuenta;
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

  public void setIdTipoCuenta(Long idTipoCuenta) {
    this.idTipoCuenta = idTipoCuenta;
  }

  public Long getIdTipoCuenta() {
    return idTipoCuenta;
  }

  public void setClabeReferencia(String clabeReferencia) {
    this.clabeReferencia = clabeReferencia;
  }

  public String getClabeReferencia() {
    return clabeReferencia;
  }

  public void setIdAcreedorBanco(Long idAcreedorBanco) {
    this.idAcreedorBanco = idAcreedorBanco;
  }

  public Long getIdAcreedorBanco() {
    return idAcreedorBanco;
  }

  public void setIdBanco(Long idBanco) {
    this.idBanco = idBanco;
  }

  public Long getIdBanco() {
    return idBanco;
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
  	return getIdAcreedorBanco();
  }

  @Override
  public void setKey(Long key) {
  	this.idAcreedorBanco = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getConvenioCuenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoCuenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClabeReferencia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAcreedorBanco());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBanco());
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
		regresar.put("convenioCuenta", getConvenioCuenta());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idTipoCuenta", getIdTipoCuenta());
		regresar.put("clabeReferencia", getClabeReferencia());
		regresar.put("idAcreedorBanco", getIdAcreedorBanco());
		regresar.put("idBanco", getIdBanco());
		regresar.put("idAcreedor", getIdAcreedor());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getConvenioCuenta(), getIdUsuario(), getObservaciones(), getIdTipoCuenta(), getClabeReferencia(), getIdAcreedorBanco(), getIdBanco(), getIdAcreedor(), getRegistro()
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
    regresar.append("idAcreedorBanco~");
    regresar.append(getIdAcreedorBanco());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAcreedorBanco());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticAcreedoresBancosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAcreedorBanco()!= null && getIdAcreedorBanco()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticAcreedoresBancosDto other = (TcManticAcreedoresBancosDto) obj;
    if (getIdAcreedorBanco() != other.idAcreedorBanco && (getIdAcreedorBanco() == null || !getIdAcreedorBanco().equals(other.idAcreedorBanco))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAcreedorBanco() != null ? getIdAcreedorBanco().hashCode() : 0);
    return hash;
  }

}


