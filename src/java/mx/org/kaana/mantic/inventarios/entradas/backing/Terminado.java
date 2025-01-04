package mx.org.kaana.mantic.inventarios.entradas.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEntrada;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticInventariosEntradasTerminado")
@ViewScoped
public class Terminado extends Accion implements IBaseStorage, Serializable {

  private static final long serialVersionUID= 327323488565639367L;
  
	@PostConstruct
  @Override
  protected void init() {		
    try {
      super.init();
			this.attrs.put("idTipoComparacion", 1);
      this.attrs.put("idArticuloTipo", 1L);      
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  @Override
  public void doUpdateAlmacenes() {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
  	  params.put("sucursales", ((NotaEntrada)this.getAdminOrden().getOrden()).getIkEmpresa().getKey());
 			List<UISelectEntity> almacenes= UIEntity.build("TcManticAlmacenesDto", "almacenPrincipal", params, columns);
      this.attrs.put("almacenes", almacenes);
			if(!almacenes.isEmpty() && this.accion.equals(EAccion.AGREGAR)) 
				if(((NotaEntrada)this.getAdminOrden().getOrden()).getIdAlmacen()== null)
				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(0));
				else {
					int index= almacenes.indexOf(new UISelectEntity(((NotaEntrada)this.getAdminOrden().getOrden()).getIdAlmacen()));
					if(index>= 0)
					  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(index));
					else
  				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(0));
				} // else	
      this.doChangeAlmacen();
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
  }

  @Override
  public void doPrepare() {
    Map<String, Object> params= new HashMap<>();
    try {      
      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }    

  @Override
  public String doAceptar() {
    String regresar= null;
    try {			
      ((NotaEntrada)this.getAdminOrden().getOrden()).setIdNotaTipo(6L);
      regresar= super.doAceptar();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  }

  
}