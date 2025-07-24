package mx.org.kaana.kalan.db.dto;

import java.io.Serializable;
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
@Table(name="tc_kalan_prestamos_pagos")
public class TcKalanPrestamosPagosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="id_tipo_afectacion")
  private Long idTipoAfectacion;
  @Column (name="id_prestamo")
  private Long idPrestamo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_prestamo_pago")
  private Long idPrestamoPago;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="orden")
  private Long orden;
  @Column (name="importe")
  private Double importe;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanPrestamosPagosDto() {
    this(new Long(-1L));
  }

  public TcKalanPrestamosPagosDto(Long key) {
    this(null, 2L, null, null, new Long(-1L), null, null, 0D, null);
    setKey(key);
  }

  public TcKalanPrestamosPagosDto(String consecutivo, Long idTipoAfectacion, Long idPrestamo, Long idUsuario, Long idPrestamoPago, String observaciones, Long orden, Double importe, Long ejercicio) {
    setConsecutivo(consecutivo);
    setIdTipoAfectacion(idTipoAfectacion);
    setIdPrestamo(idPrestamo);
    setIdUsuario(idUsuario);
    setIdPrestamoPago(idPrestamoPago);
    setObservaciones(observaciones);
    setOrden(orden);
    setImporte(importe);
    setEjercicio(ejercicio);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setIdTipoAfectacion(Long idTipoAfectacion) {
    this.idTipoAfectacion = idTipoAfectacion;
  }

  public Long getIdTipoAfectacion() {
    return idTipoAfectacion;
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

  public void setIdPrestamoPago(Long idPrestamoPago) {
    this.idPrestamoPago = idPrestamoPago;
  }

  public Long getIdPrestamoPago() {
    return idPrestamoPago;
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

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getImporte() {
    return importe;
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
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoAfectacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrestamo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrestamoPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idTipoAfectacion", getIdTipoAfectacion());
		regresar.put("idPrestamo", getIdPrestamo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idPrestamoPago", getIdPrestamoPago());
		regresar.put("observaciones", getObservaciones());
		regresar.put("orden", getOrden());
		regresar.put("importe", getImporte());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getConsecutivo(), getIdTipoAfectacion(), getIdPrestamo(), getIdUsuario(), getIdPrestamoPago(), getObservaciones(), getOrden(), getImporte(), getEjercicio(), getRegistro()
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


