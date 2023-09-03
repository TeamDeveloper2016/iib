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
@Table(name="tc_kalan_gastos_bitacora")
public class TcKalanGastosBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_gasto_bitacora")
  private Long idGastoBitacora;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_gasto_estatus")
  private Long idGastoEstatus;
  @Column (name="id_empresa_gasto")
  private Long idEmpresaGasto;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanGastosBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKalanGastosBitacoraDto(Long key) {
    this(null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKalanGastosBitacoraDto(Long idUsuario, Long idGastoBitacora, String justificacion, Long idGastoEstatus, Long idEmpresaGasto) {
    setIdUsuario(idUsuario);
    setIdGastoBitacora(idGastoBitacora);
    setJustificacion(justificacion);
    setIdGastoEstatus(idGastoEstatus);
    setIdEmpresaGasto(idEmpresaGasto);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdGastoBitacora(Long idGastoBitacora) {
    this.idGastoBitacora = idGastoBitacora;
  }

  public Long getIdGastoBitacora() {
    return idGastoBitacora;
  }

  public void setJustificacion(String justificacion) {
    this.justificacion = justificacion;
  }

  public String getJustificacion() {
    return justificacion;
  }

  public void setIdGastoEstatus(Long idGastoEstatus) {
    this.idGastoEstatus = idGastoEstatus;
  }

  public Long getIdGastoEstatus() {
    return idGastoEstatus;
  }

  public void setIdEmpresaGasto(Long idEmpresaGasto) {
    this.idEmpresaGasto = idEmpresaGasto;
  }

  public Long getIdEmpresaGasto() {
    return idEmpresaGasto;
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
  	return getIdGastoBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idGastoBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGastoBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGastoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaGasto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idGastoBitacora", getIdGastoBitacora());
		regresar.put("justificacion", getJustificacion());
		regresar.put("idGastoEstatus", getIdGastoEstatus());
		regresar.put("idEmpresaGasto", getIdEmpresaGasto());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdUsuario(), getIdGastoBitacora(), getJustificacion(), getIdGastoEstatus(), getIdEmpresaGasto(), getRegistro()
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
    regresar.append("idGastoBitacora~");
    regresar.append(getIdGastoBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdGastoBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanGastosBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdGastoBitacora()!= null && getIdGastoBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanGastosBitacoraDto other = (TcKalanGastosBitacoraDto) obj;
    if (getIdGastoBitacora() != other.idGastoBitacora && (getIdGastoBitacora() == null || !getIdGastoBitacora().equals(other.idGastoBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdGastoBitacora() != null ? getIdGastoBitacora().hashCode() : 0);
    return hash;
  }

}


