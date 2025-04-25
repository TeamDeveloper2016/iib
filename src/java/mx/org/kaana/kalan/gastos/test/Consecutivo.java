package mx.org.kaana.kalan.gastos.test;

import java.util.List;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Consecutivo {

  private static final Log LOG = LogFactory.getLog(Consecutivo.class);
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws Exception {
    Long idEmpresa    = -1L;
    int count         = 1;
    String consecutivo= "";
    List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet("select id_empresa_gasto, id_empresa, consecutivo, orden from tc_kalan_empresas_gastos order by id_empresa, id_empresa_gasto", Constantes.SQL_TODOS_REGISTROS);
    for (Entity item: items) {
      if(!Objects.equals(item.toLong("idEmpresa"), idEmpresa)) {
        idEmpresa= item.toLong("idEmpresa");
        count= 1;
      } // if
      consecutivo= "2025".concat(Cadena.rellenar(String.valueOf(count), 6, '0', Boolean.TRUE));
      if(!Objects.equals(item.toString("consecutivo"), consecutivo)) 
        DaoFactory.getInstance().execute("update tc_kalan_empresas_gastos set consecutivo= '".concat(consecutivo).concat("', orden= "+ count+ " where id_empresa_gasto= "+ item.toLong("idEmpresaGasto")));
//      if(count> 90)
//        break;
      count++;
    } // for
    LOG.info("Ok.");
  }

}
