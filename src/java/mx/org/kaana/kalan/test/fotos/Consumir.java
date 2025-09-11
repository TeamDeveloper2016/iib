package mx.org.kaana.kalan.test.fotos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 05/08/2025
 * @time 08:35:27 AM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public class Consumir {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws Exception {
    HttpURLConnection connection= null;
    InputStream inputStream     = null;
    FileOutputStream fileOutputStream= null;
    String localFileName    = "d:/foto.png";
    try {
      URL valida= new URL("https://sia.inegi.org.mx/valida.jsp");
      connection= (HttpURLConnection)valida.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("cuentaSia", "alejandro.jimenez");
      connection.setRequestProperty("contraseniaSia", "");
      connection.connect();
//      URL url = new URL("https://sia.inegi.org.mx/generaImagen?numEmpleado=4934&tipoDocto=12&tipoPersona=empleado");
//      connection = (HttpURLConnection)url.openConnection();
//      connection.setRequestMethod("POST");
      inputStream= connection.getInputStream();    
//      BufferedImage image= ImageIO.read(inputStream);
//      ImageIO.write(image, "jpg", new File(localFileName));
      fileOutputStream = new FileOutputStream(new File(localFileName));
      byte[] buffer = new byte[4096]; // Buffer size
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        fileOutputStream.write(buffer, 0, bytesRead);
      } // while
      System.out.println("File downloaded successfully: " + localFileName);
    } // try 
    finally {    
      if(inputStream!= null)
        inputStream.close();
      if(fileOutputStream!= null)
        fileOutputStream.close();
      if(connection!= null)
        connection.disconnect();
    } // finally
  }
  
}
