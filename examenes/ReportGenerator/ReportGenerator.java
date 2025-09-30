package examenes.ReportGenerator;

import java.util.*;

/**
 * Contestar a continuación las siguientes preguntas:
 * - ¿Qué patrón de diseño podés identificar en el código dado?
 * - ¿Qué patrón de diseño podrías agregar para mejorar el código?
 *
 * <p>Implementar UN patrón adicional para mejorar el código.
 */
public class ReportGenerator {

  public static void main(String[] args) {
    // Crear datos para el reporte
    List<String> datos = Arrays.asList(
        "Ventas Q1: $50000",
        "Ventas Q2: $65000",
        "Ventas Q3: $72000",
        "Ventas Q4: $80000"
    );
    
    // Crear reporte básico
    Reporte reporte = new ReporteBuilder().
      conTituloYDatos("Reporte Anual 2024", datos)
      .addEncabezado()
      .addPieDePagina()
      .addIndice()
      .addGraficos()
      .addResumenEjecutivo()
      .build();

    // Configurar generador

    GeneradorReportes generador = new GeneradorReportes();
    generador.setReporte(reporte);
    
    // Seleccionar formato de exportación
    String formato = "pdf"; // puede ser "pdf", "excel", "html"
    ExportadorReporte exportador;
    
    if ("pdf".equalsIgnoreCase(formato)) {
      exportador = new ExportadorPDF();
    } else if ("excel".equalsIgnoreCase(formato)) {
      exportador = new ExportadorExcel();
    } else {
      exportador = new ExportadorHTML();
    }
    
    generador.setExportador(exportador);
    
    // Generar reporte
    generador.generar();
    
    // Usar servicio de distribución externo
    DistributionAPI distributionAPI = new DistributionAPI();
    boolean sent = distributionAPI.distribute("reporte_anual_2024.pdf", "stakeholders@company.com");
    System.out.println("Reporte distribuido: " + (sent ? "exitoso" : "fallido"));
  }

  // ===================== Dominio =====================

  public interface IReporte {
    int calcularPaginas();
  }
  static class Reporte implements IReporte {
    private final String titulo;
    private final List<String> datos;
    
    // Secciones opcionales con flags
    private boolean conEncabezado;
    private boolean conPieDePagina;
    private boolean conIndice;
    private boolean conGraficos;
    private boolean conResumenEjecutivo;
    
    public Reporte(String titulo, List<String> datos) {
      this.titulo = titulo;
      this.datos = new ArrayList<>(datos);
    }
    
    public String getTitulo() {
      return titulo;
    }
    
    public List<String> getDatos() {
      return Collections.unmodifiableList(datos);
    }
    
    public void agregarEncabezado(boolean valor) {
      this.conEncabezado = valor;
    }
    
    public void agregarPieDePagina(boolean valor) {
      this.conPieDePagina = valor;
    }
    
    public void agregarIndice(boolean valor) {
      this.conIndice = valor;
    }
    
    public void agregarGraficos(boolean valor) {
      this.conGraficos = valor;
    }
    
    public void agregarResumenEjecutivo(boolean valor) {
      this.conResumenEjecutivo = valor;
    }
    
    public boolean tieneEncabezado() {
      return conEncabezado;
    }
    
    public boolean tienePieDePagina() {
      return conPieDePagina;
    }
    
    public boolean tieneIndice() {
      return conIndice;
    }
    
    public boolean tieneGraficos() {
      return conGraficos;
    }
    
    public boolean tieneResumenEjecutivo() {
      return conResumenEjecutivo;
    }
    
    @Override
    public int calcularPaginas() {
      int paginas = (datos.size() / 10) + 1; // 10 items por página base
      return paginas;
    }
    
