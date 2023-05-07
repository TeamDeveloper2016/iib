package mx.org.kaana.mantic.enums;

import mx.org.kaana.kajool.procesos.reportes.reglas.IExportacionXls;

public enum EExportacionXls implements IExportacionXls{

	ARTICULOS ("VistaArticulosDto", "exportar", "ART", "/Paginas/Mantic/Catalogos/Articulos/filtro", ""),
	CONTEOS ("VistaArticulosDto", "conteo", "CON", "/Paginas/Mantic/Inventarios/Almacenes/filtro", ""),
	PERSONAS    ("VistaPersonasDto", "exportar", "PER", "/Paginas/Mantic/Catalogos/Empleados/filtro", ""),
	ESTACIONES  ("VistaContratosLotesDto", "exportar", "EST", "/Paginas/Keet/Estaciones/Masivos/importar", ""),
	INCIDENCIAS ("VistaIncidentesDto", "principal", "INC", "/Paginas/Keet/Catalogos/Contratos/Personal/exportar", ""),
	ANTICIPOS   ("VistaAnticiposDto", "principal", "INC", "/Paginas/Keet/Catalogos/Contratos/Proveedor/exportar", ""),
	NOMINA      ("VistaNominaDto", "detalle", "NOM", "/Paginas/Keet/Nominas/filtro", ""),
	NOMINA_PERSONA   ("VistaNominaConsultasDto", "persona", "PER", "/Paginas/Keet/Nominas/personas", ""),
	DESTAJO_PERSONA  ("VistaNominaConsultasDto", "destajo", "CTA", "/Paginas/Keet/Nominas/personas", ""),
	DESTAJO_PROVEEDOR("VistaNominaConsultasDto", "proveedor", "SCTA", "/Paginas/Keet/Nominas/proveedores", ""),
	MATERIALES       ("VistaContratosLotesMaterialesDto", "exportar", "MAT", "/Paginas/Keet/Estaciones/Masivos/importar", ""),
	PRECIOS          ("VistaComprasAlmacenDto", "precios", "PRE", "/Paginas/Keet/Estaciones/Masivos/importar", ""),
	PRECIOS_CONVENIO ("VistaComprasAlmacenDto", "convenios", "PEC", "/Paginas/Keet/Estaciones/Masivos/importar", ""),
	CONTROLES  ("VistaControlesLotesDto", "exportar", "CTR", "/Paginas/Keet/Estaciones/Masivos/importar", ""),
	CONVENIOS  ("VistaPrecioClienteDto", "convenios", "PCS", "/Paginas/Mantic/Catalogos/Clientes/Convenios/importar", "");
  
  private final String proceso;
  private final String idXml;
  private final String nombreArchivo;
  private final String paginaRegreso;
  private final String patron;

  private EExportacionXls(String proceso, String idXml, String nombreArchivo, String paginaRegreso, String patron) {
    this.proceso      = proceso;
    this.idXml        = idXml;
    this.nombreArchivo= nombreArchivo;
    this.paginaRegreso= paginaRegreso;
    this.patron       = patron;
  }

  @Override
  public String getProceso() {
    return proceso;
  }

  @Override
  public String getIdXml() {
    return idXml;
  }

  @Override
  public String getNombreArchivo() {
    return nombreArchivo;
  }

  @Override
  public String getPaginaRegreso() {
    return paginaRegreso;
  }

	@Override
  public String getPatron() {
    return patron;
  }	
}
