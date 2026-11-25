package pe.edu.upeu.padronmascota.padronmascota.enums;

/** Una venta Pagada descuenta stock; si se Anula, el stock se devuelve. */
public enum EstadoVenta {
    PAGADA("Pagada"),
    ANULADA("Anulada");

    private final String nombre;   // así se guarda en la BD

    EstadoVenta(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public static EstadoVenta desdeNombre(String nombre) {
        for (EstadoVenta e : values()) {
            if (e.nombre.equals(nombre)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado de venta desconocido: " + nombre);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
