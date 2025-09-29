package examenes.DocumentProcessor;

import java.util.*;

public class DocumentProcessorConPatrones {

  public static void main(String[] args) {
    // Crear documentos
    Documento texto = new DocumentoTexto("Reporte Mensual", "Este es el contenido del reporte...");
    Documento imagen = new DocumentoImagen("grafico.png", 1024);
    
    // Crear procesador de documentos usando Builder
    // TODO: Debug todo el proceso de builder p/ entender bien lo que hace
    ProcesadorDocumentos procesador = new ProcesadorDocumentosBuilder()
      // .conCompresion()
      // .conEncriptacion()
      .conMarcaDeAgua()
      .agregarListener(new LoggerListener())
      .agregarListener(new MetricsListener())
      .conFormateador("htML")
      .build();
    
    // Ejecutar procesamiento
    procesador.procesar(Arrays.asList(texto, imagen));
    
    // Usar API externa de almacenamiento
    CloudStorageAPI cloudAPI = new CloudStorageAPI();
    boolean uploaded = cloudAPI.uploadFile("documento_procesado.pdf", "contenido_binario");
    System.out.println("Subida a cloud: " + (uploaded ? "exitosa" : "fallida"));
  }

  // ===================== Dominio =====================

  // BASE P/ DECORATOR
  abstract static class Documento {
    protected final String nombre;
    
    public Documento(String nombre) {
      this.nombre = nombre;
    }
    
    public String getNombre() {
      return nombre;
    }
    
    public abstract String getTipo();
    public abstract int getTamaño();
    
    @Override
    public String toString() {
      return getTipo() + ": " + nombre + " (" + getTamaño() + " bytes)";
    }
  }

  static class DocumentoTexto extends Documento {
    private final String contenido;
    
    public DocumentoTexto(String nombre, String contenido) {
      super(nombre);
      this.contenido = contenido;
    }
    
    public String getContenido() {
      return contenido;
    }
    
    @Override
    public String getTipo() {
      return "TEXTO";
    }
    
    @Override
    public int getTamaño() {
      return contenido != null ? contenido.length() : 0;
    }
  }

  static class DocumentoImagen extends Documento {
    private final int tamaño;
    
    public DocumentoImagen(String nombre, int tamaño) {
      super(nombre);
      this.tamaño = tamaño;
    }
    
    @Override
    public String getTipo() {
      return "IMAGEN";
    }
    
    @Override
    public int getTamaño() {
      return tamaño;
    }
  }

  // DECORATORS

  static class DocumentDecorator extends Documento {
    protected Documento documento;
    public DocumentDecorator (Documento documento) {
        super(documento.getNombre());
        this.documento = documento;
    }

    @Override
    public String getTipo(){
        return this.documento.getTipo();
    }

    @Override
    public int getTamaño(){
        return this.documento.getTamaño();
    }
  }

  static class DocumentoCompresion extends DocumentDecorator {
    public DocumentoCompresion(Documento documento){
        super(documento);
    }
    @Override
    public int getTamaño(){
        return (int) (this.documento.getTamaño() * 0.8);
    }
  }

  static class DocumentoEncriptado extends DocumentDecorator {
    public DocumentoEncriptado(Documento documento){
        super(documento);
    }

    @Override
    public int getTamaño(){
        return this.documento.getTamaño() + 256;
    }
  }

  static class DocumentoMarcaDeAgua extends DocumentDecorator {
    public DocumentoMarcaDeAgua(Documento documento){
        super(documento);
    }

    @Override
    public int getTamaño(){
        return this.documento.getTamaño() + 100;
    }
  }

  // ===================== PROCESADOR =====================

  interface DocumentProcessListener {
    void onProcessCompleted(List<Documento> documentos, String formato);
  }

  static class LoggerListener implements DocumentProcessListener {
    @Override
    public void onProcessCompleted(List<Documento> documentos, String formato) {
      System.out.println("[Logger] Procesamiento completado. Documentos: " + documentos.size() + 
                        ", Formato: " + formato);
    }
  }

  static class MetricsListener implements DocumentProcessListener {
    @Override
    public void onProcessCompleted(List<Documento> documentos, String formato) {
      int totalSize = documentos.stream().mapToInt(Documento::getTamaño).sum();
      System.out.println("[Metrics] Tamaño total procesado: " + totalSize + " bytes");
    }
  }

  /*  Esta clase es el centro de todo.
      Recibe documentos y aplica transformaciones,
      formateo y notifica a listeners */
  static class ProcesadorDocumentos  {
    private final List<DocumentProcessListener> listeners = new ArrayList<>();
    private FormateadorDocumento formateador;
    
    // Transformaciones modeladas con flags
    private boolean compresion; // reduce tamaño en 20%
    private boolean encriptacion; // agrega overhead de seguridad
    private boolean marcaDeAgua; // agrega marca visual
    
    public void addListener(DocumentProcessListener listener) {
      listeners.add(listener);
    }
    
    public void setFormateador(FormateadorDocumento formateador) {
      this.formateador = formateador;
    }
    
    public void aplicarCompresion(boolean compresion) {
      this.compresion = compresion;
    }
    
    public void aplicarEncriptacion(boolean encriptacion) {
      this.encriptacion = encriptacion;
    }
    
    public void aplicarMarcaDeAgua(boolean marcaDeAgua) {
      this.marcaDeAgua = marcaDeAgua;
    }
    
