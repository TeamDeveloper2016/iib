package mx.org.kaana.kalan.movimientos.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kalan.gastos.beans.Gasto;
import mx.org.kaana.kalan.movimientos.beans.Movimiento;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 2/09/2023
 *@time 05:36:29 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class AdminMovimiento implements Serializable {

  private static final long serialVersionUID = 2117382011559360170L;
  
  private Long idEmpresaMovimiento;
  
  public AdminMovimiento() {
    this(-1L);
  }

  public AdminMovimiento(Long idEmpresaMovimiento) {
    this.idEmpresaMovimiento= idEmpresaMovimiento;
  }

  public Movimiento getMovimiento() {
    Movimiento regresar       = null; 
    Entity cliente            = null;
    Map<String, Object> params= new HashMap<>();
    try {    
      if(Objects.equals(this.idEmpresaMovimiento, -1L))
        regresar= new Movimiento();
      else {
        params.put("idEmpresaMovimiento", this.idEmpresaMovimiento);      
        regresar = (Movimiento)DaoFactory.getInstance().toEntity(Gasto.class, "TcKalanEmpresasMovimientosDto", "igual", params);
        if(regresar!= null) {
          if(Objects.equals(regresar.getIdAnticipo(), 1L)) {
            params.put("idCliente", regresar.getIdCliente());
            cliente= (Entity)DaoFactory.getInstance().toEntity("TcManticClientesDto", "igual", params);
            regresar.setIkCliente(new UISelectEntity(cliente));
          } // if  
        } // if  
        else {
          regresar= new Movimiento();
          throw new RuntimeException("El concepto no esta registrado en la base de datos !");
        } // if 
      } // else  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }

  public Movimiento toMovimiento() {
    Movimiento regresar       = null; 
    Map<String, Object> params= new HashMap<>();
    try {    
      if(Objects.equals(this.idEmpresaMovimiento, -1L))
        regresar= new Movimiento();
      else {
        params.put("idEmpresaMovimiento", this.idEmpresaMovimiento);      
        regresar = (Movimiento)DaoFactory.getInstance().toEntity(Gasto.class, "TcKalanEmpresasMovimientosDto", "igual", params);
        if(regresar== null) {
          regresar= new Movimiento();
          throw new RuntimeException("El gasto no esta registrado en la base de datos !");
        } // if
      } // else  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
  
}
