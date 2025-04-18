package mx.org.kaana.kalan.gastos.reglas;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.kalan.db.dto.TcKalanEmpresasControlesDto;
import mx.org.kaana.kalan.db.dto.TcKalanEmpresasGastosDto;
import mx.org.kaana.kalan.db.dto.TcKalanGastosBitacoraDto;
import mx.org.kaana.kalan.gastos.beans.Gasto;
import mx.org.kaana.kalan.gastos.beans.Parcialidad;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private Gasto gasto;	
  private TcKalanGastosBitacoraDto bitacora;  
	private String messageError;

	public Transaccion(Gasto gasto) {
		this.gasto= gasto;		
	} 
  
	public Transaccion(Gasto gasto, TcKalanGastosBitacoraDto bitacora) {
		this.gasto= gasto;		
    this.bitacora= bitacora;
	} 

	public String getMessageError() {
		return messageError;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar          = false;
    Map<String, Object> params= new HashMap<>();
		try {
			this.messageError= "Ocurrio un error al ".concat(accion.name().toLowerCase()).concat(" el registrar el gasto");
      this.gasto.setIdUsuario(JsfBase.getIdUsuario());
      this.gasto.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			switch(accion) {
				case AGREGAR:
          Siguiente consecutivo= this.toSiguiente(sesion);	
          this.gasto.setConsecutivo(consecutivo.getConsecutivo());			
          this.gasto.setEjercicio(Long.valueOf(Fecha.getAnioActual()));			
          this.gasto.setOrden(consecutivo.getOrden());
          if(Objects.equals(this.gasto.getIdActivoProrratear(), 1L)) 
            this.gasto.setPago(0L);
          else
            this.gasto.setPagos(0L);
          regresar= DaoFactory.getInstance().insert(sesion, this.gasto)> 0L;
          if(Objects.equals(this.gasto.getIdActivoCheque(), 1L)) {
            this.gasto.getDocumento().setIdEmpresaGasto(this.gasto.getIdEmpresaGasto());
            if(Objects.equals(this.gasto.getDocumento().getIdActivoProveedor(), 2L)) 
              this.gasto.getDocumento().setIdProveedor(null);
            regresar= DaoFactory.getInstance().insert(sesion, this.gasto.getDocumento())> 0L;
          } // if  
					this.parcialidades(sesion);
          this.bitacora= new TcKalanGastosBitacoraDto(
            JsfBase.getIdUsuario(), // Long idUsuario, 
            -1L, // Long idGastoBitacora, 
            null, // String observaciones, 
            this.gasto.getIdGastoEstatus(), // Long idGastoEstatus, 
            this.gasto.getIdEmpresaGasto() // Long idEmpresaGasto
          );
          regresar= DaoFactory.getInstance().insert(sesion, this.bitacora)> 0L;
					break;
				case MODIFICAR:
          if(Objects.equals(this.gasto.getIdActivoProrratear(), 1L)) 
            this.gasto.setPago(0L);
          regresar= DaoFactory.getInstance().update(sesion, this.gasto)> 0L;
          if(Objects.equals(this.gasto.getIdActivoCheque(), 1L)) {
            if(Objects.equals(this.gasto.getDocumento().getIdActivoProveedor(), 2L)) 
              this.gasto.getDocumento().setIdProveedor(null);
            DaoFactory.getInstance().update(sesion, this.gasto.getDocumento());
          }  // if  
          else
            DaoFactory.getInstance().delete(sesion, this.gasto.getDocumento());
					this.parcialidades(sesion);
          this.bitacora= new TcKalanGastosBitacoraDto(
            JsfBase.getIdUsuario(), // Long idUsuario, 
            -1L, // Long idGastoBitacora, 
            null, // String observaciones, 
            this.gasto.getIdGastoEstatus(), // Long idGastoEstatus, 
            this.gasto.getIdEmpresaGasto() // Long idEmpresaGasto
          );
          regresar= DaoFactory.getInstance().insert(sesion, this.bitacora)> 0L;
					break;				
				case ELIMINAR:
          params.put("idGastoEmpresa", this.gasto.getIdEmpresaGasto());
          DaoFactory.getInstance().deleteAll(sesion, TcKalanGastosBitacoraDto.class, params);
					this.clean(sesion);
          regresar= DaoFactory.getInstance().delete(sesion, this.gasto.getDocumento())> 0L;
          regresar= DaoFactory.getInstance().delete(sesion, this.gasto)> 0L;
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
            params.put("idEmpresaGasto", this.gasto.getIdEmpresaGasto());
            params.put("idGastoEstatus", this.bitacora.getIdGastoEstatus());
						this.gasto.setIdGastoEstatus(this.bitacora.getIdGastoEstatus());
            // VERIFICAR SI ES EL GASTO ES EL PRINCIPAL CANCELAR TODOS LOS PAGOS Y AJUSTAR EL TOTAL
            if(!Objects.equals(this.gasto.getIdFuente(), 1L) && Objects.equals(this.gasto.getIdGastoEstatus(), 3L)) {
              TcKalanEmpresasGastosDto total= (TcKalanEmpresasGastosDto)DaoFactory.getInstance().toEntity(TcKalanEmpresasGastosDto.class, "VistaEmpresasGastosDto", "fuente", params);
              if(!Objects.equals(total, null)) {
                if(Objects.equals(total.getIdProveedor(), -1L))
                  total.setIdProveedor(null);
                if(Objects.equals(total.getIdEmpresaCuenta(), -1L))
                  total.setIdEmpresaCuenta(null);
                if(Objects.equals(total.getIdGastoComprobante(), -1L))
                  total.setIdGastoComprobante(null);
                total.setSubtotal(total.getSubtotal()- this.gasto.getSubtotal());
                total.setIvaCalculado(total.getIvaCalculado()- this.gasto.getIvaCalculado());
                total.setImporte(total.getImporte()- this.gasto.getImporte());
                total.setTotal(total.getTotal()- this.gasto.getTotal());
                DaoFactory.getInstance().update(sesion, total);
              } // if
            } // if
            DaoFactory.getInstance().updateAll(sesion, TcKalanEmpresasGastosDto.class, params);		
            if(Objects.equals(this.gasto.getIdProveedor(), -1L))
              this.gasto.setIdProveedor(null);
            if(Objects.equals(this.gasto.getIdEmpresaCuenta(), -1L))
              this.gasto.setIdEmpresaCuenta(null);
            if(Objects.equals(this.gasto.getIdGastoComprobante(), -1L))
              this.gasto.setIdGastoComprobante(null);
            regresar= DaoFactory.getInstance().update(sesion, this.gasto)>= 1L;
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
    finally {
      Methods.clean(params);
    } // finally
		return regresar;
	}	
	
  private Boolean parcialidades(Session sesion) throws Exception {
    Boolean regresar          = Boolean.TRUE;
    Map<String, Object> params= new HashMap<>();
    TcKalanEmpresasControlesDto control= null;
    int count                 = 1;
    try {
      if(Objects.equals(this.gasto.getIdActivoProrratear(), 1L)) {
        for (Parcialidad item: this.gasto.getParcialidades()) {
          item.setIdUsuario(JsfBase.getIdUsuario());
          item.setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
          switch(item.getSql()) {
            case SELECT:
              break;
            case UPDATE:
              item.setConcepto(this.gasto.getConcepto());
              item.setIdEmpresa(this.gasto.getIdEmpresa());
              item.setIdProveedor(this.gasto.getIdProveedor());
              item.setIdGastoClasificacion(this.gasto.getIdGastoClasificacion());
              item.setIdGastoSubclasificacion(this.gasto.getIdGastoSubclasificacion());
              item.setIdEmpresaCuenta(this.gasto.getIdEmpresaCuenta());
              item.setIdGastoComprobante(this.gasto.getIdGastoComprobante());
              item.setReferencia(this.gasto.getReferencia());
              item.setFechaReferencia(this.gasto.getFechaReferencia());
              item.setIdActivoCheque(2L);
              item.setIdActivoProrratear(2L);
              item.setIdFuente(2L);
              regresar= DaoFactory.getInstance().update(sesion, item)> 0L;
              break;
            case DELETE:
              params.put("idGastoEmpresa", this.gasto.getIdEmpresaGasto());
              params.put("idGastoControl", item.getIdEmpresaGasto());
              DaoFactory.getInstance().deleteAll(sesion, TcKalanEmpresasControlesDto.class, "control", params);
              regresar= DaoFactory.getInstance().delete(sesion, item)> 0L;
              break;
            case INSERT:
              Siguiente consecutivo= this.toSiguiente(sesion);	
              item.setConsecutivo(consecutivo.getConsecutivo());			
              item.setEjercicio(Long.valueOf(Fecha.getAnioActual()));			
              item.setOrden(consecutivo.getOrden());		
              item.setConcepto(this.gasto.getConcepto());
              item.setIdEmpresa(this.gasto.getIdEmpresa());
              item.setIdProveedor(this.gasto.getIdProveedor());
              item.setIdGastoClasificacion(this.gasto.getIdGastoClasificacion());
              item.setIdGastoSubclasificacion(this.gasto.getIdGastoSubclasificacion());
              item.setIdEmpresaCuenta(this.gasto.getIdEmpresaCuenta());
              item.setIdGastoComprobante(this.gasto.getIdGastoComprobante());
              item.setReferencia(this.gasto.getReferencia());
              item.setFechaReferencia(this.gasto.getFechaReferencia());
              item.setIdActivoCheque(2L);
              item.setIdActivoProrratear(2L);
              item.setIdFuente(2L);
              regresar= DaoFactory.getInstance().insert(sesion, item)> 0L;
              control= new TcKalanEmpresasControlesDto(
                new Long(count), // Long secuencia, 
                item.getIdEmpresaGasto(), // Long idGastoControl, 
                this.gasto.getIdEmpresaGasto(), // Long idEmpresaGasto, 
                -1L // Long idEmpresaControl
              );
              regresar= DaoFactory.getInstance().insert(sesion, control)> 0L;
              break;
          } // switch
          count++;
        } // for
      } // if
      else 
        this.clean(sesion);
    } // try
    catch (Exception e) {
      throw e;
    } // catch		    
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
 
	private void clean(Session sesion) throws Exception {
    Map<String, Object> params= new HashMap<>();
    try {
      params.put("idGastoEmpresa", this.gasto.getIdEmpresaGasto());
      List<Entity> items= DaoFactory.getInstance().toEntitySet(sesion, "TcKalanEmpresasControlesDto", "control", params);
      if(items!= null && !items.isEmpty()) {
        DaoFactory.getInstance().deleteAll(sesion, TcKalanEmpresasControlesDto.class, params);
        for (Entity item: items) 
          DaoFactory.getInstance().delete(sesion, TcKalanEmpresasGastosDto.class, item.toLong("idGastoControl"));
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch		    
    finally {
      Methods.clean(params);
    } // finally
  }
  
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("ejercicio", this.getCurrentYear());			
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKalanEmpresasGastosDto", "siguiente", params, "siguiente");
			if(next.getData()!= null)
				regresar= new Siguiente(next.toLong());
			else
				regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L); 
		} // try		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toSiguiente  
  
}