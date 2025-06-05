package mx.org.kaana.kalan.movimientos.backing;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;

@Named(value = "kalanMovimientosEgresos")
@ViewScoped
public class Egresos extends Filtro implements Serializable {

	private static final long serialVersionUID=1361701967796774746L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.idTipoMovimiento= 2L;
      this.titulo= " egreso extraordinario";
      this.pagina= "egresos";
      super.init();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch	
  } // init
  
}
