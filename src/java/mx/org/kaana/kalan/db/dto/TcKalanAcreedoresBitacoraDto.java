package mx.org.kaana.kalan.db.dto;

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
@Table(name="tc_kalan_acreedores_bitacora")
public class TcKalanAcreedoresBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_acreedor_bitacora")
  private Long idAcreedorBitacora;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="obervaciones")
  private String obervaciones;
  @Column (name="id_acreedor")
  private Long idAcreedor;
  @Column (name="id_acreedor_estatus")
  private Long idAcreedorEstatus;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanAcreedoresBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKalanAcreedoresBitacoraDto(Long key) {
    this(new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKalanAcreedoresBitacoraDto(Long idAcreedorBitacora, Long idUsuario, String obervaciones, Long idAcreedor, Long idAcreedorEstatus) {
    setIdAcreedorBitacora(idAcreedorBitacora);
    setIdUsuario(idUsuario);
    setObervaciones(obervaciones);
    setIdAcreedor(idAcreedor);
    setIdAcreedorEstatus(idAcreedorEstatus);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdAcreedorBitacora(Long idAcreedorBitacora) {
    this.idAcreedorBitacora = idAcreedorBitacora;
  }

  public Long getIdAcreedorBitacora() {
    return idAcreedorBitacora;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setObervaciones(String obervaciones) {
    this.obervaciones = obervaciones;
  }

  public String getObervaciones() {
    return obervaciones;
  }

  public void setIdAcreedor(Long idAcreedor) {
    this.idAcreedor = idAcreedor;
  }

  public Long getIdAcreedor() {
    return idAcreedor;
  }

  public void setIdAcreedorEstatus(Long idAcreedorEstatus) {
    this.idAcreedorEstatus = idAcreedorEstatus;
  }

  public Long getIdAcreedorEstatus() {
    return idAcreedorEstatus;
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
  	return getIdAcreedorBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idAcreedorBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdAcreedorBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObervaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAcreedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAcreedorEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idAcreedorBitacora", getIdAcreedorBitacora());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("obervaciones", getObervaciones());
		regresar.put("idAcreedor", getIdAcreedor());
		regresar.put("idAcreedorEstatus", getIdAcreedorEstatus());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdAcreedorBitacora(), getIdUsuario(), getObervaciones(), getIdAcreedor(), getIdAcreedorEstatus(), getRegistro()
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
    regresar.append("idAcreedorBitacora~");
    regresar.append(getIdAcreedorBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAcreedorBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanAcreedoresBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAcreedorBitacora()!= null && getIdAcreedorBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanAcreedoresBitacoraDto other = (TcKalanAcreedoresBitacoraDto) obj;
    if (getIdAcreedorBitacora() != other.idAcreedorBitacora && (getIdAcreedorBitacora() == null || !getIdAcreedorBitacora().equals(other.idAcreedorBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAcreedorBitacora() != null ? getIdAcreedorBitacora().hashCode() : 0);
    return hash;
  }

}


