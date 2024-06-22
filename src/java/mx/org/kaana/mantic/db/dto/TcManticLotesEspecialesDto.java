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
@Table(name="tc_mantic_lotes_especiales")
public class TcManticLotesEspecialesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_lote")
  private Long idLote;
  @Column (name="id_lote_detalle")
  private Long idLoteDetalle;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_lote_especial")
  private Long idLoteEspecial;
  @Column (name="registro")
  private Timestamp registro;

  public TcManticLotesEspecialesDto() {
    this(new Long(-1L));
  }

  public TcManticLotesEspecialesDto(Long key) {
    this(null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcManticLotesEspecialesDto(Long idUsuario, Long idLote, Long idLoteDetalle, Long idLoteEspecial) {
    setIdUsuario(idUsuario);
    setIdLote(idLote);
    setIdLoteDetalle(idLoteDetalle);
    setIdLoteEspecial(idLoteEspecial);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
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

  public void setIdLoteDetalle(Long idLoteDetalle) {
    this.idLoteDetalle = idLoteDetalle;
  }

  public Long getIdLoteDetalle() {
    return idLoteDetalle;
  }

  public void setIdLoteEspecial(Long idLoteEspecial) {
    this.idLoteEspecial = idLoteEspecial;
  }

  public Long getIdLoteEspecial() {
    return idLoteEspecial;
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
  	return getIdLoteEspecial();
  }

  @Override
  public void setKey(Long key) {
  	this.idLoteEspecial = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLoteDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLoteEspecial());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idLote", getIdLote());
		regresar.put("idLoteDetalle", getIdLoteDetalle());
		regresar.put("idLoteEspecial", getIdLoteEspecial());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdUsuario(), getIdLote(), getIdLoteDetalle(), getIdLoteEspecial(), getRegistro()
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
    regresar.append("idLoteEspecial~");
    regresar.append(getIdLoteEspecial());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdLoteEspecial());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticLotesEspecialesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdLoteEspecial()!= null && getIdLoteEspecial()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticLotesEspecialesDto other = (TcManticLotesEspecialesDto) obj;
    if (getIdLoteEspecial() != other.idLoteEspecial && (getIdLoteEspecial() == null || !getIdLoteEspecial().equals(other.idLoteEspecial))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdLoteEspecial() != null ? getIdLoteEspecial().hashCode() : 0);
    return hash;
  }

}


