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
@Table(name="tc_kalan_acreedores")
public class TcKalanAcreedoresDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_acredor_estatus")
  private Long idAcredorEstatus;
  @Column (name="plazo")
  private Long plazo;
  @Column (name="id_fuente")
  private Long idFuente;
  @Column (name="saldo")
  private Double saldo;
  @Column (name="limite")
  private Date limite;
  @Column (name="importe")
  private Double importe;
  @Column (name="tasa")
  private Double tasa;
  @Column (name="pagos")
  private Double pagos;
  @Column (name="ejericio")
  private Long ejericio;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="orden")
  private Long orden;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_acreedor")
  private Long idAcreedor;

  public TcKalanAcreedoresDto() {
    this(new Long(-1L));
  }

  public TcKalanAcreedoresDto(Long key) {
    this(null, null, null, null, new Date(Calendar.getInstance().getTimeInMillis()), null, null, null, null, null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKalanAcreedoresDto(Long idAcredorEstatus, Long plazo, Long idFuente, Double saldo, Date limite, Double importe, Double tasa, Double pagos, Long ejericio, String consecutivo, Long idUsuario, String observaciones, Long orden, Long idAcreedor) {
    setIdAcredorEstatus(idAcredorEstatus);
    setPlazo(plazo);
    setIdFuente(idFuente);
    setSaldo(saldo);
    setLimite(limite);
    setImporte(importe);
    setTasa(tasa);
    setPagos(pagos);
    setEjericio(ejericio);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setConsecutivo(consecutivo);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setOrden(orden);
    setIdAcreedor(idAcreedor);
  }
	
  public void setIdAcredorEstatus(Long idAcredorEstatus) {
    this.idAcredorEstatus = idAcredorEstatus;
  }

  public Long getIdAcredorEstatus() {
    return idAcredorEstatus;
  }

  public void setPlazo(Long plazo) {
    this.plazo = plazo;
  }

  public Long getPlazo() {
    return plazo;
  }

  public void setIdFuente(Long idFuente) {
    this.idFuente = idFuente;
  }

  public Long getIdFuente() {
    return idFuente;
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

  public void setPagos(Double pagos) {
    this.pagos = pagos;
  }

  public Double getPagos() {
    return pagos;
  }

  public void setEjericio(Long ejericio) {
    this.ejericio = ejericio;
  }

  public Long getEjericio() {
    return ejericio;
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

  public void setIdAcreedor(Long idAcreedor) {
    this.idAcreedor = idAcreedor;
  }

  public Long getIdAcreedor() {
    return idAcreedor;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdAcreedor();
  }

  @Override
  public void setKey(Long key) {
  	this.idAcreedor = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdAcredorEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPlazo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdFuente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSaldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLimite());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTasa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPagos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjericio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAcreedor());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idAcredorEstatus", getIdAcredorEstatus());
		regresar.put("plazo", getPlazo());
		regresar.put("idFuente", getIdFuente());
		regresar.put("saldo", getSaldo());
		regresar.put("limite", getLimite());
		regresar.put("importe", getImporte());
		regresar.put("tasa", getTasa());
		regresar.put("pagos", getPagos());
		regresar.put("ejericio", getEjericio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("orden", getOrden());
		regresar.put("idAcreedor", getIdAcreedor());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdAcredorEstatus(), getPlazo(), getIdFuente(), getSaldo(), getLimite(), getImporte(), getTasa(), getPagos(), getEjericio(), getRegistro(), getConsecutivo(), getIdUsuario(), getObservaciones(), getOrden(), getIdAcreedor()
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
    regresar.append("idAcreedor~");
    regresar.append(getIdAcreedor());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAcreedor());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanAcreedoresDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAcreedor()!= null && getIdAcreedor()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanAcreedoresDto other = (TcKalanAcreedoresDto) obj;
    if (getIdAcreedor() != other.idAcreedor && (getIdAcreedor() == null || !getIdAcreedor().equals(other.idAcreedor))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAcreedor() != null ? getIdAcreedor().hashCode() : 0);
    return hash;
  }

}


