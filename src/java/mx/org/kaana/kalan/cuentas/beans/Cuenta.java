package mx.org.kaana.kalan.cuentas.beans;

import java.io.Serializable;
import java.sql.Date;
import mx.org.kaana.kalan.db.dto.TcKalanCuentasMovimientosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 23/07/2025
 *@time 08:35:27 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Cuenta extends TcKalanCuentasMovimientosDto implements ICuenta, Serializable {

  private static final long serialVersionUID = -8794495412874168809L;

  private UISelectEntity ikEmpresa;  
  private UISelectEntity ikEmpresaCuenta;  
  private UISelectEntity ikDestino;  
  private UISelectEntity ikDestinoCuenta;  
  private UISelectEntity ikTipoAfectacion;  
  private UISelectEntity ikTipoMedioPago;  

  private Long idDestino;
  private Long idDestinoCuenta;
  
  public Cuenta() {
    this(-1L);
  }

  public Cuenta(Long key) {
    super(key);
    this.setIkEmpresa(new UISelectEntity(-1L));
    this.setIkEmpresaCuenta(new UISelectEntity(-1L));
    this.setIkDestinoCuenta(new UISelectEntity(-1L));
    this.setIkTipoAfectacion(new UISelectEntity(-1L));
    this.setIkTipoMedioPago(new UISelectEntity(-1L));
  }

  public Cuenta(Long idTipoAfectacion, Long idTipoMedioPago, Double importe, Long ejercicio, Date fechaPago, String consecutivo, Date fechaAplicacion, Long idEmpresaCuenta, Long idUsuario, Long idEmpresaDestino, String observaciones, Long orden, Long idCuentaMovimiento, String referencia, Long idEmpresa, Long idCuentaEstatus, Long idCuentaOrigen, Long idPivote) {
    super(idTipoAfectacion, idTipoMedioPago, importe, ejercicio, fechaPago, consecutivo, fechaAplicacion, idEmpresaCuenta, idUsuario, idEmpresaDestino, observaciones, orden, idCuentaMovimiento, referencia, idEmpresa, idCuentaEstatus, idCuentaOrigen, idPivote);
    this.setIkEmpresa(new UISelectEntity(idEmpresa));
    this.setIkEmpresaCuenta(new UISelectEntity(idEmpresaCuenta));
    this.setIkDestino(new UISelectEntity(-1L));
    this.setIkDestinoCuenta(new UISelectEntity(-1L));
    this.setIkTipoAfectacion(new UISelectEntity(idTipoAfectacion));
    this.setIkTipoMedioPago(new UISelectEntity(idTipoMedioPago));
  }
  
  
  public UISelectEntity getIkEmpresa() {
    return ikEmpresa;
  }

  public void setIkEmpresa(UISelectEntity ikEmpresa) {
    this.ikEmpresa = ikEmpresa;
    if(ikEmpresa!= null)
			this.setIdEmpresa(ikEmpresa.getKey());    
  }

  public UISelectEntity getIkEmpresaCuenta() {
    return ikEmpresaCuenta;
  }

  public void setIkEmpresaCuenta(UISelectEntity ikEmpresaCuenta) {
    this.ikEmpresaCuenta = ikEmpresaCuenta;
    if(ikEmpresaCuenta!= null)
			this.setIdEmpresaCuenta(ikEmpresaCuenta.getKey());    
  }

  public UISelectEntity getIkDestino() {
    return ikDestino;
  }

  public void setIkDestino(UISelectEntity ikDestino) {
    this.ikDestino = ikDestino;
    if(ikDestino!= null)
			this.setIdDestino(ikDestino.getKey());    
  }

  public UISelectEntity getIkDestinoCuenta() {
    return ikDestinoCuenta;
  }

  public void setIkDestinoCuenta(UISelectEntity ikDestinoCuenta) {
    this.ikDestinoCuenta = ikDestinoCuenta;
    if(ikDestinoCuenta!= null)
			this.setIdDestinoCuenta(ikDestinoCuenta.getKey());    
  }
  
  public UISelectEntity getIkTipoAfectacion() {
    return ikTipoAfectacion;
  }

  public void setIkTipoAfectacion(UISelectEntity ikTipoAfectacion) {
    this.ikTipoAfectacion = ikTipoAfectacion;
    if(ikTipoAfectacion!= null)
			this.setIdTipoAfectacion(ikTipoAfectacion.getKey());    
  }

  public UISelectEntity getIkTipoMedioPago() {
    return ikTipoMedioPago;
  }

  public void setIkTipoMedioPago(UISelectEntity ikTipoMedioPago) {
    this.ikTipoMedioPago = ikTipoMedioPago;
    if(ikTipoMedioPago!= null)
			this.setIdTipoMedioPago(ikTipoMedioPago.getKey());    
  }

  public Long getIdDestino() {
    return idDestino;
  }

  public void setIdDestino(Long idDestino) {
    this.idDestino = idDestino;
  }

  public Long getIdDestinoCuenta() {
    return idDestinoCuenta;
  }

  public void setIdDestinoCuenta(Long idDestinoCuenta) {
    this.idDestinoCuenta = idDestinoCuenta;
  }

  @Override
  public Class toHbmClass() {
    return TcKalanCuentasMovimientosDto.class;
  }
  
  @Override
  public Cuenta clone() {
    Cuenta regresar= new Cuenta(
      2L, // Long idTipoAfectacion, 
      this.getIdTipoMedioPago(), // Long idTipoMedioPago, 
      this.getImporte(), // Double importe, 
      this.getEjercicio(), // Long ejercicio, 
      this.getFechaPago(), // Date fechaPago, 
      null, // String consecutivo, 
      this.getFechaAplicacion(), // Date fechaAplicacion, 
      this.getIdDestinoCuenta(), // Long idEmpresaCuenta, 
      this.getIdUsuario(), // Long idUsuario, 
      null, // Long idEmpresaDestino, 
      this.getObservaciones(), // String observaciones, 
      null, // Long orden, 
      -1L, // Long idCuentaMovimiento, 
      this.getReferencia(), // String referencia, 
      this.getIdDestino(), // Long idEmpresa, 
      this.getIdCuentaEstatus(), // Long idCuentaEstatus      
      this.getIdCuentaOrigen(), // Long idCuentaOrigen, 
      this.getIdPivote() // Long idPivote
    );
    return regresar;
  }
  
}
