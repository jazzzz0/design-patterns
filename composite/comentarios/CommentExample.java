import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;


public class CommentExample{
    public static void main(String[] args) {
        // La lista de tipo Interfaz me sirve para manejar a todos los objetos de la misma manera sin importar si son de Comment o CommentComposite
        List<Comment> commentsTable = new ArrayList<>(); // sería la tabla en la db

        Comment comentario_1 = new Comment(1, "Comentario 1", null);
        Comment comentario_2 = new Comment(2, "Comentario 2", comentario_1);
        Comment comentario_3 = new Comment(3, "Comentario 3", null);        
        Comment comentario_4 = new Comment(4, "Comentario 4", comentario_2);
        Comment comentario_5 = new Comment(5, "Comentario 5", comentario_4);
        Comment comentario_6 = new Comment(6, "Comentario 6", null);
        Comment comentario_7 = new Comment(7, "Comentario 7", comentario_1);
        Comment comentario_8 = new Comment(8, "Comentario 8", comentario_2);

        commentsTable.add(comentario_1);
        commentsTable.add(comentario_2);
        commentsTable.add(comentario_3);
        commentsTable.add(comentario_4);
        commentsTable.add(comentario_5);
        commentsTable.add(comentario_6);
        commentsTable.add(comentario_7);
        commentsTable.add(comentario_8);
        
        List<Comentable> commentsPost = buildCommentTree(commentsTable); // arbol_de_comentarios

        for (Comentable comment : commentsPost) {
            System.out.println(comment.show());
        }


    }

    public static List<Comentable> buildCommentTree(List<Comment> commentsTable){
        // Ordenar la lista para que los padres aparezcan antes que los hijos.
        commentsTable.sort(Comparator.comparing(Comment::getId));

        List<Comentable> rootComments = new ArrayList<>();
        // HashMap es un diccionario que voy a usar para almacenar los comentarios con sus id como clave para buscarlos luego
        HashMap<Integer, Comentable> map = new HashMap<>();

        for (Comment c : commentsTable){
            // 1. Identificar comentarios raíz (sin padre)
            if(c.getParentComment()==null){
                // Por cada comentario raíz, crear un CommentComposite
                CommentComposite rootComposite = new CommentComposite(c);
                rootComments.add(rootComposite);
                map.put(c.getId(), rootComposite);

            } else {
                // 2. Si es una respuesta, hay que encontrar el padre en el HashMap y agregarlo
                Comentable parent = map.get(c.getParentComment().getId());
                
                if (parent instanceof CommentComposite){
                    // Si el padre ya es un Composite, crear un Composite para el hijo también
                    CommentComposite childComposite = new CommentComposite(c);
                    ((CommentComposite) parent).addChild(childComposite);
                    
                    // Guardar el hijo en el mapa por si tiene más respuestas
                    map.put(c.getId(), childComposite); 
                } else if (parent instanceof Comment) {
                    // Si el padre es un Comment y ahora tiene un hijo, hay que ascenderlo a Composite
                    CommentComposite newParentComposite = new CommentComposite((Comment) parent);
                    newParentComposite.addChild(c);

                    // Buscar y reemplazar el Comment original con el nuevo Composite en la lista de raíces
                    int index = rootComments.indexOf(parent);
                    if (index != -1) {
                        rootComments.set(index, newParentComposite);
                    }

                    // Actualizar el mapa con el nuevo composite y el nuevo hijo
                    map.put(((Comment) parent).getId(), newParentComposite);
                    map.put(c.getId(), c);
                }
            }
        }
        return rootComments;
    }

}


interface Comentable {
    public String show();
}

class Comment implements Comentable {
    private int id;
    // private int post_id;
    private String content;
    private Comment parentComment;

    public Comment (int id, String content, Comment parentComment) {
        this.id = id;
        this.content = content;
        this.parentComment = parentComment;
    }

    public int getId(){
        return this.id;
    }

    public Comment getParentComment(){
        return this.parentComment;
    }


    public String show() {
        return this.content;
    }

}


class CommentComposite implements Comentable {
    private Comment rootComment;
    private List<Comentable> childComments = new ArrayList<>();
    

    public CommentComposite(Comment rootComment) {
        this.rootComment = rootComment;
    }

    void addChild(Comentable child){
        this.childComments.add(child);
    }

    @Override
    public String show(){
        return show(0);
    }

    private String show(int depth) {
        StringBuilder sb = new StringBuilder();
        String indentation = "     ".repeat(depth);

        sb.append(indentation).append(this.rootComment.show()).append("\n");

        for (Comentable child : this.childComments) {
            if ( child instanceof CommentComposite ) {                
                sb.append(((CommentComposite) child).show(depth + 1));
            
            } else { 
                sb.append(indentation).append("     ").append(child.show()).append("\n");
            
            }
        }

        return sb.toString();
    }


}