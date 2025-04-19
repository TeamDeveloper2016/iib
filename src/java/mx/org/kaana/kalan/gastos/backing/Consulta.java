package mx.org.kaana.kalan.gastos.backing;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletContext;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.kalan.gastos.reglas.Consolidado;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EReportes;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "kalanGastosConsulta")
@ViewScoped
public class Consulta extends IBaseFilter implements Serializable {

	private static final long serialVersionUID=1361701967796774746L;
  
  private List<Entity> gastos;
  private Reporte reporte;

  public List<Entity> getGastos() {
    return gastos;
  }
	
	public Reporte getReporte() {
		return reporte;
	}	

	public StreamedContent getArchivo() {
		StreamedContent regresar= null;		
    Consolidado consolidado = null;
		try {
	  	consolidado= new Consolidado(((UISelectEntity)this.attrs.get("idEmpresa")).getKey(), (Long)this.attrs.get("ejercicio"), (Long)this.attrs.get("idMes"));
      String name= consolidado.execute();
      String contentType= EFormatos.XLS.getContent();
      InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(EFormatos.XLS.toPath().concat(name));  
      regresar          = new DefaultStreamedContent(stream, contentType, name);				
		} // try 
		catch (Exception e) {
			Error.mensaje(e);
		} // catch		
    return regresar;		
  }	
  
  public String getTotal() {
    double sum  = 0D;
    String value= null;
		try {
      if(!Objects.equals(this.lazyModel, null))
        for(Entity item: (List<Entity>)this.lazyModel.getWrappedData()) {
          value= item.toString("total");
          sum= sum+ Double.valueOf(Cadena.eliminar(value, ','));
        } // for  
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return Numero.formatear(Numero.MONEDA_CON_DECIMALES, Numero.toRedondearSat(sum));
  }
  
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("mesActual", "Mes actual");
      this.attrs.put("mesAnterior", "Mes anterior");
			this.toLoadCatalogos();
		  this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch	
  } // init
 
  @Override
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= this.toPrepare();
    try {
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clasificacion", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("actual", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("anterior", EFormatoDinamicos.MILES_CON_DECIMALES));
      this.gastos = DaoFactory.getInstance().toEntitySet("VistaEmpresasGastosDto", "acumulados", params);
      if(this.gastos!= null && !this.gastos.isEmpty()) {
        Entity pivot  = null;
        Double actual = 0D; Double  anterior= 0D;
        Double tactual= 0D; Double tanterior= 0D;
        for (Entity item: this.gastos) {
          actual  += item.toDouble("actual");
          anterior+= item.toDouble("anterior");
          tactual  += item.toDouble("actual");
          tanterior+= item.toDouble("anterior");
          pivot= item;
        } // for
        Entity subtotal= pivot.clone();
        subtotal.get("idKey").setData(997L);
        subtotal.get("clasificacion").setData("SUB TOTAL");
        subtotal.put("actual", new Value("actual", actual));
        subtotal.put("anterior", new Value("anterior", anterior));
        this.gastos.add(subtotal);
        Entity ingresos= (Entity)DaoFactory.getInstance().toEntity("VistaEmpresasMovimientosDto", "acumulados", params);
        if(ingresos== null || ingresos.isEmpty()) {
          ingresos= pivot.clone();
          ingresos.get("idKey").setData(998L);
          ingresos.get("clasificacion").setData("INGRESOS VARIOS");
          ingresos.put("actual", new Value("actual", 0D));
          ingresos.put("anterior", new Value("anterior", 0D));
        } // if  
        this.gastos.add(ingresos);
        Entity total= pivot.clone();
        total.get("idKey").setData(999L);
        total.get("empresa").setData("");
        total.get("clasificacion").setData("SALDO");
        total.put("actual", new Value("actual", tactual- ingresos.toDouble("actual")));
        total.put("anterior", new Value("anterior", tanterior- ingresos.toDouble("anterior")));
        this.gastos.add(total);
        UIBackingUtilities.toFormatEntitySet(this.gastos, columns);
      } // if  
      UIBackingUtilities.resetDataTable();
      this.lazyModel= null;
      this.doChangeMes();
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

  public String doAccion(String accion) {
		String regresar= "/Paginas/Kalan/Gastos/accion";
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Kalan/Gastos/filtro");		
			JsfBase.setFlashAttribute("idEmpresaGasto", -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR_AMPERSON);
  } 
	
	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
    if(!Objects.equals(this.attrs.get("ejercicio"), null)) {
      Long year = (Long)this.attrs.get("ejercicio");
      Long month= (Long)this.attrs.get("idMes");
      Calendar calendar= new GregorianCalendar(year== null || year== -1L? Fecha.getAnioActual(): year.intValue(), month== null || month== -1L? Fecha.getMesActual(): month.intValue()- 1, 1);
      int actual= calendar.get(Calendar.MONTH)+ 1;
      regresar.put("actual", calendar.get(Calendar.YEAR)+ Constantes.SEPARADOR+ (actual< 10? "0": "")+ actual);

      calendar.add(Calendar.MONTH, -1);
      int anterior= calendar.get(Calendar.MONTH)+ 1;
      regresar.put("anterior", calendar.get(Calendar.YEAR)+ Constantes.SEPARADOR+ (anterior< 10? "0": "")+ anterior);
    } // if  
    else {
      regresar.put("actual", "9999|99");
      regresar.put("anterior", "9999|99");
    } // else
    if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
      regresar.put("idEmpresa", this.attrs.get("idEmpresa"));
    else
      regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
		return regresar;
	}
	
