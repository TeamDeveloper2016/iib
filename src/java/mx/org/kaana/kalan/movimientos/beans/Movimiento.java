package mx.org.kaana.kalan.movimientos.beans;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;
import mx.org.kaana.kalan.db.dto.TcKalanEmpresasMovimientosDto;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 4/09/2023
 *@time 01:36:29 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Movimiento extends TcKalanEmpresasMovimientosDto implements Serializable {

  private static final long serialVersionUID = 1117388011559360170L;
  
	private UISelectEntity ikCliente;

  public Movimiento() {
    this(-1L);
  }

  public Movimiento(Long key) {
    super(key);
    this.init();
  }

  public Movimiento(Long idAnticipo, String justificacion, Long idCliente, Long idMovimientoEstatus, Long idEmpresaMovimiento, Long idBanco, Long ejercicio, String consecutivo, Long idTipoConcepto, Date fecha, Double total, Long idTipoMovimiento, Long idEmpresaCuenta, Long idUsuario, String observaciones, Long idEmpresa, Long orden) {
    super(idAnticipo, justificacion, idCliente, idMovimientoEstatus, idEmpresaMovimiento, idBanco, ejercicio, consecutivo, idTipoConcepto, fecha, total, idTipoMovimiento, idEmpresaCuenta, idUsuario, observaciones, idEmpresa, orden);
    this.init();
  }
  
  public UISelectEntity getIkCliente() {
    return ikCliente;
  }

  public void setIkCliente(UISelectEntity ikCliente) {
    this.ikCliente = ikCliente;
    if(!Objects.equals(ikCliente, null))
      this.setIdCliente(ikCliente.getKey());
  }
  
  public void setAnticipo(Boolean value) {
    this.setIdAnticipo(value? 1L: 2L);
  }

  public Boolean getAnticipo() {
    return !Objects.equals(this.getIdAnticipo(), null) && Objects.equals(this.getIdAnticipo(), 1L);
  }

  public Boolean getEstatus() {
    return !Objects.equals(this.getIdMovimientoEstatus(), null) && Objects.equals(this.getIdMovimientoEstatus(), 2L);
  }

  public void setEstatus(Boolean value) {
    this.setIdMovimientoEstatus(value? 2L: 1L);
  }
  
  private void init() {
    this.setIdUsuario(JsfBase.getIdUsuario());
    this.setIdMovimientoEstatus(1L);
  }
  
}
