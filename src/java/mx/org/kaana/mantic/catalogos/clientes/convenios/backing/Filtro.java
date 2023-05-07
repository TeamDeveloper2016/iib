package mx.org.kaana.mantic.catalogos.clientes.convenios.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.db.dto.TcKeetArticulosClientesDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;
import mx.org.kaana.mantic.catalogos.clientes.convenios.reglas.Transaccion;
import org.primefaces.event.SelectEvent;

@Named(value = "manticCatalogosClientesConveniosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

  @PostConstruct
  @Override
  protected void init() {
    try {
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.toLoadCatalogos();
			if(JsfBase.getFlashAttribute("idArticuloClienteProcess")!= null){
				this.attrs.put("idArticuloClienteProcess", JsfBase.getFlashAttribute("idArticuloCliente"));
				this.doLoad();
				this.attrs.put("idArticuloClienteProcess", null);
			} // if			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void toLoadCatalogos() {
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
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}
  
  @Override
  public void doLoad() {
    List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= this.toPrepare();
    try {
      params.put("sortOrder", "order by tc_mantic_clientes.razon_social, tc_mantic_articulos.nombre");
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("articulo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("menudeo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("medioMayoreo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("mayoreo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("actualizado", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaProveedoresDto", "convenios", params, columns);
      UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally		
  } // doLoad

  public String doAccion(String accion) {
    EAccion eaccion = null;
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
      JsfBase.setFlashAttribute("accion", eaccion);      
      JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(accion.toUpperCase()));      
      JsfBase.setFlashAttribute("retorno", "filtro");      
      JsfBase.setFlashAttribute("idArticuloClienteProcess", (eaccion.equals(EAccion.MODIFICAR)||eaccion.equals(EAccion.CONSULTAR)) ? ((Entity) this.attrs.get("seleccionado")).getKey() : -1L);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return "accion".concat(Constantes.REDIRECIONAR);
  } // doAccion

  public void doEliminar() {
    Transaccion transaccion = null;
    try {
      transaccion = new Transaccion(new TcKeetArticulosClientesDto(((Entity)this.attrs.get("seleccionado")).getKey()));
      transaccion.ejecutar(EAccion.ELIMINAR);
      JsfBase.addMessage("Eliminar precio", "Se ha eliminado el precio correctamente", ETipoMensaje.INFORMACION);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doEliminar
	
	public List<UISelectEntity> doCompleteCliente(String codigo) {
 		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
		boolean buscaPorCodigo    = false;
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(!Cadena.isVacio(codigo)) {
  			codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim();
				buscaPorCodigo= codigo.startsWith(".");
				if(buscaPorCodigo)
					codigo= codigo.trim().substring(1);
				codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
			} // if	
			else
				codigo= "WXYZ";
  		params.put("codigo", codigo);
			if(buscaPorCodigo)
        this.attrs.put("clientes", UIEntity.build("TcManticClientesDto", "porCodigo", params, columns, 40L));
			else
        this.attrs.put("clientes", UIEntity.build("TcManticClientesDto", "porNombre", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("clientes");
	}	
  
	protected Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb            = new StringBuilder();
		UISelectEntity cliente      = (UISelectEntity)this.attrs.get("cliente");
		List<UISelectEntity>clientes= (List<UISelectEntity>)this.attrs.get("clientes");
		if(this.attrs.get("idArticuloClienteProcess")!= null && !Cadena.isVacio(this.attrs.get("idArticuloClienteProcess")))
			sb.append("tc_keet_articulos_clientes.id_articulo_cliente =").append(this.attrs.get("idArticuloClienteProcess")).append(" and ");
    if(clientes!= null && cliente!= null && clientes.indexOf(cliente)>= 0) 
      sb.append("tc_mantic_clientes.razon_social regexp '.*").append(clientes.get(clientes.indexOf(cliente)).toString("razonSocial").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*")).append(".*' and ");				
    else 
      if(!Cadena.isVacio(JsfBase.getParametro("razonSocial_input"))) 
        sb.append("tc_mantic_clientes.razon_social regexp '.*").append(JsfBase.getParametro("razonSocial_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*")).append(".*' and ");				
    if(!Cadena.isVacio(this.attrs.get("grupo")))
      sb.append("tc_mantic_clientes.grupo like '%").append(this.attrs.get("grupo")).append("%' and ");
    if(!Cadena.isVacio(this.attrs.get("rfc")))
      sb.append("tc_mantic_clientes.rfc like '%").append(this.attrs.get("rfc")).append("%' and ");
    if(!Cadena.isVacio(this.attrs.get("clave")))
      sb.append("tc_mantic_clientes.clave like '%").append(this.attrs.get("clave")).append("%' and ");
    if(!Cadena.isVacio(JsfBase.getParametro("codigo_input")))
      sb.append("upper(tc_mantic_articulos_codigos.codigo) like upper('%").append(JsfBase.getParametro("codigo_input")).append("%') and ");						
    if(this.attrs.get("nombre")!= null && ((UISelectEntity)this.attrs.get("nombre")).getKey()> 0L) 
      sb.append("tc_mantic_articulos.id_articulo=").append(((UISelectEntity)this.attrs.get("nombre")).getKey()).append(" and ");						
    else 
      if(!Cadena.isVacio(JsfBase.getParametro("nombre_input"))) { 
        String nombre= JsfBase.getParametro("nombre_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*");
        sb.append("(tc_mantic_articulos.nombre regexp '.*").append(nombre).append(".*' or tc_mantic_articulos.descripcion regexp '.*").append(nombre).append(".*') and ");				
      } // if	
		if(this.attrs.get("idEmpresa")!= null && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>= 1L)
		  regresar.put("idEmpresa", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
		else
		  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	}

	public String doUploadCliente() {
		JsfBase.setFlashAttribute("idTipoMasivo", ECargaMasiva.ACUERDOS.getId());
		JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Catalogos/Clientes/Convenios/filtro");
		return "/Paginas/Mantic/Catalogos/Clientes/Convenios/importar".concat(Constantes.REDIRECIONAR);
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
      params.put("idArticuloTipo", "1");	      
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
			if(!Cadena.isVacio(search)) 
  			search= search.replaceAll(Constantes.CLEAN_SQL, "").trim().toUpperCase().replaceAll("(,| |\\t)+", ".*.*");			
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

}
