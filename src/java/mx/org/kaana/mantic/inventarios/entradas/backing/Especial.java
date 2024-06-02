package mx.org.kaana.mantic.inventarios.entradas.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.comun.IBaseStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 01/06/2024
 *@time 23:19:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticInventariosEntradasEspecial")
@ViewScoped
public class Especial extends Accion implements IBaseStorage, Serializable {

  private static final Log LOG = LogFactory.getLog(Especial.class);
  private static final long serialVersionUID= 327393418565639361L;
 
  @Override
  protected void toMoveData(UISelectEntity articulo, Integer index) throws Exception {
    super.toMoveData(articulo, index, Boolean.TRUE);
  }
  
}