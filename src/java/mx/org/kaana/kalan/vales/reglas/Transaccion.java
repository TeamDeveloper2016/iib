package mx.org.kaana.kalan.vales.reglas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.hibernate.Session;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.kalan.db.dto.TcKalanValesBitacoraDto;
import mx.org.kaana.kalan.db.dto.TcKalanValesDetallesDto;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.kalan.vales.beans.Vale;
import mx.org.kaana.mantic.compras.ordenes.reglas.Inventarios;
import mx.org.kaana.mantic.db.dto.TcManticFaltantesDto;
import mx.org.kaana.kalan.db.dto.TcKalanValesDetallesDto;
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
	private static final long serialVersionUID=-3186367186737677670L;
 
	private Vale orden;	
	private List<Articulo> articulos;
	private String messageError;
	private TcKalanValesBitacoraDto bitacora;
	private Long idFaltante;

	public Transaccion(Vale orden, TcKalanValesBitacoraDto bitacora) {
		this(orden);
		this.bitacora= bitacora;
	} // Transaccion
	
	public Transaccion(Vale orden) {
		this(orden, new ArrayList<Articulo>());
	} // Transaccion

	public Transaccion(Vale orden, List<Articulo> articulos) {
		super(orden.getIdAlmacen(), -1L);
		this.orden    = orden;		
		this.articulos= articulos;
	} // Transaccion

	public Transaccion(Long idFaltante) {
		super(-1L, -1L);
		this.idFaltante= idFaltante;
	} // Transaccion
	
	public String getMessageError() {
		return messageError;
	} // getMessageError

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar                     = false;
		TcKalanValesBitacoraDto bitacoraOrden= null;
		Map<String, Object> params           = new HashMap<>();
		try {
			if(this.orden!= null)
				params.put("idVale", this.orden.getIdVale());
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" el vale de almacen");
			switch(accion) {
				case MOVIMIENTOS:
					if(this.orden.isValid()) {
  					regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
	  				this.toFillArticulos(sesion);
					} // if
					else {
						Siguiente consecutivo= this.toSiguiente(sesion);
						this.orden.setConsecutivo(consecutivo.getConsecutivo());
						this.orden.setOrden(consecutivo.getOrden());
						this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
						regresar= DaoFactory.getInstance().insert(sesion, this.orden)>= 1L;
						this.toFillArticulos(sesion);
						bitacoraOrden= new TcKalanValesBitacoraDto("", JsfBase.getIdUsuario(), -1L, this.orden.getIdValeEstatus(), this.orden.getIdVale());
						regresar= DaoFactory.getInstance().insert(sesion, bitacoraOrden)>= 1L;
					} // else	
      		for (Articulo articulo: this.articulos) 
						articulo.setModificado(false);
					break;
				case AGREGAR:
					Siguiente consecutivo= this.toSiguiente(sesion);
					this.orden.setConsecutivo(consecutivo.getConsecutivo());
					this.orden.setOrden(consecutivo.getOrden());
					this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
					regresar= DaoFactory.getInstance().insert(sesion, this.orden)>= 1L;
					this.toFillArticulos(sesion);
					bitacoraOrden= new TcKalanValesBitacoraDto("", JsfBase.getIdUsuario(), -1L, this.orden.getIdValeEstatus(), this.orden.getIdVale());
          regresar= DaoFactory.getInstance().insert(sesion, bitacoraOrden)>= 1L;
					break;
				case MODIFICAR:
          this.checkConsecutivo(sesion);
					DaoFactory.getInstance().update(sesion, this.orden);
					this.toFillArticulos(sesion);
					bitacoraOrden= (TcKalanValesBitacoraDto)DaoFactory.getInstance().findFirst(sesion, TcKalanValesBitacoraDto.class, this.orden.toMap(), "ultimo");
  				regresar     = DaoFactory.getInstance().insert(sesion, bitacoraOrden)>= 1L;
					break;				
				case ELIMINAR:
          this.orden.setIdValeEstatus(2L);
          regresar= regresar && DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
          bitacoraOrden= new TcKalanValesBitacoraDto("", JsfBase.getIdUsuario(), -1L, 2L, this.orden.getIdVale());
          regresar= DaoFactory.getInstance().insert(sesion, bitacoraOrden)>= 1L;
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.orden.setIdValeEstatus(this.bitacora.getIdValeEstatus());
						regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
						if(this.orden.getIdValeEstatus().equals(7L)) 
							this.toCommonNotaEntrada(sesion, -1L, this.orden.toMap());
					} // if
					break;
				case DEPURAR:
					regresar= DaoFactory.getInstance().delete(sesion, TcManticFaltantesDto.class, this.idFaltante)>= 1L;
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {		
			Error.mensaje(e);
			throw new Exception(this.messageError.concat("<br/>")+ e);
		} // catch		
		if(this.orden!= null)
			LOG.info("Se generó de forma correcta el vale: "+ this.orden.getConsecutivo());
		return regresar;
	}	// ejecutar

	private void toFillArticulos(Session sesion) throws Exception {
		List<Articulo> todos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(sesion, Articulo.class, "VistaValesDto", "detalle", this.orden.toMap());
		for (Articulo item: todos) 
			if(this.articulos.indexOf(item)< 0)
				DaoFactory.getInstance().delete(sesion, item.toOrdenDetalle());
		for (Articulo articulo: this.articulos) {
			TcKalanValesDetallesDto item= articulo.toValeDetalle();
			item.setIdVale(this.orden.getIdVale());
			if(DaoFactory.getInstance().findIdentically(sesion, TcKalanValesDetallesDto.class, item.toMap())== null) 
		    DaoFactory.getInstance().insert(sesion, item);
			else
				if(articulo.isModificado())
		      DaoFactory.getInstance().update(sesion, item);
			articulo.setObservacion("ARTICULO SOLICITADO EN EL VALE ".concat(this.orden.getConsecutivo()).concat(" EL DIA ").concat(Global.format(EFormatoDinamicos.FECHA_HORA_CORTA, this.orden.getRegistro())));
			DaoFactory.getInstance().updateAll(sesion, TcManticFaltantesDto.class, articulo.toMap());
		} // for
	}
	
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", this.orden.getIdEmpresa());
		  params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKalanValesDto", "siguiente", params, "siguiente");
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
  
} 