package mx.org.kaana.mantic.catalogos.almacenes.transferencias.beans;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.mantic.db.dto.TcManticTransferenciasDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 16/01/2019
 *@time 10:29:26 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Transferencia extends TcManticTransferenciasDto implements Serializable {

	private static final long serialVersionUID=3088884892456452488L;
	
	private UISelectEntity ikEmpresa;
	private UISelectEntity ikAlmacen;
	private UISelectEntity ikTipoClase;
	private UISelectEntity ikArticulo;
	private UISelectEntity ikSolicito;
	private UISelectEntity ikDestino;
	private UISelectEntity ikProducto;

	public Transferencia() {
		this(-1L);
	}

	public Transferencia(Long key) {
	  this(key, 1L);
	}
	
	public Transferencia(Long key, Long idTransferenciaTipo) {
		this(-1L, 1L, idTransferenciaTipo, new Long(Calendar.getInstance().get(Calendar.YEAR)), new Long(Calendar.getInstance().get(Calendar.YEAR))+ "00000", 1L, -1L, "", -1L, JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), 1L, -1L, -1L, -1L);
	}

	public Transferencia(Long idSolicito, Long idTransferenciaEstatus, Long idTransferenciaTipo, Long ejercicio, String consecutivo, Long idUsuario, Long idAlmacen, String observaciones, Long idDestino, Long idEmpresa, Long orden, Long idTransferencia, Long idTipoClase, Long idArticulo) {
		super(idSolicito, idTransferenciaEstatus, idTransferenciaTipo, ejercicio, consecutivo, idUsuario, idAlmacen, observaciones, idDestino, idEmpresa, orden, idTransferencia, idTipoClase, idArticulo);
    this.init();
	}
	
	public UISelectEntity getIkEmpresa() {
		return ikEmpresa;
	}

	public void setIkEmpresa(UISelectEntity ikEmpresa) {
		this.ikEmpresa=ikEmpresa;
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

	public UISelectEntity getIkSolicito() {
		return ikSolicito;
	}

	public void setIkSolicito(UISelectEntity ikSolicito) {
		this.ikSolicito=ikSolicito;
		if(this.ikSolicito!= null)
		  this.setIdSolicito(this.ikSolicito.getKey());
	}

	public UISelectEntity getIkDestino() {
		return ikDestino;
	}

	public void setIkDestino(UISelectEntity ikDestino) {
		this.ikDestino=ikDestino;
		if(this.ikDestino!= null)
		  this.setIdDestino(this.ikDestino.getKey());
	}

  public UISelectEntity getIkTipoClase() {
    return ikTipoClase;
  }

  public void setIkTipoClase(UISelectEntity ikTipoClase) {
    this.ikTipoClase = ikTipoClase;
    if(ikTipoClase!= null)
      this.setIdTipoClase(this.ikTipoClase.getKey());
  }

  public UISelectEntity getIkArticulo() {
    return ikArticulo;
  }

  public void setIkArticulo(UISelectEntity ikArticulo) {
    this.ikArticulo = ikArticulo;
    if(ikArticulo!= null) 
      this.setIdArticulo(this.ikArticulo.getKey());
  }

  public UISelectEntity getIkProducto() {
    return ikProducto;
  }

  public void setIkProducto(UISelectEntity ikProducto) {
    this.ikProducto = ikProducto;
  }

  public void init() {
		this.ikEmpresa  = new UISelectEntity(this.getIdEmpresa());
		this.ikAlmacen  = new UISelectEntity(this.getIdAlmacen());
		this.ikDestino  = new UISelectEntity(this.getIdDestino());
		this.ikSolicito = new UISelectEntity(this.getIdSolicito());
		this.ikTipoClase= new UISelectEntity(this.getIdTipoClase());
		this.ikArticulo = new UISelectEntity(this.getIdArticulo());
		this.ikProducto = new UISelectEntity(-1L);
  }
  
	@Override
	public Class toHbmClass() {
		return TcManticTransferenciasDto.class;
	}
	
}
