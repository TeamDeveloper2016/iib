package mx.org.kaana.kalan.gastos.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kalan.gastos.beans.Cheque;
import mx.org.kaana.kalan.gastos.beans.Gasto;
import mx.org.kaana.kalan.gastos.beans.Parcialidad;
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

public class AdminGasto implements Serializable {

  private static final long serialVersionUID = 2117382011559360170L;
  
  private Long idEmpresaGasto;
  
  public AdminGasto() {
    this(-1L);
  }

  public AdminGasto(Long idEmpresaGasto) {
    this.idEmpresaGasto= idEmpresaGasto;
  }

  public Gasto getGasto() {
    Gasto regresar            = null; 
    Entity proveedor          = null;
    Map<String, Object> params= new HashMap<>();
    try {    
      if(Objects.equals(this.idEmpresaGasto, -1L))
        regresar= new Gasto();
      else {
        params.put("idEmpresaGasto", this.idEmpresaGasto);      
        regresar = (Gasto)DaoFactory.getInstance().toEntity(Gasto.class, "TcKalanEmpresasGastosDto", "igual", params);
        if(regresar!= null) {
          regresar.setIkTipoMedioPago(new UISelectEntity(regresar.getIdTipoMedioPago()));
          params.put("idProveedor", regresar.getIdProveedor());
          proveedor= (Entity)DaoFactory.getInstance().toEntity("TcManticProveedoresDto", "igual", params);
          if(Objects.equals(proveedor, null))
            regresar.setIkProveedor(new UISelectEntity(-1L));
          else
            regresar.setIkProveedor(new UISelectEntity(proveedor));
          if(!Objects.equals(regresar.getIdTipoMedioPago(), 1L) || Objects.equals(regresar.getIdActivoCheque(), 1L)) {
            Cheque documento= (Cheque)DaoFactory.getInstance().toEntity(Cheque.class, "TcKalanEmpresasChequesDto", "cheques", params);
            if(documento!= null) {
              if(Objects.equals(documento.getIdActivoProveedor(), 1L)) {
                params.put("idProveedor", documento.getIdProveedor());
                proveedor= (Entity)DaoFactory.getInstance().toEntity("TcManticProveedoresDto", "igual", params);
                documento.setIkProveedor(new UISelectEntity(proveedor));
              } // if  
              else
                documento.setIkProveedor(new UISelectEntity(-1L));
              regresar.setDocumento(documento);
            } // if  
          } // if  
          if(Objects.equals(regresar.getIdActivoProrratear(), 1L)) {
            List<Parcialidad> parcialidades= (List<Parcialidad>)DaoFactory.getInstance().toEntitySet(Parcialidad.class, "TcKalanEmpresasGastosDto", "iguales", params);
            if(parcialidades!= null) {
              parcialidades.forEach((item) -> {
                item.setSql(ESql.SELECT);
              }); // for
              regresar.setParcialidades(parcialidades);
            } // if  
          } // if
        } // if  
        else {
          regresar= new Gasto();
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
  
  public Gasto toGasto() {
    Gasto regresar            = null; 
    Map<String, Object> params= new HashMap<>();
    try {    
      if(Objects.equals(this.idEmpresaGasto, -1L))
        regresar= new Gasto();
      else {
        params.put("idEmpresaGasto", this.idEmpresaGasto);      
        regresar = (Gasto)DaoFactory.getInstance().toEntity(Gasto.class, "TcKalanEmpresasGastosDto", "igual", params);
        if(regresar== null) {
          regresar= new Gasto();
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
