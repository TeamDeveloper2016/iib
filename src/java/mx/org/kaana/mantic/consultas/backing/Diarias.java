package mx.org.kaana.mantic.consultas.backing;

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
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EEstatusVentas;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETipoDocumento;
import mx.org.kaana.mantic.enums.ETipoMediosPago;
import mx.org.kaana.mantic.ventas.comun.IBaseTicket;

@Named(value= "manticConsultasDiarias")
@ViewScoped
public class Diarias extends IBaseTicket implements Serializable {

  private static final long serialVersionUID = 8793667741599428332L;
	private FormatLazyModel detalle;
	private FormatLazyModel lazyCredito;
	private FormatLazyModel lazyApartado;
	private FormatLazyModel lazyDisponible;

	public FormatLazyModel getDetalle() {
		return detalle;
	}	

	public FormatLazyModel getLazyCredito() {
		return lazyCredito;
	}

	public FormatLazyModel getLazyApartado() {
		return lazyApartado;
	}
	
  public FormatLazyModel getLazyDisponible() {
    return lazyDisponible;
  }
  
	public String getCredito() {
		Double sum= 0D;
		if(this.lazyCredito!= null)
			for (IBaseDto item: (List<IBaseDto>)this.lazyCredito.getWrappedData()) {
				Entity row= (Entity)item;
				sum+= row.toDouble("total");
			} // for
	  return Global.format(EFormatoDinamicos.MONEDA_SAT_DECIMALES, sum);
	}
	
	public String getApartado() {
		Double sum= 0D;
		if(this.lazyApartado!= null)
			for (IBaseDto item: (List<IBaseDto>)this.lazyApartado.getWrappedData()) {
				Entity row= (Entity)item;
				sum+= row.toDouble("importe");
			} // for
	  return Global.format(EFormatoDinamicos.MONEDA_SAT_DECIMALES, sum);
	}

	public String getDisponible() {
		Double sum= 0D;
		if(this.lazyDisponible!= null)
			for (IBaseDto item: (List<IBaseDto>)this.lazyDisponible.getWrappedData()) {
				Entity row= (Entity)item;
				sum+= row.toDouble("total");
			} // for
	  return Global.format(EFormatoDinamicos.MONEDA_SAT_DECIMALES, sum);
	}
  
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.attrs.put("idVenta", JsfBase.getFlashAttribute("idVenta"));
      this.attrs.put("sortOrder", "order by tc_mantic_ventas.registro desc");
			this.attrs.put("fechaInicio", new Date(Calendar.getInstance().getTimeInMillis()));
			this.toLoadCatalog();      
      this.toDefaultTotal();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
 
