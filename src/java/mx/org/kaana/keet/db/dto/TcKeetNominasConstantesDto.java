package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
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
@Table(name="tc_keet_nominas_constantes")
public class TcKeetNominasConstantesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nomina_constante")
  private Long idNominaConstante;
  @Column (name="id_activo")
  private Long idActivo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="siglas")
  private String siglas;
  @Column (name="valor")
  private Double valor;
  @Column (name="inicio")
  private Date inicio;
  @Column (name="termino")
  private Date termino;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private Timestamp registro;

  public TcKeetNominasConstantesDto() {
    this(new Long(-1L));
  }

  public TcKeetNominasConstantesDto(Long key) {
    this(null, new Long(-1L), null, null, null, null, new Date(Calendar.getInstance().getTimeInMillis()), new Date(Calendar.getInstance().getTimeInMillis()), null);
    setKey(key);
  }

  public TcKeetNominasConstantesDto(String descripcion, Long idNominaConstante, Long idActivo, Long idUsuario, String siglas, Double valor, Date inicio, Date termino, String nombre) {
    setDescripcion(descripcion);
    setIdNominaConstante(idNominaConstante);
    setIdActivo(idActivo);
    setIdUsuario(idUsuario);
    setSiglas(siglas);
    setValor(valor);
    setInicio(inicio);
    setTermino(termino);
    setNombre(nombre);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdNominaConstante(Long idNominaConstante) {
    this.idNominaConstante = idNominaConstante;
  }

  public Long getIdNominaConstante() {
    return idNominaConstante;
  }

  public void setIdActivo(Long idActivo) {
    this.idActivo = idActivo;
  }

  public Long getIdActivo() {
    return idActivo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setSiglas(String siglas) {
    this.siglas = siglas;
  }

  public String getSiglas() {
    return siglas;
  }

  public void setValor(Double valor) {
    this.valor = valor;
  }

  public Double getValor() {
    return valor;
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

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
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
  	return getIdNominaConstante();
  }

  @Override
  public void setKey(Long key) {
  	this.idNominaConstante = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaConstante());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdActivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSiglas());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getValor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getInicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTermino());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("idNominaConstante", getIdNominaConstante());
		regresar.put("idActivo", getIdActivo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("siglas", getSiglas());
		regresar.put("valor", getValor());
		regresar.put("inicio", getInicio());
		regresar.put("termino", getTermino());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdNominaConstante(), getIdActivo(), getIdUsuario(), getSiglas(), getValor(), getInicio(), getTermino(), getNombre(), getRegistro()
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
    regresar.append("idNominaConstante~");
    regresar.append(getIdNominaConstante());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNominaConstante());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNominasConstantesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNominaConstante()!= null && getIdNominaConstante()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNominasConstantesDto other = (TcKeetNominasConstantesDto) obj;
    if (getIdNominaConstante() != other.idNominaConstante && (getIdNominaConstante() == null || !getIdNominaConstante().equals(other.idNominaConstante))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNominaConstante() != null ? getIdNominaConstante().hashCode() : 0);
    return hash;
  }
  
}
