package pe.edu.upeu.padronmascota.padronmascota.enums;

/**
 * Tabla estado_vacunacion como enum. El id es el mismo que en la base de datos.
 */
public enum EstadoVacunacion {
    AL_DIA(1, "Al día"),
    PENDIENTE(2, "Pendiente");

    private final int id;
    private final String nombre;

    EstadoVacunacion(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static EstadoVacunacion desdeId(int id) {
        for (EstadoVacunacion e : values()) {
            if (e.id == id) {
                return e;
            }
        }
        throw new IllegalArgumentException("No existe el estado con id " + id);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
