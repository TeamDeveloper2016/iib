package mx.org.kaana.mantic.catalogos.empleados.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.reportes.beans.ExportarXls;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.comun.Catalogos;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.personas.beans.RegistroPersona;
import mx.org.kaana.mantic.catalogos.personas.reglas.Gestor;
import mx.org.kaana.mantic.catalogos.personas.reglas.Transaccion;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EExportacionXls;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETipoPersona;

@Named(value = "manticCatalogosEmpleadosFiltro")
@ViewScoped
public class Filtro extends mx.org.kaana.mantic.catalogos.personas.backing.Filtro implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

  protected Reporte reporte;

  public Reporte getReporte() {
    return reporte;
  }	// getReporte

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("nombres", "");
      this.attrs.put("paterno", "");
      this.attrs.put("materno", "");
      this.attrs.put("rfc", "");
      this.attrs.put("curp", "");
      this.attrs.put("sortOrder", "order by tc_mantic_personas.nombres");
      this.attrs.put("idTipoPersona", ETipoPersona.EMPLEADO.getIdTipoPersona());
      this.attrs.put("selectDepartamentos", new String[]{});
      this.attrs.put("selectPuestos", new String[]{});
      this.toLoadEmpresas();
      this.toLoadTiposPersonas();
      this.toLoadContratistas();
      if (JsfBase.getFlashAttribute("idPersona") != null) {
        this.attrs.put("idPersona", JsfBase.getFlashAttribute("idPersona"));
        this.doLoad();
        this.attrs.put("idPersona", null);
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  protected void toLoadTiposPersonas() throws Exception {
    Gestor gestor = new Gestor();
    gestor.loadTiposPersonas();
    this.attrs.put("tiposPersonas", gestor.getTiposPersonas());
    this.attrs.put("tipoPersona", UIBackingUtilities.toFirstKeySelectEntity(gestor.getTiposPersonas()));
    this.attrs.put("puestos", gestor.loadPuestosSimple());
    this.attrs.put("puesto", UIBackingUtilities.toFirstKeySelectItem((List<UISelectItem>) this.attrs.get("puestos")));
    this.attrs.put("tiposGastos", gestor.loadTiposGastos());
    Long idTipoGasto = (Long) UIBackingUtilities.toFirstKeySelectItem((List<UISelectItem>) this.attrs.get("tiposGastos"));
    this.attrs.put("idTipoGasto", idTipoGasto);
    this.attrs.put("departamentos", gestor.loadDepartamentosSimple(idTipoGasto));
    this.attrs.put("departamento", UIBackingUtilities.toFirstKeySelectItem((List<UISelectItem>) this.attrs.get("departamentos")));
  } // toLoadTiposPersonas  

  protected void toLoadEmpresas() {
    Map<String, Object> params= new HashMap<>();
    List<Columna> columns     = new ArrayList<>();
    try {
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
      this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>) this.attrs.get("sucursales")));
    } // try
    catch (Exception e) {
      JsfBase.addMessageError(e);
      Error.mensaje(e);
    } // catch				
  } // loadEmpresas

  protected void toLoadContratistas() {
    List<UISelectEntity> contratistas= null;
    try {
      contratistas = Catalogos.toContratistasPorElDia();
      this.attrs.put("contratistas", contratistas);
      this.attrs.put("idContratista", UIBackingUtilities.toFirstKeySelectEntity(contratistas));
    } // try
    catch (Exception e) {
      JsfBase.addMessageError(e);
      Error.mensaje(e);
    } // catch		
  } // toLoadContratistas

  @Override
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= this.toPrepare();
    try {
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("materno", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("paterno", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("curp", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("sexo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("tipoPersona", EFormatoDinamicos.MAYUSCULAS));
      this.lazyModel = new FormatCustomLazy("VistaPersonasDto", "filter", params, columns);
      UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally		
  } // doLoad

  protected Map<String, Object> toPrepare() {
    StringBuilder sb = new StringBuilder("");
    String[] selectPuestos = (String[]) this.attrs.get("selectPuestos");
    String[] selectDepartamentos = (String[]) this.attrs.get("selectDepartamentos");
    if (!Cadena.isVacio(this.attrs.get("salarioMenor"))) {
      sb.append("tr_mantic_empresa_personal.sueldo_mensual<").append(this.attrs.get("salarioMenor")).append(" and ");
    }
    if (!Cadena.isVacio(this.attrs.get("salarioMayor"))) {
      sb.append("tr_mantic_empresa_personal.sueldo_mensual>").append(this.attrs.get("salarioMayor")).append(" and ");
    }
    if (!Cadena.isVacio(this.attrs.get("clave"))) {
      sb.append("tr_mantic_empresa_personal.clave like '%").append(this.attrs.get("clave")).append("%' and ");
    }
    if (!Cadena.isVacio(this.attrs.get("idPersona"))) {
      sb.append("tc_mantic_personas.id_persona=").append(this.attrs.get("idPersona")).append(" and ");
    }
    if (this.attrs.get("nombre") != null && !Cadena.isVacio(this.attrs.get("nombre"))) {
      String nombre = ((String) this.attrs.get("nombre")).toUpperCase().replaceAll("(,| |\\t)+", ".*");
      sb.append("(upper(concat(tc_mantic_personas.nombres, ' ', tc_mantic_personas.paterno, ' ', ifnull(tc_mantic_personas.materno, ' '), ' ', ifnull(tc_mantic_personas.apodo, ' '))) regexp '.*").append(nombre).append(".*') and ");
    } // if
    if (!Cadena.isVacio(this.attrs.get("rfc"))) {
      sb.append("tc_mantic_personas.rfc like '%").append(this.attrs.get("rfc")).append("%' and ");
    }
    if (!Cadena.isVacio(this.attrs.get("curp"))) {
      sb.append("tc_mantic_personas.curp like '%").append(this.attrs.get("curp")).append("%' and ");
    }
    sb.append("tc_mantic_personas.id_tipo_persona in (").append(this.attrs.get("idTipoPersona")).append(") and ");
    if (!Cadena.isVacio(this.attrs.get("idContratista")) && ((UISelectEntity) this.attrs.get("idContratista")).getKey() >= 1L) {
      if (((UISelectEntity) this.attrs.get("idContratista")).getKey() == 999L) {
        sb.append("tr_mantic_empresa_personal.id_contratista is null and ");
      } else {
        sb.append("tr_mantic_empresa_personal.id_contratista=").append(((UISelectEntity) this.attrs.get("idContratista")).getKey()).append(" and ");
      }
    }
    if (!Cadena.isVacio(this.attrs.get("idEmpresa")) && ((UISelectEntity) this.attrs.get("idEmpresa")).getKey() >= 1L) {
      sb.append("tr_mantic_empresa_personal.id_empresa in (").append(((UISelectEntity) this.attrs.get("idEmpresa")).getKey()).append(") and ");
    } else {
      sb.append("tr_mantic_empresa_personal.id_empresa in (").append(JsfBase.getAutentifica().getEmpresa().getSucursales()).append(") and ");
    }
    if (!Cadena.isVacio(this.attrs.get("idActivo"))) {
      sb.append("tr_mantic_empresa_personal.id_activo in (").append(this.attrs.get("idActivo")).append(") and ");
    }
    if (!Cadena.isVacio(this.attrs.get("idSeguro"))) {
      sb.append("tr_mantic_empresa_personal.id_seguro in (").append(this.attrs.get("idSeguro")).append(") and ");
    }
    if (!Cadena.isVacio(this.attrs.get("idTipoGasto")) && (Long) this.attrs.get("idTipoGasto") > 0L) {
      sb.append("tc_keet_tipos_gastos.id_tipo_gasto= ").append(this.attrs.get("idTipoGasto")).append(" and ");
    }
    if (selectDepartamentos.length > 0) {
      String allDepartametos = "";
      for (String departamento : selectDepartamentos) {
        allDepartametos = allDepartametos.concat(departamento).concat(",");
      }
      sb.append("tc_keet_contratistas_departamentos.id_departamento in (").append(allDepartametos.substring(0, allDepartametos.length() - 1)).append(") and ");
    } // if		
    if (selectPuestos.length > 0) {
      String allPuestos = "";
      for (String puesto : selectPuestos) {
        allPuestos = allPuestos.concat(puesto).concat(",");
      }
      sb.append("tc_mantic_puestos.id_puesto in (").append(allPuestos.substring(0, allPuestos.length() - 1)).append(") and ");
    } // if
    Map<String, Object> regresar = new HashMap<>();
    regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length() - 4));
    return regresar;
  } // toPrepare  

  @Override
  public String doAccion(String accion) {
    EAccion eaccion = null;
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
      JsfBase.setFlashAttribute("accion", eaccion);
      JsfBase.setFlashAttribute("tipoPersona", ETipoPersona.EMPLEADO.getIdTipoPersona());
      JsfBase.setFlashAttribute("retorno", "filtro");
      JsfBase.setFlashAttribute("idPersona", (eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR)) ? ((Entity) this.attrs.get("seleccionado")).getKey() : -1L);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return "accion".concat(Constantes.REDIRECIONAR);
  } // doAccion

  public String doExportar() {
    String regresar           = null;
    Map<String, Object> params= this.toPrepare();
    try {
      params.put("sortOrder", "order by tr_mantic_empresa_personal.id_empresa, tr_mantic_empresa_personal.clave");
      JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap) params).clone(), EExportacionXls.PERSONAS.getProceso(), EExportacionXls.PERSONAS.getIdXml(), EExportacionXls.PERSONAS.getNombreArchivo()), EExportacionXls.PERSONAS,
              "SUCURSAL,CODIGO NOMINA,DEPARTAMENTO,PUESTO,RFC,NOMBRE,1ER APELLIDO,2DO APELLIDO,CONTRATACION,INGRESO,BAJA,ACTIVO,SEGURO,NSS,SUELDO SEMANAL,SUELDO IMSS,INFONAVIT, FACTOR INFONAVIT"));
      JsfBase.getAutentifica().setMonitoreo(new Monitoreo());
      regresar = "/Paginas/Reportes/excel".concat(Constantes.REDIRECIONAR);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  } // doExportar  	

  public void doReporte(String nombre) throws Exception {
    Map<String, Object> parametros= null;
    EReportes reporteSeleccion    = null;
    Map<String, Object> params    = new HashMap<>();
    Parametros comunes            = null;
    try {
      reporteSeleccion = EReportes.valueOf(nombre);
      this.reporte = JsfBase.toReporte();
      comunes = new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      parametros = comunes.getComunes();
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
      parametros.put("REPORTE_TITULO", reporteSeleccion.getTitulo());
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));
      if (doVerificarReporte()) {
        this.reporte.doAceptar();
      }
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch	
  } // doReporte 

  public boolean doVerificarReporte() {
    boolean regresar = false;
    if (this.reporte.getTotal() > 0L) {
      UIBackingUtilities.execute("start(" + this.reporte.getTotal() + ")");
      regresar = true;
    } else {
      UIBackingUtilities.execute("generalHide();");
      JsfBase.addMessage("Reporte", "No se encontraron registros para el reporte", ETipoMensaje.ERROR);
      regresar = false;
    } // else
    return regresar;
  } // doVerificarReporte	

  public void doActivar(String activar) {
    Entity seleccionado            = null;
    RegistroPersona registroPersona= null;
    Transaccion transaccion        = null;
    Boolean isActivar              = false;
    try {
      seleccionado = (Entity) this.attrs.get("seleccionado");
      registroPersona = new RegistroPersona(seleccionado.getKey());
      isActivar = Boolean.valueOf(activar);
      registroPersona.getEmpresaPersona().setIdActivo(isActivar ? 1L : 2L);
      transaccion = new Transaccion(registroPersona);
      if (transaccion.ejecutar(EAccion.MODIFICAR)) {
        JsfBase.addMessage((isActivar ? "Activar" : "Inactivar").concat(" empleado"), "Se ".concat(isActivar ? "activo" : "inactivo").concat(" el empleado de forma correcta"), ETipoMensaje.INFORMACION);
      } else {
        JsfBase.addMessage((isActivar ? "Activar" : "Inactivar").concat(" empleado"), "Ocurrió un error al ".concat(isActivar ? "activar" : "inactivar").concat(" el empleado"), ETipoMensaje.ERROR);
      }
    } // try
    catch (Exception e) {
      JsfBase.addMessageError(e);
      Error.mensaje(e);
    } // catch		
  }

  public void doDeposito(String deposito) {
    Entity seleccionado            = null;
    RegistroPersona registroPersona= null;
    Transaccion transaccion        = null;
    Boolean isDeposito             = false;
    try {
      seleccionado = (Entity) this.attrs.get("seleccionado");
      registroPersona = new RegistroPersona(seleccionado.getKey());
      isDeposito = Boolean.valueOf(deposito);
      registroPersona.getEmpresaPersona().setIdNomina(isDeposito ? 1L : 2L);
      transaccion = new Transaccion(registroPersona);
      if (transaccion.ejecutar(EAccion.MODIFICAR)) {
        JsfBase.addMessage((isDeposito ? "Activar" : "Inactivar").concat(" pago"), "Se ".concat(isDeposito ? "activo" : "inactivo").concat(" el pago de forma correcta."), ETipoMensaje.INFORMACION);
      } else {
        JsfBase.addMessage((isDeposito ? "Activar" : "Inactivar").concat(" pago"), "Ocurrió un error al ".concat(isDeposito ? "activar" : "inactivar").concat(" el pago."), ETipoMensaje.ERROR);
      }
    } // try
    catch (Exception e) {
      JsfBase.addMessageError(e);
      Error.mensaje(e);
    } // catch		
  } // doDeposito

  public void doDepartamentos() {
    Gestor gestor = new Gestor();
    try {
      this.attrs.put("departamentos", gestor.loadDepartamentosSimple((Long) this.attrs.get("idTipoGasto")));
      this.attrs.put("departamento", UIBackingUtilities.toFirstKeySelectItem((List<UISelectItem>) this.attrs.get("departamentos")));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch	
    finally {
      gestor = null;
    } // finally
  }
  
}
