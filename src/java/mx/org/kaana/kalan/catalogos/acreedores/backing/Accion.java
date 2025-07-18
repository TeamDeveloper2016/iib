package mx.org.kaana.kalan.catalogos.acreedores.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.clientes.beans.Domicilio;
import mx.org.kaana.mantic.catalogos.clientes.reglas.MotorBusqueda;
import mx.org.kaana.kalan.catalogos.acreedores.beans.AcreedorContactoAgente;
import mx.org.kaana.kalan.catalogos.acreedores.beans.RegistroAcreedor;
import mx.org.kaana.kalan.catalogos.acreedores.reglas.Transaccion;
import mx.org.kaana.mantic.db.dto.TcManticDomiciliosDto;
import mx.org.kaana.mantic.enums.ETiposContactos;
import mx.org.kaana.mantic.enums.ETiposDomicilios;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;

@Named(value = "kalanCatalogosAcreedoresAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639361L;
  private static final Log LOG = LogFactory.getLog(Accion.class);
  
  private RegistroAcreedor registroAcreedor;
	private UISelectEntity domicilioBusqueda;

	public RegistroAcreedor getRegistroAcreedor() {
		return registroAcreedor;
	}

	public void setRegistroAcreedor(RegistroAcreedor registroAcreedor) {
		this.registroAcreedor = registroAcreedor;
	}

	public UISelectEntity getDomicilioBusqueda() {
		return domicilioBusqueda;
	}

	public void setDomicilioBusqueda(UISelectEntity domicilioBusqueda) {
		this.domicilioBusqueda = domicilioBusqueda;
	}	
	
  @PostConstruct
  @Override
  public void init() {
    try {
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idAcreedor", JsfBase.getFlashAttribute("idAcreedor"));
			this.attrs.put("admin", JsfBase.isAdminEncuestaOrAdmin());
      this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "/Paginas/Kalan/Catalogos/Acreedores/filtro": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("cpNuevo", Boolean.FALSE);
      this.doLoad(); 
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
  
	private void toLoadCollections() {
		this.loadBancos();
		this.loadTiposAcreedores();
		this.loadTiposContactos();
		this.loadTiposDomicilios();	
		this.loadDomicilios();
		this.loadEntidades();
		this.toAsignaEntidad();
		this.loadMunicipios();
		this.toAsignaMunicipio();
		this.loadLocalidades();
		this.toAsignaLocalidad();
	}
	
  public void doLoad() {
    EAccion eaccion= null;
    Long idAcreedor= -1L;
    try {
      eaccion = (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:
          this.registroAcreedor = new RegistroAcreedor();
					this.toLoadCollections();
          break;
        case MODIFICAR:
        case CONSULTAR:
          this.attrs.put("cpNuevo", Boolean.TRUE);
          idAcreedor = Long.valueOf(this.attrs.get("idAcreedor").toString());
          this.registroAcreedor = new RegistroAcreedor(idAcreedor);
					this.toLoadCollections();
					this.doCompleteCodigo(this.registroAcreedor.getDomicilio().getCodigoPostal());
					this.asignaCodigoPostal();
					if(!this.registroAcreedor.getAcreedoresDomicilio().isEmpty()) {
						this.registroAcreedor.setAcreedorDomicilioSeleccion(this.registroAcreedor.getAcreedoresDomicilio().get(0));
						this.doConsultarProveedorDomicilio();
					} // if
					if(!this.registroAcreedor.getPersonasTiposContacto().isEmpty()) {
						this.registroAcreedor.setPersonaTipoContacto(this.registroAcreedor.getPersonasTiposContacto().get(0));
						this.registroAcreedor.doConsultarAgente();
					} // if
          this.attrs.put("tipoAcreedor", new UISelectEntity(this.registroAcreedor.getAcreedor().getIdTipoAcreedor()));
          break;
      } // switch      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 

	public String doAceptar() {
    Transaccion transaccion= null;
    String regresar        = null;
    try {
      if(this.registroAcreedor.getAcreedorDomicilioSeleccion()!= null)
        this.registroAcreedor.toUpdateAcreedorPivote(this.registroAcreedor.getAcreedorDomicilioSeleccion(), Boolean.TRUE);
      transaccion= new Transaccion(this.registroAcreedor);
			this.registroAcreedor.getAcreedor().setIdTipoAcreedor(((UISelectEntity)this.attrs.get("tipoAcreedor")).getKey());
      if (transaccion.ejecutar((EAccion) this.attrs.get("accion"))) {
        regresar= this.doCancelar();
        JsfBase.addMessage("Se registro el acreedor de forma correcta", ETipoMensaje.INFORMACION);
      } // if
      else 
        JsfBase.addMessage("Ocurrió un error al registrar el acreedor", ETipoMensaje.ERROR);      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } 

  public String doCancelar() {
		JsfBase.setFlashAttribute("idAcreedorProcess", this.registroAcreedor.getAcreedor().getIdAcreedor());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } 
	
	private void loadTiposContactos() {
    List<UISelectItem> tiposContactos= new ArrayList<>();
    try {
      for (ETiposContactos tipoContacto : ETiposContactos.values()) 
        tiposContactos.add(new UISelectItem(tipoContacto.getKey(), Cadena.reemplazarCaracter(tipoContacto.name(), '_', ' ')));      
      this.attrs.put("tiposContactos", tiposContactos);
    } // try
    catch (Exception e) {
      throw e;
    } // catch		    
  } // loadTiposContactos

  private void loadTiposDomicilios() {
    List<UISelectItem> tiposDomicilios = new ArrayList<>();
    try {
      for (ETiposDomicilios tipoDomicilio : ETiposDomicilios.values()) 
        tiposDomicilios.add(new UISelectItem(tipoDomicilio.getKey(), Cadena.reemplazarCaracter(tipoDomicilio.name(), '_', ' ')));      
      this.attrs.put("tiposDomicilios", tiposDomicilios);
    } // try
    catch (Exception e) {
      throw e;
    } // catch		    
  } 
	
	private void loadTiposAcreedores() {
		List<UISelectEntity> tiposAcreedores= null;
    List<Columna> formatos               = new ArrayList<>();
    Map<String, Object> params           = new HashMap();
    try {
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      formatos.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      formatos.add(new Columna("dias", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
      tiposAcreedores= UIEntity.build("TcManticTiposAcreedoresDto", params, formatos);
			this.attrs.put("tiposAcreedores", tiposAcreedores);
    } // try
    catch (Exception e) {
      throw e;
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(formatos);
    } // finally
	} 
	
	private void loadEntidades() {
    List<UISelectEntity> entidades= null;
		List<Columna>columns      = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      params.put("idPais", 1);
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      entidades = UIEntity.build("TcJanalEntidadesDto", "comboEntidades", params, columns, Constantes.SQL_TODOS_REGISTROS);
      this.attrs.put("entidades", entidades);
      this.registroAcreedor.getDomicilio().setIdEntidad(entidades.get(0));
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // loadEntidades
	
	private void toAsignaEntidad() {
		Entity domicilio     = null;
		List<Entity>entidades= null;
		try {
			if(!this.registroAcreedor.getDomicilio().getIdDomicilio().equals(-1L)){
				domicilio= this.registroAcreedor.getDomicilio().getDomicilio();
				entidades= (List<Entity>) this.attrs.get("entidades");
				for(Entity entidad: entidades){
					if(entidad.getKey().equals(domicilio.toLong("idEntidad")))
						this.registroAcreedor.getDomicilio().setIdEntidad(entidad);
				} // for
			} // if
			else
				this.registroAcreedor.getDomicilio().setIdEntidad(new Entity(-1L));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toAsignaEntidad

  private void loadMunicipios() {
    List<UISelectEntity> municipios= null;
    Map<String, Object> params= new HashMap<>();
		List<Columna>columns      = new ArrayList<>();
    try {
			if(!this.registroAcreedor.getDomicilio().getIdEntidad().getKey().equals(-1L)){
				params.put("idEntidad", this.registroAcreedor.getDomicilio().getIdEntidad().getKey());
				columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
				municipios = UIEntity.build("TcJanalMunicipiosDto", "comboMunicipios", params, columns, Constantes.SQL_TODOS_REGISTROS);
				this.attrs.put("municipios", municipios);
				this.registroAcreedor.getDomicilio().setIdMunicipio(municipios.get(0));
			} // if
			else
				this.registroAcreedor.getDomicilio().setIdMunicipio(new Entity(-1L));
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } 
	
	private void toAsignaMunicipio() {
		Entity domicilio      = null;
		List<Entity>municipios= null;
		try {
			if(!this.registroAcreedor.getDomicilio().getIdMunicipio().getKey().equals(-1L)){
				domicilio= this.registroAcreedor.getDomicilio().getDomicilio();
				municipios= (List<Entity>) this.attrs.get("municipios");
				for(Entity municipio: municipios){
					if(municipio.getKey().equals(domicilio.toLong("idMunicipio")))
						this.registroAcreedor.getDomicilio().setIdMunicipio(municipio);
				} // for
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toAsignaMunicipio

  private void loadLocalidades() {
    List<UISelectEntity> localidades= null;
    Map<String, Object> params= new HashMap<>();
		List<Columna>columns      = new ArrayList<>();
    try {
			if(!this.registroAcreedor.getDomicilio().getIdMunicipio().getKey().equals(-1L)){
				params.put("idMunicipio", this.registroAcreedor.getDomicilio().getIdMunicipio().getKey());
				columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
				localidades = UIEntity.build("TcJanalLocalidadesDto", "comboLocalidades", params, columns, Constantes.SQL_TODOS_REGISTROS);
				this.attrs.put("localidades", localidades);
				this.registroAcreedor.getDomicilio().setLocalidad(localidades.get(0));
				this.registroAcreedor.getDomicilio().setIdLocalidad(localidades.get(0).getKey());
			} // if
			else{
				this.registroAcreedor.getDomicilio().setLocalidad(new Entity(-1L));
				this.registroAcreedor.getDomicilio().setIdLocalidad(-1L);
			} // else
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
  } 
	
	private void toAsignaLocalidad() {
		Entity domicilio       = null;
		List<Entity>localidades= null;
		try {
			if(!this.registroAcreedor.getDomicilio().getIdDomicilio().equals(-1L)){
				domicilio= this.registroAcreedor.getDomicilio().getDomicilio();
				localidades= (List<Entity>) this.attrs.get("localidades");
				for(Entity localidad: localidades){
					if(localidad.getKey().equals(domicilio.toLong("idLocalidad"))){
						this.registroAcreedor.getDomicilio().setIdLocalidad(localidad.getKey());
						this.registroAcreedor.getDomicilio().setLocalidad(localidad);
					} // if
				} // for
			} // if			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} 

  private void loadDomicilios() {
		List<UISelectEntity> domicilios= new ArrayList<>();
		try {
			this.attrs.put("domicilios", domicilios);     
			this.registroAcreedor.getDomicilio().setDomicilio(new Entity(-1L));
      this.registroAcreedor.getDomicilio().setIdDomicilio(-1L);
		} // try
		catch (Exception e) {		
			throw e;
		} // catch		
	} // loadDomicilios
	
  public void doBusquedaDomicilios() {
    List<UISelectEntity> domicilios= null;
    Map<String, Object> params= new HashMap<>();
		List<Columna>columns      = new ArrayList<>();
    try {
      params.put(Constantes.SQL_CONDICION, "upper(calle) like upper('%".concat(this.attrs.get("calle").toString()).concat("%')"));
			columns.add(new Columna("calle", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("numeroExterior", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("numeroInterior", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("asentamiento", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("entidad", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("municipio", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("domicilio", EFormatoDinamicos.MAYUSCULAS));
      domicilios = UIEntity.build("VistaDomiciliosCatalogosDto", "domicilios", params, columns, Constantes.SQL_TODOS_REGISTROS);
      this.registroAcreedor.getDomicilio().setDomicilio(new Entity(-1L));
      this.registroAcreedor.getDomicilio().setIdDomicilio(-1L);
			this.attrs.put("domiciliosBusqueda", domicilios);      
			this.attrs.put("resultados", domicilios.size());      
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } 

	public void doAsignaDomicilio() {
		List<UISelectEntity> domicilios        = new ArrayList<>();
		List<UISelectEntity> domiciliosBusqueda= null;
		UISelectEntity domicilio               = null;
		try {
			domiciliosBusqueda=(List<UISelectEntity>) this.attrs.get("domiciliosBusqueda");
			domicilio= domiciliosBusqueda.get(domiciliosBusqueda.indexOf(this.domicilioBusqueda));
			domicilios.add(domicilio);
			this.attrs.put("domicilios", domicilios);			
			this.registroAcreedor.getDomicilio().setDomicilio(domicilio);
      this.registroAcreedor.getDomicilio().setIdDomicilio(domicilio.getKey());
			this.toAsignaEntidad();
			this.loadMunicipios();
			this.toAsignaMunicipio();
			this.loadLocalidades();
			this.toAsignaLocalidad();
			this.loadAtributosComplemento();						
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} 
	
  public void doActualizaMunicipios() {
    try {
      this.loadMunicipios();
      this.loadLocalidades();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 

  public void doActualizaLocalidades() {
    try {
      this.loadLocalidades();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 

  public void doActualizaCodigosPostales() {
    try {
      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 

  public void doLoadAtributos() {
		this.doLoadAtributos(true);
	} 
	
  public void doLoadAtributos(boolean all) {    
		List<Entity> domicilios= null;
    try {
			if(all){
				if(!this.registroAcreedor.getDomicilio().getDomicilio().getKey().equals(-1L)){
					domicilios= (List<Entity>) this.attrs.get("domicilios");
					this.registroAcreedor.getDomicilio().setDomicilio(domicilios.get(domicilios.indexOf(this.registroAcreedor.getDomicilio().getDomicilio())));
					this.registroAcreedor.getDomicilio().setIdDomicilio(domicilios.get(domicilios.indexOf(this.registroAcreedor.getDomicilio().getDomicilio())).getKey());
				} // if
				else{
					this.registroAcreedor.getDomicilio().setDomicilio(new Entity(-1L));
					this.registroAcreedor.getDomicilio().setIdDomicilio(-1L);
				} // else					
				this.toAsignaEntidad();
				this.loadMunicipios();
				this.toAsignaMunicipio();
				this.loadLocalidades();
				this.toAsignaLocalidad();
			} // if
      this.loadAtributosComplemento();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doLoadAtributos

	private void loadAtributosComplemento() throws Exception{
		MotorBusqueda motor            = null;
		TcManticDomiciliosDto domicilio= null;
		try {
			if (!this.registroAcreedor.getDomicilio().getIdDomicilio().equals(-1L)) {
        motor = new MotorBusqueda(this.registroAcreedor.getIdAcreedor());
        domicilio = motor.toDomicilio(this.registroAcreedor.getDomicilio().getIdDomicilio());
        this.registroAcreedor.getDomicilio().setNumeroExterior(domicilio.getNumeroExterior());
        this.registroAcreedor.getDomicilio().setNumeroInterior(domicilio.getNumeroInterior());
        this.registroAcreedor.getDomicilio().setCalle(domicilio.getCalle());
        this.registroAcreedor.getDomicilio().setAsentamiento(domicilio.getAsentamiento());
        this.registroAcreedor.getDomicilio().setEntreCalle(domicilio.getEntreCalle());
        this.registroAcreedor.getDomicilio().setYcalle(domicilio.getYcalle());
      } // if
      else {
        clearAtributos();
      } // else
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
	} 
	
  public void clearAtributos() {
    try {
      this.registroAcreedor.getDomicilio().setNumeroExterior("");
      this.registroAcreedor.getDomicilio().setNumeroInterior("");
      this.registroAcreedor.getDomicilio().setCalle("");
      this.registroAcreedor.getDomicilio().setAsentamiento("");
      this.registroAcreedor.getDomicilio().setEntreCalle("");
      this.registroAcreedor.getDomicilio().setYcalle("");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doLoadAtributos
	
	public void doAgregarProveedor() {
    try {
      this.registroAcreedor.doAgregarAcreedorDomicilio();
      this.registroAcreedor.setDomicilio(new Domicilio());
      this.loadDomicilios();
      this.doLoadAtributos(true);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 

  public void doConsultarProveedorDomicilio() {
    Domicilio domicilio       = null;
    try {
      this.registroAcreedor.doConsultarAcreedorDomicilio();
			domicilio = this.registroAcreedor.getDomicilioPivote();
      this.registroAcreedor.getDomicilio().setIdDomicilio(domicilio.getIdDomicilio());
      this.registroAcreedor.getDomicilio().setDomicilio(domicilio.getDomicilio());      			
      this.registroAcreedor.getDomicilio().setIdEntidad(domicilio.getIdEntidad());	
			this.registroAcreedor.getDomicilio().getDomicilio().put("idEntidad", new Value("idEntidad", domicilio.getIdEntidad().getKey()));
      this.toAsignaEntidad();
			this.loadMunicipios();
      this.registroAcreedor.getDomicilio().setIdMunicipio(domicilio.getIdMunicipio());			
			this.registroAcreedor.getDomicilio().getDomicilio().put("idMunicipio", new Value("idMunicipio", domicilio.getIdMunicipio().getKey()));
      this.toAsignaMunicipio();
			this.loadLocalidades();
      this.registroAcreedor.getDomicilio().setLocalidad(domicilio.getLocalidad());			
      this.registroAcreedor.getDomicilio().setIdLocalidad(domicilio.getIdLocalidad());			
			this.registroAcreedor.getDomicilio().getDomicilio().put("idLocalidad", new Value("idLocalidad", domicilio.getLocalidad().getKey()));
      this.toAsignaLocalidad();
			this.doCompleteCodigo(domicilio.getCodigoPostal());
			this.asignaCodigoPostal();			
      this.registroAcreedor.getDomicilio().setCalle(domicilio.getCalle());
      this.registroAcreedor.getDomicilio().setNumeroExterior(domicilio.getNumeroExterior());
      this.registroAcreedor.getDomicilio().setNumeroInterior(domicilio.getNumeroInterior());
      this.registroAcreedor.getDomicilio().setAsentamiento(domicilio.getAsentamiento());
      this.registroAcreedor.getDomicilio().setEntreCalle(domicilio.getEntreCalle());
      this.registroAcreedor.getDomicilio().setYcalle(domicilio.getYcalle());
      this.registroAcreedor.getDomicilio().setIdTipoDomicilio(domicilio.getIdTipoDomicilio());
      this.registroAcreedor.getDomicilio().setPrincipal(domicilio.getPrincipal());
			this.registroAcreedor.getDomicilio().setNuevoCp(domicilio.isNuevoCp());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
	
	public void doActualizaDomicilio() {
		try {
			this.registroAcreedor.doActualizarAcreedorDomicilio();
			this.registroAcreedor.setDomicilio(new Domicilio());
      this.loadDomicilios();
      this.doLoadAtributos(true);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} 
	
	public void doEliminarDomicilio() {
		try {
			this.registroAcreedor.doEliminarAcreedorDomicilio();
			this.registroAcreedor.setDomicilio(new Domicilio());
      loadDomicilios();
      doLoadAtributos(true);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} 
	
	public void doAgregarAcreedorAgente() {
		List<UISelectItem> representantes = null;
		try {
			representantes= (List<UISelectItem>) this.attrs.get("representantes");
			if(!representantes.isEmpty())
				this.registroAcreedor.doAgregarAgenteContacto();
			else
				JsfBase.addMessage("Agregar representante", "No hay representantes registrados", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} 
	
	public void doAgregarAgente() {
    try {
      this.registroAcreedor.doAgregarAgente();
      this.registroAcreedor.setPersonaTipoContactoPivote(new AcreedorContactoAgente());      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
	
	public void doActualizaAgente() {
		try {
			this.registroAcreedor.doActualizaAgente();
      this.registroAcreedor.setPersonaTipoContactoPivote(new AcreedorContactoAgente());      
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} 
	
	public void doEliminarAgente() {
		try {
      this.registroAcreedor.doEliminarAgente();
      this.registroAcreedor.setPersonaTipoContactoPivote(new AcreedorContactoAgente());      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} 
	
	private void loadBancos() {
		List<UISelectItem> bancos= null;
		Map<String, Object>params= new HashMap<>();
		List<String> columns     = new ArrayList<>();
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			columns.add("nombre");
			bancos= UISelect.build("TcManticBancosDto", "row", params, columns, " ", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("bancos", bancos);
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
	}
	
	public List<UISelectEntity> doCompleteCodigo(String query) {
		if(this.registroAcreedor.getDomicilio().getIdEntidad().getKey()>= 1L && !Cadena.isVacio(query)) {
			this.attrs.put("condicionCodigoPostal", query);
			this.doUpdateCodigosPostales();		
			return (List<UISelectEntity>)this.attrs.get("allCodigosPostales");
		} // if
    else {
			this.registroAcreedor.getDomicilio().setNuevoCp(Boolean.FALSE);
			this.registroAcreedor.getDomicilio().setIdCodigoPostal(-1L);
			this.registroAcreedor.getDomicilio().setCodigoPostal("");
			return new ArrayList<>();
		} // else		
	}

	public void doUpdateCodigosPostales() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));      
  		params.put(Constantes.SQL_CONDICION, "id_entidad=" + this.registroAcreedor.getDomicilio().getIdEntidad().getKey() + " and codigo like '" + this.attrs.get("condicionCodigoPostal") + "%'");
      this.attrs.put("allCodigosPostales", (List<UISelectEntity>) UIEntity.build("TcManticCodigosPostalesDto", "row", params, columns, 20L));
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
  
	public void doAsignaCodigo(SelectEvent event) {
		UISelectEntity seleccion    = null;
		List<UISelectEntity> codigos= (List<UISelectEntity>) this.attrs.get("allCodigosPostales");
		try {
			seleccion= codigos.get(codigos.indexOf((UISelectEntity)event.getObject()));
			if(seleccion!= null && !seleccion.isEmpty()) {
				this.registroAcreedor.getDomicilio().setIdCodigoPostal(seleccion.getKey());
				this.registroAcreedor.getDomicilio().setCodigoPostal(seleccion.toString("codigo"));
				this.registroAcreedor.getDomicilio().setNuevoCp(true);
				this.attrs.put("codigoSeleccionado", seleccion);
			} // if	
			else {
				this.registroAcreedor.getDomicilio().setIdCodigoPostal(-1L);
				this.registroAcreedor.getDomicilio().setCodigoPostal("");
				this.registroAcreedor.getDomicilio().setNuevoCp(false);
			} //else	
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} 
	
	public void asignaCodigoPostal() {
		List<UISelectEntity> codigosPostales= null;
		try {
			codigosPostales= (List<UISelectEntity>) this.attrs.get("allCodigosPostales");
			if(codigosPostales!= null && !codigosPostales.isEmpty()){
				this.registroAcreedor.getDomicilio().setCodigoPostal(codigosPostales.get(0).toString("codigo"));
				this.registroAcreedor.getDomicilio().setNuevoCp(true);
				this.registroAcreedor.getDomicilio().setIdCodigoPostal(codigosPostales.get(0).getKey());
				this.attrs.put("codigoSeleccionado", codigosPostales.get(0));
			} // if
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} 
	
	public void doInicializaCodigo() {
		try {
			this.registroAcreedor.getDomicilio().setIdCodigoPostal(-1L);
			this.registroAcreedor.getDomicilio().setCodigoPostal("");
			if((Boolean)this.attrs.get("cpNuevo")) {
				this.registroAcreedor.getDomicilio().setNuevoCp(Boolean.TRUE);		
				this.attrs.put("codigoSeleccionado", new UISelectEntity(-1L));
			} // 				
			else
				this.registroAcreedor.getDomicilio().setNuevoCp(Boolean.FALSE);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);		
		} // catch		
	} 

  public void doCodigoPostal() {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("codigo", JsfBase.getParametro("contenedorGrupos:cp")== null? JsfBase.getParametro("contenedorGrupos:codigoPostal_input"): JsfBase.getParametro("cp"));      
      Entity codigo = (Entity)DaoFactory.getInstance().toEntity("TcManticCodigosPostalesDto", "igual", params);
      if(codigo!= null && !codigo.isEmpty()) {
				this.registroAcreedor.getDomicilio().setCodigoPostal(codigo.toString("codigo"));
        List<UISelectEntity> entidades= (List<UISelectEntity>)this.attrs.get("entidades");
        int index= entidades.indexOf(new UISelectEntity(codigo.toLong("idEntidad")));
        if(index>= 0) {
  				this.registroAcreedor.getDomicilio().setIdEntidad(entidades.get(index));
          this.doActualizaMunicipios();
        } // if  
        else {
          Entity entidad= codigo.clone();
          entidad.setKey(entidad.toLong("idEntidad"));
  				this.registroAcreedor.getDomicilio().setIdEntidad(entidad);
        } // if  
				this.registroAcreedor.getDomicilio().setIdCodigoPostal(codigo.toLong("idCodigoPostal"));
				this.attrs.put("codigoSeleccionado", new UISelectEntity(codigo));
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

  public void doUpdateChange() {
    LOG.info(this.registroAcreedor.getAcreedoresDomicilio().size());
  } 
  
}
