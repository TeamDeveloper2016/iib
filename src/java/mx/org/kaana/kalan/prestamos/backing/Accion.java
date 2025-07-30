package mx.org.kaana.kalan.prestamos.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kalan.prestamos.beans.Prestamo;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.kalan.prestamos.reglas.Transaccion;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "kalanPrestamosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565631311L;
  private static final Log LOG = LogFactory.getLog(Accion.class);
  
  private EAccion accion;
  private Prestamo prestamo;
  private Long idPrestamo;

  public Prestamo getPrestamo() {
    return prestamo;
  }

  public void setPrestamo(Prestamo prestamo) {
    this.prestamo = prestamo;
  }
  
  public Boolean getAplicar() {
    return Objects.equals(this.accion, EAccion.AGREGAR) || Objects.equals(this.accion, EAccion.MODIFICAR);
  }
  
  @PostConstruct
  @Override
  public void init() {
    try {
			if(Objects.equals(JsfBase.getFlashAttribute("accion"), null))
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion    = Objects.equals(JsfBase.getFlashAttribute("accion"), null)? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.idPrestamo= Objects.equals(JsfBase.getFlashAttribute("idPrestamo"), null)? -1L: (Long)JsfBase.getFlashAttribute("idPrestamo");
      this.attrs.put("retorno", Objects.equals(JsfBase.getFlashAttribute("retorno"), null)? "/Paginas/Kalan/Prestamos/filtro": JsfBase.getFlashAttribute("retorno"));
      this.doLoad(); 
      this.doUpdateSaldo();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
	
  public void doLoad() {
    Map<String, Object> params= new HashMap<>();
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      params.put(Constantes.SQL_CONDICION, "id_prestamo= "+ this.idPrestamo);      
      switch (this.accion) {
        case AGREGAR:
          this.prestamo= new Prestamo();
          break;
        case MODIFICAR:
        case CONSULTAR:
          this.prestamo= (Prestamo)DaoFactory.getInstance().toEntity(Prestamo.class, "TcKalanPrestamosDto", params);
          this.prestamo.setIkEmpresaPersona(new UISelectEntity(this.toLoadEmpleados(this.prestamo.getIdEmpresaPersona())));
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

	public String doAplicar() {
    String regresar = null;
    EAccion temporal= this.accion;
    try {      
      this.accion= EAccion.PROCESAR;
      regresar= this.doAceptar();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      this.accion= temporal;
    } // finally
    return regresar;
  }

	public String doAceptar() {
    Transaccion transaccion= null;
    String regresar        = null;
    try {
      transaccion= new Transaccion(this.prestamo);
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
  
	public List<UISelectEntity> doCompleteEmpleado(String codigo) {
 		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
		boolean buscaPorCodigo    = false;
    try {
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("curp", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(!Cadena.isVacio(codigo)) {
  			codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim();
				codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*");
			} // if	
			else
				codigo= "WXYZ";
  		params.put("codigo", codigo);
			if(buscaPorCodigo)
        this.attrs.put("empleados", UIEntity.build("VistaPrestamosDto", "porCodigo", params, columns, 40L));
			else
        this.attrs.put("empleados", UIEntity.build("VistaPrestamosDto", "porNombre", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("empleados");
	}	
  
	private Entity toLoadEmpleados(Long idEmpresaPersona) {
    Entity regresar           = null;
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("curp", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
 			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      if(Objects.equals(this.accion, EAccion.AGREGAR))
   			params.put(Constantes.SQL_CONDICION, "tr_mantic_empresa_personal.id_empresa_persona= "+ idEmpresaPersona+ " and tr_mantic_empresa_personal.id_activo= 1");
      else    
   			params.put(Constantes.SQL_CONDICION, "tr_mantic_empresa_personal.id_empresa_persona= "+ idEmpresaPersona);
  		regresar= (Entity)DaoFactory.getInstance().toEntity("VistaPrestamosDto", "empleados", params);
      if(Objects.equals(regresar, null))
        regresar= new Entity(-1L);
      else
        UIBackingUtilities.toFormatEntity(regresar, columns);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
    return regresar;
	}

  public void doUpdateLimite() {
    try {      
      Calendar calendar= Calendar.getInstance();
      calendar.add(Calendar.MONTH, this.prestamo.getPlazo().intValue());
      this.prestamo.getLimite().setTime(calendar.getTimeInMillis());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

  public void doUpdateSaldo() {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idEmpresaPersona", this.prestamo.getIkEmpresaPersona().getKey());      
      Entity saldo= (Entity)DaoFactory.getInstance().toEntity("VistaPrestamosDto", "disponible", params);
      if(!Objects.equals(saldo, null) && !saldo.isEmpty()) 
        this.attrs.put("disponible", saldo.toDouble("disponible"));
      else 
        this.attrs.put("disponible", 0D);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
}
