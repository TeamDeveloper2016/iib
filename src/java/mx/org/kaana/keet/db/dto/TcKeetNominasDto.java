package mx.org.kaana.keet.db.dto;

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
@Table(name="tc_keet_nominas")
public class TcKeetNominasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="neto")
  private Double neto;
  @Column (name="id_nomina_estatus")
  private Long idNominaEstatus;
  @Column (name="deducciones")
  private Double deducciones;
  @Column (name="id_tipo_nomina")
  private Long idTipoNomina;
  @Column (name="personas")
  private Long personas;
  @Column (name="aportaciones")
  private Double aportaciones;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nomina")
  private Long idNomina;
  @Column (name="fecha_pago")
  private Date fechaPago;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="proveedores")
  private Long proveedores;
  @Column (name="total")
  private Double total;
  @Column (name="fecha_dispersion")
  private Date fechaDispersion;
  @Column (name="id_nomina_periodo")
  private Long idNominaPeriodo;
  @Column (name="iva")
  private Double iva;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="subtotal")
  private Double subtotal;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="percepciones")
  private Double percepciones;
  @Column (name="id_completa")
  private Long idCompleta;

  public TcKeetNominasDto() {
    this(new Long(-1L));
  }

  public TcKeetNominasDto(Long key) {
    this(null, null, null, null, null, null, new Long(-1L), new Date(Calendar.getInstance().getTimeInMillis()), null, null, new Date(Calendar.getInstance().getTimeInMillis()), null, null, null, null, null, null, null, 2L);
    setKey(key);
  }

  public TcKeetNominasDto(Double neto, Long idNominaEstatus, Double deducciones, Long idTipoNomina, Long personas, Double aportaciones, Long idNomina, Date fechaPago, Long proveedores, Double total, Date fechaDispersion, Long idNominaPeriodo, Double iva, Long idUsuario, Double subtotal, String observaciones, Long idEmpresa, Double percepciones, Long idCompleta) {
    setNeto(neto);
    setIdNominaEstatus(idNominaEstatus);
    setDeducciones(deducciones);
    setIdTipoNomina(idTipoNomina);
    setPersonas(personas);
    setAportaciones(aportaciones);
    setIdNomina(idNomina);
    setFechaPago(fechaPago);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setProveedores(proveedores);
    setTotal(total);
    setFechaDispersion(fechaDispersion);
    setIdNominaPeriodo(idNominaPeriodo);
    setIva(iva);
    setIdUsuario(idUsuario);
    setSubtotal(subtotal);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setPercepciones(percepciones);
    this.idCompleta= idCompleta;
  }
	
  public void setNeto(Double neto) {
    this.neto = neto;
  }

  public Double getNeto() {
    return neto;
  }

  public void setIdNominaEstatus(Long idNominaEstatus) {
    this.idNominaEstatus = idNominaEstatus;
  }

  public Long getIdNominaEstatus() {
    return idNominaEstatus;
  }

  public void setDeducciones(Double deducciones) {
    this.deducciones = deducciones;
  }

  public Double getDeducciones() {
    return deducciones;
  }

  public void setIdTipoNomina(Long idTipoNomina) {
    this.idTipoNomina = idTipoNomina;
  }

  public Long getIdTipoNomina() {
    return idTipoNomina;
  }

  public void setPersonas(Long personas) {
    this.personas = personas;
  }

  public Long getPersonas() {
    return personas;
  }

  public void setAportaciones(Double aportaciones) {
    this.aportaciones = aportaciones;
  }

  public Double getAportaciones() {
    return aportaciones;
  }

  public void setIdNomina(Long idNomina) {
    this.idNomina = idNomina;
  }

  public Long getIdNomina() {
    return idNomina;
  }

  public void setFechaPago(Date fechaPago) {
    this.fechaPago = fechaPago;
  }

  public Date getFechaPago() {
    return fechaPago;
  }

  public void setRegistro(Timestamp registro) {
    this.registro = registro;
  }

  public Timestamp getRegistro() {
    return registro;
  }

  public void setProveedores(Long proveedores) {
    this.proveedores = proveedores;
  }

  public Long getProveedores() {
    return proveedores;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getTotal() {
    return total;
  }

  public void setFechaDispersion(Date fechaDispersion) {
    this.fechaDispersion = fechaDispersion;
  }

  public Date getFechaDispersion() {
    return fechaDispersion;
  }

  public void setIdNominaPeriodo(Long idNominaPeriodo) {
    this.idNominaPeriodo = idNominaPeriodo;
  }

  public Long getIdNominaPeriodo() {
    return idNominaPeriodo;
  }

  public void setIva(Double iva) {
    this.iva = iva;
  }

  public Double getIva() {
    return iva;
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

  public void setPercepciones(Double percepciones) {
    this.percepciones = percepciones;
  }

  public Double getPercepciones() {
    return percepciones;
  }

  public Long getIdCompleta() {
    return idCompleta;
  }

  public void setIdCompleta(Long idCompleta) {
    this.idCompleta = idCompleta;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdNomina();
  }

  @Override
  public void setKey(Long key) {
  	this.idNomina = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getNeto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDeducciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPersonas());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAportaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getProveedores());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaDispersion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaPeriodo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIva());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubtotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPercepciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCompleta());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("neto", getNeto());
		regresar.put("idNominaEstatus", getIdNominaEstatus());
		regresar.put("deducciones", getDeducciones());
		regresar.put("idTipoNomina", getIdTipoNomina());
		regresar.put("personas", getPersonas());
		regresar.put("aportaciones", getAportaciones());
		regresar.put("idNomina", getIdNomina());
		regresar.put("fechaPago", getFechaPago());
		regresar.put("registro", getRegistro());
		regresar.put("proveedores", getProveedores());
		regresar.put("total", getTotal());
		regresar.put("fechaDispersion", getFechaDispersion());
		regresar.put("idNominaPeriodo", getIdNominaPeriodo());
		regresar.put("iva", getIva());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("subtotal", getSubtotal());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("percepciones", getPercepciones());
		regresar.put("idCompleta", getIdCompleta());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getNeto(), getIdNominaEstatus(), getDeducciones(), getIdTipoNomina(), getPersonas(), getAportaciones(), getIdNomina(), getFechaPago(), getRegistro(), getProveedores(), getTotal(), getFechaDispersion(), getIdNominaPeriodo(), getIva(), getIdUsuario(), getSubtotal(), getObservaciones(), getIdEmpresa(), getPercepciones(), getIdCompleta()
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
    regresar.append("idNomina~");
    regresar.append(getIdNomina());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNomina());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNominasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNomina()!= null && getIdNomina()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNominasDto other = (TcKeetNominasDto) obj;
    if (getIdNomina() != other.idNomina && (getIdNomina() == null || !getIdNomina().equals(other.idNomina))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNomina() != null ? getIdNomina().hashCode() : 0);
    return hash;
  }

}


