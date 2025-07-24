package mx.org.kaana.kalan.prestamos.beans;

import java.io.Serializable;
import mx.org.kaana.kalan.db.dto.TcKalanPrestamosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 23/07/2025
 *@time 08:35:27 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Prestamo extends TcKalanPrestamosDto implements Serializable {

  private static final long serialVersionUID = -8794495402874168809L;

  private String empleado;
  private UISelectEntity ikEmpresaPersona;  

  public Prestamo() {
    super();
    this.setIkEmpresaPersona(new UISelectEntity(-1L));
  }

  public String getEmpleado() {
    return empleado;
  }

  public void setEmpleado(String empleado) {
    this.empleado = empleado;
  }

  public UISelectEntity getIkEmpresaPersona() {
    return ikEmpresaPersona;
  }

  public void setIkEmpresaPersona(UISelectEntity ikEmpresaPersona) {
    this.ikEmpresaPersona = ikEmpresaPersona;
    if(ikEmpresaPersona!= null)
			this.setIdEmpresaPersona(ikEmpresaPersona.getKey());    
  }

  @Override
  public Class toHbmClass() {
    return TcKalanPrestamosDto.class;
  }
  
}
