package examenes.DeviceController;

import java.util.*;

/**
 * Contestar a continuación las siguientes preguntas:
 * - ¿Qué patrón de diseño podés identificar en el código dado?
 * - ¿Qué patrón de diseño podrías agregar para mejorar el código?
 *
 * <p>Implementar UN patrón adicional para mejorar el código.
 */
public class DeviceController {

  public static void main(String[] args) {
    // Crear dispositivos
    Luz luzSala = new Luz("Luz Sala", 100);
    Luz luzCocina = new Luz("Luz Cocina", 80);
    Termostato termostato = new Termostato("Termostato Principal", 22.0);
    Ventilador ventilador = new Ventilador("Ventilador Habitación", 2);
    
    // Crear controlador central
    ControladorCentral controlador = new ControladorCentral();
    controlador.registrarDispositivo(luzSala);
    controlador.registrarDispositivo(luzCocina);
    controlador.registrarDispositivo(termostato);
    controlador.registrarDispositivo(ventilador);
    
    // Ejecutar operaciones
    System.out.println("=== CONTROL DE DISPOSITIVOS ===\n");
    
    controlador.encenderDispositivo("Luz Sala");
    controlador.ajustarIntensidadLuz("Luz Sala", 75);
    
    controlador.encenderDispositivo("Termostato Principal");
    controlador.ajustarTemperatura("Termostato Principal", 24.0);
    
    controlador.encenderDispositivo("Ventilador Habitación");
    controlador.ajustarVelocidadVentilador("Ventilador Habitación", 3);
    
    controlador.apagarDispositivo("Luz Cocina");
    
    System.out.println("\n=== ESTADO FINAL ===");
    controlador.mostrarEstadoDispositivos();
    
    // Usar API de monitoreo externo
    MonitoringSystemAPI monitoringAPI = new MonitoringSystemAPI();
    boolean registered = monitoringAPI.registerDevice("LUZ-SALA-001", "LIGHT");
    System.out.println("\nDispositivo registrado en monitoreo: " + (registered ? "exitoso" : "fallido"));
  }

  // ===================== Dominio =====================

  abstract static class Dispositivo {
    protected final String nombre;
    protected boolean encendido;
    
    public Dispositivo(String nombre) {
      this.nombre = nombre;
      this.encendido = false;
    }
    
    public String getNombre() {
      return nombre;
    }
    
    public boolean isEncendido() {
      return encendido;
    }
    
    public void encender() {
      encendido = true;
      System.out.println("[" + nombre + "] Encendido");
    }
    
    public void apagar() {
      encendido = false;
      System.out.println("[" + nombre + "] Apagado");
    }
    
    public abstract String getEstado();
  }

  static class Luz extends Dispositivo {
    private int intensidad; // 0-100
    
    public Luz(String nombre, int intensidad) {
      super(nombre);
      this.intensidad = intensidad;
    }
    
    public int getIntensidad() {
      return intensidad;
    }
    
    public void setIntensidad(int intensidad) {
      this.intensidad = Math.max(0, Math.min(100, intensidad));
      System.out.println("[" + nombre + "] Intensidad ajustada a " + this.intensidad + "%");
    }
    
    @Override
    public String getEstado() {
      return nombre + " - " + (encendido ? "ON" : "OFF") + " - Intensidad: " + intensidad + "%";
    }
  }

  static class Termostato extends Dispositivo {
    private double temperatura; // en grados Celsius
    
    public Termostato(String nombre, double temperatura) {
      super(nombre);
      this.temperatura = temperatura;
    }
    
    public double getTemperatura() {
      return temperatura;
    }
    
    public void setTemperatura(double temperatura) {
      this.temperatura = Math.max(16.0, Math.min(30.0, temperatura));
      System.out.println("[" + nombre + "] Temperatura ajustada a " + this.temperatura + "°C");
    }
    
    @Override
    public String getEstado() {
      return nombre + " - " + (encendido ? "ON" : "OFF") + " - Temperatura: " + temperatura + "°C";
    }
  }

  static class Ventilador extends Dispositivo {
    private int velocidad; // 1-5
    
    public Ventilador(String nombre, int velocidad) {
      super(nombre);
      this.velocidad = velocidad;
    }
    
    public int getVelocidad() {
      return velocidad;
    }
    
    public void setVelocidad(int velocidad) {
      this.velocidad = Math.max(1, Math.min(5, velocidad));
      System.out.println("[" + nombre + "] Velocidad ajustada a " + this.velocidad);
    }
    
    @Override
    public String getEstado() {
      return nombre + " - " + (encendido ? "ON" : "OFF") + " - Velocidad: " + velocidad;
    }
  }

