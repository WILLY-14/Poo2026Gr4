package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos que tiene cualquier persona del sistema (HERENCIA).
 * Hoy solo la usa Propietario, pero si mañana agregas Veterinario,
 * hereda nombre y teléfono sin volver a escribirlos.
 */
public abstract class Persona extends Entidad {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre puede tener hasta 100 letras")
    private String nombre;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "9\\d{8}", message = "El teléfono debe tener 9 dígitos y empezar con 9")
    private String telefono;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono == null ? null : telefono.trim();
    }

    /** Cada hija dice qué tipo de persona es (POLIMORFISMO). */
    public abstract String getTipo();

    @Override
    public String toString() {
        return getNombre();
    }
}
