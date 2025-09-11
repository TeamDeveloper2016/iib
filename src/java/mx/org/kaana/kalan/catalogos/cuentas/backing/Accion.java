package mx.org.kaana.kalan.catalogos.cuentas.backing;

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
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kalan.catalogos.cuentas.beans.Cuenta;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.kalan.catalogos.cuentas.reglas.Transaccion;
import mx.org.kaana.kalan.cuentas.enums.ECuentasOrigenes;
import mx.org.kaana.kalan.db.dto.TcKalanEmpresasCuentasDto;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "kalanCatalogosCuentasAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639361L;
  
	private Cuenta cuenta;
  private Long idEmpresaCuenta;
  private EAccion accion;

  public TcKalanEmpresasCuentasDto getCuenta() {
    return cuenta;
  }

  public void setCuenta(Cuenta cuenta) {
    this.cuenta = cuenta;
  }

  @PostConstruct
  @Override
  protected void init() {		
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion= Objects.equals(JsfBase.getFlashAttribute("accion"), null)? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.idEmpresaCuenta= Objects.equals(JsfBase.getFlashAttribute("idEmpresaCuenta"), null)? -1L: (Long)JsfBase.getFlashAttribute("idEmpresaCuenta");
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("aperturar", Objects.equals(this.accion, EAccion.AGREGAR));
			this.attrs.put("aperturo", null);
			this.attrs.put("titulo", "¿ Desea que se aperture la cuenta de bancos ?");
			this.doLoad();
      this.toLoadEmpresas();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  public void doLoad() {
    Map<String, Object> params= new HashMap<>();
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      switch (this.accion) {
        case AGREGAR:											
          this.cuenta= new Cuenta();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          params.put("idEmpresaCuenta", this.idEmpresaCuenta);
          this.cuenta= (Cuenta)DaoFactory.getInstance().toEntity(Cuenta.class, "TcKalanEmpresasCuentasDto", "igual", params);
          this.cuenta.setIkEmpresa(new UISelectEntity(this.cuenta.getIdEmpresa()));
          this.toCheckCuenta();
          break;
      } // switch
      this.toLoadBancos();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    try {			
			transaccion = new Transaccion(this.cuenta, (Boolean)this.attrs.get("aperturar"));
			if (transaccion.ejecutar(this.accion)) {
				regresar = this.doCancelar();
				JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el registro !"), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar la cuenta", ETipoMensaje.ERROR);      			
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
    JsfBase.setFlashAttribute("idEmpresaCuenta", this.cuenta.getIdEmpresaCuenta());
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
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}
  
  private void toLoadBancos() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> bancos= UISelect.seleccione("TcManticBancosDto", params, "idKey|nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("bancos", bancos);
      if(Objects.equals(this.accion, EAccion.AGREGAR))
        this.cuenta.setIdBanco((Long)bancos.get(0).getValue());
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }    
 
  private void toCheckCuenta() {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idEmpresaCuenta", this.cuenta.getIdEmpresaCuenta());      
      params.put("idPivote", ECuentasOrigenes.BANCOS_APERTURA.getIdCuentaOrigen());      
      columns.add(new Columna("referencia", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("importe", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("fechaAplicacion", EFormatoDinamicos.MILES_CON_DECIMALES));
      Entity row= (Entity)DaoFactory.getInstance().toEntity("TcKalanCuentasMovimientosDto", "existe", params);
      if(!Objects.equals(row, null) && !row.isEmpty()) {
        UIBackingUtilities.toFormatEntity(row, columns);
        this.attrs.put("aperturo", row.toString("observaciones").concat(" CON ")+ row.toDouble("importe")+ " EL DÍA ".concat(row.toString("fechaAplicacion")));
  			this.attrs.put("titulo", "¿ Desea que se modifique la apertura de la cuenta ?");
      } // if
      else
 			  this.attrs.put("aperturo", null);
   } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
  }
  
  public void doCheckImporte() {
    try {      
      if(Objects.equals(this.attrs.get("aperturar"), null) || (Boolean)this.attrs.get("aperturar"))  
        UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:importe', {validaciones: 'requerido|flotante|mayor({\"cuanto\":0})', mascara: 'libre'});");
      else 
        UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:importe', {validaciones: 'libre', mascara: 'libre'});");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
}