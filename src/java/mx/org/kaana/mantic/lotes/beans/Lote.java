package mx.org.kaana.mantic.lotes.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.reflection.Methods;
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
  private List<Unidad> unidades;
  private List<Articulo> articulos;
  private List<Kilo> cantidades;
  private List<Porcentaje> porcentajes;

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
    this.setUnidades(new ArrayList<Unidad>());
    this.setArticulos(new ArrayList<Articulo>());
    this.setCantidades(new ArrayList<Kilo>());
    this.setPorcentajes(new ArrayList<Porcentaje>());
  }

  public Lote(Double original, Long idLoteTipo, String nombre, Long ejercicio, String consecutivo, Long idUsuario, Long idAlmacen, Long idLote, String observaciones, Long idEmpresa, Double cantidad, Long orden, Long idArticulo, Long idLoteEstatus, Long idTipoClase, Double merma, Double terminado, Double restos) {
    super(original, idLoteTipo, nombre, ejercicio, consecutivo, idUsuario, idAlmacen, idLote, observaciones, idEmpresa, cantidad, orden, idArticulo, idLoteEstatus, idTipoClase, merma, terminado, restos);
    this.setIkEmpresa(new UISelectEntity(idEmpresa));
    this.setIkAlmacen(new UISelectEntity(idAlmacen));
    this.setIkArticulo(new UISelectEntity(idArticulo));
    this.setIkTipoClase(new UISelectEntity(idTipoClase));
    this.setPartidas(new ArrayList<Partida>());
    this.setUnidades(new ArrayList<Unidad>());
    this.setArticulos(new ArrayList<Articulo>());
    this.setCantidades(new ArrayList<Kilo>());
    this.setPorcentajes(new ArrayList<Porcentaje>());
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

  public List<Unidad> getUnidades() {
    return unidades;
  }

  public void setUnidades(List<Unidad> unidades) {
    this.unidades = unidades;
  }

  public List<Articulo> getArticulos() {
    return articulos;
  }

  public void setArticulos(List<Articulo> articulos) {
    this.articulos = articulos;
  }

  public List<Kilo> getCantidades() {
    return cantidades;
  }

  public void setCantidades(List<Kilo> cantidades) {
    this.cantidades = cantidades;
  }

  public List<Porcentaje> getPorcentajes() {
    return porcentajes;
  }

  public void setPorcentajes(List<Porcentaje> porcentajes) {
    this.porcentajes = porcentajes;
  }
  
  public void toLoadArticulos() {
    Map<String, Object> params= new HashMap<>();
    try {
      params.put("idLote", this.getIdLote());      
      this.setArticulos((List<Articulo>)DaoFactory.getInstance().toEntitySet(Articulo.class, "TcManticLotesTerminadosDto", "igual", params, -1L));
      if(!Objects.equals(this.getArticulos(), null))
        for (Articulo item: this.getArticulos()) 
          item.setSql(ESql.SELECT);
      else
        this.setArticulos(new ArrayList<>());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } 
  
  public void toLoadPorcentajes() {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("sortOrder", "order by tc_mantic_notas_calidades.id_nota_calidad");
      params.put("idLote", this.getIdLote());
      this.setPorcentajes((List<Porcentaje>)DaoFactory.getInstance().toEntitySet(Porcentaje.class, "VistaLotesDto", "porcentajes", params));
      if(!Objects.equals(this.getPorcentajes(), null)) {
        for (Porcentaje item: this.getPorcentajes()) {
          if(Objects.equals(item.getIdLotePromedio(), -1L)) {
            item.setIdLote(this.getIdLote());
            item.setIdArticulo(this.getIdArticulo());
            item.setCantidad(0D);
            item.setPorcentaje(0D);
            item.setSql(ESql.INSERT);
          } // if  
          else
            item.setSql(ESql.SELECT);
        } // for
      } // if  
      else
        this.setPorcentajes(new ArrayList<>());  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  public void toLoadCantidades() {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("sortOrder", "order by tc_mantic_notas_mermas.id_nota_merma");
      params.put("idLote", this.getIdLote());
      this.setCantidades((List<Kilo>)DaoFactory.getInstance().toEntitySet(Kilo.class, "VistaLotesDto", "cantidades", params));
      if(!Objects.equals(this.getCantidades(), null)) {
        for (Kilo item: this.getCantidades()) {
          if(Objects.equals(item.getIdLoteCalidad(), -1L)) {
            item.setIdLote(this.getIdLote());
            item.setIdArticulo(this.getIdArticulo());
            item.setCantidad(0D);
            item.setPorcentaje(0D);
            item.setSql(ESql.INSERT);
          } // if  
          else
            item.setSql(ESql.SELECT);
        } // for
      } // if  
      else
        this.setCantidades(new ArrayList<>());  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }

  public void toLoadPartidas() {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("sortOrder", "order by tc_mantic_lotes_detalles.id_lote_detalle");
      params.put("idLote", this.getIdLote());
      this.setPartidas((List<Partida>)DaoFactory.getInstance().toEntitySet(Partida.class, "VistaLotesDto", "detalle", params));
      if(!Objects.equals(this.getPartidas(), null)) {
        for (Partida item: this.getPartidas()) 
          item.setSql(ESql.INSERT);
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
}
