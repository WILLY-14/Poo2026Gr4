package pe.edu.upeu.padronmascota.padronmascota.enums;

/**
 * Tabla rol como enum. Cada rol ve solo los menús que le tocan
 * (lo decide MenuServicioImpl).
 */
public enum Rol {
    ADMIN(1, "Administrador"),
    RECEPCION(2, "Recepcionista"),
    VETERINARIO(3, "Veterinario");

    private final int id;
    private final String nombre;

    Rol(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static Rol desdeId(int id) {
        for (Rol r : values()) {
            if (r.id == id) {
                return r;
            }
        }
        throw new IllegalArgumentException("No existe el rol con id " + id);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
