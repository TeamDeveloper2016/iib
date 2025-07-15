package mx.org.kaana.kalan.catalogos.acreedores.beans;

import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.mantic.db.dto.TrManticAcreedorTipoContactoDto;

public class AcreedorTipoContacto extends TrManticAcreedorTipoContactoDto {
	
	private static final long serialVersionUID = -1199288187458829921L;
	private ESql sqlAccion;
	private Boolean nuevo;

	public AcreedorTipoContacto() {
		this(-1L);
	}

	public AcreedorTipoContacto(Long key) {
		this(key, ESql.UPDATE);
	}
	
	public AcreedorTipoContacto(Long key, ESql sqlAccion) {
		this(key, sqlAccion, false);
	}
	
	public AcreedorTipoContacto(Long key, ESql sqlAccion, Boolean nuevo) {
		super(key);
		this.sqlAccion= sqlAccion;
		this.nuevo    = nuevo;
	}

	public ESql getSqlAccion() {
		return sqlAccion;
	}

	public void setSqlAccion(ESql sqlAccion) {
		this.sqlAccion = sqlAccion;
	}	

	public Boolean getNuevo() {
		return nuevo;
	}

	public void setNuevo(Boolean nuevo) {
		this.nuevo = nuevo;
	}
  
}
