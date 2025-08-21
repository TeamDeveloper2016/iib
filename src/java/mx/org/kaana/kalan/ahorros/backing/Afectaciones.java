package mx.org.kaana.kalan.ahorros.backing;

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
import mx.org.kaana.kalan.ahorros.beans.Afectacion;
import mx.org.kaana.kalan.ahorros.beans.Ahorro;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.kalan.ahorros.reglas.Transaccion;
import mx.org.kaana.kalan.cuentas.enums.ETipoAfectacion;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.enums.ETipoMediosPago;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "kalanAhorrosAfectaciones")
@ViewScoped
public class Afectaciones extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393481565631361L;
  private static final Log LOG = LogFactory.getLog(Afectaciones.class);
  
  private EAccion accion;
  private Ahorro ahorro;
  private Afectacion afectacion;
  private Long idAhorro;

  public Ahorro getAhorro() {
    return ahorro;
  }

  public void setAhorro(Ahorro ahorro) {
    this.ahorro = ahorro;
  }

  public Afectacion getAfectacion() {
    return afectacion;
  }

  public void setAfectacion(Afectacion afectacion) {
    this.afectacion= afectacion;
  }

  @PostConstruct
  @Override
  public void init() {
    try {
			if(Objects.equals(JsfBase.getFlashAttribute("idAhorro"), null))
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion  = EAccion.COMPLEMENTAR;
      this.idAhorro= Objects.equals(JsfBase.getFlashAttribute("idAhorro"), null)? -1L: (Long)JsfBase.getFlashAttribute("idAhorro");
      this.attrs.put("retorno", Objects.equals(JsfBase.getFlashAttribute("retorno"), null)? "/Paginas/Kalan/Ahorros/filtro": JsfBase.getFlashAttribute("retorno"));
      this.toLoadTiposMediosPagos();
      this.toLoadBancos();
      this.doLoad(); 
      this.toLoadEmpresas();
      this.toLoadAfectaciones();
      this.doCheckImporte();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
	
  public void doLoad() {
    Map<String, Object> params = new HashMap<>();
    try {
      params.put(Constantes.SQL_CONDICION, "id_ahorro= "+ this.idAhorro);      
      this.ahorro= (Ahorro)DaoFactory.getInstance().toEntity(Ahorro.class, "TcKalanAhorrosDto", "empleado", params);
      this.ahorro.setIkEmpresaPersona(new UISelectEntity(this.ahorro.getIdEmpresaPersona()));
      this.attrs.put("total", Numero.redondear(this.ahorro.getInicia()+ this.ahorro.getSaldo()));
      switch (this.accion) {
        case COMPLEMENTAR:
          this.afectacion= new Afectacion();
          this.afectacion.setIdAhorro(this.idAhorro);
          this.afectacion.setIkEmpresa(new UISelectEntity(this.ahorro.getIdEmpresa()));
          this.afectacion.setIkEmpresaCuenta(new UISelectEntity(this.ahorro.getIdEmpresaCuenta()));
          this.afectacion.setIkTipoAfectacion(new UISelectEntity(ETipoAfectacion.CARGO.getIdTipoAfectacion()));
          if(!Objects.equals(this.attrs.get("tiposMediosPagos"), null))
            this.afectacion.setIkTipoMedioPago(UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("tiposMediosPagos")));
          if(!Objects.equals(this.attrs.get("bancos"), null))
            this.afectacion.setIkBanco(UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("bancos")));
          this.afectacion.setIdAhorroControl(4L); // PAGADO
          this.afectacion.setIdUsuario(JsfBase.getIdUsuario());
          this.doLoadCuentas();
          break;
      } // switch      
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } 

	public String doAceptar() {
    Transaccion transaccion= null;
    String regresar        = null;
    try {
      this.afectacion.setIdBanco(null);
      if(Objects.equals(this.afectacion.getIdTipoMedioPago(), ETipoMediosPago.EFECTIVO.getIdTipoMedioPago())) 
        this.afectacion.setReferencia(null);
      transaccion= new Transaccion(this.ahorro, this.afectacion);
      if(transaccion.ejecutar(this.accion)) {
        regresar= this.doCancelar();
        JsfBase.addMessage("Se registro el pago de forma correcta", ETipoMensaje.INFORMACION);
      } // if
      else 
        JsfBase.addMessage("Ocurrió un error al registrar el pago", ETipoMensaje.ERROR);      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } 

  public String doCancelar() {
		JsfBase.setFlashAttribute("idAhorroProcess", this.ahorro.getIdAhorro());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } 
  
	private void toLoadAfectaciones() {
		List<Columna> columns        = new ArrayList<>();
    Map<String, Object> params   = new HashMap<>();
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

  public void doCheckImporte() {
    try {
      if(Objects.equals(this.afectacion.getIdTipoAfectacion(), ETipoAfectacion.ABONO.getIdTipoAfectacion()))  // ABONO
        UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:importe', {validaciones: 'requerido|flotante|mayor({\"cuanto\":0})|menor-a({\"cual\": \"contenedorGrupos\\\\\\\\:saldo\"})', mascara: 'libre', mensaje: 'El importe debe de ser menor o igual a lo ahorrado'});");
      else 
        UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:importe', {validaciones: 'requerido|flotante|mayor({\"cuanto\":0})|menor-a({\"cual\": \"contenedorGrupos\\\\\\\\:total\"})', mascara: 'libre', mensaje: 'El importe debe de ser menor o igual al disponible'});");
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
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
    Map<String, Object> params= new HashMap<>();
    try {
			params.put("idEmpresa", this.afectacion.getIdEmpresa());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectEntity> empresaCuentas= UIEntity.build("TcKalanEmpresasCuentasDto", params);
      this.attrs.put("empresaCuentas", empresaCuentas);
      if(empresaCuentas!= null && !empresaCuentas.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.COMPLEMENTAR)) 
          this.afectacion.setIkEmpresaCuenta(UIBackingUtilities.toFirstKeySelectEntity(empresaCuentas));
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
	
	private void toLoadTiposMediosPagos() {
		List<UISelectEntity> tiposMediosPagos= null;
		Map<String, Object>params            = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, "id_cobro_caja= 1");
			tiposMediosPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposMediosPagos", tiposMediosPagos);
			this.attrs.put("ikTipoMedioPago", UIBackingUtilities.toFirstKeySelectEntity(tiposMediosPagos));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} 
  
	private void toLoadBancos() {
		List<UISelectEntity> bancos= null;
		Map<String, Object> params = new HashMap<>();
		List<Columna> columns      = new ArrayList<>();
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			bancos= UIEntity.build("TcManticBancosDto", "row", params, columns, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("bancos", bancos);
			this.attrs.put("ikBanco", UIBackingUtilities.toFirstKeySelectEntity(bancos));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} 
  
}
