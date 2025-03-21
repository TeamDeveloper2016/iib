package mx.org.kaana.mantic.catalogos.masivos.reglas;

import java.io.File;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;
import mx.org.kaana.mantic.db.dto.TcManticAlmacenesArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticAlmacenesUbicacionesDto;
import mx.org.kaana.mantic.db.dto.TcManticArticulosCodigosDto;
import mx.org.kaana.mantic.db.dto.TcManticArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDto;
import mx.org.kaana.mantic.db.dto.TcManticDomiciliosDto;
import mx.org.kaana.mantic.db.dto.TcManticEgresosDto;
import mx.org.kaana.mantic.db.dto.TcManticEncargosDto;
import mx.org.kaana.mantic.db.dto.TcManticInventariosDto;
import mx.org.kaana.mantic.db.dto.TcManticMasivasArchivosDto;
import mx.org.kaana.mantic.db.dto.TcManticMasivasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticMasivasDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticMovimientosDto;
import mx.org.kaana.mantic.db.dto.TcManticProveedoresDto;
import mx.org.kaana.mantic.db.dto.TcManticRefaccionesDto;
import mx.org.kaana.mantic.db.dto.TcManticTrabajosDto;
import mx.org.kaana.mantic.db.dto.TrManticClienteDomicilioDto;
import mx.org.kaana.mantic.db.dto.TrManticClienteTipoContactoDto;
import mx.org.kaana.mantic.db.dto.TrManticProveedorDomicilioDto;
import mx.org.kaana.mantic.db.dto.TrManticProveedorPagoDto;
import mx.org.kaana.mantic.db.dto.TrManticProveedorTipoContactoDto;
import static org.apache.commons.io.Charsets.ISO_8859_1;
import static org.apache.commons.io.Charsets.UTF_8;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {
  
  private static final Log LOG = LogFactory.getLog(Transaccion.class);
  private TcManticMasivasArchivosDto masivo;	
	private ECargaMasiva categoria;
	private Long idProveedor;
	private int errores;
	private int procesados;
	private String messageError;
  
  public Transaccion(TcManticMasivasArchivosDto masivo, ECargaMasiva categoria) {
		this(masivo, categoria, -1L);
	}
	
  public Transaccion(TcManticMasivasArchivosDto masivo, ECargaMasiva categoria, Long idProveedor) {
		this.masivo     = masivo;		
		this.categoria  = categoria;
		this.errores    = 0;
		this.procesados = 0;
		this.idProveedor= idProveedor;
	} // Transaccion

	protected void setMessageError(String messageError) {
		this.messageError= messageError;
	}

	public String getMessageError() {
		return messageError;
	}

	public int getErrores() {
		return errores;
	}

	public int getProcesados() {
		return procesados;
	}
  
	@Override
  protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
    boolean regresar= false;
    try {
      this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" catalogo de forma masiva.");
      switch (accion) {
				case PROCESAR: 
 					regresar= this.toProcess(sesion);
					break;
				case ELIMINAR: 
					// regresar = DaoFactory.getInstance().delete(sesion, this.masivo)> -1L;
					break;
      } // swtich 
      if (!regresar) {
        throw new Exception(messageError);
      } // if
    } // tyr
		catch (Exception e) {
      throw new Exception(messageError.concat("<br/>") + e.getMessage());
    } // catch
    return regresar;
  }
  
	protected boolean toProcess(Session sesion) throws Exception {
		boolean regresar                   = false;  
		TcManticMasivasBitacoraDto bitacora= null;
		File file= new File(this.masivo.getAlias());
		if(file.exists()) {
	 		if(!this.masivo.isValid()) {
			  DaoFactory.getInstance().updateAll(sesion, TcManticMasivasArchivosDto.class, this.masivo.toMap());
		    DaoFactory.getInstance().insert(sesion, this.masivo);
				bitacora= new TcManticMasivasBitacoraDto(
					"", // String justificacion, 
					this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
					JsfBase.getIdUsuario(), // Long idUsuario, 
					-1L, // Long idMasivaBitacora, 
					0L, // Long procesados, 
					1L // Long idMasivaEstatus
				);
				DaoFactory.getInstance().insert(sesion, bitacora);
				this.toDeleteXls();
			} // if
			Monitoreo monitoreo= JsfBase.getAutentifica().getMonitoreo();
			monitoreo.comenzar(0L);
			monitoreo.setTotal(this.masivo.getTuplas());
			monitoreo.setId(file.getName().toUpperCase());
			try {
				switch (this.categoria) {
					case ARTICULOS:
						this.toArticulos(sesion, file);
						break;
					case CLIENTES:
						this.toClientes(sesion, file);
						break;
					case PROVEEDORES:
						this.toProveedores(sesion, file);
						break;
					case REFACCIONES:
						this.toRefacciones(sesion, file);
						break;
					case SERVICIOS:
						this.toServicios(sesion, file);
						break;
					case EGRESOS:
						this.toEgresos(sesion, file);
						break;
					case CONTEOS:
						this.toConteos(sesion, file);
						break;
					case CODIGOS:
						this.toCodigos(sesion, file);
						break;
				} // swtich
			} // try
			finally {
				monitoreo.terminar();
				monitoreo.setProgreso(0L);
				bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 3L);
				DaoFactory.getInstance().insert(sesion, bitacora);
			} // catch
			regresar= true;
		} // if	
		else
			LOG.warn("INVESTIGAR PORQUE NO EXISTE EL ARCHIVO EN EL SERVIDOR: "+ this.masivo.getNombre());
    return regresar;
	} // toProcess
	
	private Long toFindEntidad(Session sesion, String entidad) throws Exception {
		Long regresar= -1L;
		Map<String, Object> params= new HashMap<>();
		try {
			String codigo= entidad.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim();
			params.put("descripcion", codigo.replaceAll("(,| |\\t)+", ".*"));
			Value value= DaoFactory.getInstance().toField(sesion, "TcJanalEntidadesDto", "entidad", params, "idEntidad");
			if(value!= null && value.getData()!= null)
				regresar= value.toLong();
			else {
				value= DaoFactory.getInstance().toField(sesion, "TcJanalEntidadesDto", "primero", params, "idEntidad");
  			if(value!= null && value.getData()!= null)
	  			regresar= value.toLong();
			} // else
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindEntidad

	private Long toFindMunicipio(Session sesion, Long idEntidad, String municipio) throws Exception {
		Long regresar= -1L;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idEntidad", idEntidad);
			params.put("descripcion", municipio!= null? municipio.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*"): "XYZ");
			Value value= DaoFactory.getInstance().toField(sesion, "TcJanalMunicipiosDto", "municipio", params, "idMunicipio");
			if(value!= null && value.getData()!= null)
				regresar= value.toLong();
			else {
				value= DaoFactory.getInstance().toField(sesion, "TcJanalMunicipiosDto", "primero", params, "idMunicipio");
  			if(value!= null && value.getData()!= null)
	  			regresar= value.toLong();
			} // else
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindMunicipio
	
	private Long toFindLocalidad(Session sesion, Long idMunicipio, String localidad) throws Exception {
		Long regresar= -1L;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idMunicipio", idMunicipio);
			params.put("descripcion", localidad!= null? localidad.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*"): "XYZ");
			Value value= DaoFactory.getInstance().toField(sesion, "TcJanalLocalidadesDto", "localidad", params, "idLocalidad");
			if(value!= null && value.getData()!= null)
				regresar= value.toLong();
			else {
				value= DaoFactory.getInstance().toField(sesion, "TcJanalLocalidadesDto", "primero", params, "idLocalidad");
  			if(value!= null && value.getData()!= null)
	  			regresar= value.toLong();
			} // else
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindLocalidad
	
	private Long toFindUsoCFDI(Session sesion, String clave) {
		Long regresar= 3L;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("clave", clave.toUpperCase());
			Value value= DaoFactory.getInstance().toField(sesion, "VistaCargasMasivasDto", "usoCfdi", params, "idUsoCfdi");
			if(value!= null && value.getData()!= null)
				regresar= value.toLong();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindUsoCFDI
	
	private Long toFindRegimenFiscal(Session sesion, String clave) {
		Long regresar= 13L;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("clave", clave.toUpperCase());
			Value value= DaoFactory.getInstance().toField(sesion, "VistaCargasMasivasDto", "regimenFiscal", params, "idRegimenFiscal");
			if(value!= null && value.getData()!= null)
				regresar= value.toLong();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindRegimenFiscal
	
	private TrManticClienteTipoContactoDto toFindTipoContactoCliente(Session sesion, Long idTipoContacto, Long idCliente) {
		TrManticClienteTipoContactoDto regresar= null;
		Map<String, Object> params             = new HashMap<>();
		try {
			// 1 telefono, 9 correo, 10 correo personal
			params.put("idTipoContacto", idTipoContacto);
			params.put("idCliente", idCliente);
			regresar= (TrManticClienteTipoContactoDto)DaoFactory.getInstance().toEntity(sesion, TrManticClienteTipoContactoDto.class, "VistaCargasMasivasDto", "contactoc", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindTipoContactoCliente
	
	private TrManticProveedorTipoContactoDto toFindTipoContactoProveedor(Session sesion, Long idTipoContacto, Long idProveedor) {
		TrManticProveedorTipoContactoDto regresar= null;
		Map<String, Object> params               = new HashMap<>();
		try {
			// 1 telefono, 9 correo, 10 correo personal
			params.put("idTipoContacto", idTipoContacto);
			params.put("idProveedor", idProveedor);
			regresar= (TrManticProveedorTipoContactoDto)DaoFactory.getInstance().toEntity(sesion, TrManticProveedorTipoContactoDto.class, "VistaCargasMasivasDto", "contactop", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindTipoContactoProveedor
	
	private TcManticClientesDto toFindCliente(Session sesion, String rfc) {
		TcManticClientesDto regresar= null;
		Map<String, Object> params  = new HashMap<>();
		try {
			params.put("rfc", rfc);
			regresar= (TcManticClientesDto)DaoFactory.getInstance().toEntity(sesion, TcManticClientesDto.class, "VistaCargasMasivasDto", "cliente", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindCliente

	private TcManticProveedoresDto toFindProveedor(Session sesion, String rfc, String clave) {
		TcManticProveedoresDto regresar= null;
		Map<String, Object> params     = new HashMap<>();
		try {
			params.put("rfc", rfc);
			params.put("clave", clave);
			regresar= (TcManticProveedoresDto)DaoFactory.getInstance().toEntity(sesion, TcManticProveedoresDto.class, "VistaCargasMasivasDto", "proveedor", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindProveedor

	private Long toFindUnidadMedida(Session sesion, String codigo) {
		Long regresar= 1L;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("codigo", codigo);
			Value value= DaoFactory.getInstance().toField(sesion, "VistaCargasMasivasDto", "empaque", params, "idEmpaqueUnidadMedida");
			if(value!= null && value.getData()!= null)
				regresar= value.toLong();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindUnidadMedida
	
	private Long toFindCodigoAuxiliar(Session sesion, String codigo) {
		Long regresar= -1L;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("codigo", codigo);
			Value value= DaoFactory.getInstance().toField(sesion, "VistaCargasMasivasDto", "auxiliar", params, "idArticuloCodigo");
			if(value!= null && value.getData()!= null)
				regresar= value.toLong();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindCodigoAuxiliar
	
	private TcManticArticulosDto toFindArticulo(Session sesion, String codigo, Long idArticuloTipo) {
		TcManticArticulosDto regresar= null;
		Map<String, Object> params   = new HashMap<>();
		try {
			params.put("codigo", codigo);
			params.put("idArticuloTipo", idArticuloTipo);
			regresar= (TcManticArticulosDto)DaoFactory.getInstance().toEntity(sesion, TcManticArticulosDto.class, "VistaCargasMasivasDto", "articulo", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindArticulo

	private TcManticRefaccionesDto toFindRefaccion(Session sesion, String codigo, Long idProveedor) {
		TcManticRefaccionesDto regresar= null;
		Map<String, Object> params     = new HashMap<>();
		try {
			params.put("codigo", codigo);
			params.put("idProveedor", idProveedor);
			regresar= (TcManticRefaccionesDto)DaoFactory.getInstance().toEntity(sesion, TcManticRefaccionesDto.class, "TcManticRefaccionesDto", "existe", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindRefaccion

	private TcManticEncargosDto toFindServicio(Session sesion, String codigo) {
		TcManticEncargosDto regresar= null;
		Map<String, Object> params  = new HashMap<>();
		try {
			params.put("codigo", codigo);
			regresar= (TcManticEncargosDto)DaoFactory.getInstance().toEntity(sesion, TcManticEncargosDto.class, "TcManticEncargosDto", "existe", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindServicio

	private TcManticArticulosCodigosDto toFindCodigoFabricante(Session sesion, Long idArticulo, Long idProveedor) {
		TcManticArticulosCodigosDto regresar= null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idArticulo", idArticulo);
			params.put("idProveedor", idProveedor);
      if(!Objects.equals(idProveedor, null))
   			regresar= (TcManticArticulosCodigosDto)DaoFactory.getInstance().toEntity(sesion, TcManticArticulosCodigosDto.class, "TcManticArticulosCodigosDto", "codigo", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindCodigoFabricante
  
	private Long toFindAlmacen(Session sesion, String almacen) {
		Long regresar             = -1L;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put("clave", almacen);
			Value value= DaoFactory.getInstance().toField(sesion, "TcManticAlmacenesDto", "conteo", params, "idAlmacen");
			if(value!= null && value.getData()!= null)
				regresar= value.toLong();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
	private TcManticAlmacenesArticulosDto toFindAlmacenArticulo(Session sesion, Long idAlmacen, Long idArticulo) {
		TcManticAlmacenesArticulosDto regresar= null;
		Map<String, Object> params            = new HashMap<>();
		try {
			params.put("idAlmacen", idAlmacen);
			params.put("idArticulo", idArticulo);
			regresar= (TcManticAlmacenesArticulosDto)DaoFactory.getInstance().toEntity(sesion, TcManticAlmacenesArticulosDto.class, "TcManticAlmacenesArticulosDto", "ubicacion", params);
			if(regresar== null) {
				regresar= new TcManticAlmacenesArticulosDto(
					10D, // Double minimo, 
					-1L, // Long idAlmacenArticulo, 
					JsfBase.getIdUsuario(), // Long idUsuario, 
					idAlmacen, // Long idAlmacen, 
					20D, // Double maximo, 
					-1L, // Long idAlmacenUbicacion, 
					idArticulo, // Long idArticulo, 
					0D // Double stock
				);
			} // if
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindAlmacenArticulo

	private TcManticInventariosDto toFindInventario(Session sesion, Long idAlmacen, Long idArticulo, Double stock) {
		TcManticInventariosDto regresar= null;
		Map<String, Object> params     = new HashMap<>();
		try {
			params.put("idAlmacen", idAlmacen);
			params.put("idArticulo", idArticulo);
			regresar= (TcManticInventariosDto)DaoFactory.getInstance().toEntity(sesion, TcManticInventariosDto.class, "TcManticInventariosDto", "inventario", params);
			if(regresar== null || !regresar.getEntradas().equals(0D) || !regresar.getSalidas().equals(0D)) {
				regresar= new TcManticInventariosDto(-1L);
				regresar.setIdAlmacen(idAlmacen);
				regresar.setIdArticulo(idArticulo);
				regresar.setEjercicio(new Long(Calendar.getInstance().get(Calendar.YEAR)));
				regresar.setEntradas(0D);
				regresar.setSalidas(0D);
				regresar.setIdUsuario(JsfBase.getIdUsuario());
				regresar.setIdAutomatico(2L);
				regresar.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			} // if
			regresar.setInicial(stock);
			regresar.setStock(stock);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindInventario

	private Boolean toFindPrincipal(Session sesion, Long idArticulo, Long idArticuloTipo) {
		Boolean regresar          = false;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idArticulo", idArticulo);
			params.put("idArticuloTipo", idArticuloTipo);
			Value data= DaoFactory.getInstance().toField(sesion, "VistaCargasMasivasDto", "principal", params, "total");
			if(data!= null && data.getData()!= null)
				regresar= data.toLong()>= 1;
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindPrincipal

	private TcManticArticulosDto toFindArticuloIdentico(Session sesion, Map<String, Object> params, Long idArticuloTipo) {
		TcManticArticulosDto regresar= null;
		try {
			params.put("idArticuloTipo", idArticuloTipo);
			regresar= (TcManticArticulosDto)DaoFactory.getInstance().toEntity(sesion, TcManticArticulosDto.class, "VistaCargasMasivasDto", "identico", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindArticuloIndentico

	private Long toFindClase(Session sesion, String clase) {
		Long regresar             = 1L;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("descripcion", clase);
			Value data= DaoFactory.getInstance().toField(sesion, "TcManticTiposClasesDto", "igual", params, "idTipoClase");
			if(data!= null && data.getData()!= null)
				regresar= data.toLong();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindPrincipal

  
  private Boolean toArticulos(Session sesion, File archivo) throws Exception {
		Boolean regresar	      = false;
		Workbook workbook	      = null;
		Sheet sheet             = null;
		TcManticArticulosCodigosDto codigos= null;
		TcManticMasivasBitacoraDto bitacora= null;
		try {
      WorkbookSettings workbookSettings = new WorkbookSettings();
      workbookSettings.setEncoding("Cp1252");	
			workbookSettings.setExcelDisplayLanguage("MX");
      workbookSettings.setExcelRegionalSettings("MX");
      workbookSettings.setLocale(new Locale("es", "MX"));
			workbook= Workbook.getWorkbook(archivo, workbookSettings);
			sheet		= workbook.getSheet(0);
			Monitoreo monitoreo= JsfBase.getAutentifica().getMonitoreo();
			if(sheet != null && sheet.getColumns()>= this.categoria.getColumns() && sheet.getRows()>= 2) {
				//LOG.info("<-------------------------------------------------------------------------------------------------------------->");
				LOG.info("Filas del documento: "+ sheet.getRows());
				this.errores = 0;
				int count    = 0; 
        String codigo= null;
				for(int fila= 1; fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
					try {
						if(sheet.getCell(0, fila)!= null && sheet.getCell(2, fila)!= null && !sheet.getCell(0, fila).getContents().toUpperCase().startsWith("NOTA") && !Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(2, fila).getContents())) {
							// 0           1          2        3          4          5          6           7        8        9            10            11          12      13         14         15         16
							//CODIGO|CODIGOAUXILIAR|NOMBRE|COSTOS/IVA|MENUDEONETO|MEDIONETO|MAYOREONETO|UNIDADMEDIDA|IVA|LIMITEMENUDEO|LIMITEMAYOREO|STOCKMINIMO|STOCKMAXIMO|SAT|CODIGOFABRICANTE|FACTOR|TIPO PRODUCTO|CLASE  
							String contenido= new String(sheet.getCell(2, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
							double costo   = Numero.getDouble(sheet.getCell(3, fila).getContents()!= null? sheet.getCell(3, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
							double menudeo = Numero.getDouble(sheet.getCell(4, fila).getContents()!= null? sheet.getCell(4, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
							double medio   = Numero.getDouble(sheet.getCell(5, fila).getContents()!= null? sheet.getCell(5, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
							double mayoreo = Numero.getDouble(sheet.getCell(6, fila).getContents()!= null? sheet.getCell(6, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
							double iva     = Numero.getDouble(sheet.getCell(8, fila).getContents()!= null? sheet.getCell(8, fila).getContents().replaceAll("[$, ]", ""): "0", 16D);
							double lmenudeo= Numero.getDouble(sheet.getCell(9, fila).getContents()!= null? sheet.getCell(9, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
							double lmayoreo= Numero.getDouble(sheet.getCell(10, fila).getContents()!= null? sheet.getCell(10, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
							double minimo  = Numero.getDouble(sheet.getCell(11, fila).getContents()!= null? sheet.getCell(11, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
							double maximo  = Numero.getDouble(sheet.getCell(12, fila).getContents()!= null? sheet.getCell(12, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
							String sat     = new String(sheet.getCell(13, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
							String fabricante= new String(sheet.getCell(14, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
							double pesoEstimado= Numero.getDouble(sheet.getCell(15, fila).getContents()!= null? sheet.getCell(15, fila).getContents().replaceAll("[$, ]", ""): "0", 1D);
							long articuloTipo= Numero.getLong(sheet.getCell(16, fila).getContents()!= null? sheet.getCell(16, fila).getContents().replaceAll("[$, ]", ""): "1", 1L);
							String clase     = new String(sheet.getCell(17, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
							String nombre    = new String(contenido.getBytes(ISO_8859_1), UTF_8);
							if(costo>= 0 && menudeo>= 0 && medio>= 0 && mayoreo>= 0) {
                Long idTipoClse= this.toFindClase(sesion, clase);
								nombre= nombre.replaceAll(Constantes.CLEAN_ART, "").trim();
                fabricante= fabricante.replaceAll(Constantes.CLEAN_ART, "").trim();                
                codigo= new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								codigo= codigo.replaceAll(Constantes.CLEAN_ART, "").trim();
								if(codigo.length()> 0) {
									TcManticArticulosDto articulo= this.toFindArticulo(sesion, codigo, 1L);
									if(articulo!= null) {
										articulo.setNombre(nombre);
										articulo.setDescripcion(nombre);
										articulo.setPrecio(costo);
										articulo.setMenudeo(Numero.toAjustarDecimales(menudeo, articulo.getIdRedondear().equals(1L)));
										articulo.setMedioMayoreo(Numero.toAjustarDecimales(medio, articulo.getIdRedondear().equals(1L)));
										articulo.setMayoreo(Numero.toAjustarDecimales(mayoreo, articulo.getIdRedondear().equals(1L)));
										// si trae nulo, blanco o cero se respeta el valor que tiene el campo
										if(lmenudeo>= 0D)
											articulo.setLimiteMedioMayoreo(lmenudeo);
										if(lmenudeo>= 0D)
											articulo.setLimiteMayoreo(lmayoreo);
										if(minimo>= 0D)
											articulo.setMinimo(minimo);
										if(maximo>=0D)
											articulo.setMaximo(maximo);
										if(iva>=0D)
											articulo.setIva(iva< 1? iva* 100: iva);
                    if(!Cadena.isVacio(fabricante))
                      articulo.setFabricante(fabricante.replaceAll(Constantes.CLEAN_ART, "").trim());
										if(!Cadena.isVacio(sat))
											articulo.setSat(sat);
										if(pesoEstimado>= 0D) 
											articulo.setPesoEstimado(pesoEstimado);
                    if(!Objects.equals(clase, null) && clase.trim().length()> 0)
                      articulo.setIdTipoClase(idTipoClse);
										DaoFactory.getInstance().update(sesion, articulo);
									} // if
									else {
										articulo= new TcManticArticulosDto(
											nombre, // String descripcion, 
											"0", // String descuentos, 
											null, // Long idImagen, 
											null, // Long idCategoria, 
											"0", // String extras, 
											null, // String metaTag, 
											nombre, // String nombre, 
											costo, // Double precio, 
											iva, // Double iva, 
											Numero.toAjustarDecimales(mayoreo, costo<= 10), // Double mayoreo, 
											2D, // Double desperdicio, 
											null, // String metaTagDescipcion, 
											1L, // Long idVigente, 
											-1L, // Long idArticulo, 
											0D, // Double stock, 
											Numero.toAjustarDecimales(medio, costo<= 10), // Double medioMayoreo, 
											pesoEstimado, // Double pesoEstimado, 
											this.toFindUnidadMedida(sesion, sheet.getCell(7, fila).getContents()), // Long idEmpaqueUnidadMedida, 
											costo<= 10? 1L: 2L, // Long idRedondear, 
											Numero.toAjustarDecimales(menudeo, costo<= 10), // Double menudeo, 
											null, // String metaTagTeclado, 
											new Timestamp(Calendar.getInstance().getTimeInMillis()), // Timestamp fecha, 
											JsfBase.getIdUsuario(), //  Long idUsuario, 
											JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), // Long idEmpresa, 
											0D, // Double cantidad, 
											minimo== 0D? 100000D: minimo, // Double minimo, 
											maximo== 0D? 200000D: maximo, // Double maximo, 
											lmenudeo, // Double limiteMedioMayoreo, 
											lmayoreo, // Double limiteMayoreo, 
											Cadena.isVacio(sat)? Constantes.CODIGO_SAT: sat, // String sat, 
											articuloTipo, // Long idArticuloTipo, 
											2L, // Long idBarras, 
											"0", // String descuento, 
											"0", // String extra, 
											null, // String idFacturama
											2L, // String idDescontinuado
                      !Cadena.isVacio(fabricante)? fabricante.replaceAll(Constantes.CLEAN_ART, "").trim(): null, // String fabricante
                      2L, // Long idVerificado 
                      Numero.toAjustarDecimales(menudeo, costo<= 10), // especial
                      0D, // Double factor
                      idTipoClse // Long idTipoClase      
										);
										TcManticArticulosDto identico= this.toFindArticuloIdentico(sesion, articulo.toMap(), 1L);
										if(identico== null)
											DaoFactory.getInstance().insert(sesion, articulo);
										else {
                      identico.setNombre(nombre);
                      identico.setDescripcion(nombre);
  										if(minimo>= 0D)
 											  identico.setMinimo(minimo== 0D? 100000D: minimo);
  										if(maximo>= 0D)
		  									identico.setMaximo(maximo== 0D? 200000D: maximo);
  										if(lmenudeo>= 0D)
  											identico.setLimiteMedioMayoreo(lmenudeo);
  										if(lmayoreo>= 0D)
	   										identico.setLimiteMayoreo(lmayoreo);
											identico.setMenudeo(Numero.toAjustarDecimales(menudeo, identico.getIdRedondear().equals(1L)));
											identico.setMedioMayoreo(Numero.toAjustarDecimales(medio, identico.getIdRedondear().equals(1L)));
											identico.setMayoreo(Numero.toAjustarDecimales(mayoreo, identico.getIdRedondear().equals(1L)));
											identico.setEspecial(Numero.toAjustarDecimales(menudeo, identico.getIdRedondear().equals(1L)));
  										if(iva>= 0D)
  											identico.setIva(iva);
											identico.setPrecio(costo);
                      if(!Cadena.isVacio(fabricante))
                        identico.setFabricante(fabricante);
  										if(pesoEstimado>= 0D) 
                        identico.setPesoEstimado(pesoEstimado);
											DaoFactory.getInstance().update(sesion, identico);
											articulo.setIdArticulo(identico.getIdArticulo());
										} // if
										Long idPrincipal= 1L;
										if(this.toFindPrincipal(sesion, articulo.getIdArticulo(), articulo.getIdArticuloTipo()))
											idPrincipal= 2L;
										// insertar el codigo principal del articulo
										codigos= new TcManticArticulosCodigosDto(
											codigo, // String codigo, 
											null, // Long idProveedor, 
											JsfBase.getIdUsuario(), // Long idUsuario, 
											idPrincipal, // Long idPrincipal, 
											null, // String observaciones, 
											-1L, // Long idArticuloCodigo, 
											1L, // Long orden, 
											articulo.getIdArticulo() // Long idArticulo
										);
										DaoFactory.getInstance().insert(sesion, codigos);
										TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
											sheet.getCell(0, fila).getContents(), // String codigo, 
											-1L, // Long idMasivaDetalle, 
											this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
											"ESTE ARTICULO FUE AGREGADO ["+ sheet.getCell(2, fila).getContents()+ "]" // String observaciones
										);
										DaoFactory.getInstance().insert(sesion, detalle);
										// aqui va el codigo para que se registre en facturama el articulo
										// **
										// **
									} // if
									// buscar si el codigo auxiliar existe para este articulo, en caso de que no insertarlo
									codigo= new String(sheet.getCell(1, fila).getContents().getBytes(UTF_8), ISO_8859_1);
									codigo= codigo.replaceAll(Constantes.CLEAN_ART, "").trim();
                  if(!Cadena.isVacio(codigo)) {
                    Long auxiliar= this.toFindCodigoAuxiliar(sesion, codigo);
                    if(auxiliar< 0 && codigo.length()> 0) {
                      codigos= new TcManticArticulosCodigosDto(
                        codigo, // String codigo, 
                        null, // Long idProveedor, 
                        JsfBase.getIdUsuario(), // Long idUsuario, 
                        2L, // Long idPrincipal, 
                        null, // String observaciones, 
                        -1L, // Long idArticuloCodigo, 
                        this.toNextOrden(sesion, articulo.getIdArticulo()), // Long orden, 
                        articulo.getIdArticulo() // Long idArticulo
                      );
                      DaoFactory.getInstance().insert(sesion, codigos);
                    } // if
                  } // if
									// buscar si el codigo del fabricante existe para este articulo, en caso de que no insertarlo
                  if(!Cadena.isVacio(fabricante)) {
                    codigo= fabricante.replaceAll(Constantes.CLEAN_ART, "").trim();
                    TcManticArticulosCodigosDto clon= this.toFindCodigoFabricante(sesion, articulo.getIdArticulo(), Constantes.ID_PROVEEDOR_FABRICANTE);
                    if(clon== null) {
                      codigos= new TcManticArticulosCodigosDto(
                        codigo, // String codigo, 
                        Constantes.ID_PROVEEDOR_FABRICANTE, // Long idProveedor, 
                        JsfBase.getIdUsuario(), // Long idUsuario, 
                        2L, // Long idPrincipal, 
                        null, // String observaciones, 
                        -1L, // Long idArticuloCodigo, 
                        0L, // Long orden, 
                        articulo.getIdArticulo() // Long idArticulo
                      );
                      DaoFactory.getInstance().insert(sesion, codigos);
                    } // if
                    else 
                      if(!Objects.equals(clon.getCodigo(), codigo)) {
                        clon.setCodigo(codigo);
                        DaoFactory.getInstance().update(sesion, clon);
                      } // if  
                  } // if
								} // if codigo
								monitoreo.incrementar();
								if(fila% this.categoria.getTuplas()== 0) {
									if(bitacora== null) {
										bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, new Long(fila), 2L);
										DaoFactory.getInstance().insert(sesion, bitacora);
									} // if
									else {
										bitacora.setProcesados(new Long(fila));
										bitacora.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
										DaoFactory.getInstance().update(sesion, bitacora);
									} // else
									this.commit();
									this.procesados= fila;
								} // if
							} // if
							else {
								this.errores++;
								LOG.warn(fila+ ": ["+ nombre+ "] costo: ["+ costo+ "] menudeo: ["+ menudeo+ "] menudeo: ["+ medio+ "] menudeo: ["+ mayoreo+ "]");
								TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
									sheet.getCell(0, fila).getContents(), // String codigo, 
									-1L, // Long idMasivaDetalle, 
									this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
									"EL COSTO["+ costo+ "], MENUDEO["+ menudeo+ "], MEDIO MAYOREO["+ medio+ "], MAYOREO["+ mayoreo+ "] ESTAN EN CEROS" // String observaciones
								);
								DaoFactory.getInstance().insert(sesion, detalle);
							} // else	
							count++;
 							LOG.error("Procesando "+ fila+ " de "+ sheet.getRows()+ " ["+ sheet.getCell(0, fila).getContents()+ "]");
						} // if	
	//					if(fila> 500)
	//						throw new KajoolBaseException("Este error fue provocado intencionalmente !");
					} // try
					catch(Exception e) {
            LOG.error("[--->>> ["+ fila+ "] {"+ sheet.getCell(0, fila).getContents().toUpperCase()+ "} {"+ sheet.getCell(2, fila).getContents().toUpperCase()+ "} <<<---]");
						Error.mensaje(e);
					} // catch
					this.procesados= count;
					LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
				} // for
				if(bitacora== null) {
					bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  				DaoFactory.getInstance().insert(sesion, bitacora);
				} // if
			  else {
					bitacora.setProcesados(this.masivo.getTuplas());
					bitacora.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
					DaoFactory.getInstance().update(sesion, bitacora);
				} // if
				LOG.warn("Cantidad de filas con error son: "+ this.errores);
 				this.procesados= count;
				regresar= true;
			} // if
		} // try
    finally {
      if(workbook!= null) {
        workbook.close();
        workbook = null;
      } // if
    } // finally
		return regresar;
	} // toArticulos		

	private Long toNextOrden(Session sesion, Long idArticulo) throws Exception {
		Long regresar             = 1L;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idArticulo", idArticulo);
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticArticulosCodigosDto", "siguiente", params, "siguiente");
			if(next!= null && next.getData()!= null)
			  regresar= next.toLong();
		} // try
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toNextOrden

	private TcManticTrabajosDto toFindTrabajo(Session sesion, String codigo) {
		TcManticTrabajosDto regresar= null;
		Map<String, Object> params  = new HashMap<>();
		try {
			params.put("codigo", codigo);
			regresar= (TcManticTrabajosDto)DaoFactory.getInstance().toEntity(sesion, TcManticTrabajosDto.class, "VistaCargasMasivasDto", "trabajo", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindTrabajo
	
  private Boolean toRefacciones(Session sesion, File archivo) throws Exception {
		Boolean regresar	                 = false;
		Workbook workbook	                 = null;
		Sheet sheet                        = null;
		TcManticMasivasBitacoraDto bitacora= null;		
		Map<String, Object>params          = null;
		try {
      WorkbookSettings workbookSettings = new WorkbookSettings();
      workbookSettings.setEncoding("Cp1252");	
			workbookSettings.setExcelDisplayLanguage("MX");
      workbookSettings.setExcelRegionalSettings("MX");
      workbookSettings.setLocale(new Locale("es", "MX"));
			workbook= Workbook.getWorkbook(archivo, workbookSettings);
			sheet		= workbook.getSheet(0);
			Monitoreo monitoreo= JsfBase.getAutentifica().getMonitoreo();
			if(sheet != null && sheet.getColumns()>= this.categoria.getColumns() && sheet.getRows()>= 2) {
				//LOG.info("<-------------------------------------------------------------------------------------------------------------->");
				LOG.info("Filas del documento: "+ sheet.getRows());
				this.errores= 0;
				int count   = 0; 
				for(int fila= 1; fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
					try {
						if(sheet.getCell(0, fila)!= null && sheet.getCell(1, fila)!= null && sheet.getCell(3, fila)!= null && sheet.getCell(4, fila)!= null && !Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(1, fila).getContents()) && !Cadena.isVacio(sheet.getCell(3, fila).getContents()) && !Cadena.isVacio(sheet.getCell(4, fila).getContents())) {
							String contenido= new String(sheet.getCell(1, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
							String texto    = new String(sheet.getCell(2, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
              String sat      = sheet.getCell(6, fila).getContents()!= null? new String(sheet.getCell(6, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): "";
							//  0      1         2         3        4         5   6   7      8
							//CODIGO|NOMBRE|HERRAMIENTA|COSTO|PRECIO MENUDEO|IVA|SAT|RFC|CLAVE PROVEEDOR
						  String codigo= new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
							codigo= codigo.replaceAll(Constantes.CLEAN_ART, "").trim().toUpperCase();
							double costo  = Numero.getDouble(sheet.getCell(3, fila).getContents()!= null? sheet.getCell(3, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);						
							double menudeo= Numero.getDouble(sheet.getCell(4, fila).getContents()!= null? sheet.getCell(4, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);						
							double iva  = Numero.getDouble(sheet.getCell(5, fila).getContents()!= null? sheet.getCell(5, fila).getContents().replaceAll("[$, ]", ""): "0", 16D);						
							String nombre= new String(contenido.getBytes(ISO_8859_1), UTF_8);
							String herramienta= new String(texto.getBytes(ISO_8859_1), UTF_8);
              String rfc  = new String(sheet.getCell(7, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
              String clave= new String(sheet.getCell(8, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
              TcManticProveedoresDto proveedor= this.toFindProveedor(sesion, rfc, clave);
							if(proveedor!= null && costo> 0 && menudeo> 0) {
								nombre= nombre.replaceAll(Constantes.CLEAN_ART, "").trim().toUpperCase();
								TcManticRefaccionesDto refaccion= this.toFindRefaccion(sesion, codigo, proveedor.getIdProveedor());
                if(refaccion!= null) {
                  // si trae nulo, blanco o cero se respeta el valor que tiene el campo								
                  refaccion.setCosto(Numero.toRedondearSat(costo));
                  refaccion.setPrecio(Numero.toRedondearSat(menudeo));
                  if(!Cadena.isVacio(nombre))
                    refaccion.setNombre(nombre);
                  if(!Cadena.isVacio(herramienta))
                    refaccion.setHerramienta(herramienta);
                  if(iva!= 0D)
                    refaccion.setIva(iva<1? iva*100: iva);					
                  if(!Cadena.isVacio(sat))
                    refaccion.setSat(sat);
                  refaccion.setActualizado(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                  DaoFactory.getInstance().update(sesion, refaccion);
                } // if
                else {
                  refaccion= new TcManticRefaccionesDto(
                    JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), // Long idEmpresa, 
                    proveedor.getIdProveedor(), // Long idProveedor, 
                    codigo, // String codigo, 
                    herramienta, // String herramienta, 
                    Numero.toRedondearSat(costo), // Double costo, 
                    sat, // String sat, 
                    nombre, // String nombre, 
                    2L, // Long idDescontinuado, 
                    new Timestamp(Calendar.getInstance().getTimeInMillis()), // Timestamp actualizado, 
                    -1L, // Long idRefaccion, 
                    iva, // Double iva, 
                    JsfBase.getIdUsuario(), // Long idUsuario, 
                    null, // String fabricante, 
                    1L, // Long idVigente
                    Numero.toRedondearSat(menudeo) // precio      
                  );
                  DaoFactory.getInstance().insert(sesion, refaccion);
                } // else
                TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
                  codigo, // String codigo, 
                  -1L, // Long idMasivaDetalle, 
                  this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
                  "ESTA REFACCION FUE AGREGADO ["+ nombre + "]" // String observaciones
                );
                DaoFactory.getInstance().insert(sesion, detalle);
                monitoreo.incrementar();
                if(fila% this.categoria.getTuplas()== 0) {
                  if(bitacora== null) {
                    bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, new Long(fila), 2L);
                    DaoFactory.getInstance().insert(sesion, bitacora);
                  } // if
                  else {
                    bitacora.setProcesados(new Long(fila));
                    bitacora.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                    DaoFactory.getInstance().update(sesion, bitacora);
                  } // else
                  this.commit();
                  this.procesados= fila;
                } // if
              } // if
              else {
                this.errores++;
                LOG.warn(fila+ "codigo: ["+ codigo+ "] nombre: ["+ nombre+ "] costo: ["+ costo+ "] herramienta: ["+ herramienta+ "] sat: ["+ sat+ "]");
                TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
                  new String(sheet.getCell(0, fila).getContents().getBytes(UTF_8), ISO_8859_1), // String codigo, 
                  -1L, // Long idMasivaDetalle, 
                  this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
                  "EL CODIGO["+ codigo+ "], NOMBRE["+ nombre + "], COSTO["+ costo+ "], HERRAMIENTA["+ herramienta+ "] ESTAN EN CEROS" // String observaciones
                );
                DaoFactory.getInstance().insert(sesion, detalle);
              } // else	
            } // if
            count++;
					} // try
					catch(Exception e) {
            LOG.error("[--->>> ["+ fila+ "] {"+ sheet.getCell(0, fila).getContents().toUpperCase()+ "} {"+ sheet.getCell(2, fila).getContents().toUpperCase()+ "} {"+ sheet.getCell(3, fila).getContents().toUpperCase()+ "} <<<---]");
						Error.mensaje(e);
            throw e;
					} // catch
  				this.procesados= fila;
					LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
				} // for
				if(bitacora== null) {
					bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  				DaoFactory.getInstance().insert(sesion, bitacora);
				} // if
			  else {
					bitacora.setProcesados(this.masivo.getTuplas());
					bitacora.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
					DaoFactory.getInstance().update(sesion, bitacora);
				} // if
				LOG.warn("Cantidad de filas con error son: "+ this.errores);
 				this.procesados= count;
				regresar       = true;
			} // if
		} // try
    finally {
      if(workbook!= null) {
        workbook.close();
        workbook = null;
      } // if
      Methods.clean(params);
    } // finally
		return regresar;
	} // toRefacciones		
	
  private Boolean toServicios(Session sesion, File archivo) throws Exception {
		Boolean regresar	                 = false;
		Workbook workbook	                 = null;
		Sheet sheet                        = null;
		TcManticMasivasBitacoraDto bitacora= null;		
		Map<String, Object>params          = null;
		try {
      WorkbookSettings workbookSettings = new WorkbookSettings();
      workbookSettings.setEncoding("Cp1252");	
			workbookSettings.setExcelDisplayLanguage("MX");
      workbookSettings.setExcelRegionalSettings("MX");
      workbookSettings.setLocale(new Locale("es", "MX"));
			workbook= Workbook.getWorkbook(archivo, workbookSettings);
			sheet		= workbook.getSheet(0);
			Monitoreo monitoreo= JsfBase.getAutentifica().getMonitoreo();
			if(sheet != null && sheet.getColumns()>= this.categoria.getColumns() && sheet.getRows()>= 2) {
				//LOG.info("<-------------------------------------------------------------------------------------------------------------->");
				LOG.info("Filas del documento: "+ sheet.getRows());
				this.errores= 0;
				int count   = 0; 
				for(int fila= 1; fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
					try {
						if(sheet.getCell(0, fila)!= null && sheet.getCell(1, fila)!= null && sheet.getCell(2, fila)!= null && sheet.getCell(3, fila)!= null && !Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(1, fila).getContents()) && !Cadena.isVacio(sheet.getCell(2, fila).getContents()) && !Cadena.isVacio(sheet.getCell(3, fila).getContents())) {
              String codigo   = new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
              codigo= codigo.replaceAll(Constantes.CLEAN_ART, "").trim().toUpperCase();
							String contenido= new String(sheet.getCell(1, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
              String sat      = sheet.getCell(4, fila).getContents()!= null? new String(sheet.getCell(4, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): "";
              String linea    = sheet.getCell(5, fila).getContents()!= null? new String(sheet.getCell(5, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): "";
							//  0      1          2         3   4    5
							//CODIGO|NOMBRE|PRECIO MENUDEO|IVA|SAT|LINEA
							double costo= Numero.getDouble(sheet.getCell(2, fila).getContents()!= null? sheet.getCell(2, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);						
							double iva  = Numero.getDouble(sheet.getCell(3, fila).getContents()!= null? sheet.getCell(3, fila).getContents().replaceAll("[$, ]", ""): "0", 16D);						
							String nombre= new String(contenido.getBytes(ISO_8859_1), UTF_8);
							if(costo > 0) {
                costo= Numero.toRedondear(costo- (costo* ((iva< 1? iva* 100: iva)/ 100)));
								nombre= nombre.replaceAll(Constantes.CLEAN_ART, "").trim().toUpperCase();
								TcManticEncargosDto servicio= this.toFindServicio(sesion, codigo);
								if(servicio!= null) {
                  servicio.setCosto(0D);
                  servicio.setPrecio(Numero.toRedondearSat(costo));
                  servicio.setIva(iva<1? iva*100: iva);					
                  if(!Cadena.isVacio(nombre))
                    servicio.setNombre(nombre);
                  if(!Cadena.isVacio(linea))
                    servicio.setLinea(linea);
                  if(!Cadena.isVacio(sat))
                    servicio.setSat(sat);
                  servicio.setActualizado(new Timestamp(Calendar.getInstance().getTimeInMillis()));
									DaoFactory.getInstance().update(sesion, servicio);
								} // if
								else {
									servicio= new TcManticEncargosDto(
                    JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), // Long idEmpresa, 
                    codigo, // String codigo, 
                    0D, // Double costo, 
                    iva, // Double iva, 
                    sat, // String sat, 
                    JsfBase.getIdUsuario(), // Long idUsuario, 
                    -1L, // Long idEncargo, 
                    1L, // Long idVigente, 
                    nombre, // String nombre, 
                    2L, // Long idDescontinuado, 
                    linea, // String linea, 
                    new Timestamp(Calendar.getInstance().getTimeInMillis()), // Timestamp actualizado}
                    Numero.toRedondearSat(costo) // precio      
									);
									DaoFactory.getInstance().insert(sesion, servicio);
									TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
										codigo, // String codigo, 
										-1L, // Long idMasivaDetalle, 
										this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
										"ESTE SERVICIO FUE AGREGADO ["+ sheet.getCell(2, fila).getContents()+ "]" // String observaciones
									);
									DaoFactory.getInstance().insert(sesion, detalle);
								} // if
								monitoreo.incrementar();
								if(fila% this.categoria.getTuplas()== 0) {
									if(bitacora== null) {
										bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, new Long(fila), 2L);
										DaoFactory.getInstance().insert(sesion, bitacora);
									} // if
									else {
										bitacora.setProcesados(new Long(fila));
										bitacora.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
										DaoFactory.getInstance().update(sesion, bitacora);
									} // else
									this.commit();
									this.procesados= fila;
								} // if
							} // if
							else {
								this.errores++;
								LOG.warn(fila+ "codigo: ["+ codigo+ "] nombre: ["+ nombre+ "] linea: ["+ linea+ "] sat: ["+ sat+ "]");
								TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
									new String(sheet.getCell(0, fila).getContents().getBytes(UTF_8), ISO_8859_1), // String codigo, 
									-1L, // Long idMasivaDetalle, 
									this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
									"EL CODIGO["+ codigo+ "], NOMBRE["+ nombre+ "], LINEA["+ linea+ "], SAT["+ sat+ "] ESTAN EN CEROS" // String observaciones
								);
								DaoFactory.getInstance().insert(sesion, detalle);
							} // else	
							count++; 
						} // if	
					} // try
					catch(Exception e) {
            LOG.error("[--->>> ["+ fila+ "] {"+ sheet.getCell(0, fila).getContents().toUpperCase()+ "} {"+ sheet.getCell(2, fila).getContents().toUpperCase()+ "} <<<---]");
						Error.mensaje(e);
            throw e;
					} // catch
  				this.procesados= fila;
					LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
				} // for
				if(bitacora== null) {
					bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  				DaoFactory.getInstance().insert(sesion, bitacora);
				} // if
			  else {
					bitacora.setProcesados(this.masivo.getTuplas());
					bitacora.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
					DaoFactory.getInstance().update(sesion, bitacora);
				} // if
				LOG.warn("Cantidad de filas con error son: "+ this.errores);
 				this.procesados= count;
				regresar       = true;
			} // if
		} // try
    finally {
      if(workbook!= null) {
        workbook.close();
        workbook = null;
      } // if
      Methods.clean(params);
    } // finally
		return regresar;
	} // toServicios		
	
  private Boolean toEgresos(Session sesion, File archivo) throws Exception {
		Boolean regresar	                 = false;
		Workbook workbook	                 = null;
		Sheet sheet                        = null;
		TcManticMasivasBitacoraDto bitacora= null;		
		try {
      WorkbookSettings workbookSettings = new WorkbookSettings();
      workbookSettings.setEncoding("Cp1252");	
			workbookSettings.setExcelDisplayLanguage("MX");
      workbookSettings.setExcelRegionalSettings("MX");
      workbookSettings.setLocale(new Locale("es", "MX"));
			workbook= Workbook.getWorkbook(archivo, workbookSettings);
			sheet		= workbook.getSheet(0);
			Monitoreo monitoreo= JsfBase.getAutentifica().getMonitoreo();
			if(sheet != null && sheet.getColumns()>= this.categoria.getColumns() && sheet.getRows()>= 2) {
				//LOG.info("<-------------------------------------------------------------------------------------------------------------->");
				LOG.info("Filas del documento: "+ sheet.getRows());
				this.errores= 0;
				int fila    = 0; 
				int count   = 0; 
				for(fila= 1; fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
					try {
						if(sheet.getCell(0, fila)!= null && sheet.getCell(1, fila)!= null && sheet.getCell(2, fila)!= null && !Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(1, fila).getContents()) && !Cadena.isVacio(sheet.getCell(2, fila).getContents())) {
							String contenido= new String(sheet.getCell(1, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);						
							//  0      1         2
							//FECHA|DESCRIPCION|IMPORTE
							double importe= Numero.getDouble(sheet.getCell(2, fila).getContents()!= null? sheet.getCell(2, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);						
							String descripcion= new String(contenido.getBytes(ISO_8859_1), UTF_8);
							String fecha= sheet.getCell(0, fila).getContents();
							if(importe > 0) {
								TcManticEgresosDto egreso= this.toFindEgreso(sesion, descripcion);
								if(egreso!= null) {
									egreso.setIdUsuario(JsfBase.getIdUsuario());								
									egreso.setImporte(importe);
									egreso.setFecha(Fecha.toDateDefault(fecha));										
									DaoFactory.getInstance().update(sesion, egreso);
								} // if
								else {
									Siguiente consecutivo= this.toSiguiente(sesion);
									egreso= new TcManticEgresosDto(
										consecutivo.getConsecutivo(), // consecutivo
										descripcion, // descripcion
										Fecha.toDateDefault(fecha), // fecha
										1L, // idEgresoEstatus 
										-1L, // idEgreso
										JsfBase.getIdUsuario(), // idUsuario
										JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende(), // idEmpresa
										consecutivo.getOrden(), // orden
										importe, // importe
										new Long(Fecha.getAnioActual()) // ejercicio
									);
									DaoFactory.getInstance().insert(sesion, egreso);
									TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
										descripcion, // descripcion
										-1L, // Long idMasivaDetalle, 
										this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
										"ESTE EGRESO FUE AGREGADO ["+ descripcion+ "]" // String observaciones
									);
									DaoFactory.getInstance().insert(sesion, detalle);								
								} // if												
								monitoreo.incrementar();
								if(fila% this.categoria.getTuplas()== 0) {
									if(bitacora== null) {
										bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, new Long(fila), 2L);
										DaoFactory.getInstance().insert(sesion, bitacora);
									} // if
									else {
										bitacora.setProcesados(new Long(fila));
										bitacora.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
										DaoFactory.getInstance().update(sesion, bitacora);
									} // else
									this.commit();
									this.procesados= fila;
								} // if
							} // if
							else {
								this.errores++;
								TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
									sheet.getCell(0, fila).getContents(), // String codigo, 
									-1L, // Long idMasivaDetalle, 
									this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
									"EL IMPORTE[" + importe + "]" // String observaciones
								);
								DaoFactory.getInstance().insert(sesion, detalle);
							} // else	
							count++; 
						} // if	
					} // try
					catch(Exception e) {
            LOG.error("[--->>> ["+ fila+ "] {"+ sheet.getCell(0, fila).getContents().toUpperCase()+ "} {"+ sheet.getCell(2, fila).getContents().toUpperCase()+ "} <<<---]");
						Error.mensaje(e);
					} // catch
  				this.procesados= fila;
					LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
				} // for
				if(bitacora== null) {
					bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  				DaoFactory.getInstance().insert(sesion, bitacora);
				} // if
			  else {
					bitacora.setProcesados(this.masivo.getTuplas());
					bitacora.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
					DaoFactory.getInstance().update(sesion, bitacora);
				} // if
				LOG.warn("Cantidad de filas con error son: "+ this.errores);
 				this.procesados= this.masivo.getTuplas().intValue();
				regresar       = true;
			} // if
		} // try
    finally {
      if(workbook!= null) {
        workbook.close();
        workbook = null;
      } // if
    } // finally
		return regresar;
	} // toEgresos		
	
	private TcManticEgresosDto toFindEgreso(Session sesion, String descripcion) {
		TcManticEgresosDto regresar= null;
		Map<String, Object> params = new HashMap<>();
		try {
			params.put("descripcion", descripcion);
			regresar= (TcManticEgresosDto)DaoFactory.getInstance().toEntity(sesion, TcManticEgresosDto.class, "VistaCargasMasivasDto", "egresos", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindEgreso
	
	private void toProcessContactoCliente(Session sesion, String valor, Long idTipoContacto, Long idCliente) throws Exception {
		TrManticClienteTipoContactoDto contacto= this.toFindTipoContactoCliente(sesion, idTipoContacto, idCliente);
		if(contacto== null) {
      contacto= new TrManticClienteTipoContactoDto(
				idCliente, // Long idCliente, 
				JsfBase.getIdUsuario(), // Long idUsuario, 
				valor.trim(), // String valor, 
				"", // String observaciones, 
				-1L, // Long idClienteTipoContacto, 
				1L, // Long orden, 
				idTipoContacto, // Long idTipoContacto,
        2L // Long idPreferido
			);
			DaoFactory.getInstance().insert(sesion, contacto);
		} // if
		else {
			contacto.setValor(valor);
			DaoFactory.getInstance().update(sesion, contacto);
		} // else
	} // toProcessContactoCliente

  private Boolean toClientes(Session sesion, File archivo) throws Exception {	
		Boolean regresar = false;
		Workbook workbook= null;
		Sheet sheet      = null;
		TcManticMasivasBitacoraDto bitacora= null;
		try {
      WorkbookSettings workbookSettings = new WorkbookSettings();
      workbookSettings.setEncoding("Cp1252");	
			workbookSettings.setExcelDisplayLanguage("MX");
      workbookSettings.setExcelRegionalSettings("MX");
      workbookSettings.setLocale(new Locale("es", "MX"));
			workbook= Workbook.getWorkbook(archivo, workbookSettings);
			sheet		= workbook.getSheet(0);
			Monitoreo monitoreo= JsfBase.getAutentifica().getMonitoreo();
			if(sheet != null && sheet.getColumns()>= this.categoria.getColumns() && sheet.getRows()>= 2) {
				// LOG.info("<-------------------------------------------------------------------------------------------------------------->");
				LOG.info("Filas del documento: "+ sheet.getRows());
				this.errores= 0;
				int fila    = 0;
				int count   = 0;
				for(fila= 1; fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
					try {
						if(sheet.getCell(0, fila)!= null && sheet.getCell(2, fila)!= null && !sheet.getCell(0, fila).getContents().toUpperCase().startsWith("NOTA") && !Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(2, fila).getContents())) {
							String contenido= new String(sheet.getCell(2, fila).getContents().getBytes(UTF_8), ISO_8859_1);
							// 0    1       2          3       4       5       6       7       8        9         10    11       12        13
							//RFC|CLAVE|RAZONSOCIAL|USOCFDI|TELEFONO|CORREO1|CORREO2|ENTIDAD|MUNICIPIO|COLONIA|CALLE|NUMERO|CODIGOPOSTAL|REGIMEN
							String nombre= new String(contenido.toUpperCase().getBytes(ISO_8859_1), UTF_8);
							if(!Cadena.isVacio(sheet.getCell(5, fila).getContents()) && !Cadena.isVacio(sheet.getCell(7, fila).getContents()) && !Cadena.isVacio(sheet.getCell(8, fila).getContents())) {
								String rfc= new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								TcManticClientesDto cliente= this.toFindCliente(sesion, rfc);
								if(cliente!= null) {
									cliente.setRazonSocial(nombre);
									// si trae nulo, blanco o cero se respeta el valor que tiene el campo
									if(!Cadena.isVacio(sheet.getCell(1, fila).getContents()))
										cliente.setClave(sheet.getCell(1, fila).getContents().trim());
									if(!Cadena.isVacio(sheet.getCell(3, fila).getContents()))
										cliente.setIdUsoCfdi(this.toFindUsoCFDI(sesion, Cadena.isVacio(sheet.getCell(3, fila).getContents())? "XYZW": sheet.getCell(3, fila).getContents().trim()));
  								cliente.setIdRegimenFiscal(this.toFindRegimenFiscal(sesion, Cadena.isVacio(sheet.getCell(13, fila).getContents())? "XYZW": sheet.getCell(13, fila).getContents().trim()));
									DaoFactory.getInstance().update(sesion, cliente);
								} // if
								else {
									cliente= new TcManticClientesDto(
										Cadena.isVacio(sheet.getCell(1, fila).getContents())? null: sheet.getCell(1, fila).getContents().toUpperCase().trim(), // String clave, 
										0L, // Long plazoDias, 
										-1L, // Long idCliente, 
										0D, // Double limiteCredito, 
										2L, // Long idCredito, 
										nombre, // String razonSocial, 
										0D, // Double saldo, 
										rfc, // String rfc, 
										JsfBase.getIdUsuario(), // Long idUsuario, 
										this.toFindUsoCFDI(sesion, Cadena.isVacio(sheet.getCell(3, fila).getContents())? "XYZW": sheet.getCell(3, fila).getContents().trim()), // Long idUsoCfdi, 
										null, // String observaciones, 
										JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), // Long idEmpresa, 
										1L, // Long idTipoVenta, 
										null, // String idFacturama
                    0D, // Double especial 
                    this.toFindRegimenFiscal(sesion, Cadena.isVacio(sheet.getCell(13, fila).getContents())? "XYZW": sheet.getCell(13, fila).getContents().trim()), // Long idRegimenFiscal
                    1L, // Long idTipoCliente
                    null, // String paterno
                    null, // String materno
                    null // String grupo      
									);
									DaoFactory.getInstance().insert(sesion, cliente);

									Long idEntidad  = this.toFindEntidad(sesion, Cadena.isVacio(sheet.getCell(7, fila).getContents())? "XYZW": sheet.getCell(7, fila).getContents().toUpperCase().trim());
									Long idMunicipio= this.toFindMunicipio(sesion, idEntidad, Cadena.isVacio(sheet.getCell(8, fila).getContents())? "XYZW": sheet.getCell(8, fila).getContents().toUpperCase().trim());
									Long idLocalidad= this.toFindLocalidad(sesion, idMunicipio, "XYZW");
									TcManticDomiciliosDto domicilio= new TcManticDomiciliosDto(
										Cadena.isVacio(sheet.getCell(9, fila).getContents())? "COLONIA NO DEFINIDA": sheet.getCell(9, fila).getContents().toUpperCase().trim(), // String asentamiento, 
										idLocalidad, // Long idLocalidad, 
										Cadena.isVacio(sheet.getCell(12, fila).getContents())? "20000": sheet.getCell(12, fila).getContents().toUpperCase().trim(), // String codigoPostal, 
										null, // String latitud, 
										"", // String entreCalle, 
										Cadena.isVacio(sheet.getCell(10, fila).getContents())? "CALLE NO DEFINIDA": sheet.getCell(10, fila).getContents().toUpperCase().trim(), // String calle, 
										-1L, // Long idDomicilio, 
										Cadena.isVacio(sheet.getCell(12, fila).getContents())? "1": sheet.getCell(12, fila).getContents().toUpperCase().trim(), // String numeroInterior,  
										null, // String ycalle, 
										null, // String longitud, 
										null, // String numeroExterior, 
										JsfBase.getAutentifica()!= null? JsfBase.getIdUsuario(): 1L, // Long idUsuario, 
										"" // String observaciones
									);
									DaoFactory.getInstance().insert(sesion, domicilio);
									TrManticClienteDomicilioDto particular= new TrManticClienteDomicilioDto(
										cliente.getIdCliente(), // Long idCliente, 
										-1L, // Long idClienteDomicilio, 
										JsfBase.getIdUsuario(), // Long idUsuario, 
										1L, // Long idTipoDomicilio, 
										domicilio.getIdDomicilio(), // Long idDomicilio, 
										1L, // Long idPrincipal, 
										"" // String observaciones
									);
									DaoFactory.getInstance().insert(sesion, particular);
									particular= new TrManticClienteDomicilioDto(
										cliente.getIdCliente(), // Long idCliente, 
										-1L, // Long idClienteDomicilio, 
										JsfBase.getIdUsuario(), // Long idUsuario, 
										2L, // Long idTipoDomicilio, 
										domicilio.getIdDomicilio(), // Long idDomicilio, 
										1L, // Long idPrincipal, 
										"" // String observaciones
									);
									DaoFactory.getInstance().insert(sesion, particular);
									// aqui va el codigo para que se registre en facturama el cliente
									// **
									// **
								} // if
								// telefono
								if(!Cadena.isVacio(sheet.getCell(4, fila).getContents())) 
									this.toProcessContactoCliente(sesion, sheet.getCell(4, fila).getContents(), 1L, cliente.getIdCliente());
								// correos
								if(!Cadena.isVacio(sheet.getCell(5, fila).getContents())) 
									this.toProcessContactoCliente(sesion, sheet.getCell(5, fila).getContents(), 9L, cliente.getIdCliente());
								if(!Cadena.isVacio(sheet.getCell(6, fila).getContents())) 
									this.toProcessContactoCliente(sesion, sheet.getCell(6, fila).getContents(), 10L, cliente.getIdCliente());
								monitoreo.incrementar();
								if(fila% this.categoria.getTuplas()== 0) {
									if(bitacora== null) {
										bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, new Long(fila), 2L);
										DaoFactory.getInstance().insert(sesion, bitacora);
									} // if
									else {
										bitacora.setProcesados(new Long(fila));
										bitacora.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
										DaoFactory.getInstance().update(sesion, bitacora);
									} // else
									this.commit();
									this.procesados= fila; 
								} // if
								count++;
							} // if
							else {
								this.errores++;
								LOG.warn(fila+ ": ["+ nombre+ "]  LOS DATOS DE ESTA FILA ESTAN EN BLANCO");
								TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
									sheet.getCell(0, fila).getContents(), // String rfc, 
									-1L, // Long idMasivaDetalle, 
									this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
									"EL ["+ sheet.getCell(2, fila).getContents()+ "] ALGUNOS DE SUS DATOS ESTAN VACIOS, CORREO, ENTIDAD, MUNICIPIO" // String observaciones
								);
								DaoFactory.getInstance().insert(sesion, detalle);
							} // else	
						} // if	
					} // try
					catch(Exception e) {
            LOG.error("[--->>> ["+ fila+ "] {"+ sheet.getCell(0, fila).getContents().toUpperCase()+ "} {"+ sheet.getCell(2, fila).getContents().toUpperCase()+ "} <<<---]");
						Error.mensaje(e);
					} // catch
					this.procesados= count; 
					LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
 				} // for
				if(bitacora== null) {
					bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  				DaoFactory.getInstance().insert(sesion, bitacora);
				} // if
			  else {
					bitacora.setProcesados(this.masivo.getTuplas());
					bitacora.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
					DaoFactory.getInstance().update(sesion, bitacora);
				} // if
				LOG.warn("Cantidad de filas con error son: "+ this.errores);
 				this.procesados= this.masivo.getTuplas().intValue();
				regresar       = true;
			} // if
		} // try
    finally {
      if(workbook!= null) {
        workbook.close();
        workbook = null;
      } // if
    } // finally
		return regresar;
	} // toClientes

	private void toProcessContactoProveedor(Session sesion, String valor, Long idTipoContacto, Long idProveedor, Long orden) throws Exception {
		TrManticProveedorTipoContactoDto contacto= this.toFindTipoContactoProveedor(sesion, idTipoContacto, idProveedor);
		if(contacto== null) {
      contacto= new TrManticProveedorTipoContactoDto(
				idProveedor, // Long idProveedor, 
				JsfBase.getIdUsuario(), // Long idUsuario, 
				valor.trim(), // String valor, 
				"", // String observaciones, 
				orden, // Long orden, 
				-1L, // Long idProveedorTipoContacto, 
				idTipoContacto, // Long idTipoContacto
        1L // idPreferido
			);
			DaoFactory.getInstance().insert(sesion, contacto);
		} // if
		else {
			contacto.setValor(valor);
			DaoFactory.getInstance().update(sesion, contacto);
		} // else
	} // toProcessContactoProveedor

	
  private Boolean toProveedores(Session sesion, File archivo) throws Exception {	
		Boolean regresar = false;
		Workbook workbook= null;
		Sheet sheet      = null;
		TcManticMasivasBitacoraDto bitacora= null;
		try {
      WorkbookSettings workbookSettings = new WorkbookSettings();
      workbookSettings.setEncoding("Cp1252");	
			workbookSettings.setExcelDisplayLanguage("MX");
      workbookSettings.setExcelRegionalSettings("MX");
      workbookSettings.setLocale(new Locale("es", "MX"));
			workbook= Workbook.getWorkbook(archivo, workbookSettings);
			sheet		= workbook.getSheet(0);
			Monitoreo monitoreo= JsfBase.getAutentifica().getMonitoreo();
			if(sheet != null && sheet.getColumns()>= this.categoria.getColumns() && sheet.getRows()>= 2) {
				//LOG.info("<-------------------------------------------------------------------------------------------------------------->");
				LOG.info("Filas del documento: "+ sheet.getRows());
				this.errores= 0;
				int fila    = 0; 
				int count   = 0;
				for(fila= 1; fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
					try {
						if(sheet.getCell(0, fila)!= null && sheet.getCell(2, fila)!= null && !sheet.getCell(0, fila).getContents().toUpperCase().startsWith("NOTA") && !Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(2, fila).getContents())) {
							String contenido= new String(sheet.getCell(2, fila).getContents().getBytes(UTF_8), ISO_8859_1);
							// 0    1       2          3       4       5       6        7        8       9     10      11
							//RFC|CLAVE|RAZONSOCIAL|TELEFONO|CORREO1|CORREO2|ENTIDAD|MUNICIPIO|COLONIA|CALLE|NUMERO|CODIGOPOSTAL
							String nombre= new String(contenido.toUpperCase().getBytes(ISO_8859_1), UTF_8);
							if(!Cadena.isVacio(sheet.getCell(4, fila).getContents()) && !Cadena.isVacio(sheet.getCell(6, fila).getContents()) && !Cadena.isVacio(sheet.getCell(7, fila).getContents())) {
								String rfc= new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								TcManticProveedoresDto proveedor= this.toFindProveedor(sesion, rfc, rfc);
								if(proveedor!= null) {
									proveedor.setRazonSocial(nombre);
									// si trae nulo, blanco o cero se respeta el valor que tiene el campo
									if(!Cadena.isVacio(sheet.getCell(1, fila).getContents()))
										proveedor.setClave(sheet.getCell(1, fila).getContents().trim());
									DaoFactory.getInstance().update(sesion, proveedor);
								} // if
								else {
									proveedor= new TcManticProveedoresDto(
										1L, // Long idTipoProveedor, 
										-1L, // Long idProveedor, 
										Cadena.isVacio(sheet.getCell(1, fila).getContents())? null: sheet.getCell(1, fila).getContents().toUpperCase().trim(), // String clave, 
										30L, // Long diasEntrega, 
										"0", // String descuento, 
										rfc.substring(0, 4), // String prefijo, 
										nombre, // String razonSocial, 
										rfc, // String rfc, 
										1L, // Long idTipoDia, 
										JsfBase.getIdUsuario(), // Long idUsuario, 
										1L, // Long idTipoMoneda, 
										null, // String observaciones, 
										JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), // Long idEmpresa, 
										null // String comentarios
									);
									DaoFactory.getInstance().insert(sesion, proveedor);

									Long idEntidad  = this.toFindEntidad(sesion, Cadena.isVacio(sheet.getCell(7, fila).getContents())? "XYZW": sheet.getCell(7, fila).getContents().toUpperCase().trim());
									Long idMunicipio= this.toFindMunicipio(sesion, idEntidad, Cadena.isVacio(sheet.getCell(8, fila).getContents())? "XYZW": sheet.getCell(8, fila).getContents().toUpperCase().trim());
									Long idLocalidad= this.toFindLocalidad(sesion, idMunicipio, "XYZW");
									// 0    1       2          3       4       5       6        7        8       9     10      11
									//RFC|CLAVE|RAZONSOCIAL|TELEFONO|CORREO1|CORREO2|ENTIDAD|MUNICIPIO|COLONIA|CALLE|NUMERO|CODIGOPOSTAL
									TcManticDomiciliosDto domicilio= new TcManticDomiciliosDto(
										Cadena.isVacio(sheet.getCell(8, fila).getContents())? "COLONIA NO DEFINIDA": sheet.getCell(8, fila).getContents().toUpperCase().trim(), // String asentamiento, 
										idLocalidad, // Long idLocalidad, 
										Cadena.isVacio(sheet.getCell(11, fila).getContents())? "20000": sheet.getCell(11, fila).getContents().toUpperCase().trim(), // String codigoPostal, 
										null, // String latitud, 
										null, // String entreCalle, 
										Cadena.isVacio(sheet.getCell(9, fila).getContents())? "CALLE NO DEFINIDA": sheet.getCell(9, fila).getContents().toUpperCase().trim(), // String calle, 
										-1L, // Long idDomicilio, 
										Cadena.isVacio(sheet.getCell(10, fila).getContents())? "1": sheet.getCell(10, fila).getContents().toUpperCase().trim(), // String numeroInterior,  
										null, // String ycalle, 
										null, // String longitud, 
										null, // String numeroExterior, 
										JsfBase.getAutentifica()!= null? JsfBase.getIdUsuario(): 1L, // Long idUsuario, 
										"" // String observaciones
									);
									DaoFactory.getInstance().insert(sesion, domicilio);
									TrManticProveedorDomicilioDto particular= new TrManticProveedorDomicilioDto(
										-1L, // Long idProveedorDomicilio, 
										proveedor.getIdProveedor(), // Long idProveedor, 
										JsfBase.getIdUsuario(), // Long idUsuario, 
										1L, // Long idTipoDomicilio, 
										domicilio.getIdDomicilio(), // Long idDomicilio, 
										1L, // Long idPrincipal, 
										"" // String observaciones
									);
									DaoFactory.getInstance().insert(sesion, particular);
									particular= new TrManticProveedorDomicilioDto(
										-1L, // Long idProveedorDomicilio, 
										proveedor.getIdProveedor(), // Long idProveedor, 
										JsfBase.getIdUsuario(), // Long idUsuario, 
										2L, // Long idTipoDomicilio, 
										domicilio.getIdDomicilio(), // Long idDomicilio, 
										1L, // Long idPrincipal, 
										"" // String observaciones
									);
									DaoFactory.getInstance().insert(sesion, particular);
                  TrManticProveedorPagoDto condicion= new TrManticProveedorPagoDto(
                    -1L, // Long idProveedorPago, 
                    proveedor.getIdProveedor(), // Long idProveedor, 
                    "TRANSFERENCIA", // String clave, 
                    2L, // Long idTipoPago, 
                    JsfBase.getIdUsuario(), // Long idUsuario, 
                    "0.00", // String descuento, 
                    null, // String observaciones, 
                    30L // Long plazo      
                  );
									DaoFactory.getInstance().insert(sesion, condicion);
								} // if
								// telefono
								if(!Cadena.isVacio(sheet.getCell(3, fila).getContents())) 
									this.toProcessContactoProveedor(sesion, sheet.getCell(3, fila).getContents(), 1L, proveedor.getIdProveedor(), 1L);
								// correos
								if(!Cadena.isVacio(sheet.getCell(4, fila).getContents())) 
									this.toProcessContactoProveedor(sesion, sheet.getCell(4, fila).getContents(), 9L, proveedor.getIdProveedor(), 2L);
								if(!Cadena.isVacio(sheet.getCell(5, fila).getContents())) 
									this.toProcessContactoProveedor(sesion, sheet.getCell(5, fila).getContents(), 10L, proveedor.getIdProveedor(), 3L);
								monitoreo.incrementar();
								if(fila% this.categoria.getTuplas()== 0) {
									if(bitacora== null) {
										bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, new Long(fila), 2L);
										DaoFactory.getInstance().insert(sesion, bitacora);
									} // if
									else {
										bitacora.setProcesados(new Long(fila));
										bitacora.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
										DaoFactory.getInstance().update(sesion, bitacora);
									} // else
									this.commit();
									this.procesados= fila; 
								} // if
								count++;
							} // if
							else {
								this.errores++;
								LOG.warn(fila+ ": ["+ nombre+ "]  LOS DATOS DE ESTA FILA ESTAN EN BLANCO");
								TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
									sheet.getCell(0, fila).getContents(), // String rfc, 
									-1L, // Long idMasivaDetalle, 
									this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
									"EL ["+ sheet.getCell(2, fila).getContents()+ "] ALGUNOS DE SUS DATOS ESTAN VACIOS, CORREO, ENTIDAD, MUNICIPIO" // String observaciones
								);
								DaoFactory.getInstance().insert(sesion, detalle);
							} // else	
						} // if	
					} // try
					catch(Exception e) {
            LOG.error("[--->>> ["+ fila+ "] {"+ sheet.getCell(0, fila).getContents().toUpperCase()+ "} {"+ sheet.getCell(2, fila).getContents().toUpperCase()+ "} <<<---]");
						Error.mensaje(e);
					} // catch
					this.procesados= count; 
					LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
				} // for
				if(bitacora== null) {
					bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  				DaoFactory.getInstance().insert(sesion, bitacora);
				} // if
			  else {
					bitacora.setProcesados(this.masivo.getTuplas());
					bitacora.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
					DaoFactory.getInstance().update(sesion, bitacora);
				} // if
				LOG.warn("Cantidad de filas con error son: "+ this.errores);
 				this.procesados= this.masivo.getTuplas().intValue();
				regresar       = true;
			} // if
		} // try
    finally {
      if(workbook!= null) {
        workbook.close();
        workbook = null;
      } // if
    } // finally
		return regresar;
	} // toProveedores
	
  private Boolean toConteos(Session sesion, File archivo) throws Exception {
		Boolean regresar	      = false;
		Workbook workbook	      = null;
		Sheet sheet             = null;
		TcManticMasivasBitacoraDto bitacora= null;
		try {
      WorkbookSettings workbookSettings = new WorkbookSettings();
      workbookSettings.setEncoding("Cp1252");	
			workbookSettings.setExcelDisplayLanguage("MX");
      workbookSettings.setExcelRegionalSettings("MX");
      workbookSettings.setLocale(new Locale("es", "MX"));
			workbook= Workbook.getWorkbook(archivo, workbookSettings);
			sheet		= workbook.getSheet(0);
			Monitoreo monitoreo= JsfBase.getAutentifica().getMonitoreo();
			if(sheet != null && sheet.getColumns()>= this.categoria.getColumns() && sheet.getRows()>= 2) {
				//LOG.info("<-------------------------------------------------------------------------------------------------------------->");
				LOG.info("Filas del documento: "+ sheet.getRows());
				this.errores= 0;
				int count   = 0; 
				for(int fila= 1; fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
					try {
						if(sheet.getCell(0, fila)!= null && sheet.getCell(2, fila)!= null && !sheet.getCell(0, fila).getContents().toUpperCase().startsWith("NOTA") && !Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(2, fila).getContents())) {
							String contenido= new String(sheet.getCell(2, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
							//   0       1      2        3          4         5
							//ALMACEN|CODIGO|NOMBRE|STOCKMINIMO|STOCKMAXIMO|STOCK
  						String codigo = new String(sheet.getCell(1, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
							codigo= codigo.replaceAll(Constantes.CLEAN_ART, "").trim();
							String almacen= new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
							double minimo = Numero.getDouble(sheet.getCell(3, fila).getContents()!= null? sheet.getCell(3, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
							double maximo = Numero.getDouble(sheet.getCell(4, fila).getContents()!= null? sheet.getCell(4, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
							double stock  = Numero.getDouble(sheet.getCell(5, fila).getContents()!= null? sheet.getCell(5, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
							String nombre = new String(contenido.getBytes(ISO_8859_1), UTF_8);
							if(codigo.length()> 0 && almacen.length()> 0 && stock> 0) {
								TcManticArticulosDto articulo= this.toFindArticulo(sesion, codigo, 1L);
								Long idAlmacen= this.toFindAlmacen(sesion, almacen);
								if(articulo!= null && idAlmacen> 0L) {
									TcManticAlmacenesArticulosDto ubicacion= this.toFindAlmacenArticulo(sesion, idAlmacen, articulo.getIdArticulo());
									// double cantidad= stock- ubicacion.getStock();
									if(minimo!= 0D) 
										ubicacion.setMinimo(minimo);
									if(maximo!= 0D)
										ubicacion.setMaximo(maximo);
									ubicacion.setStock(stock);
									TcManticInventariosDto inventario= this.toFindInventario(sesion, idAlmacen, articulo.getIdArticulo(), stock);
                  this.toAffectAlmacenes(sesion, ubicacion, inventario);
									if(minimo!= 0D) 
										articulo.setMinimo(minimo);
									if(maximo!= 0D)
										articulo.setMaximo(maximo);
									sesion.flush();
							   	articulo.setStock(this.toSumAlmacenArticulo(sesion, articulo.getIdArticulo()));
									DaoFactory.getInstance().update(sesion, articulo);
								} // if
								TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
									sheet.getCell(0, fila).getContents(), // String codigo, 
									-1L, // Long idMasivaDetalle, 
									this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
									"ESTE ARTICULO FUE AGREGADO ["+ sheet.getCell(2, fila).getContents()+ "]" // String observaciones
								);
								DaoFactory.getInstance().insert(sesion, detalle);
								monitoreo.incrementar();
								if(fila% this.categoria.getTuplas()== 0) {
									if(bitacora== null) {
										bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, new Long(fila), 2L);
										DaoFactory.getInstance().insert(sesion, bitacora);
									} // if
									else {
										bitacora.setProcesados(new Long(fila));
										bitacora.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
										DaoFactory.getInstance().update(sesion, bitacora);
									} // else
									this.commit();
									this.procesados= fila;
									LOG.warn("Realizando proceso de commit en la fila "+ this.procesados);
								} // if
							} // if
							else {
								this.errores++;
								LOG.warn(fila+ "almacen: ["+ almacen+ "] codigo: ["+ codigo+ "] nombre: ["+ nombre+ "] minimo: ["+ minimo+ "] maximo: ["+ maximo+ "] stock: ["+ stock+ "]");
								TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
									sheet.getCell(0, fila).getContents(), // String codigo, 
									-1L, // Long idMasivaDetalle, 
									this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
									"EL ALMACEN["+ almacen+ "], CODIGO["+ codigo+ "], MINIMO["+ minimo+ "], MAXIMO["+ maximo+ "], STOCK["+ stock+ "] ESTAN EN CEROS" // String observaciones
								);
								DaoFactory.getInstance().insert(sesion, detalle);
							} // else	
							count++;
						} // if	
	//					if(fila> 500)
	//						throw new KajoolBaseException("Este error fue provocado intencionalmente !");
					} // try
					catch(Exception e) {
            LOG.error("[--->>> ["+ fila+ "] {"+ sheet.getCell(0, fila).getContents().toUpperCase()+ "} {"+ sheet.getCell(2, fila).getContents().toUpperCase()+ "} <<<---]");
						Error.mensaje(e);
					} // catch
					this.procesados= count;
					LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
				} // for
				if(bitacora== null) {
					bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  				DaoFactory.getInstance().insert(sesion, bitacora);
				} // if
			  else {
					bitacora.setProcesados(this.masivo.getTuplas());
					bitacora.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
					DaoFactory.getInstance().update(sesion, bitacora);
				} // if
				LOG.warn("Cantidad de filas con error son: "+ this.errores);
 				this.procesados= count;
				regresar= true;
			} // if
		} // try
    finally {
      if(workbook!= null) {
        workbook.close();
        workbook = null;
      } // if
    } // finally
		return regresar;
	} // toArticulos		
	
	private void toAffectAlmacenes(Session sesion, TcManticAlmacenesArticulosDto almacen, TcManticInventariosDto inventario) throws Exception {
		Map<String, Object> params= null;
		double stock              = inventario.getInicial();
		try {
			params=new HashMap<>();
			almacen.setStock(inventario.getInicial());
			if(almacen.isValid()) 
				DaoFactory.getInstance().update(sesion, almacen);
			else {
				params.put("idAlmacen", inventario.getIdAlmacen());
				params.put("idArticulo", inventario.getIdArticulo());
				TcManticAlmacenesArticulosDto ubicacion= (TcManticAlmacenesArticulosDto)DaoFactory.getInstance().findFirst(sesion, TcManticAlmacenesArticulosDto.class, params, "ubicacion");
				if(ubicacion== null) {
					TcManticAlmacenesUbicacionesDto general= (TcManticAlmacenesUbicacionesDto)DaoFactory.getInstance().findFirst(sesion, TcManticAlmacenesUbicacionesDto.class, params, "general");
					if(general== null) {
						general= new TcManticAlmacenesUbicacionesDto("GENERAL", "", "GENERAL", "", "", JsfBase.getAutentifica().getPersona().getIdUsuario(), inventario.getIdAlmacen(), -1L);
						DaoFactory.getInstance().insert(sesion, general);
					} // if	
					almacen.setIdAlmacenUbicacion(general.getIdAlmacenUbicacion());
					DaoFactory.getInstance().insert(sesion, almacen);
				} // if		
			} // else	
			if(inventario.isValid()) 
				DaoFactory.getInstance().update(sesion, inventario);
			else 
				DaoFactory.getInstance().insert(sesion, inventario);
			// generar un registro en la bitacora de movimientos de los articulos 
			TcManticMovimientosDto movimiento= new TcManticMovimientosDto(
				"VER", // String consecutivo, 
				6L, // Long idTipoMovimiento, 
				JsfBase.getIdUsuario(), // Long idUsuario, 
				almacen.getIdAlmacen(), // Long idAlmacen, 
				-1L, // Long idMovimiento, 
				0D, // Double cantidad, 
				inventario.getIdArticulo(), // Long idArticulo, 
				stock, // Double stock, 
				Numero.toRedondearSat(stock), // Double calculo
				null // String observaciones
			);
			DaoFactory.getInstance().insert(sesion, movimiento);
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
	public void toDeleteXls() throws Exception {
		List<TcManticMasivasArchivosDto> list= (List<TcManticMasivasArchivosDto>)DaoFactory.getInstance().findViewCriteria(TcManticMasivasArchivosDto.class, this.masivo.toMap(), "all");
		if(list!= null)
			for (TcManticMasivasArchivosDto item: list) {
				LOG.info("Catalogo importado: "+ item.getIdMasivaArchivo()+ " delete file: "+ item.getAlias());
				File file= new File(item.getAlias());
				file.delete();
			} // for
	}	// toDeleteXls 	
	
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticEgresosDto", "siguiente", params, "siguiente");
			if(next.getData()!= null)
			  regresar= new Siguiente(next.toLong());
			else
			  regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L);
		} // try
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}

  private Double toSumAlmacenArticulo(Session sesion, Long idArticulo) {
		Double regresar           = 0D;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put("idArticulo", idArticulo);
			Value value= DaoFactory.getInstance().toField(sesion, "VistaKardexDto", "existencias", params, "total");
			if(value!= null && value.getData()!= null)
				regresar= value.toDouble();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toSumAlmacenArticulo

	private TcManticArticulosDto toFindArticuloCodigo(Session sesion, String codigo) {
		TcManticArticulosDto regresar= null;
		Map<String, Object> params   = new HashMap<>();
		try {
			params.put("codigo", codigo);
			regresar= (TcManticArticulosDto)DaoFactory.getInstance().toEntity(sesion, TcManticArticulosDto.class, "VistaCargasMasivasDto", "codigo", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindArticulo

	private TcManticArticulosCodigosDto toExistsArticuloCodigo(Session sesion, Long idArticulo, String codigo) {
		TcManticArticulosCodigosDto regresar= null;
		Map<String, Object> params          = new HashMap<>();
		try {
			params.put("codigo", codigo);
			params.put("idArticulo", idArticulo);
			regresar= (TcManticArticulosCodigosDto)DaoFactory.getInstance().toEntity(sesion, TcManticArticulosCodigosDto.class, "VistaCargasMasivasDto", "encontrado", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toExistsArticuloCodigo
		
	
  private Boolean toCodigos(Session sesion, File archivo) throws Exception {
		Boolean regresar	      = false;
		Workbook workbook	      = null;
		Sheet sheet             = null;
		TcManticProveedoresDto proveedor   = null;
		TcManticMasivasBitacoraDto bitacora= null;
		Map<String, Object> params         = new HashMap<>();
		try {
      WorkbookSettings workbookSettings = new WorkbookSettings();
      workbookSettings.setEncoding("Cp1252");	
			workbookSettings.setExcelDisplayLanguage("MX");
      workbookSettings.setExcelRegionalSettings("MX");
      workbookSettings.setLocale(new Locale("es", "MX"));
			workbook= Workbook.getWorkbook(archivo, workbookSettings);
			sheet		= workbook.getSheet(0);
			Monitoreo monitoreo= JsfBase.getAutentifica().getMonitoreo();
			if(sheet != null && sheet.getColumns()>= this.categoria.getColumns() && sheet.getRows()>= 2) {
				//LOG.info("<-------------------------------------------------------------------------------------------------------------->");
				LOG.info("Filas del documento: "+ sheet.getRows());
				this.errores= 0;
				int count   = 0; 
				for(int fila= 1; fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
					try {
						if(sheet.getCell(0, fila)!= null && sheet.getCell(1, fila)!= null && sheet.getCell(2, fila)!= null && !sheet.getCell(0, fila).getContents().toUpperCase().startsWith("NOTA") && !Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(1, fila).getContents()) && !Cadena.isVacio(sheet.getCell(2, fila).getContents())) {
							String contenido= new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
							//   0            1             2							3
							//PROVEEDOR|CODIGO PROPIO|CODIGO PROVEEDOR|MULTIPLO
  						String codigo = new String(sheet.getCell(1, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
							codigo= codigo.replaceAll(Constantes.CLEAN_ART, "").trim();
							String auxiliar= new String(sheet.getCell(2, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
							auxiliar= auxiliar.replaceAll(Constantes.CLEAN_ART, "").trim();
							long multiplo= Numero.getLong(sheet.getCell(3, fila).getContents()!= null? sheet.getCell(3, fila).getContents().replaceAll("[$, ]", ""): "0", 1L);
							String rfc = new String(contenido.getBytes(ISO_8859_1), UTF_8);
							if(codigo.length()> 0 && auxiliar.length()> 0) {
								TcManticArticulosDto articulo= this.toFindArticuloCodigo(sesion, codigo);
								if(articulo!=null && (proveedor==null || !Objects.equals(rfc, proveedor.getRfc())))
								  proveedor= this.toFindProveedor(sesion, rfc, rfc);
								if(articulo!= null && proveedor!= null && (Objects.equals(this.idProveedor, proveedor.getIdProveedor()) || Objects.equals(proveedor.getIdProveedor(), Constantes.ID_PROVEEDOR_FABRICANTE))) {
                  TcManticArticulosCodigosDto update= null;
                  if(Objects.equals(proveedor.getIdProveedor(), Constantes.ID_PROVEEDOR_FABRICANTE))
                    update= this.toFindCodigoFabricante(sesion, articulo.getIdArticulo(), Constantes.ID_PROVEEDOR_FABRICANTE);
                  else  
									  update= this.toExistsArticuloCodigo(sesion, articulo.getIdArticulo(), auxiliar);
									if(update== null) {
										TcManticArticulosCodigosDto adicional= new TcManticArticulosCodigosDto(
											auxiliar, // String codigo
											proveedor.getIdProveedor(),  // Long idProveedor
											JsfBase.getIdUsuario(), // Long idUsuario
											2L, // Long idPrincipal
											null, // String observaciones
											-1L, // Long idArticuloCodigo
											Objects.equals(proveedor.getIdProveedor(), Constantes.ID_PROVEEDOR_FABRICANTE)? 0L: this.toNextOrden(sesion, articulo.getIdArticulo()), // Long orden 
											articulo.getIdArticulo(), // Long idArticulo
											Objects.equals(proveedor.getIdProveedor(), Constantes.ID_PROVEEDOR_FABRICANTE)? 0L: multiplo, // multiplo
											null // nombre
										); 
									  DaoFactory.getInstance().insert(sesion, adicional);
                    // SI ES EL CODIGO DEL FABRICANTE ACTUALIZAR EL CATALOGO DE ARTICULOS
                    if(Objects.equals(proveedor.getIdProveedor(), Constantes.ID_PROVEEDOR_FABRICANTE)) {
                      articulo.setFabricante(auxiliar);
                      DaoFactory.getInstance().update(sesion, articulo);
                    } // if
									  TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
											sheet.getCell(0, fila).getContents(), // String codigo, 
											-1L, // Long idMasivaDetalle, 
											this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
											"ESTE ARTICULO FUE AGREGADO ["+ sheet.getCell(2, fila).getContents()+ "]" // String observaciones
										);
										DaoFactory.getInstance().insert(sesion, detalle);
									} // if	
									else {
                    if(Objects.equals(update.getIdProveedor(), Constantes.ID_PROVEEDOR_FABRICANTE)) {
                      // SI ES EL CODIFO DE FABRICANTE Y EL REGISTRO YA EXISTE ENTONCES ACTUALIZARLO
                      if(!Objects.equals(update.getCodigo(), auxiliar)) {
                        update.setCodigo(auxiliar);
                        DaoFactory.getInstance().update(sesion, update);
                        articulo.setFabricante(auxiliar);
                        DaoFactory.getInstance().update(sesion, articulo);
                      } // if
                    } // if  
                    else                        
                      if(update.getMultiplo()!= multiplo && multiplo>= 1L) {
                        update.setMultiplo(multiplo);
                        DaoFactory.getInstance().update(sesion, update);
                      } // if
                  } // else  
								} // if
								monitoreo.incrementar();
								if(fila% this.categoria.getTuplas()== 0) {
									if(bitacora== null) {
										bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, new Long(fila), 2L);
										DaoFactory.getInstance().insert(sesion, bitacora);
									} // if
									else {
										bitacora.setProcesados(new Long(fila));
										bitacora.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
										DaoFactory.getInstance().update(sesion, bitacora);
									} // else
									this.commit();
									this.procesados= fila;
									LOG.warn("Realizando proceso de commit en la fila "+ this.procesados);
								} // if
							} // if
							else {
								this.errores++;
								LOG.warn(fila+ " proveedor: ["+ rfc+ "] codigo propio: ["+ codigo+ "] codigo proveedor: ["+ auxiliar+ "] multiplo["+ multiplo+ "]");
								TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
									sheet.getCell(0, fila).getContents(), // String codigo, 
									-1L, // Long idMasivaDetalle, 
									this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
									"EL PROVEEDOR["+ rfc+ "], CODIGO PROPIO["+ codigo+ "], CODIGO PROVEEDOR["+ auxiliar+ "], MULTIPLO["+ multiplo+ "] NO EXISTE" // String observaciones
								);
								DaoFactory.getInstance().insert(sesion, detalle);
							} // else	
							count++;
						} // if	
	//					if(fila> 500)
	//						throw new KajoolBaseException("Este error fue provocado intencionalmente !");
					} // try
					catch(Exception e) {
            LOG.error("[--->>> ["+ fila+ "] {"+ sheet.getCell(0, fila).getContents().toUpperCase()+ "} {"+ sheet.getCell(2, fila).getContents().toUpperCase()+ "} <<<---]");
						Error.mensaje(e);
					} // catch
					this.procesados= count;
					LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
				} // for
				if(bitacora== null) {
					bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  				DaoFactory.getInstance().insert(sesion, bitacora);
				} // if
			  else {
					bitacora.setProcesados(this.masivo.getTuplas());
					bitacora.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
					DaoFactory.getInstance().update(sesion, bitacora);
				} // if
				LOG.warn("Cantidad de filas con error son: "+ this.errores);
 				this.procesados= count;
				regresar= true;
			} // if
		} // try
    finally {
			Methods.clean(params);
      if(workbook!= null) {
        workbook.close();
        workbook = null;
      } // if
    } // finally
		return regresar;
	} // toCodigos
	
}