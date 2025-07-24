package mx.org.kaana.kalan.catalogos.acreedores.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.mantic.db.dto.TcManticAcreedoresDto;
import mx.org.kaana.mantic.catalogos.clientes.beans.Domicilio;
import mx.org.kaana.mantic.catalogos.personas.beans.PersonaTipoContacto;
import mx.org.kaana.kalan.catalogos.acreedores.reglas.MotorBusqueda;
import mx.org.kaana.kalan.catalogos.acreedores.reglas.Transaccion;
import mx.org.kaana.mantic.db.dto.TcManticAcreedoresPortalesDto;
import mx.org.kaana.mantic.enums.ETiposCuentas;

public class RegistroAcreedor implements Serializable{
	
	private static final long serialVersionUID = 4690869520445115664L;
	private Long idAcreedor;
	private TcManticAcreedoresDto acreedor;
	private TcManticAcreedoresPortalesDto portal;
	private List<AcreedorDomicilio> acreedoresDomicilio;
	private AcreedorDomicilio acreedorDomicilioSeleccion;
	private List<AcreedorTipoContacto> acreedoresTipoContacto;
	private AcreedorTipoContacto acreedorTipoContactoSeleccion;
	private List<AcreedorBanca> acreedoresServicio;
	private AcreedorBanca acreedorServicioSeleccion;
	private List<AcreedorBanca> acreedoresTransferencia;
	private AcreedorBanca acreedorTransferenciaSeleccion;
	private List<IBaseDto> deleteList;
	private ContadoresListas contadores;
	private Long countIndice;
	private Domicilio domicilio;
	private Domicilio domicilioPivote;
	private List<AcreedorContactoAgente> personasTiposContacto;
	private PersonaTipoContacto personaTipoContactoSeleccion;
	private AcreedorContactoAgente personaTipoContactoPivote;
	private AcreedorContactoAgente personaTipoContacto;

	public RegistroAcreedor() {
		this(-1L, new TcManticAcreedoresDto(), new AcreedorDomicilio(), new ArrayList<AcreedorTipoContacto>(), new Domicilio(), new Domicilio(), new ArrayList<AcreedorContactoAgente>(), new AcreedorContactoAgente(), new AcreedorContactoAgente(), new ArrayList<AcreedorDomicilio>(), new TcManticAcreedoresPortalesDto(), new ArrayList<AcreedorBanca>(), new ArrayList<AcreedorBanca>());
	} 

	public RegistroAcreedor(Long idAcreedor) {
		this.idAcreedor               = idAcreedor;
		this.contadores               = new ContadoresListas();
		this.countIndice              = 0L;
		this.deleteList               = new ArrayList<>();
		this.domicilio                = new Domicilio();
		this.domicilioPivote          = new Domicilio();
		this.personaTipoContactoPivote= new AcreedorContactoAgente();
		this.personaTipoContacto      = new AcreedorContactoAgente();
		this.init();
	} 

	public RegistroAcreedor(Long idAcreedor, TcManticAcreedoresDto acreedor, AcreedorDomicilio acreedorDomicilioSeleccion, List<AcreedorTipoContacto> acreedoresTipoContacto, Domicilio domicilio, Domicilio domicilioPivote, List<AcreedorContactoAgente> personasTiposContacto, AcreedorContactoAgente personaTipoContactoPivote, AcreedorContactoAgente personaTipoContacto, List<AcreedorDomicilio> acreedoresDomicilio, TcManticAcreedoresPortalesDto portal, List<AcreedorBanca> acreedoresServicio, List<AcreedorBanca> acreedoresTransferencia) {
		this.idAcreedor                = idAcreedor;
		this.acreedor                  = acreedor;
		this.acreedorDomicilioSeleccion= acreedorDomicilioSeleccion;
		this.acreedoresTipoContacto    = acreedoresTipoContacto;
		this.deleteList                = new ArrayList<>();
		this.contadores                = new ContadoresListas(); 
		this.countIndice               = 0L;
		this.domicilio                 = domicilio;
		this.domicilioPivote           = domicilioPivote;
		this.personasTiposContacto     = personasTiposContacto;
		this.personaTipoContactoPivote = personaTipoContactoPivote;
		this.personaTipoContacto       = personaTipoContacto;
		this.acreedoresDomicilio       = acreedoresDomicilio;
		this.portal                    = portal;
		this.acreedoresServicio        = acreedoresServicio;
		this.acreedoresTransferencia   = acreedoresTransferencia;
	} 

