# Modelo de examen: TaskManager
Contestar a continuación las siguientes preguntas:
 - ¿Qué patrón de diseño podés identificar en el código dado?
    * **Strategy**: se utiliza para definir el proceso a realizar según la ejecución de tareas elegida sin preocuparse por cómo se ejecuta, manejandolo a través de una interfaz.

 - ¿Qué patrón de diseño podrías agregar para mejorar el código?
    * **Command**: se puede aplicar para encapsular las acciones como ejecutarTareas, mostrarResumen, etc.
    * **Decorator**: .
    * **Builder**: .
    * **Factory**: .
    * **Adapter**: .
    * **Composite**: .
 
 - Implementar UN patrón adicional para mejorar el código.
    * **Observer**: para notificar de los cambios en estados de los objetos a nuevas clases suscriptoras para desacoplar el código de GestorTareas que no debería tener la responsabilidad de lógica de logging, backup o notificaciones.
    * **Singleton**: para garantizar que haya una sola instancia de LoggerListener en el sistema. Esto evita que se creen múltiples loggers con configuraciones distintas y asegura un punto centralizado de registro.

## Explicación de Observer (en GestorTareas)
### Problema original

El `GestorTareas` tenía muchos if en distintos lados:
      
```
if (loggingActivo)
if (backupActivo)
if (notificacionesActivas)
```

Eso lo hacía responsable de cosas que **no deberían ser suyas** (logging, backup, notificaciones).

El código quedaba **acoplado** y difícil de extender.

### Solucion

 - Crear la interfaz `TaskListener` con un método único `onEvent(TaskEvent event)`. 

 - `TaskEvent`encapsula información sobre lo que pasó (`EXECUTION_START`, `ALL_COMPLETED`, lista de tareas, etc.).

 - El `GestorTareas` mantiene una lista de listeners (`addListener`) y, cuando ocurre algo importante, **notifica a todos** con `notifyEvent()`.

 - Se implementan tres observadores concretos:
   - `LoggerListener` (escribe en log).
   - `BackupListener` (hace respaldo).
   - `NotificationListener`(avisa cuántas tareas se completaron).

### ¿Qué se logra?

 - **Desacoplar**: `GestorTareas` ya no sabe cómo se loguea, respalda o notifica, solo dispara eventos.
 - **Extensibilidad**: agregar nuevos listeners no requiere modificar el gestor.
 - **Reutilización**: los observadores se pueden enchufar o quitar fácilmente.

### Resumen

Se aplica *Observer* para que GestorTareas **notifique a distintos listeners cuando ocurren eventos importantes**. Esto desacopla el gestor de las acciones concretas y permite extender el sistema agregando nuevos observadores sin modificar el código existente.

## Explicación de Singleton (LoggerListener)
### Problema original

El logger es un recurso que **no tiene sentido duplicar**: teniendo varios instancias, podría terminar con configuraciones distintas o con registros dispersos.

Se quiso garantizar que hubiera **una sola instancia global** del logger en toda la aplicación. 

### Solucion

 - Declarar un atributo estático `private static LoggerListener instance;`.
 - Hacer el constructor privado (`private LoggerListener(){}`) para que nadie pueda instanciarlo con `new`.
 - Implementar un método estático `getInstance()` que:
   - Cree la instancia si no existe.
   - Devuelve siempre la misma instancia si ya fue creada.

### ¿Qué se logra?

 - **Una sola instancia** de `LoggerListener` en toda la aplicación.
 - **Acceso global** a través de `LoggerListener.getInstance()`.
 - Evitar inconsistencias y duplicación en el logging.

### Resumen

Se aplica *Singleton* en LoggerListener para garantizar que solo exista una instancia de logger en toda la aplicación. Esto evita múltiples logger con configuraciones distintas y **asegura un punto centralizado de registro**.

## 💡 Resumen concenptual
 - **Observer** → desacoplar y permitir múltiples suscriptores.
 - **Singleton** → garantizar una única instancia de un recurso compartido.
