package pe.edu.upeu.padronmascota.padronmascota.enums;

/**
 * Tabla categoria_producto del Excel como enum. El id es el mismo que en la base de datos.
 */
public enum CategoriaProducto {
    COMIDA(1, "Comida"),
    ACCESORIO(2, "Accesorio"),
    MEDICAMENTO(3, "Medicamento"),
    VACUNA(4, "Vacuna"),
    INSUMO_MEDICO(5, "Insumo médico");

    private final int id;
    private final String nombre;

    CategoriaProducto(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static CategoriaProducto desdeId(int id) {
        for (CategoriaProducto e : values()) {
            if (e.id == id) {
                return e;
            }
        }
        throw new IllegalArgumentException("No existe CategoriaProducto con id " + id);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
