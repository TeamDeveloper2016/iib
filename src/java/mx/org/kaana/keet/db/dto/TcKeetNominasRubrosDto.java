package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
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
@Table(name="tc_keet_nominas_rubros")
public class TcKeetNominasRubrosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="codigo")
  private String codigo;
  @Column (name="total")
  private Double total;
  @Column (name="id_nomina_proveedor")
  private Long idNominaProveedor;
  @Column (name="iva")
  private Double iva;
  @Column (name="subtotal")
  private Double subtotal;
  @Column (name="sat")
  private String sat;
  @Column (name="cantidad")
  private String cantidad;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nomina_rubro")
  private Long idNominaRubro;
  @Column (name="id_rubro")
  private Long idRubro;
  @Column (name="nombre")
  private String nombre;
  @Column (name="lote")
  private String lote;
  @Column (name="id_contrato_lote")
  private Long idContratoLote;
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

  public TcKeetNominasRubrosDto() {
    this(new Long(-1L));
  }

  public TcKeetNominasRubrosDto(Long key) {
    this(null, null, null, null, null, null, null, new Long(-1L), null, null, null, null, 0D, 0D, 0.3D, 0D);
    setKey(key);
  }

  public TcKeetNominasRubrosDto(String codigo, Double total, Long idNominaProveedor, Double iva, Double subtotal, String sat, String cantidad, Long idNominaRubro, Long idRubro, String nombre, String lote, Long idContratoLote, Double fondoGarantia, Double destajo, Double porcentajeFondo, Double anticipo) {
    setCodigo(codigo);
    setTotal(total);
    setIdNominaProveedor(idNominaProveedor);
    setIva(iva);
    setSubtotal(subtotal);
    setSat(sat);
    setCantidad(cantidad);
    setIdNominaRubro(idNominaRubro);
    setIdRubro(idRubro);
    setNombre(nombre);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		this.lote= lote;
		this.idContratoLote= idContratoLote;
    this.fondoGarantia= fondoGarantia;
    this.destajo= destajo;
    this.porcentajeFondo= porcentajeFondo;
    this.anticipo= anticipo;
  }
	
  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
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

  public void setSat(String sat) {
    this.sat = sat;
  }

  public String getSat() {
    return sat;
  }

  public void setCantidad(String cantidad) {
    this.cantidad = cantidad;
  }

  public String getCantidad() {
    return cantidad;
  }

  public void setIdNominaRubro(Long idNominaRubro) {
    this.idNominaRubro = idNominaRubro;
  }

  public Long getIdNominaRubro() {
    return idNominaRubro;
  }

  public void setIdRubro(Long idRubro) {
    this.idRubro = idRubro;
  }

  public Long getIdRubro() {
    return idRubro;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setRegistro(Timestamp registro) {
    this.registro = registro;
  }

  public Timestamp getRegistro() {
    return registro;
  }

	public String getLote() {
		return lote;
	}

	public void setLote(String lote) {
		this.lote=lote;
	}

	public Long getIdContratoLote() {
		return idContratoLote;
	}

	public void setIdContratoLote(Long idContratoLote) {
		this.idContratoLote=idContratoLote;
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
  	return getIdNominaRubro();
  }

  @Override
  public void setKey(Long key) {
  	this.idNominaRubro = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIva());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubtotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSat());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaRubro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRubro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFondoGarantia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDestajo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentajeFondo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAnticipo());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("codigo", getCodigo());
		regresar.put("total", getTotal());
		regresar.put("idNominaProveedor", getIdNominaProveedor());
		regresar.put("iva", getIva());
		regresar.put("subtotal", getSubtotal());
		regresar.put("sat", getSat());
		regresar.put("cantidad", getCantidad());
		regresar.put("idNominaRubro", getIdNominaRubro());
		regresar.put("idRubro", getIdRubro());
		regresar.put("nombre", getNombre());
		regresar.put("lote", getLote());
		regresar.put("idContratoLote()", getIdContratoLote());
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
      getCodigo(), getTotal(), getIdNominaProveedor(), getIva(), getSubtotal(), getSat(), getCantidad(), getIdNominaRubro(), getIdRubro(), getNombre(), getLote(), getIdContratoLote(), getFondoGarantia(), getDestajo(), getPorcentajeFondo(), getRegistro(), getAnticipo()
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
    regresar.append("idNominaRubro~");
    regresar.append(getIdNominaRubro());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNominaRubro());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNominasRubrosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNominaRubro()!= null && getIdNominaRubro()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNominasRubrosDto other = (TcKeetNominasRubrosDto) obj;
    if (getIdNominaRubro() != other.idNominaRubro && (getIdNominaRubro() == null || !getIdNominaRubro().equals(other.idNominaRubro))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNominaRubro() != null ? getIdNominaRubro().hashCode() : 0);
    return hash;
  }

}


