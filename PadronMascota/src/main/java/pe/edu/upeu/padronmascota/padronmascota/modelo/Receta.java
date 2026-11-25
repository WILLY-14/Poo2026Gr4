package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Lo que el doctor indica para casa (tabla receta + detalle_receta). No descuenta stock. */
public class Receta extends Entidad {

    @NotNull(message = "Elige la atención")
    private Atencion atencion;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @Size(max = 255, message = "Las indicaciones pueden tener hasta 255 letras")
    private String indicaciones;

    @Valid
    @NotEmpty(message = "Agrega al menos un medicamento")
    private final List<DetalleReceta> detalles = new ArrayList<>();

    public List<DetalleReceta> getDetalles() {
        return detalles;
    }

    public Atencion getAtencion() {
        return atencion;
    }

    public void setAtencion(Atencion atencion) {
        this.atencion = atencion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = Propietario.vacioANull(indicaciones);
    }
}
