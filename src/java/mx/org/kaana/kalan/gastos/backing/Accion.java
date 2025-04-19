package mx.org.kaana.kalan.gastos.backing;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kalan.gastos.beans.Gasto;
import mx.org.kaana.kalan.gastos.beans.Parcialidad;
import mx.org.kaana.kalan.gastos.enums.EGastos;
import mx.org.kaana.kalan.gastos.reglas.AdminGasto;
import mx.org.kaana.kalan.gastos.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "kalanGastosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;
  private static final String TEXT_GLOBAL= "TOTAL {total}, {pagos} MESES, DEL {inicia} AL {termina}";
  private static final String TEXT_PARCIALIDAD= "TOTAL {total}, {pagos} MESES, DEL {inicia} AL {termina}, {importe} (PARCIALIDAD {pago}, {mes})";

  private Gasto gasto;
  private EAccion accion;
  private EGastos egasto;

  public Gasto getGasto() {
    return gasto;
  }

  public void setGasto(Gasto gasto) {
    this.gasto = gasto;
  }

  public int getSize() {
    return this.gasto.getParcialidades().size();
  }

  public void setSize(int size) {
  }
  
	@PostConstruct
  @Override
  protected void init() {		
    try {
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "/Paginas/Kalan/Gastos/filtro": JsfBase.getFlashAttribute("retorno"));
			this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
			this.egasto= JsfBase.getFlashAttribute("egasto")== null? EGastos.GASTOS_ADMINISTRATIVOS: (EGastos)JsfBase.getFlashAttribute("egasto");
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));      
      if(JsfBase.getFlashAttribute("retorno")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("idEmpresaGasto", JsfBase.getFlashAttribute("idEmpresaGasto")== null? -1L: JsfBase.getFlashAttribute("idEmpresaGasto"));
      this.attrs.put("total", 0D);
			this.doLoad();
      this.toLoadEmpresas();
      this.toLoadClasificaciones();
      this.toLoadComprobantes();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
  public void doLoad() {
    Map<String, Object> params= new HashMap<>();
    try {      
      AdminGasto admin= new AdminGasto((Long)this.attrs.get("idEmpresaGasto"));
      switch(this.accion) {
        case AGREGAR:
          this.gasto= admin.getGasto();
          break;
        case MODIFICAR:
        case CONSULTAR:
          this.gasto= admin.getGasto();
          break;
      } // switch
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // doLoad

  private void toLoadEmpresas() {
    Map<String, Object> params = new HashMap<>();
    List<UISelectItem> empresas= null;
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        empresas= UISelect.seleccione("TcManticEmpresasDto", "empresas", params, "idKey|titulo", EFormatoDinamicos.MAYUSCULAS);
      else
        empresas= UISelect.build("TcManticEmpresasDto", "empresas", params, "idKey|titulo", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("empresas", empresas);
      if(empresas!= null && !empresas.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) 
          this.gasto.setIdEmpresa((Long)empresas.get(0).getValue());
      } // if  
      this.doLoadCuentas();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  
  
	public List<UISelectEntity> doCompleteProveedor(String codigo) {
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
        this.attrs.put("proveedores", UIEntity.build("TcManticProveedoresDto", "porCodigo", params, columns, 40L));
			else
        this.attrs.put("proveedores", UIEntity.build("TcManticProveedoresDto", "porNombre", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
		return (List<UISelectEntity>)this.attrs.get("proveedores");
	}	
  
  private void toLoadClasificaciones() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, "id_gasto_clasificacion= "+ this.egasto.getKey());
      // List<UISelectItem> clasificaciones= UISelect.seleccione("TcKalanGastosClasificacionesDto", params, "idKey|descripcion", EFormatoDinamicos.MAYUSCULAS);
      List<UISelectItem> clasificaciones= UISelect.build("TcKalanGastosClasificacionesDto", params, "idKey|descripcion", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("clasificaciones", clasificaciones);
      if(clasificaciones!= null && !clasificaciones.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) 
          this.gasto.setIdGastoClasificacion((Long)clasificaciones.get(0).getValue());
      } // if  
      this.doLoadSubclasificaciones();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  
  
  public void doLoadSubclasificaciones() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put("idGastoClasificacion", this.gasto.getIdGastoClasificacion());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> subclasificaciones= UISelect.seleccione("TcKalanGastosSubclasificacionesDto", params, "idKey|descripcion", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("subclasificaciones", subclasificaciones);
      if(subclasificaciones!= null && !subclasificaciones.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) 
          this.gasto.setIdGastoSubclasificacion((Long)subclasificaciones.get(0).getValue());
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  
  
  private void toLoadComprobantes() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> comprobantes= UISelect.seleccione("TcKalanGastosComprobantesDto", params, "idKey|descripcion", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("comprobantes", comprobantes);
      if(comprobantes!= null && !comprobantes.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) 
          this.gasto.setIdGastoComprobante((Long)comprobantes.get(0).getValue());
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  
  
  public void doLoadCuentas() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put("idEmpresa", this.gasto.getIdEmpresa());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> cuentas= UISelect.seleccione("TcKalanEmpresasCuentasDto", params, "idKey|banco|nombre", " - ", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("cuentas", cuentas);
      if(cuentas!= null && !cuentas.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) 
          this.gasto.setIdEmpresaCuenta((Long)cuentas.get(0).getValue());
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  
 
  public String doAplicar() {  
    this.gasto.setIdGastoEstatus(2L);
    return this.doAceptar();
  }
  
  public String doAceptar() {  
    String regresar        = null;
    Transaccion transaccion= null;
    try {			
      if(Objects.equals(this.gasto.getIdProveedor(), -1L))
        this.gasto.setIdProveedor(null);
      if(Objects.equals(this.gasto.getIdEmpresaCuenta(), -1L))
        this.gasto.setIdEmpresaCuenta(null);
      if(Objects.equals(this.gasto.getIdGastoComprobante(), -1L))
        this.gasto.setIdGastoComprobante(null);
      transaccion = new Transaccion(this.gasto);
			if (transaccion.ejecutar(this.accion)) {
			  regresar= this.doCancelar();
  		  if(!this.accion.equals(EAccion.CONSULTAR)) 
  			  JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el gasto de la empresa !"), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el gasto !", ETipoMensaje.ALERTA);      			
      if(Objects.equals(this.gasto.getDocumento().getIdProveedor(), null))
        this.gasto.getDocumento().setIdProveedor(-1L);
      if(Objects.equals(this.gasto.getIdProveedor(), null))
        this.gasto.setIdProveedor(-1L);
      if(Objects.equals(this.gasto.getIdEmpresaCuenta(), null))
        this.gasto.setIdEmpresaCuenta(-1L);
      if(Objects.equals(this.gasto.getIdGastoComprobante(), null))
        this.gasto.setIdGastoComprobante(-1L);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } 

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idEmpresaGastoProcess", this.gasto.getIdEmpresaGasto());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } 
	
  public void doCalculate() {
    if(this.gasto.getTotal()> 0D) {
      this.gasto.setIvaCalculado(Numero.toRedondear((this.gasto.getIva()/ 100)* this.gasto.getTotal()));
      this.gasto.setImporte(Numero.toRedondear(this.gasto.getTotal()));
      this.gasto.setSubtotal(Numero.toRedondear(this.gasto.getTotal()- this.gasto.getIvaCalculado()));
    } // if
    else {
      this.gasto.setIvaCalculado(0D);
      this.gasto.setImporte(0D);
      this.gasto.setIepsCalculado(0D);
      this.gasto.setTotal(0D);
    } // else
    this.doProrratear();
  }
 
  public String toFecha(Date fecha) {
    return Fecha.formatear(Fecha.FECHA_CORTA, fecha);
  }
  
  public void doCheque() {
    
  }
 
  public void doProrratear() {
    this.gasto.setPagos(this.gasto.getProrratear() && Objects.equals(this.gasto.getPagos(), 0L)? 12L: 0L);
    if(this.gasto.getProrratear())
      this.doParcialidades(); 
    else {
      this.clean(0);
      this.attrs.put("total", 0D);
    } // if 
    UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:pagos', {validaciones: 'requerido|flotante|mayor-igual({\"cuanto\":".concat(this.gasto.getProrratear()? "1": "0").concat("})', mascara: 'libre', grupo: 'general'});"));
  }
  
  private void clean(int start) {
    int count= start;
    while(count< this.gasto.getParcialidades().size()) {
      if(Objects.equals(this.gasto.getParcialidades().get(count).getSql(), ESql.INSERT))
        this.gasto.getParcialidades().remove(count);
      else {
        this.gasto.getParcialidades().get(count).setSql(ESql.DELETE);
        count++;        
      } // else
    } // while    
  }
  
  public void doParcialidades() {
    double sum      = 0D;
    int count       = this.gasto.getParcialidades().size();
    Parcialidad clon= this.gasto.clon();
    Calendar start  = Calendar.getInstance();
    start.setTimeInMillis(clon.getFechaAplicacion().getTime());
    for (int x= 0; x< this.gasto.getPagos(); x++) {
      if(x>= count)
        this.gasto.getParcialidades().add(this.gasto.clon(this.gasto.getPagos()));
      else {
        this.gasto.getParcialidades().get(x).calculate(clon, this.gasto.getPagos());
        if(Objects.equals(this.gasto.getParcialidades().get(x).getSql(), ESql.SELECT) || Objects.equals(this.gasto.getParcialidades().get(x).getSql(), ESql.DELETE))
          this.gasto.getParcialidades().get(x).setSql(ESql.UPDATE);
      } // else
      sum+= this.gasto.getParcialidades().get(x).getTotal();
      // AJUSTAR LA FECHA DE APLICACION AGREGANDO UN MES CALENDARIO A CADA PAGO
      if(x> 0) 
        start.add(Calendar.MONTH, 1);
      this.gasto.getParcialidades().get(x).setFechaAplicacion(new Date(start.getTimeInMillis()));
      this.gasto.getParcialidades().get(x).setPago(new Long(x+ 1));
    } // for
    // VERIFICAR SI HAY MENOS PAGOS DE LOS YA REGISTRADOS ENTONCES MARCAR COMO ELIMINADOS O DEPURARLOS
    if(count> this.gasto.getPagos()) 
      this.clean(this.gasto.getPagos().intValue());
    this.attrs.put("total", Numero.toRedondear(sum));
    this.toFormatPago();
  }

  private void toFormatPago() {
    Map<String, Object> params= new HashMap<>();
    Calendar calendar         = Calendar.getInstance();
    try {      
      if(this.gasto.getParcialidades()!= null && !this.gasto.getParcialidades().isEmpty()) {
        params.put("total", Numero.formatear(Numero.MONEDA_CON_DECIMALES, this.gasto.getTotal()));
        Date fecha= this.gasto.getParcialidades().get(0).getFechaAplicacion();
        params.put("inicia", Fecha.formatear(Fecha.FECHA_ANIO_MES, fecha));
        fecha= this.gasto.getParcialidades().get(this.gasto.getParcialidades().size()- 1).getFechaAplicacion();
        params.put("termina", Fecha.formatear(Fecha.FECHA_ANIO_MES, fecha));
        params.put("pagos", this.gasto.getPagos());
        for (Parcialidad item: this.gasto.getParcialidades()) {
          params.put("importe", Numero.formatear(Numero.MONEDA_CON_DECIMALES, item.getTotal()));
          params.put("pago", item.getPago());
          calendar.setTimeInMillis(item.getFechaAplicacion().getTime());
          int month= calendar.get(Calendar.MONTH);
          params.put("mes", Fecha.getNombreMesCorto(month));
          item.setObservaciones(Cadena.replaceParams(TEXT_PARCIALIDAD, params));
        } // for
        this.gasto.setObservaciones(Cadena.replaceParams(TEXT_GLOBAL, params));
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
 
  }

  public String doColor(Parcialidad row) {
    return Objects.equals(row.getSql(), ESql.DELETE)? "janal-display-none": "";
  }
 
  public char getGroupProveedor(UISelectEntity item) {
    return item.toString("rfc").charAt(0);
  }  
  
}