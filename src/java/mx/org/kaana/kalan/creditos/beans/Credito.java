package mx.org.kaana.kalan.creditos.beans;


import java.io.Serializable;
import mx.org.kaana.kalan.db.dto.TcKalanCreditosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 23/07/2025
 *@time 08:35:27 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Credito extends TcKalanCreditosDto implements Serializable {

  private static final long serialVersionUID = -8794495402874168809L;

  private UISelectEntity ikEmpresa;  
  private UISelectEntity ikAcreedor;  

  public Credito() {
    super();
    this.setIkEmpresa(new UISelectEntity(-1L));
    this.setIkAcreedor(new UISelectEntity(-1L));
  }

  public UISelectEntity getIkEmpresa() {
    return ikEmpresa;
  }

  public void setIkEmpresa(UISelectEntity ikEmpresa) {
    this.ikEmpresa = ikEmpresa;
    if(ikEmpresa!= null)
			this.setIdEmpresa(ikEmpresa.getKey());    
  }
  
  
  public UISelectEntity getIkAcreedor() {
    return ikAcreedor;
  }

  public void setIkAcreedor(UISelectEntity ikAcreedor) {
    this.ikAcreedor = ikAcreedor;
    if(ikAcreedor!= null)
			this.setIdAcreedor(ikAcreedor.getKey());    
  }

  @Override
  public Class toHbmClass() {
    return TcKalanCreditosDto.class;
  }
  
}
