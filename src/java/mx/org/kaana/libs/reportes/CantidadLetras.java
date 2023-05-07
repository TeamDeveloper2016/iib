package mx.org.kaana.libs.reportes;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date Mar 10, 2014
 *@time 1:01:28 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Letras;
import mx.org.kaana.libs.reportes.scriptlets.BarraProgreso;
import net.sf.jasperreports.engine.JRScriptletException;

public class CantidadLetras extends BarraProgreso {
	
	private static final long serialVersionUID=3883135413560819745L;

  public String getLetras(String cantidad) throws JRScriptletException {
    String regresar= "";
    try {
      Letras letras= new Letras();
      regresar= letras.getMoneda(cantidad, false);
      letras= null;
    }
    catch(Exception e) {
        Error.mensaje(e);
    } // try
    return regresar;
  }

  public String getLetras(float cantidad) throws JRScriptletException {
    String regresar= "";
    try {
      Letras letras= new Letras();
      regresar= letras.getMoneda(String.valueOf(cantidad), false);
      letras= null;
    }
    catch(Exception e) {
        Error.mensaje(e);
    } // try
    return regresar;
  }

}
