package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Una línea de la receta: medicamento, dosis, frecuencia y cuántos días. */
public class DetalleReceta extends Entidad {

    @NotNull(message = "Elige el medicamento")
    private Producto producto;

    @NotBlank(message = "La dosis es obligatoria (ej. 1 tableta)")
    @Size(max = 50, message = "La dosis puede tener hasta 50 letras")
    private String dosis;

    @NotBlank(message = "La frecuencia es obligatoria (ej. Cada 8 horas)")
    @Size(max = 50, message = "La frecuencia puede tener hasta 50 letras")
    private String frecuencia;

    @NotNull(message = "Los días son obligatorios")
    @Min(value = 1, message = "Mínimo 1 día")
    private Integer dias;

    public DetalleReceta() {
    }

    public DetalleReceta(Producto producto, String dosis, String frecuencia, Integer dias) {
        this.producto = producto;
        this.dosis = dosis == null ? null : dosis.trim();
        this.frecuencia = frecuencia == null ? null : frecuencia.trim();
        this.dias = dias;
    }

    public Producto getProducto() {
        return producto;
    }

    public String getDosis() {
        return dosis;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public Integer getDias() {
        return dias;
    }
}
