package mx.org.kaana.mantic.correos.enums;

import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.recurso.TcConfiguraciones;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 23/03/2019
 *@time 01:08:38 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public enum ECorreos {
	
  FACTURACION   ("facturacion.html", "resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "Facturas"), 
  TICKET        ("ticket.html", "resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "Ticket"), 
	COTIZACIONES  ("cotizacion.html", "resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "Ventas"),
	ORDENES_COMPRA("ordenes.html", "resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "Compras"),
	CUENTAS       ("cuentas.html", "resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "Ventas"),
	CREDITO       ("cobrar.html", "resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "Créditos"),
	DEVOLUCION    ("devolucion.html", "resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "Devoluciones"),
	PAGOS         ("pagos.html", "resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "Pagos"),
	ORDENES_TALLER("taller.html", "resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "Taller");
	 
	private String template;
	private String images;
	private String user;
	private String password;
	private String alias;
  private static Map<String, String> empresas;
  
  static {
    empresas= new HashMap<>();
    empresas.put("iib.facturacion.email", "facturas@elfrijolito.com");  
    empresas.put("iib.facturacion.backup", "");  
    empresas.put("iib.ticket.email", "facturas@elfrijolito.com");  
    empresas.put("iib.cotizaciones.email", "ventas@elfrijolito.com");  
    empresas.put("iib.cotizaciones.backup", "");  
    empresas.put("iib.ordenes_compra.email", "compras@elfrijolito.com");  
    empresas.put("iib.ordenes_compra.backup", "");  
    empresas.put("iib.cuentas.email", "ventas@elfrijolito.com");  
    empresas.put("iib.cuentas.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("iib.credito.email", "ventas@elfrijolito.com");  
    empresas.put("iib.credito.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("iib.pagos.email", "administracion@elfrijolito.com");  
    empresas.put("iib.pagos.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("iib.ventas.email", "ventas@elfrijolito.com");  
    empresas.put("iib.ventas.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("iib.compras.email", "compras@elfrijolito.com");  
    empresas.put("iib.compras.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("iib.administracion.email", "administracion@elfrijolito.com");  
    empresas.put("iib.administracion.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("iib.control.email", "administrador@elfrijolito.com");  
    
    empresas.put("kalan.facturacion.email", "facturas@kalan.com");  
    empresas.put("kalan.facturacion.backup", "");  
    empresas.put("kalan.ticket.email", "facturas@kalan.com");  
    empresas.put("kalan.cotizaciones.email", "ventas@kalan.com");  
    empresas.put("kalan.cotizaciones.backup", "");  
    empresas.put("kalan.ordenes_compra.email", "compras@kalan.com");  
    empresas.put("kalan.ordenes_compra.backup", "");  
    empresas.put("kalan.cuentas.email", "ventas@kalan.com");  
    empresas.put("kalan.cuentas.backup", "");  
    empresas.put("kalan.credito.email", "ventas@casaelfrijolito.com");  
    empresas.put("kalan.credito.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("kalan.pagos.email", "administracion@casaelfrijolito.com");  
    empresas.put("kalan.pagos.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("kalan.ventas.email", "ventas@kalan.com");  
    empresas.put("kalan.ventas.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("kalan.compras.email", "compras@kalan.com");  
    empresas.put("kalan.compras.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("kalan.administracion.email", "administracion@kalan.com");  
    empresas.put("kalan.administracion.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("kalan.control.email", "imox.soluciones.web@gmail.com");  
    
    empresas.put("tsaak.facturacion.email", "facturas@tsaak.com");  
    empresas.put("tsaak.facturacion.backup", "");  
    empresas.put("tsaak.ticket.email", "facturas@tsaak.com");  
    empresas.put("tsaak.cotizaciones.email", "ventas@tsaak.com");  
    empresas.put("tsaak.cotizaciones.backup", "compras@tsaak.com");  
    empresas.put("tsaak.ordenes_compra.email", "compras@tsaak.com");  
    empresas.put("tsaak.ordenes_compra.backup", "compras@tsaak.com");  
    empresas.put("tsaak.cuentas.email", "ventas@tsaak.com");  
    empresas.put("tsaak.cuentas.backup", "");  
    empresas.put("tsaak.credito.email", "ventas@casaelfrijolito.com");  
    empresas.put("tsaak.credito.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("tsaak.pagos.email", "administracion@casaelfrijolito.com");  
    empresas.put("tsaak.pagos.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("tsaak.ventas.email", "ventas@tsaak.com");  
    empresas.put("tsaak.ventas.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("tsaak.compras.email", "compras@tsaak.com");  
    empresas.put("tsaak.compras.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("tsaak.administracion.email", "administracion@tsaak.com");  
    empresas.put("tsaak.administracion.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("tsaak.control.email", "imox.soluciones.web@gmail.com");  
  }

	private ECorreos(String template, String images, String user, String password, String alias) {
		this.template=template;
		this.images=images;
		this.user= user;
		this.password= password;
		this.alias= alias;
    // AQUI SE AGREGA LA CUENTA DE CORREO DE jimenez76@yahoo.com QUE ESTA REGISTRADA EN LA BASE DE DATOS 
		// this.backup  = Cadena.isVacio(backup)? TcConfiguraciones.getInstance().getPropiedad(Constantes.EMAILS_@BACKUP_SYSTEM): backup.concat(",").concat(TcConfiguraciones.getInstance().getPropiedad(Constantes.EMAILS_BACKUP_SYSTEM));
	}

	public String getTemplate() {
		return template;
	}

	public String getImages() {
		return images;
	}	

	public String getUser() {
		return TcConfiguraciones.getInstance().getPropiedadServidor(this.user);
	}

	public String getPassword() {
		return TcConfiguraciones.getInstance().getPropiedadServidor(this.password);
	}

	public String getEmail() {
    String token   = Configuracion.getInstance().getPropiedad("sistema.empresa.principal").toLowerCase().concat(".").concat(this.name().toLowerCase()).concat(".").concat("email");
    String regresar= "";
    if(empresas.containsKey(token))
      regresar= empresas.get(token);
		return regresar;
	}

	public String getAlias() {
		return alias.concat(" | ").concat(Configuracion.getInstance().getPropiedad("sistema.empresa.principal").toUpperCase());
	}

  public String getBackup() {
    String token   = Configuracion.getInstance().getPropiedad("sistema.empresa.principal").toLowerCase().concat(".").concat(this.name().toLowerCase()).concat(".").concat("backup");
    String regresar= "";
    if(empresas.containsKey(token))
      regresar= empresas.get(token);
		return regresar;
  }
	
  public String getControl() {
    String token   = Configuracion.getInstance().getPropiedad("sistema.empresa.principal").toLowerCase().concat(".control.").concat("email");
    String regresar= "";
    if(empresas.containsKey(token))
      regresar= empresas.get(token);
		return regresar;
  }
	
  public String getRespaldo() {
    String token   = Configuracion.getInstance().getPropiedad("sistema.empresa.principal").toLowerCase().concat(".administracion.").concat("backup");
    String regresar= "";
    if(empresas.containsKey(token))
      regresar= empresas.get(token);
		return regresar;
  }
  
  public String getSource() {
	  return "/mx/org/kaana/".concat(Configuracion.getInstance().getPropiedad("sistema.empresa.principal").toLowerCase()).concat("/correos/templates/");
  }          
  
}