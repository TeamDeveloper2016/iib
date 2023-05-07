package mx.org.kaana.mantic.catalogos.clientes.convenios.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.reportes.beans.ExportarXls;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.comun.IBaseImportar;
import mx.org.kaana.mantic.catalogos.clientes.convenios.reglas.Intercambio;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;
import mx.org.kaana.mantic.db.dto.TcManticMasivasArchivosDto;
import mx.org.kaana.mantic.enums.EExportacionXls;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;

@Named(value= "manticCatalogosClientesConveniosImportar")
@ViewScoped
public class Importar extends IBaseImportar implements Serializable {

	private static final Log LOG=LogFactory.getLog(Importar.class);
  private static final long serialVersionUID= 318633488565639363L;

  private TcManticMasivasArchivosDto masivo;
	protected FormatLazyModel lazyModel;
	private ECargaMasiva categoria;

	public TcManticMasivasArchivosDto getMasivo() {
		return masivo;
	}

	public FormatLazyModel getLazyModel() {
		return lazyModel;
	}

	public ECargaMasiva getCategoria() {
		return categoria;
	}
	
	@PostConstruct
  @Override
  protected void init() {		
    try {
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_MASIVO);
			this.attrs.put("idLimpiar", 2L);
			this.attrs.put("procesados", 0L);
			this.attrs.put("idTipoMasivo", JsfBase.getFlashAttribute("idTipoMasivo")!= null? JsfBase.getFlashAttribute("idTipoMasivo"): ECargaMasiva.ACUERDOS.getId());
  		this.categoria= ECargaMasiva.ACUERDOS;
			this.attrs.put("xls", ""); 
			this.attrs.put("tuplas", 0L);
			this.masivo = new TcManticMasivasArchivosDto(
				-1L, // Long idMasivaArchivo, 
				Configuracion.getInstance().getPropiedadSistemaServidor("masivos"), // String ruta, 
				this.categoria.getId(), // Long idTipoMasivo, 
				1L, // Long idMasivaEstatus, 
				null, // String nombre, 
				0L, // Long tamanio, 
			  JsfBase.getIdUsuario(), // Long idUsuario, 
				8L, // Long idTipoArchivo, 
				0L, // Long tuplas, 
				"", // String observaciones, 
				null, // String alias, 
				JsfBase.getAutentifica().getEmpresa().getIdEmpresa(),
				1L,
				null
			);
  		this.toCheckRequerido();
  		this.doChangeTipo();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
	public void doTabChange(TabChangeEvent event) {
		if(event.getTab().getTitle().equals("Archivos")) 
			this.doLoadArhivos("VistaCargasMasivasDto", "importados", this.attrs);
	} // doTabChange		
	
	protected void doLoadArhivos(String proceso, String idXml, Map<String, Object> params) {
		List<Columna> columns= new ArrayList<>();
		try {
      columns.add(new Columna("ruta", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("usuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA));
		  this.lazyModel= new FormatCustomLazy(proceso, idXml, params, columns);
			UIBackingUtilities.resetDataTable();
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(columns);
    } // finally
  } // doLoadArhivos
	
	public void doFileUpload(FileUploadEvent event) {
		String tuplas= "0";
		try {
  		this.attrs.put("procesados", 0);
      this.doFileUploadMasivo(event, this.masivo.getRegistro().getTime(), Configuracion.getInstance().getPropiedadSistemaServidor("masivos"), this.masivo, this.categoria);
			tuplas= Global.format(EFormatoDinamicos.MILES_SIN_DECIMALES, this.masivo.getTuplas());
			this.attrs.put("tuplas", tuplas);
		} // try
		catch(Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		if(this.masivo.getIdTipoMasivo()== 1L) {
	    UIBackingUtilities.execute("janal.show([{summary: 'Procesar:', detail: 'Filas encontradas ["+ tuplas+ "].'}], 'info');"); 
		} // if	
	} // doFileUpload	
	
	public void doViewFile() {
		this.doViewFile(Configuracion.getInstance().getPropiedadSistemaServidor("listaprecios"));
	}
	
  public String doCancelar() {   
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
	
	public String doAceptar() {
		String regresar        = null;
		Intercambio transaccion= null;
		long tuplas            = this.masivo.getTuplas();
		try {
      this.masivo.setIdMasivaArchivo(-1L);
			this.masivo.setArchivo(this.getXls().getOriginal());
		  this.masivo.setObservaciones((String)this.attrs.get("observaciones"));
      switch (this.categoria) {
        case ACUERDOS:
          transaccion= new Intercambio(this.masivo, this.categoria, ((UISelectEntity)this.attrs.get("idCliente")).getKey());
          break;
      } // swtich
      if(!(tuplas> 0L && transaccion!= null && transaccion.ejecutar(EAccion.PROCESAR))) 
        JsfBase.addMessage("Error:", "Ocurrio un error en la cargar del archivo !", ETipoMensaje.ERROR);		
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
		if(tuplas> 0L &&  transaccion!= null) {
			this.attrs.put("procesados", transaccion.getProcesados());
			UIBackingUtilities.execute("janal.alert('Se termin\\u00F3 de procesar el archivo !\\u000DTotal de registros: "+ tuplas+ "\\u000DRegistros procesados: "+ transaccion.getProcesados()+ 
				(tuplas!= transaccion.getProcesados()? "\\u000D\\u000DEl total de filas del archivo es diferente al procesado, favor de verificarlo": "")+ "')");
		} // if	
    return regresar;
	} // doAceptar	
  
  public void doCompleto() {
		// JsfBase.addMessage("Detalle del mensaje", "Se proceso correctamente el catalogo !", ETipoMensaje.INFORMACION);		
	} // doCompleto

	public void doChangeTipo() {
		switch(this.masivo.getIdTipoMasivo().intValue()) {
			case 18: 
				this.categoria= ECargaMasiva.ACUERDOS;
				this.toLoadClientes();
				break;
		} // switch
		if(this.masivo!= null && this.masivo.isValid()) {
			this.attrs.put("procesados", 0);
			this.setXls(null);
			this.attrs.put("xls", ""); 
			this.attrs.put("tuplas", 0L);
			this.masivo = new TcManticMasivasArchivosDto(
				-1L, // Long idMasivaArchivo, 
				null, // String ruta, 
				this.categoria.getId(), // Long idTipoMasivo, 
				1L, // Long idMasivaEstatus, 
				null, // String nombre, 
				0L, // Long tamanio, 
				JsfBase.getIdUsuario(), // Long idUsuario, 
				8L, // Long idTipoArchivo, 
				0L, // Long tuplas, 
				null, // String observaciones, 
				null, // String alias, 
				JsfBase.getAutentifica().getEmpresa().getIdEmpresa(),
				1L,
				null
			);
		} // if
		this.toCheckRequerido();
	} // doChangeTipo

	private void toCheckRequerido() {
    UIBackingUtilities.execute(
			"janal.renovate('contenedorGrupos\\\\:idCliente', {validaciones: 'libre', mascara: 'libre'});"
	  );		
		UIBackingUtilities.update("catalogo @(.involucrados) @(.importado) @(.janal-upload-frame)");
		switch(this.masivo.getIdTipoMasivo().intValue()) {
			case 18: 
				this.categoria= ECargaMasiva.ACUERDOS;
        UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:idCliente', {validaciones: 'requerido', mascara: 'libre'});");			
				break;
		} // switch
	}
	
	public String doMovimientos() {
		JsfBase.setFlashAttribute("idMasivaArchivo", ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("retorno", this.attrs.get("regreso"));
		JsfBase.setFlashAttribute("regreso", "/Paginas/Keet/Estaciones/Masivos/importar");
		return "/Paginas/Mantic/Catalogos/Clientes/Convenios/movimientos".concat(Constantes.REDIRECIONAR);
	} // doMovimientos
	
	public String doDetalles() {
		JsfBase.setFlashAttribute("idMasivaArchivo", ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("retorno", this.attrs.get("regreso"));
		JsfBase.setFlashAttribute("regreso", "/Paginas/Keet/Estaciones/Masivos/importar");
		return "/Paginas/Mantic/Catalogos/Clientes/Convenios/detalles".concat(Constantes.REDIRECIONAR);
	} // doDetalles	

	public void toLoadClientes() {
    Map<String, Object> params= new HashMap();
		try {
      Entity entity= new Entity(Constantes.TOP_OF_ITEMS);
      entity.add("clave", "TODOS");
      entity.add("rfc", "TODOS");
      entity.add("razonSocial", "TODOS");
      UISelectEntity todos= new UISelectEntity(entity);
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(this.attrs.get("clientes")== null) {
        List<UISelectEntity> clientes= UIEntity.seleccione("TcManticClientesDto", "sucursales", params, "clave");
        clientes.add(todos);
        this.attrs.put("clientes", clientes);
      } // if  
      this.attrs.put("idCliente", todos);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
	} // toLoadClientes
	
	public String doExportar() {
		String regresar           = null;
		Map<String, Object> params= new HashMap<>();
		try {
			switch (this.categoria) {
				case ACUERDOS:
					params.put("idCliente", this.attrs.get("idCliente"));
					JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap)params).clone(), EExportacionXls.CONVENIOS.getProceso(), EExportacionXls.CONVENIOS.getIdXml(), EExportacionXls.CONVENIOS.getNombreArchivo()), EExportacionXls.CONVENIOS, "RFC CLIENTE,CLIENTE,CLAVE,AUXILIAR,ARTICULO,MENUDEO,MEDIO MAYOREO,MAYOREO,LIMITE MEDIO MAYOREO,LIMITE MAYOREO"));
					JsfBase.getAutentifica().setMonitoreo(new Monitoreo());
	  			break;
			} // swtich
			regresar = "/Paginas/Reportes/excel".concat(Constantes.REDIRECIONAR);				
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
  
}
