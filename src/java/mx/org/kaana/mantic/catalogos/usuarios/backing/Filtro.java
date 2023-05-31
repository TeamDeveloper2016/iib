package mx.org.kaana.mantic.catalogos.usuarios.backing;

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
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.enums.ETipoPersona;

@Named(value = "manticCatalogosUsuariosFiltro")
@ViewScoped
public class Filtro extends mx.org.kaana.mantic.catalogos.personas.backing.Filtro implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

  @PostConstruct
  @Override
  protected void init() {
    try {
			super.init();
      this.attrs.put("idTipoPersona", ETipoPersona.USUARIO.getIdTipoPersona());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
  @Override
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      params.put("nombres", this.attrs.get("nombres"));
      params.put("paterno", this.attrs.get("paterno"));
      params.put("materno", this.attrs.get("materno"));
      params.put("rfc", this.attrs.get("rfc"));
      params.put("curp", this.attrs.get("curp"));
      params.put("idTipoSexo", this.attrs.get("idTipoSexo"));
      params.put("idTipoPersona", this.attrs.get("idTipoPersona"));
      params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getDependencias());
      params.put("sortOrder", "order by tc_mantic_personas.nombres");
      
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("materno", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("paterno", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("curp", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("sexo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cuenta", EFormatoDinamicos.MAYUSCULAS));     
      this.lazyModel = new FormatCustomLazy("VistaPersonasDto", "row", params, columns);
      UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
  } // doLoad

  @Override
  public String doAccion(String accion) {
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("tipoPersona", this.attrs.get("idTipoPersona"));		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Catalogos/Usuarios/filtro");		
			JsfBase.setFlashAttribute("idPersona", (eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR)) ? ((Entity)this.attrs.get("seleccionado")).getKey() : -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Mantic/Catalogos/Personas/accion".concat(Constantes.REDIRECIONAR);
  } // doAccion  
  
}
