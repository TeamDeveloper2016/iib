package mx.org.kaana.kalan.db.dto;

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
@Table(name="tc_kalan_empresas_cheques")
public class TcKalanEmpresasChequesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="fecha")
  private Date fecha;
  @Column (name="id_proveedor")
  private Long idProveedor;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_empresa_cheque")
  private Long idEmpresaCheque;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_activo_proveedor")
  private Long idActivoProveedor;
  @Column (name="folio")
  private String folio;
  @Column (name="concepto")
  private String concepto;
  @Column (name="id_empresa_gasto")
  private Long idEmpresaGasto;
  @Column (name="beneficiario")
  private String beneficiario;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanEmpresasChequesDto() {
    this(new Long(-1L));
  }

  public TcKalanEmpresasChequesDto(Long key) {
    this(new Date(Calendar.getInstance().getTimeInMillis()), -1L, new Long(-1L), null, 2L, null, null, -1L, null);
    setKey(key);
  }

  public TcKalanEmpresasChequesDto(Date fecha, Long idProveedor, Long idEmpresaCheque, Long idUsuario, Long idActivoProveedor, String folio, String concepto, Long idEmpresaGasto, String beneficiario) {
    setFecha(fecha);
    setIdProveedor(idProveedor);
    setIdEmpresaCheque(idEmpresaCheque);
    setIdUsuario(idUsuario);
    setIdActivoProveedor(idActivoProveedor);
    setFolio(folio);
    setConcepto(concepto);
    setIdEmpresaGasto(idEmpresaGasto);
    setBeneficiario(beneficiario);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setFecha(Date fecha) {
    this.fecha = fecha;
  }

  public Date getFecha() {
    return fecha;
  }

  public void setIdProveedor(Long idProveedor) {
    this.idProveedor = idProveedor;
  }

  public Long getIdProveedor() {
    return idProveedor;
  }

  public void setIdEmpresaCheque(Long idEmpresaCheque) {
    this.idEmpresaCheque = idEmpresaCheque;
  }

  public Long getIdEmpresaCheque() {
    return idEmpresaCheque;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdActivoProveedor(Long idActivoProveedor) {
    this.idActivoProveedor = idActivoProveedor;
  }

  public Long getIdActivoProveedor() {
    return idActivoProveedor;
  }

  public void setFolio(String folio) {
    this.folio = folio;
  }

  public String getFolio() {
    return folio;
  }

  public void setConcepto(String concepto) {
    this.concepto = concepto;
  }

  public String getConcepto() {
    return concepto;
  }

  public void setIdEmpresaGasto(Long idEmpresaGasto) {
    this.idEmpresaGasto = idEmpresaGasto;
  }

  public Long getIdEmpresaGasto() {
    return idEmpresaGasto;
  }

  public void setBeneficiario(String beneficiario) {
    this.beneficiario = beneficiario;
  }

  public String getBeneficiario() {
    return beneficiario;
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
  	return getIdEmpresaCheque();
  }

  @Override
  public void setKey(Long key) {
  	this.idEmpresaCheque = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getFecha());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaCheque());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdActivoProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFolio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConcepto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaGasto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getBeneficiario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("fecha", getFecha());
		regresar.put("idProveedor", getIdProveedor());
		regresar.put("idEmpresaCheque", getIdEmpresaCheque());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idActivoProveedor", getIdActivoProveedor());
		regresar.put("folio", getFolio());
		regresar.put("concepto", getConcepto());
		regresar.put("idEmpresaGasto", getIdEmpresaGasto());
		regresar.put("beneficiario", getBeneficiario());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getFecha(), getIdProveedor(), getIdEmpresaCheque(), getIdUsuario(), getIdActivoProveedor(), getFolio(), getConcepto(), getIdEmpresaGasto(), getBeneficiario(), getRegistro()
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
    regresar.append("idEmpresaCheque~");
    regresar.append(getIdEmpresaCheque());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdEmpresaCheque());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanEmpresasChequesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdEmpresaCheque()!= null && getIdEmpresaCheque()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanEmpresasChequesDto other = (TcKalanEmpresasChequesDto) obj;
    if (getIdEmpresaCheque() != other.idEmpresaCheque && (getIdEmpresaCheque() == null || !getIdEmpresaCheque().equals(other.idEmpresaCheque))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdEmpresaCheque() != null ? getIdEmpresaCheque().hashCode() : 0);
    return hash;
  }

}


