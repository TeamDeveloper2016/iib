package mx.org.kaana.mantic.ventas.reglas;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import org.hibernate.Session;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.facturama.reglas.CFDIGestor;
import mx.org.kaana.libs.facturama.reglas.TransaccionFactura;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.clientes.beans.ClienteDomicilio;
import mx.org.kaana.mantic.catalogos.clientes.beans.Domicilio;
import mx.org.kaana.mantic.catalogos.clientes.reglas.NotificaCliente;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.correos.enums.ECorreos;
import mx.org.kaana.mantic.db.dto.TcManticClientesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDeudasDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDto;
import mx.org.kaana.mantic.db.dto.TcManticDomiciliosDto;
import mx.org.kaana.mantic.db.dto.TcManticServiciosDto;
import mx.org.kaana.mantic.db.dto.TcManticVentasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticVentasDto;
import mx.org.kaana.mantic.db.dto.TcManticVentasDetallesDto;
import mx.org.kaana.mantic.db.dto.TrManticClienteDomicilioDto;
import mx.org.kaana.mantic.db.dto.TrManticClienteTipoContactoDto;
import mx.org.kaana.mantic.enums.EEstatusClientes;
import mx.org.kaana.mantic.enums.EEstatusVentas;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETipoVenta;
import mx.org.kaana.mantic.enums.ETiposContactos;
import mx.org.kaana.mantic.facturas.beans.ClienteFactura;
import mx.org.kaana.mantic.ventas.beans.ClienteVenta;
import org.apache.log4j.Logger;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Transaccion extends TransaccionFactura {

  private static final Logger LOG  = Logger.getLogger(Transaccion.class);
	private ClienteVenta clienteVenta;
	private TcManticVentasBitacoraDto bitacora;
	private TcManticVentasDto orden;	
	private List<Articulo> articulos;
	private String messageError;	
	private String justificacion;
	private Long idClienteNuevo;
	private Date vigencia;
	private boolean aplicar;
	private Long idServicio;

	public Transaccion(TcManticVentasBitacoraDto bitacora) {
		this.bitacora= bitacora;
	} // Transaccion
	
	public Transaccion(TcManticVentasDto orden) {
		this(orden, "");
	}
	
	public Transaccion(TcManticVentasDto orden, String justificacion) {
		this(orden, new ArrayList<Articulo>(), justificacion);
	} // Transaccion

	public Transaccion(TcManticVentasDto orden, List<Articulo> articulos) {
		this(orden, articulos, new Date(Calendar.getInstance().getTimeInMillis()));
	}
	
	public Transaccion(TcManticVentasDto orden, List<Articulo> articulos, Long idServicio) {
		this(orden, articulos, "", new Date(Calendar.getInstance().getTimeInMillis()), idServicio);
	}
	
	public Transaccion(TcManticVentasDto orden, List<Articulo> articulos, Date vigencia) {
		this(orden, articulos, "", vigencia);
	}
	
	public Transaccion(TcManticVentasDto orden, List<Articulo> articulos, String justificacion) { 
		this(orden, articulos, justificacion, new Date(Calendar.getInstance().getTimeInMillis()));
	}
	
	public Transaccion(TcManticVentasDto orden, List<Articulo> articulos, String justificacion, Date vigencia) {
		this(orden, articulos, justificacion, vigencia, -1L);
	}
	
	public Transaccion(TcManticVentasDto orden, List<Articulo> articulos, String justificacion, Date vigencia, Long idServicio) {
		this.orden        = orden;		
		this.articulos    = articulos;
		this.justificacion= justificacion;
		this.vigencia     = vigencia;
		this.aplicar      = false;
		this.idServicio   = idServicio;
	} // Transaccion

	public Transaccion(ClienteVenta clienteVenta) {
		this.clienteVenta = clienteVenta;
	}	
	
	public String getMessageError() {
		return messageError;
	} // Transaccion

	public void setMessageError(String messageError) {
		this.messageError = messageError;
	}	
	
	public TcManticVentasDto getOrden() {
		return orden;
	}	

	public void setOrden(TcManticVentasDto orden) {
		this.orden = orden;
	}	
	
	public void setClienteVenta(ClienteVenta clienteVenta) {
		this.clienteVenta = clienteVenta;
	}	

	public Long getIdClienteNuevo() {
		return idClienteNuevo;
	}

	public boolean isAplicar() {
		return aplicar;
	}

	public void setAplicar(boolean aplicar) {
		this.aplicar = aplicar;
	}	

  public List<Articulo> getArticulos() {
    return articulos;
  }
  
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar          = Boolean.FALSE;
		Map<String, Object> params= new HashMap<>();
		Long idEstatusVenta       = EEstatusVentas.ELABORADA.getIdEstatusVenta();
		Siguiente consecutivo     = null;
		try {
			if(this.orden!= null)
				params.put("idVenta", this.orden.getIdVenta());
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" el ticket de venta");
			switch(accion) {
				case GENERAR:
					idEstatusVenta= EEstatusVentas.COTIZACION.getIdEstatusVenta();
					this.orden.setVigencia(this.vigencia);
					regresar= this.orden.getIdVenta()!= null && !this.orden.getIdVenta().equals(-1L)? this.actualizarVenta(sesion, idEstatusVenta): this.registrarVentaCotizacion(sesion, idEstatusVenta, true);					
					break;
				case AGREGAR:
				case REGISTRAR:			
				case DESACTIVAR:
					idEstatusVenta= accion.equals(EAccion.AGREGAR) ? EEstatusVentas.ABIERTA.getIdEstatusVenta(): (accion.equals(EAccion.DESACTIVAR) ? this.orden.getIdVentaEstatus() : idEstatusVenta);
					regresar= this.orden.getIdVenta()!= null && !this.orden.getIdVenta().equals(-1L)? this.actualizarVenta(sesion, idEstatusVenta): this.registrarVenta(sesion, idEstatusVenta);					
					break;
				case MODIFICAR:
					regresar= this.actualizarVenta(sesion, EEstatusVentas.ABIERTA.getIdEstatusVenta());					
					break;				
				case ELIMINAR:
					idEstatusVenta= EEstatusVentas.CANCELADA.getIdEstatusVenta();
					this.orden= (TcManticVentasDto) DaoFactory.getInstance().findById(sesion, TcManticVentasDto.class, this.orden.getIdVenta());
					this.orden.setIdVentaEstatus(idEstatusVenta);					
					if(DaoFactory.getInstance().update(sesion, this.orden)>= 1L)
						regresar= this.registraBitacora(sesion, this.orden.getIdVenta(), idEstatusVenta, this.justificacion);					
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L){
						this.orden= (TcManticVentasDto) DaoFactory.getInstance().findById(sesion, TcManticVentasDto.class, this.bitacora.getIdVenta());
						this.orden.setIdVentaEstatus(this.bitacora.getIdVentaEstatus());
						regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
					} // if
					break;				
				case ASIGNAR:
					regresar= this.procesarCliente(sesion);
					break;					
				case TRANSFORMACION:
					regresar= this.actualizarCliente(sesion);
					break;
				case REPROCESAR:
				case COPIAR:
					regresar= this.actualizarVenta(sesion, accion.equals(EAccion.REPROCESAR)? EEstatusVentas.PAGADA.getIdEstatusVenta(): EEstatusVentas.CREDITO.getIdEstatusVenta());				
					break;		
				case NO_APLICA:
					params= new HashMap<>();
					params.put("idVenta", this.orden.getIdVenta());
					if(DaoFactory.getInstance().deleteAll(sesion, TcManticVentasBitacoraDto.class, params)>= 0) {
						if(DaoFactory.getInstance().deleteAll(sesion, TcManticVentasDetallesDto.class, params)>= 0)
							regresar= DaoFactory.getInstance().delete(sesion, this.orden)>= 1L;
					} // if					
	        break;
				case PROCESAR:
					consecutivo= this.toSiguienteEspecial(sesion);			
          // NO RECUERDO PORQUE SE DEJO FIJO EL 2020 PARA UNA VENTA EXPRESS
          this.orden.setEjercicio(2020L);
					this.orden.setConsecutivo(consecutivo.getOrden());			
					this.orden.setOrden(consecutivo.getOrden());
          this.orden.setCticket(consecutivo.getOrden());			
          this.orden.setTicket(consecutivo.getConsecutivo());
					this.orden.setIdUsuario(JsfBase.getIdUsuario());
          if(this.orden.getIdBanco()!=null && this.orden.getIdBanco()<= -1L)
            this.orden.setIdBanco(null);
					if(this.orden.isValid())
						regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
					else
						regresar= DaoFactory.getInstance().insert(sesion, this.orden)>= 1L;
				  this.registraBitacora(sesion, this.orden.getIdVenta(), this.orden.getIdVentaEstatus(), "REGISTRO DE VENTA EXPRESS");
					if(!this.aplicar)
						this.registrarDeuda(sesion, this.orden.getTotal());
					break;
				case MOVIMIENTOS:
					idEstatusVenta= EEstatusVentas.ABIERTA.getIdEstatusVenta();
					regresar= this.orden.getIdVenta()!= null && !this.orden.getIdVenta().equals(-1L) ? this.actualizarVenta(sesion, idEstatusVenta): this.registrarVenta(sesion, idEstatusVenta);					
          sesion.flush();
					break;
				case LISTAR:
					//Procesar servicio de taller
					boolean actualizar= this.orden.getIdVenta()!= null && !this.orden.getIdVenta().equals(-1L);
					idEstatusVenta= accion.equals(EAccion.AGREGAR) ? EEstatusVentas.ABIERTA.getIdEstatusVenta() : (accion.equals(EAccion.DESACTIVAR)? this.orden.getIdVentaEstatus() : idEstatusVenta);
					regresar= actualizar ? this.actualizarVenta(sesion, idEstatusVenta): this.registrarVenta(sesion, idEstatusVenta);					
					if(!actualizar) {
						TcManticServiciosDto servicio= (TcManticServiciosDto) DaoFactory.getInstance().findById(sesion, TcManticServiciosDto.class, this.idServicio);
						servicio.setIdVenta(this.orden.getIdVenta());
						regresar= DaoFactory.getInstance().update(sesion, servicio)>= 1L;
					} // if
					break;
				case DESTRANSFORMACION:
          params.put("idAcepta", JsfBase.getIdUsuario());
          params.put("idVenta", this.orden.getIdVenta());
          regresar= DaoFactory.getInstance().updateAll(sesion, TcManticVentasDto.class, params, "autoriza")>= 1L;
					break;
				case COMPLEMENTAR:
          this.orden.setCandado(2L);
          regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
          break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {		
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
		} // catch		
		if(this.orden!= null)
			LOG.info("Se genero de forma correcta la orden: "+ this.orden.getConsecutivo());
		return regresar;
	}	// ejecutar

	private boolean registrarVenta(Session sesion, Long idEstatusVenta) throws Exception{
		return this.registrarVentaCotizacion(sesion, idEstatusVenta, false);
	}
	
	private boolean registrarVentaCotizacion(Session sesion, Long idEstatusVenta, boolean cotizacion) throws Exception {
		boolean regresar     = false;
		Siguiente consecutivo= null;
		Siguiente siguiente  = null;
    try {
      if(cotizacion) {
        siguiente= this.toSiguienteCotizacion(sesion);
        this.orden.setCcotizacion(siguiente.getOrden());
        this.orden.setCotizacion(siguiente.getConsecutivo());
      } // if
      consecutivo= this.toSiguienteVenta(sesion);			
      this.orden.setConsecutivo(consecutivo.getOrden());			
      this.orden.setOrden(consecutivo.getOrden());
      this.orden.setIdVentaEstatus(idEstatusVenta);
      this.orden.setEjercicio(new Long(Fecha.getAnioActual()));

      if(this.orden.getIdCliente()< 0)
        this.orden.setIdCliente(this.toClienteDefault(sesion));
      if(this.orden.isValid())
        regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
      else
        regresar= DaoFactory.getInstance().insert(sesion, this.orden)>= 1L;
      if(regresar)
        regresar= this.registraBitacora(sesion, this.orden.getIdVenta(), idEstatusVenta, "");
      this.toFillArticulos(sesion);				
    } // try
    catch(Exception e) {
      throw e;
    } // catch
		return regresar;
	} // registrarVenta
	
	private boolean actualizarVenta(Session sesion, Long idEstatusVenta) throws Exception {
		boolean regresar         = false;
    Siguiente consecutivo    = null;
		Map<String, Object>params= null;
		try {	
      String hoy  = Fecha.getHoyEstandar();
      String venta= Fecha.formatear(Fecha.FECHA_ESTANDAR, this.orden.getRegistro());
      // SI ES UNA COTIZACI�N ENTONCES ACTUALIZAR LA FECHA Y EL NUMERO DE CUENTA DEL D�A 
      if(Objects.equals(this.orden.getIdVentaEstatus(), EEstatusVentas.COTIZACION.getIdEstatusVenta()) && !Objects.equals(hoy, venta)) {
        consecutivo= this.toSiguienteVenta(sesion);			
        this.orden.setConsecutivo(consecutivo.getOrden());			
        this.orden.setOrden(consecutivo.getOrden());
        this.orden.setDia(new Date(Calendar.getInstance().getTimeInMillis()));
        this.orden.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
      } // if
			this.orden.setIdVentaEstatus(idEstatusVenta);			
			if(this.orden.getIdCliente()< 0)
				this.orden.setIdCliente(this.toClienteDefault(sesion));
			if(this.orden.getIdBanco()!= null && this.orden.getIdBanco()<= -1L)
				this.orden.setIdBanco(null);
			regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
			if(this.registraBitacora(sesion, this.orden.getIdVenta(), idEstatusVenta, "")) {
				params= new HashMap<>();
				params.put("idVenta", this.orden.getIdVenta());
				regresar= DaoFactory.getInstance().deleteAll(sesion, TcManticVentasDetallesDto.class, params)>= 1;
				this.toFillArticulos(sesion);
			} // if
		} // try		
		finally{
			Methods.clean(params);
		} // finally			
		return regresar;
	} // actualizarVenta
	
	protected boolean registraBitacora(Session sesion, Long idVenta, Long idVentaEstatus, String justificacion) throws Exception{
		boolean regresar                  = false;
		TcManticVentasBitacoraDto bitVenta= new TcManticVentasBitacoraDto(-1L, justificacion, JsfBase.getIdUsuario(), idVenta, idVentaEstatus, this.orden.getConsecutivo(), this.orden.getTotal());
		regresar= DaoFactory.getInstance().insert(sesion, bitVenta)>= 1L;		
		return regresar;
	} // registrarBitacora
	
	protected void toFillArticulos(Session sesion) throws Exception {
		toFillArticulos(sesion, this.articulos);
	} // toFillArticulos
	
	protected void toFillArticulos(Session sesion, List<Articulo> detalleArt) throws Exception {
		List<Articulo> todos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(sesion, Articulo.class, "TcManticVentasDetallesDto", "detalle", this.orden.toMap());
		for (Articulo item: todos) 
			if(detalleArt.indexOf(item)< 0)
				DaoFactory.getInstance().delete(sesion, TcManticVentasDetallesDto.class, item.getKey());
		for (Articulo articulo: detalleArt) {
			if(articulo.isValid()){
				TcManticVentasDetallesDto item= articulo.toVentaDetalle();
				item.setIdVenta(this.orden.getIdVenta());
				if(DaoFactory.getInstance().findIdentically(sesion, TcManticVentasDetallesDto.class, item.toMap())== null) 
					DaoFactory.getInstance().insert(sesion, item);
				else
					DaoFactory.getInstance().update(sesion, item);
			} // if
		} // for
	} // toFillArticulos
	
	protected Siguiente toSiguienteVenta(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());
			params.put("dia", Fecha.getHoyEstandar());
			params.put("idEmpresa", this.orden.getIdEmpresa());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticVentasDto", "siguiente", params, "siguiente");
			if(next.getData()!= null)
				regresar= new Siguiente(next.toLong());
			else
				regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L); 
		} // try		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toSiguiente
	
	protected Siguiente toSiguienteEspecial(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", 2020);
			params.put("idEmpresa", this.orden.getIdEmpresa());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticVentasDto", "especial", params, "siguiente");
			if(next.getData()!= null)
				regresar= new Siguiente("2020", next.toLong(), next.toLong());
			else
				regresar= new Siguiente("2020", Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L, Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L); 
		} // try		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toSiguiente
	
	private Siguiente toSiguienteCotizacion(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", this.orden.getIdEmpresa());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticVentasDto", "siguienteCotizacion", params, "siguiente");
			if(next.getData()!= null)
				regresar= new Siguiente(next.toLong());
			else
				regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L);
		} // try		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toSiguiente
	
	protected Long toClienteDefault(Session sesion) throws Exception{
		Long regresar            = -1L;
		Entity cliente           = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("clave", Constantes.VENTA_AL_PUBLICO_GENERAL_CLAVE);
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getDependencias());
			cliente= (Entity) DaoFactory.getInstance().toEntity(sesion, "TcManticClientesDto", "clienteDefault", params);
			if(cliente!= null)
				regresar= cliente.getKey();
		} // try
		finally {			
			Methods.clean(params);
		} // finally
		return regresar;
	} // toClienteDefault
	
	@Override
	protected boolean procesarCliente(Session sesion) throws Exception {
    boolean regresar = Boolean.FALSE;
    Long idCliente   = -1L;    
		this.messageError= "ERROR AL REGISTRAR EL CLIENTE";
		this.clienteVenta.getCliente().setIdCredito(2L);
		this.clienteVenta.getCliente().setLimiteCredito(0D);
		this.clienteVenta.getCliente().setPlazoDias(15L);
		this.clienteVenta.getCliente().setIdUsuario(JsfBase.getIdUsuario());
		this.clienteVenta.getCliente().setIdEmpresa(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
		this.clienteVenta.getCliente().setIdTipoVenta(ETipoVenta.MENUDEO.getIdTipoVenta());
		idCliente = DaoFactory.getInstance().insert(sesion, this.clienteVenta.getCliente());
		this.idClienteNuevo= idCliente;
		if(this.updateDomicilioPrincipal(sesion, this.clienteVenta.getCliente().getIdCliente())){
			if(this.registraClientesDomicilios(sesion, idCliente)) 
				regresar= this.registraClientesTipoContacto(sesion, idCliente);			
		} // if
		sesion.flush();
		if(idCliente > -1)
			this.registraClienteFacturama(sesion, idCliente);        
    return regresar;
  } // procesarCliente
	
	protected void registraClienteFacturama(Session sesion, Long idCliente) {	
		CFDIGestor gestor     = null;
		ClienteFactura cliente= null;
		try {
			gestor= new CFDIGestor(idCliente);
			cliente= gestor.toClienteFactura(sesion);
			setCliente(cliente);
			super.procesarCliente(sesion);
		} // try
		catch (Exception e) {			
			Error.mensaje(e);
		} // catch		
	} // registraArticuloFacturama
	
	protected boolean actualizarCliente(Session sesion) throws Exception {
    boolean regresar= false;
		TrManticClienteDomicilioDto domicilio= null;    
		this.messageError = "Error al registrar el cliente";						
		if(DaoFactory.getInstance().update(sesion, this.clienteVenta.getCliente())>= 1L){
			domicilio= (TrManticClienteDomicilioDto) DaoFactory.getInstance().findById(sesion, TrManticClienteDomicilioDto.class, this.clienteVenta.getDomicilio().getIdClienteDomicilio());
			domicilio.setIdDomicilio(toIdDomicilio(sesion, toClienteDomicilio(), false));
			domicilio.setIdTipoDomicilio(this.clienteVenta.getDomicilio().getIdTipoDomicilio());
			domicilio.setIdPrincipal(1L);
			if(updateDomicilioPrincipal(sesion, this.clienteVenta.getCliente().getIdCliente())){
				if (DaoFactory.getInstance().update(sesion, domicilio)>= 1L) {
					regresar = registraClientesTipoContacto(sesion, this.clienteVenta.getCliente().getIdCliente());	
					sesion.flush();
					actualizarClienteFacturama(sesion, this.clienteVenta.getCliente().getIdCliente());
				} // if
			} // if
		} // if			    
    return regresar;
  } // procesarCliente
	
	private void actualizarClienteFacturama(Session sesion, Long idCliente){		
		CFDIGestor gestor     = null;
		ClienteFactura cliente= null;
		try {
			gestor= new CFDIGestor(idCliente);
			cliente= gestor.toClienteFactura(sesion);
			setCliente(cliente);
			if(cliente.getIdFacturama()!= null)
				updateCliente(sesion);
			else
				super.procesarCliente(sesion);
		} // try
		catch (Exception e) {			
			Error.mensaje(e);
		} // catch		
	} // actualizarArticuloFacturama
	
	private boolean registraClientesDomicilios(Session sesion, Long idCliente) throws Exception {
    TrManticClienteDomicilioDto dto  = null;
		ClienteDomicilio clienteDomicilio= null;
    boolean regresar                 = true;
    try {			
			clienteDomicilio= this.toClienteDomicilio();
			clienteDomicilio.setIdCliente(idCliente);
			clienteDomicilio.setIdUsuario(JsfBase.getIdUsuario());
			clienteDomicilio.setIdDomicilio(this.toIdDomicilio(sesion, clienteDomicilio));		
			clienteDomicilio.setIdClienteDomicilio(-1L);
			clienteDomicilio.setIdPrincipal(1L);
			dto= (TrManticClienteDomicilioDto)clienteDomicilio;
			regresar= DaoFactory.getInstance().insert(sesion, dto)>= 1L;			
    } // try    
    finally {
      this.messageError = "Error al registrar los domicilios, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraClientesDomicilios
	
	private boolean updateDomicilioPrincipal(Session sesion, Long idCliente) throws Exception {
		boolean regresar         = Boolean.FALSE;
		Map<String, Object>params= new HashMap<>();
		try {
			params.put("idCliente", idCliente);
			regresar= DaoFactory.getInstance().execute(ESql.UPDATE, sesion, "TrManticClienteDomicilioDto", "principal", params)>= 0L;
		} // try		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;		
	} // updateDomicilioPrincipal
	
	private ClienteDomicilio toClienteDomicilio() throws Exception{
		ClienteDomicilio regresar= null;		
		regresar= new ClienteDomicilio();
		regresar.setIdPrincipal(1L);			
		regresar.setDomicilio(this.clienteVenta.getDomicilio().getDomicilio());
		regresar.setIdDomicilio(this.clienteVenta.getDomicilio().getDomicilio().getKey());
		regresar.setIdUsuario(JsfBase.getIdUsuario());
		regresar.setIdTipoDomicilio(this.clienteVenta.getDomicilio().getIdTipoDomicilio());
		regresar.setConsecutivo(1L);
		regresar.setIdEntidad(this.clienteVenta.getDomicilio().getIdEntidad());
		regresar.setIdMunicipio(this.clienteVenta.getDomicilio().getIdMunicipio());
		regresar.setIdLocalidad(this.clienteVenta.getDomicilio().getLocalidad());
		regresar.setCodigoPostal(this.clienteVenta.getDomicilio().getCodigoPostal());
		regresar.setCalle(this.clienteVenta.getDomicilio().getCalle());
		regresar.setExterior(this.clienteVenta.getDomicilio().getNumeroExterior());
		regresar.setInterior(this.clienteVenta.getDomicilio().getNumeroInterior());
		regresar.setEntreCalle(this.clienteVenta.getDomicilio().getEntreCalle());
		regresar.setyCalle(this.clienteVenta.getDomicilio().getYcalle());
		regresar.setColonia(this.clienteVenta.getDomicilio().getAsentamiento());
		regresar.setNuevoCp(this.clienteVenta.getDomicilio().getCodigoPostal()!= null && !Cadena.isVacio(this.clienteVenta.getDomicilio().getCodigoPostal()));		
		return regresar;
	} // setValuesClienteDomicilio
	
	private Long toIdDomicilio(Session sesion, ClienteDomicilio clienteDomicilio) throws Exception{		
		return toIdDomicilio(sesion, clienteDomicilio, true);
	} // toIdDomicilio
	
	private Long toIdDomicilio(Session sesion, ClienteDomicilio clienteDomicilio, boolean agregar) throws Exception{		
		Entity entityDomicilio= null;
		Long regresar= -1L;		
		if(agregar)
			entityDomicilio= toDomicilio(sesion, clienteDomicilio);
		else
			entityDomicilio= toDomicilio(sesion, this.clienteVenta.getDomicilio());
		if(entityDomicilio!= null)
			regresar= entityDomicilio.getKey();
		else
			regresar= insertDomicilio(sesion, clienteDomicilio);										
		return regresar;
	} // registrarDomicilio	
	
	private Long insertDomicilio(Session sesion, ClienteDomicilio clienteDomicilio) throws Exception{
		TcManticDomiciliosDto domicilio= new TcManticDomiciliosDto();
		domicilio.setIdLocalidad(clienteDomicilio.getIdLocalidad().getKey());
		domicilio.setAsentamiento(clienteDomicilio.getColonia());
		domicilio.setCalle(clienteDomicilio.getCalle());
		domicilio.setCodigoPostal(clienteDomicilio.getCodigoPostal());
		domicilio.setEntreCalle(clienteDomicilio.getEntreCalle());
		domicilio.setIdUsuario(JsfBase.getIdUsuario());
		domicilio.setNumeroExterior(clienteDomicilio.getExterior());
		domicilio.setNumeroInterior(clienteDomicilio.getInterior());
		domicilio.setYcalle(clienteDomicilio.getyCalle());
		return DaoFactory.getInstance().insert(sesion, domicilio);
	} // insertDomicilio
	
	private Entity toDomicilio(Session sesion, ClienteDomicilio clienteDomicilio) throws Exception{
		Entity regresar= null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idLocalidad", clienteDomicilio.getIdLocalidad().getKey());
			params.put("codigoPostal", clienteDomicilio.getCodigoPostal());
			params.put("calle", clienteDomicilio.getCalle());
			params.put("numeroExterior", clienteDomicilio.getExterior());
			params.put("numeroInterior", clienteDomicilio.getInterior());
			params.put("asentamiento", clienteDomicilio.getColonia());
			params.put("entreCalle", clienteDomicilio.getEntreCalle());
			params.put("yCalle", clienteDomicilio.getyCalle());
			regresar= (Entity) DaoFactory.getInstance().toEntity(sesion, "TcManticDomiciliosDto", "domicilioExiste", params);
		} // try		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toDomicilio
	
	private Entity toDomicilio(Session sesion, Domicilio clienteDomicilio) throws Exception{
		Entity regresar= null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idLocalidad", clienteDomicilio.getIdLocalidad());
			params.put("codigoPostal", clienteDomicilio.getCodigoPostal());
			params.put("calle", clienteDomicilio.getCalle());
			params.put("numeroExterior", clienteDomicilio.getNumeroExterior());
			params.put("numeroInterior", clienteDomicilio.getNumeroInterior());
			params.put("asentamiento", clienteDomicilio.getAsentamiento());
			params.put("entreCalle", clienteDomicilio.getEntreCalle());
			params.put("yCalle", clienteDomicilio.getYcalle());
			regresar= (Entity) DaoFactory.getInstance().toEntity(sesion, "TcManticDomiciliosDto", "domicilioExiste", params);
		} // try		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toDomicilio
	
	public boolean registraClientesTipoContacto(Session sesion, Long idCliente) throws Exception {
    int count       = 0;
    boolean validate= false;
    boolean regresar= false;
		TrManticClienteTipoContactoDto pivote= null;
		int orden = 1;
    boolean validateOrden = true;
    try {
      for (TrManticClienteTipoContactoDto clienteTipoContacto : this.clienteVenta.getContacto()) {
				if(clienteTipoContacto.getValor()!= null && !Cadena.isVacio(clienteTipoContacto.getValor())){
					if(clienteTipoContacto.getIdTipoContacto().equals(ETiposContactos.CORREO.getKey()) && validateOrden){
						clienteTipoContacto.setOrden(1L);
						validateOrden= false;
					} // if
					else
						clienteTipoContacto.setOrden(orden + 1L);
					if(clienteTipoContacto.getIdClienteTipoContacto().equals(-1L)){
						clienteTipoContacto.setIdCliente(idCliente);
						clienteTipoContacto.setIdUsuario(JsfBase.getIdUsuario());
						clienteTipoContacto.setIdClienteTipoContacto(-1L);
						validate = registrarSentencia(sesion, clienteTipoContacto);		
					}	// if				
					else{
						pivote= (TrManticClienteTipoContactoDto) DaoFactory.getInstance().findById(sesion, TrManticClienteTipoContactoDto.class, clienteTipoContacto.getIdClienteTipoContacto());
						pivote.setValor(clienteTipoContacto.getValor());
						validate = actualizarSentencia(sesion, pivote);		
					} // else
					orden++;
				} // if
				else
					validate= true;
        if (validate) {
          count++;
        }
      } // for		
      regresar = count == this.clienteVenta.getContacto().size();
    } // try    
    finally {
      this.messageError = "Error al registrar los tipos de contacto, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraClientesTipoContacto
	
	private boolean registrarSentencia(Session sesion, IBaseDto dto) throws Exception {
    return DaoFactory.getInstance().insert(sesion, dto) >= 1L;
  } // registrar
	
	private boolean actualizarSentencia(Session sesion, IBaseDto dto) throws Exception {
    return DaoFactory.getInstance().update(sesion, dto) >= 1L;
  } // registrar
	
	protected void registrarDeuda(Session sesion, Double importe) throws Exception {
		TcManticClientesDeudasDto deuda= null;		
    try {
      deuda= new TcManticClientesDeudasDto();
      deuda.setIdVenta(this.getOrden().getIdVenta());
      deuda.setIdCliente(this.getOrden().getIdCliente());
      deuda.setIdUsuario(JsfBase.getIdUsuario());
      deuda.setImporte(importe);
      deuda.setSaldo(importe);
      deuda.setLimite(this.toLimiteCredito(sesion));
      deuda.setIdClienteEstatus(EEstatusClientes.INICIADA.getIdEstatus());
      DaoFactory.getInstance().insert(sesion, deuda);	
      TcManticClientesBitacoraDto movimiento= new TcManticClientesBitacoraDto(
        -1L, // Long idClienteBitacora, 
        deuda.getIdClienteEstatus(), // Long idClienteEstatus, 
        null, // String justificacion, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        deuda.getIdClienteDeuda() // Long idClienteDeuda
      );
      DaoFactory.getInstance().insert(sesion, movimiento);

      sesion.flush();
      NotificaCliente notifica= new NotificaCliente(
        sesion, // sesion
        this.getOrden().getIdCliente(), // Long idCliente, 
        EReportes.CUENTAS_POR_COBRAR, // EReportes reportes, 
        ECorreos.CREDITO, // ECorreos correo
        this.getOrden().getIdVenta() // Long idVenta
      );
      notifica.doSendMail();
      notifica.doSendWhatsup();
    } // try 
    catch(Exception e) {
      throw e;
    } // catch
	} // registrarDeuda
	
	public Date toLimiteCredito(Session sesion) throws Exception{
		Date regresar              = null;
		TcManticClientesDto cliente= null;
		Long addDias               = 15L;
		Calendar calendar          = null;		
		cliente= (TcManticClientesDto) DaoFactory.getInstance().findById(sesion, TcManticClientesDto.class, this.orden.getIdCliente());
		addDias= cliente.getPlazoDias();			
		calendar= Calendar.getInstance();
		regresar= new Date(calendar.getTimeInMillis());			
		calendar.setTime(regresar);
		calendar.add(Calendar.DAY_OF_YEAR, addDias.intValue());
		regresar= new Date(calendar.getTimeInMillis());		
		return regresar;
	} // toLimiteCredito
  
	protected Siguiente toSiguienteTicket(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", getOrden().getIdEmpresa());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticVentasDto", "siguienteTicket", params, "siguiente");
			if(next!= null && next.getData()!= null)
				regresar= new Siguiente(next.toLong());
			else
				regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L);
		} // try		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toSiguienteTicket
  
} 