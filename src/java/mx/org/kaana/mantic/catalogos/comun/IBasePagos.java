package mx.org.kaana.mantic.catalogos.comun;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.enums.ETipoMediosPago;
import org.primefaces.event.TabChangeEvent;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;

public abstract class IBasePagos extends mx.org.kaana.mantic.inventarios.comun.IBaseImportar implements Serializable{
	
	private static final long serialVersionUID= -3677487238720891767L;
	
	protected void initValues() throws Exception {
		try {
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());			
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());			
			this.attrs.put("mostrarBanco", false);			
			this.setFile(new Importado());
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_IDENTIFICACION);
			this.attrs.put("xml", ""); 
			this.attrs.put("file", ""); 
			this.loadSucursales();							
			this.doLoadCajas();
			this.loadTiposPagos();
			this.loadBancos();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} 
	
	protected void loadBancos(){
		List<UISelectEntity> bancos= null;
		Map<String, Object> params = new HashMap<>();
		List<Columna> columns      = new ArrayList<>();
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			bancos= UIEntity.build("TcManticBancosDto", "row", params, columns, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("bancos", bancos);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadBancos
	
	protected void loadSucursales() {
		List<UISelectEntity> sucursales= null;
		Map<String, Object>params      = new HashMap<>();
		List<Columna> columns          = new ArrayList<>();
		try {
			if(JsfBase.isAdminEncuestaOrAdmin())
  			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      else
  			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			sucursales=(List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns);
			this.attrs.put("sucursales", sucursales);
			this.attrs.put("idEmpresa", UIBackingUtilities.toFirstKeySelectEntity(sucursales));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} 
	
	public void doLoadCajas() {
		List<UISelectEntity> cajas= null;
		Map<String, Object>params = new HashMap<>();
		List<Columna> columns     = new ArrayList<>();
		try {
			params.put("idEmpresa", this.attrs.get("idEmpresa"));
			columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			cajas=(List<UISelectEntity>) UIEntity.build("TcManticCajasDto", "cajas", params, columns);
			this.attrs.put("cajas", cajas);
			this.attrs.put("caja", UIBackingUtilities.toFirstKeySelectEntity(cajas));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch	
	} // loadCajas
	
	protected void loadTiposPagos(){
		List<UISelectEntity> tiposPagos= null;
		Map<String, Object>params      = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, "id_cobro_caja=1");
			tiposPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposPagos", tiposPagos);
			this.attrs.put("tipoPago", UIBackingUtilities.toFirstKeySelectEntity(tiposPagos));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // loadTiposPagos
	
	public void doValidaTipoPago() {
		Long tipoPago= -1L;
		try {
			tipoPago= Long.valueOf(this.attrs.get("tipoPago").toString());
			this.attrs.put("mostrarBanco", !ETipoMediosPago.EFECTIVO.getIdTipoMedioPago().equals(tipoPago));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doValidaTipoPago
	
	public void doTabChange(TabChangeEvent event) {
		if(event.getTab().getTitle().equals("Archivos")) 
			doLoadImportados();
		else if(event.getTab().getTitle().equals("Importar")) {
			doLoadPagosArchivos();
			doLoadFiles();
		} // else if
	}	// doTabChange
	
	public abstract void doLoadImportados();
	
	public abstract void doLoadPagosArchivos();
	
	public abstract void doLoadFiles();
	
}
