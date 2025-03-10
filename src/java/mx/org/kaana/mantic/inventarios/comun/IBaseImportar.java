package mx.org.kaana.mantic.inventarios.comun;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.db.dto.TcManticArchivosDto;
import mx.org.kaana.mantic.db.dto.TcManticProveedoresDto;
import mx.org.kaana.mantic.libs.factura.beans.ComprobanteFiscal;
import mx.org.kaana.mantic.libs.factura.beans.Concepto;
import mx.org.kaana.mantic.libs.factura.beans.Emisor;
import mx.org.kaana.mantic.libs.factura.beans.Receptor;
import mx.org.kaana.mantic.libs.factura.reglas.Reader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 24/07/2018
 *@time 03:35:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public abstract class IBaseImportar extends IBaseFilter implements Serializable {

	private static final long serialVersionUID= 3555865994317044663L;
	private static final Log LOG              = LogFactory.getLog(IBaseImportar.class);
	
	private ComprobanteFiscal factura;
	private Importado xml;
	private Importado pdf;
	private Importado jpg;
	private Importado file;			
	private Emisor emisor;
	private Receptor receptor;

	public ComprobanteFiscal getFactura() {
		return factura;
	}

	public Importado getXml() {
		return xml;
	}

	public Importado getPdf() {
		return pdf;
	}

  public Importado getJpg() {
    return jpg;
  }
  
  public Emisor getEmisor() {
		return emisor;
	}

	public Receptor getReceptor() {
		return receptor;
	}

	public Importado getFile() {
		return file;
	}

	public void setFile(Importado file) {
		this.file = file;
	}
		
	protected void doFileUpload(FileUploadEvent event, Long fechaFactura, String carpeta, String clave) {
		this.doFileUpload(event, fechaFactura, carpeta, clave, true, 1D);
	}
	
	protected void doFileUpload(FileUploadEvent event, Long fechaFactura, String carpeta, String clave, Boolean sinIva, Double tipoDeCambio) {
		StringBuilder path= new StringBuilder();  
		StringBuilder temp= new StringBuilder();  
		String nameFile   = Archivo.toFormatNameFile(event.getFile().getFileName().toUpperCase());
    File result       = null;		
		Long fileSize     = 0L;
		try {
			Calendar calendar= Calendar.getInstance();
			calendar.setTimeInMillis(fechaFactura);
      path.append(carpeta);
      temp.append(JsfBase.getAutentifica().getEmpresa().getIdEmpresa().toString());
      temp.append("/");
      temp.append(Calendar.getInstance().get(Calendar.YEAR));
      temp.append("/");
      temp.append(Fecha.getNombreMes(calendar.get(Calendar.MONTH)).toUpperCase());
      temp.append("/");
      temp.append(clave!= null? clave.trim(): "NoDefinido");
      temp.append("/");
			path.append(temp.toString());
			result= new File(path.toString());		
			if (!result.exists())
				result.mkdirs();
      String ruta= path.toString();
      path.append(nameFile);
			result = new File(path.toString());
			if (result.exists())
				result.delete();			      
			this.toWriteFile(result, event.getFile().getInputstream(), nameFile.endsWith(EFormatos.XML.name()));
			fileSize= event.getFile().getSize();			
      String observaciones= (String)this.attrs.get("observaciones");
      Long idTipoDocumento= 13L;
      if(this.attrs.get("idTipoDocumento")!= null)
        idTipoDocumento= (Long)this.attrs.get("idTipoDocumento");
			if(nameFile.endsWith(EFormatos.XML.name())) {
			  this.xml= new Importado(nameFile, event.getFile().getContentType(), EFormatos.XML, event.getFile().getSize(), fileSize.equals(0L) ? fileSize: fileSize/1024, event.getFile().equals(0L)? " Bytes": " Kb", temp.toString(), observaciones, event.getFile().getFileName().toUpperCase(), idTipoDocumento);
        if(Objects.equals(idTipoDocumento, 13L))
				  this.toReadFactura(result, sinIva, tipoDeCambio);
				this.attrs.put("xml", this.xml.getName());
        this.toSaveFileRecord(event.getFile().getFileName().toUpperCase(), ruta, path.toString(), this.xml.getName());            
			} //
			else
			  if(nameFile.endsWith(EFormatos.PDF.name())) {
			    this.pdf= new Importado(nameFile, event.getFile().getContentType(), EFormatos.PDF, event.getFile().getSize(), fileSize.equals(0L) ? fileSize: fileSize/1024, event.getFile().equals(0L)? " Bytes": " Kb", temp.toString(), observaciones, event.getFile().getFileName().toUpperCase(), idTipoDocumento);
  				this.attrs.put("pdf", this.pdf.getName()); 
          this.toSaveFileRecord(event.getFile().getFileName().toUpperCase(), ruta, path.toString(), this.pdf.getName());            
				} // if
        else
			    if(nameFile.endsWith(EFormatos.JPG.name()) || nameFile.endsWith(EFormatos.PNG.name())) {
			      this.jpg= new Importado(nameFile, event.getFile().getContentType(), EFormatos.JPG, event.getFile().getSize(), fileSize.equals(0L) ? fileSize: fileSize/1024, event.getFile().equals(0L)? " Bytes": " Kb", temp.toString(), observaciones, event.getFile().getFileName().toUpperCase(), idTipoDocumento);
  				  this.attrs.put("jpg", this.jpg.getName()); 
            UIBackingUtilities.execute("reload();");
            this.toSaveFileRecord(event.getFile().getFileName().toUpperCase(), ruta, path.toString(), this.jpg.getName());            
		  		} // if
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessage("Importar:", "El archivo no pudo ser importado !", ETipoMensaje.ERROR);
			if(result!= null)
			  result.delete();
		} // catch
	} // doFileUpload	
	
	public void doFileUpload(FileUploadEvent event, String clave, String propiedadServidor) {
		StringBuilder path= new StringBuilder();  
		StringBuilder temp= new StringBuilder();  
		String nameFile   = Archivo.toFormatNameFile(event.getFile().getFileName().toUpperCase());
    File result       = null;		
		Long fileSize     = 0L;
		try {			
      path.append(Configuracion.getInstance().getPropiedadSistemaServidor(propiedadServidor));
      temp.append(JsfBase.getAutentifica().getEmpresa().getNombreCorto().replaceAll(" ", ""));
      temp.append("/");
      temp.append(clave!= null? clave.trim(): "NoDefinido");
      temp.append("/");      
			path.append(temp.toString());
			result= new File(path.toString());		
			if (!result.exists())
				result.mkdirs();
      String ruta= path.toString();
      path.append(nameFile);
			result = new File(path.toString());
			if (result.exists())
				result.delete();			      
			Archivo.toWriteFile(result, event.getFile().getInputstream());
			fileSize= event.getFile().getSize();						
			this.file= new Importado(nameFile, event.getFile().getContentType(), EFormatos.PDF, event.getFile().getSize(), fileSize.equals(0L) ? fileSize: fileSize/1024, event.getFile().equals(0L)? " Bytes": " Kb", temp.toString(), (String)this.attrs.get("observaciones"), event.getFile().getFileName().toUpperCase());
  		this.attrs.put("file", this.file.getName()); 	
      
      //**
      this.toSaveFileRecord(event.getFile().getFileName().toUpperCase(), ruta, path.toString(), this.file.getName());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessage("Importar:", "El archivo no pudo ser importado !", ETipoMensaje.ERROR);
			if(result!= null)
			  result.delete();
		} // catch
	} // doFileUpload	
	
	protected void toWriteFile(File result, InputStream upload) throws Exception {
		toWriteFile(result, upload, false);
	} // toWriteFile
	
	protected void toWriteFile(File result, InputStream upload, boolean xml) throws Exception {
		FileOutputStream fileOutputStream= new FileOutputStream(result);
		InputStream inputStream          = upload;
		byte[] buffer                    = new byte[Constantes.BUFFER_SIZE];
		int bulk;
		while(true) {
			if(xml)
			  bulk= inputStream.read();
			else
			  bulk= inputStream.read(buffer);
			if (bulk < 0) 
				break;        
			if(xml) {
				if(bulk!= 13 && bulk!= 10) {
					fileOutputStream.write(bulk);
					fileOutputStream.flush();
				} // if
			} // if
			else{
			  fileOutputStream.write(buffer, 0, bulk);
			  fileOutputStream.flush();
			} // else
		} // while
		fileOutputStream.close();
		inputStream.close();
	} // toWriteFile 

	private int existsItem(List<Articulo> faltantes, Concepto concepto) {
		int regresar= 0;
		if(Cadena.isVacio(concepto.getNoIdentificacion())) 
			concepto.setNoIdentificacion("");
		//&& faltante.getUnidadMedida().equals(concepto.getUnidad()))
		for (Articulo faltante: faltantes) {
			if(faltante.getNombre().equals(concepto.getDescripcion()) && 
				 faltante.getCodigo().equals(concepto.getNoIdentificacion()))
				break;
			else
				regresar++;
		} // for
		return regresar>= faltantes.size()? -1: regresar;
	}
	
	public void toReadFactura(File file, Boolean sinIva, Double tipoDeCambio) throws Exception {
    Reader reader           = null;
		List<Articulo> faltantes= null;
		try {
			faltantes    = new ArrayList<>();
			reader       = new Reader(file.getAbsolutePath());
			this.factura = reader.execute();
			this.emisor  = this.factura.getEmisor();
			this.receptor= this.factura.getReceptor();
      for (Concepto concepto: this.factura.getConceptos()) {
        LOG.info("|"+ concepto.getDescripcion()+ "|"+ concepto.getCantidad()+ "|"+ concepto.getImporte()+ "|"+ concepto.getValorUnitario()+ "|"+ Numero.toRedondearSat((Double.parseDouble(concepto.getTraslado().getBase()))/ Double.parseDouble(concepto.getCantidad()))+ "|"+ concepto.getTraslado().getBase()+ "|"+ concepto.getDescuento()+ "|"+ concepto.getTraslado().getImporte()+ "|"+ concepto.getTraslado().getImpuesto());
      } // for
			for (Concepto concepto: this.factura.getConceptos()) {
				int index= this.existsItem(faltantes, concepto);
				// double descuento= Numero.toRedondearSat(Double.parseDouble(concepto.getDescuento()!= null? concepto.getDescuento(): "0"));
				if(index>= 0) {
					Articulo item= faltantes.get(index);
					item.setCantidad(item.getCantidad()+ Double.parseDouble(concepto.getCantidad()));
					item.setCosto(Numero.toRedondearSat((item.getSubTotal()+ Double.parseDouble(concepto.getTraslado().getBase()))/ item.getCantidad()));
					item.setSubTotal(item.getSubTotal()+ Double.parseDouble(concepto.getTraslado().getBase()));
				} // if
				else 
					faltantes.add(new Articulo(
						sinIva, // sinIva
						tipoDeCambio, // tipoDeCambio
						concepto.getDescripcion(), // nombre
						concepto.getNoIdentificacion(), // codigo
						Numero.toRedondearSat((Double.parseDouble(concepto.getTraslado().getBase()))/ Double.parseDouble(concepto.getCantidad())), // costo
						"", // descuento,
						-1L, // idOrdenCompra
						"", // extras
						0D, // importe
						"", // propio
						Numero.getDouble(concepto.getTraslado().getTasaCuota(), 0D)* 100, // iva
						0D, // totalImpuesto 
						Numero.toRedondearSat(Numero.getDouble(concepto.getTraslado().getBase(), 0D)), // subTotal
						Double.parseDouble(concepto.getCantidad()), // cantidad
						-1L, // idOrdenDetalle 
						new Random().nextLong(), // idArticulo 
						0D, // totalDescuentos
						-1L, // idProveedor
						false, // ultimo
						false, // solicitado
						0D, // stock
						0D, // excedentes
						concepto.getClaveProdServ(), // sat
						concepto.getUnidad(), // unidadMedida
						2L, // idAplicar
						concepto.getDescripcion() // origen
					));
			} // for
			Collections.sort(faltantes);
			this.attrs.put("faltantes", faltantes);
		} // try
		catch (Exception e) {
			reader = null;
			throw e;
		} // catch
	}
	
	protected void doLoadFiles(String proceso, Long idSelected, String idNombre) {
		this.doLoadFiles(proceso, idSelected, idNombre, true, 1D);
	}
	
	protected void doLoadFiles(String proceso, Long idSelected, String idNombre, Boolean sinIva, Double tipoDeCambio) {
		Entity tmp= null;
		if(idSelected> 0) {
			Map<String, Object> params= new HashMap<>();
			try {
				params.put(idNombre, idSelected);
				params.put("idTipoArchivo", 1L);
				tmp= (Entity)DaoFactory.getInstance().toEntity(proceso, "exists", params);
				if(tmp!= null) {
					File reference= new File(tmp.toString("alias"));
					if(reference.exists()) {
					  this.xml= new Importado(tmp.toString("nombre"), EFormatos.XML.name(), EFormatos.XML, 0L, tmp.toLong("tamanio"), "", tmp.toString("ruta"), tmp.toString("observaciones"));
					  this.toReadFactura(reference, sinIva, tipoDeCambio);
  				  this.attrs.put("xml", this.xml.getName()); 
					} // if	
				} // if	
				params.put("idTipoArchivo", 2L);
				tmp= (Entity)DaoFactory.getInstance().toEntity(proceso, "exists", params);
				if(tmp!= null) {
					this.pdf= new Importado(tmp.toString("nombre"), EFormatos.PDF.name(), EFormatos.PDF, 0L, tmp.toLong("tamanio"), "", tmp.toString("ruta"), tmp.toString("observaciones"));
  				this.attrs.put("pdf", this.pdf.getName()); 
				} // if	
				params.put("idTipoArchivo", 17L);
				tmp= (Entity)DaoFactory.getInstance().toEntity(proceso, "exists", params);
				if(tmp!= null) {
					this.jpg= new Importado(tmp.toString("nombre"), EFormatos.JPG.name(), EFormatos.JPG, 0L, tmp.toLong("tamanio"), "", tmp.toString("ruta"), tmp.toString("observaciones"));
  				this.attrs.put("jpg", this.pdf.getName()); 
				} // if	
			} // try
			catch (Exception e) {
				Error.mensaje(e);
				JsfBase.addMessageError(e);
			} // catch
			finally {
				Methods.clean(params);
			} // finally
		} // if
	}

	public StreamedContent doPdfFileDownload(String carpeta) {
		StreamedContent regresar= null;
		try {
		  InputStream stream = new FileInputStream(new File(carpeta.concat(this.pdf.getRuta()).concat(this.pdf.getName())));
	    regresar= new DefaultStreamedContent(stream, "application/pdf", this.pdf.getName());
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
		return regresar;
	}	
	
	public StreamedContent doFileDownload(UISelectEntity file) {
		StreamedContent regresar= null;
		try {
			File reference= new File(file.toString("alias"));
			if(reference.exists()) {
				InputStream stream = new FileInputStream(reference);
        switch (file.toLong("idTipoArchivo").intValue()) {
          case 1:
            regresar= new DefaultStreamedContent(stream, EFormatos.XML.getContent(), file.toString("nombre"));
            break;
          case 2:
            regresar= new DefaultStreamedContent(stream, EFormatos.PDF.getContent(), file.toString("nombre"));
            break;
          default:
            regresar= new DefaultStreamedContent(stream, EFormatos.JPG.getContent(), file.toString("nombre"));
            break;
        } // switch
			} // if	
			else {
				LOG.warn("No existe el archivo: "+ file.toString("alias"));
        JsfBase.addMessage("No existe el archivo:"+ file.toString("nombre")+ ", favor de verificarlo.");
			} // else	
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
		return regresar;
	}
	
	protected StreamedContent toPdfFileDownload(Entity file) {
		StreamedContent regresar= null;
		try {
			File reference= new File(file.toString("alias"));
			if(reference.exists()) {
				InputStream stream = new FileInputStream(reference);
				regresar= new DefaultStreamedContent(stream, EFormatos.PDF.getContent(), file.toString("nombre"));
			} // if	
			else {
				LOG.warn("No existe el archivo: "+ file.toString("alias"));
        JsfBase.addMessage("No existe el archivo:"+ file.toString("nombre")+ ", favor de verificarlo.");
			} // else	
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
		return regresar;
	}
	
	protected StreamedContent toXmlFileDownload(Entity file) {
		StreamedContent regresar= null;
		try {
			File reference= new File(file.toString("alias"));
			if(reference.exists()) {
				InputStream stream = new FileInputStream(reference);
  			regresar= new DefaultStreamedContent(stream, EFormatos.XML.getContent(), file.toString("nombre"));
			} // if	
			else {
				LOG.warn("No existe el archivo: "+ file.toString("alias"));
        JsfBase.addMessage("No existe el archivo:"+ file.toString("nombre")+ ", favor de verificarlo.");
			} // else	
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
		return regresar;
	}

	public void doViewFileDocument(UISelectEntity item) {
		this.doViewPdfDocument(item);
	}
	
	public void doViewPdfDocument(UISelectEntity item) { 
		this.toCopyDocument(item.toString("alias"), item.toString("nombre"));
	}
	
	public void doViewPdfDocument(Entity item) { 
		this.toCopyDocument(item.toString("alias"), item.toString("archivo"));
	}
	
	protected void doViewDocument(String carpeta) {
		this.toCopyDocument(carpeta.concat(this.pdf.getRuta()).concat(this.pdf.getName()), this.pdf.getName());
	}
	
	protected void doViewDocumentFile(String carpeta) {
		this.toCopyDocument(carpeta.concat(this.file.getRuta()).concat(this.file.getName()), this.file.getName());
	}

	public void doViewXmlDocument(UISelectEntity item) {
		this.toViewFile(item.toString("alias"));
	}

	protected void doViewFile(String carpeta) {
		this.toViewFile(carpeta.concat(this.xml.getRuta()).concat(this.xml.getName()));
	}	
	
	protected void toViewFile(String nameXml) {
		String regresar   = "";
		String name       = nameXml;
    StringBuilder sb  = new StringBuilder("");
    FileReader in     = null;
		BufferedReader br = null;
		try {
			in= new FileReader(name);
			br= new BufferedReader(in);
			String line;
			while ((line = br.readLine()) != null) {
  			sb.append(line);
			} // while
			regresar= this.prettyFormat(sb.toString().startsWith("<")? sb.toString(): sb.substring(sb.indexOf("<")), 2);
		} // try
		catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
		} // catch
		finally {
			try {
        if(br != null)
					br.close();
				if(in != null)
  				in.close();
			} // try
			catch (IOException e) {
        JsfBase.addMessageError(e);
			} // catch
		} // finally
		this.attrs.put("temporal", regresar);
	}
	
	private String prettyFormat(String input, int indent) throws Exception {
		Source xmlInput = new StreamSource(new StringReader(input));
		StringWriter stringWriter = new StringWriter();
		StreamResult xmlOutput = new StreamResult(stringWriter);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", indent);
		Transformer transformer = transformerFactory.newTransformer(); 
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(xmlInput, xmlOutput);
		return xmlOutput.getWriter().toString();
  }
	
  protected void toCopyDocument(String alias, String name) {
		try {
			LOG.warn("context: "+ JsfBase.getContext().concat("/").concat(Constantes.PATH_INVOICE).concat(name).concat("?pfdrid_c=true"));
  	  this.attrs.put("temporal", JsfBase.getContext().concat("/").concat(Constantes.PATH_INVOICE).concat(name).concat("?pfdrid_c=true"));
  		File source= new File(JsfBase.getRealPath().concat(Constantes.PATH_INVOICE).concat(name));
			LOG.warn("source: "+ JsfBase.getRealPath().concat(Constantes.PATH_INVOICE).concat(name));
			if(!source.exists()) {
   			LOG.warn("input: "+ alias);
   	  	FileInputStream input= new FileInputStream(new File(alias));
        Archivo.toWriteFile(source, input);		
			} // if	
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	}	
	
	public void doCerrar() {
		try {
//			String name= (String)this.attrs.get("temporal");
//			if(name.endsWith("XML"))
//				name= JsfBase.getContext().concat(name);
//			else
//				name= name.substring(0, name.lastIndexOf("?"));
//			File file= new File(JsfBase.getRealPath().concat(name));
//			file.delete();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
	}
	
	protected void doUpdateRfc(TcManticProveedoresDto proveedor) {
	  Map<String, Object> params= new HashMap<>();
		try {
			params.put("rfc", this.emisor.getRfc());
			params.put("idProveedor", proveedor.getIdProveedor());
			List<Entity> values= (List<Entity>)DaoFactory.getInstance().toEntitySet("TcManticProveedoresDto", "duplicados", params);
			StringBuilder sb= new StringBuilder();
			if(values!= null && values.size()> 0) {
				// VERIFICAR SI EXISTE OTRO PROVEEDOR CON EL MISMO RFC Y MOSTRARLO CON ESTE MISMO MENSAJE EN CASO DE ENCONTRARLO
				for (Entity item: values) {
					sb.append("  [");
					sb.append(item.toString("rfc"));
					sb.append("]  ");
					sb.append(item.toString("razonSocial"));
					sb.append(".<br/>");
				} // for
				sb.append("<br/>Por lo tanto no se puede cambiar el RFC a este proveedor.");
				JsfBase.addAlert("El RFC del proveedor ya esta asociado a otro(s) proveedor(es):<br/><br/>".concat(sb.toString()), ETipoMensaje.ALERTA);
			} // if
			else {
			  proveedor.setRfc(this.emisor.getRfc());
		  	if(DaoFactory.getInstance().update(proveedor)>= 1L)
  				JsfBase.addAlert("El cat�logo del proveedor fu� actualizado de forma correcta con<br/> ["+ proveedor.getRfc()+ "] a nombre de "+ proveedor.getRazonSocial(), ETipoMensaje.ALERTA);
			} // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
		finally {
			Methods.clean(params);
		} // finally	
	}

	protected void doLoadImportados(String proceso, String idXml, Map<String, Object> params) {
		List<Columna> columns= new ArrayList<>();
		try {
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("usuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
		  this.attrs.put("importados", UIEntity.build(proceso, idXml, params, columns));
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(columns);
    } // finally
  } 
 
  /*
   TcManticArchivosDto para el campo de idEliminar
   1 SIGNIFICA QUE EL ARCHIVO SE DEBE DE QUEDAR
   2 SIGNIFICA QUE EL ARCHIVO SE TIENE QUE ELIMINAR
   3 SIGNIFICA QUE EL ARCHIVO YA FUE ELIMINADO
   4 SIGNIFICA QUE EL ARCHIVO SE INTENTO ELIMINAR PERO NO EXISTE
  */
  protected void toSaveFileRecord(String archivo, String ruta, String alias, String nombre) throws Exception {
		TcManticArchivosDto registro= new TcManticArchivosDto(
			archivo, // String archivo, 
			2L, // Long idEliminado, 
			ruta, // String ruta, 
			JsfBase.getIdUsuario(), // Long idUsuario, 
			alias, // String alias, 
			-1L, // Long idArchivo, 
			nombre // String nombre
		);
		DaoFactory.getInstance().insert(registro);
	}

  public void reset() {
    this.xml= null;  
    this.pdf= null;  
    this.jpg= null;  
  } 
  
}
