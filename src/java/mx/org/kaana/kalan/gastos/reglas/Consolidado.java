package mx.org.kaana.kalan.gastos.reglas;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.scene.paint.Color;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.archivo.XlsBase;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticEmpresasDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 18/04/2025
 *@time 07:52:16 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Consolidado extends XlsBase implements Serializable {

  private static final long serialVersionUID = 6910973771034810995L;
  private static final Log LOG = LogFactory.getLog(Consolidado.class);
  
  private Long idEmpresa;
  private Long ejercicio;
  private Long mes;
  private String actual;
  private String anterior;  
  private String tactual;
  private String tanterior;  
  private String path;

  public Consolidado() throws Exception {
    this(-1L);  
  }
  
  public Consolidado(Long idEmpresa) throws Exception {
    this(idEmpresa, -1L, -1L);
  }

  public Consolidado(Long idEmpresa, Long ejercicio, Long mes) throws Exception {
    this.idEmpresa= idEmpresa;
    this.ejercicio= ejercicio== null || ejercicio== -1L? Fecha.getAnioActual(): ejercicio;
    this.mes      = mes== null || mes== -1L? Fecha.getMesActual(): mes;
    this.path     = "";
    this.init();
  }

  private void init() throws Exception {
    // this.posicion= 0;
    this.toNameTitle();
  }

  @Override
  protected String getNombresColumnas() {
    return "";
  }

  @Override
  protected int getColumnasInformacion() {
    return 0;
  }

  @Override
  public boolean generarRegistros(boolean titulo) throws Exception {
    return Boolean.FALSE;
  }
  
  protected String local() throws Exception {
    this.path= "d:/";
    return this.process();
  }
  
  public String execute() throws Exception {
    this.path= JsfBase.getRealPath("").concat(EFormatos.XLS.toPath());
    return this.process();
  }
  
  private String process() throws Exception {
    String regresar           = "";
    Map<String, Object> params= new HashMap<>();
    List<Entity> gastos       = null;
    try {      
      TcManticEmpresasDto empresa= (TcManticEmpresasDto)DaoFactory.getInstance().findById(TcManticEmpresasDto.class, this.idEmpresa);
      regresar= Archivo.toFormatNameOutFile(Constantes.ARCHIVO_PATRON_NOMBRE, "RESUMEN-".concat(this.tactual).concat(".").concat(EFormatos.XLS.name().toLowerCase()));
      this.posicionFila   = 0;
      this.posicionColumna= 0;
      this.libro= Workbook.createWorkbook(new File(this.path.concat(regresar)));
      this.hoja = this.libro.createSheet(Constantes.ARCHIVO_PATRON_NOMBRE, 0);
      this.addCell(this.posicionColumna, this.posicionFila++, "SUCURSAL: [".concat(empresa.getNombre()).concat("] ").concat(empresa.getTitulo()));
      this.addCell(this.posicionColumna, this.posicionFila++, "RESUMEN DE GASTOS DEL MES DE ".concat(Fecha.getNombreMes(this.mes.intValue()- 1).toUpperCase()).concat(" DEL ")+ this.ejercicio);
      gastos  = this.toLoadData();
      if(!Objects.equals(gastos, null) && !gastos.isEmpty()) {
        this.posicionFila++;
        this.toWriteResumen(gastos);
        this.posicionFila++;
        this.toLoadIndividuales(empresa);
      } // if
      else {
        this.posicionFila++;
        this.addCellColor(this.posicionColumna, this.posicionFila, "NO EXISTEN GASTOS REGISTRADOS EN EL MES DE REFERENCIA", jxl.format.Colour.BLUE);
        this.toAddView(0, 80);
      } // else
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
  	  this.libro.write();
      this.libro.close();
    } // finally
    return regresar;
  }  
  
  private List<Entity> toLoadData() throws Exception {
    List<Entity> regresar     = null;
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("actual", this.actual);
      params.put("anterior", this.anterior);
      params.put("idEmpresa", this.idEmpresa);
      regresar = DaoFactory.getInstance().toEntitySet("VistaEmpresasGastosDto", "acumulados", params);
      if(regresar!= null && !regresar.isEmpty()) {
        Entity pivot  = null;
        Double vigente= 0D; Double  atras   = 0D;
        Double tactual= 0D; Double tanterior= 0D;
        for (Entity item: regresar) {
          vigente  += item.toDouble("actual");
          atras    += item.toDouble("anterior");
          tactual  += item.toDouble("actual");
          tanterior+= item.toDouble("anterior");
          pivot= item;
        } // for
        Entity subtotal= pivot.clone();
        subtotal.get("idKey").setData(997L);
        subtotal.get("clasificacion").setData("SUB TOTAL");
        subtotal.put("actual", new Value("actual", vigente));
        subtotal.put("anterior", new Value("anterior", atras));
        regresar.add(subtotal);
        Entity ingresos= (Entity)DaoFactory.getInstance().toEntity("VistaEmpresasMovimientosDto", "acumulados", params);
        if(ingresos== null || ingresos.isEmpty()) {
          ingresos= pivot.clone();
          ingresos.get("idKey").setData(998L);
          ingresos.get("clasificacion").setData("INGRESOS VARIOS");
          ingresos.put("actual", new Value("actual", 0D));
          ingresos.put("anterior", new Value("anterior", 0D));
        } // if  
        regresar.add(ingresos);
        Entity total= pivot.clone();
        total.get("idKey").setData(999L);
        total.get("empresa").setData("");
        total.get("clasificacion").setData("TOTAL");
        total.put("actual", new Value("actual", tactual- ingresos.toDouble("actual")));
        total.put("anterior", new Value("anterior", tanterior- ingresos.toDouble("anterior")));
        regresar.add(total);
      } //if  
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
    return regresar;
  }

  private void toWriteResumen(List<Entity> items) throws Exception {
    try {      
      this.addCellColor(this.posicionColumna,    this.posicionFila, "CLASIFICACION", Alignment.CENTRE, Colour.LIGHT_ORANGE);
      this.addCellColor(this.posicionColumna+ 1, this.posicionFila, this.tactual, Alignment.CENTRE, Colour.LIGHT_ORANGE);
      this.addCellColor(this.posicionColumna+ 2, this.posicionFila++, this.tanterior, Alignment.CENTRE, Colour.LIGHT_ORANGE);
      LOG.info("------------------------[ RESUMEN GLOBAL ]-------------------------------");
      for (Entity item: items) {
        if(item.toString("clasificacion").contains("TOTAL")) {
          this.addCellColor(this.posicionColumna, this.posicionFila, item.toString("clasificacion"), Alignment.RIGHT, Colour.LIME);
          this.addCellNegritasColor(this.posicionColumna+ 1, this.posicionFila, Numero.formatear(Numero.MILES_SAT_DECIMALES, item.toDouble("actual")), Alignment.RIGHT, Colour.LIME);
          this.addCellNegritasColor(this.posicionColumna+ 2, this.posicionFila++, Numero.formatear(Numero.MILES_SAT_DECIMALES, item.toDouble("anterior")), Alignment.RIGHT, Colour.LIME);
        } // if  
        else {
          this.addCell(this.posicionColumna, this.posicionFila, item.toString("clasificacion"));
          this.addCellCosto(this.posicionColumna+ 1, this.posicionFila, Numero.formatear(Numero.MILES_SAT_DECIMALES, item.toDouble("actual")), Alignment.RIGHT);
          this.addCellCosto(this.posicionColumna+ 2, this.posicionFila++, Numero.formatear(Numero.MILES_SAT_DECIMALES, item.toDouble("anterior")), Alignment.RIGHT);
        } // else  
      } // for
      LOG.info("-----------------------------------------------------------------------");
      this.toAddView(0, 30);
      this.toAddView(1, 20);
      this.toAddView(2, 20);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }
  
  private void toNameTitle() throws Exception {
    try {
      Calendar calendar= new GregorianCalendar(this.ejercicio.intValue(), this.mes.intValue()- 1, 1);
      int mesActual= calendar.get(Calendar.MONTH)+ 1;
      this.actual  = calendar.get(Calendar.YEAR)+ Constantes.SEPARADOR+ (mesActual< 10? "0": "")+ mesActual;
      calendar.add(Calendar.MONTH, -1);
      int mesAnterior= calendar.get(Calendar.MONTH)+ 1;
      this.anterior  = calendar.get(Calendar.YEAR)+ Constantes.SEPARADOR+ (mesAnterior< 10? "0": "")+ mesAnterior;
      
      String mactual  = Fecha.getNombreMesCorto(this.mes.intValue()- 1);
      String manterior= Fecha.getNombreMesCorto(Objects.equals(this.mes, 1L)? 11: this.mes.intValue()- 2);
      this.tactual    = this.ejercicio+ "-".concat(mactual);
      this.tanterior  = this.ejercicio+ "-".concat(manterior);
    } // try
    catch (Exception e) {
      throw e;
    } // catch      
  }

  private void toLoadIndividuales(TcManticEmpresasDto empresa) throws Exception {
    Map<String, Object> params= new HashMap<>();
    try {
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);      
      List<Entity> clasificaciones= (List<Entity>)DaoFactory.getInstance().toEntitySet("TcKalanGastosClasificacionesDto", params);
      int index= 0;
      for (Entity item: clasificaciones) {
        this.toWriteGastoIndividual(empresa, item, ++index);
      } // for
    } // try
    catch (Exception e) {
      throw e;
    } // catch      
    finally {
      Methods.clean(params);
    } // finally
  } 
  
  private void toWriteGastoIndividual(TcManticEmpresasDto empresa, Entity clasificacion, int index) throws Exception {
    Map<String, Object> params= new HashMap<>();
    Double sactual  = 0D;
    Double santerior= 0D;
    try {
      this.posicionFila   = 0;
      this.posicionColumna= 0;
      this.hoja = this.libro.createSheet("COMPARATIVO-".concat(clasificacion.toString("clave")), index);
      this.addCell(this.posicionColumna, this.posicionFila++, "SUCURSAL: [".concat(empresa.getNombre()).concat("] ").concat(empresa.getTitulo()));
      this.addCell(this.posicionColumna, this.posicionFila++, "AÑO: "+ this.ejercicio);
      this.addCell(this.posicionColumna, this.posicionFila++, "MES: ".concat(Fecha.getNombreMes(this.mes.intValue()- 1).toUpperCase()));
      this.addCell(this.posicionColumna, this.posicionFila++, "CLASIFICADOR: ".concat(clasificacion.toString("descripcion")));
      
      this.posicionFila++;
      this.addCellColor(this.posicionColumna,    this.posicionFila, "CLASIFICACION", Alignment.CENTRE, Colour.LIGHT_ORANGE);
      this.addCellColor(this.posicionColumna+ 1, this.posicionFila, "SUB CLASIFICACION", Alignment.CENTRE, Colour.LIGHT_ORANGE);
      this.addCellColor(this.posicionColumna+ 2, this.posicionFila, this.tactual, Alignment.CENTRE, Colour.LIGHT_ORANGE);
      this.addCellColor(this.posicionColumna+ 3, this.posicionFila++, this.tanterior, Alignment.CENTRE, Colour.LIGHT_ORANGE);
      params.put("idEmpresa", empresa.getIdEmpresa());
      params.put("actual", this.actual);
      params.put("anterior", this.anterior);
      params.put("idEmpresa", empresa.getIdEmpresa());
      params.put("idGastoClasificacion", clasificacion.toLong("idGastoClasificacion"));
      List<Entity> detalle= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaEmpresasGastosDto", "individual", params);
      if(!Objects.equals(detalle, null) && !detalle.isEmpty()) {
        LOG.info("------------------------[ COMPARATIVO ".concat(clasificacion.toString("descripcion")).concat(" ]-------------------------------"));
        for (Entity item: detalle) {
          this.addCell(this.posicionColumna,    this.posicionFila, item.toString("clasificacion"));
          this.addCell(this.posicionColumna+ 1, this.posicionFila, item.toString("subClasificacion"));
          this.addCellCosto(this.posicionColumna+ 2, this.posicionFila, Numero.formatear(Numero.MILES_SAT_DECIMALES, item.toDouble("actual")), Alignment.RIGHT);
          this.addCellCosto(this.posicionColumna+ 3, this.posicionFila++, Numero.formatear(Numero.MILES_SAT_DECIMALES, item.toDouble("anterior")), Alignment.RIGHT);
          sactual  += item.toDouble("actual");
          santerior+= item.toDouble("anterior");
        } // for
        this.addCellColor(this.posicionColumna+ 1, this.posicionFila, "TOTAL:", Alignment.RIGHT, Colour.LIME);
        this.addCellNegritasColor(this.posicionColumna+ 2, this.posicionFila, Numero.formatear(Numero.MILES_SAT_DECIMALES, sactual), Alignment.RIGHT, Colour.LIME);
        this.addCellNegritasColor(this.posicionColumna+ 3, this.posicionFila++, Numero.formatear(Numero.MILES_SAT_DECIMALES, santerior), Alignment.RIGHT, Colour.LIME);
        LOG.info("-----------------------------------------------------------------------");
      } // if
      else
        this.addCell(this.posicionColumna, this.posicionFila++, "NO EXISTEN GASTOS REGISTRADOS PARA ESTE CLASIFICADOR");
      this.toAddView(0, 30);
      this.toAddView(1, 30);
      this.toAddView(2, 20);
      this.toAddView(3, 20);
    } // try
    catch (Exception e) {
      throw e;
    } // catch      
    finally {
      Methods.clean(params);
    } // finally
  } 
  
  @Override
  public void finalize() {
//    Methods.clean(this.resumen);
    super.finalize(); 
  }
  
  public static void main(String ... args) throws Exception {
    Consolidado gastos= new Consolidado(1L, 2025L, 4L);
    LOG.info(gastos.local());
  }
  
}
