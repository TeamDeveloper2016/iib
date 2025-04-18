package mx.org.kaana.kalan.catalogos.clasificaciones.backing;

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
import mx.org.kaana.kalan.catalogos.clasificaciones.reglas.Transaccion;
import mx.org.kaana.kalan.db.dto.TcKalanGastosClasificacionesDto;

@Named(value = "kalanCatalogosClasificacionesAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;
	private TcKalanGastosClasificacionesDto clasificacion;

  public TcKalanGastosClasificacionesDto getClasificacion() {
    return clasificacion;
  }

  public void setClasificacion(TcKalanGastosClasificacionesDto clasificacion) {
    this.clasificacion = clasificacion;
  }

	@PostConstruct
  @Override
  protected void init() {		
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idGastoClasificacion", JsfBase.getFlashAttribute("idGastoClasificacion"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  public void doLoad() {
    EAccion eaccion         = null;
    Long idGastoClasificacion= -1L;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.clasificacion= new TcKalanGastosClasificacionesDto();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          idGastoClasificacion= (Long)this.attrs.get("idGastoClasificacion");
          this.clasificacion= (TcKalanGastosClasificacionesDto)DaoFactory.getInstance().findById(TcKalanGastosClasificacionesDto.class, idGastoClasificacion);
          break;
      } // switch
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
			eaccion= (EAccion) this.attrs.get("accion");
			transaccion = new Transaccion(this.clasificacion);
			if (transaccion.ejecutar(eaccion)) {
				regresar = this.doCancelar();
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el registro !"), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el clasificacion", ETipoMensaje.ERROR);      			
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
    JsfBase.setFlashAttribute("idGastoClasificacion", this.clasificacion.getIdGastoClasificacion());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
	
}