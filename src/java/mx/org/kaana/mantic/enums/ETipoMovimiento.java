package mx.org.kaana.mantic.enums;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 19/06/2018
 *@time 07:51:53 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public enum ETipoMovimiento {
  
	ORDENES_COMPRAS("orden(es) de compra(s)", "tc_mantic_ordenes_bitacora", "idOrdenCompra", "VistaOrdenesComprasDto"), 
	NOTAS_ENTRADAS("nota(s) de entrada(s)", "tc_mantic_notas_bitacora", "idNotaEntrada", "VistaNotasEntradasDto"), 
	VENTAS("venta(s)", "tc_mantic_ventas_bitacoras", "idVenta", "VistaVentasDto"), 
	SERVICIOS("servicio(s)", "tc_mantic_servicios_bitacoras", "idServicio", "VistaTallerServiciosDto"), 
	DEVOLUCIONES("devolucion(es)", "tc_mantic_devoluciones_bitacoras", "idDevolucion", "VistaDevolucionesDto"),
	NOTAS_CREDITOS("nota(s) de credito(s)", "tc_mantic_creditos_notas_bitacoras", "idCreditoNota", "VistaNotasCreditosDto"),
	CIERRES_CAJA("cierre(s) de caja", "tc_mantic_cierres_bitacoras", "idCierre", "VistaCierresCajasDto"),
	FACTURAS_FICTICIAS("factura(s)", "tc_mantic_ficticias_bitacoras", "idFicticia", "VistaFicticiasDto"),
	TRANSFERENCIAS("transferencia(s)", "tc_mantic_transferencias_bitacoras", "idTransferencia", "VistaAlmacenesTransferenciasDto"),
	MULTIPLES("transferencia(s) multiples", "tc_mantic_transferencias_multiples_bitacoras", "idTransferenciaMultiple", "VistaTransferenciasMultiplesDto"),
  GASTOS("gasto(s)", "tc_kalan_gastos_bitacoras", "idEmpresaGasto", "VistaEmpresasGastosDto"),
  INGRESOS("ingreso(s)", "tc_kalan_movimientos_bitacoras", "idEmpresaMovimiento", "VistaEmpresasMovimientosDto"),
  EGRESOS("egreso(s)", "tc_kalan_movimientos_bitacoras", "idEmpresaMovimiento", "VistaEmpresasMovimientosDto"),
  LOTES("lote(s)", "tc_mantic_lotes_bitacora", "idLote", "VistaLotesDto"),
  CREDITOS("credito(s)", "tc_kalan_creditos_bitacoras", "idCredito", "VistaCreditosDto"),
  PRESTAMOS("prestamo(s)", "tc_kalan_prestamos_bitacoras", "idPrestamo", "VistaPrestamosDto"),
  AHORROS("ahorro(s)", "tc_kalan_ahorros_bitacoras", "idAhorro", "VistaAhorrosDto");
	 
	private final String title;
	private final String table;
	private final String idKey;
	private final String proceso;
	
	private ETipoMovimiento(String title, String table, String idKey, String proceso) {
		this.title= title;
		this.table= table;
		this.idKey= idKey;
		this.proceso= proceso;
	}
	
	public String getTitle() {
		return "Movimientos generados de las ".concat(this.title);
	}
	
	public String getTable() {
		return "tc_mantic_".concat(this.table);
	}
	
	public String getIdKey() {
		return this.idKey;
	}
	
	public String getProceso() {
		return proceso;
	}
	
}