	public Long getIdAcreedor() {
		return idAcreedor;
	}

	public void setIdAcreedor(Long idAcreedor) {
		this.idAcreedor = idAcreedor;
	}

	public TcManticAcreedoresDto getAcreedor() {
		return acreedor;
	}

	public void setAcreedor(TcManticAcreedoresDto acreedor) {
		this.acreedor = acreedor;
	}

	public List<AcreedorDomicilio> getAcreedoresDomicilio() {
		return acreedoresDomicilio;
	}

	public void setAcreedoresDomicilio(List<AcreedorDomicilio> acreedoresDomicilio) {
		this.acreedoresDomicilio = acreedoresDomicilio;
	}

	public AcreedorDomicilio getAcreedorDomicilioSeleccion() {
		return acreedorDomicilioSeleccion;
	}

	public void setAcreedorDomicilioSeleccion(AcreedorDomicilio acreedorDomicilioSeleccion) {
		this.acreedorDomicilioSeleccion = acreedorDomicilioSeleccion;
	}

	public List<AcreedorTipoContacto> getAcreedoresTipoContacto() {
		return acreedoresTipoContacto;
	}

	public void setAcreedoresTipoContacto(List<AcreedorTipoContacto> acreedoresTipoContacto) {
		this.acreedoresTipoContacto = acreedoresTipoContacto;
	}

	public AcreedorTipoContacto getAcreedorTipoContactoSeleccion() {
		return acreedorTipoContactoSeleccion;
	}

	public void setAcreedorTipoContactoSeleccion(AcreedorTipoContacto acreedorTipoContactoSeleccion) {
		this.acreedorTipoContactoSeleccion = acreedorTipoContactoSeleccion;
	}

	public List<IBaseDto> getDeleteList() {
		return deleteList;
	}

	public void setDeleteList(List<IBaseDto> deleteList) {
		this.deleteList = deleteList;
	}

