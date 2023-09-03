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
@Table(name="tc_kalan_empresas_controles")
public class TcKalanEmpresasControlesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="secuencia")
  private Long secuencia;
  @Column (name="id_gasto_control")
  private Long idGastoControl;
  @Column (name="id_empresa_gasto")
  private Long idEmpresaGasto;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_empresa_control")
  private Long idEmpresaControl;
  @Column (name="registro")
  private Timestamp registro;

  public TcKalanEmpresasControlesDto() {
    this(new Long(-1L));
  }

  public TcKalanEmpresasControlesDto(Long key) {
    this(null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKalanEmpresasControlesDto(Long secuencia, Long idGastoControl, Long idEmpresaGasto, Long idEmpresaControl) {
    setSecuencia(secuencia);
    setIdGastoControl(idGastoControl);
    setIdEmpresaGasto(idEmpresaGasto);
    setIdEmpresaControl(idEmpresaControl);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setSecuencia(Long secuencia) {
    this.secuencia = secuencia;
  }

  public Long getSecuencia() {
    return secuencia;
  }

  public void setIdGastoControl(Long idGastoControl) {
    this.idGastoControl = idGastoControl;
  }

  public Long getIdGastoControl() {
    return idGastoControl;
  }

  public void setIdEmpresaGasto(Long idEmpresaGasto) {
    this.idEmpresaGasto = idEmpresaGasto;
  }

  public Long getIdEmpresaGasto() {
    return idEmpresaGasto;
  }

  public void setIdEmpresaControl(Long idEmpresaControl) {
    this.idEmpresaControl = idEmpresaControl;
  }

  public Long getIdEmpresaControl() {
    return idEmpresaControl;
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
  	return getIdEmpresaControl();
  }

  @Override
  public void setKey(Long key) {
  	this.idEmpresaControl = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getSecuencia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGastoControl());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaGasto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaControl());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("secuencia", getSecuencia());
		regresar.put("idGastoControl", getIdGastoControl());
		regresar.put("idEmpresaGasto", getIdEmpresaGasto());
		regresar.put("idEmpresaControl", getIdEmpresaControl());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getSecuencia(), getIdGastoControl(), getIdEmpresaGasto(), getIdEmpresaControl(), getRegistro()
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
    regresar.append("idEmpresaControl~");
    regresar.append(getIdEmpresaControl());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdEmpresaControl());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanEmpresasControlesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdEmpresaControl()!= null && getIdEmpresaControl()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanEmpresasControlesDto other = (TcKalanEmpresasControlesDto) obj;
    if (getIdEmpresaControl() != other.idEmpresaControl && (getIdEmpresaControl() == null || !getIdEmpresaControl().equals(other.idEmpresaControl))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdEmpresaControl() != null ? getIdEmpresaControl().hashCode() : 0);
    return hash;
  }

}


