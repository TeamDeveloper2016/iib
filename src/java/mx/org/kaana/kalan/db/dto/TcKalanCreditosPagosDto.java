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
@Table(name="tc_kalan_creditos_pagos")
public class TcKalanCreditosPagosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="consecutivo")
  private String consecutivo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_credito_pago")
  private Long idCreditoPago;
  @Column (name="id_credito")
  private Long idCredito;
  @Column (name="importe")
  private Double importe;

  public TcKalanCreditosPagosDto() {
    this(new Long(-1L));
  }

  public TcKalanCreditosPagosDto(Long key) {
    this(null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcKalanCreditosPagosDto(String consecutivo, Long idCreditoPago, Long idCredito, Double importe) {
    setConsecutivo(consecutivo);
    setIdCreditoPago(idCreditoPago);
    setIdCredito(idCredito);
    setImporte(importe);
  }
	
  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setIdCreditoPago(Long idCreditoPago) {
    this.idCreditoPago = idCreditoPago;
  }

  public Long getIdCreditoPago() {
    return idCreditoPago;
  }

  public void setIdCredito(Long idCredito) {
    this.idCredito = idCredito;
  }

  public Long getIdCredito() {
    return idCredito;
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
  	return getIdCreditoPago();
  }

  @Override
  public void setKey(Long key) {
  	this.idCreditoPago = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCreditoPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCredito());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idCreditoPago", getIdCreditoPago());
		regresar.put("idCredito", getIdCredito());
		regresar.put("importe", getImporte());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getConsecutivo(), getIdCreditoPago(), getIdCredito(), getImporte()
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
    regresar.append("idCreditoPago~");
    regresar.append(getIdCreditoPago());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdCreditoPago());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanCreditosPagosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdCreditoPago()!= null && getIdCreditoPago()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanCreditosPagosDto other = (TcKalanCreditosPagosDto) obj;
    if (getIdCreditoPago() != other.idCreditoPago && (getIdCreditoPago() == null || !getIdCreditoPago().equals(other.idCreditoPago))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdCreditoPago() != null ? getIdCreditoPago().hashCode() : 0);
    return hash;
  }

}


