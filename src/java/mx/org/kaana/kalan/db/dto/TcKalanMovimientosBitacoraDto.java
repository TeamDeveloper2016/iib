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
@Table(name="tc_kalan_movimientos_bitacora")
public class TcKalanMovimientosBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_movimiento_bitacora")
  private Long idMovimientoBitacora;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_movimiento_estatus")
  private Long idMovimientoEstatus;
  @Column (name="id_empresa_movimiento")
  private Long idEmpresaMovimiento;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanMovimientosBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKalanMovimientosBitacoraDto(Long key) {
    this(new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKalanMovimientosBitacoraDto(Long idMovimientoBitacora, String justificacion, Long idUsuario, Long idMovimientoEstatus, Long idEmpresaMovimiento) {
    setIdMovimientoBitacora(idMovimientoBitacora);
    setJustificacion(justificacion);
    setIdUsuario(idUsuario);
    setIdMovimientoEstatus(idMovimientoEstatus);
    setIdEmpresaMovimiento(idEmpresaMovimiento);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdMovimientoBitacora(Long idMovimientoBitacora) {
    this.idMovimientoBitacora = idMovimientoBitacora;
  }

  public Long getIdMovimientoBitacora() {
    return idMovimientoBitacora;
  }

  public void setJustificacion(String justificacion) {
    this.justificacion = justificacion;
  }

  public String getJustificacion() {
    return justificacion;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdMovimientoEstatus(Long idMovimientoEstatus) {
    this.idMovimientoEstatus = idMovimientoEstatus;
  }

  public Long getIdMovimientoEstatus() {
    return idMovimientoEstatus;
  }

  public void setIdEmpresaMovimiento(Long idEmpresaMovimiento) {
    this.idEmpresaMovimiento = idEmpresaMovimiento;
  }

  public Long getIdEmpresaMovimiento() {
    return idEmpresaMovimiento;
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
  	return getIdMovimientoBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idMovimientoBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdMovimientoBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMovimientoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaMovimiento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idMovimientoBitacora", getIdMovimientoBitacora());
		regresar.put("justificacion", getJustificacion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idMovimientoEstatus", getIdMovimientoEstatus());
		regresar.put("idEmpresaMovimiento", getIdEmpresaMovimiento());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdMovimientoBitacora(), getJustificacion(), getIdUsuario(), getIdMovimientoEstatus(), getIdEmpresaMovimiento(), getRegistro()
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
    regresar.append("idMovimientoBitacora~");
    regresar.append(getIdMovimientoBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdMovimientoBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanMovimientosBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdMovimientoBitacora()!= null && getIdMovimientoBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanMovimientosBitacoraDto other = (TcKalanMovimientosBitacoraDto) obj;
    if (getIdMovimientoBitacora() != other.idMovimientoBitacora && (getIdMovimientoBitacora() == null || !getIdMovimientoBitacora().equals(other.idMovimientoBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdMovimientoBitacora() != null ? getIdMovimientoBitacora().hashCode() : 0);
    return hash;
  }

}