	private void toLoadCatalogos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      List<UISelectEntity> empresas= (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns);
      this.attrs.put("empresas", empresas);
			this.attrs.put("idEmpresa", UIBackingUtilities.toFirstKeySelectEntity(empresas));
      this.toLoadEjercios();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}

  private void toLoadEjercios() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> ejercicios= UISelect.build("TcKalanEmpresasGastosDto", "ejercicios", params, "idKey|ejercicio", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("ejercicios", ejercicios);
      if(ejercicios!= null && !ejercicios.isEmpty()) {
        int index= ejercicios.indexOf(new UISelectItem(new Long(Fecha.getAnioActual())));
        if(index>= 0)
          this.attrs.put("ejercicio", ejercicios.get(index).getValue());
        else
          this.attrs.put("ejercicio", ejercicios.get(0).getValue());
      } // if  
      else
        this.attrs.put("ejercicio", new Long(Fecha.getAnioActual()));
      this.doLoadMeses();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
      throw e;
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  

  public void doLoadMeses() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put("ejercicio", this.attrs.get("ejercicio"));
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> meses= UISelect.build("TcKalanEmpresasGastosDto", "meses", params, "idKey|nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("meses", meses);
      if(meses!= null && !meses.isEmpty()) {
        int index= meses.indexOf(new UISelectItem(new Long(Fecha.getMesActual())));
        if(index>= 0)
          this.attrs.put("idMes", meses.get(index).getValue());
        else
          this.attrs.put("idMes", meses.get(0).getValue());
      } // if  
      else
        this.attrs.put("idMes", new Long(Fecha.getMesActual()));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
      throw e;
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }
  
  public void doReporte(String nombre) throws Exception{
    Parametros comunes           = null;
		Map<String, Object>params    = null;
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;
    Entity seleccionado          = null;
		try{		
      params= this.toPrepare();
      seleccionado = ((Entity)this.attrs.get("seleccionado"));
      params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());	
      params.put("sortOrder", "order by tc_mantic_notas_entradas.id_empresa, tc_mantic_notas_entradas.ejercicio, tc_mantic_notas_entradas.orden");
      reporteSeleccion= EReportes.valueOf(nombre);
      if(!reporteSeleccion.equals(EReportes.NOTAS_ENTRADA)){
        params.put("idNotaEntrada", seleccionado.getKey());	
        comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), seleccionado.toLong("idAlmacen"), seleccionado.toLong("idProveedor"), -1L);
      }
      else
        comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.reporte= JsfBase.toReporte();	
      parametros= comunes.getComunes();
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
	
	public void doVerificarReporte() {
		RequestContext rc= UIBackingUtilities.getCurrentInstance();
		if(this.reporte.getTotal()> 0L)
			rc.execute("start(" + this.reporte.getTotal() + ")");		
    else {
			rc.execute("generalHide();");		
			JsfBase.addMessage("Reporte", "No se encontraron registros para el reporte", ETipoMensaje.ERROR);
		} // else
	} 

  public void doView(Entity row, String fecha) {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= this.toPrepare();
    try {
      params.put("fecha", fecha);
      params.put("idGastoClasificacion", row.toLong("idGastoClasificacion"));
      params.put("sortOrder", "order by consecutivo desc");
      columns.add(new Columna("clasificacion", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("subclasificacion", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("fechaAplicacion", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));
      // ESTA ES LA CONSULTA PARA DETERMINAR EL DETALLE DE LOS INGRESOS DEL MES SELECCIONADO
      if(Objects.equals(row.toLong("idKey"), 998L))
        this.lazyModel= new FormatCustomLazy("VistaEmpresasMovimientosDto", "detalle", params, columns);
      else
        this.lazyModel= new FormatCustomLazy("VistaEmpresasGastosDto", "detalle", params, columns);
      int mes= 0;
      if(!Objects.equals(fecha, null) && fecha.length()> 0 && fecha.indexOf(Constantes.SEPARADOR)> 0)
        mes= Integer.valueOf(fecha.substring(fecha.indexOf(Constantes.SEPARADOR)+ 1));
      this.attrs.put("titulo", row.toString("clasificacion"));
      this.attrs.put("mes", Fecha.getNombreMes(mes- 1).toUpperCase());
      this.attrs.put("viewColumn", !Objects.equals(row.toLong("idKey"), 998L));
      UIBackingUtilities.resetDataTable("detalle");
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

  public void doChangeMes() {
    String actual           = "...";
    String anterior         = "...";
    try {
      if(!Objects.equals(this.attrs.get("ejercicio"), null)) {
        String ejercicio= Objects.equals(JsfBase.getParametro("ejercicio_input"), null)? String.valueOf(Fecha.getAnioActual()): JsfBase.getParametro("ejercicio_input");
        Long month      = (Long)this.attrs.get("idMes");      
        if(month> 0) {
          actual  = Fecha.getNombreMesCorto(month.intValue()- 1);
          anterior= Fecha.getNombreMesCorto(Objects.equals(month, 1L)? 11: month.intValue()- 2);
        } // if  
        this.attrs.put("mesActual", ejercicio.concat("-").concat(actual));
        this.attrs.put("mesAnterior", ejercicio.concat("-").concat(anterior));
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch      
  }
  
  public String doColor(Entity row) {
    return Objects.equals(row.toLong("idKey"), 997L)? "janal-tr-diferencias": Objects.equals(row.toLong("idKey"), 999L)? "janal-tr-yellow": "";
  }
  
}
