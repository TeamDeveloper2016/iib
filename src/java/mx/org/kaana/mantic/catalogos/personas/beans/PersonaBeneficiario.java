package mx.org.kaana.mantic.catalogos.personas.beans;

import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetPersonasBeneficiariosDto;

public class PersonaBeneficiario extends TcKeetPersonasBeneficiariosDto {
	
	private static final long serialVersionUID = 731679150148040999L;	
  
	private ESql sqlAccion;
	private Boolean nuevo;	
	private Long consecutivo;
	private Boolean modificar;	

	public PersonaBeneficiario() {
		this(-1L);
	}

	public PersonaBeneficiario(Long key) {
		this(key, ESql.UPDATE);
	}
	
	public PersonaBeneficiario(Long key, ESql sqlAccion) {
		this(key, sqlAccion, false);
	}
	
	public PersonaBeneficiario(Long key, ESql sqlAccion, Boolean nuevo) {		
		this(key, sqlAccion, nuevo, 0L, false);
	}
	
	public PersonaBeneficiario(Long key, ESql sqlAccion, Boolean nuevo, Long consecutivo, Boolean modificar) {		
		super(key);
		this.sqlAccion  = sqlAccion;
		this.nuevo      = nuevo;
		this.consecutivo= consecutivo;
		this.modificar  = modificar;
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

	public Long getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(Long consecutivo) {
		this.consecutivo = consecutivo;
	}

	public Boolean getModificar() {
		return modificar;
	}

	public void setModificar(Boolean modificar) {
		this.modificar = modificar;
	}		
  
}