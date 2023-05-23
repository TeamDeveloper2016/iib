package mx.org.kaana.kalan.catalogos.servicios.backing;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;

@Named(value = "kalanCatalogosServiciosAccion")
@ViewScoped
public class Accion extends mx.org.kaana.mantic.catalogos.articulos.backing.Accion implements Serializable {

  private static final long serialVersionUID = 317393488565639367L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      super.init();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
}
