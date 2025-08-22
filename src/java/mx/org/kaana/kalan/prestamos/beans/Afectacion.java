package mx.org.kaana.kalan.prestamos.beans;

import java.io.Serializable;
import mx.org.kaana.kalan.cuentas.beans.ICuenta;
import mx.org.kaana.kalan.cuentas.enums.EEstatusCuentas;
import mx.org.kaana.kalan.db.dto.TcKalanPrestamosPagosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 23/07/2025
 *@time 08:35:27 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Afectacion extends TcKalanPrestamosPagosDto implements ICuenta, Serializable {

  private static final long serialVersionUID = -8794495402874168801L;

  private UISelectEntity ikEmpresa;  
  private UISelectEntity ikEmpresaCuenta;  
  private UISelectEntity ikTipoAfectacion;  
  private UISelectEntity ikTipoMedioPago;  
  private UISelectEntity ikBanco;  

  public Afectacion() {
    super();
    this.setIkEmpresa(new UISelectEntity(-1L));
    this.setIkEmpresaCuenta(new UISelectEntity(-1L));
    this.setIkTipoAfectacion(new UISelectEntity(-1L));
    this.setIkTipoMedioPago(new UISelectEntity(-1L));
    this.setIkBanco(new UISelectEntity(-1L));
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

  public UISelectEntity getIkBanco() {
    return ikBanco;
  }

  public void setIkBanco(UISelectEntity ikBanco) {
    this.ikBanco = ikBanco;
    if(ikBanco!= null)
			this.setIdBanco(ikBanco.getKey());    
  }

  @Override
  public Long getIdCuentaEstatus() {
    return EEstatusCuentas.APLICADO.getIdEstatusCuenta();
  }
  
  @Override
  public Class toHbmClass() {
    return TcKalanPrestamosPagosDto.class;
  }

}
