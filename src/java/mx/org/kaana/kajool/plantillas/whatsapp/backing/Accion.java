package mx.org.kaana.kajool.plantillas.whatsapp.backing;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.wassenger.Bonanza;


@Named(value = "manticPlantillasWhatsappAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 3212393488565639367L;

	@PostConstruct
  @Override
  protected void init() {		
    try {
      this.attrs.put("persona", "");
      this.attrs.put("celular", "");
      this.attrs.put("mensaje", "");
      this.attrs.put("aplicar", Boolean.FALSE);      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  public String doAceptar() {  
    String regresar= null;
    String celular = (String)this.attrs.get("celular");
    try {	
      if((Boolean)this.attrs.get("aplicar"))
        celular= Bonanza.IMOX_GROUP_MANTIC;
      Bonanza mensaje= new Bonanza((String)this.attrs.get("persona"), celular);
			if (mensaje.doSendPrueba((String)this.attrs.get("mensaje"))) {
        regresar= this.doCancelar();
				JsfBase.addMessage("Se envío el mensaje con éxito !", ETipoMensaje.INFORMACION);
      } // if  
			else 
				JsfBase.addMessage("Ocurrió un error al enviar el mensaje !", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
    this.attrs.put("persona", "");
    this.attrs.put("celular", "");
    this.attrs.put("mensaje", "");
    return null;
  } // doCancelar
	
}