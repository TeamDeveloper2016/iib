package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Blob;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
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
@Table(name="tc_keet_bitacoras")
public class TcKeetBitacorasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_key")
  private Long idKey;
  @Column (name="despues")
  private String despues;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="proceso")
  private String proceso;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_bitacora")
  private Long idBitacora;
  @Column (name="tabla")
  private String tabla;
  @Column (name="campo")
  private String campo;
  @Column (name="antes")
  private String antes;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetBitacorasDto() {
    this(new Long(-1L));
  }

  public TcKeetBitacorasDto(Long key) {
    this(null, null, null, null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetBitacorasDto(Long idKey, String despues, Long idUsuario, String proceso, Long idBitacora, String tabla, String campo, String antes) {
    setIdKey(idKey);
    setDespues(despues);
    setIdUsuario(idUsuario);
    setProceso(proceso);
    setIdBitacora(idBitacora);
    setTabla(tabla);
    setCampo(campo);
    setAntes(antes);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdKey(Long idKey) {
    this.idKey = idKey;
  }

  public Long getIdKey() {
    return idKey;
  }

  public void setDespues(String despues) {
    this.despues = despues;
  }

  public String getDespues() {
    return despues;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setProceso(String proceso) {
    this.proceso = proceso;
  }

  public String getProceso() {
    return proceso;
  }

  public void setIdBitacora(Long idBitacora) {
    this.idBitacora = idBitacora;
  }

  public Long getIdBitacora() {
    return idBitacora;
  }

  public void setTabla(String tabla) {
    this.tabla = tabla;
  }

  public String getTabla() {
    return tabla;
  }

  public void setCampo(String campo) {
    this.campo = campo;
  }

  public String getCampo() {
    return campo;
  }

  public void setAntes(String antes) {
    this.antes = antes;
  }

  public String getAntes() {
    return antes;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdKey());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDespues());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getProceso());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTabla());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCampo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAntes());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idKey", getIdKey());
		regresar.put("despues", getDespues());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("proceso", getProceso());
		regresar.put("idBitacora", getIdBitacora());
		regresar.put("tabla", getTabla());
		regresar.put("campo", getCampo());
		regresar.put("antes", getAntes());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdKey(), getDespues(), getIdUsuario(), getProceso(), getIdBitacora(), getTabla(), getCampo(), getAntes(), getRegistro()
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
    regresar.append("idBitacora~");
    regresar.append(getIdBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetBitacorasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdBitacora()!= null && getIdBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetBitacorasDto other = (TcKeetBitacorasDto) obj;
    if (getIdBitacora() != other.idBitacora && (getIdBitacora() == null || !getIdBitacora().equals(other.idBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdBitacora() != null ? getIdBitacora().hashCode() : 0);
    return hash;
  }
}
