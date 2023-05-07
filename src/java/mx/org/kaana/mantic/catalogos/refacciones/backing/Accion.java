package mx.org.kaana.mantic.catalogos.refacciones.backing;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.refacciones.reglas.Transaccion;
import mx.org.kaana.mantic.db.dto.TcManticRefaccionesDto;


@Named(value = "manticCatalogosRefaccionesAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639361L;
  
	private EAccion accion;	
	private TcManticRefaccionesDto pojo;
	private UISelectEntity ikEmpresa;
	private UISelectEntity ikProveedor;

  
  public TcManticRefaccionesDto getPojo() {
    return pojo;
  }

  public void setPojo(TcManticRefaccionesDto pojo) {
    this.pojo = pojo;
  }

  public UISelectEntity getIkEmpresa() {
    return ikEmpresa;
  }

  public void setIkEmpresa(UISelectEntity ikEmpresa) {
    this.ikEmpresa = ikEmpresa;
		if(this.ikEmpresa!= null)
		  this.pojo.setIdEmpresa(this.ikEmpresa.getKey());
  }

  public UISelectEntity getIkProveedor() {
    return ikProveedor;
  }

  public void setIkProveedor(UISelectEntity ikProveedor) {
    this.ikProveedor = ikProveedor;
		if(this.ikProveedor!= null)
		  this.pojo.setIdProveedor(this.ikProveedor.getKey());
  }

	@PostConstruct
  @Override
  protected void init() {		
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("idRefaccion", JsfBase.getFlashAttribute("idRefaccion"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  public void doLoad() {
    Long idRefaccion= -1L;
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      switch (this.accion) {
        case AGREGAR:											
          this.pojo= new TcManticRefaccionesDto();
          this.pojo.setSat(Constantes.CODIGO_SAT);
          this.pojo.setActualizado(new Timestamp(Calendar.getInstance().getTimeInMillis()));
          this.pojo.setIdUsuario(JsfBase.getAutentifica().getPersona().getIdUsuario());
          this.pojo.setIva(16D);
          this.pojo.setIdVigente(1L);
          this.pojo.setIdDescontinuado(2L);
    			this.setIkEmpresa(new UISelectEntity(JsfBase.getAutentifica().getEmpresa().getIdEmpresa()));
          this.setIkProveedor(new UISelectEntity(-1L));
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          idRefaccion= (Long)this.attrs.get("idRefaccion");
          this.pojo  = (TcManticRefaccionesDto)DaoFactory.getInstance().findById(TcManticRefaccionesDto.class, idRefaccion);
          this.setIkEmpresa(new UISelectEntity(new Entity(this.pojo.getIdEmpresa())));
          this.setIkProveedor(new UISelectEntity(new Entity(this.pojo.getIdProveedor())));
          break;
      } // switch
      this.toLoadCatalog();      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    try {			
			transaccion = new Transaccion(this.pojo);
			if (transaccion.ejecutar(this.accion)) {
				regresar = this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);
				JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el registro de la refacción de forma correcta."), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar la refacción.", ETipoMensaje.ERROR);      			
  		JsfBase.setFlashAttribute("idRefaccion", this.attrs.get("idRefaccion"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
		JsfBase.setFlashAttribute("idRefaccion", this.attrs.get("idRefaccion"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doAccion

	private void toLoadCatalog() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns));
 			List<UISelectEntity> empresas= (List<UISelectEntity>)this.attrs.get("empresas");
			if(!empresas.isEmpty()) 
				if(this.accion.equals(EAccion.AGREGAR))
  				this.setIkEmpresa(empresas.get(0));
			  else 
				  this.setIkEmpresa(empresas.get(empresas.indexOf(this.getIkEmpresa())));
			this.attrs.put("ikEmpresa", new UISelectEntity("-1"));
      this.attrs.put("proveedores", (List<UISelectEntity>) UIEntity.build("VistaNotasEntradasDto", "proveedores", params, columns));
 			List<UISelectEntity> proveedores= (List<UISelectEntity>)this.attrs.get("proveedores");
			if(!proveedores.isEmpty()) 
				if(this.accion.equals(EAccion.AGREGAR))
  				this.setIkProveedor(proveedores.get(0));
			  else {
          int index= proveedores.indexOf(this.getIkProveedor());
          if(index> 0)
				    this.setIkProveedor(proveedores.get(index));
          else
    				this.setIkProveedor(proveedores.get(0));
        } // else  
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}
  
}