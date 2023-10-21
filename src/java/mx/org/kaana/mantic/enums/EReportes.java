package mx.org.kaana.mantic.enums;

import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.libs.reportes.IReportAttribute;

public enum EReportes implements IReportAttribute{

	ORDEN_COMPRA              ("VistaReportesOrdenesComprasDto", "reporteOrden", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/orden", EFormatos.PDF, "OC", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	ORDENES_COMPRA            ("VistaReportesOrdenesComprasDto", "ordenesCompra", "Ordenes de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/ordenesCompra", EFormatos.PDF, "OC", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	ORDEN_DETALLE             ("VistaReportesOrdenesComprasDto", "ordenDetalle", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/ordenDetalle", EFormatos.PDF, "OCD", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	ORDEN_DETALLE_DIF         ("VistaReportesOrdenesComprasDto", "diferenciasOrdenCompra", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/ordenDetalleDiferencias", EFormatos.PDF, "OCDD", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	ORDEN_DETALLE_DIF_DIF     ("VistaReportesOrdenesComprasDto", "diferenciasOrdenCompra", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/ordenDetalleDiferencias", EFormatos.PDF, "OCDD", "/Paginas/Mantic/Compras/Ordenes/diferencias"),
	ORDEN_DETALLES_COMP       ("VistaReportesOrdenesComprasDto", "detalleCompletoOrdenCompra", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/detCompletoOrdenCompra", EFormatos.PDF, "OCDC", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	ORDEN_DETALLES_COMP2      ("VistaReportesOrdenesComprasDto", "detalleCompletoOrdenCompra", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/detalleCompletoOrdenCompra", EFormatos.PDF, "OCDC", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	TICKET_VENTA              ("VistaTicketVentaDto", "ticket", "Ticket de venta", "/Paginas/Mantic/Ventas/Reportes/ticketVenta", EFormatos.PDF, "TV", "/Paginas/Mantic/Ventas/accion"),
	TICKET_VENTA_CREDITO      ("VistaTicketVentaDto", "ticket", "Ticket de venta", "/Paginas/Mantic/Ventas/Reportes/ticketCredito", EFormatos.PDF, "TVC", "/Paginas/Mantic/Ventas/accion"),
	NOTAS_ENTRADA             ("VistaNotasEntradasDto", "lazy", "Notas de Entrada", "/Paginas/Mantic/Inventarios/Entradas/Reportes/notasEntrada", EFormatos.PDF, "NE", "/Paginas/Mantic/Inventarios/Entradas/filtro"),
	NOTA_ENTRADA_DETALLE      ("VistaReporteNotaEntrada", "detalleNotaEntrada", "Nota de Entrada", "/Paginas/Mantic/Inventarios/Entradas/Reportes/notaEntradaDetalle", EFormatos.PDF, "NED", "/Paginas/Mantic/Inventarios/Entradas/filtro"),
	NOTA_ENTRADA_DETALLE_D    ("VistaReporteNotaEntrada", "detalleNotaEntradaDif", "Nota de Entrada", "/Paginas/Mantic/Inventarios/Entradas/Reportes/notaEntradaDetalleDif", EFormatos.PDF, "NEDD", "/Paginas/Mantic/Inventarios/Entradas/filtro"),
	DEVOLUCIONES              ("VistaDevolucionesDto", "lazy", "Devoluciones", "/Paginas/Mantic/Inventarios/Devoluciones/Reportes/devoluciones", EFormatos.PDF, "D", "/Paginas/Mantic/Inventarios/Devoluciones/filtro"),
	DEVOLUCIONES_DETALLE      ("VistaReporteDevoluciones", "detalleDevolucion", "Devolución", "/Paginas/Mantic/Inventarios/Devoluciones/Reportes/devolucionDetalle", EFormatos.PDF, "DD", "/Paginas/Mantic/Inventarios/Devoluciones/filtro"),
	NOTAS_CREDITO             ("VistaCreditosNotasDto", "lazy", "Notas de credito", "/Paginas/Mantic/Inventarios/Creditos/Reportes/notasCredito", EFormatos.PDF, "NC", "/Paginas/Mantic/Inventarios/Caja/Creditos/filtro"),
	NOTA_CREDITO_DETALLE      ("VistaReporteNotasCredito", "detalleNostasCredito", "Nota de crédito", "/Paginas/Mantic/Inventarios/Creditos/Reportes/notaCreditoDetalle", EFormatos.PDF, "NCD", "/Paginas/Mantic/Inventarios/Caja/Creditos/filtro"),
  CUENTAS_POR_PAGAR         ("VistaEmpresasDto", "cuentasBusqueda", "Cuentas por pagar", "/Paginas/Mantic/Catalogos/Empresas/Cuentas/Reportes/cuentasPorPagar", EFormatos.PDF, "CXP", "/Paginas/Mantic/Catalogos/Empresas/Cuentas/saldos"),
	CUENTA_PAGAR_DETALLE      ("VistaReporteCuentaPorPagarDetalle", "pagosDeuda", "Cuenta por pagar", "/Paginas/Mantic/Catalogos/Empresas/Cuentas/Reportes/cuentaPorPagarDetalle", EFormatos.PDF, "CXPD", "/Paginas/Mantic/Catalogos/Empresas/Cuentas/saldos"),
  CUENTAS_POR_COBRAR        ("VistaClientesDto", "cuentasBusqueda", "Cuentas por cobrar", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/cuentasPorCobrar", EFormatos.PDF, "CXC", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
  PAGOS_CUENTAS_POR_COBRAR  ("VistaClientesDto", "pagosCuentasCobrar", "Pagos a cuentas por cobrar", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/cuentasPorCobrar", EFormatos.PDF, "PCXC", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
  DEUDAS_CLIENTES           ("VistaDeudasClienteDto", "row", "Estado de cuenta", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/clienteDeudasDetalle", EFormatos.PDF, "EDO", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
  DEUDAS_CLIENTES_PENDIENTES("VistaDeudasClienteDto", "pendientes", "Estado de cuenta", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/clienteDeudasDetalle", EFormatos.PDF, "EDOC", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
  DEUDAS_CLIENTES_DETALLE   ("VistaDeudasClienteDto", "individual", "Detalle deuda", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/deudaDetalleSaldo", EFormatos.PDF, "DEUDA", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
	CUENTA_COBRAR_DETALLE     ("VistaReporteCuentaPorCobrarDetalle", "cobroDeuda", "Cuenta por cobrar", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/cuentaPorCobrarDetalle", EFormatos.PDF, "CXCD", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
  REQUISICIONES             ("VistaReporteRequisiciones", "requisiciones", "Requisiciones", "/Paginas/Mantic/Compras/Requisiciones/Reportes/requisiciones", EFormatos.PDF, "REQ", "/Paginas/Mantic/Catalogos/Proveedores/Requisiciones/filtro"),
	REQUISICIONES_DETALLE     ("VistaReporteRequisiciones", "requisicionDetalle", "Requisición", "/Paginas/Mantic/Compras/Requisiciones/Reportes/requisicionDetalle", EFormatos.PDF, "REQD", "/Paginas/Mantic/Compras/Requisiciones/filtro"),
	REQUISICIONES_DETALLE_PROV("VistaReporteRequisiciones", "requisicionDetalle", "Requisición", "/Paginas/Mantic/Compras/Requisiciones/Reportes/requisicionDetalle", EFormatos.PDF, "REQD", "/Paginas/Mantic/Compras/Requisiciones/filtro"),
  TRANSFERENCIAS            ("VistaAlmacenesTransferenciasDto", "lazy", "Transferencias", "/Paginas/Mantic/Catalogos/Almacenes/Transferencias/Reportes/transferencias", EFormatos.PDF, "TRA", "/Paginas/Mantic/Catalogos/Almacenes/Transferencias/filtro"),
	TRANSFERENCIAS_DETALLE    ("VistaReporteTransferenciasDto", "row", "Transferencia", "/Paginas/Mantic/Catalogos/Almacenes/Transferencias/Reportes/transferenciasDetalle", EFormatos.PDF, "TRAD", "/Paginas/Mantic/Catalogos/Almacenes/Transferencias/filtro"),
  UBICACIONES               ("VistaAlmacenesUbicacionesDto", "lazy", "Ubicaciones", "/Paginas/Mantic/Catalogos/Almacenes/Ubicaciones/Reportes/ubicaciones", EFormatos.PDF, "UBC", "/Paginas/Mantic/Catalogos/Almacenes/Ubicaciones/filtro"),
	UBICACIONES_DETALLE       ("VistaAlmacenesUbicacionesDto", "lazyArticulo", "Ubicacion", "/Paginas/Mantic/Catalogos/Almacenes/Ubicaciones/Reportes/ubicacionesDetalle", EFormatos.PDF, "UBCD", "/Paginas/Mantic/Catalogos/Almacenes/Ubicaciones/filtro"),
  CONFTONTAS                ("VistaConfrontasDto", "lazy", "Confrontas", "/Paginas/Mantic/Catalogos/Almacenes/Confrontas/Reportes/confrontas", EFormatos.PDF, "CON", "/Paginas/Mantic/Catalogos/Almacenes/Confrontas/filtro"),
	CONFTONTAS_DETALLE        ("VistaConfrontasReporteDto", "consulta", "Confronta", "/Paginas/Mantic/Catalogos/Almacenes/Confrontas/Reportes/confrontaDetalle", EFormatos.PDF, "COND", "/Paginas/Mantic/Catalogos/Almacenes/Confrontas/filtro"),
  VENTAS_A_CREDITO          ("VistaVentasDto", "lazy", "Ventas a credito", "/Paginas/Mantic/Ventas/Autorizacion/Reportes/ventasCredito", EFormatos.PDF, "VC", "/Paginas/Mantic/Ventas/Autorizacion/filtro"),
  VENTA_A_CREDITO_DETALLE   ("VistaReporteVentasDetalle", "detalleVenta", "Venta a crédito", "/Paginas/Mantic/Ventas/Autorizacion/Reportes/ventaCreditoDetalle", EFormatos.PDF, "VCD", "/Paginas/Mantic/Ventas/Autorizacion/filtro"),
  COTIZACIONES              ("VistaVentasDto", "cotizacion", "Cotizaciones", "/Paginas/Mantic/Ventas/Cotizacion/Reportes/cotizaciones", EFormatos.PDF, "COT", "/Paginas/Mantic/Ventas/Cotizacion/filtro"),
	COTIZACION_DETALLE        ("VistaTicketVentaDto", "ventas", "Cotización", "/Paginas/Mantic/Ventas/Cotizacion/Reportes/cotizacionDetalle", EFormatos.PDF, "COTD", "/Paginas/Mantic/Ventas/Cotizacion/filtro"),
  CUENTAS                   ("VistaVentasDto", "lazy", "Cuentas abiertas", "/Paginas/Mantic/Ventas/Cuentas/Reportes/cuentas", EFormatos.PDF, "CUE", "/Paginas/Mantic/Ventas/Cuentas/filtro"),
	CUENTAS_DETALLE           ("VistaTicketVentaDto", "ventas", "Cuenta abierta", "/Paginas/Mantic/Ventas/Cuentas/Reportes/cuentaDetalle", EFormatos.PDF, "CUED", "/Paginas/Mantic/Ventas/Cuentas/filtro"),
  CIERRES_CAJA              ("VistaCierresCajasDto", "lazy", "Cierres caja", "/Paginas/Mantic/Ventas/Caja/Cierres/Reportes/cierres", EFormatos.PDF, "CIERRE", "/Paginas/Mantic/Ventas/Caja/Cierres/filtro"),
  ABONOS_RETIROS            ("VistaCierresCajasDto", "retiros", "Abonos y retiros de efectivo", "/Paginas/Mantic/Ventas/Caja/Cierres/Reportes/abonosRetiros", EFormatos.PDF, "ABONO", "/Paginas/Mantic/Ventas/Caja/Cierres/ambos"),
  APARTADOS                 ("VistaApartadosDto", "apartados", "Apartados", "/Paginas/Mantic/Ventas/Apartados/Reportes/apartados", EFormatos.PDF, "APARTA", "/Paginas/Mantic/Ventas/Apartados/filtro"),
	APARTADO_DETALLE          ("VistaApartadosDto", "pagosApartado", "Apartado", "/Paginas/Mantic/Ventas/Apartados/Reportes/apartadoDetalle", EFormatos.PDF, "APARTAD", "/Paginas/Mantic/Ventas/Apartados/abono"),
  FACTURAS_FICTICIAS        ("VistaFicticiasDto", "lazy", "Listado de facturación", "/Paginas/Mantic/Facturas/Reportes/facturas", EFormatos.PDF, "FAC", "/Paginas/Mantic/Facturas/filtro"),
	FACTURAS_FICTICIAS_DETALLE("reporteFacturaDetalle", "ficticiaDetalle", "Facturación", "/Paginas/Mantic/Facturas/Reportes/facturaDetalle", EFormatos.PDF, "FAC", "/Paginas/Mantic/Facturas/filtro"),
	FACTURAS_RESUMEN          ("VistaReporteJuntaFacturas", "facturas", "Facturama", "", EFormatos.PDF, "FACS", "/Paginas/Mantic/Facturas/filtro"),
	ORDEN_TALLER              ("VistaReportesOrdenesTallerDto", "ordenTaller", "Orden de reparación", "/Paginas/Mantic/Taller/Reportes/ordenTaller", EFormatos.PDF, "ODT", "/Paginas/Mantic/Taller/filtro"),
	PRODUCTOS                 ("VistaProductosDto", "productos", "Catalogo de productos", "/Paginas/Mantic/Productos/Reportes/productos", EFormatos.PDF, "PTO", "/Paginas/Mantic/Productos/filtro"),
	CUENTA_COBRAR_TICKETS     ("VistaClientesDto", "tickets", "Tickets", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/ticketsPorCobrarDetalle", EFormatos.PDF, "CXCT", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
	EMPLEADOS                 ("VistaEmpleadosEstatusDto", "empleados", "Plantilla de personal", "/Paginas/Mantic/Catalogos/Empleados/Reportes/empleados", EFormatos.PDF, "PDP", "/Paginas/Mantic/Catalogos/Empleados/filtro"),
	EMPLEADOS_DESARROLLO      ("VistaEmpleadosEstatusDto", "desarrollo", "Plantilla de personal", "/Paginas/Keet/Catalogos/Contratos/Personal/Reportes/empleados", EFormatos.PDF, "PDP", "/Paginas/Mantic/Catalogos/Contratos/Personal/consulta"),
	PRESTAMOS                 ("VistaPrestamosDto", "lazy", "Préstamos", "/Paginas/Keet/Prestamos/Reportes/prestamos", EFormatos.PDF, "prestamos", "Paginas/Keet/Prestamos/filtro"),
	ANTICIPOS                 ("VistaAnticiposDto", "lazy", "Anticipos", "/Paginas/Keet/Prestamos/Proveedor/Reportes/prestamos", EFormatos.PDF, "ANT", "Paginas/Keet/Prestamos/Proveedor/filtro"),
	PRESTAMOS_PAGOS           ("VistaReportesPrestamosDto", "prestamosPagos", "Resumen de pagos", "/Paginas/Keet/Prestamos/Reportes/pagosPrestamos", EFormatos.PDF, "PAP", "/Paginas/Keet/Prestamos/filtro"),
	ANTICIPOS_PAGOS           ("VistaReportesAnticiposDto", "anticiposPagos", "Resumen de pagos de anticipos", "/Paginas/Keet/Prestamos/Proveedor/Reportes/pagosPrestamos", EFormatos.PDF, "PAA", "/Paginas/Keet/Prestamos/Proveedor/filtro"),
	RESUMEN_PRESTAMOS         ("VistaReportesPrestamosDto", "resumenPrestamos", "Resumen de los préstamos", "/Paginas/Keet/Prestamos/Reportes/resumenPrestamos", EFormatos.PDF, "RPRE", "/Paginas/Keet/Prestamos/filtro"),
	RESUMEN_ANTICIPOS         ("VistaReportesAnticiposDto", "resumenAnticipos", "Resumen de los anticipos", "/Paginas/Keet/Prestamos/Proveedor/Reportes/resumenPrestamos", EFormatos.PDF, "RANT", "/Paginas/Keet/Prestamos/Proveedor/filtro"),
	NOMINA_SUBCONTRATISTA     ("VistaNominaReportesDto", "proveedorDetalle", "Detalle de nómina del subcontratista", "/Paginas/Keet/Nominas/Reportes/detalleSubcontratista", EFormatos.PDF, "DNC", "/Paginas/Keet/Nominas/filtro"),
  RESUMEN_NOMINA_SUBC       ("VistaNominaReportesDto", "resumen", " Resumen de nómina de los subcontratistas", "/Paginas/Keet/Nominas/Reportes/nominaSubcontratistas", EFormatos.PDF, "RNSC", "/Paginas/Keet/Nominas/filtro"),
  LISTADO_NOMINA            ("VistaNominaDto", "lazy", "Listado de nómina", "/Paginas/Keet/Nominas/Reportes/nomina", EFormatos.PDF, "LIN", "/Paginas/Keet/Nominas/filtro"),
  DETALLE_NOMINA_PERSONAS   ("VistaNominaConsultasDto", "personas", "Detalle de nómina del personal", "/Paginas/Keet/Nominas/Reportes/detallePersona", EFormatos.PDF, "DNP", "/Paginas/Keet/Nominas/filtro"),
  LISTADO_NOMINA_PERSONAS   ("VistaNominaConsultasDto", "personas", "Listado de nómina del personal", "/Paginas/Keet/Nominas/Reportes/personas", EFormatos.PDF, "LNC", "/Paginas/Keet/Nominas/personas"),
  LISTADO_NOMINA_CALCULADA  ("VistaNominaConsultasDto", "calculada", "Listado de nómina del personal", "/Paginas/Keet/Nominas/Reportes/personas", EFormatos.PDF, "LNC", "/Paginas/Keet/Nominas/personas"),
  LISTADO_NOMINA_PROVEEDORES("VistaNominaConsultasDto", "proveedores", "Listado de nómina de los subcontratistas", "/Paginas/Keet/Nominas/Reportes/proveedores", EFormatos.PDF, "LNSC", "/Paginas/Keet/Nominas/proveedores"),
  DESTAJOS_CONTRATISTA_X    ("VistaNominaConsultasDto", "personas", "Detalle del destajos del contratista", "/Paginas/Keet/Nominas/Reportes/detalleDestajosContratistas", EFormatos.PDF, "DDC", "/Paginas/Keet/Nominas/filtro"),
  DESTAJOS_CAT_CONTRATISTA  ("VistaNominaConsultasDto", "destajoPersona", "Destajos del contratista", "/Paginas/Keet/Catalogos/Contratos/Destajos/Reportes/detallesDestajos", EFormatos.PDF, "DC", "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro"),
  DESTAJOS_CAT_RESIDENTE    ("VistaNominaConsultasDto", "destajoResidente", "Avande del residente de obra", "/Paginas/Keet/Controles/Reportes/detallesDestajos", EFormatos.PDF, "AR", "/Paginas/Keet/Controles/control"),
  DESTAJOS_CAT_SUBCONTRATISTA("VistaNominaConsultasDto", "destajoProveedor", "Destajos del subcontratista", "/Paginas/Keet/Catalogos/Contratos/Destajos/Reportes/detallesProveedor", EFormatos.PDF, "DSC", "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro"),
  DESTAJOS_TOTALES_SUBCONTRATISTA("VistaNominaConsultasReportesDto", "destajoProveedor", "Destajos del subcontratista", "/Paginas/Keet/Catalogos/Contratos/Destajos/Reportes/destajosProveedor", EFormatos.PDF, "DSCD", "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro"),
  DESTAJOS_TOTALES_CONTRATISTA("VistaNominaConsultasReportesDto", "destajoPersona", "Destajos del contratista", "/Paginas/Keet/Catalogos/Contratos/Destajos/Reportes/destajosPersona", EFormatos.PDF, "DCD", "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro"),
  DESTAJOS_TOTALES_RESIDENTE("VistaNominaConsultasReportesDto", "destajoResidente", "Avance del resindente de obra", "/Paginas/Keet/Controles/Reportes/destajosPersona", EFormatos.PDF, "ARD", "/Paginas/Keet/Controles/control"),
  NOMINA_LISTADO            ("VistaNominaReportesDto", "listadoNomina", "Listado nómina", "/Paginas/Keet/Nominas/Reportes/nominaEmpresa", EFormatos.PDF, "NOM", "/Paginas/Keet/Nominas/filtro"),
  PUNTOS_CONTROL            ("VistaPuntosControlReporteDto", "principal", "Puntos de revisión", "/Paginas/Keet/Catalogos/PuntosControl/Reportes/puntosRevision", EFormatos.PDF, "PC", "/Paginas/Keet/Catalogos/PuntosControl/filtro"),
  ESTACIONES                ("VistaReportesEstacionesDto", "estaciones", "Estaciones", "/Paginas/Keet/Estaciones/Reportes/estaciones", EFormatos.PDF, "EST", "/Paginas/Keet/Estaciones/contrato"),
  PROTOTIPOS                ("VistaReportesEstacionesDto", "estaciones", "Estaciones", "/Paginas/Keet/Estaciones/Reportes/estaciones", EFormatos.PDF, "ESTP", "/Paginas/Keet/Estaciones/contrato"),
  MATERIALES                ("VistaReportesEstacionesDto", "materiales", "Materiales", "/Paginas/Keet/Materiales/Reportes/materiales", EFormatos.PDF, "MAT", "/Paginas/Keet/Materiales/filtro"),
  CONTROLES                 ("VistaReportesEstacionesDto", "controles", "Controles", "/Paginas/Keet/Estaciones/Reportes/estaciones", EFormatos.PDF, "CTR", "/Paginas/Keet/Estaciones/contrato"),
  CAJA_CHICA                ("VistaReportesCajaChicaDto", "detalle", "Listado de gastos de caja chica", "/Paginas/Keet/CajaChica/Reportes/detalleCajaChica", EFormatos.PDF, "CC", "/Paginas/Keet/CajaChica/filtro"),
  ESTIMACION_SALDOS         ("VistaEstimacionesDto", "saldos", "Estado de cuenta", "/Paginas/Keet/Estimaciones/Reportes/estimacion", EFormatos.PDF, "EDOC", "/Paginas/Keet/Estimaciones/saldos"),
  CONTRATO_RESUMEN          ("VistaContratosDto", "findDesarrollo", "Resumen de estimación de obra", "/Paginas/Contenedor/Reportes/resumen", EFormatos.PDF, "RES", "/Paginas/Contenedor/resumen");
	
	
	private final String proceso;
  private final String idXml;
  private final String titulo; 
  private final String jrxml;
  private final EFormatos formato;
  private final String nombre;
  private final String regresar;

	private EReportes(String proceso, String idXml, String titulo, String jrxml, EFormatos formato, String nombre, String regresar) {
    this.proceso = proceso;
    this.idXml   = idXml;
    this.titulo  = titulo;
    this.jrxml   = jrxml;
    this.formato = formato;
    this.nombre  = nombre;
    this.regresar= regresar;
  }

	@Override
  public String getIdentificador() {
    return EReportes.class.getName();
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
  public String getJrxml() {
    return jrxml;
  }

  @Override
  public EFormatos getFormato() {
    return formato;
  }

  @Override
  public String getNombre() {
    return nombre;
  }

  @Override
  public String getTitulo() {
    return titulo;
  }

  @Override
  public String getRegresar() {
    return regresar;
  }
  
  public Boolean getAutomatico() {
    return Boolean.TRUE;
	}
}
