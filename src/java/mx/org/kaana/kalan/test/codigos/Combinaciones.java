package mx.org.kaana.kalan.test.codigos;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import mx.org.kaana.libs.formato.Numero;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @company INEGI
 * @project IKTAN (Sistema de seguimiento y control de proyectos)
 * @date 12/05/2023
 * @time 12:10:27 PM
 * @author Alejandro Jimenez Garcia <alejandro.jimenez@inegi.org.mx>
 */
public class Combinaciones implements Serializable {

  private static final long serialVersionUID = 2160778894882974671L;
  private static final Log LOG = LogFactory.getLog(Combinaciones.class);

  private Map<String, String> productos;
  private Map<String, String> clases;
  private Map<String, String> presentaciones;
  private Map<String, String> pesos;
  private Map<String, String> empaquetados;
  private Map<String, String> relacionesClase;
  private Map<String, String> relacionesPresentacion;
  private Map<String, String> relacionesPeso;

  public Combinaciones() {
    this.init();
  }
  
  private void init() {
    this.productos= new TreeMap<>();
    this.productos.put("ALP","ALPISTE");
    this.productos.put("ALU","ALUBIA");
    this.productos.put("ARR","ARROZ CAMPO NUEVO");
    this.productos.put("ARE","ARROZ EMBOL");
    this.productos.put("AZU","AZÚCAR");
    this.productos.put("BAY","BAYO");
    this.productos.put("BAE","BAYO EMBOL");
    this.productos.put("FRJ","FLOR DE JUNIO");
    this.productos.put("FRM","FLOR DE MAYO");
    this.productos.put("FJE","FLOR JUNIO EMBOL");
    this.productos.put("FME","FLOR MAYO EMBOL");
    this.productos.put("LNT","LENTEJA");
    this.productos.put("MOR","MEDIA OREJA");
    this.productos.put("MOE","MEDIA OREJA EMBOL");
    this.productos.put("NEG","NEGRO");
    this.productos.put("NEE","NEGRO EMBOL");
    this.productos.put("NOR","NEGRO ORGANICO");
    this.productos.put("NOE","NGRO ORGANICO EMBOL");
    this.productos.put("PER","PERUANO");
    this.productos.put("PEE","PERUANO EMBOL");
    this.productos.put("PIE","PINTO EMBOL");
    this.productos.put("PIN","PINTO S");

    this.clases= new TreeMap<>();
    this.clases.put("CL00", "CL00");
    this.clases.put("CL01", "CL01");
    this.clases.put("CL0E", "CL0E");
    this.clases.put("CL0G", "CL0G");
    this.clases.put("CL0H", "CL0H");
    this.clases.put("CL0L", "CL0L");
    this.clases.put("CL0M", "CL0M");
    this.clases.put("CL0N", "CL0N");
    this.clases.put("CL0R", "CL0R");
    this.clases.put("CL0P", "CL0P");
    this.clases.put("CL0S", "CL0S");
    
    this.presentaciones= new TreeMap<>();
    this.presentaciones.put("PB","PLASTICO BLANCO");
    this.presentaciones.put("PN","PLASTICO NEGRO");
    this.presentaciones.put("PT","PLASTICO TRANSPARENTE");
    
    this.pesos= new TreeMap<>();
    this.pesos.put("0010","1 KGS");
    this.pesos.put("0225","22.5 KGS");
    this.pesos.put("0250","25 KGS");
    this.pesos.put("0450","45 KGS");
    this.pesos.put("0460","46 KGS");
    this.pesos.put("0500","50 KGS");
    
    this.empaquetados= new TreeMap<>();
    this.empaquetados.put("0010","BOLSA");
    this.empaquetados.put("0225","COSTAL");
    this.empaquetados.put("0250","COSTAL");
    this.empaquetados.put("0450","COSTAL");
    this.empaquetados.put("0460","COSTAL");
    this.empaquetados.put("0500","COSTAL");
    
    this.relacionesClase= new TreeMap<>();
    this.relacionesClase.put("ALP","|CL00|");
    this.relacionesClase.put("ALU","|CL00|");
    this.relacionesClase.put("ARR","|CL00|");
    this.relacionesClase.put("ARE","|CL00|");
    this.relacionesClase.put("AZU","|CL00|");
    this.relacionesClase.put("BAY","|CL00|CL01|CL0E|CL0G|CL0H|CL0L|CL0M|CL0N|CL0R|CL0P|CL0S|");
    this.relacionesClase.put("BAE","|CL00|CL01|CL0E|CL0G|CL0H|CL0L|CL0M|CL0N|CL0R|CL0P|CL0S|");
    this.relacionesClase.put("FRJ","|CL00|CL01|CL0E|CL0G|CL0H|CL0L|CL0M|CL0N|CL0R|CL0P|CL0S|");
    this.relacionesClase.put("FRM","|CL00|CL01|CL0E|CL0G|CL0H|CL0L|CL0M|CL0N|CL0R|CL0P|CL0S|");
    this.relacionesClase.put("FJE","|CL00|CL01|CL0E|CL0G|CL0H|CL0L|CL0M|CL0N|CL0R|CL0P|CL0S|");
    this.relacionesClase.put("FME","|CL00|CL01|CL0E|CL0G|CL0H|CL0L|CL0M|CL0N|CL0R|CL0P|CL0S|");
    this.relacionesClase.put("LNT","|CL00|");
    this.relacionesClase.put("MOR","|CL00|CL01|CL0E|CL0G|CL0H|CL0L|CL0M|CL0N|CL0R|CL0P|CL0S|");
    this.relacionesClase.put("MOE","|CL00|CL01|CL0E|CL0G|CL0H|CL0L|CL0M|CL0N|CL0R|CL0P|CL0S|");
    this.relacionesClase.put("NEG","|CL00|CL01|CL0E|CL0G|CL0H|CL0L|CL0M|CL0N|CL0R|CL0P|CL0S|");
    this.relacionesClase.put("NEE","|CL00|CL01|CL0E|CL0G|CL0H|CL0L|CL0M|CL0N|CL0R|CL0P|CL0S|");
    this.relacionesClase.put("NOR","|CL00|CL01|CL0E|CL0G|CL0H|CL0L|CL0M|CL0N|CL0R|CL0P|CL0S|");
    this.relacionesClase.put("NOE","|CL00|CL01|CL0E|CL0G|CL0H|CL0L|CL0M|CL0N|CL0R|CL0P|CL0S|");
    this.relacionesClase.put("PER","|CL00|CL01|CL0E|CL0G|CL0H|CL0L|CL0M|CL0N|CL0R|CL0P|CL0S|");
    this.relacionesClase.put("PEE","|CL00|CL01|CL0E|CL0G|CL0H|CL0L|CL0M|CL0N|CL0R|CL0P|CL0S|");
    this.relacionesClase.put("PIE","|CL00|CL01|CL0E|CL0G|CL0H|CL0L|CL0M|CL0N|CL0R|CL0P|CL0S|");
    this.relacionesClase.put("PIN","|CL00|CL01|CL0E|CL0G|CL0H|CL0L|CL0M|CL0N|CL0R|CL0P|CL0S|");
    
    this.relacionesPresentacion= new TreeMap<>();
    this.relacionesPresentacion.put("ALP","|PN|");
    this.relacionesPresentacion.put("ALU","|PN|");
    this.relacionesPresentacion.put("ARR","|PN|");
    this.relacionesPresentacion.put("ARE","|PN|");
    this.relacionesPresentacion.put("AZU","|PN|");
    this.relacionesPresentacion.put("BAY","|PB|PN|PT|");
    this.relacionesPresentacion.put("BAE","|PB|PN|PT|");
    this.relacionesPresentacion.put("FRJ","|PB|PN|PT|");
    this.relacionesPresentacion.put("FRM","|PB|PN|PT|");
    this.relacionesPresentacion.put("FJE","|PB|PN|PT|");
    this.relacionesPresentacion.put("FME","|PB|PN|PT|");
    this.relacionesPresentacion.put("LNT","|PN|");
    this.relacionesPresentacion.put("MOR","|PB|PN|PT|");
    this.relacionesPresentacion.put("MOE","|PB|PN|PT|");
    this.relacionesPresentacion.put("NEG","|PB|PN|PT|");
    this.relacionesPresentacion.put("NEE","|PB|PN|PT|");
    this.relacionesPresentacion.put("NOR","|PB|PN|PT|");
    this.relacionesPresentacion.put("NOE","|PB|PN|PT|");
    this.relacionesPresentacion.put("PER","|PB|PN|PT|");
    this.relacionesPresentacion.put("PEE","|PB|PN|PT|");
    this.relacionesPresentacion.put("PIE","|PB|PN|PT|");
    this.relacionesPresentacion.put("PIN","|PB|PN|PT|");
    
    this.relacionesPeso= new TreeMap<>();
    this.relacionesPeso.put("ALP","|0010|0250|");
    this.relacionesPeso.put("ALU","|0010|0250|");
    this.relacionesPeso.put("ARR","|0010|0250|");
    this.relacionesPeso.put("ARE","|0010|0250|");
    this.relacionesPeso.put("AZU","|0010|0250|");
    this.relacionesPeso.put("BAY","|0010|0225|0250|0450|0460|0500|");
    this.relacionesPeso.put("BAE","|0010|0225|0250|0450|0460|0500|");
    this.relacionesPeso.put("FRJ","|0010|0225|0250|0450|0460|0500|");
    this.relacionesPeso.put("FRM","|0010|0225|0250|0450|0460|0500|");
    this.relacionesPeso.put("FJE","|0010|0225|0250|0450|0460|0500|");
    this.relacionesPeso.put("FME","|0010|0225|0250|0450|0460|0500|");
    this.relacionesPeso.put("LNT","|0010|0250|"); // CL00, 1, 25 
    this.relacionesPeso.put("MOR","|0010|0225|0250|0450|0460|0500|");
    this.relacionesPeso.put("MOE","|0010|0225|0250|0450|0460|0500|");
    this.relacionesPeso.put("NEG","|0010|0225|0250|0450|0460|0500|");
    this.relacionesPeso.put("NEE","|0010|0225|0250|0450|0460|0500|");
    this.relacionesPeso.put("NOR","|0010|0225|0250|0450|0460|0500|");
    this.relacionesPeso.put("NOE","|0010|0225|0250|0450|0460|0500|");
    this.relacionesPeso.put("PER","|0010|0225|0250|0450|0460|0500|");
    this.relacionesPeso.put("PEE","|0010|0225|0250|0450|0460|0500|");
    this.relacionesPeso.put("PIE","|0010|0225|0250|0450|0460|0500|");
    this.relacionesPeso.put("PIN","|0010|0225|0250|0450|0460|0500|");
  }
  
