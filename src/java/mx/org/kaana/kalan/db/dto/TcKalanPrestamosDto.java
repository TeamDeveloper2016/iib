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
@Table(name="tc_kalan_prestamos")
public class TcKalanPrestamosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="plazo")
  private Long plazo;
  @Column (name="saldo")
  private Double saldo;
  @Column (name="limite")
  private Date limite;
  @Column (name="nombre")
  private String nombre;
  @Column (name="importe")
  private Double importe;
  @Column (name="id_prestamo_estatus")
  private Long idPrestamoEstatus;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_prestamo")
  private Long idPrestamo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_empresa_persona")
  private Long idEmpresaPersona;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="orden")
  private Long orden;

  public TcKalanPrestamosDto() {
    this(new Long(-1L));
  }

  public TcKalanPrestamosDto(Long key) {
    this(1L, 0D, new Date(Calendar.getInstance().getTimeInMillis()), null, 0D, null, null, null, new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKalanPrestamosDto(Long plazo, Double saldo, Date limite, String nombre, Double importe, Long idPrestamoEstatus, Long ejercicio, String consecutivo, Long idPrestamo, Long idUsuario, Long idEmpresaPersona, String observaciones, Long orden) {
    setPlazo(plazo);
    setSaldo(saldo);
    setLimite(limite);
    setNombre(nombre);
    setImporte(importe);
    setIdPrestamoEstatus(idPrestamoEstatus);
    setEjercicio(ejercicio);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setConsecutivo(consecutivo);
    setIdPrestamo(idPrestamo);
    setIdUsuario(idUsuario);
    setIdEmpresaPersona(idEmpresaPersona);
    setObservaciones(observaciones);
    setOrden(orden);
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

  public void setIdPrestamoEstatus(Long idPrestamoEstatus) {
    this.idPrestamoEstatus = idPrestamoEstatus;
  }

  public Long getIdPrestamoEstatus() {
    return idPrestamoEstatus;
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

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdPrestamo();
  }

  @Override
  public void setKey(Long key) {
  	this.idPrestamo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getPlazo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSaldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLimite());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrestamoEstatus());
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
		regresar.append(getIdEmpresaPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("plazo", getPlazo());
		regresar.put("saldo", getSaldo());
		regresar.put("limite", getLimite());
		regresar.put("nombre", getNombre());
		regresar.put("importe", getImporte());
		regresar.put("idPrestamoEstatus", getIdPrestamoEstatus());
		regresar.put("ejericio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idPrestamo", getIdPrestamo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idEmpresaPersona", getIdEmpresaPersona());
		regresar.put("observaciones", getObservaciones());
		regresar.put("orden", getOrden());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getPlazo(), getSaldo(), getLimite(), getNombre(), getImporte(), getIdPrestamoEstatus(), getEjercicio(), getRegistro(), getConsecutivo(), getIdPrestamo(), getIdUsuario(), getIdEmpresaPersona(), getObservaciones(), getOrden()
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
    regresar.append("idPrestamo~");
    regresar.append(getIdPrestamo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPrestamo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanPrestamosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPrestamo()!= null && getIdPrestamo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) { 
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanPrestamosDto other = (TcKalanPrestamosDto) obj;
    if (getIdPrestamo() != other.idPrestamo && (getIdPrestamo() == null || !getIdPrestamo().equals(other.idPrestamo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPrestamo() != null ? getIdPrestamo().hashCode() : 0);
    return hash;
  }

}


