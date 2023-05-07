package mx.org.kaana.mantic.incidentes.beans;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 6/04/2022
 *@time 08:37:16 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Repercusion extends TcManticIncidentesDto implements Serializable {

  private static final long serialVersionUID = -1793098780460113513L;
  
  private ESql sql;
  private String clave;
  private String rfc;
  private String nombre;
  private String puesto;
  private String contacto;
  private String incidencia;
  private Long ikTipoIncidente;
  private Long ikEmpresaPersona;
  private Date kinicio;
  
  public Repercusion() {
    this(-1L);
  }

  public Repercusion(Long key) {
    super(key);
    super.setCosto(0D);
    super.setIdUsuario(JsfBase.getIdUsuario());
    this.sql= ESql.INSERT;
  }
  
  public ESql getSql() {
    return sql;
  }

  public void setSql(ESql sql) {
    this.sql = sql;
  }

  public String getClave() {
    return clave;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getRfc() {
    return rfc;
  }

  public void setRfc(String rfc) {
    this.rfc = rfc;
  }

  public String getPuesto() {
    return puesto;
  }

  public void setPuesto(String puesto) {
    this.puesto = puesto;
  }

  public String getContacto() {
    return contacto;
  }

  public void setContacto(String contacto) {
    this.contacto = contacto;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getIncidencia() {
    return incidencia;
  }

  public void setIncidencia(String incidencia) {
    this.incidencia = incidencia;
  }

  public String getIniciox() {
    return Global.format(EFormatoDinamicos.FECHA_CORTA, this.getInicio());
  }

  public String getTerminox() {
    return Global.format(EFormatoDinamicos.FECHA_CORTA, this.getTermino());
  }

  @Override
  public void setInicio(Date inicio) {
    super.setInicio(inicio); 
    this.kinicio= inicio;
  }

  @Override
  public void setIdEmpresaPersona(Long idEmpresaPersona) {
    super.setIdEmpresaPersona(idEmpresaPersona);
    this.ikEmpresaPersona= idEmpresaPersona;
  }

  @Override
  public void setIdTipoIncidente(Long idTipoIncidente) {
    super.setIdTipoIncidente(idTipoIncidente);
    this.ikTipoIncidente= idTipoIncidente;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 11 * hash + Objects.hashCode(this.ikTipoIncidente);
    hash = 11 * hash + Objects.hashCode(this.ikEmpresaPersona);
    hash = 11 * hash + Objects.hashCode(this.kinicio);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) 
      return true;
    if (obj == null) 
      return false;
    if (getClass() != obj.getClass()) 
      return false;
    final Repercusion other = (Repercusion) obj;
    if (!Objects.equals(this.ikTipoIncidente, other.ikTipoIncidente)) 
      return false;
    if (!Objects.equals(this.ikEmpresaPersona, other.ikEmpresaPersona)) 
      return false;
    if (!Objects.equals(this.kinicio, other.kinicio))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Repercusion{" + "sql=" + sql + ", clave=" + clave + ", rfc=" + rfc + ", nombre=" + nombre + ", puesto=" + puesto + ", contacto=" + contacto + ", incidencia=" + incidencia + ", ikTipoIncidente=" + ikTipoIncidente + ", ikEmpresaPersona=" + ikEmpresaPersona + ", kinicio=" + kinicio + '}';
  }

  @Override
  public Repercusion clone() throws CloneNotSupportedException {
    Repercusion regresar= new Repercusion(); 
    regresar.setIncidencia(this.incidencia);
    regresar.setIdEmpresaPersona(this.getIdEmpresaPersona());
    regresar.setNombre(this.getNombre());
    regresar.setRfc(this.getRfc());
    regresar.setPuesto(this.getPuesto());
    regresar.setIdTipoIncidente(this.getIdTipoIncidente());
    regresar.setInicio(this.getTermino());
    regresar.setTermino(this.getTermino());
    regresar.setCosto(this.getCosto());
    regresar.setObservaciones(this.getObservaciones());
    return regresar;
  }
  
}
