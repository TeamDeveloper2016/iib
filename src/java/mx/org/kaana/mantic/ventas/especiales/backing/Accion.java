package mx.org.kaana.mantic.ventas.especiales.backing;

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
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.ventas.beans.SaldoCliente;
import mx.org.kaana.mantic.ventas.beans.TicketVenta;
import mx.org.kaana.mantic.ventas.caja.especial.beans.Producto;
import mx.org.kaana.mantic.ventas.caja.especial.reglas.AdminEspecial;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value= "manticVentasEspecialesAccion")
@ViewScoped
public class Accion extends mx.org.kaana.mantic.ventas.backing.Accion implements Serializable {

	private static final Log LOG = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID = 327393488562639267L;
   
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
		Long idCliente = Constantes.VENTA_AL_PUBLICO_GENERAL_ID_KEY;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
			LOG.warn("Inicializando admin orden");
			LOG.warn("Accion:" + eaccion.name());
      switch (eaccion) {
        case AGREGAR:											
          this.setAdminOrden(new AdminEspecial(new TicketVenta(-1L)));
					this.saldoCliente= new SaldoCliente();
					this.attrs.put("consecutivo", "");		
					idCliente= Long.valueOf(this.attrs.get("idCliente").toString());
					if(idCliente!= null && !idCliente.equals(-1L))
						this.doAsignaClienteInicial(idCliente);
					else
						this.attrs.put("mostrarCorreos", true);
          break;
        case MODIFICAR:			
        case CONSULTAR:			
					LOG.warn("Atributes:" + this.attrs.toString());
          this.setAdminOrden(new AdminEspecial((TicketVenta)DaoFactory.getInstance().toEntity(TicketVenta.class, "TcManticVentasDto", "detalle", this.attrs)));					
    			this.attrs.put("sinIva", this.getAdminOrden().getIdSinIva().equals(1L));
					idCliente= ((TicketVenta)getAdminOrden().getOrden()).getIdCliente();
					if(idCliente!= null && !idCliente.equals(-1L)){
						this.doAsignaClienteInicial(idCliente);
						this.doLoadSaldos(idCliente);
            if(((TicketVenta)this.getAdminOrden().getOrden()).getIdManual()== 1L) {
              this.attrs.put("observaciones", ((TicketVenta)this.getAdminOrden().getOrden()).getObservaciones());
              this.attrs.put("transporta", ((TicketVenta)this.getAdminOrden().getOrden()).getTransporta());
              this.getAdminOrden().getTotales().setIva(((TicketVenta)this.getAdminOrden().getOrden()).getImpuestos());
              this.getAdminOrden().getTotales().setSubTotal(((TicketVenta)this.getAdminOrden().getOrden()).getSubTotal());
              this.getAdminOrden().getTotales().setTotal(((TicketVenta)this.getAdminOrden().getOrden()).getTotal());
            } // if
					} // if
					this.toLoadSucursales();
          break;
      } // switch
			this.attrs.put("consecutivo", "");
      this.attrs.put("paginator", Boolean.FALSE);
      this.attrs.put("indexPaginator", 0);
			// this.doResetDataTable();      
			this.toLoadCatalogos();
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad
  
  @Override
	public void doAsignaTicketAbierto() {
		Map<String, Object>params = new HashMap<>();
		try {
			params.put("idVenta", ((Entity)this.attrs.get("selectedCuentaAbierta")).get("idVenta"));
			this.setAdminOrden(new AdminEspecial((TicketVenta)DaoFactory.getInstance().toEntity(TicketVenta.class, "TcManticVentasDto", "detalle", params)));
    	this.unlockVentaExtends(Long.valueOf(params.get("idVenta").toString()), (Long)this.attrs.get("ticketLock"));
			this.attrs.put("ticketLock", Long.valueOf(params.get("idVenta").toString()));
			this.attrs.put("sinIva", this.getAdminOrden().getIdSinIva().equals(1L));
			this.attrs.put("consecutivo", ((TicketVenta)this.getAdminOrden().getOrden()).getConsecutivo());
			this.toLoadCatalogos();
			this.doAsignaClienteTicketAbierto();
			this.doResetDataTable();      
			UIBackingUtilities.execute("jsArticulos.initArrayArt(" + String.valueOf(getAdminOrden().getArticulos().size()-1) + ");");
		} // try
		catch (Exception e) {
			mx.org.kaana.libs.formato.Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // doAsignaTicketAbierto
  
  @Override
  protected void loadOrdenVenta() {		  
    super.loadOrdenVenta();
    ((TicketVenta)this.getAdminOrden().getOrden()).setIdEspecial(1L);
  }

  @Override
	public void doLoadTicketAbiertos() {	
		List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= new HashMap<>();
		try {
			params.put("sortOrder", "");
			params.put("idEmpresa", this.attrs.get("idEmpresa"));			
			columns.add(new Columna("cuenta", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("cliente", EFormatoDinamicos.MAYUSCULAS));
			params.put(Constantes.SQL_CONDICION, this.toCondicion().concat(" and (tc_mantic_ventas.id_especial= 1)"));
			this.lazyCuentasAbiertas= new FormatLazyModel("VistaVentasDto", "lazy", params, columns);			
			UIBackingUtilities.execute("PF('dlgOpenTickets').show();");			
			UIBackingUtilities.resetDataTable("tablaTicketsAbiertos");
		} // try
		catch (Exception e) {
			mx.org.kaana.libs.formato.Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} 
  
	@Override
	protected void toMoveData(UISelectEntity articulo, Integer index) throws Exception {
		Producto temporal= (Producto) getAdminOrden().getArticulos().get(index);
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
        temporal.setPesoEstimado(articulo.toDouble("pesoEstimado"));
        
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
  
  @Override
	public String doCancelar() {   
  	JsfBase.setFlashAttribute("idVenta", ((TicketVenta)this.getAdminOrden().getOrden()).getIdVenta());
    return "/Paginas/Mantic/Ventas/filtro".concat(Constantes.REDIRECIONAR);
  } // doCancelar
 
  @Override
  public String doAceptar() {
    String regresar              = null;
    Boolean continuar            = Boolean.TRUE;
		UISelectEntity cliente       = null;
		List<UISelectEntity> clientes= null;
		try {
			clientes= (List<UISelectEntity>) this.attrs.get("clientes");
      if(clientes!= null && !clientes.isEmpty()) {
        int index= clientes.indexOf(((TicketVenta)this.getAdminOrden().getOrden()).getIkCliente());
        if(index>= 0) 
          cliente= clientes.get(index); 
        if(!Objects.equals(cliente, null) && !cliente.isEmpty()) {
          if(!Objects.equals(cliente.toLong("idCliente"), Constantes.VENTA_AL_PUBLICO_GENERAL_ID_KEY) && 
              Objects.equals(cliente.toLong("idCredito"), 1L) && 
              cliente.toDouble("disponible")< this.getAdminOrden().getTotales().getTotal()) {
            // JsfBase.addMessage("El cliente no tiene suficiente línea de crédito suficiente !", ETipoMensaje.ERROR); 
            String disponible= Numero.formatear(Numero.MONEDA_CON_DECIMALES, cliente.toDouble("disponible"));
            UIBackingUtilities.execute("janal.show([{summary: 'Precaución:', detail: 'El cliente no tiene suficiente línea de crédito, disponible: ["+ disponible+ "].'}], 'error');"); 
            UIBackingUtilities.execute("janal.desbloquear();");
            continuar= Boolean.FALSE;
          } // if  
        } // if 
      } // if
      if(continuar)
        regresar= super.doAceptar();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		    
    return regresar;
  }  
  
}