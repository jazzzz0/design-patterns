# Modelo de examen: TaskManager
Contestar a continuación las siguientes preguntas:
 - ¿Qué patrón de diseño podés identificar en el código dado?
    * **Strategy**: se utiliza para definir el proceso a realizar según la ejecución de tareas elegida sin preocuparse por cómo se ejecuta, manejandolo a través de una interfaz.

 - ¿Qué patrón de diseño podrías agregar para mejorar el código?
    * **Singleton**: .
    * **Command**: .
    * **Decorator**: .
    * **Builder**: .
    * **Factory**: .
    * **Adapter**: .
    * **Composite**: .
 
 - Implementar UN patrón adicional para mejorar el código.
    * **Observer**: para notificar de los cambios en estados de los objetos a nuevas clases suscriptoras para desacoplar el código de GestorTareas que no debería tener la responsabilidad de lógica de logging, backup o notificaciones.