    public int calcularTotalPaginas() {
      IReporte reporteFinal = this;
      if (conEncabezado) {
        reporteFinal = new ReporteConEncabezado(reporteFinal);
      }
      
      if (conPieDePagina){
        reporteFinal = new ReporteConPieDePagina(reporteFinal);
      }
      
      if (conIndice) {
        reporteFinal = new ReporteConIndice(reporteFinal);
      }
      
      if(conGraficos){
        reporteFinal = new ReporteConGraficos(reporteFinal);
      }
      
      if (conResumenEjecutivo){
        reporteFinal = new ReporteConResumenEjecutivo(reporteFinal);
      }
      
      return reporteFinal.calcularPaginas();
      
    }
  }

  // Decorator

  static class ReporteDecorator implements IReporte {
    protected IReporte reporte;

    public ReporteDecorator(IReporte reporte){
      this.reporte = reporte;
    }

    @Override
    public int calcularPaginas(){
      return this.reporte.calcularPaginas();
    }

  }

  static class ReporteConEncabezado extends ReporteDecorator {
    public ReporteConEncabezado(IReporte reporte){
      super(reporte);
    }

    @Override
    public int calcularPaginas(){
      return this.reporte.calcularPaginas() + 1;
    }

  }

  static class ReporteConPieDePagina extends ReporteDecorator {
    public ReporteConPieDePagina(IReporte reporte){
      super(reporte);
    }

    @Override
    public int calcularPaginas(){
      return this.reporte.calcularPaginas();
    }
  }

  static class ReporteConIndice extends ReporteDecorator {
    public ReporteConIndice(IReporte reporte){
      super(reporte);
    }

    @Override
    public int calcularPaginas(){
      return this.reporte.calcularPaginas() + 2;
    }

  }

  static class ReporteConGraficos extends ReporteDecorator {
    public ReporteConGraficos(IReporte reporte){
      super(reporte);
    }

    @Override
    public int calcularPaginas(){
      return this.reporte.calcularPaginas() + 3;
    }

  }

  static class ReporteConResumenEjecutivo extends ReporteDecorator {
    public ReporteConResumenEjecutivo(IReporte reporte){
      super(reporte);
    }

    @Override
    public int calcularPaginas(){
      return this.reporte.calcularPaginas() + 1;
    }

  }

  static class ReporteBuilder {
    private String titulo;
    private List<String> datos;
    private boolean conEncabezado = false;
    private boolean conPieDePagina = false;
    private boolean conIndice = false;
    private boolean conGraficos = false;
    private boolean conResumenEjecutivo = false;
    
    public ReporteBuilder conTituloYDatos(String titulo, List<String> datos){
      this.titulo = titulo;
      this.datos = datos;
      return this;
    }

    public ReporteBuilder addEncabezado(){
      this.conEncabezado = true;
      return this;
    }

    public ReporteBuilder addPieDePagina(){
      this.conPieDePagina = true;
      return this;
    }

    public ReporteBuilder addIndice(){
      this.conIndice = true;
      return this;
    }

    public ReporteBuilder addGraficos(){
      this.conGraficos = true;
      return this;
    }

    public ReporteBuilder addResumenEjecutivo(){
      this.conResumenEjecutivo = true;
      return this;
    }

    public Reporte build() {
      Reporte reporte = new Reporte(titulo, datos);
      reporte.agregarEncabezado(conEncabezado);
      reporte.agregarPieDePagina(conPieDePagina);
      reporte.agregarIndice(conIndice);
      reporte.agregarGraficos(conGraficos);
      reporte.agregarResumenEjecutivo(conResumenEjecutivo);

      return reporte;
    }


  }

  // ===================== GENERADOR =====================

  interface ExportadorReporte {
    void exportar(Reporte reporte);
  }

  static class ExportadorPDF implements ExportadorReporte {
    @Override
    public void exportar(Reporte reporte) {
      System.out.println("[PDF] Exportando reporte: " + reporte.getTitulo());
      System.out.println("[PDF] Generando " + reporte.calcularPaginas() + " páginas");
    }
  }

