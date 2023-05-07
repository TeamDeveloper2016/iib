package mx.org.kaana.mantic.ventas.caja.especial.beans;

import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.mantic.compras.ordenes.reglas.Descuentos;

public class Producto extends mx.org.kaana.mantic.ventas.beans.ArticuloVenta implements Cloneable {
	
	private static final long serialVersionUID = -7272868284426340705L;

  public Producto() {
    this(-1L);
  }

  public Producto(Long key) {
    super(key);
  }

  public Producto(Long key, Boolean costoLibre) {
    super(key, costoLibre);
  }

  
	@Override
	public void toCalculate() {
		Boolean asignar= this.getImportes().getTotal()<= 0D;
		if(!asignar)
			this.setTotal(this.getTotal());
//  ESTO ES PARA CALCULAR EL PRECIO BASADO EN EL PRECIO POR CANTIDAD
//		if(!this.viejosPrecios)
//		  this.toCalculateCostoPorCantidad();
		double porcentajeIva = 1+ (this.getIva()/ 100); 		
		double costoMoneda   = this.getCosto()* this.getTipoDeCambio();
		double costoReal     = this.getCantidad()* costoMoneda;
		this.getImportes().setImporte(Numero.toRedondearSat(costoReal));
		
		Descuentos descuentos= new Descuentos(this.getImportes().getImporte(), this.getDescuento().concat(",").concat(this.getExtras()));
		double temporal= descuentos.toImporte();
		this.getImportes().setSubTotal(Numero.toRedondearSat(temporal<= 0? this.getImportes().getImporte(): temporal));
	  
		// la utilidad es calculada tomando como base el costo menos los descuento y a eso quitarle el precio de lista
		double utilidad= this.getImportes().getSubTotal()- (this.getPrecio()*this.getCantidad());
		
		temporal= descuentos.toImporte(this.getDescuento());
		this.getImportes().setDescuento(Numero.toRedondearSat(temporal> 0? this.getImportes().getImporte()- temporal: 0D));
		
		temporal= descuentos.toImporte(this.getExtras());
		this.getImportes().setExtra(Numero.toRedondearSat(temporal> 0? (this.getImportes().getImporte()- this.getImportes().getSubTotal())- this.getImportes().getDescuento(): 0D));

		if(this.isSinIva()) {
	  	this.getImportes().setIva(Numero.toRedondearSat(this.getImportes().getSubTotal()- (this.getImportes().getSubTotal()/porcentajeIva)));
	  	this.getImportes().setSubTotal(Numero.toRedondearSat(this.getImportes().getSubTotal()- this.getImportes().getIva()));
		} // if	
		else 
	  	this.getImportes().setIva(Numero.toRedondearSat((this.getImportes().getSubTotal()* porcentajeIva)- this.getImportes().getSubTotal()));
		this.getImportes().setTotal(Numero.toRedondearSat(this.getImportes().getSubTotal() + this.getImportes().getIva()));
		
		// verificar si la cantidad tiene decimales entonces realizar el procedimiento de calculo nuevamente tomando como base el precio unitario 
		if(Numero.toRedondear(this.getCantidad()% 1)!= 0) 
			this.toRecalculate(Numero.toRedondear(this.getImportes().getSubTotal()/ this.getCantidad()), porcentajeIva);
		this.setSubTotal(this.getImportes().getSubTotal());
		this.setImpuestos(this.getImportes().getIva());
		this.setDescuentos(this.getImportes().getDescuento());		
		this.setDescuentoDescripcion(!Cadena.isVacio(this.getDescuento()) && !this.getDescuento().equals("0") ? this.getDescuento().concat("% [ $").concat(String.valueOf(this.getImportes().getDescuento())).concat(" ] ") : "0");
		this.setExcedentes(this.getImportes().getExtra());
		this.setImporte(Numero.toRedondearSat(this.getImportes().getTotal()));	
		if(asignar)
			this.setTotal(Numero.toRedondearSat(this.getImportes().getTotal()));
		this.setUtilidad(utilidad);
		this.toDiferencia();
	}
  
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone(); 
  }
  
}