  // ===================== CONTROLADOR CENTRAL =====================

  static class ControladorCentral {
    private final Map<String, Dispositivo> dispositivos = new HashMap<>();
    
    public void registrarDispositivo(Dispositivo dispositivo) {
      dispositivos.put(dispositivo.getNombre(), dispositivo);
      System.out.println("Dispositivo registrado: " + dispositivo.getNombre());
    }
    
    public void encenderDispositivo(String nombre) {
      Dispositivo dispositivo = dispositivos.get(nombre);
      if (dispositivo != null) {
        dispositivo.encender();
      } else {
        System.out.println("Dispositivo no encontrado: " + nombre);
      }
    }
    
    public void apagarDispositivo(String nombre) {
      Dispositivo dispositivo = dispositivos.get(nombre);
      if (dispositivo != null) {
        dispositivo.apagar();
      } else {
        System.out.println("Dispositivo no encontrado: " + nombre);
      }
    }
    
    public void ajustarIntensidadLuz(String nombre, int intensidad) {
      Dispositivo dispositivo = dispositivos.get(nombre);
      if (dispositivo instanceof Luz) {
        ((Luz) dispositivo).setIntensidad(intensidad);
      } else {
        System.out.println("El dispositivo no es una luz: " + nombre);
      }
    }
    
    public void ajustarTemperatura(String nombre, double temperatura) {
      Dispositivo dispositivo = dispositivos.get(nombre);
      if (dispositivo instanceof Termostato) {
        ((Termostato) dispositivo).setTemperatura(temperatura);
      } else {
        System.out.println("El dispositivo no es un termostato: " + nombre);
      }
    }
    
    public void ajustarVelocidadVentilador(String nombre, int velocidad) {
      Dispositivo dispositivo = dispositivos.get(nombre);
      if (dispositivo instanceof Ventilador) {
        ((Ventilador) dispositivo).setVelocidad(velocidad);
      } else {
        System.out.println("El dispositivo no es un ventilador: " + nombre);
      }
    }
    
    public void apagarTodo() {
      System.out.println("Apagando todos los dispositivos...");
      for (Dispositivo dispositivo : dispositivos.values()) {
        if (dispositivo.isEncendido()) {
          dispositivo.apagar();
        }
      }
    }
    
    public void encenderTodo() {
      System.out.println("Encendiendo todos los dispositivos...");
      for (Dispositivo dispositivo : dispositivos.values()) {
        if (!dispositivo.isEncendido()) {
          dispositivo.encender();
        }
      }
    }
    
    public void mostrarEstadoDispositivos() {
      for (Dispositivo dispositivo : dispositivos.values()) {
        System.out.println(dispositivo.getEstado());
      }
    }
    
    public void ejecutarModoNoche() {
      System.out.println("Activando modo noche...");
      for (Dispositivo dispositivo : dispositivos.values()) {
        if (dispositivo instanceof Luz) {
          Luz luz = (Luz) dispositivo;
          luz.encender();
          luz.setIntensidad(20);
        } else if (dispositivo instanceof Termostato) {
          Termostato termo = (Termostato) dispositivo;
          termo.encender();
          termo.setTemperatura(20.0);
        } else {
          dispositivo.apagar();
        }
      }
    }
    
    public void ejecutarModoAhorro() {
      System.out.println("Activando modo ahorro...");
      for (Dispositivo dispositivo : dispositivos.values()) {
        if (dispositivo instanceof Luz) {
          Luz luz = (Luz) dispositivo;
          if (luz.isEncendido()) {
            luz.setIntensidad(50);
          }
        } else if (dispositivo instanceof Termostato) {
          Termostato termo = (Termostato) dispositivo;
          termo.setTemperatura(21.0);
        }
      }
    }
  }

  // ===================== API externa =====================

  /** Esta API de monitoreo es externa y no podemos modificarla. Falta integrarla */
  static class MonitoringSystemAPI {
    public boolean registerDevice(String deviceId, String deviceType) {
      System.out.println("[MonitoringSystemAPI] Registrando dispositivo");
      System.out.println("[MonitoringSystemAPI] ID: " + deviceId);
      System.out.println("[MonitoringSystemAPI] Tipo: " + deviceType);
      
      // Lógica ficticia: solo acepta IDs en formato XXX-XXX-NNN
      return deviceId.matches("[A-Z]+-[A-Z]+-\\d+");
    }
    
    public void sendStatus(String deviceId, int statusCode) {
      System.out.println("[MonitoringSystemAPI] Estado recibido para " + deviceId + ": " + statusCode);
    }
  }
}