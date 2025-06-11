package mx.org.kaana.mantic.lotes.backing;

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
import mx.org.kaana.mantic.db.dto.TcManticLotesBitacoraDto;
import mx.org.kaana.mantic.lotes.reglas.Transaccion;
import mx.org.kaana.mantic.enums.ETipoMovimiento;
import mx.org.kaana.mantic.lotes.beans.Lote;
import mx.org.kaana.mantic.lotes.reglas.AdminLotes;

@Named(value = "manticLotesFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

	private static final long serialVersionUID=1168701967796774746L;
  private FormatLazyModel lazyDetalle;
  private FormatLazyModel lazyMerma;

	public FormatLazyModel getLazyDetalle() {
		return lazyDetalle;
	}		

  public FormatLazyModel getLazyMerma() {
    return lazyMerma;
  }
	
  public String getGeneral() {
    String kilos  = Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("cantidad"));
    return "Suma de kilos: <strong>"+ kilos+ "</strong>";  
  }
  
  public String getParticular() {
    String kilos  = Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("particular")).toDouble("cantidad"));
    return "Suma de kilos: <strong>"+ kilos+ "</strong>";  
  }
  
  public String getPorcentaje() {
    Double total= 0D;
		try {
      if(!Objects.equals(this.lazyMerma, null))
			  for(Entity item: (List<Entity>)this.lazyMerma.getWrappedData())
				  total+= Numero.getDouble(Cadena.eliminar(item.toString("porcentaje"), ','), 0D);
		} // try
		catch (Exception e) {			
			Error.mensaje(e);			
		} // catch		
    return "Total: <strong>"+ Numero.formatear(Numero.MILES_CON_DECIMALES, total)+ "%</strong>";  
  }
  
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("idLote", JsfBase.getFlashAttribute("idLote"));
      this.attrs.put("idArticuloTipo", 4L);
			this.toLoadCatalogos();
      if(this.attrs.get("idLote")!= null) 
			  this.doLoad();
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
      params.put("sortOrder", "order by tc_mantic_lotes.id_empresa, tc_mantic_lotes.id_lote desc");
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaLotesDto", params, columns);
      UIBackingUtilities.resetDataTable();
      this.attrs.put("general", this.toTotales("VistaLotesDto", "general", params));
			this.attrs.put("idLote", null);
      this.lazyDetalle= null;
      this.lazyMerma  = null;
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
		String regresar    = "/Paginas/Mantic/Lotes/accion";
    Entity seleccionado= (Entity)this.attrs.get("seleccionado");
    Long idLoteTipo    = 1L;
    EAccion eaccion    = null;
		try {
      eaccion= EAccion.valueOf(accion.toUpperCase());      
      if(!Objects.equals(eaccion, EAccion.AGREGAR)) {
        switch(seleccionado.toLong("idLoteTipo").intValue()) {
          case 1: // NORMAL
            break;
          case 2: // FRACCIONADO
            idLoteTipo= 2L;
            break;
          case 3:  // POR TIPO DE CLASE DESDE LAS NOTAS DE ENTRADAS
            idLoteTipo= 3L;
            regresar  = "/Paginas/Mantic/Lotes/especial"; 
            break;
          case 4: // POR TIPO DE CLASE DESDE LOS LOTES
            idLoteTipo= 4L;
            regresar  = "/Paginas/Mantic/Lotes/agrupado";
            break;
        } // switch
      } // if  
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("idLoteTipo", idLoteTipo);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Lotes/filtro");		
			JsfBase.setFlashAttribute("idLote", Objects.equals(eaccion, EAccion.MODIFICAR) || Objects.equals(eaccion, EAccion.CONSULTAR)? ((Entity)this.attrs.get("seleccionado")).getKey(): -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR);
  } // doAccion  
	
  public String doEspecial() {
		String regresar= "/Paginas/Mantic/Lotes/especial";
		try {
      JsfBase.setFlashAttribute("accion", EAccion.AGREGAR);		
			JsfBase.setFlashAttribute("idLoteTipo", 3L);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Lotes/filtro");		
			JsfBase.setFlashAttribute("idLote", -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR);
  } 
	
  public String doAgrupado() {
		String regresar= "/Paginas/Mantic/Lotes/agrupado";
		try {
			JsfBase.setFlashAttribute("accion", EAccion.AGREGAR);
			JsfBase.setFlashAttribute("idLoteTipo", 4L);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Lotes/filtro");		
			JsfBase.setFlashAttribute("idLote", -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR);
  } 
	
  public void doEliminar() {
		Transaccion transaccion   = null;
		Entity seleccionado       = null;
    Map<String, Object> params= new HashMap<>();
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
      params.put("idLote", seleccionado.getKey());
      Lote orden= (Lote)DaoFactory.getInstance().toEntity(Lote.class, "TcManticLotesDto", "igual", params);
			transaccion= new Transaccion(orden);
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "El lote se ha eliminado correctamente", ETipoMensaje.ERROR);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar el lote", ETipoMensaje.ERROR);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
    finally {
      Methods.clean(params);
    } // finally
  } // doEliminar

	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
  	StringBuilder sb= new StringBuilder("tc_mantic_articulos.id_articulo_tipo=").append(this.attrs.get("idArticuloTipo")).append(" and ");			
    try {
      // ESTO ES UN PARCHE PARA MOSTRAR SOLO LOS REGISTROS DEL VENDEDOR 31/01/2024
      if(!JsfBase.isEncargado())
        sb.append("(tc_mantic_lotes.id_usuario= ").append(JsfBase.getIdUsuario()).append(") and ");
      
      if(!Cadena.isVacio(this.attrs.get("idAlmacen")) && !this.attrs.get("idAlmacen").toString().equals("-1"))
        sb.append("(tc_mantic_lotes.id_almacen=").append(this.attrs.get("idAlmacen")).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("idLote")) && !this.attrs.get("idLote").toString().equals("-1"))
        sb.append("(tc_mantic_lotes.id_lote=").append(this.attrs.get("idLote")).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("consecutivo")))
        sb.append("(tc_mantic_lotes.consecutivo= ").append(this.attrs.get("consecutivo")).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("notaEntrada")))
        sb.append("(tc_mantic_notas_entradas.consecutivo= '").append(this.attrs.get("notaEntrada")).append("') and ");
      if(!Cadena.isVacio(this.attrs.get("nombre")))
        sb.append("(tc_mantic_lotes.nombre like '%").append(this.attrs.get("nombre")).append("%') and ");
      if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
        sb.append("(date_format(tc_mantic_lotes.registro, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");	
      if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
        sb.append("(date_format(tc_mantic_lotes.registro, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");	
      if(!Cadena.isVacio(this.attrs.get("idProveedor")) && !this.attrs.get("idProveedor").toString().equals("-1"))
        sb.append("(tc_mantic_proveedores.id_proveedor= ").append(this.attrs.get("idProveedor")).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("idLoteEstatus")) && !this.attrs.get("idLoteEstatus").toString().equals("-1"))
        sb.append("(tc_mantic_lotes.id_lote_estatus= ").append(this.attrs.get("idLoteEstatus")).append(") and ");
			if(this.attrs.get("articulo")!= null && ((UISelectEntity)this.attrs.get("articulo")).getKey()> 0L) 
				sb.append("tc_mantic_articulos.id_articulo=").append(((UISelectEntity)this.attrs.get("articulo")).getKey()).append(" and ");						
  		else 
	  		if(!Cadena.isVacio(JsfBase.getParametro("articulo_input"))) { 
					String nombre= JsfBase.getParametro("articulo_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*");
		  		sb.append("(tc_mantic_articulos.nombre regexp '.*").append(nombre).append(".*' or tc_mantic_articulos.descripcion regexp '.*").append(nombre).append(".*') and ");				
				} // if	
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
      List<UISelectEntity> empresas= (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave");
      this.attrs.put("empresas", empresas);
			this.attrs.put("idEmpresa", UIBackingUtilities.toFirstKeySelectEntity(empresas));
      this.attrs.put("proveedores", (List<UISelectEntity>) UIEntity.build("VistaLotesDto", "proveedores", params, columns));
			this.attrs.put("idProveedor", new UISelectEntity("-1"));
			columns.remove(0);
			params.put(Constantes.SQL_CONDICION, "id_lote_estatus in (1, 2, 3, 4, 5, 6, 8, 9)");
      this.attrs.put("catalogo", (List<UISelectEntity>) UIEntity.build("TcManticLotesEstatusDto", "row", params, columns));
			this.attrs.put("idLoteEstatus", new UISelectEntity("-1"));
      this.doLoadAlmacenes();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}
  
	public void doLoadAlmacenes() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
				params.put("sucursales", this.attrs.get("idEmpresa"));
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			List<UISelectEntity> almacenes= (List<UISelectEntity>)UIEntity.seleccione("TcManticAlmacenesDto", "almacenes", params, columns, "clave");
      this.attrs.put("almacenes", almacenes);
			if(almacenes!= null && !almacenes.isEmpty())
				this.attrs.put("idAlmacen", almacenes.get(0));
			else
			  this.attrs.put("idAlmacen", new UISelectEntity("-1"));
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
  
	public void doLoadEstatus() {
		Entity seleccionado          = (Entity)this.attrs.get("seleccionado");
		Map<String, Object>params    = new HashMap<>();
		List<UISelectItem> allEstatus= null;
		try {
			params.put(Constantes.SQL_CONDICION, "id_lote_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcManticLotesEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
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
		TcManticLotesBitacoraDto bitacora= null;
		Entity seleccionado              = null;
    Map<String, Object> params       = new HashMap<>();
		try {
			seleccionado    = (Entity)this.attrs.get("seleccionado");
      AdminLotes orden= new AdminLotes(seleccionado.getKey());
      orden.getLote().toLoadArticulos();
			bitacora= new TcManticLotesBitacoraDto(
        (String)this.attrs.get("justificacion"), // String justificacion, 
        Long.valueOf(this.attrs.get("estatus").toString()), // Long idLoteEstatus, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        seleccionado.getKey(), // Long idLote, 
        -1L // Long idLoteBitacora              
      );
			transaccion= new Transaccion(orden.getLote(), bitacora);
			if(transaccion.ejecutar(EAccion.JUSTIFICAR))
				JsfBase.addMessage("Cambio estatus", "Se realizo el cambio de estatus !", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Cambio estatus", "Ocurrio un error al realizar el cambio", ETipoMensaje.ERROR);
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
	
  public void doConsultar() {
    this.doLoadDetalle((Entity)this.attrs.get("seleccionado"));
  }
  
  public void doLoadDetalle(Entity row) {
		Map<String, Object>params= new HashMap<>();
		List<Columna>columns     = new ArrayList<>();
		try {
			if(row!= null && !row.isEmpty()) {
        this.attrs.put("seleccionado", row);
        params.put("idLote", row.toLong("idLote"));
        params.put("sortOrder", "order by tc_mantic_lotes_detalles.registro");
        columns.add(new Columna("proveedor", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("articulo", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_CON_DECIMALES));
				columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
				this.lazyDetalle= new FormatLazyModel("VistaLotesDto", "detalle", params, columns);
				UIBackingUtilities.resetDataTable("tablaDetalle");
        this.attrs.put("particular", this.toTotales("VistaLotesDto", "particular", params));
        this.toLoadMermas();
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

	public List<UISelectEntity> doCompleteArticulo(String search) {
		List<Columna> columns         = new ArrayList<>();
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> articulos= null;
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);
			if(!Cadena.isVacio(search)) {
  			search= search.replaceAll(Constantes.CLEAN_SQL, "").trim().toUpperCase().replaceAll("(,| |\\t)+", ".*");			
        if(Cadena.isVacio(search))
          search= ".*";
      } // if  
			else
				search= "WXYZ";
  		params.put("codigo", search);			        
  		params.put("idArticuloTipo", this.attrs.get("idArticuloTipo"));
      articulos= (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porNombreTipoArticulo", params, columns, 40L);
      this.attrs.put("articulos", articulos);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("articulos");
	}	// doCompleteArticulo
  
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
    return regresar;
  }

	public String doMovimientos() {
		JsfBase.setFlashAttribute("tipo", ETipoMovimiento.LOTES);
		JsfBase.setFlashAttribute(ETipoMovimiento.LOTES.getIdKey(), ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("regreso", "/Paginas/Mantic/Lotes/filtro");
		return "/Paginas/Mantic/Compras/Ordenes/movimientos".concat(Constantes.REDIRECIONAR);
	}

  public String doPorcentajes() {
		String regresar= "/Paginas/Mantic/Lotes/porcentajes";
    Entity seleccionado= (Entity)this.attrs.get("seleccionado");    
		try {
      JsfBase.setFlashAttribute("accion", Objects.equals(seleccionado.toLong("idLoteEstatus"), 1L)? EAccion.TRANSFORMACION: EAccion.CONSULTAR);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Lotes/filtro");		
			JsfBase.setFlashAttribute("idLote", seleccionado.getKey());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR);
  }

  public String doCalidad() {
		String regresar= "/Paginas/Mantic/Lotes/calidad";
    Entity seleccionado= (Entity)this.attrs.get("seleccionado");
		try {
      JsfBase.setFlashAttribute("accion", Objects.equals(seleccionado.toLong("idLoteEstatus"), 2L)? EAccion.RESTAURAR: EAccion.CONSULTAR);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Lotes/filtro");		
			JsfBase.setFlashAttribute("idLote", seleccionado.getKey());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR);
  }

  public String doTerminado() {
		String regresar    = "/Paginas/Mantic/Lotes/terminado";
    Entity seleccionado= (Entity)this.attrs.get("seleccionado");
		try {
      JsfBase.setFlashAttribute("accion", Objects.equals(seleccionado.toLong("idLoteEstatus"), 3L)? EAccion.COPIAR: EAccion.CONSULTAR);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Lotes/filtro");		
			JsfBase.setFlashAttribute("idLote", seleccionado.getKey());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR);
  }

  public String doFraccionar() {
		String regresar= "/Paginas/Mantic/Lotes/fraccionar";
		try {
      JsfBase.setFlashAttribute("accion", EAccion.GENERAR);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Lotes/filtro");		
			JsfBase.setFlashAttribute("idLote", ((Entity)this.attrs.get("seleccionado")).getKey());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR);
  }

  private void toLoadMermas() {
		List<Columna>columns     = new ArrayList<>();
		Map<String, Object>params= new HashMap<>();
    try {      
      params.put("idLote", ((Entity)this.attrs.get("seleccionado")).getKey());
      params.put("sortOrder", "order by tc_mantic_notas_calidades.id_nota_calidad");
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("porcentaje", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyMerma= new FormatLazyModel("VistaLotesDto", "porcentajes", params, columns);
      UIBackingUtilities.resetDataTable("tablaMerma");
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
  }

}
