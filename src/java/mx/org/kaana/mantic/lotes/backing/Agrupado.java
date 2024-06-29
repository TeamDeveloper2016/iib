package mx.org.kaana.mantic.lotes.backing;

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
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.lotes.beans.Partida;
import mx.org.kaana.mantic.lotes.reglas.AdminLotes;
import mx.org.kaana.mantic.lotes.reglas.Transaccion;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticLotesAgrupado")
@ViewScoped
public class Agrupado extends Especial implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Agrupado.class);
  private static final long serialVersionUID= 117393488565639367L;
	
	@PostConstruct
  @Override
  protected void init() {		
    try {
      super.init();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	@Override
  public void doLoad() {
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      switch (this.accion) {
        case AGREGAR:											
          this.orden= new AdminLotes();
          this.orden.getLote().setIdLoteTipo(4L);
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          this.orden= new AdminLotes((Long)this.attrs.get("idLote"));
          break;
      } // switch
			this.toLoadCatalogos();
      this.toSumPartidas("porcentajes");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 

  @Override
  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    try {			
      if(this.checkLote()) {
        this.accion= Objects.equals(this.accion, EAccion.AGREGAR)? EAccion.COMPLETO: Objects.equals(this.accion, EAccion.MODIFICAR)? EAccion.COMPLEMENTAR: this.accion;
        this.orden.getLote().setIdTipoClase(null);
        transaccion = new Transaccion(this.orden.getLote());
        if (transaccion.ejecutar(this.accion)) {
          regresar= this.doCancelar();
        } // if
        else 
          JsfBase.addMessage("Ocurrió un error al registrar el lote", ETipoMensaje.ERROR);
      } // if  
      else
        JsfBase.addMessage("No se ha agregado un producto al lote", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  @Override
	public void doUpdateProductos() {
    List<Columna> columns      = new ArrayList<>();    
    Map<String, Object> params = new HashMap<>();
		List<UISelectEntity> clases= (List<UISelectEntity>)this.attrs.get("clases");
    try {
      int index= clases.indexOf(this.orden.getLote().getIkTipoClase());
      if(index>= 0) {
        this.orden.getLote().setIkTipoClase(clases.get(index));
        params.put("sortOrder", "order by tc_mantic_lotes_detalles.id_lote, tc_mantic_lotes_detalles.id_lote_detalle");
        params.put("idLote", this.orden.getLote().getIdLote());
        params.put("idAlmacen", this.orden.getLote().getIkAlmacen().getKey());
        params.put("clase", this.orden.getLote().getIkTipoClase().toString("clave"));
        columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("articulo", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("kilos", EFormatoDinamicos.MILES_CON_DECIMALES));
        params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
        if(!Objects.equals(this.orden.getLote().getIdTipoClase(), -1L))
          this.lazyModel= new FormatCustomLazy("VistaLotesDto", "agrupado", params, columns);
        if(!Objects.equals(this.orden.getLote().getIdTipoClase(), this.orden.getLote().getItTipoClase())) 
          this.toCheckPartidas();
      } // if  
      else 
        this.lazyModel= null;
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

  @Override
  public void doAdd(Entity row) {
    Partida partida= this.toNewPartida(row);
    partida.setIdLote(row.toLong("idLote"));
    partida.setIdLoteDetalle(row.toLong("idLoteDetalle"));
    partida.setLote(row.toString("lote"));
    super.toAdd(row, partida);        
  }

}