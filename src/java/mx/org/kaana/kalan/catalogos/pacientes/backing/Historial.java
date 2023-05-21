package mx.org.kaana.kalan.catalogos.pacientes.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kalan.catalogos.pacientes.beans.Clinico;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.kalan.catalogos.pacientes.reglas.Transaccion;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "kalanCatalogosPacientesHistorial")
@ViewScoped
public class Historial extends IBaseAttribute implements Serializable {

  private static final Log LOG = LogFactory.getLog(Historial.class);
  private static final long serialVersionUID = -7668101942302148046L;
  
  private Clinico paciente;

  public Clinico getPaciente() {
    return paciente;
  }

  public void setPaciente(Clinico paciente) {
    this.paciente = paciente;
  }
  
  @PostConstruct
  @Override
  protected void init() {
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idCliente", JsfBase.getFlashAttribute("idCliente"));
			this.attrs.put("retorno", Objects.equals(JsfBase.getFlashAttribute("retorno"), null)? "filtro": JsfBase.getFlashAttribute("retorno"));
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
  
  public void doLoad() {
    EAccion eaccion           = null;
    Map<String, Object> params= new HashMap<>();
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
        case MODIFICAR:					
        case CONSULTAR:					
          params.put("idCliente", this.attrs.get("idCliente"));      
          this.paciente= (Clinico)DaoFactory.getInstance().toEntity(Clinico.class, "TcKalanHistorialesDto", "identically", params);
          break;
      } // switch
      if(Objects.equals(this.paciente, null))
        this.paciente= new Clinico((Long)this.attrs.get("idCliente"));
      else
        this.attrs.put("accion", EAccion.MODIFICAR);
      this.paciente.init();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } 
  
  public String doAceptar() {
    Transaccion transaccion= null;
    String regresar        = null;
		EAccion eaccion        = null;
    try {			
			eaccion= (EAccion) this.attrs.get("accion");
			transaccion = new Transaccion(this.paciente);
			if (transaccion.ejecutar(eaccion)) {
				regresar = this.doCancelar();
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el registro del historial"), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el historial", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } 
  
  public String doCancelar() {
    JsfBase.setFlashAttribute("idClienteProcess", this.attrs.get("idCliente"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } 
 
  public void doCalculateEdad() {
    this.paciente.toEdad();
  }
  
}
