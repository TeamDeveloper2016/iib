package mx.org.kaana.mantic.inventarios.entradas.reglas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEntrada;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.compras.ordenes.enums.EOrdenes;
import mx.org.kaana.mantic.comun.IAdminArticulos;
import mx.org.kaana.mantic.inventarios.entradas.beans.Costo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 8/05/2018
 *@time 03:09:42 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public final class AdminNotas extends IAdminArticulos implements Serializable {

	private static final long serialVersionUID=-5550539230224591510L;
	private static final Log LOG=LogFactory.getLog(AdminNotas.class);

	private NotaEntrada orden;
	
	public AdminNotas(NotaEntrada orden) throws Exception {
		this.orden= orden;
		this.setArticulos(new ArrayList<>());
		if(!this.orden.isValid()) {
		  this.orden.setDiasPlazo(1L);	
  	  this.orden.setConsecutivo(this.toConsecutivo("0"));
		  this.orden.setIdUsuario(JsfBase.getAutentifica().getPersona().getIdUsuario());
		  this.orden.setIdEmpresa(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.orden.setCostos(new ArrayList<>());
		} // if
	}

	public AdminNotas(NotaEntrada orden, EOrdenes tipoOrden) throws Exception {
    Map<String, Object> params= new HashMap<>();
    try {
      this.orden= orden;
      params.put("idNotaEntrada", this.orden.getIdNotaEntrada());
      if(this.orden.isValid()) {
        if(this.orden.getIdNotaTipo().equals(1L))
          this.setArticulos((List<Articulo>)DaoFactory.getInstance().toEntitySet(Articulo.class, "VistaNotasEntradasDto", "detalle", params, -1L));
        else	
          this.setArticulos(this.toLoadOrdenDetalle());
        this.orden.setIkEmpresa(new UISelectEntity(new Entity(this.orden.getIdEmpresa())));
        this.orden.setIkAlmacen(new UISelectEntity(new Entity(this.orden.getIdAlmacen())));
        this.orden.setIkProveedor(new UISelectEntity(new Entity(Objects.equals(this.orden.getIdProveedor(), null)? -1L: this.orden.getIdProveedor())));
        this.orden.setIdEmpresaBack(this.orden.getIdEmpresa());
        // RECUPERAR LOS GASTOS DE LOS COSTOS DE LA NOTA DE ENTRADA
        this.orden.setCostos((List<Costo>)DaoFactory.getInstance().toEntitySet(Costo.class, "VistaNotasEntradasDto", "costos", params, -1L));
        if(Objects.equals(this.orden.getCostos(), null))
          this.orden.setCostos(new ArrayList<>());
      }	// if
      else {
        // this.orden.setIdNotaTipo(Objects.equals(tipoOrden, EOrdenes.NORMAL)? 1L: Objects.equals(tipoOrden, EOrdenes.ESPECIAL)? 4L: 2L);
        this.orden.setIdNotaTipo(tipoOrden.getIdNotaTipo());
        if(Objects.equals(tipoOrden, EOrdenes.NORMAL) || Objects.equals(tipoOrden, EOrdenes.ESPECIAL) || Objects.equals(tipoOrden, EOrdenes.TERMINADO)) {
          this.setArticulos(new ArrayList<>());
          this.orden.setDiasPlazo(1L);
        } // if	
        else
          this.setArticulos(this.toDefaultOrdenDetalle());
        this.orden.setConsecutivo(this.toConsecutivo("0"));
        this.orden.setIdUsuario(JsfBase.getAutentifica().getPersona().getIdUsuario());
        this.orden.setIdEmpresa(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
        this.orden.setCostos(new ArrayList<>());
      } // else	
      for (Articulo articulo: this.getArticulos()) {
        if(articulo.getIdOrdenDetalle()!= null && articulo.getIdOrdenDetalle()<= 0)
          articulo.setIdOrdenDetalle(null);
      } // for
      for (Costo item: this.orden.getCostos()) 
        item.setSql(ESql.SELECT);
      this.toStartCalculate();
      this.getTotales().setGastos(this.orden.getCostos().size());
    } // try
    catch(Exception e) {
      throw e;
    } // catch
    finally {
      Methods.clean(params);
    } // finally
	}

	@Override
	public Long getIdAlmacen() {
		return this.orden.getIdAlmacen();
	}

	@Override
	public Long getIdProveedor() {
		return this.orden.getIdProveedor();
	}
	
	@Override
	public IBaseDto getOrden() {
		return orden;
	}

	@Override
	public void setOrden(IBaseDto orden) {
		this.orden= (NotaEntrada)orden;
	}

	@Override
	public Double getTipoDeCambio() {
		return this.orden.getTipoDeCambio();
	}
	
	@Override
	public String getDescuento() {
		return this.orden.getDescuento();
	}
	
	@Override
	public String getExtras() {
		return this.orden.getExtras();
	}
	
	@Override
	public Long getIdSinIva() {
		return this.orden.getIdSinIva();
	}
	
	@Override
	public void setIdSinIva(Long idSinIva) {
		this.orden.setIdSinIva(idSinIva);
	}

	@Override
	public void setAjusteDeuda(double deuda) {
		this.orden.setDeuda(Numero.toRedondearSat(deuda));
	}

	private ArrayList<Articulo> toLoadOrdenDetalle() throws Exception {
		ArrayList<Articulo> regresar= null;
		ArrayList<Articulo> loaded  = null;
		Map<String, Object> params  = new HashMap<>();
		try {
			params.put("idNotaEntrada", this.orden.getIdNotaEntrada());
			params.put("idOrdenCompra", this.orden.getIdOrdenCompra());
			params.put("idProveedor", this.orden.getIdProveedor());
			params.put("idAlmacen", this.orden.getIdAlmacen());
  		regresar= new ArrayList<>((List<Articulo>)DaoFactory.getInstance().toEntitySet(Articulo.class, "VistaNotasEntradasDto", "detalle", params));
	  	loaded  = new ArrayList<>((List<Articulo>)DaoFactory.getInstance().toEntitySet(Articulo.class, "VistaNotasEntradasDto", "diferencia", params));
			for (Articulo item: loaded) {
  			params.put("idArticulo", item.getIdArticulo());
				Value stock= (Value)DaoFactory.getInstance().toField("TcManticInventariosDto", "stock", params, "stock");
				int index= regresar.indexOf(item);
				item.setStock(stock== null? 0D: stock.toDouble());
				if(index< 0) 
					regresar.add(item);
				else {
					((Articulo)regresar.get(index)).setSolicitados(item.getSolicitados());
					((Articulo)regresar.get(index)).setStock(stock== null? 0D: stock.toDouble());
				} // else	
			} // for
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			throw e;
		} // catch
		finally {
			Methods.clean(params);
			Methods.clean(loaded);
		} // finally
		return regresar;
	}
	
	private ArrayList<Articulo> toDefaultOrdenDetalle() throws Exception {
		ArrayList<Articulo> regresar= null;
		Map<String, Object> params  = new HashMap<>();
		try {
			params.put("idOrdenCompra", this.orden.getIdOrdenCompra());
			params.put("idProveedor", this.orden.getIdProveedor());
			params.put("idAlmacen", this.orden.getIdAlmacen());
			regresar= new ArrayList<>((List<Articulo>)DaoFactory.getInstance().toEntitySet(Articulo.class, "VistaNotasEntradasDto", "diferencia", params));
			for (Articulo item: regresar) {
  			params.put("idArticulo", item.getIdArticulo());
        Value stock= (Value)DaoFactory.getInstance().toField("TcManticInventariosDto", "stock", params, "stock");
				item.setStock(stock== null? 0D: stock.toDouble());
			} // for
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

	@Override
	public void setDescuento(String descuento) {
		this.orden.setDescuento(descuento);
	}

  @Override
  public Long getIdCliente() {
    return Constantes.VENTA_AL_PUBLICO_GENERAL_ID_KEY;
  }

  @Override
	public void toAdjustArticulos() {
		int count= 0;
		while(count< this.getArticulos().size()) {
			if(!this.getArticulos().get(count).isValid() || (!Objects.equals(this.orden.getIdNotaTipo(), 4L) && this.getArticulos().get(count).getCantidad()<= 0))
				this.getArticulos().remove(count);
			else
				if(count> 0 && this.getArticulos().get(count- 1).getKey().equals(this.getArticulos().get(count).getKey())) {
					this.getArticulos().get(count- 1).setCantidad(this.getArticulos().get(count- 1).getCantidad()+ this.getArticulos().get(count).getCantidad());
					this.getArticulos().remove(count);
				} // if
				else
				  count++;
		} // while
    if(!this.getArticulos().isEmpty())
		  this.toRefreshCalculate();
	}

}
