package mx.org.kaana.mantic.ventas.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.compras.ordenes.reglas.Descuentos;

public class ArticuloVenta extends Articulo implements Cloneable {
	
	private static final long serialVersionUID = -7272868284456340705L;
	private String descripcionPrecio;
	private Double menudeo;
	private boolean descuentoActivo;
	private boolean descuentoAsignado;
	private boolean viejosPrecios;
	
	public ArticuloVenta() {
		this(-1L);
	}

	public ArticuloVenta(Long key) {
		this(key, Boolean.FALSE);
	}
	
	public ArticuloVenta(Long key, Boolean costoLibre) {
		super(key, costoLibre);
		this.descuentoActivo  = false;
		this.descuentoAsignado= false;
		this.viejosPrecios    = false;
	}

	public boolean isViejosPrecios() {
		return viejosPrecios;
	}

	public void setViejosPrecios(boolean viejosPrecios) {
		this.viejosPrecios=viejosPrecios;
	}

	@Override
	public Double getImporte() {
    return Numero.toRedondearSat(super.getImporte());
  }
	
	@Override
	public String getImporte$() {
		return Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, this.getImporte()); 
	}
	
	public String getImpuestos$() {
		return Global.format(EFormatoDinamicos.NUMERO_CON_DECIMALES, this.getImpuestos()); 
	}

	@Override
	public void toCalculate() {
		boolean asignar= this.getImportes().getTotal()<= 0D;
		if(!asignar)
			this.setTotal(this.getTotal());
		if(!this.viejosPrecios)
		  this.toCalculateCostoPorCantidad();
		double porcentajeIva = 1+ (this.getIva()/ 100); 		
		double costoMoneda   = this.getCosto()* this.getTipoDeCambio();
		double costoReal     = this.getCantidad()* costoMoneda;
		this.getImportes().setImporte(Numero.toRedondearSat(costoReal));
		
		Descuentos descuentos= new Descuentos(this.getImportes().getImporte(), this.getDescuento().concat(",").concat(this.getExtras()));
		double temporal= descuentos.toImporte();
		this.getImportes().setSubTotal(Numero.toRedondearSat(temporal<= 0? this.getImportes().getImporte(): temporal));
	  
		// la utilidad es calculada tomando como base el costo menos los descuento y a eso quitarle el precio de lista
		double utilidad      = this.getImportes().getSubTotal()- (this.getPrecio()*this.getCantidad());
		
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
	
	private void toCalculateCostoPorCantidad() {
		Entity validate          = null;	
		Map<String, Object>params= new HashMap<>();
		try {
			if(!this.isDescuentoActivo()) {
				if(!this.isCostoLibre()) {
          params.put("idProveedor", -1L);
          params.put("idArticulo", this.getIdArticulo());
          params.put("idComodin", this.getIdComodin());
          params.put("idCliente", this.getIdCliente());
          params.put("precioCliente", Cadena.isVacio(this.descripcionPrecio) || Objects.equals(this.descripcionPrecio, "ESPECIAL")? "menudeo": this.descripcionPrecio);
					if(this.getIdComodin()!= null && this.getIdComodin()> -1L) 
						validate= (Entity) DaoFactory.getInstance().toEntity("VistaPrecioClienteDto", "precios", params);
					else
						validate= (Entity) DaoFactory.getInstance().toEntity("VistaPrecioClienteDto", "cliente", params);
					if(validate!= null) {
            // SI EL CLIENTE TIENE UN DESCUENTO ESPECIAL ENTONCES CONSIDERAR EL CALCULO DEL PRECIO BASE POR EL PORCENTAJE ASIGNADO
            if("ESPECIAL".equals(this.descripcionPrecio)) 
              this.setCosto(Numero.toRedondearSat(validate.toDouble("precio")* this.getFactor()));
            else { 
              if(this.getDescuentos()> 0D)
                this.setCosto(validate.toDouble("menudeo"));
              else {
                if (this.getCantidad()> validate.toDouble("limiteMayoreo") || (this.getCosto().equals(validate.toDouble("mayoreo")) && this.descuentoAsignado)) {
                  this.setCosto(validate.toDouble("mayoreo"));
                  this.setDescripcionPrecio("mayoreo");
                } // if
                else  
                  if((this.getCantidad()> validate.toDouble("limiteMedioMayoreo") && this.getCantidad()<= validate.toDouble("limiteMayoreo")) || (this.getCosto().equals(validate.toDouble("medioMayoreo")) && this.descuentoAsignado)) {
                    this.setCosto(validate.toDouble("medioMayoreo"));
                    this.setDescripcionPrecio("medioMayoreo");
                  } // if
                  else {
                    if(!Cadena.isVacio(this.descripcionPrecio) && !this.descripcionPrecio.equals("menudeo")) {
                      this.setCosto((Double)validate.toValue(this.descripcionPrecio));
                      this.setDescripcionPrecio(this.descripcionPrecio);
                    } // if
                    else {
                      this.setCosto(validate.toDouble("menudeo"));
                      this.setDescripcionPrecio("menudeo");
                    } // else
                  } // else
              } // else
						} // else						
					} // if	
				} // if costoLibre
			} // if isDescuentoActivo
		} // try
		catch (Exception e) {			
			Error.mensaje(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // toCalculateCostoPorCantidad
	
	@Override
	public double getDiferenciaCosto() {
		return Numero.toRedondearSat(getDiferenciaReal());
	}
		
	@Override
	public void toDiferencia() {
		Descuentos descuentos= new Descuentos(this.getCosto(), this.getDescuento());
		double value= descuentos.toImporte();
		setCalculado(Numero.toRedondearSat(value== 0? this.getCosto(): value));
		descuentos= new Descuentos(this.getCosto(), this.getDescuento().concat(",").concat(this.getExtras()));
		value= descuentos.toImporte();
		setReal(Numero.toRedondearSat(value== 0? this.getCosto(): value));
		value= Numero.toRedondearSat((value== 0? this.getCosto(): value)- this.getValor()); 
  	setDiferencia(this.getValor()== 0? 0: Numero.toRedondearSat(value* 100/ this.getValor()));
	}	// toDiferencia	

	public String getDetallePrecio() {
		String regresar = "";
		boolean display = (!Cadena.isVacio(this.getDescuentos()) && !getDescuentos().equals(0D)) || (!Cadena.isVacio(this.getDescuento()) && !this.getDescuento().equals("0"));			
		if(this.getDescripcionPrecio()!= null || display) {
			String color  = "janal-color-blue";
			boolean precio= false;			
			regresar      = "Menudeo";
			if(this.getDescripcionPrecio()!= null) {
				switch(this.getDescripcionPrecio()) {
					case "medioMayoreo":
						color   = "janal-color-orange";
						precio  = true;
						regresar= "Medio mayoreo";
						break;
					case "mayoreo":
						color   = "janal-color-green";
						precio  = true;
						regresar= "Mayoreo";
						break;				
					case "ESPECIAL":
						precio  = false;
						regresar= "<i class='fa fa-fw fa-question-circle janal-color-purple' style='float:right;' title='Precio especial'></i>";
           break;				
				} // switch
			} // if
      if(this.getFactor()== 1D)
        regresar= "<i class='fa fa-fw fa-question-circle ".concat(color)
                  .concat("' style='float:right; display:").concat(precio || display? "": "none").concat("' title='")
                  .concat("Menudeo: ").concat(Global.format(EFormatoDinamicos.MONEDA_SAT_DECIMALES, this.getMenudeo()))
                  .concat(this.getDescripcionPrecio()!= null? "\n"+ regresar+": "+ Global.format(EFormatoDinamicos.MONEDA_SAT_DECIMALES, this.getCosto()): "")
                  .concat(display? "\nDescuento: "+ this.getDescuento()+ ", "+ this.getDescuentos(): "")
                  .concat("'></i>");
		} // if
		return regresar;
	} // getDetallePrecio
	
	public String getDescripcionPrecio() {
		return descripcionPrecio;
	}

	public void setDescripcionPrecio(String descripcionPrecio) {
		this.descripcionPrecio= descripcionPrecio;
	}	

	public Double getMenudeo() {
		return menudeo;
	}

	public void setMenudeo(Double menudeo) {
		this.menudeo= menudeo;
	}

	public boolean isDescuentoActivo() {
		return descuentoActivo;
	}

	public void setDescuentoActivo(boolean descuentoActivo) {
		this.descuentoActivo = descuentoActivo;
	}

	public boolean isDescuentoAsignado() {
		return descuentoAsignado;
	}

	public void setDescuentoAsignado(boolean descuentoAsignado) {
		this.descuentoAsignado = descuentoAsignado;
	}		
 
  public Object clone() throws CloneNotSupportedException {
    return super.clone(); 
  }
  
}