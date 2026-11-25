package pe.edu.upeu.padronmascota.padronmascota.modelo;

/**
 * Clase base de todo el modelo (ABSTRACCIÓN + HERENCIA).
 * Toda tabla tiene un id, así que lo escribimos una sola vez aquí.
 */
public abstract class Entidad {

    private int id;   // ENCAPSULAMIENTO: privado, solo se usa con get/set

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /** true si todavía no se guardó en la BD (la BD aún no le dio un id). */
    public boolean esNuevo() {
        return id == 0;
    }
}
