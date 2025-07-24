package mx.org.kaana.kalan.catalogos.acreedores.reglas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.wassenger.Bonanza;
import mx.org.kaana.mantic.catalogos.personas.beans.PersonaTipoContacto;
import mx.org.kaana.kalan.catalogos.acreedores.beans.AcreedorBanca;
import mx.org.kaana.kalan.catalogos.acreedores.beans.AcreedorContactoAgente;
import mx.org.kaana.kalan.catalogos.acreedores.beans.AcreedorDomicilio;
import mx.org.kaana.kalan.catalogos.acreedores.beans.AcreedorTipoContacto;
import mx.org.kaana.kalan.catalogos.acreedores.beans.RegistroAcreedor;
import mx.org.kaana.mantic.db.dto.TcManticDomiciliosDto;
import mx.org.kaana.mantic.db.dto.TcManticPersonasDto;
import mx.org.kaana.mantic.db.dto.TcManticAcreedoresBancosDto;
import mx.org.kaana.mantic.db.dto.TcManticAcreedoresDto;
import mx.org.kaana.mantic.db.dto.TrManticAcreedorDomicilioDto;
import mx.org.kaana.mantic.db.dto.TrManticAcreedorTipoContactoDto;
import mx.org.kaana.mantic.db.dto.TrManticPersonaTipoContactoDto;
import mx.org.kaana.mantic.db.dto.TrManticAcreedorAgenteDto;
import mx.org.kaana.mantic.enums.ETipoPersona;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

  private static final Log LOG = LogFactory.getLog(Transaccion.class);
	private IBaseDto dto;
	private RegistroAcreedor registroAcreedor;
	private String messageError;
	
	public Transaccion(IBaseDto dto) {
		this.dto= dto;
	}
	
  public Transaccion(RegistroAcreedor registroAcreedor) {
    this.registroAcreedor= registroAcreedor;
  }

  @Override
  protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
    boolean regresar = false;
    try {
			if(this.registroAcreedor!= null)
				this.registroAcreedor.getAcreedor().setIdEmpresa(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      switch (accion) {
        case AGREGAR:
          regresar = this.procesarAcreedor(sesion);
          break;
        case MODIFICAR:
          regresar = this.actualizarAcreedor(sesion);
          break;
        case ELIMINAR:
          regresar = this.eliminarAcreedor(sesion);
          break;
				case DEPURAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.dto)>= 1L;
					break;
      } // switch
      if (!regresar) {
        throw new Exception(this.messageError);
      } // if
    } // try
    catch (Exception e) {
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
    } // catch		
    return regresar;
  } // ejecutar
	
	private boolean procesarAcreedor(Session sesion) throws Exception {
    boolean regresar = false;
    Long idAcreedor = -1L;
    try {
      this.messageError = "Error al registrar el acreedor";
      if (eliminarRegistros(sesion)) {
        this.registroAcreedor.getAcreedor().setIdUsuario(JsfBase.getIdUsuario());
        this.registroAcreedor.getAcreedor().setIdEmpresa(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
        this.registroAcreedor.getAcreedor().setIdActivo(1L);
        idAcreedor = DaoFactory.getInstance().insert(sesion, this.registroAcreedor.getAcreedor());
        if (this.registraDomicilios(sesion, idAcreedor)) {
          if (this.registraAcreedoresAgentes(sesion, idAcreedor)) {
            if(this.registraAcreedoresTipoContacto(sesion, idAcreedor)){
							if(this.registraAcreedoresServicios(sesion, idAcreedor)){
								if(this.registraAcreedoresTransferencia(sesion, idAcreedor)) {
                  this.registroAcreedor.getPortal().setIdAcreedor(idAcreedor);
                  regresar= DaoFactory.getInstance().insert(sesion, this.registroAcreedor.getPortal())>= 1L;
								} // if
							} // if
						} // if
          } // if
        } // if
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    return regresar;
  } 

  private boolean actualizarAcreedor(Session sesion) throws Exception {
    boolean regresar = false;
    Long idAcreedor = -1L;
    try {
      idAcreedor = this.registroAcreedor.getIdAcreedor();
      if (this.registraDomicilios(sesion, idAcreedor)) {
        if (this.registraAcreedoresAgentes(sesion, idAcreedor)) {
          if (this.registraAcreedoresTipoContacto(sesion, idAcreedor)) {
            if (this.registraAcreedoresServicios(sesion, idAcreedor)){
              if (this.registraAcreedoresTransferencia(sesion, idAcreedor)){
                if (this.registroAcreedor.getPortal().isValid()){
                  if(DaoFactory.getInstance().update(sesion, this.registroAcreedor.getPortal())>= 0L){
                    regresar = DaoFactory.getInstance().update(sesion, this.registroAcreedor.getAcreedor()) >= 1L;
                  } // if
                } // if
                else{										
                  regresar = DaoFactory.getInstance().update(sesion, this.registroAcreedor.getAcreedor()) >= 1L;
                  if(regresar && this.registroAcreedor.getPortal()!= null && !Cadena.isVacio(this.registroAcreedor.getPortal())){
                    this.registroAcreedor.getPortal().setIdAcreedor(idAcreedor);
                    regresar= DaoFactory.getInstance().insert(sesion, this.registroAcreedor.getPortal())>= 1L;
                  } // if
                } // else
              } // if
            } // if
          } // if
        } // if
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    return regresar;
  } 

  private boolean eliminarAcreedor(Session sesion) throws Exception {
    boolean regresar = false;
    Map<String, Object> params = new HashMap<>();
    try {
      params.put("idAcreedor", this.registroAcreedor.getIdAcreedor());
      if (DaoFactory.getInstance().deleteAll(sesion, TrManticAcreedorDomicilioDto.class, params) > -1L) {
        if (DaoFactory.getInstance().deleteAll(sesion, TrManticAcreedorAgenteDto.class, params) > -1L) {
          if (DaoFactory.getInstance().deleteAll(sesion, TrManticAcreedorTipoContactoDto.class, params) > -1L) 
  				  regresar = DaoFactory.getInstance().delete(sesion, TcManticAcreedoresDto.class, this.registroAcreedor.getIdAcreedor()) >= 1L;
        } // if
      } // if
    } // try // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  } 

  private Boolean registraDomicilios(Session sesion, Long idAcreedor) throws Exception {
    Boolean regresar= Boolean.FALSE;
    if(!Objects.equals(this.registroAcreedor.getAcreedoresDomicilio(), null) && this.registroAcreedor.getAcreedoresDomicilio().size()> 0)
      regresar= this.registraAcreedoresDomicilios(sesion, idAcreedor);
    else {
      TcManticDomiciliosDto pivote   = (TcManticDomiciliosDto)DaoFactory.getInstance().findById(sesion, TcManticDomiciliosDto.class, 1L);
      TcManticDomiciliosDto domicilio= new TcManticDomiciliosDto(
        pivote.getAsentamiento(), // String asentamiento, 
        pivote.getIdLocalidad(), // Long idLocalidad, 
        pivote.getCodigoPostal(), // String codigoPostal, 
        pivote.getLatitud(), // String latitud, 
        pivote.getEntreCalle(), // String entreCalle, 
        pivote.getCalle(), // String calle, 
        -1L, // Long idDomicilio, 
        pivote.getNumeroInterior(), // String numeroInterior,  
        pivote.getYcalle(), // String ycalle, 
        pivote.getLongitud(), // String longitud, 
        pivote.getNumeroExterior(), // String numeroExterior, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        ""
      );
      DaoFactory.getInstance().insert(sesion, domicilio);
      TrManticAcreedorDomicilioDto relacion= new TrManticAcreedorDomicilioDto(
        -1L, // Long idAcreedorDomicilio, 
        idAcreedor, // Long idAcreedor, 
        domicilio.getIdUsuario(), // Long idUsuario, 
        1L, // Long idTipoDomicilio, 
        domicilio.getIdDomicilio(), // Long idDomicilio, 
        1L, // Long idPrincipal, 
        "" // String observaciones      
      );
      regresar= DaoFactory.getInstance().insert(sesion, relacion)> 0L;
    } // if
    return regresar;
  }
  
  private boolean registraAcreedoresDomicilios(Session sesion, Long idAcreedor) throws Exception {
    TrManticAcreedorDomicilioDto dto = null;
    ESql sqlAccion    = null;
    int count         = 0;
    int countPrincipal= 0;
    boolean validate  = Boolean.FALSE;
    boolean regresar  = Boolean.FALSE;
    try {
			if(this.registroAcreedor.getAcreedoresDomicilio().size()== 1)
				this.registroAcreedor.getAcreedoresDomicilio().get(0).setIdPrincipal(1L);
      for (AcreedorDomicilio proveedorDomicilio : this.registroAcreedor.getAcreedoresDomicilio()) {								
				if(proveedorDomicilio.getIdPrincipal().equals(1L))
					countPrincipal++;
        else
					proveedorDomicilio.setIdPrincipal(2L);
				if(countPrincipal== 0 && Objects.equals(this.registroAcreedor.getAcreedoresDomicilio().size()-1, count))
					proveedorDomicilio.setIdPrincipal(1L);
        proveedorDomicilio.setIdAcreedor(idAcreedor);
        proveedorDomicilio.setIdUsuario(JsfBase.getIdUsuario());
				proveedorDomicilio.setIdDomicilio(toIdDomicilio(sesion, proveedorDomicilio));		
        dto = (TrManticAcreedorDomicilioDto) proveedorDomicilio;
        sqlAccion = proveedorDomicilio.getSqlAccion();
        switch (sqlAccion) {
          case INSERT:
            dto.setIdAcreedorDomicilio(-1L);
            validate = registrar(sesion, dto);
            break;
          case UPDATE:
            validate = actualizar(sesion, dto);
            break;
        } // switch
        if (validate) {
          count++;
        }
      } // for		
      regresar = count == this.registroAcreedor.getAcreedoresDomicilio().size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los domicilios, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraAcreedoresDomicilios

  private boolean registraAcreedoresAgentes(Session sesion, Long idAcreedor) throws Exception {
    TrManticAcreedorAgenteDto dto = null;
    ESql sqlAccion = null;
    int count = 0;
    int countPrincipal = 0;
    boolean validate = false;
    boolean regresar = false;
    try {
			if(this.registroAcreedor.getPersonasTiposContacto().size()== 1)
					this.registroAcreedor.getPersonasTiposContacto().get(0).setIdPrincipal(1L);
      for (AcreedorContactoAgente proveedorRepresentante : this.registroAcreedor.getPersonasTiposContacto()) {				
				if(proveedorRepresentante.getIdPrincipal().equals(1L))
					countPrincipal++;
				if(countPrincipal== 0 && this.registroAcreedor.getPersonasTiposContacto().size()-1 == count)
					proveedorRepresentante.setIdPrincipal(1L);
        proveedorRepresentante.setIdAcreedor(idAcreedor);
        proveedorRepresentante.setIdUsuario(JsfBase.getIdUsuario());
        proveedorRepresentante.setIdAgente(addAgente(sesion, proveedorRepresentante));
        dto = (TrManticAcreedorAgenteDto) proveedorRepresentante;
        sqlAccion = proveedorRepresentante.getSqlAccion();
        switch (sqlAccion) {
          case INSERT:
            dto.setIdAcreedorAgente(-1L);
            validate = registrar(sesion, dto);
            break;
          case UPDATE:
            validate = actualizar(sesion, dto);
            break;
        } // switch
        if (validate) {
          count++;
        }
      } // for		
      regresar = count == this.registroAcreedor.getPersonasTiposContacto().size();
    } // try // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los agentes, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraAcreedoresAgentes
	
	private Long addAgente(Session sesion, AcreedorContactoAgente proveedorAgente) throws Exception{
		Long regresar= -1L;
		TcManticPersonasDto representante= null;
		try {
			representante= new TcManticPersonasDto();
			representante.setNombres(proveedorAgente.getNombres());
			representante.setPaterno(proveedorAgente.getPaterno());
			representante.setMaterno(proveedorAgente.getMaterno());
			representante.setIdTipoPersona(ETipoPersona.REPRESENTANTE_LEGAL.getIdTipoPersona());	
			representante.setIdTipoSexo(1L);
			representante.setEstilo(Configuracion.getInstance().getEmpresa("theme"));
			representante.setIdPersonaTitulo(1L);		
			regresar= DaoFactory.getInstance().insert(sesion, representante);
			if(regresar > -1L)
				registraPersonasTipoContacto(sesion, regresar, proveedorAgente.getContactos());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			this.messageError = "Error al registrar el agente, verifique que no haya duplicados";
		} // finally
		return regresar;
	} // addRepresentante

	private boolean registraPersonasTipoContacto(Session sesion, Long idPersona, List<PersonaTipoContacto> tiposContactos) throws Exception {
    TrManticPersonaTipoContactoDto dto = null;
    ESql sqlAccion = null;
    int count = 0;
    boolean validate = false;
    boolean regresar = false;
    try {
      for (PersonaTipoContacto personaTipoContacto : tiposContactos) {
				if(personaTipoContacto.getValor()!= null && !Cadena.isVacio(personaTipoContacto.getValor())){
					personaTipoContacto.setIdPersona(idPersona);
					personaTipoContacto.setIdUsuario(JsfBase.getIdUsuario());
					personaTipoContacto.setOrden(count+1L);
					dto = (TrManticPersonaTipoContactoDto) personaTipoContacto;
					sqlAccion = personaTipoContacto.getSqlAccion();
					switch (sqlAccion) {
						case INSERT:
							dto.setIdPersonaTipoContacto(-1L);
							validate = registrar(sesion, dto);
							break;
						case UPDATE:
							validate = actualizar(sesion, dto);
							break;
					} // switch
				} // if
				else
					validate= true;
        if (validate) 
          count++;        
      } // for		
      regresar = count == tiposContactos.size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los tipos de contacto, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraAcreedoresTipoContacto
	
  private boolean registraAcreedoresTipoContacto(Session sesion, Long idAcreedor) throws Exception {
    TrManticAcreedorTipoContactoDto dto = null;
    ESql sqlAccion = null;
    int count = 0;
    int orden = 0;
    boolean validate = false;
    boolean regresar = false;
    try {
      for (AcreedorTipoContacto proveedorTipoContacto : this.registroAcreedor.getAcreedoresTipoContacto()) {
				if(proveedorTipoContacto.getValor()!= null && !Cadena.isVacio(proveedorTipoContacto.getValor())){
					proveedorTipoContacto.setOrden(orden + 1L);
					proveedorTipoContacto.setIdAcreedor(idAcreedor);
					proveedorTipoContacto.setIdUsuario(JsfBase.getIdUsuario());
					dto = (TrManticAcreedorTipoContactoDto) proveedorTipoContacto;
					sqlAccion = proveedorTipoContacto.getSqlAccion();
					switch (sqlAccion) {
						case INSERT:
							dto.setIdAcreedorTipoContacto(-1L);
							validate = registrar(sesion, dto);
              // VERIFICAR SI YA FUE NOTIFICADO PARA RECIBIR MENSAJES POR WHATSAPP
              if(dto.getIdPreferido().equals(1L) && (dto.getIdTipoContacto().equals(6L) || dto.getIdTipoContacto().equals(7L) || dto.getIdTipoContacto().equals(8L))) {
                Bonanza notificar= new Bonanza(this.registroAcreedor.getAcreedor().getRazonSocial(), dto.getValor());
                notificar.doSendAcreedor(sesion);
              } // if
							break;
						case UPDATE:
							validate = actualizar(sesion, dto);
							break;
					} // switch
					orden++;
				} // if
				else
					validate= true;
        if (validate) 
          count++;
      } // for		
      regresar = count == this.registroAcreedor.getAcreedoresTipoContacto().size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los tipos de contacto, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraAcreedoresTipoContacto
	
  private boolean registraAcreedoresServicios(Session sesion, Long idAcreedor) throws Exception {
    TcManticAcreedoresBancosDto dto = null;
    ESql sqlAccion = null;
    int count = 0;
    boolean validate = false;
    boolean regresar = false;
    try {
      for (AcreedorBanca proveedorBanca : this.registroAcreedor.getAcreedoresServicio()) {
				if(proveedorBanca.getConvenioCuenta()!= null && !Cadena.isVacio(proveedorBanca.getConvenioCuenta())){
					proveedorBanca.setIdAcreedor(idAcreedor);
					proveedorBanca.setIdUsuario(JsfBase.getIdUsuario());
					dto = (TcManticAcreedoresBancosDto) proveedorBanca;
					sqlAccion = proveedorBanca.getSqlAccion();
					switch (sqlAccion) {
						case INSERT:
							dto.setIdAcreedorBanco(-1L);
							validate = registrar(sesion, dto);
							break;
						case UPDATE:
							validate = actualizar(sesion, dto);
							break;
					} // switch
				} // if
				else
					validate= true;
        if (validate) {
          count++;
        }
      } // for		
      regresar = count == this.registroAcreedor.getAcreedoresServicio().size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar el servicio de banco, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraAcreedoresServicios
	
  private boolean registraAcreedoresTransferencia(Session sesion, Long idAcreedor) throws Exception {
    TcManticAcreedoresBancosDto dto = null;
    ESql sqlAccion = null;
    int count = 0;
    boolean validate = false;
    boolean regresar = false;
    try {
      for (AcreedorBanca proveedorBanca : this.registroAcreedor.getAcreedoresTransferencia()) {
				if(proveedorBanca.getConvenioCuenta()!= null && !Cadena.isVacio(proveedorBanca.getConvenioCuenta())){
					proveedorBanca.setIdAcreedor(idAcreedor);
					proveedorBanca.setIdUsuario(JsfBase.getIdUsuario());
					dto = (TcManticAcreedoresBancosDto) proveedorBanca;
					sqlAccion = proveedorBanca.getSqlAccion();
					switch (sqlAccion) {
						case INSERT:
							dto.setIdAcreedorBanco(-1L);
							validate = registrar(sesion, dto);
							break;
						case UPDATE:
							validate = actualizar(sesion, dto);
							break;
					} // switch
				} // if
				else
					validate= true;
        if (validate) {
          count++;
        }
      } // for		
      regresar = count == this.registroAcreedor.getAcreedoresTransferencia().size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar el servicio de banco, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraAcreedoresTransferencia
	
  private boolean eliminarRegistros(Session sesion) throws Exception {
    boolean regresar= Boolean.TRUE;
    int count       = 0;
    try {
      for (IBaseDto dto : this.registroAcreedor.getDeleteList()) {
        if (DaoFactory.getInstance().delete(sesion, dto) >= 1L) 
          count++;
      } // for
      regresar = count == this.registroAcreedor.getDeleteList().size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al eliminar registros";
    } // finally
    return regresar;
  } // eliminarRegistros

  private boolean registrar(Session sesion, IBaseDto dto) throws Exception {
    return registrarSentencia(sesion, dto) >= 1L;
  } // registrar
  
	private Long registrarSentencia(Session sesion, IBaseDto dto) throws Exception {
    return DaoFactory.getInstance().insert(sesion, dto);
  } // registrar

  private boolean actualizar(Session sesion, IBaseDto dto) throws Exception {
    return DaoFactory.getInstance().update(sesion, dto) >= 1L;
  } // actualizar
	
	private Long toIdDomicilio(Session sesion, AcreedorDomicilio proveedorDomicilio) throws Exception{		
		Entity entityDomicilio= null;
		Long regresar= -1L;
		try {
			entityDomicilio= toDomicilio(sesion, proveedorDomicilio);
			if(entityDomicilio!= null)
				regresar= entityDomicilio.getKey();
			else
				regresar= insertDomicilio(sesion, proveedorDomicilio);					
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarDomicilio	
	
	private Long insertDomicilio(Session sesion, AcreedorDomicilio proveedorDomicilio) throws Exception{
		TcManticDomiciliosDto domicilio= null;
		Long regresar= -1L;
		try {
			domicilio= new TcManticDomiciliosDto();
			domicilio.setIdLocalidad(proveedorDomicilio.getIdLocalidad().getKey());
			domicilio.setAsentamiento(proveedorDomicilio.getColonia());
			domicilio.setCalle(proveedorDomicilio.getCalle());
			domicilio.setCodigoPostal(proveedorDomicilio.getCodigoPostal());
			domicilio.setEntreCalle(proveedorDomicilio.getEntreCalle());
			domicilio.setIdUsuario(JsfBase.getIdUsuario());
			domicilio.setNumeroExterior(proveedorDomicilio.getExterior());
			domicilio.setNumeroInterior(proveedorDomicilio.getInterior());
			domicilio.setYcalle(proveedorDomicilio.getyCalle());
			regresar= DaoFactory.getInstance().insert(sesion, domicilio);
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		return regresar;
	} // insertDomicilio
	
	private Entity toDomicilio(Session sesion, AcreedorDomicilio proveedorDomicilio) throws Exception{
		Entity regresar= null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idLocalidad", proveedorDomicilio.getIdLocalidad().getKey());
			params.put("codigoPostal", proveedorDomicilio.getCodigoPostal());
			params.put("calle", proveedorDomicilio.getCalle());
			params.put("numeroExterior", proveedorDomicilio.getExterior());
			params.put("numeroInterior", proveedorDomicilio.getInterior());
			params.put("asentamiento", proveedorDomicilio.getColonia());
			params.put("entreCalle", proveedorDomicilio.getEntreCalle());
			params.put("yCalle", proveedorDomicilio.getyCalle());
			regresar= (Entity) DaoFactory.getInstance().toEntity(sesion, "TcManticDomiciliosDto", "domicilioExiste", params);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toDomicilio
}
