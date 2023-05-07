package mx.org.kaana.mantic.catalogos.personas.reglas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.keet.db.dto.TcKeetDeudoresDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.comun.MotorBusquedaCatalogos;
import mx.org.kaana.mantic.catalogos.personas.beans.PersonaBanco;
import mx.org.kaana.mantic.catalogos.personas.beans.PersonaBeneficiario;
import mx.org.kaana.mantic.catalogos.personas.beans.PersonaDomicilio;
import mx.org.kaana.mantic.catalogos.personas.beans.PersonaTipoContacto;
import mx.org.kaana.mantic.catalogos.personas.beans.Especialidad;
import mx.org.kaana.mantic.db.dto.TcManticDomiciliosDto;
import mx.org.kaana.mantic.db.dto.TcManticPersonasDto;
import mx.org.kaana.mantic.db.dto.TrManticEmpresaPersonalDto;

public class MotorBusqueda extends MotorBusquedaCatalogos implements Serializable{

	private static final long serialVersionUID= 5366287658013154045L;	
	private static final Long PRINCIPAL       = 1L;
	private Long idPersona;
	private Long idEmpresaPersona;

	public MotorBusqueda(Long idPersona) {
	  this(idPersona, -1L);
	}
	
	public MotorBusqueda(Long idPersona, Long idEmpresaPersona) {
		this.idPersona       = idPersona;
		this.idEmpresaPersona= idEmpresaPersona;
	}

