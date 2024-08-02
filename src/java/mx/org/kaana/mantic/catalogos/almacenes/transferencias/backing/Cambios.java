package mx.org.kaana.mantic.catalogos.almacenes.transferencias.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.catalogos.almacenes.transferencias.reglas.Transaccion;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 21/06/2024
 *@time 23:19:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticCatalogosAlmacenesTransferenciasCambios")
@ViewScoped
public class Cambios extends Simples implements Serializable {

  private static final long serialVersionUID= 827393488565639361L;
  
	@PostConstruct
	@Override
	protected void init() {
		try {
      this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.PROCESAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.accion= Objects.equals(EAccion.LISTAR, this.accion)? EAccion.PROCESAR: this.accion;
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("idTransferencia", JsfBase.getFlashAttribute("idTransferencia"));
      this.attrs.put("idTerminado", Boolean.FALSE);
      this.doLoad();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} 

  @Override
  public void doLoadProductos() {
    this.transferencia.setIkDestino(this.transferencia.getIkAlmacen());
    this.toLoadProductos(this.transferencia.getIdAlmacen());
  }
  
  @Override
  public void doUpdateOrigen() {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
  	  params.put("idEmpresa", this.transferencia.getIdEmpresa());
  	  params.put("tipo", "1");
 			List<UISelectEntity> almacenes= UIEntity.seleccione("TcManticAlmacenesDto", "especial", params, columns, "clave");
			if(!Objects.equals(almacenes, null) && !almacenes.isEmpty()) {
				if(this.accion.equals(EAccion.PROCESAR))
				  this.transferencia.setIkAlmacen(almacenes.get(0));
				else {
					int index= almacenes.indexOf(new UISelectEntity(this.transferencia.getIdAlmacen()));
					if(index>= 0)
					  this.transferencia.setIkAlmacen(almacenes.get(index));
					else
  				  this.transferencia.setIkAlmacen(almacenes.get(0));
				} // else	
			} // if
      else
			  this.transferencia.setIkAlmacen(new UISelectEntity(-1L));
      this.attrs.put("destinos", almacenes);
      this.doLoadArticulos();
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
  public void doUpdateDestino() {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
  	  params.put("idEmpresa", this.transferencia.getIdEmpresa());
  	  params.put("tipo", "3");
 			List<UISelectEntity> destinos= UIEntity.seleccione("TcManticAlmacenesDto", "especial", params, columns, "clave");
			if(!Objects.equals(destinos, null) && !destinos.isEmpty()) {
				if(this.accion.equals(EAccion.PROCESAR))
				  this.transferencia.setIkDestino(destinos.get(0));
				else {
					int index= destinos.indexOf(new UISelectEntity(this.transferencia.getIdDestino()));
					if(index>= 0)
					  this.transferencia.setIkDestino(destinos.get(index));
					else
  				  this.transferencia.setIkDestino(destinos.get(0));
				} // else	
			} // if
      else
			  this.transferencia.setIkDestino(new UISelectEntity(-1L));
      this.attrs.put("almacenes", destinos);
      this.doLoadProductos();
    } // try  
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
  }
  
  @Override
	public String doAceptar() {
		Transaccion transaccion = null;
    String regresar         = null;
		List<Articulo> articulos= new ArrayList<>();
		try {
      if(this.checkStock()) {
        articulos.add(this.detalle);
        this.transferencia.setIdTransferenciaTipo(5L);
        this.transferencia.setIdSolicito(null);
        transaccion= new Transaccion(this.transferencia, articulos);
        if(transaccion.ejecutar(EAccion.LISTAR)) {
          regresar= this.doCancelar();
          UIBackingUtilities.execute("janal.back(' gener\\u00F3 la transferencia ', '"+ this.transferencia.getConsecutivo()+ "');");
          JsfBase.addMessage("Se registró la conversión de forma correcta", ETipoMensaje.INFORMACION);
        } // if
        else
          JsfBase.addMessage("Ocurrió un error al registrar la conversión", ETipoMensaje.ERROR);
      } // if  
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		finally {
			Methods.clean(articulos);
		} // finally
		return regresar;
	} 
 
  @Override
  public void doUpdateProductos() {  
    super.doUpdateProductos();
    this.doLoadArticulos();
  }
  
  @Override
  public void doLoadArticulos() {
    this.toLoadArticulos(this.transferencia.getIdAlmacen());
  }
  
  @Override
  protected void toLoadArticulos(Long idAlmacen) {
		List<Columna> columns         = new ArrayList<>();
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> articulos= null;
    try {
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("stock", EFormatoDinamicos.MILES_CON_DECIMALES));
  		params.put("sucursales", this.transferencia.getIdEmpresa());
  		params.put("idAlmacen", idAlmacen);
  		params.put("idTerminado", "1, 2");
  		params.put("idTipoClase", this.transferencia.getIdTipoClase());
  		params.put("idArticuloTipo", 1L);
      params.put(Constantes.SQL_CONDICION, "tc_mantic_articulos.id_articulo!= "+ this.transferencia.getIkProducto().getKey());
      articulos= (List<UISelectEntity>) UIEntity.seleccione("VistaAlmacenesSimplesDto", "productos", params, columns, 20L, "codigo");
      this.attrs.put("articulos", articulos);
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