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
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;
import mx.org.kaana.kalan.catalogos.acreedores.reglas.Gestor;
import mx.org.kaana.kalan.catalogos.acreedores.reglas.Transaccion;
import mx.org.kaana.mantic.db.dto.TcManticAcreedoresDto;

@Named(value = "kalanCatalogosAcreedoresFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("sortOrder", "order by tc_kalan_acreedores.razon_social");
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.loadTiposAcreedores();
      if(JsfBase.getFlashAttribute("idAcreedorProcess")!= null) {
        this.attrs.put("idAcreedorProcess", JsfBase.getFlashAttribute("idAcreedorProcess"));
        this.doLoad();
        this.attrs.put("idAcreedorProcess", null);
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  private void loadTiposAcreedores() throws Exception {
    Gestor gestor = new Gestor();
    gestor.loadTiposAcreedores();
    this.attrs.put("tiposAcreedores", gestor.getTiposAcreedores());
    this.attrs.put("tipoAcreedor", UIBackingUtilities.toFirstKeySelectEntity(gestor.getTiposAcreedores()));
  }

  private String toAllTiposAcreedores() {
    StringBuilder regresar = new StringBuilder();
    List<UISelectEntity> tiposAcreedores = (List<UISelectEntity>) this.attrs.get("tiposAcreedores");
    for (UISelectEntity tipoAcreedor: tiposAcreedores) {
      regresar.append(tipoAcreedor.getKey());
      regresar.append(",");
    } // for
    return regresar.substring(0, regresar.length() - 1);
  }

  @Override
  public void doLoad() {
    List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= null;
    try {
      params= this.toPrepare();	
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("tipoAcreedor", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      this.lazyModel = new FormatCustomLazy("VistaAcreedoresDto", params, columns);
      UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally		
  } // doLoad

  public String doAccion(String accion) {
    EAccion eaccion = null;
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
      JsfBase.setFlashAttribute("accion", eaccion);      
      JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(accion.toUpperCase()));      
      JsfBase.setFlashAttribute("idAcreedor", (eaccion.equals(EAccion.MODIFICAR)||eaccion.equals(EAccion.CONSULTAR)) ? ((Entity) this.attrs.get("seleccionado")).getKey() : -1L);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return "accion".concat(Constantes.REDIRECIONAR);
  } // doAccion

  public void doEliminar() {
    Transaccion transaccion = null;
    try {
      transaccion = new Transaccion(new TcManticAcreedoresDto(((Entity)this.attrs.get("seleccionado")).getKey()));
      transaccion.ejecutar(EAccion.ELIMINAR);
      JsfBase.addMessage("Eliminar acreedor", "El acreedor se ha eliminado correctamente", ETipoMensaje.INFORMACION);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
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
	
	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar  = new HashMap<>();	
		StringBuilder sb              = new StringBuilder();
	  UISelectEntity acreedor       = (UISelectEntity)this.attrs.get("acreedor");
		List<UISelectEntity>acreedores= (List<UISelectEntity>)this.attrs.get("acreedores");
    if(!Cadena.isVacio(this.attrs.get("idAcreedorProcess")) && !this.attrs.get("idAcreedorProcess").toString().equals("-1")) 
      sb.append("(tc_mantic_acreedores.id_acreedor=").append(this.attrs.get("idAcreedorProcess")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("clave")))
			sb.append("(tc_mantic_acreedores.clave like '%").append(this.attrs.get("clave")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("rfc")))
			sb.append("(tc_mantic_acreedores.rfc like '%").append(this.attrs.get("rfc")).append("%') and ");
		if(acreedores!= null && acreedor!= null && acreedores.indexOf(acreedor)>= 0) 
			sb.append("(tc_mantic_acreedores.razon_social like '%").append(acreedores.get(acreedores.indexOf(acreedor)).toString("razonSocial")).append("%') and ");
		else
 		  if(!Cadena.isVacio(JsfBase.getParametro("razonSocial_input")))
			  sb.append("(tc_mantic_acreedores.razon_social like '%").append(JsfBase.getParametro("razonSocial_input")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("idTipoAcreedor")) && !this.attrs.get("idTipoAcreedor").toString().equals("-1"))
  		sb.append("(tc_mantic_acreedores.id_tipo_acreedor= ").append(this.attrs.get("idTipoAcreedor")).append(") and ");
	  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	}

  public String doMasivo() {
    JsfBase.setFlashAttribute("retorno", "/Paginas/Kalan/Catalogos/Acreedores/filtro");
    JsfBase.setFlashAttribute("idTipoMasivo", ECargaMasiva.ACREEDORES.getId());
    return "/Paginas/Mantic/Catalogos/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}

}
