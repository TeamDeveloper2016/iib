package mx.org.kaana.kalan.catalogos.cuentas.reglas;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kalan.catalogos.cuentas.beans.Cuenta;
import mx.org.kaana.kalan.cuentas.enums.ECuentasOrigenes;
import mx.org.kaana.kalan.cuentas.reglas.IBaseCuenta;
import mx.org.kaana.kalan.db.dto.TcKalanCuentasBitacoraDto;
import mx.org.kaana.kalan.db.dto.TcKalanCuentasMovimientosDto;
import org.hibernate.Session;

public class Transaccion extends IBaseCuenta implements Serializable {

  private static final long serialVersionUID = -2111022211173287699L;

	private Cuenta cuenta;	
	private Boolean aperturar;	
	private String messageError;

	public Transaccion(Cuenta cuenta) {
    this(cuenta, Boolean.FALSE);
  }
  
	public Transaccion(Cuenta cuenta, Boolean aperturar) {
		this.cuenta   = cuenta;	
    this.aperturar= aperturar;
	}

	public String getMessageError() {
		return messageError;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar= false;
		try {
			this.messageError= "Ocurrio un error al ".concat(accion.name().toLowerCase()).concat(" la cuenta");
			switch(accion) {
				case AGREGAR:
          this.cuenta.setIdPrincipal(1L);
          this.cuenta.setEsBanca(1L);
					regresar= DaoFactory.getInstance().insert(sesion, this.cuenta)>= 1L;
          if(this.aperturar)
            this.toControlCuentaCargo(sesion);
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.cuenta)>= 1L;
          if(this.aperturar)
            this.toControlCuentaCargo(sesion);
					break;				
				case ELIMINAR:
          if(this.toCheckCuenta(sesion)) {
					  DaoFactory.getInstance().deleteAll(sesion, TcKalanCuentasBitacoraDto.class, "cuentas", this.cuenta.toMap());
					  DaoFactory.getInstance().deleteAll(sesion, TcKalanCuentasMovimientosDto.class, this.cuenta.toMap());
					  regresar= DaoFactory.getInstance().delete(sesion, this.cuenta)>= 1L;
          } // if
          else 
            throw new Exception("NO SE PUEDE ELIMINAR LA CUENTA, EXISTEN MOVIMIENTOS ASOCIADOS");
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
	
  private void toControlCuentaCargo(Session sesion) throws Exception {
    try {
      super.control(sesion, this.cuenta, ECuentasOrigenes.BANCOS_APERTURA);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }
 
  private Boolean toCheckCuenta(Session sesion) throws Exception {
    Boolean regresar= Boolean.TRUE;
    try {
      Entity row= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcKalanCuentasMovimientosDto", "rows", this.cuenta.toMap());
      if(!Objects.equals(row, null) && !row.isEmpty())
        regresar= row.toLong("movimientos")<= 1L;
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    return regresar;
  }
  
}