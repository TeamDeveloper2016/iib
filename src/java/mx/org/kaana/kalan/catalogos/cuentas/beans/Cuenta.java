package mx.org.kaana.kalan.catalogos.cuentas.beans;

import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;
import mx.org.kaana.kalan.cuentas.beans.ICuenta;
import mx.org.kaana.kalan.cuentas.enums.EEstatusCuentas;
import mx.org.kaana.kalan.cuentas.enums.ETipoAfectacion;
import mx.org.kaana.kalan.db.dto.TcKalanEmpresasCuentasDto;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.mantic.enums.ETipoMediosPago;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 11/09/2025
 *@time 09:01:27 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Cuenta extends TcKalanEmpresasCuentasDto implements ICuenta, Serializable {

  private static final long serialVersionUID = -8194495412874168809L;

  private UISelectEntity ikEmpresa;  
  private Long idEstatusCuenta;

  public Cuenta() {
    this(-1L);
  }

  public Cuenta(Long key) {
    super(key);
    this.setIkEmpresa(new UISelectEntity(-1L));
    this.setIdUsuario(JsfBase.getIdUsuario());
    this.idEstatusCuenta= EEstatusCuentas.APLICADO.getIdEstatusCuenta();
  }

  public UISelectEntity getIkEmpresa() {
    return ikEmpresa;
  }

  public void setIkEmpresa(UISelectEntity ikEmpresa) {
    this.ikEmpresa = ikEmpresa;
    if(ikEmpresa!= null)
			this.setIdEmpresa(ikEmpresa.getKey());    
  }

  public Long getIdEstatusCuenta() {
    return idEstatusCuenta;
  }

  public void setIdEstatusCuenta(Long idEstatusCuenta) {
    this.idEstatusCuenta = idEstatusCuenta;
  }
  
  @Override
  public Long getIdTipoAfectacion() {
    return ETipoAfectacion.CARGO.getIdTipoAfectacion();
  }

  @Override
  public Long getIdTipoMedioPago() {
    return ETipoMediosPago.TRANSFERENCIA.getIdTipoMedioPago();
  }

  @Override
  public Date getFechaPago() {
    return new Date(Calendar.getInstance().getTimeInMillis());
  }

  @Override
  public Date getFechaAplicacion() {
    return new Date(Calendar.getInstance().getTimeInMillis());
  }

  @Override
  public String getReferencia() {
    return "SE APERTURO LA CUENTA";
  }

  @Override
  public String getObservaciones() {
    return "SE APERTURO LA CUENTA DE BANCO";
  }

  @Override
  public Long getIdCuentaEstatus() {
    return this.idEstatusCuenta;
  }

  @Override
  public Class toHbmClass() {
    return TcKalanEmpresasCuentasDto.class;
  }
  
}