  public void process() {
    StringBuilder codigo= new StringBuilder();
    StringBuilder nombre= new StringBuilder();
    int count= 1;
    for (String producto: this.productos.keySet()) {
//      if(Objects.equals(producto, "MOR"))
        for (String clase: this.clases.keySet()) {
          for (String presentacion: this.presentaciones.keySet()) {
            for (String peso: this.pesos.keySet()) {
              String cualClase       = this.relacionesClase.get(producto);
              String cualPresentacion= this.relacionesPresentacion.get(producto);
              String cualPeso= this.relacionesPeso.get(producto);
              if(cualClase.contains("|"+ clase+ "|") && cualPresentacion.contains("|"+ presentacion+ "|") && cualPeso.contains("|"+ peso+ "|")) {
                codigo.append(producto).append(clase).append("").append(presentacion).append("").append(peso);
                nombre.append(this.empaquetados.get(peso)).append(" DE ").append(this.productos.get(producto)).append(" ").append(presentacion).append("").append(clase).append(", DE ").append(Numero.redondea(Double.valueOf(peso)/10, 2)).append(" KGS");
                // LOG.info((count++)+ "|"+ codigo.toString()+ "|"+ nombre.toString());
                System.out.println((count++)+ "|"+ codigo.toString()+ "|"+ nombre.toString()+ "|"+ clase+ "|"+ (Numero.redondearSat(Double.valueOf(peso)/10))+ "|"+ presentacion);
                codigo.delete(0, codigo.length());
                nombre.delete(0, nombre.length());
              } // if  
            } // for
          } // for
        } // for
    } // for
    LOG.info("Ok.");
  }
  
  public static void main(String[] args) {
    Combinaciones combinaciones= new Combinaciones();
    combinaciones.process();
  }

}
