package mx.org.kaana.mantic.db.dto;

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
@Table(name="tc_mantic_lotes")
public class TcManticLotesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="original")
  private Double original;
  @Column (name="id_lote_tipo")
  private Long idLoteTipo;
  @Column (name="nombre")
  private String nombre;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_almacen")
  private Long idAlmacen;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_lote")
  private Long idLote;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="orden")
  private Long orden;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="id_lote_estatus")
  private Long idLoteEstatus;
  @Column (name="id_tipo_clase")
  private Long idTipoClase;

  public TcManticLotesDto() {
    this(new Long(-1L));
  }

  public TcManticLotesDto(Long key) {
    this(null, null, null, null, null, null, null, new Long(-1L), null, null, null, null, null, 1L, null);
    setKey(key);
  }

  public TcManticLotesDto(Double original, Long idLoteTipo, String nombre, Long ejercicio, String consecutivo, Long idUsuario, Long idAlmacen, Long idLote, String observaciones, Long idEmpresa, Double cantidad, Long orden, Long idArticulo, Long idLoteEstatus, Long idTipoClase) {
    setOriginal(original);
    setIdLoteTipo(idLoteTipo);
    setNombre(nombre);
    setEjercicio(ejercicio);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setConsecutivo(consecutivo);
    setIdUsuario(idUsuario);
    setIdAlmacen(idAlmacen);
    setIdLote(idLote);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setCantidad(cantidad);
    setOrden(orden);
    setIdArticulo(idArticulo);
    setIdLoteEstatus(idLoteEstatus);
    setIdTipoClase(idTipoClase);
  }
	
  public void setOriginal(Double original) {
    this.original = original;
  }

  public Double getOriginal() {
    return original;
  }

  public void setIdLoteTipo(Long idLoteTipo) {
    this.idLoteTipo = idLoteTipo;
  }

  public Long getIdLoteTipo() {
    return idLoteTipo;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
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

  public void setIdLote(Long idLote) {
    this.idLote = idLote;
  }

  public Long getIdLote() {
    return idLote;
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

  public void setCantidad(Double cantidad) {
    this.cantidad = cantidad;
  }

  public Double getCantidad() {
    return cantidad;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setIdArticulo(Long idArticulo) {
    this.idArticulo = idArticulo;
  }

  public Long getIdArticulo() {
    return idArticulo;
  }

  public Long getIdLoteEstatus() {
    return idLoteEstatus;
  }

  public void setIdLoteEstatus(Long idLoteEstatus) {
    this.idLoteEstatus = idLoteEstatus;
  }

  public Long getIdTipoClase() {
    return idTipoClase;
  }

  public void setIdTipoClase(Long idTipoClase) {
    this.idTipoClase = idTipoClase;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdLote();
  }

  @Override
  public void setKey(Long key) {
  	this.idLote = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getOriginal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLoteTipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAlmacen());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLoteEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoClase());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("original", getOriginal());
		regresar.put("idLoteTipo", getIdLoteTipo());
		regresar.put("nombre", getNombre());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idAlmacen", getIdAlmacen());
		regresar.put("idLote", getIdLote());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("cantidad", getCantidad());
		regresar.put("orden", getOrden());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("idLoteEstatus", getIdLoteEstatus());
		regresar.put("idTipoClase", getIdTipoClase());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getOriginal(), getIdLoteTipo(), getNombre(), getEjercicio(), getRegistro(), getConsecutivo(), getIdUsuario(), getIdAlmacen(), getIdLote(), getObservaciones(), getIdEmpresa(), getCantidad(), getOrden(), getIdArticulo(), getIdLoteEstatus(), getIdTipoClase()
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
    regresar.append("idLote~");
    regresar.append(getIdLote());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdLote());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticLotesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdLote()!= null && getIdLote()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticLotesDto other = (TcManticLotesDto) obj;
    if (getIdLote() != other.idLote && (getIdLote() == null || !getIdLote().equals(other.idLote))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdLote() != null ? getIdLote().hashCode() : 0);
    return hash;
  }

}


