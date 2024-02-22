package mx.org.kaana.mantic.consultas.backing;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.procesos.comun.Comun;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.consultas.reglas.UtilidadArticulosLazy;

@Named(value = "manticConsultasArticulos")
@ViewScoped
public class Articulos extends Comun implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

  @PostConstruct
  @Override
  protected void init() {
    try {
    	this.attrs.put("buscaPorCodigo", false);
      this.attrs.put("codigo", "");
      this.attrs.put("idTipoArticulo", 1L);
      this.attrs.put("idTipoDocumento", 2L);
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
		Map<String, Object> params= this.toPrepare();
    try {
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("precios", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA));
      params.put("sortOrder", "order by tc_mantic_ventas.ticket desc");
      this.lazyModel = new UtilidadArticulosLazy("VistaConsultasDto", "articulo", params, columns);
      UIBackingUtilities.resetDataTable();
    } // try // try
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
      List<UISelectEntity> empresas= UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "nombre");
      this.attrs.put("empresas", empresas);
			this.attrs.put("idEmpresa",  UIBackingUtilities.toFirstKeySelectEntity(empresas));
      List<UISelectEntity> almacenes= UIEntity.seleccione("TcManticAlmacenesDto", "almacenes", params, columns, "nombre");
      this.attrs.put("almacenes", (List<UISelectEntity>) almacenes);
			this.attrs.put("idAlmacen", UIBackingUtilities.toFirstKeySelectEntity(almacenes));
      this.toLoadVentaEstatus();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}

	private void toLoadVentaEstatus() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			params.put("idTipoDocumento", "2");
			params.put("estatusAsociados", "3, 4, 12, 15, 18");
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      List<UISelectEntity> estatus= UIEntity.seleccione("TcManticVentasEstatusDto", "estatus", params, columns, "nombre");
      this.attrs.put("estatus", estatus);
			this.attrs.put("idVentaEstatus", UIBackingUtilities.toFirstKeySelectEntity(estatus));
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}

	protected Map<String, Object> toPrepare() {
		Map<String, Object> regresar= new HashMap<>();
		StringBuilder sb            = null;
		try {
			sb= new StringBuilder("tc_mantic_articulos.id_articulo_tipo=").append(this.attrs.get("idTipoArticulo")).append(" and ");						
			if(!Cadena.isVacio(this.attrs.get("codigo")))
				sb.append("upper(tc_mantic_articulos_codigos.codigo) like upper('%").append(this.attrs.get("codigo")).append("%') and ");						
			if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
				sb.append("(date_format(tc_mantic_ventas_detalles.registro, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");
			if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
				sb.append("(date_format(tc_mantic_ventas_detalles.registro, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");
			if(!Cadena.isVacio(this.attrs.get("montoInicio")))
				sb.append("(tc_mantic_ventas_detalles.precio>= ").append((Double)this.attrs.get("montoInicio")).append(") and ");			
			if(!Cadena.isVacio(this.attrs.get("montoTermino")))
				sb.append("(tc_mantic_ventas_detalles.precio<= ").append((Double)this.attrs.get("montoTermino")).append(") and ");			
			if(this.attrs.get("nombre")!= null && ((UISelectEntity)this.attrs.get("nombre")).getKey()> 0L) 
				sb.append("tc_mantic_articulos.id_articulo=").append(((UISelectEntity)this.attrs.get("nombre")).getKey()).append(" and ");						
  		else 
	  		if(!Cadena.isVacio(JsfBase.getParametro("nombre_input"))) { 
					String nombre= JsfBase.getParametro("nombre_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*");
		  		sb.append("(tc_mantic_articulos.nombre regexp '.*").append(nombre).append(".*' or tc_mantic_articulos.descripcion regexp '.*").append(nombre).append(".*') and ");				
				} // if	
		  sb.append("tc_mantic_articulos.id_vigente=").append(this.attrs.get("idVigente")).append(" and ");
		  if(!Cadena.isVacio(this.attrs.get("idVentaEstatus")) && !Objects.equals(((UISelectEntity)this.attrs.get("idVentaEstatus")).getKey(), -1L))
  		  sb.append("(tc_mantic_ventas.id_venta_estatus= ").append(((UISelectEntity)this.attrs.get("idVentaEstatus")).getKey()).append(") and ");
			if(this.attrs.get("vendedor")!= null && ((UISelectEntity)this.attrs.get("vendedor")).getKey()> 0L) 
				sb.append("tc_mantic_personas.id_persona=").append(((UISelectEntity)this.attrs.get("vendedor")).getKey()).append(" and ");						
  		else 
	  		if(!Cadena.isVacio(JsfBase.getParametro("vendedor_input"))) { 
					String nombre= JsfBase.getParametro("vendedor_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*");
		  		sb.append("(upper(concat(tc_mantic_personas.nombres, ' ', ifnull(tc_mantic_personas.paterno, ''), ' ', ifnull(tc_mantic_personas.materno, ''))) regexp '.*").append(nombre).append(".*') and ");
				} // if	
		  if(!Cadena.isVacio(this.attrs.get("idAlmacen")) && !Objects.equals(((UISelectEntity)this.attrs.get("idAlmacen")).getKey(), -1L))
  		  regresar.put("almacen", " and (tc_mantic_almacenes_articulos.id_almacen= "+ ((UISelectEntity)this.attrs.get("idAlmacen")).getKey()+ ")");
			else
  		  regresar.put("almacen", " ");
			if(Cadena.isVacio(sb.toString()))
				regresar.put("condicion", Constantes.SQL_VERDADERO);
			else
			  regresar.put("condicion", sb.substring(0, sb.length()- 4));			
      regresar.put("todas", Constantes.SQL_FALSO);
		  if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !Objects.equals(((UISelectEntity)this.attrs.get("idEmpresa")).getKey(), -1L))
			  regresar.put("idEmpresa", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
      else {
			  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getDependencias());
        regresar.put("todas", Constantes.SQL_VERDADERO);
      } // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} 

	public void doUpdateArticulos() {
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
		this.attrs.put("existeFiltro", null);
		this.attrs.put("codigoFiltro", query);
    this.doUpdateArticulos();
		return (List<UISelectEntity>)this.attrs.get("articulosFiltro");
	}	// doCompleteArticulo

	public void doAlmacenes() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
			if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
				params.put("sucursales", this.attrs.get("idEmpresa"));
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("almacenes", (List<UISelectEntity>) UIEntity.build("TcManticAlmacenesDto", "almacenes", params, columns));
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

	public void doMontoUpdate() {
	  if(this.attrs.get("montoInicio")!= null && this.attrs.get("montoTermino")== null)
			this.attrs.put("montoTermino", this.attrs.get("montoInicio"));
	  if(this.attrs.get("montoTermino")!= null && this.attrs.get("montoInicio")== null)
			this.attrs.put("montoInicio", this.attrs.get("montoTermino"));
	} 

	public List<UISelectEntity> doCompletePersona(String codigo) {
 		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("empleado", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("correo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("citados", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(!Cadena.isVacio(codigo)) {
  			codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim();
				codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*");
			} // if	
			else
				codigo= "WXYZ";
			params.put("fecha", Fecha.toRegistro());			
  		params.put(Constantes.SQL_CONDICION, "(upper(concat(tc_mantic_personas.nombres, ' ', ifnull(tc_mantic_personas.paterno, ''), ' ', ifnull(tc_mantic_personas.materno, ''))) regexp '.*".concat(codigo).concat(".*' or upper(tc_mantic_personas.rfc) regexp '.*").concat(codigo).concat(".*')"));
      this.attrs.put("personas", UIEntity.build("VistaClientesCitasDto", "citados", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
		return (List<UISelectEntity>)this.attrs.get("personas");
	}
 
	public void doMoveSection(Entity row) {
		List<Columna> columns         = new ArrayList<>();
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> documento= null;
    try {
      this.attrs.put("seleccionado", row);
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cantidad", EFormatoDinamicos.NUMERO_CON_DECIMALES));
      columns.add(new Columna("impuestos", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("precio", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MONEDA_SAT_DECIMALES));
      columns.add(new Columna("fecha", EFormatoDinamicos.FECHA_HORA));
			params.put("idVenta", row.toLong("idVenta"));
			documento= (List<UISelectEntity>) UIEntity.build("VistaKardexDto", "venta", params, columns, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("documentos", documento);
			if(documento!= null && !documento.isEmpty()) {
				documento.get(0).put("articulos", new Value("articulos", documento.size()));
        this.attrs.put("documento", documento.get(0));
			} // if	
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
  
}