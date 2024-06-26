package mx.org.kaana.mantic.lotes.beans;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.mantic.db.dto.TcManticLotesDetallesDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 13/06/2024
 *@time 09:36:51 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Partida extends TcManticLotesDetallesDto implements Serializable {

  private static final long serialVersionUID = 1193246911478417193L;
  
  private String consecutivo;
  private String proveedor;
  private String codigo;
  private String articulo;
  private Double original;
  private ESql sql;
  
  public Partida() {
    this(-1L);
  }

  public Partida(Long idKey) {
    super(idKey);
    this.original= 0D;
  }

  public Partida(Long idNotaDetalle, Long idArticulo) {
    super(-1L);
    this.original= 0D;
    this.setIdNotaDetalle(idNotaDetalle);
    this.setIdArticulo(idArticulo);
  }

  public Partida(Long idUsuario, Long idNotaDetalle, Long idLote, Long idLoteDetalle, Double cantidad, Double saldo, Long idArticulo, Double original) {
    super(idUsuario, idNotaDetalle, idLote, idLoteDetalle, cantidad, saldo, idArticulo);
    this.original= original;
    this.sql= ESql.INSERT;
  }
  
  public String getConsecutivo() {
    return consecutivo;
  }

  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getProveedor() {
    return proveedor;
  }

  public void setProveedor(String proveedor) {
    this.proveedor = proveedor;
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

  public Double getOriginal() {
    return original;
  }

  public void setOriginal(Double original) {
    this.original = original;
  }

  public void setSql(ESql sql) {
    this.sql = sql;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 89 * hash + Objects.hashCode(this.getIdNotaDetalle());
    hash = 89 * hash + Objects.hashCode(this.getIdArticulo());
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
    final Partida other = (Partida) obj;
    if (!Objects.equals(this.getIdNotaDetalle(), other.getIdNotaDetalle())) 
      return false;
    if (!Objects.equals(this.getIdArticulo(), other.getIdArticulo())) 
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Partida{" + "consecutivo=" + consecutivo + ", proveedor=" + proveedor + ", codigo=" + codigo + ", articulo=" + articulo + ", original=" + original + ", sql=" + sql + '}';
  }

}
