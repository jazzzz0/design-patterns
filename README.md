## Práctica de patrones de diseño
Los patrones a practicar en principio son:
- Creacionales:
    - Factory,
    - Singleton,
    - Builder.
- Comportamiento:
    - Command,
    - Observer,
    - Strategy.
- Estructurales:
    - Adapter,
    - Composite,
    - Decorator.

### Ejercicios hechos
#### Adapter
- **Adaptador de servicios de almacenamiento**. 
<br> Tenemos una interfaz ISubidorFoto que expone un contrato a toda clase que la implemente, éstas deberán implementar el método subir_foto(). Las clases que implementan la interfaz serán **Adapter** de un servicio.
<br> Se simula un servidor que recibe una petición POST para guardar una imagen. La práctica se basa en almacenar la misma en el servicio determinado por la configuración del usuario. Dependiendo de la configuración, se instanciará el Adapter adecuado. 
<br> El uso de **Adapter** y la definición del método **publicar_foto_en_nube()** permite intercambiar objetos sin modificar el código que los utiliza. Sin importar si se usa `DropboxAdapter` o `GoogleDriveAdapter`, el código siempe llama a la misma cosa: **foto_uploader.subir_foto()**

```

    public String publicar_foto_en_nube(String foto, String storage){
        ISubidorFoto foto_uploader = null;

        if (storage=="gdrive"){
            foto_uploader = new GoogleDriveAdapter();
        }
        if (storage=="dropbox"){
            foto_uploader = new DropboxAdapter();
        } 
        if (foto_uploader == null){
            return new Exception().toString();
        }

        return foto_uploader.subir_foto(foto);
    }

```

<br> El beneficio de este patron es que aísla el lugar donde se decide que tipo de objeto usar. El codigo que realmente hace el trabajo (`subir_foto`) no necesita saber si esta trabajando con Dropbox o Google Drive.
<br> La clave está en que si tuviera que añadir 10 servicios de almacenamiento más, solo tendría que añadir unos `if` o un `switch`. Pero el código que llama al método `subir_foto()` seguiría siendo el mismo, una sola línea. Esto simplifica el **mantenimiento del código a largo plazo** en un proyecto.
