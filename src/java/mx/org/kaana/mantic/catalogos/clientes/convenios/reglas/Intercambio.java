package mx.org.kaana.mantic.catalogos.clientes.convenios.reglas;

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
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetArticulosClientesDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;
import mx.org.kaana.mantic.db.dto.TcManticArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDto;
import mx.org.kaana.mantic.db.dto.TcManticMasivasArchivosDto;
import mx.org.kaana.mantic.db.dto.TcManticMasivasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticMasivasDetallesDto;
import static org.apache.commons.io.Charsets.ISO_8859_1;
import static org.apache.commons.io.Charsets.UTF_8;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Intercambio extends IBaseTnx {
  
  private static final Log LOG = LogFactory.getLog(Transaccion.class);
  private TcManticMasivasArchivosDto masivo;	
	private ECargaMasiva categoria;
	private Long idCliente;
	private int errores;
	private int procesados;
	private String messageError;
  
  public Intercambio(TcManticMasivasArchivosDto masivo, ECargaMasiva categoria, Long idCliente) {
		this.masivo    = masivo;		
		this.categoria = categoria;
		this.errores   = 0;
		this.procesados= 0;
		this.idCliente = idCliente;
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
  protected boolean ejecutar(Session sesion, EAccion accion) throws Exception{
    boolean regresar= false;
    try {
      this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" catalogo de forma masiva");
      switch (accion) {
				case PROCESAR: 
 					regresar= this.toProcess(sesion);
					break;
				case ELIMINAR: 
					break;
      } // swtich 
      if (!regresar) {
        throw new Exception(this.messageError);
      } // if
    } // tyr
		catch (Exception e) {
			Error.mensaje(e);
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
    } // catch
    return regresar;
  }
  
	protected boolean toProcess(Session sesion) throws Exception {
		boolean regresar                   = false;  
		TcManticMasivasBitacoraDto bitacora= null;
		try {
			File file= new File(this.masivo.getAlias());
			if(file.exists()) {
				if(!this.masivo.isValid()) {
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
						case ACUERDOS:
							this.toPreciosClientes(sesion, file);
						  break;
					} // swtich
				} // try
        catch(Exception e) {
          throw e;
        } // catch
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
		} // try
		catch (Exception e) {
			throw e;
		} // catch
    return regresar;
	} // toProcess
	
	public void toDeleteXls() throws Exception {
		List<TcManticMasivasArchivosDto> list= (List<TcManticMasivasArchivosDto>)DaoFactory.getInstance().findViewCriteria(TcManticMasivasArchivosDto.class, this.masivo.toMap(), "all");
		if(list!= null)
			for (TcManticMasivasArchivosDto item: list) {
				LOG.info("Catalogo importado: "+ item.getIdMasivaArchivo()+ " delete file: "+ item.getAlias());
				File file= new File(item.getAlias());
				file.delete();
			} // for
	}	// toDeleteXls 	
  
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
	
	private TcManticArticulosDto toFindArticulo(Session sesion, String codigo, String auxiliar, Long idArticuloTipo) {
		TcManticArticulosDto regresar= null;
		Map<String, Object> params   = new HashMap<>();
		try {
			params.put("codigo", codigo);
			params.put("auxiliar", auxiliar);
			params.put("idArticuloTipo", idArticuloTipo);
			regresar= (TcManticArticulosDto)DaoFactory.getInstance().toEntity(sesion, TcManticArticulosDto.class, "VistaCargasMasivasDto", "ambos", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindArticulo
	
  private Boolean toPreciosClientes(Session sesion, File archivo) throws Exception {
		Boolean regresar = false;
		Workbook workbook= null;
		Sheet sheet      = null;
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
						if(!Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(2, fila).getContents()) && !Cadena.isVacio(sheet.getCell(4, fila).getContents()) && !sheet.getCell(0, fila).getContents().toUpperCase().startsWith("NOTA")) {
							//      0      1       2      3        4         5         6           7                8         9
							//RFCCLIENTE|CLIENTE|CLAVE|AUXILIAR|ARTICULO|MENUDEO|MEDIOMAYOREO|MAYOREO|LIMITEMEDIOMAYOREO|LIMITEMAYOREO
							String rfcCliente= sheet.getCell(0, fila).getContents()!= null? new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): null;
							String clave     = sheet.getCell(2, fila).getContents()!= null? new String(sheet.getCell(2, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): null;
							String auxiliar  = sheet.getCell(3, fila).getContents()!= null? new String(sheet.getCell(3, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): null;
							double menudeo     = Numero.getDouble(sheet.getCell(5, fila).getContents()!= null? sheet.getCell(5, fila).getContents().replaceAll("[$, *\"]", ""): "0", 0D);
							double medioMayoreo= Numero.getDouble(sheet.getCell(6, fila).getContents()!= null? sheet.getCell(6, fila).getContents().replaceAll("[$, *\"]", ""): "0", 0D);
							double mayoreo     = Numero.getDouble(sheet.getCell(7, fila).getContents()!= null? sheet.getCell(7, fila).getContents().replaceAll("[$, *\"]", ""): "0", 0D);
							double limiteMedioMayoreo= Numero.getDouble(sheet.getCell(8, fila).getContents()!= null? sheet.getCell(8, fila).getContents().replaceAll("[$, *\"]", ""): "0", 0D);
							double limiteMayoreo     = Numero.getDouble(sheet.getCell(9, fila).getContents()!= null? sheet.getCell(9, fila).getContents().replaceAll("[$, *\"]", ""): "0", 0D);
							if(!Cadena.isVacio(rfcCliente) && !Cadena.isVacio(clave)) {
								TcManticClientesDto cliente= null;
								if(Objects.equals(rfcCliente, "*") || this.idCliente!= Constantes.TOP_OF_ITEMS)
									cliente= (TcManticClientesDto)DaoFactory.getInstance().findById(TcManticClientesDto.class, this.idCliente);
								else
									cliente= this.toFindCliente(sesion, rfcCliente);
								TcManticArticulosDto articulo   = this.toFindArticulo(sesion, clave, auxiliar, 1L);
								if(cliente!= null && articulo!= null) {
									params.put("idCliente", cliente.getIdCliente());
									params.put("idArticulo", articulo.getIdArticulo());
									TcKeetArticulosClientesDto precios= (TcKeetArticulosClientesDto)DaoFactory.getInstance().toEntity(sesion, TcKeetArticulosClientesDto.class, "TcKeetArticulosClientesDto", "igual", params);
									if(precios== null) {
										precios= new TcKeetArticulosClientesDto(
                      -1L, // Long idArticuloCliente, 
                      cliente.getIdCliente(), // Long idCliente, 
                      limiteMedioMayoreo, // Double limiteMedioMayoreo, 
                      JsfBase.getIdUsuario(), // Long idUsuario, 
                      mayoreo, // Double mayoreo, 
                      menudeo, // Double menudeo, 
                      articulo.getIdArticulo(), // Long idArticulo, 
                      limiteMayoreo, // Double limiteMayoreo, 
                      medioMayoreo, // Double medioMayoreo, 
                      new Timestamp(Calendar.getInstance().getTimeInMillis()) // Timestamp actualizado                          
										);
									  DaoFactory.getInstance().insert(sesion, precios);
									} // if
									else {
                    if(menudeo+ medioMayoreo+ mayoreo!= 0) {
                      if(!"*".equals(sheet.getCell(5, fila).getContents())) 
                        precios.setMenudeo(menudeo);
                      if(!"*".equals(sheet.getCell(6, fila).getContents())) 
                        precios.setMedioMayoreo(medioMayoreo);
                      if(!"*".equals(sheet.getCell(7, fila).getContents())) 
                        precios.setMayoreo(mayoreo);
                      if(!"*".equals(sheet.getCell(8, fila).getContents())) 
                        precios.setLimiteMedioMayoreo(limiteMedioMayoreo);
                      if(!"*".equals(sheet.getCell(9, fila).getContents())) 
                        precios.setLimiteMayoreo(limiteMayoreo);
                      precios.setActualizado(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                      DaoFactory.getInstance().update(sesion, precios);
                    } // if
                    else 
                      DaoFactory.getInstance().delete(sesion, precios);
									} // else
								} // if
								monitoreo.incrementar();
							} // if
							else {
								this.errores++;
								LOG.warn(fila+ ": cliente: ["+ rfcCliente+ "] codigo: ["+ clave+ "] menudeo: ["+ menudeo+ "]");
								TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
									sheet.getCell(4, fila).getContents(), // String codigo, 
									-1L, // Long idMasivaDetalle, 
									this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
									"EL CLIENTE["+ rfcCliente+ "] CODIGO["+ clave+ "], MENUDEO ["+ menudeo+ "] ESTAN EN CEROS O VACIO" // String observaciones
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
					this.procesados= count;
					LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
				} // for
				bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  			DaoFactory.getInstance().insert(sesion, bitacora);
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
	} // toPreciosClientes

}