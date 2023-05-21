package mx.org.kaana.kalan.catalogos.pacientes.beans;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import mx.org.kaana.kalan.db.dto.TcKalanHistorialesDto;
import mx.org.kaana.libs.formato.Cadena;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 19/05/2023
 *@time 04:33:01 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Clinico extends TcKalanHistorialesDto implements Serializable {

  private static final long serialVersionUID = -2463401498611484487L;

  private String[] k14;
  private String[] k25;

  public Clinico() {
    this(-1L);
  }
  
  public Clinico(Long idCliente) {
    super(-1L);
    super.setIdCliente(idCliente);
    this.k14= new String[] {};
    this.k25= new String[] {};
  }

  public void init() {
    this.k14= this.toArray(super.getC14());
    this.k25= this.toArray(super.getC25());
    this.toEdad();
  }
  
  public String[] getK14() {
    return k14;
  }

  public void setK14(String[] k14) {
    this.k14 = k14;
    if(this.k14!= null) {
      List<String> items= Arrays.asList(this.k14);
      this.setC14(items.toString());
    } // if  
  }

  public String[] getK25() {
    return k25;
  }

  public void setK25(String[] k25) {
    this.k25 = k25;
    if(this.k25!= null) {
      List<String> items= Arrays.asList(this.k25);
      this.setC25(items.toString());
    } // if  
  }
  
  public Boolean getIsC14() {
    Boolean regresar= Boolean.FALSE;
    if(this.k14!= null) {
      List<String> items= Arrays.asList(this.k14);
      regresar= items.contains("10");
    } // if          
    return regresar;
  }

  public void toEdad() {
    if(!Objects.equals(this.getC02(), null) && !Cadena.isVacio(this.getC02())) {
      TimeUnit time   = TimeUnit.DAYS;
      long miliseconds= Calendar.getInstance().getTimeInMillis() - this.getC02().getTime();
      long days       = time.convert(miliseconds, TimeUnit.MILLISECONDS);
      super.setC03((long)(days/365));
    } // if
  } 
  
  private String[] toArray(String txt) {
    String[] regresar= new String[] {};
    if(!Objects.equals(txt, null) && !Cadena.isVacio(txt)) {
      StringTokenizer st= new StringTokenizer(txt, "[, ]", Boolean.FALSE);
      if(!Objects.equals(st, null)) {
        regresar= new String[st.countTokens()];
        int count= 0;
        while(st.hasMoreTokens()) {
          regresar[count++]= (String)st.nextToken();
        } // while
      } // if  
    } // if
    return regresar;
  }
}
