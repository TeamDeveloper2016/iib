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
@Table(name="tc_mantic_acreedores_portales")
public class TcManticAcreedoresPortalesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="pagina")
  private String pagina;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_acreedor_portal")
  private Long idAcreedorPortal;
  @Column (name="cuenta")
  private String cuenta;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="contrasenia")
  private String contrasenia;
  @Column (name="id_acreedor")
  private Long idAcreedor;
  @Column (name="registro")
  private Timestamp registro;

  public TcManticAcreedoresPortalesDto() {
    this(new Long(-1L));
  }

  public TcManticAcreedoresPortalesDto(Long key) {
    this(null, null, new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcManticAcreedoresPortalesDto(String pagina, Long idUsuario, Long idAcreedorPortal, String cuenta, String observaciones, String contrasenia, Long idAcreedor) {
    setPagina(pagina);
    setIdUsuario(idUsuario);
    setIdAcreedorPortal(idAcreedorPortal);
    setCuenta(cuenta);
    setObservaciones(observaciones);
    setContrasenia(contrasenia);
    setIdAcreedor(idAcreedor);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setPagina(String pagina) {
    this.pagina = pagina;
  }

  public String getPagina() {
    return pagina;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdAcreedorPortal(Long idAcreedorPortal) {
    this.idAcreedorPortal = idAcreedorPortal;
  }

  public Long getIdAcreedorPortal() {
    return idAcreedorPortal;
  }

  public void setCuenta(String cuenta) {
    this.cuenta = cuenta;
  }

  public String getCuenta() {
    return cuenta;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setContrasenia(String contrasenia) {
    this.contrasenia = contrasenia;
  }

  public String getContrasenia() {
    return contrasenia;
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
  	return getIdAcreedorPortal();
  }

  @Override
  public void setKey(Long key) {
  	this.idAcreedorPortal = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getPagina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAcreedorPortal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCuenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getContrasenia());
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
		regresar.put("pagina", getPagina());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idAcreedorPortal", getIdAcreedorPortal());
		regresar.put("cuenta", getCuenta());
		regresar.put("observaciones", getObservaciones());
		regresar.put("contrasenia", getContrasenia());
		regresar.put("idAcreedor", getIdAcreedor());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getPagina(), getIdUsuario(), getIdAcreedorPortal(), getCuenta(), getObservaciones(), getContrasenia(), getIdAcreedor(), getRegistro()
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
    regresar.append("idAcreedorPortal~");
    regresar.append(getIdAcreedorPortal());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAcreedorPortal());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticAcreedoresPortalesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAcreedorPortal()!= null && getIdAcreedorPortal()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticAcreedoresPortalesDto other = (TcManticAcreedoresPortalesDto) obj;
    if (getIdAcreedorPortal() != other.idAcreedorPortal && (getIdAcreedorPortal() == null || !getIdAcreedorPortal().equals(other.idAcreedorPortal))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAcreedorPortal() != null ? getIdAcreedorPortal().hashCode() : 0);
    return hash;
  }

}


