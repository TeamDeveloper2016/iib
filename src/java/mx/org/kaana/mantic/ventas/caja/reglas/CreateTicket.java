package mx.org.kaana.mantic.ventas.caja.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.procesos.acceso.beans.Sucursal;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Encriptar;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.enums.ETiposContactos;
import mx.org.kaana.mantic.ventas.beans.TicketVenta;
import mx.org.kaana.mantic.ventas.caja.beans.Abono;
import mx.org.kaana.mantic.ventas.caja.beans.Pago;
import mx.org.kaana.mantic.ventas.reglas.AdminTickets;

public class CreateTicket implements Serializable {

  private static final long serialVersionUID = 2708311485895423674L;

	private AdminTickets ticket;
	protected Pago pago;
	protected Sucursal principal;
	protected String tipo;
	protected String cliente;
	
	public CreateTicket(Pago pago, String tipo) {
		this(null, pago, tipo);
	}
	
	public CreateTicket(AdminTickets ticket, Pago pago, String tipo) {
		this(ticket, pago, tipo, "");
	} 
	
	public CreateTicket(AdminTickets ticket, Pago pago, String tipo, String cliente) {
		this.ticket = ticket;
		this.pago   = pago;
		this.tipo   = tipo;
		this.cliente= cliente;
    if(this.ticket!= null && this.ticket.getOrden()!= null && (this.ticket.getOrden() instanceof TicketVenta) && ((TicketVenta)this.ticket.getOrden()).getIdManual()== 1L) {
      this.pago.getTotales().setIva(((TicketVenta)this.ticket.getOrden()).getImpuestos());
      this.pago.getTotales().setSubTotal(((TicketVenta)this.ticket.getOrden()).getSubTotal());
      this.pago.getTotales().setTotal(((TicketVenta)this.ticket.getOrden()).getTotal());
    } // if
		this.init();
	} // CreateTicket
	
	protected void init() {		
		Sucursal matriz= null;		
		for(Sucursal sucursal: JsfBase.getAutentifica().getSucursales()) {
			if(sucursal.isMatriz())
				matriz= sucursal;					
		} // for		
		this.principal= matriz!= null ? matriz : JsfBase.getAutentifica().getEmpresa();		
	} // init

	public Sucursal getPrincipal() {
		return principal;
	}
	
	public String toHtml() throws Exception {
		StringBuilder sb= new StringBuilder();
		sb.append(this.toHeader());
		sb.append(this.toBlackBar());
		sb.append(this.toCredito());
		sb.append(this.toNoTicket());
		sb.append(this.toTipoTransaccion());
		sb.append(this.toFecha());
		sb.append(this.toTable());			
		sb.append(this.toHeaderTable());
		sb.append(this.toFinishTable());
		sb.append(this.toArticulos());
		sb.append(this.toPagos());
		sb.append(this.toFinishTable());
		sb.append(this.toVendedor());
		sb.append(this.toCajero());
		sb.append(this.toFooter());		
		return sb.toString();
	} // toHtml
	
	protected String toHeader() throws Exception{
    StringBuilder regresar= new StringBuilder("<div id=\"ticket\" style=\"width: 90px; max-width: 80px;\">");
    String image= null;
		try {
      regresar.append("<table style=\"width: 290px;\"><tr>");
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "iib":
      		image= this.toImageIib(); 
          break;
        case "kalan":
      		image= this.toImageKalan(); 
          break;
        case "tsaak":
      		//image= this.toImageTsaak(); 
      		image= this.toImageKalan(); 
          break;
        default:  
      		image= this.toImageKalan(); 
      } // swtich
      regresar.append("<td><img src=\"").append(image).append("\" alt=\"Logotipo\" width=\"70px\"/></td>");
      regresar.append("<td><p style=\"text-align: center;align-content: center;font-family: sans-serif;font-size: 13px;font-weight: bold;\">");
      regresar.append(this.principal.getTitulo());
      regresar.append("</p>");
      regresar.append(this.toDomicilio());
      regresar.append("</td>");
      regresar.append("</tr></table>");		
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
		return regresar.toString();
	} // toEncabezado;
	
	protected String toBlackBar() {
		StringBuilder regresar= new StringBuilder();
		regresar.append("<p style=\"width: 290px;text-align: center;font-family: sans-serif;font-size: 13px;font-weight: bold;background: black;color: white\">").append(Configuracion.getInstance().getEmpresa("slogan")).append("</p>");
		return regresar.toString();
	} // toBlackBar
	
	private String toDomicilio() throws Exception{
		StringBuilder regresar= new StringBuilder();
		regresar.append("<p style=\"text-align: center;align-content: center;font-family: sans-serif;font-size: 12px;\">").append(this.toFindDomicilio());	
    if(!Cadena.isVacio(Configuracion.getInstance().getEmpresa("portal")))
 		  regresar.append("<br><span style=\"width: 290px;font-size: 12px;\">").append(Configuracion.getInstance().getEmpresa("portal")).append("<br>").append(Configuracion.getInstance().getEmpresa("compras")).append("</span>");		
    regresar.append("</p>");
		return regresar.toString();
	} // toDomicilio
	
	protected String toFindDomicilio() throws Exception {
		Entity domicilio         = null;
		String regresar          = null;
		Map<String, Object>params= new HashMap<>();
		try {
			params.put("idEmpresa", ((TicketVenta)this.ticket.getOrden()).getIdEmpresa());
			domicilio= (Entity) DaoFactory.getInstance().toEntity("VistaInformacionEmpresas", "datosEmpresa", params);
			regresar= domicilio.toString("empresaDireccion").concat(" C.P. ").concat(domicilio.toString("codigoPostal")).concat("<br><span style=\"font-size: 11px;\">").concat(domicilio.toString("colonia")).concat("</span><br><span style=\"font-size: 12px;\"> TEL: ").concat(this.toTelefono()).concat("</span><br><span style=\"font-size: 12px;\"> WS: ").concat(this.toCelular()).concat("</span>");
		} // try
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindDomicilio
	
	private String toTelefono() throws Exception {
		String regresar          = "";
		Map<String, Object>params= new HashMap<>();
		Entity telefono          = null;
		try {
			params.put(Constantes.SQL_CONDICION, "id_empresa=" + ((TicketVenta)this.ticket.getOrden()).getIdEmpresa() + " and id_tipo_contacto in ("+ ETiposContactos.TELEFONO.getKey()+ ","+ ETiposContactos.TELEFONO_CASA.getKey()+ ","+ ETiposContactos.TELEFONO_NEGOCIO.getKey()+ ","+ ETiposContactos.TELEFONO_TRABAJO.getKey()+ ") and id_preferido= 1");
			telefono= (Entity) DaoFactory.getInstance().toEntity("TrManticEmpresaTipoContactoDto", "row", params);
			if(telefono!= null)
				regresar= telefono.toString("valor");
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toTelefono
	
	private String toCelular() throws Exception {
		String regresar          = "";
		Map<String, Object>params= new HashMap<>();
		Entity telefono          = null;
		try {
			params.put(Constantes.SQL_CONDICION, "id_empresa=" + ((TicketVenta)this.ticket.getOrden()).getIdEmpresa() + " and id_tipo_contacto in (" + ETiposContactos.CELULAR.getKey()+ ","+ ETiposContactos.CELULAR_NEGOCIO.getKey()+ ","+ ETiposContactos.CELULAR_PERSONAL.getKey()+ ") and id_preferido= 1");
			telefono= (Entity) DaoFactory.getInstance().toEntity("TrManticEmpresaTipoContactoDto", "row", params);
			if(telefono!= null)
				regresar= telefono.toString("valor");
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toCelular
	
	private String toCredito() {		
		StringBuilder	regresar= new StringBuilder("");
		if(this.tipo.equals("CREDITO")) {			
			regresar.append("<p style=\"width: 290px;text-align: center;align-content: center;font-family: sans-serif;font-size: 15px;font-weight: bold\">");
			regresar.append("<br>");
			regresar.append(this.tipo);			
			regresar.append("</p>");		
		}	// if			
		return regresar.toString();
	} // toCredito
	
	private String toNoTicket() {		
		StringBuilder	regresar= new StringBuilder();
		String descripcionTicket= this.tipo.equals("COTIZACI�N") ? ((TicketVenta)this.ticket.getOrden()).getCotizacion(): ((TicketVenta)this.ticket.getOrden()).getTicket();
		regresar.append("<p style=\"width: 290px;text-align: center;align-content: center;font-family: sans-serif;font-size: 15px;font-weight: bold\">");
		regresar.append(this.tipo.equals("COTIZACI�N") ? "CONSECUTIVO: " : (this.tipo.equals("FACTURA") ? "FACTURA No.: " : "TICKET No.: "));
		regresar.append(this.principal.getClave()).append("-").append(descripcionTicket).append("<br>");		
		return regresar.toString();
	} // toNoTicket
	
	protected String toTipoTransaccion() {
		StringBuilder regresar= new StringBuilder();
		regresar.append(this.tipo.equals("CREDITO") || this.tipo.equals("FACTURA") ? "VENTA DE MOSTRADOR" : this.tipo).append("<br>");		
		return regresar.toString();
	} // toTipoVenta
	
	protected String toFecha() {
		StringBuilder regresar= new StringBuilder();
		regresar.append("Fecha:").append(Fecha.formatear(Fecha.FECHA_HORA_CORTA, ((TicketVenta)this.ticket.getOrden()).getCobro()));
		if(this.tipo.equals("APARTADO")) {
			regresar.append("<br>");		
			regresar.append("Vencimiento: ").append(Fecha.formatear(Fecha.FECHA_HORA_CORTA, ((TicketVenta)this.ticket.getOrden()).getVigencia()));
		} // if
		if(!Cadena.isVacio(this.cliente)) {
			regresar.append("<br>");		
			regresar.append("<span style=\"font-size: 11px;\">Cliente: ").append(this.cliente).append("</span>");
		} // if
		regresar.append("</p>");		
		return regresar.toString();
	} // toFecha
	
	protected String toTable() {
		StringBuilder regresar= new StringBuilder();
		regresar.append("<table style=\"width: 290px;border-top: 1px solid black;border-collapse: collapse;\">");		
		return regresar.toString();
	} // toTable
	
	protected String toHeaderTable() {
		StringBuilder regresar= new StringBuilder();
		regresar.append("<thead>");
		regresar.append("<tr style=\"border-top: 1px solid black;border-collapse: collapse;\">");
		regresar.append("<th style=\"font-family: sans-serif;font-size: 12px;width: 80px; max-width: 80px;border-top: 1px solid black;border-collapse: collapse;text-align: left\">CONCEPTO</th>");
		regresar.append("<th style=\"font-family: sans-serif;font-size: 12px;width: 35px;max-width: 35px;word-break: break-all;border-top: 1px solid black;border-collapse: collapse;text-align: center\">CANT</th>");
		regresar.append("<th style=\"font-family: sans-serif;font-size: 12px;width: 35px;max-width: 35px;word-break: break-all;border-top: 1px solid black;border-collapse: collapse;\">NETO</th>");
		regresar.append("<th style=\"font-family: sans-serif;font-size: 12px;width: 55px;max-width: 55px;word-break: break-all;border-top: 1px solid black;border-collapse: collapse;\">IMPORTE</th>");
		regresar.append("</tr></thead>");		
		return regresar.toString();
	} // toHeaderTable
	
	private String toArticulos() {				
		StringBuilder regresar= new StringBuilder();			
		for(Articulo articulo : this.ticket.getArticulos()) {
			if(articulo.isValid()) {				
				regresar.append(toTable());
				regresar.append("<tbody>");
				regresar.append("<tr style=\"border-top: 1px solid black;border-collapse: collapse;\">");
				regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 80px; max-width: 80px;border-top: 1px solid black;border-collapse: collapse;\">").append(articulo.getNombre().length()> 35 ? articulo.getNombre().substring(0, 35) : articulo.getNombre()).append("</td>");
				regresar.append("</tr>");
				regresar.append("</tbody>");
				regresar.append(toFinishTable());
				regresar.append("<table style=\"width: 290px;\">");
				regresar.append("<tbody>");
				regresar.append("<tr>");
				regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 80px;max-width: 80px;\">").append("</td>");
				regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 35px;max-width: 35px;word-break: break-all;text-align: center\">").append(articulo.getCantidad()).append("</td>");
				regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 35px;max-width: 35px;word-break: break-all;text-align: right\">").append(Numero.formatear(Numero.NUMERO_CON_DECIMALES, articulo.getCosto())).append("</td>");
				regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 55px;max-width: 55px;word-break: break-all;padding-right: 10px;text-align: right\">").append(articulo.getImporte()).append("</td>");
				regresar.append("</tr>");
				regresar.append("</tbody>");
				regresar.append(toFinishTable());
			} // if
		} // for
		regresar.append(toTable());
		regresar.append("<tbody>");
		regresar.append("<tr style=\"height: 15px;border-top: 1px solid black;border-collapse: collapse;\"><td></td><td></td><td></td><td></td></tr>");			
		regresar.append("</tbody>");			
		return regresar.toString();
	} // toArticulos
	
