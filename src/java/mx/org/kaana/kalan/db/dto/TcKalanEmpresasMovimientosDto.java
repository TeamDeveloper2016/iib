package mx.org.kaana.kalan.db.dto;

import java.io.Serializable;
import java.sql.Date;
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
@Table(name="tc_kalan_empresas_movimientos")
public class TcKalanEmpresasMovimientosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_anticipo")
  private Long idAnticipo;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_cliente")
  private Long idCliente;
  @Column (name="id_movimiento_estatus")
  private Long idMovimientoEstatus;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_empresa_movimiento")
  private Long idEmpresaMovimiento;
  @Column (name="id_banco")
  private Long idBanco;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="id_tipo_concepto")
  private Long idTipoConcepto;
  @Column (name="fecha_aplicacion")
  private Date fechaAplicacion;
  @Column (name="total")
  private Double total;
  @Column (name="id_tipo_movimiento")
  private Long idTipoMovimiento;
  @Column (name="id_empresa_cuenta")
  private Long idEmpresaCuenta;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="orden")
  private Long orden;
  @Column (name="concepto")
  private String concepto;
  @Column (name="fecha_pago")
  private Date fechaPago;
  @Column (name="id_tipo_medio_pago")
  private Long idTipoMedioPago;
  @Column (name="referencia")
  private String referencia;

  public TcKalanEmpresasMovimientosDto() {
    this(new Long(-1L));
  }

  public TcKalanEmpresasMovimientosDto(Long key) {
    this(2L, null, -1L, 1L, new Long(-1L), null, null, null, null, new Date(Calendar.getInstance().getTimeInMillis()), 0D, null, null, null, null, null, null, null, new Date(Calendar.getInstance().getTimeInMillis()), 1L, null);
    setKey(key);
  }

  public TcKalanEmpresasMovimientosDto(Long idAnticipo, String justificacion, Long idCliente, Long idMovimientoEstatus, Long idEmpresaMovimiento, Long idBanco, Long ejercicio, String consecutivo, Long idTipoConcepto, Date fechaAplicacion, Double total, Long idTipoMovimiento, Long idEmpresaCuenta, Long idUsuario, String observaciones, Long idEmpresa, Long orden, String concepto, Date fechaPago, Long idTipoMedioPago, String referencia) {
    setIdAnticipo(idAnticipo);
    setJustificacion(justificacion);
    setIdCliente(idCliente);
    setIdMovimientoEstatus(idMovimientoEstatus);
    setIdEmpresaMovimiento(idEmpresaMovimiento);
    setIdBanco(idBanco);
    setEjercicio(ejercicio);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setConsecutivo(consecutivo);
    setIdTipoConcepto(idTipoConcepto);
    setFechaAplicacion(fechaAplicacion);
    setTotal(total);
    setIdTipoMovimiento(idTipoMovimiento);
    setIdEmpresaCuenta(idEmpresaCuenta);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setOrden(orden);
    setConcepto(concepto);
    setFechaPago(fechaPago);
    setIdTipoMedioPago(idTipoMedioPago);
    setReferencia(referencia);
  }
	
  public void setIdAnticipo(Long idAnticipo) {
    this.idAnticipo = idAnticipo;
  }

  public Long getIdAnticipo() {
    return idAnticipo;
  }

  public void setJustificacion(String justificacion) {
    this.justificacion = justificacion;
  }

  public String getJustificacion() {
    return justificacion;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setIdMovimientoEstatus(Long idMovimientoEstatus) {
    this.idMovimientoEstatus = idMovimientoEstatus;
  }

  public Long getIdMovimientoEstatus() {
    return idMovimientoEstatus;
  }

  public void setIdEmpresaMovimiento(Long idEmpresaMovimiento) {
    this.idEmpresaMovimiento = idEmpresaMovimiento;
  }

  public Long getIdEmpresaMovimiento() {
    return idEmpresaMovimiento;
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

  public void setIdTipoConcepto(Long idTipoConcepto) {
    this.idTipoConcepto = idTipoConcepto;
  }

  public Long getIdTipoConcepto() {
    return idTipoConcepto;
  }

  public void setFechaAplicacion(Date fechaAplicacion) {
    this.fechaAplicacion = fechaAplicacion;
  }

  public Date getFechaAplicacion() {
    return fechaAplicacion;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getTotal() {
    return total;
  }

  public void setIdTipoMovimiento(Long idTipoMovimiento) {
    this.idTipoMovimiento = idTipoMovimiento;
  }

  public Long getIdTipoMovimiento() {
    return idTipoMovimiento;
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

  public String getConcepto() {
    return concepto;
  }

  public void setConcepto(String concepto) {
    this.concepto = concepto;
  }

  public Date getFechaPago() {
    return fechaPago;
  }

  public void setFechaPago(Date fechaPago) {
    this.fechaPago = fechaPago;
  }

  public Long getIdTipoMedioPago() {
    return idTipoMedioPago;
  }

  public void setIdTipoMedioPago(Long idTipoMedioPago) {
    this.idTipoMedioPago = idTipoMedioPago;
  }

  public String getReferencia() {
    return referencia;
  }

  public void setReferencia(String referencia) {
    this.referencia = referencia;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdEmpresaMovimiento();
  }

  @Override
  public void setKey(Long key) {
  	this.idEmpresaMovimiento = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdAnticipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMovimientoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaMovimiento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBanco());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoConcepto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaAplicacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMovimiento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaCuenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConcepto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMedioPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getReferencia());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idAnticipo", getIdAnticipo());
		regresar.put("justificacion", getJustificacion());
		regresar.put("idCliente", getIdCliente());
		regresar.put("idMovimientoEstatus", getIdMovimientoEstatus());
		regresar.put("idEmpresaMovimiento", getIdEmpresaMovimiento());
		regresar.put("idBanco", getIdBanco());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idTipoConcepto", getIdTipoConcepto());
		regresar.put("fechaAplicacion", getFechaAplicacion());
		regresar.put("total", getTotal());
		regresar.put("idTipoMovimiento", getIdTipoMovimiento());
		regresar.put("idEmpresaCuenta", getIdEmpresaCuenta());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("orden", getOrden());
		regresar.put("concepto", getConcepto());
		regresar.put("fechaPago", getFechaPago());
		regresar.put("idTipoMedioPago", getIdTipoMedioPago());
		regresar.put("referencia", getReferencia());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getIdAnticipo(), getJustificacion(), getIdCliente(), getIdMovimientoEstatus(), getIdEmpresaMovimiento(), getIdBanco(), getEjercicio(), getRegistro(), getConsecutivo(), getIdTipoConcepto(), getFechaAplicacion(), getTotal(), getIdTipoMovimiento(), getIdEmpresaCuenta(), getIdUsuario(), getObservaciones(), getIdEmpresa(), getOrden(), getConcepto(), getFechaPago(), getIdTipoMedioPago(), getReferencia()
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
    regresar.append("idEmpresaMovimiento~");
    regresar.append(getIdEmpresaMovimiento());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdEmpresaMovimiento());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanEmpresasMovimientosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdEmpresaMovimiento()!= null && getIdEmpresaMovimiento()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanEmpresasMovimientosDto other = (TcKalanEmpresasMovimientosDto) obj;
    if (getIdEmpresaMovimiento() != other.idEmpresaMovimiento && (getIdEmpresaMovimiento() == null || !getIdEmpresaMovimiento().equals(other.idEmpresaMovimiento))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdEmpresaMovimiento() != null ? getIdEmpresaMovimiento().hashCode() : 0);
    return hash;
  }

}