  static class ExportadorExcel implements ExportadorReporte {
    @Override
    public void exportar(Reporte reporte) {
      System.out.println("[EXCEL] Exportando reporte: " + reporte.getTitulo());
      System.out.println("[EXCEL] Creando " + reporte.getDatos().size() + " filas");
    }
  }

  static class ExportadorHTML implements ExportadorReporte {
    @Override
    public void exportar(Reporte reporte) {
      System.out.println("[HTML] Exportando reporte: " + reporte.getTitulo());
      System.out.println("[HTML] Generando página web interactiva");
    }
  }

  static class GeneradorReportes {
    private Reporte reporte;
    private ExportadorReporte exportador;
    
    public void setReporte(Reporte reporte) {
      this.reporte = reporte;
    }
    
    public void setExportador(ExportadorReporte exportador) {
      this.exportador = exportador;
    }
    
    public void generar() {
      if (reporte == null) {
        System.out.println("No hay reporte configurado.");
        return;
      }
      
      if (exportador == null) {
        System.out.println("No hay exportador configurado.");
        return;
      }
      
      System.out.println("=== GENERANDO REPORTE ===");
      System.out.println("Título: " + reporte.getTitulo());
      System.out.println("Datos: " + reporte.getDatos().size() + " elementos");
      
      // Mostrar secciones incluidas
      System.out.println("\nSecciones incluidas:");
      if (reporte.tieneEncabezado()) {
        System.out.println("  ✓ Encabezado");
      }
      if (reporte.tienePieDePagina()) {
        System.out.println("  ✓ Pie de página");
      }
      if (reporte.tieneIndice()) {
        System.out.println("  ✓ Índice");
      }
      if (reporte.tieneGraficos()) {
        System.out.println("  ✓ Gráficos");
      }
      if (reporte.tieneResumenEjecutivo()) {
        System.out.println("  ✓ Resumen ejecutivo");
      }
      
      System.out.println("\nPáginas totales: " + reporte.calcularTotalPaginas());
      System.out.println();
      
      // Exportar usando la estrategia seleccionada
      exportador.exportar(reporte);
      
      System.out.println("\n✓ Reporte generado exitosamente");
    }
  }


  // ===================== Construcción compleja de reportes =====================

  static class ReporteFinanciero {
    private String titulo;
    private String periodo;
    private List<String> ingresos;
    private List<String> gastos;
    private String conclusion;
    private boolean incluirBalance;
    private boolean incluirProyecciones;
    private boolean incluirComparativas;
    
    public ReporteFinanciero(String titulo, String periodo, List<String> ingresos, 
                            List<String> gastos, String conclusion, boolean incluirBalance,
                            boolean incluirProyecciones, boolean incluirComparativas) {
      this.titulo = titulo;
      this.periodo = periodo;
      this.ingresos = ingresos;
      this.gastos = gastos;
      this.conclusion = conclusion;
      this.incluirBalance = incluirBalance;
      this.incluirProyecciones = incluirProyecciones;
      this.incluirComparativas = incluirComparativas;
    }
    
    @Override
    public String toString() {
      return "ReporteFinanciero{" +
             "titulo='" + titulo + '\'' +
             ", periodo='" + periodo + '\'' +
             ", ingresos=" + ingresos.size() +
             ", gastos=" + gastos.size() +
             ", balance=" + incluirBalance +
             ", proyecciones=" + incluirProyecciones +
             ", comparativas=" + incluirComparativas +
             '}';
    }
  }

  // ===================== API externa =====================

  /** Esta API de distribución es externa y no podemos modificarla. Falta integrarla */
  static class DistributionAPI {
    public boolean distribute(String fileName, String recipients) {
      System.out.println("[DistributionAPI] Distribuyendo: " + fileName);
      System.out.println("[DistributionAPI] Destinatarios: " + recipients);
      
      // Lógica ficticia: falla si hay más de 3 destinatarios
      int recipientCount = recipients.split(",").length;
      return recipientCount <= 3;
    }
  }
}