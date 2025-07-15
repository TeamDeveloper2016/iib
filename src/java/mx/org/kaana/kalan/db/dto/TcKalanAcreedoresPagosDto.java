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
@Table(name="tc_kalan_acreedores_pagos")
public class TcKalanAcreedoresPagosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="consecutivo")
  private String consecutivo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_acreedor_pago")
  private Long idAcreedorPago;
  @Column (name="importe")
  private Double importe;

  public TcKalanAcreedoresPagosDto() {
    this(new Long(-1L));
  }

  public TcKalanAcreedoresPagosDto(Long key) {
    this(null, new Long(-1L), null);
    setKey(key);
  }

  public TcKalanAcreedoresPagosDto(String consecutivo, Long idAcreedorPago, Double importe) {
    setConsecutivo(consecutivo);
    setIdAcreedorPago(idAcreedorPago);
    setImporte(importe);
  }
	
  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setIdAcreedorPago(Long idAcreedorPago) {
    this.idAcreedorPago = idAcreedorPago;
  }

  public Long getIdAcreedorPago() {
    return idAcreedorPago;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getImporte() {
    return importe;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdAcreedorPago();
  }

  @Override
  public void setKey(Long key) {
  	this.idAcreedorPago = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAcreedorPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idAcreedorPago", getIdAcreedorPago());
		regresar.put("importe", getImporte());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getConsecutivo(), getIdAcreedorPago(), getImporte()
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
    regresar.append("idAcreedorPago~");
    regresar.append(getIdAcreedorPago());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAcreedorPago());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanAcreedoresPagosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAcreedorPago()!= null && getIdAcreedorPago()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanAcreedoresPagosDto other = (TcKalanAcreedoresPagosDto) obj;
    if (getIdAcreedorPago() != other.idAcreedorPago && (getIdAcreedorPago() == null || !getIdAcreedorPago().equals(other.idAcreedorPago))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAcreedorPago() != null ? getIdAcreedorPago().hashCode() : 0);
    return hash;
  }

}


