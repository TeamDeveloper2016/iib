package mx.org.kaana.kalan.cuentas.beans;

import java.sql.Date;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 23/07/2025
 *@time 08:35:27 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public interface ICuenta {
  
  public Long getKey();
  public Long getIdEmpresa();
  public Long getIdEmpresaCuenta();
  public Long getIdTipoAfectacion();
  public Long getIdTipoMedioPago();
  public Double getImporte();
  public Date getFechaPago();
  public Date getFechaAplicacion();
  public String getReferencia();
  public String getObservaciones();
  public Long getIdCuentaEstatus();
  public Long getIdUsuario();
  
}
