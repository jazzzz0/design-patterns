package examenes.TaskManager;
import java.util.*;

public class TaskManager {

  public static void main(String[] args) {
    // Crear tareas
    Tarea tarea1 = new TareaSimple("Revisar emails", "Revisar y responder emails pendientes", Prioridad.MEDIA);
    Tarea tarea2 = new TareaCompleja("Preparar presentación", "Crear slides para reunión", Prioridad.ALTA, 120);
    
    // Crear gestor de tareas
    GestorTareas gestor = new GestorTareas();
    gestor.agregarTarea(tarea1);
    gestor.agregarTarea(tarea2);
    
    LoggerListener loggerListener = new LoggerListener();
    BackupListener backupListener = new BackupListener();
    NotificationListener notificationListener = new NotificationListener();

    // Configurar procesamiento
    gestor.addListener(loggerListener);
    gestor.addListener(backupListener);
    gestor.addListener(notificationListener);
    
    // Seleccionar estrategia de ejecución
    String tipoEjecucion = "paralelo"; // puede ser "secuencial", "paralelo", "por-prioridad"
    EstrategiaEjecucion estrategia;
    
    if ("secuencial".equalsIgnoreCase(tipoEjecucion)) {
      estrategia = new EjecucionSecuencial();
    } else if ("paralelo".equalsIgnoreCase(tipoEjecucion)) {
      estrategia = new EjecucionParalela();
    } else {
      estrategia = new EjecucionPorPrioridad();
    }
    
    gestor.setEstrategiaEjecucion(estrategia);
    
    // Ejecutar tareas
    gestor.mostrarResumen();
    gestor.ejecutarTareas();
    
    // Usar servicio externo de reportes
    ReportingServiceAPI reportingAPI = new ReportingServiceAPI();
    boolean sent = reportingAPI.sendDailyReport("resumen_tareas", "datos_del_reporte");
    System.out.println("Reporte enviado: " + (sent ? "exitoso" : "fallido"));
  }

  // ===================== Dominio =====================

  enum Prioridad {
    BAJA(1), MEDIA(2), ALTA(3), CRITICA(4);
    
    private final int valor;
    
    Prioridad(int valor) {
      this.valor = valor;
    }
    
    public int getValor() {
      return valor;
    }
  }

  abstract static class Tarea {
    protected final String nombre;
    protected final String descripcion;
    protected final Prioridad prioridad;
    protected boolean completada = false;
    
    public Tarea(String nombre, String descripcion, Prioridad prioridad) {
      this.nombre = nombre;
      this.descripcion = descripcion;
      this.prioridad = prioridad;
    }
    
    public String getNombre() {
      return nombre;
    }
    
    public String getDescripcion() {
      return descripcion;
    }
    
    public Prioridad getPrioridad() {
      return prioridad;
    }
    
    public boolean isCompletada() {
      return completada;
    }
    
    public abstract int getTiempoEstimado();
    
    public void ejecutar() {
      System.out.println("Ejecutando tarea: " + nombre);
      // Simular trabajo
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      completada = true;
      System.out.println("Tarea completada: " + nombre);
    }
    
    @Override
    public String toString() {
      return nombre + " [" + prioridad + "] - " + (completada ? "COMPLETADA" : "PENDIENTE");
    }
  }

  static class TareaSimple extends Tarea {
    public TareaSimple(String nombre, String descripcion, Prioridad prioridad) {
      super(nombre, descripcion, prioridad);
    }
    
    @Override
    public int getTiempoEstimado() {
      return 30; // 30 minutos por defecto
    }
  }

  static class TareaCompleja extends Tarea {
    private final int tiempoEstimado;
    
    public TareaCompleja(String nombre, String descripcion, Prioridad prioridad, int tiempoEstimado) {
      super(nombre, descripcion, prioridad);
      this.tiempoEstimado = tiempoEstimado;
    }
    
    @Override
    public int getTiempoEstimado() {
      return tiempoEstimado;
    }
  }

  // ===================== GESTOR DE TAREAS =====================

  static class GestorTareas {
    private final List<Tarea> tareas = new ArrayList<>();
    private EstrategiaEjecucion estrategiaEjecucion;
    private final List<TaskListener> listeners = new ArrayList<>();
    
    public void agregarTarea(Tarea tarea) {
      tareas.add(tarea);
    }
    
    public void setEstrategiaEjecucion(EstrategiaEjecucion estrategia) {
      this.estrategiaEjecucion = estrategia;
    }

    public void addListener(TaskListener listener){
      this.listeners.add(listener);
    }
    
    public void mostrarResumen() {
      System.out.println("=== RESUMEN DE TAREAS ===");
      System.out.println("Total de tareas: " + tareas.size());
      System.out.println("Notificaciones: " + (findInListeners("notificacion") ? "Activadas" : "Desactivadas"));
      System.out.println("Logging: " + (findInListeners("logger") ? "Activado" : "Desactivado"));
      System.out.println("Backup: " + (findInListeners("backup") ? "Activado" : "Desactivado"));
      
      int tiempoTotal = tareas.stream().mapToInt(Tarea::getTiempoEstimado).sum();
      System.out.println("Tiempo estimado total: " + tiempoTotal + " minutos");
    }

