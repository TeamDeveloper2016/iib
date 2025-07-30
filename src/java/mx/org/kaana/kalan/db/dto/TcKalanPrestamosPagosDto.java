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
@Table(name="tc_kalan_prestamos_pagos")
public class TcKalanPrestamosPagosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_tipo_afectacion")
  private Long idTipoAfectacion;
  @Column (name="id_tipo_medio_pago")
  private Long idTipoMedioPago;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_prestamo_pago")
  private Long idPrestamoPago;
  @Column (name="importe")
  private Double importe;
  @Column (name="id_banco")
  private Long idBanco;
  @Column (name="fecha_pago")
  private Date fechaPago;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="id_prestamo")
  private Long idPrestamo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="orden")
  private Long orden;
  @Column (name="referencia")
  private String referencia;

  public TcKalanPrestamosPagosDto() {
    this(new Long(-1L));
  }

  public TcKalanPrestamosPagosDto(Long key) {
    this(1L, null, new Long(-1L), 0D, null, new Date(Calendar.getInstance().getTimeInMillis()), null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKalanPrestamosPagosDto(Long idTipoAfectacion, Long idTipoMedioPago, Long idPrestamoPago, Double importe, Long idBanco, Date fechaPago, Long ejercicio, String consecutivo, Long idPrestamo, Long idUsuario, String observaciones, Long orden, String referencia) {
    setIdTipoAfectacion(idTipoAfectacion);
    setIdTipoMedioPago(idTipoMedioPago);
    setIdPrestamoPago(idPrestamoPago);
    setImporte(importe);
    setIdBanco(idBanco);
    setFechaPago(fechaPago);
    setEjercicio(ejercicio);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setConsecutivo(consecutivo);
    setIdPrestamo(idPrestamo);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setOrden(orden);
    setReferencia(referencia);
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

  public void setIdPrestamoPago(Long idPrestamoPago) {
    this.idPrestamoPago = idPrestamoPago;
  }

  public Long getIdPrestamoPago() {
    return idPrestamoPago;
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

  public void setIdPrestamo(Long idPrestamo) {
    this.idPrestamo = idPrestamo;
  }

  public Long getIdPrestamo() {
    return idPrestamo;
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
  	return getIdPrestamoPago();
  }

  @Override
  public void setKey(Long key) {
  	this.idPrestamoPago = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdTipoAfectacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMedioPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrestamoPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBanco());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrestamo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
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
		regresar.put("idTipoMedioPago", getIdTipoMedioPago());
		regresar.put("idPrestamoPago", getIdPrestamoPago());
		regresar.put("importe", getImporte());
		regresar.put("idBanco", getIdBanco());
		regresar.put("fechaPago", getFechaPago());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idPrestamo", getIdPrestamo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("orden", getOrden());
		regresar.put("referencia", getReferencia());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdTipoAfectacion(), getIdTipoMedioPago(), getIdPrestamoPago(), getImporte(), getIdBanco(), getFechaPago(), getEjercicio(), getRegistro(), getConsecutivo(), getIdPrestamo(), getIdUsuario(), getObservaciones(), getOrden(), getReferencia()
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
    regresar.append("idPrestamoPago~");
    regresar.append(getIdPrestamoPago());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPrestamoPago());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanPrestamosPagosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPrestamoPago()!= null && getIdPrestamoPago()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanPrestamosPagosDto other = (TcKalanPrestamosPagosDto) obj;
    if (getIdPrestamoPago() != other.idPrestamoPago && (getIdPrestamoPago() == null || !getIdPrestamoPago().equals(other.idPrestamoPago))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPrestamoPago() != null ? getIdPrestamoPago().hashCode() : 0);
    return hash;
  }

}


