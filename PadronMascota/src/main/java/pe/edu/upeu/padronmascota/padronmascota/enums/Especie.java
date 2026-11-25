package pe.edu.upeu.padronmascota.padronmascota.enums;

/**
 * Tabla especie como enum. El id es el mismo que en la base de datos.
 */
public enum Especie {
    PERRO(1, "Perro"),
    GATO(2, "Gato"),
    OTRO(3, "Otro");

    private final int id;
    private final String nombre;

    Especie(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    /** Convierte el id que viene de la BD en el enum. */
    public static Especie desdeId(int id) {
        for (Especie e : values()) {
            if (e.id == id) {
                return e;
            }
        }
        throw new IllegalArgumentException("No existe la especie con id " + id);
    }

    @Override
    public String toString() {
        return nombre; // lo que se ve en el ComboBox y en la tabla
    }
}
