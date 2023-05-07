package mx.org.kaana.mantic.catalogos.personas.beans;


import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.keet.db.dto.TcKeetContratistasDepartamentosDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 1/03/2022
 *@time 08:23:04 PM 
 @author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Especialidad extends TcKeetContratistasDepartamentosDto implements Serializable {

  private static final long serialVersionUID = 2395460855351309417L;
  
  private Long idKey;

  public Especialidad() {
    this(-1L);
  }

  public Especialidad(Long idKey) {
    super(idKey);
    this.idKey= idKey;
  }

  
  public Long getIdKey() {
    return idKey;
  }

  public void setIdKey(Long idKey) {
    this.idKey = idKey;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 89 * hash + Objects.hashCode(this.idKey);
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
    final Especialidad other = (Especialidad) obj;
    if (!Objects.equals(this.idKey, other.idKey)) {
      return false;
    }
    return true;
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratistasDepartamentosDto.class; 
  }
  
}
