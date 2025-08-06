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
@Table(name="tc_kalan_ahorros_pagos")
public class TcKalanAhorrosPagosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_tipo_afectacion")
  private Long idTipoAfectacion;
  @Column (name="id_ahorro")
  private Long idAhorro;
  @Column (name="id_tipo_medio_pago")
  private Long idTipoMedioPago;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_ahorro_pago")
  private Long idAhorroPago;
  @Column (name="importe")
  private Double importe;
  @Column (name="id_banco")
  private Long idBanco;
  @Column (name="fecha_pago")
  private Date fechaPago;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="fecha_aplicacion")
  private Date fechaAplicacion;
  @Column (name="id_ahorro_control")
  private Long idAhorroControl;
  @Column (name="id_empresa_cuenta")
  private Long idEmpresaCuenta;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="orden")
  private Long orden;
  @Column (name="referencia")
  private String referencia;

  public TcKalanAhorrosPagosDto() {
    this(new Long(-1L));
  }

  public TcKalanAhorrosPagosDto(Long key) {
    this(null, null, null, new Long(-1L), null, null, new Date(Calendar.getInstance().getTimeInMillis()), null, null, new Date(Calendar.getInstance().getTimeInMillis()), null, null, null, null, null);
    setKey(key);
  }

  public TcKalanAhorrosPagosDto(Long idTipoAfectacion, Long idAhorro, Long idTipoMedioPago, Long idAhorroPago, Double importe, Long idBanco, Date fechaPago, Long ejercicio, String consecutivo, Date fechaAplicacion, Long idAhorroControl, Long idEmpresaCuenta, Long idEmpresa, Long orden, String referencia) {
    setIdTipoAfectacion(idTipoAfectacion);
    setIdAhorro(idAhorro);
    setIdTipoMedioPago(idTipoMedioPago);
    setIdAhorroPago(idAhorroPago);
    setImporte(importe);
    setIdBanco(idBanco);
    setFechaPago(fechaPago);
    setEjercicio(ejercicio);
    setConsecutivo(consecutivo);
    setFechaAplicacion(fechaAplicacion);
    setIdAhorroControl(idAhorroControl);
    setIdEmpresaCuenta(idEmpresaCuenta);
    setIdEmpresa(idEmpresa);
    setOrden(orden);
    setReferencia(referencia);
  }
	
  public void setIdTipoAfectacion(Long idTipoAfectacion) {
    this.idTipoAfectacion = idTipoAfectacion;
  }

  public Long getIdTipoAfectacion() {
    return idTipoAfectacion;
  }

  public void setIdAhorro(Long idAhorro) {
    this.idAhorro = idAhorro;
  }

  public Long getIdAhorro() {
    return idAhorro;
  }

  public void setIdTipoMedioPago(Long idTipoMedioPago) {
    this.idTipoMedioPago = idTipoMedioPago;
  }

  public Long getIdTipoMedioPago() {
    return idTipoMedioPago;
  }

  public void setIdAhorroPago(Long idAhorroPago) {
    this.idAhorroPago = idAhorroPago;
  }

  public Long getIdAhorroPago() {
    return idAhorroPago;
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

  public void setIdAhorroControl(Long idAhorroControl) {
    this.idAhorroControl = idAhorroControl;
  }

  public Long getIdAhorroControl() {
    return idAhorroControl;
  }

  public void setIdEmpresaCuenta(Long idEmpresaCuenta) {
    this.idEmpresaCuenta = idEmpresaCuenta;
  }

  public Long getIdEmpresaCuenta() {
    return idEmpresaCuenta;
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

  @Transient
  @Override
  public Long getKey() {
  	return getIdAhorroPago();
  }

  @Override
  public void setKey(Long key) {
  	this.idAhorroPago = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdTipoAfectacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAhorro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMedioPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAhorroPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBanco());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaAplicacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAhorroControl());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaCuenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getReferencia());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idTipoAfectacion", getIdTipoAfectacion());
		regresar.put("idAhorro", getIdAhorro());
		regresar.put("idTipoMedioPago", getIdTipoMedioPago());
		regresar.put("idAhorroPago", getIdAhorroPago());
		regresar.put("importe", getImporte());
		regresar.put("idBanco", getIdBanco());
		regresar.put("fechaPago", getFechaPago());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("fechaAplicacion", getFechaAplicacion());
		regresar.put("idAhorroControl", getIdAhorroControl());
		regresar.put("idEmpresaCuenta", getIdEmpresaCuenta());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("orden", getOrden());
		regresar.put("referencia", getReferencia());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdTipoAfectacion(), getIdAhorro(), getIdTipoMedioPago(), getIdAhorroPago(), getImporte(), getIdBanco(), getFechaPago(), getEjercicio(), getConsecutivo(), getFechaAplicacion(), getIdAhorroControl(), getIdEmpresaCuenta(), getIdEmpresa(), getOrden(), getReferencia()
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
    regresar.append("idAhorroPago~");
    regresar.append(getIdAhorroPago());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAhorroPago());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanAhorrosPagosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAhorroPago()!= null && getIdAhorroPago()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanAhorrosPagosDto other = (TcKalanAhorrosPagosDto) obj;
    if (getIdAhorroPago() != other.idAhorroPago && (getIdAhorroPago() == null || !getIdAhorroPago().equals(other.idAhorroPago))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAhorroPago() != null ? getIdAhorroPago().hashCode() : 0);
    return hash;
  }

}


