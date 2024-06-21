package mx.org.kaana.mantic.inventarios.origenes.backing;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.inventarios.entradas.beans.Costo;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEntrada;

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
  public void doLoad() {
    try {
      super.doLoad();
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      switch (this.accion) {
        case AGREGAR:	
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIdNotaEstatus(1L);
          break;
      } // switch
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad
  
  @Override
  protected void toNewCosto(Costo item) {
  }
  
  @Override
	public void doPrepare() {
	}
 
}