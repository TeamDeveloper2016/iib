package mx.org.kaana.mantic.lotes.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.mantic.db.dto.TcManticLotesDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 13/06/2024
 *@time 09:36:51 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Lote extends TcManticLotesDto implements Serializable {

  private static final long serialVersionUID = 1193346911478417193L;
  
  private Long itTipoClase;
  private Long itArticulo;
	private UISelectEntity ikEmpresa;
	private UISelectEntity ikAlmacen;
  private UISelectEntity ikArticulo;
  private UISelectEntity ikTipoClase;
  private List<Partida> partidas;

  public Lote() {
    this(-1L);
  }

  public Lote(Long key) {
    super(key);
    this.setIkEmpresa(new UISelectEntity(-1L));
    this.setIkAlmacen(new UISelectEntity(-1L));
    this.setIkArticulo(new UISelectEntity(-1L));
    this.setIkTipoClase(new UISelectEntity(-1L));
    this.setPartidas(new ArrayList<Partida>());
  }

  public Lote(Double original, Long idLoteTipo, String nombre, Long ejercicio, String consecutivo, Long idUsuario, Long idAlmacen, Long idLote, String observaciones, Long idEmpresa, Double cantidad, Long orden, Long idArticulo, Long idLoteEstatus, Long idTipoClase, Double merma, Double terminado, Double restos) {
    super(original, idLoteTipo, nombre, ejercicio, consecutivo, idUsuario, idAlmacen, idLote, observaciones, idEmpresa, cantidad, orden, idArticulo, idLoteEstatus, idTipoClase, merma, terminado, restos);
    this.setIkEmpresa(new UISelectEntity(-1L));
    this.setIkAlmacen(new UISelectEntity(-1L));
    this.setIkArticulo(new UISelectEntity(-1L));
    this.setIkTipoClase(new UISelectEntity(-1L));
    this.setPartidas(new ArrayList<Partida>());
  }
  
  public UISelectEntity getIkEmpresa() {
    return ikEmpresa;
  }

  public void setIkEmpresa(UISelectEntity ikEmpresa) {
    this.ikEmpresa = ikEmpresa;
		if(this.ikEmpresa!= null)
		  this.setIdEmpresa(this.ikEmpresa.getKey());
  }

	public UISelectEntity getIkAlmacen() {
		return ikAlmacen;
	}

	public void setIkAlmacen(UISelectEntity ikAlmacen) {
		this.ikAlmacen=ikAlmacen;
		if(this.ikAlmacen!= null)
		  this.setIdAlmacen(this.ikAlmacen.getKey());
	}
  
  public UISelectEntity getIkArticulo() {
    return ikArticulo;
  }

  public void setIkArticulo(UISelectEntity ikArticulo) {
    this.ikArticulo = ikArticulo;
    if(!Objects.equals(ikArticulo, null))
      this.setIdArticulo(ikArticulo.getKey());
  }

  public UISelectEntity getIkTipoClase() {
    return ikTipoClase;
  }

  public void setIkTipoClase(UISelectEntity ikTipoClase) {
    this.ikTipoClase = ikTipoClase;
    if(!Objects.equals(ikTipoClase, null))
      this.setIdTipoClase(ikTipoClase.getKey());
  }

  public List<Partida> getPartidas() {
    return partidas;
  }

  public void setPartidas(List<Partida> partidas) {
    this.partidas = partidas;
  }

  public Long getItTipoClase() {
    return itTipoClase;
  }

  public void setItTipoClase(Long itTipoClase) {
    this.itTipoClase = itTipoClase;
  }

  public Long getItArticulo() {
    return itArticulo;
  }

  public void setItArticulo(Long itArticulo) {
    this.itArticulo = itArticulo;
  }
  
}
