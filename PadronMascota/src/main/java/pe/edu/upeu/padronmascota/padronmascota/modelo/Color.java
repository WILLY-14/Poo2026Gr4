package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Catálogo de colores de pelaje/plumaje (tabla color). */
public class Color extends Entidad {

    @NotBlank(message = "El nombre del color es obligatorio")
    @Size(max = 50, message = "El color puede tener hasta 50 letras")
    private String nombre;

    private int numeroMascotas;   // calculado con COUNT (columna "n° mascotas" del Excel)

    public Color() {
    }

    public Color(int id, String nombre) {
        setId(id);
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public int getNumeroMascotas() {
        return numeroMascotas;
    }

    public void setNumeroMascotas(int numeroMascotas) {
        this.numeroMascotas = numeroMascotas;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
