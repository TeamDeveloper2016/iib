package mx.org.kaana.kalan.catalogos.conceptos.backing;

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
import mx.org.kaana.kalan.catalogos.conceptos.reglas.Transaccion;
import mx.org.kaana.kalan.db.dto.TcKalanTiposConceptosDto;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "kalanCatalogosConceptosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565619367L;
  
	private TcKalanTiposConceptosDto concepto;

  public TcKalanTiposConceptosDto getConcepto() {
    return concepto;
  }

  public void setConcepto(TcKalanTiposConceptosDto concepto) {
    this.concepto = concepto;
  }

	@PostConstruct
  @Override
  protected void init() {		
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idTipoConcepto", JsfBase.getFlashAttribute("idTipoConcepto"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  public void doLoad() {
    EAccion eaccion             = null;
    Long idTipoConcepto= -1L;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.concepto= new TcKalanTiposConceptosDto();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          idTipoConcepto= (Long)this.attrs.get("idTipoConcepto");
          this.concepto= (TcKalanTiposConceptosDto)DaoFactory.getInstance().findById(TcKalanTiposConceptosDto.class, idTipoConcepto);
          break;
      } // switch
      this.toLoadClasificaciones();
    } // try // try // try // try
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
			transaccion = new Transaccion(this.concepto);
			if (transaccion.ejecutar(eaccion)) {
				regresar = this.doCancelar();
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el registro !"), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el tipo de concepto", ETipoMensaje.ERROR);      			
    } // try // try // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
    JsfBase.setFlashAttribute("idTipoConcepto", this.concepto.getIdTipoConcepto());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar

  private void toLoadClasificaciones() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> movimientos= UISelect.seleccione("TcKalanTiposMovimientosDto", params, "idKey|descripcion", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("movimientos", movimientos);
      if(Objects.equals((EAccion)this.attrs.get("accion"), EAccion.AGREGAR))
        this.concepto.setIdTipoMovimiento((Long)movimientos.get(0).getValue());
    } // try // try // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }    
  
}