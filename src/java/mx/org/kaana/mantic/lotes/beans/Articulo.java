package mx.org.kaana.mantic.lotes.beans;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.mantic.db.dto.TcManticLotesTerminadosDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 23/06/2024
 *@time 08:41:22 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Articulo extends TcManticLotesTerminadosDto implements Serializable {
  
  private static final long serialVersionUID = 1173425869124398460L;

  private UISelectEntity ikArticulo;
  private String codigo;
  private String articulo;
  private ESql sql;

  public Articulo() {
    this(-1L);
  }

  public Articulo(Long key) {
    this(JsfBase.getIdUsuario(), -1L, null, 0D, -1L, key);
  }

  public Articulo(Long idUsuario, Long idLote, String observaciones, Double cantidad, Long idArticulo, Long idLoteTerminado) {
    super(idUsuario, idLote, observaciones, cantidad, idArticulo, idLoteTerminado);
    this.ikArticulo= new UISelectEntity(idArticulo);
    this.sql= ESql.INSERT;
  }

  public UISelectEntity getIkArticulo() {
    return ikArticulo;
  }

  public void setIkArticulo(UISelectEntity ikArticulo) {
    this.ikArticulo = ikArticulo;
    if(!Objects.equals(ikArticulo, null))
      this.setIdArticulo(ikArticulo.getKey());
  }
  
  public String getCodigo() {
    return codigo;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getArticulo() {
    return articulo;
  }

  public void setArticulo(String articulo) {
    this.articulo = articulo;
  }

  public ESql getSql() {
    return sql;
  }

  public void setSql(ESql accion) {
    this.sql = accion;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 37 * hash + Objects.hashCode(this.getIdArticulo());
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) 
      return true;
    if (obj == null) 
      return false;
    if (getClass() != obj.getClass()) 
      return false;
    final Articulo other = (Articulo) obj;
    if (!Objects.equals(this.getIdArticulo(), other.getIdArticulo())) 
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Terminado{" + "codigo=" + codigo + ", articulo=" + articulo + ", sql=" + sql + '}';
  }

  @Override
  public Class toHbmClass() {
    return TcManticLotesTerminadosDto.class;
  }

  @Override
  public Articulo clone() {
    Articulo regresar= new Articulo();
    regresar.setIkArticulo(this.getIkArticulo());
    regresar.setCodigo(this.codigo);
    regresar.setArticulo(this.articulo);
    regresar.setCantidad(this.getCantidad());
    regresar.setIdLote(this.getIdLote());
    regresar.setIdLoteTerminado(-1L);
    regresar.setIdUsuario(this.getIdUsuario());
    regresar.setObservaciones(this.getObservaciones());
    regresar.setRegistro(this.getRegistro());
    return regresar;
  } 
  
}
