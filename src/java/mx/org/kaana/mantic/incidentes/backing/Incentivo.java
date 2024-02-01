package mx.org.kaana.mantic.incidentes.backing;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.incidentes.reglas.Transaccion;
import mx.org.kaana.mantic.incidentes.beans.Repercusion;
import java.time.LocalDate;
import java.util.Calendar;
import mx.org.kaana.mantic.incidentes.beans.Dia;
import mx.org.kaana.mantic.incidentes.reglas.Semana;

@Named(value = "manticIncidentesIncentivo")
@ViewScoped
public class Incentivo extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;
  private Repercusion repercusion;
	private List<Repercusion> incidentes;
  private Semana dias;

  public Repercusion getRepercusion() {
    return repercusion;
  }

  public void setRepercusion(Repercusion repercusion) {
    this.repercusion = repercusion;
  }

  public List<Repercusion> getIncidentes() {
    return incidentes;
  }

  public void setIncidentes(List<Repercusion> incidentes) {
    this.incidentes = incidentes;
  }

  public Semana getDias() {
    return dias;
  }

  public void setDias(Semana dias) {
    this.dias = dias;
  }
  
  @PostConstruct
  @Override
  protected void init() {
    try {    	
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "/Paginas/Mantic/Incidentes/filtro": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("automatico", Boolean.TRUE);
      this.toDefaultPersona();
      this.incidentes= new ArrayList<>();
			this.toLoadTiposIncidentes();
      this.toLoadSemana();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void toLoadTiposIncidentes() {
		List<UISelectItem> tipos = null;
		Map<String, Object>params= new HashMap<>();
		try {
			params.put("grupo", 3L);
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			tipos= UISelect.build("TcManticTiposIncidentesDto", "byGrupo", params, "nombre", " ", EFormatoDinamicos.MAYUSCULAS);
			this.attrs.put("tipos", tipos);
      if(tipos!= null && tipos.size()> 0) 
        this.toChangeTipo(Boolean.TRUE);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toLoadTiposInicidentes
  
	public List<UISelectEntity> doCompleteNombreEmpleado(String query) {
		this.attrs.put("nombreEmpleado", query);
    this.doUpdateNombresEmpleados();		
		return (List<UISelectEntity>)this.attrs.get("nombres");
	}	// doCompleteNombreEmpleado
	
	public void doUpdateNombresEmpleados() {
		List<Columna> columns        = new ArrayList<>();
    Map<String, Object> params   = new HashMap<>();
		List<UISelectEntity> personas= null;		
    try {
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));			
			String nombreEmpleado= (String)this.attrs.get("nombreEmpleado");
      if(Cadena.isVacio(nombreEmpleado))
        nombreEmpleado= "WXYZ";
      else 
  			nombreEmpleado= nombreEmpleado.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*");		
			if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
				params.put("idEmpresa", this.attrs.get("idEmpresa"));
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales()); 
			params.put("nombreEmpleado", nombreEmpleado);	
      personas= (List<UISelectEntity>) UIEntity.build("VistaPersonasDto", "autoCompletar", params, columns, 20L);
      this.attrs.put("nombres", personas);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}	// doUpdateNombresEmpleados  
	
	public String doAceptar(){
		String regresar        = null;
		Transaccion transaccion= null;
		try {
			if(this.incidentes.size()> 0) {
				transaccion= new Transaccion(this.incidentes);				
				if(transaccion.ejecutar(EAccion.REPROCESAR)) {
					regresar= this.doCancelar();
				} // if
				else
					JsfBase.addMessage("Ocurrió un error al registrar las incidencias", ETipoMensaje.ERROR);
			} // if
			else
				JsfBase.addMessage("Se tiene que agregar una incidencia", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
		return regresar;
	} // doAceptar
	
	public String doCancelar() {
		return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
	} // doCancelar

  private void toDefaultPersona() {
    try {
      if(this.repercusion== null) {
        this.repercusion= new Repercusion(-1L);
        this.toChangeTipo(Boolean.TRUE);
        UISelectEntity persona= new UISelectEntity(-1L);
        persona.put("idEmpresaPesona", new Value("idEmpresaPersona", -1L));
        this.attrs.put("nombre", persona);
      } // if  
      else
        this.repercusion= this.repercusion.clone();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
  }
  
  public void doAddDia() {
    try {
      if(!Objects.equals(this.repercusion.getNombre(), null) && this.repercusion.getNombre().length()> 0) {
        int index= this.incidentes.indexOf(this.repercusion);
        if(index< 0) {
          // AQUI FALTA VALIDAR SI TIENE UNA INCIDENCIA YA REGISTRADA DE ALGUN TIPO PARA ESTA PERSONA
          if(this.toCheckLocal() && this.toCheckIncidencia())
            this.incidentes.add(this.repercusion);   
        } // if  
        else 
          JsfBase.addMessage("El empleado ya tiene una ["+ this.repercusion.getIncidencia()+ "] el día ["+ this.repercusion.getIniciox()+ "]", ETipoMensaje.ERROR);
        this.toDefaultPersona();
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doAddSemana() {
    try {
      if(!Objects.equals(this.repercusion.getNombre(), null) && this.repercusion.getNombre().length()> 0) {
        for (Dia dia: this.dias) {
          if(((Objects.equals(this.repercusion.getIdTipoIncidente(), 15L) || Objects.equals(this.repercusion.getIdTipoIncidente(), 17L) || Objects.equals(this.repercusion.getIdTipoIncidente(), 24L)) && dia.getActivo())
         || (!(Objects.equals(this.repercusion.getIdTipoIncidente(), 15L) || Objects.equals(this.repercusion.getIdTipoIncidente(), 17L) || Objects.equals(this.repercusion.getIdTipoIncidente(), 24L)) && dia.getCosto()> 0D)) {
            this.repercusion.setInicio(dia.getFecha());
            this.repercusion.setTermino(dia.getFecha());
            if(!(Objects.equals(this.repercusion.getIdTipoIncidente(), 15L) || Objects.equals(this.repercusion.getIdTipoIncidente(), 17L) || Objects.equals(this.repercusion.getIdTipoIncidente(), 24L)))
              this.repercusion.setCosto(dia.getCosto());
            int index= this.incidentes.indexOf(this.repercusion);
            if(index< 0) {
              if(this.toCheckLocal() && this.toCheckIncidencia())
                this.incidentes.add(this.repercusion);   
            } // if  
            else 
              JsfBase.addMessage("El empleado ya tiene una ["+ this.repercusion.getIncidencia()+ "] el día ["+ this.repercusion.getIniciox()+ "]", ETipoMensaje.ERROR);
            this.toDefaultPersona();
          } // if  
        } // for
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }  
  
  public void doDelete(Repercusion row) {
    this.incidentes.remove(row);
  }
  
  public void doChangeInicio() {
    if(this.repercusion.getInicio()!= null)
      this.repercusion.setTermino(new Date(this.repercusion.getInicio().getTime()));
  }
  
  public void doLoadNombres() {
    UISelectEntity persona= (UISelectEntity)this.attrs.get("nombre");  
    if(persona!= null) {
      List<UISelectEntity> personas= (List<UISelectEntity>)this.attrs.get("nombres");
      if(personas!= null && !persona.isEmpty()) {
        int index= personas.indexOf(persona);
        if(index>= 0) {
          persona= personas.get(index);
          this.repercusion.setIdEmpresaPersona(persona.toLong("idEmpresaPersona"));
          this.repercusion.setRfc(persona.toString("rfc"));
          this.repercusion.setNombre(persona.toString("nombreCompleto"));
          this.repercusion.setPuesto(persona.toString("puesto"));
          this.repercusion.setContacto(persona.toString("contacto"));
          if((Boolean)this.attrs.get("automatico"))
            this.doAddDia();
        } // if  
      } // if
    } // if
  }
 
  public void doLoadEmpleado() {
    UISelectEntity persona= (UISelectEntity)this.attrs.get("nombre");  
    if(persona!= null) {
      List<UISelectEntity> personas= (List<UISelectEntity>)this.attrs.get("nombres");
      if(personas!= null && !persona.isEmpty()) {
        int index= personas.indexOf(persona);
        if(index>= 0) {
          persona= personas.get(index);
          this.repercusion.setIdEmpresaPersona(persona.toLong("idEmpresaPersona"));
          this.repercusion.setRfc(persona.toString("rfc"));
          this.repercusion.setNombre(persona.toString("nombreCompleto"));
          this.repercusion.setPuesto(persona.toString("puesto"));
          this.repercusion.setContacto(persona.toString("contacto"));
        } // if  
      } // if
    } // if
  }
 
  public void doChangeTipo() {
    this.toChangeTipo(Boolean.FALSE);
  }
  
  private void toChangeTipo(Boolean recover) {
		List<UISelectItem> tipos= (List<UISelectItem>)this.attrs.get("tipos");
    if(tipos!= null && !tipos.isEmpty()) {
      if(recover)
        this.repercusion.setIdTipoIncidente((Long)UIBackingUtilities.toFirstKeySelectItem(tipos));
      int index= tipos.indexOf(new UISelectItem(this.repercusion.getIdTipoIncidente()));
      if(index>= 0) {
        UISelectItem tipo= tipos.get(index);
        this.repercusion.setIncidencia(tipo.getLabel());
      } // if  
    } // if  
  }
 
  private Boolean toCheckIncidencia() {
    Boolean regresar          = Boolean.FALSE;
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idEmpresaPersona", this.repercusion.getIdEmpresaPersona());      
      params.put("idTipoInidencia", this.repercusion.getIdTipoIncidente());      
      params.put("inicio", Global.format(EFormatoDinamicos.FECHA_ESTANDAR, this.repercusion.getInicio()));
      params.put("termino", Global.format(EFormatoDinamicos.FECHA_ESTANDAR, this.repercusion.getTermino()));
      columns.add(new Columna("incidencia", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
      Entity entity= (Entity)DaoFactory.getInstance().toEntity("VistaIncidenciasDto", "incentivo", params);
      if(entity!= null) {
        UIBackingUtilities.toFormatEntity(entity, columns);
			  JsfBase.addMessage("El empleado ya tiene una [".concat(entity.toString("incidencia")).concat("] registrada del ").concat(entity.toString("inicio")).concat(" al ").concat(entity.toString("termino")), ETipoMensaje.ERROR);
      } // if  
      else
        regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
    return regresar;
  }
  
  private Boolean toCheckLocal() {
    Boolean regresar= Boolean.TRUE;
    try {
      for (Repercusion item: this.incidentes) {
        if(
            Objects.equals(item.getIdEmpresaPersona(), this.repercusion.getIdEmpresaPersona()) &&
           (((item.getInicio().equals(this.repercusion.getInicio()) || item.getTermino().equals(this.repercusion.getTermino()))
            && item.getInicio().before(this.repercusion.getInicio())
            && item.getTermino().after(this.repercusion.getTermino())))
          ) {
    		  JsfBase.addMessage("El empleado ya tiene una incidencia ".concat(item.getIncidencia()).concat(" registrada del ").concat(item.getIniciox()).concat(" al ").concat(item.getTerminox()), ETipoMensaje.ERROR);
          regresar= Boolean.FALSE;
          break;
        } // if
      } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return regresar;
  }  
 
  private void toLoadSemana() {
    Map<String, Object> params = new HashMap<>();
    try {      
      params.put("idTipoNomina", 1L);      
      Entity entity = (Entity)DaoFactory.getInstance().toEntity("VistaNominaDto", "ultima", params);
      if(entity!= null && !entity.isEmpty())
        this.dias= new Semana(entity.toDate("inicio"));
      else
        this.dias= new Semana(new Date(Calendar.getInstance().getTimeInMillis()));
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