package mx.org.kaana.kalan.cuentas.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EEstatusCuentas {

  REGISTRADO(1L),
  APLICADO(2L),
  CANCELADO(3L),
  ELIMINADO(4L);

  private Long idEstatusCuenta;
  
	private static final Map<Long, EEstatusCuentas> lookup= new HashMap<>();

  static {
    for (EEstatusCuentas item: EnumSet.allOf(EEstatusCuentas.class)) 
      lookup.put(item.getIdEstatusCuenta(), item);    
  }

  private EEstatusCuentas(Long idEstatusCuenta) {
    this.idEstatusCuenta = idEstatusCuenta;
  }

  public Long getIdEstatusCuenta() {
    return idEstatusCuenta;
  }

	public static EEstatusCuentas fromIdEstatusCuenta(Long idEstatusCuenta) {
    return lookup.get(idEstatusCuenta);
  } 
  
}