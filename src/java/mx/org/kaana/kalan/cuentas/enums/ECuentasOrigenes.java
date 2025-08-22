package mx.org.kaana.kalan.cuentas.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ECuentasOrigenes {

  BANCOS(1L, "1"),
  BANCOS_CARGOS(2L, "11"),
  BANCOS_ABONOS(3L, "12"),
  BANCOS_TRANSFERENCIAS(19L, "13"),
  VENTAS(4L, "2"),
	AHORROS(7L, "3"),
	AHORROS_CARGOS(8L, "31"),
	AHORROS_ABONOS(9L, "32"),
	AHORROS_INICIA(20L, "33"),
  PRESTAMOS(10L, "4"),
  PRESTAMOS_CARGOS(11L, "41"),
  PRESTAMOS_ABONOS(12L, "42"),
  GASTOS(13L, "5"),
  CREDITOS(16L, "6"),
  CREDITOS_CARGOS(17L, "61"),
  CREDITOS_ABONOS(18L, "62");

  private Long idCuentaOrigen;
  private String clave;
  
	private static final Map<Long, ECuentasOrigenes> lookup= new HashMap<>();

  static {
    for (ECuentasOrigenes item: EnumSet.allOf(ECuentasOrigenes.class)) 
      lookup.put(item.getIdCuentaOrigen(), item);    
  }

  private ECuentasOrigenes(Long idCuentaOrigen, String clave) {
    this.idCuentaOrigen = idCuentaOrigen;
    this.clave = clave;
  }

  public Long getIdCuentaOrigen() {
    return idCuentaOrigen;
  }

  public String getClave() {
    return clave;
  }
	
	public static ECuentasOrigenes fromIdCuentaOrigen(Long getIdCuentaOrigen) {
    return lookup.get(getIdCuentaOrigen);
  } 
  
}