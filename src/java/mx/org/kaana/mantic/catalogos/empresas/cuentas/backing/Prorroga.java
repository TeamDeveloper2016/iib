package mx.org.kaana.mantic.catalogos.empresas.cuentas.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.empresas.cuentas.reglas.Transaccion;
import mx.org.kaana.mantic.db.dto.TcManticNotasEntradasDto;
import mx.org.kaana.mantic.inventarios.comun.IBaseImportar;
import org.primefaces.event.TabChangeEvent;

@Named(value = "manticCatalogosEmpresasCuentasProrroga")
@ViewScoped
public class Prorroga extends IBaseImportar implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;	
  
  private Entity deuda;
	private Date prorroga;
	private Date fechaRecepcion;
	private UISelectEntity ikRecibio;
	private UISelectEntity ikProveedorPago;
  private TcManticNotasEntradasDto nota;

  public UISelectEntity getIkRecibio() {
    return ikRecibio;
  }

  public void setIkRecibio(UISelectEntity ikRecibio) {
    this.ikRecibio = ikRecibio;
  }

	public Date getProrroga() {
		return prorroga;
	}

	public void setProrroga(Date prorroga) {
		this.prorroga = prorroga;
	}		

  public Date getFechaRecepcion() {
    return fechaRecepcion;
  }

  public void setFechaRecepcion(Date fechaRecepcion) {
    this.fechaRecepcion = fechaRecepcion;
  }

  public UISelectEntity getIkProveedorPago() {
    return ikProveedorPago;
  }

  public void setIkProveedorPago(UISelectEntity ikProveedorPago) {
    this.ikProveedorPago = ikProveedorPago;
  }

  public TcManticNotasEntradasDto getNota() {
    return nota;
  }

  @PostConstruct
  @Override
  protected void init() {
    try {			
			if(JsfBase.getFlashAttribute("idEmpresaDeuda")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
			this.attrs.put("codigosBarras", JsfBase.getFlashAttribute("codigosBarras"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "/Paginas/Mantic/Catalogos/Empresas/Cuentas/saldos": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("idEmpresaDeuda", JsfBase.getFlashAttribute("idEmpresaDeuda"));     
      this.nota= (TcManticNotasEntradasDto)DaoFactory.getInstance().findById(TcManticNotasEntradasDto.class, (Long)JsfBase.getFlashAttribute("idNotaEntrada"));
			this.attrs.put("xml", ""); 
			this.attrs.put("pdf", ""); 
			this.doLoad();
      this.toLoadCondiciones();
      this.toLoadPersonas();
			this.toLoadTiposPagos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init	
	
  @Override
  public void doLoad() {
		Map<String, Object>params= new HashMap<>();
		try {
			params.put("idEmpresaDeuda", this.attrs.get("idEmpresaDeuda"));			
			params.put("sortOrder", "order by	tc_mantic_empresas_deudas.registro desc");
			this.deuda= (Entity) DaoFactory.getInstance().toEntity("VistaEmpresasDto", "cuentas", params);
			this.prorroga= deuda.toDate("limite");
      if(deuda.toDate("fechaRecepcion")== null)
        this.fechaRecepcion= new Date(Calendar.getInstance().getTimeInMillis());
      else
        this.fechaRecepcion= deuda.toDate("fechaRecepcion");
      this.attrs.put("idRevisado", deuda.toLong("idRevisado"));       
			this.attrs.put("deuda", deuda);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
      JsfBase.addMessageError(e);
		} // catch
		finally{
			Methods.clean(params);
		} // finally	
  }

	public String doRegresar() {	  
		JsfBase.setFlashAttribute("idEmpresaDeuda", this.attrs.get("idEmpresaDeuda"));
		return "saldos".concat(Constantes.REDIRECIONAR);
	} // doRegresar		

  private void toLoadPersonas() {
    List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("materno", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("paterno", EFormatoDinamicos.MAYUSCULAS));
      List<UISelectEntity> personas= UIEntity.seleccione("VistaAlmacenesTransferenciasDto", "solicito", params, columns, "nombres");
      this.attrs.put("personas", personas);
      if(Objects.equals(this.deuda.toLong("idRecibio"), null))
        this.ikRecibio= UIBackingUtilities.toFirstKeySelectEntity(personas);
      else {
        int index= personas.indexOf(new UISelectEntity(this.deuda.toLong("idRecibio")));
        if(index>= 0)
          this.ikRecibio= personas.get(index);
        else
    	    this.ikRecibio= UIBackingUtilities.toFirstKeySelectEntity(personas);
      } // if  
      if(Objects.equals(this.ikRecibio, null) || Objects.equals(this.ikRecibio.getKey(), -1L))
        this.ikRecibio.getValue("nombres").setData("TECLEAR PERSONA");
    } // try
    catch (Exception e) {
      throw e;
    } // catch    
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally 
  }
	  
	public List<UISelectEntity> doCompletePersona(String codigo) {
 		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("paterno", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("materno", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(!Cadena.isVacio(codigo)) {
  			codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim();
				codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*");
			} // if	
			else
				codigo= "WXYZ";
  		params.put(Constantes.SQL_CONDICION, "(upper(concat(tc_mantic_personas.nombres, ' ', ifnull(tc_mantic_personas.paterno, ''), ' ', ifnull(tc_mantic_personas.materno, ''))) regexp '.*".concat(codigo).concat(".*' or upper(tc_mantic_personas.rfc) regexp '.*").concat(codigo).concat(".*')"));
      this.attrs.put("personas", UIEntity.build("VistaAlmacenesTransferenciasDto", "solicito", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
		return (List<UISelectEntity>)this.attrs.get("personas");
	}	
  
	private void toLoadTiposPagos() {
		List<UISelectEntity> tiposPagos= null;
		Map<String, Object>params      = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			tiposPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposPagos", tiposPagos);
			this.attrs.put("tipoPago", UIBackingUtilities.toFirstKeySelectEntity(tiposPagos));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} 
	
	private void toLoadCondiciones() {
		List<UISelectEntity> condiciones= null;
		Map<String, Object>params       = new HashMap<>();
		try {
			params.put("idProveedor", this.deuda.toLong("idProveedor"));
			condiciones= UIEntity.build("VistaOrdenesComprasDto", "condiciones", params);
			this.attrs.put("condiciones", condiciones);
      if(this.deuda.toLong("idProveedorPago")== null) {
        if(this.nota!= null && this.nota.getIdProveedorPago()!= null) {
          int index= condiciones.indexOf(new UISelectEntity(this.nota.getIdProveedorPago()));
          if(index>= 0)
            this.ikProveedorPago= condiciones.get(index);
          else
    	      this.ikProveedorPago= UIBackingUtilities.toFirstKeySelectEntity(condiciones);
        } // if
        else
  	      this.ikProveedorPago= UIBackingUtilities.toFirstKeySelectEntity(condiciones);
      } // if  
      else {
        int index= condiciones.indexOf(new UISelectEntity(this.deuda.toLong("idProveedorPago")));
        if(index>= 0)
          this.ikProveedorPago= condiciones.get(index);
        else
    	    this.ikProveedorPago= UIBackingUtilities.toFirstKeySelectEntity(condiciones);
      } // if  
      this.doCalculateFechaPago();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} 
  
  public void doUpdatePlazo() {
    List<UISelectEntity> condiciones= (List<UISelectEntity>) this.attrs.get("condiciones");
    int index= condiciones.indexOf(this.ikProveedorPago);
    if(index>= 0) {
      this.ikProveedorPago= condiciones.get(index);
      this.doCalculateFechaPago();		
		} // if
		else 
      this.ikProveedorPago= new UISelectEntity(-1L);
	}	  
	
	public void doCalculateFechaPago() {
    Long diasPlazo= 1L;
  	Calendar calendar= Calendar.getInstance();
		calendar.setTimeInMillis(this.fechaRecepcion.getTime());
		if(this.ikProveedorPago!= null && this.ikProveedorPago.size()> 1)
 		  diasPlazo= this.ikProveedorPago.toLong("plazo");
    if(diasPlazo> 0L) {
  		calendar.add(Calendar.DATE, diasPlazo.intValue()- 1);
	  	this.prorroga= new Date(calendar.getTimeInMillis());
		} // if	
	}
  
	public String doAceptar() {
		String regresar        = null;
		Transaccion transaccion= null;
		try {
			if(this.validaImporte()) {
				transaccion= new Transaccion(this.deuda, this.prorroga, this.fechaRecepcion, (Long)this.attrs.get("idRevisado"), this.ikRecibio.getKey(), this.ikProveedorPago.getKey());
				if(transaccion.ejecutar(EAccion.MODIFICAR)) {
					JsfBase.addMessage("Modificar cuenta por pagar", "Se realiz� la modificaci�n de forma correcta", ETipoMensaje.INFORMACION);
					regresar= this.doCancelar();
				} // if
				else
					JsfBase.addMessage("Modificar cuenta por pagar", "Ocurri� un error al realizar la modificaci�n", ETipoMensaje.ERROR);
			} // if
			else
				JsfBase.addMessage("Modificar cuenta por pagar", "El importe tiene que ser mayor a cero", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		return regresar;
	} 
	
  public String doCancelar() {   
    String regresar= ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
 		JsfBase.setFlashAttribute("idEmpresa", this.deuda.toLong("idEmpresa"));
 		JsfBase.setFlashAttribute("idEmpresaDeuda", this.attrs.get("idEmpresaDeuda"));
  	JsfBase.setFlashAttribute("idNotaEntrada", this.deuda.toLong("idNotaEntrada"));
    if(!Objects.equals(this.attrs.get("codigosBarras"), null)) {
    	regresar= (String)this.attrs.get("codigosBarras");    
    	JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Entradas/filtro");
    } // if  
    return regresar;
  } 
  
	private boolean validaImporte() {
		boolean regresar= false;
		Double importe  = null;
		try {
			importe = Numero.toRedondearSat(Double.valueOf(String.valueOf(this.deuda.get("importe"))));
			regresar= importe>= 1D;
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		return regresar;
	} 
	
	public void doTabChange(TabChangeEvent event) {
    Map<String, Object> params = new HashMap<>();
    try {      
      if(event.getTab().getTitle().equals("Archivos")) {
        params.put("idNotaEntrada", this.deuda.toLong("idNotaEntrada"));      
        params.put("idTipoDocumento", 13L);      
 			  this.doLoadImportados("VistaNotasEntradasDto", "importados", params);
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
	}

	public void doViewDocument() {
		this.doViewDocument(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas"));
	}

	public void doViewFile() {
		this.doViewFile(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas"));
	}
	
}