package mx.org.kaana.kalan.catalogos.subclasificaciones.reglas;

import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kalan.db.dto.TcKalanGastosSubclasificacionesDto;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private TcKalanGastosSubclasificacionesDto subclasificacion;	
	private String messageError;

	public Transaccion(TcKalanGastosSubclasificacionesDto subclasificacion) {
		this.subclasificacion= subclasificacion;		
	}

	public String getMessageError() {
		return messageError;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar= false;
		try {
			this.messageError= "Ocurrio un error al ".concat(accion.name().toLowerCase()).concat(" la sub clasificador");
			switch(accion){
				case AGREGAR:
					regresar= DaoFactory.getInstance().insert(sesion, this.subclasificacion)>= 1L;
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.subclasificacion)>= 1L;
					break;				
				case ELIMINAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.subclasificacion)>= 1L;
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