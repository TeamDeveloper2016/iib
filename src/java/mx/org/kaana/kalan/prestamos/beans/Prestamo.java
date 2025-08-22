package mx.org.kaana.kalan.prestamos.beans;

import java.io.Serializable;
import mx.org.kaana.kalan.cuentas.beans.ICuenta;
import mx.org.kaana.kalan.cuentas.enums.EEstatusCuentas;
import mx.org.kaana.kalan.cuentas.enums.ETipoAfectacion;
import mx.org.kaana.kalan.db.dto.TcKalanPrestamosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 23/07/2025
 *@time 08:35:27 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Prestamo extends TcKalanPrestamosDto implements ICuenta, Serializable {

  private static final long serialVersionUID = -8794495402874168809L;

  private String empleado;
  private UISelectEntity ikEmpresa;  
  private UISelectEntity ikEmpresaCuenta;  
  private UISelectEntity ikEmpresaPersona;  
  private UISelectEntity ikTipoMedioPago;  
  
  public Prestamo() {
    this(-1L);
  }

  public Prestamo(Long key) {
    super(key);
    this.setIkEmpresa(new UISelectEntity(-1L));
    this.setIkEmpresaCuenta(new UISelectEntity(-1L));
    this.setIkEmpresaPersona(new UISelectEntity(-1L));
    this.setIkTipoMedioPago(new UISelectEntity(-1L));
  }
  
  public String getEmpleado() {
    return empleado;
  }

  public void setEmpleado(String empleado) {
    this.empleado = empleado;
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
  
  public UISelectEntity getIkEmpresaPersona() {
    return ikEmpresaPersona;
  }

  public void setIkEmpresaPersona(UISelectEntity ikEmpresaPersona) {
    this.ikEmpresaPersona = ikEmpresaPersona;
    if(ikEmpresaPersona!= null)
			this.setIdEmpresaPersona(ikEmpresaPersona.getKey());    
  }

  public UISelectEntity getIkTipoMedioPago() {
    return ikTipoMedioPago;
  }

  public void setIkTipoMedioPago(UISelectEntity ikTipoMedioPago) {
    this.ikTipoMedioPago = ikTipoMedioPago;
    if(ikTipoMedioPago!= null)
			this.setIdTipoMedioPago(ikTipoMedioPago.getKey());    
  }

  @Override
  public Long getIdTipoAfectacion() {
    return ETipoAfectacion.CARGO.getIdTipoAfectacion();
  }

  @Override
  public Long getIdCuentaEstatus() {
    return EEstatusCuentas.APLICADO.getIdEstatusCuenta();
  }
  
  @Override
  public Class toHbmClass() {
    return TcKalanPrestamosDto.class;
  }
  
}
