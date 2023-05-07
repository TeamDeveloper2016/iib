package mx.org.kaana.mantic.ventas.especiales.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.ventas.beans.SaldoCliente;
import mx.org.kaana.mantic.ventas.beans.TicketVenta;
import mx.org.kaana.mantic.ventas.caja.especial.reglas.AdminEspecial;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value= "manticVentasEspecialesAccion")
@ViewScoped
public class Accion extends mx.org.kaana.mantic.ventas.backing.Accion implements Serializable {

	private static final Log LOG = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID = 327393488562639267L;
   
	@Override
  public void doLoad() {
    EAccion eaccion= null;
		Long idCliente = Constantes.VENTA_AL_PUBLICO_GENERAL_ID_KEY;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
			LOG.warn("Inicializando admin orden.");
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
	} // doLoadTicketAbiertos		
  
}