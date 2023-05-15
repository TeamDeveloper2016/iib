package mx.org.kaana.libs.correo;

import javax.mail.PasswordAuthentication;

import mx.org.kaana.libs.formato.Encriptar;
import mx.org.kaana.libs.recurso.TcConfiguraciones;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SMTPAuthenticator extends javax.mail.Authenticator {

  private static final Log LOG= LogFactory.getLog(SMTPAuthenticator.class);

	@Override
  public PasswordAuthentication getPasswordAuthentication() {
    Encriptar encriptado = new Encriptar();
    String username = TcConfiguraciones.getInstance().getPropiedadServidor("correo.admin.user");
    String password = TcConfiguraciones.getInstance().getPropiedadServidor("correo.admin.pass");
    LOG.warn("Uusuario: "+ username +"  contrase�a: "+ encriptado.encriptar(password));
    return new PasswordAuthentication(username, password);
  }

}
