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
@Table(name="tc_mantic_acreedores")
public class TcManticAcreedoresDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="clave")
  private String clave;
  @Column (name="prefijo")
  private String prefijo;
  @Column (name="razon_social")
  private String razonSocial;
  @Column (name="rfc")
  private String rfc;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="id_activo")
  private Long idActivo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="logotipo")
  private String logotipo;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="comentarios")
  private String comentarios;
  @Column (name="id_tipo_acreedor")
  private Long idTipoAcreedor;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_acreedor")
  private Long idAcreedor;

  public TcManticAcreedoresDto() {
    this(new Long(-1L));
  }

  public TcManticAcreedoresDto(Long key) {
    this(null, null, null, null, null, null, null, null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcManticAcreedoresDto(String clave, String prefijo, String razonSocial, String rfc, Long idActivo, Long idUsuario, String logotipo, String observaciones, Long idEmpresa, String comentarios, Long idTipoAcreedor, Long idAcreedor) {
    setClave(clave);
    setPrefijo(prefijo);
    setRazonSocial(razonSocial);
    setRfc(rfc);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setIdActivo(idActivo);
    setIdUsuario(idUsuario);
    setLogotipo(logotipo);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setComentarios(comentarios);
    setIdTipoAcreedor(idTipoAcreedor);
    setIdAcreedor(idAcreedor);
  }
	
  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setPrefijo(String prefijo) {
    this.prefijo = prefijo;
  }

  public String getPrefijo() {
    return prefijo;
  }

  public void setRazonSocial(String razonSocial) {
    this.razonSocial = razonSocial;
  }

  public String getRazonSocial() {
    return razonSocial;
  }

  public void setRfc(String rfc) {
    this.rfc = rfc;
  }

  public String getRfc() {
    return rfc;
  }

  public void setRegistro(Timestamp registro) {
    this.registro = registro;
  }

  public Timestamp getRegistro() {
    return registro;
  }

  public void setIdActivo(Long idActivo) {
    this.idActivo = idActivo;
  }

  public Long getIdActivo() {
    return idActivo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setLogotipo(String logotipo) {
    this.logotipo = logotipo;
  }

  public String getLogotipo() {
    return logotipo;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

  public void setComentarios(String comentarios) {
    this.comentarios = comentarios;
  }

  public String getComentarios() {
    return comentarios;
  }

  public void setIdTipoAcreedor(Long idTipoAcreedor) {
    this.idTipoAcreedor = idTipoAcreedor;
  }

  public Long getIdTipoAcreedor() {
    return idTipoAcreedor;
  }

  public void setIdAcreedor(Long idAcreedor) {
    this.idAcreedor = idAcreedor;
  }

  public Long getIdAcreedor() {
    return idAcreedor;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdAcreedor();
  }

  @Override
  public void setKey(Long key) {
  	this.idAcreedor = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPrefijo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRazonSocial());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRfc());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdActivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLogotipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getComentarios());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoAcreedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAcreedor());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("clave", getClave());
		regresar.put("prefijo", getPrefijo());
		regresar.put("razonSocial", getRazonSocial());
		regresar.put("rfc", getRfc());
		regresar.put("registro", getRegistro());
		regresar.put("idActivo", getIdActivo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("logotipo", getLogotipo());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("comentarios", getComentarios());
		regresar.put("idTipoAcreedor", getIdTipoAcreedor());
		regresar.put("idAcreedor", getIdAcreedor());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getClave(), getPrefijo(), getRazonSocial(), getRfc(), getRegistro(), getIdActivo(), getIdUsuario(), getLogotipo(), getObservaciones(), getIdEmpresa(), getComentarios(), getIdTipoAcreedor(), getIdAcreedor()
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
    regresar.append("idAcreedor~");
    regresar.append(getIdAcreedor());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAcreedor());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticAcreedoresDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAcreedor()!= null && getIdAcreedor()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticAcreedoresDto other = (TcManticAcreedoresDto) obj;
    if (getIdAcreedor() != other.idAcreedor && (getIdAcreedor() == null || !getIdAcreedor().equals(other.idAcreedor))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAcreedor() != null ? getIdAcreedor().hashCode() : 0);
    return hash;
  }

}


