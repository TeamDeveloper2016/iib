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
import mx.org.kaana.mantic.enums.ETipoMediosPago;
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
			if(Objects.equals(JsfBase.getFlashAttribute("idPrestamo"), null))
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion   = EAccion.COMPLEMENTAR;
      this.idPrestamo= Objects.equals(JsfBase.getFlashAttribute("idPrestamo"), null)? -1L: (Long)JsfBase.getFlashAttribute("idPrestamo");
      this.attrs.put("retorno", Objects.equals(JsfBase.getFlashAttribute("retorno"), null)? "/Paginas/Kalan/Prestamos/filtro": JsfBase.getFlashAttribute("retorno"));
      this.toLoadAfectaciones();
      this.toLoadSucursales();
      this.toLoadTiposMediosPagos();
      this.toLoadBancos();
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
      this.prestamo= (Prestamo)DaoFactory.getInstance().toEntity(Prestamo.class, "TcKalanPrestamosDto", "empleado", params);
      this.prestamo.setIkEmpresaPersona(new UISelectEntity(this.prestamo.getIdEmpresaPersona()));
      this.attrs.put("total", Numero.redondear(this.prestamo.getImporte()- this.prestamo.getSaldo()));
      switch (this.accion) {
        case COMPLEMENTAR:
          this.afectacion= new Afectacion();
          this.afectacion.setIdPrestamo(this.idPrestamo);
          this.afectacion.setIkTipoAfectacion(new UISelectEntity(2L));
          if(!Objects.equals(this.attrs.get("tiposMediosPagos"), null))
            this.afectacion.setIkTipoMedioPago(UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("tiposMediosPagos")));
          if(!Objects.equals(this.attrs.get("bancos"), null))
            this.afectacion.setIkBanco(UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("bancos")));
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
      if(Objects.equals(this.afectacion.getIdTipoMedioPago(), ETipoMediosPago.EFECTIVO.getIdTipoMedioPago())) {
        this.afectacion.setIdBanco(null);
        this.afectacion.setReferencia(null);
      } // if  
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
        UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:importe', {validaciones: 'requerido|flotante|mayor({\"cuanto\":0})|menor-a({\"cual\": \"contenedorGrupos\\\\\\\\:saldo\"})', mascara: 'libre', mensaje: 'El importe debe de ser menor o igual al saldo'});");
      else 
        UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:importe', {validaciones: 'requerido|flotante|mayor({\"cuanto\":0})|menor-a({\"cual\": \"contenedorGrupos\\\\\\\\:total\"})', mascara: 'libre', mensaje: 'El importe debe de ser menor o igual a lo abonado'});");
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
  }
  
  private void toLoadSucursales() {
		List<UISelectEntity> sucursales= null;
		Map<String, Object>params      = new HashMap<>();
		List<Columna> columns          = new ArrayList<>();
		try {
			if(JsfBase.isAdminEncuestaOrAdmin())
  			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      else
  			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			sucursales=(List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns);
			this.attrs.put("sucursales", sucursales);
			this.attrs.put("idEmpresa", UIBackingUtilities.toFirstKeySelectEntity(sucursales));
      this.doLoadCajas();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} 
	
	public void doLoadCajas() {
		List<UISelectEntity> cajas= null;
		Map<String, Object>params = new HashMap<>();
		List<Columna> columns     = new ArrayList<>();
		try {
			params.put("idEmpresa", this.attrs.get("idEmpresa"));
			columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			cajas=(List<UISelectEntity>) UIEntity.build("TcManticCajasDto", "cajas", params, columns);
			this.attrs.put("cajas", cajas);
			this.attrs.put("caja", UIBackingUtilities.toFirstKeySelectEntity(cajas));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch	
	}
	
	private void toLoadTiposMediosPagos() {
		List<UISelectEntity> tiposMediosPagos= null;
		Map<String, Object>params            = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, "id_cobro_caja= 1");
			tiposMediosPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposMediosPagos", tiposMediosPagos);
			this.attrs.put("ikTipoMedioPago", UIBackingUtilities.toFirstKeySelectEntity(tiposMediosPagos));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} 
  
	private void toLoadBancos() {
		List<UISelectEntity> bancos= null;
		Map<String, Object> params = new HashMap<>();
		List<Columna> columns      = new ArrayList<>();
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			bancos= UIEntity.build("TcManticBancosDto", "row", params, columns, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("bancos", bancos);
			this.attrs.put("ikBanco", UIBackingUtilities.toFirstKeySelectEntity(bancos));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} 
  
}
