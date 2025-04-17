package mx.org.kaana.kalan.gastos.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 16/04/2025
 *@time 06:33:40 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public enum EGastos {
  
  GASTOS_ADMINISTRATIVOS,
  GASTOS_COMPRA,
  GASTOS_MERMA,
  GASTOS_VARIOS;

  private static final Map<Integer, EGastos> lookup= new HashMap<>();

  static {
    for (EGastos item: EnumSet.allOf(EGastos.class))
      lookup.put(item.ordinal()+ 1, item);
  } // static

  public Long getKey(){
		return new Long(this.ordinal()+ 1);
	}

  public static EGastos fromOrdinal(Long ordinal) {
    return lookup.get(ordinal.intValue());
  }
  
}
