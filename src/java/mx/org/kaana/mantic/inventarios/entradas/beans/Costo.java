package mx.org.kaana.mantic.inventarios.entradas.beans;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.mantic.db.dto.TcManticNotasCostosDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 30/05/2024
 *@time 09:55:46 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Costo extends TcManticNotasCostosDto implements Serializable {

  private static final long serialVersionUID = 8508760016618493061L;

  private String clave;
  private String nombre;
  private Long idCuenta;
  private String proveedor;
  private String articulo;
  private ESql sql;

  public Costo() {
    this(-1L);
  }

  public Costo(Long key) {
    super(
     -1L, // Long idProveedor, 
     JsfBase.getIdUsuario(), // Long idUsuario, 
     key, // Long idNotaCosto, 
     null, // String observaciones, 
     2L, // Long idGenerar, 
     -1L, // Long idNotaEntrada, 
     0D, // Double importe, 
     -1L, // Long idTipoCosto
     -1L // Long idArticulo      
    );
    this.sql= ESql.INSERT;
  }
  
  public String getClave() {
    return clave;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Long getIdCuenta() {
    return idCuenta;
  }

  public void setIdCuenta(Long idCuenta) {
    this.idCuenta = idCuenta;
  }

  public String getProveedor() {
    return proveedor;
  }

  public void setProveedor(String proveedor) {
    this.proveedor = proveedor;
  }
  
  public ESql getSql() {
    return sql;
  }

  public void setSql(ESql sql) {
    this.sql = sql;
  }

  public String getArticulo() {
    return articulo;
  }

  public void setArticulo(String articulo) {
    this.articulo = articulo;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 73 * hash + Objects.hashCode(this.getIdTipoCosto());
    hash = 73 * hash + Objects.hashCode(this.getIdArticulo());
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
    final Costo other = (Costo) obj;
    if (!Objects.equals(this.getIdTipoCosto(), other.getIdTipoCosto())) 
      return false;
    if (!Objects.equals(this.getIdArticulo(), other.getIdArticulo())) 
      return false;
    return true;
  }

  
  @Override
  public Class toHbmClass() {
    return TcManticNotasCostosDto.class;
  }
  
}
