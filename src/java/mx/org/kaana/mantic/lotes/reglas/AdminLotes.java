package mx.org.kaana.mantic.lotes.reglas;

import mx.org.kaana.mantic.lotes.beans.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Variables;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.lotes.enums.EEstatusLotes;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 13/06/2024
 *@time 09:36:51 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class AdminLotes implements Serializable {

  private static final long serialVersionUID = 1113346911478417193L;
  
  private Lote lote;
  
  public AdminLotes() throws Exception {
    this(
      new Lote(
        0D, // Double original, 
        1L, // Long idLoteTipo, 
        null, // String nombre, 
        new Long(Fecha.getAnioActual()), // Long ejercicio, 
        null, // String consecutivo, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        -1L, // Long idAlmacen, 
        -1L, // Long idLote, 
        null, // String observaciones, 
        -1L, // Long idEmpresa, 
        0D, // Double cantidad, 
        1L,// Long orden, 
        -1L, // Long idArticulo
        EEstatusLotes.ELABORADO.getKey(), // Long idLoteEstatus  
        -1L, // Long idTipoClase     
        0D, // merma
        0D, // terminado
        0D // restos
      )
    );
    this.lote.setItArticulo(-1L);
    this.lote.setItTipoClase(-1L);
  }

  public AdminLotes(Long idLote) throws Exception {
    this((Lote)DaoFactory.getInstance().toEntity(Lote.class, "TcManticLotesDto", "igual", Variables.toMap("idLote~"+ idLote)));
  }
  
  public AdminLotes(Lote lote) throws Exception {
    Map<String, Object> params= new HashMap<>();
    try {    
      this.lote= lote;
      if(this.lote.isValid()) {
        params.put("idLote", this.lote.getIdLote());
        this.lote.setPartidas((List<Partida>)DaoFactory.getInstance().toEntitySet(Partida.class, "VistaLotesDto", "igual", params, -1L));
        this.lote.setIkEmpresa(new UISelectEntity(new Entity(this.lote.getIdEmpresa())));
        this.lote.setIkAlmacen(new UISelectEntity(new Entity(this.lote.getIdAlmacen())));
        this.lote.setIkArticulo(new UISelectEntity(new Entity(this.lote.getIdArticulo())));
        for (Partida item: this.lote.getPartidas()) {
          item.setSql(ESql.SELECT);
        } // for
      } // if
    } // try
    catch(Exception e) {
      throw e;
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }

  public Lote getLote() {
    return lote;
  }

  public void setLote(Lote lote) {
    this.lote = lote;
  }

  
}
