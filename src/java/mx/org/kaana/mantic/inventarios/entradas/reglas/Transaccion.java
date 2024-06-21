package mx.org.kaana.mantic.inventarios.entradas.reglas;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.hibernate.Session;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.reportes.FileSearch;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.compras.ordenes.reglas.Inventarios;
import mx.org.kaana.mantic.db.dto.TcManticArticulosPromediosDto;
import mx.org.kaana.mantic.db.dto.TcManticEmpresasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticEmpresasDeudasDto;
import mx.org.kaana.mantic.db.dto.TcManticFaltantesDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasArchivosDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasCostosDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesComprasDto;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesDetallesDto;
import mx.org.kaana.mantic.inventarios.entradas.beans.Costo;
import mx.org.kaana.mantic.inventarios.entradas.beans.Nombres;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEntrada;
import mx.org.kaana.mantic.inventarios.entradas.beans.Promedio;
import org.apache.log4j.Logger;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Transaccion extends Inventarios implements Serializable {

  private static final Logger LOG = Logger.getLogger(Transaccion.class);
	private static final long serialVersionUID= -6069204157451117549L;
 
	protected NotaEntrada orden;	
	private List<Articulo> articulos;
	private boolean aplicar;
	protected Importado xml;
	private Importado pdf;
	private Importado jpg;
	private String messageError;
	private TcManticNotasBitacoraDto bitacora;

	public Transaccion(NotaEntrada orden, TcManticNotasBitacoraDto bitacora) {
		this(orden);
		this.xml= null;
		this.pdf= null;
		this.bitacora= bitacora;
	}
	
	public Transaccion(NotaEntrada orden) {
		this(orden, new ArrayList<Articulo>(), false, null, null);
	}

	public Transaccion(NotaEntrada orden, List<Articulo> articulos, boolean aplicar, Importado xml, Importado pdf) {
    this(orden, articulos, aplicar, xml, pdf, null);
  }
  
	public Transaccion(NotaEntrada orden, List<Articulo> articulos, boolean aplicar, Importado xml, Importado pdf, Importado jpg) {
		super(orden.getIdAlmacen(), orden.getIdProveedor());
		this.orden    = orden;		
		this.articulos= articulos;
		this.aplicar  = aplicar;
		this.xml      = xml;
		this.pdf      = pdf;
    this.jpg      = jpg;
	} // Transaccion

	protected void setMessageError(String messageError) {
		this.messageError=messageError;
	}

	public String getMessageError() {
		return messageError;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar                     = false;
		TcManticNotasBitacoraDto bitacoraNota= null;
		Map<String, Object> params           = new HashMap<>();
		Siguiente consecutivo                = null;
		try {
			if(this.orden!= null) {
				params.put("idNotaEntrada", this.orden.getIdNotaEntrada());
				params.put("idOrdenCompra", this.orden.getIdOrdenCompra());
			} // if
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" la nota de entrada");
			switch(accion) {
				case MOVIMIENTOS:
					if(this.orden.isValid()) {
  					regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
  					this.toRemoveOrdenDetalle(sesion);
					} // if
					else {
					  consecutivo= this.toSiguiente(sesion);
					  this.orden.setConsecutivo(consecutivo.getConsecutivo());
					  this.orden.setOrden(consecutivo.getOrden());
					  this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
					  if(Objects.equals(this.orden.getIdNotaTipo(), 1L) || Objects.equals(this.orden.getIdNotaTipo(), 4L))
						  this.orden.setIdOrdenCompra(null);
					  regresar= DaoFactory.getInstance().insert(sesion, this.orden)>= 1L;
					  bitacoraNota= new TcManticNotasBitacoraDto(-1L, null, JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), this.orden.getIdNotaEstatus(), this.orden.getConsecutivo(), this.orden.getTotal());
					  regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
       	    this.toUpdateDeleteXml(sesion);	
					} // else	
					this.toFillArticulos(sesion);
          this.toFillCostos(sesion);
      		for (Articulo articulo: this.articulos) 
						articulo.setModificado(false);
					break;
				case COMPLETO:
					consecutivo= this.toSiguiente(sesion);
					this.orden.setConsecutivo(consecutivo.getConsecutivo());
					this.orden.setOrden(consecutivo.getOrden());
					this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
					this.orden.setIdOrdenCompra(null);					
					regresar= DaoFactory.getInstance().insert(sesion, this.orden)>= 1L;
					bitacoraNota= new TcManticNotasBitacoraDto(-1L, null, JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), this.orden.getIdNotaEstatus(), this.orden.getConsecutivo(), this.orden.getTotal());
					regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
  				if(this.aplicar) 
						this.toApplyNotaEntrada(sesion);
	   	    this.toUpdateDeleteXml(sesion);	
					break;
				case AGREGAR:
					consecutivo= this.toSiguiente(sesion);
					this.orden.setConsecutivo(consecutivo.getConsecutivo());
					this.orden.setOrden(consecutivo.getOrden());
					this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
					if(Objects.equals(this.orden.getIdNotaTipo(), 1L) || Objects.equals(this.orden.getIdNotaTipo(), 4L))
						this.orden.setIdOrdenCompra(null);
					regresar= DaoFactory.getInstance().insert(sesion, this.orden)>= 1L;
					bitacoraNota= new TcManticNotasBitacoraDto(-1L, null, JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), this.orden.getIdNotaEstatus(), this.orden.getConsecutivo(), this.orden.getTotal());
					regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
					this.toFillArticulos(sesion);
          this.toFillCostos(sesion);
					this.toCheckOrden(sesion);
     	    this.toUpdateDeleteXml(sesion);	
					break;
				case COMPLEMENTAR:
          this.checkConsecutivo(sesion);
					regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
  				if(this.aplicar) 
						this.toApplyNotaEntrada(sesion);
	   	    this.toUpdateDeleteXml(sesion);	
					break;				
				case MODIFICAR:
  				if(this.aplicar) {
						this.orden.setIdNotaEstatus(3L);
  					bitacoraNota= new TcManticNotasBitacoraDto(-1L, null, JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), this.orden.getIdNotaEstatus(), this.orden.getConsecutivo(), this.orden.getTotal());
	  				regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
					} // if	
          this.checkConsecutivo(sesion);
					this.toRemoveOrdenDetalle(sesion);
					this.toFillArticulos(sesion);
          this.toFillCostos(sesion);
					this.toCheckOrden(sesion);
     	    this.toUpdateDeleteXml(sesion);	
          regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;					
          break;				
				case ELIMINAR:
          regresar= this.toDeleteNota(sesion);
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.orden.setIdNotaEstatus(this.bitacora.getIdNotaEstatus());
						regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
            switch(this.bitacora.getIdNotaEstatus().intValue()) {
              case 2: // ELIMINADA
                regresar= this.toDeleteNota(sesion);
                break;
              case 4: // CANCELADA
                // FALTA EL PROCESO PARA CANCELAR LAS CUENTAS POR PAGAR Y RECALCULAR LOS PRECIOS PROMEDIOS
                regresar= this.toCancelDeudas(sesion, this.orden.getIdEmpresa(), this.orden.getIdNotaEntrada());
                break;
            } // switch
					} // if
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
		} // catch		
		LOG.info("Se genero de forma correcta la nota de entrada: "+ this.orden.getConsecutivo());
		return regresar;
	}	// ejecutar

  private Boolean toDeleteNota(Session sesion) throws Exception {
    Boolean regresar                     = Boolean.FALSE;
		TcManticNotasBitacoraDto bitacoraNota= null;
		Map<String, Object> params           = new HashMap<>();
		try {
  		params.put("idNotaEntrada", this.orden.getIdNotaEntrada());
      regresar= this.toNotExistsArticulosBitacora(sesion);
      if(regresar) {
        this.toRemoveOrdenDetalle(sesion);
        DaoFactory.getInstance().deleteAll(sesion, TcManticNotasCostosDto.class, params);
        DaoFactory.getInstance().deleteAll(sesion, TcManticNotasDetallesDto.class, params);
        DaoFactory.getInstance().delete(sesion, this.orden);
        this.orden.setIdNotaEstatus(2L);
        bitacoraNota= new TcManticNotasBitacoraDto(-1L, null, JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), 2L, this.orden.getConsecutivo(), this.orden.getTotal());
        DaoFactory.getInstance().insert(sesion, bitacoraNota);
        this.toCheckOrden(sesion);
        this.toCheckDeleteFile(sesion);
      } // if
      else
        this.messageError= "No se puede eliminar la nota de entrada";
      regresar= Boolean.TRUE;
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(params);
		} // finally
    return regresar;
  }
          
	private void toFillArticulos(Session sesion) throws Exception {
	  StringBuilder error = new StringBuilder();
		List<Articulo> todos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(sesion, Articulo.class, "VistaNotasEntradasDto", "detalle", this.orden.toMap());
		Map<String, Object> params= new HashMap<>();
		try {
			for (Articulo item: todos) 
				if(this.articulos.indexOf(item)< 0) {
					this.toAffectOrdenDetalle(sesion, item);
					DaoFactory.getInstance().delete(sesion, TcManticNotasDetallesDto.class, item.getIdComodin());
				} // if
			for (Articulo articulo: this.articulos) {
				TcManticNotasDetallesDto item= articulo.toNotaDetalle();
				item.setIdNotaEntrada(this.orden.getIdNotaEntrada());
				if(item.getDiferencia()!= 0)
					error.append("[").append(item.getNombre()!= null && item.getNombre().length()> 20? item.getNombre().substring(0, 20): item.getNombre()).append(" - ").append(item.getDiferencia()).append("]</br> ");
        TcManticNotasDetallesDto detalle= (TcManticNotasDetallesDto)DaoFactory.getInstance().findIdentically(sesion, TcManticNotasDetallesDto.class, item.toMap());
				if((Objects.equals(detalle, null) && (Objects.equals(this.orden.getIdNotaTipo(), 4L) || articulo.getCantidad()> 0D || articulo.getCosto()> 0D))) {
					this.toAffectOrdenDetalle(sesion, articulo);
					if(!item.isValid()) 
						DaoFactory.getInstance().insert(sesion, item);
				  else
						if(articulo.isModificado())
							DaoFactory.getInstance().update(sesion, item);
					articulo.setObservacion("ARTICULO SURTIDO EN LA NOTA DE ENTRADA ".concat(this.orden.getConsecutivo()).concat(" EL DIA ").concat(Global.format(EFormatoDinamicos.FECHA_HORA_CORTA, this.orden.getRegistro())));
					// QUITAR DE LAS VENTAS PERDIDAS LOS ARTICULOS QUE FUERON YA SURTIDOS EN EL ALMACEN
					params.put("idArticulo", articulo.getIdArticulo());
					params.put("idEmpresa", this.orden.getIdEmpresa());
					params.put("observaciones", "ESTE ARTICULO FUE SURTIDO CON NO. NOTA DE ENTRADA "+ this.orden.getConsecutivo()+ " EL DIA "+ Global.format(EFormatoDinamicos.FECHA_HORA_CORTA, this.orden.getRegistro()));
					DaoFactory.getInstance().updateAll(sesion, TcManticFaltantesDto.class, params);
				} // if
			} // for
      sesion.flush();
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(todos);
			Methods.clean(params);
		} // finally
	}

	private void toRemoveOrdenDetalle(Session sesion) throws Exception {
		List<Articulo> todos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(sesion, Articulo.class, "VistaNotasEntradasDto", "detalle", this.orden.toMap());
		for (Articulo articulo: todos) {
			if(articulo.getIdOrdenDetalle()!= null && articulo.getIdOrdenDetalle()> 0L) {
				TcManticOrdenesDetallesDto detalle= (TcManticOrdenesDetallesDto)DaoFactory.getInstance().findById(sesion, TcManticOrdenesDetallesDto.class, articulo.getIdOrdenDetalle());
				detalle.setCantidades(detalle.getCantidades()+ articulo.getCantidad());
				detalle.setImportes(Numero.toRedondearSat(detalle.getImportes()+ articulo.getImporte()));
				detalle.setPrecios(Numero.toRedondearSat(detalle.getCosto()- articulo.getCosto()));
				DaoFactory.getInstance().update(sesion, detalle);
			} // if
		} // for
	}
	
	private void toAffectOrdenDetalle(Session sesion, Articulo articulo) throws Exception {
		if(articulo.getIdOrdenDetalle()!= null && articulo.getIdOrdenDetalle()> 0L) {
			TcManticOrdenesDetallesDto detalle= (TcManticOrdenesDetallesDto)DaoFactory.getInstance().findById(sesion, TcManticOrdenesDetallesDto.class, articulo.getIdOrdenDetalle());
      detalle.setCantidades(detalle.getCantidades()- articulo.getCantidad());
      detalle.setImportes(Numero.toRedondearSat(detalle.getImportes()- articulo.getImporte()));
      detalle.setPrecios(Numero.toRedondearSat(articulo.getValor()- articulo.getCosto()));
			DaoFactory.getInstance().update(sesion, detalle);
		} // if
	}
	
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", this.orden.getIdEmpresa());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticNotasEntradasDto", "siguiente", params, "siguiente");
			if(next.getData()!= null)
			  regresar= new Siguiente(next.toLong());
			else
			  regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
	private void toCheckOrden(Session sesion) throws Exception {
		try {
			sesion.flush();
			if(this.orden.getIdNotaTipo().equals(2L)) {
        TcManticOrdenesComprasDto ordenCompra= (TcManticOrdenesComprasDto)DaoFactory.getInstance().findById(sesion, TcManticOrdenesComprasDto.class, this.orden.getIdOrdenCompra());
  		  Value errors= DaoFactory.getInstance().toField(sesion, "VistaNotasEntradasDto", "errores", this.orden.toMap(), "total");
			  if(errors.toLong()!= null && errors.toLong()== 0) {
				  ordenCompra.setIdOrdenEstatus(6L); // TERMINADA
					this.toApplyNotaEntrada(sesion);
				} // if	
				else {
					ordenCompra.setIdOrdenEstatus(5L); // INCOMPLETA
   				if(this.aplicar)
  					this.toApplyNotaEntrada(sesion);
				} // else	
				DaoFactory.getInstance().update(sesion, ordenCompra);
				TcManticOrdenesBitacoraDto estatus= new TcManticOrdenesBitacoraDto(ordenCompra.getIdOrdenEstatus(), "", JsfBase.getIdUsuario(), ordenCompra.getIdOrdenCompra(), -1L, ordenCompra.getConsecutivo(), this.orden.getTotal());
				DaoFactory.getInstance().insert(sesion, estatus);
			} // if
			else
			  if((Objects.equals(this.orden.getIdNotaTipo(), 1L) || Objects.equals(this.orden.getIdNotaTipo(), 4L)) && this.aplicar) 
					this.toApplyNotaEntrada(sesion);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
	} 
	
	private boolean toNotExistsArticulosBitacora(Session sesion) throws Exception {
		boolean regresar= true;
		Value total= DaoFactory.getInstance().toField(sesion, "TcManticArticulosBitacoraDto", "existe", this.orden.toMap(), "total");
		if(total.getData()!= null)
		  regresar= total.toLong()<= 0;
		return regresar;
	}
	
	private void toApplyNotaEntrada(Session sesion) throws Exception {
		Map<String, Object> params= new HashMap<>();
		try {
			for (Articulo articulo: this.articulos) {
				TcManticNotasDetallesDto item= articulo.toNotaDetalle();
				item.setIdNotaEntrada(this.orden.getIdNotaEntrada());
				// Si la cantidad es mayor a cero realizar todo el proceso para el articulos, si no ignorarlo porque el articulo no se surtio
				if(articulo.getCantidad()> 0L)
					this.toAffectAlmacenes(sesion, this.orden.getConsecutivo(), this.orden, item, articulo);
			} // for
			this.orden.setIdNotaEstatus(3L);
			DaoFactory.getInstance().update(sesion, this.orden);
      // RECALCULAR LOS COSTOS PROMEDIOS 
      this.toCheckPromedios(sesion, this.orden.getIdEmpresa(), 1L);

			// Una vez que la nota de entrada es cambiada a terminada se registra la cuenta por cobrar
			TcManticEmpresasDeudasDto deuda= null;
      if(!Objects.equals(this.orden.getIdProveedor(), null)) {
        if(this.orden.getDiasPlazo()> 1) 
          deuda= new TcManticEmpresasDeudasDto(
            1L, // Long idEmpresaEstatus, 
            JsfBase.getIdUsuario(), // Long idUsuario,
            -1L, // Long idEmpresaDeuda,
            null, // String observaciones, 
            this.orden.getIdEmpresa(), // Long idEmpresa, 
            this.orden.getDeuda()- this.orden.getExcedentes(), // Double saldo, 
            this.orden.getIdNotaEntrada(), // Long idNotaEntrada, 
            this.orden.getFechaPago(), // Date limite, 
            this.orden.getDeuda(), // Double importe, 
            this.orden.getDeuda()- this.orden.getExcedentes(), // Double pagar, 
            2L, // Long idRevisado,
            Cadena.isVacio(this.orden.getFactura())? 1L: 2L, // Long idCompleto,
            null, // Date fechaRecepcion, 
            null, // Long idRecibio, 
            this.orden.getIdProveedorPago(), // Long idProveedorPago
            null, // Long idNotaCosto
            this.orden.getIdProveedor() // Long idProveedor
          );
        else
          deuda= new TcManticEmpresasDeudasDto(
            3L, // Long idEmpresaEstatus, 
            JsfBase.getIdUsuario(), // Long idUsuario,
            -1L, // Long idEmpresaDeuda,
            "ESTE DEUDA FUE LIQUIDADA EN EFECTIVO", // String observaciones, 
            this.orden.getIdEmpresa(),  // Long idEmpresa,
            0D, // Double saldo, 
            this.orden.getIdNotaEntrada(), // Long idNotaEntrada, 
            this.orden.getFechaPago(),  // Date limite, 
            this.orden.getDeuda(),  // Double importe,
            this.orden.getDeuda()- this.orden.getExcedentes(), // Double pagar,
            2L,  // Long idRevisado,
            Cadena.isVacio(this.orden.getFactura())? 1L: 2L, // Long idCompleto, 
            null, // Date fechaRecepcion,
            null, // Long idRecibio, 
            this.orden.getIdProveedorPago(), // Long idProveedorPago
            null, // Long idNotaCosto
            this.orden.getIdProveedor() // Long idProveedor
          );
        DaoFactory.getInstance().insert(sesion, deuda);
        TcManticEmpresasBitacoraDto registro= new TcManticEmpresasBitacoraDto(
          "SE REGISTRO LA DEUDA", // String justificacion, 
          deuda.getIdEmpresaEstatus(), // Long idEmpresaEstatus, 
          JsfBase.getIdUsuario(), // Long idUsuario, 
          deuda.getIdEmpresaDeuda(), // Long idEmpresaDeuda
          -1L // Long idEmpresaBitacora, 
        );
        DaoFactory.getInstance().insert(sesion, registro);
      } // if  
   		TcManticNotasBitacoraDto registro= new TcManticNotasBitacoraDto(-1L, null, JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), this.orden.getIdNotaEstatus(), this.orden.getConsecutivo(), this.orden.getTotal());
  		DaoFactory.getInstance().insert(sesion, registro);
      
      // FALTA AGREGAR LAS CUENTAS POR COBRAR DE LOS COSTOS 
      this.toAddCuentaPagar(sesion);
      
			if(!this.orden.getIdNotaTipo().equals(3L))
				this.toCommonNotaEntrada(sesion, this.orden.getIdNotaEntrada(), this.orden.toMap());
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
	private void toDeleteAll(String path, String type, List<Nombres> listado) {
    FileSearch fileSearch = new FileSearch();
    fileSearch.searchDirectory(new File(path), type.toLowerCase());
    if(fileSearch.getResult().size()> 0)
		  for (String matched: fileSearch.getResult()) {
				String name= matched.substring((matched.lastIndexOf("/")< 0? matched.lastIndexOf("\\"): matched.lastIndexOf("/"))+ 1);
				if(listado.indexOf(new Nombres(name))< 0) {
          LOG.warn("Nota crédito: "+ this.orden.getConsecutivo()+ " delete file: ".concat(matched));
				  // File file= new File(matched);
				  // file.delete();
				} // if
      } // for
	}
	
	private List<Nombres> toListFile(Session sesion, Importado tmp, Long idTipoArchivo) throws Exception {
		List<Nombres> regresar    = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idTipoArchivo", idTipoArchivo);
			params.put("ruta", tmp.getRuta());
			regresar= (List<Nombres>)DaoFactory.getInstance().toEntitySet(sesion, Nombres.class, "TcManticNotasArchivosDto", "listado", params);
			regresar.add(new Nombres(tmp.getName()));
		} // try 
		catch (Exception e) {
			Error.mensaje(e);
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} 
	
	protected void toUpdateDeleteXml(Session sesion) throws Exception {
		TcManticNotasArchivosDto tmp= null;
		if(this.orden.getIdNotaEntrada()!= -1L) {
			if(this.xml!= null) {
				tmp= new TcManticNotasArchivosDto(
					-1L,
					this.xml.getRuta(),
					this.xml.getFileSize(),
					JsfBase.getIdUsuario(),
					1L,
					Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.xml.getRuta()).concat(this.xml.getName()),
					new Long(Calendar.getInstance().get(Calendar.MONTH)+ 1),
					this.orden.getIdNotaEntrada(),
					this.xml.getName(),
					this.xml.getObservaciones(),
					new Long(Calendar.getInstance().get(Calendar.YEAR)),
					1L,
					this.xml.getOriginal(),
          this.xml.getIdTipoDocumento(),
          2L      
				);
				TcManticNotasArchivosDto exists= (TcManticNotasArchivosDto)DaoFactory.getInstance().toEntity(TcManticNotasArchivosDto.class, "TcManticNotasArchivosDto", "identically", tmp.toMap());
				File file= new File(tmp.getAlias());
				if(exists== null && file.exists()) {
					DaoFactory.getInstance().updateAll(sesion, TcManticNotasArchivosDto.class, tmp.toMap());
					DaoFactory.getInstance().insert(sesion, tmp);
				} // if
				else
				  if(!file.exists())
						LOG.warn("INVESTIGAR PORQUE NO EXISTE EL ARCHIVO EN EL SERVIDOR: "+ tmp.getAlias());
				sesion.flush();
        this.toCheckDeleteFile(sesion, this.xml.getName());
				// this.toDeleteAll(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.xml.getRuta()), ".".concat(this.xml.getFormat().name()), this.toListFile(sesion, this.xml, 1L));
			} // if	
			if(this.pdf!= null) {
				tmp= new TcManticNotasArchivosDto(
					-1L,
					this.pdf.getRuta(),
					this.pdf.getFileSize(),
					JsfBase.getIdUsuario(),
					2L,
					Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.pdf.getRuta()).concat(this.pdf.getName()),
					new Long(Calendar.getInstance().get(Calendar.MONTH)+ 1),
					this.orden.getIdNotaEntrada(),
					this.pdf.getName(),
					this.pdf.getObservaciones(),
					new Long(Calendar.getInstance().get(Calendar.YEAR)),
					1L,
					this.pdf.getOriginal(),
          this.pdf.getIdTipoDocumento(),
          2L
				);
				TcManticNotasArchivosDto exists= (TcManticNotasArchivosDto)DaoFactory.getInstance().toEntity(TcManticNotasArchivosDto.class, "TcManticNotasArchivosDto", "identically", tmp.toMap());
				File file= new File(tmp.getAlias());
				if(exists== null && file.exists()) {
					DaoFactory.getInstance().updateAll(sesion, TcManticNotasArchivosDto.class, tmp.toMap());
					DaoFactory.getInstance().insert(sesion, tmp);
				} // if
				else
				  if(!file.exists())
						LOG.warn("INVESTIGAR PORQUE NO EXISTE EL ARCHIVO EN EL SERVIDOR: "+ tmp.getAlias());
				sesion.flush();
        this.toCheckDeleteFile(sesion, this.pdf.getName());
				// this.toDeleteAll(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.pdf.getRuta()), ".".concat(this.pdf.getFormat().name()), this.toListFile(sesion, this.pdf, 2L));
			} // if	
			if(this.jpg!= null) {
				tmp= new TcManticNotasArchivosDto(
					-1L,
					this.jpg.getRuta(),
					this.jpg.getFileSize(),
					JsfBase.getIdUsuario(),
					17L,
					Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.jpg.getRuta()).concat(this.jpg.getName()),
					new Long(Calendar.getInstance().get(Calendar.MONTH)+ 1),
					this.orden.getIdNotaEntrada(),
					this.jpg.getName(),
					this.jpg.getObservaciones(),
					new Long(Calendar.getInstance().get(Calendar.YEAR)),
					1L,
					this.jpg.getOriginal(),
          this.jpg.getIdTipoDocumento(),
          2L
				);
				TcManticNotasArchivosDto exists= (TcManticNotasArchivosDto)DaoFactory.getInstance().toEntity(TcManticNotasArchivosDto.class, "TcManticNotasArchivosDto", "identically", tmp.toMap());
				File file= new File(tmp.getAlias());
				if(exists== null && file.exists()) {
					DaoFactory.getInstance().updateAll(sesion, TcManticNotasArchivosDto.class, tmp.toMap());
					DaoFactory.getInstance().insert(sesion, tmp);
				} // if
				else
				  if(!file.exists())
						LOG.warn("INVESTIGAR PORQUE NO EXISTE EL ARCHIVO EN EL SERVIDOR: "+ tmp.getAlias());
				sesion.flush();
        this.toCheckDeleteFile(sesion, this.jpg.getName());
			} // if	
  	} // if	
	}

	public void toDeleteXmlPdf() throws Exception {
		List<TcManticNotasArchivosDto> list= (List<TcManticNotasArchivosDto>)DaoFactory.getInstance().findViewCriteria(TcManticNotasArchivosDto.class, this.orden.toMap(), "all");
		if(list!= null)
			for (TcManticNotasArchivosDto item: list) {
				LOG.info("Nota entrada: "+ this.orden.getConsecutivo()+ " delete file: "+ item.getAlias());
				File file= new File(item.getAlias());
				file.delete();
			} // for
	}	

  private void checkConsecutivo(Session sesion) throws Exception {
    try {      
      if(!Objects.equals(this.orden.getIdEmpresa(), this.orden.getIdEmpresaBack())) {
        Siguiente consecutivo= this.toSiguiente(sesion);
        this.orden.setConsecutivo(consecutivo.getConsecutivo());
        this.orden.setOrden(consecutivo.getOrden());
        this.orden.setEjercicio(new Long(Fecha.getAnioActual()));      }
    } // try
    catch (Exception e) {
      throw e;      
    } // catch	
  }
  
	private Boolean toFillCostos(Session sesion) throws Exception {
    Boolean regresar        = Boolean.FALSE;
    List<Promedio> promedios= new ArrayList<>();
    try {
      for (Costo item: this.orden.getCostos()) {
        item.setIdUsuario(JsfBase.getIdUsuario());
        item.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        switch(item.getSql()) {
          case SELECT:
            break;
          case UPDATE:
            regresar= DaoFactory.getInstance().update(sesion, item)> 0L;
            break;
          case DELETE:
            regresar= DaoFactory.getInstance().delete(sesion, item)> 0L;
            break;
          case INSERT:
            item.setIdNotaEntrada(this.orden.getIdNotaEntrada());
            regresar= DaoFactory.getInstance().insert(sesion, item)> 0L;
            break;
        } // switch    
        if(!Objects.equals(item.getIdArticulo(), null)) {
          int index= promedios.indexOf(new Promedio(item.getIdArticulo()));
          if(index< 0)
            promedios.add(new Promedio(item.getIdArticulo(), item.getImporte()));
          else
            promedios.get(index).setTotal(promedios.get(index).getTotal()+ item.getImporte()); 
        } // if  
      } // for
      this.toCheckCostos(sesion, promedios);
      regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
      throw e;      
    } // catch	
    return regresar;
  }
  
	private Boolean toCheckCostos(Session sesion, List<Promedio> promedios) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    try {      
      // ESTE PROCESO RECALCULA EL PRECIO PROMEDIO DE LA NOTA DE ENTRADA
      params.put("idNotaEntrada", this.orden.getIdNotaEntrada());
      List<TcManticNotasDetallesDto> items= (List<TcManticNotasDetallesDto>)DaoFactory.getInstance().toEntitySet(sesion, TcManticNotasDetallesDto.class, "TcManticNotasDetallesDto", "igual", params);
      for (TcManticNotasDetallesDto item: items) {
        int index= promedios.indexOf(new Promedio(item.getIdArticulo()));
        if(index>= 0) {
          Promedio promedio= promedios.get(index);
          item.setPromedio((item.getImporte()+ promedio.getTotal())/ (item.getCantidad()<= 0? 1: item.getCantidad()));
          item.setGastos(promedio.getTotal());
        } // if
        else {
          item.setPromedio(item.getImporte()/ (item.getCantidad()<= 0? 1: item.getCantidad()));
          item.setGastos(0D);
        } // else
        DaoFactory.getInstance().update(sesion, item);
      } // for
      regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
      throw e;      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
  
	private Boolean toCheckPromedios(Session sesion, Long idEmpresa, Long idTipoMovimiento) throws Exception {
    return this.toCheckPromedios(sesion, idEmpresa, idTipoMovimiento, 1D);
  }
  
	private Boolean toCheckPromedios(Session sesion, Long idEmpresa, Long idTipoMovimiento, Double factor) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    try {      
      // ESTE PROCESO RECALCULA EL PRECIO PROMEDIO DE LO QUE SE TIENE EN LA SUCURSAL
      params.put("idNotaEntrada", this.orden.getIdNotaEntrada());
      List<TcManticNotasDetallesDto> items= (List<TcManticNotasDetallesDto>)DaoFactory.getInstance().toEntitySet(sesion, TcManticNotasDetallesDto.class, "TcManticNotasDetallesDto", "igual", params);
      for (TcManticNotasDetallesDto item: items) {
        TcManticArticulosPromediosDto promedio= new TcManticArticulosPromediosDto(
          idTipoMovimiento, // Long idTipoMovimiento, ENTRADA
          JsfBase.getIdUsuario(), // Long idUsuario, 
          item.getPromedio(), //Double promedio, 
          item.getCantidad(), // Double cantidad, 
          (Objects.equals(factor, 1D)? "SE REGISTRO": "SE CANCELO").concat(" LA NOTA DE ENTRADA ").concat(this.orden.getConsecutivo()), // String observaciones, 
          -1L, // Long idArticuloPromedio, 
          idEmpresa, // Long idEmpresa, 
          item.getIdArticulo(), // Long idArticulo, 
          item.getImporte()+ item.getGastos()// Double importe
        );
        params.put("idEmpresa", idEmpresa);
        params.put("idArticulo", item.getIdArticulo());
        Entity costo= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcManticArticulosPromediosDto", "ultimo", params);
        if(!Objects.equals(costo, null) && !costo.isEmpty()) {
          Double cantidad= item.getCantidad()* factor;
          Double importe = promedio.getImporte()* factor;
          promedio.setCantidad(costo.toDouble("cantidad")+ cantidad);
          promedio.setImporte(costo.toDouble("importe")+ importe);
          promedio.setPromedio(promedio.getImporte()/ (promedio.getCantidad()<= 0? 1D: promedio.getCantidad()));
        } // if
        DaoFactory.getInstance().insert(sesion, promedio);
      } // for
      regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
      throw e;      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
  
  private Boolean toAddCuentaPagar(Session sesion) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    Long dias                 = 15L;
    try {      
      for (Costo item: this.orden.getCostos()) {
        if(!Objects.equals(item.getIdProveedor(), null) && Objects.equals(item.getIdGenerar(), 1L)) {
          params.put("idProveedor", item.getIdProveedor());
          Entity fecha= (Entity)DaoFactory.getInstance().toEntity(sesion, "TrManticProveedorPagoDto", "dias", params);
          if(!Objects.equals(fecha, null) && !fecha.isEmpty())
            dias= fecha.toLong("plazo");
          TcManticEmpresasDeudasDto deuda= new TcManticEmpresasDeudasDto(
            1L, // Long idEmpresaEstatus, 
            JsfBase.getIdUsuario(), // Long idUsuario, 
            -1L, // Long idEmpresaDeuda, 
            "GASTO ".concat(item.getNombre()).concat(" | ").concat(item.getArticulo()), // String observaciones, 
            this.orden.getIdEmpresa(), // Long idEmpresa, 
            item.getImporte(), // Double saldo, 
            this.orden.getIdNotaEntrada(), // Long idNotaEntrada, 
            Fecha.toFecha(dias.intValue()), // Date limite, 
            item.getImporte(), // Double importe, 
            item.getImporte(), // Double pagar, 
            2L, // Long idRevisado, 
            2L, // Long idCompleto, 
            null, // Date fechaRecepcion, 
            null, // Long idRecibio, 
            Objects.equals(fecha, null) && !fecha.isEmpty()? fecha.toLong("idProveedorPago"): null, // Long idProveedorPago
            item.getIdNotaCosto(), // Long idNotaCosto
            item.getIdProveedor() // Long idProveedor
          );
          DaoFactory.getInstance().insert(sesion, deuda);
          TcManticEmpresasBitacoraDto registro= new TcManticEmpresasBitacoraDto(
            "SE REGISTRO LA DEUDA", // String justificacion, 
            deuda.getIdEmpresaEstatus(), // Long idEmpresaEstatus, 
            JsfBase.getIdUsuario(), // Long idUsuario, 
            deuda.getIdEmpresaDeuda(), // Long idEmpresaDeuda
            -1L // Long idEmpresaBitacora, 
          );
          DaoFactory.getInstance().insert(sesion, registro);
        } // if  
      } // for
      regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
      throw e;      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
  
  private Boolean toCancelDeudas(Session sesion, Long idEmpresa, Long idNotaEntrada) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    StringBuilder sb          = new StringBuilder();
    try {      
      params.put("idNotaEntrada", idNotaEntrada);
      List<Entity> deudas= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "VistaEmpresasDto", "cancela", params);
      for (Entity item: deudas) {
        if(!Objects.equals(item.toLong("idEmpresaEstatus"), 1L)) {
          sb.append("[").append(item.toString("consecutivo")).append(Constantes.SEPARADOR).append(Numero.formatear(Numero.MILES_CON_DECIMALES, item.toDouble("importe"))).append(Constantes.SEPARADOR).append(item.toString("proveedor")).append("], ");
        } // if
        else {
          TcManticEmpresasDeudasDto deuda= (TcManticEmpresasDeudasDto)DaoFactory.getInstance().findById(sesion, TcManticEmpresasDeudasDto.class, idProveedor);
          deuda.setIdEmpresaEstatus(5L);
          DaoFactory.getInstance().update(sesion, deuda);
          TcManticEmpresasBitacoraDto registro= new TcManticEmpresasBitacoraDto(
            "SE CANCELO LA NOTA DE ENTRADA ".concat(item.toString("consecutivo")), // String justificacion, 
            deuda.getIdEmpresaEstatus(), // Long idEmpresaEstatus, 
            JsfBase.getIdUsuario(), // Long idUsuario, 
            deuda.getIdEmpresaDeuda(), // Long idEmpresaDeuda
            -1L // Long idEmpresaBitacora, 
          );
          DaoFactory.getInstance().insert(sesion, registro);
        } // if
      } // for
      if(sb.length()> 0) {
        sb.delete(sb.length()- 2, sb.length());
        sb.insert(0, "Las CxP ").append(" no fueron canceladas porque tienen un pago registrado !");
        this.messageError= sb.toString();
        throw new RuntimeException(this.messageError);
      } // if 
      else 
        this.toCheckPromedios(sesion, idEmpresa, 1L, -1D);
      regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
      throw e;      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
  
} 