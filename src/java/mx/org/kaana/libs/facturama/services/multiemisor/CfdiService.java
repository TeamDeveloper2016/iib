package mx.org.kaana.libs.facturama.services.multiemisor;

import mx.org.kaana.libs.facturama.models.response.CfdiSearchResult;
import mx.org.kaana.libs.facturama.services.HttpService;
import mx.org.kaana.libs.facturama.models.exception.FacturamaException;
import com.squareup.okhttp.OkHttpClient;
import java.io.IOException;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.net.URLEncoder;
import mx.org.kaana.libs.facturama.models.response.InovoiceFile;
import mx.org.kaana.libs.facturama.models.response.CfdiSendEmail;
import mx.org.kaana.libs.facturama.models.response.CancelationStatus;
import com.google.gson.Gson;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.FileOutputStream;
import java.util.Objects;
import org.primefaces.util.Base64;

public class CfdiService extends HttpService { //<mx.org.kaana.libs.facturama.models.request.Cfdi,mx.org.kaana.libs.facturama.models.response.Cfdi>{

  public enum FileFormat {
    Xml, Pdf, Html
  }

  public enum InvoiceType {
    Issued, Received, Payroll, IssuedLite
  }

  public enum CfdiStatus {
    All, Active, Cancel
  }

  public CfdiService(OkHttpClient client) {
    super(client, "");
    singleType = mx.org.kaana.libs.facturama.models.response.Cfdi.class;
    multiType = new TypeToken<List<mx.org.kaana.libs.facturama.models.response.Cfdi>>() {
    }.getType();
    cancelationStatus = CancelationStatus.class;
  }

  public mx.org.kaana.libs.facturama.models.response.Cfdi Create(mx.org.kaana.libs.facturama.models.request.CfdiLite model) throws IOException, FacturamaException, Exception {
    return (mx.org.kaana.libs.facturama.models.response.Cfdi) Post(model, "api-lite/2/cfdis");
  }

  public mx.org.kaana.libs.facturama.models.response.Cfdi Create3(mx.org.kaana.libs.facturama.models.request.CfdiLite model) throws IOException, FacturamaException, Exception {
    return (mx.org.kaana.libs.facturama.models.response.Cfdi) Post(model, "api-lite/3/cfdis");
  }

  public mx.org.kaana.libs.facturama.models.response.CancelationStatus Remove(String id, String motive, String uuidReplacement) throws IOException, FacturamaException, Exception {
    uuidReplacement = uuidReplacement == null ? null : uuidReplacement.isEmpty() ? null : uuidReplacement;
    motive = motive.isEmpty() ? "02" : motive;
    if (id != null && !id.isEmpty()) {
      if (uuidReplacement == null || Objects.equals(uuidReplacement, "null")) {
        return (mx.org.kaana.libs.facturama.models.response.CancelationStatus) Delete("api-lite/cfdis/" + id + "?motive=" + motive);
      } 
      else {
        return (mx.org.kaana.libs.facturama.models.response.CancelationStatus) Delete("api-lite/cfdis/" + id + "?motive=" + motive + "&uuidReplacement=" + uuidReplacement);
      }
    } 
    else {
      throw new NullPointerException(singleType.getTypeName());
    }
  }

  public mx.org.kaana.libs.facturama.models.response.CancelationStatus RemoveRet(String id, String motive)
          throws IOException, FacturamaException, Exception {
    if (id != null && !id.isEmpty()) {
      return (mx.org.kaana.libs.facturama.models.response.CancelationStatus) Delete("retenciones/" + id + "?motive=" + motive);
    } 
    else {
      throw new NullPointerException(singleType.getTypeName());
    }
  }

  public mx.org.kaana.libs.facturama.models.response.Cfdi Retrive(String id) throws IOException, FacturamaException, Exception {
    return (mx.org.kaana.libs.facturama.models.response.Cfdi) Get("cfdi/" + id + "?type=IssuedLite");
  }

  public mx.org.kaana.libs.facturama.models.response.Cfdi Retrive(String id, InvoiceType type) throws IOException, FacturamaException, Exception {
    return (mx.org.kaana.libs.facturama.models.response.Cfdi) Get("cfdi/" + id + "?type=" + type.toString());
  }

  public List<CfdiSearchResult> List(String keyword) throws IOException, FacturamaException, Exception {
    return this.List(keyword, CfdiStatus.Active);
  }

  public List<CfdiSearchResult> List(String keyword, CfdiStatus status) throws IOException, FacturamaException, Exception {
    return this.List(keyword, CfdiStatus.Active, InvoiceType.Issued);
  }

  public List<CfdiSearchResult> List(String keyword, CfdiStatus status, InvoiceType type) throws IOException, FacturamaException, Exception {
    keyword = URLEncoder.encode(keyword);

    String resource = "cfdi?type=" + type + "Lite&keyword=" + keyword + "&status=" + status;

    return GetList(resource, new TypeToken<List<mx.org.kaana.libs.facturama.models.response.CfdiSearchResult>>() {
    }.getType());
  }

  public List<CfdiSearchResult> List() throws IOException, FacturamaException, Exception {
    return this.List(-1, -1, null, null, "", "", "", "", CfdiStatus.Active, InvoiceType.Issued);
  }

