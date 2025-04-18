package mx.org.kaana.kalan.catalogos.movimientos.backing;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.kalan.catalogos.movimientos.reglas.Transaccion;
import mx.org.kaana.kalan.db.dto.TcKalanTiposMovimientosDto;

@Named(value = "kalanCatalogosMovimientosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;
	private TcKalanTiposMovimientosDto movimiento;

  public TcKalanTiposMovimientosDto getMovimiento() {
    return movimiento;
  }

  public void setMovimiento(TcKalanTiposMovimientosDto movimiento) {
    this.movimiento = movimiento;
  }

	@PostConstruct
  @Override
  protected void init() {		
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idTipoMovimiento", JsfBase.getFlashAttribute("idTipoMovimiento"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  public void doLoad() {
    EAccion eaccion      = null;
    Long idTipoMovimiento= -1L;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.movimiento= new TcKalanTiposMovimientosDto();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          idTipoMovimiento= (Long)this.attrs.get("idTipoMovimiento");
          this.movimiento= (TcKalanTiposMovimientosDto)DaoFactory.getInstance().findById(TcKalanTiposMovimientosDto.class, idTipoMovimiento);
          break;
      } // switch
    } // try
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
			eaccion= (EAccion) this.attrs.get("accion");
			transaccion = new Transaccion(this.movimiento);
			if (transaccion.ejecutar(eaccion)) {
				regresar = this.doCancelar();
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el registro !"), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el tipo movimiento", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
    JsfBase.setFlashAttribute("idTipoMovimiento", this.movimiento.getIdTipoMovimiento());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
	
}