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
@Table(name="tc_mantic_lotes_bitacora")
public class TcManticLotesBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_lote_estatus")
  private Long idLoteEstatus;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_lote")
  private Long idLote;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_lote_bitacora")
  private Long idLoteBitacora;
  @Column (name="registro")
  private Timestamp registro;

  public TcManticLotesBitacoraDto() {
    this(new Long(-1L));
  }

  public TcManticLotesBitacoraDto(Long key) {
    this(null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcManticLotesBitacoraDto(String justificacion, Long idLoteEstatus, Long idUsuario, Long idLote, Long idLoteBitacora) {
    setJustificacion(justificacion);
    setIdLoteEstatus(idLoteEstatus);
    setIdUsuario(idUsuario);
    setIdLote(idLote);
    setIdLoteBitacora(idLoteBitacora);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setJustificacion(String justificacion) {
    this.justificacion = justificacion;
  }

  public String getJustificacion() {
    return justificacion;
  }

  public void setIdLoteEstatus(Long idLoteEstatus) {
    this.idLoteEstatus = idLoteEstatus;
  }

  public Long getIdLoteEstatus() {
    return idLoteEstatus;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdLote(Long idLote) {
    this.idLote = idLote;
  }

  public Long getIdLote() {
    return idLote;
  }

  public void setIdLoteBitacora(Long idLoteBitacora) {
    this.idLoteBitacora = idLoteBitacora;
  }

  public Long getIdLoteBitacora() {
    return idLoteBitacora;
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
  	return getIdLoteBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idLoteBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLoteEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLoteBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("justificacion", getJustificacion());
		regresar.put("idLoteEstatus", getIdLoteEstatus());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idLote", getIdLote());
		regresar.put("idLoteBitacora", getIdLoteBitacora());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getJustificacion(), getIdLoteEstatus(), getIdUsuario(), getIdLote(), getIdLoteBitacora(), getRegistro()
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
    regresar.append("idLoteBitacora~");
    regresar.append(getIdLoteBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdLoteBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticLotesBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdLoteBitacora()!= null && getIdLoteBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticLotesBitacoraDto other = (TcManticLotesBitacoraDto) obj;
    if (getIdLoteBitacora() != other.idLoteBitacora && (getIdLoteBitacora() == null || !getIdLoteBitacora().equals(other.idLoteBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdLoteBitacora() != null ? getIdLoteBitacora().hashCode() : 0);
    return hash;
  }

}