    public void procesar(List<Documento> documentos) {
      if (formateador == null) {
        System.out.println("No hay formateador configurado.");
        return;
      }
      
      System.out.println("=== PROCESANDO DOCUMENTOS ===");
      
        
      List<Documento> procesados = new ArrayList<>();
      for (Documento doc : documentos) {
        System.out.println("Procesando: " + doc);
        
        // Aplicar transformaciones con Patrón Decorator
        Documento docFinal = aplicarTransformaciones(doc); // Esta función maneja los decoradores
        procesados.add(docFinal);
      }
      
      // String formato = formateador.formatear(documentos);
      String formato = formateador.formatear(procesados);

      System.out.println("Formato de salida: " + formato);
      
      // notifyProcessCompleted(documentos, formato);
      notifyProcessCompleted(procesados, formato);
    }

    private Documento aplicarTransformaciones(Documento doc){
        /*
         * Esta función determinará qué transformaciones se ejecutarán.
         */
        Documento docFinal = doc;
        if (compresion) {
            docFinal = new DocumentoCompresion(docFinal);
            System.out.println("  - Compresión aplicada");
          }
          if (encriptacion) {
              docFinal = new DocumentoEncriptado(docFinal);
              System.out.println("  - Encriptación aplicada");
          }
          if (marcaDeAgua) {
              docFinal = new DocumentoMarcaDeAgua(docFinal);
              System.out.println("  - Marca de agua aplicada");
          }
          
          System.out.println("  - Tamaño final: " + docFinal.getTamaño() + " bytes");
          return docFinal;
    }
    
    private void notifyProcessCompleted(List<Documento> documentos, String formato) {
      for (DocumentProcessListener listener : listeners) {
        listener.onProcessCompleted(documentos, formato);
      }
    }
  }

  static class ProcesadorDocumentosBuilder {
    boolean compresion = false;
    boolean encriptacion = false;
    boolean marcaDeAgua = false;
    String formato;
    private final List<DocumentProcessListener> listeners = new ArrayList<>();
    public ProcesadorDocumentosBuilder conCompresion(){
        this.compresion = true;
        return this;
    }

    public ProcesadorDocumentosBuilder conEncriptacion(){
        this.encriptacion = true;
        return this;
    }

    public ProcesadorDocumentosBuilder conMarcaDeAgua(){
        this.marcaDeAgua = true;
        return this;
    }

    public ProcesadorDocumentosBuilder conFormateador(String tipo){
        this.formato = tipo;
        return this;
    }

    public ProcesadorDocumentosBuilder agregarListener(DocumentProcessListener listener){
      listeners.add(listener);
      return this;
    }

    public ProcesadorDocumentos build(){
        ProcesadorDocumentos instancia = new ProcesadorDocumentos();
        instancia.aplicarCompresion(compresion);
        instancia.aplicarEncriptacion(encriptacion);
        instancia.aplicarMarcaDeAgua(marcaDeAgua);
        instancia.setFormateador(FormateadorFactory.crear(formato));
        for (DocumentProcessListener listener : listeners) {
          instancia.addListener(listener);
        }

        return instancia;
    }
  }

  // ===================== FORMATEADORES =====================
                        /* STRATEGY */
  interface FormateadorDocumento {
    String formatear(List<Documento> documentos);
  }

  static class FormateadorPDF implements FormateadorDocumento {
    @Override
    public String formatear(List<Documento> documentos) {
      System.out.println("[PDF] Generando documento PDF con " + documentos.size() + " elementos");
      return "PDF";
    }
  }

  static class FormateadorDOCX implements FormateadorDocumento {
    @Override
    public String formatear(List<Documento> documentos) {
      System.out.println("[DOCX] Generando documento Word con " + documentos.size() + " elementos");
      return "DOCX";
    }
  }

  static class FormateadorHTML implements FormateadorDocumento {
    @Override
    public String formatear(List<Documento> documentos) {
      System.out.println("[HTML] Generando página web con " + documentos.size() + " elementos");
      return "HTML";
    }
  }

                        /* FACTORY */
  static class FormateadorFactory {
    public static FormateadorDocumento crear(String outputFormat){
      // Usando switch expressions
      FormateadorDocumento formateador = switch (outputFormat.toLowerCase()){
        case "pdf" -> new FormateadorPDF();
        case "docx" -> new FormateadorDOCX();
        default -> new FormateadorHTML();
      };
      return formateador;

      /* Usando switch-case 
      switch (outputFormat.toLowerCase()) {
        case "pdf":
          return new FormateadorPDF();

        case "docx":
          return new FormateadorDOCX();
        
          case "html":
          return new FormateadorHTML();
        
          default:
          return new FormateadorHTML();

      }*/

      /* CÓDIGO ORIGINAL
      if ("pdf".equalsIgnoreCase(outputFormat)) {
        return new FormateadorPDF();
      } else if ("docx".equalsIgnoreCase(outputFormat)) {
        return new FormateadorDOCX();
      } else {
        return new FormateadorHTML();
      }*/

    }
  }

  // ===================== API externa =====================

  /** Esta API de almacenamiento en cloud es externa y no podemos modificarla. Falta integrarla */
  static class CloudStorageAPI {
    public boolean uploadFile(String fileName, String content) {
      System.out.println("[CloudStorageAPI] Subiendo archivo: " + fileName);
      // Lógica ficticia: falla si el nombre del archivo es muy largo
      return fileName.length() <= 30;
    }
  }
}