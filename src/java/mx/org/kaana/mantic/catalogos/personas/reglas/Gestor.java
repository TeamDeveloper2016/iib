package mx.org.kaana.mantic.catalogos.personas.reglas;

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
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Gestor implements Serializable {

  private static final Log LOG = LogFactory.getLog(Gestor.class);
  private static final long serialVersionUID = 4918891922274546780L;

  private List<UISelectEntity> tiposPersonas;

  public Gestor() {
    this.tiposPersonas = new ArrayList();
  }

  public List<UISelectEntity> getTiposPersonas() {
    return tiposPersonas;
  }

  public void loadTiposPersonas() throws Exception {
    Entity entityDefault     = null;
    List<Columna> formatos   = null;
    Map<String,Object> params= new HashMap();
    try {
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      entityDefault = new Entity();
      formatos      = new ArrayList<>();
      entityDefault.put("idKey", new Value("idKey", -1L, "id_key"));
      entityDefault.put("nombre", new Value("nombre", "SELECCIONE", "nombre"));
      formatos.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));      
      this.tiposPersonas.add(0, new UISelectEntity(entityDefault));
      this.tiposPersonas.addAll(UIEntity.build("TcManticTiposPersonasDto",params, formatos));
    } // try
    catch (Exception e) {
      throw e;
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(formatos);
    } // finally
  }  
	
  public List<UISelectItem> loadPuestos() throws Exception {    
		List<UISelectItem> regresar= null;    
    Map<String,Object> params  = new HashMap();
    try {
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			regresar= UISelect.seleccione("TcManticPuestosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
    } // try
    catch (Exception e) {
      throw e;
    } // catch
    finally {
      Methods.clean(params);      
    } // finally
		return regresar;
  } // loadPuestos
	
  public List<UISelectItem> loadDepartamentos() throws Exception {    
		List<UISelectItem> regresar= null;    
    Map<String,Object> params  = new HashMap();
    try {
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			regresar= UISelect.seleccione("TcKeetDepartamentosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
    } // try
    catch (Exception e) {
      throw e;
    } // catch
    finally {
      Methods.clean(params);      
    } // finally
		return regresar;
  } // loadPuestos
	
  public List<UISelectItem> loadPuestosSimple() throws Exception {    
		List<UISelectItem> regresar= null;    
    Map<String,Object> params  = new HashMap();
    try {
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			regresar= UISelect.build("TcManticPuestosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
    } // try
    catch (Exception e) {
      throw e;
    } // catch
    finally {
      Methods.clean(params);      
    } // finally
		return regresar;
  } // loadPuestosSimple
	
  public List<UISelectItem> loadDepartamentosSimple(Long idTipoGasto) throws Exception {    
		List<UISelectItem> regresar= null;    
    Map<String,Object> params  = new HashMap();
    try {
      if(idTipoGasto> 0)
        params.put(Constantes.SQL_CONDICION, "id_tipo_gasto= "+ idTipoGasto);
      else
        params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			regresar= UISelect.build("TcKeetDepartamentosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
    } // try
    catch (Exception e) {
      throw e;
    } // catch
    finally {
      Methods.clean(params);      
    } // finally
		return regresar;
  } // loadDepartamentosSimple
  
  public List<UISelectItem> loadDepartamentosSimple() throws Exception {    
    return loadDepartamentosSimple(-1L);
  } // loadDepartamentosSimple
  
  public List<UISelectItem> loadTiposGastos() throws Exception {    
		List<UISelectItem> regresar= null;    
    Map<String,Object> params  = new HashMap();
    try {
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			regresar= UISelect.seleccione("TcKeetTiposGastosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
    } // try
    catch (Exception e) {
      throw e;
    } // catch
    finally {
      Methods.clean(params);      
    } // finally
		return regresar;
  } // loadTiposGastos
  
}
