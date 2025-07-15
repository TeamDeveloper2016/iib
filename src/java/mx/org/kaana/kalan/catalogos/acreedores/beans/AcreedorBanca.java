package mx.org.kaana.kalan.catalogos.acreedores.beans;

import java.io.Serializable;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.mantic.db.dto.TcManticAcreedoresBancosDto;

public class AcreedorBanca extends TcManticAcreedoresBancosDto implements Serializable{

	private static final long serialVersionUID = 7894536715985257448L;
	private ESql sqlAccion;
	private Boolean nuevo;

	public AcreedorBanca() {
		this(-1L);
	}

	public AcreedorBanca(Long key) {
		this(key, ESql.UPDATE);
	}
	
	public AcreedorBanca(Long key, ESql sqlAccion) {
		this(key, sqlAccion, false);
	}
	
	public AcreedorBanca(Long key, ESql sqlAccion, Boolean nuevo) {
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