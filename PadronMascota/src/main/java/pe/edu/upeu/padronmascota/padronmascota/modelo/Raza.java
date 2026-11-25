package pe.edu.upeu.padronmascota.padronmascota.modelo;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Catálogo de razas (tabla raza). Cada raza es de una especie. */
public class Raza extends Entidad {

    @NotBlank(message = "El nombre de la raza es obligatorio")
    @Size(max = 60, message = "La raza puede tener hasta 60 letras")
    private String nombre;

    @NotNull(message = "Elige la especie")
    private Especie especie;

    private int numeroMascotas;   // calculado con COUNT: cuántas mascotas activas tienen esta raza

    public Raza() {
    }

    public Raza(int id, String nombre, Especie especie) {
        setId(id);
        this.nombre = nombre;
        this.especie = especie;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
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
