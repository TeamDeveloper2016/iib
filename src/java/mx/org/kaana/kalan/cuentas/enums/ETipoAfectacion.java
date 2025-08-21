package mx.org.kaana.kalan.cuentas.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ETipoAfectacion {

  CARGO(1L),
  ABONO(2L);

  private Long idTipoAfectacion;
  
	private static final Map<Long, ETipoAfectacion> lookup= new HashMap<>();

  static {
    for (ETipoAfectacion item: EnumSet.allOf(ETipoAfectacion.class)) 
      lookup.put(item.getIdTipoAfectacion(), item);    
  }

  private ETipoAfectacion(Long idTipoAfectacion) {
    this.idTipoAfectacion = idTipoAfectacion;
  }

  public Long getIdTipoAfectacion() {
    return idTipoAfectacion;
  }

	public static ETipoAfectacion fromIdTipoAfectacion(Long idTipoAfectacion) {
    return lookup.get(idTipoAfectacion);
  } 
  
}