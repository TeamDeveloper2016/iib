package mx.org.kaana.mantic.consultas.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.consultas.reglas.UtilidadArticulosLazy;

@Named(value = "manticConsultasAcumulados")
@ViewScoped
public class Acumulados extends Articulos implements Serializable {

  private static final long serialVersionUID = 8793667741599428819L;

  @PostConstruct
  @Override
  protected void init() {
    super.init();    
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= super.toPrepare();
    try {
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("precios", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("ventas", EFormatoDinamicos.MILES_SIN_DECIMALES));
      params.put("sortOrder", "order by tc_mantic_articulos.nombre");
      this.lazyModel = new UtilidadArticulosLazy("VistaConsultasDto", "acumulados", params, columns);
      UIBackingUtilities.resetDataTable();
    } // try // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
    } // finally		
  } // doLoad

}