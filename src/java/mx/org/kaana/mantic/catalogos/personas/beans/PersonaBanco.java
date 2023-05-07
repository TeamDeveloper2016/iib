package mx.org.kaana.mantic.catalogos.personas.beans;

import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetPersonasBancosDto;

public class PersonaBanco extends TcKeetPersonasBancosDto {
	
	private static final long serialVersionUID = 4804251646663919847L;
	private ESql sqlAccion;
	private Boolean nuevo;
	private Boolean principal;

	public PersonaBanco() {
		this(-1L);
	}

	public PersonaBanco(Long key) {
		this(key, ESql.UPDATE);
	}
	
	public PersonaBanco(Long key, ESql sqlAccion) {
		this(key, sqlAccion, false, false);
	}
	
	public PersonaBanco(Long key, ESql sqlAccion, Boolean nuevo, Boolean principal) {
		super(key);
		this.sqlAccion= sqlAccion;
		this.nuevo    = nuevo;
		this.principal= principal;
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

	public Boolean getPrincipal() {
		return principal;
	}

	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}	
}
