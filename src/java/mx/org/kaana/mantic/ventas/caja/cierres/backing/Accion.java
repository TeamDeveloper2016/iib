package mx.org.kaana.mantic.ventas.caja.cierres.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticCierresDto;
import mx.org.kaana.mantic.ventas.caja.cierres.beans.Denominacion;
import mx.org.kaana.mantic.ventas.caja.cierres.beans.Importe;
import mx.org.kaana.mantic.ventas.caja.cierres.reglas.Cierre;
import org.primefaces.event.TabChangeEvent;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticVentasCajaCierresAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 323323488565639361L;
  
	private List<Importe> importes;
	private Importe debito;
	private Importe credito;
	private Importe pivote;
	private List<Denominacion> denominaciones;
	private List<Denominacion> fondos;
	private FormatCustomLazy lazyModel;
	private EAccion accion;
  private FormatLazyModel lazyDetalle;
  private FormatLazyModel lazyMedios;

	public List<Importe> getImportes() {
		return importes;
	}

	public List<Denominacion> getDenominaciones() {
		return denominaciones;
	}

	public List<Denominacion> getFondos() {
		return fondos;
	}

	public FormatCustomLazy getLazyModel() {
		return lazyModel;
	}

	public String getCalculate() {
		this.doLoadAmbos();
		return "";
	}
		
	public FormatLazyModel getLazyDetalle() {
		return lazyDetalle;
	}		

	public FormatLazyModel getLazyMedios() {
		return lazyMedios;
	}		

  public String getParticular() {
    String kilos= Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("particular")).toDouble("cantidad"));
    String total= Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("particular")).toDouble("importe"));
    return "Suma de kilos: <strong>"+ kilos+ "</strong>    importe: <strong>"+ total+ "</strong>";  
  }

  public String getGeneral() {
    String total= Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("importe"));
    return "Abono(s): <strong>"+ total+ "</strong>";  
  }

  @Override
	@PostConstruct
  protected void init() {		
    try {
			if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion = JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("idCierre", JsfBase.getFlashAttribute("idCierre")== null? -1L: JsfBase.getFlashAttribute("idCierre"));
      this.attrs.put("idCaja", JsfBase.getFlashAttribute("idCaja")== null? -1L: JsfBase.getFlashAttribute("idCaja"));
      this.attrs.put("idEmpresa", JsfBase.getFlashAttribute("idEmpresa")== null? -1L: JsfBase.getFlashAttribute("idEmpresa"));
      this.attrs.put("sucursales", this.attrs.get("idEmpresa"));
  		this.attrs.put("totalCreditos", 0L);
			this.attrs.put("efectivo", 0D);
			this.attrs.put("total", 0D);
			this.attrs.put("disponible", 0D);
			this.attrs.put("continuar", this.accion.equals(EAccion.CONSULTAR)? "": "none");
      this.attrs.put("particular", this.toEmptyTotales());          
      this.attrs.put("general", this.toEmptyTotales());          
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  public void doLoad() {
    try {
  		this.attrs.put("limite", 3000D);
  		this.attrs.put("idEfectivo", 1);
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
			this.importes= (List<Importe>)DaoFactory.getInstance().toEntitySet(Importe.class, "VistaCierresCajasDto", "importes", this.attrs);
			switch(this.accion) {
				case AGREGAR:
					this.denominaciones= (List<Denominacion>)DaoFactory.getInstance().toEntitySet(Denominacion.class, "TcManticMonedasDto", "denominacion", this.attrs);
					break;	
				case CONSULTAR:
					this.denominaciones= (List<Denominacion>)DaoFactory.getInstance().toEntitySet(Denominacion.class, "VistaCierresCajasDto", "denominacion", this.attrs);
  		    this.attrs.put("idEfectivo", 2);
					this.fondos= (List<Denominacion>)DaoFactory.getInstance().toEntitySet(Denominacion.class, "VistaCierresCajasDto", "denominacion", this.attrs);
					break;	
			} // switch
			this.toLoadEmpresas();
			this.toLoadCuentas();
			this.toLoadCreditos();
			this.doCalculate();
			if(this.debito!= null) 
				this.importes.remove(this.debito);
			if(this.credito!= null) 
				this.importes.remove(this.credito);
			if(this.debito!= null || this.credito!= null) {
				this.pivote= new Importe();
				this.pivote.setMedioPago("TARJETA BANCARIA");
				this.pivote.setCaja(this.debito.getCaja());
				this.pivote.setEmpresa(this.debito.getEmpresa());
				this.pivote.setIdCaja(this.debito.getIdCaja());
				this.pivote.setIdCierre(this.debito.getIdCierre());
				this.pivote.setIdCierreCaja(this.debito.getIdCierreCaja());
				this.pivote.setIdTipoMedioPago(-1L);
				this.pivote.setDisponible((this.debito.getDisponible()== null? 0D: this.debito.getDisponible())+ (this.credito.getDisponible()== null? 0D: this.credito.getDisponible()));
				this.pivote.setAcumulado((this.debito.getAcumulado()== null? 0D: this.debito.getAcumulado())   + (this.credito.getAcumulado()== null? 0D: this.credito.getAcumulado()));
				this.pivote.setSaldo((this.debito.getSaldo()== null? 0D: this.debito.getSaldo())               + (this.credito.getSaldo()== null? 0D: this.credito.getSaldo()));
				this.pivote.setImporte((this.debito.getImporte()== null? 0D: this.debito.getImporte())         + (this.credito.getImporte()== null? 0D: this.credito.getImporte()));
				if(this.importes.size()> 1)
					this.importes.add(1, pivote);
				else
					this.importes.add(pivote);
			} // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

  public String doAceptar() {  
    Cierre transaccion= null;
    String regresar   = null;
    try {			
			TcManticCierresDto cierre= (TcManticCierresDto)DaoFactory.getInstance().findById(TcManticCierresDto.class, (Long)this.attrs.get("idCierre"));
			cierre.setObservaciones((String)this.attrs.get("observaciones"));
			// ajustar los importes capturados en las tarjetas de credito y agregarlos de nueva cuenta al arreglo de importes para que se actualicen
			double importe= 0D;
			if(this.pivote!= null)  {
				importe= this.pivote.getImporte();
				if(this.debito!= null) {
					importe= this.pivote.getImporte()>= this.debito.getSaldo()? this.pivote.getImporte()- this.debito.getSaldo(): 0D;
					this.debito.setImporte(this.pivote.getImporte()>= this.debito.getSaldo()? this.debito.getSaldo(): this.pivote.getImporte());
					this.importes.add(this.debito);
				} // if	
				if(this.credito!= null) {
					this.credito.setImporte(importe);
					this.importes.add(this.credito);
				} // if
				this.importes.remove(this.pivote);
			} // if	
			transaccion = new Cierre((Long)this.attrs.get("idCaja"), (Double)this.attrs.get("efectivo"), cierre, this.importes, this.denominaciones);
			if (transaccion.ejecutar(this.accion)) {
				if(this.accion.equals(EAccion.AGREGAR)) {
 				  regresar = "fondo".concat(Constantes.REDIRECIONAR);
					JsfBase.setFlashAttribute("accion", EAccion.PROCESAR);
					JsfBase.setFlashAttribute("idCaja", this.attrs.get("idCaja"));
					JsfBase.setFlashAttribute("idCierre", transaccion.getIdApertura());
					JsfBase.setFlashAttribute("idCierreAnterior", cierre.getIdCierre());
					JsfBase.setFlashAttribute("idEmpresa", this.attrs.get("idEmpresa"));
    			// UIBackingUtilities.execute("janal.alert('Se gener\\u00F3 el cierre de caja, con consecutivo: "+ cierre.getConsecutivo()+ "');");
				} // if	
				else
    			JsfBase.setFlashAttribute("idCierre", this.attrs.get("idCierre"));
 				if(!this.accion.equals(EAccion.CONSULTAR)) 
  				JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR)? "agregó": this.accion.equals(EAccion.COMPLETO) ? "aplicó": "modificó").concat(" el cierre de caja."), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al realizar el cierre de caja", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion
	
  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idCierre", this.attrs.get("idCierre"));
    return "filtro".concat(Constantes.REDIRECIONAR);
  } 
	
	private void toLoadEmpresas() {
		List<Columna> columns= new ArrayList<>();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			List<UISelectEntity> sucursales= (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", this.attrs, columns);
      this.attrs.put("sucursales", sucursales);
			this.doLoadCajas();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
    }// finally
	}
	
	public void doLoadCajas() {
		List<Columna> columns= new ArrayList<>();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			List<UISelectEntity> cajas= (List<UISelectEntity>) UIEntity.build("TcManticCajasDto", "unica", this.attrs, columns);
      this.attrs.put("cajas", cajas);
 			this.attrs.put("temporal", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("cajas")));
			if(this.attrs.get("temporal")!= null)
				this.attrs.put("limite", ((UISelectEntity)this.attrs.get("temporal")).toDouble("limite"));
    } // try
    finally {
      Methods.clean(columns);
    }// finally
	}

	private void toLoadCuentas() {
		List<Columna> columns= new ArrayList<>();
    try {
      columns.add(new Columna("dia", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("total", EFormatoDinamicos.MONEDA_SAT_DECIMALES));
      this.attrs.put("cuentas", UIEntity.build("VistaCierresCajasDto", "abiertas", this.attrs, columns));
 			this.attrs.put("idCuenta", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("cuentas")));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
    }// finally
	}

	private void toLoadCreditos() throws Exception {
		List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= new HashMap<>();
    try {
			params.put("idEmpresa", this.attrs.get("idEmpresa"));
			params.put("idCaja", this.attrs.get("idCaja"));
			TcManticCierresDto cierre= (TcManticCierresDto)DaoFactory.getInstance().findById(TcManticCierresDto.class, (Long)this.attrs.get("idCierre"));
			params.put("vigenciaInicio", Fecha.formatear(Fecha.FECHA_HORA_LARGA, cierre.getRegistro()));
			switch(this.accion) {
				case AGREGAR:
    			params.put("vigenciaFin", Fecha.getRegistro());
					break;
				case CONSULTAR:
    			params.put("vigenciaFin", Fecha.formatear(Fecha.FECHA_HORA_LARGA, cierre.getTermino()));
					break;
			} // switch		
      columns.add(new Columna("cliente", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("total", EFormatoDinamicos.MONEDA_SAT_DECIMALES));
			params.put("idCierre", cierre.getIdCierre());
      this.attrs.put("creditos", UIEntity.build("VistaCierresCajasDto", "creditos", params, columns));
 			this.attrs.put("idCredito", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("creditos")));
  		this.attrs.put("totalCreditos", ((List<UISelectEntity>)this.attrs.get("creditos")).size());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
			Methods.clean(params);
    }// finally
	}

	public void doCalculate() throws CloneNotSupportedException {
		Double sum  = 0D;
		Double total= 0D;
		for (Denominacion denominacion: this.denominaciones) {
			denominacion.setImporte(Numero.toRedondearSat(denominacion.getDenominacion()* denominacion.getCantidad()));
			sum+= denominacion.getImporte();
		} // for
    this.attrs.put("efectivo", Numero.toRedondearSat(sum));		
		for (Importe importe: this.importes) {
			importe.toCalculate();
			switch (importe.getIdTipoMedioPago().intValue()) {
				case 1:
					importe.setImporte(Numero.toRedondearSat(sum));
					this.attrs.put("disponible", importe.getDisponible()> importe.getSaldo()? importe.getSaldo(): importe.getDisponible());
					break;
				case 4: // esto es para acumular los conceptos de tajertas de credito y tarjetas de debito como un solo rubro
					importe.setOmitir(Boolean.TRUE);
					this.debito= importe;
					break;
				case 18: // esto es para acumular los conceptos de tajertas de credito y tarjetas de debito como un solo rubro
					importe.setOmitir(Boolean.TRUE);
					this.credito= importe;
					break;
				default:
					break;
			} // switch
		  total+= importe.getImporte();
		} // for
    this.attrs.put("total", Numero.toRedondearSat(total));		
	}
	
  public void doTotales() {
		Double total= 0D;
		for (Importe importe: this.importes) {
			importe.toCalculate();
		  total+= importe.getImporte();
		} // for
    this.attrs.put("total", Numero.toRedondearSat(total));		
	}

  public void doContinuar() {
		this.attrs.put("continuar", "");
	}	
	
	public void doTabChange(TabChangeEvent event) {
		if(event.getTab().getTitle().equals("Retiros/Abonos"))
			this.doLoadRetirosAbonos();
	}

	private void doLoadRetirosAbonos() {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= new HashMap<>();
    try {
      params.put("idCierre", this.attrs.get("idCierre"));
      params.put("sortOrder", "order by tc_mantic_cierres_retiros.id_abono, tc_mantic_cierres_retiros.consecutivo ");
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("concepto", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("usuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("autorizo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("caja", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaCierresCajasDto", "retiros", params, columns);
      UIBackingUtilities.resetDataTable("contenedorGrupos:movimientos");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally		
  } // doLoad

  private void doLoadAmbos() {
		Double abonos = 0D;
		Double retiros= 0D;
		if(this.lazyModel!= null)
			for (IBaseDto item: (List<IBaseDto>)this.lazyModel.getWrappedData()) {
				Entity row= (Entity)item;
				if(row.toLong("idAbono").equals(1L))
					abonos+= new Double(row.toString("importe"));
				else
					retiros+= new Double(row.toString("importe"));
			} // for
	  this.attrs.put("abonos", Global.format(EFormatoDinamicos.MONEDA_SAT_DECIMALES, abonos));
	  this.attrs.put("retiros", Global.format(EFormatoDinamicos.MONEDA_SAT_DECIMALES, retiros));
	}	

  public void doConsultar() {
    this.doDetalle((Importe)this.attrs.get("seleccionado"));
  }
  
  public void doDetalle(Importe row) {
		Map<String, Object>params= new HashMap<>();
		List<Columna>columns     = new ArrayList<>();
		try {
			if(row!= null) {
        this.attrs.put("seleccionado", row);
        params.put("idCierre", row.getIdCierre());
        params.put("idTipoMedioPago", row.getIdTipoMedioPago());
        params.put("sortOrder", "order by tc_mantic_ventas.registro desc");
        columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("ventas", EFormatoDinamicos.MILES_SIN_DECIMALES));
        columns.add(new Columna("saldo", EFormatoDinamicos.MILES_SIN_DECIMALES));
				columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
				this.lazyDetalle= new FormatLazyModel("VistaCierresCajasDto", "desglosado", params, columns);
				UIBackingUtilities.resetDataTable("contenedorGrupos:tablaDetalle");
        this.attrs.put("particular", this.toTotales("VistaCierresCajasDto", "general", params));
        this.doMedios(row);
			} // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
  }

  private Entity toTotales(String proceso, String idXml, Map<String, Object> params) {
    Entity regresar= null;
    try {      
      regresar= (Entity)DaoFactory.getInstance().toEntity(proceso, idXml, params);
      if(Objects.equals(regresar, null) || regresar.isEmpty()) 
        regresar= this.toEmptyTotales();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return regresar;
  }
  
  private Entity toEmptyTotales() {
    Entity regresar= new Entity(-1L);
    regresar.put("cantidad", new Value("cantidad", 0D));
    regresar.put("importe", new Value("importe", 0D));
    regresar.put("ventas", new Value("ventas", 0D));
    return regresar;
  }

	public void doMoveSection(Entity row) {
		List<Columna> columns         = new ArrayList<>();
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> documento= null;
    try {
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cantidad", EFormatoDinamicos.NUMERO_CON_DECIMALES));
      columns.add(new Columna("impuestos", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("precio", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MONEDA_SAT_DECIMALES));
      columns.add(new Columna("fecha", EFormatoDinamicos.FECHA_HORA));
			params.put("idVenta", row.toLong("idVenta"));
			documento= (List<UISelectEntity>) UIEntity.build("VistaKardexDto", "venta", params, columns, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("documentos", documento);
			if(documento!= null && !documento.isEmpty()) {
				documento.get(0).put("articulos", new Value("articulos", documento.size()));
        this.attrs.put("documento", documento.get(0));
			} // if	
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
 
  private void doMedios(Importe row) {
		Map<String, Object>params= new HashMap<>();
		List<Columna>columns     = new ArrayList<>();
		try {
			if(row!= null) {
        this.attrs.put("seleccionado", row);
        params.put("idCierre", row.getIdCierre());
        params.put("idTipoMedioPago", row.getIdTipoMedioPago());
        params.put("sortOrder", "order by tc_mantic_clientes_pagos.registro desc");
        columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
				columns.add(new Columna("fechaPago", EFormatoDinamicos.FECHA_CORTA));
				columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
				this.lazyMedios= new FormatLazyModel("VistaCierresCajasDto", "medios", params, columns);
				UIBackingUtilities.resetDataTable("contenedorGrupos:tablaMedios");
        this.attrs.put("general", this.toTotales("VistaCierresCajasDto", "complicado", params));
			} // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
  }
  
}