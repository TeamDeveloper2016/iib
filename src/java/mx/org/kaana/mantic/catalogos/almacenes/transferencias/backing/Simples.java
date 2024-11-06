package mx.org.kaana.mantic.catalogos.almacenes.transferencias.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.almacenes.transferencias.beans.Transferencia;
import mx.org.kaana.mantic.catalogos.almacenes.transferencias.reglas.Transaccion;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 21/06/2024
 *@time 23:19:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticCatalogosAlmacenesTransferenciasSimples")
@ViewScoped
public class Simples extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID= 127393488565639361L;
  
  protected EAccion accion;
  protected Transferencia transferencia;
  protected Articulo detalle;

  public Transferencia getTransferencia() {
    return transferencia;
  }

  public void setTransferencia(Transferencia transferencia) {
    this.transferencia = transferencia;
  }
  
	public Articulo getDetalle() {
		return detalle;
	}

	public void setDetalle(Articulo detalle) {
		this.detalle=detalle;
	}

	@PostConstruct
	@Override
	protected void init() {
		try {
      this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.PROCESAR: (EAccion)JsfBase.getFlashAttribute("accion");
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("idTransferencia", JsfBase.getFlashAttribute("idTransferencia"));
      this.attrs.put("idTerminado", Boolean.TRUE);
      this.attrs.put("stock", 0D);
      this.doLoad();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} 
  
	private void toInitTransferencia() {
    this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
		this.transferencia= new Transferencia(
			-1L, // Long idSolicito, 
			8L, // Long idTransferenciaEstatus, 
			3L, // Long idTransferenciaTipo, 
			new Long(Calendar.getInstance().get(Calendar.YEAR)), // Long ejercicio, 
			Calendar.getInstance().get(Calendar.YEAR)+ "00000", // String consecutivo, 
			JsfBase.getIdUsuario(), // Long idUsuario, 
			-1L, // Long idAlmacen, 
			"", // String observaciones, 
			-1L, // Long idDestino, 
			JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende(), // Long idEmpresa, 
			1L, // Long orden,
			-1L, // Long idTransferencia
			-1L, // Long idTipoClase
      -1L, // Long idArticulo
      4L // Long idArticuloTipo
		);
		this.detalle= new Articulo(-1L);
		this.detalle.setCalculado(1D);
	}
	
  public void doLoad() {
    Map<String, Object> params= new HashMap<>();
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      switch (this.accion) {
        case PROCESAR:
				 	this.toInitTransferencia();
          break;
        case MODIFICAR:
        case CONSULTAR:
          params.put("idTransferencia", this.attrs.get("idTransferencia"));
          this.transferencia= (Transferencia) DaoFactory.getInstance().toEntity(Transferencia.class, "TcManticTransferenciasDto", "detalle", params);
          this.transferencia.init();
					if(this.transferencia!= null) {
						this.detalle= (Articulo)DaoFactory.getInstance().toEntity(Articulo.class, "VistaAlmacenesTransferenciasDto", "detalle", this.attrs);
  					if(Objects.equals(this.detalle, null)) 
							this.toInitTransferencia();
            else
              this.transferencia.setIkProducto(new UISelectEntity(this.detalle.getIdArticulo()));
					} // if
					else
  				 	this.toInitTransferencia();
          break;
      } // switch    
      this.toLoadEmpresas();
      this.toLoadClases();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
		finally {
      Methods.clean(params);
    } // finally
  } // doLoad
  
  private void toLoadEmpresas() {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      if(JsfBase.isAdminEncuestaOrAdmin())
			  params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      else
			  params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
 			List<UISelectEntity> empresas= UIEntity.build("TcManticEmpresasDto", "empresas", params, columns);
      this.attrs.put("empresas", empresas);
			if(!Objects.equals(empresas, null) && !empresas.isEmpty()) {
				if(Objects.equals(this.accion, EAccion.PROCESAR))
				  this.transferencia.setIkEmpresa(empresas.get(0));
				else {
					int index= empresas.indexOf(new UISelectEntity(this.transferencia.getIdEmpresa()));
					if(index>= 0)
					  this.transferencia.setIkEmpresa(empresas.get(index));
					else
  				  this.transferencia.setIkEmpresa(empresas.get(0));
				} // else	
      } // if  
      else 
 			  this.transferencia.setIkEmpresa(new UISelectEntity(-1L));
      this.doUpdateAlmacenes();
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
  
  public void doUpdateAlmacenes() {
    this.doUpdateOrigen();
    this.doUpdateDestino();
  }
  
  protected void doUpdateOrigen() {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
  	  params.put("idEmpresa", this.transferencia.getIdEmpresa());
  	  params.put("tipo", "3");
 			List<UISelectEntity> almacenes= UIEntity.seleccione("TcManticAlmacenesDto", "especial", params, columns, "clave");
      this.attrs.put("almacenes", almacenes);
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

	private void toLoadClases() {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      params.put(Constantes.SQL_CONDICION, "id_terminado in ("+ ((Boolean)this.attrs.get("idTerminado")? "1)": "1, 2)"));
 			List<UISelectEntity> clases= UIEntity.seleccione("TcManticTiposClasesDto", params, columns, "clave");
      this.attrs.put("clases", clases);
			if(!Objects.equals(clases, null) && !clases.isEmpty()) {
				if(this.accion.equals(EAccion.PROCESAR))
				  this.transferencia.setIkTipoClase(clases.get(0));
				else {
					int index= clases.indexOf(new UISelectEntity(this.transferencia.getIdTipoClase()));
					if(index>= 0)
					  this.transferencia.setIkTipoClase(clases.get(index));
					else
  				  this.transferencia.setIkTipoClase(clases.get(0));
				} // else	
      } // if  
      else 
 			  this.transferencia.setIkTipoClase(new UISelectEntity(-1L));
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
    try {
      this.doLoadArticulos();
      this.doLoadProductos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  protected void doUpdateDestino() {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
  	  params.put("idEmpresa", this.transferencia.getIdEmpresa());
  	  params.put("tipo", "1");
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
      this.attrs.put("destinos", destinos);
      this.doLoadProductos();
    } // try  
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
  }
  
  public void doLoadArticulos() {
    this.toLoadArticulos(this.transferencia.getIdAlmacen());
  }
  
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
  		params.put("idTerminado", (Boolean)this.attrs.get("idTerminado")? "1": "1, 2");
  		params.put("idTipoClase", this.transferencia.getIdTipoClase());
  		params.put("idArticuloTipo", 4L);
  		params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
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

  public void doLoadProductos() {
    this.toLoadProductos(this.transferencia.getIdDestino());
  }
  
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
  		params.put("idArticuloTipo", 1L);
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
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

	public String doAceptar() {
		Transaccion transaccion = null;
    String regresar         = null;
		List<Articulo> articulos= new ArrayList<>();
		try {
      if(this.checkStock()) {
        articulos.add(this.detalle);
        this.transferencia.setIdSolicito(null);
        transaccion= new Transaccion(this.transferencia, articulos);
        if(transaccion.ejecutar(this.accion)) {
          regresar= this.doCancelar();
          UIBackingUtilities.execute("janal.back(' gener\\u00F3 la transferencia ', '"+ this.transferencia.getConsecutivo()+ "');");
          JsfBase.addMessage("Se registró la transferencia de correcta", ETipoMensaje.INFORMACION);
        } // if
        else
          JsfBase.addMessage("Ocurrió un error al registrar la transferencia", ETipoMensaje.ERROR);
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
  
  public String doCancelar() {
    JsfBase.setFlashAttribute("idTransferencia", this.transferencia.getIdTransferencia());
		return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
	} 
 
  public void doCalculate() {
    this.checkStock();
  }
  
  public void doUpdateProductos() {
    List<UISelectEntity> productos= (List<UISelectEntity>)this.attrs.get("productos");
    try {
      if(!Objects.equals(productos, null) && !productos.isEmpty()) {
        int index= productos.indexOf(this.transferencia.getIkProducto());
        if(index>= 0)
          this.transferencia.setIkProducto(productos.get(index));
        if(this.transferencia.getIkProducto().containsKey("idArticulo")) {
          this.detalle.setIdArticulo(this.transferencia.getIkProducto().toLong("idArticulo"));
          this.detalle.setPropio(this.transferencia.getIkProducto().toString("codigo"));
          this.detalle.setNombre(this.transferencia.getIkProducto().toString("nombre"));
          this.detalle.setSat(this.transferencia.getIkProducto().toString("sat"));
        } // if
        else
          JsfBase.addMessage("No se tiene un producto seleccionado en el almacen de origen !", ETipoMensaje.ERROR);
      }
      else
        JsfBase.addMessage("No se tiene un producto seleccionado en el almacen de origen !", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
  }
  
  public void doUpdateTerminado() {
    this.toLoadClases();   
    this.doUpdateClases();   
  }
 
  protected Boolean checkStock() {
    Boolean regresar              = Boolean.TRUE;
    List<UISelectEntity> articulos= (List<UISelectEntity>)this.attrs.get("articulos");
    try {
      if(!Objects.equals(articulos, null) && !articulos.isEmpty()) {
        int index= articulos.indexOf(this.transferencia.getIkArticulo());
        if(index>= 0)
          this.transferencia.setIkArticulo(articulos.get(index));
        if(this.transferencia.getIkArticulo().containsKey("cantidad")) {
          this.attrs.put("stock", this.transferencia.getIkArticulo().toDouble("cantidad"));
          this.detalle.setIdOrdenDetalle(this.transferencia.getIkArticulo().toLong("idArticulo"));
          this.detalle.setCodigo(this.transferencia.getIkArticulo().toString("codigo"));
          Double stock= this.transferencia.getIkArticulo().toDouble("cantidad");
          if(stock< this.detalle.getCantidad())
            JsfBase.addMessage("No se cuenta con el suficiente stock en el almacen de origen !", ETipoMensaje.ERROR);
          else
            regresar= Boolean.TRUE;
        } // if
        else
          JsfBase.addMessage("No se tiene un producto seleccionado en el almacen de origen !", ETipoMensaje.ERROR);
      }
      else
        JsfBase.addMessage("No se tiene un producto seleccionado en el almacen de origen !", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
    return regresar;
  }
 
  public void doUpdateTipo() {
    try {
      this.attrs.put("stock", 0D);
      this.transferencia.setIkAlmacen(new UISelectEntity(-1L));
      this.transferencia.setIkProducto(new UISelectEntity(-1L));
      this.transferencia.setIkArticulo(new UISelectEntity(-1L));
      this.detalle.setCantidad(0D);
      this.detalle.setReal(0D);
      this.doUpdateAlmacenes();
    } // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
  }
  
}