package mx.org.kaana.libs.archivo;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.StringTokenizer;
import jxl.CellView;
import jxl.biff.CellReferenceHelper;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Blank;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableWorkbook;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Encriptar;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class XlsBase implements Serializable {

  private static final long serialVersionUID = 5339778084264506908L;	
  private static final char[] ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
  private static final Log LOG= LogFactory.getLog(XlsBase.class);
  
	protected WritableWorkbook libro;
  protected WritableSheet hoja;
  protected boolean debug;
  protected int posicionColumna;
  protected int posicionFila;
  private int numeroColumna;
  private int indiceColumna;
  private String[] formatos;
  
  public void setLibro(WritableWorkbook libro) {
    this.libro = libro;
  }

  public WritableWorkbook getLibro() {
    return libro;
  }

  public void setHoja(WritableSheet hoja) {
    this.hoja = hoja;
  }

  public WritableSheet getHoja() {
    return hoja;
  }

  public void setPosicionColumna(int posicionColumna) {
    this.posicionColumna = posicionColumna;
  }

  public int getPosicionColumna() {
    return posicionColumna;
  }

  public void setPosicionFila(int posicionFila) {
    this.posicionFila = posicionFila;
  }

  public int getPosicionFila() {
    return posicionFila;
  }

  public void setNumeroColumna(int numeroColumna) {
    this.numeroColumna = numeroColumna;
  }

  public int getNumeroColumna() {
    return numeroColumna;
  }

  public void setIndiceColumna(int indiceColumna) {
    this.indiceColumna = indiceColumna;
  }

  public int getIndiceColumna() {
    return indiceColumna;
  }

  public void setFormatos(String[] formatos) {
    this.formatos = formatos;
  }

  public String[] getFormatos() {
    return formatos;
  }
  
	@Override
  public void finalize() {
    libro   = null;
    hoja    = null;
    formatos= null;
  } // finalize
  
  public void demoDatos(int columnas, int filas) {
    try {
      for (int x = 0; x < columnas; x++) {
        if (debug) {
          Label label = new Label(0, x, "[" + x + "]");
          hoja.addCell(label);
        } // if
        Label label = new Label(filas + 2, x, "=suma(" + (char)(65 + x) + "2:" + (char)(65 + x) + (filas + 1) + ")");
        hoja.addCell(label);
        for (int y = 0; y < filas; y++) {
          if (x % 2 == 0) {
            Number number = new Number(y + 1, x, 1231234.12341234);
            hoja.addCell(number);
            libro.write();
          } // if
          else {
            Label label2 = new Label(y + 1, x, "[" + y + "," + x + "]");
            hoja.addCell(label2);
            libro.write();
          } // else
        } // for y
      } // for x
      libro.close();
    } // try
    catch (Exception e) {
       Error.mensaje(e);
    }// try
  } 
   
  protected abstract String getNombresColumnas();
  
  protected abstract int getColumnasInformacion();
	
	public abstract boolean generarRegistros(boolean titulo) throws Exception;
  
  protected void procesarEncabezado(String algunos) {
    try {
      StringTokenizer stringTokenizer= new StringTokenizer(algunos.toUpperCase(), ",");
      String alias= "";
      int x       = 1;
      while (stringTokenizer.hasMoreTokens()) {
        alias = stringTokenizer.nextToken();
        Label label = new Label(getPosicionColumna()+ x- 1, getPosicionFila(), alias);
        hoja.addCell(label);  
        x++;
      }// while
    } // try
    catch (Exception e) {
      Error.mensaje(e);
    }// try
  }
    
  public String formatearValor(String celda, String formato) {
    try {
      int codigo  = Integer.parseInt(formato);
      double valor= 0;
      switch (codigo) {
        case 0: // no hacer ninguna validacion
        break;
        case 1: // formato moneda
          valor = Double.parseDouble(celda);
          celda = Numero.formatear("$ ###,##0.00", valor);
          break;
        case 2: // separacion de miles
          valor = Double.parseDouble(celda);
          celda = Numero.formatear("###,##0.00", valor);
          break;
        case 3: // separacion de miles sin decimales
          valor = Double.parseDouble(celda);
          celda = Numero.formatear("###,###", valor);
          break;
        case 4: // Letra capital la primer letra
          celda = Cadena.letraCapital(celda);
          break;
        case 5: // Letra capital por cada palabra en la cadena
          celda = Cadena.nombrePersona(celda);
          break;
        case 6: // Letra en mayusculas
          celda = celda.toUpperCase();
          break;
        case 7: // Letra en minusculas
          celda = celda.toLowerCase();
          break;
        case 14: // desencriptar palabra
          Encriptar desencriptar = new Encriptar();
          celda = desencriptar.desencriptar(celda, desencriptar._CLAVE);
          desencriptar = null;
          break;
        case 15: // encriptar palabra
          Encriptar encriptar = new Encriptar();
          celda = encriptar.encriptar(celda, encriptar._CLAVE);
          encriptar = null;
          break;
        case 16: // wakko
          valor = Double.parseDouble(celda);
          celda = Numero.formatear("0.00", valor);
          break;
      }// switch
      if (codigo > 7 && codigo < 14) {
        GregorianCalendar calendario = new GregorianCalendar();
        int xAnio = Integer.parseInt(celda.substring(0, 4));
        int xMes = Integer.parseInt(celda.substring(4, 6)) - 1;
        int xDia = Integer.parseInt(celda.substring(6, 8));
        calendario.set(xAnio, xMes, xDia);
        switch (codigo) {
        case 8: // Fecha en dd/mes/yyyy   26/Noviembre/2003
          celda =
             calendario.get(calendario.DATE) + "/" + Fecha.getNombreMes(calendario.get(calendario.MONTH)) + "/" + celda.substring(0,4);
          break;
        case 9: // Fecha en dd/mm/yyyy    26/11/2003
          celda = celda.substring(6, 8) + "/" + celda.substring(4, 6) + "/" + celda.substring(0, 4);
          break;
        case 10: // Fecha en:  nombre del dia, dd/mm/yyyy    Miercoles, 26/11/2003
          celda = Fecha.getNombreDia(calendario.get(calendario.DAY_OF_WEEK)) + ", " + celda.substring(6, 8) + "/" + celda.substring(4,6) +
              "/" + celda.substring(0, 4);
          break;
        case 11: // Fecha en:  nombre del dia, dia mes a?o   Miercoles, 26 de Noviembre del 2003
          celda = Fecha.getNombreDia(calendario.get(calendario.DAY_OF_WEEK)) + ", " + calendario.get(calendario.DATE) + " de " + Fecha.getNombreMes(calendario.get(calendario.MONTH)) + " de " + calendario.get(calendario.YEAR);
          break;
        case 12: // Fecha en dd/mm/yy   26/11/03
          celda = celda.substring(6, 8) + "/" + celda.substring(4, 6) + "/" + celda.substring(2, 4);
          break;
        case 13: // Fecha en:  dia mes a?o  26 de Noviembre del 2003
          celda =
              calendario.get(calendario.DATE) + " de " + Fecha.getNombreMes(calendario.get(calendario.MONTH)) + " de " +
              calendario.get(calendario.YEAR);
          break;
        }// switch
      }// if
    }
    catch (Exception e) {
      Error.mensaje(e);
    }    
    return celda;
  } 

  protected WritableCellFormat toCellFormat(Colour colour) throws WriteException {
    WritableFont cellFonts = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, colour);
    WritableCellFormat regresar= new WritableCellFormat(cellFonts);
    return regresar;
  }  
  
  protected WritableCellFormat toCellColorFormat(Colour colour) throws WriteException {
    return this.toCellColorFormat(Alignment.CENTRE, colour);
  }
  
  protected WritableCellFormat toCellColorFormat(Alignment alignment, Colour colour) throws WriteException {
    WritableFont cellFonts = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.WHITE);
    WritableCellFormat regresar= new WritableCellFormat(cellFonts);
    regresar.setBorder(jxl.format.Border.BOTTOM, jxl.format.BorderLineStyle.THIN);
    regresar.setAlignment(alignment);
    regresar.setBackground(colour);
    return regresar;
  }  
  
  protected WritableCellFormat toCellFormatNegritas(Alignment alignment) throws WriteException {
    WritableFont cellFonts     = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
    WritableCellFormat regresar= new WritableCellFormat(new NumberFormat("#,##0.00"));
    regresar.setAlignment(alignment);
    regresar.setFont(cellFonts);
    return regresar;
  }  
  
  protected WritableCellFormat toCellFormatNegritasColor(Alignment alignment, Colour colour) throws WriteException {
    WritableFont cellFonts     = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
    WritableCellFormat regresar= new WritableCellFormat(new NumberFormat("#,##0.00"));
    regresar.setAlignment(alignment);
    regresar.setBorder(jxl.format.Border.TOP, jxl.format.BorderLineStyle.THIN);
    regresar.setFont(cellFonts);
    regresar.setBackground(colour);
    return regresar;
  }  
  
  protected WritableCellFormat toCellFormatPorcentaje(Alignment alignment) throws WriteException {
    WritableFont cellFonts     = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
    WritableCellFormat regresar= new WritableCellFormat(new NumberFormat("##0.00%"));
    regresar.setAlignment(alignment);
    regresar.setFont(cellFonts);
    return regresar;
  }  
  
  protected WritableCellFormat toCellCostoFormat(Alignment alignment) throws WriteException {
    WritableFont cellFonts     = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
    WritableCellFormat regresar= new WritableCellFormat(cellFonts);
    regresar.setAlignment(alignment);
    return regresar;
  }
  
  protected WritableCellFormat toCellCostoFormatColor(Alignment alignment, Colour colour) throws WriteException {
    WritableFont cellFonts     = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
    WritableCellFormat regresar= new WritableCellFormat(cellFonts);
    regresar.setBorder(jxl.format.Border.BOTTOM, jxl.format.BorderLineStyle.THIN);
    regresar.setAlignment(alignment);
    regresar.setBackground(colour);
    return regresar;
  }  
  
  protected WritableCellFormat toCellTotalFormat(Alignment alignment, Boolean line) throws WriteException {
    return this.toCellTotalFormat(alignment, line, Colour.WHITE);
  }
  
  protected WritableCellFormat toCellTotalFormat(Alignment alignment, Boolean line, Colour colour) throws WriteException {
    WritableFont cellFonts = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
    WritableCellFormat regresar= new WritableCellFormat(cellFonts);
    if(line)
      regresar.setBorder(jxl.format.Border.TOP, jxl.format.BorderLineStyle.THIN);
    regresar.setAlignment(alignment);
    regresar.setBackground(colour);
    return regresar;
  }  
  
  protected void addCell(int column, int row, String data) throws Exception {
    try {
      Label label= new Label(column, row, data);
      this.hoja.addCell(label);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  protected void addCell(int column, int row, String data, Alignment alignment) throws Exception {
    try {
      Label label= new Label(column, row, data, this.toCellCostoFormat(alignment));
      this.hoja.addCell(label);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  protected void addFormula(int column, int row, String data, WritableCellFormat format) throws Exception {
    try {
      Formula formula= new Formula(column, row, data, format);
      this.hoja.addCell(formula);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  protected void addCellNegritas(int column, int row, String data, Alignment alignment) throws Exception {
    try {
      Label label= new Label(column, row, data, this.toCellFormatNegritas(alignment));
      this.hoja.addCell(label);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  protected void addCellNegritasColor(int column, int row, String data, Alignment alignment, Colour colour) throws Exception {
    try {
      Label label= new Label(column, row, data, this.toCellFormatNegritasColor(alignment, colour));
      this.hoja.addCell(label);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  protected void addCellColor(int column, int row, String data) throws Exception {
    try {
      this.addCellColor(column, row, data, jxl.format.Colour.BLUE_GREY);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  protected void addCellColor(int column, int row, String data, Colour color) throws Exception {
    try {
      Label label= new Label(column, row, data, this.toCellColorFormat(color));
      this.hoja.addCell(label);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  protected void addCellColor(int column, int row, String data, Alignment alignment, Colour color) throws Exception {
    try {
      WritableCell cell= null;
      if(data== null)
        cell= new Blank(column, row, this.toCellColorFormat(alignment, color));
      else        
        cell= new Label(column, row, data, this.toCellColorFormat(alignment, color));
      this.hoja.addCell(cell);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  protected void addFontColor(int column, int row, String data, Colour color) throws Exception {
    try {
      Label label= new Label(column, row, data, this.toCellFormat(color));
      this.hoja.addCell(label);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  protected void addCellCosto(int column, int row, String data) throws Exception {
    this.addCellCosto(column, row, data, Alignment.CENTRE);
  }
  
  protected void addCellCosto(int column, int row, String data, Alignment alignment) throws Exception {
    try {
      Label label= new Label(column, row, data, this.toCellCostoFormat(alignment));
      this.hoja.addCell(label);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  protected void addCellCosto(int column, int row, String data, Alignment alignment, Colour colour) throws Exception {
    try {
      Label label= new Label(column, row, data, this.toCellCostoFormatColor(alignment, colour));
      this.hoja.addCell(label);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  protected void addCellTotal(int column, int row, String data, Alignment alignment, Boolean line) throws Exception {
    try {
      Label label= new Label(column, row, data, this.toCellTotalFormat(alignment, line));
      this.hoja.addCell(label);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
 
  protected void addCellTotal(int column, int row, String data, Alignment alignment, Boolean line, Colour colour) throws Exception {
    try {
      Label label= new Label(column, row, data, this.toCellTotalFormat(alignment, line, colour));
      this.hoja.addCell(label);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
 
  protected WritableCellFormat toCell(Alignment alignment, Colour background, Colour foreground, Boolean line) throws WriteException {
    WritableFont cellFonts     = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, foreground);
    WritableCellFormat regresar= new WritableCellFormat(cellFonts);
    if(line)
      regresar.setBorder(jxl.format.Border.TOP, jxl.format.BorderLineStyle.THIN);
    regresar.setAlignment(alignment);
    regresar.setBackground(background);
    return regresar;
  }  
  
  protected void addCell(int column, int row, String data, Alignment alignment, Colour background, Colour foreground, Boolean line) throws Exception {
    try {
      WritableCell cell= null;
      if(data== null)
        cell= new Blank(column, row, this.toCell(alignment, background, foreground, line));
      else        
        cell= new Label(column, row, data, this.toCell(alignment, background, foreground, line));
      this.hoja.addCell(cell);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }

  protected WritableCellFormat toNumberNegritas(Alignment alignment, Colour background, Colour foreground, Boolean line) throws WriteException {
    NumberFormat numberFormat= new NumberFormat("###,##0.00");
    WritableFont cellFonts   = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, foreground);
    WritableCellFormat regresar= new WritableCellFormat(cellFonts, numberFormat);
    if(line)
      regresar.setBorder(jxl.format.Border.TOP, jxl.format.BorderLineStyle.THIN);
    regresar.setAlignment(alignment);
    if(!Objects.equals(background, Colour.UNKNOWN) && !Objects.equals(background, Colour.DEFAULT_BACKGROUND))
      regresar.setBackground(background);
    return regresar;
  }  
  
  protected WritableCellFormat toNumber(Alignment alignment, Colour background, Colour foreground, Boolean line) throws WriteException {
    NumberFormat numberFormat= new NumberFormat("###,##0.00");
    WritableFont cellFonts   = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, foreground);
    WritableCellFormat regresar= new WritableCellFormat(cellFonts, numberFormat);
    if(line)
      regresar.setBorder(jxl.format.Border.TOP, jxl.format.BorderLineStyle.THIN);
    regresar.setAlignment(alignment);
    if(!Objects.equals(background, Colour.UNKNOWN) && !Objects.equals(background, Colour.DEFAULT_BACKGROUND))
      regresar.setBackground(background);
    return regresar;
  }  
  
  protected void addNumber(int column, int row, Double data, Alignment alignment, Colour background, Colour foreground, Boolean line) throws Exception {
    try {
      WritableCell cell= null;
      if(data== null || data== 0D)
        cell= new Blank(column, row, this.toCell(alignment, background, foreground, line));
      else        
        cell= new Number(column, row, data, this.toNumber(alignment, background, foreground, line));
      this.hoja.addCell(cell);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
 
  protected void addNumber(int column, int row, Double data, WritableCellFormat format) throws Exception {
    try {
      WritableCell cell= null;
      if(data== null || data== 0D)
        cell= new Blank(column, row, format);
      else        
        cell= new Number(column, row, data, format);
      this.hoja.addCell(cell);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
 
  protected void addDouble(int column, int row, Double data, WritableCellFormat format) throws Exception {
    try {
      WritableCell cell= new Number(column, row, data, format);
      this.hoja.addCell(cell);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
 
  protected void toAddView(int column, int characters) {
    CellView cell= this.hoja.getColumnView(column);
    cell.setSize(characters* 256 + 100);
    this.hoja.setColumnView(column, cell);      
  }
 
  protected String toColumnName(int column, int row) {
    return CellReferenceHelper.getCellReference(column, row); 
  }
  
}