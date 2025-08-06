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
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kalan.ahorros.beans.Ahorro;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.kalan.ahorros.reglas.Transaccion;
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

  public Ahorro getAhorro() {
    return ahorro;
  }

  public void setAhorro(Ahorro ahorro) {
    this.ahorro = ahorro;
  }
  
  public Boolean getAplicar() {
    return Objects.equals(this.accion, EAccion.AGREGAR) || Objects.equals(this.accion, EAccion.MODIFICAR);
  }
  
  @PostConstruct
  @Override
  public void init() {
    try {
//  		if(Objects.equals(JsfBase.getFlashAttribute("accion"), null))
//				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion  = Objects.equals(JsfBase.getFlashAttribute("accion"), null)? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.idAhorro= Objects.equals(JsfBase.getFlashAttribute("idAhorro"), null)? -1L: (Long)JsfBase.getFlashAttribute("idAhorro");
      this.attrs.put("retorno", Objects.equals(JsfBase.getFlashAttribute("retorno"), null)? "/Paginas/Kalan/Ahorros/filtro": JsfBase.getFlashAttribute("retorno"));
      this.doLoad(); 
      this.toLoadEmpresas();
      this.doUpdateSaldo();
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
          break;
        case MODIFICAR:
        case CONSULTAR:
          this.ahorro= (Ahorro)DaoFactory.getInstance().toEntity(Ahorro.class, "TcKalanAhorrosDto", params);
          this.ahorro.setIkEmpresa(new UISelectEntity(this.ahorro.getIdEmpresa()));
          this.ahorro.setIkEmpresaCuenta(new UISelectEntity(this.ahorro.getIdEmpresaCuenta()));
          this.ahorro.setIkEmpresaPersona(new UISelectEntity(this.toLoadEmpleados(this.ahorro.getIdEmpresaPersona())));
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

	public String doAplicar() {
    String regresar = null;
    EAccion temporal= this.accion;
    try {      
      this.accion= EAccion.PROCESAR;
      regresar= this.doAceptar();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      this.accion= temporal;
    } // finally
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
  		regresar= (Entity)DaoFactory.getInstance().toEntity("VistaAhorrosDto", "empleados", params);
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

  public void doUpdateLimite() {
    try {      
      Calendar calendar= Calendar.getInstance();
      calendar.setTimeInMillis(this.ahorro.getFechaAplicacion().getTime());
      calendar.add(Calendar.MONTH, this.ahorro.getPlazo().intValue());
      this.ahorro.getLimite().setTime(calendar.getTimeInMillis());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doUpdateSaldo() {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idEmpresaPersona", this.ahorro.getIkEmpresaPersona().getKey());      
      Entity saldo= (Entity)DaoFactory.getInstance().toEntity("VistaAhorrosDto", "disponible", params);
      if(!Objects.equals(saldo, null) && !saldo.isEmpty()) 
        this.attrs.put("disponible", saldo.toDouble("disponible"));
      else {
        params.put(Constantes.SQL_CONDICION, "id_empresa_persona= "+ this.ahorro.getIkEmpresaPersona().getKey());      
        saldo= (Entity)DaoFactory.getInstance().toEntity("TrManticEmpresaPersonalDto", params);
        if(!Objects.equals(saldo, null) && !saldo.isEmpty()) 
          this.attrs.put("disponible", saldo.toDouble("limite"));
        else
          this.attrs.put("disponible", 0D);
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
  
}
