package mx.org.kaana.mantic.catalogos.mermas.backing;

import java.io.Serializable;
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
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.mermas.reglas.Transaccion;
import mx.org.kaana.mantic.db.dto.TcManticNotasMermasDto;

@Named(value = "manticCatalogosMermasFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 136617741599428361L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("sortOrder", "order by tc_mantic_notas_mermas.registro");
      this.attrs.put("nombre", "");
      this.attrs.put("descripcion", "");
      this.attrs.put("idNotaMerma", JsfBase.getFlashAttribute("idNotaMerma"));
      if(!Objects.equals(this.attrs.get("idNotaMerma"), null))
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
    Map<String, Object> params= null;
    try {
      params= this.toPrepare();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA));      
      this.lazyModel = new FormatCustomLazy("TcManticNotasMermasDto", params, columns);
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

	private Map<String, Object> toPrepare() {
		Map<String, Object> regresar= new HashMap<>();
		StringBuilder sb            = new StringBuilder();
		try {
    	if(!Cadena.isVacio(this.attrs.get("idNotaMerma")))
		    sb.append("(tc_mantic_notas_mermas.id_nota_merma= ").append(this.attrs.get("idNotaMerma")).append(") and ");
    	if(!Cadena.isVacio(this.attrs.get("nombre")))
		    sb.append("(tc_mantic_notas_mermas.nombre like '%").append(this.attrs.get("clave")).append("%') and ");
    	if(!Cadena.isVacio(this.attrs.get("descripcion")))
		    sb.append("(tc_mantic_notas_mermas.descripcion like '%").append(this.attrs.get("descripcion")).append("%') and ");
			if(Cadena.isVacio(sb.toString()))
				regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			else
			  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} 
  
  public String doAccion(String accion) {
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Catalogos/Mermas/filtro");		
			JsfBase.setFlashAttribute("idNotaMerma", (eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR))? ((Entity)this.attrs.get("seleccionado")).getKey(): -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Mantic/Catalogos/Mermas/accion".concat(Constantes.REDIRECIONAR);
  } // doAccion  
	
  public void doEliminar() {
		Transaccion transaccion = null;
		Entity seleccionado     = null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
			transaccion= new Transaccion(new TcManticNotasMermasDto(seleccionado.getKey()));
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "El registro se eliminó correctamente", ETipoMensaje.ERROR);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar el registro", ETipoMensaje.ERROR);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
  } // doEliminar	
  
}