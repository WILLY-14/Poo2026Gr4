package pe.edu.upeu.padronmascota.padronmascota.enums;

/** Boleta o Factura. La serie se usa para armar el número: B001-000001, F001-000001. */
public enum TipoComprobante {
    BOLETA("Boleta", "B001"),
    FACTURA("Factura", "F001");

    private final String nombre;   // así se guarda en la BD (igual que en el Excel)
    private final String serie;

    TipoComprobante(String nombre, String serie) {
        this.nombre = nombre;
        this.serie = serie;
    }

    public String getNombre() {
        return nombre;
    }

    public String getSerie() {
        return serie;
    }

    public static TipoComprobante desdeNombre(String nombre) {
        for (TipoComprobante t : values()) {
            if (t.nombre.equals(nombre)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Comprobante desconocido: " + nombre);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
