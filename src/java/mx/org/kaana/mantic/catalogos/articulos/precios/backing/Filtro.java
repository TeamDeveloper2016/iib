package mx.org.kaana.mantic.catalogos.articulos.precios.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.comun.Comun;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kalan.db.dto.TcKalanProductosDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.clientes.convenios.reglas.Transaccion;

@Named(value = "manticCatalogosArticulosPreciosFiltro")
@ViewScoped
public class Filtro extends Comun implements Serializable {

  private static final long serialVersionUID = 8792667741599428879L;
  
  private TcKalanProductosDto producto= null;

  public TcKalanProductosDto getProducto() {
    return producto;
  }

  public void setProducto(TcKalanProductosDto producto) {
    this.producto = producto;
  }
  
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("idEmpresa", new UISelectEntity(JsfBase.getAutentifica().getEmpresa().getIdEmpresa()));      
			this.toLoadCatalogos();
      this.doLoad();
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
      columns.add(new Columna("menudeo", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("medioMayoreo", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("mayoreo", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("factor", EFormatoDinamicos.MILES_SAT_DECIMALES));
      params.put("sortOrder", "order by tc_mantic_articulos.nombre, tc_mantic_articulos.actualizado");
      this.lazyModel = new FormatCustomLazy("VistaArticuloPrecioDto", params, columns);
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
	
	private Map<String, Object> toPrepare() {
		Map<String, Object> regresar= new HashMap<>();
		StringBuilder codigo        = null;
		StringBuilder sb            = null;
		try {
			codigo= new StringBuilder();			
			sb    = new StringBuilder();			
    	if(!Cadena.isVacio(this.attrs.get("idProducto")) && !this.attrs.get("idProducto").toString().equals("-1")) {
        List<UISelectEntity> productos= (List<UISelectEntity>)this.attrs.get("productos");
        if(productos!= null && !productos.isEmpty()) {
          int index= productos.indexOf(this.attrs.get("idProducto"));
          if(index>= 0) {
            this.attrs.put("idProducto", productos.get(index));
            codigo.append(((UISelectEntity)this.attrs.get("idProducto")).toString("clave"));
          } // if  
        } // if  
      } // if
    	if(!Cadena.isVacio(this.attrs.get("idClase")) && !this.attrs.get("idClase").toString().equals("-1")) {
        List<UISelectEntity> clases= (List<UISelectEntity>)this.attrs.get("clases");
        if(clases!= null && !clases.isEmpty()) {
          int index= clases.indexOf(this.attrs.get("idClase"));
          if(index>= 0) {
            this.attrs.put("idClase", clases.get(index));
            codigo.append(((UISelectEntity)this.attrs.get("idClase")).toString("clave"));
          } // if  
        } // if  
      } // if
      if(codigo.length()> 0)
        codigo.append("%");
    	if(!Cadena.isVacio(this.attrs.get("idPeso")) && !this.attrs.get("idPeso").toString().equals("-1")) {
        List<UISelectEntity> pesos= (List<UISelectEntity>)this.attrs.get("pesos");
        if(pesos!= null && !pesos.isEmpty()) {
          int index= pesos.indexOf(this.attrs.get("idPeso"));
          if(index>= 0) {
            this.attrs.put("idPeso", pesos.get(index));
            codigo.append(((UISelectEntity)this.attrs.get("idPeso")).toString("clave"));
          } // if  
        } // if  
      } // if
      if(codigo.length()> 0)
        sb.append("(tc_mantic_articulos_codigos.codigo like '").append(codigo.toString()).append("') and ");
			if(Cadena.isVacio(sb.toString()))
				regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			else
			  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));			
		  if(Cadena.isVacio(this.attrs.get("idEmpresa")))
			  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getDependencias());
			else
      	if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1")) 
          regresar.put("idEmpresa", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
        else  
          regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toPrepare

  public void doAceptar() {
    Transaccion transaccion= null;
    try {
			transaccion= new Transaccion(this.producto, this.toPrepare());
			if (transaccion.ejecutar(EAccion.REPROCESAR)) {
				JsfBase.addMessage("Se modificó el precio de forma correcta", ETipoMensaje.INFORMACION);
        this.doLoad();
      } // if  
			else 
				JsfBase.addMessage("Ocurrió un error al actualizar los precio", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doAccion
  
	private void toLoadCatalogos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MILES_SAT_DECIMALES));
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectEntity> empresas= (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave");
      this.attrs.put("empresas", empresas);
			this.attrs.put("idEmpresa", UIBackingUtilities.toFirstKeySelectEntity(empresas));
      this.doProductos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}
  
	public void doProductos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
			params.put("idEmpresa", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
      List<UISelectEntity> productos= (List<UISelectEntity>) UIEntity.build("TcKalanProductosDto", "row", params, columns);
      this.attrs.put("productos", productos);
			this.attrs.put("idProducto", UIBackingUtilities.toFirstKeySelectEntity(productos));
      this.doClases();
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

	public void doClases() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      this.producto= (TcKalanProductosDto)DaoFactory.getInstance().findById(TcKalanProductosDto.class, ((UISelectEntity)this.attrs.get("idProducto")).getKey());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
			params.put("idProducto", ((UISelectEntity)this.attrs.get("idProducto")).getKey());
      List<UISelectEntity> clases= (List<UISelectEntity>) UIEntity.todos("TcKalanProductosClasesDto", "row", params, columns, "clave");
      this.attrs.put("clases", clases);
			this.attrs.put("idClase", UIBackingUtilities.toFirstKeySelectEntity(clases));
      this.doPesos();
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
  
	public void doPesos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
			params.put("idProducto", ((UISelectEntity)this.attrs.get("idProducto")).getKey());
      List<UISelectEntity> pesos= (List<UISelectEntity>) UIEntity.todos("TcKalanProductosPesosDto", "row", params, columns, "clave");
      this.attrs.put("pesos", pesos);
			this.attrs.put("idPeso", UIBackingUtilities.toFirstKeySelectEntity(pesos));
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