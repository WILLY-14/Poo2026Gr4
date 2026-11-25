package pe.edu.upeu.padronmascota.padronmascota.modelo;

import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoCita;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/** Cita de una mascota con un veterinario (tabla cita). Un doctor no puede tener dos citas a la misma hora. */
public class Cita extends Entidad {

    @NotNull(message = "Elige la mascota")
    private Mascota mascota;

    @NotNull(message = "Elige el veterinario")
    private Veterinario veterinario;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria (por ejemplo 09:30)")
    private LocalTime hora;

    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 150, message = "El motivo puede tener hasta 150 letras")
    private String motivo;

    private EstadoCita estado = EstadoCita.PENDIENTE;

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

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo == null ? null : motivo.trim();
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    /** Texto para el combo de citas en Atenciones: "05/11/2026 09:00 - Thor". */
    @Override
    public String toString() {
        return fecha + " " + hora + " - " + (mascota == null ? "" : mascota.getNombre()) + " (" + motivo + ")";
    }
}
