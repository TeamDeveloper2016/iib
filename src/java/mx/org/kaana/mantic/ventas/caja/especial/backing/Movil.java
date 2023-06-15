package mx.org.kaana.mantic.ventas.caja.especial.backing;

import java.io.Serializable;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelectEntity;

@Named(value= "manticVentasCajaEspecialMovil")
@ViewScoped
public class Movil extends Accion implements Serializable {

  private static final long serialVersionUID= 327393488565639127L;
  
  @Override
	protected void loadDomiciliosFactura(Long idCliente) throws Exception {
    try {      
      super.loadDomiciliosFactura(idCliente);
      List<UISelectEntity> domicilios= (List<UISelectEntity>)this.attrs.get("domiciliosFactura");
      UIBackingUtilities.toMovilColumnLength("calle", 10, domicilios, Boolean.TRUE);      
      UIBackingUtilities.toMovilColumnLength("asentamiento", 10, domicilios, Boolean.TRUE);      
      UIBackingUtilities.toMovilColumnLength("municipio", 10, domicilios, Boolean.TRUE);      
    } // try
    catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
    } // catch	
  }
  
  @Override
	protected void toLoadRegimenesFiscales() {
    try {      
      super.toLoadRegimenesFiscales();
      List<UISelectEntity> regimenesFiscales= (List<UISelectEntity>)this.attrs.get("regimenesFiscales");
      UIBackingUtilities.toMovilColumnLength("nombre", 25, regimenesFiscales, Boolean.TRUE);      
    } // try
    catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
    } // catch	
	} // toLoadRegimenesFiscales

  @Override
  public void refreshTicketsAbiertos() {
    try {      
      super.refreshTicketsAbiertos();
      this.toCutDescripciones();
    } // try
    catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
    } // catch	
  }

  @Override
  public void doLoadTicketAbiertos() {
    try {      
      super.doLoadTicketAbiertos();
      this.toCutDescripciones();
    } // try
    catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
    } // catch	
  }
  
  private void toCutDescripciones() {
    List<UISelectEntity> tickets= (List<UISelectEntity>)this.attrs.get("ticketsAbiertos");
    UIBackingUtilities.toMovilColumnLength("cliente", 20, tickets, Boolean.TRUE);      
    UIBackingUtilities.toMovilColumnLength("nombre", 10, tickets, Boolean.TRUE);      
  }
  
}