	private String toPagos() {
    double suma= 0D;
		StringBuilder regresar= new StringBuilder();
		regresar.append("<table style=\"width: 290px;\">");
		regresar.append("<tbody>");
		regresar.append("<tr style=\"border-collapse: collapse;\">");						
		regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 220px;max-width: 220px;word-break: break-all;border-collapse: collapse;text-align: right;\">SUBTOTAL:</td>");			
		regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 70px;max-width: 70px;word-break: break-all;border-collapse: collapse;text-align: right\">").append(this.ticket.getTotales().getSubTotalDosDecimales$()).append("</td>");			
		regresar.append("</tr>");			
		regresar.append("<tr style=\"border-collapse: collapse;\">");						
		regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 220px;max-width: 220px;word-break: break-all;border-collapse: collapse;text-align: right;\">IVA:</td>");			
		regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 70px;max-width: 70px;word-break: break-all;border-collapse: collapse;text-align: right\">").append(this.ticket.getTotales().getIvaDosDecimales$()).append("</td>");			
		regresar.append("</tr>");			
		regresar.append("<tr style=\"border-collapse: collapse;\">");						
		regresar.append("<td style=\"font-family: sans-serif;font-size: 14px;width: 220px;max-width: 220px;word-break: break-all;border-collapse: collapse;text-align: right;font-weight: bold;\">TOTAL:</td>");			
		regresar.append("<td style=\"font-family: sans-serif;font-size: 14px;width: 70px;max-width: 70px;word-break: break-all;border-collapse: collapse;text-align: right;font-weight: bold;\">$").append(this.ticket.getTotales().getTotalDosDecimales$()).append("</td>");			
		regresar.append("</tr>");			
		regresar.append("<tr style=\"height: 15px;\"><td></td><td></td><td></td><td></td></tr>");	
		if(this.tipo.equals("APARTADO")) {
      if(this.pago.getAbonos().isEmpty()) {
  			regresar.append("<tr style=\"border-collapse: collapse;\">");				
	  		regresar.append("<td style=\"font-family: sans-serif;font-size: 14px;width: 220px;max-width: 220px;word-break: break-all;border-collapse: collapse;text-align: right;font-weight: bold;\">ABONO:</td>");			
			  regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 70px;max-width: 70px;word-break: break-all;border-collapse: collapse;text-align: right;\"></td>");
  			regresar.append("</tr>");
        suma+= this.pago.getEfectivo()+ this.pago.getDebito()+ this.pago.getCredito()+ this.pago.getTransferencia()+ this.pago.getCheque();
      } // if
      else {
        regresar.append("<tr style=\"border-collapse: collapse;\">");				
        regresar.append("<td style=\"font-family: sans-serif;font-size: 14px;width: 220px;max-width: 220px;word-break: break-all;border-collapse: collapse;text-align: right;font-weight: bold;\">ABONO(S):</td>");
			  regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 70px;max-width: 70px;word-break: break-all;border-collapse: collapse;text-align: right;\"></td>");
        regresar.append("</tr>");
        for (Abono abono : this.pago.getAbonos()) {
          regresar.append("<tr style=\"border-collapse: collapse;\">");				
          regresar.append("<td style=\"font-family: sans-serif;font-size: 14px;width: 220px;max-width: 220px;word-break: break-all;border-collapse: collapse;text-align: right;\">").append(Fecha.formatear(Fecha.FECHA_HORA_CORTA, abono.getRegistro())).append(" ").append(abono.getMedio()).append(":</td>");
          regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 70px;max-width: 70px;word-break: break-all;border-collapse: collapse;text-align: right;\">").append(abono.getMonto()).append("</td>");
          regresar.append("</tr>");
          suma+= abono.getMonto();
        } // for
        regresar.append("<tr style=\"border-collapse: collapse;\">");				
        regresar.append("<td style=\"font-family: sans-serif;font-size: 14px;width: 220px;max-width: 220px;word-break: break-all;border-collapse: collapse;text-align: right;\"></td>");
			  regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 70px;max-width: 70px;word-break: break-all;border-collapse: collapse;text-align: right;\"></td>");
        regresar.append("</tr>");
      } // else
		} // if
		if(!this.tipo.equals("APARTADO") && this.pago.getAbono()> 0) {			
			regresar.append("<tr style=\"border-collapse: collapse;\">");				
			regresar.append("<td style=\"font-family: sans-serif;font-size: 14px;width: 220px;max-width: 220px;word-break: break-all;border-collapse: collapse;text-align: right;\">ABONO(S):</td>");			
			regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 70px;max-width: 70px;word-break: break-all;border-collapse: collapse;text-align: right;\">").append(this.pago.getAbono()).append("</td>");
			regresar.append("</tr>");
		} // if
		if(this.pago.getEfectivo()> 0) {			
			regresar.append("<tr style=\"border-collapse: collapse;\">");				
			regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 220px;max-width: 220px;word-break: break-all;border-collapse: collapse;text-align: right;\">EFECTIVO:</td>");
			regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 70px;max-width: 70px;word-break: break-all;border-collapse: collapse;text-align: right;\">").append(this.pago.getEfectivo()).append("</td>");
			regresar.append("</tr>");
		} // if
		if(this.pago.getDebito()> 0) {			
			regresar.append("<tr style=\"border-collapse: collapse;\">");
			regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 220px;max-width: 220px;word-break: break-all;border-collapse: collapse;text-align: right;\">DEBITO (REF ").append(this.pago.getReferenciaDebito()).append("):").append("</td>");
			regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 70px;max-width: 70px;word-break: break-all;border-collapse: collapse;text-align: right;\">").append(this.pago.getDebito()).append("</td>");
			regresar.append("</tr>");
		} // if
		if(this.pago.getCredito()> 0) {			
			regresar.append("<tr style=\"border-collapse: collapse;\">");
			regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 220px;max-width: 220px;word-break: break-all;border-collapse: collapse;text-align: right;\">CREDITO (REF ").append(this.pago.getReferenciaCredito()).append("):").append("</td>");
			regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 70px;max-width: 70px;word-break: break-all;border-collapse: collapse;text-align: right;\">").append(this.pago.getCredito()).append("</td>");
			regresar.append("</tr>");
		} // if
		if(this.pago.getTransferencia()> 0) {			
			regresar.append("<tr style=\"border-collapse: collapse;\">");
			regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 220px;max-width: 220px;word-break: break-all;border-collapse: collapse;text-align: right;\">TRANSFERENCIA (REF ").append(this.pago.getReferenciaTransferencia()).append("):").append("</td>");
			regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 70px;max-width: 70px;word-break: break-all;border-collapse: collapse;text-align: right;\">").append(this.pago.getTransferencia()).append("</td>");
			regresar.append("</tr>");
		} // if
		if(this.pago.getCheque()> 0) {			
			regresar.append("<tr style=\"border-collapse: collapse;\">");
			regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 220px;max-width: 220px;word-break: break-all;border-collapse: collapse;text-align: right;\">CHEQUE (REF ").append(this.pago.getReferenciaCheque()).append("):").append("</td>");
			regresar.append("<td style=\"font-family: sans-serif;font-size: 12px;width: 70px;max-width: 70px;word-break: break-all;border-collapse: collapse;text-align: right;\">").append(this.pago.getCheque()).append("</td>");
			regresar.append("</tr>");
		} // if
		regresar.append("<tr style=\"border-collapse: collapse;\">");
    if(this.tipo.equals("APARTADO")) {
		  regresar.append("<td style=\"font-family: sans-serif;font-size: 14px;width: 220px;max-width: 220px;word-break: break-all;border-collapse: collapse;text-align: right;font-weight: bold;\">TOTAL ABONO(S):</td>");
		  regresar.append("<td style=\"font-family: sans-serif;font-size: 14px;width: 70px;max-width: 70px;word-break: break-all;border-collapse: collapse;text-align: right;font-weight: bold;\">").append(suma).append("</td>");
    } // if
    else {
		  regresar.append("<td style=\"font-family: sans-serif;font-size: 14px;width: 220px;max-width: 220px;word-break: break-all;border-collapse: collapse;text-align: right;font-weight: bold;\">CAMBIO:</td>");
		  regresar.append("<td style=\"font-family: sans-serif;font-size: 14px;width: 70px;max-width: 70px;word-break: break-all;border-collapse: collapse;text-align: right;font-weight: bold;\">").append(this.pago.getCambio$()).append("</td>");
    } // else
		regresar.append("</tr>");
		if(this.tipo.equals("APARTADO")) {			
			regresar.append("<tr style=\"height: 15px;\"><td></td><td></td><td></td><td></td></tr>");	
			regresar.append("<tr style=\"border-collapse: collapse;\">");				
			regresar.append("<td style=\"font-family: sans-serif;font-size: 14px;width: 220px;max-width: 220px;word-break: break-all;border-collapse: collapse;text-align: right;font-weight: bold;\">RESTANTE:</td>");
			regresar.append("<td style=\"font-family: sans-serif;font-size: 14px;width: 70px;max-width: 70px;word-break: break-all;border-collapse: collapse;text-align: right;font-weight: bold;\">$").append(Numero.formatear(Numero.NUMERO_CON_DECIMALES, this.pago.getDifEfectivo())).append("</td>");
			regresar.append("</tr>");
		} // if
		regresar.append("</tbody></table>");					
		return regresar.toString();
	} // toPagos
	
	protected String toFinishTable() {		
		return "</table>";
	} // toArticulos

	protected String toVendedor() throws Exception {
		StringBuilder regresar= new StringBuilder("<br/>");
		regresar.append("<p style=\"width: 290px;font-family: sans-serif;font-size: 13px;border-top: 1px solid black;border-collapse: collapse;\">");
		regresar.append("<br/><strong>VENDEDOR:</strong>").append(this.toUsuario()).append("<br/>");		
		return regresar.toString();
	} // toArticulos

	protected String toUsuario() throws Exception {
		String regresar          = null;
		Entity usuario           = null;
		Map<String, Object>params= new HashMap<>();
		try {
			params.put("idUsuario", ((TicketVenta)this.ticket.getOrden()).getIdUsuario());
			usuario= (Entity) DaoFactory.getInstance().toEntity("VistaUsuariosDto", "perfilUsuario", params);
			regresar= usuario.toString("nombreCompleto");
		} // try
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toUsuario
	
	protected String toCajero() {
		StringBuilder regresar=  new StringBuilder();
		regresar.append("<strong>CAJERO:</strong>");
		regresar.append(JsfBase.getAutentifica().getPersona().getNombreCompleto());
		regresar.append("</p>");		
		return regresar.toString();
	} // toArticulos
	
	protected String toFooter() {
		StringBuilder regresar= new StringBuilder();
		String descripcion= this.tipo.equals("COTIZACI�N") || this.tipo.equals("APARTADO") ? "GRACIAS POR SU PREFERENCIA" : "GRACIAS POR SU COMPRA";							
		regresar.append("<p style=\"width: 290px;text-align: center;align-content: center;font-family: sans-serif;font-size: 14px;border-top: 1px solid black;border-collapse: collapse;\">");				
		regresar.append("<br/>�");
		regresar.append(descripcion);
		regresar.append("!</p>");
		regresar.append("<p style=\"width: 290px;text-align: center;align-content: center;font-family: sans-serif;font-size: 10px;\">");
		regresar.append("PARA CUALQUIER ACLARACION, MANTENER SU TICKET");
		regresar.append("</p>");
		if(!this.tipo.equals("COTIZACI�N") && !this.tipo.equals("APARTADO")) {			
      Encriptar encriptado= new Encriptar();
      String codigo= encriptado.encriptar(Fecha.formatear(Fecha.CODIGO_SEGURIDAD, ((TicketVenta)this.ticket.getOrden()).getRegistro()));
			regresar.append("<p style=\"width: 290px;text-align: center;align-content: center;font-family: sans-serif;font-size: 10px;\">");
			regresar.append("PARA LA GENERACI�N Y/O DESCARGA DE TUS ARCHIVOS FISCALES INGRESAR A LA SIGUIENTE PAGINA");
			regresar.append("<br/><br/>");
			regresar.append(Configuracion.getInstance().getPropiedadServidor("sistema.dns")); 
			regresar.append("<br/><br/>C�digo de seguridad: <span style=\"font-size: 12px;font-weight:bold;\">").append(codigo).append("</span>");
			regresar.append("</p>");
		} // if
		//regresar.append("<svg id=\"barcode\"></svg>");
		regresar.append("<p style=\"width: 290px;text-align: center;align-content: center;font-family: sans-serif;font-size: 10px;\">");
		regresar.append("<span style=\"font-size: 12px;font-weight:bold;\">IMOX</span> ... SOLUCIONES WEB ...");
		regresar.append("<br/>");
		regresar.append("<span style=\"font-weight:bold;\">Contacto:</span> 449-209-05-86, imox.soluciones.web@gmail.com");
		regresar.append("</p>");
		regresar.append("</div>");		
		return regresar.toString();
	} // toFooter	
	
	private String toImageIib() {
		StringBuilder regresar= new StringBuilder("data:image/png;base64,");
		regresar.append("iVBORw0KGgoAAAANSUhEUgAAALsAAAD2CAYAAACOVvbYAAAk43pUWHRSYXcgcHJvZmlsZSB0eXBlIGV4aWYAAHjarZtpkhy3kq3/YxW9BMzDcuAYzHoHb/n9HWSSEiVdXcnsqURWMSszAoC7n8GBcOf//e91/8N/vcbqcmm9jlo9/+WRR5z80P3nv/H+Dj6/v99/Zt/fhV9fd7d+fxF5KfE9ff7Z5vf9k9fLbx/4cY9gv77u+vc3sX8v9P3Fjwsm3Tnyw/79IHk9fl4P+XuhcT4/1NHbL1OIn+/r+8Y3lO8f+170jch//u1+/0JurNIu3CjFeFJInr9j+o4g6U9Kk++Zv0MqvC+kys85Rfe+/ZgrC/LL9H589/73C/TLIq/vyN0fV//nT39Y/Di/r6c/rGX9caH6178I5a8X/y3x726cvj85Xv7lF+3HWP2fF/ne3e89n9nNXFnR+s2ot9jhx2V4I+mW0/tY5avxp/Bze1+Dr+6nX4R8++WNrxVGiETlupDDDjPccN73FRhRzPHExvcYV0zvtZ5aHHElxSnrK9zY0kg7dWK54nEElOD9HEt49x3vfit07rwDb42BiwU+8h+/3N/98t98uXuXlij4/lknZsa4olacYShy+pt3EZBwv3Erb4F/fH3D73+XP7pI5m1a5s4Ep7fPJayE33IrvTgn3lf4/imh4Nr+XoAl4t6FwYREBHylBkINvsXYQmAdOwGajDymHI");
    regresar.append("0IhFLiZpAxp1Sja7FH3ZvPtPDeG0usUS+DTSofqqkRm5Emwcq5kD8td3JollRyKaWWVroro8yaaq6l1tqqQG621HIrrbbWehtt9tRzL7321nsffY44EhhYRh1t9DHGnNFNbjS51uT9k1csWrJsxao16zZsLtJn5VVWXW31NdbccacNTOy62+577HmCOyDFyaecetrpZ5x5ybWbbr7l1ttuv+POn1EL37L949e/iFr4Ri2+SOl97WfUeNW19uMSQXBSFDMiFnMg4k0RIKGjYuZ7yDkqcoqZH0K5EhlkUWzcDooYIcwnxHLDz9j9Frl/FDdX+j+KW/xvkXMK3f+PyDlC9+e4/UXUtnhuvYh9qlBr6hPVx3tm7I4/3vPXn76f1lYZJ9Ud0gpWw61jl3hYknXvtB6Yc0sHrPJJCbnKzpe/Trg7DKtnhml7Lr/X9esSnHCMgZab6u3DDuDV510EtO7aZiZ+62wW+y4Lxfpi7okZpMsMrQxCdf22Mnc+ObUwx17dWuTtiZCtrrcxtjLXnLu5CSlqeVa1fPo6k2UODL51m7cfO75x1b7arcvGaIHRgwU28j1pzJ7bavv65AYj4CNksBITkui7jbKohkngSOF1bXMF1nSbmIRJJrug9A5hNG+p7DhucZ75rGh9nLp7yWe3NEdlqt0INYvTdYsQbcUyD5hVx6RoLKaxfD9ctFg6d7tp08CTlj0TC4OUywIzz3qO2CiBXhopxMsbju");
    regresar.append("h5rjRggp58u2kWxWNlCt2Vu9LZedR1qE9Gb7mccHY9/rR+SX+NKDGhTbLDRdPa9Yerk7m8cepeG2C7c5POK19vrbMs6wbS0oLSvHBhEgnG83efvo3Re8K0be618ku6KWn1EvL7wz/8zvrNS7jn6WPmvessDSmVzTWrSqGTx1lv2t6f2k5MbeVgQolKIFj1Mqz4cSMFWPdY9aaNEuAWi3pisRuLOijBdcu+vd8DFbAsJRgFUPbml62e3tO1SbLOEiZLfKbdoBRqcZPPtVyXioWAvs3MHKKfHsJsaVvMJMe8xTaIQoJvWIjSWHOR8xBXX8Sr1LT5N3+mu+BcWI2o5jaoipBINw+MtUJomH80lnmiMe+kkryuBUgbFy0xncpHWP00XXwJM0epJA1hSg042+gRlsHPDY5+MMESyoQ0ZOUIunHHUDrT2WuyfBmdvXdmMqx3U4aX01mwUYBNYHYsEgVonarZcApQsIatUE6sO7dDSfVNFaPiHdi398qtjVDxAPmsw5wrQHMSg51HoWZ+RnT5ITTPKvepGgjHM6ZLjq98AP8AMubdrRDndqUFNsXBZM++o7QJwPmitasUlYcB6rpRa5Z3u7FkcO+O5vqaqMI0GaegFaDJcWWWCya7EVifFCHT8GmX+TLzgPrrT8nq/ls2syLpQi03AFgtbECqxj3zamNcO6ZlXOQM3H/ToQwnUDZmKdImkUo1bg96Yg7WJvFGS6zzSVyHuYaGSC");
    regresar.append("NNZ7ERN3Ths6um66Ue7iB8J08oMrJQYF9ZsFTsZyfYY/qOGMoRPuD6AcJjme1AYAX8t+l4c5HFYU16kd7CXwErBtKAdnMB30BGgyAIYACWI/yH2vosV0vfNOvuJwfFUQUe1sB/AmYJQquQq90chgB22SU3blvE5JCKx3xcJedbQTFKZI6s6iXQNZGvdaOkmYzx7lyNYu2kMOGkDoDIfC0BRY0KiFAsZVdi4/rZ9etF9DYmAABfvyGz8Desf4NTD9gOi6aJedClwfG7wJLo+gDDE4PcQV3fK+BbmNcgwg1Ahfxv5e6UQA8hunDbmY2Qtclk4NoJalOXTO/AYPAZ0QDgz4C9PABy1yrEFdyOO7OC8AHCJLiOhQAim6CDYmy1WkEJcUMg0J+zbA5+AlTQORBDwJZD11eh1j/897t7P9SSkD0X3bIxRF01C7ECczWBpBNcqRA8vDMmYHgsiFU8CkYpsY7m7HrkTtImDCoD6UJ2BkW5bnhnABS7RhXwWpHcB4MBSzJ4NGOEuLEbF/KuObNA1hH+ZblTHLveQn1uWD4s5GSnavqZFAmaUWgQH/Ny9xFvC/Ui6jPuwGEsEIMsRibxykEE+IGLRJjFDSqQlyAFlwXmIVLgyEPIfq2Ex2syJirTOSja2mHeDiKdw1io3xQlVEo1yjHstbWY5Qw4qZe3sNj7P393f/WLQDmSClHXZZ5kO+qECQ0BKAirSmjTBlDbe06HNyCPJeWl1S");
    regresar.append("A/oNQCQqhv3or5OYPkIPG23fEAjZrdYyYh6EWDlwDPEYEBaLidqwA57bp48VzY9byBFcIBnPw6WkTyOn5zxZs9uoPvC1+FEATYYFiEzYFijKRF0VG9QelTMghM5oKI6C0SkUqKYx7FHYEXFxzbA/5NWOiYEZjDq6R0IGc9OYkPnmDPOmhqSmWrJ0JaVtvgHpl2qfgwKwKFxCfjAbPlsHnCQlaioGS8auhCTjdwPWGdSpJw4THaSCRUqjAceJrX0PRhODCy+MnUnhAP5UVqIbmgoyxLROYDcl7lxET5MRsZcgSeGSbajQQU9x6EYt0ObfJZTrj4lyr8798bOXYtV7+QwI5MTAeZ3l6EyXdkduYFEoDRNxUFbAoTxSSOrQPV3QCqerbQk8/0lGtD1jREM/yJCAa275Z8Ra8QRST+Ap+xamQFWn1rIGQjwlJEjU5AETbRAG5mO0LqiSYCq5JwaN1CeRJRmAzOgGyRcoBA2RQNNsdjRyCEwYgqI+GuE2KHIx1GhyjE3H3rC7SWUitVyw2XpL6Rqht7VSgNjyphdYWDHsjCdYynePlULZiacIPUNA4B4AKYqXVGW1jLwbRYEQsPhdBhxHpQTVy/wNwZyZJqz0/5uDlsl9CwJYimeCApSeVysQ1ZeQgQI2Mu3v8ZlElOY6oIOFFoNUfInV+k5TKmtiNiGS0Qm0HFjsBu4LkQSRCbo5Hum0s0xmpYEZjT85s6xCiIMsZXHFnmF1");
    regresar.append("50kRjnY20iPoepbBCyQloL9wYDIPVrQSc1xh+j7C26ybge9gof5/DoajFSrHwaHE7WZS/ldeA0XBVuCX/HFYb8Hs6AMs4Jc4nhRb6QeRRIig7qvheWxpRlgcQAiBkpfEW0R29PfC4j7NVjq21QWffguA8Uh0if0CXytrjID2i9cYBmWXLEbDGwpT4+xh38Q+J2P1+Aiw3/i4HEHTBewClsfG8uDPLkDRmleTJpgGukaEEz9cHW3YDkPA6BydqOGtFxIKdqOCKdAOsmH1mOAAubeJ7d8gt3QjmGg+2A5VCYFJ8nO11QuY75moGdwAQQcAqpFzJqobsRz5Qd+Q5EVVNPc8yQYubzxH0dFgiu8e6catiWOGGgMTrfITLE7PHiOuah6CCEFD3wc2C8sYE40N4rSEJJ8BcCGPAfKjCKPjXLc4iFCL5oqMDShrDqQJiMBYKMrMj4oRtRhHjfk+JgXrvZcKTGlpEja3OZ9ZCsshag4oE7JsPGPE6ULWtpFCUWBHZHISVURvEFhIoAi3cfxwK9Dz5lX+gDmALxarCkJJVhXNRJ4x/USFdjglVrBLmhvwFzmMHtQ3HVWi+T4hPgHeTXca7kdACmWD3sGELDAujAXIDRktHfag8AaxEIuWgShyTCruApAFekGmkPDLaMGqfyMXxcEY3STtW0sW5NFzd5uzpnROyxQEDndlXS0wOE/0p68h1XK23hYUz1BlyJ0rjAGdovvmLzlIztY4");
    regresar.append("cZdPKyP4Kf3DtFcm/VYdRmxEyDaFQ2/Iq0IGrqjuC5BEPXdxgtS4kg+FG6QBnaFc3R4cneXm+ItEH3gIKl1LEwwYjqEtwkXtDiJp5EOKq1gC8oJ0xWcj+Dx2KjP3B7M3eECiSNwGQCeHs4dDG5fQZ2nc/KrU8A8kIPTLRFSgZ4K6tTr4noIofOIXYjByhtBI/IBZXw5tqXaZaaQ8WaN4x5lwnBzxv35SIWMlXIwsr+oVBRquDDiuJ6Nd3B4VqEE+RMruG4ucvQ2wa1ibjATu6EeAhoD9XTqaIAcHrpfQCtvYZZl87ESdoAdV7H2aURIoyxtMrwWe1qMdTMTYDwxGSRlzfVKB2EsTDyUzgZj8yGjRH0a6jUXQ1xzXWhp6iVt7nULMAPQjrBKIcqmGBp48XbYsS2Fui8gKEqJ7yeZ3JkDr+IXysEk4MDTf+z7HxD12I0uR4W515wu907AYXdoWtqMUYQL3FrFzAYasWSU1grIOgkEnZXia2KkPfqbqpDyquvHij8XgvJuSOgrCUDIjDHatqqqzoxCgBzZXwg1ly++9dd49skOW9BzJy/EdruP/yiYdID9MUyHjQTyAxtBVbFyEBCaJ+WWQU5Lpk4rgNB0R5Pf7UgpA8kEQvAOFsWAjWCCWqjIr0+imjGtPGno8hgCcacoLnm6mptYskCGBHQz4gKBoE2wGtjlg2EBrnIFaKHoN7oa26E6moNxwR6XpScb9gsEET1l/KGoJ");
    regresar.append("Ay4IyM0SCWQe1jeBUPQHDUREBVoQSaGnoVE9EB98g/KQc3LBwj/9R9BEFIO0hnIEPEf4hhy94M2K9UuqYGQyHh0TuAGdKdFCY9bCNGCxhbK3OK9VCG4b2d4gIRTZqZRMVekMVcfKu0SLBZEA8KNS/zBurUHB94xK8OSpNaQ1xLOOFIMv5sUXqyWMRxAgVPDyxgWW4S6XJzBF577Xham8Dga0d0BCa3B/n267tPQiMLRGGd5iFbMg9/DTiDX4AUGbg7V2NJy3QMhZ+TFJJ2jRbCLyyvbj0ER9HhmlFOceVExscagnreUAXyjaw5z5tNax1PiyiUxz4v+uR2Ei0/6Uh+MkmVjVwGGEch7ovRU3/nrWXPEQcL/WyH1fLyRRIwGzOEnD1L5cEaIrBRUhuQEjeQWYxwodnGwtOqIQOe1IQdYS3VrZnqiKt/kO3ZVzi9/p19/cvv7rfqGqO9Mj1SrAEDQULjl0YXPJFR1Hb+NKcay0QZS/khHLQLcRLyuGqvFAO/1J7MqFZDbiF8YsLqN4nbof0ENQCxeZFLsBp4+T0Wi8v6DFB9OnVYIC5EO2ZskWgN+ReA3h4RIkYV1ZqAMPTmMW3oLpDSa09gkF91kDQdMQHTJpKRfw3GGKyr30AtkMyhNI9znhdUAOOLXC23ud6SMlJameTfgVoesKILdjdKfAETwD7m8FCUS2VwZAMMi9JlvjZ6+DJKchwkUFszy8tT48hmQFsbLIdgwz");
    regresar.append("+sScHayvKQ4ggd8GbpNqVjxNGcXf0lXBKscbxJw2ds8pBDDuaizmrgRATAaqJgBK/0TifxcHK4wI6dVi+VkKrbH9UKoqol009n9iAP3sP5k6SeFmmtVSOrtXHewmL8ejdzxbuo0a8tCZQfzHeCYlkBADUhQb9auiOj+ITtEYbakSiV/hqUU0p5aK8iJqqGCo5RyNsll9WBvnAtKhpYVyO1OnVTYb1tSHmgvEAfKJcK0CLW1d7WNhbK5JMl3JCrwPwJRW0+R9JX9vJGt0BhwA4iVV8chaM0RVIYXlv7LmVn7CTeCt6Q0IwNrSseIGpL217YnHGl2JC7lxU0bQphe7CQ2E3sLYJIH6IkYByG7P9eDrq/14tw466qMzBR2+C77JV8HuSqRYYQ0CQj4PQrQmtMS5+Gxg54RbKQMCBVwC41DQg3mIYZO+r2kUtFt8B9kvosN3GEA25wEaWTZW8o7lXOmdShOgcRCoE8lQg9oIqn/DMOnegr8qifVvJnd+ptSDiQlCpCuK6MTsDBjEYiVdNGRlGPk8RqHRxFY+JuyCDCDxCqLwlfrvaaHFgIkYap24nOwFjj25gU4kt7zZbAZRKZUQMor8/JHzQJ92ElQIfXdbqBGoL7U5PUi+sdOBikfyVS2pIaB34Oq2jx7oSpVpEdBmIxuYhSMBhcGZEq2sth1SkLIFq5RHYV9BFcBzHjAKlsaUo7aogd5tKlThkdWieZf13FK4JIyXGPKk");
    regresar.append("+qbuhRT6kD6QhjZETEDOUFo4JvSRszYFsuD6cLaFrQi/L3QXuwy6GVd1rIHBKP4gI+EG/i1YWufn0yAPHZuGMyX/B6G31rdzwjzU8ZJjMc3IatyA4y1/Phps4GykM0HCoZNYjdRhM9WYgrYOH8w7moHRP+BxVEocvJGUYIDl3B7WB50R1cRWZnj0phgKN7k0Xh/fIARj3+uV3m/mE/LZUtugHlEZiQk/pTXa17fFREUy8Xn0fWbtFBp1Dc0ixQLm67ont6U89A57IQGHAeOE+BI4mGtl60/YXELZ414iNF++tAC8RMotwzX6uAu4UmlaStXmKvVfZ1D/xmQBF9ehcYADz07d27nER+Uxvu4O2WnaO0CRiloy0FTLBQRp7MQmiy42/31pYwVRtPgwq9zUGCSqFCFY6X9ejHrY7SEGMx6K1mp7xeR6tFeUQiOjKLFMaukURo3qI2oSggDAND1z4USSDa7VgvZoGeypAzYEc5cP3cCWd4iJDkZRFg1PokO5qDxrDV3Y+IKFfXBFEF60oLDPl1CWJIBieKqZRYPCQ8xq8zP6AVX6u2+DJMjZY2v03GiOSGUKe8BqWh5oiUvDYPW0ZekN7Gp+SJ+UyB3yFoH3GXuTtyk7WwFZt+QmVUsARXHbRLIXGImN9q2YBILSiHLzkMQL6Nlpjm2wTMR9WvLifCgpfHxv6KX40iQtUG2N037d9ht6gJFCEgp42awICLdu4oXqCjwLTa5K");
    regresar.append("LqWUaM7jMqeD/qs+o0RNNWQpX0kojyLN9VjkosygVoIyWcshA3+DVtf7TG2nXPHQ7wKvSB5dQGiGCHZ1a4/aVzClcsbWoaVO3Iw21kHjbuOHLD1DHUSQQCLOMgP2yhZ2QN0mPXVzEg9lXrAjmNxO2T5fJ91IjYVNMvuF60+0gMkBb/8VTLH79jmnC+oBESoujwz0hud9X2BCOBPW2jgrGkC/6TelcPiaHAczq1kwMMgEaN2hp+647AgTmxHEUacrKUVUC7qWkAgbBoZ5g1pgqrX6zRUVai37UlIRGr1g75+nhM+1YzJJ0/MkgZO4MjCSK3jKfDhVLhgAYiFtZupADBxyUnHBxEjbsMFEyN1NtSz4LqRw6lLq0mh7r/sIlU3iZYNhimkGK5ELSu8wivGQ2tRHtbG4iIA8am15vJcg1UkY4vIbJK3GRKMWQKWm5mYI56pFiR4ORzUkv5o9Yj2v3q1Nh9jq1qm1N+dO6uvs5C/M0RNQWKnUtQeEWyMwAKCb8M0hto1Q+reyLymDSQWiCw9q+2RX5+L75oF6JTEFEtPzKMZShNdpX4aI8QNQPqKcUbwI0lOJC6jGWEcikrbBIyjXIyPK3a0OM2VqwVnU1hDaq20XRSAdOetV2KnEKuJGCTYsDzE0xE5PC4UdazDKSOS8JGDCJyilHFGXHE6klR0UckDMEIqTGn2j+qLYIoq3Te0vPCQGBfSsHYOrXzhroUFBWh1CGYmzEIld");
    regresar.append("sgOOFDVph1LfkmbYFrY5kgeqqV8u7P6gKKBytaA9mLyv9YSyM7MmJk4dSOjsYMRCwAA0obynmYdikqq7EIELJ9SUpiF9zRFmN7koYFKJjkxErFsUZ8exIYacSv5ouJkrWf5FlQyVK3mPFhiQrxzY0CMSCh+/YjjdoOcIFerFlWC+BAxQUMDBQDKqKxJh5XgH8x8LeSauorInldRr1SvZHCX2qEE121xHjDZeGa6CAhBZFQJIS6ajqBosUp2gJDp/Dh5XVqTD16ikabA1HiXxZyo1A2jlC7jxeh1wGFpc7noCTjjbOlTRK1U7L6AbOk4dR+h39BoIVl6gBsqq8BRmWmMnYABlLs6qOqbwvNand6MNKjH0nuq00VMjviU5njgM4nQG45qswapOLb6oDQgiajVHbWGRtWGU9HtusA2gEukGI6F+cao4SsDllz2kTkCMP4JaiUBNFRhxTbmnKMKFNGjBO3fLf6FiAeiIEoW9lhXlGBGULUaSuYXiVs2kkdjx/IdOI21QJQz11HpELUASeAjhAxR1TCQkRcrDVgPbXdBVUff4gJ9qJ0AgmAiCGW8nnryDTJDXDpdGaXwWkA6WhT2swJXaqpLcecZ0bj6AjSgdODIc4RLaUFuKdBPf6dudpNDX/8Nj4IsNKWSRvenV7A5QM8pwy9Ad865oI1GFFJAKuXTL0P35K6muq1qlGo9gMZKKukhTnHUV3aVJT50LGtQgC1Fa0lZcWQi2");
    regresar.append("QVwdRCW6I81GhokitVEZR12UDnNSeERmwm62rP6UCIto7GqTPqqI6uoBNdKDnmzOtqA77DqDeoxpDSUkTvVD3ojlBI6jNrxwhPQqCC9qqLlCorVqbc0FJXlDq7U0ctyGvGxgLpQB20oeP5/WE7iMEIAAeocakwTN38oobA9GoMMElSNWYQlv/xNQGnTj5ih4AnhxSA+482YLDW+JxOXpOI+AQAVCOEELHgZDZFOxQveBaQgX1KeqIykSTmJoiOYWTcB69Hlm0yIN0sux9a1jkvnWdcoPcMEqk+DEgtT5PQXu2SkZnsc+TGxUZT1VcHenA7jGpXvJA2mQhoo/r6jup26zgsmAG9kRuX/GUqiCUq6WX28KqpDNlqDw03brBpbFyRisPChFGTnkGAw0DSlLW8vVD8amjhwI8eoPEOr6O6q8DkABSAyrHS2yrRr3XIEKnG31IZU+eb1BSe2vbReUR1kSET+NxJ+HTkaLxEuCmjoXCP/Ac/PBcE9viZzEcIoTJ1xoUVJi32awGTfej+lopjobbwYMk9h7J04O0uKQgUAKjDpd75D/gxMGDtxB+UVNZROI+bIxsBqlGcUYPKfCq0w2eddK0yQLxdvVR4+WovM+J9dQCu1KSDjfI9LJQOAPB/86NStKbubTR0W8vaslfr7RJfMGAP7yOEdVh6DwscQ6XhyjCu/TdRqXPTzcn1a+cID2w6WdCoCLQDWTqWNkO8BkJ66cjDlRtX24");
    regresar.append("WCjExRmzMQ6fYxT6eWMaIQcm/YGjWz/BI/Mzyd5SwUps7h6RSZjrxR47KR8T0WkupRx7yq2+a0i4L7uigGnN7bglMvXFI266cKLUjp6xyjvTPKct0ZiVtuJF4xv4eYEmtEENW8WLtAP9j7A0BnPCfER2asSrkg7xbcSE73RM4fnY04U/v9qoB5IF/HP5m+7njVEcDzI+dgEeVn1TmFJRRC76KecHOqflhmY5Xz2EKcoUcYWGxKZeigvZqlpU7tSB2QgEhoJ4C0YZS9q1ELGpBwlzmb6TA1RJZQKfi7hYZGjGLIZ33OuodSUXiUAWXJe6cij6clwXE6Fi5B085fyhvvwogIOemMkWjDocFxWiwxnL51cATjpPbR1S57v7Ac8nVjkAIu8KoJhSKB9CKltHvUeQtTi8fNczCLCD8dmRgkviRF5lUcv45UYlrEa6Xej4bSsaNctb+hDapyNBOtvEP6pDRJRtQ5WLvkhBcXZg0wVF1NFT1roNxIOgqlJitrCyV77cDguVFxJwP+ajeSCTAAQIS4xOSM1/oBvedrWwNKJqxBCPADmXrAIYAXB7sHsBtZwe4Y+dHuwR06ioCOewdvCMuUSDbt96VhUQaUAtxmeAEoVM9SgLgYI4KlPoLbLKmOJeG4yVkL6v0iMgXJWU+NVAlwba4W9T2ujrpjXnREGSB++0gsNoCh59coiOT1zAOhQP+DPmrtMRfZaMiJynysKI9uujhR0/mW6E");
    regresar.append("1yNwPfGcGOK/Wm3TYyO4StRupUItYkgFYDglJHzHqxC/IuZp0Dot564rMYmJuaHs4hM5IdmaZxxkFihHfKom4JdR1bvhA/amaI1+ZdcuiMFfZAUWy1SMM7FOTUC6m4MrhfICk7FcEpiAxvvSCsFAY4QqV1nZNRP1onn5hbwu7Xpf5uWXc4STF4sCG0l3oF4YlceFIttfK07kw6q5O0axXFn2OAmgteKqMW6SaIOzkMnB54g2/NyskkjqYtjGfpUJxSYcQpR6pCWw/oNdwzDKodJq28TmuR5w4t6hf4VMkKWcOjXUSwW0fpdZRjvuctsNOq/qJUM53wFkvpHmCwoDVvB8uhftE/KBakssfiTkg+EBPu17R1oQ1c00n5oh7oO/NAgne1LDo+f0oSodggbKanJy23MDFVdTo3wz8MopiOpOnUe0AiUtnx4P5Q44hJFRzyT2kNwjqoB9ZfTSfgwG9qVARBvHRQAGhd4y5Zqph08LBlHZvbSUsKcoOpAlwdSneo98HF4HLMIqGWGMFAYIAUaYQdnKv/xuAOMu2N6sQmwJo3AWnaemFxm4OLK2SqnXg9bbR1BmS/c7VCD2rvoL2bTnniavw78kvsj4wm0iB0I9ok6XE1SqIJOboEbcAso4/wPYSVe3IBG3qioqWjc6Zq9bH42tatxPDqCC96MnlXjiStifRMp6pxyORmzghZ7dKQ7XcymqYz51JmCA3yzIBbbSRPRhzVDl8O2N");
    regresar.append("C2rpq5NceiDmJ5WUZc8EQlU5H12fKRYgVy/eHaWR7+6HkvqkOn/pA1XacVWOWoLiXFNQXnTMUgy6tWtr1HSKCTDsuj/jz+rFV1aYHMPI5M3ohOO5wBfXLeERS5Ni4CqhzsJ+5HB7T06BcVunKO+Z1ZJyZkCn7sOUNpHaC2l4eU3Lnc8xqVkPMkkRFncv5qYFataiad0V9VdwIG02NHmINyA6AqU4s6sUqKqo/nu3DrPbnG5EzHdFCVoBxCSV3j5tEmXnu5b4+tBLWGdKicqKnlp6PJJRHncPXYgaQEUUH7ZpR0wwJCUepFkbYIMj2mTDJLAe7XqIIGtC22dD6Md2KotC2tJ5OWzmHKNZieVYhqmpC2U5tLkbVHNoMBOG4REgyO8chuMEUxmBB3iQSyHoPRw6DYTcm2ilGDcwPqWJSiM5/a6/IyXEcnOVVjfbiovkokiFXyvpKIeliuC7HeCRhYB7UhXlmdlcPHHWlM6S3KkLkXqqB3xChAhtrbAHsaEoF5vrPQa6M6QXYVVxg6dAxuszLayReAee2RHyUyV4N9HFaIBJdxAQtu+nT3t+nUko4kUVAox6lzsKZe/9VjEkt5jZiYGAxwe2XYw+lMUCYUXH4/NQKGrf36DFUaAcHQ9bDdznoAXr1x1o+EhWZRPEj+o8cD+3YQ+9Jhq1IlHQTYOtgSlKIHcgDP9FTIUBNGChVpYXryT0c1qBtgAB1sQAhRi7HfzH2APNPGsl");
    regresar.append("ooOi890saSlNH18KcqSmdiy8RhhR8HFTDRlR/A5uWgd0EbNwFB4WUdJkJkRn107vw6luN7kMK/27fPOZ+ghyRh0UlSkcDu6nlrGHM0jGzFq6gRgKZ5VwgJ6e47xAF9IL0IvgRpZSXrek21ozNjMFrRwznB65iXnzn5XMnEjJieKFn1OMfbr61v70NgO3XkuX6fBQyHOAwMBsDh2vGCHyKJQZLJB6h1CAFm1q4buafupxrQTTJW8q3oqHmiFgJ1XuADEtkcmmHoPIZ2oNE66hDpKSxMI0VYhh6s9mImPb4ThQYQuZ4veb1OP4PiQsEUoPadjJfBg2y094JdsfZsVgQP+ud6ejC36FiNnpzRIQw9mfgQ4GjTJVSdhwQYQsUNM7kA3hftg+jgNWCOQAEOpG8qBgat7ok2emvqTIEOppkesdRRPQdPxi2YoaSxWupcMhDtA3RQwoS9rd1MFA2pHHC1Z+rRXYZnaEQ9EApkGzpbe2o6S2HhnVBQb9b3ruPnae6DrAZDfYZplyy9Hs5UY+DoY3oiogsEkKgOj1lRKX0QezJwcuemA5hYPm059fckYw+ssM7AHT3QB1UDVAOcSnrGIt2IuEZnrzR1IImoSYrX5zE6KUph7qA8njrJurUFQDrae/xQz5D4IvvPioAt3qvnryenhKyX7EYfQApkU5KRnYwrsaTADJ6ZEkYX6PQKSADwkrRI4KxTxktPild125dOSKyk5tiG95e10L");
    regresar.append("RorDAUAsJpEwhTy+rY/DwUxmz5Uh+4UsMOib5N+aUN3w2AYGjUzn85+LtTDXoOzP0fIwpOdcy77tkAAAGFaUNDUElDQyBwcm9maWxlAAB4nH2RPUjDQBzFX9NKRSodrCDikKHqYkFUpKNUsQgWSluhVQeTS7+gSUOS4uIouBYc/FisOrg46+rgKgiCHyCuLk6KLlLi/9JCixgPjvvx7t7j7h0gNCpMNX2TgKpZRioeE7O5VdH/Ch+CGMQ4ohIz9UR6MQPX8XUPD1/vIjzL/dyfo1/JmwzwiMRzTDcs4g3i2U1L57xPHGIlSSE+J54w6ILEj1yXW/zGueiwwDNDRiY1TxwiFotdLHcxKxkq8QxxWFE1yheyLVY4b3FWKzXWvid/YSCvraS5TnMEcSwhgSREyKihjAosRGjVSDGRov2Yi3/Y8SfJJZOrDEaOBVShQnL84H/wu1uzMD3VSgrEgJ4X2/4YBfy7QLNu29/Htt08AbzPwJXW8VcbQPST9HpHCx8BwW3g4rqjyXvA5Q4w9KRLhuRIXppCoQC8n9E35YCBW6BvrdVbex+nD0CGulq+AQ4OgbEiZa+7vLu3u7d/z7T7+wHizHLURSZJfAAAAAZiS0dEAP4A/gD+6xjUggAAAAlwSFlzAAAOxAAADsQBlSsOGwAAAAd0SU1FB+cEDxUqL7Qhp+YAACAASURBVHja7L15vGVVde/7HXPOtdbep62eaqGooilBpEBQsA");
    regresar.append("cbwCAaTdTY3KfJCyaxSfKSvHtNYyD5PK8vpFejIeZFbIMRUbFHQBpjB4JWhZ6qoqqgqqi+TrP3WmvOOe4fc+19ToH6kpv7Xp2CPT+fVXWafc5Ze63fHOs3x/yN34DBGIzBGIzBGIzBGIzBGIzBGIzBGIw5MGRwCf7jY+OPN+hN37qBf/vxj9j4bxu474FNlGVJVVWoKoigqohYjDEQFVVldHSUdevWccq6dVxyySWcuf4Mlh23anAPBmCfG2PHrp368Y9/nPvvv59ut8uP7ryLjRs3guoTXmvEYIwhaCTGCBgAnLGoKlEjEDFiiBpp5wWve8PrqKoOL3jRC3nbpe8Y3I/B+P9/fPjvPqTz589X46wipMPIzMeCglHEpv8xCq45MoVMLYVmpt3/PH3PaJZlmmWZQvN7bPo/E6NveO3rdO+ex24f3IHB+P903HDTjfobv/42HWkVakENoiKiYk0CpWsOg1qXzwK3Sa/D9A+HVYtR2/tYnApGjTEzkyUTxTRgt6LOZGoxmrtM3/Gut+uDDz+kg7syGP9Lx79+5we6fPlKdc6pgDrQITFaIOpAHaLGMANMg4o1mkuhOU4tqG1+zoHmoO3myEHbpvmeoNYkoJvCpt/nRCnaim0pZCpkKji1GG3lhb75zW8cAH7A2f8XRPJv3qzvfMc7uOfee4DEp50YVGPi4L3DQBXBOrAKEsABLaBtIWvB6BjMHx9ifKRFK3NYA6oRVZia7r");
    regresar.append("Jj9yR7D4IX2DsJAcBCFcAb0pSShgyJI8QKJaICY+Nj3HDDTTzzmc8ccPoB2P/j47Wvfa1+7jOfwZCAJ8ZCTCRaEMRaQqgxBCwwbMEGWN6G56wfZtXCcU5evZKli8fI8kCRC0OFo7ARQ42EClBAKAPY1hiPHegyWVu27JzgR/du4sGH93LnAzANdADXTCppzonmf20Wun9y+R/xnj++fAD4Adj/ndH8+m/qi1/6EqwBiTOAslYggBWDaIKaA8ZyOHWt4bmnn8jPn/9chqVkOHTJ4jTd6T0QOjgbQGuINUYrRD2Cx6CoGJCMbrBEN0I0w1CMI9kotj3KzgM1n/3m97jljq3c/yh4IAh0NT1r1GZEVVQ9VuCSn7uYa6/70gDwA7D/7HHBBRfpN77+VQwgVvDBYqwlhhIBnAPxiZ6cuAwues4qnvf0FZy2egGmcxDtTEPVxRAQPEpF1IpMpKFB9GdQ7M2kXmRWly63OsChEUQstWmRL1zBjunILXds4ppvbmLDozAFqMuoQg1AnhuqMmIkZ/369fzwzu8PAD8A+xPHzf/6fX3Va17N/l07EA39Nx01AwItieQNnzl9LbzlF1/Cc05ZzlC5E5l4lHacxPoSCYnXY4QoEZUAqhgFUVCBKIDKzMcAmmhIL0kppNcbhWgyOljqfAQ7upx9cR7X3baFD33mTh6rwdt0l0IAi0PVoCjnvehF3Pit6weAH4B9Zvw//3SV/vLbfhWCB4");
    regresar.append("2IA/WJTjubY3zFKLB+FfyX1zyLF5+9Gt2/mTj1GFndpeUM3gdEQIyiEvuMOvYWlA3P1lmXVSWBPP6/XGERi5iMGKBbe6S9EJ1/LA8csPzeX32Ze3dBRyGqI1OLM5Yq1BgxvOil5/GNb3xtAPgB2OEDf/tB/c3ffhdBQ3qnRsAr0mRRxMM84HfefCYXnrOWMbufct8DtPUgo04hREIQsC2isahEAgFR3/8bBoOJceZCqvS/o7Oubm8i9MCvDc0REWw0ECKFyyhVmCKjHF7KxMgJvPfvP8/Xv9eh0/wtDwgWJeIl8EeXXcaf/vFlA8A/lcH+67/2Lv3Qhz+AdZGoaXdfImRA0RwXnpPz22+6gJV2ms5jW2gVihGPxBKhwpAWrFEFMEQMEUWaNKWIYmfR9R7IRWeoy+GADwn0EnvfxmiE4HGuhfcZXR8oiozaODpmmNFj1/PXn7yNj3xhG/sBbyxiofahP4OuvvpqXve61w0A/1QE+/vee4X+t99/dwrdgEjKrFiFQmEIeMdr1/Gal5xIMbWZkc4+hjOlW9ZUGsiLgqrukllDiB4jtvnNDsFiGqBrkxjsX8YGwT/toqpqn/ODNjQoIlqDOoIMYV2BLydxzhDVM8U4cfEZ/P3nv8+Hv/QIXQvTESQzmJBmsbEZdV0PwP5UA/tvves39YPv/xu8gjhLjJpAGivGgYUGrvnQW1jAY7D/IYZlAgkliscYQ4wRcRYRwXuPzQwxgB");
    regresar.append("EBlZTxjnoYOQly+GU0PwPsIodPDCSCVqiAmgIfwYqQCVSdDiYf5ZCMk684kw9ft5G//cwGpoBoW2joYkw6i1NPPY0NGzYMAP8zhnkyvZm/+dsP6N+8/29RhcxC9CFF5VgxDJx5HHzyL1/D8PQD2AP3MxoOktWdtGK1EELAGIi1J9ZJrhsbPq6qiMZmVzQd0nDux9OVKEpMXAZQ0ktm7frr4ZddMViTEWNI2SKJ1HVFXhQQSob0IPXOf+PNF5zO+afmtABCjZP01IoRNmzYwOc+e81AWvBUiOyf/PSn9I1vegtEj9OISawai1IAF6y3/PX/8SrCY3eTxZIsdnDeI1qjzqDSoyOH/x9FMfqTL1Mv3UhD2/9nI4cosxa1EaROqwJ1gKGFMF1b6vYK9hfH80vvuYaH9qfNpyBNyjPC2mOP5fbbb79j/uIlZw2g/cRhnwxv4t5779bzzz+/j1GD0mzh0AJ+8QXz+T/f+hJGJu8n6+5EfInVpNi1zqKkgovD5780/8q/K1TIfzLkCAbUNHn4NNGCOBQwMZAT0w5tlnHq2WfzpRvuITSMSmz6+wcPHGTBgvnLr7/hhssH0H6SRvalxyzWx3bvThmTGMmyAqlL2sBFZ4/zJ5e+FPbfw5DpYKpJWs7hUHzdSRg3c4PNiYJoeiapgG/OSzTijKGqDVN2HsXxz+Hdf/MvXPuvU0wDtYCxNiUkfSQMinKenJz9/PNfort27caIoDFiBG");
    regresar.append("Jd0gLOOdHwnl9/NbL/fuabCXLKhocrpa9xuWvy3UeW6s5w/IYWNWJhGxPQI0oZPC6LZHGSiS138Ou/cB7HjqcskxFDDErtI2KFP3vvfx9w9ycb2P/56mv0xhu/hUXQkFJwFhgSWDUCf/r2V1IcuodRc5B6eg/SPcRYbokxYvICr544a4NorgwVC1iEiCFgTCM7jjUtphlnH8eN1LzqvJPJATStGMQaQlA+9rGPDpD9ZAP7G9/4psTETJ5utkZyYFjhI+97M0vdAbKpbbgwQZEJQ84Q6i4GqLslmCTlPcLQftynpll49qr20tPKWUsMNZmJDEtJtW8Lv/CSs1g11NRKZVlahFnh/vvv58q/+9Aguj9ZwH7xK16pwXsQwUeIGDQGcoXLf/XprGntxU5uYTSr0Zi0LN57LIpTT26UugoYlxGPMMXtVbD2JAbBKMF41HiESCi7WI0Y47A2oyxLci1ZMdLhgnMXUgASPEZSzWCMyheu/dwA3U8GsF911cf1S9ddhxjpV/kXJjIEvPFly7jgzJXE3feQxwm0LtO2vslS5X/tcQLWGJxx1FWcIwvT2TTGoyb0P29Zi1NBsHS6Fc5mOGqmHruP1//cOQwDhJrgK0IIWBG+fv31A3Q/GcD+K7/yKw0qQp8GmBhZOwRvf/U5FN1HsGGSzAgYg8WCKlEckudp2z5qKoUWh1FzZIHeS4v1VZJJexMlpnenSowpeeayFgGLEmjLNAuKLi");
    regresar.append("8+d1GK7iQVZWgCwIc+eOWAyhzNYP+93/ldJYaUSzeCM4olZV/+8O1ns8jsQ8q9OKkJITQeAQlI2k93zH77c+ESxCaiR1RiMh+YHen759iIyxptjY1dMj/Bs56+OgncbNrp7f3oYKF6+HBH08lu2bRZTzrpJJRUgBGDpxBoCbz+/OWcu24R0zt+RMsFJMuxmni6im2UiokvxMYiIPFlw+Nki0coAxObtYPBzt5RxeCNhSgzTwB8s32bIXWHM9edwJLW7Wzv9n4XiBE2bNgwQPjRGtmvuOIKDBFi0r5YSZmIEYXXX3gGOvEIbVORO+hWJaUPmCxndkDXWZGUOQBzlcMfNqKxWbCmp06URlpsbP+MxTTRPwY0dFkxZnn6mp4TgkEVQki6nm9+4/oBlTnaIvt99z2g604+sU88YrNN2Abe82vPYO28Gr/3ECKCBM9QnlFWQlTBNBtHQZJq0aoS8QTypCs5wrNeHxd9ehTGJDFx/ymURARJJiyNn6SJFW2dZP0Jy7jp7h101RBJ6odud5qbb755gPKjLbK/+93vBpKYp3Azj/QFI/C8Z6yk3rsJF7tpkygqGiLGguCbHUpSpX8THQ0BNf6wwugjtERN0VtM/2ak9xYf96remSvaexRIxGkF3b2sP/nY/s/bzDV6S6U7PTlA+dEU2bc/sktXrliGQYgolVdym6L7m173fNoyzVhR4TsVBkXEEUIgtwEfatQ6okiTy55NYEKjJz");
    regresar.append("+yS1NF+otoITYShpnnjdXZrxeUDNGIxSNUlOV+TjruFCwQNaJ1KooVgYcffniA8qMpsr/vij8DSb5xPRqjAU4+Bl64/lji9GN0J/ZRWMEZQ1lVDXcN2J+wQxoFopi0HNS5Q2mFJ2ZgTBPlhZjK+VTQmBayIpKKwH2HVq4MO7BJC4mxgip8+9vfHqD8aAL75790HZg+W8WTpLu/9srTWOUeIasmKLIMHyMRyPJUHK2k+lFR6YuqekBSDKb5njnCN0C0mcCS1I7an9LSz9T0CrStJGmyIRJVCJq4uzPKquVJv28B9aloZNee3QOUHy1g/+jHrtLtmzeBs43mXMmAY4bhlS86DTn0MI7yp2Q6EvvtezbO+ro2+nUzJ2+C8LNUuqafl0+vcVbQWLH2+BXNhEiXSpWmLHEwjgqwv+3SX0/81VdAoHCQAxc+fzV++gBWQ6rQfyrfxOjRumTxkoUz2Z0mpTmA+lEC9u1bt2lZNlG7V+vpYXEGF7/wdDr7HsFonYqhn7I3MIIGrHpo6mW9pjI9MyjhOHrA/kd//J7es7jhqin1ePY6OHFJTlu6WK2ZCzugR3I4AoVNmZyZoivT32AajKMA7DfffDMut2QuI9YRSzI5esV5Z+DKncTuATIT+xxWnoIRXhQ0BmLwZJnFxxkaEwfVeUcH2L/85et006ZN+KqmrkPDTWHVAnje+jXUB7YzUggh+Kf8TRQMIQSqqjqc4MQ4QPjRAPa7fr");
    regresar.append("yhX/AvWHJrcMDzz15BHicopENVTpG77Cl9AxVDlg+hNuPQ5BQqYFyztzDgMEcH2D/09/8ASmrXIoIPkQI479xn4Kd2E+tJhlot6hDS5spThMJoA2Bj0qZSQOgGweSjbN76SLPE6UV0S6uVD1A+18G+/eFt5Llp9tOV3MCq+XDcwgLf3c9wO8d7/5QB+ePfZx/QYol2mFJaPLA5FXtYaTzxVMmybIDyuQz291z+XkUMvkrFxkgqoH/O6QuY50pyCcQYUUk7iE8pft6oHXtRPqhhKmYcLC3TdVPjFHsuw8KLXnT+AOVzGeyf+tSn6G2HqCZtSAace+pqWvEAEsqZDtJiiU8uy8qfCfDHR/koDjd6DD96YBvTMW0kZdb1u3s87WlPG6B8roL9ke1bdfeexxLKm6ex0cDiITh97RJkcjeFa6KXWHiSA302T58N9h7gvRrMyBK+v3EzvbxU8DUORVFGRkYGKJ+rYP/hD3/Iwb17sNbQo6UWeNraYZaNZ0i5n1azNWiMecrcKFU9DPQ9+2txBXsmar575+7GuTNdk0wshc14xSteMUD5XAX7ho0bgUjwVRO500k+64xT0c5eRjKopifIs4zgPSJPjfTa7GgeY+yD3bqcfRMlO/alBsIYhxOLV8/iJQs544wzBjtLcxbsTZGwcw7VJFdtAScduxB8l+hrRCxVtyQzkrbIjzTeGyuOXslFH6AKojM+7T/jF6RqqqaVTe9rs78POt");
    regresar.append("O6ponyUQq8G+Wb392YGhSkmYDXgEV4+ctfPkD4XAb7d77zvdTh2XucsThgcRuOP6ZFiDWBDFyOywp8XWJFZ7r3HiGgiya76V7xdCR9Tfr2daFp755AahBEFV/VWBGUGowSxFCrQU0yFo4xGR4ZSb9HxBK84oyhDop3o0zJfG68YwvTgBgS/QNK4B3vetcA4XMV7A888IBu3bo18XRj0RhoGVi9FIZNSYyhqSNNhQ2p1+4R3hKXOGuyadO8oPdp03jMK3VdJ6C7HI8SgbGhIUJZNTWzYLEULsOHgFePNVD7pPr0dUxdQCQZs7r2OJM6yt3bD3L3dqgAVSH4CgWGRoY57fRnDCjMXAX7vffe2/84xEDucnyEdWvnkduIhAqIMws1Y+aE3lElgNQgHoNHCE1NqYOYkbkhMtciGqETamrjiMbiOyVta3HSwkYLZQfxZTIxjRHnklFpVQaKfAQJqZOwsS262qa1+GQ++flv44Hh4eHDdlffNYjqcxvsX//615seRoI1lspXWODE1ctwoQtNtOsBfq7JBGw02NijM7HvB1NVFd1uhXPJes9rJIjBu4IyOnwQnLHkBiTU+ACStZkqI1EtkrUIUXB5lsoTVfAU3LtzmtsfTC2Ip6amsNbinMMYwyWXXDJA9+PGnHIXuPrqqxNorMUHj4EkE1g8D1NPYGKdusMpDfd1RAHRcEQ5e5TUHsZEUBMJkuiNkURBsixHgk2uwWJxGFQcu7");
    regresar.append("0wf2whobuPUE4ynCtRDYEhvLYp2oay20nF1iYyVZc4kxx/h8YW8C/Xfp9dAdSSjBKaDn8veMELOOeccwYUZi5H9r179+KcwwePbRywxoZh6XgLW03gxGOY6U4XZW5dyl7xdM92OjaF0pWvqWNonkYWJaPLGG7Zeu49NIxfsI4w7zgOhIyuGaZTrGSqWMsDhxZgl5xOzBegLscVOeoKvBtn02NdbvzBI5QmpRyNMX2t0OWXD1oqzfnIDsmbscfZHXDMIsfC0ZxsYhqrvulqN6slo+qRLVHoLU7Fz1jyNpNR1SX3FyMYNQQC0RhKP8y0W87vv/fL/GATrD4GLvvdCzlh/giHDk6xs7uUP/7z69i6D553ivBnb7+QcOg+lA5lyJi/8jS+fs332LEXjO11y4v9Pq7nnXfeIKrP5ch+y223au+G9U7MAS0TGSsEpzVGIjF6QJNDgCbTJHRuvI3YcHUkNhZ2FjRDoxBDnSaDaxFHl/PBT93IbZtgP3DnLviHz9/OVLaCbPEp/Lf/+zru3gd7gBvuVq7+xvdpLTwOY4ZxrQV0imVcdd0jySEmzMjWVZUrr7xygOq5DvaNGzcmwDSpROdSkDxx9XLET1G4mADz+MB6pBeoPQNSHF4yFIuLkEWQmIFmECJ55lAfqKXNts4Q194BU0BHoAZu+8Eetu3N2Lh5ki0HoUvqglcKfOW7e9nnx1E7ytDwYj5//e0cgEYLY/oBYs2aNVx66a");
    regresar.append("WDqD7Xwb59+/bDPg8+Rfann3w8WpdorA/LvvT2LFOv3rlRfpZMnHpOvOmcjEJuDbH2ZK5FFRz7OwZP2viJQBBH6UHdMA9seRQFgpVEeRS274bdkzXBjXGgcnzhxu/RbX7WWtvPTF100UUDRB8NYN/28FbEmj7nNaTi6nUnrCLUnRTxjSC2Se2pzo3ILj1gR2jaw6jxqPgm914TNXW88xW03TBOEpcXAGcJeMRCZ3Ivq5YvwgMBwWQ5AoyPQNFyTMkwP9y0lx9vaTaRID3tGrB/4AMfGET1owHsu3fvnonaJrlaWWB8KCPEiqipC7XOsoUzCjJHHK96nalFG3u9xjE4SiQ09YVF4Sgn97FsRDhxQbLbLmJgWODY+bBu+QgnLR1mBGiFSFF1aQGnHAsLxocIxQJuvWsrHQCTkeUz2H7Tm940QPPRAvaJiQlimMmXiybnr4KUslNjCQgx6syGktEjT2HUIOoQdf2ud1ELAkXTVD71Sap8Bx8PMpQfYEm2nd/6pXUsAsYCLIrwztesY2G1mRVmL5desITFwLwIS4B3vuG5lPseJbQW8dmb91ADIUKnUlyePDAvu+yyAZqPltTj5OSMj3gvuzBeQCtPPF1J/8emtlJEGs8UPeLWV4rMRHVpYoi6Po9HDGIziDVGu8SJLbzw9HV86ooTufar3+TlL1rP2iUOv2cLRTbMr73meZx12h7u3bydF557GstbewhZzjVfuwUPGCzGZE");
    regresar.append("QJ1HXkoosuYs2aNQMKc7RE9kOHDoEIYnqleLB4ATgDgaQIjA09iIctCecAjZGAim+spRtLOvEzByb1P3KjiClwpiYc2MRxbhu//apncNLYAcyhrWQOup1DdPdu4Mxju7zh/OUsstvpTOyiNTLKXRvvafh8IMQKTJpQL7vwggGSj5bIvn37dj399NObqC59AC8YBfHT4GOKjtIL+3No47efU4+HpSLTsiLRMrEWDeDrNDHyTCjL/ZiqgzUZUWrEZgiGIoOcCZiaoKw9eTGE5i22bt/Fg1trPGCdAx9RH5m3cDG//Zu/NYjqR0tkP3hgHwcO7Gt2ZQxiHAZYNA5tOrgYk8BKZ5XjqUkbSkeYwohCpoqLgNqkbVGbQD9L/quqGK3JRCEGnBGsBETT3kGMkUDEWE1rFY2MWaUIHmeHeXT3NDsOpZz8dPBgHahh1dKVAxQfTWCfnp4khN42oKAxZWKGc8hMndpo9Vue01/0zZURYyCqpt5IVhAXERMwSNLyRCEzlsIYMpnphCciyexIwWrEaoVQpXYL0mv8a8AO8chjh/A0G0mm184ycumv/soAxUcT2L3/ycakrVaWGoKhjyuS6AH/yCNeBbwz1E6oXMDbGk+HqM3eQJCUHo2Kxkj0igbBxAyVDDWWTJU81jhKjJQEU1PZSGkNpckoTcH9mx9piJJpfp/HSOTVr7l4gOKjCew9KzcRmfFhB1qtFiH4pnNcnGmZOIdqrCOgYo");
    regresar.append("m2V5PXbHapwanFYLGSlka1RnwMmKDY5j0TZiawNOV3vZYyKmlBHtRyaKKb3L5M1tS2pmPFytUDvn5UpR7VNPWZPS+YdLPb7QLVwEwp80+6r3LE44WqIMFiVTFq+2lRp0mm7C1EI0RrMBHyKIhGhEAQCCYjYLEoaMSqg6bXk5c09bPMAR4fI7k4ojYduxk49R5VYJ+hL4bZNfpDw200Jvpi0CaHLYdNkiOvi+nVhgpWk+OwkpoAxBBQoBMD6sAYxZqYymaDEq0Fm6HaM3uqm4g9047bSMBrIEbf5+tVrLFEnGR4HYD9KOPsqehAZgFZgFYrbyS9s2/o3POJERNBalRqolR4qYnWo86juUK7wLuMoJGgSi1KZYTKCkFSZslGSZtFs+XK4lMO3yj5cEEtQC7Qq4ayNXfe/p2BL/XRFNnLsmyEXaGPZQFyl6WihFlRVOaY+4dBISZ+7YHatghmFFyLwjjEFrihcarpfYTpR7BhCmzTw08dqgaR2PeiV5P6l6broAgeazwrVyxB2Aa+m3QUFVQeHty0eYDiownsvciuCkgqto4hkBcO1dBsJsU5kX15AtijkEXBm4xpdXTbxzDpljMZh3l02x62bdvFV2/4Lq/7uZO4+MwVyPQWxHo0RDIyYgRnAtHUqcA8cwQPMXiGWxZbB2I9zepjFjDENsrYyz8KmIzPf/EbAxQfTWA/3LgzoEGwJK22NGV3PVfaOMfwrpImqzeO9vh8bt");
    regresar.append("uwmT/4yA+ZbIKzNCRszX2HeNnZaxlye6nrx3AoJkQsFh8rjIu4PKMOissKMhvoTB3Ctsao64rlCxbSIsme6yhNlbXlxhtvGqD4aEs9zl6oKpokviaVtCUB+9w0MVUBhgtKF6i1Yv6iY5gAJkiVSBPANPDN7+6EkRUcrFs4W5AZxRHRuppVwmeJtVJ3upiotFwbpw5q5ZgFi1i9DIqerFlB1eOreoDiownszrlZhZTm33+6c6BCKQJTZTf1MfIlx65YyrLFqbiii8FLixLHfoWrvvht8iXrmAw5VS1EamyutPOMUHvqOpAVbVxW0A1ChzYHwxB1sZhibBkLFy7Da3JLc9YhBPbs2cmVV145WKQeNWDPZgw7Z1wDQH2ynjjsNJ/QaUOP+CVsFWOYINiyw4JMefapCymaqVDRRa1QW7jqK/ewaXIYP7SaMh+lYyK1qaiakkObZVTB0yFS5sNMjCzFHftMtsfF/P5ff5rbNuygJsPHZDcCESeRu+68Y4Dko4WzO+dmxUmLIAQU7z0GN0saMDc3C+s65cddjPjpA/z8i8/iulu+DhamPIRY01HY0YH/+r6P8xe/8/MsH2lT1btxoYOpAq5dcKCqyUfmoVhivoCd3Raf+cSNXPuN3UwCXQQxrbRCjTV5bqiqyPXXXz9A8tEC9tT/Z8bpS/pZmoBIAZim3O3xFOY/H9VnP03+5+J6JAJFlkEVmJ7az6kr13DeevjiD9P8zCxk1l");
    regresar.append("CVkY074JfffS2//LpzWXf8Ck5YMZ+h0chkCEyPFDyw5xDbtu/m23d8j6//YD8dTUrH0KxpQpxKsmILZZXUFQ88uIkNGzboaaedNpAOzPlsTE//3fMfb0AciLNYi5kB9yyuHmfk75imWgikbx0988R4ImfTx68TZv/e2a8/7Pva3+HsnZsTCL5LZoW2eA7uf5h3vunnuOmHXyYoaIDSR7K8oKwqHqmVP//EdxgGTlgNw0W6BrsPRR7dDV2fnAcmgWBM8qIOsclQKT4m4aMKiCSX4Pe//4MDNB8Vkd3YRj9l+lE9kmzjsLPAGWWWCiwl9rQBhBCxMWKVJItVm6qapNcQICKSKFGMESNCDAExhrppoRjKaaw1hKaXk0izo9loXFSS+YWoPXy9YBSDQX0HI5F2vZdlw2P85e+dz+9ecSOTmlLj3nv8zMxkOsLuLb2pGHEmbSdoIirNLikQTdOeXSGkDoJCkhF5H0ENV/7DPw7QfDQsUIMqh6G6GRNTUxhrf+Kz4DBGc5jWST4+6wAAIABJREFU/YkZGmuF4GtUQ79FS42DkaVMuYVU+SImuhZTjCI2CdFcllqk9/h4/zjs0kkTXSWpGW2WlIv1BHl3N2escFz+tmcyCgwDloBzDskKpmLjG+MyapvRxTAZDdMYajGobVyiQrLqztUzqoFlbVizeBjnwVfpJaZxe/3oJz49yMrMdbDDrFx7o+QT4MChQ4jNZzaSfkKqcabwoa");
    regresar.append("E+xhBFmrgfydRDXTKUZ+B9ciSwjq4dY3O5gNt3FRzMVsDC1Uxqi646vI/EsqbVaqXzanzXrSbpbnpiGAI5kYwIVDFQ25xgckQUV+1hsT7CJc9cwF++az1rhiGPoNFT12CyEYwYoq8hhKaIOtnnBTGEaCAacoFRYO0Q/OYrj+Xbn3oPv3T+egqF3BZoHEJjxOK55up/HiB6roNdRIixpteothefJya7YPPGK2YG3amweYaHW9W+1l2bvkRRaCqcFNGIU0WjTx0szDAytpIvfncz7/qLDVz2oRvZsMsSFp7CVL4UGZpHEMvk5HTq7STJ/yWtD5rpOGspGGPEGUdUiw9KZiyZBMLEdsK++3jBKeN8+PKLeM2581gMDFOS15MUGsmBISItarIYaSuMRc+oViwkctoC+J3Xnso//enPc+nFp1Jv/w4XP+9khiAVtaohM4IV+Mp1X+Cee+4bRPe5zNmzWVQl+a6nMdUNRJM1ZtBN89ufkIGZ4fmGiBBM01RMe6nNnLquaDmhKxmHQsGEH+cj1z7KIeAb98HNf3ILT18Fl/3mxSx3QiEVbSdEH4k2/RHTTLbYkyE3X9MAmcsIIa0FAsmg1RU5MXap9z3EQjPC5W95Nr/6ysgP79vBbXfdyx0bPLVAqCHP0ukWwGmr4flnrOEZJ61i9fIRhuwU1cQ9hMf24ew4J6w5jtOPhVu3RmpS+x3XBIi//9CHB6iey2AfGhrCiGk2UWeoyn");
    regresar.append("SlBIpG+puEYEZnNCc9GoMm7kzTfCvVbqYUpolKjEIUgzOGKmS0lz2Nv/zYt/pb+rkItSp3bYNf/d0v8Y/vOYkTx4fIfEWoKrA2ea73/67OrA/UYIxNFUoh0CpyKu8J0eOyjJYDKafIqIl7O6wwLVY/axWXPG8d+0rFm5z9+/eTZRkjrZyx3DBmA1l1iOrQTszBhxGZoEWJdcJ0hIld9/DqC5/OnVduxFODSQYMBsP7//ZvBqiey2AfHx1jfHycA/sPoiSQKnBoCrw6RMwTNk61B3Sa7EtDX0QjhgASiWpSyVxUnGszXXZw847hR4/WfPHW/cmWwliqKBR4BDj/+WOcsPo4qkd+SBYq2q2CLiGZYkiTKpktOpaIIZk3qQasBnKUSj1VWaHOYFxOZoXO5ASjheL3P4SvIitGx5isuywdaqU2mCXEQxW1dhEDLtYYV9OpD9IaKuiWqQAkTm7l2aes5YRlcOcOKCO4osCXNVYs1335K/ryl184yLnPRc6+atUqGR8fP6x4IwITHag0IzY2codnYcxhue7ZHFpmTYReh44gDu/moUMr+IfP3sTB0NSPqmKwCMl78Tfe8ho6+3YxlOcYY6iq7hN+p+lnf9IkU1WMMRgrlL7Eq6coCsQajDhijPiqy2g7w8Qp2vEQC/Npss4jLJB95N1tFOUjjNZ7GNMDDMshCjudnBWiJ88dIQSMccRQM5LVzCu6vOR5a8kAm0FZBVQctSp/+P");
    regresar.append("vvHiB7Lmdj5s+fT2xMhZw1KLB5K0Q7nLri9XqIGvoAm4nyAWsFZ1LlvY1ge8UPElENVGoJreU8Vo9z411TTDcTwYqieDLgzReOsYhHyOpDmKgEa6l6zRG01yDMIiqIWkws0qGpyESNEKzgbcZUACNtTCxw6nAIwZcgNcHW1NRgBB/BNtMtamOh0XTQSPsOjsy0qTpKZnJyDFp1sPU0zz3rVHKgrgFXoDZDgR9vuIsbvvm1wUJ1roK976FioAoRBaYiTEwH1OR9B4Je49zez0gDaqJPJXxRMYGm6W6qX3V5Ri1tOq3l/On7P0MHCJITxRKikhE4fhG84RXPYWrXfTit8d4TxJC1h5pEaDqkST9KbLYwm16nTe4nZYNE+s2AbTNJej7y2hzBNJtGs9YezfKDmU6v6S/7MjI2PI7v1AxlbWJVE6YPceYJC3juM1LhEqFCgwcRQlC+9pUvD9A9V8G+ZMmSpu3hzNc8MNlVxCR/lV62JsrhHjNGPUKNkPLoIhlWDVYUkbS9HvMxbn9wilvurhv7i0iwGZDaxb/ihSczbLtk1tNuF6n1h3WUdQBc4zPZgFF70A4gaZWR8iEOGy1ZhDxGrHqMeoyEpsbWImREyUCEaFMXPGkoURCDF4dociGwMSIaKVwBtWJRyk6X0aEFtGJk+uHbufRVZzEKoElBY7KUKv2rv3r/AN1zFexr167tV+OLzHgM7Np7ELWOECN1DH2gR4SoMmNfzY");
    regresar.append("wwTJp+SxoFr47J4DBjK/noF29hghRRNXrUVxQGljl46yXPRTp7yPGpQsgoDsXO6qIReVyXPvEgFdGEZtL1+ikpgiZrjd45NZqdiPS1NYYZ54SUQUoLXjS1moxGm4ZphhCUwlmsKL7yWDxmeicnL23x9NWQKxirRO9TUzGFqz70dwMqMxfBvmrVcf2PbcPZA3D3fZuxedEUIs90cO5F+dnUpvd91RTNQ4CaFvnCE7h146N8/8EuJYaoqb+qIVJE+IO3Pp1i7z3Y7gGcRArjcb7DUKxo+QpDSBmeWYAPJkLT2dpqjaXsd9rodd6IMtNqpkdOTH8jDLIALtLQLW12atMEiGLwIngjdFVRI6Cewine14goQ22HhEO87HlrmG9BfOomFpvF9xVXXDFA+FwE++rVq/sf1z72wX7vg5uwLk/1qA3I02JVELGpr2izfa+Nq5hKo3qUgmjGmXJL+fBnvk8JYPM0oWIqcTt1JTz31GW47k4KF6h9N0XGmIqinbEYTTu2PY/GHrdWiY3ZUezz62S4mihJFNOYIM1kjUTNLG+YxOt7kdxo6sfELFewiME2m24+1lSxJh/KsdbQmZqgLZEXn3UyI708slhwGWqE+zdt5ltf+eogus81sK9fv/4JeXQFdu85SFn7vuBKVWf6K0miMsFYAg4hQ7BEkxaA2BYxX8R1t93Phu2p+5wPXZxx/VTjm195LiZOYVxN10+StTO6PhBtgeQjTPmkgk");
    regresar.append("xAdZiYNSDuNRxwM5dSLZECbTpvVMZSGYs3EKRHzGIzYZrILb2Fb7PYlkRdVNLTJNOICwEnhmAzfOYoYxevZVqsTk5yzAhc/OJ55Mxyym6cyT73+WsGKJ9rYF+37iSZoTHSN3ab7MDB6UA3WKLJiZrScr0ImFSMBlVDaPqiKoEghtqNUueLuPorP6Z24HubUM1y8ulL4Pz1a8h1ilh1KTJHqD3WOIwtODTdISvyZuKZfr69p2/vZV9mLqOZ8Wfv5eB/RifunpfjE29GnJXbT5qeGH1afGLS7rAowXucevJqHy8682QyQGKVooRxBFU+89nPDVA+18AOMDw8lkryGvvq3LTYuge27DqIL+bhaVMHwRqDIVBXHYwouRQQHBiT7OBCjUaQ8eV87lt38uAu6PiZiRREscB7fuNihiZ3YMuD5EbJvCWLOZaUN88KQ6CadYaRntRMZm3hRkl2RioRg0/GRupxMTbe8s2WmMzQH3oUSONMBhMws1KZqRFZSv+IUQjJkMmpgajYzGC1honHOOOERaxbBhmKaICgCDm79x3g1ltvHVCZuQb2C172sn5ZXpFnlDEhdOeBilqG6PpIURQYhbqqGGq3UQ1orIi+JNQ1mbFUNdj2QqazpVz1xS10gKw9ijZ2zy7Ac08vWLeyhUxvx9JBCFhllv1cnFX6N7Nbq4fJjGN/EaqzZMiN7rI5fvpF/unfE35aza15/M+JJ4vTmPIAzz9nNTmQEX");
    regresar.append("FiiSgRw7vfPdhRnXNgv/Rtb2uyLVBWNYISgHse2kE+uhhjBA0lVixO8qRHESW6aYZHoXABDWCLBVTZMj71tbvYNp2KJLplF+OSkGyJgzdcdDa+uwVx+wm2JJhIMKHfB0n68uAZAPYWnqEBfe840sZNIkqopnjes05hxPXUmSE9hQRu+/ZtA6TPNbCfcOIabOaICsYZFKECNjxwgIoRjHNoDIgaMtem7AbEOqrKpxy8j9TkTDDG9m6Lj35hI9OA2AJUUR8YNvCiMxxnrZ2HdnaQLIxi4t5NeBadzatnsWiZcRjuve7wKqkjMSJiFPVTrFxQ8LTjUs2XxhQsVANi4Js3Xq8DsM+h0RoaSsUSmmorazxqYNtu2HkgomLJMkusPeoV4zLAkdl5+LKNcaN07TCy/GQ+ccP3eaQLwUIM0DI5LVK10DvfcAHt7qOYukvLFZjGSg4caN4cLvHl2cjGN1VRERfBRYeLbsZi+giNEGpMrBjhEC945somPxQxJi3XVeGaawZZmTkF9pVLl8lFF10EQJEXZHlOFWFfFx55bBpXjBBj7NeRZllG7VP+O0ahGx3aXsTmA8KnvraHSlL60RqLhmnawMXnjLFmcUY89ChjebKWS/qV2Xlu+vp4kD5NMU107ykgTRTMHDCfFIWMmtwf4FmnHpeUkAIxNsI5Aw899NAA7HPthI477jicc5RVTVVViE1E4/sbHsQUo9Q+3UBnlbop5UMqnBO8tc");
    regresar.append("jIIj5x7a3UQKZAVSGxy6jA8gze8srnM7HjAVrUaFkiMWC0xlBi+rugVcPdU01SL13SIzEqkWhSRw1vlSMc2DHG4YjYch8nLR9jxDJT5GIMBNiyZSuPPbJLB2CfQ+PCC19G8BHTFGTUMe2RfPPb91EyhM1Hkz6dmlCVZLlFQ0jWE60F/GjzHr5y804qaGQBQm5S0cVrL1zB6vGArSZoOZt2M8XOyrQ0AjGjRNE+OzePO3qgDwJBZE7cQKsR6R5k6bycYxbM5HRC07PpgQceYNOmTYPIPpfGRRe8XBYvWtz0DALEUAEHOrD7QMTbcdRYVGvEKCZJrqgYIgwt57Nf/x4TCqU07ZkI+Jisnl//8rNhYgttF6lCxIsjmJxghlDTpsZQiQFniUYJGvoyYmkku6IG03MYaAq7j6S9qtBIJmKkLRGdOsgpJ86fZUySLoSGx1uDD8A+Z6hMqxhKyywVxBgOdOGWO+5Dho6hxJAN5YDS6ZREk5OPLePenRVf+0E9sw0kSfQlwKVvfSGOThJsEQlNswNfJ4/04AVrCqzJKMuKGCHL8qaIgv4GUF8L0+TkjZojfhk1Ck4MlsDUgX2ce9b6mdqupLNAjOGmW24egH2ujbe+9X+jW06ngmkRokY88J07H2ZCxylNQden8ueiNcy0L5DRFbz/qq8yDdjM0W6P4H0C5XAG5z7rXLLWfLwUaJaBE8qpQ7SdomVJHiNSB0ytZFKAt1RlbIRmoC");
    regresar.append("YpHA0ppZeyMQYX3BFvVdn3yIxK8F2WLRznMO/jmBSh3/rWtwZgn2vjN97x9qYvXr/REAps2Axb9wfM8AK8ZKhYSm8o5h/HTd9/iLseajaQ6khnupukwC55J/7uH76Pj37xu8SFz+BQvppusYxYLMbHDOMczhk0eoKvyI2lyPOmf6npK+UTn58RdFmNj3MJO0JURmYoirUW9R2GHDjbFLmk2cC2bdsGYJ+L41nPPhslpKp7l+OBnRXctnErtBbS8QZjk7pwXz3ORz57B1OAcVmTeouoNdQKdSHctw/+4totnPMrn+GTtx7ivolVVPNPZSpbxJQq3VCSF5ZW4ai7UynaG0F9oF9FZAzegDcRNYHY7LYe6V6khpjcxJyj8jUL5rdZsjDp+Wd4emRqamIA9rk4fumNb2hUKYIGxWPoAt+47UfQWkgdC1QhyzKmp/YzUqTyutzXZERaRNR7EEddKmodB3DsAf7i6jt553u/xl/983fY6Y7HrjibqaGV7C5bTMacrDWMMYYYapzMtI6POCKp7C1Fen2cVuZIhPVG/04gxPRxq20ZH0vnaK1tJGXCvgP7B2Cfi+NlF72MhQvn9ygnSoY3jru3wpZH9zE8thgfDbGzl6XtHfzlu5/H1e+7gBccD4sAp9CiBT6j0ALxHjEe00p5+60VfOSmvVz821/k3R+9k+/uXUpY9Vw6Y6vZGxxdAsYqmU3ldjYajFqMZqAZkcc5+R5Jzo5Htd");
    regresar.append("HoGCH4LnZ2sxKSrmGqM/mUBrudqyf2wfd/8PJznvOcyzZt2tz0Pk3Wd46IrXbx7PVPIysnyW1yMo/VJKNFzqsueiFnPv0YuhO72bljGoMnIyQ/MYE6pI1Rr6CuzVT03PfwJNffupn7ttzLopXHsXL1WsQUlN260R7appIoNSY2mtYTh7UonrXjmj6fsdaOSN/FTPp2Zj3r1f885xdVjBWctRyqDTJ2LN/4zr1s2wtVBNsYTxnriDFePojsc3D88v/+a2AsSMCZCBqpgK9+t8P9+9p0Yo5ERaLF+ZpWtZu464ecOr6DK97+XK7+v17G658zSpvkoSgxNYhOnfig9h2sy6gMHAS+viHwtvfeyi/+1y/wgz3HUC16FmV7DUGGEFVsCGTRk6mgTQv3fuVUFMRl1CFVaziT4X2Nml7l0eySvF6Btc40TTiscZr+jIP+op1ZJX4SBV92GSpaTE9VRJ1ZSWQuQ4AFo/MHkX2ujmuv+ZfLwVxmUaKGJPFVJQQYyQPnn7UGyv0QI5lErNZkTJGFA8TOPkZakRe/4Nm8+PmnUZgOe3bspy4T6K0kKXEdNXXHsFApdGLawPrSDffx43+7G81arDz+RIrhcboxRcoqhKTA1IiYXoQXYvAYkWb3V0ECYpOHpahgtNm1Rft1siDYmQLVx12ByOHOlrOeJP2/mnQBIuCcYSoUVCNr+ept97BtnxKsEEPqDD5//gIOTh66fAD2OTr+4A/fc9");
    regresar.append("ktt9yEMUnY1BsPP7iPN73q2cTpg9g4RduBatK0ZM5ROKHuHCJO7WeEkuefvpYXnLGME5Zl3LVhH1ETuBu71FRfqhlGbJILG9i8D27+0T5u+vGD+NElnHTWuZRiiBIQCYSywlkHIklKqzV5JhjrqL1PbDkGnCSjpLSP6xIta5iPQQ/31vspoH7C12d1RzAO6lCjOEK+iN3lIr58y0Z2TUJofI/zPGfp0iXs3rtnAPa5Oj796U9f9vGP/SNTU910jw04kzIk84cqTl13PDq1h8J0wSpRFV/WaIi0c4cNJUWcxlT7mV9ETj1hFW/+xZcw0qrZ9eguyg7kjee7QZOfIgavKSrWVtk1Abfe8SjXfPkOTFtYsnwVw2MLwLSoqkAUxWWG3CoheKpawRpc5nDWQuwVWefpDTTA7jsS/If1NXIY5amjR8XiXIHPFvPQviH++av3Mg2YPFGe2nvmLxhn7779A84+V8fKFcvkkksuASDPk2NYGVJG5crP3MejnTZ2ZAFlDRoduW3TGhpPHTtikm05M4WpH6PV3YXsvhv36L/ytpcexz+95wJ+/81reMby5DRgiYTYBapkCDZr06gEtk/Dn3/qId51xQ184Mub2FmcgFl5OmUxzlRQusGiJscWLYzNKGuPj0oUQzQQTCAYT5CeUlJnlf7NPuSwIzkPyKx2N9o/AIIYxDlUlW63pBsdUz1j15gqmQDOetYzn9Kc/aiwNd686UE9fu0JKb");
    regresar.append("ILqSVLCIwAv/DchfzRf3k2dv89FLFEQomq4vIMjxK1wto6ZSRCgSOjDoapaCjbw+j4Siq7ktt+/DCfvPYW7t4KlUC3kciGJp2nkiWdidbkMVIA8wRe/dL5vPaCM1ncqhiKXWL3AFno4qQCDck4VSO9sr6eA0Gyz6uT/+MTYo48LtvCT01zqkBtkvuZiUo19jQ++l3hv3/iNrxA3VA16xwf+8RVvOH1b5RBZJ/D4/g1J8j5Lz6vf69jBI+hNnDtt/eycef/aO/Mw6Sorv7/OfdWdfdsDAwMoCyCgqBGUFQUF0yMuGsSfTFxS37RuCVxFxX1NYpRMJoYNcYkLonvm+VVTIwxLnFBNG4oAiIqooDIvm+zdVfVPb8/qrvpnulBQEki9Pd5+pmZnq6q21Xfe+65ZxVch/6ELoWvkq2fEhJJQGAcobU0hI6MKKGEEDVR7WXoaFvwVs/BX/4mx+2e4A9jTmTc2TszYjfoRCztVSGMbBwqa7P5nkALsFrg/mdW87XLnufOJxfxxrLOrPF3JkrUkkmnsS5EgkxeKnsa4bm46oAoqEhcf6Zdk2K2iFKpSSAu32NKA0HV4tkUtXVdef6112nJTgTPxA85Ch09e/cpS/YvAmZ99KHu2r9/NjgsiXMR4KjCcchAn5+NOoWqZVOoDpaS9BxpF5DxDJL0CcMQzwjGERcccnE0YCCgJoFzQmR8AvGxVd1osl2ZMb+JBx59hVfeb6aZuDtj6I");
    regresar.append("oDA3IKh81OjFrg/GM68u3D+1IdLsbTgCBIIzYVVx3QIK7hiI+KIZK4jIagrYLJJE/2DVYZiq+cIzoGFY8wMoSSIOq2D/uf+1cWh7kCrPH5OnXpykuvv8rAfruUJft/Onbt11+uuPLKmAguiJ064tEMvDIr4JGXpiJ13QmTFTRmmvE8g5CE0MePUpiMRaLYy5g2QgsCmsCqIZGwuLCRhDZjGhbhrZ7OAT2buOfyw3n0J0fztcGWHj5UAn4uNg0TmxWN4IzBmJj01dXVdKisQsOIMHI4zyNjIWOV0Ma12dUE2coEObWmrdpSaJxxzuVL4KlqXA3NeCCWKAgxURD3iK3cgSkfLWNtkO3PlEhmi00pBw8/ZLsm+heK7AA/GXeLJDwfcPgmDv2NBBoVfvPwO3zSmKQpUY+p7IRTwTcWE8R9lawSV+UVRa3BeBarsbSPwhashFiXISnNVLMOr2E2iXXv09Mu5OYfHMe9PzqB44d0oFbjVo1JHDaKxyEaS/0kcMyIr7B2zQrAEURBlqS5vFah0KSer/P4aQ/JGNLpdPYPIQwjwjCM2+ckfIwxqFdBS6o7j73wNiGAGNKZACs+WEuffn3Z3mG/aAN+ZPz46x95+CEijfB8i7GWKKsDvzXlQ0447giCxmX4miZKp0klBDSDEYdIEJfUkNi64UeCdTmHVdbLKfHaH4UZoqCZKt8hTSvpYBoZedRQvn74QKrMelYsWk86Q9zpg5joV5");
    regresar.append("+5HwO7G/zMUjya8T1DmMngixdPLPUQ5yNqs7Z9mw06yMUJZ60vRYpSLOJzLSpVNW4cbD3CSAnCAAXSiXreW9uJn/9xBg0OnFosijWWgIjXXn5lu++xZL5oA/7GSSfKAcOG4ntCEASEQbxmN4bw/lL4xf+9SNWOg2nWDqh4cVPdnHqQje3WyGXVgVxjA4tnElgUDYO4VIYF3ygSNOBHK6jRxTTOe5Xahg/43rGD+d3NJ3PpyXvSXaADSr8aGNKvlszqj/ElypvOPc/D5IotqUWL0ypK+5JKIFJHGIYYL4HD0NIS4nkJEhUdyXgdMR378NjEd1gVEu9KcXgYQheyY8+elPEF2qAWYtHCebrvvvuyZOnKWCN1sS7tCaQcXPa1Ppx3wr6EK97GBqsQibtHGwlxRETE2dUiEqfWkchK0Bacy8Tve35ctTGK4iJEkcOaBE5SNLRYokQNqU5dSUuKZ154ExM2cdJX9iRYN5+qhCFIN+JclA2xlSLLS9zRD8TFySEq4QYLC+Tt5xtEUqy6WGvBeARBhBgPay3rMpaopjez1nXg9P9+ijXECepxN6a4rPdLb7zC/vsNlTLZv6B44onH9RvfOCkut0E2WwdLhUbUAbdfdhj794Vk80Ki9Do8QqwJMCZWFVSVKCfV1ceFUdydzkAUxVWA1cb1Ej3Pkkk3kxRLGDowPuKnaAoDSFTheR3wrJBZvxifTLyhFCGVSJLJZGKdWigwM0bZHq");
    regresar.append("05Ke+KTIqtyZ4r143xaWnJ4CdSGM+nubmZoKI7mc5788Ob/oeXP4QW6wMhLlIq/SSDBu3Fa29NKreJ/CKqMTkce+zx8vVvnESu2q2xibjjhm9ZBlx21wTeWGhoqOiNpqrARkSq4BTPRYhz+dzMiDTOy8SlMZBs/6bYdm00SabFkfKqcEFIwoLvR4RhAwnfYINm/PQKaFyKGEdaFesnSPgpWpqaSRovby/3XOyVjRuJhUS5bKd2RVH2s1nyp9PpOO1OlcbGRjwvQaq2G394cgpvfgQB4KKASBW10BSkOe/888ss/6JL9hwOOvjL+uqrr6IuxPMMYRQCBh9HJ4U7rhjOsJ19/KZPMOl1pDTE1xCHxnmsnkHJYCUuJuSc4hkLYnEOIhV8K0RhBl8inAuJULAeoSawxuC5EFUlbQ1ewselI0w2AMw5h1ivQErHBM+F/KJx2b04Rr6wlmS8kc71ak14lnQ6TSJVxdqmEEl1pKpjT56euoRL7pnJyuzqFgHixy3iD/vKYUyYMKEs1bcVsme/haLgeyb2dBofXICv0MXCmB8exBFDdiRcNI2qaA2VEmIMNEcOPItmSSbZAhTiNN+3KXLBhrJ36thQOz3ufAdgXBpEcVl1BTV5/ZscfSXnONpQ9ddkkz0iPNQ5fI3wrSHSuD1maBJY30KUxiME10La+QSJrrjaXZi9qpKzRz/GMqCBuPa8y64CzsHChQvp0aNHmexfVNNjO7hB4P");
    regresar.append("pcCLCqiTtPAC1O+eek+bSkV7L/0KEIhiCTQYkdNeoiPAMuikBzLb5iiWxEsCbrzcw2HIjFg2FDrpLGk0XMBslRYGJxeStQfLxgskdmA7uMwVg/O9kckQuJnOJ5yThUOAiBOH4+VEuQrMPrsjsvvbeG7//4eVYRhy6I52GsRxhGqMJDDz3EsGHDykTf5iQ7MOmVV3X/gw7GWB9jLGEQ4PkGjdIYhUqF44Z15oJvjWDHxArcmtkkw/UkTYCxWcmusWQ3LrZiRChRFGH9uKO2K5DKInGqnRFBXTubn2wBpbhnqyno6OfykYiII8wEVFRVEGpIJgzwJIFRD5cJ8XyD8WBN2hFWdsfrshtPT17Kj+54jkLZAAAgAElEQVT8J2uIg9YchkhdnCTuHCNHjmT8+PFlom+rZAd4482pOnS/oXnpaiSu+CsSL2FJhXofbrroYA7eox5Wz8UPVqPBOpKe4olFo2xYb47V1uSrCUDcp1RF81n9DpMteb1BqJss0XP9l3Lpe7GZMdsFT3Jy3+EZQxgGOCM4EVzgMFhS4hFhaFYf12FHGqv68Is/Pccfn1tJE/GGVCWB0zBunxNFOcKXib6tkx3g5dcm61cOG44LW3BBXANAAd+zBGFEhQepEA7bp4YLT/sqPauasS2LMOE6SKex4khag3ERGmWlpUq+0ZeaXLRJXC8mn0OaS8TOtnnccHM3NCNWcXE/11xXVTUoES4IqUimiJxHFEV4fl");
    regresar.append("zSLogMLtEZOg5g0pz1/PT3zzD1E0gbEE9wGcGKjxoliDL06NGDhQsXlom+vZAdYPXaNTt37tRxtrjYmaguZ6VIxmqJC6kAelbCyCN68/XDBlGXSmOCJoKGFfhhAxUmxLgANMJIAoyQnzqS7XotUdzTydg4AjLfubpAk8kVaCWboG1ilSaXYagaa/GRAzVVZByknZCsrsMlO7K0IcHv/voaf3llLWsAl7JkMlm3kSP2khJiPY8wDMtE397InsNpJ5+k4x/+cxxhYgWnNt54ovhG8bPVfbv48LWv9uXo4XuyU52QCpZgmpbgawMJH8JAs1V7vThZWxVRhxdb5QvaP7oNBZQ0zlCioPtdTuIbJNtoLL796iwBHplUDVR3p9F24d2FTfzt+ak8/tIiWoDAg5YI4r6rgk34+OJoaWnhwAMP5NVXXy0TfXsmO8B1o6/QH4/7SVzN18QmPnBxkFboSCHYODuUzhYO2jvJ1w4fxN79u1Ehjbj0etKNjYhG2bIdUdzyURweAUbAZbv65Vo+5lq+O+LS1oKHUwvqEeVqwouNi7aKR3WnbqRNBasz8M+35/LnZ2cwdTasz1pa4mwpC+JBFOEnKwhaGkEcZ531He6/78Ey0ctkjzHt7Xf0W2ecxsx3ZsSlwsI4MMvzwIXkyzvnShx5QKWF4UNrGNyvN/sO6EOXakOnhCNhAjzXQphuIAyaMBIhLgKJz6kaxaWijcY6uQBeAvUqkGQNaq");
    regresar.append("tZ1yIEkmTt2jSL17Xw6vTZvP3RAqbPirOggpxt3mbT6jTrSNBsprkq9V3ruP22n3L6Gd8tE71M9ra4/Oqr9LZx40DBZhuLeRLr9L5vaAkcYsB4gguVhIME0EmgdxfYfecKutT47Ni1I3379qCmpooONRWkPINvZUPQFxHOxVGKQehY09DIqvXNvD97PsvXplnd5Phgzlo+ng/NxAWaMnF1DVxkMOLjNMp7VONEFSFSBwKnnX4qv7z7FyNqO9Q9V6ZwmewbxZ5D9tN3pr4ZB2Nl21CGocNYi2oUqzwCxkEyAVEmNl2msql5EE+SUOPJUOFtuJFRYdGubLJzJpvOZ2RDrZrc3xHZLiE+aCbWyY3xslUNInBhzm1Fl/p6nnnmOfbee3BZmpex6Zj0xmQ97fST1bO5DAmjxngKqO9bhdhKKCLqeZ7GpbeMGuurmPhv63tqjFGJI1w2tGViwzljvclX61Uo+Ipkjwf144I1ahNxKqmf9NRYX61JquCpgHqgJ55wrI7/0+/LLdnL+GxYvGSZnn3O99XzK9T3KuJ8IuOrZ3xNiK+SLWlqrVWxRq1vYuIL6mWbcuT+FkHFmuxk8GNy5142pRCTWcSqFU/jaPqY8LkJY7MEt6BXXTlKp055o0zyMj4/PPyXxxTx1JiYgILRCi+lHlYNNiuhjRrPqvFEMaj1UGtiwhsbE32DNM9J9OxLNpzDWqvGeJrwYukdkz4m+1eHH6SnnXyi/s");
    regresar.append("/9vykTvIytg8OPOjKuSGFNrL5ky5Cee/Z5etkll8dkNVYRE7/yBaxRm50cG14FqkzseFUkq7Jk1Rtj4utY62fbkFm98+d3lwm+leGVbwFMf2dadkPp8gVUu9XX8+t7f1WU+zzzo9k6ZcoU3p3+DnPnzGHVitUsW7YMDSPSYUCYbiHUEM+ATVhSqSSJRIKa2g5UVVVxxBFH8uQTT/PEk0/FFUFUs7EsMGXKlC0e/8KFC3XBggWMHz+eu+66i3Q6je/7fOtb3+Kcc87hkEMOKW9oy4B//vOfan1Pre9lpW28Od1rr722iqRdvHixVlVV5aW8xOUMtEOHjpt9vWXLlk0+//zzta6uTpPJZJuVI3f+YcOG6dy5c8srx/aOO+64I08Ma22WfOiYMWO2Gjn2339/BWIrT57w6E9/+tNNuuZHH32kBx10UNG4C8ld+D0KJ8DEiRPLhN+ecd555ylQSjJuNUybNi1PRs/z8pL4sMMO+9TrnnLKKfljjTFFUry9l4jEJlKRMtm3ZwwbNqyIGAVScquia9euRdfNkX7+/Pklr/3UU09pz5491fO8PNl93y9F5jYkL5zEI0eOLBN+e8SiRYtKqhPHHnvsVifEDTfc0Epvj8l42mmntbn2AQcc0EZdyf2eG3vheXK/F543NzEAffLJJ8uE394wadKkIskX28CN3nLLLVudDBMmTCgiaiGRc58ZP3685laAHFlbj7e1NC9F/ELJbq3Vs8");
    regresar.append("46q0z27Q2nn356ngiJRCL/++OPP/4vIcPw4cPzBCwk8NixY/XOO+/ME7ZwbIXSenNJ30q/L2N7Qk7PLVzyu3Tpou+8886/hAy//e1v2yVxawtL4Ua28HO+75fU1TdB0pexvWD16tU7t97E+b6vgwYN0n/1hNsUa0p7kntTX4UbWxHRDz74YLsjvNleyT5v3rzZBx54INbabAWCuCrwrrvu+i8dx+jRozfpc6pbzk0RIQzjqmW5SgcDBgwoe1W3N8yZM0cnT56st956q9bU1Oibb775L5V4U6ZM+UwSe0ske0VFRVmN2ZYxffp0nTNnzn/cQ85uhrfqq9DsCOjFF19cJvu2jL322ks7d+6s/+///b//mAc9derUIs/t1n4ZY7SiokI//vjjMtm3ZXTr1q3owScSCT3zzDP1ySef1HfffVfnzp2rixcvzpNg1qxZOnPmTL3jjju0vr5eAf32t7+t9913n7733nuadUhtMf7yl78U2dm3NslzG+GFCxfmx33PPfdo1rlVxraCZ599Nm/KM8bkSVb4HqC9evXSIUOG6K677trG/NeamHV1dXrUUUfpD37wA7377s2LRX/55ZeLLCyf1dLyaZYcQHv27KmvvfZafpxnn312Xo9/+eWXy4TfVvDLX/6ypIOltUmvdXxJ65iT1pOj8Jw1NTXaq1cvzdrON4oPP/ywyKS4tcieO3fWeQbAqlWrDh8xYkTR2Hv37v2ZV6oy/kNw0U");
    regresar.append("UXqbW2JFlbO5ZKvTZHguY2g9/85jf19ddfb5dAf/7zn4sCuVqrHe1NqlwIL209ovm/c5N0wIAB+uKLL2qh5alz5875zxWuVqNHjy6TfVtALrKxlAPn04i+KWRvTc5Cb+gFF1zQLomOOOKIopgVSrv1P/W6hRMg9/Ob3/ymtl5NevToUSI10Op3v/vdMtG3BSxevHijWTyft25cKDVz7x166KHtkqn1itAe2XPxL4XEzoURtP4erVeUE088sWhSWGvV8zzt0qWLvvXWW2WibyvIOomKpGChSvB5Eb6QgK0lrohoVoq3wXHHHVdyVfi0zWspFWbYsGE6ffr0/HVeeOEF3XHHHfMTqVD6n3rqqWWSb2u4/vrrS248P0/JXkj03N85giUSiTwxb7311jYEe+WVV/JRje1ZaFoHh5WKhmxtEfr1r3+dPyYXukw20O2JJ54oE31bRN++fdtYUzZXN95UwreWzu1sXtugX79+Ja1BpdSt1skagwcP1hkzZuTP++STT+rgwYNLrjTDhw8vk3wbR7uJyZ+XZG8vUaLQmZMj8o033qilVJn2SN5aqucmlYjo9ddfX3Sua665pt1VZ/z48WWib8t44YUXSuZibg2HTqFKsbHJUCrl7/zzz2+z6lCQTldoNgW0X79+Rbmq77//vg4ZMqSN9LfWbnRzXMY2hqlTp+qYMWN0+PDhmkgkikiek7w56wQlbPClCNTeylDqf63NknV1dW3IN3");
    regresar.append("bs2I0mcBRe58wzzyw6/qc//anW1taWzFr6+c9/Xib69oD+/fvrWWedpRMnTtQlS5ZMzr2/ZMmSyXfffbcee+yxuvvuu2/Uvr0xW3vhPqC1M2djTiJjjC5ZsqSIhPfee28+46jUsSKiPXv21EmTJuWPmzBhgn7pS18quVn92te+Vib59oRCEnfo0EGHDRumo0aN0scee0xbmyYfeeQRPfbYY7VTp05tVJ5CNaW1lC78XKGdvFQcTuH7CxYsKBrDQw89VNL6kiPwSSedVPT5X/ziF+3uQTbmxCpjG8TkyZM3SkBA99hjD73hhhv06aefLiLHJ598or/61a/01FNP1R133FErKio2Kv0LJXthnmhrHbyQzK3J/rvf/S5/vO/7edUqmUxq1oSYx3/913+1WT08z9MePXrovHnzykTf3nDOOedsrExF0f+MMVpXV6f77befXnHFFfrXv/61iDAffPCB3n333XrxxRdrfX19nojtqSqFXs3W7+U+m81/zSNbuqNN4aNC8t5yyy1aW1tbdJ7cZy+88MIyybdXPPDAA1pZWVlSzy7c9CWTyTxpC0MKksmk7rLLLnr55ZfrtGnT2hDpnnvu0ZNOOkl79+5d0mHVXpSkiOhOO+3U5nw//OEPixxR++23X/4zs2fP1sGDBxdN0tz5UqnUv6wSQhlfADz33HN68cUX68iRI7Vbt27tbgALpX6peJNhw4bpJZdcoo8//rhmQ3QBWLBggT");
    regresar.append("7xxBN6xhln6EEHHZSfZK3t67lz33TTTW3ImQsIA/RHP/qRaja7+tprr81PwsLYGECvvvrqMsnLaB+LFi3SqVOn6s0336z77LNPuw4YSiRrFK4InTt31n333VfPO++8ooQIgLlz5+qzzz6rJ598slZXV5dy77fBzjvvrPX19fkAru9973v5YykRJPbAAw+UiV7G5uPjjz/W+++/X0eMGKG77757SVt7a+KXMjUOGDBAR40apY8//rguXbp0VeE1HnnkET3llFP0pZdeakPS+fPn61VXXaWqqlOnTs2vDIW1HHOTJRs0VkYZnw9efPFF/d3vfqeHHnpoG9Nee6pPa+nbvXt3HTRokF5++eV67733bhJBr7jiivzEKlRZctfZ1POUUcYWY9asWXr99dfroYceqp06dSrqmlHKxc9GgsSeffbZNoRdsGCB9unTp92UwIEDB5ZJXsa/j/zjxo3Tiy66SIcOHdpmc1toZy/MQDLGlCwVnY0/b3OcMaYszcv4z8Inn3yiTz31lA4cOFBTqVSbuJtWe4CSyNnZc3uDfv36adYxVkYZ/7l4//339fe//72ee+65ethhh2nHjh3bDQArRFZd0TPOOKNM8jK+mJg/f77+8Y9/1IceemijJJ4zZ04b720ZZZRRRhlllFFGGWWUUUYZZZRRRhlllFFGGWWUUcY2iVNOOUU9zyuZO1lGGV94dOrUqTCuI4fCsNgyythmoIA+/PDDeWLnaq");
    regresar.append("BkA6DKKGPbIXopUnuep8uXL38497mc5M+GvBadwxiju+22W2GkILkEhrfffjsfDdi3b9/y5Cnj30t2NhK+WojKysoi1aYwEbm9c/ztb3/LNaktE72Mfw/+8Y9/6MYkew5jxozJZ+EUpK4BUFNTU5TnWTgRCpMYyvp/Gf+xakxtbW2+AGcuj/KZZ54pmcRQou0LBRk/ZMu5lSV7Gf9elOpmAehtt92WI6aKSL4TW0FRIlqrMyKiVVVVetFFFxXp7purKpVRxlZBKpXK69utsu3zJM6Vg851mM6R2BijyWQyX5Ur9/5Xv/rVfHZ9YXJztsREGWWUUUYZZZRRRhlllLEFG9SDDz54s/TpbJ8fBfQ73/lOm2MPOuigjfU+2mxk2yYW7S8+rVNeYdm7z9PsKSJqEr6CpwnxVUCR2DRbWMraGNQYTwXUA015VsV4apDPvVHvxIkT85XI2tt/FT1zIwq+JkChQsFonz59tt09lTFGEdtu20U2qcscKqCdO3fWDefdUNvQZh90AjQh8TEPPvigbi65PGPb7WhHG2tSwSQQ1AiFPZc+E3r37h2X1QC9/pS9dNLYYfqP6w7Tn3xzoPp4+cKnRx99tGI97QR64bF76Ks3H6wTbz5Cq4jv1+cxlkJYyfoy8DdMruz9X7FixTiAUVdekb+PFaATbz5S3xh3sN5//l6atPH9+k8vuCpbdFDCqMk4KgwYB+sFVM0mH+97DhvCXgM68/rsFm");
    regresar.append("zYSAQiYlR9hx8Y9t25kstGDmG37kk6Boto8KoYeMkb+IkEmUxmk8bteZ7iLBFpqlyxmIqyPwvtm5J9KWAAD/jJmYP4yR+ns6AFuvfqxfz582WLBYSgvsLMWweS0DUkUFwkZEgx9ukGfv3SGpIVCTItab4ztI7rRnYnFTaScA1EnmGttzN/e6uRUX+cQegi+ZwIoGoNNnIcv3cXLvj6AHp09qiKVvBhUz/+ungn7rzzTjFYdSidcLx111DqGucSqiMtNbwXDOSI6/6BMYYo+nzG9R9B9imT39B99j2AFbd0x0YhSIiKFp0qMEoycigGFXAIuangABuXH0dFCMRw9E9WMHWlZfYte1Pj5gEO20pzcBg6X7WKH/94DNdee+2njvvpp5/Wo446noU3VNKxuoZM0FRA6Q1jMYBRJW18vCgitII4D2MjjAtj+SaOSWt25eRb3yZKQGNz82bdNwuqxmPZ2D5Y3VDg16jixOHEMEuHMGz085y2f3fGndiBardiwwnUEBlHGDSxqu4IBl/0FEEQfGZSJUEXjuuKpwGRAaPFp5wpX2Jur/M49dRT+eTWQXQI56NSeP88wNGU6kmvi6djreTJ7pPQAEfP6pAzDu9JTdKne9c6Vq5dx9sfZfj9q/PkCyHZEdQmUjxy+f58ufpdEFf07yiKwBjmmQGcdcvrNISQ5Tcq8MYNXbHNPphmABpcJb2uWcbOqZA/jTmO/uFERLyic2aoZcer5h");
    regresar.append("JtzpgFNYkqatKNzB7XHUumtYqDRhFrEr0Zes10lgfxDBAgAUy55SB2jN5DBNAks80u7H/tm4RhZrPuW8KIRk5ZcktPPNeCE1BVWqxPk7c7V/7+bR6dshYI5bW7TtG+6ckkolVkexJgI2VlqhcDRr2LWmHOR7PZaaedPjNZRKwO6RLx3GWdcFgMxc/xupe6c/QPb+OYY45jwdgepGiK75kqIoIh4Pez+3HBg+9wwF578/obkySnOlYbZdaN9RhrSboMghKKJUXE2srd2PHCV//lZDdbctC+++6DybTw3ZtexLUasqriGYuTar585etMW2WYs84ye71h9nrDnHWGacsH4LwMoDhRqhESKHNaHN+87m+otr0PkdNWj+LT8T8P/i8200xDyiMUv+0HQsda25Nr/m8eK8Mk4rz4YZoKMhgOufIVnPERHJFN00GXEkaZzb5fgRMiSTA/7EckHqiHmiShP5C9L5vAo1NWIlk1/LgL/8QnYTeiSFE8QqlgZVV/hlw2ncgJNgg/F6LnntVbqyswTrLrXLEgeO7l9zjmmGNELLy2Ygca/K449TDGI2M78uCc/lx077v4EXmi5479+bmH4DtHVdiMiuZ5st74NNL536LGeFty0OQ33xKANcaohxLgYQnzXzTSBLO8ITR4KxiwS29SqRQigmSXwGPGvsFhfdL87/d2IEVE4LWQ0QgwnHz0IKzMa2UGUKKKWrAGok2n/Le/fUZ8wRanga");
    regresar.append("nEc40UBhhHvmG/K2aw0hM61NSyZs2a7Da4mcNHjNAXnp/A8lR/OrfMxDifjtqExRBt5rSbPPkNDjhwPw688XVSLS04IASaZBGe9fCjkCCMl/+GRJKh175GxzCK9w8Ca1gKBv70h//llFNO+RwlohOPQEObwDpLoYxRVRasy31MGfnTt/FQOpDBAitYBcwDAoKoeLX1JMURfZbiRUogtkCyOowaZi5a/e8xqmzpgeP/70+acgYjDmn18JtNitNGP4YNW/jggw/k7bfflmnTpkldXR0zZszAiWPiwiQLaoeRttAkXRDi83x9rypEDIUqu6iwKN0NdbLFXzPh1rWxpLnIsBJLAmKi5zbQvq8TJkwg9IT9Ln4F50JCm2GlvwNGXcngtRwOPvjgvMk0F7u/z777SpBRef6551kLrAOaAFFwziHWJ2eJSafTQhjJOoQ1wBolOzXgjDPOKJUPUGwha6cdpYjofffd1+ZYF0agfkmp3+TlfxdIy5lnn8UaY1gFKAGa3+aj9fX1Wl1drcYYTdkmqsK1aAl6+c5RnZJ8n9o//OEPujGzdi5OqtBcvKXWqC2WEoKnf7jsQI6un9Hmf2ntwNCbPuaTZiAroCyGtbd2JYia6XxNEy4qscESqyvH9sDQUPwQNaLr6LWceMop/OlPf9qsMY8dO1ZHjx7N6nGd2nzdlO9TNWo5qoJqJAkSmqkMWXlNR1bX7km/H76Y/aq+XnBED35wxK7s0D");
    regresar.append("SZ0CpGBaNKi0K3a1eTSFWRbGpk3i29EG1ENFa7dhi9GkeCK4/tyfcPbqZC0vlN54a9jrLDVatpEegI3HPhEEbssABLmBcjBlivlRx1+2q67TGUF154QXxBQ7+CO7/Tl9N3XoYTQ0XS8pbuw/gnXuasY3anT+NHqB8QSayTL0oewB6XPIuRgBEjjuSlZ/7BohL3BqfUXbMaFHr78NK4PanMLMeTTH5jT3bC7jh6Fb3rfF4aVYmVGnDrMcaUNAbgHGAxCD2uXsEd9z/Ay6+9ym/v/x2jj+nKpQdHOAmxWGa53Rh9/2QWLm/k+R/tQW1mEWpCUJ+WKMGu1yyhQUB103hstlz/idirR6Lk/1KS5upThiKRoTKR1IRfxe1n7Uk6aEHUx0nA1044tu3s1AhLc9u3JTYVbi7RAe6/9z48Y0vO66YwgzqLEuEhmiGke5NDjWCbl+XULq0KA274aki3prdwYvJWCydCwsBeXZLYlkZm/GwIBGvzliaAXTpCggxnHN6dKrUFE9sViY4oe1c/HlvPsTvOy6uFpuAhNXs7MndZI0cddRQJgxqF35w5kNN3WkKEomT40O3BIZc/yV3Pr2GfyyaxriqFiuQ3nzu2TGTkoBQOj9ffnESlQGuXmQMi8RAx9KiE12/sQipYhLGZNqSJrW2QrKjisU92YobbFRETW7GKiBZPfiMR6/x6PkntTyRw/tln8eAD9/H9w7pz9SEhRiK8SPDUceDV/+");
    regresar.append("TF2c3Ma6pg58veJTJZIQGkTMisn+yMb5NcPuoy3apkV5ukNphTWhM0wsh+87nwuH4cP7Qbs35cz3d3XYEVj8hE4ODFF18qOmbcuHGaBEppKpEYQrNli9Ccj+cSbVhui02CUYqFdwzjwmN3JSnKu7cfyfSbOuGppcLFCqu1lkMG9+DRmV1ZlhqIttqgGgx/vbwPH98ygI7hPEzBCisiXHH2l2nCp4luBAQl1QWAAI9qDck4Q9CKKPnvsq6etDGceeaZRA4Wjq3nhL6zMTbCQwhJMfSy5xg0aE9Wrlqzi0E4/fZPWj0bn199s4J37jqatavX8L1vHhQbRwv2R2CIbAVOHQua4NeTauNvGpbeq4TAuwvXcN69s/jq1S+wxnbLf6+ihVsdXUevZ5er5zFo1BM4L0XklP8+dQi3jYiIHHgaYQystDtm1WMnQdgsTiyLK4cQaTxthYgKt5znbzyA2269Y+vq7KG0YKPGkg9PohDr0lx7yCJ+9bX11Oo6Qm1GxWEUjLPYRLGeeN1111FJ1n/ZZvZUYmXLhurU0d5+MjIBlU3vcMOBy5h7c0e6Nr+GtRYkwMehqvTv359n3lnM9+6dwoDLX6VROrd60EqNLsMLMqxL1OOpIKqIxhLz/TlrERuw3yWP4vxUG9+BBdJSBRLSbJKcfn8T6xK92u4vgD9MmIo6R4/6nXjvl0ehGBLq4bKbQBf4hAamT3tb6uo6zum1y068trDVA1eLZ9");
    regresar.append("J0bp6MAMfuXoEWrDKigorDugAxBmMMNz86mwseTtNku7Ta3ioOg1rLX/72d6KwRZwRIhIl1lGhiRrSQBg0oxGSjlr4n4sP5vt7LiJNGjUhTgTE8f6aal6fNKlgPwLvLQqzJs945Qw1QX/5EMmugltPjTE+yRIXERFCv4WMUzyXItPgiFqZEhWlvnPXNrb5Gg8iaWvaS5tE3pKz+V5L0+7XNC4i9HwiP4XB4kkCcLGDJbu2z5w5U2pra/EqUohTGvxubW6gUTjq1rnsdulM5ld0J6MWlYi0C/jtn6ciEYTi0ZDs1epYRzqdxtV0zZIn4Jk563n2vfUlhcg/XlmEGohoorphGl7RihVBZXWRsap3rx5EJYSHks5btbp2yLRRGcGQsRWIOpxzgiR5eso6Gv26NvplKEkip5xwwnGSG2eSdbSVMIp6HsYYRIS//OUvinoc1v0TErQVmm99tJb9999fCvlR16lj1oGZC3NQUtqMJ1Cq7ebnQvYLLrhA/UyAmJa20lKFT6Ih9L1mNXVXr6b7mLV8xF7xggRExqE4jjxyRLGUcI57rjsW69paBhpSXQjDcIvIrurwaXtOh0eYSNJ31DLqRy2hy+jVHHjbMpxUYiOfQDdI4dWrV8tB+x8AJoGXbPWd1eBheXM1NDjY/ZIPaUnU02i7sP+Y9awwhkjAGqEmM6uVlFX8ZAeu+L94I6iRk2rgkEF926pcIvndTMJAZRQW6f0qjtWmY5");
    regresar.append("FQeGHiS1w4YucSd6WShIZgoTb8sK3Ach6L/d4MH/7l7OoY0qczdIzmttHDm6t3w3qmaFJWRhGIbSPZ31ndCxHhV7/6FSf+14lM/ulX8KUBKRFqcvvD7xZbcQT660ykteAUoYPC8OHDt45kv/vuuzh7eFfQZAn1Jsl+o1+mEVCNRBUZNvoFDr99FfiVKCm8hM8dd9wllUQa+k4AAA0FSURBVKkahQr96mHDVUUZ4k1rE2anqoy88U18u0UuAQTYo52N9JdvWwsdOqIg4GTmKuFHz1XTLJZLxhdL14kTJ5IQR13LvFYXiGgw1SQSCfbcc09UVX74z4Gc8fROfJxGnHNijVCvAV6m1d5BDLdM68rDL8/LW8YuOXkInZqmFW+o1SMiSYOAdfDRrbu18Vo7MZww6rW8rvz0008rwPlHdS6h2sUbVnUJkq1liBoiEzLyyolMnDhRYlUt4tH/3pXKjNdKDXQMuOA5Bu85KBcXob0qwaOheCJmPa4jb36JZDLJueeeK74Tdsm8hVVFs3sGl6WjKKwzxdcKTIIK1mIKJ4YaQGMz7ias/GbLpKVw1MEDKeHoRLCoRkUXFwOLV4BpWUtLVMOE556nqqpKg6gFa5uZ+OJLeC72GZXCqmYIoi2T7EZhh05+KW2etWuaURcV2ql5dcY8fNfEI5NX4nkbbri1FudcyekUmAoymQyXXnopAH977FF54fkJG5ZgVQ4cVINK0GoEjt//eTqBbj");
    regresar.append("jvkN17l9B3HVHW/+fw0ExDW8mPsr7gwIceeggRwdegxCoR4ozFGEW0scRXcqxLF/7tURE2ELRSW8X5ZBx8/etfz79XnaSNDi0iRFFECOyxxx4A1OTdBVKk1oESiVe0wV2xduXhtPv8HWFsYt5KOrtYdu9VmZ+JbeJNWu3EnTrWRLDe9OLKvwccf/zxACQjRzICJxUYXzAlrCYiwvoA+vTps2UbaaB7dekb1dySjeMp0AtnLm7mzZbhqLFMnz696H+qpXe6aa8SgBEjRrQzCp8ThvVDWi3tTkLWh8VSqVuqCdt6fRNH6CWxSYPnOVISlJDWIY0RHHHEEQC89NJLsf4ctp0YvhGc+EgUtJWI4pDQUbSuWUsibGpjPRCnqMB1110nOdoetm/vdhxeQigJbrrpJgBqkwbBttHrBReHduiG5/LcP5571hewWqy6IQ5VQ4jhqquukq1CdmtCvMZPSkp21XQbwt92622Enkfv0e/x6JRlrF3XyJ41jcwb04HZN+9EvWkmEyrGNUKJB7kuI/zqnns3e5yvvfaaIjDq1GElJb4IReN8++23aQGOH/sUGRfJ7rvvXhDvYTlmV4Mtwfen34klf8+ePdvcka4du2gfG3BknwaiVmQ36tPgkY8FOvvc7+lOOhPTShqLwoz1PYlaIAxdaV9g5BE5eOaZZwRgzpw5CFBZEGWZn5yhz5KKPfjWgd1wbfRlxbgK0kaYMu1NBaPdJY0z2ka3bv");
    regresar.append("FrkexY7vufB9SL4MKjd8qvQq3NSSk1LF22HIBFTQ7PZrex2fMaZ3HOo9lLIQVqzOgrLqODujaBajjh3ZbBiPU/f2vMLgN31gSePnDuPlSFS7AlJF1lFJASjU141lMwevnlV2QfkiETGVbd1ImnL6pDPZ8ZuivLA9itCoz4rWy+MeaO7U9i0rUkNiSJbBIOHDaML1VA53VTSygfET3qDemM48DhhyjA4MGDBZAwDFuzSfftEnHnOQPIGL+Njjvq/rc47bTTSo5h+ZoV/POmnUnpihL3S5n1kyHcfd5gUqD33nsflZppI21DAyffMBFrBTGGuyZ3iG3N2YkqIkjCkgYeejR2v1dWVlIBiEsQSfx9jUakSdDlmpXsc9lz/Ogb3bPGz+I7Ix7MvWUgwcTrAZjy453xnY29lwWoitby8vXDqAf93rfPJMCnXueW1J9VlPdu7c253z4N36Y0LXD3270x6sXOouwmW63DqsHphmutXLaap362YdX0ogziezy0dCDDx0zERelNMtVtjj1PPS/BhBtHsJu8jtdOhpzDoCqs9HvQQCWFQkpEqDbr6OIWYzWOyRg8djnz1ivT7jqRnRonlraHqxBZS9/Ry+jUvQcLFy5sd9zvvjtD99hjb7AR4oRlt/QmEa7FmdbqjZCRCna5cgEtAqlkipaWFmnnLukT/z2c/Svej1WMwqVUDV1Hr2HW3A/p27evlND4dNmPu+GVWLH8KKLFeET4fO");
    regresar.append("t3q3jhw4hVN1Yj1raRYDtcvZKxt99OGIZcO2oUS8bVtZL+ym9n9+bS/30PCRRLyAfjdqc2Wo0lTSQG52DAtStY5QxewrJgTB0pl0aNaTOBRQIW1hzFHj94iBXjuuY9usXjShJJhlnJ4Rx06aMYSbB0TBLrl5K0CjgCmyDhHB2vXIk1SRaPrSHhHJqdkOJ8nInY47rVLM1aRSsMzLmpDl9ygYbK/IqD2eviv7M5AVOb/EHfM7rohm54hMUzN/vgRbM22k/JWAptiIgldA4PpfPodQgRc3+yFx2jBeAsASEJCQgl3hQ+s3xvvnPXP7N6s250zCJWX7/zRHqte4GUKM5KkTtcs+5xh8YqiRfRSB29rvi4Hf9tbGNbOGYHKk1LvBaqJWMiLJa0q6XH6E+AoN2JsnRcJ5IONLTgOwKjVIQhK6ULg0fPpTG7t7DAyrGdihIkYknmUXPtivyDFRH9y7VfYXjFFNSksC6rOkrsoGqqqKeu8WOc+gSZ9VBVwzL7Jfa6/PmceV2s9XXJjd0wEmIkgziPUBTViDAMGb9gNy78zZt4NsGSm6sRIqyzQLzRtJpiacVu7HfFi7RgiMIApx7Lx3Yi4YTQxpLac47QgBclaCLixxMq+f1z85jy0Uf069+Pc4Z35bqjq0jqCqym4s2zxLE5673uWM3QIVpNiBKIIbSdGXjFhzRYcNHmxXZtshqjoQPTRGgDQhsQ5X6aIP9e0futfs/99GN7Mh");
    regresar.append("VEtJhaUAfiEyZqSUsIppGEBDh8AqlgUcV+nHPfJMIw/FSix/IjIilpqo3grGCjEKsu//IjRyJy+C7CmYiM2YH5MhjxExuVCUk/IBQPh6BkqAhie/TUsHcbM2Cr2UfadMapR5jKxuRnPFZW9uHLY+ay1vi5UAhxEMeVtNaxxSsSIqoqI2+cQJPdAQkcjV4FgoIarDbRueETIhPhvBZsoiM/f70b+136fJxdmhVwIkrkRaiJxWfopbESkknuwILKg7joN5OorKwgpIWMVKDqk7EBgRi8KMHsmt3Y65IJBMYwcuRIjJfEWkXCgBYvwBBgo5BQEliNSHsJbnulll8+P49GW0W/fv0kZSq4/8VmrnmiGY8qnIQE4sdhg6J00qVU61rSNrsn0QpOuOND1nmVeJJkqyFfYHQLXrbgZ8KgnUGrCjL44zBOT0fs0UVvOHVvPah/B7UmTn4Wa3TGjBmbrKePHz8+TtY2SZXYE6Ap0K6g3Q3aEbQ6+358bU/bCVJo5SvMFl/NJWsj8fg+pfJAz547qljUl/j7+wXJ3tWVVVo8pTxdc3NXXT2uruj1yc/7Ze/HBhxzzDHZUFdPa0GfuuYQnfvzfXXOz/bU6bd/RfftbrSiINz3iiuu0LaSTvSHx++t3/tqP+0uhYnonhY/d6NeNhHbgqaScf39bNWGolXVA/360F56zlGDtA405RWGGbe9z3//+9/VilGR+Nn89of76WtjD9E5P9tPZ/");
    regresar.append("18f31t7KF6ztF7aNKgubr/W8rhzfXBq7W2yFy3WRcTwRiDqsZx3LHVRjbYnTys9dGoGadQXV1NQ0PDZscJVFZWanNzc5GlJU6mllZucUgmkzz++OOMGDFio9fxfV+DIMjb40UEa+0mJX9bazV3z3JJLLvssgsffvihFNvK0VJqzLoOA+j7/TdwFCczW2vVRRFeIkWQyVArDgs0q0+zRGAdEkm7K2IsjASHBeOBa6G+vp7ly5cXJ2N4nhZ6sI0xcRhBexzZEMBAIpEgiiKSySRNTU2y0UXZsxBFWOPTxQasz4DzLGEYEZkERqLPlNC9uW5JOfHEEzWns1dWVlJdXU11dTXJZBJVpaGhgaampvwBiUSCVCpFFEU0NDQwefJk3nprKsZYXEGG/O23387ll1wCYRzD/Y1vfINHH310i75Y7qb++te/1uuvv56mpibWrl2fTbd2VFdX032HHXjwwQc58MADpX37eIEHLwjkxRdf1HXr1nH88cdLzva+KYiiSO666y69++67Oemkk7jpppvkww+L3fQzZ87UPQcObEN0gKUttWgJg0DuwYdhWhFlnYkZJhIg6pHyK2kO2xcW1TW1rF+/HkuIIeQ3v/0t3/3ud9t8funSpeeefPLJv16+fDmHH34411577Yi6urqS5zzyyCP5x7PPZLc/jh/84AfcfvvtUsiJdmVhJAoWR8DKjCUSh4bxPd5vn8G8+eabnylL6z+27MH2gqOOOk");
    regresar.append("qff/55wiDgl+fty8l9Z2OykZNxrLhP/TXLeOyxJzn+uGPKz6tM9i801BjDvFsGUBMtAFKIi/Nxl1T24dCrJrM87RFpWH5Wn3XfWb4F/34451itNQRSi0YQWFhT0Y0vXzaZJS0+u/TfuXyTyth2MGHChKISgpdeemm5Hn0ZZZRRRhlllFFGGWWUUUYZZZRRRhlllFFGGWWUUUYZZfyr8f8BLdKmvKj3dl0AAAAASUVORK5CYII=");
		return regresar.toString();
	} 

	private String toImageKalan() {
		StringBuilder regresar= new StringBuilder("data:image/png;base64,");
    regresar.append("iVBORw0KGgoAAAANSUhEUgAAAbEAAAGYCAYAAADMc9xPAAAgAElEQVR4Ae2dbcwux1nf55zjvDgv5NiGgE2jnDh2VdSiFkwS2kpJCA2QtMmHVBQkS+AASqAVrVRVyFDJObFUiFC/ACokEQ0GCSlp1XygFEMCbpIPVZzgAuqrFNs5VkoCAb9AQpwY7NP93c9zPWeePfsyszuzO7P7v6Xn2fvenZ255je7899r3vbM5ebj9BEBERABERCBCgmcrdBmmSwCIiACIiACBwISMV0IIiACIiAC1RKQiFVbdDJcBERABERAIqZrQAREQAREoFoCErFqi06Gi4AIiIAISMR0DYiACIiACFRLQCJWbdHJcBEQAREQAYmYrgEREAEREIFqCUjEqi06GS4CIiACIiAR0zUgAiIgAiJQLQGJWLVFJ8NFQAREQAQkYroGREAEREAEqiUgEau26GS4CIiACIiAREzXgAiIgAiIQLUEJGLVFp0MFwEREAERkIjpGhABERABEaiWgESs2qKT4SIgAiIgAhIxXQMiIAIiIALVEpCIVVt0MlwEREAEREAipmtABERABESgWgISsWqLToaLgAiIgAhIxHQNiIAIiIAIVEtAIlZt0clwERABERABiZiuAREQAREQgWoJSMSqLToZLgIiIAIiIBHTNSACIiACIlAtAYlYtUUnw0VABERABCRiugZEQAREQASqJSARq7boZLgIiIAIiIBETNeACIiA");
    regresar.append("CIhAtQQkYtUWnQwXAREQARGQiOkaEAEREAERqJaARKzaopPhIiACIiACEjFdAyIgAiIgAtUSkIhVW3QyXAREQAREQCKma0AEREAERKBaAhKxaotOhouACIiACEjEdA2IgAiIgAhUS0AiVm3RyXAREAEREAGJmK4BERABERCBaglIxKotOhkuAiIgAiIgEdM1IAIiIAIiUC0BiVi1RSfDRUAEREAEJGK6BkRABERABKolIBGrtuhkuAiIgAiIgERM14AIiIAIiEC1BCRi1RadDBcBERABEZCI6RoQAREQARGoloBErNqik+EiIAIiIAISMV0DIiACIiAC1RKQiFVbdDJcBERABERAIqZrQAREQAREoFoCErFqi06Gi4AIiIAISMR0DYiACIiACFRL4JpqLZfhIrAAgUuPOffIE+7yRx88SuwjD7nLl5rf/Hrtze7Mhevcmbu+w505Ohr2/+4PNXE83vw1cftxvfbl7sxLr3fuNU28YTEplAiIwJnLzUcYREAEjgggLL/yu+4yYsVfCJcLNzh3x23u7JiYIV4XP+SeCY3zwnl35kIjat//CndGwhZCTWH2SEAitsdSV55PETDhGhMYxMqEhQg+8nDjTT16JaouMeuL24+LMGOCSXg8PwnaFd76JgIQkIjpOtglgT5xacO4+B3u7Gtu6W/iw2u7+GH3jC9mxIHotPfZ/j6vDZtounzkWNTaImm2SdCMhLYiIBHTNbAzAgjFDc3zAPd");
    regresar.append("Pl+dAnxR/9Vfd80l1GLB7+cXduDJEJ4j0PXC1mdi5x9YmXhWlvEcg73n/U/IhdXTYT7y99TyO06kdr49PvnRDQwI6dFPTes4nQ3N14TIiTz+KOpr+pPTijEblTYfzwXd/pt6I/7Pu+xZ3Dk/rlJg1Lh/hTNAG+4zvdmfedd2c/1jRhImYWP97et/28e4Z07nq9O4st+ojAnghIxPZU2jvMq3lJ7f4umgkb0TkMnEiFBQFp/g4jC01kUgiY2WfxY3cjWIcBKJYv0muaH5/G20udL0tfWxEokYBErMRSkU1JCHy08VrwUvzIEK+xUYR++FK/I2jm/R365ZpRj3hliFrTrOkuNl4ZYlaq/bJLBFIR0GTnVCQVT1EEqNh9ATv0b/2EO7cFAfNBm5g93OSNfjOOHcSsaTplSL8fVt9FYIsEJGJbLNWd54mBGzYgAvHC+2KABhX+Vj/k7b4fcWfJK3k0r0xCttUSV76MgETMSGi7CQKv+4Urgzds5N7WvK+hgiKveGXknQ/Nizf/lHuavkF9RGCLBCRiWyzVneYJAbNh6DSt4X3tcej5wSt7++nmRbzTnV4WyvbGCUjENl7Ae8kezWa+gNG0tpe8d+UTIXtfM3+Mofcch42aFrtIaV/tBHZ9o9deeLL/iACjEG2oOc1oexcwuy4QMuaO2YAPGDHgxY5rKwJbICAR20Ip7jgP9PX4oxBZvWLHOK7KunlkduCwRJb6xwyHthsgoBt+A4W4");
    regresar.append("5yz4fT3/9Z9uZ/mlg3fZDJO3sn1r06fFPvsds0XI7vneK6MWX/ce93TM+QorAiUTkIiVXDqybZCA3w/G0PKtDOJggAreJcPk7cN3hGxqcyATn/3h9774WxraikCNBCRiNZaabD4QsH4w+ny2Moweb8sGqNgcNxucYZOYpw6XR8isf4wV8nUZicAWCEjEtlCKO8yD75GwOO5WELB4sOXlvmaYPOLMKEO/OZBFhi1MzJZmRWOFIGq0Ygw9hS2VgESs1JKRXYMEGKBAALyVrTQjkh/zssgXomOfV3uvWvnog7Y3fvvS5m3R5o3x6pj4GHSGCJRFQCJWVnnImgACeGHWX8RCtwGnVBPEhIv8+QM5eAWLZcLC2O+YLef6zZO+RxsTj8KKQCkENlUBlAJVduQlYF4YqWxtpXbzksgbAzlo8uPP1oJkv++V8Tv2w/l4enx8lkd79F8E6iIgEaurvHZvre+FWT/RlqC0RxEyeMUGsJBP8jzHEyMOzjcPtu3xcVwfEaiJgESsptKSracIzPVITkVW0A9fyMwsPCfmwaXyPH12c/rYzD5tRWAtAnop5lrkle4kAjb8nJPneiR9BvjDz+mXWnrgCPliVGLzd46BHjnySZwII54YTO9ybjMjPPvKVfu3SUCe2DbLdbO5uud4CLoNTkidUSYa26ARtvRL2YjB1GmFxJdDwCzdC81IRb5fmjhk3+LRVgTWJCARW5O+0o4i4I/W8wdAREUyENh/lY");
    regresar.append("sNfEDItrpMkz0IkEef7QAiHRKB4ghIxIorEhnUR+ARb+Fav0+nL3zMfgaMWFMlAsa7yLa+TJPPUP1iMVeLwpZEQCJWUmnIlkECJjIEStnMhhfiD2G3lfAZRGEeH82YW1vhwvrF4Omz5bc+IlALAYlYLSUlO09WszBhSYGE/i7/VS7+SvhU8iz5ZE2LDHXfnJAd94ulYKk4RGANAhKxNagrzUkEcgxA8FdzZw5WeyQiQsYahmYwSzVtsf8oB1tjpq0I5CQgEctJV3FnIZCqKdEfyIF31zcHi/RsYvWSIxbpp8PzQ2hzLQ+VimWWglakIhBAQPPEAiApSBkEEJBUH8TB+oEQsPt+ZHgNRgSuaXo8S5OijVhk8Ecqe/x4aOJEuMw+jtEn1ywRdfAKUwrPhesOw+xP1mX07dB3EaiBgDyxGkpJNp4icFzxntoX+8OWcqK/a0zALG4mIPsjFnM1K7YFzNI38bTfKbcpHxBS2qW4RGCMgDyxMUI6XgQBf8LxXE+EuJgjxcocNhIxNJMHj+zxo3NDz4kJ5w/1x0YGlmAv+80LxIt8zS0xsYaFJZ25bMNSUigRSEdAIpaOpWLKRIDK1R+AMTcZKmrEYUo8c84NSc9vQrzr+DUzpIl4NiJ2+By8yOPvIXGGhrm7eUfb9zfC2R7cEnq+wonAGgQm3chrGKo090fAxOvmn3RP+5U7rw/J1ZS3NmVrKqWZ03+D8xIeEv1uTDfggWGrfNcuX6Wf");
    regresar.append("noA8sfRMFeNMAiZevnARJc1rVLT037CmIU2BJXoNv9zYaAj4HmPjoZmw8bLII+fyJmbiwkuyOOmXS92cSFqwJQ22/B36C5vpBUsIqOVNWxGIJXDmcvOJPUnhRSAXAfp7bNCFpUGlTXMalal/nEq2NCHDizEx8O1nUIj9Htq2BZw8+oMuQkZSDsU/dIy0re/NwpH+Hbe5s6H223naisBSBCRiS5FWOoME2pU3gX3x8k9uC1kplawvYFT+fEyAyEuoEMCCRYft3KOYmkEXC4l2n5gx6VtemZWGtqUQkIiVUhI7toP+F3/pp5DKGo/B1jsswVtoCxgeIk2BvhjFCtnHGi40qVo/WagIprqU2mJaAudUeVM82yEgEdtOWVaZE1+MyIANKw/JjH/umhWsv/IHdviToNtCECNkIQxyh+nyymrLQ25Gin9dAhKxdfnvOnVfhADhL74bCsaPYw0hGxIwywNCwAhL+12jCLS95RrzYPy13RYBidi2yrOa3Pjig9FTBMwy245rqQo2RMDMxi0I2RbyYOWh7XYISMS2U5bV5KT9VD9HwCzT7QqWBXsZ0WjHU29jBMzS9m1cw2s0O+Zs/TwQz1IPDHNs1rnbJqDJztsu3+JyRyXoD+JIIWBkklFzD/+EO1mQtz3HLCWIKQLWtpGRh7zWBS8ypW2542pzJg+UqT4isBYBidha5HeaLqP4LOupBMziswr24B0cL9lkx1JtpwqYpY+N/mtdalx9pJ");
    regresar.append("0HRmBa/rQVgaUJqDlxaeI7Ts8XgBqboXz750469vvxaFosbdJ2yGXqz9eLGVUaErfCiEAoAXlioaQUbhYB+sGsiQ8BWHrO0yzjm5NTChi20F/ne2QsowWjuXYueT55oCxJk1VKarN/SVZKKx8BiVg+torZI/DO37pSQU9dQd6LLvgr/TV4DHP6nlILmBmPCOCR8ps+MoSspv4lmhX9svTL2PKorQjkJiARy01Y8TvfC6PZicpviQ+CQB8cazHS9zQlzVwCZrbgkfpCdljho6KBEpQlZUp+8LTljVnJarsUAYnYUqR3nI7/hG7vyMqNwwTspAnz5vjh9rkFzBj0CRl5QBTwIlOJQw5Pzy9Tv6wtf9qKQE4CehVLTrqKexUv7CoBa/pt/GavkGJZSsDMluM+wrN4jTQt+msuWpipg2HgwatcbHX91HPU8MboG+OBwbyxmNfPWP60FYEpBDQ6cQo1nRNMwF8Yl3lcuZsSuwTsvh856ncKNXppAfPt8kf8+fvte+yoSHj4S15ZPGwRM3+dR/9Y7Hc/nVgbY9NSeBHwCag50aeh78kJ2NP/En1hKQSMZruTJsjGu4gVwLkAfZFHZBjByB/f+Zinc/Rr/L8/L4848OZsRCEeH6I5Hst4CPPGCHnpiTRxjqeqECLgnJoTdRVkI+CPCLSKM1dividAGlPnLdEMZgMt1p4G4M8de/XN7px5VB990LnGzqCPCbLP4y7XvCLmF9wzHGPFjWab");
    regresar.append("ZHkuEy/EkYcBNSkGFZECzSQgEZsJUKf3E7AKlBA51zFMJWCWkzXFy2fmiwCeDp4UAnHp8YOnMyo8/mCQ9kMEv0nrEN+j6T0nBng0XuyojcZcWxGYSkAiNpWczhsl4DcljgaeGCC1gE00I9lpxy/APDTx0dRngopXi+DwaQvS0d6r//NSTttLWfgPEnhgdiw0Pgs/tMUbOwijmhSHMOlYQgISsYQwFdUVAks0JW5NwKCH0DQCc/C4GKnIdz4mYHxvmhZPxInffR/rp8Lj4u/mn3JP33GbO4uAWXxTRzz2pYn3xwLPxM9335vsO0f7RWAOAQ3smENP5wYRCK10gyI7DrRFASNrCM9Fb/Hig1dz7IFxnEWTCRP68acWEJcN4ed8mifN0wuNbyyc7/3Rd6ePCOQmIBHLTXin8ft9OzGVbgiuEgSMpj7f2wyxOzQM3hjTERiMQVMfYsN39sV6NrDnPL/J0EYpphpe35cv/xroC6P9IjCXgJoT5xLU+Z0EPtI0JXHArzw7A0bubAtY6uawEHMQLzwawjb2nE3tzRAv4uN7Ueyb+iGupaYKkBZljoDZaMWpdus8EQghIE8shJLCRBOwPhcqtVQf+lhsmDlxMn8qh4CM2UvzKN4MH8Qs1Vyroxjr/5/6waV+IspBTgISsZx0dxo33pJ9UlVoNmDA4sUD80fb2f4ltgfP5u3unISsm7Y9uNjgju5Q2isCaQhIxNJwVCwegY8dNyWy66UJPL");
    regresar.append("G2gBHvYYSdJ5bsW/IjIeunnaLM+2PXERE4TUAidpqHfiUm4I9WmxJ1W8AY4EA8POWv/dqSFEJGUySrZzD8nSWicg0WmcI+xTmPrPigkcJ+xVE+AYlY+WW0WwvbAnboA2uGn9uyUKUKmb9e4VDhIV70qR0GQTSizITkO95ffx/b3AeXIWY6JgJtAhKxNhH9nk3A7xObGlmXgNEHhvfDtmQhQ4zGhIzjNgTdhtAbK4RtKx5ZimvBuGgrAl0ENMS+i4r2JSNgnfwxEbYFjAm+/vwo4jwelXjq/Vv3MdgiQR9cjK0WlnRJ394DhpA1FfgzNrDleL1DC37wuvjBcRv+3rxc8uS1Ke1lok5O1BcREIFTBCRip3DoRwoC7Qo7Js4xAfPjKl3I8LTM2/Lt9r9bHx/7EEJ+HwSw4rUH13qQ8Lnq+34IqDlxP2VdfE5jBMwyg5CV1LTIyEybI2c2Dm3bzW02STwmjqH41z4254FmbduVfh0E5InVUU5VWemvxB5q+BQBs7i7PDL/XVwWLveWfi68KD+ddlMoxxAu6xNjqkCz6zDnjX4wEy/fQ/Pjq+378bVQm9mytyIC8sQqKqytmjpHwIxJ2yN7ayMoxGvHc299AbNJ0H1p0tz2ju+8MlWAgRysRMLWzmn6x6q9N33vUk2LVqLa5iJQ7Y2SC4jinU/Ar7j8Cq0r5hQCZvGuJWRtAeN1J2ZT35aBKtYM6odBAGNXqvfPn/N9rKzmxK1zRSAX");
    regresar.append("ATUn5iKreEcJpBQwS6zdtHiYbHuzHU2/Za6XDd5AgGjGPKTyofG0sLWZLnDukWYQB3ay0oU/CnM8hvkhEK67P3y6GZR8zBnpSX7MMq3eYSS0zUVAIpaL7I7j9SsuKrTGM7vqJY70/zCx1zB19R3Zsdgt4vCaW9xZ3meVc33FtoDZq00Q51Cb8VoPfDIK7ZAtNiXAD2OTyKf2K2qVDp+mvucmMNrskdsAxb8/AjkFzGji0Rx7ZbYr6bZPwJImkjkymkFtIAneF82bNqCE/e/8rXAx7jNVq3f0kdH+VATkiaUiqXhOCPgVF95QIygnnyUE7CSxTF98AfMnK2dKLlu0NpwfATMvsknsTNPEeGgipZmUNR1jDTBhjD1P4UVgCgGJ2BRqOmeQwKGJrKkYqcyoCO9qKkZOqF3A6D+yofHkp2YBw34Tm/ZAFLwx6+ezMISf8uFa0EcEchKQiOWku+O4L5xvnugfbeY9HXfyS8DKuxgQYcSKuWpN0+s5s9Cf62bNi3YsZGvnTzk3JH6FEQGfgETMp6HvyQhYBcmTvAQsGdZDRHiEMGU1DDgf3jQ9wePxy4hmw9c2/Yg0MZr3hQi9z0ZbRmShEbFDE6QmOkdAU9DJBCRik9HpxCECzehA546HmdsoRBuCvvQw8iE7Q46V1ITo98dhO14PXGkSjB3IwsjNxhM7eGMI1z2N52w8iHPKhGvE1eI4XAP2Q1sRyERAoxMzgd17tP7gDljUKmAMl7");
    regresar.append("f+ITwTW3F+jfJtC5jZcBCgpkkQDy3mQ38VnhajEikfPgdBbPLJQI8p/VnGirhqe1jBZn3qIyBPrL4yq8JiKkBrrsLgqXOOLLM84V9sJuVO8TgsjvYWgWJ5KvZ7o/NOBaMi5mWcCESsp3MqogQ/TCDgas18By7NclUIGR5jrMhSTuTL7xObY6qNeFR/2ByKOjeGgEQshpbCRhHwRSzqxI7ANJtRUR+vLxjddNaOEgH7tp8/EjCOIVJ9nkfOCdNtu/p++810rLtotiJAJ02C3koZffHk3A9DyogPZX/0Tf9FIC8BNSfm5bvr2P3Kn/licz54HtbkhZDd/aErfS+x8bYFDE/LRCE2rqXC+6ugtNO0EaAMzGgfW/I3r6Gx9BhsYt+1FYGcBCRiOenuPG6EwZ7Ij185MpkIcR3W8zvuu5kqZF0C5ovtZAMzn+j3Mfor9CPm5v2sPRqQ5l4wUOalPxRkLi5FvyABidiCsPeYlPWNUNEiIHMYzBWytoCxXmMNAgYz8u6zpCn07L9yp17fsuZoQJo7TUzNzjllrXNFIJSARCyUlMJNIuA3K9kgikkRHZ/UJWQMaBiLs0vAahs9x5D3rte3WN59D832LbU1L4wm31oeDJZio3TyEpCI5eW7+9gRHfqcAMGTuj9AYSqctpAx6GNIyLYgYLAi3wzkgCfeDoJmfxyH7xpC5nthFyt+mScM9amPgEYn1ldm1Vl8WFGieUKnkuWJvXlSP1niaGpm");
    regresar.append("TMjsVSIIWRPXMzb03OKlgrXJ1uxL+coXS2PpLZ5Oh7dzln5CE7I57wOLzY+8sFhiCp+SgDyxlDQVVycBBMee0Klk54ws9BMwIbNRiwiZv+r6FgXMz7//HQ/NmhphfBD3yMnPfnyh3/2BJVbGoecqnAikICARS0FRcYwSMG+MgIxUpIlv9KSAACZkNgqSChwh25OAGaalhYwyPJ63d1jpo8M7NNO0FYFsBCRi2dAqYp+AiQ37rMkrdpkkPz7/O3HTjOgL2ZpNiGu+2XgpIaPs/MnirMjil4m+i8BSBHThLUVa6RwGJviDPIYGY8TiagsZ59PMuHQfGN6JL6Cx+UgRfgkh88uOZszaRnqm4Kw4yiAgESujHHZjBU1ONo+ItQD9ynAuBFaMsPUFiWvueo2x9rRHQcaenzI8nNt9ZKni9xcixvtFNFPFrXhEIJaARCyWmMLPJsB8J38wRgohY4CBeUBreWB+89psSDMjwDNtC5k/6GVq9L6AwTl2weGp6eo8EegjIBHrI6P92QhY/5j1YbVHFcYmjAj6AwzW9sDMA4rNR+rwJmTm+dqgl6np+AJG2fWt/D81fp0nAlMISMSmUNM5swm0+7Csgo0d7IGAHc8RW+WdZe0mxEMfHC8ELeQDZzxfe2CAc6znS5ngxVlTLXHJAyukgGWGRhTpGliPgAmZ7ykwvyl0HlmRAlbg6u3G2YQM0Q8VMsri5p90TyN+fCRgRxz0vxwC8sTKKYtdWm");
    regresar.append("KegjXBUVnSNMiTP15OHxSatorzwBYQMLwi8s7iv/zBKUT0Y4XM0rFmWsqBMpIH1ndFav9aBM5cbj5rJa50RcAncKg4G0/Mnvo5hpf2/c2fP4Tb75thcMHSfTOdTYiegPnHUw7x9+P1uRmn9pJb7TD8bjNGmPzRhRy/u1kazB4QOAfGS/czkq4+IhBCQJ5YCCWFWYQA3gJr/plXRqJUpoz6o/mLSrw0AWPemy+wOUH5bwGgWc+aYY0Tq5SMpW+MESY+eFp4cogXjGk69AWMNHhIWCqPR1bpvwiEE5AnFs5KIRckQKVKpew3Z/nJl+CBIWBdSy35HpP1Q/m2T/nO25vNQ/XThRPCQ5wx/VWcZ4snd9nDgwR5Q/T0EYGSCUjESi4d2XZo/mqL2RoC5osFxeILSbuYfBFrH0vx++GfcOd8cTHvNJZLl5BJvFKUkOJYkoBexbIkbaUVTYDKmj6b5q3FZ20y8dKrpccIWDuDCMuF82lWtLAh7qxM0nA5WSXD9r/W65dr29H1G7Z33Na8k6xpUuR4yv67rvS0TwRyEJCI5aCqOJMTeGkiIYg1bI6AkVaqARG+18T7uxrhOnPhOneGNwJYnhgAY99Dt75HtxbjUFsVTgS6CEjEuqhonwg0BNoCZk1ta8BBbPBAWVqLvrF7Hj0M4jgZyEF/mAZfrFEySnNtAhqduHYJKP1oAohL7k9bwBil5w9Fz51+V/wMtKDJzx8sQnMl4qr5W13EtG8P");
    regresar.append("BOSJ7aGUlccoAl0CFjIHKyqRiYHxthrBOoONjzQjFud6X/4Dgd+0ONE8nSYCixOQiC2OXAlOIbBUBVuygPnc4NH8RfeB+XHouwhsgYCaE7dQijvJA01nfC49Pj6p9yhk3P+2gHE2gyfiYqkrdC6WdVGQtTUTkIjVXHo7s92GqvtNYKkQdAkYcduKFqnSKTUee0Ao1T7ZJQJ9BCRifWS0fzcE2gJ2WGqpmVBsALYsZPZAYA8IlmdtRaAWAhKxWkpKdp4QYAmmkx8zv3QJGIM46HM6rIzRWmNwZnLFnW4sl+pzLA6ADKqegESs+iLcTwZsaLmtITg3530CZvFSsbMgsTW1bdEjS8XSmGkrAksTkIgtTVzpTSbQLD118mF9wpMfE76MCZhFuWUh81e9twcEy7e2IlALAYlYLSUlO52/LNJHH5wOJFTALIUuIQt9M7LFUfr21ZHrLpaeH9m3HwISsf2UdfU5RUzMY7BFb6dkyl5dwrkM4giZyNwWMt65VbuQ+QzJnz4iUCMBiViNpbZjm03EbEBCLAq8MPuECpiFbwvZR2Y2aVq8a23NfjisZYPSFYG5BLRix1yCOn9RAod+sQ81E56bRXDpF4tddgkhYv3BRxox63qh5VhmTMjoT/L76MbOK/G4BnWUWCqyKZaARCyWmMKvSsDvF/vlpkkvVsQw/nDOzdOzgZDlWAwYUX7nb7nLNPPZiEje95UjLb8pdMorXKbT05kikJaAmhPT8l");
    regresar.append("RsmQkgINakSHOY3zyYOems0SNgvPTT+qnwkvjLNazfmhJhOeVBICsMRS4CEQQkYhGwFLQMAu/4zqM+HCr5u5sXRJZh1Twr7K3VxIKw+P1UvPgSkZuXwpWzaQq1pkQ/nSsh9E0E6iEgEaunrGTpMQE8B98bqx2MP1/rnu89ejcYIybpuyNvCM6cKQVtPubtsX9Kv2A7Pv0WgTUJSMTWpK+0JxMwD4IK3heByRGueGKfqCDW1jeWarV5ml+ZHkB2jeGKWVfSIjCbgERsNkJFsAYBJudaBX+x8iZF/3UvfrMh363ZLxVjDehIRVLxlEJAIlZKSciOKAIM8Lj4+ivNbX7lHBVRAYH9Jr23fsA9c/eH3GX++G7mpRhBiMdqXh9emAZ0GF1tayYgEau59HZuO96Y9Y3RRJa6WZGmN98zyoX7IMjfcUWQGZHIn3lhqQTnjvdfEcWQVUpy5VfxikBKAhKxlDQV16IEqPz9yphmxVRD7hFElqdi1OASQsZcMAZ1WBMpIPl+sRE3P49TAb/uF64ImA0YmZicUBcAACAASURBVBqXzhOBkghIxEoqDdkSTQAho/LnRDyX173HPR0dSesEBMz3WpZqdqNZ8ZeaUYlmDt9TTHRGhK0ZUfPCjK62WyFwcsNsJUPKx/4IUPnjsZBzhGxO/xh9USZgeEK1ey0ImD8HLYVXt78rTDkumYBErOTSkW3BBBAyv39sipBxDn1RJIqA4Qkt5YUFZzQiYFvA");
    regresar.append("EGQ8V31EYEsEJGJbKs0d58X6x6xPiYEeN/+Uezq0jwwBs/lTWxWwmgV5x5e2sj5CQCI2AkiH6yGAkN33dnfOJvFaH9mYkG1NwGgS9ZsQ8cAkYPVcx7I0joBELI6XQhdOACG7q5k/5veRMdiDir3L9C0JGGLNKERrEiW/ErCuUte+LRGQiG2pNJWXA4GDkDVD1n0ho2KnedGfS7YlAUOkmRJgoxBtUIo8MN0UWyeg94ltvYR3nD+GpzeCdvYwf6wZtUjzIiMPm4r+8CbjLfSBIV6+50VxI94phubv+NJR1isiIBGrqLBkajwBRi02K3ucwwOzyt7Ey2J7+MfdOfs+Z0tzHunw6hSLh6WjeLElb4FO5RVZOpYfS4vRmQyhxxPVRwT2QuDM5eazl8wqn/sm0Ff50/T22mYJK9YnnCo0XR5Rm3aIh+QPi/f7s8x24myLF/bnegN0Ow/6LQKlEZCIlVYisic7AQTh7maJqrZHRsImaHg1h5XyR7wa4mLgCE2V9jFR4fUpHLd+Ko7bsb7mPl/EWInEzvfjaKfTF5eF01YEtkxAIrbl0lXeBgkgEB87XpKpS9A4GdG5cN7Rt3Z44/JLmy3eGuf6TZQWtmuCNGEZROILEfGyCj9Cybl8xmwhjIkgzaRqNoSIPnsnIBHb+xWg/B8ImKAhZr7YhOIJaSqkyZH+Mt9rC4kf4aK5E+8Q8Qo5R2FEYC8EJGJ7KWnlM5gAgvbIE+7yI80WQeN3n7");
    regresar.append("DFDqYgrrYH5xtmnh/xphwM4qeh7yKwJQISsS2VpvKSlYB5a2z5zGnSa8clwTpiqv8iEEtAIhZLTOFFQAREQASKIaAVO4opChkiAiIgAiIQS0AiFktM4UVABERABIohIBErpihkiAiIgAiIQCwBiVgsMYUXAREQAREohoBErJiikCEiIAIiIAKxBCRiscQUXgREQAREoBgCErFiikKGiIAIiIAIxBKQiMUSU3gREAEREIFiCEjEiikKGSICIiACIhBLQCIWS0zhRUAEREAEiiEgESumKGSICIiACIhALAGJWCwxhRcBERABESiGgESsmKKQISIgAiIgArEEJGKxxBReBERABESgGAISsWKKQoaIgAiIgAjEEpCIxRJTeBEQAREQgWIISMSKKQoZIgIiIAIiEEtAIhZLTOFFQAREQASKISARK6YoZIgIiIAIiEAsAYlYLDGFFwEREAERKIaARKyYopAhIiACIiACsQQkYrHEFF4EREAERKAYAhKxYopChoiACIiACMQSkIjFElN4ERABERCBYghIxIopChkiAiIgAiIQS0AiFktM4UVABERABIohIBErpihkiAiIgAiIQCwBiVgsMYUXAREQAREohoBErJiikCEiIAIiIAKxBCRiscQUXgREQAREoBgC1xRjSQZD/ugJ93RItF933p0LCZcqTKhdlt7S9lm6S2/FZWni9aS312tjr/mOuTI3J2IU+nvvc1/4j59wT4aCeMn1RyL2");
    regresar.append("vre5G3IKxi9+xH3hkw+5p/7bp9xTobYRDvve8kp37Q+91r0w5rxawsLlZ37TfTHW3i1y4fr99d93X/Kvkyn59Jly/ktucOfe/M3ueTdd58590wX37FjWa4Q3FlOuDeydwm2NfLbTnFKHWRzkme9bri8sr7Y9c7n52I8tbN/40+7zn3kszANr55cL4N/8E3c+x03uVyrtdEN/Y99v/Jh7cWj4GsK9/d+7R2NFvZ2vmrlYRU2exirr724eZO56izvfzn/79xhTeH3rLe7Zt73MPecffpO7tn1+Cb/h8o7/5J6Ye22Ql3/xXe4FtTwA/t4l99T3vds9mqIMasr3nPxuSsTu/qB7IsYD6wKXo0JMeWGGVmRdeStt31hlG2NvTVxMuHxvKzSvv/LD7oahh6z/8nvuyTs/4J4IjY9wXPM8uf+jv+Oel7MlIsamlNcG6dZSoc95CO/iO3a9dJ1T275NDez4+INxzXRLFdYDl9xXUqWFSOPVpYpvrXgQ9hRP2WZ/qWVv9rFFvCi717/LfR6va0r+x66lBz4df63RcoE9P/Be92gp19YUNj7r9vcPNvcN11x7f0m/Ee6prUh9+Ri7XvrOq2n/pkQsxQVAHFQ2KQvxsxObN/tsqOGG7LOd/fBN1WQylE4px9riNceusWvpDx+ffu2amOENrClmOcSGvP3r/xDnoc4pp9hz4Z1auLFh7HqJtbPE8JsSsRIBYxN9DyltsxsytdimtHEoLvo6ho");
    regresar.append("5POQaTKeflPofKyTyv3Gmlin9tMbuxGS38925NP/iEfNHlkIpTqngQ7bH+0Klppa57ptqR8zyJWE66x3E3F1Ly0WDckDnEIDeOXE+cue2OjZ98fuOd7nO5KqdYe6aEX0vM6JdjJOUUm8fOKa05PnerRKkDd8bKKea4RCyG1sSw3JTv+p7xUWWx0dP8QGUZe95a4XM+ca6Vp6506duoWbzaeTIxW9KL4cEvhzdG3kpqjs/5IMqgjnZZbvG3RGyhUuWJiBFSqZMr6YYcylvuJ86htJc6Rh5Tj6pbyvaQdPBi6C9bohmbB793/mN3npGTIbbFhEGU6R9bIh9DduVslaCuGRrFOmRXbcckYguWGEOYUz9dlnJDjmHM+cQ5lvYSx/Ey6fvK0Tm/hP2haXC9MYpxCQFAyJi3GWpbTDjyseY1mbNVgjqmlnlxMWXWF1Yi1kcmw357ukwdtVUsqeNNFV/OJ85UNs6Jh7lZexptadfbEk3ZeBM5WjAo77Wa43kAyHW94Lm+5wf30Yxo96xEzEgstM3VP0bFskSlEosp5xNnrC05wsM8dnJxDjuWjpPrjX6/Ja65HC0YxmuN5vicHmAuz9V4lbiViK1QKrn6x6hUEI0VstSZZM4nzs4EF965tQEcU/AtIWTWgrGF/rGcfaZ76gfzr1WJmE9jwe+5ni5L6LA2jDmfOC2NtbY5K6O18jQ1XYQs98hFhIwFuqfaOHQeXuUS1yoPmLn6TPfWD+aX");
    regresar.append("p0TMp7Hgd3u6TJ2k9Vekjjc2PpqZct2wsbakDr9m3t40Mn/qh799nTcdLDH/KldTPNdH7v4xBEz9YKnvxKP4JGJ5uAbFyk2ZYy4HQpb7yXgog1vuB1s7b2PDplntYqhsch5bon+JpngWe86Rj5z2v/t38s3n3GM/mF/+EjGfxgrfc42+4smYUXMrZMmVvEbdHB5r9vHRHxQyYZ4Ho1yj+cbY8fC0RHP2217nXph6qgp5y2V/zqZnHoLHHmzGyq324xKxAkowV//Yv/uw+8IS83l8hNywVAb+vq18X6LfpIsVngfvkQtdQog5Qh++0714DTGj7HNzytUUD/vU9udsekbI9y5glJlEDAorf+ymTD36ihuSialLZS/nDbtUHvrSyfk03ZUm1wIi9D/e5W4MeRFmOw6uKV/Mcngu7TTtd+7+JdIhfyGeqdkUs01lf86mZ8pzb/PB+spQItZHZuH93JQ52rYRsiX6x3LesAsXxVXJLSXOJlx4UXheKVZdMDGjwlvSO2PEYu7m7FxTVbgAUvSP5WxWX2sAz1U3RwE7JGIFFIKZUHP/WM6Oa+OzxpbmWCrknGnzVI1XYcKF8ORIzwQNMSO91J5/22aas9v7Uv/O1RTPw9+c/r2czerqBzt9FUnETvNY/VeumzJn/9jSTW1LFtJ778tbEdNkiJcU2t+VIu+IGekx7ypnv9kSrQDkJedCwVOa43N67pSX+sFO3wUSsdM8Vv+V66bM1T9Gk9");
    regresar.append("FW54PhhTHKM9dFwRN1iibDqfaZZ5ZTyD7+YP4VZMhHjqZ4uHLfIEqhjHM2q+Oxr3m9hDJYOpxEbGniAenluilTPxlTyW953cBcXhjNeCU1CdkAkBzNi6mvub7bJ1dTPOnRnIw49aVt+7kfNKHZaCy3lYgtxzoqpVw3ZcqVFXIPpY4CljhwLi+Mp2n6vkprEuLBiebFHBOJ8cbgmbiIrooOMYbvVQcS7AjpH8t5P+TyNBOgWT0KidjqRdBvQK6bMsXIqy33g1EiObwwBKLkYdEIGROJUzcv4o3l4Nl15+TsHxvKAyOAczWrqx+sq6Sv7JOIXWFR5LccNyWVypzhvzSt5LphSyiEHF4YHsKU+V5L80DIGFyUumlxKW/MPMoc3Pr697gfcvWdqh9svCQlYuOMVg2R66ac2leRs91/VdBe4jmahXgY8ZIo+muOa25Jbwz7c0yEJg/tZtGc9wMPEiV77qVcxJsSsVTt4dwEpRQQdmBP6iYe4p3SP5ajgseWkj6pvUwGcZR2TY3xziEEfZ7MmC1TjueaCN0ux5z3g/rBwkp+UyKWYhZ7jie4sKIYDkX/WI5O95j+sZzzX4Zzv9zR1KtM8GBV2iCOUJoIQaoHQ9LEkwkZ5Rdq31i41HMu2/dfzn7hkkavjnFe+/imRGzuiD4u0iUnncYWPp3uqfsqqFhC+seofHKvXBHLI0f4Bz7tvpIq3i00B6Xuk33gUjq+Y+WE15TKfsrS79PMOT+y");
    regresar.append("5gefsTLJcfzM5eaTI+I146SduqmMngqtkG5qLlCe2tpNBWvmoS9t8vb6d7nP9x2fuh8B929SP55caZIGN2yq5jsWy/XtnvL9jT/tPo+wTzm3fc5WnqapsFPNB6S8l+7n4fr99d93X/rkQ+6pzzwaV7YvucGde8XLT08y5oEu13ywNfi0r9vafl9Tm8Eh9iJGeFQle1Uh+egKQ95o8kxVqVga9I8h5jRb2j7b5mr354blSTmHKJvtsdtUAka6tTYjtpnd9rJ0c69SPbC0bRz6zT3Ddd38JfmEtFxMTShFl8jUtGs9b1PNibUWQqzdiHO7fT42jq7wXf1jOfvBSrthU/aH5SifrjJbYh8iwANHqrSW7BdLZbPFo4V9jUQ5W4lYOWURZUnO/jGaXzAmZz9YiU1toc3PIQVF+YSEqyVMygeOWt94kPOBThOap98JErHp7FY9k6djlglKbQTNaTQfImS52v1LvWFTTVjFa6F8UpfNmvHdmNAbi+2XWjPflnbOBzqul65mfEtb22ECm+wTI8tUwp879iiGERwdrbH/gooyR/8Y/RZTXkERwrnUGzZlU+Kbv9k9L4RFTWG41hjgkKJPiwclRKGWey7nA90WRrCufR1vTsS44PAkYm82LqZ/9nr3wtoGg2Dv5/7M/VXq4e8pBzjYRb6XGzblQAhjV8KWEbypr7MS8jVmQ66BTaSrCc1j9MePb6o5EQFjpFusgIGJSpsXR9bY6Zx6Uuf4ZT");
    regresar.append("MtRMk3LA8C03J1+qwtNiVaDvHGeBCx33O2n308bqj7nLTmnKt+sDn0ljl3UyI2tMp0CE6ELOfw2RAbpoShcil9bb4SB3L4rD/blL3/e+r3r78uTSU/Nf3c5zFvKkUaqR4aUtjSF4f6wfrIlLV/UyKWYm22HM1oSxS59Y8tkVZsGngntfR/xOZN4bdJQP1g9ZTrpkQslQBxAddThFcspX8sx0LBV1KI/4aALb1CQ7yVzv1houYtJoxPSb+WcxjckcLWVJ5vClu64lA/WBeVMvdtSsTKRLysVaX1j6WcX5STZKph3ze+yG1usFQO7qkeGnLYpoV9c1DNF6dELB/bVWIuqX+s9H6wVQqo8kRTiXSqh4bUOOkHmzIwLMQONauHUIoPIxGLZ1b8GSX0j5U6obmv8FI1RW91eL1xu2nDA1dy9oPV0qxu5VzTViJWU2lF2Lpm/xg3rFYgiCisioKyckcKc1M9NKSwxeLI2Q+G51lrX7vxKXUrESu1ZBLYtUb/WI0TmlNWLnjBCYpOUSxMIGc/GFlBtHOtgrMwquKSk4gVVyTpDFqjf6zkCc19ZFMKT42T5fu4dO2PWcqt6/wS9+Wc0OznFyG7+4PuCX+fvs8nIBGbz7DoGJbsH9NAjqIvhSTGpVppI9XKH3MzlXNCc5dtLDKdcp3OrjT2tk8itoMSX6J/rPaRV6kq1VSV/NYvy1Qrf8zltMYKPbUubzeXda7zJWK5yBYWb87+sS2MvEpV");
    regresar.append("qdawnNKcSzPVO9dKWJ4r5wsuhxjTrLiGeA7ZVPMxiVjNpRdhu/WPpfI4/KRLX7fRt1XfRQACS/WD9dFW/1gfmfj9ErF4ZtWegZClHnhBP1jKgRFrwU3lGXzyIffUWnlYIt1UK22suTzX0v1gfeVC/xhi2ndc+8MISMTCOG0mFAvxplpfsbYJzUOFuGalOmRXacdSrbSRauWPWD45JzTH2kL4DzZCtvURrVO4xJwjEYuhtZGwKfrHNKG5+2JIVcl3x77+XprB1rdiugU5JzRPscr6x1LOVZxiR83nSMRqLr2Jts/tH6txQvMYqtsuuOeMhQk5TqW01SHUKfO1xvJVa/eD9V0/XDOaCN1HZ3y/RGyc0SZDzOkfS92vVgLgVMspkZdf++/uSyXkKbUNDA1PFefS75crpR+sjx9Cpv6xPjrD+yViw3w2fXRK/9hWJzQj6jSRpihwVkHfWj8H+aGiTcHnu1/prk0RT2gcpfWD9dn9M7/pvpjS2+1LZ2v7JWJbK9HI/MT0j9U+oXkMTaoXPpLOA5fcV8bSq+n4f07oXTYr/Sdpug3lV1o/2JDdeLvqHxsidPUxidjVTHa1J7R/bAsTmscKNlW/GOkw6mwsvZqOf/zBdFMHlnxdTc6FfXPMuVT/WPxdobfQxjObfAZNMu/+HfeF0JfucZN86y3u2W97nXshYjM54ZETrX/s+97tHu0Kih17mNCcsl+MyojyXrrvp6v85u6jiStVUyK25L");
    regresar.append("yW/bzCP/Re888L+c70EloxGJCRkg1pEx8LBd/1Fnc+xJa9h5EnttAVwEWJSMTcVFzMTIjkRuGGzGkqle2H73Qvbj9d8vt9b9vGhOYxflSuqfrFSIsHlrE0aziecqDKUv1h3C99D2VzmXON8L48e/ibG1/X+ZoI3UWle59ErJtL0r3cUFyUUyNFzJZYa42bEsFCzN71Pe48gzh+48fci5d6cp7KJ+V5b/5m97xU8fHAUvuIs9TD0pfqD8v1AMFD3Xt+0N1g18iUwVF27thWE6HHCB0dl4iFcZoVKkWnOEKW2xsjkwgWf6x8v4WmsNiCS91fU3NFxPXGiLlYhkPhU/PtSitnP1jX9JKYwVFd9vbts4dXDfToI3S0XyI2zCfJ0VTrzek1H0mKYzASBDxlk6JVRIOJFnowdXMcTYm5vfrUnqNfNH3LrJEn+ozbTfH+uVO/c/3UNLpyaj7nnCcRm0NP526SwA9/u3thyoxREdX2Rl+8mZQMiOtNCZtqu2zL4TlaOtYPZr/bW4Ssy0trh5vyewvN0lPyHXqORCyUlMLthgDNqCm9McDRJ1rLRFbsjBmAFHJhwDNn83TOCc3tfrC+/ObsH9NE6D7qzknE+tnoyI4JpPbGQMlE1tIHemDfnR9wT6Qu+hw8fRtzNrnFeFi5+sfs+lH/mF/qR98lYlcz0R4RcDm8MZoVeaIuUcioHGlCTD2Qg0uJvrCcXtga/WB9t0ju/jEtFHw1eYnY1Uy0");
    regresar.append("RwQOBHJ5DwjFG3/afb6Up2rsoHJM3YRol1HOvrA1+8Esf+0tQsZUlfb+FL9r7F9Nke+hOCRiQ3R0bNcEcnhjBpTKCOFY2yuj/+v173Kfxx6zLeU2pxeG+KYeQWl5D+0Hs/DtLUKW6uWz7bg1Efo0EYnYaR76JQKnCOTyxkjEb15c2isjPUZM5uj/8gGyZJr/O+X3UvrB+vLEqh6pBwhZWjXPP7Q8pNpKxFKRVDybJIA3luuJ2oDRvIg3RJ9UzhGMCBeeH+mQ3pxVZMz2oS3c8EiGwkw9Rh5yNX/2zQebYmvO+WOs4rP0w88UBrnP0QLAuQkr/uoJMOLskw/lW0zWAFEp88coRhZ+pi8pxYAI+o1YNSa3aFk+2I7Nq/LDxn4nP7kELLXd1j/GQ0NsPsfC48njjfrLYI2ds8Xj8sS2WKrKU1ICVES5nqi7DKVyQnDo72EAyNR+M877xjvd54hnSQGb25/UxcT24XmU2g9mNra3XD+sRdren+I3Yj71+kiRfglxSMRKKAXZUDwBe6Je2lDrN0PM8EBC0icc4XMMlw9JP2ZeVUh8fpjS+8F8W/3vrEWaawV/yjn02vBt2sp3idhWSlL5yE4g5xP1mPGIWeibDPBUCD8WZ47jv9K8+SBFE2iXbTn7wXLabXlhkEuO9RWJf8/9YxIxu8K0FYEAAjxR5x7o0WcGwjT2xJ1zYEifXbY/5YAIi9O2OZbCsrhzL4ll6eT05rk29joRWiJmV5");
    regresar.append("i2IhBIIOfSQmMmPHDJfWUozAOfHj4+dO6cY6kHRPi2INy5pgLk7L/z82Dfc3rzCFltC00blzlbidgcejp3lwSoiJYc6OFD/uxIM2Gq1/74aY59R8ByjpALbUYds7PreM7+u6702Jezf4wBPHsb6CER67vStF8EBghY09BaTYsDpi16iMEKOQWMfrBc/XtL9IP1FUbO/rG9TYSWiPVdZdovAiMEEDKaFvcqZOT7rrfkGToOejyKnPPBcg1AGblsDoftISgkbGwYRD+n9xprT+7wErHchBX/pglQGbG80N6EDC+GfOcqXPrBck0RWLofrI8R106u+WN76h+TiPVdYdovAhEEqNA/fKd7ccQpVQZFAHI3w+Wc0Az0NfrB+go752jXvfSPScT6ri7tF4FIAjxZI2S55gJFmpM8OPn6jR9zL87dDJdzQnNuAZ4CPedo1z30j0nEplx1OkcEeghYX8fWmhfJDwLWk+1ku7fcD9YHiWuG0a59x+fst/6xLS8ULBGbc4XoXBHoIEClZM2LuZYa6kg2yy6Gz+Nd5uz/8g3fej+Yn1f/O9dMzv6xX/999yU/vS19l4htqTSVl6IIUDExeg8RQAyKMm7EGBMvhs+Tj5HgSQ7nXG2kpH6wPlg5+8doVuxLt/b9mxKxVBVF6pv2FS9PU4Hd9rI08ZR00aZiXbLHQx4RA560516jNzX9UkPlN/das4EbS4qX5eem64bzZuFityX2g/XlIWf/WF+a");
    regresar.append("te/flIi9uXn/0twCyVEZcmHOtYvKL1WFP9eW1Oen6D/i3Vup7UodH0/ac8Xsxhe5wXcA3nbBPWeK3YgXIrvEwI0++27M4PFxP+ceiNKXnyn7ucdz9I/xfrop9tRwzrmLzacGQ0Ns/Kpr3dkvP+We+d9/6P4qJHw7DDfye3/I3dDeP/f3C57rzv7dW9xzfvdh99SfP+kux8aHXe//Ufc1sefVEv6m8+6a61/gztz/YNirRtr5ovJ99Te457b3l/r7r9/onoXovuVb3PP+5te7Z133fHcm5JrlQeZfvtF91VC+zjQHH/pj95djq1xwTb31Ne75VG6/2Fzzt/9993zsGoo79zHukz94xD01ZnuoHfD6t7e760PDlxIODi+9wV3z2//LfTmVTXe+yb0ox0NCKvvmxHPmcvOZE0GJ5zLCiTfxfubR8NdRvKV5Ysvdec0IoWaB1qdCF2m1pqPcdpVShvSJhLLBZvjg5W7FQ7Xr49eatzD7K1UgODHXJ/HQkU8/iC8IVOo0N5bMDNtZjd23e8r1SV7xeqecW8o51GMpBrrwkEcrQCn5Sm3HJkUsNSTFJwJLE6Ay/1zzx9PzHJG2eGpqUsPmmIc9v2x4sKHJdSuV9lwWe3gAloj5d4C+i4AIiIAIVEVgUwM7qiIvY0VABERABGYTkIjNRqgIREAEREAE1iIgEVuLvNIVAREQARGYTUAiNhuhIhABERABEViLgERsLfJKVwREQAREYDYBid");
    regresar.append("hshIpABERABERgLQISsbXIK10REAEREIHZBCRisxEqAhEQAREQgbUISMTWIq90RUAEREAEZhOQiM1GqAhEQAREQATWIiARW4u80hUBERABEZhNQCI2G6EiEAEREAERWIuARGwt8kpXBERABERgNgGJ2GyEikAEREAERGAtAhKxtcgrXREQAREQgdkErpkdgyLoJfBXz7jHOPgXT7lPPfucu/5Z59wN15yd/7p04rU4r32Wu7XXgIEDZhtBpthk54eeGxu+y/Sp+V4zbT8f2BHKy84Ltd3C2Xl929D0ie8vn3aPPvW0e4xrd+p11mcH+83mEJsIOxZuLD47PmSTHRtKayieofMsbm3TEpCIpeV5Etsf/YW795Ofdfee7Dj+8jXXuusvXOde9XXPd29oHxv6TXx/+iX34KefcJ/ywxEfv2PjRAQ/9hn3q5z/rX/NXfTjDPn+mT939/7PP3H3/62vca962Xl3+9g5//dR96vYHhre4uvLN8dDWf7uZ93P/smT7rFXv8Td/qLnuldZ3GPbvrTnMH/sy+7BEF7YRmV570NHZfOGl7uLfRXkk3/pPvXbl9zPjeWH4//ggvvRIUEirj/4Y/er8PLjI89f+wJ3a3PdvnLofP+cse+h18SffdndP8aNMFzPpNnHyq7ZMbuG7gm/TPriCb0u+87X/jgCErE4XkGhm8r953yx4aK2SoFt83dvc6O9qq9S8hPhprGb3d9vcXrx3tvsu/9bbnL/");
    regresar.append("PCReP64Sv3flmzxjq5fnA0vy/be/1t2eqnLtSpt0UzBH+IkrVMgIu9SnSwwbO2/lWj6+bu9vGCfzyr76ee4W4v7jL7pPNen0fj7zBfeJL37FPToUBpEjAuxd6vq369E3/JjT4bpsHpquj3lo8uPR93ACErFwVkEheSI0AcPreMlXuTfYTUXl+KdPuvtf9Gx3i+0birRdqbziJvcGzrXKmvg4nzjx+riB8DpSVuhD9uU6NpZv0jWWlm9jMtemsbRTMEfIEINYbzwkb2+61f1sSLiuMHhgtt/3e83c8gAACvBJREFU2P7GDUfN1whFSpu/+tqDV3y4buHeV4Z2P3Fv9YkCQojtL3mhe6XloW+L0DX35o/2HQ/d33WfcX2Yx/d//tTd27RyBHv+oekq3GkCGthxmsfsX/ZESETNzXK7L1Z8pxLou1nbiT/0hPtN9vHER6XSPpf4LE6aUMxT8Cujdpw1/B7LN3nw803eU+VrLO1UzBFfmitT2Z0iHvNwaXb1r1HyjHhwPadIx+IgXgSF3w2LT9h+f4tw2W88Mvvubwljtj//2dP6iP345nw/vi4PQopN9tAzJ06dO0xAIjbMZ/LRrqaGmMio4OwJtOuJrx0XNw9Niezn5imtgmzb2/d7Sr7Je198MfunpD2HeUlC5le2DOSI4TYnLE2KnG+eVDsuvBnbx/2Ax2a/bWsPjghiqmvB4p6yZTCMnVeCPWbLVrcSscQle/1zj27KuUJCBY");
    regresar.append("dpNCH6T8VD5nLDEJ4wlx6/8gQ7dE5px6bkO1UepqQ9lbk95JQkZGYTnrwvaqn4dsVz3KR4ePBqCxS/zcMy25oHjau8MRPAkKbELhum7kOs4OT/4RWa8Nq9ODV+nRdGQH1iYZyCQ9GcwRMhT41UUM3NFz3owL+Z7SYPNSC0nyE0viXD+U1Hsfmea+ectGOZUyHjwdmoSXvgSNHf1Fx3J/1axoQHq76+JAuDGH/DV7s3/Ekzwg/hwLbYEa8WV8yWdO1++bOn3IP+A5sJFn3LDZtXMgKzPQjEF7rQpkQGiXRxIg0//bF82GjIvnBLX8N9dmx9v0QscQlzUzYd4bc3zSQngy24+XgqC62k5jRHkD6VJBVRu1JInNWs0ZGPrAkMRB6bth8+lDnn+ELWmHPo+wm9RvrMt9GP/vFGBNyYiBGeME1/mKNy5vpp/iY9hPlph3y3UYqIuZ9/ywvi8qxmjiVxYRfCZWJjQhfTlHict5O+NrMRsW/iDf6Yd+ifQNz2e6kHAUtvr1uJWIaSp4LiZmSILU0LXNjmlYU83fpt/FPMe8Fz3A3HN/vJDTUlnqXPsXzz5F1b2uZNNBVsMPMuIfNHn05h0MXOmrhD4kPImoEyt/ojXnkII15/pG1IXKFhujxZ3zM2wcIGhI3BN833w4AQa0q0vrXQNLs4xfYF9vVV07zoTYs5jEa2PITap3DhBCRi4ayiQ1IhNE/bpyqERlzubbwy5z9xtiNuLviDF0Kz");
    regresar.append("R/tYyO+p54XEnTOM5fsvIoQglT1z057K3ISMSc08eNAf1Vc5huS1EdPbQ8INhbGHMOYyWpMn4oFg4D1yfOj82GPEZw8B5snaSES/XwkRxQ6fNcxIL6bpjrRScOrLJ/mhNaY5fpjg74tu3znaP52ABnZMZxd0plchHIbAcxJeWVebvEXI07h9n7K1GzvmCXxKOpyTUnAs334lNdWu2PPmpj2HOdeITRMwIVtqYMUQJ+xCtExIsA1RGzpn6jHzpKx/kD5l4vLFCXsQIOxgJKnvrXFsato5zsOel59330Xclpcc6ShO5yRiC10FViFwE5IkT7X+AA7fjHb7v39s7Lt/Y4d2dI/F2XXcPJdQwbFwQ8I6J99dNsbsm5N2CuZcH76QIRasXRiThxxhseu4afzg4SEgQw9gU20wsfLjp8mP9P04bQQiYmejALuaBv1zSvhewkNJCRxy2CARy0G1J05uyONmhkMHdU+ww0ReE7vYicvWDEOnc7sC6Etvyn7zXELPpXIaC4u91lluk47Hzkl13E97LebYwERj8gSv3w5cDzEVg6F4aBq3azKl921pkneL3wZ0dD3w2IMZfOyaopnR4ilp6w/QKsmurdkiEVu4RLlZLcmhi9wXO5pO7JyhLR6BNV3QrzIUNtUxKpIx+/zjVgn1pW92kw//vL7wKfdb2iF5snSxMSXz4xGCi5Sd5SF0+/yZfbVj6ViTooWDhX23rS92/j77XtL2SU16XqQ4JG");
    regresar.append("KJMdNE+PH/5y72NR/4FfNQhc7Nan0RIRNiifdjx6t407zSNPcdmi0TZ+8kOuK3J2eadvx8nQRqvsAD+9nX1Tzkh+U78cbmO1XzVkzalC95trxhcyrmVN7GoM0n52849pUj+TUPidXsc9hhTYrEPdREaH1NY+Fy2BgaZ/u6Dz1P4eIJaHRiPLPBM2iK4kmePg2GuvP0SrMIT2XNhX1SESAAvlfWFSk3dVOZHQaCUFk2TW33U4EQH8OBGcnFeYiINa0Qb/MX9STfV3ERN82GfZUz3mJT8R1G1TXp39tUPI/5tvl2EVdos89YvvFg4enHT7pdT+6kG/MZS9uYm3gRN8yHRpvGpG9hic/K3vaFbIfKkvP7ypPzTKT864xzYO3nt7FtdJFdzov9mJeFZzt0rXA9wnwsXF/69M+OceI6GLs/mzg+0dhyuAf9tPzrkv1DefHP0/dpBCRi07j1nkWTlAmZCUsT+NTESvp9rLmwN6LmADfRcaf6yXyzJk7iOhUfcRAnafcJDmG6PtiIAHUdY9/xYrCdh7GPwQgINvEcV4K9to1VCpZIbL6Ze5dCwEg/Nu0pzC2fY9spQuaLTVf8fR6jL97H10TndXZ8PWTxxLDXmhTHrhXCIWJj4boYjF3znPMPLoy/acJEvysN9nFP5piS0JfeXvdLxBKXPCLChctLJ5m8Syc4NxsXNJ4ZNx+VU0yyVNBNnLd2xUkFTlyxceLJ8TQ7ZsfYBFAqkb784jU2");
    regresar.append("PA5CPJZO1/GhfMfETdiG/eENxV3pdO0bSnsuc+tb6kq3vc+ErL2//TukLNvn+L9NvJkbZteZTSQmHHkO8U78OKd8J41rAxYgJhyef0waXIuhnGy0alf8Q3FQtqTT5/F2xad98wicudx85kWhs0VABERABERgHQIa2LEOd6UqAiIgAiKQgIBELAFERSECIiACIrAOAYnYOtyVqgiIgAiIQAICErEEEBWFCIiACIjAOgQkYutwV6oiIAIiIAIJCEjEEkBUFCIgAiIgAusQkIitw12pioAIiIAIJCAgEUsAUVGIgAiIgAisQ0Aitg53pSoCIiACIpCAgEQsAURFIQIiIAIisA4Bidg63JWqCIiACIhAAgISsQQQFYUIiIAIiMA6BCRi63BXqiIgAiIgAgkISMQSQFQUIiACIiAC6xCQiK3DXamKgAiIgAgkICARSwBRUYiACIiACKxDQCK2DnelKgIiIAIikICARCwBREUhAiIgAiKwDgGJ2DrclaoIiIAIiEACAhKxBBAVhQiIgAiIwDoEJGLrcFeqIiACIiACCQhIxBJAVBQiIAIiIALrEJCIrcNdqYqACIiACCQgIBFLAFFRiIAIiIAIrENAIrYOd6UqAiIgAiKQgIBELAFERSECIiACIrAOAYnYOtyVqgiIgAiIQAICErEEEBWFCIiACIjAOgQkYutwV6oiIAIiIAIJCEjEEkBUFCIgAiIgAusQkIitw12pioAIiIAIJCAgEU");
    regresar.append("sAUVGIgAiIgAisQ0Aitg53pSoCIiACIpCAgEQsAURFIQIiIAIisA4Bidg63JWqCIiACIhAAgISsQQQFYUIiIAIiMA6BCRi63BXqiIgAiIgAgkISMQSQFQUIiACIiAC6xCQiK3DXamKgAiIgAgkICARSwBRUYiACIiACKxDQCK2DnelKgIiIAIikICARCwBREUhAiIgAiKwDgGJ2DrclaoIiIAIiEACAhKxBBAVhQiIgAiIwDoEJGLrcFeqIiACIiACCQhIxBJAVBQiIAIiIALrEJCIrcNdqYqACIiACCQg8P8BY4tQj+xdiwcAAAAASUVORK5CYII=");
		return regresar.toString();
	} 
  
	private String toImageTsaak() {
		StringBuilder regresar= new StringBuilder("data:image/png;base64,");
		return regresar.toString();
	} 
  
}