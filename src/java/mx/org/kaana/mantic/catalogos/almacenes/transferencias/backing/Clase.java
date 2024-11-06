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
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
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

@Named(value= "manticCatalogosAlmacenesTransferenciasClase")
@ViewScoped
public class Clase extends Cambios implements Serializable {

  private static final long serialVersionUID= 817393488565639361L;
  
	@PostConstruct
	@Override
	protected void init() {
		try {
      this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.PROCESAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.accion= Objects.equals(EAccion.COPIAR, this.accion)? EAccion.PROCESAR: this.accion;
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("idTransferencia", JsfBase.getFlashAttribute("idTransferencia"));
      this.attrs.put("idTerminado", Boolean.FALSE);
      this.attrs.put("promedio", 0D);
      this.doLoad();
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
        this.transferencia.setIdTransferenciaTipo(6L);
        this.transferencia.setIdSolicito(null);
        transaccion= new Transaccion(this.transferencia, articulos);
        if(transaccion.ejecutar(EAccion.COPIAR)) {
          regresar= this.doCancelar();
          UIBackingUtilities.execute("janal.back(' gener\\u00F3 la transferencia ', '"+ this.transferencia.getConsecutivo()+ "');");
          JsfBase.addMessage("Se registró el traspaso de forma correcta", ETipoMensaje.INFORMACION);
        } // if
        else
          JsfBase.addMessage("Ocurrió un error al registrar el traspaso", ETipoMensaje.ERROR);
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
  protected void toLoadProductos(Long idAlmacen) {
		List<Columna> columns         = new ArrayList<>();
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> productos= null;
    try {
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("stock", EFormatoDinamicos.MILES_CON_DECIMALES));
  		params.put("sucursales", this.transferencia.getIdEmpresa());
  		params.put("idAlmacen", idAlmacen);
  		params.put("idTerminado", "1, 2");
  		params.put("idTipoClase", this.transferencia.getIdTipoClase());
  		params.put("idArticuloTipo", this.transferencia.getIdArticuloTipo());
      params.put(Constantes.SQL_CONDICION, "(tc_mantic_articulos_codigos.codigo not like '%".concat(Constantes.CODIGO_EMBOLSADO).concat("')"));
      productos= (List<UISelectEntity>) UIEntity.seleccione("VistaAlmacenesDto", "productos", params, columns, 20L, "codigo");
      this.attrs.put("productos", productos);
      this.attrs.put("stock", 0D);
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
  		params.put("idArticuloTipo", this.transferencia.getIdArticuloTipo());
      params.put(Constantes.SQL_CONDICION, "(tc_mantic_articulos.id_articulo!= "+ this.transferencia.getIkProducto().getKey()+ ") and (tc_mantic_articulos_codigos.codigo not like '%".concat(Constantes.CODIGO_EMBOLSADO).concat("')"));
      articulos= (List<UISelectEntity>) UIEntity.seleccione("VistaAlmacenesDto", "productos", params, columns, 20L, "codigo");
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
 
  public void doCostoPromedio() {
    Map<String, Object> params= new HashMap<>();
    try {
      this.doCalculate();
  		params.put("idEmpresa", this.transferencia.getIdEmpresa());
  		params.put("idArticulo", this.transferencia.getIdArticulo());
      // FALTA RECUPERAR EL PRECIO PROMEDIO DE ESTE PRODUCTO Y FALTA EN LA TRANSACCIÓN RECALCULAR EL PRECIO PROMEDIO POR ESTA TRASPASO DE CLASE
      Entity costo= (Entity)DaoFactory.getInstance().toEntity("TcManticArticulosPromediosDto", "ultimo", params);
      if(!Objects.equals(costo, null) && !costo.isEmpty())
        this.detalle.setReal(costo.toDouble("promedio"));
      else
        this.detalle.setReal(0D);
      this.attrs.put("promedio", this.detalle.getReal());
   } // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(params);
    } // finally
  }
  
  public void doCheckCosto() {
    try {
      if(!Objects.equals(this.detalle.getReal(), (double)this.attrs.get("promedio")))
        JsfBase.addMessage("El costo promedio es diferente al último registrado ["+ this.attrs.get("promedio")+ "]", ETipoMensaje.INFORMACION);
    } // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
  }
  
  
}