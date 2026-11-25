package pe.edu.upeu.padronmascota.padronmascota.enums;

/** Tabla estado_cita como enum. El id es el mismo que en la base de datos. */
public enum EstadoCita {
    PENDIENTE(1, "Pendiente"),
    ATENDIDA(2, "Atendida"),
    CANCELADA(3, "Cancelada");

    private final int id;
    private final String nombre;

    EstadoCita(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static EstadoCita desdeId(int id) {
        for (EstadoCita e : values()) {
            if (e.id == id) {
                return e;
            }
        }
        throw new IllegalArgumentException("No existe el estado de cita con id " + id);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
