package mx.org.kaana.mantic.inventarios.entradas.beans;

import java.io.Serializable;
import java.util.Objects;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 13/06/2024
 *@time 09:36:51 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Promedio implements Serializable {

  private static final long serialVersionUID = 1493346911478417193L;
  
  private Long idArticulo;
  private Double total;

  public Promedio() {
    this(-1L);
  }

  public Promedio(Long idArticulo) {
    this(idArticulo, 0D);
  }

  public Promedio(Long idArticulo, Double total) {
    this.idArticulo = idArticulo;
    this.total = total;
  }

  public Long getIdArticulo() {
    return idArticulo;
  }

  public void setIdArticulo(Long idArticulo) {
    this.idArticulo = idArticulo;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + Objects.hashCode(this.idArticulo);
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
    final Promedio other = (Promedio) obj;
    if (!Objects.equals(this.idArticulo, other.idArticulo)) 
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Promedio{" + "idArticulo=" + idArticulo + ", total=" + total + '}';
  }

}
