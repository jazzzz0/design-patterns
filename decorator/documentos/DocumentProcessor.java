package decorator.documentos;

import java.util.*;

/**
 * Contestar a continuación las siguientes preguntas:
 * - ¿Qué patrón de diseño podés identificar en el código dado?
 *      Observer: para que el procesador de documentos pueda notificar a múltiples objetos de los cambios realizados.
 *      Strategy: para accionar de la manera correcta según el formato de salida indicado.
 * 
 * - ¿Qué patrón de diseño podrías agregar para mejorar el código?
 *      Adapter: para manejar de manera más general la conexión al Cloud Storage
 *               sin tener que depender del y cambiar el flujo principal, cada vez que se modifique algo en esa llamada.
 *      Builder: para encadenar las configuraciones del procesador de documentos y construirlo ya listo.
 *      Factory: para crear el formateador según el criterio de salida por un parámetro 
 * 
 * <p>Implementar UN patrón adicional para mejorar el código.
 *      Decorator: para las transformaciones que se harán a los documentos,
 *                 de manera que se puedan aplicar dinámicamente sin modificar la clase base. 
 *                 Así también podemos tener código menos acoplado.
 */

public class DocumentProcessor {

  public static void main(String[] args) {
    // Crear documentos
    Documento texto = new DocumentoTexto("Reporte Mensual", "Este es el contenido del reporte...");
    Documento imagen = new DocumentoImagen("grafico.png", 1024);
    
    // Crear procesador de documentos
    ProcesadorDocumentos procesador = new ProcesadorDocumentos();
    procesador.addListener(new LoggerListener());
    procesador.addListener(new MetricsListener());
    
    // Configurar transformaciones
    procesador.aplicarCompresion(true);
    procesador.aplicarEncriptacion(true);
    procesador.aplicarMarcaDeAgua(true);
    
    // Procesar documentos
    String outputFormat = "pdf"; // puede ser "pdf", "docx", "html"
    FormateadorDocumento formateador;
    
    if ("pdf".equalsIgnoreCase(outputFormat)) {
      formateador = new FormateadorPDF();
    } else if ("docx".equalsIgnoreCase(outputFormat)) {
      formateador = new FormateadorDOCX();
    } else {
      formateador = new FormateadorHTML();
    }
    
    procesador.setFormateador(formateador);
    
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