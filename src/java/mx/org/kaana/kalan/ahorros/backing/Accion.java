package mx.org.kaana.kalan.ahorros.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kalan.ahorros.beans.Afectacion;
import mx.org.kaana.kalan.ahorros.beans.Ahorro;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.kalan.ahorros.reglas.Transaccion;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "kalanAhorrosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488561631311L;
  private static final Log LOG = LogFactory.getLog(Accion.class);
  
  private EAccion accion;
  private Ahorro ahorro;
  private Long idAhorro;
  private Double cuota;

  public Ahorro getAhorro() {
    return ahorro;
  }

  public void setAhorro(Ahorro ahorro) {
    this.ahorro = ahorro;
  }
  
  public Boolean getAplicar() {
    return Objects.equals(this.accion, EAccion.AGREGAR) || Objects.equals(this.accion, EAccion.MODIFICAR);
  }

  public Boolean getIsEdit() {
    return !(Objects.equals(this.ahorro.getIdAhorroEstatus(), 1L) || Objects.equals(this.ahorro.getIdAhorroEstatus(), 2L));
  }
  
  public Boolean getIsAdmin() {
		try {
      return JsfBase.isEncargado();
		} // try
		catch (Exception e) {			
			Error.mensaje(e);			
		} // catch		
    return Boolean.FALSE;
  }
  
  @PostConstruct
  @Override
  public void init() {
    try {
  		if(Objects.equals(JsfBase.getFlashAttribute("accion"), null))
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion  = Objects.equals(JsfBase.getFlashAttribute("accion"), null)? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.idAhorro= Objects.equals(JsfBase.getFlashAttribute("idAhorro"), null)? -1L: (Long)JsfBase.getFlashAttribute("idAhorro");
      this.attrs.put("retorno", Objects.equals(JsfBase.getFlashAttribute("retorno"), null)? "/Paginas/Kalan/Ahorros/filtro": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("periodos", 0L);
      this.doLoad(); 
      this.toLoadEmpresas();
      this.toLoadTiposMediosPagos();
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
      params.put(Constantes.SQL_CONDICION, "id_ahorro= "+ this.idAhorro);      
      switch (this.accion) {
        case AGREGAR:
          this.ahorro= new Ahorro();
          this.ahorro.prepare();
          this.ahorro.setIdUsuario(JsfBase.getIdUsuario());
          break;
        case MODIFICAR:
        case CONSULTAR:
          this.ahorro= (Ahorro)DaoFactory.getInstance().toEntity(Ahorro.class, "TcKalanAhorrosDto", params);
          this.ahorro.toLoadCuotas();
          this.attrs.put("periodos", this.ahorro.getPeriodos());
          this.ahorro.setIkEmpresa(new UISelectEntity(this.ahorro.getIdEmpresa()));
          this.ahorro.setIkEmpresaCuenta(new UISelectEntity(this.ahorro.getIdEmpresaCuenta()));
          this.ahorro.setIkEmpresaPersona(new UISelectEntity(this.toLoadEmpleados(this.ahorro.getIdEmpresaPersona())));
          this.ahorro.setIkTipoMedioPago(new UISelectEntity(this.ahorro.getCuotas().get(0).getIdTipoMedioPago()));
          this.ahorro.setReferencia(this.ahorro.getCuotas().get(0).getReferencia());
          this.doLoadCuentas();
          break;
      } // switch      
      this.cuota= this.ahorro.getCuota();
      this.toNameDayOfWeek();
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
    String regresar= null;
    try {      
      if(Objects.equals(this.ahorro.getIdAhorroEstatus(), null) || this.ahorro.getIdAhorroEstatus()< 3L)
        this.ahorro.setIdAhorroEstatus(2L);
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
      transaccion= new Transaccion(this.ahorro);
      if(transaccion.ejecutar(this.accion)) {
        regresar= this.doCancelar();
        JsfBase.addMessage("Se registro el ahorro de forma correcta", ETipoMensaje.INFORMACION);
      } // if
      else 
        JsfBase.addMessage("Ocurrió un error al registrar el ahorro", ETipoMensaje.ERROR);      
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
  
	public List<UISelectEntity> doCompleteEmpleado(String codigo) {
 		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
		boolean buscaPorCodigo    = false;
    try {
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("curp", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(!Cadena.isVacio(codigo)) {
  			codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim();
				codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*");
			} // if	
			else
				codigo= "WXYZ";
  		params.put("codigo", codigo);
			if(buscaPorCodigo)
        this.attrs.put("empleados", UIEntity.build("VistaPrestamosDto", "porCodigo", params, columns, 40L));
			else
        this.attrs.put("empleados", UIEntity.build("VistaPrestamosDto", "porNombre", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("empleados");
	}	
  
	private Entity toLoadEmpleados(Long idEmpresaPersona) {
    Entity regresar           = null;
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("curp", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
 			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      if(Objects.equals(this.accion, EAccion.AGREGAR))
   			params.put(Constantes.SQL_CONDICION, "tr_mantic_empresa_personal.id_empresa_persona= "+ idEmpresaPersona+ " and tr_mantic_empresa_personal.id_activo= 1");
      else    
   			params.put(Constantes.SQL_CONDICION, "tr_mantic_empresa_personal.id_empresa_persona= "+ idEmpresaPersona);
  		regresar= (Entity)DaoFactory.getInstance().toEntity("VistaPrestamosDto", "empleados", params);
      if(Objects.equals(regresar, null))
        regresar= new Entity(-1L);
      else
        UIBackingUtilities.toFormatEntity(regresar, columns);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
    return regresar;
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
          this.ahorro.setIkEmpresa(UIBackingUtilities.toFirstKeySelectEntity(empresas));
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
			params.put("idEmpresa", this.ahorro.getIdEmpresa());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      if(Objects.equals(this.ahorro.getIdEmpresa(), -1L))
        empresaCuentas= UIEntity.seleccione("TcKalanEmpresasCuentasDto", params, "banco");
      else
        empresaCuentas= UIEntity.build("TcKalanEmpresasCuentasDto", params);
      this.attrs.put("empresaCuentas", empresaCuentas);
      if(empresaCuentas!= null && !empresaCuentas.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) 
          this.ahorro.setIkEmpresaCuenta(UIBackingUtilities.toFirstKeySelectEntity(empresaCuentas));
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

  private void toNameDayOfWeek() {
    try {      
      Calendar calendar= Calendar.getInstance();
      calendar.setTimeInMillis(this.ahorro.getFechaArranque().getTime());
      this.attrs.put("dia", Fecha.getNombreDia(calendar.get(Calendar.DAY_OF_WEEK)));
    } // try  
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doUpdateLimite() {
    try {      
      this.ahorro.toCalculatePayments();
      this.toNameDayOfWeek();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doUpdateImporte() {
    try {      
      for (Afectacion item: this.ahorro.getCuotas()) {
        if(Objects.equals(1L, item.getIdAhorroControl()) && Objects.equals(this.cuota, item.getImporte()))
          item.setImporte(this.ahorro.getCuota());
      } // for
      this.cuota= this.ahorro.getCuota();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

  public String doColor(Afectacion row) {
    return Objects.equals(row.getSql(), ESql.DELETE)? "janal-tr-yellow": "";
  }
  
  public void doRemove(Afectacion row) {
    try {
      if(Objects.equals(row.getSql(), ESql.INSERT))
        this.ahorro.getCuotas().remove(row);
      else
        row.setSql(ESql.DELETE);
      this.ahorro.toUpdate();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doAgregar() {
    try {
      Afectacion afectacion= this.ahorro.addCuota();
      this.doUpdateMedios();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doRecover(Afectacion row) {
    try {
      row.setSql(ESql.UPDATE);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

	private void toLoadTiposMediosPagos() {
		List<UISelectEntity> tiposMediosPagos= null;
		Map<String, Object>params            = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, "id_cobro_caja= 1");
			tiposMediosPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposMediosPagos", tiposMediosPagos);
      if(tiposMediosPagos!= null && !tiposMediosPagos.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) 
    			this.ahorro.setIkTipoMedioPago(UIBackingUtilities.toFirstKeySelectEntity(tiposMediosPagos));
      } // if  
      if(Objects.equals(this.accion, EAccion.AGREGAR))
        this.doUpdateMedios();        
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} 
  
  public void doUpdateMedios() {
    List<UISelectEntity> tiposMediosPagos= (List<UISelectEntity>)this.attrs.get("tiposMediosPagos"); 
    String medios= "EFECTIVO";
    try {      
      if(!Objects.equals(tiposMediosPagos, null)) {
        int index= tiposMediosPagos.indexOf(this.ahorro.getIkTipoMedioPago());
        if(index>= 0)
          medios= tiposMediosPagos.get(index).toString("nombre");
      } // if
      for (Afectacion item: this.ahorro.getCuotas()) {
        if(Objects.equals(item.getIdAhorroControl(), 1L)) {
          item.setIdTipoMedioPago(this.ahorro.getIkTipoMedioPago().getKey());
          item.setReferencia(!Objects.equals(this.ahorro.getReferencia(), null)? this.ahorro.getReferencia().toUpperCase(): this.ahorro.getReferencia());
          item.setMedios(medios);
          if(Objects.equals(item.getSql(), ESql.SELECT))
            item.setSql(ESql.UPDATE);
        } // if  
      } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }  
  
  public void doUpdateAhorro() {
    try {      
      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
}
