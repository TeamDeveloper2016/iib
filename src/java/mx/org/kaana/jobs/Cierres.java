package mx.org.kaana.jobs;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 17-mayo-2022
 *@time 9:11:42
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.jobs.comun.IBaseJob;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticCierresCajasDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Cierres extends IBaseJob implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Cierres.class);
	private static final long serialVersionUID= 7160794038594054563L;

	@Override
	public void procesar(JobExecutionContext jec) throws JobExecutionException {
		Map<String, Object> params= new HashMap<>();
		try {
			if(Configuracion.getInstance().isEtapaDesarrollo()|| Configuracion.getInstance().isEtapaProduccion()) {
        LOG.error("------------------------ENTRO A CHECAR LOS CIERRES DE CAJA---------------------");
        params.put(Constantes.SQL_CONDICION, "id_cierre_estatus in (1, 2)");
        List<Entity> cierres= (List<Entity>)DaoFactory.getInstance().toEntitySet("TcManticCierresDto", params, 500L);
        if(!Objects.equals(cierres, null) && !cierres.isEmpty())
          for (Entity item: cierres) {
            this.toCheckImportes(item);
          } // for
        LOG.error("-------------------------------------------------------------------------------");
      } // if
	  } // try
		catch (Exception e) {
			Error.mensaje(e);
      LOG.error("Ocurrio un error al realizar la verificación de cierre de caja");
		} // catch	
		finally {
			Methods.clean(params);
		} // finally
	} // execute
  
  private void toCheckImportes(Entity corte) throws Exception {
		Map<String, Object> params= new HashMap<>();
		try {
      params.put("idCierre", corte.toLong("idCierre"));
      List<Entity> tipos= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaCierresCajasDto", "calculos", params, 500L);
      if(!Objects.equals(tipos, null) && !tipos.isEmpty()) {
        for (Entity tipo: tipos) {
          params.put("idTipoMedioPago", tipo.toLong("idTipoMedioPago"));
          Entity cierre= (Entity)DaoFactory.getInstance().toEntity("TcManticCierresCajasDto", params);
          if(!Objects.equals(cierre, null) && !cierre.isEmpty()) {
            if(!Objects.equals(cierre.toDouble("saldo"), tipo.toDouble("importe"))) {
              LOG.error("CAJA ERRONEA: "+ cierre.toLong("idCierreCaja")+ " | "+ tipo.toLong("idTipoMedioPago")+ " | CIERRE ["+ cierre.toDouble("saldo")+ "] VENTAS ["+ tipo.toDouble("importe")+ "] DIFERENCIA ["+ (tipo.toDouble("importe")- cierre.toDouble("saldo"))+ "]");
              params.put("idCierreCaja", cierre.toLong("idCierreCaja"));
              params.put("importe", tipo.toDouble("importe"));
              DaoFactory.getInstance().updateAll(TcManticCierresCajasDto.class, params, "cierre");
            } // if
          } // if
        } // for
      } // if
	  } // try
		catch (Exception e) {
			throw e;
		} // catch	
		finally {
			Methods.clean(params);
		} // finally
  }

  public static void main(String ... args) throws JobExecutionException {
    Cierres cierre= new Cierres();
    cierre.procesar(null);
  }
  
}

