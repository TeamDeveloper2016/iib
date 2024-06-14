package mx.org.kaana.mantic.inventarios.entradas.beans;

import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.mantic.db.dto.TcManticNotasEntradasDto;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesComprasDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 8/05/2018
 *@time 10:29:26 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class NotaEntrada extends TcManticNotasEntradasDto implements Serializable {

	private static final long serialVersionUID=3088884892456452488L;
	
	private UISelectEntity ikEmpresa;
	private UISelectEntity ikAlmacen;
	private UISelectEntity ikProveedor;
  private UISelectEntity ikProveedorPago;
	private UISelectEntity ikOrdenCompra;
  private Long idEmpresaBack;
  private List<Costo> costos;

	public NotaEntrada() throws Exception {
		this(-1L, null);
	}

	public NotaEntrada(Long key, Long idOrdenCompra) throws Exception {
		super(0D, null, "0.00", idOrdenCompra, 1L, new Date(Calendar.getInstance().getTimeInMillis()), "0.00", key, new Date(Calendar.getInstance().getTimeInMillis()), 7L, new Long(Calendar.getInstance().get(Calendar.YEAR)), Calendar.getInstance().get(Calendar.YEAR)+ "00000", 0D, "", 1L, -1L, 0D, 0D, 1D, 2L, "", -1L, 1L, 0D, 30L, new Date(Calendar.getInstance().getTimeInMillis()), 0D, -1L, 0D);
		if(!Cadena.isVacio(idOrdenCompra) && idOrdenCompra> 0L) {
		  TcManticOrdenesComprasDto compra= (TcManticOrdenesComprasDto)DaoFactory.getInstance().findById(TcManticOrdenesComprasDto.class, idOrdenCompra);
			super.setIdEmpresa(compra.getIdEmpresa());
			super.setIdAlmacen(compra.getIdAlmacen());
		  super.setIdProveedor(compra.getIdProveedor());
			super.setIdProveedorPago(compra.getIdProveedorPago());
		} // if
	}

	public NotaEntrada(Double descuentos, Long idProveedor, String descuento, Long idOrdenCompra, Long idDirecta, Date fechaRecepcion, String extras, Long idNotaEntrada, Date fechaFactura, Long idNotaEstatus, Long ejercicio, String consecutivo, Double total, String factura, Long idUsuario, Long idAlmacen, Double subTotal, Double impuestos, Double tipoDeCambio, Long idSinIva, String observaciones, Long idEmpresa, Long orden, Double excedentes, Long idProveedorPago) {
		super(descuentos, idProveedor, descuento, idOrdenCompra, idDirecta, fechaRecepcion, extras, idNotaEntrada, fechaFactura, idNotaEstatus, ejercicio, consecutivo, total, factura, idUsuario, idAlmacen, subTotal, impuestos, tipoDeCambio, idSinIva, observaciones, idEmpresa, orden, excedentes, 30L, new Date(Calendar.getInstance().getTimeInMillis()), 0D, idProveedorPago, 0D);
	}

  public UISelectEntity getIkEmpresa() {
    return ikEmpresa;
  }

  public void setIkEmpresa(UISelectEntity ikEmpresa) {
    this.ikEmpresa = ikEmpresa;
		if(this.ikEmpresa!= null)
		  this.setIdEmpresa(this.ikEmpresa.getKey());
  }

	public UISelectEntity getIkAlmacen() {
		return ikAlmacen;
	}

	public void setIkAlmacen(UISelectEntity ikAlmacen) {
		this.ikAlmacen=ikAlmacen;
		if(this.ikAlmacen!= null)
		  this.setIdAlmacen(this.ikAlmacen.getKey());
	}

	public UISelectEntity getIkProveedor() {
		return ikProveedor;
	}

	public void setIkProveedor(UISelectEntity ikProveedor) {
		this.ikProveedor=ikProveedor;
		if(this.ikProveedor!= null)
		  this.setIdProveedor(this.ikProveedor.getKey());
	}

	public UISelectEntity getIkProveedorPago() {
		return ikProveedorPago;
	}

	public void setIkProveedorPago(UISelectEntity ikProveedorPago) {
		this.ikProveedorPago=ikProveedorPago;
		if(this.ikProveedorPago!= null)
		  this.setIdProveedorPago(this.ikProveedorPago.getKey());
	}
	
	public UISelectEntity getIkOrdenCompra() {
		return ikOrdenCompra;
	}

	public void setIkOrdenCompra(UISelectEntity ikOrdenCompra) {
		this.ikOrdenCompra=ikOrdenCompra;
		if(this.ikOrdenCompra!= null)
  	  this.setIdOrdenCompra(this.ikOrdenCompra.getKey());
	}

  public Long getIdEmpresaBack() {
    return idEmpresaBack;
  }

  public void setIdEmpresaBack(Long idEmpresaBack) {
    this.idEmpresaBack = idEmpresaBack;
  }

  public List<Costo> getCostos() {
    return costos;
  }

  public void setCostos(List<Costo> costos) {
    this.costos = costos;
  }
	
  public void add(Costo costo) {
    int index= this.costos.indexOf(costo);
    if(index>= 0) {
      if(Objects.equals(this.costos.get(index).getSql(), ESql.DELETE)) {
        this.costos.get(index).setIdUsuario(costo.getIdUsuario());
        this.costos.get(index).setIdGenerar(costo.getIdGenerar());
        this.costos.get(index).setProveedor(costo.getProveedor());
        this.costos.get(index).setIdProveedor(costo.getIdProveedor());
        this.costos.get(index).setImporte(costo.getImporte());
        this.costos.get(index).setRegistro(costo.getRegistro());
        this.costos.get(index).setObservaciones(costo.getObservaciones());
        this.costos.get(index).setSql(ESql.UPDATE);
      } // if
      else 
        JsfBase.addMessage("Costo", "El gasto con cargo al costo ya existe en la lista !");
    } // if  
    else 
      this.costos.add(costo);
  }
  
  public void remove(Costo costo) {
    int index= this.costos.indexOf(costo);
    if(index>= 0) {
      if(Objects.equals(this.costos.get(index).getSql(), ESql.SELECT) || Objects.equals(this.costos.get(index).getSql(), ESql.UPDATE)) 
        this.costos.get(index).setSql(ESql.DELETE);
      else
        this.costos.remove(index);
      JsfBase.addMessage("Costo", "El gasto con cargo al costo fue eliminado de la lista !");
    } // if
    else
      JsfBase.addMessage("Costo", "El gasto con cargo al costo NO existe en la lista !");
  }
  
  public void recover(Costo costo) {
    int index= this.costos.indexOf(costo);
    if(index>= 0) {
      if(Objects.equals(this.costos.get(index).getSql(), ESql.DELETE)) 
        this.costos.get(index).setSql(ESql.UPDATE);
    } // if
    else
      JsfBase.addMessage("Costo", "El gasto con cargo al costo NO existe en la lista !");
  }
  
	@Override
	public Class toHbmClass() {
		return TcManticNotasEntradasDto.class;
	}
	
}
