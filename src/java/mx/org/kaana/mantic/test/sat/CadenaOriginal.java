package mx.org.kaana.mantic.test.sat;

import java.io.File;
import java.io.StringWriter;
import java.util.Objects;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import mx.org.kaana.libs.recurso.Configuracion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 27/11/2018
 * @time 08:33:13 AM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public class CadenaOriginal {

	private static final Log LOG=LogFactory.getLog(CadenaOriginal.class);

	private void toProcess() throws Exception {
		// StreamSource source=new StreamSource(new File("D:\\Temporal\\archivos\\facturama\\2018\\NOVIEMBRE\\AAGF591108KI2\\AAGF591108KI2-4171.xml"));
		StreamSource source=new StreamSource(new File("D:\\temporal\\mantic\\archivos\\facturama\\1\\2023\\FEBRERO\\CCV9103253F1\\CCV9103253F1-101.xml"));
		StreamSource stylesource=new StreamSource(this.getClass().getResourceAsStream("/mx/org/kaana/mantic/libs/factura/".concat(Objects.equals(Configuracion.getInstance().getPropiedad("sistema.nivel.facturacion"), "4.0")? "cadenaoriginal_4_0.xslt": "cadenaoriginal_3_3.xslt")));
		TransformerFactory factory=TransformerFactory.newInstance();
		Transformer transformer=factory.newTransformer(stylesource);
		StreamResult result=new StreamResult(new StringWriter());
		transformer.transform(source, result);
		String cadenaOriginal= result.getWriter().toString();
		LOG.info("Cadena original:"+ cadenaOriginal);
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		CadenaOriginal co=new CadenaOriginal();
		co.toProcess();
	}

}
