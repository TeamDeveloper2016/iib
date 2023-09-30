package mx.org.kaana.kalan.db.dto;

import java.io.Serializable;
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
@Table(name="tc_kalan_vales")
public class TcKalanValesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="id_solicito")
  private Long idSolicito;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_almacen")
  private Long idAlmacen;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_vale_estatus")
  private Long idValeEstatus;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="orden")
  private Long orden;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_vale")
  private Long idVale;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanValesDto() {
    this(new Long(-1L));
  }

  public TcKalanValesDto(Long key) {
    this(null, null, null, null, null, null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKalanValesDto(String consecutivo, Long idSolicito, Long idUsuario, Long idAlmacen, String observaciones, Long idValeEstatus, Long idEmpresa, Long orden, Long idVale, Long ejercicio) {
    setConsecutivo(consecutivo);
    setIdSolicito(idSolicito);
    setIdUsuario(idUsuario);
    setIdAlmacen(idAlmacen);
    setObservaciones(observaciones);
    setIdValeEstatus(idValeEstatus);
    setIdEmpresa(idEmpresa);
    setOrden(orden);
    setIdVale(idVale);
    setEjercicio(ejercicio);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setIdSolicito(Long idSolicito) {
    this.idSolicito = idSolicito;
  }

  public Long getIdSolicito() {
    return idSolicito;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdAlmacen(Long idAlmacen) {
    this.idAlmacen = idAlmacen;
  }

  public Long getIdAlmacen() {
    return idAlmacen;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdValeEstatus(Long idValeEstatus) {
    this.idValeEstatus = idValeEstatus;
  }

  public Long getIdValeEstatus() {
    return idValeEstatus;
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

  public void setIdVale(Long idVale) {
    this.idVale = idVale;
  }

  public Long getIdVale() {
    return idVale;
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
  	return getIdVale();
  }

  @Override
  public void setKey(Long key) {
  	this.idVale = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSolicito());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAlmacen());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdValeEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdVale());
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
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idSolicito", getIdSolicito());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idAlmacen", getIdAlmacen());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idValeEstatus", getIdValeEstatus());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("orden", getOrden());
		regresar.put("idVale", getIdVale());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getConsecutivo(), getIdSolicito(), getIdUsuario(), getIdAlmacen(), getObservaciones(), getIdValeEstatus(), getIdEmpresa(), getOrden(), getIdVale(), getEjercicio(), getRegistro()
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
    regresar.append("idVale~");
    regresar.append(getIdVale());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdVale());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanValesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdVale()!= null && getIdVale()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanValesDto other = (TcKalanValesDto) obj;
    if (getIdVale() != other.idVale && (getIdVale() == null || !getIdVale().equals(other.idVale))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdVale() != null ? getIdVale().hashCode() : 0);
    return hash;
  }

}


