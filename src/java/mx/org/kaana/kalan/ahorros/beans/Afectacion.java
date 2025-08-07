package mx.org.kaana.kalan.ahorros.beans;

import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;
import java.util.Objects;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kalan.db.dto.TcKalanAhorrosPagosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 05/08/2025
 *@time 08:35:27 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Afectacion extends TcKalanAhorrosPagosDto implements Serializable {

  private static final long serialVersionUID = -8794495402874168801L;

  private String estatus;
  private String medios;
  private UISelectEntity ikEmpresa;  
  private UISelectEntity ikEmpresaCuenta;  
  private UISelectEntity ikTipoAfectacion;  
  private UISelectEntity ikTipoMedioPago;  
  private UISelectEntity ikBanco;  
  private ESql sql;

  public Afectacion() {
    this(0D, new Date(Calendar.getInstance().getTimeInMillis()));
  }
  
  public Afectacion(Double importe, Date fecha) {
    super(Long.valueOf((int)(Math.random()* -1000000)));
    this.setIkEmpresa(new UISelectEntity(-1L));
    this.setIkEmpresaCuenta(new UISelectEntity(-1L));
    this.setIkTipoAfectacion(new UISelectEntity(-1L));
    this.setIkTipoMedioPago(new UISelectEntity(-1L));
    this.setIkBanco(new UISelectEntity(-1L));
    this.setImporte(importe);
    this.setIdAhorroControl(1L);
    this.setFechaAplicacion(fecha);
    this.setFechaPago(fecha);
    this.setMedios("EFECTIVO");
    this.setEstatus("PROGRAMADO");
    this.setSql(ESql.INSERT);
  }

  public String getEstatus() {
    return estatus;
  }

  public void setEstatus(String estatus) {
    this.estatus = estatus;
  }

  public String getMedios() {
    return medios;
  }

  public void setMedios(String medios) {
    this.medios = medios;
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

  public ESql getSql() {
    return sql;
  }

  public void setSql(ESql sql) {
    this.sql = sql;
  }

  public Boolean getIsEdit() {
    return Objects.equals(ESql.SELECT, this.getSql()) || Objects.equals(ESql.DELETE, this.getSql()) || Objects.equals(2L, this.getIdAhorroControl()) ||  Objects.equals(3L, this.getIdAhorroControl());
  }

  public void update(Double importe, Date fecha) {
    this.setImporte(importe);
    this.setFechaAplicacion(fecha);
    this.setFechaPago(fecha);
    if(Objects.equals(this.getIdAhorroControl(), 1L) && (Objects.equals(this.getSql(), ESql.DELETE) || Objects.equals(this.getSql(), ESql.SELECT)))
      this.setSql(ESql.UPDATE); 
  }  
  
  @Override
  public Class toHbmClass() {
    return TcKalanAhorrosPagosDto.class;
  }
  
}
