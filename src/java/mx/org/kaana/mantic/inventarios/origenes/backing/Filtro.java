package mx.org.kaana.mantic.inventarios.origenes.backing;

import java.io.Serializable;
import java.util.Objects;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 01/06/2024
 *@time 23:19:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value = "manticInventariosOrigenesFiltro")
@ViewScoped
public class Filtro extends mx.org.kaana.mantic.inventarios.entradas.backing.Filtro implements Serializable {

	private static final long serialVersionUID=1368701967796774741L;
  
  @Override
  public String doAccion(String accion) {
		String regresar   = "/Paginas/Mantic/Inventarios/Origenes/accion?zOyOxDwIvGuCt=zLyOxRwMvAuNt";
    EAccion eaccion   = null;
		Long idNotaEntrada= -1L;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			if(this.attrs.get("seleccionado")!= null) 
			  idNotaEntrada= ((Entity)this.attrs.get("seleccionado")).getKey();
      JsfBase.setFlashAttribute("accion", eaccion);		
      JsfBase.setFlashAttribute("idOrdenCompra", -1L);
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Origenes/filtro");		
			JsfBase.setFlashAttribute("idNotaEntrada", Objects.equals(eaccion, EAccion.MODIFICAR) || Objects.equals(eaccion, EAccion.CONSULTAR)? idNotaEntrada: -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR_AMPERSON);
  } // doAccion  
  
}