	public Domicilio getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}

	public Domicilio getDomicilioPivote() {
		return domicilioPivote;
	}

	public void setDomicilioPivote(Domicilio domicilioPivote) {
		this.domicilioPivote = domicilioPivote;
	}

	public List<AcreedorContactoAgente> getPersonasTiposContacto() {
		return personasTiposContacto;
	}

	public void setPersonasTiposContacto(List<AcreedorContactoAgente> personasTiposContacto) {
		this.personasTiposContacto = personasTiposContacto;
	}

	public PersonaTipoContacto getPersonaTipoContactoSeleccion() {
		return personaTipoContactoSeleccion;
	}

	public void setPersonaTipoContactoSeleccion(PersonaTipoContacto personaTipoContactoSeleccion) {
		this.personaTipoContactoSeleccion = personaTipoContactoSeleccion;
	}

	public AcreedorContactoAgente getPersonaTipoContactoPivote() {
		return personaTipoContactoPivote;
	}

	public void setPersonaTipoContactoPivote(AcreedorContactoAgente personaTipoContactoPivote) {
		this.personaTipoContactoPivote = personaTipoContactoPivote;
	}

	public AcreedorContactoAgente getPersonaTipoContacto() {
		return personaTipoContacto;
	}

	public void setPersonaTipoContacto(AcreedorContactoAgente personaTipoContacto) {
		this.personaTipoContacto = personaTipoContacto;
	}

	public TcManticAcreedoresPortalesDto getPortal() {
		return portal;
	}

	public void setPortal(TcManticAcreedoresPortalesDto portal) {
		this.portal = portal;
	}

	public List<AcreedorBanca> getAcreedoresServicio() {
		return acreedoresServicio;
	}

	public void setAcreedoresServicio(List<AcreedorBanca> acreedoresServicio) {
		this.acreedoresServicio = acreedoresServicio;
	}

	public AcreedorBanca getAcreedorServicioSeleccion() {
		return acreedorServicioSeleccion;
	}

	public void setAcreedorServicioSeleccion(AcreedorBanca acreedorServicioSeleccion) {
		this.acreedorServicioSeleccion = acreedorServicioSeleccion;
	}

	public List<AcreedorBanca> getAcreedoresTransferencia() {
		return acreedoresTransferencia;
	}

	public void setAcreedoresTransferencia(List<AcreedorBanca> acreedoresTransferencia) {
		this.acreedoresTransferencia = acreedoresTransferencia;
	}

	public AcreedorBanca getAcreedorTransferenciaSeleccion() {
		return acreedorTransferenciaSeleccion;
	}

	public void setAcreedorTransferenciaSeleccion(AcreedorBanca acreedorTransferenciaSeleccion) {
		this.acreedorTransferenciaSeleccion = acreedorTransferenciaSeleccion;
	}
	
	private void init() {
		MotorBusqueda motorBusqueda= null;
		try {
			motorBusqueda= new MotorBusqueda(this.idAcreedor);
			this.acreedor= motorBusqueda.toAcreedor();					
			this.portal= motorBusqueda.toPortal();
			this.initCollections(motorBusqueda);
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
	} // init
	
	private void initCollections(MotorBusqueda motor) throws Exception {
		int count= 0;
		try {
			this.acreedoresDomicilio= motor.toAcreedoresDomicilio(true);
			for(AcreedorDomicilio acreedorDomicilio: this.acreedoresDomicilio){
				count++;
				acreedorDomicilio.setConsecutivo(Long.valueOf(count));
			} // for				
			this.acreedoresTipoContacto= motor.toAcreedoresTipoContacto();			
			this.personasTiposContacto= motor.toAgentes();
			this.acreedoresServicio= motor.toServicios();
			this.acreedoresTransferencia= motor.toTransferencias();
		} // try
		catch (Exception e) {
			Error.mensaje(e);			
			throw e;
		} // catch		
	} // initCollections
	
	public void doAgregarAcreedorDomicilio() {
		AcreedorDomicilio acreedorDomicilio= null;
		try {								
			acreedorDomicilio= new AcreedorDomicilio(this.contadores.getTotalAcreedoresDomicilios() + this.countIndice, ESql.INSERT, true);	
			this.toUpdateAcreedorPivote(acreedorDomicilio, Boolean.FALSE);			
			this.acreedoresDomicilio.add(acreedorDomicilio);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarAcreedorDomicilio
	
	public void doEliminarAcreedorDomicilio() {
		try {			
			if(this.acreedoresDomicilio.remove(this.acreedorDomicilioSeleccion)){
				if(!this.acreedorDomicilioSeleccion.getNuevo())
					addDeleteList(this.acreedorDomicilioSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el domicilio", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue porsible eliminar el domicilio", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarAcreedorDomicilio	
	
	public void doConsultarAcreedorDomicilio() {
		AcreedorDomicilio pivote= null;
		try {			
			pivote= this.acreedoresDomicilio.get(this.acreedoresDomicilio.indexOf(this.acreedorDomicilioSeleccion));
			pivote.setModificar(true);
			this.domicilioPivote= new Domicilio();
			this.domicilioPivote.setIdTipoDomicilio(pivote.getIdTipoDomicilio());
			this.domicilioPivote.setPrincipal(Objects.equals(pivote.getIdPrincipal(), 1L) || Objects.equals(pivote.getIdPrincipal(), null));	
			this.domicilioPivote.setIdDomicilio(pivote.getDomicilio().getKey());
			this.domicilioPivote.setDomicilio(pivote.getDomicilio());
			this.domicilioPivote.setIdEntidad(pivote.getIdEntidad());
			this.domicilioPivote.setIdMunicipio(pivote.getIdMunicipio());
			this.domicilioPivote.setLocalidad(pivote.getIdLocalidad());
			this.domicilioPivote.setIdLocalidad(pivote.getIdLocalidad().getKey());
			this.domicilioPivote.setCodigoPostal(pivote.getCodigoPostal());
			this.domicilioPivote.setCalle(pivote.getCalle());
			this.domicilioPivote.setNumeroExterior(pivote.getExterior());
			this.domicilioPivote.setNumeroInterior(pivote.getInterior());
			this.domicilioPivote.setAsentamiento(pivote.getColonia());
			this.domicilioPivote.setEntreCalle(pivote.getEntreCalle());
			this.domicilioPivote.setYcalle(pivote.getyCalle());
			this.domicilioPivote.setNuevoCp(pivote.getCodigoPostal()!= null && !Cadena.isVacio(pivote.getCodigoPostal()));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // doConsultarAcreedorDomicilio
	
	public void doActualizarAcreedorDomicilio() {
		AcreedorDomicilio pivote= null;
		try {			
			pivote= this.acreedoresDomicilio.get(this.acreedoresDomicilio.indexOf(this.acreedorDomicilioSeleccion));			
			pivote.setModificar(Boolean.FALSE);
			this.toUpdateAcreedorPivote(pivote, Boolean.TRUE);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch				
	} // doActualizarAcreedorDomicilio
	
	public void toUpdateAcreedorPivote(AcreedorDomicilio acreedorDomicilio, boolean actualizar) throws Exception {
    Long idDomicilio= this.getDomicilioPivote()== null? -1L: this.getDomicilioPivote().getIdDomicilio();
		try {
			if(this.domicilio.getPrincipal()) {
				for(AcreedorDomicilio record: this.acreedoresDomicilio)
					record.setIdPrincipal(0L);
			} // if
			if(this.domicilio.getDomicilio()!= null){
    		acreedorDomicilio.setIdDomicilio(idDomicilio);
	  		acreedorDomicilio.setIdPrincipal(this.domicilio.getPrincipal()? 1L: 2L);
			} // if	
			acreedorDomicilio.setDomicilio(new Entity(idDomicilio));
			acreedorDomicilio.setIdUsuario(JsfBase.getIdUsuario());
			acreedorDomicilio.setIdTipoDomicilio(this.domicilio.getIdTipoDomicilio());
			if(!actualizar)
				acreedorDomicilio.setConsecutivo(this.acreedoresDomicilio.size()+ 1L);
			acreedorDomicilio.setIdEntidad(this.domicilio.getIdEntidad());
			acreedorDomicilio.setIdMunicipio(this.domicilio.getIdMunicipio());
			acreedorDomicilio.setIdLocalidad(this.domicilio.getLocalidad());
			acreedorDomicilio.setCodigoPostal(this.domicilio.getCodigoPostal());
			acreedorDomicilio.setCalle(this.domicilio.getCalle());
			acreedorDomicilio.setExterior(this.domicilio.getNumeroExterior());
			acreedorDomicilio.setInterior(this.domicilio.getNumeroInterior());
			acreedorDomicilio.setEntreCalle(this.domicilio.getEntreCalle());
			acreedorDomicilio.setyCalle(this.domicilio.getYcalle());
			acreedorDomicilio.setColonia(this.domicilio.getAsentamiento());
			acreedorDomicilio.setNuevoCp(this.domicilio.getCodigoPostal()!= null && !Cadena.isVacio(this.domicilio.getCodigoPostal()));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // setValuesAcreedorDomicilio
	
	public void doAgregarAcreedorTipoContacto() {
		AcreedorTipoContacto acreedorTipoContacto= null;
		try {					
			acreedorTipoContacto= new AcreedorTipoContacto(this.contadores.getTotalAcreedoresTipoContacto()+ this.countIndice, ESql.INSERT, true);				
			acreedorTipoContacto.setOrden(this.acreedoresTipoContacto.size() + 1L);
			this.acreedoresTipoContacto.add(acreedorTipoContacto);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarAcreedorTipoContacto
	
	public void doAgregarAcreedorServicio() {
		AcreedorBanca acreedorBanca= null;
		try {					
			acreedorBanca= new AcreedorBanca(this.contadores.getTotalAcreedoresServicio()+ this.countIndice, ESql.INSERT, true);				
			acreedorBanca.setIdTipoCuenta(ETiposCuentas.SERVICIOS.getKey());
			this.acreedoresServicio.add(acreedorBanca);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} 
	
	public void doAgregarAcreedorTransferencia() {
		AcreedorBanca acreedorBanca= null;
		try {					
			acreedorBanca= new AcreedorBanca(this.contadores.getTotalAcreedoresTransferencia()+ this.countIndice, ESql.INSERT, true);				
			acreedorBanca.setIdTipoCuenta(ETiposCuentas.TRANSFERENCIAS.getKey());
			this.acreedoresTransferencia.add(acreedorBanca);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarAcreedorTipoContacto
	
	public void doAgregarAgenteContacto() {
		PersonaTipoContacto personaTipoContacto= null;
		try {
			personaTipoContacto= new PersonaTipoContacto(this.contadores.getTotalPersonasTipoContacto() + this.countIndice, ESql.INSERT, true, "");
			this.personaTipoContactoPivote.getContactos().add(personaTipoContacto);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			this.countIndice++;
		} // finally
	}
	
	
	public void doEliminarAcreedorTipoContacto() {
		try {			
			if(this.acreedoresTipoContacto.remove(this.acreedorTipoContactoSeleccion)){
				if(!this.acreedorTipoContactoSeleccion.getNuevo())
					addDeleteList(this.acreedorTipoContactoSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el tipo de contacto", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue porsible eliminar el tipo de contacto", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} 
	
	public void doEliminarAcreedorServicio() {
		try {			
			if(this.acreedoresServicio.remove(this.acreedorServicioSeleccion)){
				if(!this.acreedorServicioSeleccion.getNuevo())
					addDeleteList(this.acreedorServicioSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el registro de servicio", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue porsible eliminar el registro de servicio", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarAcreedorTipoContacto
	
	public void doEliminarAcreedorTransferencia() {
		try {			
			if(this.acreedoresTransferencia.remove(this.acreedorTransferenciaSeleccion)){
				if(!this.acreedorTransferenciaSeleccion.getNuevo())
					addDeleteList(this.acreedorTransferenciaSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el registro de transferencia", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue porsible eliminar el registro de transferencia", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} 
	
	public void doEliminarAgenteContacto() {
		try {			
			if(this.personaTipoContactoPivote.getContactos().remove(this.personaTipoContactoSeleccion)){
				if(!this.personaTipoContactoSeleccion.getNuevo())
					addDeleteList(this.personaTipoContactoSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el tipo de contacto", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue porsible eliminar el tipo de contacto", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} 
	
	private void addDeleteList(IBaseDto dto) throws Exception {
		Transaccion transaccion= null;
		try {
			transaccion= new Transaccion(dto);
			transaccion.ejecutar(EAccion.DEPURAR);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} 
	
	public void doAgregarAgente() {
		AcreedorContactoAgente acreedorContactoAgente= null;
		try {								
			acreedorContactoAgente= new AcreedorContactoAgente(this.contadores.getTotalAcreedoresAgentes() + this.countIndice, ESql.INSERT, true);				
			acreedorContactoAgente.setConsecutivo(this.personasTiposContacto.size()+1L);
			acreedorContactoAgente.setNombres(this.personaTipoContactoPivote.getNombres());
			acreedorContactoAgente.setPaterno(this.personaTipoContactoPivote.getPaterno());
			acreedorContactoAgente.setMaterno(this.personaTipoContactoPivote.getMaterno());
			acreedorContactoAgente.setContactos(this.personaTipoContactoPivote.getContactos());
			this.personasTiposContacto.add(acreedorContactoAgente);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} 
	
	public void doConsultarAgente() {
		AcreedorContactoAgente pivote= null;
		try {			
			pivote= this.personasTiposContacto.get(this.personasTiposContacto.indexOf(this.personaTipoContacto));
			pivote.setModificar(true);
			this.personaTipoContactoPivote= new AcreedorContactoAgente();
			this.personaTipoContactoPivote.setNombres(pivote.getNombres());
			this.personaTipoContactoPivote.setPaterno(pivote.getPaterno());
			this.personaTipoContactoPivote.setMaterno(pivote.getMaterno());
			this.personaTipoContactoPivote.setContactos(pivote.getContactos());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} 
	
	public void doEliminarAgente() {
		try {
			if(this.personasTiposContacto.remove(this.personaTipoContacto)) {
				if(!this.personaTipoContacto.getNuevo() && !Objects.equals(this.personaTipoContacto, null))
					addDeleteList(this.personaTipoContacto);
				JsfBase.addMessage("Se eliminó correctamente el tipo de contacto", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue porsible eliminar el tipo de contacto", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} 
	
	public void doActualizaAgente() {
		AcreedorContactoAgente pivote= null;
		try {			
			pivote= this.personasTiposContacto.get(this.personasTiposContacto.indexOf(this.personaTipoContacto));
			pivote.setModificar(false);
			pivote.setNombres(this.personaTipoContactoPivote.getNombres());
			pivote.setPaterno(this.personaTipoContactoPivote.getPaterno());
			pivote.setMaterno(this.personaTipoContactoPivote.getMaterno());
			pivote.setContactos(this.personaTipoContactoPivote.getContactos());			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch	
	} 
	
}
