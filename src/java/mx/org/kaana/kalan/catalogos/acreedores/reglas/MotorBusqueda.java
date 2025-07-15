package mx.org.kaana.kalan.catalogos.acreedores.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.comun.MotorBusquedaCatalogos;
import mx.org.kaana.kalan.catalogos.acreedores.beans.AcreedorBanca;
import mx.org.kaana.kalan.catalogos.acreedores.beans.AcreedorContactoAgente;
import mx.org.kaana.kalan.catalogos.acreedores.beans.AcreedorDomicilio;
import mx.org.kaana.kalan.catalogos.acreedores.beans.AcreedorTipoContacto;
import mx.org.kaana.mantic.db.dto.TcManticDomiciliosDto;
import mx.org.kaana.mantic.db.dto.TcManticPersonasDto;
import mx.org.kaana.mantic.db.dto.TcManticAcreedoresDto;
import mx.org.kaana.mantic.db.dto.TcManticAcreedoresPortalesDto;
import mx.org.kaana.mantic.enums.ETiposCuentas;

public class MotorBusqueda extends MotorBusquedaCatalogos implements Serializable {
	
	private static final long serialVersionUID = 5085305397727758226L;
	private Long idAcreedor;
	
	public MotorBusqueda(Long idAcreedor) {
		this.idAcreedor= idAcreedor;
	} 
	
	public TcManticAcreedoresDto toAcreedor() throws Exception {
		TcManticAcreedoresDto regresar= null;
		try {
			regresar= (TcManticAcreedoresDto) DaoFactory.getInstance().findById(TcManticAcreedoresDto.class, this.idAcreedor);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} 
	
	public List<AcreedorDomicilio> toAcreedoresDomicilio() throws Exception {
		return toAcreedoresDomicilio(false);
	} 
	
	public List<AcreedorDomicilio> toAcreedoresDomicilio(boolean update) throws Exception {
		List<AcreedorDomicilio> regresar= null;
		TcManticDomiciliosDto domicilio = null;
		Map<String, Object>params       = new HashMap<>();
		Entity entityDomicilio          = null;
		try {
			params.put(Constantes.SQL_CONDICION, "id_acreedor=" + this.idAcreedor);
			regresar= DaoFactory.getInstance().toEntitySet(AcreedorDomicilio.class, "TrManticAcreedorDomicilioDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
			for(AcreedorDomicilio proveedorDomicilio: regresar){
				if(proveedorDomicilio.getIdDomicilio()!= null)
					proveedorDomicilio.setIdLocalidad(toLocalidad(proveedorDomicilio.getIdDomicilio()));
				if(proveedorDomicilio.getIdLocalidad()!= null)
					proveedorDomicilio.setIdMunicipio(toMunicipio(proveedorDomicilio.getIdLocalidad().getKey()));
				if(proveedorDomicilio.getIdMunicipio()!= null)
					proveedorDomicilio.setIdEntidad(toEntidad(proveedorDomicilio.getIdMunicipio().getKey()));
				if(update){
					if(proveedorDomicilio.getIdDomicilio()!= null)
						domicilio= toDomicilio(proveedorDomicilio.getIdDomicilio());
					if(domicilio!= null){
						proveedorDomicilio.setCalle(domicilio.getCalle());
						proveedorDomicilio.setCodigoPostal(domicilio.getCodigoPostal());
						proveedorDomicilio.setColonia(domicilio.getAsentamiento());
						proveedorDomicilio.setEntreCalle(domicilio.getEntreCalle());
						proveedorDomicilio.setyCalle(domicilio.getYcalle());
						proveedorDomicilio.setExterior(domicilio.getNumeroExterior());
						proveedorDomicilio.setInterior(domicilio.getNumeroInterior());
					} // if
					if(proveedorDomicilio.getIdDomicilio()!= null){
						entityDomicilio= new Entity(proveedorDomicilio.getIdDomicilio());
						entityDomicilio.put("idEntidad", new Value("idEntidad", proveedorDomicilio.getIdEntidad().getKey()));
						entityDomicilio.put("idMunicipio", new Value("idMunicipio", proveedorDomicilio.getIdMunicipio().getKey()));
						entityDomicilio.put("idLocalidad", new Value("idLocalidad", proveedorDomicilio.getIdLocalidad().getKey()));
						proveedorDomicilio.setDomicilio(entityDomicilio);
					} // if
				} // if				
			} // for
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} 
	
	public List<AcreedorTipoContacto> toAcreedoresTipoContacto() throws Exception {
		List<AcreedorTipoContacto> regresar= null;
		Map<String, Object>params          = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, "id_acreedor="+ this.idAcreedor);
			regresar= DaoFactory.getInstance().toEntitySet(AcreedorTipoContacto.class, "TrManticAcreedorTipoContactoDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {		
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} 
	
	public List<AcreedorTipoContacto> toAllAcreedoresTipoContacto() throws Exception {
		List<AcreedorTipoContacto> regresar= null;
		Map<String, Object>params          = new HashMap<>();
		try {
			params.put("idAcreedor", this.idAcreedor);
			regresar= DaoFactory.getInstance().toEntitySet(AcreedorTipoContacto.class, "TrManticAcreedorTipoContactoDto", "contacto", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {		
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} 
	
	public List<AcreedorContactoAgente> toAgentes() throws Exception {
		List<AcreedorContactoAgente> regresar= null;
		Map<String, Object>params            = new HashMap<>();
		TcManticPersonasDto persona          = null;
		try {
			params.put(Constantes.SQL_CONDICION, "id_acreedor=" + this.idAcreedor);
			regresar= DaoFactory.getInstance().toEntitySet(AcreedorContactoAgente.class, "TrManticAcreedorAgenteDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
			if(!regresar.isEmpty()) {
				for(AcreedorContactoAgente contacto: regresar){
					contacto.setContactos(toPersonaContacto(contacto.getIdAgente()));
					persona= toPersona(contacto.getIdAgente());
					contacto.setNombres(persona.getNombres());
					contacto.setPaterno(persona.getPaterno());
					contacto.setMaterno(persona.getMaterno());
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
	} 
	
	public TcManticAcreedoresPortalesDto toPortal() throws Exception {
		TcManticAcreedoresPortalesDto regresar= null;
		Map<String, Object>params             = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, "id_acreedor=" + this.idAcreedor);
			regresar= (TcManticAcreedoresPortalesDto) DaoFactory.getInstance().toEntity(TcManticAcreedoresPortalesDto.class, "TcManticAcreedoresPortalesDto", params);
			if(regresar== null)
				regresar= new TcManticAcreedoresPortalesDto();
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} 
	
	public List<AcreedorBanca> toServicios() throws Exception {
		return toAcreedorBanca(ETiposCuentas.SERVICIOS);
	} 
	
	public List<AcreedorBanca> toTransferencias() throws Exception {
		return toAcreedorBanca(ETiposCuentas.TRANSFERENCIAS);
	} 
	
	private List<AcreedorBanca> toAcreedorBanca(ETiposCuentas tipoCuenta) throws Exception {
		List<AcreedorBanca> regresar= null;
		Map<String, Object>params   = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, " id_acreedor="+ this.idAcreedor+ " and id_tipo_cuenta=" + tipoCuenta.getKey());
			regresar= DaoFactory.getInstance().toEntitySet(AcreedorBanca.class, "TcManticAcreedoresBancosDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} 
  
}
