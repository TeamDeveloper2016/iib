package mx.org.kaana.mantic.ventas.caja.especial.backing;

import java.io.Serializable;
import java.sql.Date;
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
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.clientes.beans.Domicilio;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.db.dto.TcManticArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDto;
import mx.org.kaana.mantic.enums.EEstatusVentas;
import mx.org.kaana.mantic.enums.EPrecioArticulo;
import mx.org.kaana.mantic.ventas.beans.TicketVenta;
import mx.org.kaana.mantic.ventas.caja.beans.Facturacion;
import mx.org.kaana.mantic.ventas.caja.beans.Pago;
import mx.org.kaana.mantic.ventas.caja.beans.VentaFinalizada;
import mx.org.kaana.mantic.ventas.caja.especial.beans.Producto;
import mx.org.kaana.mantic.ventas.caja.especial.reglas.AdminEspecial;
import mx.org.kaana.mantic.ventas.caja.reglas.CreateTicket;
import mx.org.kaana.mantic.ventas.caja.reglas.Transaccion;
import mx.org.kaana.mantic.ventas.reglas.CambioUsuario;
import mx.org.kaana.mantic.ventas.reglas.MotorBusqueda;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value= "manticVentasCajaEspecialAccion")
@ViewScoped
public class Accion extends mx.org.kaana.mantic.ventas.caja.backing.Accion implements Serializable {

  private static final Log LOG = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 327393488565639327L;
  
	@PostConstruct
  @Override
  protected void init() {				
    super.init();
    this.attrs.put("titulo", Objects.equals(Configuracion.getInstance().getEmpresa(), "iib")? "embarques": "notas de remisión");
    this.idEspecial= 1L;
  }
  
