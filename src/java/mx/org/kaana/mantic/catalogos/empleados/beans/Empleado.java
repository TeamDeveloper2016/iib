package mx.org.kaana.mantic.catalogos.empleados.beans;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.mantic.db.dto.TrManticEmpresaPersonalDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/11/2021
 *@time 01:49:27 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Empleado extends TrManticEmpresaPersonalDto implements Serializable {
  
  private static final long serialVersionUID = 5254966367088680266L;

  private Long id;
  private String nombre;
  private String rfc;
  private String curp;
  private String empresa;
  private String puesto;
  private String departamento;
  private String activo;
  private String seguro;
  private Double sueldo;
  private Double sobre;
  private Boolean limpiar;

  public Empleado() {
    this(new Long((int)(Math.random()*-10000)));
  }

  public Empleado(Long key) {
    super(key);
    this.id= key;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmpresa() {
    return empresa;
  }

  public void setEmpresa(String empresa) {
    this.empresa = empresa;
  }

  public String getCurp() {
    return curp;
  }

  public void setCurp(String curp) {
    this.curp = curp;
  }

  public String getRfc() {
    return rfc;
  }

  public void setRfc(String rfc) {
    this.rfc = rfc;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getPuesto() {
    return puesto;
  }

  public void setPuesto(String puesto) {
    this.puesto = puesto;
  }

  public String getDepartamento() {
    return departamento;
  }

  public void setDepartamento(String departamento) {
    this.departamento = departamento;
  }

  public String getActivo() {
    return activo;
  }

  public void setActivo(String activo) {
    this.activo = activo;
  }

  public String getSeguro() {
    return seguro;
  }

  public void setSeguro(String seguro) {
    this.seguro = seguro;
  }

  public Double getSueldo() {
    return sueldo;
  }

  public void setSueldo(Double sueldo) {
    this.sueldo = sueldo;
  }

  public Double getSobre() {
    return sobre;
  }

  public void setSobre(Double sobre) {
    this.sobre = sobre;
  }

  public Boolean getLimpiar() {
    return limpiar;
  }

  public void setLimpiar(Boolean limpiar) {
    this.limpiar = limpiar;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + Objects.hashCode(this.id);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Empleado other = (Empleado) obj;
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return true;
  }
  
  @Override
  public Class toHbmClass() {
    return TrManticEmpresaPersonalDto.class;
  }

  @Override
  public String toString() {
    return "Empleado{" + "id=" + id + ", empresa=" + empresa + ", rfc=" + rfc + ", nombre=" + nombre + ", puesto=" + puesto + ", departamento=" + departamento + ", activo=" + activo + ", seguro=" + seguro + ", sueldo=" + sueldo + ", sobre=" + sobre + '}';
  }

}
