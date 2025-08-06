package mx.org.kaana.kalan.ahorros.beans;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kalan.db.dto.TcKalanAhorrosDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 05/08/2025
 *@time 08:35:27 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Ahorro extends TcKalanAhorrosDto implements Serializable {

  private static final long serialVersionUID = -8794495402874168809L;

  private String empleado;
  private UISelectEntity ikEmpresa;  
  private UISelectEntity ikEmpresaCuenta;  
  private UISelectEntity ikEmpresaPersona;  
  private List<Afectacion> cuotas;

  public Ahorro() throws Exception {
    this(-1L);
  }

  public Ahorro(Long key) throws Exception {
    super(key);
    this.setIkEmpresa(new UISelectEntity(-1L));
    this.setIkEmpresaCuenta(new UISelectEntity(-1L));
    this.setIkEmpresaPersona(new UISelectEntity(-1L));
    this.setCuotas(new ArrayList<>());
    this.toNextFriday();
  }
  
  public String getEmpleado() {
    return empleado;
  }

  public void setEmpleado(String empleado) {
    this.empleado = empleado;
  }

  public UISelectEntity getIkEmpresa() {
    return ikEmpresa;
  }

  public void setIkEmpresa(UISelectEntity ikEmpresa) {
    this.ikEmpresa = ikEmpresa;
    if(ikEmpresa!= null)
			this.setIdEmpresa(ikEmpresa.getKey());    
  }

  public UISelectEntity getIkEmpresaCuenta() {
    return ikEmpresaCuenta;
  }

  public void setIkEmpresaCuenta(UISelectEntity ikEmpresaCuenta) {
    this.ikEmpresaCuenta = ikEmpresaCuenta;
    if(ikEmpresaCuenta!= null)
			this.setIdEmpresaCuenta(ikEmpresaCuenta.getKey());    
  }
  
  public UISelectEntity getIkEmpresaPersona() {
    return ikEmpresaPersona;
  }

  public void setIkEmpresaPersona(UISelectEntity ikEmpresaPersona) {
    this.ikEmpresaPersona = ikEmpresaPersona;
    if(ikEmpresaPersona!= null)
			this.setIdEmpresaPersona(ikEmpresaPersona.getKey());    
  }

  public List<Afectacion> getCuotas() {
    return cuotas;
  }

  public void setCuotas(List<Afectacion> cuotas) {
    this.cuotas = cuotas;
  }
  
  private void toNextFriday() throws Exception {
    Calendar calendar= Calendar.getInstance();
    try {      
      int day= calendar.get(Calendar.DAY_OF_WEEK)>= 6? 0: 7- calendar.get(Calendar.DAY_OF_WEEK);
      calendar.add(Calendar.DATE, day);
      this.setFechaArranque(new Date(calendar.getTimeInMillis()));
      this.toCalculatePayments();
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }  
  
  public void toCalculatePayments() throws Exception {
    int pagos        = 0;
    Calendar calendar= Calendar.getInstance();
    try {      
      calendar.setTimeInMillis(this.getFechaArranque().getTime());
      int year= calendar.get(Calendar.YEAR);
      while(calendar.get(Calendar.YEAR)<= year || pagos> Constantes.REGISTROS_TOPE_PAGINA) {
        calendar.add(Calendar.DATE, this.getPlazo().intValue());
        if(calendar.get(Calendar.YEAR)<= year) {
          if(pagos< this.cuotas.size()) 
            this.cuotas.get(pagos).update(this.getImporte(), new Date(calendar.getTimeInMillis()));
          else  
            this.cuotas.add(new Afectacion(this.getImporte(), new Date(calendar.getTimeInMillis())));
          pagos++; 
        } // if  
      } // while
      calendar.add(Calendar.DATE, -1* this.getPlazo().intValue());
      this.setLimite(new Date(calendar.getTimeInMillis()));
      if(pagos< this.cuotas.size()- 1) {
        while(pagos< this.cuotas.size()) {
          if(Objects.equals(this.cuotas.get(pagos).getSql(), ESql.INSERT))
            this.cuotas.remove(pagos);
          else {
            if(Objects.equals(this.cuotas.get(pagos).getIdAhorroControl(), 1L))
              this.cuotas.remove(pagos).setSql(ESql.DELETE);
            pagos++;
          } // if  
        } // while
      } // if
      this.setPagos(Long.valueOf(this.cuotas.size()));
      if(!this.cuotas.isEmpty())
        this.setLimite(this.cuotas.get(this.cuotas.size()- 1).getFechaAplicacion());
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }  
  
  @Override
  public Class toHbmClass() {
    return TcKalanAhorrosDto.class;
  }
  
}