	@Override
  public void doLoad() {
    EAccion eaccion= null;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));			
			this.attrs.put("mostrarCorreos", true);
      switch (eaccion) {
        case AGREGAR:											
          this.setAdminOrden(new AdminEspecial(new TicketVenta(-1L)));
          break;
        case MODIFICAR:			
        case CONSULTAR:			
          this.setAdminOrden(new AdminEspecial((TicketVenta)DaoFactory.getInstance().toEntity(TicketVenta.class, "TcManticVentasDto", "detalle", this.attrs)));					
    			this.attrs.put("sinIva", this.getAdminOrden().getIdSinIva().equals(1L));					
          break;
      } // switch
			this.attrs.put("servicio", ((TicketVenta)getAdminOrden().getOrden()).getIdServicio() > 0L);
			this.attrs.put("pago", new Pago(getAdminOrden().getTotales()));
			this.toLoadCatalogos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad
 
	@Override
  public void doFindArticulo(Integer index) {
		try {
    	List<UISelectEntity> articulos= (List<UISelectEntity>)this.attrs.get("articulos");
	    UISelectEntity articulo  = (UISelectEntity)this.attrs.get("articulo");
	    UISelectEntity encontrado= (UISelectEntity)this.attrs.get("encontrado");
			if(encontrado!= null) {
				articulo= encontrado;
				this.attrs.remove("encontrado");
			} // else
			else 
				if(articulo== null)
					articulo= new UISelectEntity(new Entity(-1L));
				else
					if(articulos.indexOf(articulo)>= 0) 
						articulo= articulos.get(articulos.indexOf(articulo));
					else
						articulo= articulos.get(0);
			if(articulo.size()> 1) {
				int position= this.getAdminOrden().getArticulos().indexOf(new Producto(articulo.toLong("idArticulo"), this.costoLibre));
				if(articulo.size()> 1 && position>= 0) {
					if(index!= position)
						UIBackingUtilities.execute("jsArticulos.exists('"+ articulo.toString("propio")+ "', '"+ articulo.toString("nombre")+ "', "+ position+ ");");
				} // if	
				else
					this.toMoveData(articulo, index);
			} // else
			else 
					this.toMoveData(articulo, index);
		} // try
	  catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	} 

  @Override
	protected void generateNewVenta() throws Exception {
		List<Producto> articulosPivote= new ArrayList<>();
		try {
			for(Articulo vigente: getAdminOrden().getArticulos())				
				articulosPivote.add((Producto)vigente);			
			this.getAdminOrden().getArticulos().clear();
			for(Producto addArticulo : articulosPivote)
				this.toMoveArticulo(addArticulo, -1);			
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
	} // generateNewVenta  
  
  @Override
	public void doReCalculatePreciosArticulos(boolean descuentoVigente, Long idCliente) {
		MotorBusqueda motor= null;
		Entity articulo    = null;
		String descuento   = null;
		String sinDescuento= "0";
		try {			
      UISelectEntity cliente= (UISelectEntity)this.attrs.get("clienteSeleccion");      
      List<UISelectEntity> clientes= (List<UISelectEntity>) this.attrs.get("clientesSeleccion");					
			if(cliente!= null && cliente.size()<= 1 && clientes!= null && !clientes.isEmpty())
        cliente= clientes.get(clientes.indexOf(cliente));
      // SI EL CLIENTE TIENE UN PRECIO ESPECIAL ENTONCES DEBEMOS DE CONSIDERAR EL PRECIO BASE * POR EL PORCENTAJE ASIGNADO AL CLIENTE
			if(!this.getAdminOrden().getArticulos().isEmpty()) {
        if(cliente!= null && !cliente.isEmpty() && cliente.toDouble("especial")!= 0D) {
          Double costo  = 0D;
          Double mayoreo= 0D;
          Double venta  = 0D;
          Double factor = 0D;
          for(Articulo original: getAdminOrden().getArticulos()) {
            original.setIdCliente(idCliente);
            if(original.getIdArticulo()!= null && !original.getIdArticulo().equals(-1L)) {
              //* ESTE METODO SE TIENE QUE AJUSTAR CON EL VALOR DEL CLIENTE */
              motor   = new MotorBusqueda(original.getIdArticulo(), idCliente);
              articulo= motor.toPrecioCliente(this.getPrecio());
              mayoreo = articulo.toDouble("mayoreo");
              costo   = articulo.toDouble(this.getPrecio());
              venta   = Numero.toRedondearSat(costo* (1+ (articulo.toDouble("iva")/ 100))* (1+ (cliente.toDouble("especial")/ 100)));
              factor  = Numero.toRedondearSat(venta* 100/ costo/ 100);
              // SI AUN CUANDO EL PRECIO ESPECIAL ASIGNADO AL CLIENTE NO ES MENOR QUE EL PRECIO SUGERIDO SE RESPETA EL PRECIO MENOR DEL ARTICULO
              if(mayoreo< venta) {
                costo = mayoreo;
                factor= Numero.toRedondearSat(costo* 100/ articulo.toDouble("precio")/ 100);
              } // if  
              else
                costo= venta;
              ((Producto)original).setDescripcionPrecio("ESPECIAL");
              original.setFactor(factor);
              original.setDescuento("0");
              original.setValor(costo);
              original.setCosto(costo);
            } // if
          } // for  
        } // if
        else {
          for(Articulo original: getAdminOrden().getArticulos()) {
            original.setIdCliente(idCliente);
            if(original.getIdArticulo()!= null && !original.getIdArticulo().equals(-1L)) {
              motor   = new MotorBusqueda(original.getIdArticulo(), this.getAdminOrden().getIdCliente());
              articulo= motor.toPrecioCliente(this.getPrecio());
              // ESTO ES PARA CORREGIR CUANDO SE CAMBIE UN CLIENTE DE ESPECIAL A O NO ESPECIAL Y VUELVA A CALCULAR LOS IMPORTES
              if("ESPECIAL".equals(((Producto)original).getDescripcionPrecio())) {
                ((Producto)original).setIdComodin(-1L);
                ((Producto)original).setDescripcionPrecio(this.getPrecio());
              } // if
              original.setCosto(articulo.toDouble(this.getPrecio()));
              if(!original.getCosto().equals(articulo.toDouble("mayoreo")) && !original.getCosto().equals(articulo.toDouble("medioMayoreo"))) {
                original.setValor(articulo.toDouble(this.getPrecio()));
                original.setCosto(articulo.toDouble(this.getPrecio()));
                ((Producto)original).setDescripcionPrecio(this.getPrecio());
              } // if		
              else
                ((Producto)original).setDescuentoAsignado(true);						
              if(descuentoVigente) {
                descuento= this.toDescuentoVigente(original.getIdArticulo(), idCliente);
                if(descuento!= null)
                  original.setDescuento(descuento);							
              } // if
              else
                original.setDescuento(sinDescuento);
              original.setFactor(1D);
            } // if
          } // for  
        } // else
        if(this.getAdminOrden().getArticulos().size()> 1) {
          this.getAdminOrden().toCalculate();
          this.getAdminOrden().cleanPrecioDescuentoArticulo();
          UIBackingUtilities.update("@(.filas) @(.recalculo) @(.informacion)");
        } // if
      } // if			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // doReCalculatePreciosArticulos  
  
  @Override
	public void doAplicarDescuento(Integer index) {
		Boolean isIndividual       = false;
		Boolean isGlobal           = false;
		Boolean isMedioMayoreo     = false;
		Boolean isMayoreo          = false;
		CambioUsuario cambioUsuario= null;
		String cuenta              = null;
		String contrasenia         = null;
		Double global              = 0D;
		Double globalCalculado     = 0D;
		Boolean recalculate        = false;
		try {
			if(!getAdminOrden().getArticulos().isEmpty()){
				cuenta       = (String)this.attrs.get("usuarioDescuento");
				contrasenia  = (String)this.attrs.get("passwordDescuento");
				cambioUsuario= new CambioUsuario(cuenta, contrasenia);
				if(cambioUsuario.validaPrivilegiosDescuentos()){
					this.attrs.put("decuentoAutorizadoActivo", true);				
					((Producto)getAdminOrden().getArticulos().get(index)).setDescuentoActivo(true);
					isIndividual= Boolean.valueOf(this.attrs.get("isIndividual").toString());
					isGlobal= Boolean.valueOf(this.attrs.get("isGlobal").toString());
					isMedioMayoreo= Boolean.valueOf(this.attrs.get("isMedioMayoreo").toString());
					isMayoreo= Boolean.valueOf(this.attrs.get("isMayoreo").toString());
					if(isIndividual) {
						this.attrs.put("tipoDecuentoAutorizadoActivo", INDIVIDUAL);
						this.getAdminOrden().getArticulos().get(index).setDescuento(this.attrs.get("descuentoIndividual").toString());
						if(getAdminOrden().getArticulos().get(index).autorizedDiscount())
							UIBackingUtilities.execute("jsArticulos.divDiscount('".concat(this.attrs.get("descuentoIndividual").toString()).concat("');"));
						else
							JsfBase.addMessage("No es posble aplicar el descuento, el descuento es superior a la utilidad", ETipoMensaje.ERROR);
					} // if
					else if (isGlobal) {		
						this.attrs.put("tipoDecuentoAutorizadoActivo", GLOBAL);
						global= Double.valueOf(this.attrs.get("descuentoGlobal").toString());
						this.getAdminOrden().toCalculate();
						globalCalculado= (this.getAdminOrden().getTotales().getTotal() * global) / 100;
						if(globalCalculado < this.getAdminOrden().getTotales().getUtilidad()){
							((TicketVenta)this.getAdminOrden().getOrden()).setGlobal(globalCalculado);
							this.getAdminOrden().setDescuento(global.toString());
							this.doUpdateDescuento();
							this.getAdminOrden().toCalculate();
						} // if
						else
							JsfBase.addMessage("No es posble aplicar el descuento, el descuento es superior a la utilidad", ETipoMensaje.ERROR);
					} // else if
					else if (isMedioMayoreo){
						this.attrs.put("tipoDecuentoAutorizadoActivo", MEDIO_MAYOREO);
						this.setPrecio("medioMayoreo");
						this.getAdminOrden().getArticulos().get(index).setCosto(this.getAdminOrden().getArticulos().get(index).toEntity().toDouble("medioMayoreo"));
						((Producto)getAdminOrden().getArticulos().get(index)).setDescripcionPrecio("medioMayoreo");
						((Producto)getAdminOrden().getArticulos().get(index)).setDescuentoAsignado(true);
						((Producto)getAdminOrden().getArticulos().get(index)).setDescuentos(0D);						
						((Producto)getAdminOrden().getArticulos().get(index)).setDescuento("");						
						((Producto)getAdminOrden().getArticulos().get(index)).toCalculate();
						recalculate= true;
					} // else if
					else if (isMayoreo){
						this.attrs.put("tipoDecuentoAutorizadoActivo", MAYOREO);
						setPrecio("mayoreo");
						getAdminOrden().getArticulos().get(index).setCosto(getAdminOrden().getArticulos().get(index).toEntity().toDouble("mayoreo"));
						((Producto)getAdminOrden().getArticulos().get(index)).setDescripcionPrecio("mayoreo");
						((Producto)getAdminOrden().getArticulos().get(index)).setDescuentoAsignado(true);
						((Producto)getAdminOrden().getArticulos().get(index)).setDescuentos(0D);												
						((Producto)getAdminOrden().getArticulos().get(index)).setDescuento("");						
						((Producto)getAdminOrden().getArticulos().get(index)).toCalculate();
						recalculate= true;
					} // else if
					else{
						this.attrs.put("decuentoAutorizadoActivo", false);					
						this.attrs.put("tipoDecuentoAutorizadoActivo", MENUDEO);
						setPrecio("menudeo");
						((Producto)getAdminOrden().getArticulos().get(index)).setDescuentoActivo(false);
						getAdminOrden().getArticulos().get(index).setCosto(getAdminOrden().getArticulos().get(index).toEntity().toDouble("menudeo"));
						((Producto)getAdminOrden().getArticulos().get(index)).setDescripcionPrecio("menudeo");
						((Producto)getAdminOrden().getArticulos().get(index)).setDescuentoAsignado(false);
						((Producto)getAdminOrden().getArticulos().get(index)).setDescuentos(0D);						
						((Producto)getAdminOrden().getArticulos().get(index)).setDescuento("");						
						((Producto)getAdminOrden().getArticulos().get(index)).toCalculate();
					} // else
				} // if
				else
					JsfBase.addMessage("El usuario no tiene privilegios o el usuario y la contraseña son incorrectos", ETipoMensaje.ERROR);
			} // if
			if(recalculate){
				getAdminOrden().toCalculate();
				UIBackingUtilities.update("@(.filas) @(.recalculo) @(.informacion)");
			} // if
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally{			
			this.attrs.put("descuentoIndividual", 0);
			this.attrs.put("descuentoGlobal", 0);
			this.attrs.put("usuarioDescuento", "");
			this.attrs.put("passwordDescuento", "");
			this.attrs.put("tipoDescuento", 1L);
		} // finally
	} // doAplicarDescuento
  
	@Override
	protected void toMoveData(UISelectEntity articulo, Integer index) throws Exception {
		Producto temporal= (Producto)this.getAdminOrden().getArticulos().get(index);
		Map<String, Object> params= new HashMap<>();
		try {
			if(articulo.size()> 1) {
        Double precioVenta= articulo.toDouble(this.getPrecio());
        temporal.setDescripcionPrecio(this.getPrecio());
        UISelectEntity cliente= (UISelectEntity)this.attrs.get("clienteSeleccion");      
        List<UISelectEntity> clientes= (List<UISelectEntity>) this.attrs.get("clientesSeleccion");					
        if(cliente!= null && cliente.size()<= 1 && clientes!= null && !clientes.isEmpty())
          cliente= clientes.get(clientes.indexOf(cliente));
        // SI EL CLIENTE TIENE UN PRECIO ESPECIAL ENTONCES DEBEMOS DE CONSIDERAR EL PRECIO BASE * POR EL PORCENTAJE ASIGNADO AL CLIENTE
        if(cliente!= null && !cliente.isEmpty() && cliente.toDouble("especial")!= 0D) {
          Double mayoreo= articulo.toDouble("mayoreo");
          Double venta  = Numero.toRedondearSat(articulo.toDouble("precio")* (1+ (articulo.toDouble("iva")/ 100))* (1+ (cliente.toDouble("especial")/ 100)));
          Double factor = Numero.toRedondearSat(venta* 100/ articulo.toDouble("precio")/ 100);
          // SI AUN CUANDO EL PRECIO ESPECIAL ASIGNADO AL CLIENTE NO ES MENOR QUE EL PRECIO SUGERIDO SE RESPETA EL PRECIO MENOR DEL ARTICULO
          if(mayoreo< venta) {
            precioVenta= mayoreo;
            factor     = Numero.toRedondearSat(precioVenta* 100/ articulo.toDouble("precio")/ 100);
          } // if  
          else
            precioVenta= venta;
				  temporal.setDescripcionPrecio("ESPECIAL");
          temporal.setFactor(factor);
        } // if
				this.doSearchArticulo(articulo.toLong("idArticulo"), index);
				params.put("idArticulo", articulo.toLong("idArticulo"));
				params.put("idProveedor", this.getAdminOrden().getIdProveedor());
				params.put("idAlmacen", this.getAdminOrden().getIdAlmacen());
				temporal.setKey(articulo.toLong("idArticulo"));
				temporal.setIdArticulo(articulo.toLong("idArticulo"));
				temporal.setFabricante(articulo.toString("fabricante"));
				temporal.setIdProveedor(this.getAdminOrden().getIdProveedor());
				temporal.setIdRedondear(articulo.toLong("idRedondear"));
				temporal.setImagen(articulo.toString("archivo"));
				Value codigo= (Value)DaoFactory.getInstance().toField("TcManticArticulosCodigosDto", "codigo", params, "codigo");
				temporal.setCodigo(codigo== null? "": codigo.toString());
				temporal.setPropio(articulo.toString("propio"));
				temporal.setNombre(articulo.toString("nombre"));
				temporal.setValor(precioVenta);
				temporal.setCosto(precioVenta);
				temporal.setIva(articulo.toDouble("iva"));				
				temporal.setSat(articulo.get("sat").getData()!= null ? articulo.toString("sat") : "");				
				temporal.setDescuento(this.getAdminOrden().getDescuento());
				temporal.setExtras(this.getAdminOrden().getExtras());				
				// SON ARTICULOS QUE ESTAN EN LA FACTURA MAS NO EN LA ORDEN DE COMPRA
				if(articulo.containsKey("descuento")) 
				  temporal.setDescuento(articulo.toString("descuento"));
				if(articulo.containsKey("cantidad")) {
				  temporal.setCantidad(articulo.toDouble("cantidad"));
				  temporal.setSolicitados(articulo.toDouble("cantidad"));
				} // if	
				if(temporal.getCantidad()<= 0D)					
					temporal.setCantidad(1D);
				temporal.setMenudeo(articulo.toDouble("menudeo"));				
 				temporal.setDescuentoActivo((Boolean)this.attrs.get("decuentoAutorizadoActivo"));
				temporal.setUltimo(this.attrs.get("ultimo")!= null);
				temporal.setSolicitado(this.attrs.get("solicitado")!= null);
				temporal.setUnidadMedida(articulo.toString("unidadMedida"));
				temporal.setPrecio(articulo.toDouble("precio"));	
				// RECUPERA EL STOCK DEL ALMACEN MAS SABER SI YA FUE HUBO UN CONTEO O NO
				Entity inventario= (Entity)DaoFactory.getInstance().toEntity("TcManticInventariosDto", "stock", params);
				if(inventario!= null && inventario.size()> 0) {
				  temporal.setStock(inventario.toDouble("stock"));
				  temporal.setIdAutomatico(inventario.toLong("idAutomatico"));
				} // if
				if(index== getAdminOrden().getArticulos().size()- 1) {
					this.getAdminOrden().getArticulos().add(new Producto(-1L, this.costoLibre));
					this.getAdminOrden().toAddUltimo(this.getAdminOrden().getArticulos().size()- 1);
					UIBackingUtilities.execute("jsArticulos.update("+ (this.getAdminOrden().getArticulos().size()- 1)+ ");");
				} // if	
				UIBackingUtilities.execute("jsArticulos.callback('"+ articulo.getKey()+ "');");
				this.getAdminOrden().toCalculate();
			} // if	
			else
				temporal.setNombre("<span class='janal-color-orange'>EL ARTICULO NO EXISTE EN EL CATALOGO !</span>");
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
  protected void toMoveArticulo(Producto articulo, Integer index) throws Exception {
		Producto temporal               = (Producto)articulo.clone();
		TcManticArticulosDto artTemporal= null;
		Map<String, Object> params      = new HashMap<>();
		EPrecioArticulo eprecioArticulo = null;
		try {			
			this.doSearchArticulo(articulo.getIdArticulo(), index);
			params.put("idArticulo", articulo.getIdArticulo());
			params.put("idProveedor", this.getAdminOrden().getIdProveedor());
			params.put("idAlmacen", this.getAdminOrden().getIdAlmacen());
			temporal.setKey(articulo.getIdArticulo());
			temporal.setIdArticulo(articulo.getIdArticulo());
			temporal.setIdProveedor(this.getAdminOrden().getIdProveedor());
			temporal.setIdRedondear(articulo.getIdRedondear());
			Value codigo= (Value)DaoFactory.getInstance().toField("TcManticArticulosCodigosDto", "codigo", params, "codigo");
			temporal.setCodigo(codigo== null? "": codigo.toString());
			temporal.setPropio(articulo.getPropio());
			temporal.setNombre(articulo.getNombre());
			eprecioArticulo= EPrecioArticulo.fromNombre(this.getPrecio().toUpperCase());
			artTemporal= (TcManticArticulosDto) DaoFactory.getInstance().findById(TcManticArticulosDto.class, articulo.getIdArticulo());
			if(artTemporal!= null) {
				switch(eprecioArticulo) {
					case MAYOREO:
						temporal.setValor(artTemporal.getMayoreo());
						temporal.setCosto(artTemporal.getMayoreo());
						break;
					case MEDIO_MAYOREO:
						temporal.setValor(artTemporal.getMedioMayoreo());
						temporal.setCosto(artTemporal.getMedioMayoreo());
						break;
					case MENUDEO:
						temporal.setValor(artTemporal.getMenudeo());
						temporal.setCosto(artTemporal.getMenudeo());
						break;
				}	// switch	 	
			} // if
			temporal.setIva(articulo.getIva());
			temporal.setSat(articulo.getSat());
			temporal.setDescuento(this.getAdminOrden().getDescuento());
			temporal.setExtras(this.getAdminOrden().getExtras());										
			temporal.setCantidad(articulo.getCantidad());
			temporal.setCuantos(0D);
			temporal.setUltimo(this.attrs.get("ultimo")!= null);
			temporal.setSolicitado(this.attrs.get("solicitado")!= null);
			temporal.setUnidadMedida(articulo.getUnidadMedida());
			temporal.setPrecio(articulo.getPrecio());				
			Value stock= (Value)DaoFactory.getInstance().toField("TcManticInventariosDto", "stock", params, "stock");
			temporal.setStock(stock== null? 0D: stock.toDouble());				
			this.getAdminOrden().getArticulos().add(temporal);
			this.getAdminOrden().toAddUltimo(this.getAdminOrden().getArticulos().size()- 1);
			UIBackingUtilities.execute("jsArticulos.update("+ (this.getAdminOrden().getArticulos().size()- 1)+ ");");				
			UIBackingUtilities.execute("jsArticulos.callback('"+ articulo.getKey()+ "');");
			this.getAdminOrden().toAddArticulo(this.getAdminOrden().getArticulos().size()- 1);		
			if(this.attrs.get("paginator")== null || !(boolean)this.attrs.get("paginator"))
  			this.attrs.put("paginator", this.getAdminOrden().getArticulos().size()> Constantes.REGISTROS_LOTE_TOPE);
		} // try
		finally {
			Methods.clean(params);
		} // finally
	} // toMoveDataArt  
  
  @Override
	protected void validatePrecioAsignado(Integer index) {
		List<UISelectEntity> clientesSeleccion= null;
		UISelectEntity seleccion              = null;		
		try {
			seleccion= (UISelectEntity) this.attrs.get("clienteSeleccion");
			clientesSeleccion= (List<UISelectEntity>) this.attrs.get("clientesSeleccion");					
			if(seleccion!= null && clientesSeleccion!= null && !clientesSeleccion.isEmpty()) {
        UISelectEntity cliente= clientesSeleccion.get(clientesSeleccion.indexOf(seleccion));
        String tipoVenta= (cliente.toDouble("especial")== 0D)? Cadena.toBeanName(cliente.toString("tipoVenta").toLowerCase()): "ESPECIAL";
				((Producto)getAdminOrden().getArticulos().get(index)).setDescripcionPrecio(tipoVenta);
      } // if  
			else
				((Producto)getAdminOrden().getArticulos().get(index)).setDescripcionPrecio("menudeo");
		} // try
		catch (Exception e) {			
			Error.mensaje(e);
		} // catch		
	} // validatePrecioAsignado

  @Override
  public void doAceptar() {  
    Transaccion transaccion               = null;
		Boolean validarCredito                = true;
		Boolean creditoVenta                  = null;
		CreateTicket ticket                   = null;
		VentaFinalizada ventaFinalizada       = null;
		UISelectEntity cliente                = null;
		UISelectEntity seleccionado           = null;
		List<UISelectEntity> clientesSeleccion= null;
		String tipoTicket                     = null;
    try {	
			creditoVenta     = (Boolean) this.attrs.get("creditoVenta");
			cliente          = (UISelectEntity) this.attrs.get("clienteSeleccion");	
			clientesSeleccion= (List<UISelectEntity>) this.attrs.get("clientesSeleccion");
			seleccionado     = clientesSeleccion.get(clientesSeleccion.indexOf(cliente));
			if(creditoVenta)
				validarCredito= this.doValidaCreditoVenta();
			if(validarCredito) {
				ventaFinalizada= this.loadVentaFinalizada();
        // ACTUALIZAR EL REGIMEN FISCAL DEL CLIENTE 
        DaoFactory.getInstance().update(ventaFinalizada.getCliente());
				transaccion = new Transaccion(ventaFinalizada);
				if (transaccion.ejecutar(EAccion.REPROCESAR)) {
					if(ventaFinalizada.isFacturar() && !ventaFinalizada.getApartado()) {
						UIBackingUtilities.addCallbackParam("facturacionOk", true);
						UIBackingUtilities.addCallbackParam("facturacion", new Facturacion(((TicketVenta)this.getAdminOrden().getOrden()).getIdVenta(), seleccionado.getKey(), transaccion.getCorreosFactura(), seleccionado.toString("razonSocial"), ventaFinalizada.getIdTipoPago(), transaccion.getFacturaPrincipal().getIdFactura(), transaccion.getFacturaPrincipal().getIdFacturama(), transaccion.getFacturaPrincipal().getSelloSat(), ventaFinalizada.getTicketVenta().getIdClienteDomicilio()));						
					} // if
					else
						UIBackingUtilities.addCallbackParam("facturacionOk", false);
					tipoTicket= ventaFinalizada.getApartado() ? "APARTADO" : (ventaFinalizada.isFacturar() ? "FACTURA" : (ventaFinalizada.isCredito() ? "CREDITO" : "VENTA DE MOSTRADOR"));
          if(ventaFinalizada.isCredito()) {
            this.toPrintTicket(((TicketVenta)this.getAdminOrden().getOrden()).getIdVenta(), ((TicketVenta)this.getAdminOrden().getOrden()).getRegistro());
          } // if
          else {
            if(tipoTicket.equals("FACTURA"))						
              ticket= new CreateTicket((AdminEspecial)this.getAdminOrden(), (Pago) this.attrs.get("pago"), tipoTicket, seleccionado.toString("razonSocial"));
            else
              if(Objects.equals(seleccionado.getKey(), Constantes.VENTA_AL_PUBLICO_GENERAL_ID_KEY))
                ticket= new CreateTicket(((AdminEspecial)this.getAdminOrden()), (Pago)this.attrs.get("pago"), tipoTicket);
              else
                ticket= new CreateTicket(((AdminEspecial)this.getAdminOrden()), (Pago)this.attrs.get("pago"), tipoTicket, seleccionado.toString("razonSocial"));
            UIBackingUtilities.execute("jsTicket.imprimirTicket('" + ticket.getPrincipal().getClave()  + "-" + ((TicketVenta)(((AdminEspecial)this.getAdminOrden()).getOrden())).getTicket() + "','" + ticket.toHtml() + "');");
            UIBackingUtilities.execute("jsTicket.clicTicket();");
          } // if  
					JsfBase.addMessage("Se finalizó el pago del ticket de venta", ETipoMensaje.INFORMACION);
          this.toSaveCostoCliente(ventaFinalizada);
					this.setAdminOrden(new AdminEspecial(new TicketVenta()));
					this.attrs.put("pago", new Pago(getAdminOrden().getTotales()));
					this.attrs.put("clienteSeleccion", null);
					this.init();
				} // if
				else 
					JsfBase.addMessage("Ocurrió un error al registrar el ticket de venta", ETipoMensaje.ERROR);
			} // if
			else
				JsfBase.addMessage(this.attrs.get("mensajeErrorCredito").toString(), ETipoMensaje.ERROR);      			
      UIBackingUtilities.update("contenedorGrupos");
      UIBackingUtilities.update("@(.filas)");
      UIBackingUtilities.update("@(.recalculo)");
      UIBackingUtilities.update("contenedorGrupos:deudorPago");
      UIBackingUtilities.update("contenedorGrupos:deudor");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doAccion
  
  @Override
  public String doAceptarCotizacion() {	  
		UISelectEntity ticketAbierto= null;
    Transaccion transaccion     = null;
    String regresar             = null;
		CreateTicket ticket         = null;
		boolean confirmacion        = false;
    try {				
			ticketAbierto= (UISelectEntity) this.attrs.get("ticketAbierto");			
			if(ticketAbierto== null && !this.getAdminOrden().getArticulos().isEmpty() && (this.getAdminOrden().getArticulos().size() > 1 || (this.getAdminOrden().getArticulos().size()== 1 && (this.getAdminOrden().getArticulos().get(0).getIdArticulo()!= null && !this.getAdminOrden().getArticulos().get(0).getIdArticulo().equals(-1L)))) && this.getAdminOrden().getTotales().getImporte()> 0D) {
				this.getAdminOrden().getTotales().setTotal(this.getAdminOrden().getTotales().getImporte());
				this.loadOrdenVenta();				
			} // if
      this.getAdminOrden().validatePrecioArticulo();
      this.getAdminOrden().toAdjustArticulos();
      this.getAdminOrden().cleanPrecioDescuentoArticulo();				
      TicketVenta ticketVenta= (TicketVenta)this.getAdminOrden().getOrden();
 			ticketVenta.setTotal(this.getAdminOrden().getTotales().getTotal());
			ticketVenta.setSubTotal(this.getAdminOrden().getTotales().getSubTotal());
			ticketVenta.setDescuentos(this.getAdminOrden().getTotales().getDescuentos());
			ticketVenta.setImpuestos(this.getAdminOrden().getTotales().getIva());
			ticketVenta.setUtilidad(this.getAdminOrden().getTotales().getUtilidad());
      transaccion= new Transaccion(ticketVenta, this.getAdminOrden().getArticulos());
			confirmacion= transaccion.ejecutar(EAccion.MOVIMIENTOS);				
			if(confirmacion) {
				((TicketVenta)(((AdminEspecial)this.getAdminOrden()).getOrden())).setCotizacion(transaccion.getCotizacion());
				ticket= new CreateTicket(((AdminEspecial)this.getAdminOrden()), (Pago)this.attrs.get("pago"), "COTIZACIÓN");				
				UIBackingUtilities.execute("jsTicket.imprimirTicket('" + ticket.getPrincipal().getClave()  + "-" + transaccion.getCotizacion() + "','" + ticket.toHtml() + "');");
				UIBackingUtilities.execute("jsTicket.clicTicket();");
				JsfBase.addMessage("Se finalizó la cotización del ticket.", ETipoMensaje.INFORMACION);								
				this.setAdminOrden(new AdminEspecial(new TicketVenta()));
				this.attrs.put("pago", new Pago(this.getAdminOrden().getTotales()));
				this.attrs.put("clienteSeleccion", null);
				this.init();
			} // if
			else
				JsfBase.addMessage("Ocurrió un error al generar la cotización.", ETipoMensaje.ERROR);			  
      this.refreshTicketsAbiertos(); 
      UIBackingUtilities.update("ticketsAbiertos");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAceptarCotizacion
  
	@Override
	public void doLoadTicketAbiertos() {		
		List<UISelectEntity> ticketsAbiertos= null;
		Map<String, Object>params           = new HashMap<>();
		List<Columna> campos                = new ArrayList<>();
		List<Columna> columns               = new ArrayList<>();
		try {
			this.loadRangoFechas(false);
			this.loadCajas();			
			params.put("sortOrder", "");
			params.put("idEmpresa", this.attrs.get("idEmpresa"));
			campos.add(new Columna("cliente", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			params.put(Constantes.SQL_CONDICION, this.toCondicion(true).concat(" and (tc_mantic_ventas.id_especial= 1)"));
			ticketsAbiertos= UIEntity.build("VistaVentasDto", "lazy", params, campos, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("ticketsAbiertos", ticketsAbiertos);			
			this.attrs.put("ticketAbierto", new UISelectEntity("-1"));
			this.setAdminOrden(new AdminEspecial(new TicketVenta()));
			this.attrs.put("pago", new Pago(getAdminOrden().getTotales()));
			this.attrs.put("pagarVenta", false);
			this.attrs.put("facturarVenta", false);
			this.attrs.put("cobroVenta", false);
			this.attrs.put("clienteAsignado", false);
			this.attrs.put("tabIndex", 0);
			this.setDomicilio(new Domicilio());
			this.attrs.put("registroCliente", new TcManticClientesDto());
      this.setIkRegimenFiscal(new UISelectEntity(-1L));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			params.clear();
			params.put("sucursales", this.attrs.get("idEmpresa"));			
      this.attrs.put("almacenes", UIEntity.build("TcManticAlmacenesDto", "almacenPrincipal", params, columns));
 			List<UISelectEntity> almacenes= (List<UISelectEntity>)this.attrs.get("almacenes");
			if(!almacenes.isEmpty()) 
				((TicketVenta)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(0));
			params.clear();
			this.unlockVentaExtends(-1L, (Long)this.attrs.get("ticketLock"));			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // doLoadTicketAbiertos  
  
	@Override
	public void doAsignaTicketAbierto() {
		Map<String, Object>params           = new HashMap<>();		
		UISelectEntity ticketAbierto        = null;
		UISelectEntity ticketAbiertoPivote  = null;
		List<UISelectEntity> ticketsAbiertos= null;
		Date actual                         = null;
		String tipo                         = null;
    MotorBusqueda motorBusqueda         = null;
		try {			
			ticketAbierto= (UISelectEntity) this.attrs.get("ticketAbierto");
			params.put("idVenta", ticketAbierto!= null? ticketAbierto.getKey(): -1L);
			this.setDomicilio(new Domicilio());
			this.attrs.put("registroCliente", new TcManticClientesDto());
			if(ticketAbierto!= null && !ticketAbierto.getKey().equals(-1L)) {				
				this.unlockVentaExtends(ticketAbierto.getKey(), (Long)this.attrs.get("ticketLock"));									
				this.attrs.put("ticketLock", ticketAbierto.getKey());
				ticketsAbiertos= (List<UISelectEntity>) this.attrs.get("ticketsAbiertos");
        int index= ticketsAbiertos.indexOf(ticketAbierto);
				if(!ticketsAbiertos.isEmpty() && index>= 0) {
				  ticketAbiertoPivote= ticketsAbiertos.get(index);
					this.attrs.put("ticketAbierto", ticketAbiertoPivote); 
					this.setAdminOrden(new AdminEspecial((TicketVenta)DaoFactory.getInstance().toEntity(TicketVenta.class, "TcManticVentasDto", "detalle", params), true));
					tipo= ticketAbiertoPivote.toString("tipo");
					this.attrs.put("tipo", tipo);
					this.attrs.put("mostrarApartado", tipo.equals(EEstatusVentas.APARTADOS.name()));								
					if(tipo.equals(EEstatusVentas.COTIZACION.name()) || tipo.equals(EEstatusVentas.APARTADOS.name())) {					
						if(tipo.equals(EEstatusVentas.APARTADOS.name()))
							this.asignaFechaApartado();
						actual= new Date(Calendar.getInstance().getTimeInMillis());
						if(actual.after(((TicketVenta)getAdminOrden().getOrden()).getVigencia()))
							this.generateNewVenta();					
					} // if
					this.attrs.put("observaciones", ((TicketVenta)this.getAdminOrden().getOrden()).getObservaciones());
					this.attrs.put("transporta", ((TicketVenta)this.getAdminOrden().getOrden()).getTransporta());
					this.attrs.put("sinIva", this.getAdminOrden().getIdSinIva().equals(1L));
					this.attrs.put("consecutivo", ((TicketVenta)this.getAdminOrden().getOrden()).getConsecutivo());
					this.toLoadCatalogos();
					this.doAsignaClienteTicketAbierto();
					this.attrs.put("pagarVenta", true);
					this.attrs.put("cobroVenta", true);				
					this.attrs.put("tabIndex", 0);
					this.attrs.put("creditoCliente", ticketAbiertoPivote.toLong("idCredito").equals(1L));
				} // if
			} // if
			else {				
				this.unlockVentaExtends(-1L, (Long)this.attrs.get("ticketLock"));
				this.attrs.put("ticketLock", -1L);
				this.setAdminOrden(new AdminEspecial(new TicketVenta()));
        motorBusqueda= new MotorBusqueda(Long.valueOf(this.attrs.get("idEmpresa").toString()));
        this.attrs.put("clienteGeneral", motorBusqueda.toClienteDefault());
   			this.attrs.put("clienteAsignado", false);
				this.attrs.put("clienteSeleccion", null);
				this.attrs.put("pagarVenta", false);
				this.attrs.put("facturarVenta", false);
				this.attrs.put("cobroVenta", false);
				this.attrs.put("clienteAsignado", false);
				this.attrs.put("tabIndex", 0);
				this.attrs.put("creditoCliente", false);		
				this.attrs.put("observaciones", "");
				this.attrs.put("transporta", "");
			} // else			
			this.validaFacturacion();
			this.attrs.put("pago", new Pago(getAdminOrden().getTotales()));
			this.doLoadSaldos(((TicketVenta)this.getAdminOrden().getOrden()).getIdCliente());
			this.saldoCliente.setTotalVenta(getAdminOrden().getTotales().getTotal());
			UIBackingUtilities.update("deudor");
			UIBackingUtilities.update("deudorPago");
			if(tipo!= null && tipo.equals(EEstatusVentas.APARTADOS.name()))
				this.asignaAbonoApartado();
			this.doActivarCliente();
      this.toLoadRegimenesFiscales();      
			UIBackingUtilities.execute("jsArticulos.initArrayArt(" + String.valueOf(getAdminOrden().getArticulos().size()-1) + ");");
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // doAsignaTicketAbierto

  private void toSaveCostoCliente(VentaFinalizada ventaFinalizada) {
    mx.org.kaana.mantic.catalogos.clientes.convenios.reglas.Transaccion transaccion= null;
    try {      
      transaccion= new mx.org.kaana.mantic.catalogos.clientes.convenios.reglas.Transaccion(ventaFinalizada.getTicketVenta().getIdCliente(), ventaFinalizada.getArticulos());
      if (!transaccion.ejecutar(EAccion.PROCESAR))
        JsfBase.addMessage("No se guardaron los precios por cliente !", ETipoMensaje.INFORMACION);
    } // try
    catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch	
    finally {
      transaccion= null;
    } // finally
  }

  @Override
	public String doClientes() {
    String regresar= super.doClientes();
  	JsfBase.setFlashAttribute("regreso", "/Paginas/Mantic/Ventas/Caja/Especial/accion");								
    return regresar;
  }
  
}