    public boolean findInListeners(String searchedListener){
      boolean found = switch (searchedListener.toLowerCase()){
        case "logger" -> listeners.stream().anyMatch(l -> l instanceof LoggerListener);
        case "backup" -> listeners.stream().anyMatch(l -> l instanceof BackupListener);
        default -> listeners.stream().anyMatch(l -> l instanceof NotificationListener);
      }; 

      return found;
    }
    
    public void ejecutarTareas() {
      if (estrategiaEjecucion == null) {
        System.out.println("No hay estrategia de ejecución configurada.");
        return;
      }
      
      System.out.println("=== INICIANDO EJECUCIÓN ===");
      
      notifyEvent(new TaskEvent(TaskType.EXECUTION_START, tareas));

      estrategiaEjecucion.ejecutar(tareas);

      notifyEvent(new TaskEvent(TaskType.ALL_COMPLETED, tareas));
    }

    public void notifyEvent(TaskEvent event){
      for (TaskListener listener : this.listeners){
        listener.onEvent(event);
      }
    }
    
    public List<Tarea> getTareas() { 
      return Collections.unmodifiableList(tareas);
    }
  }
  
  // ===================== Listeners =====================

  static class TaskEvent {
    private TaskType type;
    private List<Tarea> tareas;

    public TaskEvent (TaskType type, List<Tarea> tareas) {
      this.type = type;
      this.tareas = tareas;
    }

    public int getTareasSize(){
      return this.tareas.size();
    }

    public List<Tarea> getTareas(){
      return this.tareas;
    }
  }

  enum TaskType {
    EXECUTION_START, ALL_COMPLETED
  }

  interface TaskListener {
    void onEvent(TaskEvent event);
  }

  static class LoggerListener implements TaskListener {
    @Override
    public void onEvent(TaskEvent event){
      if (event.type == TaskType.EXECUTION_START) {
        System.out.println("[LOG] Iniciando ejecución de " + event.getTareasSize() + " tareas");
      }

      if (event.type == TaskType.ALL_COMPLETED){
        System.out.println("[LOG] Ejecución finalizada");
      }
    }
  }

  static class BackupListener implements TaskListener {
    @Override
    public void onEvent(TaskEvent event){
      if (event.type == TaskType.EXECUTION_START){
        System.out.println("[BACKUP] Creando respaldo antes de ejecutar");
      }

      if (event.type == TaskType.ALL_COMPLETED){
        System.out.println("[BACKUP] Actualizando respaldo post-ejecución");
      }
    }
  }

  static class NotificationListener implements TaskListener {
    @Override
    public void onEvent(TaskEvent event){
      if (event.type == TaskType.ALL_COMPLETED){
        long completadas = event.getTareas().stream().filter(Tarea::isCompletada).count();
        System.out.println("[NOTIFICACIÓN] Se completaron " + completadas + " de " + event.getTareas().size() + " tareas");
      }
    }
    
  }


  // ===================== ESTRATEGIAS DE EJECUCIÓN =====================

  interface EstrategiaEjecucion {
    void ejecutar(List<Tarea> tareas);
  }

  static class EjecucionSecuencial implements EstrategiaEjecucion {
    @Override
    public void ejecutar(List<Tarea> tareas) {
      System.out.println("[SECUENCIAL] Ejecutando tareas una por una");
      for (Tarea tarea : tareas) {
        tarea.ejecutar();
      }
    }
  }

  static class EjecucionParalela implements EstrategiaEjecucion {
    @Override
    public void ejecutar(List<Tarea> tareas) {
      System.out.println("[PARALELO] Ejecutando todas las tareas simultáneamente");
      for (Tarea tarea : tareas) {
        new Thread(() -> tarea.ejecutar()).start();
      }
      
      // Esperar un poco para que terminen
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  static class EjecucionPorPrioridad implements EstrategiaEjecucion {
    @Override
    public void ejecutar(List<Tarea> tareas) {
      System.out.println("[POR PRIORIDAD] Ejecutando tareas ordenadas por prioridad");
      List<Tarea> tareasOrdenadas = new ArrayList<>(tareas);
      tareasOrdenadas.sort((t1, t2) -> Integer.compare(t2.getPrioridad().getValor(), t1.getPrioridad().getValor()));
      
      for (Tarea tarea : tareasOrdenadas) {
        tarea.ejecutar();
      }
    }
  }

  // ===================== API externa =====================

  /** Esta API de reportes es externa y no podemos modificarla. Falta integrarla */
  static class ReportingServiceAPI {
    public boolean sendDailyReport(String reportName, String reportData) {
      System.out.println("[ReportingServiceAPI] Enviando reporte: " + reportName);
      // Lógica ficticia: falla si el nombre del reporte contiene espacios
      return !reportName.contains(" ");
    }
  }
}