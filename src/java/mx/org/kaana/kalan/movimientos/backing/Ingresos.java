package mx.org.kaana.kalan.movimientos.backing;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;

@Named(value = "kalanMovimientosIngresos")
@ViewScoped
public class Ingresos extends Filtro implements Serializable {

	private static final long serialVersionUID= 1361701967796774746L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.idTipoMovimiento= 1L;
      this.titulo= " ingreso sin factura";
      this.pagina= "ingresos";
      super.init();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch	
  } // init
  
}
