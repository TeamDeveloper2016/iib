package mx.org.kaana.mantic.catalogos.personas.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.db.dto.TcKeetDeudoresDto;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.mantic.catalogos.clientes.beans.Domicilio;
import mx.org.kaana.mantic.catalogos.clientes.reglas.Transaccion;
import mx.org.kaana.mantic.catalogos.personas.reglas.MotorBusqueda;
import mx.org.kaana.mantic.db.dto.TcManticPersonasDto;
import mx.org.kaana.mantic.db.dto.TrManticEmpresaPersonalDto;

public class RegistroPersona implements Serializable {

	private static final long serialVersionUID= -8922183204418674058L;
	private Long idPersona;
	private TcManticPersonasDto persona;
	private TcKeetDeudoresDto deudor;
	private TrManticEmpresaPersonalDto empresaPersona;
	private List<PersonaDomicilio> personasDomicilio;
	private PersonaDomicilio personaDomicilioSeleccion;
	private List<PersonaBeneficiario> personasBeneficiarios;
	private PersonaBeneficiario personaBeneficiarioSeleccion;
	private PersonaBeneficiario personaBeneficiario;
	private PersonaBeneficiario beneficiarioPivote;
	private List<PersonaTipoContacto> personasTiposContacto;
	private PersonaTipoContacto personaTipoContactoSeleccion;	
	private List<PersonaBanco> personasBancos;
	private PersonaBanco personaBancoSeleccion;	
	private List<IBaseDto> deleteList;
	private ContadoresListas contadores;
	private Long countIndice;
	private Domicilio domicilio;
	private Domicilio domicilioPivote;
	private Long idPuesto;
	private Long idContratista;
	private Long idEmpresa;
	private boolean activo;
  private Object[] departamentos;
  private List<Especialidad> especialidades;
	
	public RegistroPersona() {
		this(-1L, new TcManticPersonasDto(), new ArrayList<PersonaDomicilio>(), new ArrayList<PersonaTipoContacto>(), new Domicilio(), new ArrayList<PersonaBanco>(), new ArrayList<PersonaBeneficiario>(), new TrManticEmpresaPersonalDto(), new PersonaBeneficiario(), new PersonaBeneficiario(), new TcKeetDeudoresDto());
	}
	
	public RegistroPersona(Long idPersona) {
		this.idPersona          = idPersona;
		this.contadores         = new ContadoresListas();
		this.countIndice        = 0L;
		this.idPuesto           = -1L;
    this.idContratista      = -1L;
		this.idEmpresa          = -1L;
		this.deleteList         = new ArrayList<>();
		this.domicilio          = new Domicilio();
		this.domicilioPivote    = new Domicilio();
		this.beneficiarioPivote = new PersonaBeneficiario();
		this.personaBeneficiario= new PersonaBeneficiario();
		this.init();		
	}
	
