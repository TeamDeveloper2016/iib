package mx.org.kaana.jobs;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 08-AGO-2025
 *@time 10:44:42
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

import java.io.Serializable;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.kalan.ahorros.reglas.Transaccion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Cuotas implements Job, Serializable {

	private static final Log LOG              = LogFactory.getLog(Cuotas.class);
	private static final long serialVersionUID= 7960714038594054561L;

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
    Transaccion transaccion= null;
		try {
			if(!Configuracion.getInstance().isEtapaDesarrollo() && !Configuracion.getInstance().isEtapaCapacitacion()) {
        LOG.error("VERIFICANDO CUOTAS A APLICAR");
        transaccion= new Transaccion();     
        transaccion.ejecutar(EAccion.REGISTRAR);
        LOG.error("SE ACTUALIZARON: "+ transaccion.getMessageError()+ " CUOTAS");
			} // if
	  } // try
		catch (Exception e) {
			Error.mensaje(e);
      LOG.error("Ocurrio un error al verificar las cuotas");
		} // catch	
    finally {
      transaccion= null;
    }
	} // execute

  public static void main(String ... args) throws JobExecutionException {
    Cuotas cuotas= new Cuotas();
    cuotas.execute(null);
  }
  
}

