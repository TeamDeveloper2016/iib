package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EOpcionesResidente {
	
	EMPLEADOS       ("Asignación de empleados a los desarrollos", "818f9da3a0a8a0aab83e", "/Paginas/Keet/Catalogos/Contratos/Personal/registro.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 11L),
	INCIDENCIAS     ("Registro de incidencias de empleados", "3bcd5dee77f6758d91e81a5d", "/Paginas/Keet/Catalogos/Contratos/Personal/empleados.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 11L),
	DIAS_FERIADOS   ("Registro de dias feriados de empleados", "4cd962ed0e202f3acd2556aacc73", "/Paginas/Keet/Catalogos/Contratos/Personal/empleados.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 11L),
	DESTAJOS        ("Registrar un avance para el destajo realizado por los contratistas o subcontratistas", "e864e872898798a9bd", "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 8L),
	GEOREFERENCIA   ("Georeferencia", "21212738c546c545d62e4cbf3c2e", "", "/Paginas/Keet/Catalogos/Contratos/georeferencia.jsf?opcion=", 11L),
	VALES           ("Generación de vales", "708f919397a7", "/Paginas/Keet/Catalogos/Contratos/Vales/Normales/filtro.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 11L),
	VALES_ESPECIALES("Generación de vales especiales", "051212101a203635c9285d90ec7d808499", "/Paginas/Keet/Catalogos/Contratos/Vales/Especiales/filtro.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 11L),
	REGISTRO_GASTO  ("Registrar un gasto de caja chica", "223dc345c158e57684d1295a98bf46", "/Paginas/Keet/CajaChica/accion.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 11L),
	CONSULTA_GASTO  ("Consultar gastos generados", "b0ba4bd764f408121243bacb2a2d35", "/Paginas/Keet/CajaChica/consulta.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 11L),
	//CAJA
	ABONO_CAJA_CHICA("Abono a caja chica", "afb9ba44dd60f674f40d7eed041f212528", "/Paginas/Keet/CajaChica/abonar.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 11L),
	//ALMACENISTA	
	MATERIALES      ("Entrega de materiales", "c95ede7d8d92929da8df03", "/Paginas/Keet/Catalogos/Contratos/Materiales/filtro.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 11L),
	AUTORIZACION    ("Autorización de vales especiales", "e661f7021e3dcf64e41a5392d5", "/Paginas/Keet/Catalogos/Contratos/Vales/Autorizacion/filtro.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 11L),
	CONTROLES       ("Registrar un avance en el seguimiento a los lotes por los residentes de obra", "ec798999a2b940c249ae", "/Paginas/Keet/Controles/control.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 11L),
	// DESTAJOS
	AUDITORIA       ("Auditar trabajos realizados", "919bb1bb4ad36af27d8c", "/Paginas/Keet/Auditoria/filtro.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 11L),
	HISTORIAL       ("Historial de destajos realizados por lote", "2e3fc953e8758e999bd5", "/Paginas/Keet/Catalogos/Contratos/Destajos/historia.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 8L),
	SEGUIMIENTO     ("Seguimiento a los destajos realizados por lote", "c451d751e97e82858beb708f", "/Paginas/Keet/Catalogos/Contratos/Destajos/seguimiento.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 8L),
	CONTROL         ("Control en los pagos de los destajos", "323cca57e47a8082", "/Paginas/Keet/Catalogos/Contratos/Destajos/control.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 8L),
	RESUMEN         ("Detalle de los destajos realizados", "ba44c852e87b8999", "/Paginas/Keet/Catalogos/Contratos/Destajos/resumen.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 8L),
	ENTREGAS        ("Entrega de lotes terminados", "ba49d665f873f37b8f", "/Paginas/Keet/Catalogos/Contratos/Entregas/filtro.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 8L),
	PROPUESTA       ("Registrar un avance para el destajo realizado por los contratistas o subcontratistas", "9ca5b648d760ec0e2356", "/Paginas/Keet/Catalogos/Contratos/Destajos/faltan.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 8L),
	COMBUSTIBLES    ("Registrar un ticket de compra de combustible", "36303ec547d16c8e96ea077cfa", "/Paginas/Sakbe/Compras/filtro.jsf", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=", 8L),
	DIESEL          ("Registrar un suministro de diesel", "52df68e378f074", "/Paginas/Sakbe/Suministros/accion.jsf", "/Paginas/Sakbe/Combustibles/desarrollos.jsf?opcion=", 8L),
	LUBRICANTE      ("Registrar un suministro de lubricante", "aabf54d864e06de06ef006", "/Paginas/Sakbe/Suministros/lubricante.jsf", "/Paginas/Sakbe/Combustibles/desarrollos.jsf?opcion=", 8L),
	HERRAMIENTA     ("Registrar un suministro de herramientas", "a8b9bf5ce16ef272f677fb0d", "/Paginas/Sakbe/Suministros/herramienta.jsf", "/Paginas/Sakbe/Combustibles/desarrollos.jsf?opcion=", 8L);
  
	private static final Map<Long, EOpcionesResidente> lookup= new HashMap<>();	
	private String titulo;
	private String cifrado;
	private String ruta;
	private String retorno;
  private Long idContratoEstatus;
	
	private EOpcionesResidente(String titulo, String cifrado, String ruta, String retorno, Long idContratoEstatus) {		
		this.titulo   = titulo;
		this.cifrado  = cifrado;		
		this.ruta     = ruta;		
		this.retorno  = retorno.concat(cifrado);
    this.idContratoEstatus= idContratoEstatus;
	} // EOpcionesResidente

	static {
    for (EOpcionesResidente item: EnumSet.allOf(EOpcionesResidente.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	public Long getKey(){
		return this.ordinal() + 1L;
	} // getKey	

	public String getTitulo() {
		return titulo;
	} // getTitulo

	public String getCifrado() {
		return cifrado;
	}	// getCifrado
	
	public String getRetorno(){
		return retorno;
	}
	
	public String getRuta(){
		return ruta;
	}
	
	public static EOpcionesResidente fromId(Long id) {
    return lookup.get(id);
  } // fromIdEstatusEgreso	

  public Long getIdContratoEstatus() {
    return idContratoEstatus;
  }
  
}