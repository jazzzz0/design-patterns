## Composite
- Un álbum de fotos puede contener fotos y sub-álbumes, creando una estructura de carpetas anidadas, como las de una computadora.
- La **meta del patrón** es que se puedan hacer cosas como:
    - Calcular el peso total en MB de un álbum (sumando el peso de todas las fotos dentro, sin importar si están en sub-álbumes).
    - Cambiar el nombre de todas las fotos dentro de un álbum.
- Con este patrón, no se necesita una lógica diferente para manejar un álbum que tiene 10 fotos y otra para manejar un álbum con 5 sub-álbumes. Composite permite tratar a ambos como si fueran lo mismo.
- La interfaz define todas las operaciones que las hojas y los composites puedan realizar.
### Ejemplo c/ FocusApp
**Los comentarios de las publicaciones**. Un comentario puede existir por sí mismo, o puede ser la **raíz** de una conversación que tiene respuestas anidadas. Cada una de esas respuestas puede tener otras respuestas, y así sucesivamente.
<br> Si quisiera aplicar un método a todos los comentarios (y sus respuestas), es más fácil al tratar toda la estructura como un solo objeto.
#### Métodos que se podrían aplicar a los comentarios:
- Buscar una palabra específica.
- Eliminar un hilo completo.

