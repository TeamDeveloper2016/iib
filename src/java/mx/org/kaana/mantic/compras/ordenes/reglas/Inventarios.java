package mx.org.kaana.mantic.compras.ordenes.reglas;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kalan.db.dto.TcKalanValesDetallesDto;
import mx.org.kaana.kalan.vales.beans.Vale;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.db.dto.TcManticAlmacenesArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticAlmacenesUbicacionesDto;
import mx.org.kaana.mantic.db.dto.TcManticArticulosBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticArticulosCodigosDto;
import mx.org.kaana.mantic.db.dto.TcManticArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticEmpresasDeudasDto;
import mx.org.kaana.mantic.db.dto.TcManticInventariosDto;
import mx.org.kaana.mantic.db.dto.TcManticMovimientosDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasEntradasDto;
import org.hibernate.Session;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 15/06/2018
 *@time 09:59:37 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public abstract class Inventarios extends IBaseTnx implements Serializable {

	private static final long serialVersionUID=-3572599320057390307L;
	
	private Long idAlmacen;
	protected Long idProveedor;

	public Inventarios(Long idAlmacen, Long idProveedor) {
		this.idAlmacen  = idAlmacen;
		this.idProveedor= idProveedor;
	}
	
	protected void toAffectAlmacenes(Session sesion, String consecutivo, Vale vale, TcKalanValesDetallesDto item) throws Exception {
		Map<String, Object> params= new HashMap<>();
		double stock= 0D;
		try {
			params.put("idAlmacen", this.idAlmacen);
			params.put("idArticulo", item.getIdArticulo());
			TcManticAlmacenesArticulosDto ubicacion= (TcManticAlmacenesArticulosDto)DaoFactory.getInstance().findFirst(sesion, TcManticAlmacenesArticulosDto.class,  params, "ubicacion");
			if(ubicacion== null) {
			  TcManticAlmacenesUbicacionesDto general= (TcManticAlmacenesUbicacionesDto)DaoFactory.getInstance().findFirst(sesion, TcManticAlmacenesUbicacionesDto.class, params, "general");
				if(general== null) {
  				general= new TcManticAlmacenesUbicacionesDto("GENERAL", "", "GENERAL", "", "", JsfBase.getAutentifica().getPersona().getIdUsuario(), this.idAlmacen, -1L);
					DaoFactory.getInstance().insert(sesion, general);
				} // if	
			  Entity entity= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcManticArticulosDto", "inventario", params);
				TcManticAlmacenesArticulosDto articulo= new TcManticAlmacenesArticulosDto(entity.toDouble("minimo"), -1L, general.getIdUsuario(), general.getIdAlmacen(), entity.toDouble("maximo"), general.getIdAlmacenUbicacion(), item.getIdArticulo(), item.getCantidad());
				DaoFactory.getInstance().insert(sesion, articulo);
		  } // if
			else { 
				stock= ubicacion.getStock();
				ubicacion.setStock(ubicacion.getStock()- item.getCantidad());
				DaoFactory.getInstance().update(sesion, ubicacion);
			} // if

			// generar un registro en la bitacora de movimientos de los articulos 
			TcManticMovimientosDto entrada= new TcManticMovimientosDto(
			  consecutivo, // String consecutivo, 
				8L, // Long idTipoMovimiento, 
				JsfBase.getIdUsuario(), // Long idUsuario, 
				this.idAlmacen, // Long idAlmacen, 
				-1L, // Long idMovimiento, 
				item.getCantidad(), // Double cantidad, 
				item.getIdArticulo(), // Long idArticulo, 
				stock, // Double stock, 
				Numero.toRedondearSat(stock- item.getCantidad()), // Double calculo
				null // String observaciones
		  );
			DaoFactory.getInstance().insert(sesion, entrada);
			
			// afectar el inventario general de articulos dentro del almacen
			TcManticInventariosDto inventario= (TcManticInventariosDto)DaoFactory.getInstance().findFirst(sesion, TcManticInventariosDto.class, "inventario", params);
			if(inventario== null)
				DaoFactory.getInstance().insert(sesion, 
          new TcManticInventariosDto(
            JsfBase.getIdUsuario(), 
            this.idAlmacen, // idAlmacen
            item.getCantidad(), // entrada
            -1L, //idInventario
            item.getIdArticulo(), // idArticulo
            0D,  // inicial
            item.getCantidad(), // stock
            0D, // salida
            new Long(Calendar.getInstance().get(Calendar.YEAR)), // ejercicio
            1L)); // idAutomatico
			else {
				inventario.setEntradas(inventario.getEntradas()- item.getCantidad());
				inventario.setStock((inventario.getStock()< 0D? 0D: inventario.getStock())- item.getCantidad());
				DaoFactory.getInstance().update(sesion, inventario);
			} // else
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	}	
  
	protected void toAffectAlmacenes(Session sesion, String consecutivo, TcManticNotasEntradasDto notaEntrada, TcManticNotasDetallesDto item, Articulo codigos) throws Exception {
		Map<String, Object> params= new HashMap<>();
		double stock= 0D;
		try {
			params.put("idAlmacen", this.idAlmacen);
			params.put("idArticulo", item.getIdArticulo());
			params.put("idProveedor", this.idProveedor);
			TcManticAlmacenesArticulosDto ubicacion= (TcManticAlmacenesArticulosDto)DaoFactory.getInstance().findFirst(sesion, TcManticAlmacenesArticulosDto.class,  params, "ubicacion");
			if(ubicacion== null) {
			  TcManticAlmacenesUbicacionesDto general= (TcManticAlmacenesUbicacionesDto)DaoFactory.getInstance().findFirst(sesion, TcManticAlmacenesUbicacionesDto.class, params, "general");
				if(general== null) {
  				general= new TcManticAlmacenesUbicacionesDto("GENERAL", "", "GENERAL", "", "", JsfBase.getAutentifica().getPersona().getIdUsuario(), this.idAlmacen, -1L);
					DaoFactory.getInstance().insert(sesion, general);
				} // if	
			  Entity entity= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcManticArticulosDto", "inventario", params);
				TcManticAlmacenesArticulosDto articulo= new TcManticAlmacenesArticulosDto(entity.toDouble("minimo"), -1L, general.getIdUsuario(), general.getIdAlmacen(), entity.toDouble("maximo"), general.getIdAlmacenUbicacion(), item.getIdArticulo(), item.getCantidad());
				DaoFactory.getInstance().insert(sesion, articulo);
		  } // if
			else { 
				stock= ubicacion.getStock();
				ubicacion.setStock(ubicacion.getStock()+ item.getCantidad());
				DaoFactory.getInstance().update(sesion, ubicacion);
			} // if

			// generar un registro en la bitacora de movimientos de los articulos 
			TcManticMovimientosDto entrada= new TcManticMovimientosDto(
			  consecutivo, // String consecutivo, 
				1L, // Long idTipoMovimiento, 
				JsfBase.getIdUsuario(), // Long idUsuario, 
				this.idAlmacen, // Long idAlmacen, 
				-1L, // Long idMovimiento, 
				item.getCantidad(), // Double cantidad, 
				item.getIdArticulo(), // Long idArticulo, 
				stock, // Double stock, 
				Numero.toRedondearSat(stock+ item.getCantidad()), // Double calculo
				null // String observaciones
		  );
			DaoFactory.getInstance().insert(sesion, entrada);
			
			// registar el cambio de precios en la bitacora de articulo 
			TcManticArticulosDto global= (TcManticArticulosDto)DaoFactory.getInstance().findById(sesion, TcManticArticulosDto.class, item.getIdArticulo());
			
			TcManticArticulosBitacoraDto movimiento= new TcManticArticulosBitacoraDto(global.getIva(), JsfBase.getIdUsuario(), global.getMayoreo(), -1L, global.getMenudeo(), global.getCantidad(), global.getIdArticulo(), notaEntrada.getIdNotaEntrada(), global.getMedioMayoreo(), global.getPrecio(), global.getLimiteMedioMayoreo(), global.getLimiteMayoreo(), global.getDescuento(), global.getExtra(), global.getEspecial());			
			DaoFactory.getInstance().insert(sesion, movimiento);
			
			// afectar el inventario general de articulos dentro del almacen
			TcManticInventariosDto inventario= (TcManticInventariosDto)DaoFactory.getInstance().findFirst(sesion, TcManticInventariosDto.class, "inventario", params);
			if(inventario== null)
				DaoFactory.getInstance().insert(sesion, 
          new TcManticInventariosDto(
            JsfBase.getIdUsuario(), 
            this.idAlmacen, // idAlmacen
            item.getCantidad(), // entrada
            -1L, //idInventario
            item.getIdArticulo(), // idArticulo
            0D,  // inicial
            item.getCantidad(), // stock
            0D, // salida
            new Long(Calendar.getInstance().get(Calendar.YEAR)), // ejercicio
            1L)); // idAutomatico
			else {
				inventario.setEntradas(inventario.getEntradas()+ item.getCantidad());
				inventario.setStock((inventario.getStock()< 0D? 0D: inventario.getStock())+ item.getCantidad());
				DaoFactory.getInstance().update(sesion, inventario);
			} // else
			
			// afectar los precios del catalogo de articulos
			if(!Cadena.isVacio(codigos.getSat()))
			  global.setSat(codigos.getSat());
			// esto aplica para cuando el precio que llega es mayor al registrado dejar el nuevo
			Descuentos descuentos= new Descuentos(item.getCosto(), item.getDescuento());
			double costo= descuentos.getFactor()== 0D? item.getCosto(): descuentos.toImporte();
      
      // SI SE ELIGIO CALCULAR A PRECIOS NETOS ENTONCES SE DEBE DE CALCULAR EL COSTO MENOS EL IVA
      if(Objects.equals(1L, notaEntrada.getIdSinIva())) 
        costo= Numero.toRedondearSat(costo/ (1+ (global.getIva()/ 100)));
        
			// si esta marcado como afectar los costos se aplicara el cambio en el catalogo de articulos
			if(codigos.getIdAplicar().equals(1L) || costo> global.getPrecio()) {
				// aplicar el descuento sobre el valor del costo del articulo para afectar el catalogo
				double menudeo= Numero.toRedondearSat((global.getMenudeo()* 100/ global.getPrecio())/ 100);
				double medio  = Numero.toRedondearSat((global.getMedioMayoreo()* 100/ global.getPrecio())/ 100);
				double mayoreo= Numero.toRedondearSat((global.getMayoreo()* 100/ global.getPrecio())/ 100);
				double especial= Numero.toRedondearSat((global.getEspecial()* 100/ global.getPrecio())/ 100);
				
			  global.setPrecio(Numero.toRedondearSat(costo));
			  global.setMenudeo(Numero.toAjustarDecimales(global.getPrecio()* menudeo, global.getIdRedondear().equals(1L)));
			  global.setMedioMayoreo(Numero.toAjustarDecimales(global.getPrecio()* medio, global.getIdRedondear().equals(1L)));
			  global.setMayoreo(Numero.toAjustarDecimales(global.getPrecio()* mayoreo, global.getIdRedondear().equals(1L)));
			  global.setEspecial(Numero.toAjustarDecimales(global.getPrecio()* especial, global.getIdRedondear().equals(1L)));
				global.setDescuento(item.getDescuento());
				global.setExtra(item.getExtras());
			} // if	
			else {
			  global.setPrecio(Numero.toRedondearSat(costo));
				// ajustar solo los decimales cuando sea redondear 
				if(global.getIdRedondear().equals(1L)) {
					global.setMenudeo(Numero.toAjustarDecimales(global.getMenudeo(), true));
					global.setMedioMayoreo(Numero.toAjustarDecimales(global.getMedioMayoreo(), true));
					global.setMayoreo(Numero.toAjustarDecimales(global.getMayoreo(), true));
					global.setEspecial(Numero.toAjustarDecimales(global.getEspecial(), true));
				} // if
			} // else
			// siempre se modifica el costo del catalogo de articulo 
			global.setActualizado(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			global.setStock(global.getStock()+ item.getCantidad());
			DaoFactory.getInstance().update(sesion, global);
			
			// afectar el catalogo de codigos del proveedor y si se encuentra entonces actualizarlo en caso de ser diferente al que se tenia
			if(!Cadena.isVacio(codigos.getCodigo())) {
				params.put("codigo", codigos.getCodigo());
				TcManticArticulosCodigosDto remplazo= (TcManticArticulosCodigosDto)DaoFactory.getInstance().findFirst(sesion, TcManticArticulosCodigosDto.class, "proveedor", params);
				// si el codigo ya lo tiene el proveedor ya no hacer nada de nada porque es el mismo
				if(remplazo== null) {
					remplazo= (TcManticArticulosCodigosDto)DaoFactory.getInstance().findFirst(sesion, TcManticArticulosCodigosDto.class, "codigo", params);
					// si el codigo es diferente el que se tiene registrado al que tiene el proveedor actualizarlo
					if(remplazo== null) {
						Value next= DaoFactory.getInstance().toField(sesion, "TcManticArticulosCodigosDto", "siguiente", params, "siguiente");
						if(next.getData()== null)
							next.setData(1L);
						TcManticArticulosCodigosDto clon= new TcManticArticulosCodigosDto(
							codigos.getCodigo(), // String codigo, 
							this.idProveedor, // Long idProveedor, 
							JsfBase.getIdUsuario(), // Long idUsuario, 
							2L, // Long idPrincipal, 
							null, // String observaciones, 
							-1L, // Long idArticuloCodigo, 
							next.toLong(), // Long orden, 
							item.getIdArticulo(), // Long idArticulo, 
							1L, // Long multiplo, 
							Cadena.isVacio(codigos.getOrigen())? null: codigos.getOrigen().toUpperCase() // String nombre
						);
						DaoFactory.getInstance().insert(sesion, clon);
					} // if	
					else { 
						if(!Objects.equals(remplazo.getCodigo(), codigos.getCodigo()))
							remplazo.setCodigo(codigos.getCodigo());
					  remplazo.setNombre(Cadena.isVacio(codigos.getOrigen())? null: codigos.getOrigen().toUpperCase());
						DaoFactory.getInstance().update(sesion, remplazo);
					} // if	
				} // if
				else {
					remplazo.setNombre(Cadena.isVacio(codigos.getOrigen())? null: codigos.getOrigen().toUpperCase());
					DaoFactory.getInstance().update(sesion, remplazo);
				} // else
			} // if	
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	}	

	protected void toCommonNotaEntrada(Session sesion, Long idNotaEntrada, Map<String, Object> params) throws Exception {
		List<TcManticNotasEntradasDto> notas= (List<TcManticNotasEntradasDto>)DaoFactory.getInstance().findViewCriteria(sesion, TcManticNotasEntradasDto.class, params, "consulta");
		// Recuperar todas las notas de entrada abiertas para cerrarlas y aplicarlas
		for (TcManticNotasEntradasDto nota: notas) {
			if(!nota.getIdNotaEstatus().equals(3L) && !nota.getIdNotaEntrada().equals(idNotaEntrada)) {
				nota.setIdNotaEstatus(3L);
				DaoFactory.getInstance().update(sesion, nota);
				TcManticNotasBitacoraDto registro= new TcManticNotasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), nota.getIdNotaEntrada(), nota.getIdNotaEstatus(), nota.getConsecutivo(), nota.getTotal());
				DaoFactory.getInstance().insert(sesion, registro);
        
				// Afectar todas las notas de entrada que ya fueron aceptadas para agregarlas como cuentas por pagar
				TcManticEmpresasDeudasDto deuda= new TcManticEmpresasDeudasDto(1L, JsfBase.getIdUsuario(), -1L, "", JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), nota.getDeuda(), nota.getIdNotaEntrada(), nota.getFechaPago(), nota.getDeuda()- nota.getExcedentes(), nota.getDeuda()- nota.getExcedentes(), 2L, Cadena.isVacio(nota.getFactura())? 1L: 2L, null, null, nota.getIdProveedorPago(), null, nota.getIdProveedor());
				DaoFactory.getInstance().insert(sesion, deuda);
				
    		// Recuperar el detalle de las notas de entrada para afectas inventarios 
    		List<Articulo> todos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(sesion, Articulo.class, "VistaNotasEntradasDto", "detalle", nota.toMap());
				for (Articulo articulo: todos) {
					TcManticNotasDetallesDto item= articulo.toNotaDetalle();
					// Si la cantidad es mayor a cero realizar todo el proceso para el articulos, si no ignorarlo porque el articulo no se surtio
					if(articulo.getCantidad()> 0L)
    		    this.toAffectAlmacenes(sesion, nota.getConsecutivo(), nota, item, articulo);
				} // for
			} // if	
		} // for
	}
	
}
