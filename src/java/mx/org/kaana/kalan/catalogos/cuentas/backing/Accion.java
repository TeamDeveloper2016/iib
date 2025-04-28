package mx.org.kaana.kalan.catalogos.cuentas.backing;

import java.io.Serializable;
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
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.kalan.catalogos.cuentas.reglas.Transaccion;
import mx.org.kaana.kalan.db.dto.TcKalanEmpresasCuentasDto;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "kalanCatalogosCuentasAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639361L;
	private TcKalanEmpresasCuentasDto cuenta;

  public TcKalanEmpresasCuentasDto getCuenta() {
    return cuenta;
  }

  public void setCuenta(TcKalanEmpresasCuentasDto cuenta) {
    this.cuenta = cuenta;
  }

  @PostConstruct
  @Override
  protected void init() {		
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idEmpresaCuenta", JsfBase.getFlashAttribute("idEmpresaCuenta"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  public void doLoad() {
    EAccion eaccion     = null;
    Long idEmpresaCuenta= -1L;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.cuenta= new TcKalanEmpresasCuentasDto();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          idEmpresaCuenta= (Long)this.attrs.get("idEmpresaCuenta");
          this.cuenta= (TcKalanEmpresasCuentasDto)DaoFactory.getInstance().findById(TcKalanEmpresasCuentasDto.class, idEmpresaCuenta);
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
		EAccion eaccion        = null;
    try {			
			eaccion= (EAccion)this.attrs.get("accion");
			transaccion = new Transaccion(this.cuenta);
			if (transaccion.ejecutar(eaccion)) {
				regresar = this.doCancelar();
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el registro !"), ETipoMensaje.INFORMACION);
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
  } // doCancelar

  private void toLoadBancos() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> bancos= UISelect.seleccione("TcManticBancosDto", params, "idKey|nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("bancos", bancos);
      if(Objects.equals((EAccion)this.attrs.get("accion"), EAccion.AGREGAR))
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
  
}