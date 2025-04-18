package mx.org.kaana.kalan.catalogos.conceptos.reglas;

import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kalan.db.dto.TcKalanTiposConceptosDto;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private TcKalanTiposConceptosDto concepto;	
	private String messageError;

	public Transaccion(TcKalanTiposConceptosDto concepto) {
		this.concepto= concepto;		
	}

	public String getMessageError() {
		return messageError;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar= false;
		try {
			this.messageError= "Ocurrio un error al ".concat(accion.name().toLowerCase()).concat(" el registro del concepto");
			switch(accion){
				case AGREGAR:
					regresar= DaoFactory.getInstance().insert(sesion, this.concepto)>= 1L;
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.concepto)>= 1L;
					break;				
				case ELIMINAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.concepto)>= 1L;
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try // try
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