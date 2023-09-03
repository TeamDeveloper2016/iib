package mx.org.kaana.kalan.gastos.beans;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.kalan.db.dto.TcKalanEmpresasChequesDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 1/09/2023
 *@time 01:36:29 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Cheque extends TcKalanEmpresasChequesDto implements Serializable {

  private static final long serialVersionUID = -2120721680251562854L;

	private UISelectEntity ikProveedor;

  public UISelectEntity getIkProveedor() {
    return ikProveedor;
  }

  public void setIkProveedor(UISelectEntity ikProveedor) {
    this.ikProveedor = ikProveedor;
    if(!Objects.equals(ikProveedor, null))
      this.setIdProveedor(ikProveedor.getKey());
  }

  public void setProveedor(Boolean value) {
    this.setIdActivoProveedor(value? 1L: 2L);
  }
  
  public Boolean getProveedor() {
    return !Objects.equals(this.getIdActivoProveedor(), null) && Objects.equals(this.getIdActivoProveedor(), 1L);
  }

}
