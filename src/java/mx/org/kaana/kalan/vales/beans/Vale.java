package mx.org.kaana.kalan.vales.beans;

import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;
import mx.org.kaana.kalan.db.dto.TcKalanValesDto;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 8/05/2018
 *@time 10:29:26 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Vale extends TcKalanValesDto implements Serializable {

	private static final long serialVersionUID=1088884892456452488L;
	
	private UISelectEntity ikEmpresa;
	private UISelectEntity ikAlmacen;
  private Long idEmpresaBack;

	public Vale() {
		this(-1L);
	}

	public Vale(Long key) {
		this(Calendar.getInstance().get(Calendar.YEAR)+ "00000", -1L, JsfBase.getAutentifica().getEmpresa().getIdEmpresaPersonal(), -1L, null, 1L, -1L, 1L, -1L, new Long(Calendar.getInstance().get(Calendar.YEAR)));
	}

	public Vale(String consecutivo, Long idSolicito, Long idUsuario, Long idAlmacen, String observaciones, Long idValeEstatus, Long idEmpresa, Long orden, Long idVale, Long ejercicio) {
		super(consecutivo, idSolicito, idUsuario, idAlmacen, observaciones, idValeEstatus, idEmpresa, orden, idVale, ejercicio);
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

  public Long getIdEmpresaBack() {
    return idEmpresaBack;
  }

  public void setIdEmpresaBack(Long idEmpresaBack) {
    this.idEmpresaBack = idEmpresaBack;
  }
	
	@Override
	public Class toHbmClass() {
		return TcKalanValesDto.class;
	}
	
}
