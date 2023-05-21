package mx.org.kaana.kalan.catalogos.pacientes.reglas;

import java.sql.Timestamp;
import java.util.Calendar;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kalan.catalogos.pacientes.beans.Clinico;
import mx.org.kaana.libs.pagina.JsfBase;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private Clinico paciente;	
	private String messageError;

	public Transaccion(Clinico paciente) {
		this.paciente= paciente;		
	} // Transaccion

	public String getMessageError() {
		return messageError;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar= false;
		try {
			this.messageError= "Ocurrio un error al ".concat(accion.name().toLowerCase()).concat(" el registro el historial");
      this.paciente.setIdUsuario(JsfBase.getIdUsuario());
      this.paciente.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			switch(accion){
				case AGREGAR:
					regresar= DaoFactory.getInstance().insert(sesion, this.paciente)>= 1L;
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.paciente)>= 1L;
					break;				
				case ELIMINAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.paciente)>= 1L;
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {			
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
		} // catch		
		return regresar;
	}	// ejecutar
	
}