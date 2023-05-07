package mx.org.kaana.mantic.incidentes.reglas;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import mx.org.kaana.mantic.incidentes.beans.Dia;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 30/04/2022
 *@time 05:23:12 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Semana extends ArrayList<Dia> implements Serializable {

  private static final long serialVersionUID = -8203127693041044565L;
  private static final int dias= 7;
  
  private Date fecha;

  public Semana(Date fecha) {
    Calendar calendar= Calendar.getInstance();
    calendar.setTimeInMillis(fecha.getTime());
    for (int x= 0; x< dias; x++) {
      this.add(new Dia(new Date(calendar.getTimeInMillis())));
      calendar.add(Calendar.DATE, 1);
    } // for
    this.fecha= new Date(calendar.getTimeInMillis());
  }

}
