package mx.org.kaana.kalan.catalogos.subclasificaciones.backing;

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
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.kalan.catalogos.clasificaciones.reglas.Transaccion;
import mx.org.kaana.kalan.db.dto.TcKalanGastosClasificacionesDto;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectItem;

@Named(value = "kalanCatalogosSubClasificacionesFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428369L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("idActivo", 1L);
      this.toLoadClasificaciones();
      if(!Objects.equals(JsfBase.getFlashAttribute("idGastoSubclasificacion"), null)) {
        this.attrs.put("idGastoSubclasificacion", JsfBase.getFlashAttribute("idGastoSubclasificacion"));
        this.doLoad();
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
 
  @Override
  public void doLoad() {
    List<Columna> columns    = new ArrayList<>();
    Map<String, Object> params= this.toPrepare();
    try {
      params.put("sortOrder", "order by tc_kalan_gastos_clasificaciones.descripcion, tc_kalan_gastos_subclasificaciones.descripcion");
      columns.add(new Columna("clasificacion", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA));      
      this.lazyModel = new FormatCustomLazy("TcKalanGastosSubclasificacionesDto", params, columns);
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
  } 
  
	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idGastoSubclasificacion")) && !this.attrs.get("idGastoSubclasificacion").toString().equals("-1")) {
  		sb.append("(tc_kalan_gastos_subclasificaciones.id_gasto_subclasificacion=").append(this.attrs.get("idGastoSubclasificacion")).append(") and ");
      this.attrs.put("idGastoSubclasificacion", null);
    } // if  
		if(!Cadena.isVacio(this.attrs.get("idGastoClasificacion")) && !Objects.equals(this.attrs.get("idGastoClasificacion").toString(), "-1"))
  		sb.append("(tc_kalan_gastos_subclasificaciones.id_gasto_clasificacion= ").append(this.attrs.get("idGastoClasificacion")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("clave")))
  		sb.append("(tc_kalan_gastos_subclasificaciones.clave like '%").append(this.attrs.get("clave")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("descripcion")))
  		sb.append("(tc_kalan_gastos_subclasificaciones.descripcion like '%").append(this.attrs.get("descripcion")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
		  sb.append("(date_format(tc_kalan_gastos_subclasificaciones.registro, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
		  sb.append("(date_format(tc_kalan_gastos_subclasificaciones.registro, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");	
		sb.append("(tc_kalan_gastos_subclasificaciones.id_activo= ").append(this.attrs.get("idActivo")).append(") and ");
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;
	}

  public String doAccion(String accion) {
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Kalan/Catalogos/SubClasificaciones/filtro");		
			JsfBase.setFlashAttribute("idGastoSubclasificacion", (eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR)) ? ((Entity)this.attrs.get("seleccionado")).getKey(): -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Kalan/Catalogos/SubClasificaciones/accion".concat(Constantes.REDIRECIONAR);
  } // doAccion  
	
  public void doEliminar() {
		Transaccion transaccion = null;
		Entity seleccionado     = null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
			transaccion= new Transaccion(new TcKalanGastosClasificacionesDto(seleccionado.getKey()));
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "El sub clasificador se elimin� correctamente", ETipoMensaje.ERROR);
			else
				JsfBase.addMessage("Eliminar", "Ocurri� un error al eliminar el sub clasificador", ETipoMensaje.ERROR);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
  } // doEliminar	
 
  private void toLoadClasificaciones() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> clasificaciones= UISelect.seleccione("TcKalanGastosClasificacionesDto", params, "idKey|descripcion", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("clasificaciones", clasificaciones);
      this.attrs.put("idGastoClasificacion", UIBackingUtilities.toFirstKeySelectItem(clasificaciones));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }    
  
}