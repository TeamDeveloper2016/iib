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
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.lotes.reglas.Transaccion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.lotes.beans.Partida;
import mx.org.kaana.mantic.lotes.reglas.AdminLotes;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticLotesEspecial")
@ViewScoped
public class Especial extends Accion implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Especial.class);
  private static final long serialVersionUID= 317393488565639367L;
	
  protected Long idPivote;
  protected List<UISelectEntity> articulos;

	@PostConstruct
  @Override
  protected void init() {		
    try {
      this.articulos= new ArrayList<>();
      UISelectEntity articulo= new UISelectEntity(-1L);
      articulo.put("codigo", new Value("codigo", "SELECCIONE"));
      articulo.put("articulo", new Value("articulo", ""));
      articulo.put("idArticulo", new Value("idArticulo", -1L));
      this.articulos.add(articulo);
      this.attrs.put("articulos", articulos);      
      super.init();
      this.idPivote= -1L;
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
          this.orden.getLote().setIdLoteTipo(3L);
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
  public void doUpdateAlmacenes() {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
  	  params.put("sucursales", this.orden.getLote().getIdEmpresa());
 			List<UISelectEntity> almacenes= UIEntity.seleccione("TcManticAlmacenesDto", "origen", params, columns, "clave");
      this.attrs.put("almacenes", almacenes);
			if(!Objects.equals(almacenes, null) && !almacenes.isEmpty()) {
				if(this.accion.equals(EAccion.AGREGAR))
				  this.orden.getLote().setIkAlmacen(almacenes.get(0));
				else {
					int index= almacenes.indexOf(new UISelectEntity(this.orden.getLote().getIdAlmacen()));
					if(index>= 0)
					  this.orden.getLote().setIkAlmacen(almacenes.get(index));
					else
  				  this.orden.getLote().setIkAlmacen(almacenes.get(0));
				} // else	
			} // if
      else
			  this.orden.getLote().setIkAlmacen(new UISelectEntity(-1L));
      this.doUpdateClases();
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
  
	public void doUpdateClases() {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
 			List<UISelectEntity> clases= UIEntity.seleccione("TcManticTiposClasesDto", params, columns, "clave");
      this.attrs.put("clases", clases);
			if(!Objects.equals(clases, null) && !clases.isEmpty()) {
				if(this.accion.equals(EAccion.AGREGAR))
				  this.orden.getLote().setIkTipoClase(clases.get(0));
				else {
					int index= clases.indexOf(new UISelectEntity(this.orden.getLote().getIdTipoClase()));
					if(index>= 0)
					  this.orden.getLote().setIkTipoClase(clases.get(index));
					else
  				  this.orden.getLote().setIkTipoClase(clases.get(0));
				} // else	
      } // if  
      else 
 			  this.orden.getLote().setIkTipoClase(new UISelectEntity(-1L));
      this.doUpdateProductos();
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
	public void doUpdateProductos() {
    List<Columna> columns      = new ArrayList<>();    
    Map<String, Object> params = new HashMap<>();
		List<UISelectEntity> clases= (List<UISelectEntity>)this.attrs.get("clases");
    try {
      int index= clases.indexOf(this.orden.getLote().getIkTipoClase());
      if(index>= 0) {
        this.orden.getLote().setIkTipoClase(clases.get(index));
        params.put("sortOrder", "order by tc_mantic_notas_detalles.id_nota_detalle");
        params.put("idLote", this.orden.getLote().getIdLote());
        params.put("idAlmacen", this.orden.getLote().getIkAlmacen().getKey());
        params.put("clase", this.orden.getLote().getIkTipoClase().toString("clave"));
        columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("articulo", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("kilos", EFormatoDinamicos.MILES_CON_DECIMALES));
        params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
        if(!Objects.equals(this.orden.getLote().getIdTipoClase(), -1L))
          this.lazyModel= new FormatCustomLazy("VistaLotesDto", "clase", params, columns);
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
  public void doRecover(Partida row) {
    if(Objects.equals(row.getIdTipoClase(), this.orden.getLote().getIdTipoClase())) {
      if(Objects.equals(ESql.DELETE, row.getSql()))
        row.setSql(ESql.SELECT);
      this.toSumPartidas("promedios");
    } // if  
    else
      JsfBase.addMessage("No se puede recuperar porque no coincide con la clase", ETipoMensaje.ERROR);      			 
  }
  
  @Override
  protected void toSumPartidas(String idXml) {
    Long idArticulo= -1L;
    Double cantidad= 0D;
    Double sum     = 0D;
    try {
      this.articulos.clear();
      UISelectEntity articulo= new UISelectEntity(-1L);
      articulo.put("codigo", new Value("codigo", "SELECCIONE"));
      articulo.put("articulo", new Value("articulo", ""));
      articulo.put("idArticulo", new Value("idArticulo", -1L));
      this.articulos.add(articulo);
      for (Partida item: this.orden.getLote().getPartidas()) {
        if(!Objects.equals(ESql.DELETE, item.getSql())) {
          sum+= item.getCantidad();
          
          articulo= new UISelectEntity(item.getIdArticulo());
          articulo.put("codigo", new Value("codigo", item.getCodigo()));
          articulo.put("articulo", new Value("articulo", item.getArticulo()));
          articulo.put("idArticulo", new Value("idArticulo", item.getIdArticulo()));
          int index= this.articulos.indexOf(articulo);
          if(index< 0)
            this.articulos.add(articulo);
        } // if  
        if(cantidad< item.getCantidad()) {
          idArticulo= item.getIdArticulo();
          cantidad  = item.getCantidad();
          this.idPivote= item.getIdNotaDetalle();
        } // if  
      } // if
      this.attrs.put("total", "Suma de kilos: <strong>"+ Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, sum)+ "</strong>");
      this.orden.getLote().setIkArticulo(new UISelectEntity(idArticulo));
      this.toLoadMermas(idXml);
      this.attrs.put("articulos", this.articulos);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
  }
 
	public String doIdentificar(Partida row) {
		return Objects.equals(row.getIdNotaDetalle(), this.idPivote) ? "janal-tr-yellow": "";
	} 

  @Override
  protected void toCheckPartidas() {
    try {      
      int index= 0;
      int count= 0;
      while (index<this.orden.getLote().getPartidas().size()) {
        Partida item= this.orden.getLote().getPartidas().get(index);
        if(!Objects.equals(item.getIdTipoClase(), this.orden.getLote().getIdTipoClase())) {
          if(Objects.equals(item.getSql(), ESql.INSERT))
            this.orden.getLote().getPartidas().remove(index);
          else {
            item.setSql(ESql.DELETE);
            index++;
          } // if  
        } // if
        else {
          if(Objects.equals(item.getSql(), ESql.DELETE))
            item.setSql(ESql.UPDATE);
          index++;
          count++;
        } // else  
      } // while
      this.orden.getLote().setItTipoClase(this.orden.getLote().getIdTipoClase());
      if(count<= 0) {
        this.idPivote = -1L;
        this.lazyMerma= null;
        this.attrs.put("total", "Suma de kilos: <strong>0</strong>");
     } //   
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  } 

}