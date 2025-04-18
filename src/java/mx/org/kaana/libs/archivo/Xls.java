package mx.org.kaana.libs.archivo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import jxl.Workbook;
import jxl.write.Label;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.page.LinkPage;
import mx.org.kaana.kajool.db.comun.page.PageRecords;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Xls extends XlsBase {

	private static final long serialVersionUID = 7676537739920483322L;
	private static final Log LOG= LogFactory.getLog(Xls.class);
  
	protected List<IBaseDto> registros;
	private Modelo definicion;  
  private int totalColumnas;
  private boolean algunos;
  protected String campos;
	private String nombreArchivo;
  private Monitoreo monitoreo;
  private List<Columna> columns;
  
  public Xls(String nombreArchivo, Modelo definicion, String campos) {
    this(nombreArchivo, definicion, campos, new ArrayList<>());
  } 
  
  public Xls(String nombreArchivo, Modelo definicion, String campos, List<Columna> columns) {    
		this.nombreArchivo= nombreArchivo;
		this.definicion   = definicion;
		this.campos       = campos;  
    this.columns      = columns;
		this.init();
  }

  public Xls(String nombreArchivo, String campos) {    
		this.nombreArchivo= nombreArchivo;
		this.campos       = campos;  
    this.columns      = new ArrayList<>();
		this.init();
  }
  
  public Xls(String archivo, Modelo definicion) {
    this(archivo, definicion, null, new ArrayList<>());    
  }
  
	private void init(){
		try {      
      this.setPosicionColumna(0);
      this.setPosicionFila(0);
      this.setIndiceColumna(0);			                 
      algunos= campos!= null;
      this.monitoreo= JsfBase.getAutentifica().getMonitoreo();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
    } // catch
	}
	
  public void setAlgunos(boolean algunos){
    this.algunos= algunos;
  }
  
  public boolean isAlgunos() {
    return algunos;
  }

	public Modelo getDefinicion() {
		return definicion;
	}

	public void setCampos(String campos){
    this.campos= campos;
  }
  
  public String getCampos() {
		return campos;
	}
	
	protected void setTotalColumnas() {
    try {      
      if (!isAlgunos()) {        
        this.totalColumnas= this.registros.get(0).toMap().keySet().size();        
      } // if
      else 
        this.totalColumnas= this.campos.split(",").length;     
    } // try
    catch (Exception e) {
      Error.mensaje(e);
    }// catch
  }

  public int getTotalColumnas() {
    return totalColumnas;
  }

	public String getNombreArchivo() {
		return nombreArchivo;
	}
  	
  protected Map convertirHashMap(IBaseDto registro) throws Exception {
    Map columna             = new HashMap();    
    Map registroMap         = null;
    String [] nombreColumnas= this.campos.split(",");
    try  {            
      registroMap= registro.toMap();            
      for (int i= 0; i< getColumnasInformacion(); i++) {        
        columna.put("col"+ String.valueOf(i), registroMap.get(Cadena.toBeanName(nombreColumnas[i])));
      } // for  
    } // try 
    catch (Exception e)  {
      Error.mensaje(e);
    } // catch
    return columna;    
  }
  	
	@Override
  protected String getNombresColumnas() {
    StringBuffer columnas	= new StringBuffer();    
    String regresar				= null;    
		Set<String> fields		= null;		
    try {               
			fields= this.registros.get(0).toMap().keySet();
			for(String field: fields) {
				columnas.append(field);
				columnas.append(",");
			} // for x    
			columnas= columnas.delete(columnas.length() -1, columnas.length());
			regresar= columnas.toString();      
    }
    catch (Exception e) {
      Error.mensaje(e);
    } // try
    return regresar.toUpperCase();
  }

	@Override
  protected int getColumnasInformacion() {
    return getTotalColumnas();
  }

	private void detail(List<IBaseDto> registros) throws Exception {
		Map columna = null;
    Object value= null;
		int fila    = 1;
		try {						
      String[] names= this.campos.split(",");
			for (IBaseDto registro: registros) {
        columna= this.convertirHashMap(registro);
        for (int x= 0; x < columna.size(); x++) {
          if(Objects.equals(columna.get("col"+ String.valueOf(x)), null))
            value= "";
          else {
            int index= this.columns.indexOf(new Columna(Cadena.toBeanName(names[x])));
            if(index>= 0) {
              Columna column= this.columns.get(index);
              value= Global.format(column.getFormat(), columna.get("col"+ String.valueOf(x)));
            } // if
            else
              value= columna.get("col"+ String.valueOf(x)).toString();
          } // if  
          LOG.info("Fila: "+ fila+ " celda: "+ value);
          Label label= new Label(getPosicionColumna()+ x, getPosicionFila()+ fila, value.toString());         
          hoja.addCell(label);
        } // for x
        columna.clear();
        columna= null;
        fila++;
        this.monitoreo.incrementar();
        LOG.info("Registro: "+ fila+ " procesado. !");       
      } // for
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			throw e;
		} // catch
	}
	
	@Override
  public boolean generarRegistros(boolean titulo) throws Exception {
    boolean termino= Boolean.FALSE; 			
		int top				= new Long(Constantes.SQL_TODOS_REGISTROS).intValue();
    try {			
      this.libro= Workbook.createWorkbook(new File(this.nombreArchivo));
      this.hoja = this.libro.createSheet("IMOX", 0);
      if (!isAlgunos())
        this.campos= getNombresColumnas();		
      if (titulo)
        this.procesarEncabezado(this.campos);   
      this.setTotalColumnas();
			PageRecords pages= DaoFactory.getInstance().toEntityPage(getDefinicion().getProceso(), getDefinicion().getIdXml(), getDefinicion().getParams(), 0, top);
			if(!Objects.equals(pages, null) && !Objects.equals(pages.getList(), null) && pages.getList().size()> 0) {				
        this.monitoreo.comenzar(DaoFactory.getInstance().toSize(getDefinicion().getProceso(), getDefinicion().getIdXml(), getDefinicion().getParams()));
				this.registros= pages.getList();
				pages.calculate(true);
				List<LinkPage> list= pages.getPages();								
				this.detail((List)pages.getList());
				for(LinkPage page: list) {
					PageRecords values= DaoFactory.getInstance().toEntityPage(getDefinicion().getProceso(), getDefinicion().getIdXml(), getDefinicion().getParams(), (int)(page.getIndex()* top), top);
					this.detail((List)values.getList());
				} // for				
        termino= Boolean.TRUE;
			} // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
    } // catch
		finally {
  	  this.libro.write();
      this.libro.close();
    } // finally
    return termino;
  }

  public boolean procesar(boolean titulo, List<IBaseDto> registros) throws Exception {        
    boolean regresar= false;
    this.registros  = registros;
    try {			
			if (this.registros!= null && !this.registros.isEmpty()) {				
        this.monitoreo.comenzar(new Long(this.registros.size()));
				libro= Workbook.createWorkbook(new File(this.nombreArchivo));
				hoja = libro.createSheet("IMOX", 0);
				if (!isAlgunos())
					this.campos= this.getNombresColumnas();		
				if (titulo)
					this.procesarEncabezado(this.campos);   
				this.setTotalColumnas();
				this.detail(this.registros);
        regresar= true;
			} // if
    } // try
    catch (Exception e) {
			regresar= false;
      Error.mensaje(e);
    } // catch
		finally {
  	  getLibro().write();
      getLibro().close();
    } // finally
    return regresar;
  } 
  
  public boolean procesar() throws Exception {        
    return this.generarRegistros(true);
  } 

  public boolean procesar(int posColumna, int posFila, boolean colocarTitulo) throws Exception {    
    setPosicionFila(posColumna);
    setPosicionColumna(posFila);    
    return generarRegistros(colocarTitulo);
  } 
  
  public static void main(String[] args) {
    Xls xls				= null;    
    Map parametros= null;
    Modelo modelo	= null;
    try {      
      parametros= new HashMap();
      parametros.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);      
			modelo= new Modelo(parametros, "TcManticArticulosDto", "row");
      xls= new Xls("E:\\pruebaExcel.xls", modelo);      
      xls.procesar();      
    }
    catch(Exception e) {
      Error.mensaje(e);
    } // try
  } // main

}