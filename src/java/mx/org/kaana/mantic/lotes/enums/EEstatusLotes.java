package mx.org.kaana.mantic.lotes.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EEstatusLotes {

	ELABORADO, EN_PROCESO, EN_PRODUCCION, CANCELADO, TERMIADO, TRANSFERIDO, REPROCESADO, DISPONIBLE;
	
	private static final Map<Long, EEstatusLotes> lookup= new HashMap<>();
	
	static {
    for (EEstatusLotes item: EnumSet.allOf(EEstatusLotes.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	public Long getKey(){
		return this.ordinal() + 1L;
	} // getKey
	
	public static EEstatusLotes fromIdEstatusLotes(Long idEstatusLotes) {
    return lookup.get(idEstatusLotes);
  } // fromIdEstatusEgreso
}
