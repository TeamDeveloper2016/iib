package mx.org.kaana.keet.comun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 10/04/2020
 *@time 02:06:58 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public final class Catalogos {
	
	public static void toLoadResidentes(Map<String, Object> attrs) throws Exception {
		Map<String, Object> params= new HashMap<>();		
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
			attrs.put("departamentos", UISelect.seleccione("TcKeetDepartamentosDto", "residentes", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS));
		} // try
		finally {
			Methods.clean(params);
		} // finally		
	} // toLoadResidentes
  
	public static void toLoadEspecialidades(Map<String, Object> attrs) throws Exception {
		Map<String, Object> params= new HashMap<>();		
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
			attrs.put("departamentos", UISelect.seleccione("TcKeetDepartamentosDto", "especialidades", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS));
		} // try
		finally {
			Methods.clean(params);
		} // finally		
	} // toLoadEspecialidades

  public static void toLoadTiposGastos(Map<String, Object> attrs) throws Exception {    
    Map<String,Object> params= new HashMap();
    try {
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			attrs.put("tiposGastos", UISelect.seleccione("TcKeetTiposGastosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS));
    } // try
    catch (Exception e) {
      throw e;
    } // catch
    finally {
      Methods.clean(params);      
    } // finally
  } // toLoadTiposGastos
  
	public static void toLoadDepartamentos(Long idTipoGasto, Map<String, Object> attrs) throws Exception {
		Map<String, Object> params= new HashMap<>();		
		try {
      if(idTipoGasto> 0)
        params.put(Constantes.SQL_CONDICION, "id_tipo_gasto= "+ idTipoGasto);
      else
        params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			attrs.put("departamentos", UISelect.seleccione("TcKeetDepartamentosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS));
		} // try
		finally {
			Methods.clean(params);
		} // finally		
	} // toLoadDepartamentos
	
	public static void toLoadDepartamentos(Map<String, Object> attrs) throws Exception {
    toLoadDepartamentos(-1L, attrs);
  }

	public static void toLoadTodosDepartamentos(Map<String, Object> attrs) throws Exception {
		Map<String, Object> params= new HashMap<>();		
		try {
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			attrs.put("departamentos", UISelect.build("TcKeetDepartamentosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS));
		} // try
		finally {
			Methods.clean(params);
		} // finally		
	} // toLoadTodosDepartamentos
  
	public static void toLoadEjercicios(Map<String, Object> attrs) throws Exception {
		Map<String, Object> params      = null;		
		try {
			params= new HashMap<>();
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      attrs.put("ejercicios", UIEntity.seleccione("VistaNominaDto", "ejercicios", params, "ejercicio"));
      attrs.put("ejercicio", new UISelectEntity(-1L));
		} // try
		finally {
			Methods.clean(params);
		} // finally		
	} // toLoadEjercicios
	
	public static void toLoadNominas(Map<String, Object> attrs) throws Exception {
		List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= new HashMap<>();		
		try {
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put("anterior", Fecha.getAnioActual()- 1);
			List<UISelectEntity> nominas= UIEntity.seleccione("VistaNominaConsultasDto", "nominas", params, columns, "nomina");
      if(nominas!= null && !nominas.isEmpty()) {
        attrs.put("nominas", nominas);
				attrs.put("idNomina", UIBackingUtilities.toFirstKeySelectEntity(nominas));
			} // if
			else
			  attrs.put("idNomina", new UISelectEntity(-1L));
		} // try
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally		
	} // toLoadNominas
	
	public static void toLoadSemanas(Map<String, Object> attrs) throws Exception {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= new HashMap<>();		
    try {
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      attrs.put("semanas", UIEntity.seleccione("VistaNominaDto", "semanas", params, columns, "semana"));
      attrs.put("semana", new UISelectEntity(-1L));
		} // try
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally		
	} // toLoadSemanas
	
	public static void toLoadTiposNominas(Map<String, Object> attrs) throws Exception {
		Map<String, Object> params= new HashMap<>();		
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectEntity> items= UIEntity.seleccione("TcKeetTiposNominasDto", "row", params, "nombre"); 
      if(items!= null && !items.isEmpty()) {
			  attrs.put("tipos", items);
				attrs.put("idTipoNomina", UIBackingUtilities.toFirstKeySelectEntity(items));
			}
			else
			  attrs.put("idTipoNomina", new UISelectEntity(-1L));
		} // tryUISelectEntity
		finally {
			Methods.clean(params);
		} // finally		
	} // toLoadTiposNominas
	
	public static void toLoadPuestos(Map<String, Object> attrs) throws Exception {
		List<UISelectItem> puestos= null;
    Map<String, Object> params= new HashMap<>();
    try {
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      puestos = UISelect.seleccione("TcManticPuestosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
			if(!puestos.isEmpty()) {
				attrs.put("puestos", puestos);
				attrs.put("idPuesto", UIBackingUtilities.toFirstKeySelectItem(puestos));
			} // if
			else
			  attrs.put("idPuesto", new UISelectItem(-1L));
    } // try
    finally {
      Methods.clean(params);
    } // finally
	} // toLoadPuestos
	
	public static List<UISelectEntity> toContratistasPorElDia(boolean seleccione) throws Exception {
		List<Columna> columns        = new ArrayList<>();
		List<UISelectEntity> regresar= null;
		try {
			columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      if(seleccione) 
		    regresar= UIEntity.seleccione("VistaPersonasDto", "contratistas", Collections.EMPTY_MAP, columns, Constantes.SQL_TODOS_REGISTROS, "nombre");
      else
		    regresar= UIEntity.build("VistaPersonasDto", "contratistas", Collections.EMPTY_MAP, columns, Constantes.SQL_TODOS_REGISTROS);
      if(regresar!= null) {
        Entity sinContratista=  new Entity(999L);
        sinContratista.put("nombre", new Value("nombre", "POR EL DIA"));
        sinContratista.put("nombres", new Value("nombres", "SIN CONTRATISTA"));
        sinContratista.put("puesto", new Value("puesto", "POR EL DIA"));
        // sinContratista
        if(regresar.size()<= 0)
          regresar.add(new UISelectEntity(sinContratista));
        else
          if(seleccione)
            regresar.add(1, new UISelectEntity(sinContratista));
          else
            regresar.add(new UISelectEntity(sinContratista));
      } // if
		} // try
		finally {
			Methods.clean(columns);
		} // finally
		return regresar;
	}

	public static List<UISelectEntity> toContratistasPorElDia() throws Exception {
		return toContratistasPorElDia(Boolean.TRUE);
	}

	public static void toLoadContratistas(Map<String, Object> attrs) throws Exception {
		List<UISelectEntity>contratistas= toContratistasPorElDia();
		attrs.put("contratistas", contratistas);
		attrs.put("idContratista", UIBackingUtilities.toFirstKeySelectEntity(contratistas));
	}

	public static void toLoadDesarrollos(Map<String, Object> attrs) throws Exception {
		List<UISelectEntity>desarrollos= null;
    Map<String, Object> params     = new HashMap<>();
    try {
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
  		desarrollos= UIEntity.seleccione("TcKeetDesarrollosDto", "row", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
      attrs.put("desarrollos", desarrollos);
      attrs.put("idDesarrollo", desarrollos!= null? UIBackingUtilities.toFirstKeySelectEntity(desarrollos): new UISelectEntity(-1L));
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}
  
	public static void toLoadDesarrollosEmpresa(Map<String, Object> attrs) throws Exception {
		List<UISelectEntity>desarrollos= null;
    Map<String, Object> params     = new HashMap<>();
    try {
      params.put("idEmpresa", ((UISelectEntity)attrs.get("idEmpresa")).getKey());
  		desarrollos= UIEntity.seleccione("TcKeetDesarrollosDto", "empresa", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
      attrs.put("desarrollos", desarrollos);
      attrs.put("idDesarrollo", desarrollos!= null? UIBackingUtilities.toFirstKeySelectEntity(desarrollos): new UISelectEntity(-1L));
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}

	public static void toLoadDesarrollosCliente(Map<String, Object> attrs) throws Exception {
		List<UISelectEntity>desarrollos= null;
    Map<String, Object> params     = new HashMap<>();
    try {
      params.put("idCliente", ((UISelectEntity)attrs.get("idCliente")).getKey());
  		desarrollos= UIEntity.build("TcKeetDesarrollosDto", "cliente", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
      attrs.put("desarrollos", desarrollos);
      attrs.put("idDesarrollo", desarrollos!= null? UIBackingUtilities.toFirstKeySelectEntity(desarrollos): new UISelectEntity(-1L));
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}

	public static void toLoadContratos(Long idDesarrollos, Map<String, Object> attrs) throws Exception {
		List<UISelectEntity>contratos= null;
    Map<String, Object> params   = new HashMap<>();
    try {
      params.put("idDesarrollo", idDesarrollos);
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
  		contratos= UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
      attrs.put("contratos", contratos);
      attrs.put("idContrato", contratos!= null? UIBackingUtilities.toFirstKeySelectEntity(contratos): new UISelectEntity(-1L));
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}

	public static void toLoadPrototipos(Long idContrato, Map<String, Object> attrs) throws Exception {
		List<UISelectEntity>prototipos= null;
    Map<String, Object> params    = new HashMap<>();
    try {
      params.put("idContrato", idContrato);
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
  		prototipos= UIEntity.seleccione("VistaContratosDto", "findPrototipos", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
      attrs.put("prototipos", prototipos);
      attrs.put("idPrototipo", prototipos!= null? UIBackingUtilities.toFirstKeySelectEntity(prototipos): new UISelectEntity(-1L));
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}

	public static void toLoadClientesEmpresa(Map<String, Object> attrs) throws Exception {
		List<UISelectEntity>clientes= null;
    Map<String, Object> params  = new HashMap<>();
    try {
      params.put("sucursales", ((UISelectEntity)attrs.get("idEmpresa")).getKey());
  		clientes= UIEntity.build("TcManticClientesDto", "sucursales", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
      attrs.put("clientes", clientes);
      attrs.put("idCliente", clientes!= null? UIBackingUtilities.toFirstKeySelectEntity(clientes): new UISelectEntity(-1L));
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}
  
}
