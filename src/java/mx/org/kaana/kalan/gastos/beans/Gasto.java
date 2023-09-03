package mx.org.kaana.kalan.gastos.beans;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mx.org.kaana.kalan.db.dto.TcKalanEmpresasGastosDto;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 1/09/2023
 *@time 01:36:29 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Gasto extends TcKalanEmpresasGastosDto implements Serializable {

  private static final long serialVersionUID = 2117388011559360170L;
  
  private Cheque documento;
	private UISelectEntity ikProveedor;
  private List<Parcialidad> parcialidades;

  public Gasto() {
    this(-1L);
  }

  public Gasto(Long key) {
    super(key);
    this.init();
  }

  public Gasto(Long idGastoClasificacion, Long idGastoComprobante, Double ivaCalculado, Long idActivoIeps, Date fechaAplicacion, Double total, Long idEmpresaCuenta, Double iva, Date fechaReferencia, Long idProveedor, Double ivaRetenido, Long idActivoCheque, Double importe, Long pagos, Long idGastoSubclasificacion, Double ieps, Long idUsuario, Double subtotal, String concepto, Double iepsCalculado, String observaciones, Long idEmpresa, Long idEmpresaGasto, String referencia, Long idActivoProrratear, Long idGastoEstatus, String consecutivo, Long ejercicio, Long orden) {
    super(idGastoClasificacion, idGastoComprobante, ivaCalculado, idActivoIeps, fechaAplicacion, total, idEmpresaCuenta, iva, fechaReferencia, idProveedor, ivaRetenido, idActivoCheque, importe, pagos, idGastoSubclasificacion, ieps, idUsuario, subtotal, concepto, iepsCalculado, observaciones, idEmpresa, idEmpresaGasto, referencia, idActivoProrratear, idGastoEstatus, consecutivo, ejercicio, orden);
    this.init();
  }
  
  public Cheque getDocumento() {
    return documento;
  }

  public void setDocumento(Cheque documento) {
    this.documento = documento;
  }

  public UISelectEntity getIkProveedor() {
    return ikProveedor;
  }

  public void setIkProveedor(UISelectEntity ikProveedor) {
    this.ikProveedor = ikProveedor;
    if(!Objects.equals(ikProveedor, null))
      this.setIdProveedor(ikProveedor.getKey());
  }
  
  public void setIEPS(Boolean value) {
    this.setIdActivoIeps(value? 1L: 2L);
  }

  public List<Parcialidad> getParcialidades() {
    return parcialidades;
  }

  public void setParcialidades(List<Parcialidad> parcialidades) {
    this.parcialidades = parcialidades;
  }
  
  public Boolean getIEPS() {
    return !Objects.equals(this.getIdActivoIeps(), null) && Objects.equals(this.getIdActivoIeps(), 1L);
  }
  
  public void setCheque(Boolean value) {
    this.setIdActivoCheque(value? 1L: 2L);
  }
  
  public Boolean getCheque() {
    return !Objects.equals(this.getIdActivoCheque(), null) && Objects.equals(this.getIdActivoCheque(), 1L);
  }
  
  public void setProrratear(Boolean value) {
    this.setIdActivoProrratear(value? 1L: 2L);
  }
  
  public Boolean getProrratear() {
    return !Objects.equals(this.getIdActivoProrratear(), null) && Objects.equals(this.getIdActivoProrratear(), 1L);
  }
  
  public Parcialidad clon() {
    return this.clon(1L);
  }
  
  private void init() {
    this.documento= new Cheque();
    this.parcialidades= new ArrayList<>();
    this.setIdUsuario(JsfBase.getIdUsuario());
    this.documento.setIdUsuario(JsfBase.getIdUsuario());
    this.setIdGastoEstatus(1L);
  }
  
  public Parcialidad clon(Long pagos) {
    Parcialidad regresar= new Parcialidad(
      this.getIdGastoClasificacion(), // Long idGastoClasificacion, 
      this.getIdGastoComprobante(), //  Long idGastoComprobante, 
      Numero.toRedondear(this.getIvaCalculado()/ pagos), // Double ivaCalculado, 
      this.getIdActivoIeps(), // Long idActivoIeps, 
      this.getFechaAplicacion(), //  Date fechaAplicacion, 
      Numero.toRedondear(this.getTotal()/ pagos), // Double total, 
      this.getIdEmpresaCuenta(), // Long idEmpresaCuenta, 
      this.getIva(), // Double iva, 
      this.getFechaReferencia(), // Date fechaReferencia, 
      this.getIdProveedor(), // Long idProveedor, 
      Numero.toRedondear(this.getIvaRetenido()/ pagos), // Double ivaRetenido, 
      this.getIdActivoCheque(), // Long idActivoCheque, 
      Numero.toRedondear(this.getImporte()/ pagos), // Double importe, 
      1L, // Long pagos, 
      this.getIdGastoSubclasificacion(), // Long idGastoSubclasificacion, 
      this.getIeps(), // Double ieps, 
      this.getIdUsuario(), // Long idUsuario, 
      Numero.toRedondear(this.getSubtotal()/ pagos), // Double subtotal,
      this.getConcepto(), // String concepto, 
      Numero.toRedondear(this.getIepsCalculado()/ pagos), // Double iepsCalculado, 
      this.getObservaciones(), // String observaciones, 
      this.getIdEmpresa(), // Long idEmpresa, 
      new Long((int)(Math.random()* -10000)), // Long idEmpresaGasto, 
      this.getReferencia(), // String referencia, 
      2L, // Long idActivoProrratear
      this.getIdGastoEstatus(), // Long idGastoEstatus, 
      this.getConsecutivo(), // String consecutivo, 
      this.getEjercicio(), // Long ejercicio, 
      this.getOrden() // Long orden      
    );
    regresar.setRegistro(this.getRegistro());
    return regresar;
  }
}
