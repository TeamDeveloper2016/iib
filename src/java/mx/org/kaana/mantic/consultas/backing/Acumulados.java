package mx.org.kaana.mantic.consultas.backing;

import java.io.Serializable;
import java.sql.Date;
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
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.consultas.reglas.UtilidadArticulosLazy;

@Named(value = "manticConsultasAcumulados")
@ViewScoped
public class Acumulados extends Articulos implements Serializable {

  private static final long serialVersionUID = 8793667741599428819L;

  private FormatLazyModel lazyDetalle;

	public FormatLazyModel getLazyDetalle() {
		return lazyDetalle;
	}		

  public String getGeneral() {
    String kilos= Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("cantidad"));
    String total= Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("importe"));
    return "Suma de kilos: <strong>"+ kilos+ "</strong>    importe: <strong>"+ total+ "</strong>";  
  }
  
  public String getParticular() {
    String kilos= Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("particular")).toDouble("cantidad"));
    String total= Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("particular")).toDouble("importe"));
    return "Suma de kilos: <strong>"+ kilos+ "</strong>    importe: <strong>"+ total+ "</strong>";  
  }
  
  @PostConstruct
  @Override
  protected void init() {
    super.init();    
    this.attrs.put("general", this.toEmptyTotales());
    this.attrs.put("particular", this.toEmptyTotales());
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= super.toPrepare();
    try {
      params.put("sortOrder", "order by tc_mantic_articulos.nombre");
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("precios", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("ventas", EFormatoDinamicos.MILES_SIN_DECIMALES));
      params.put("sortOrder", "order by tc_mantic_articulos.nombre");
      this.lazyModel = new UtilidadArticulosLazy("VistaConsultasDto", "acumulados", params, columns);
      this.attrs.put("general", this.toTotales("VistaConsultasDto", "general", params));
      UIBackingUtilities.resetDataTable();
      this.lazyDetalle= null;
    } // try // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
    } // finally		
  } 

  public void doConsultar() {
    this.doDetalle((Entity)this.attrs.get("seleccionado"));
  }
  
  public void doDetalle(Entity row) {
		Map<String, Object>params= null;
		List<Columna>columns     = new ArrayList<>();
		try {
			if(row!= null && !row.isEmpty()) {
        this.attrs.put("seleccionado", row);
        params= this.toDetalle(row);
        params.put("sortOrder", "order by tc_mantic_ventas.registro desc");
				columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("precios", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("ventas", EFormatoDinamicos.MILES_SIN_DECIMALES));
				columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
				this.lazyDetalle= new FormatLazyModel("VistaConsultasDto", "registro", params, columns);
				UIBackingUtilities.resetDataTable("tablaDetalle");
        this.attrs.put("particular", this.toTotales("VistaConsultasDto", "particular", params));
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
 
	protected Map<String, Object> toDetalle(Entity row) {
		Map<String, Object> regresar= new HashMap<>();
		StringBuilder sb            = null;
		try {
			sb= new StringBuilder("tc_mantic_articulos.id_articulo_tipo=").append(this.attrs.get("idTipoArticulo")).append(" and ");						
			if(!Cadena.isVacio(this.attrs.get("codigo")))
				sb.append("upper(tc_mantic_articulos_codigos.codigo) like upper('%").append(this.attrs.get("codigo")).append("%') and ");						
			if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
				sb.append("(date_format(tc_mantic_ventas_detalles.registro, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");
			if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
				sb.append("(date_format(tc_mantic_ventas_detalles.registro, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");
			if(!Cadena.isVacio(this.attrs.get("montoInicio")))
				sb.append("(tc_mantic_ventas_detalles.precio>= ").append((Double)this.attrs.get("montoInicio")).append(") and ");			
			if(!Cadena.isVacio(this.attrs.get("montoTermino")))
				sb.append("(tc_mantic_ventas_detalles.precio<= ").append((Double)this.attrs.get("montoTermino")).append(") and ");			
  		sb.append("tc_mantic_articulos.id_articulo=").append(row.toLong("idArticulo")).append(" and ");
		  sb.append("tc_mantic_articulos.id_vigente=").append(this.attrs.get("idVigente")).append(" and ");
		  if(!Cadena.isVacio(this.attrs.get("idVentaEstatus")) && !Objects.equals(((UISelectEntity)this.attrs.get("idVentaEstatus")).getKey(), -1L))
  		  sb.append("(tc_mantic_ventas.id_venta_estatus= ").append(((UISelectEntity)this.attrs.get("idVentaEstatus")).getKey()).append(") and ");
			if(this.attrs.get("vendedor")!= null && ((UISelectEntity)this.attrs.get("vendedor")).getKey()> 0L) 
				sb.append("tc_mantic_personas.id_persona=").append(((UISelectEntity)this.attrs.get("vendedor")).getKey()).append(" and ");						
  		else 
	  		if(!Cadena.isVacio(JsfBase.getParametro("vendedor_input"))) { 
					String nombre= JsfBase.getParametro("vendedor_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*");
		  		sb.append("(upper(concat(tc_mantic_personas.nombres, ' ', ifnull(tc_mantic_personas.paterno, ''), ' ', ifnull(tc_mantic_personas.materno, ''))) regexp '.*").append(nombre).append(".*') and ");
				} // if	
		  if(!Cadena.isVacio(this.attrs.get("idAlmacen")) && !Objects.equals(((UISelectEntity)this.attrs.get("idAlmacen")).getKey(), -1L))
  		  regresar.put("almacen", " and (tc_mantic_almacenes_articulos.id_almacen= "+ ((UISelectEntity)this.attrs.get("idAlmacen")).getKey()+ ")");
			else
  		  regresar.put("almacen", " ");
			if(Cadena.isVacio(sb.toString()))
				regresar.put("condicion", Constantes.SQL_VERDADERO);
			else
			  regresar.put("condicion", sb.substring(0, sb.length()- 4));			
      regresar.put("todas", Constantes.SQL_FALSO);
		  if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !Objects.equals(((UISelectEntity)this.attrs.get("idEmpresa")).getKey(), -1L))
			  regresar.put("idEmpresa", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
      else {
			  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getDependencias());
        regresar.put("todas", Constantes.SQL_VERDADERO);
      } // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
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
  
}