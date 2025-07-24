package mx.org.kaana.kalan.creditos.backing;

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
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kalan.creditos.beans.Credito;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.kalan.creditos.reglas.Transaccion;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "kalanCreditosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565631361L;
  private static final Log LOG = LogFactory.getLog(Accion.class);
  
  private EAccion accion;
  private Credito credito;
  private Long idCredito;

  public Credito getCredito() {
    return credito;
  }

  public void setCredito(Credito credito) {
    this.credito = credito;
  }
  
  public Boolean getAplicar() {
    return Objects.equals(this.accion, EAccion.AGREGAR) || Objects.equals(this.accion, EAccion.MODIFICAR);
  }
  
  @PostConstruct
  @Override
  public void init() {
    try {
      this.accion   = Objects.equals(JsfBase.getFlashAttribute("accion"), null)? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.idCredito= Objects.equals(JsfBase.getFlashAttribute("idCredito"), null)? -1L: (Long)JsfBase.getFlashAttribute("idCredito");
      this.attrs.put("retorno", Objects.equals(JsfBase.getFlashAttribute("retorno"), null)? "/Paginas/Kalan/Creditos/filtro": JsfBase.getFlashAttribute("retorno"));
      this.toLoadEmpresas();
      this.toLoadAcreedores();
      this.doLoad(); 
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
	
  public void doLoad() {
    Map<String, Object> params = new HashMap<>();
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      params.put(Constantes.SQL_CONDICION, "id_credito= "+ this.idCredito);      
      switch (this.accion) {
        case AGREGAR:
          this.credito= new Credito();
          break;
        case MODIFICAR:
        case CONSULTAR:
          this.credito= (Credito)DaoFactory.getInstance().toEntity(Credito.class, "TcKalanCreditosDto", params);
          this.credito.setIkEmpresa(new UISelectEntity(this.credito.getIdEmpresa()));
          this.credito.setIkAcreedor(new UISelectEntity(this.credito.getIdAcreedor()));
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
      transaccion= new Transaccion(this.credito);
      if(transaccion.ejecutar(this.accion)) {
        regresar= this.doCancelar();
        JsfBase.addMessage("Se registro el crédito de forma correcta", ETipoMensaje.INFORMACION);
      } // if
      else 
        JsfBase.addMessage("Ocurrió un error al registrar el crédito", ETipoMensaje.ERROR);      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } 

  public String doCancelar() {
		JsfBase.setFlashAttribute("idCreditoProcess", this.credito.getIdCredito());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } 
  
	private void toLoadAcreedores() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
 			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
 			params.put(Constantes.SQL_CONDICION, "id_activo= 1");
  		this.attrs.put("acreedores", UIEntity.seleccione("TcManticAcreedoresDto", "row", params, columns, "clave"));
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

  public void doUpdateLimite() {
    try {      
      Calendar calendar= Calendar.getInstance();
      // calendar.setTimeInMillis(this.credito.getLimite().getTime());
      calendar.add(Calendar.MONTH, this.credito.getPlazo().intValue());
      this.credito.getLimite().setTime(calendar.getTimeInMillis());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
}
