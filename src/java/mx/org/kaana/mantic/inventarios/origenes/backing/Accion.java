package mx.org.kaana.mantic.inventarios.origenes.backing;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEntrada;
import org.primefaces.event.TabChangeEvent;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 01/06/2024
 *@time 23:19:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticInventariosOrigenesAccion")
@ViewScoped
public class Accion extends mx.org.kaana.mantic.inventarios.entradas.backing.Accion implements IBaseStorage, Serializable {

  private static final long serialVersionUID= 327393488565639361L;
  
  @Override
  protected void toNewCosto() {
    
  }
  
  @Override
	public void doPrepare() {
    
	}
 
}