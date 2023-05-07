package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ETiposIncidentes {
	
	FALTA            ("FALTA", "incidencia-rojo"), // 1
	VACACIONES       ("VACACIONES", "incidencia-verde"), // 2
	PERMISO          ("PERMISO", "incidencia-amarilla"), // 3
	LICENCIA_MEDICA  ("LICENCIA MEDICA", "incidencia-naranja"), // 4
	LICENCIA_MATERNA ("LICENCIA MATERNA", "incidencia-naranja"), // 5
	LICENCIA_PATERNA ("LICENCIA PATERNA", "incidencia-naranja"), // 6
	OMISION_ENTRADA  ("OMISION ENTRADA", "incidencia-azul"), // 7
	OMISION_SALIDA   ("OMISION SALIDA", "incidencia-azul"), // 8
	ONOMASTICO       ("ONOMASTICO", "incidencia-verde"), // 9
	ALTA             ("ALTA", "incidencia-verde"), // 10
	BAJA             ("BAJA", "incidencia-rojo"), // 11
	REINGRESO        ("REINGRESO", "incidencia-verde"), // 12
	DEPOSITO         ("DEPOSITO", "incidencia-rojo"), // 13
	NO_DEPOSITO      ("NO DEPOSITO", "incidencia-verde"), // 14
	DIA_FESTIVO      ("DIA FESTIVO", "incidencia-verde"), // 15
	EXEDENTE_NOMINA  ("EXCEDENTE NOMINA", "incidencia-amarilla"), // 16
	DIA_TRIPLE       ("DIA TRIPLE", "incidencia-rojo"), // 17
	PRESTAMO_NOMINA  ("PRESTAMO NOMINA", "incidencia-rojo"), // 18
	ABONO_NOMINA     ("ABONO PRESTAMO", "incidencia-rojo"), // 19
	APERTURA_CAJA    ("APERTURA DE CAJA CHICA", "incidencia-verde"), // 20
	SALDO_CAJA       ("SALDO DE CAJA CHICA", "incidencia-verde"), // 21
	HORAS_EXTRAS     ("HORAS EXTRAS", "incidencia-amarilla"), // 22
	MEDIO_DIA        ("MEDIO_DIA", "incidencia-rojo"), // 23
  DIA_DOBLE        ("DIA FESTIVO", "incidencia-verde"); // 24
	
	private static final Map<Long, ETiposIncidentes> lookup= new HashMap<>();		
	private String nombre;	
	private String styleClass;	
	
	static {
    for (ETiposIncidentes item: EnumSet.allOf(ETiposIncidentes.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	private ETiposIncidentes(String nombre, String styleClass) {
		this.nombre    = nombre;
		this.styleClass= styleClass;
	}	
	
	public Long getKey(){
		return this.ordinal()+ 1L;
	} // getKey

	public String getNombre() {
		return nombre;
	}		

	public String getStyleClass() {
		return styleClass;
	}
	
	public static ETiposIncidentes fromId(Long id) {
    return lookup.get(id);
  } // fromIdEstatusEgreso	
}