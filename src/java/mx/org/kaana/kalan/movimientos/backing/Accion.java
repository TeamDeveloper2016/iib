package mx.org.kaana.kalan.movimientos.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kalan.movimientos.reglas.Transaccion;
import mx.org.kaana.kalan.movimientos.beans.Movimiento;
import mx.org.kaana.kalan.movimientos.reglas.AdminMovimiento;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "kalanMovimientosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 127393488565639367L;

  private Movimiento movimiento;
  private EAccion accion;
  private Long idTipoMovimiento;

  public Movimiento getMovimiento() {
    return movimiento;
  }

  public void setMovimiento(Movimiento movimiento) {
    this.movimiento = movimiento;
  }

  public Long getIdTipoMovimiento() {
    return idTipoMovimiento;
  }

	@PostConstruct
  @Override
  protected void init() {		
    try {
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "/Paginas/Kalan/Movimientos/ingreso": JsfBase.getFlashAttribute("retorno"));
			this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));      
      if(JsfBase.getFlashAttribute("retorno")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("encabezado", JsfBase.getFlashAttribute("pagina")== null? "Ingreso": Cadena.letraCapital((String)JsfBase.getFlashAttribute("pagina")));
      this.attrs.put("pagina", JsfBase.getFlashAttribute("pagina")== null? "ingreso": JsfBase.getFlashAttribute("pagina"));
      this.attrs.put("titulo", JsfBase.getFlashAttribute("titulo")== null? " ingreso sin factura": JsfBase.getFlashAttribute("titulo"));
      this.attrs.put("idEmpresaMovimiento", JsfBase.getFlashAttribute("idEmpresaMovimiento")== null? -1L: JsfBase.getFlashAttribute("idEmpresaMovimiento"));
      this.idTipoMovimiento= JsfBase.getFlashAttribute("idTipoMovimiento")== null? 1L: (Long)JsfBase.getFlashAttribute("idTipoMovimiento");
			this.doLoad();
      this.toLoadEmpresas();
      this.doLoadBancos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
  public void doLoad() {
    Map<String, Object> params= new HashMap<>();
    Entity concepto           = null;
    try {      
      AdminMovimiento admin= new AdminMovimiento((Long)this.attrs.get("idEmpresaMovimiento"));
      switch(this.accion) {
        case AGREGAR:
          this.movimiento= admin.getMovimiento();
          break;
        case MODIFICAR:
        case CONSULTAR:
          this.movimiento= admin.getMovimiento();
          break;
      } // switch
      concepto= new Entity(Objects.equals(this.movimiento.getIdTipoConcepto(), null)? -1L: this.movimiento.getIdTipoConcepto());
      concepto.put("concepto", new Value("concepto", this.movimiento.getConcepto()));
      this.attrs.put("concepto", new UISelectEntity(concepto));
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
        params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        empresas= UISelect.seleccione("TcManticEmpresasDto", "empresas", params, "idKey|titulo", EFormatoDinamicos.MAYUSCULAS);
      else
        empresas= UISelect.build("TcManticEmpresasDto", "empresas", params, "idKey|titulo", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("empresas", empresas);
      if(empresas!= null && !empresas.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) 
          this.movimiento.setIdEmpresa((Long)empresas.get(0).getValue());
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
  
	public List<UISelectEntity> doCompleteCliente(String codigo) {
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
        this.attrs.put("clientes", UIEntity.build("TcManticClientesDto", "porCodigo", params, columns, 40L));
			else
        this.attrs.put("clientes", UIEntity.build("TcManticClientesDto", "porNombre", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("clientes");
	}	
  
  public void doLoadBancos() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put("idEmpresa", this.movimiento.getIdEmpresa());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> bancos= UISelect.seleccione("TcKalanEmpresasCuentasDto", "bancos", params, "idKey|nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("bancos", bancos);
      if(bancos!= null && !bancos.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) 
          this.movimiento.setIdBanco((Long)bancos.get(0).getValue());
      } // if  
      else
        this.movimiento.setIdBanco(-1L);          
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
  
  public void doLoadCuentas() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put("idEmpresa", this.movimiento.getIdEmpresa());
			params.put("idBanco", this.movimiento.getIdBanco());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> cuentas= UISelect.seleccione("TcKalanEmpresasCuentasDto", "cuentas", params, "idKey|nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("cuentas", cuentas);
      if(cuentas!= null && !cuentas.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) 
          this.movimiento.setIdEmpresaCuenta((Long)cuentas.get(0).getValue());
      } // if  
      else
        this.movimiento.setIdEmpresaCuenta(-1L);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  
  
  public List<UISelectEntity> doCompleteConcepto(String codigo) {
 		List<Columna> columns        = new ArrayList<>();
    Map<String, Object> params   = new HashMap<>();
    List<UISelectEntity> regresar= null;
    try {
      columns.add(new Columna("concepto", EFormatoDinamicos.MAYUSCULAS));
			params.put("idTipoMovimiento", this.idTipoMovimiento);
			if(!Cadena.isVacio(codigo)) {
  			codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim();
				codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*");
			} // if	
			else
				codigo= "WXYZ";
  		params.put("codigo", codigo);
      regresar= UIEntity.build("TcKalanTiposConceptosDto", "porNombre", params, columns, 40L); 
      this.attrs.put("conceptos", regresar);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
    return regresar;
  } 
  
  public void doUpdateConcepto() {
    List<UISelectEntity> conceptos= (List<UISelectEntity>)this.attrs.get("conceptos");
    UISelectEntity concepto       = (UISelectEntity)this.attrs.get("concepto");
    try {
      if(!Objects.equals(conceptos, null)) {
        int index= conceptos.indexOf(concepto);
        if(index>= 0) {
          concepto= conceptos.get(index);
          this.attrs.put("concepto", concepto);
          this.movimiento.setConcepto(concepto.toString("concepto"));
          this.movimiento.setIdTipoConcepto(concepto.getKey());
        } // if  
        else
          this.movimiento.setIdTipoConcepto(null);
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  }

  public String doAplicar() {  
    this.movimiento.setIdMovimientoEstatus(2L);
    return this.doAceptar();
  }
  
  public String doAceptar() {  
    String regresar        = null;
    Transaccion transaccion= null;
    try {			
      this.movimiento.setIdTipoMovimiento(this.idTipoMovimiento);
      this.movimiento.setConcepto(JsfBase.getParametro("contenedorGrupos:idTipoConcepto_input"));
      transaccion = new Transaccion(this.movimiento);
			if (transaccion.ejecutar(this.accion)) {
			  regresar= this.doCancelar();
  		  if(!this.accion.equals(EAccion.CONSULTAR)) 
  			  JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el ").concat((String)this.attrs.get("pagina")).concat(" !"), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el ".concat((String)this.attrs.get("pagina")).concat(" !"), ETipoMensaje.ALERTA);      			
      if(Objects.equals(this.movimiento.getIdCliente(), null))
        this.movimiento.setIdCliente(-1L);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } 

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idEmpresaMovimientoProcess", this.movimiento.getIdEmpresaMovimiento());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } 
	
  public char getGroupCliente(UISelectEntity item) {
    return item.toString("rfc").charAt(0);
  }  
  
}