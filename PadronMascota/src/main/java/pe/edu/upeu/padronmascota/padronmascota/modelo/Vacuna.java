package pe.edu.upeu.padronmascota.padronmascota.modelo;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Catálogo de vacunas (tabla vacuna). Si tiene producto, cada vacunación descuenta 1 dosis del stock. */
public class Vacuna extends Entidad {

    @NotBlank(message = "El nombre de la vacuna es obligatorio")
    @Size(max = 60, message = "El nombre puede tener hasta 60 letras")
    private String nombre;

    @NotNull(message = "Elige la especie")
    private Especie especie;

    private Producto producto;   // opcional: la dosis que se usa

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

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
