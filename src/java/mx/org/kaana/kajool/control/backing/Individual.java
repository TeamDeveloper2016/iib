package mx.org.kaana.kajool.control.backing;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Cifrar;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.mantic.productos.beans.Caracteristica;
import mx.org.kaana.mantic.productos.beans.Partida;
import mx.org.kaana.mantic.productos.beans.Producto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 21/08/2015
 * @time 12:27:03 PM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */
@ViewScoped
@Named(value = "kajoolControlIndividual")
public class Individual extends BaseMenu implements Serializable {

  private static final Log LOG = LogFactory.getLog(Individual.class);
  private static final long serialVersionUID = 5323749709626263838L;
  
  private String pathImage;
  private Producto producto;
  
	public String getPathImage() {
		return pathImage;
	}

	public String getBrand() {
    return this.pathImage.concat("1/marcas/");
  }
  
  public Producto getProducto() {
    return producto;
  }
  
  public Boolean getIsEmpty() {
    return (Long)this.attrs.get("idProducto")== -1L;  
  }
  
  @Override
  @PostConstruct
  protected void init() {
    super.init();
    try {
      String idProducto= JsfBase.getParametro("zAiOx");
      if(!Cadena.isVacio(idProducto)) {
        String codigo= Cifrar.descifrar(idProducto);
        idProducto= String.valueOf(Numero.getLong(codigo, -1L));
      } // if  
      else
        idProducto= String.valueOf(JsfBase.getFlashAttribute("idProducto")== null? "-1": (Long)JsfBase.getFlashAttribute("idProducto"));
      this.attrs.put("idProducto", idProducto== null? -1L: Numero.getLong(idProducto, -1L));
      String dns= Configuracion.getInstance().getPropiedadServidor("sistema.dns");
      this.pathImage= dns.substring(0, dns.lastIndexOf("/")+ 1).concat(Configuracion.getInstance().getEtapaServidor().name().toLowerCase()).concat("/galeria/");
      if(!Cadena.isVacio(this.attrs.get("idProducto")))
        this.doLoadArticulo();
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
    } // catch   
  }

  @Override
  public void doLoad() {
  }
  
  public void doLoadArticulo() {
    try {
      this.producto= new Producto((Long)this.attrs.get("idProducto"), "menudeo");
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
  }
 
  public String doLetraCapital(Caracteristica caracteristica) {
    return Cadena.letraCapital(caracteristica.getDescripcion());
  }  
  
  public String toColorPartida(Partida partida) {
    return ""; 
  }
  
}