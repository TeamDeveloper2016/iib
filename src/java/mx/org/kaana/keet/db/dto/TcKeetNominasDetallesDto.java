package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
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
@Table(name="tc_keet_nominas_detalles")
public class TcKeetNominasDetallesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="valor")
  private Double valor;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nomina_detalle")
  private Long idNominaDetalle;
  @Column (name="formula")
  private String formula;
  @Column (name="id_nomina_concepto")
  private Long idNominaConcepto;
  @Column (name="id_nomina_persona")
  private Long idNominaPersona;
  @Column (name="nombre")
  private String nombre;
  @Column (name="clave")
  private String clave;
  @Column (name="fecha")
  private Date fecha;
  @Column (name="registro")
  private Timestamp registro;

  public TcKeetNominasDetallesDto() {
    this(new Long(-1L));
  }

  public TcKeetNominasDetallesDto(Long key) {
    this(null, new Long(-1L), null, null, null, null, null, new Date(Calendar.getInstance().getTimeInMillis()));
    setKey(key);
  }

  public TcKeetNominasDetallesDto(Double valor, Long idNominaDetalle, String formula, Long idNominaConcepto, Long idNominaPersona, String nombre, String clave, Date fecha) {
    setValor(valor);
    setIdNominaDetalle(idNominaDetalle);
    setFormula(formula);
    setIdNominaConcepto(idNominaConcepto);
    setIdNominaPersona(idNominaPersona);
    setNombre(nombre);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		this.clave= clave;
		this.fecha= fecha;
  }
	
  public void setValor(Double valor) {
    this.valor = valor;
  }

  public Double getValor() {
    return valor;
  }

  public void setIdNominaDetalle(Long idNominaDetalle) {
    this.idNominaDetalle = idNominaDetalle;
  }

  public Long getIdNominaDetalle() {
    return idNominaDetalle;
  }

  public void setFormula(String formula) {
    this.formula = formula;
  }

  public String getFormula() {
    return formula;
  }

  public void setIdNominaConcepto(Long idNominaConcepto) {
    this.idNominaConcepto = idNominaConcepto;
  }

  public Long getIdNominaConcepto() {
    return idNominaConcepto;
  }

  public void setIdNominaPersona(Long idNominaPersona) {
    this.idNominaPersona = idNominaPersona;
  }

  public Long getIdNominaPersona() {
    return idNominaPersona;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave=clave;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha=fecha;
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
  	return getIdNominaDetalle();
  }

  @Override
  public void setKey(Long key) {
  	this.idNominaDetalle = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getValor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFormula());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaConcepto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFecha());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("valor", getValor());
		regresar.put("idNominaDetalle", getIdNominaDetalle());
		regresar.put("formula", getFormula());
		regresar.put("idNominaConcepto", getIdNominaConcepto());
		regresar.put("idNominaPersona", getIdNominaPersona());
		regresar.put("nombre", getNombre());
		regresar.put("clave", getClave());
		regresar.put("fecha", getFecha());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getValor(), getIdNominaDetalle(), getFormula(), getIdNominaConcepto(), getIdNominaPersona(), getNombre(), getClave(), getFecha(), getRegistro()
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
    regresar.append("idNominaDetalle~");
    regresar.append(getIdNominaDetalle());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNominaDetalle());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNominasDetallesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNominaDetalle()!= null && getIdNominaDetalle()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNominasDetallesDto other = (TcKeetNominasDetallesDto) obj;
    if (getIdNominaDetalle() != other.idNominaDetalle && (getIdNominaDetalle() == null || !getIdNominaDetalle().equals(other.idNominaDetalle))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNominaDetalle() != null ? getIdNominaDetalle().hashCode() : 0);
    return hash;
  }

}


