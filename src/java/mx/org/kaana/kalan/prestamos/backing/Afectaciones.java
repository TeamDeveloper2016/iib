package mx.org.kaana.kalan.prestamos.backing;

import java.io.Serializable;
import java.util.ArrayList;
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
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kalan.prestamos.beans.Afectacion;
import mx.org.kaana.kalan.prestamos.beans.Prestamo;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.kalan.prestamos.reglas.Transaccion;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "kalanPrestamosAfectaciones")
@ViewScoped
public class Afectaciones extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565631361L;
  private static final Log LOG = LogFactory.getLog(Afectaciones.class);
  
  private EAccion accion;
  private Prestamo prestamo;
  private Afectacion afectacion;
  private Long idPrestamo;

  public Prestamo getPrestamo() {
    return prestamo;
  }

  public void setPrestamo(Prestamo prestamo) {
    this.prestamo = prestamo;
  }

  public Afectacion getAfectacion() {
    return afectacion;
  }

  public void setAfectacion(Afectacion afectacion) {
    this.afectacion= afectacion;
  }

  @PostConstruct
  @Override
  public void init() {
    try {
      this.accion   = EAccion.COMPLEMENTAR;
      this.idPrestamo= Objects.equals(JsfBase.getFlashAttribute("idPrestamo"), null)? -1L: (Long)JsfBase.getFlashAttribute("idPrestamo");
      this.attrs.put("retorno", Objects.equals(JsfBase.getFlashAttribute("retorno"), null)? "/Paginas/Kalan/Prestamos/filtro": JsfBase.getFlashAttribute("retorno"));
      this.toLoadAfectaciones();
      this.doLoad(); 
      this.doCheckImporte();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
	
  public void doLoad() {
    Map<String, Object> params = new HashMap<>();
    try {
      params.put(Constantes.SQL_CONDICION, "id_prestamo= "+ this.idPrestamo);      
      this.prestamo= (Prestamo)DaoFactory.getInstance().toEntity(Prestamo.class, "TcKalanPrestamosDto", params);
      this.prestamo.setIkEmpresaPersona(new UISelectEntity(this.prestamo.getIdEmpresaPersona()));
      this.attrs.put("total", Numero.redondear(this.prestamo.getImporte()- this.prestamo.getSaldo()));
      switch (this.accion) {
        case COMPLEMENTAR:
          this.afectacion= new Afectacion();
          this.afectacion.setIdPrestamo(this.idPrestamo);
          this.afectacion.setIkTipoAfectacion(new UISelectEntity(2L));
          this.afectacion.setIdUsuario(JsfBase.getIdUsuario());
          break;
      } // switch      
    } // try // try
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
    try {
      transaccion= new Transaccion(this.prestamo, this.afectacion);
      if(transaccion.ejecutar(this.accion)) {
        regresar= this.doCancelar();
        JsfBase.addMessage("Se registro el prestamo de forma correcta", ETipoMensaje.INFORMACION);
      } // if
      else 
        JsfBase.addMessage("Ocurrió un error al registrar el prestamo", ETipoMensaje.ERROR);      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } 

  public String doCancelar() {
		JsfBase.setFlashAttribute("idPrestamoProcess", this.prestamo.getIdPrestamo());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } 
  
	private void toLoadAfectaciones() {
		List<Columna> columns        = new ArrayList<>();
    Map<String, Object> params   = new HashMap<>();
    List<UISelectEntity> afectaciones= null;
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      afectaciones= (List<UISelectEntity>) UIEntity.seleccione("TcKalanTiposAfectacionesDto", params, columns, "clave");
      this.attrs.put("afectaciones", afectaciones);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}

  public void doCheckImporte() {
    try {
      if(Objects.equals(this.afectacion.getIdTipoAfectacion(), 2L))  // ABONO
        UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:importe', {validaciones: 'requerido|flotante|mayor({\"cuanto\":0})|menor-a({\"cual\": \"contenedorGrupos\\\\\\\\:saldo\"})', mascara: 'libre', mensaje: 'El cargo debe de ser menor o igual a lo abonado'});");
      else 
        UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:importe', {validaciones: 'requerido|flotante|mayor({\"cuanto\":0})|menor-a({\"cual\": \"contenedorGrupos\\\\\\\\:total\"})', mascara: 'libre', mensaje: 'El abono debe de ser menor o igual al saldo'});");
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
  }
}
