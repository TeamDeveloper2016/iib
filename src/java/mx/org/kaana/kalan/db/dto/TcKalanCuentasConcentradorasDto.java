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
@Table(name="tc_kalan_cuentas_concentradoras")
public class TcKalanCuentasConcentradorasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_tipo_afectacion")
  private Long idTipoAfectacion;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_cuenta_concentradora")
  private Long idCuentaConcentradora;
  @Column (name="id_tipo_medio_pago")
  private Long idTipoMedioPago;
  @Column (name="importe")
  private Double importe;
  @Column (name="fecha_pago")
  private Date fechaPago;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="fecha_aplicacion")
  private Date fechaAplicacion;
  @Column (name="id_pivote")
  private Long idPivote;
  @Column (name="id_empresa_cuenta")
  private Long idEmpresaCuenta;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_cuenta_estatus")
  private Long idCuentaEstatus;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="orden")
  private Long orden;
  @Column (name="referencia")
  private String referencia;
  @Column (name="id_cuenta_origen")
  private Long idCuentaOrigen;

  public TcKalanCuentasConcentradorasDto() {
    this(new Long(-1L));
  }

  public TcKalanCuentasConcentradorasDto(Long key) {
    this(null, new Long(-1L), null, null, new Date(Calendar.getInstance().getTimeInMillis()), null, null, new Date(Calendar.getInstance().getTimeInMillis()), null, null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKalanCuentasConcentradorasDto(Long idTipoAfectacion, Long idCuentaConcentradora, Long idTipoMedioPago, Double importe, Date fechaPago, Long ejercicio, String consecutivo, Date fechaAplicacion, Long idPivote, Long idEmpresaCuenta, Long idUsuario, String observaciones, Long idCuentaEstatus, Long idEmpresa, Long orden, String referencia, Long idCuentaOrigen) {
    setIdTipoAfectacion(idTipoAfectacion);
    setIdCuentaConcentradora(idCuentaConcentradora);
    setIdTipoMedioPago(idTipoMedioPago);
    setImporte(importe);
    setFechaPago(fechaPago);
    setEjercicio(ejercicio);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setConsecutivo(consecutivo);
    setFechaAplicacion(fechaAplicacion);
    setIdPivote(idPivote);
    setIdEmpresaCuenta(idEmpresaCuenta);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setIdCuentaEstatus(idCuentaEstatus);
    setIdEmpresa(idEmpresa);
    setOrden(orden);
    setReferencia(referencia);
    setIdCuentaOrigen(idCuentaOrigen);
  }
	
  public void setIdTipoAfectacion(Long idTipoAfectacion) {
    this.idTipoAfectacion = idTipoAfectacion;
  }

  public Long getIdTipoAfectacion() {
    return idTipoAfectacion;
  }

  public void setIdCuentaConcentradora(Long idCuentaConcentradora) {
    this.idCuentaConcentradora = idCuentaConcentradora;
  }

  public Long getIdCuentaConcentradora() {
    return idCuentaConcentradora;
  }

  public void setIdTipoMedioPago(Long idTipoMedioPago) {
    this.idTipoMedioPago = idTipoMedioPago;
  }

  public Long getIdTipoMedioPago() {
    return idTipoMedioPago;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getImporte() {
    return importe;
  }

  public void setFechaPago(Date fechaPago) {
    this.fechaPago = fechaPago;
  }

  public Date getFechaPago() {
    return fechaPago;
  }

  public void setEjercicio(Long ejercicio) {
    this.ejercicio = ejercicio;
  }

  public Long getEjercicio() {
    return ejercicio;
  }

  public void setRegistro(Timestamp registro) {
    this.registro = registro;
  }

  public Timestamp getRegistro() {
    return registro;
  }

  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setFechaAplicacion(Date fechaAplicacion) {
    this.fechaAplicacion = fechaAplicacion;
  }

  public Date getFechaAplicacion() {
    return fechaAplicacion;
  }

  public void setIdPivote(Long idPivote) {
    this.idPivote = idPivote;
  }

  public Long getIdPivote() {
    return idPivote;
  }

  public void setIdEmpresaCuenta(Long idEmpresaCuenta) {
    this.idEmpresaCuenta = idEmpresaCuenta;
  }

  public Long getIdEmpresaCuenta() {
    return idEmpresaCuenta;
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

  public void setIdCuentaEstatus(Long idCuentaEstatus) {
    this.idCuentaEstatus = idCuentaEstatus;
  }

  public Long getIdCuentaEstatus() {
    return idCuentaEstatus;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setReferencia(String referencia) {
    this.referencia = referencia;
  }

  public String getReferencia() {
    return referencia;
  }

  public void setIdCuentaOrigen(Long idCuentaOrigen) {
    this.idCuentaOrigen = idCuentaOrigen;
  }

  public Long getIdCuentaOrigen() {
    return idCuentaOrigen;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdCuentaConcentradora();
  }

  @Override
  public void setKey(Long key) {
  	this.idCuentaConcentradora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdTipoAfectacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCuentaConcentradora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMedioPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaAplicacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPivote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaCuenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCuentaEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getReferencia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCuentaOrigen());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idTipoAfectacion", getIdTipoAfectacion());
		regresar.put("idCuentaConcentradora", getIdCuentaConcentradora());
		regresar.put("idTipoMedioPago", getIdTipoMedioPago());
		regresar.put("importe", getImporte());
		regresar.put("fechaPago", getFechaPago());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("fechaAplicacion", getFechaAplicacion());
		regresar.put("idPivote", getIdPivote());
		regresar.put("idEmpresaCuenta", getIdEmpresaCuenta());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idCuentaEstatus", getIdCuentaEstatus());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("orden", getOrden());
		regresar.put("referencia", getReferencia());
		regresar.put("idCuentaOrigen", getIdCuentaOrigen());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdTipoAfectacion(), getIdCuentaConcentradora(), getIdTipoMedioPago(), getImporte(), getFechaPago(), getEjercicio(), getRegistro(), getConsecutivo(), getFechaAplicacion(), getIdPivote(), getIdEmpresaCuenta(), getIdUsuario(), getObservaciones(), getIdCuentaEstatus(), getIdEmpresa(), getOrden(), getReferencia(), getIdCuentaOrigen()
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
    regresar.append("idCuentaConcentradora~");
    regresar.append(getIdCuentaConcentradora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdCuentaConcentradora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanCuentasConcentradorasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdCuentaConcentradora()!= null && getIdCuentaConcentradora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanCuentasConcentradorasDto other = (TcKalanCuentasConcentradorasDto) obj;
    if (getIdCuentaConcentradora() != other.idCuentaConcentradora && (getIdCuentaConcentradora() == null || !getIdCuentaConcentradora().equals(other.idCuentaConcentradora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdCuentaConcentradora() != null ? getIdCuentaConcentradora().hashCode() : 0);
    return hash;
  }

}


