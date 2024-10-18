package mx.org.kaana.mantic.inventarios.entradas.backing;

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
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.formato.Variables;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.inventarios.entradas.reglas.Transaccion;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.db.dto.TcManticNotasBitacoraDto;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETipoMovimiento;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEntrada;
import org.primefaces.context.RequestContext;

@Named(value = "manticInventariosEntradasFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

	private static final long serialVersionUID=1368701967796774746L;
  private Reporte reporte;
  protected FormatLazyModel lazyDetalle;
  protected FormatLazyModel lazyGasto;

	public FormatLazyModel getLazyDetalle() {
		return lazyDetalle;
	}		
	
	public FormatLazyModel getLazyGasto() {
		return lazyGasto;
	}		
	
	public Reporte getReporte() {
		return reporte;
	}	// getReporte
	
  public String getGeneral() {
    String kilos  = Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("cantidad"));
    String importe= Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("importe"));
    String total  = Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("total"));
    return "Suma de kilos: <strong>"+ kilos+ "</strong> | importe: <strong>"+ importe+ "</strong> | total:<strong>"+ total+ "</strong>";  
  }
  
  public String getParticular() {
    String kilos  = Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("particular")).toDouble("cantidad"));
    String importe= Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("particular")).toDouble("importe"));
    String total  = Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("particular")).toDouble("total"));
    return "Suma de kilos: <strong>"+ kilos+ "</strong> | importe: <strong>"+ importe+ "</strong> | total:<strong>"+ total+ "</strong>";  
  }
  
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("idNotaEntrada", JsfBase.getFlashAttribute("idNotaEntrada"));
      this.attrs.put("ordenCompra", JsfBase.getFlashAttribute("ordenCompra"));
			this.toLoadCatalogos();
      if(this.attrs.get("idNotaEntrada")!= null || this.attrs.get("ordenCompra")!= null) {
			  this.doLoad();
        this.attrs.put("ordenCompra", null);
			} // if
      else
        this.attrs.put("general", this.toEmptyTotales());
      this.attrs.put("particular", this.toEmptyTotales());
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
      params.put("sortOrder", "order by tc_mantic_notas_entradas.id_empresa, tc_mantic_notas_entradas.registro desc");
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaNotasEntradasDto", params, columns);
      UIBackingUtilities.resetDataTable();
      this.attrs.put("general", this.toTotales("VistaNotasEntradasDto", "general", params));
			this.attrs.put("idNotaEntrada", null);
      this.lazyDetalle= null;
      this.lazyGasto= null;
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
		String regresar= "/Paginas/Mantic/Inventarios/Entradas/accion";
    EAccion eaccion   = null;
		Long idNotaEntrada= -1L;
		Long idOrdenCompra= -1L;
		Long idNotaTipo   = -1L;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			if(this.attrs.get("seleccionado")!= null) {
			  idNotaEntrada= ((Entity)this.attrs.get("seleccionado")).getKey();
			  idOrdenCompra= ((Entity)this.attrs.get("seleccionado")).toLong("idOrdenCompra");
			  idNotaTipo   = ((Entity)this.attrs.get("seleccionado")).toLong("idNotaTipo");
			} // if
			if(Objects.equals(idNotaTipo, 3L)) {
				regresar= "/Paginas/Mantic/Catalogos/Empresas/Cuentas/accion".concat("?zOyOxDwIvGuCt=zLyOxRwMvAuNt");
				JsfBase.setFlashAttribute("accion", eaccion.equals(EAccion.MODIFICAR)? EAccion.COMPLEMENTAR: EAccion.CONSULTAR);
			} // if	
			else 
        if(Objects.equals(idNotaTipo, 4L)) {
          regresar= "/Paginas/Mantic/Inventarios/Entradas/especial".concat("?zOyOxDwIvGuCt=zLySxPwEvCuItAsEr");
          JsfBase.setFlashAttribute("accion", eaccion.equals(EAccion.COMPLETO)? EAccion.AGREGAR: eaccion);
        } // if	
        else 
          if(Objects.equals(idNotaTipo, 6L)) {
            regresar= "/Paginas/Mantic/Inventarios/Entradas/terminado".concat("?zOyOxDwIvGuCt=zOyExRwMvIuNtAsDrTq");
            JsfBase.setFlashAttribute("accion", eaccion.equals(EAccion.COMPLETO)? EAccion.AGREGAR: eaccion);
          } // if	
          else 
            if(Objects.equals(eaccion, EAccion.ACTIVAR)) {
              regresar= "/Paginas/Mantic/Catalogos/Empresas/Cuentas/accion".concat("?zOyOxDwIvGuCt=zLyOxRwMvAuNt");
              JsfBase.setFlashAttribute("accion", EAccion.COMPLETO);
            } // if
            else {
              if(Objects.equals(eaccion, EAccion.COMPLETO)) 
                JsfBase.setFlashAttribute("accion", EAccion.AGREGAR);		
              else 
                JsfBase.setFlashAttribute("accion", eaccion);		
              if(!Objects.equals(eaccion, EAccion.COMPLETO) || ((Objects.equals(eaccion, EAccion.MODIFICAR) || Objects.equals(eaccion, EAccion.CONSULTAR)) && Objects.equals(idNotaTipo, 2L))) 
                regresar= regresar.concat("?zOyOxDwIvGuCt=zNyLxMwAvCuEtAs");
              else
                regresar= regresar.concat("?zOyOxDwIvGuCt=zLyOxRwMvAuNt");
              JsfBase.setFlashAttribute("idOrdenCompra", (Objects.equals(eaccion, EAccion.MODIFICAR) || Objects.equals(eaccion, EAccion.CONSULTAR) || Objects.equals(idNotaTipo, 2L)) && idOrdenCompra!= null? idOrdenCompra: -1L);
            } // else	
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Entradas/filtro");		
			JsfBase.setFlashAttribute("idNotaEntrada", Objects.equals(eaccion, EAccion.MODIFICAR) || Objects.equals(eaccion, EAccion.CONSULTAR)? idNotaEntrada: -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR_AMPERSON);
  } // doAccion  
	
  public String doEspecial() {
		String regresar= "/Paginas/Mantic/Inventarios/Entradas/especial?zOyOxDwIvGuCt=zLySxPwEvCuItAsEr";
		try {
      JsfBase.setFlashAttribute("accion", EAccion.AGREGAR);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Entradas/filtro");		
			JsfBase.setFlashAttribute("idNotaEntrada", -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR_AMPERSON);
  } 
	
  public String doNormal() {
		String regresar= "/Paginas/Mantic/Inventarios/Entradas/terminado?zOyOxDwIvGuCt=zLySxPwEvCuItAsEr";
		try {
      JsfBase.setFlashAttribute("accion", EAccion.AGREGAR);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Entradas/filtro");		
			JsfBase.setFlashAttribute("idNotaEntrada", -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR_AMPERSON);
  } 
	
  public void doEliminar() {
		Transaccion transaccion = null;
		Entity seleccionado     = null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
      NotaEntrada orden= (NotaEntrada)DaoFactory.getInstance().toEntity(NotaEntrada.class, "TcManticNotasEntradasDto", "igual", Variables.toMap("idNotaEntrada~"+ seleccionado.getKey()));
			transaccion= new Transaccion(orden);
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "La nota de entrada se ha eliminado correctamente", ETipoMensaje.ERROR);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar la nota de entrada", ETipoMensaje.ERROR);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
  } // doEliminar

	protected Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
    StringBuilder sb            = new StringBuilder();
    try {
      // ESTO ES UN PARCHE PARA MOSTRAR SOLO LOS REGISTROS DEL VENDEDOR 31/01/2024
      if(!JsfBase.isEncargado())
        sb.append("(tc_mantic_notas_entradas.id_usuario= ").append(JsfBase.getIdUsuario()).append(") and ");
      
      if(!Cadena.isVacio(this.attrs.get("idNotaEntrada")) && !this.attrs.get("idNotaEntrada").toString().equals("-1"))
        sb.append("(tc_mantic_notas_entradas.id_nota_entrada=").append(this.attrs.get("idNotaEntrada")).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("ordenCompra")))
        sb.append("(tc_mantic_ordenes_compras.id_orden_compra= ").append(this.attrs.get("ordenCompra")).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("consecutivo")))
        sb.append("(tc_mantic_notas_entradas.consecutivo= '").append(this.attrs.get("consecutivo")).append("') and ");
      if(!Cadena.isVacio(this.attrs.get("factura")))
        sb.append("(tc_mantic_notas_entradas.factura like '%").append(this.attrs.get("factura")).append("%') and ");
      if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
        sb.append("(date_format(tc_mantic_notas_entradas.registro, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");	
      if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
        sb.append("(date_format(tc_mantic_notas_entradas.registro, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");	
      if(!Cadena.isVacio(this.attrs.get("idProveedor")) && !this.attrs.get("idProveedor").toString().equals("-1"))
        sb.append("(tc_mantic_proveedores.id_proveedor= ").append(this.attrs.get("idProveedor")).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("idNotaEstatus")) && !this.attrs.get("idNotaEstatus").toString().equals("-1"))
        sb.append("(tc_mantic_notas_entradas.id_nota_estatus= ").append(this.attrs.get("idNotaEstatus")).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
        regresar.put("idEmpresa", this.attrs.get("idEmpresa"));
      else
        regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
      if(sb.length()== 0)
        regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      else	
        regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
    } // try 
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch
		return regresar;
	}
	
	private void toLoadCatalogos() {
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
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns));
			this.attrs.put("idEmpresa", new UISelectEntity("-1"));
      this.attrs.put("proveedores", (List<UISelectEntity>) UIEntity.build("VistaNotasEntradasDto", "proveedores", params, columns));
			this.attrs.put("idProveedor", new UISelectEntity("-1"));
			columns.remove(0);
			params.put(Constantes.SQL_CONDICION, "id_nota_estatus in (1, 3, 4, 5, 7)");
      this.attrs.put("catalogo", (List<UISelectEntity>) UIEntity.build("TcManticNotasEstatusDto", "row", params, columns));
			this.attrs.put("idNotaEstatus", new UISelectEntity("-1"));
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}
  
  public void doReporte(String nombre) throws Exception{
    Parametros comunes           = null;
		Map<String, Object>params    = this.toPrepare();
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;
    Entity seleccionado          = null;
		try{		
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
		else{
			rc.execute("generalHide();");		
			JsfBase.addMessage("Reporte", "No se encontraron registros para el reporte", ETipoMensaje.ERROR);
		} // else
	} // doVerificarReporte		

	public void doLoadEstatus() {
		Entity seleccionado          = (Entity)this.attrs.get("seleccionado");
		Map<String, Object>params    = new HashMap<>();
		List<UISelectItem> allEstatus= null;
		try {
			params.put(Constantes.SQL_CONDICION, "id_nota_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcManticNotasEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
			this.attrs.put("allEstatus", allEstatus);
			this.attrs.put("estatus", allEstatus.get(0));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadEstatus
	
	public void doActualizarEstatus() {
		Transaccion transaccion          = null;
		TcManticNotasBitacoraDto bitacora= null;
		Entity seleccionado              = null;
		Map<String, Object>params        = new HashMap<>();
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
      params.put("idNotaEntrada", seleccionado.getKey());
      NotaEntrada orden= (NotaEntrada)DaoFactory.getInstance().toEntity(NotaEntrada.class, "TcManticNotasEntradasDto", "igual", params);
			bitacora   = new TcManticNotasBitacoraDto(-1L, (String)this.attrs.get("justificacion"), JsfBase.getIdUsuario(), seleccionado.getKey(), Long.valueOf(this.attrs.get("estatus").toString()), orden.getConsecutivo(), orden.getTotal());
			transaccion= new Transaccion(orden, bitacora);
			if(transaccion.ejecutar(EAccion.JUSTIFICAR))
				JsfBase.addMessage("Cambio estatus", "Se realizo el cambio de estatus de forma correcta", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Cambio estatus", "Ocurrio un error al realizar el cambio de estatus", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			this.attrs.put("justificacion", "");
 			Methods.clean(params);
		} // finally
	}	
	
	public String doDevolucion() {
		JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Devoluciones/filtro");		
		JsfBase.setFlashAttribute("idNotaEntrada", ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("accion", EAccion.AGREGAR);
		return "/Paginas/Mantic/Inventarios/Devoluciones/accion".concat(Constantes.REDIRECIONAR);
	}		
	
	public String doDiferencias() {
		JsfBase.setFlashAttribute("idNotaEntrada",((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("idAlmacen",((Entity)this.attrs.get("seleccionado")).get("idAlmacen"));
		JsfBase.setFlashAttribute("idProveedor",((Entity)this.attrs.get("seleccionado")).get("idProveedor"));
		return "diferencias".concat(Constantes.REDIRECIONAR);
	}
	
	public String doImportar() {
		JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Entradas/filtro");		
		JsfBase.setFlashAttribute("idNotaEntrada",((Entity)this.attrs.get("seleccionado")).getKey());
		return "importar".concat(Constantes.REDIRECIONAR);
	}
	
	public String doMovimientos() {
		JsfBase.setFlashAttribute("tipo", ETipoMovimiento.NOTAS_ENTRADAS);
		JsfBase.setFlashAttribute(ETipoMovimiento.NOTAS_ENTRADAS.getIdKey(), ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("regreso", "/Paginas/Mantic/Inventarios/Entradas/filtro");
		return "/Paginas/Mantic/Compras/Ordenes/movimientos".concat(Constantes.REDIRECIONAR);
	}
	
	public String doOrdenCompra() {
		JsfBase.setFlashAttribute("idOrdenCompra", this.attrs.get("idOrdenCompra"));
		return "/Paginas/Mantic/Compras/Ordenes/filtro".concat(Constantes.REDIRECIONAR);
	}
	
	public String doAgregar() {
		JsfBase.setFlashAttribute("accion", EAccion.AGREGAR);
  	JsfBase.setFlashAttribute("idOrdenCompra", ((Value)this.attrs.get("idOrdenCompra")).toLong());
		JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Entradas/filtro");		
		return "/Paginas/Mantic/Inventarios/Entradas/accion?zOyOxDwIvGuCt=zNyLxMwAvCuEtAs".concat(Constantes.REDIRECIONAR_AMPERSON);
	}

	public String doDevoluciones() {
		JsfBase.setFlashAttribute("notaEntrada", this.attrs.get("notaEntrada"));
		JsfBase.setFlashAttribute("idDevolucion", null);
		return "/Paginas/Mantic/Inventarios/Devoluciones/filtro".concat(Constantes.REDIRECIONAR);
	}		

	public String doCreditosNotas() {
		JsfBase.setFlashAttribute("idTipoCreditoNota", 2L);
		JsfBase.setFlashAttribute("idNotaEntrada", ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Creditos/filtro");
		JsfBase.setFlashAttribute("accion", EAccion.AGREGAR);
		return "/Paginas/Mantic/Inventarios/Creditos/accion".concat(Constantes.REDIRECIONAR);
	}
	
	public void doAssignNota(Long idNotaEntrada) {
		this.attrs.put("idNotaEntrada", idNotaEntrada);
	}
	
  public void doConsultar() {
    this.doDetalle((Entity)this.attrs.get("seleccionado"));
  }
  
  public void doDetalle(Entity row) {
		Map<String, Object>params= new HashMap<>();
		List<Columna>columns     = new ArrayList<>();
		try {
			if(row!= null && !row.isEmpty()) {
        this.attrs.put("seleccionado", row);
        params.put("idNotaEntrada", row.toLong("idNotaEntrada"));
        params.put("sortOrder", "order by tc_mantic_notas_detalles.registro");
        columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("promedio", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("gastos", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
				columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
				this.lazyDetalle= new FormatLazyModel("TcManticNotasDetallesDto", "igual", params, columns);
				UIBackingUtilities.resetDataTable("tablaDetalle");
        this.attrs.put("particular", this.toTotales("TcManticNotasDetallesDto", "particular", params));
        this.toGasto(row);
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

  public void toGasto(Entity row) {
		Map<String, Object>params= new HashMap<>();
		List<Columna>columns     = new ArrayList<>();
		try {
			if(row!= null && !row.isEmpty()) {
        this.attrs.put("seleccionado", row);
        params.put("idNotaEntrada", row.toLong("idNotaEntrada"));
        params.put("sortOrder", "order by tc_mantic_notas_costos.registro");
        columns.add(new Columna("articulo", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("proveedor", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
				columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
				this.lazyGasto= new FormatLazyModel("VistaNotasEntradasDto", "costos", params, columns);
				UIBackingUtilities.resetDataTable("tablaGasto");
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

  protected Entity toTotales(String proceso, String idXml, Map<String, Object> params) {
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
    regresar.put("total", new Value("total", 0D));
    return regresar;
  }
  
  public String doPorcentajes() {
		String regresar= "/Paginas/Mantic/Inventarios/Origenes/calidad";
		try {
      JsfBase.setFlashAttribute("accion", EAccion.TRANSFORMACION);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Entradas/filtro");		
			JsfBase.setFlashAttribute("idNotaEntrada", ((Entity)this.attrs.get("seleccionado")).getKey());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR);
  }
  
}
