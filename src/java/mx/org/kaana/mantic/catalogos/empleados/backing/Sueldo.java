package mx.org.kaana.mantic.catalogos.empleados.backing;

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
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.comun.Catalogos;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.empleados.beans.Empleado;
import mx.org.kaana.mantic.catalogos.personas.reglas.Transaccion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "manticCatalogosEmpleadosSueldo") 
@ViewScoped
public class Sueldo extends Filtro implements Serializable {

  private static final long serialVersionUID = 8793667741599428876L;
  private static final Log LOG = LogFactory.getLog(Sueldo.class);
  
  private List<Empleado> chalanes;

  public List<Empleado> getChalanes() {
    return chalanes;
  }

  public void setChalanes(List<Empleado> chalanes) {
    this.chalanes = chalanes;
  }

  @PostConstruct
  @Override
  protected void init() {		
    try {			
      this.attrs.put("idActivo", 1L);			
      this.attrs.put("importe", 0D);
      super.init();
      this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	@Override
  public void doLoad() {
		Map<String, Object>params= null;
    try {
			params= this.toPrepare();
      this.chalanes= (List<Empleado>)DaoFactory.getInstance().toEntitySet(Empleado.class, "VistaPersonasDto", "chalanes", params);
      this.doMakeNothing();
      UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally		
  } // doLoad
  
  @Override
	protected void toLoadEmpresas() {
		Map<String, Object>params= null;
		List<Columna> columns    = null;
		try {
			params= new HashMap<>();
			columns= new ArrayList<>();			
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns));
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch				
	} // toLoadEmpresas
  
  @Override
	protected void toLoadContratistas() {
		List<UISelectEntity>contratistas= null;		
		try {
			contratistas= Catalogos.toContratistasPorElDia(Boolean.FALSE);
			this.attrs.put("contratistas", contratistas);
			this.attrs.put("idContratista", UIBackingUtilities.toFirstKeySelectEntity(contratistas));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // toLoadContratistas

  public void toUpdateSueldo() {
    try {
      double sueldo= 0D;
      if(this.chalanes!= null && !this.chalanes.isEmpty())
        for (Empleado item: this.chalanes) {
          item.setSueldo((Double)this.attrs.get("importe"));
          sueldo+= item.getSueldo();
        } // for
      this.attrs.put("totalSueldo", Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, sueldo));
      this.attrs.put("importe", 0D);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
  }  

  public void toUpdateSobre() {
    try {
      double sobre = 0D;
      if(this.chalanes!= null && !this.chalanes.isEmpty())
        for (Empleado item: this.chalanes) {
          item.setSobre((Double)this.attrs.get("importe"));
          sobre+= item.getSobre();
        } // for
      this.attrs.put("totalSobre", Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, sobre));
      this.attrs.put("importe", 0D);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
  }  

  public void doUpdateComunSueldo() {
    if(Objects.equals((String)this.attrs.get("nombre"), "sueldo"))
      this.toUpdateSueldo();
    else
      this.toUpdateSobre();
  }

  public void doMakeNothing() {
    if(this.chalanes!= null && !this.chalanes.isEmpty()) {
      double sueldo= 0D;
      double sobre = 0D;
      for (Empleado item: this.chalanes) {
        item.setLimpiar(Objects.equals(item.getIdLimpiar(), 1L));
        sueldo+= item.getSueldo();
        sobre+= item.getSobre();
      } // for
      this.attrs.put("totalSueldo", Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, sueldo));
      this.attrs.put("totalSobre", Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, sobre));
    } // if
    else {
      this.attrs.put("totalSueldo", 0D);
      this.attrs.put("totalSobre", 0D);
    } // if
  }

  public void doGlobalImporte(Double importe) {
    this.attrs.put("importe", importe);    
  }

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    try {					
			transaccion = new Transaccion(this.chalanes);
			if(transaccion.ejecutar(EAccion.MOVIMIENTOS)) {
				JsfBase.addMessage("Se actualizó la información", ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al actualizar la información", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion
  
}
