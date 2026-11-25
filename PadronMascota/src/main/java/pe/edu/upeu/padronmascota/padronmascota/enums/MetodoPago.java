package pe.edu.upeu.padronmascota.padronmascota.enums;

/**
 * Tabla metodo_pago del Excel como enum. El id es el mismo que en la base de datos.
 */
public enum MetodoPago {
    EFECTIVO(1, "Efectivo"),
    TARJETA(2, "Tarjeta"),
    YAPE_PLIN(3, "Yape/Plin"),
    TRANSFERENCIA(4, "Transferencia");

    private final int id;
    private final String nombre;

    MetodoPago(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static MetodoPago desdeId(int id) {
        for (MetodoPago e : values()) {
            if (e.id == id) {
                return e;
            }
        }
        throw new IllegalArgumentException("No existe MetodoPago con id " + id);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
