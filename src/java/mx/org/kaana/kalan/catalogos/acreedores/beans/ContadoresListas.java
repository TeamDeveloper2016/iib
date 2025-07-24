package mx.org.kaana.kalan.catalogos.acreedores.beans;

import java.util.Collections;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;

public class ContadoresListas {
	
	private static final Long INCREMENTO= 10000L;
	private Long totalAcreedoresDomicilios;
	private Long totalAcreedoresAgentes;
	private Long totalAcreedoresTipoContacto;	
	private Long totalPersonasTipoContacto;	
	private Long totalAcreedoresServicio;	
	private Long totalAcreedoresTransferencia;	
	
	public ContadoresListas() {
		init();
	} // ContadoresListas
	
	public ContadoresListas(Long totalAcreedoresDomicilios, Long totalAcreedoresAgentes, Long totalAcreedoresTipoContacto, Long totalPersonasTipoContacto, Long totalAcreedoresServicio, Long totalAcreedoresTransferencia) {
		this.totalAcreedoresDomicilios   = totalAcreedoresDomicilios;
		this.totalAcreedoresAgentes      = totalAcreedoresAgentes;
		this.totalAcreedoresTipoContacto = totalAcreedoresTipoContacto;
		this.totalPersonasTipoContacto    = totalPersonasTipoContacto;	
		this.totalAcreedoresServicio     = totalAcreedoresServicio;
		this.totalAcreedoresTransferencia= totalAcreedoresTransferencia;
	} // ContadoresListas

	public Long getTotalAcreedoresDomicilios() {
		return totalAcreedoresDomicilios;
	}

	public void setTotalAcreedoresDomicilios(Long totalAcreedoresDomicilios) {
		this.totalAcreedoresDomicilios = totalAcreedoresDomicilios;
	}

	public Long getTotalAcreedoresAgentes() {
		return totalAcreedoresAgentes;
	}

	public void setTotalAcreedoresAgentes(Long totalAcreedoresAgentes) {
		this.totalAcreedoresAgentes = totalAcreedoresAgentes;
	}

	public Long getTotalAcreedoresTipoContacto() {
		return totalAcreedoresTipoContacto;
	}

	public void setTotalAcreedoresTipoContacto(Long totalAcreedoresTipoContacto) {
		this.totalAcreedoresTipoContacto = totalAcreedoresTipoContacto;
	}

	public Long getTotalPersonasTipoContacto() {
		return totalPersonasTipoContacto;
	}

	public void setTotalPersonasTipoContacto(Long totalPersonasTipoContacto) {
		this.totalPersonasTipoContacto = totalPersonasTipoContacto;
	}

	public Long getTotalAcreedoresServicio() {
		return totalAcreedoresServicio;
	}

	public void setTotalAcreedoresServicio(Long totalAcreedoresServicio) {
		this.totalAcreedoresServicio = totalAcreedoresServicio;
	}

	public Long getTotalAcreedoresTransferencia() {
		return totalAcreedoresTransferencia;
	}

	public void setTotalAcreedoresTransferencia(Long totalAcreedoresTransferencia) {
		this.totalAcreedoresTransferencia = totalAcreedoresTransferencia;
	}
	
	private void init() {
		try {
			this.totalAcreedoresDomicilios   = toMaxAcreedorDomicilio();
			this.totalAcreedoresAgentes      = toMaxAcreedorAgentes();
			this.totalAcreedoresTipoContacto = toMaxAcreedorTiposContactos();		
			this.totalPersonasTipoContacto   = toMaxPersonaTiposContactos();
			this.totalAcreedoresServicio     = toMaxAcreedorBanco();
			this.totalAcreedoresTransferencia= toMaxAcreedorBanco();
		} // try
		catch (Exception e) {
			Error.mensaje(e);						
		} // catch		
	} 
	
	private Long toMaxAcreedorDomicilio() throws Exception{		
		return toMax("TrManticAcreedorDomicilioDto");
	} 
	
	private Long toMaxAcreedorAgentes() throws Exception{		
		return toMax("TrManticAcreedorAgenteDto");
	} 
	
	private Long toMaxAcreedorTiposContactos() throws Exception{		
		return toMax("TrManticAcreedorTipoContactoDto");
	} 
	
	private Long toMaxPersonaTiposContactos() throws Exception{		
		return toMax("TrManticPersonaTipoContactoDto");
	} 
	
	private Long toMaxAcreedorBanco() throws Exception{		
		return toMax("TcManticAcreedoresBancosDto");
	} 
	
	private Long toMax(String vista) throws Exception{
		Long regresar= 0L;
		Value maximo = null;
		try {
			maximo= DaoFactory.getInstance().toField(vista, "maximo", Collections.EMPTY_MAP, "maximo");
			if(maximo.getData()!= null)
				regresar= Long.valueOf(maximo.toString())+ INCREMENTO;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} 
  
}
