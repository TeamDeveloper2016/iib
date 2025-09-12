package mx.org.kaana.kalan.gastos.beans;

import java.io.Serializable;
import java.sql.Date;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kalan.cuentas.beans.ICuenta;
import mx.org.kaana.kalan.cuentas.enums.EEstatusCuentas;
import mx.org.kaana.kalan.cuentas.enums.ETipoAfectacion;
import mx.org.kaana.kalan.db.dto.TcKalanEmpresasGastosDto;
import mx.org.kaana.libs.formato.Numero;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 1/09/2023
 *@time 01:36:29 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Parcialidad extends TcKalanEmpresasGastosDto implements ICuenta, Serializable {

  private static final long serialVersionUID = 8615932969972480092L;

  private Long idEstatusCuenta;
  
  private ESql sql;
  
  public Parcialidad() {
    this(-1L);
  }

  public Parcialidad(Long key) {
    super(key);
    this.sql= ESql.INSERT;
    this.idEstatusCuenta= EEstatusCuentas.APLICADO.getIdEstatusCuenta();
  }

  public Parcialidad(Long idGastoClasificacion, Long idGastoComprobante, Double ivaCalculado, Long idActivoIeps, Date fechaAplicacion, Double total, Long idEmpresaCuenta, Double iva, Date fechaReferencia, Long idProveedor, Double ivaRetenido, Long idActivoCheque, Double importe, Long pago, Long pagos, Long idGastoSubclasificacion, Double ieps, Long idUsuario, Double subtotal, String concepto, Double iepsCalculado, String observaciones, Long idEmpresa, Long idEmpresaGasto, String referencia, Long idActivoProrratear, Long idGastoEstatus, String consecutivo, Long ejercicio, Long orden, Long idFuente, Long idTipoMedioPago) {
    super(idGastoClasificacion, idGastoComprobante, ivaCalculado, idActivoIeps, fechaAplicacion, total, idEmpresaCuenta, iva, fechaReferencia, idProveedor, ivaRetenido, idActivoCheque, importe, pago, pagos, idGastoSubclasificacion, ieps, idUsuario, subtotal, concepto, iepsCalculado, observaciones, idEmpresa, idEmpresaGasto, referencia, idActivoProrratear, 1L, consecutivo, ejercicio, orden, idFuente, idTipoMedioPago);
    this.sql= ESql.INSERT;
  }

  public ESql getSql() {
    return sql;
  }

  public void setSql(ESql sql) {
    this.sql = sql;
  }

  public void calculate(Parcialidad pago, Long pagos) {
    this.setSubtotal(Numero.toRedondear(pago.getSubtotal()/ pagos));
    this.setIvaCalculado(Numero.toRedondear(pago.getIvaCalculado()/ pagos));
    this.setImporte(Numero.toRedondear(pago.getImporte()/ pagos));
    this.setIvaRetenido(Numero.toRedondear(pago.getIvaRetenido()/ pagos));
    this.setIepsCalculado(Numero.toRedondear(pago.getIepsCalculado()/ pagos));
    this.setTotal(Numero.toRedondear(pago.getTotal()/ pagos));
  }  

  @Override
  public Long getIdTipoAfectacion() {
    return ETipoAfectacion.CARGO.getIdTipoAfectacion();
  }

  @Override
  public Date getFechaPago() {
    return this.getFechaReferencia();
  }

  @Override
  public Long getIdCuentaEstatus() {
    return idEstatusCuenta;
  }
  
}