	public TcManticPersonasDto toPersona() throws Exception {
		TcManticPersonasDto regresar= null;
		try {
			regresar= (TcManticPersonasDto) DaoFactory.getInstance().findById(TcManticPersonasDto.class, this.idPersona);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toPersona	
	
	public List<PersonaDomicilio> toPersonasDomicilio() throws Exception {
		return toPersonasDomicilio(false);
	}
	
	public List<PersonaDomicilio> toPersonasDomicilio(boolean update) throws Exception {
		List<PersonaDomicilio> regresar= null;
		TcManticDomiciliosDto domicilio= null;
		Map<String, Object>params      = null;
		Entity entityDomicilio         = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_persona=" + this.idPersona);
			regresar= DaoFactory.getInstance().toEntitySet(PersonaDomicilio.class, "TrManticPersonaDomicilioDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
			for(PersonaDomicilio personaDomicilio: regresar){
				personaDomicilio.setIdLocalidad(toLocalidad(personaDomicilio.getIdDomicilio()));
				personaDomicilio.setIdMunicipio(toMunicipio(personaDomicilio.getIdLocalidad().getKey()));
				personaDomicilio.setIdEntidad(toEntidad(personaDomicilio.getIdMunicipio().getKey()));
				if(update){
					domicilio= toDomicilio(personaDomicilio.getIdDomicilio());
					personaDomicilio.setCalle(domicilio.getCalle());
					personaDomicilio.setCodigoPostal(domicilio.getCodigoPostal());
					personaDomicilio.setColonia(domicilio.getAsentamiento());
					personaDomicilio.setEntreCalle(domicilio.getEntreCalle());
					personaDomicilio.setyCalle(domicilio.getYcalle());
					personaDomicilio.setExterior(domicilio.getNumeroExterior());
					personaDomicilio.setInterior(domicilio.getNumeroInterior());
					entityDomicilio= new Entity(personaDomicilio.getIdDomicilio());
					entityDomicilio.put("idEntidad", new Value("idEntidad", personaDomicilio.getIdEntidad().getKey()));
					entityDomicilio.put("idMunicipio", new Value("idMunicipio", personaDomicilio.getIdMunicipio().getKey()));
					entityDomicilio.put("idLocalidad", new Value("idLocalidad", personaDomicilio.getIdLocalidad().getKey()));
					personaDomicilio.setDomicilio(entityDomicilio);
				} // if				
			} // for
		} // try
		catch (Exception e) {		
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toClientesDomicilio
	
	public List<PersonaTipoContacto> toPersonasTipoContacto() throws Exception {
		List<PersonaTipoContacto> regresar= null;
		Map<String, Object>params    = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_persona=" + this.idPersona);
			regresar= DaoFactory.getInstance().toEntitySet(PersonaTipoContacto.class, "TrManticPersonaTipoContactoDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {		
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toClientesTipoContacto	
	
	public Long toClienteRepresentante() throws Exception{
		Long regresar= -1L;
		Entity registro= null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_representante=" + this.idPersona + " and id_principal=1");
			registro= (Entity) DaoFactory.getInstance().toEntity("TrManticClienteRepresentanteDto", "row", params);
			if(registro!= null)
				regresar= registro.toLong("idCliente");
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		return regresar;
	}
	
	public Long toProveedorAgente() throws Exception{
		Long regresar= -1L;
		Entity registro= null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_agente=" + this.idPersona + " and id_principal=1");
			registro= (Entity) DaoFactory.getInstance().toEntity("TrManticProveedorAgenteDto", "row", params);
			if(registro!= null)
				regresar= registro.toLong("idProveedor");
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		return regresar;
	}
	
	public Long toEmpresaPersona() throws Exception{
		Long regresar= -1L;
		TrManticEmpresaPersonalDto empresaPersona= toDetallePersona();
		if(empresaPersona!= null && empresaPersona.isValid())
				regresar= empresaPersona.getIdEmpresa();
		return regresar;
	}
	
	public Long toPuestoPersona() throws Exception{
		Long regresar= -1L;
		TrManticEmpresaPersonalDto puestoPersona= toDetallePersona();
		if(puestoPersona!= null && puestoPersona.isValid())
				regresar= puestoPersona.getIdPuesto();
		return regresar;
	} // toPuestoPersona
	
	public Long toContratistaPersona() throws Exception{
		Long regresar= -1L;
		TrManticEmpresaPersonalDto contratistaPersona= toDetallePersona();
		if(contratistaPersona!= null && contratistaPersona.isValid())
				regresar= contratistaPersona.getIdContratista();
		return regresar;
	} // toPuestoPersona
	
	public TrManticEmpresaPersonalDto toDetallePersona() throws Exception{
		TrManticEmpresaPersonalDto regresar= null;
		Map<String, Object>params          = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_persona=" + this.idPersona);
			regresar= (TrManticEmpresaPersonalDto) DaoFactory.getInstance().findFirst(TrManticEmpresaPersonalDto.class, "row", params);			
			if(regresar!= null)
			  this.idEmpresaPersona= regresar.getIdEmpresaPersona();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toPuestoPersona
	
	public TcKeetDeudoresDto toDeudor(Long idEmpresaPersona) throws Exception{
		TcKeetDeudoresDto regresar= null;
		Map<String, Object>params = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_empresa_persona=" + idEmpresaPersona);
			regresar= (TcKeetDeudoresDto) DaoFactory.getInstance().findFirst(TcKeetDeudoresDto.class, "row", params);	
			if(regresar== null)
				regresar= new TcKeetDeudoresDto();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toPuestoPersona
	
	public List<PersonaBeneficiario> toPersonasBeneficiarios() throws Exception{
		List<PersonaBeneficiario> regresar= null;
		Map<String, Object>params         = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_empresa_persona=" + this.idEmpresaPersona);
			regresar= DaoFactory.getInstance().toEntitySet(PersonaBeneficiario.class, "TcKeetPersonasBeneficiariosDto", "row", params, Constantes.SQL_TODOS_REGISTROS);			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toPuestoPersona
	
	public List<PersonaBanco> toPersonasBancos() throws Exception {
		List<PersonaBanco> regresar= null;
		Map<String, Object>params  = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_empresa_persona=" + this.idEmpresaPersona);
			regresar= DaoFactory.getInstance().toEntitySet(PersonaBanco.class, "TcKeetPersonasBancosDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
			if(!regresar.isEmpty()){
				for(PersonaBanco personaBanco: regresar){
					if(personaBanco.getIdPrincipal().equals(PRINCIPAL))
						personaBanco.setPrincipal(Boolean.TRUE);
				} // for
			} // if
		} // try
		catch (Exception e) {		
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toClientesTipoContacto	
  
	public List<Especialidad> toPersonaDepartamentos(Long idEmpresaPersona) throws Exception{
		List<Especialidad> regresar= null;
		Map<String, Object>params  = new HashMap<>();
		try {
			params.put("idEmpresaPersona", idEmpresaPersona);
			regresar= (List<Especialidad>) DaoFactory.getInstance().toEntitySet(Especialidad.class, "TcKeetContratistasDepartamentosDto", "departamentos", params);	
			if(regresar== null)
        regresar= new ArrayList<>();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toPersonaDepartamentos
	
  
}
