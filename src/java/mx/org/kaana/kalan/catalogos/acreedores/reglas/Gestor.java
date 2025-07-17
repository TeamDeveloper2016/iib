package mx.org.kaana.kalan.catalogos.acreedores.reglas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.clientes.beans.Domicilio;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Gestor implements Serializable {

  private static final Log LOG = LogFactory.getLog(Gestor.class);
  private static final long serialVersionUID = 4918891922274546780L;

  private List<UISelectEntity> tiposAcreedores;
  private List<UISelectEntity> entidades;
  private List<UISelectEntity> municipios;
  private List<UISelectEntity> localidades;
  private List<UISelectEntity> detalleCalles;
  private List<Domicilio> direcciones;

  public Gestor() {
    this.tiposAcreedores = new ArrayList();
    this.entidades = new ArrayList();
    this.municipios = new ArrayList();
    this.localidades = new ArrayList();
    this.detalleCalles = new ArrayList();
    this.direcciones = new ArrayList();
  }

  public List<UISelectEntity> getEntidades() {
    return entidades;
  }

  public List<UISelectEntity> getMunicipios() {
    return municipios;
  }

  public List<UISelectEntity> getLocalidades() {
    return localidades;
  }

  public List<UISelectEntity> getDetalleCalles() {
    return detalleCalles;
  }

  public List<UISelectEntity> getTiposAcreedores() {
    return tiposAcreedores;
  }

  public List<Domicilio> getDirecciones() {
    return direcciones;
  }

  public void loadTiposAcreedores() throws Exception {
    try {
      this.loadTiposAcreedores(true);
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
  }

  public void loadTiposAcreedores(boolean incluyeItemTodos) throws Exception {
    Entity entityDefault      = new Entity();
    List<Columna> formatos    = new ArrayList<>();
    Map<String, Object> params= new HashMap();
    try {
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      if (incluyeItemTodos) {
        entityDefault.put("idKey", new Value("idKey", -1L, "id_key"));
        entityDefault.put("nombre", new Value("nombre", "TODOS", "nombre"));
        this.tiposAcreedores.add(0, new UISelectEntity(entityDefault));
      } // if
      formatos.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      formatos.add(new Columna("dias", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
      this.tiposAcreedores.addAll(UIEntity.build("TcManticTiposAcreedoresDto", params, formatos));
    } // try
    catch (Exception e) {
      throw e;
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(formatos);
    } // finally
  }

}
