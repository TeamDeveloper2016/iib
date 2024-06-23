package mx.org.kaana.mantic.inventarios.origenes.beans;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.mantic.db.dto.TcManticNotasPromediosDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 23/06/2024
 *@time 08:41:22 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Porcentaje extends TcManticNotasPromediosDto implements Serializable {
  
  private static final long serialVersionUID = 8773425869124398460L;

  private String merma;
  private ESql sql;

  public Porcentaje() {
    this(-1L);
  }

  public Porcentaje(Long key) {
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
    hash = 37 * hash + Objects.hashCode(this.getIdNotaCalidad());
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
    final Porcentaje other = (Porcentaje) obj;
    if (!Objects.equals(this.getIdNotaCalidad(), other.getIdNotaCalidad())) 
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Porcentaje{merma=" + merma + ", sql=" + sql+ '}';
  }

  @Override
  public Class toHbmClass() {
    return TcManticNotasPromediosDto.class;
  }
  
}
