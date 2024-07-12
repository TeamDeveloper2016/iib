package mx.org.kaana.mantic.catalogos.clases.backing;

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
import mx.org.kaana.mantic.catalogos.clases.reglas.Transaccion;
import mx.org.kaana.mantic.db.dto.TcManticTiposClasesDto;


@Named(value = "manticCatalogosClasesAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 121393488565639367L;
  
	private TcManticTiposClasesDto clase;

	public TcManticTiposClasesDto getClase() {
		return this.clase;
	}

	public void setClase(TcManticTiposClasesDto clase) {
		this.clase = clase;
	}	

	@PostConstruct
  @Override
  protected void init() {		
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("idTipoClase", JsfBase.getFlashAttribute("idTipoClase"));
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
          this.clase= new TcManticTiposClasesDto(
            null, // String descripcion, 
            -1L, // Long idTipoClase, 
            null, // String clave, 
            null, // String sat
            2L // Long idTerminado
          );
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          this.clase= (TcManticTiposClasesDto)DaoFactory.getInstance().findById(TcManticTiposClasesDto.class, (Long)this.attrs.get("idTipoClase"));
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
			transaccion = new Transaccion(this.clase);
			if (transaccion.ejecutar(eaccion)) {
				regresar = this.doCancelar();
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR)? "agregó": "modificó").concat(" el registro !"), ETipoMensaje.INFORMACION);
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
    JsfBase.setFlashAttribute("idTipoClase", this.clase.getIdTipoClase());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
	
}