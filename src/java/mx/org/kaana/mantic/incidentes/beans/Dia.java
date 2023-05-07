package mx.org.kaana.mantic.incidentes.beans;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;
import mx.org.kaana.libs.formato.Fecha;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 30/04/2022
 *@time 05:19:15 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Dia implements Serializable {

  private static final long serialVersionUID = 7174179665875272596L;

  private String dia;
  private Boolean activo;
  private Double costo;
  private Date fecha;

  public Dia(Date fecha) {
    this.fecha = fecha;
    this.activo= Boolean.FALSE;
    this.costo = 0D;
    this.dia   = Fecha.formatear(Fecha.DIA_CORTO_FECHA, this.fecha);
  }

  public String getDia() {
    return dia;
  }

  public void setDia(String dia) {
    this.dia = dia;
  }

  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }

  public Double getCosto() {
    return costo;
  }

  public void setCosto(Double costo) {
    this.costo = costo;
  }

  public Date getFecha() {
    return fecha;
  }

  public void setFecha(Date fecha) {
    this.fecha = fecha;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 11 * hash + Objects.hashCode(this.fecha);
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
    final Dia other = (Dia) obj;
    if (!Objects.equals(this.fecha, other.fecha)) 
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Dia{" + "dia=" + dia + ", activo=" + activo + ", fecha=" + fecha + '}';
  }
  
}
