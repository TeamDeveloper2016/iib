package mx.org.kaana.mantic.lotes.beans;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.mantic.db.dto.TcManticLotesLimpiosDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 23/06/2024
 *@time 08:41:22 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Unidad extends TcManticLotesLimpiosDto implements Serializable {
  
  private static final long serialVersionUID = 1773425869124398460L;

  private String merma;
  private ESql sql;

  public Unidad() {
    this(-1L);
  }

  public Unidad(Long key) {
    super(key);
    this.sql= ESql.INSERT;
  }

  public String getMerma() {
    return merma;
  }

  public void setMerma(String merma) {
    this.merma = merma;
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
    hash = 37 * hash + Objects.hashCode(this.getIdNotaLimpio());
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
    final Unidad other = (Unidad) obj;
    if (!Objects.equals(this.getIdNotaLimpio(), other.getIdNotaLimpio())) 
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Kilo{merma=" + merma + ", sql=" + sql+ '}';
  }

  @Override
  public Class toHbmClass() {
    return TcManticLotesLimpiosDto.class;
  }
  
}
