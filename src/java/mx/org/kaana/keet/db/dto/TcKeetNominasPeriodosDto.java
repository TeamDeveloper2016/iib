package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.LocalDate;
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
@Table(name="tc_keet_nominas_periodos")
public class TcKeetNominasPeriodosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="corte")
  private Timestamp corte;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nomina_periodo")
  private Long idNominaPeriodo;
  @Column (name="inicio")
  private Date inicio;
  @Column (name="termino")
  private Date termino;
  @Column (name="orden")
  private Long orden;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private Timestamp registro;

  public TcKeetNominasPeriodosDto() {
    this(new Long(-1L));
  }

  public TcKeetNominasPeriodosDto(Long key) {
    this(new Timestamp(Calendar.getInstance().getTimeInMillis()), new Long(-1L), new Date(Calendar.getInstance().getTimeInMillis()), new Date(Calendar.getInstance().getTimeInMillis()), null, null);
    setKey(key);
  }

  public TcKeetNominasPeriodosDto(Timestamp corte, Long idNominaPeriodo, Date inicio, Date termino, Long orden, Long ejercicio) {
    setCorte(corte);
    setIdNominaPeriodo(idNominaPeriodo);
    setInicio(inicio);
    setTermino(termino);
    setOrden(orden);
    setEjercicio(ejercicio);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setCorte(Timestamp corte) {
    this.corte = corte;
  }

  public Timestamp getCorte() {
    return corte;
  }

  public void setIdNominaPeriodo(Long idNominaPeriodo) {
    this.idNominaPeriodo = idNominaPeriodo;
  }

  public Long getIdNominaPeriodo() {
    return idNominaPeriodo;
  }

  public void setInicio(Date inicio) {
    this.inicio = inicio;
  }

  public Date getInicio() {
    return inicio;
  }

  public void setTermino(Date termino) {
    this.termino = termino;
  }

  public Date getTermino() {
    return termino;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
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
  	return getIdNominaPeriodo();
  }

  @Override
  public void setKey(Long key) {
  	this.idNominaPeriodo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getCorte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaPeriodo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getInicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTermino());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
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
		regresar.put("corte", getCorte());
		regresar.put("idNominaPeriodo", getIdNominaPeriodo());
		regresar.put("inicio", getInicio());
		regresar.put("termino", getTermino());
		regresar.put("orden", getOrden());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getCorte(), getIdNominaPeriodo(), getInicio(), getTermino(), getOrden(), getEjercicio(), getRegistro()
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
    regresar.append("idNominaPeriodo~");
    regresar.append(getIdNominaPeriodo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNominaPeriodo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNominasPeriodosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNominaPeriodo()!= null && getIdNominaPeriodo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNominasPeriodosDto other = (TcKeetNominasPeriodosDto) obj;
    if (getIdNominaPeriodo() != other.idNominaPeriodo && (getIdNominaPeriodo() == null || !getIdNominaPeriodo().equals(other.idNominaPeriodo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNominaPeriodo() != null ? getIdNominaPeriodo().hashCode() : 0);
    return hash;
  }

}


