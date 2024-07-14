package mx.org.kaana.mantic.catalogos.almacenes.transferencias.reglas;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.db.dto.TcManticArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticFaltantesDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasEntradasDto;
import mx.org.kaana.mantic.db.dto.TcManticTransferenciasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticTransferenciasDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticTransferenciasDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends ComunInventarios {

	private static final Log LOG= LogFactory.getLog(Transaccion.class);
	
  private TcManticTransferenciasDto dto;
	private Long idTransferenciaEstatus;
	private Long idFaltante;

	public Transaccion(Long idFaltante) {
		this(new TcManticTransferenciasDto(-1L));
		this.idFaltante= idFaltante;
	}
	
	public Transaccion(TcManticTransferenciasDto dto) {
		this(dto, 1L);
	}
	
	public Transaccion(TcManticTransferenciasDto dto, TcManticTransferenciasBitacoraDto bitacora) {
		this(dto, bitacora.getIdTransferenciaEstatus());
		this.bitacora = bitacora;
	}
	
	public Transaccion(TcManticTransferenciasDto dto, List<Articulo> articulos) {
		this(dto, dto.getIdTransferenciaEstatus());
		this.articulos= articulos;
	}
	
	public Transaccion(TcManticTransferenciasDto dto, Long idTransferenciaEstatus) {
		this.dto= dto;
		if(this.dto.getIdSolicito()!= null && this.dto.getIdSolicito()< 0L)
		  this.dto.setIdSolicito(null);
		this.idTransferenciaEstatus= idTransferenciaEstatus;
	}

  @Override
  protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
    boolean regresar= false;
    try {			
    	this.messageError    = "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" para transferencia de articulos.");
  		Siguiente consecutivo= null;
      switch (accion) {
        case GENERAR:
        case PROCESAR:
        case ACTIVAR:
        case AGREGAR:
					consecutivo= this.toSiguiente(sesion);
					this.dto.setConsecutivo(consecutivo.getConsecutivo());
					this.dto.setOrden(consecutivo.getOrden());
          DaoFactory.getInstance().insert(sesion, this.dto);
					this.toFillArticulos(sesion, accion);
					this.bitacora= new TcManticTransferenciasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), null, this.dto.getIdTransferenciaEstatus(), this.dto.getIdTransferencia());
          regresar= DaoFactory.getInstance().insert(sesion, bitacora).intValue()> 0;
          if(Objects.equals(EAccion.GENERAR, accion)) 
            this.toNewNotaEntrada(sesion);
					break;
        case MODIFICAR:
          this.dto.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
					this.toFillArticulos(sesion, accion);
          regresar= DaoFactory.getInstance().update(sesion, this.dto).intValue()> 0;
          break;
        case ELIMINAR:
          //if (DaoFactory.getInstance().deleteAll(sesion, TcManticTransferenciasBitacoraDto.class, this.dto.toMap())> -1L) 
					this.dto.setIdTransferenciaEstatus(2L);
          regresar= DaoFactory.getInstance().update(sesion, this.dto)>= 1L;
					this.bitacora= new TcManticTransferenciasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), null, this.dto.getIdTransferenciaEstatus(), this.dto.getIdTransferencia());
					if(regresar)
            regresar= DaoFactory.getInstance().insert(sesion, bitacora).intValue()> 0;
          break;
				case DEPURAR:
					regresar= DaoFactory.getInstance().delete(sesion, TcManticFaltantesDto.class, this.idFaltante)>= 1L;
					break;
        case REGISTRAR:
      		this.articulos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(Articulo.class, "VistaAlmacenesTransferenciasDto", "detalle", dto.toMap());
					this.dto.setIdTransferenciaEstatus(this.idTransferenciaEstatus);
					if(this.idTransferenciaEstatus!= 1L)
					  this.toFillArticulos(sesion, accion);
					regresar= DaoFactory.getInstance().update(sesion, this.dto).intValue()> 0;
					if(regresar)
            regresar= DaoFactory.getInstance().insert(sesion, this.bitacora).intValue()> 0;
          // throw new RuntimeException("ERROR PROVACADO INTENCIONALMENTE");
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
    return regresar;
  } // ejecutar

	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", this.dto.getIdEmpresa());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticTransferenciasDto", "siguiente", params, "siguiente");
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

	private void toFillArticulos(Session sesion, EAccion accion) throws Exception {
		List<Articulo> todos=(List<Articulo>) DaoFactory.getInstance().toEntitySet(sesion, Articulo.class, "VistaAlmacenesTransferenciasDto", "detalle", this.dto.toMap());
		for (Articulo item: todos) 
			if (this.articulos.indexOf(item)< 0) 
				DaoFactory.getInstance().delete(sesion, item.toTransferenciaDetalle());
		for (Articulo articulo: this.articulos) {
			TcManticTransferenciasDetallesDto item= articulo.toTransferenciaDetalle();
			item.setIdTransferencia(this.dto.getIdTransferencia());
			if (DaoFactory.getInstance().findIdentically(sesion, TcManticTransferenciasDetallesDto.class, item.toMap())== null) 
				DaoFactory.getInstance().insert(sesion, item);
			else 
				DaoFactory.getInstance().update(sesion, item);
			if(Objects.equals(EAccion.ACTIVAR, accion)) 
				this.toMovimientos(sesion, this.dto.getConsecutivo(), this.dto.getIdAlmacen(), this.dto.getIdDestino(), articulo, this.idTransferenciaEstatus);
			else
			  if(Objects.equals(EAccion.PROCESAR, accion))
				  this.toAlmacenOrigen(sesion, this.dto.getConsecutivo(), this.dto.getIdAlmacen(), this.dto.getIdDestino(), articulo, this.idTransferenciaEstatus);
        else
          if(Objects.equals(EAccion.GENERAR, accion))
            this.toAlmacenTerminado(sesion, this.dto.getConsecutivo(), this.dto.getIdAlmacen(), this.dto.getIdDestino(), articulo, this.idTransferenciaEstatus);
          else
            if(EAccion.REGISTRAR.equals(accion)) {
              TcManticArticulosDto umbrales= (TcManticArticulosDto)DaoFactory.getInstance().findById(TcManticArticulosDto.class, articulo.getIdArticulo());
              articulo.setSolicitados(articulo.getCantidad());
              switch(this.idTransferenciaEstatus.intValue()) {
                case 3: // TRANSITO
                  this.toMovimientosAlmacenOrigen(sesion, this.dto.getConsecutivo(), this.dto.getIdAlmacen(), articulo, umbrales, this.idTransferenciaEstatus);
                  break;
                case 4: // CANCELAR
                  this.toMovimientosAlmacenOrigen(sesion, this.dto.getConsecutivo(), this.dto.getIdAlmacen(), articulo, umbrales, this.idTransferenciaEstatus);
                  break;
                case 5: // RECEPCION
                  this.toMovimientosAlmacenDestino(sesion, this.dto.getConsecutivo(), this.dto.getIdDestino(), articulo, umbrales, articulo.getCantidad());
                  break;
              } // switch
            } // if
		} // for
	}

  private Boolean toNewNotaEntrada(Session sesion) throws Exception {
    Boolean regresar          = Boolean.FALSE;
		Map<String, Object> params= new HashMap<>();
    Siguiente consecutivo     = null;
    TcManticNotasBitacoraDto estatus= null;
		try {
      TcManticNotasEntradasDto orden= new TcManticNotasEntradasDto(
        0D, // Double descuentos, 
        1L, // Long idProveedor, 
        "0.00", // String descuento, 
        null, // Long idOrdenCompra, 
        5L, // Long idNotaTipo, 
        new Date(Calendar.getInstance().getTimeInMillis()), // Date fechaRecepcion, 
        "0.00", // String extras, 
        -1L, // Long idNotaEntrada, 
        new Date(Calendar.getInstance().getTimeInMillis()), // Date fechaFactura, 
        3L, // Long idNotaEstatus, 
        new Long(Fecha.getAnioActual()), // Long ejercicio, 
        "", // String consecutivo, 
        0D, // Double total, 
        null, // String factura, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        this.dto.getIdDestino(), // Long idAlmacen, 
        0D, // Double subTotal, 
        0D, // Double impuestos, 
        1D, // Double tipoDeCambio, 
        2L, // Long idSinIva, 
        "NOTA GENERADA DE FORMA AUTOMATICA", // String observaciones, 
        dto.getIdEmpresa(), // Long idEmpresa, 
        0L, // Long orden, 
        0D, // Double excedentes, 
        30L, // Long diasPlazo, 
        new Date(Calendar.getInstance().getTimeInMillis()), // Date fechaPago, 
        0D, // Double deuda, 
        1L, // Long idProveedorPago, 
        0D // Double original              
      );
      consecutivo= this.toSiguiente(sesion, orden.getIdEmpresa());
      orden.setConsecutivo(consecutivo.getConsecutivo());
      orden.setOrden(consecutivo.getOrden());
      orden.setEjercicio(new Long(Fecha.getAnioActual()));
      DaoFactory.getInstance().insert(sesion, orden);
		  estatus= new TcManticNotasBitacoraDto(
        -1L, // Long idNotaBitacora, 
        "SE GENERO DE FORMA AUTOMATICA", // String justificacion, 
        orden.getIdUsuario(), // Long idUsuario, 
        orden.getIdNotaEntrada(), // Long idNotaEntrada, 
        orden.getIdNotaEstatus(), // Long idNotaEstatus, 
        orden.getConsecutivo(), // String consecutivo, 
        orden.getTotal() // Double importe
      );
			DaoFactory.getInstance().insert(sesion, estatus);
      for (Articulo articulo: this.articulos) {
        TcManticNotasDetallesDto item= new TcManticNotasDetallesDto(
          articulo.getCodigo(), // String codigo, 
          "KILOGRAMO", // String unidadMedida, 
          0D, // Double costo, 
          "0.00", // String descuento, 
          articulo.getSat(), // String sat, 
          "0.00", // String extras, 
          orden.getIdNotaEntrada(), //  Long idNotaEntrada, 
          articulo.getNombre(), // String nombre, 
          0D, // Double importe, 
          0D, // Double iva, 
          -1L, // Long idNotaDetalle, 
          0D, // Double subTotal, 
          articulo.getCantidad(), // Double cantidad, 
          articulo.getIdOrdenDetalle(), //  Long idArticulo, 
          0D, // Double descuentos, 
          0D, // Double impuestos, 
          null, // Long idOrdenDetalle, 
          articulo.getCantidad(), // Double cantidades, 
          0D, // Double excedentes, 
          2L, // Long idAplicar, 
          0D, // Double declarados, 
          articulo.getCantidad(), // Double diferencia, 
          0D, // Double costoReal, 
          0D, // Double costoCalculado, 
          null, // String origen, 
          0D, // Double promedio, 
          0D, // Double gastos, 
          1D // Double costales              
        );
        DaoFactory.getInstance().insert(sesion, item);
      } // for
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
    return regresar;
  }

	private Siguiente toSiguiente(Session sesion, Long idEmpresa) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", idEmpresa);
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
  
}
