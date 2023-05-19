package mx.org.kaana.jobs;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 18-may-2023
 *@time 12:07:42
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.jobs.comun.IBaseJob;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kalan.db.dto.TcKalanCitasDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.wassenger.Saras;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Notificaciones extends IBaseJob {

	private static final Log LOG              =LogFactory.getLog(Notificaciones.class);
	private static final long serialVersionUID=7501746848602636876L;

	@Override
	public void procesar(JobExecutionContext jec) throws JobExecutionException {
    Saras notificar           = null;
    List<Entity> servicios    = null; 
    Map<String, Object> params= new HashMap<>();
		try {
			LOG.error("ENTRO AL PROCESO DE NOTIFICAR A LOS PACIENTES/CLIENTES");
			if(true || Configuracion.getInstance().isEtapaPruebas() || Configuracion.getInstance().isEtapaProduccion()) {
        notificar= new Saras();
        List<Entity> pacientes= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaExpedientesDto", "recordatorios", params, Constantes.SQL_TODOS_REGISTROS);
        // NOTIFICAR POR WHASTAPP AL CLIENTE
        if(pacientes!= null && !pacientes.isEmpty())
          for (Entity paciente: pacientes) {
            notificar.setNombre(paciente.toString("cliente"));
            notificar.setCelular(paciente.toString("celular"));
            notificar.setFecha(paciente.toTimestamp("inicio"));
            notificar.setEstatus("agendó");
            notificar.setObservaciones(paciente.toString("observaciones"));
            params.put("idCita", paciente.toLong("idCita"));      
            params.put("idCliente", paciente.toLong("idCliente"));            
            servicios= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaClientesCitasDto", "detalle", params);
            if(servicios== null || servicios.isEmpty()) 
              servicios= new ArrayList<>();            
            notificar.setServicios(servicios);
            if(paciente.toLong("horas")>= 1)
              notificar.doSendRecordatorioCliente(paciente.toLong("recordatorio"));
            DaoFactory.getInstance().updateAll(TcKalanCitasDto.class, params, "recordatorio");
          } // for
        pacientes= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaExpedientesDto", "notificaciones", params, Constantes.SQL_TODOS_REGISTROS);
        if(pacientes!= null && !pacientes.isEmpty())
          for (Entity paciente: pacientes) {
            notificar.setNombre(paciente.toString("cliente"));
            notificar.setCelular(paciente.toString("celular"));
            notificar.setFecha(paciente.toTimestamp("inicio"));
            notificar.setEstatus("agendó");
            notificar.setObservaciones(paciente.toString("observaciones"));
            params.put("idCita", paciente.toLong("idCita"));      
            params.put("idCliente", paciente.toLong("idCliente"));            
            servicios= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaClientesCitasDto", "detalle", params);
            if(servicios== null || servicios.isEmpty()) 
              servicios= new ArrayList<>();            
            notificar.setServicios(servicios);
            if(paciente.toLong("horas")>= 1)
              notificar.doSendRecordatorioCliente(paciente.toLong("notificacion"));
            DaoFactory.getInstance().updateAll(TcKalanCitasDto.class, params, "notificacion");
          } // for
      } // if
	  } // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch	
    finally {
      Methods.clean(servicios);
      Methods.clean(params);
    } // finally
	} // execute

  public static void main(String ... args) throws JobExecutionException {
    Notificaciones notificaciones= new Notificaciones();
    notificaciones.execute(null);
  }
  
}

