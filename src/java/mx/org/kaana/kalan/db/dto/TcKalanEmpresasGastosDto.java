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
@Table(name="tc_kalan_empresas_gastos")
public class TcKalanEmpresasGastosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_gasto_clasificacion")
  private Long idGastoClasificacion;
  @Column (name="id_gasto_comprobante")
  private Long idGastoComprobante;
  @Column (name="iva_calculado")
  private Double ivaCalculado;
  @Column (name="id_activo_ieps")
  private Long idActivoIeps;
  @Column (name="fecha_aplicacion")
  private Date fechaAplicacion;
  @Column (name="total")
  private Double total;
  @Column (name="id_empresa_cuenta")
  private Long idEmpresaCuenta;
  @Column (name="iva")
  private Double iva;
  @Column (name="fecha_referencia")
  private Date fechaReferencia;
  @Column (name="id_proveedor")
  private Long idProveedor;
  @Column (name="iva_retenido")
  private Double ivaRetenido;
  @Column (name="id_activo_cheque")
  private Long idActivoCheque;
  @Column (name="importe")
  private Double importe;
  @Column (name="pago")
  private Long pago;
  @Column (name="pagos")
  private Long pagos;
  @Column (name="id_gasto_subclasificacion")
  private Long idGastoSubclasificacion;
  @Column (name="ieps")
  private Double ieps;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="subtotal")
  private Double subtotal;
  @Column (name="concepto")
  private String concepto;
  @Column (name="ieps_calculado")
  private Double iepsCalculado;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_empresa_gasto")
  private Long idEmpresaGasto;
  @Column (name="referencia")
  private String referencia;
  @Column (name="id_activo_prorratear")
  private Long idActivoProrratear;
  @Column (name="id_gasto_estatus")
  private Long idGastoEstatus;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="orden")
  private Long orden;
  @Column (name="id_fuente")
  private Long idFuente;
  @Column (name="id_tipo_medio_pago")
  private Long idTipoMedioPago;

  public TcKalanEmpresasGastosDto() {
    this(new Long(-1L));
  }

  public TcKalanEmpresasGastosDto(Long key) {
    this(-1L, -1L, 0D, 2L, new Date(Calendar.getInstance().getTimeInMillis()), 0D, -1L, 0D, new Date(Calendar.getInstance().getTimeInMillis()), -1L, 0D, 2L, 0D, 1L, 12L, -1L, Constantes.PORCENTAJE_IEPS* 100, null, 0D, null, 0D, null, -1L, new Long(-1L), null, 2L, -1L, null, null, null, 1L, 3L);
    setKey(key);
  }

  public TcKalanEmpresasGastosDto(Long idGastoClasificacion, Long idGastoComprobante, Double ivaCalculado, Long idActivoIeps, Date fechaAplicacion, Double total, Long idEmpresaCuenta, Double iva, Date fechaReferencia, Long idProveedor, Double ivaRetenido, Long idActivoCheque, Double importe, Long pago, Long pagos, Long idGastoSubclasificacion, Double ieps, Long idUsuario, Double subtotal, String concepto, Double iepsCalculado, String observaciones, Long idEmpresa, Long idEmpresaGasto, String referencia, Long idActivoProrratear, Long idGastoEstatus, String consecutivo, Long ejercicio, Long orden, Long idFuente, Long idTipoMedioPago) {
    setIdGastoClasificacion(idGastoClasificacion);
    setIdGastoComprobante(idGastoComprobante);
    setIvaCalculado(ivaCalculado);
    setIdActivoIeps(idActivoIeps);
    setFechaAplicacion(fechaAplicacion);
    setTotal(total);
    setIdEmpresaCuenta(idEmpresaCuenta);
    setIva(iva);
    setFechaReferencia(fechaReferencia);
    setIdProveedor(idProveedor);
    setIvaRetenido(ivaRetenido);
    setIdActivoCheque(idActivoCheque);
    setImporte(importe);
    setPago(pago);
    setPagos(pagos);
    setIdGastoSubclasificacion(idGastoSubclasificacion);
    setIeps(ieps);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setIdUsuario(idUsuario);
    setSubtotal(subtotal);
    setConcepto(concepto);
    setIepsCalculado(iepsCalculado);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setIdEmpresaGasto(idEmpresaGasto);
    setReferencia(referencia);
    setIdActivoProrratear(idActivoProrratear);
    setIdGastoEstatus(idGastoEstatus);
    setConsecutivo(consecutivo);
    setEjercicio(ejercicio);
    setOrden(orden);
    setIdFuente(idFuente);
    setIdTipoMedioPago(idTipoMedioPago);
  }
	
  public void setIdGastoClasificacion(Long idGastoClasificacion) {
    this.idGastoClasificacion = idGastoClasificacion;
  }

  public Long getIdGastoClasificacion() {
    return idGastoClasificacion;
  }

  public void setIdGastoComprobante(Long idGastoComprobante) {
    this.idGastoComprobante = idGastoComprobante;
  }

  public Long getIdGastoComprobante() {
    return idGastoComprobante;
  }

  public void setIvaCalculado(Double ivaCalculado) {
    this.ivaCalculado = ivaCalculado;
  }

  public Double getIvaCalculado() {
    return ivaCalculado;
  }

  public void setIdActivoIeps(Long idActivoIeps) {
    this.idActivoIeps = idActivoIeps;
  }

  public Long getIdActivoIeps() {
    return idActivoIeps;
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

  public void setIdEmpresaCuenta(Long idEmpresaCuenta) {
    this.idEmpresaCuenta = idEmpresaCuenta;
  }

  public Long getIdEmpresaCuenta() {
    return idEmpresaCuenta;
  }

  public void setIva(Double iva) {
    this.iva = iva;
  }

  public Double getIva() {
    return iva;
  }

  public void setFechaReferencia(Date fechaReferencia) {
    this.fechaReferencia = fechaReferencia;
  }

  public Date getFechaReferencia() {
    return fechaReferencia;
  }

  public void setIdProveedor(Long idProveedor) {
    this.idProveedor = idProveedor;
  }

  public Long getIdProveedor() {
    return idProveedor;
  }

  public void setIvaRetenido(Double ivaRetenido) {
    this.ivaRetenido = ivaRetenido;
  }

  public Double getIvaRetenido() {
    return ivaRetenido;
  }

  public void setIdActivoCheque(Long idActivoCheque) {
    this.idActivoCheque = idActivoCheque;
  }

  public Long getIdActivoCheque() {
    return idActivoCheque;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getImporte() {
    return importe;
  }

  public void setPago(Long pago) {
    this.pago = pago;
  }

  public Long getPago() {
    return pago;
  }

  public void setPagos(Long pagos) {
    this.pagos = pagos;
  }

  public Long getPagos() {
    return pagos;
  }

  public void setIdGastoSubclasificacion(Long idGastoSubclasificacion) {
    this.idGastoSubclasificacion = idGastoSubclasificacion;
  }

  public Long getIdGastoSubclasificacion() {
    return idGastoSubclasificacion;
  }

  public void setIeps(Double ieps) {
    this.ieps = ieps;
  }

  public Double getIeps() {
    return ieps;
  }

  public void setRegistro(Timestamp registro) {
    this.registro = registro;
  }

  public Timestamp getRegistro() {
    return registro;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setSubtotal(Double subtotal) {
    this.subtotal = subtotal;
  }

  public Double getSubtotal() {
    return subtotal;
  }

  public void setConcepto(String concepto) {
    this.concepto = concepto;
  }

  public String getConcepto() {
    return concepto;
  }

  public void setIepsCalculado(Double iepsCalculado) {
    this.iepsCalculado = iepsCalculado;
  }

  public Double getIepsCalculado() {
    return iepsCalculado;
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

  public void setIdEmpresaGasto(Long idEmpresaGasto) {
    this.idEmpresaGasto = idEmpresaGasto;
  }

  public Long getIdEmpresaGasto() {
    return idEmpresaGasto;
  }

  public void setReferencia(String referencia) {
    this.referencia = referencia;
  }

  public String getReferencia() {
    return referencia;
  }

  public void setIdActivoProrratear(Long idActivoProrratear) {
    this.idActivoProrratear = idActivoProrratear;
  }

  public Long getIdActivoProrratear() {
    return idActivoProrratear;
  }

  public Long getIdGastoEstatus() {
    return idGastoEstatus;
  }

  public void setIdGastoEstatus(Long idGastoEstatus) {
    this.idGastoEstatus = idGastoEstatus;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public Long getEjercicio() {
    return ejercicio;
  }

  public void setEjercicio(Long ejercicio) {
    this.ejercicio = ejercicio;
  }

  public Long getOrden() {
    return orden;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getIdFuente() {
    return idFuente;
  }

  public void setIdFuente(Long idFuente) {
    this.idFuente = idFuente;
  }

  public Long getIdTipoMedioPago() {
    return idTipoMedioPago;
  }

  public void setIdTipoMedioPago(Long idTipoMedioPago) {
    this.idTipoMedioPago = idTipoMedioPago;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdEmpresaGasto();
  }

  @Override
  public void setKey(Long key) {
  	this.idEmpresaGasto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdGastoClasificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGastoComprobante());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIvaCalculado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdActivoIeps());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaAplicacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaCuenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIva());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaReferencia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIvaRetenido());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdActivoCheque());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPagos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGastoSubclasificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIeps());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubtotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConcepto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIepsCalculado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaGasto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getReferencia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdActivoProrratear());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGastoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdFuente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMedioPago());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idGastoClasificacion", getIdGastoClasificacion());
		regresar.put("idGastoComprobante", getIdGastoComprobante());
		regresar.put("ivaCalculado", getIvaCalculado());
		regresar.put("idActivoIeps", getIdActivoIeps());
		regresar.put("fechaAplicacion", getFechaAplicacion());
		regresar.put("total", getTotal());
		regresar.put("idEmpresaCuenta", getIdEmpresaCuenta());
		regresar.put("iva", getIva());
		regresar.put("fechaReferencia", getFechaReferencia());
		regresar.put("idProveedor", getIdProveedor());
		regresar.put("ivaRetenido", getIvaRetenido());
		regresar.put("idActivoCheque", getIdActivoCheque());
		regresar.put("importe", getImporte());
		regresar.put("pago", getPago());
		regresar.put("pagos", getPagos());
		regresar.put("idGastoSubclasificacion", getIdGastoSubclasificacion());
		regresar.put("ieps", getIeps());
		regresar.put("registro", getRegistro());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("subtotal", getSubtotal());
		regresar.put("concepto", getConcepto());
		regresar.put("iepsCalculado", getIepsCalculado());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("idEmpresaGasto", getIdEmpresaGasto());
		regresar.put("referencia", getReferencia());
		regresar.put("idActivoProrratear", getIdActivoProrratear());
		regresar.put("idGastoEstatus", getIdGastoEstatus());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("orden", getOrden());
		regresar.put("idFuente", getIdFuente());
		regresar.put("idTipoMedioPago", getIdTipoMedioPago());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getIdGastoClasificacion(), getIdGastoComprobante(), getIvaCalculado(), getIdActivoIeps(), getFechaAplicacion(), getTotal(), getIdEmpresaCuenta(), getIva(), getFechaReferencia(), getIdProveedor(), getIvaRetenido(), getIdActivoCheque(), getImporte(), getPago(), getPagos(), getIdGastoSubclasificacion(), getIeps(), getRegistro(), getIdUsuario(), getSubtotal(), getConcepto(), getIepsCalculado(), getObservaciones(), getIdEmpresa(), getIdEmpresaGasto(), getReferencia(), getIdActivoProrratear(), getIdGastoEstatus(), getConsecutivo(), getEjercicio(), getOrden(), getIdFuente(), getIdTipoMedioPago()
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
    regresar.append("idEmpresaGasto~");
    regresar.append(getIdEmpresaGasto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdEmpresaGasto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanEmpresasGastosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdEmpresaGasto()!= null && getIdEmpresaGasto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanEmpresasGastosDto other = (TcKalanEmpresasGastosDto) obj;
    if (getIdEmpresaGasto() != other.idEmpresaGasto && (getIdEmpresaGasto() == null || !getIdEmpresaGasto().equals(other.idEmpresaGasto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdEmpresaGasto() != null ? getIdEmpresaGasto().hashCode() : 0);
    return hash;
  }

}


