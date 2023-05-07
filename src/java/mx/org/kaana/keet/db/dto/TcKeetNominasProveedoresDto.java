package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
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
@Table(name="tc_keet_nominas_proveedores")
public class TcKeetNominasProveedoresDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_proveedor")
  private Long idProveedor;
  @Column (name="total")
  private Double total;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nomina_proveedor")
  private Long idNominaProveedor;
  @Column (name="iva")
  private Double iva;
  @Column (name="subtotal")
  private Double subtotal;
  @Column (name="id_nomina")
  private Long idNomina;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="fondo_garantia")
  private Double fondoGarantia;
  @Column (name="destajo")
  private Double destajo;
  @Column (name="porcentaje_fondo")
  private Double porcentajeFondo;
  @Column (name="anticipo")
  private Double anticipo;

  public TcKeetNominasProveedoresDto() {
    this(new Long(-1L));
  }

  public TcKeetNominasProveedoresDto(Long key) {
    this(null, null, new Long(-1L), null, null, null, 0D, 0D, 0.3D, 0D);
    setKey(key);
  }

  public TcKeetNominasProveedoresDto(Long idProveedor, Double total, Long idNominaProveedor, Double iva, Double subtotal, Long idNomina, Double fondoGarantia, Double destajo, Double porcentajeFondo, Double anticipo) {
    setIdProveedor(idProveedor);
    setTotal(total);
    setIdNominaProveedor(idNominaProveedor);
    setIva(iva);
    setSubtotal(subtotal);
    setIdNomina(idNomina);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    this.fondoGarantia= fondoGarantia;
    this.destajo= destajo;
    this.porcentajeFondo= porcentajeFondo;
    this.anticipo= anticipo;
  }
	
  public void setIdProveedor(Long idProveedor) {
    this.idProveedor = idProveedor;
  }

  public Long getIdProveedor() {
    return idProveedor;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getTotal() {
    return total;
  }

  public void setIdNominaProveedor(Long idNominaProveedor) {
    this.idNominaProveedor = idNominaProveedor;
  }

  public Long getIdNominaProveedor() {
    return idNominaProveedor;
  }

  public void setIva(Double iva) {
    this.iva = iva;
  }

  public Double getIva() {
    return iva;
  }

  public void setSubtotal(Double subtotal) {
    this.subtotal = subtotal;
  }

  public Double getSubtotal() {
    return subtotal;
  }

  public void setIdNomina(Long idNomina) {
    this.idNomina = idNomina;
  }

  public Long getIdNomina() {
    return idNomina;
  }

  public void setRegistro(Timestamp registro) {
    this.registro = registro;
  }

  public Timestamp getRegistro() {
    return registro;
  }

  public Double getFondoGarantia() {
    return fondoGarantia;
  }

  public void setFondoGarantia(Double fondoGarantia) {
    this.fondoGarantia = fondoGarantia;
  }

  public Double getDestajo() {
    return destajo;
  }

  public void setDestajo(Double destajo) {
    this.destajo = destajo;
  }

  public Double getPorcentajeFondo() {
    return porcentajeFondo;
  }

  public void setPorcentajeFondo(Double porcentajeFondo) {
    this.porcentajeFondo = porcentajeFondo;
  }

  public Double getAnticipo() {
    return anticipo;
  }

  public void setAnticipo(Double anticipo) {
    this.anticipo = anticipo;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdNominaProveedor();
  }

  @Override
  public void setKey(Long key) {
  	this.idNominaProveedor = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIva());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubtotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFondoGarantia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDestajo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentajeFondo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAnticipo());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idProveedor", getIdProveedor());
		regresar.put("total", getTotal());
		regresar.put("idNominaProveedor", getIdNominaProveedor());
		regresar.put("iva", getIva());
		regresar.put("subtotal", getSubtotal());
		regresar.put("idNomina", getIdNomina());
		regresar.put("fondoGarantia", getFondoGarantia());
		regresar.put("destajo", getDestajo());
		regresar.put("porcentajeFondo", getPorcentajeFondo());
		regresar.put("registro", getRegistro());
		regresar.put("anticipo", getAnticipo());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getIdProveedor(), getTotal(), getIdNominaProveedor(), getIva(), getSubtotal(), getIdNomina(), getFondoGarantia(), getDestajo(), getPorcentajeFondo(), getRegistro(), getAnticipo()
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
    regresar.append("idNominaProveedor~");
    regresar.append(getIdNominaProveedor());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNominaProveedor());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNominasProveedoresDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNominaProveedor()!= null && getIdNominaProveedor()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) 
      return false;
    if (getClass() != obj.getClass()) 
      return false;
    final TcKeetNominasProveedoresDto other = (TcKeetNominasProveedoresDto) obj;
    if (getIdNominaProveedor() != other.idNominaProveedor && (getIdNominaProveedor() == null || !getIdNominaProveedor().equals(other.idNominaProveedor))) 
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNominaProveedor() != null ? getIdNominaProveedor().hashCode() : 0);
    return hash;
  }

}


