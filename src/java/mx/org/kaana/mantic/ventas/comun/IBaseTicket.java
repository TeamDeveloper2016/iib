package mx.org.kaana.mantic.ventas.comun;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Encriptar;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EEstatusVentas;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETipoMediosPago;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.mantic.ventas.beans.TicketVenta;
import mx.org.kaana.mantic.ventas.caja.beans.Pago;
import mx.org.kaana.mantic.ventas.caja.reglas.CreateTicket;
import mx.org.kaana.mantic.ventas.reglas.AdminTickets;
import org.primefaces.context.RequestContext;

public abstract class IBaseTicket extends IBaseFilter implements Serializable {
	
	private static final long serialVersionUID = -2088985265691847994L;
	
  protected Reporte reporte;

	public Reporte getReporte() {
		return reporte;
	}
  
	public void setReporte(Reporte reporte) {
		this.reporte= reporte;
	}
  
  protected void toPrintTicket(Long idVenta, Timestamp registro) {
		Map<String, Object>params    = new HashMap<>();
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = EReportes.TICKET_VENTA_CREDITO;
    Encriptar encriptado         = new Encriptar();
		try{				
			this.reporte  = JsfBase.toReporte();
			params.put("idVenta", idVenta);
      Entity empresa= (Entity)DaoFactory.getInstance().toEntity("VistaVentasDto", "impresion", params);
      Parametros comunes= new Parametros(empresa.toLong("idEmpresa"));
			parametros= comunes.getComunes();
			parametros.put("REPORTE_EMPRESA", empresa.toString("nombreCorto"));
		  parametros.put("ENCUESTA", empresa.toString("nombreCorto"));
			parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
			parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));		
			parametros.put("REPORTE_DNS", Configuracion.getInstance().getPropiedadServidor("sistema.dns"));		
			parametros.put("REPORTE_PORTAL", Configuracion.getInstance().getEmpresa("portal"));		
			parametros.put("REPORTE_ECOMPRAS", Configuracion.getInstance().getEmpresa("compras"));		
      switch(Configuracion.getInstance().getEmpresa()) {
        case "iib":
        case "kalan":
        case "tsaak":
   			  parametros.put("REPORTE_SUB_TITULO", Configuracion.getInstance().getEmpresa("slogan"));		
          break;
        default:
   			  parametros.put("REPORTE_SUB_TITULO", "LA CALIDAD Y EL SERVICIO NOS DISTINGUE");		
          break;
      } // swtich
			parametros.put("REPORTE_NOTIFICA", Configuracion.getInstance().getEmpresa("celular"));		
      String codigo= encriptado.encriptar(Fecha.formatear(Fecha.CODIGO_SEGURIDAD, registro));
			parametros.put("REPORTE_CODIGO_SEGURIDAD", codigo);			
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));		
      this.reporte.setPrevisualizar(Boolean.TRUE);
      if(this.doVerificarReporte())
        this.reporte.doAceptar();			
		} // try
		catch(Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
    } // catch	
    finally {
      this.reporte= null;
    } // finally
  }
  
	public boolean doVerificarReporte() {
    boolean regresar = this.reporte.getTotal()> 0L;
		RequestContext rc= UIBackingUtilities.getCurrentInstance();
		if(regresar) 
			rc.execute("start("+ this.reporte.getTotal()+ ")");	
    else {
			rc.execute("generalHide();");		
			JsfBase.addMessage("Reporte", "No se encontraron registros para el reporte", ETipoMensaje.ERROR);
		} // else
    return regresar;
	} 
  
	public void doTicket() {
		Entity seleccionado      = (Entity) this.attrs.get("seleccionado");
		Map<String, Object>params= new HashMap<>();
		CreateTicket ticket      = null;
		AdminTickets adminTicket = null;
    String       cliente     = "";
		try {			
			params.put("idVenta", seleccionado.toLong("idVenta"));
      if(seleccionado.containsKey("cliente") && !Objects.equals(Constantes.VENTA_AL_PUBLICO_GENERAL, seleccionado.toString("cliente"))) 
        cliente= seleccionado.toString("cliente");
      if((seleccionado.containsKey("tipo") && Objects.equals(seleccionado.toString("tipo"), "CREDITO")) || 
         (seleccionado.containsKey("idVentaEstatus") && Objects.equals(seleccionado.toLong("idVentaEstatus"), 4L)) || 
         (seleccionado.containsKey("estatus") && Objects.equals(seleccionado.toString("estatus"), "CREDITO"))) 
        this.toPrintTicket(seleccionado.toLong("idVenta"), seleccionado.toTimestamp("registro"));
      else {
			  adminTicket= new AdminTickets((TicketVenta)DaoFactory.getInstance().toEntity(TicketVenta.class, "TcManticVentasDto", "detalle", params));			
			  ticket= new CreateTicket(adminTicket, this.toPago(adminTicket, seleccionado.toLong("idVenta")), this.toTipoTransaccion(seleccionado.toLong("idVentaEstatus")), cliente, seleccionado.toLong("idCajero"));
			  UIBackingUtilities.execute("jsTicket.imprimirTicket('" + ticket.getPrincipal().getClave()  + "-" + toConsecutivoTicket(seleccionado.toLong("idVentaEstatus"), adminTicket) + "','" + ticket.toHtml() + "');");
        UIBackingUtilities.execute("jsTicket.clicTicket();");
      } // if  
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			mx.org.kaana.libs.formato.Error.mensaje(e);
		} // catch		
	} // doTicket
	
	private String toTipoTransaccion(Long idEstatus) {
		String regresar       = "VENTA DE MOSTRADOR";
		EEstatusVentas estatus= null;		
		try {
			if(idEstatus<= EEstatusVentas.EN_CAPTURA.getIdEstatusVenta() || idEstatus.equals(EEstatusVentas.TIMBRADA.getIdEstatusVenta())) {
				estatus= EEstatusVentas.fromIdTipoPago(idEstatus);
				switch(estatus) {
					case PAGADA:
					case TIMBRADA:
					case TERMINADA:
						regresar= "VENTA DE MOSTRADOR";
					break;
					case COTIZACION:
						regresar= "COTIZACIÓN";
						break;
					case APARTADOS:
						regresar= "APARTADO";
						break;
					case CREDITO:
						regresar= "VENTA A CREDITO";
						break;
				} // switch			
			} // if			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toTipoTransaccion
	
	private String toConsecutivoTicket(Long idEstatus, AdminTickets ticket) {
		String regresar= null;		
		try {
			if(idEstatus.equals(EEstatusVentas.COTIZACION.getIdEstatusVenta()))
				regresar= ((TicketVenta)(ticket.getOrden())).getCotizacion();							
			else
				regresar= ((TicketVenta)(ticket.getOrden())).getTicket();			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toTipoTransaccion
	
	private Pago toPago(AdminTickets adminTicket, Long idVenta) throws Exception {
		Pago regresar            = null;
		List<Entity> detallePago = null;
		Map<String, Object>params= new HashMap<>();
		ETipoMediosPago medioPago= null;
    EEstatusVentas venta     = EEstatusVentas.ABIERTA;
		try {
			regresar= new Pago(adminTicket.getTotales());
			params.put("idVenta", idVenta);
      // SON LOS ABONOS QUE SE HAN REALIZADO POR EL CLIENTE SI ES UN APARTADO
      detallePago= DaoFactory.getInstance().toEntitySet("VistaApartadosDto", "abonos", params, Constantes.SQL_TODOS_REGISTROS);
      if(detallePago!= null && !detallePago.isEmpty()) {
        for (Entity pago : detallePago) {
          medioPago= ETipoMediosPago.fromIdTipoPago(pago.toLong("idTipoMedioPago"));
          if(ETipoMediosPago.EFECTIVO.equals(medioPago))
            regresar.addAbono(pago.toDouble("importe"), pago.toTimestamp("registro"), pago.toString("referencia"));
          else
            regresar.addAbono(pago.toDouble("importe"), pago.toTimestamp("registro"), "(REF "+ pago.toString("referencia")+ ")");
        } // for
        venta= EEstatusVentas.APARTADOS;
      } // if
      switch (venta) {
        case APARTADOS:
          break;
        default:  
    			detallePago= DaoFactory.getInstance().toEntitySet("TrManticVentaMedioPagoDto", "ticket", params, Constantes.SQL_TODOS_REGISTROS);
      } // switch
			if(detallePago!= null && !detallePago.isEmpty()) {
				for(Entity pago: detallePago) {
					medioPago= ETipoMediosPago.fromIdTipoPago(pago.toLong("idTipoMedioPago"));
					switch(medioPago) {
						case EFECTIVO:
							regresar.setEfectivo(regresar.getEfectivo()+ pago.toDouble("importe"));							
							break;
						case TARJETA_DEBITO:
							regresar.setDebito(regresar.getDebito()+ pago.toDouble("importe"));							
							regresar.setReferenciaDebito(pago.toString("referencia"));							
							break;
						case TARJETA_CREDITO:
							regresar.setCredito(regresar.getCredito()+ pago.toDouble("importe"));							
							regresar.setReferenciaCredito(pago.toString("referencia"));							
							break;
						case CHEQUE:
							regresar.setCheque(regresar.getCheque()+ pago.toDouble("importe"));							
							regresar.setReferenciaCheque(pago.toString("referencia"));							
							break;
						case TRANSFERENCIA:
							regresar.setTransferencia(regresar.getTransferencia()+ pago.toDouble("importe"));							
							regresar.setReferenciaTransferencia(pago.toString("referencia"));							
							break;
					} // switch
				} // for
			} // if
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
		return regresar;
	} 
  
}