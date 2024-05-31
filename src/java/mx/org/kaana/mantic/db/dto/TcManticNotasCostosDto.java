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
@Table(name="tc_mantic_notas_costos")
public class TcManticNotasCostosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_proveedor")
  private Long idProveedor;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nota_costo")
  private Long idNotaCosto;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_generar")
  private Long idGenerar;
  @Column (name="id_nota_entrada")
  private Long idNotaEntrada;
  @Column (name="importe")
  private Double importe;
  @Column (name="id_tipo_costo")
  private Long idTipoCosto;
  @Column (name="registro")
  private Timestamp registro;

  public TcManticNotasCostosDto() {
    this(new Long(-1L));
  }

  public TcManticNotasCostosDto(Long key) {
    this(null, null, new Long(-1L), null, null, null, null, null);
    setKey(key);
  }

  public TcManticNotasCostosDto(Long idProveedor, Long idUsuario, Long idNotaCosto, String observaciones, Long idGenerar, Long idNotaEntrada, Double importe, Long idTipoCosto) {
    setIdProveedor(idProveedor);
    setIdUsuario(idUsuario);
    setIdNotaCosto(idNotaCosto);
    setObservaciones(observaciones);
    setIdGenerar(idGenerar);
    setIdNotaEntrada(idNotaEntrada);
    setImporte(importe);
    setIdTipoCosto(idTipoCosto);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdProveedor(Long idProveedor) {
    this.idProveedor = idProveedor;
  }

  public Long getIdProveedor() {
    return idProveedor;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdNotaCosto(Long idNotaCosto) {
    this.idNotaCosto = idNotaCosto;
  }

  public Long getIdNotaCosto() {
    return idNotaCosto;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdGenerar(Long idGenerar) {
    this.idGenerar = idGenerar;
  }

  public Long getIdGenerar() {
    return idGenerar;
  }

  public void setIdNotaEntrada(Long idNotaEntrada) {
    this.idNotaEntrada = idNotaEntrada;
  }

  public Long getIdNotaEntrada() {
    return idNotaEntrada;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getImporte() {
    return importe;
  }

  public void setIdTipoCosto(Long idTipoCosto) {
    this.idTipoCosto = idTipoCosto;
  }

  public Long getIdTipoCosto() {
    return idTipoCosto;
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
  	return getIdNotaCosto();
  }

  @Override
  public void setKey(Long key) {
  	this.idNotaCosto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGenerar());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaEntrada());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idProveedor", getIdProveedor());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idNotaCosto", getIdNotaCosto());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idGenerar", getIdGenerar());
		regresar.put("idNotaEntrada", getIdNotaEntrada());
		regresar.put("importe", getImporte());
		regresar.put("idTipoCosto", getIdTipoCosto());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdProveedor(), getIdUsuario(), getIdNotaCosto(), getObservaciones(), getIdGenerar(), getIdNotaEntrada(), getImporte(), getIdTipoCosto(), getRegistro()
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
    regresar.append("idNotaCosto~");
    regresar.append(getIdNotaCosto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNotaCosto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticNotasCostosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNotaCosto()!= null && getIdNotaCosto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticNotasCostosDto other = (TcManticNotasCostosDto) obj;
    if (getIdNotaCosto() != other.idNotaCosto && (getIdNotaCosto() == null || !getIdNotaCosto().equals(other.idNotaCosto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNotaCosto() != null ? getIdNotaCosto().hashCode() : 0);
    return hash;
  }

}


