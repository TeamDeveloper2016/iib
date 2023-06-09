package mx.org.kaana.mantic.catalogos.clientes.convenios.reglas;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kalan.db.dto.TcKalanProductosDto;
import mx.org.kaana.keet.db.dto.TcKeetArticulosClientesDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.db.dto.TcManticArticulosDto;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private TcKeetArticulosClientesDto precio;	
	private TcKalanProductosDto producto;	
	private Map<String, Object> parametros;	
  private Long idCliente;
  private List<Articulo> articulos;
	private String messageError;

	public Transaccion(TcKeetArticulosClientesDto precio) {
		this.precio= precio;
	}

	public Transaccion(Long idCliente, List<Articulo> articulos) {
		this.idCliente= idCliente;
    this.articulos= articulos;
	}

	public Transaccion(TcKalanProductosDto producto, Map<String, Object> parametros) {
    this.producto= producto;
    this.parametros= parametros;
  }
          
	public String getMessageError() {
		return messageError;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar          = false;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			this.messageError= "Ocurrio un error al ".concat(accion.name().toLowerCase()).concat(" el registrar el precio !");
			switch(accion) {
				case AGREGAR:
					regresar= DaoFactory.getInstance().insert(sesion, this.precio)>= 1L;
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.precio)>= 1L;
					break;				
				case ELIMINAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.precio)>= 1L;
					break;
				case PROCESAR:
					regresar= this.toRegistrar(sesion);
					break;
				case REPROCESAR:
					regresar= this.toRegistrarPrecios(sesion);
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
	}	// ejecutar

  private boolean toRegistrar(Session sesion) throws Exception {
    Boolean regresar                = this.articulos== null || this.articulos.isEmpty();
    TcKeetArticulosClientesDto costo= null;
		Map<String, Object>params       = new HashMap<>();
    try {      
      if(this.articulos!= null)
        for (Articulo item: this.articulos) {
          params.put("idCliente", this.idCliente);
          params.put("idArticulo", item.getIdArticulo());
          costo= (TcKeetArticulosClientesDto)DaoFactory.getInstance().toEntity(TcKeetArticulosClientesDto.class, "TcKeetArticulosClientesDto", "igual", params);
          if(!Objects.equals(item.getIdArticulo(), null) && !Objects.equals(item.getIdArticulo(), -1L))
            if(costo== null) {
              costo= new TcKeetArticulosClientesDto(
                -1L, // Long idArticuloCliente, 
                this.idCliente, // Long idCliente, 
                0D, // Double limiteMedioMayoreo, 
                JsfBase.getIdUsuario(), // Long idUsuario, 
                item.getCosto(), // Double mayoreo, 
                item.getCosto(), // Double menudeo, 
                item.getIdArticulo(), // Long idArticulo, 
                0D, // Double limiteMayoreo, 
                item.getCosto(), // Double medioMayoreo, 
                new Timestamp(Calendar.getInstance().getTimeInMillis()) // Timestamp actualizado
              );
              regresar= DaoFactory.getInstance().insert(sesion, costo)> 0L;
            } // if
            else 
              if(!Objects.equals(item.getCosto(), costo.getMenudeo())) {
                costo.setMenudeo(item.getCosto());
                costo.setMedioMayoreo(item.getCosto());
                costo.setMayoreo(item.getCosto());
                costo.setLimiteMedioMayoreo(0D);
                costo.setLimiteMayoreo(0D);
                costo.setIdUsuario(JsfBase.getIdUsuario());
                costo.setActualizado(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                regresar= DaoFactory.getInstance().update(sesion, costo)> 0L;
              } // if
              else
                regresar= Boolean.TRUE;
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

  private boolean toRegistrarPrecios(Session sesion) throws Exception {
    Boolean regresar         = Boolean.FALSE;
    Double costo             = 0D;
    Double menudeo           = 0D;
    Double medio             = 0D;
    Double mayoreo           = 0D;
    Map<String, Object>params= new HashMap<>(this.parametros);
    try {      
      this.producto.setIdUsuario(JsfBase.getIdUsuario());
      this.producto.setActualizado(new Timestamp(Calendar.getInstance().getTimeInMillis()));
      regresar= DaoFactory.getInstance().update(sesion, this.producto)>= 0L;
      List<TcManticArticulosDto> items= (List<TcManticArticulosDto>)DaoFactory.getInstance().toEntitySet(sesion, TcManticArticulosDto.class, "TcManticArticulosDto", "precio", params, Constantes.SQL_TODOS_REGISTROS);
      for (TcManticArticulosDto item: items) {
        costo = Numero.toRedondearSat(this.producto.getCosto());
        menudeo= Numero.toRedondearSat(this.producto.getPrecio1()* item.getPesoEstimado());
        medio  = Numero.toRedondearSat(this.producto.getPrecio2()* item.getPesoEstimado());
        mayoreo= Numero.toRedondearSat(this.producto.getPrecio3()* item.getPesoEstimado());
        if(!Objects.equals(item.getPrecio(), costo) || !Objects.equals(item.getMenudeo(), menudeo) || !Objects.equals(item.getMedioMayoreo(), medio) || !Objects.equals(item.getMayoreo(), mayoreo)) {
          item.setPrecio(costo);
          item.setMenudeo(menudeo);
          item.setMedioMayoreo(medio);
          item.setMayoreo(mayoreo);
          item.setIdUsuario(JsfBase.getIdUsuario());
          item.setActualizado(new Timestamp(Calendar.getInstance().getTimeInMillis()));
          DaoFactory.getInstance().update(sesion, item);
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
  
}