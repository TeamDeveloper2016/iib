package mx.org.kaana.mantic.inventarios.almacenes.backing;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.procesos.comun.Comun;
import mx.org.kaana.kajool.procesos.reportes.beans.ExportarXls;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.LoadImages;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;
import mx.org.kaana.mantic.enums.EExportacionXls;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

@Named(value = "manticInventariosAlmacenesFiltro")
@ViewScoped
public class Filtro extends Comun implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

  @PostConstruct
  @Override
  protected void init() {
    try {
    	this.attrs.put("buscaPorCodigo", false);
      this.attrs.put("codigo", "");
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());      
			this.toLoadCatalog();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= null;
    try {
		  params= this.toPrepare();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("fecha", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("hora", EFormatoDinamicos.HORA_LARGA));
      columns.add(new Columna("stock", EFormatoDinamicos.NUMERO_CON_DECIMALES));
      params.put("sortOrder", "order by tc_mantic_inventarios.registro desc");
      this.lazyModel = new FormatCustomLazy("VistaArticulosDto", "conteo", params, columns);
      UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
    } // finally		
  } // doLoad
	
	private void toLoadCatalog() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			List<UISelectEntity> empresas= (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns);
      this.attrs.put("empresas", empresas);
			if(empresas!= null && !empresas.isEmpty())
				this.attrs.put("idEmpresa", empresas.get(0));
			else
			  this.attrs.put("idEmpresa", new UISelectEntity("-1"));
			this.doAlmacenes();
			this.doEjercicios();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}

	private Map<String, Object> toPrepare() throws Exception {
		Map<String, Object> regresar= new HashMap<>();
		StringBuilder sb= new StringBuilder();			
		if(!Cadena.isVacio(JsfBase.getParametro("codigo_input")))
			sb.append("upper(tc_mantic_articulos_codigos.codigo) like upper('%").append(JsfBase.getParametro("codigo_input")).append("%') and ");						
		if(this.attrs.get("nombre")!= null && ((UISelectEntity)this.attrs.get("nombre")).getKey()> 0L) 
			sb.append("tc_mantic_articulos.id_articulo=").append(((UISelectEntity)this.attrs.get("nombre")).getKey()).append(" and ");						
		else 
			if(!Cadena.isVacio(JsfBase.getParametro("nombre_input"))) { 
				String nombre= JsfBase.getParametro("nombre_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*");
				sb.append("(tc_mantic_articulos.nombre regexp '.*").append(nombre).append(".*' or tc_mantic_articulos.descripcion regexp '.*").append(nombre).append(".*') and ");				
			} // if	
		sb.append("tc_mantic_articulos.id_vigente=").append(this.attrs.get("idVigente")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
			sb.append("(date_format(tc_mantic_articulos.actualizado, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
			sb.append("(date_format(tc_mantic_articulos.actualizado, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("idInventario")) && !this.attrs.get("idInventario").toString().equals("-1"))
			if(this.attrs.get("idInventario").toString().equals("1"))
				sb.append("(tc_mantic_inventarios.id_automatico= 2) and ");
			else
				sb.append("(tc_mantic_inventarios.id_automatico!= 2) and ");
		if(!Cadena.isVacio(this.attrs.get("idVerificado")) && !this.attrs.get("idVerificado").toString().equals("-1"))
			sb.append("(tc_mantic_inventarios.id_verificado=").append(this.attrs.get("idVerificado").toString()).append(") and ");
		regresar.put("idAlmacen", ((UISelectEntity)this.attrs.get("idAlmacen")).getKey());
		regresar.put("ejercicio", ((UISelectEntity)this.attrs.get("ejercicio")).getKey());
		if(Cadena.isVacio(sb.toString()))
			regresar.put("condicion", Constantes.SQL_VERDADERO);
		else
			regresar.put("condicion", sb.substring(0, sb.length()- 4));			
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) || this.attrs.get("idEmpresa").toString().equals("-1"))
			regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getDependencias());
		else
			regresar.put("idEmpresa", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
		return regresar;
	} // toCondicion

  public String doAccion(String accion) {
    EAccion eaccion = null;
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
      JsfBase.setFlashAttribute("accion", eaccion);
      JsfBase.setFlashAttribute("idArticulo", (eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR) || eaccion.equals(EAccion.COPIAR) || eaccion.equals(EAccion.ACTIVAR)) ? ((Entity) this.attrs.get("seleccionado")).getKey() : -1L);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return "accion".concat(Constantes.REDIRECIONAR);
  } // doAccion
  
	public void doUpdateArticulos() {
		List<Columna> columns         = new ArrayList<>();
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> articulos= null;
		boolean buscaPorCodigo        = false;
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			params.put("idAlmacen", JsfBase.getAutentifica().getEmpresa().getIdAlmacen());
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);
			String search= new String((String)this.attrs.get("codigo")); 
			if(!Cadena.isVacio(search)) {
  			search= search.replaceAll(Constantes.CLEAN_SQL, "").trim();
				buscaPorCodigo= search.startsWith(".");
				if(buscaPorCodigo)
					search= search.trim().substring(1);
				search= search.toUpperCase().replaceAll("(,| |\\t)+", ".*");
        if(Cadena.isVacio(search))
          search= ".*";
			} // if	
			else
				search= "WXYZ";
  		params.put("codigo", search);
			if((boolean)this.attrs.get("buscaPorCodigo") || buscaPorCodigo)
        articulos= (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porCodigo", params, columns, 40L);
			else
        articulos= (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porNombre", params, columns, 40L);
      this.attrs.put("articulos", articulos);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}	// doUpdateArticulos
	
	public void doUpdateArticulosFiltro() {
		List<Columna> columns         = new ArrayList<>();
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> articulos= null;
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);
			String search= (String) this.attrs.get("codigoFiltro"); 
			if(!Cadena.isVacio(search)) {
  			search= search.replaceAll(Constantes.CLEAN_SQL, "").trim().toUpperCase().replaceAll("(,| |\\t)+", ".*");			
        if(Cadena.isVacio(search))
          search= ".*";
      } // if  
			else
				search= "WXYZ";
  		params.put("codigo", search);			        
      params.put("idArticuloTipo", "1,2,3");	      
      articulos= (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porNombreTipoArticulo", params, columns, 40L);
      this.attrs.put("articulosFiltro", articulos);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}	// doUpdateArticulos

	public List<UISelectEntity> doCompleteArticulo(String query) {
		this.attrs.put("existe", null);
		this.attrs.put("codigo", query);
    this.doUpdateArticulos();		
		return (List<UISelectEntity>)this.attrs.get("articulos");
	}	// doCompleteArticulo
	
	public List<UISelectEntity> doCompleteArticuloFiltro(String query) {
		this.attrs.put("existeFiltro", null);
		this.attrs.put("codigoFiltro", query);
    this.doUpdateArticulosFiltro();
		return (List<UISelectEntity>)this.attrs.get("articulosFiltro");
	}	// doCompleteArticulo

  public void doFindArticulo() {
		try {
    	List<UISelectEntity> articulos= (List<UISelectEntity>)this.attrs.get("articulos");
	    UISelectEntity articulo= (UISelectEntity)this.attrs.get("custom");
			if(articulo== null)
			  articulo= new UISelectEntity(new Entity(-1L));
			else
				if(articulos.indexOf(articulo)>= 0) 
					articulo= articulos.get(articulos.indexOf(articulo));
			  else
			    articulo= articulos.get(0);
			this.attrs.put("seleccionado", new Entity(articulo.toLong("idArticulo")));
		} // try
	  catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	} // doFindArticulo
	
	public StreamedContent doPrepareImage(Entity row) {
		StreamedContent regresar= null;
		try {
			regresar= LoadImages.getImage(row.toLong("idKey"));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
		return regresar;
	}
	
	public void doAlmacenes() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
				params.put("sucursales", this.attrs.get("idEmpresa"));
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			List<UISelectEntity> almacenes= (List<UISelectEntity>)UIEntity.build("TcManticAlmacenesDto", "almacenes", params, columns);
      this.attrs.put("almacenes", almacenes);
			if(almacenes!= null && !almacenes.isEmpty())
				this.attrs.put("idAlmacen", almacenes.get(0));
			else
			  this.attrs.put("idAlmacen", new UISelectEntity("-1"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}

	public void doEjercicios() {
    try {
			List<UISelectEntity> ejercicios= (List<UISelectEntity>)UIEntity.build("TcManticInventariosDto", "ejercicios", Collections.EMPTY_MAP);
      this.attrs.put("ejercicios", ejercicios);
			if(ejercicios!= null && !ejercicios.isEmpty())
				this.attrs.put("ejercicio", ejercicios.get(0));
			else
			  this.attrs.put("ejercicio", new UISelectEntity(String.valueOf(Fecha.getAnioActual())));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	}

	public void doUpdateCodigos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			String search= (String)this.attrs.get("codigoCodigo"); 
			search= !Cadena.isVacio(search) ? search.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim(): "WXYZ";
			params.put("idAlmacen", JsfBase.getAutentifica().getEmpresa().getIdAlmacen());
			if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
				params.put("sucursales", this.attrs.get("idEmpresa"));
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);			
  		params.put("codigo", search);			
      this.attrs.put("codigos", (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porCodigo", params, columns, 20L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}	// doUpdateCodigos

	public List<UISelectEntity> doCompleteCodigo(String query) {
		this.attrs.put("codigoCodigo", query);
    this.doUpdateCodigos();		
		return (List<UISelectEntity>)this.attrs.get("codigos");
	}	// doCompleteCodigo

	public void doAsignaCodigo(SelectEvent event) {
		UISelectEntity seleccion    = null;
		List<UISelectEntity> codigos= null;
		try {
			codigos= (List<UISelectEntity>) this.attrs.get("codigos");
			seleccion= codigos.get(codigos.indexOf((UISelectEntity)event.getObject()));
			this.attrs.put("codigoSeleccion", seleccion);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doAsignaCodigo	

	public String doExportarXls() {
		String regresar          = null;		
		Map<String, Object>params= null;
		try {									   
		  params= this.toPrepare();
			params.put("sortOrder", "order by tc_mantic_articulos.nombre, tc_mantic_articulos.actualizado");
			JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap)params).clone(), EExportacionXls.CONTEOS.getProceso(), EExportacionXls.CONTEOS.getIdXml(), EExportacionXls.CONTEOS.getNombreArchivo()), EExportacionXls.CONTEOS, "EJERCICIO,CODIGO,NOMBRE,STOCK,FECHA"));
			JsfBase.getAutentifica().setMonitoreo(new Monitoreo());
			regresar = "/Paginas/Reportes/excel".concat(Constantes.REDIRECIONAR);
		} // try
		catch (Exception e){
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // doExportarFdDbf  

	public String doMasivo() {
    JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Almacenes/filtro");
    JsfBase.setFlashAttribute("idTipoMasivo", ECargaMasiva.CONTEOS.getId());
    return "/Paginas/Mantic/Catalogos/Masivos/importar".concat(Constantes.REDIRECIONAR);
	} // doMasivo	
	
	public String doRegistrar() {
    JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Almacenes/filtro");
    return "/Paginas/Mantic/Catalogos/Inventarios/conteos".concat(Constantes.REDIRECIONAR);
	} // doRegistrar	
	
	public String toColor(Entity row) {
		return Objects.equals(row.toLong("idVerificado"), 1L)? "janal-tr-orange": "";
	} 
 
}