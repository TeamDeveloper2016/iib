package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.sql.Date;
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
@Table(name="tc_keet_nominas_conceptos")
public class TcKeetNominasConceptosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="clave")
  private String clave;
  @Column (name="celda")
  private String celda;
  @Column (name="inicio")
  private Date inicio;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="id_tipo_concepto")
  private Long idTipoConcepto;
  @Column (name="id_activo")
  private Long idActivo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="aplicar")
  private String aplicar;
  @Column (name="formula")
  private String formula;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nomina_concepto")
  private Long idNominaConcepto;
  @Column (name="termino")
  private Timestamp termino;
  @Column (name="id_cargar")
  private Long idCargar;

  public TcKeetNominasConceptosDto() {
    this(new Long(-1L));
  }

  public TcKeetNominasConceptosDto(Long key) {
    this(null, null, null, new Date(Calendar.getInstance().getTimeInMillis()), null, null, null, null, null, null, new Long(-1L), new Timestamp(Calendar.getInstance().getTimeInMillis()), null);
    setKey(key);
  }

  public TcKeetNominasConceptosDto(String descripcion, String clave, String celda, Date inicio, String nombre, Long idTipoConcepto, Long idActivo, Long idUsuario, String aplicar, String formula, Long idNominaConcepto, Timestamp termino, Long idCargar) {
    setDescripcion(descripcion);
    setClave(clave);
    setCelda(celda);
    setInicio(inicio);
    setNombre(nombre);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setIdTipoConcepto(idTipoConcepto);
    setIdActivo(idActivo);
    setIdUsuario(idUsuario);
    setAplicar(aplicar);
    setFormula(formula);
    setIdNominaConcepto(idNominaConcepto);
    setTermino(termino);
    setIdCargar(idCargar);
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setCelda(String celda) {
    this.celda = celda;
  }

  public String getCelda() {
    return celda;
  }

  public void setInicio(Date inicio) {
    this.inicio = inicio;
  }

  public Date getInicio() {
    return inicio;
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

  public void setIdTipoConcepto(Long idTipoConcepto) {
    this.idTipoConcepto = idTipoConcepto;
  }

  public Long getIdTipoConcepto() {
    return idTipoConcepto;
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

  public void setAplicar(String aplicar) {
    this.aplicar = aplicar;
  }

  public String getAplicar() {
    return aplicar;
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

  public void setTermino(Timestamp termino) {
    this.termino = termino;
  }

  public Timestamp getTermino() {
    return termino;
  }

  public void setIdCargar(Long idCargar) {
    this.idCargar = idCargar;
  }

  public Long getIdCargar() {
    return idCargar;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdNominaConcepto();
  }

  @Override
  public void setKey(Long key) {
  	this.idNominaConcepto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCelda());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getInicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoConcepto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdActivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAplicar());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFormula());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaConcepto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTermino());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCargar());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("clave", getClave());
		regresar.put("celda", getCelda());
		regresar.put("inicio", getInicio());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
		regresar.put("idTipoConcepto", getIdTipoConcepto());
		regresar.put("idActivo", getIdActivo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("aplicar", getAplicar());
		regresar.put("formula", getFormula());
		regresar.put("idNominaConcepto", getIdNominaConcepto());
		regresar.put("termino", getTermino());
		regresar.put("idCargar", getIdCargar());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getDescripcion(), getClave(), getCelda(), getInicio(), getNombre(), getRegistro(), getIdTipoConcepto(), getIdActivo(), getIdUsuario(), getAplicar(), getFormula(), getIdNominaConcepto(), getTermino(), getIdCargar()
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
    regresar.append("idNominaConcepto~");
    regresar.append(getIdNominaConcepto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNominaConcepto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNominasConceptosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNominaConcepto()!= null && getIdNominaConcepto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNominasConceptosDto other = (TcKeetNominasConceptosDto) obj;
    if (getIdNominaConcepto() != other.idNominaConcepto && (getIdNominaConcepto() == null || !getIdNominaConcepto().equals(other.idNominaConcepto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNominaConcepto() != null ? getIdNominaConcepto().hashCode() : 0);
    return hash;
  }

}


