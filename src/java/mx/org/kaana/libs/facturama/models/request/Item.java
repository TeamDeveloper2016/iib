package mx.org.kaana.libs.facturama.models.request;

import java.util.List;
import mx.org.kaana.libs.facturama.models.request.complements.ItemComplement;

public class Item {

  private String IdProduct;
  private String ProductCode;
  private String IdentificationNumber;
  private String Description;
  private String Unit;
  private String UnitCode;
  private Double UnitPrice;
  private double Quantity;
  private double Subtotal;
  private Double Discount;
  private String TaxObject;
  private ThirdPartyAccount ThirdPartyAccount;
  private String CuentaPredial;
  private List<String> NumerosPedimento;
  private List<Tax> Taxes;

  private double Total;
  private ItemComplement Complement;

  public String getIdProduct() {
    return IdProduct;
  }

  public void setIdProduct(String IdProduct) {
    this.IdProduct = IdProduct;
  }

  public String getProductCode() {
    return ProductCode;
  }

  public void setProductCode(String ProductCode) {
    this.ProductCode = ProductCode;
  }

  public String getIdentificationNumber() {
    return IdentificationNumber;
  }

  public void setIdentificationNumber(String IdentificationNumber) {
    this.IdentificationNumber = IdentificationNumber;
  }

  public String getDescription() {
    return Description;
  }

  public void setDescription(String Description) {
    this.Description = Description;
  }

  public String getUnit() {
    return Unit;
  }

  public void setUnit(String Unit) {
    this.Unit = Unit;
  }

  public String getUnitCode() {
    return UnitCode;
  }

  public void setUnitCode(String UnitCode) {
    this.UnitCode = UnitCode;
  }

  public Double getUnitPrice() {
    return UnitPrice;
  }

  public void setUnitPrice(Double UnitPrice) {
    this.UnitPrice = UnitPrice;
  }

  public double getQuantity() {
    return Quantity;
  }

  public void setQuantity(double Quantity) {
    this.Quantity = Quantity;
  }

  public double getSubtotal() {
    return Subtotal;
  }

  public void setSubtotal(double Subtotal) {
    this.Subtotal = Subtotal;
  }

  public Double getDiscount() {
    return (this.Discount == null) ? 0 : this.Discount;
  }

  public void setDiscount(Double Discount) {
    this.Discount = Discount;
  }

  public List<Tax> getTaxes() {
    return Taxes;
  }

  public void setTaxes(List<Tax> Taxes) {
    this.Taxes = Taxes;
  }

  public String getCuentaPredial() {
    return CuentaPredial;
  }

  public void setCuentaPredial(String CuentaPredial) {
    this.CuentaPredial = CuentaPredial;
  }

  public double getTotal() {
    return Total;
  }

  public void setTotal(double Total) {
    this.Total = Total;
  }

  public String getTaxObject() {
    return TaxObject;
  }

  public void setTaxObject(String TaxObject) {
    this.TaxObject = TaxObject;
  }

  public ThirdPartyAccount getThirdPartyAccount() {
    return ThirdPartyAccount;
  }

  public void setThirdPartyAccount(ThirdPartyAccount ThirdPartyAccount) {
    this.ThirdPartyAccount = ThirdPartyAccount;
  }

  public ItemComplement getComplement() {
    return Complement;
  }

  public void setComplement(ItemComplement Complement) {
    this.Complement = Complement;
  }

  public List<String> getNumerosPedimento() {
    return NumerosPedimento;
  }

  public void setNumerosPedimento(List<String> NumerosPedimento) {
    this.NumerosPedimento = NumerosPedimento;
  }

  @Override
  public String toString() {
    return "Item{" + "IdProduct=" + IdProduct + ", ProductCode=" + ProductCode + ", IdentificationNumber=" + IdentificationNumber + ", Description=" + Description + ", Unit=" + Unit + ", UnitCode=" + UnitCode + ", UnitPrice=" + UnitPrice + ", Quantity=" + Quantity + ", Subtotal=" + Subtotal + ", Discount=" + Discount + ", TaxObject=" + TaxObject + ", ThirdPartyAccount=" + ThirdPartyAccount + ", CuentaPredial=" + CuentaPredial + ", NumerosPedimento=" + NumerosPedimento + ", Taxes=" + Taxes + ", Total=" + Total + ", Complement=" + Complement + '}';
  }

}
