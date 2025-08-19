package mx.org.kaana.kalan.db.dto;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
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
@Table(name="tc_kalan_cuentas_movimientos")
public class TcKalanCuentasMovimientosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_tipo_afectacion")
  private Long idTipoAfectacion;
  @Column (name="id_tipo_medio_pago")
  private Long idTipoMedioPago;
  @Column (name="importe")
  private Double importe;
  @Column (name="id_banco")
  private Long idBanco;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="fecha_pago")
  private Date fechaPago;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="fecha_aplicacion")
  private Date fechaAplicacion;
  @Column (name="id_empresa_cuenta")
  private Long idEmpresaCuenta;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_empresa_destino")
  private Long idEmpresaDestino;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="orden")
  private Long orden;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
  @Column (name="id_cuenta_movimiento")
  private Long idCuentaMovimiento;
  @Column (name="referencia")
  private String referencia;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="id_cuenta_estatus")
  private Long idCuentaEstatus;

  public TcKalanCuentasMovimientosDto() {
    this(new Long(-1L));
  }

  public TcKalanCuentasMovimientosDto(Long key) {
    this(2L, null, 0D, null, null, new Date(Calendar.getInstance().getTimeInMillis()), null, new Date(Calendar.getInstance().getTimeInMillis()), null, null, null, null, null, null, null, null, 1L);
    setKey(key);
  }

  public TcKalanCuentasMovimientosDto(Long idTipoAfectacion, Long idTipoMedioPago, Double importe, Long idBanco, Long ejercicio, Date fechaPago, String consecutivo, Date fechaAplicacion, Long idEmpresaCuenta, Long idUsuario, Long idEmpresaDestino, String observaciones, Long orden, Long idCuentaMovimiento, String referencia, Long idEmpresa, Long idCuentaEstatus) {
    setIdTipoAfectacion(idTipoAfectacion);
    setIdTipoMedioPago(idTipoMedioPago);
    setImporte(importe);
    setIdBanco(idBanco);
    setEjercicio(ejercicio);
    setFechaPago(fechaPago);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setConsecutivo(consecutivo);
    setFechaAplicacion(fechaAplicacion);
    setIdEmpresaCuenta(idEmpresaCuenta);
    setIdUsuario(idUsuario);
    setIdEmpresaDestino(idEmpresaDestino);
    setObservaciones(observaciones);
    setOrden(orden);
    setIdCuentaMovimiento(idCuentaMovimiento);
    setReferencia(referencia);
    setIdEmpresa(idEmpresa);
    setIdCuentaEstatus(idCuentaEstatus);
  }
	
  public void setIdTipoAfectacion(Long idTipoAfectacion) {
    this.idTipoAfectacion = idTipoAfectacion;
  }

  public Long getIdTipoAfectacion() {
    return idTipoAfectacion;
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

  public void setIdBanco(Long idBanco) {
    this.idBanco = idBanco;
  }

  public Long getIdBanco() {
    return idBanco;
  }

  public void setEjercicio(Long ejercicio) {
    this.ejercicio = ejercicio;
  }

  public Long getEjercicio() {
    return ejercicio;
  }

  public void setFechaPago(Date fechaPago) {
    this.fechaPago = fechaPago;
  }

  public Date getFechaPago() {
    return fechaPago;
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

  public void setIdEmpresaDestino(Long idEmpresaDestino) {
    this.idEmpresaDestino = idEmpresaDestino;
  }

  public Long getIdEmpresaDestino() {
    return idEmpresaDestino;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setIdCuentaMovimiento(Long idCuentaMovimiento) {
    this.idCuentaMovimiento = idCuentaMovimiento;
  }

  public Long getIdCuentaMovimiento() {
    return idCuentaMovimiento;
  }

  public void setReferencia(String referencia) {
    this.referencia = referencia;
  }

  public String getReferencia() {
    return referencia;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }

  public Long getIdCuentaEstatus() {
    return idCuentaEstatus;
  }

  public void setIdCuentaEstatus(Long idCuentaEstatus) {
    this.idCuentaEstatus = idCuentaEstatus;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdCuentaMovimiento();
  }

  @Override
  public void setKey(Long key) {
  	this.idCuentaMovimiento = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdTipoAfectacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMedioPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBanco());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaAplicacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaCuenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaDestino());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCuentaMovimiento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getReferencia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCuentaEstatus());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idTipoAfectacion", getIdTipoAfectacion());
		regresar.put("idTipoMedioPago", getIdTipoMedioPago());
		regresar.put("importe", getImporte());
		regresar.put("idBanco", getIdBanco());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("fechaPago", getFechaPago());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("fechaAplicacion", getFechaAplicacion());
		regresar.put("idEmpresaCuenta", getIdEmpresaCuenta());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idEmpresaDestino", getIdEmpresaDestino());
		regresar.put("observaciones", getObservaciones());
		regresar.put("orden", getOrden());
		regresar.put("idCuentaMovimiento", getIdCuentaMovimiento());
		regresar.put("referencia", getReferencia());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("idCuentaEstatus", getIdCuentaEstatus());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getIdTipoAfectacion(), getIdTipoMedioPago(), getImporte(), getIdBanco(), getEjercicio(), getFechaPago(), getRegistro(), getConsecutivo(), getFechaAplicacion(), getIdEmpresaCuenta(), getIdUsuario(), getIdEmpresaDestino(), getObservaciones(), getOrden(), getIdCuentaMovimiento(), getReferencia(), getIdEmpresa(), getIdCuentaEstatus()
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
    regresar.append("idCuentaMovimiento~");
    regresar.append(getIdCuentaMovimiento());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdCuentaMovimiento());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanCuentasMovimientosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdCuentaMovimiento()!= null && getIdCuentaMovimiento()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanCuentasMovimientosDto other = (TcKalanCuentasMovimientosDto) obj;
    if (getIdCuentaMovimiento() != other.idCuentaMovimiento && (getIdCuentaMovimiento() == null || !getIdCuentaMovimiento().equals(other.idCuentaMovimiento))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdCuentaMovimiento() != null ? getIdCuentaMovimiento().hashCode() : 0);
    return hash;
  }

}


