package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Una consulta del veterinario (tabla atencion) con los insumos que usó (tabla atencion_insumo).
 * La cita es opcional: puede haber atención sin cita.
 */
public class Atencion extends Entidad {

    @NotNull(message = "Elige la mascota")
    private Mascota mascota;

    @NotNull(message = "Elige el veterinario")
    private Veterinario veterinario;

    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha de atención no puede ser futura")
    private LocalDate fecha;

    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 150, message = "El motivo puede tener hasta 150 letras")
    private String motivo;

    private Cita cita;

    // Lo que cobra el doctor por la consulta (a su criterio). Sale solo en Ventas.
    @NotNull(message = "Pon el precio de la consulta")
    @jakarta.validation.constraints.DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    @jakarta.validation.constraints.Digits(integer = 5, fraction = 2, message = "El precio puede tener hasta 2 decimales")
    private java.math.BigDecimal precioConsulta;

    public java.math.BigDecimal getPrecioConsulta() {
        return precioConsulta;
    }

    public void setPrecioConsulta(java.math.BigDecimal precioConsulta) {
        this.precioConsulta = precioConsulta;
    }

    @Valid
    private final List<AtencionInsumo> insumos = new ArrayList<>();

    /** Agrega un insumo; si ya estaba, suma la cantidad. */
    public void agregarInsumo(Producto producto, int cantidad) {
        for (AtencionInsumo i : insumos) {
            if (i.getProducto().getId() == producto.getId()) {
                i.setCantidad(i.getCantidad() + cantidad);
                return;
            }
        }
        insumos.add(new AtencionInsumo(producto, cantidad));
    }

    public List<AtencionInsumo> getInsumos() {
        return insumos;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo == null ? null : motivo.trim();
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    /** Texto para los combos de Recetas y Ventas: "14/09/2026 - Thor - Control de piel". */
    @Override
    public String toString() {
        return fecha + " - " + (mascota == null ? "" : mascota.getNombre()) + " - " + motivo;
    }
}
