package mx.org.kaana.mantic.incidentes;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 18/10/2020
 *@time 12:41:51 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public enum ETipoIndicente {
  
  FALTA("fa-user-times", "FALTA"), VACACIONES("fa-ship", "VACACIONES"), PERMISO("fa-hotel", "PERMISO"), LICENCIA_MEDICA("fa-ambulance", "LICENCIA MEDICA"), LICENCIA_MATERNA("fa-ambulance", "LICENCIA MATERNA"), LICENCIA_PATERNA("fa-ambulance", "LICENCIA PATERNA"), OMISION_ENTRADA("fa-user", "OMISION DE ENTRADA"), OMISION_SALIDA("fa-user", "OMISION DE SALIDA"), ONOMASTICO("fa-birthday-cake", "ONOMASTICO"), ALTA("", "ALTA"), BAJA("", "BAJA");
  
  private String icon;
  private String title;

  private ETipoIndicente(String icon, String title) {
    this.icon = icon;
    this.title= title;
  }

  public String getIcon() {
    return icon;
  }
  
  public String getTitle() {
    return title;
  }
  
  public static String toIcon(int idTipoIncidente) {
    if(idTipoIncidente>= 0 && idTipoIncidente< ETipoIndicente.values().length)
      return ETipoIndicente.values()[idTipoIncidente].icon;
    else
      return "fas fa-bomb";
  }
  
  public static String toTitle(int idTipoIncidente) {
    if(idTipoIncidente>= 0 && idTipoIncidente< ETipoIndicente.values().length)
      return ETipoIndicente.values()[idTipoIncidente].title;
    else
      return "NO DEFINIDO";
  }
  
}