	public RegistroPersona(Long idPersona, TcManticPersonasDto persona, List<PersonaDomicilio> personasDomicilio, List<PersonaTipoContacto> personasTiposContacto, Domicilio domicilio, List<PersonaBanco> personasBancos, List<PersonaBeneficiario> personasBeneficiarios, TrManticEmpresaPersonalDto empresaPersona, PersonaBeneficiario beneficiarioPivote, PersonaBeneficiario personaBeneficiario, TcKeetDeudoresDto deudor) {
		this.idPersona            = idPersona;
		this.persona              = persona;
		this.personasDomicilio    = personasDomicilio;
		this.personasTiposContacto= personasTiposContacto;
		this.deleteList           = new ArrayList<>();
		this.contadores           = new ContadoresListas();
		this.countIndice          = 0L;
		this.domicilio            = domicilio;
		this.domicilioPivote      = domicilio;
		this.idPuesto             = -1L;
    this.idContratista        = -1L;
		this.idEmpresa            = -1L;
		this.personasBeneficiarios= personasBeneficiarios;
		this.personasBancos       = personasBancos;
		this.empresaPersona       = empresaPersona;
		this.beneficiarioPivote   = beneficiarioPivote;
		this.personaBeneficiario  = personaBeneficiario;
		this.activo               = true;
		this.deudor               = deudor;
    this.especialidades       = new ArrayList<>();
    this.departamentos        = new Object[] {};
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public TcManticPersonasDto getPersona() {
		return persona;
	}

	public void setPersona(TcManticPersonasDto persona) {
		this.persona = persona;
	}

	public List<PersonaDomicilio> getPersonasDomicilio() {
		return personasDomicilio;
	}

	public void setPersonasDomicilio(List<PersonaDomicilio> personasDomicilio) {
		this.personasDomicilio = personasDomicilio;
	}

	public PersonaDomicilio getPersonaDomicilioSeleccion() {
		return personaDomicilioSeleccion;
	}

	public void setPersonaDomicilioSeleccion(PersonaDomicilio personaDomicilioSeleccion) {
		this.personaDomicilioSeleccion = personaDomicilioSeleccion;
	}

	public List<PersonaTipoContacto> getPersonasTiposContacto() {
		return personasTiposContacto;
	}

	public void setPersonasTiposContacto(List<PersonaTipoContacto> personasTiposContacto) {
		this.personasTiposContacto = personasTiposContacto;
	}

	public PersonaTipoContacto getPersonaTipoContactoSeleccion() {
		return personaTipoContactoSeleccion;
	}

	public void setPersonaTipoContactoSeleccion(PersonaTipoContacto personaTipoContactoSeleccion) {
		this.personaTipoContactoSeleccion = personaTipoContactoSeleccion;
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

	public Long getIdPuesto() {
		return idPuesto;
	}

	public void setIdPuesto(Long idPuesto) {
		this.idPuesto = idPuesto;
	}	

  public Long getIdContratista() {
    return idContratista;
  }

  public void setIdContratista(Long idContratista) {
    this.idContratista = idContratista;
  }

	public Long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}	

	public List<PersonaBeneficiario> getPersonasBeneficiarios() {
		return personasBeneficiarios;
	}

	public void setPersonasBeneficiarios(List<PersonaBeneficiario> personasBeneficiarios) {
		this.personasBeneficiarios = personasBeneficiarios;
	}

	public PersonaBeneficiario getPersonaBeneficiarioSeleccion() {
		return personaBeneficiarioSeleccion;
	}

	public void setPersonaBeneficiarioSeleccion(PersonaBeneficiario personaBeneficiarioSeleccion) {
		this.personaBeneficiarioSeleccion = personaBeneficiarioSeleccion;
	}

	public List<PersonaBanco> getPersonasBancos() {
		return personasBancos;
	}

	public void setPersonasBancos(List<PersonaBanco> personasBancos) {
		this.personasBancos = personasBancos;
	}

	public PersonaBanco getPersonaBancoSeleccion() {
		return personaBancoSeleccion;
	}

	public void setPersonaBancoSeleccion(PersonaBanco personaBancoSeleccion) {
		this.personaBancoSeleccion = personaBancoSeleccion;
	}	

	public TrManticEmpresaPersonalDto getEmpresaPersona() {
		return empresaPersona;
	}

	public void setEmpresaPersona(TrManticEmpresaPersonalDto empresaPersona) {
		this.empresaPersona = empresaPersona;
	}	

	public PersonaBeneficiario getBeneficiarioPivote() {
		return beneficiarioPivote;
	}

	public void setBeneficiarioPivote(PersonaBeneficiario beneficiarioPivote) {
		this.beneficiarioPivote = beneficiarioPivote;
	}	

	public PersonaBeneficiario getPersonaBeneficiario() {
		return personaBeneficiario;
	}

	public void setPersonaBeneficiario(PersonaBeneficiario personaBeneficiario) {
		this.personaBeneficiario = personaBeneficiario;
	}	

	public boolean isActivo() {
		return this.empresaPersona.getIdActivo().equals(1L);
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
		this.empresaPersona.setIdActivo(this.activo ? 1L : 2L);
	}	

	public TcKeetDeudoresDto getDeudor() {
		return deudor;
	}

	public void setDeudor(TcKeetDeudoresDto deudor) {
		this.deudor = deudor;
	}	

  public Object[] getDepartamentos() {
    return departamentos;
  }

  public void setDepartamentos(Object[] departamentos) {
    this.departamentos = departamentos;
  }

  public List<Especialidad> getEspecialidades() {
    return especialidades;
  }
	
	private void init() {
		int countDomicilio   = 0;
		int countBeneficiario= 0;
		MotorBusqueda motor  = null;
		try {
			motor= new MotorBusqueda(this.idPersona);
			this.persona          = motor.toPersona();									
			this.empresaPersona   = motor.toDetallePersona();
			this.deudor           = motor.toDeudor(this.empresaPersona.getIdEmpresaPersona());
			this.personasDomicilio= motor.toPersonasDomicilio(true);
			for(PersonaDomicilio personaDomicilio: this.personasDomicilio) {
				countDomicilio++;
				personaDomicilio.setConsecutivo(Long.valueOf(countDomicilio));
			} // for				
			this.personasBeneficiarios= motor.toPersonasBeneficiarios();
			for(PersonaBeneficiario personaBenefi: this.personasBeneficiarios) {
				countBeneficiario++;
				personaBenefi.setConsecutivo(Long.valueOf(countBeneficiario));
			} // for				
			this.personasTiposContacto= motor.toPersonasTipoContacto();		
			this.personasBancos= motor.toPersonasBancos();		
			this.idPuesto= motor.toPuestoPersona();
      this.idContratista= motor.toContratistaPersona();
			this.idEmpresa= motor.toEmpresaPersona();
      this.especialidades= motor.toPersonaDepartamentos(this.empresaPersona.getIdEmpresaPersona());
      if(this.especialidades== null || this.especialidades.isEmpty()) 
        this.departamentos= new Object[] {};
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
	} // init
	
	public void doAgregarPersonaDomicilio() {
		PersonaDomicilio personaDomicilio= null;
		try {								
			personaDomicilio= new PersonaDomicilio(this.contadores.getTotalPersonasDomicilios() + this.countIndice, ESql.INSERT, true);	
			this.toUpdatePersonaPivote(personaDomicilio, false);			
			this.personasDomicilio.add(personaDomicilio);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarPersonaDomicilio
	
	public void doEliminarPersonaDomicilio() {
		try {			
			if(this.personasDomicilio.remove(this.personaDomicilioSeleccion)) {
				if(!this.personaDomicilioSeleccion.getNuevo())
					addDeleteList(this.personaDomicilioSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el domicilio", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el domicilio", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarPersonaDomicilio	
	
	public void doConsultarPersonaDomicilio() {
		PersonaDomicilio pivote= null;
		try {			
			pivote= this.personasDomicilio.get(this.personasDomicilio.indexOf(this.personaDomicilioSeleccion));
			pivote.setModificar(true);
			this.domicilioPivote= new Domicilio();
			this.domicilioPivote.setIdTipoDomicilio(pivote.getIdTipoDomicilio());
			this.domicilioPivote.setPrincipal(pivote.getIdPrincipal().equals(1L));	
			if(pivote.getDomicilio() != null) {
				this.domicilioPivote.setIdDomicilio(pivote.getDomicilio().getKey());
				this.domicilioPivote.setDomicilio(pivote.getDomicilio());
			} // if
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
	} // doConsultarPersonaDomicilio
	
	public void doActualizarPersonaDomicilio() {
		PersonaDomicilio pivote= null;
		try {			
			pivote= this.personasDomicilio.get(this.personasDomicilio.indexOf(this.personaDomicilioSeleccion));			
			pivote.setModificar(false);
			this.toUpdatePersonaPivote(pivote, true);						
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch				
	} // doActualizarPersonaDomicilio
	
	public void toUpdatePersonaPivote(PersonaDomicilio personaDomicilio, boolean actualizar) throws Exception {
    Long idDomicilio= this.getDomicilioPivote()== null? -1L: this.getDomicilioPivote().getIdDomicilio();
		try {
			if(this.domicilio.getPrincipal()) {
				for(PersonaDomicilio record: this.personasDomicilio)
					record.setIdPrincipal(0L);
			} // if
			if(this.domicilio.getDomicilio()!= null){
    		personaDomicilio.setIdDomicilio(idDomicilio);
	  		personaDomicilio.setIdPrincipal(this.domicilio.getPrincipal()? 1L: 2L);
			} // if	
			personaDomicilio.setDomicilio(new Entity(idDomicilio));
			personaDomicilio.setIdUsuario(JsfBase.getIdUsuario());
			personaDomicilio.setIdTipoDomicilio(this.domicilio.getIdTipoDomicilio());
			if(!actualizar)
				personaDomicilio.setConsecutivo(this.personasDomicilio.size() + 1L);
			personaDomicilio.setIdEntidad(this.domicilio.getIdEntidad());
			personaDomicilio.setIdMunicipio(this.domicilio.getIdMunicipio());
			personaDomicilio.setIdLocalidad(this.domicilio.getLocalidad());
			personaDomicilio.setCodigoPostal(this.domicilio.getCodigoPostal());
			personaDomicilio.setCalle(this.domicilio.getCalle());
			personaDomicilio.setExterior(this.domicilio.getNumeroExterior());
			personaDomicilio.setInterior(this.domicilio.getNumeroInterior());
			personaDomicilio.setEntreCalle(this.domicilio.getEntreCalle());
			personaDomicilio.setyCalle(this.domicilio.getYcalle());
			personaDomicilio.setColonia(this.domicilio.getAsentamiento());
			personaDomicilio.setCodigoPostal(this.domicilio.getCodigoPostal());
			personaDomicilio.setNuevoCp(this.domicilio.getCodigoPostal()!= null && !Cadena.isVacio(this.domicilio.getCodigoPostal()));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toUpdatePersonaPivote
        
	public void doAgregarPersonaBeneficiario() {
		PersonaBeneficiario perBeneficiario= null;
		try {								
			perBeneficiario= new PersonaBeneficiario(this.contadores.getTotalPersonasBeneficiarios()+ this.countIndice, ESql.INSERT, true);	
			setValuesPersonaBeneficiario(perBeneficiario, false);			
			this.personasBeneficiarios.add(perBeneficiario);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarPersonaBeneficiario
	
	public void doEliminarPersonaBeneficiario() {
		try {			
			if(this.personasBeneficiarios.remove(this.personaBeneficiarioSeleccion)) {
				if(!this.personaBeneficiarioSeleccion.getNuevo())
					addDeleteList(this.personaBeneficiarioSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el domicilio", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el domicilio", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarPersonaBeneficiario	
	
	public void doConsultarPersonaBeneficiario() {
		PersonaBeneficiario pivote= null;
		try {			
			pivote= this.personasBeneficiarios.get(this.personasBeneficiarios.indexOf(this.personaBeneficiarioSeleccion));
			pivote.setModificar(true);
			this.beneficiarioPivote= new PersonaBeneficiario();
			this.beneficiarioPivote.setFechaNacimiento(pivote.getFechaNacimiento());
			this.beneficiarioPivote.setIdTipoParentesco(pivote.getIdTipoParentesco());	
			this.beneficiarioPivote.setMaterno(pivote.getMaterno());
			this.beneficiarioPivote.setPaterno(pivote.getPaterno());
			this.beneficiarioPivote.setNombre(pivote.getNombre());			
			this.beneficiarioPivote.setContacto(pivote.getContacto());			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // doConsultarPersonaBeneficiario
	
	public void doActualizarPersonaBeneficiario() {
		PersonaBeneficiario pivote= null;
		try {			
			pivote= this.personasBeneficiarios.get(this.personasBeneficiarios.indexOf(this.personaBeneficiarioSeleccion));			
			pivote.setModificar(false);
			setValuesPersonaBeneficiario(pivote, true);						
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch				
	} // doActualizarPersonaBeneficiario
	
	private void setValuesPersonaBeneficiario(PersonaBeneficiario personaBeneficiario, boolean actualizar) throws Exception{
		try {			
			personaBeneficiario.setIdUsuario(JsfBase.getIdUsuario());			
			if(!actualizar)
				personaBeneficiario.setConsecutivo(this.personasBeneficiarios.size() + 1L);
			personaBeneficiario.setFechaNacimiento(this.personaBeneficiario.getFechaNacimiento());
			personaBeneficiario.setIdTipoParentesco(this.personaBeneficiario.getIdTipoParentesco());
			personaBeneficiario.setMaterno(this.personaBeneficiario.getMaterno());
			personaBeneficiario.setPaterno(this.personaBeneficiario.getPaterno());
			personaBeneficiario.setNombre(this.personaBeneficiario.getNombre());			
			personaBeneficiario.setContacto(this.personaBeneficiario.getContacto());			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // setValuesPersonaBeneficiario
	
	public void doAgregarClienteTipoContacto() {
		PersonaTipoContacto personaTipoContacto= null;
		try {					
			personaTipoContacto= new PersonaTipoContacto(this.contadores.getTotalPersonasTipoContacto()+ this.countIndice, ESql.INSERT, true, null);				
			personaTipoContacto.setOrden(this.personasTiposContacto.size() + 1L);
			this.personasTiposContacto.add(personaTipoContacto);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarClienteTipoContacto
	
	public void doAgregarPersonaBanco() {
		PersonaBanco personaBanco= null;
		try {					
			personaBanco= new PersonaBanco(this.contadores.getTotalPersonasBancos()+ this.countIndice, ESql.INSERT, true, null);							
			this.personasBancos.add(personaBanco);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarClienteTipoContacto
	
	public void doEliminarClienteTipoContacto() {
		try {			
			if(this.personasTiposContacto.remove(this.personaTipoContactoSeleccion)) {
				if(!this.personaTipoContactoSeleccion.getNuevo())
					this.addDeleteList(this.personaTipoContactoSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el tipo de contacto", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el tipo de contacto", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarClienteTipoContacto
	
	public void doEliminarPersonaBanco() {
		try {			
			if(this.personasBancos.remove(this.personaBancoSeleccion)) {
				if(!this.personaBancoSeleccion.getNuevo())
					this.addDeleteList(this.personaBancoSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el banco", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el banco", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarPersonaBanco
  
	private void addDeleteList(IBaseDto dto) throws Exception{
		Transaccion transaccion= null;
		try {
			transaccion= new Transaccion(dto);
			transaccion.ejecutar(EAccion.DEPURAR);
			//this.deleteList.add(dto);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // addDeleteList
	
	public void doSeleccionarPrincipal(PersonaBanco principal) {
		try {
			for(PersonaBanco perBanco: this.personasBancos) {
				if(!perBanco.equals(principal)) {
					perBanco.setIdPrincipal(2L);
					perBanco.setPrincipal(false);
				} // if					
			} // for
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	}
  
}
