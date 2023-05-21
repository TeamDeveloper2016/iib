package mx.org.kaana.kalan.db.dto;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 10/10/2016
 *@time 11:58:22 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Entity
@Table(name="tc_kalan_historiales")
public class TcKalanHistorialesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="c31")
  private String c31;
  @Column (name="c30")
  private String c30;
  @Column (name="c11")
  private String c11;
  @Column (name="c33")
  private String c33;
  @Column (name="c10")
  private Date c10;
  @Column (name="c32")
  private String c32;
  @Column (name="c13")
  private Long c13;
  @Column (name="c35")
  private String c35;
  @Column (name="c12")
  private String c12;
  @Column (name="c34")
  private String c34;
  @Column (name="c15")
  private String c15;
  @Column (name="c37")
  private String c37;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_historial")
  private Long idHistorial;
  @Column (name="c14")
  private String c14;
  @Column (name="c36")
  private String c36;
  @Column (name="c17")
  private String c17;
  @Column (name="c39")
  private String c39;
  @Column (name="c16")
  private String c16;
  @Column (name="c38")
  private String c38;
  @Column (name="c19")
  private String c19;
  @Column (name="c18")
  private String c18;
  @Column (name="c40")
  private String c40;
  @Column (name="c20")
  private String c20;
  @Column (name="c22")
  private String c22;
  @Column (name="id_cliente")
  private Long idCliente;
  @Column (name="c21")
  private String c21;
  @Column (name="c02")
  private Date c02;
  @Column (name="c24")
  private String c24;
  @Column (name="c01")
  private Long c01;
  @Column (name="c23")
  private String c23;
  @Column (name="c04")
  private Long c04;
  @Column (name="c26")
  private String c26;
  @Column (name="c03")
  private Long c03;
  @Column (name="c25")
  private String c25;
  @Column (name="c06")
  private String c06;
  @Column (name="c28")
  private String c28;
  @Column (name="c05")
  private String c05;
  @Column (name="c27")
  private String c27;
  @Column (name="c08")
  private String c08;
  @Column (name="c07")
  private String c07;
  @Column (name="c29")
  private String c29;
  @Column (name="c09")
  private String c09;
  @Column (name="c41")
  private String c41;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="id_usuario")
  private Long idUsuario;

  public TcKalanHistorialesDto() {
    this(new Long(-1L));
  }

  public TcKalanHistorialesDto(Long key) {
    this(null, null, null, null, null, null, null, null, null, null, null, null, new Long(-1L), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKalanHistorialesDto(String c31, String c30, String c11, String c33, Date c10, String c32, Long c13, String c35, String c12, String c34, String c15, String c37, Long idHistorial, String c14, String c36, String c17, String c39, String c16, String c38, String c19, String c18, String c40, String c20, String c22, Long idCliente, String c21, Date c02, String c24, Long c01, String c23, Long c04, String c26, Long c03, String c25, String c06, String c28, String c05, String c27, String c08, String c07, String c29, String c09, Long idUsuario, String c41) {
    setC31(c31);
    setC30(c30);
    setC11(c11);
    setC33(c33);
    setC10(c10);
    setC32(c32);
    setC13(c13);
    setC35(c35);
    setC12(c12);
    setC34(c34);
    setC15(c15);
    setC37(c37);
    setIdHistorial(idHistorial);
    setC14(c14);
    setC36(c36);
    setC17(c17);
    setC39(c39);
    setC16(c16);
    setC38(c38);
    setC19(c19);
    setC18(c18);
    setC40(c40);
    setC20(c20);
    setC22(c22);
    setIdCliente(idCliente);
    setC21(c21);
    setC02(c02);
    setC24(c24);
    setC01(c01);
    setC23(c23);
    setC04(c04);
    setC26(c26);
    setC03(c03);
    setC25(c25);
    setC06(c06);
    setC28(c28);
    setC05(c05);
    setC27(c27);
    setC08(c08);
    setC07(c07);
    setC29(c29);
    setC09(c09);
    setC41(c41);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setIdUsuario(idUsuario);
  }
	
  public void setC31(String c31) {
    this.c31 = c31;
  }

  public String getC31() {
    return c31;
  }

  public void setC30(String c30) {
    this.c30 = c30;
  }

  public String getC30() {
    return c30;
  }

  public void setC11(String c11) {
    this.c11 = c11;
  }

  public String getC11() {
    return c11;
  }

  public void setC33(String c33) {
    this.c33 = c33;
  }

  public String getC33() {
    return c33;
  }

  public void setC10(Date c10) {
    this.c10 = c10;
  }

  public Date getC10() {
    return c10;
  }

  public void setC32(String c32) {
    this.c32 = c32;
  }

  public String getC32() {
    return c32;
  }

  public void setC13(Long c13) {
    this.c13 = c13;
  }

  public Long getC13() {
    return c13;
  }

  public void setC35(String c35) {
    this.c35 = c35;
  }

  public String getC35() {
    return c35;
  }

  public void setC12(String c12) {
    this.c12 = c12;
  }

  public String getC12() {
    return c12;
  }

  public void setC34(String c34) {
    this.c34 = c34;
  }

  public String getC34() {
    return c34;
  }

  public void setC15(String c15) {
    this.c15 = c15;
  }

  public String getC15() {
    return c15;
  }

  public void setC37(String c37) {
    this.c37 = c37;
  }

  public String getC37() {
    return c37;
  }

  public void setIdHistorial(Long idHistorial) {
    this.idHistorial = idHistorial;
  }

  public Long getIdHistorial() {
    return idHistorial;
  }

  public void setC14(String c14) {
    this.c14 = c14;
  }

  public String getC14() {
    return c14;
  }

  public void setC36(String c36) {
    this.c36 = c36;
  }

  public String getC36() {
    return c36;
  }

  public void setC17(String c17) {
    this.c17 = c17;
  }

  public String getC17() {
    return c17;
  }

  public void setC39(String c39) {
    this.c39 = c39;
  }

  public String getC39() {
    return c39;
  }

  public void setC16(String c16) {
    this.c16 = c16;
  }

  public String getC16() {
    return c16;
  }

  public void setC38(String c38) {
    this.c38 = c38;
  }

  public String getC38() {
    return c38;
  }

  public void setC19(String c19) {
    this.c19 = c19;
  }

  public String getC19() {
    return c19;
  }

  public void setC18(String c18) {
    this.c18 = c18;
  }

  public String getC18() {
    return c18;
  }

  public void setC40(String c40) {
    this.c40 = c40;
  }

  public String getC40() {
    return c40;
  }

  public void setC20(String c20) {
    this.c20 = c20;
  }

  public String getC20() {
    return c20;
  }

  public void setC22(String c22) {
    this.c22 = c22;
  }

  public String getC22() {
    return c22;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setC21(String c21) {
    this.c21 = c21;
  }

  public String getC21() {
    return c21;
  }

  public void setC02(Date c02) {
    this.c02 = c02;
  }

  public Date getC02() {
    return c02;
  }

  public void setC24(String c24) {
    this.c24 = c24;
  }

  public String getC24() {
    return c24;
  }

  public void setC01(Long c01) {
    this.c01 = c01;
  }

  public Long getC01() {
    return c01;
  }

  public void setC23(String c23) {
    this.c23 = c23;
  }

  public String getC23() {
    return c23;
  }

  public void setC04(Long c04) {
    this.c04 = c04;
  }

  public Long getC04() {
    return c04;
  }

  public void setC26(String c26) {
    this.c26 = c26;
  }

  public String getC26() {
    return c26;
  }

  public void setC03(Long c03) {
    this.c03 = c03;
  }

  public Long getC03() {
    return c03;
  }

  public void setC25(String c25) {
    this.c25 = c25;
  }

  public String getC25() {
    return c25;
  }

  public void setC06(String c06) {
    this.c06 = c06;
  }

  public String getC06() {
    return c06;
  }

  public void setC28(String c28) {
    this.c28 = c28;
  }

  public String getC28() {
    return c28;
  }

  public void setC05(String c05) {
    this.c05 = c05;
  }

  public String getC05() {
    return c05;
  }

  public void setC27(String c27) {
    this.c27 = c27;
  }

  public String getC27() {
    return c27;
  }

  public void setC08(String c08) {
    this.c08 = c08;
  }

  public String getC08() {
    return c08;
  }

  public void setC07(String c07) {
    this.c07 = c07;
  }

  public String getC07() {
    return c07;
  }

  public void setC29(String c29) {
    this.c29 = c29;
  }

  public String getC29() {
    return c29;
  }

  public void setC09(String c09) {
    this.c09 = c09;
  }

  public String getC09() {
    return c09;
  }

  public String getC41() {
    return c41;
  }

  public void setC41(String c41) {
    this.c41 = c41;
  }

  public void setRegistro(Timestamp registro) {
    this.registro = registro;
  }

  public Timestamp getRegistro() {
    return registro;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdHistorial();
  }

  @Override
  public void setKey(Long key) {
  	this.idHistorial = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getC31());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC30());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC11());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC33());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC10());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC32());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC13());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC35());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC12());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC34());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC15());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC37());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdHistorial());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC14());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC36());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC17());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC39());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC16());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC38());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC19());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC18());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC40());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC20());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC22());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC21());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC02());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC24());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC01());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC23());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC04());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC26());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC03());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC25());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC06());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC28());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC05());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC27());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC08());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC07());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC29());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC09());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getC41());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("c31", getC31());
		regresar.put("c30", getC30());
		regresar.put("c11", getC11());
		regresar.put("c33", getC33());
		regresar.put("c10", getC10());
		regresar.put("c32", getC32());
		regresar.put("c13", getC13());
		regresar.put("c35", getC35());
		regresar.put("c12", getC12());
		regresar.put("c34", getC34());
		regresar.put("c15", getC15());
		regresar.put("c37", getC37());
		regresar.put("idHistorial", getIdHistorial());
		regresar.put("c14", getC14());
		regresar.put("c36", getC36());
		regresar.put("c17", getC17());
		regresar.put("c39", getC39());
		regresar.put("c16", getC16());
		regresar.put("c38", getC38());
		regresar.put("c19", getC19());
		regresar.put("c18", getC18());
		regresar.put("c40", getC40());
		regresar.put("c20", getC20());
		regresar.put("c22", getC22());
		regresar.put("idCliente", getIdCliente());
		regresar.put("c21", getC21());
		regresar.put("c02", getC02());
		regresar.put("c24", getC24());
		regresar.put("c01", getC01());
		regresar.put("c23", getC23());
		regresar.put("c04", getC04());
		regresar.put("c26", getC26());
		regresar.put("c03", getC03());
		regresar.put("c25", getC25());
		regresar.put("c06", getC06());
		regresar.put("c28", getC28());
		regresar.put("c05", getC05());
		regresar.put("c27", getC27());
		regresar.put("c08", getC08());
		regresar.put("c07", getC07());
		regresar.put("c29", getC29());
		regresar.put("c09", getC09());
		regresar.put("c41", getC41());
		regresar.put("registro", getRegistro());
		regresar.put("idUsuario", getIdUsuario());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getC31(), getC30(), getC11(), getC33(), getC10(), getC32(), getC13(), getC35(), getC12(), getC34(), getC15(), getC37(), getIdHistorial(), getC14(), getC36(), getC17(), getC39(), getC16(), getC38(), getC19(), getC18(), getC40(), getC20(), getC22(), getIdCliente(), getC21(), getC02(), getC24(), getC01(), getC23(), getC04(), getC26(), getC03(), getC25(), getC06(), getC28(), getC05(), getC27(), getC08(), getC07(), getC29(), getC09(), getC41(), getRegistro(), getIdUsuario()
    };
    return regresar;
  }

  @Override
  public Object toValue(String name) {
    return Methods.getValue(this, name);
  }

  @Override
  public String toAllKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("|");
    regresar.append("idHistorial~");
    regresar.append(getIdHistorial());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdHistorial());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKalanHistorialesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdHistorial()!= null && getIdHistorial()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKalanHistorialesDto other = (TcKalanHistorialesDto) obj;
    if (getIdHistorial() != other.idHistorial && (getIdHistorial() == null || !getIdHistorial().equals(other.idHistorial))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdHistorial() != null ? getIdHistorial().hashCode() : 0);
    return hash;
  }

}