  public List<CfdiSearchResult> ListFilterByRfc(String rfc) throws IOException, FacturamaException, Exception {
    return this.List(-1, -1, rfc, null, null, null, null, null, CfdiStatus.Active, InvoiceType.Issued);
  }

  public List<CfdiSearchResult> List(int folioStart, int folioEnd, String rfc, String taxEntityName, String dateStart, String dateEnd, String idBranch, String serie,
          CfdiStatus status, InvoiceType type) throws IOException, FacturamaException, Exception {
    String resource = "cfdi?type=" + type + "Lite&status=" + status;

    if (folioStart > -1) {
      resource += "&folioStart=" + folioStart;
    }

    if (folioEnd > -1) {
      resource += "&folioEnd=" + folioEnd;
    }

    if (rfc != null) {
      resource += "&rfc=" + rfc;
    }

    if (taxEntityName != null) {
      resource += "&taxEntityName=" + taxEntityName;
    }

    if (dateStart != null) {
      resource += "&dateStart=" + dateStart;
    }

    if (dateEnd != null) {
      resource += "&dateEnd=" + dateEnd;
    }

    if (idBranch != null) {
      resource += "&idBranch=" + idBranch;
    }

    if (serie != null) {
      resource += "&serie=" + serie;
    }

    return GetList(resource, new TypeToken<List<CfdiSearchResult>>() {
    }.getType());
  }

  /**
   * -
   * Obtiene un archivo referente a un CFDI del tipo "Issued"
   *
   * @param id Identificador del CFDI
   * @param format Formato deseado ( pdf | html | xml )
   * @return Archivo en cuestion
   */
  public InovoiceFile GetFile(String id, FileFormat format) throws Exception {
    return GetFile(id, format, InvoiceType.Issued);
  }

  /**
   * Obtiene un archivo referente a un CFDI
   *
   * @param id Identificador del CFDI
   * @param format Formato deseado ( pdf | html | xml )
   * @param type Tipo de comprobante ( payroll | received | issued )
   * @return Archivo en cuestion
   */
  public InovoiceFile GetFile(String id, FileFormat format, InvoiceType type) throws FacturamaException, Exception {
    String sFormat   = format.name().toLowerCase();
    String resource  = "cfdi/" + sFormat + "/" + type + "Lite/" + "/" + id;
    InovoiceFile file= (InovoiceFile) Get(resource, InovoiceFile.class);
    return file;
  }

  /**
   * Guardada el PDF de un CFDI del tipo "Issued" en la ruta especificada
   *
   * ADVERTENCIA: Facturama NO ofrece la descarga de PDF en API Multiemisor como una de las características El PDF obtenido mediante esta función es básico, y en el caso de requerir algo más
   * específico, se recomienda a cada uno de nuestros clientes, el desarrollarlo por su cuenta
   *
   * @param filePath Ruta donde se va a guardar el PDF
   * @param id Idenficador del CFDI
   */
  public void SavePdf(String filePath, String id) throws Exception {
    SavePdf(filePath, id, InvoiceType.Issued);
  }

  /**
   * Guardada el PDF de un CFDI en la ruta especificada
   *
   * ADVERTENCIA: Facturama NO ofrece la descarga de PDF en API Multiemisor como una de las características El PDF obtenido mediante esta función es básico, y en el caso de requerir algo más
   * específico, se recomienda a cada uno de nuestros clientes, el desarrollarlo por su cuenta
   *
   * @param filePath Ruta donde se va a guardar el PDF
   * @param id Idenficador del CFDI
   * @param type Tipo del comprobante (payroll | received | issued)
   */
  public void SavePdf(String filePath, String id, InvoiceType type) throws Exception {
    InovoiceFile file = GetFile(id, FileFormat.Pdf, type);

    FileOutputStream fos = new FileOutputStream(filePath);
    fos.write(Base64.decode(file.getContent()));
    fos.close();
  }

  public void SaveXml(String filePath, String id) throws Exception {
    SaveXml(filePath, id, InvoiceType.Issued);
  }

  /**
   * Guardada el XML de un CFDI en la ruta especificada
   *
   * @param filePath Ruta donde se va a guardar el PDF
   * @param id Idenficador del CFDI
   * @param type Tipo del comprobante (payroll | received | issued)
   */
  public void SaveXml(String filePath, String id, InvoiceType type) throws Exception {
    InovoiceFile file = GetFile(id, FileFormat.Xml, type);

    FileOutputStream fos = new FileOutputStream(filePath);
    fos.write(Base64.decode(file.getContent()));
    fos.close();
  }

  public boolean SendEmail(String email, InvoiceType type, String cfdiId) throws FacturamaException, Exception {
    HttpUrl.Builder urlBuilder
            = HttpUrl.parse(baseUrl + "/Cfdi?cfdiType=" + type + "&cfdiId=" + cfdiId + "&email=" + email).newBuilder();
    String jsonObj = new Gson().toString();
    String url = urlBuilder.build().toString();
    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObj);

    Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();

    Response response = Execute(request);
    String jsonData = response.body().string();

    CfdiSendEmail object = new Gson().fromJson(jsonData, CfdiSendEmail.class);
    return object.getsuccess();
  }

}
