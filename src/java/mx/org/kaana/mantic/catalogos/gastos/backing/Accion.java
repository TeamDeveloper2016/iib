package mx.org.kaana.mantic.catalogos.gastos.backing;

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
import mx.org.kaana.mantic.catalogos.gastos.reglas.Transaccion;
import mx.org.kaana.mantic.db.dto.TcManticTiposCostosDto;


@Named(value = "manticCatalogosGastosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;
	private TcManticTiposCostosDto costo;

	public TcManticTiposCostosDto getCosto() {
		return this.costo;
	}

	public void setCosto(TcManticTiposCostosDto costo) {
		this.costo = costo;
	}	

	@PostConstruct
  @Override
  protected void init() {		
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("idTipoCosto", JsfBase.getFlashAttribute("idTipoCosto"));
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  public void doLoad() {
    EAccion eaccion= null;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.costo= new TcManticTiposCostosDto(
            null, // String descripcion, 
            null, // String clave, 
            JsfBase.getIdUsuario(), // Long idUsuario, 
            1L, // Long idCuenta, 
            -1L, // Long idTipoCosto
            null // String observaciones
          );
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          this.costo= (TcManticTiposCostosDto)DaoFactory.getInstance().findById(TcManticTiposCostosDto.class, (Long)this.attrs.get("idTipoCosto"));
          break;
      } // switch
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
		EAccion eaccion        = null;
    try {			
			eaccion= (EAccion) this.attrs.get("accion");
			transaccion = new Transaccion(this.costo);
			if (transaccion.ejecutar(eaccion)) {
				regresar = this.doCancelar();
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR)? "agregó": "modificó").concat(" el costo !"), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el costo", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
    JsfBase.setFlashAttribute("idTipoCosto", this.costo.getIdTipoCosto());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
	
}