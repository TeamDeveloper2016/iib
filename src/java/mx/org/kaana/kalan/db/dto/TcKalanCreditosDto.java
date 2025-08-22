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
@Table(name="tc_kalan_creditos")
public class TcKalanCreditosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="plazo")
  private Long plazo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_credito")
  private Long idCredito;
  @Column (name="saldo")
  private Double saldo;
  @Column (name="limite")
  private Date limite;
  @Column (name="fecha_aplicacion")
  private Date fechaAplicacion;
  @Column (name="nombre")
  private String nombre;
  @Column (name="importe")
  private Double importe;
  @Column (name="tasa")
  private Double tasa;
  @Column (name="pagos")
  private Long pagos;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="id_credito_estatus")
  private Long idCreditoEstatus;
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
  @Column (name="id_acreedor")
  private Long idAcreedor;
  @Column (name="id_tipo_medio_pago")
  private Long idTipoMedioPago;
  @Column (name="fecha_pago")
  private Date fechaPago;
  @Column (name="referencia")
  private String referencia;
  

  public TcKalanCreditosDto() {
    this(new Long(-1L));
  }

  public TcKalanCreditosDto(Long key) {
    this(1L, new Long(-1L), 0D, new Date(Calendar.getInstance().getTimeInMillis()), null, 0D, 0D, 1L, null, null, 1L, null, null, null, null, null, null, new Date(Calendar.getInstance().getTimeInMillis()), null, new Date(Calendar.getInstance().getTimeInMillis()), null);
    setKey(key);
  }

  public TcKalanCreditosDto(Long plazo, Long idCredito, Double saldo, Date limite, String nombre, Double importe, Double tasa, Long pagos, Long ejercicio, String consecutivo, Long idCreditoEstatus, Long idEmpresaCuenta, Long idUsuario, String observaciones, Long idEmpresa, Long orden, Long idAcreedor, Date fechaAplicacion, Long idTipoMedioPago, Date fechaPago, String referencia) {
    setPlazo(plazo);
    setIdCredito(idCredito);
    setSaldo(saldo);
    setLimite(limite);
    setNombre(nombre);
    setImporte(importe);
    setTasa(tasa);
    setPagos(pagos);
    setEjercicio(ejercicio);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setConsecutivo(consecutivo);
    setIdCreditoEstatus(idCreditoEstatus);
    setIdEmpresaCuenta(idEmpresaCuenta);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setOrden(orden);
    setIdAcreedor(idAcreedor);
    setFechaAplicacion(fechaAplicacion);
    setIdTipoMedioPago(idTipoMedioPago);
    setFechaPago(fechaPago);
    setReferencia(referencia);
  }
	
  public void setPlazo(Long plazo) {
    this.plazo = plazo;
  }

  public Long getPlazo() {
    return plazo;
  }

  public void setIdCredito(Long idCredito) {
    this.idCredito = idCredito;
  }

  public Long getIdCredito() {
    return idCredito;
  }

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setLimite(Date limite) {
    this.limite = limite;
  }

  public Date getLimite() {
    return limite;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getImporte() {
    return importe;
  }

  public void setTasa(Double tasa) {
    this.tasa = tasa;
  }

  public Double getTasa() {
    return tasa;
  }

  public void setPagos(Long pagos) {
    this.pagos = pagos;
  }

  public Long getPagos() {
    return pagos;
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

  public void setIdCreditoEstatus(Long idCreditoEstatus) {
    this.idCreditoEstatus = idCreditoEstatus;
  }

  public Long getIdCreditoEstatus() {
    return idCreditoEstatus;
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

  public void setIdAcreedor(Long idAcreedor) {
    this.idAcreedor = idAcreedor;
  }

  public Long getIdAcreedor() {
    return idAcreedor;
  }

  public Date getFechaAplicacion() {
    return fechaAplicacion;
  }

  public void setFechaAplicacion(Date fechaAplicacion) {
    this.fechaAplicacion = fechaAplicacion;
  }

  public Long getIdTipoMedioPago() {
    return idTipoMedioPago;
  }

  public void setIdTipoMedioPago(Long idTipoMedioPago) {
    this.idTipoMedioPago = idTipoMedioPago;
  }

  public Date getFechaPago() {
    return fechaPago;
  }

  public void setFechaPago(Date fechaPago) {
    this.fechaPago = fechaPago;
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
  	return getIdCredito();
  }

  @Override
  public void setKey(Long key) {
  	this.idCredito = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getPlazo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCredito());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSaldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLimite());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTasa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPagos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCreditoEstatus());
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
		regresar.append(getIdAcreedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaAplicacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMedioPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getReferencia());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("plazo", getPlazo());
		regresar.put("idCredito", getIdCredito());
		regresar.put("saldo", getSaldo());
		regresar.put("limite", getLimite());
		regresar.put("nombre", getNombre());
		regresar.put("importe", getImporte());
		regresar.put("tasa", getTasa());
		regresar.put("pagos", getPagos());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idCreditoEstatus", getIdCreditoEstatus());
		regresar.put("idEmpresaCuenta", getIdEmpresaCuenta());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("orden", getOrden());
		regresar.put("idAcreedor", getIdAcreedor());
		regresar.put("fechaAplicacion", getFechaAplicacion());
		regresar.put("idTipoMedioPago", getIdTipoMedioPago());
		regresar.put("fechaPago", getFechaPago());
		regresar.put("referencia", getReferencia());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getPlazo(), getIdCredito(), getSaldo(), getLimite(), getNombre(), getImporte(), getTasa(), getPagos(), getEjercicio(), getRegistro(), getConsecutivo(), getIdCreditoEstatus(), getIdEmpresaCuenta(), getIdUsuario(), getObservaciones(), getIdEmpresa(), getOrden(), getIdAcreedor(), getFechaAplicacion(), getIdTipoMedioPago(), getFechaPago(), getReferencia()
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
    regresar.append("idCredito~");
    regresar.append(getIdCredito());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdCredito());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanCreditosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdCredito()!= null && getIdCredito()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanCreditosDto other = (TcKalanCreditosDto) obj;
    if (getIdCredito() != other.idCredito && (getIdCredito() == null || !getIdCredito().equals(other.idCredito))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdCredito() != null ? getIdCredito().hashCode() : 0);
    return hash;
  }

}


