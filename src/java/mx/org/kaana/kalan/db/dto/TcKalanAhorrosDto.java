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
@Table(name="tc_kalan_ahorros")
public class TcKalanAhorrosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_ahorro")
  private Long idAhorro;
  @Column (name="fecha_arranque")
  private Date fechaArranque;
  @Column (name="plazo")
  private Long plazo;
  @Column (name="saldo")
  private Double saldo;
  @Column (name="id_ahorro_estatus")
  private Long idAhorroEstatus;
  @Column (name="limite")
  private Date limite;
  @Column (name="nombre")
  private String nombre;
  @Column (name="importe")
  private Double importe;
  @Column (name="ejercicio")
  private Long ejercicio;
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
  @Column (name="id_empresa_persona")
  private Long idEmpresaPersona;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="orden")
  private Long orden;
  @Column (name="pagos")
  private Long pagos;

  public TcKalanAhorrosDto() {
    this(new Long(-1L));
  }

  public TcKalanAhorrosDto(Long key) {
    this(new Long(-1L), new Date(Calendar.getInstance().getTimeInMillis()), 7L, 0D, 1L, new Date(Calendar.getInstance().getTimeInMillis()), null, 0D, null, null, new Date(Calendar.getInstance().getTimeInMillis()), null, null, null, null, null, null, 0L);
    setKey(key);
  }

  public TcKalanAhorrosDto(Long idAhorro, Date fechaArranque, Long plazo, Double saldo, Long idAhorroEstatus, Date limite, String nombre, Double importe, Long ejercicio, String consecutivo, Date fechaAplicacion, Long idEmpresaCuenta, Long idUsuario, Long idEmpresaPersona, String observaciones, Long idEmpresa, Long orden, Long pagos) {
    setIdAhorro(idAhorro);
    setFechaArranque(fechaArranque);
    setPlazo(plazo);
    setSaldo(saldo);
    setIdAhorroEstatus(idAhorroEstatus);
    setLimite(limite);
    setNombre(nombre);
    setImporte(importe);
    setEjercicio(ejercicio);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setConsecutivo(consecutivo);
    setFechaAplicacion(fechaAplicacion);
    setIdEmpresaCuenta(idEmpresaCuenta);
    setIdUsuario(idUsuario);
    setIdEmpresaPersona(idEmpresaPersona);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setOrden(orden);
    setPagos(pagos);
  }
	
  public void setIdAhorro(Long idAhorro) {
    this.idAhorro = idAhorro;
  }

  public Long getIdAhorro() {
    return idAhorro;
  }

  public void setFechaArranque(Date fechaArranque) {
    this.fechaArranque = fechaArranque;
  }

  public Date getFechaArranque() {
    return fechaArranque;
  }

  public void setPlazo(Long plazo) {
    this.plazo = plazo;
  }

  public Long getPlazo() {
    return plazo;
  }

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setIdAhorroEstatus(Long idAhorroEstatus) {
    this.idAhorroEstatus = idAhorroEstatus;
  }

  public Long getIdAhorroEstatus() {
    return idAhorroEstatus;
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

  public void setIdEmpresaPersona(Long idEmpresaPersona) {
    this.idEmpresaPersona = idEmpresaPersona;
  }

  public Long getIdEmpresaPersona() {
    return idEmpresaPersona;
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

  public Long getPagos() {
    return pagos;
  }

  public void setPagos(Long pagos) {
    this.pagos = pagos;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdAhorro();
  }

  @Override
  public void setKey(Long key) {
  	this.idAhorro = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdAhorro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaArranque());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPlazo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSaldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAhorroEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLimite());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
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
		regresar.append(getIdEmpresaPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPagos());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idAhorro", getIdAhorro());
		regresar.put("fechaArranque", getFechaArranque());
		regresar.put("plazo", getPlazo());
		regresar.put("saldo", getSaldo());
		regresar.put("idAhorroEstatus", getIdAhorroEstatus());
		regresar.put("limite", getLimite());
		regresar.put("nombre", getNombre());
		regresar.put("importe", getImporte());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("fechaAplicacion", getFechaAplicacion());
		regresar.put("idEmpresaCuenta", getIdEmpresaCuenta());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idEmpresaPersona", getIdEmpresaPersona());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("orden", getOrden());
		regresar.put("pagos", getPagos());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getIdAhorro(), getFechaArranque(), getPlazo(), getSaldo(), getIdAhorroEstatus(), getLimite(), getNombre(), getImporte(), getEjercicio(), getRegistro(), getConsecutivo(), getFechaAplicacion(), getIdEmpresaCuenta(), getIdUsuario(), getIdEmpresaPersona(), getObservaciones(), getIdEmpresa(), getOrden(), getPagos()
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
    regresar.append("idAhorro~");
    regresar.append(getIdAhorro());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAhorro());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanAhorrosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAhorro()!= null && getIdAhorro()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanAhorrosDto other = (TcKalanAhorrosDto) obj;
    if (getIdAhorro() != other.idAhorro && (getIdAhorro() == null || !getIdAhorro().equals(other.idAhorro))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAhorro() != null ? getIdAhorro().hashCode() : 0);
    return hash;
  }

}


