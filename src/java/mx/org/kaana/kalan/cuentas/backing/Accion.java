package mx.org.kaana.kalan.cuentas.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kalan.cuentas.beans.Cuenta;
import mx.org.kaana.kalan.cuentas.enums.ECuentasOrigenes;
import mx.org.kaana.kalan.cuentas.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "kalanCuentasAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393188565631311L;
  private static final Log LOG = LogFactory.getLog(Accion.class);
  
  private EAccion accion;
  private Cuenta cuenta;
  private Long idCuentaMovimiento;

  public Cuenta getCuenta() {
    return cuenta;
  }

  public void setCuenta(Cuenta cuenta) {
    this.cuenta = cuenta;
  }
  
  public Boolean getAplicar() {
    return Objects.equals(this.accion, EAccion.AGREGAR) || Objects.equals(this.accion, EAccion.MODIFICAR);
  }
  
  @PostConstruct
  @Override
  public void init() {
    try {
  		if(Objects.equals(JsfBase.getFlashAttribute("accion"), null))
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion    = Objects.equals(JsfBase.getFlashAttribute("accion"), null)? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.idCuentaMovimiento= Objects.equals(JsfBase.getFlashAttribute("idCuentaMovimiento"), null)? -1L: (Long)JsfBase.getFlashAttribute("idCuentaMovimiento");
      this.attrs.put("retorno", Objects.equals(JsfBase.getFlashAttribute("retorno"), null)? "/Paginas/Kalan/Cuentas/filtro": JsfBase.getFlashAttribute("retorno"));
      this.toLoadTiposMediosPagos();
      this.doLoad(); 
      this.toLoadEmpresas();
      this.toLoadAfectaciones();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
	
  public void doLoad() {
    Map<String, Object> params= new HashMap<>();
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      params.put(Constantes.SQL_CONDICION, "id_cuenta_movimiento= "+ this.idCuentaMovimiento);      
      switch (this.accion) {
        case AGREGAR:
          this.cuenta= new Cuenta();
          this.cuenta.setIdCuentaOrigen(ECuentasOrigenes.BANCOS_ABONOS.getIdCuentaOrigen());
          this.cuenta.setIkTipoAfectacion(new UISelectEntity(2L));
          if(!Objects.equals(this.attrs.get("tiposMediosPagos"), null))
            this.cuenta.setIkTipoMedioPago(UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("tiposMediosPagos")));
          this.cuenta.setIdUsuario(JsfBase.getIdUsuario());
          break;
        case MODIFICAR:
        case CONSULTAR:
          this.cuenta= (Cuenta)DaoFactory.getInstance().toEntity(Cuenta.class, "TcKalanCuentasMovimientosDto", params);
          this.cuenta.setIkEmpresa(new UISelectEntity(this.cuenta.getIdEmpresa()));
          this.cuenta.setIkEmpresaCuenta(new UISelectEntity(this.cuenta.getIdEmpresaCuenta()));
          this.cuenta.setIkTipoAfectacion(new UISelectEntity(this.cuenta.getIdTipoAfectacion()));
          this.cuenta.setIkTipoMedioPago(new UISelectEntity(this.cuenta.getIdTipoMedioPago()));
          this.doLoadCuentas();
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
  } 

	public String doAplicar() {
    String regresar = null;
    try {      
      if(Objects.equals(this.cuenta.getIdCuentaEstatus(), null) || this.cuenta.getIdCuentaEstatus()< 3L)
        this.cuenta.setIdCuentaEstatus(2L);
      regresar= this.doAceptar();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return regresar;
  }

	public String doAceptar() {
    Transaccion transaccion= null;
    String regresar        = null;
    try {
      this.cuenta.setIdCuentaOrigen(Objects.equals(this.cuenta.getIdTipoAfectacion(), 2L)? ECuentasOrigenes.BANCOS_ABONOS.getIdCuentaOrigen(): ECuentasOrigenes.BANCOS_CARGOS.getIdCuentaOrigen());
      this.cuenta.setIdEmpresaDestino(null);
      transaccion= new Transaccion(this.cuenta);
      if(transaccion.ejecutar(this.accion)) {
        regresar= this.doCancelar();
        JsfBase.addMessage("Se registro el movimiento de forma correcta", ETipoMensaje.INFORMACION);
      } // if
      else 
        JsfBase.addMessage("Ocurrió un error al registrar el movimiento", ETipoMensaje.ERROR);      
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } 

  public String doCancelar() {
		JsfBase.setFlashAttribute("idCuentaMovimientoProcess", this.cuenta.getIdCuentaMovimiento());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } 
  
	private void toLoadEmpresas() {
		List<Columna> columns        = new ArrayList<>();
    Map<String, Object> params   = new HashMap<>();
    List<UISelectEntity> empresas= null;
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        empresas= (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave");
      else
        empresas= (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns);
      this.attrs.put("empresas", empresas);
      if(empresas!= null && !empresas.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) 
          this.cuenta.setIkEmpresa(UIBackingUtilities.toFirstKeySelectEntity(empresas));
      } // if
      if(Objects.equals(this.accion, EAccion.AGREGAR)) 
        this.doLoadCuentas();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}

  public void doLoadCuentas() {
    List<UISelectEntity> empresaCuentas= null;
    Map<String, Object> params         = new HashMap<>();
    try {
			params.put("idEmpresa", this.cuenta.getIdEmpresa());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      if(Objects.equals(this.cuenta.getIdEmpresa(), -1L))
        empresaCuentas= UIEntity.seleccione("TcKalanEmpresasCuentasDto", params, "banco");
      else
        empresaCuentas= UIEntity.build("TcKalanEmpresasCuentasDto", params);
      this.attrs.put("empresaCuentas", empresaCuentas);
      if(empresaCuentas!= null && !empresaCuentas.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) 
          this.cuenta.setIkEmpresaCuenta(UIBackingUtilities.toFirstKeySelectEntity(empresaCuentas));
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

	private void toLoadAfectaciones() {
		List<Columna> columns            = new ArrayList<>();
    Map<String, Object> params       = new HashMap<>();
    List<UISelectEntity> afectaciones= null;
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      afectaciones= (List<UISelectEntity>) UIEntity.seleccione("TcKalanTiposAfectacionesDto", params, columns, "clave");
      this.attrs.put("afectaciones", afectaciones);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}

	private void toLoadTiposMediosPagos() {
		List<UISelectEntity> tiposMediosPagos= null;
		Map<String, Object>params            = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, "id_cobro_caja= 1");
			tiposMediosPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposMediosPagos", tiposMediosPagos);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} 

  public void doUpdateMedios() {
    try {      
      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }  

}
