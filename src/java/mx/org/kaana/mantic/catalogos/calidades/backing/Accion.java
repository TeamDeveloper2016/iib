package mx.org.kaana.mantic.catalogos.calidades.backing;

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
import mx.org.kaana.mantic.catalogos.calidades.reglas.Transaccion;
import mx.org.kaana.mantic.db.dto.TcManticNotasCalidadesDto;


@Named(value = "manticCatalogosCalidadesAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 127393488565639367L;
	private TcManticNotasCalidadesDto merma;

	public TcManticNotasCalidadesDto getMerma() {
		return this.merma;
	}

	public void setMerma(TcManticNotasCalidadesDto merma) {
		this.merma = merma;
	}	

	@PostConstruct
  @Override
  protected void init() {		
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("idNotaCalidad", JsfBase.getFlashAttribute("idNotaCalidad"));
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
          this.merma= new TcManticNotasCalidadesDto(
            null, // String descripcion, 
            JsfBase.getIdUsuario(), // Long idUsuario, 
            -1L, // Long idNotaMerma, 
            null // String nombre
          );
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          this.merma= (TcManticNotasCalidadesDto)DaoFactory.getInstance().findById(TcManticNotasCalidadesDto.class, (Long)this.attrs.get("idNotaCalidad"));
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
			transaccion = new Transaccion(this.merma);
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
    JsfBase.setFlashAttribute("idNotaCalidad", this.merma.getIdNotaCalidad());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
	
}