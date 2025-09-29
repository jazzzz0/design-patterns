# Modelo de examen: DocumentProcessor
Contestar a continuación las siguientes preguntas:
 - ¿Qué patrón de diseño podés identificar en el código dado?
    * **Observer**: para que el procesador de documentos pueda notificar a múltiples objetos de los cambios realizados.
    * **Strategy**: para accionar de la manera correcta según el formato de salida indicado.

 - ¿Qué patrón de diseño podrías agregar para mejorar el código?
    * **Adapter**: para manejar de manera más general la conexión al Cloud Storage sin tener que depender del y cambiar el flujo principal, cada vez que se modifique algo en esa llamada.
 
 - Implementar UN patrón adicional para mejorar el código.
    * **Decorator**: para las transformaciones que se harán a los documentos, de manera que se puedan aplicar dinámicamente sin modificar la clase base. Así también podemos tener código menos acoplado.
    * **Builder**: para encadenar las configuraciones del procesador de documentos y construirlo ya listo.
    * **Factory**: para crear el formateador según el criterio de salida por un parámetro 

## Explicación de Builder
El objeto complejo aquí es el ProcesadorDocumentos. ¿Por qué es complejo?
 - Tiene listeners (`LoggerListener`, `MetricsListener`)
 - Puede tener transformaciones activadas (compresion, encriptacion, marcaDeAgua)
 - Puede tener un formateador (PDF, DOCX, HTML)

En pocas palabras: hay varias configuraciones opcionales que se aplican paso a paso, por lo tanto es un típico candidato para Builder.

---

Cada método del Builder debe devolver this, es decir, una referencia al Builder mismo.

#### 💡Resumen conceptual:
 - Métodos como `conCompresion`, `conEncriptacion`, `conMarcaDeAgua`, `conFormateador`, `agregarListener` → **devuelven el Builder (this)**, no void.
 - `build()` → *devuelve el ProcesadorDocumentos ya configurado*.

## Explicación de Factory
Tenemos este código:
```
   if (tipo.equals("pdf")) → FormateadorPDF
   else if (tipo.equals("docx")) → FormateadorDOCX
   else → FormateadorHTML
```

**Problema**: si mañana se agregase otro formato, tendría que modificarse este if dentro del main o del Builder.

**Solución**: *Factory centraliza la creación de formateadores*, y el código cliente (main o Builder) solo pide “dame un formateador de tipo X”.

### Qué hace el Factory conceptualmente
 - Crea un objeto de la familia FormateadorDocumento según un parámetro.
 - El cliente solo sabe que va a recibir un `FormateadorDocumento`, **no le importa la clase concreta**.
 - Ventajas:
   - Encapsula la lógica de creación.
   - Hace que agregar nuevos formatos sea más fácil (solo modificar la Factory).

### Cómo funcionaría en este caso
1. El cliente pide al Factory: “quiero un formateador de tipo X” (string: “pdf”, “docx”, “html”).
2. El Factory decide qué subclase concreta crear: `FormateadorPDF`, `FormateadorDOCX` o `FormateadorHTML`.
3. Devuelve el objeto ya creado, listo para usar.

Esto reemplaza el `if-else` que estaba en el main.

### Relación con Builder
El Builder, al integrarlo con Factory, puede hacer lo siguiente:
 - Guardar el string formato internamente.
 - En `build()`, llamar al Factory para obtener el formateador y asignarlo al procesador.

### Beneficio principal
- **Separación de responsabilidades**: el Builder solo configura el procesador, el Factory decide qué clase concreta usar.
- **Extensible**: si se agrega “FormateadorXML” mañana, solo se añade al Factory, y el resto del código no cambia.

## Resumen general
### 1️⃣ ProcesadorDocumentos
Es el **centro de todo**: recibe documentos y aplica transformaciones, formateo y notificaciones a listeners.

Recibe:
 - **Decorators** aplicados a los documentos (compresión, encriptación, marca de agua).
 - **Formateador** (PDF, DOCX, HTML) para generar la salida.
 - **Listeners** que reaccionan al terminar el procesamiento.

Responsable de *procesar documentos* y *notificar a los listeners*.

### 2️⃣ ProcesadorDocumentosBuilder
**Propósito**: Construir un `ProcesadorDocumentos` configurado según las preferencias del cliente.

**Acumula información** antes de construir:
 - Flags de transformaciones: compresión, encriptación, marca de agua.
 - Tipo de formateador: string `"pdf"`, `"docx"`, `"html"`.
 - Lista de listeners: Logger, Metrics, etc.

**Build()**:
 1. Crea un objeto `ProcesadorDocumentos`.
 2. Aplica los flags de transformación al procesador.
 3. Llama a la **Factory** para obtener el formateador concreto.
 4. Registra todos los listeners.
 5. Devuelve el procesador listo.

### 3️⃣ FormateadorFactory
**Propósito**: Decidir qué clase concreta de `FormateadorDocumento` crear según un parametro.

**Cliente**: el Builder (no el main directamente).

**Beneficio**: el main y el Builder no necesitan conocer las subclases concretas, solo piden "dame un formateador de tipo X".

**Salida**: devuelve un objeto de tipo `FormateadorDocumento` (PDF, DOCX, HTML, etc).

### 4️⃣ Decorator
**Propósito**: Aplicar transformaciones a los documentos sin modificar la clase base (`Documento`).

**Implementaciones concretas**:
 - `DocumentoCompresion` → reduce tamaño.
 - `DocumentoEncriptado` → agrega overhead de seguridad.
 - `DocumentoMarcaDeAgua` → agrega marca visual.

**Cómo se aplica**:
 - El procesador envuelve cada documento con los decorators según los flags configurados.
 - Cada decorator implementa la misma interfaz que `Documento`.

### 5️⃣ Listeners (Observer)
**Propósito**: Reaccionar al finalizar el procesamiento (Logger, Metrics, etc.),

**Integración**:
 - Builder acumula la lista de listeners.
 - En `build()`, el procesador los registra.
 - Cuando termina el procesamiento, el procesador notifica a todos.

### 6️⃣ Flujo general
1. **Main**: crea Builder y lo configura (con los métodos que nos aporta el mismo).

2. **Builder.build()**:
   - Crea `ProcesadorDocumentos`.
   - Aplica flags de transformación.
   - Llama a Factory para el formateador.
   - Registra listeners.
   - Devuelve procesador listo.

3. **ProcesadorDocumentos.procesar(documentos)**:
   - Aplica decorators segun los flags.
   - Genera salida con el formateador.
   - Notifica a los listeners.

### Resumen conceptual:
 - **Builder** → configura y entrega un procesador listo.
 - **Factory** → decide qué formateador concreto usar.
 - **Strategy** → encapsula la variación del comportamiento de los formateadores
 - **Decorator** → transforma documentos dinámicamente.
 - **Listeners** → reaccionan al final del proceso.