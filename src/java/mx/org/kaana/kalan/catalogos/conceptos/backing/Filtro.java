package mx.org.kaana.kalan.catalogos.conceptos.backing;

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
import mx.org.kaana.kalan.catalogos.conceptos.reglas.Transaccion;
import mx.org.kaana.kalan.db.dto.TcKalanTiposConceptosDto;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectItem;

@Named(value = "kalanCatalogosConceptosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428361L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("idActivo", 1L);
      this.toLoadMovimientos();
      if(!Objects.equals(JsfBase.getFlashAttribute("idTipoConcepto"), null)) {
        this.attrs.put("idTipoConcepto", JsfBase.getFlashAttribute("idTipoConcepto"));
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
      params.put("sortOrder", "order by tc_kalan_tipos_movimientos.descripcion, tc_kalan_tipos_conceptos.descripcion");
      columns.add(new Columna("movimiento", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA));      
      this.lazyModel = new FormatCustomLazy("TcKalanTiposConceptosDto", params, columns);
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
		if(!Cadena.isVacio(this.attrs.get("idTipoConcepto")) && !this.attrs.get("idTipoConcepto").toString().equals("-1")) {
  		sb.append("(tc_kalan_tipos_conceptos.id_tipo_concepto=").append(this.attrs.get("idTipoConcepto")).append(") and ");
      this.attrs.put("idTipoConcepto", null);
    } // if  
		if(!Cadena.isVacio(this.attrs.get("idTipoMovimiento")) && !Objects.equals(this.attrs.get("idTipoMovimiento").toString(), "-1"))
  		sb.append("(tc_kalan_tipos_conceptos.id_tipo_movimiento= ").append(this.attrs.get("idTipoMovimiento")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("clave")))
  		sb.append("(tc_kalan_tipos_conceptos.clave like '%").append(this.attrs.get("clave")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("descripcion")))
  		sb.append("(tc_kalan_tipos_conceptos.descripcion like '%").append(this.attrs.get("descripcion")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
		  sb.append("(date_format(tc_kalan_tipos_conceptos.registro, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
		  sb.append("(date_format(tc_kalan_tipos_conceptos.registro, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");	
		sb.append("(tc_kalan_tipos_conceptos.id_activo= ").append(this.attrs.get("idActivo")).append(") and ");
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
			JsfBase.setFlashAttribute("retorno", "/Paginas/Kalan/Catalogos/Conceptos/filtro");		
			JsfBase.setFlashAttribute("idTipoConcepto", (eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR)) ? ((Entity)this.attrs.get("seleccionado")).getKey(): -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Kalan/Catalogos/Conceptos/accion".concat(Constantes.REDIRECIONAR);
  } 
	
  public void doEliminar() {
		Transaccion transaccion = null;
		Entity seleccionado     = null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
			transaccion= new Transaccion(new TcKalanTiposConceptosDto(seleccionado.getKey()));
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "El concepto se eliminó correctamente", ETipoMensaje.ERROR);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar el concepto", ETipoMensaje.ERROR);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
  } // doEliminar	
 
  private void toLoadMovimientos() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> movimientos= UISelect.seleccione("TcKalanTiposMovimientosDto", params, "idKey|descripcion", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("movimientos", movimientos);
      this.attrs.put("idTipoMovimiento", UIBackingUtilities.toFirstKeySelectItem(movimientos));
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