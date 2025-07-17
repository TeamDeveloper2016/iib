package mx.org.kaana.kalan.catalogos.acreedores.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.kalan.catalogos.acreedores.reglas.MotorBusqueda;
import mx.org.kaana.mantic.db.dto.TcManticAcreedoresPortalesDto;
import mx.org.kaana.mantic.enums.ETiposCuentas;

@Named(value = "kalanCatalogosAcreedoresPortal")
@ViewScoped
public class Portal extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428179L;
	private FormatLazyModel lazyModelServicios;

	public FormatLazyModel getLazyModelServicios() {
		return lazyModelServicios;
	}	
	
  @PostConstruct
  @Override
  protected void init() {
    try {			
      this.attrs.put("codigo", "");
      this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	  
	@Override
  public void doLoad() {
		MotorBusqueda motor                 = null;
		UISelectEntity acreedor             = null;
		TcManticAcreedoresPortalesDto portal= null;
		try {
  	  acreedor= (UISelectEntity)this.attrs.get("acreedor");
			motor= new MotorBusqueda(acreedor.getKey());
			portal= motor.toPortal();
			if(portal.isValid()) {
				this.attrs.put("pagina", portal.getPagina());
				this.attrs.put("cuenta", portal.getCuenta());
				this.attrs.put("contrasenia", portal.getContrasenia());
			} // if
			else {
				this.attrs.put("pagina", "");
				this.attrs.put("cuenta", "");
				this.attrs.put("contrasenia", "");
			} // else
			doLoadServicios();
			doLoadTransferencias();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	}
	
	public void doLoadServicios() {
		loadBanca(ETiposCuentas.SERVICIOS.getKey());
		UIBackingUtilities.resetDataTable();
	} 
	
	public void doLoadTransferencias(){
		loadBanca(ETiposCuentas.TRANSFERENCIAS.getKey());
		UIBackingUtilities.resetDataTable("tablaTransferencia");
	} 
	
	private void loadBanca(Long idServicio){
		Map<String, Object>params= new HashMap<>();
		List<Columna>columns     = new ArrayList<>();
		try {
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("convenioCuenta", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("clabeReferencia", EFormatoDinamicos.MAYUSCULAS));
			params.put("idAcreedor", ((Entity) this.attrs.get("acreedor")).getKey());
			params.put("idTipoCuenta", idServicio);
			if(idServicio.equals(ETiposCuentas.SERVICIOS.getKey()))
				this.lazyModelServicios= new FormatLazyModel("VistaAcreedoresDto", "banca", params, columns);
			else	
				this.lazyModel= new FormatLazyModel("VistaAcreedoresDto", "banca", params, columns);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally		
	} 
	
	public List<UISelectEntity> doCompleteAcreedor(String codigo) {
 		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
		boolean buscaPorCodigo    = false;
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			params.put("idAlmacen", JsfBase.getAutentifica().getEmpresa().getIdAlmacen());
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idAcreedor", -1L);
			if(!Cadena.isVacio(codigo)) {
  			codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim();
				buscaPorCodigo= codigo.startsWith(".");
				if(buscaPorCodigo)
					codigo= codigo.trim().substring(1);
				codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*");
			} // if	
			else
				codigo= "WXYZ";
  		params.put("codigo", codigo);
			if(buscaPorCodigo)
        this.attrs.put("acreedores", UIEntity.build("TcManticAcreedoresDto", "porCodigo", params, columns, 40L));
			else
        this.attrs.put("acreedores", UIEntity.build("TcManticAcreedoresDto", "porNombre", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("acreedores");
	}	

}
