package adapter.almacenamiento;
public class Main {
    public static void main(String[] args) {
        String foto = "foto.jpg";
        User user = new User("as3");
        String storage_config = user.get_storage_config();

        String request = "POST www.api.com/api/posts/\n-------Headers-------\nAuthorization: Bearer ajcofjdv.swier3nflj3.dfg\n-------Body-------\n{'image':'"+ foto +"'}";

        PostView APIview = new PostView();
        System.out.println(APIview.post(request, foto, storage_config));
    }

}

class User {
    String storage_config;
    public User(String storage_selected){
        if (storage_selected == "gdrive" || storage_selected == "dropbox") {
            this.storage_config = storage_selected;
        } else {
            this.storage_config = "as3";
        }
    }
    String get_storage_config(){
        return this.storage_config;
    }
}

class PostView{
    public String post(String request, String foto, String storage){
        try {
            String response = publicar_foto_en_nube(foto, storage);
            return "Response: {\n'success': True, \n'data':" + response + "\n}\nStatus code: 200 OK";
        } catch (Exception e) {
            return "Response: {\n'success': False, \n'message': " + e.toString() + "\n}\nStatus code: 400 BAD REQUEST";
        }
    }

    public String publicar_foto_en_nube(String foto, String storage){
        ISubidorFoto foto_uploader = null;
        if (storage=="gdrive"){
            foto_uploader = new GoogleDriveAdapter();
        }
        if (storage=="dropbox"){
            foto_uploader = new DropboxAdapter();
        } 
        if (storage=="as3"){
            foto_uploader = new AmazonS3Adapter();
        }
        if (foto_uploader == null){
            return new Exception().toString();
        }

        return foto_uploader.subir_foto(foto);
    }

    // ESTA FORMA ESTARÍA MAL YA QUE REPITE CÓDIGO (los return)
    // public String publicar_foto_en_nube(String foto, String storage){
    //     if (storage == "gdrive"){
    //         GoogleDriveAdapter gdriveAdapter = new GoogleDriveAdapter();
    //         return gdriveAdapter.subir_foto(foto);
    //     }
    //     if (storage == "dropbox"){
    //         DropboxAdapter dropboxAdapter = new DropboxAdapter();
    //         return dropboxAdapter.subir_foto(foto);
    //     }
    //     return new Exception().toString();
    // }

}

// ----------------- SERVICIOS -----------------
class Dropbox {
    public String upload_to_dropbox(String foto){
        return foto + " se ha subido a www.dropbox.com/posts/"+ foto;
    }
}

class Drive {
    public String send_to_gdrive(String foto){
        return foto + " se ha subido a www.drive.google.com/posts/"+ foto;
    }
}

class AmazonS3 {
    public String save_to_as3(String foto){
        return foto + " se ha subido a www.s3.amazon.com/posts/" + foto;
    }
}

// ----------------- INTERFAZ P/ ADAPTERS -----------------
interface ISubidorFoto {
    public String subir_foto(String foto);
}

// ----------------- ADAPTERS  -----------------

class DropboxAdapter implements ISubidorFoto {
    Dropbox adaptee = new Dropbox();
    public String subir_foto(String foto){
        return adaptee.upload_to_dropbox(foto);
    }
}

class GoogleDriveAdapter implements ISubidorFoto {
    Drive adaptee = new Drive();
    public String subir_foto(String foto){
        return adaptee.send_to_gdrive(foto);
    }
}

class AmazonS3Adapter implements ISubidorFoto{
    AmazonS3 adaptee = new AmazonS3();
    public String subir_foto(String foto){
        return adaptee.save_to_as3(foto);
    }
}
