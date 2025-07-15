package mx.org.kaana.kalan.catalogos.acreedores.beans;

import java.util.Collections;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;

public class ContadoresListas {
	
	private static final Long INCREMENTO= 10000L;
	private Long totalAcreedoresDomicilios;
	private Long totalAcreedoresAgentes;
	private Long totalAcreedoresPago;
	private Long totalAcreedoresTipoContacto;	
	private Long totalPersonasTipoContacto;	
	private Long totalAcreedoresServicio;	
	private Long totalAcreedoresTransferencia;	
	
	public ContadoresListas() {
		init();
	} // ContadoresListas
	
	public ContadoresListas(Long totalAcreedoresDomicilios, Long totalAcreedoresAgentes, Long totalAcreedoresTipoContacto, Long totalPersonasTipoContacto, Long totalAcreedoresPago, Long totalAcreedoresServicio, Long totalAcreedoresTransferencia) {
		this.totalAcreedoresDomicilios   = totalAcreedoresDomicilios;
		this.totalAcreedoresAgentes      = totalAcreedoresAgentes;
		this.totalAcreedoresTipoContacto = totalAcreedoresTipoContacto;
		this.totalPersonasTipoContacto    = totalPersonasTipoContacto;	
		this.totalAcreedoresPago         = totalAcreedoresPago;
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

	public Long getTotalAcreedoresPago() {
		return totalAcreedoresPago;
	}

	public void setTotalAcreedoresPago(Long totalAcreedoresPago) {
		this.totalAcreedoresPago = totalAcreedoresPago;
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
	
	private void init(){
		try {
			this.totalAcreedoresDomicilios   = toMaxProveedorDomicilio();
			this.totalAcreedoresAgentes      = toMaxProveedorAgentes();
			this.totalAcreedoresTipoContacto = toMaxProveedorTiposContactos();		
			this.totalPersonasTipoContacto    = toMaxPersonaTiposContactos();
			this.totalAcreedoresPago         = toMaxProveedorPago();
			this.totalAcreedoresServicio     = toMaxProveedorBanco();
			this.totalAcreedoresTransferencia= toMaxProveedorBanco();
		} // try
		catch (Exception e) {
			mx.org.kaana.libs.formato.Error.mensaje(e);						
		} // catch		
	} // init
	
	private Long toMaxProveedorPago() throws Exception{
		return toMax("TrManticProveedorPagoDto");
	}
	
	private Long toMaxProveedorDomicilio() throws Exception{		
		return toMax("TrManticProveedorDomicilioDto");
	} // toMaxArticuloCodigo
	
	private Long toMaxProveedorAgentes() throws Exception{		
		return toMax("TrManticProveedorAgenteDto");
	} // toMaxProveedorRepresentantes
	
	private Long toMaxProveedorTiposContactos() throws Exception{		
		return toMax("TrManticProveedorTipoContactoDto");
	} // toMaxProveedorTiposContactos	
	
	private Long toMaxPersonaTiposContactos() throws Exception{		
		return toMax("TrManticPersonaTipoContactoDto");
	} // toMaxProveedorTiposContactos
	
	private Long toMaxProveedorBanco() throws Exception{		
		return toMax("TcManticAcreedoresBancosDto");
	} // toMaxProveedorTiposContactos
	
	private Long toMax(String vista) throws Exception{
		Long regresar= 0L;
		Value maximo = null;
		try {
			maximo= DaoFactory.getInstance().toField(vista, "maximo", Collections.EMPTY_MAP, "maximo");
			if(maximo.getData()!= null)
				regresar= Long.valueOf(maximo.toString()) + INCREMENTO;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} // toMax
}