  @Override
  public void doLoad() {
    List<Columna> columns       = new ArrayList<>();
		Map<String, Object> params  = this.toPrepare();
		Map<String, Object> credito = null;
		Map<String, Object> apartado= null;
    try {
      columns.add(new Columna("nombreEmpresa", EFormatoDinamicos.MAYUSCULAS));      
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_SAT_DECIMALES));      
      this.lazyModel= new FormatCustomLazy("VistaConsultasDto", "ventas", params, columns);
      Entity total  = (Entity)DaoFactory.getInstance().toEntity("VistaConsultasDto", "diariasTotales", params);
      if(Objects.equals(total, null) || Objects.equals(total.toDouble("total"), null))
        this.toDefaultTotal();
      else
  			this.attrs.put("total", total);
      UIBackingUtilities.resetDataTable();
			columns.remove(columns.size()- 1);
			apartado= this.toPrepare(EEstatusVentas.APARTADOS);
      this.lazyApartado= new FormatCustomLazy("VistaConsultasDto", "apartado", apartado, columns);
			credito= this.toPrepare(EEstatusVentas.CREDITO);
      columns.add(new Columna("devuelto", EFormatoDinamicos.MONEDA_CON_DECIMALES));
      columns.add(new Columna("cuando", EFormatoDinamicos.FECHA_HORA_CORTA));      
      columns.add(new Columna("fecha", EFormatoDinamicos.FECHA_HORA_CORTA));      
      this.lazyCredito = new FormatCustomLazy("VistaConsultasDto", "credito", credito, columns);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(credito);
      Methods.clean(apartado);
      Methods.clean(columns);
    } // finally		
  } // doLoad

	protected Map<String, Object> toPrepare() {
	  return this.toPrepare(EEstatusVentas.TIMBRADA);
	}
	
	protected Map<String, Object> toPrepare(EEstatusVentas consulta) {
	  Map<String, Object> regresar= new HashMap<>();	
	  Map<String, Object> params  = new HashMap<>();	
		StringBuilder sb= new StringBuilder();		
		StringBuilder sf= new StringBuilder();		
    try {
      sb.append("tc_mantic_ventas.id_tipo_documento=").append(ETipoDocumento.VENTAS_NORMALES.getIdTipoDocumento()).append(" and ");
      switch(consulta) {
        case CREDITO:
          sb.append("tc_mantic_ventas.id_venta_estatus in (").append(EEstatusVentas.CREDITO.getIdEstatusVenta()).append(") and ");
          break;
        case APARTADOS:
          sb.append("tc_mantic_ventas.id_venta_estatus in (").append(EEstatusVentas.APARTADOS.getIdEstatusVenta()).append(") and ");
          break;
        default:
          sb.append("tc_mantic_ventas.id_venta_estatus in (").append(EEstatusVentas.PAGADA.getIdEstatusVenta()).append(",").append(EEstatusVentas.TIMBRADA.getIdEstatusVenta()).append(",").append(EEstatusVentas.TERMINADA.getIdEstatusVenta()).append(") and ");
          break;
      } // switch
      if(!Cadena.isVacio(this.attrs.get("fechaInicio"))) {
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(((Date)this.attrs.get("fechaInicio")).getTime());
        calendar.add(Calendar.DATE, -1);
        params.put("fecha", Fecha.formatear(Fecha.FECHA_ESTANDAR, calendar));
        if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1")) {
          params.put("idEmpresa", this.attrs.get("idEmpresa"));
          Entity corte= (Entity)DaoFactory.getInstance().toEntity("TcManticCierresCajasDto", "corte", params);
          if(corte!= null && !corte.isEmpty()) {
            sb.append("(date_format(tc_mantic_ventas.registro, '%Y%m%d%H%i%S')> '").append(Fecha.formatear(Fecha.FECHA_HORA_LARGA, corte.toTimestamp("corte"))).append("') and ");			
            sb.append("(date_format(tc_mantic_ventas.registro, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");			
          } // if  
          else 
            sb.append("(date_format(tc_mantic_ventas.registro, '%Y%m%d')= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");			
        } // if
        else 
          sb.append("(date_format(tc_mantic_ventas.registro, '%Y%m%d')= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");			
      } // if  
      if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
        regresar.put("idEmpresa", this.attrs.get("idEmpresa"));
      else
        regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
      if(!Cadena.isVacio(this.attrs.get("idCaja")) && !this.attrs.get("idCaja").toString().equals("-1"))
        sf.append("(tc_mantic_cierres_cajas.id_caja= ").append(this.attrs.get("idCaja")).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("idMedioPago")) && !this.attrs.get("idMedioPago").toString().equals("-1"))
        sf.append("(tc_mantic_tipos_medios_pagos.id_tipo_medio_pago= ").append(this.attrs.get("idMedioPago")).append(") and ");
      if(sf.length()== 0)
        regresar.put("apartado", Constantes.SQL_VERDADERO);
      else	
        regresar.put("apartado", sf.substring(0, sf.length()- 4));
      if(sb.length()== 0)
        regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      else	
        regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
    } // try  
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(params);
    }// finally
		return regresar;		
	} // toPrepare
	
	protected void toLoadCatalog() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns));			
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
			this.doLoadCajas();
			this.doLoadMediosPagos();
      columns.add(new Columna("limiteCredito", EFormatoDinamicos.MONEDA_SAT_DECIMALES));
      this.attrs.put("clientes", (List<UISelectEntity>) UIEntity.build("VistaVentasDto", "clientes", params, columns));
			this.attrs.put("idCliente", new UISelectEntity("-1"));
			columns.remove(0);
			columns.remove(1);
      this.attrs.put("estatusFiltro", (List<UISelectEntity>) UIEntity.build("TcManticVentasEstatusDto", "row", params, columns));
			this.attrs.put("idVentaEstatus", new UISelectEntity("-1"));
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // toLoadCatalog
	
	public void doLoadCajas() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			params.put("idEmpresa", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
      this.attrs.put("cajas", (List<UISelectEntity>) UIEntity.build("TcManticCajasDto", "cajas", params, columns));
			this.attrs.put("idCaja", new UISelectEntity("-1"));
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // doLoadCajas
	
	
	private void doLoadMediosPagos(){
		List<UISelectItem> mediosPagos= null;
		try {
			mediosPagos= new ArrayList<>();
			for(ETipoMediosPago record: ETipoMediosPago.values()){
				if(record.isCaja())
					mediosPagos.add(new UISelectItem(record.getIdTipoMedioPago(), record.getNombre()));
			} // for
			mediosPagos.add(0, new UISelectItem(-1L, "TODOS"));
			this.attrs.put("mediosPago", mediosPagos);
			this.attrs.put("idMedioPago", UIBackingUtilities.toFirstKeySelectItem(mediosPagos));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // doLoadMediosPagos
	
	public void doReporte() throws Exception {
		Map<String, Object>params    = new HashMap<>();
		Map<String, Object>parametros= new HashMap<>();
		EReportes reporteSeleccion   = null;
		try{				
			reporteSeleccion= EReportes.ORDEN_COMPRA;
			this.reporte= JsfBase.toReporte();
			params.put("idVenta", ((Entity)this.attrs.get("seleccionado")).getKey());			
			parametros.put("REPORTE_EMPRESA", JsfBase.getAutentifica().getEmpresa().getNombreCorto());
		  parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
			parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
			parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));			
			this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));		
			this.doVerificarReporte();
			this.reporte.doAceptar();			
		} // try
		catch(Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
    } // catch	
	} // doReporte
	
  public void doLoadDisponibleCaja() {
    Entity caja               = (Entity)this.attrs.get("caja");      
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= new HashMap<>();
    try {
			params.put("idCaja", caja.toLong("idCaja"));
			params.put("dia", Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio")));
      columns.add(new Columna("medioPago", EFormatoDinamicos.MAYUSCULAS));      
      columns.add(new Columna("disponible", EFormatoDinamicos.MILES_SAT_DECIMALES));      
      columns.add(new Columna("acumulado", EFormatoDinamicos.MILES_SAT_DECIMALES));      
      columns.add(new Columna("saldo", EFormatoDinamicos.MILES_SAT_DECIMALES));      
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_SAT_DECIMALES));      
      this.lazyDisponible = new FormatCustomLazy("VistaConsultasDto", "disponible", params, columns);
      UIBackingUtilities.resetDataTable();
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

	public String toColor(Entity row) {
		return Cadena.isVacio(row.toString("garantia"))? "": "janal-tr-lime";
	} 

  private void toDefaultTotal() {
		Entity total= new Entity();
    total.put("total", new Value("total", 0L));
    this.attrs.put("total", total);
  }
  
}
