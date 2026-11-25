package pe.edu.upeu.padronmascota.padronmascota.enums;

/** Sexo de la mascota. En la base de datos se guarda el nombre ('Macho' / 'Hembra'). */
public enum Sexo {
    MACHO("Macho"),
    HEMBRA("Hembra");

    private final String nombre;

    Sexo(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public static Sexo desdeNombre(String nombre) {
        for (Sexo s : values()) {
            if (s.nombre.equalsIgnoreCase(nombre)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Sexo no válido: " + nombre);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
