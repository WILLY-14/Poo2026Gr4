package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

/** Una vacuna aplicada a una mascota (tabla vacunacion). */
public class Vacunacion extends Entidad {

    @NotNull(message = "Elige la mascota")
    private Mascota mascota;

    @NotNull(message = "Elige la vacuna")
    private Vacuna vacuna;

    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha de vacunación no puede ser futura")
    private LocalDate fecha;

    private LocalDate proximaDosis;

    /** proxima_dosis debe ser posterior a fecha. */
    @AssertTrue(message = "La próxima dosis debe ser después de la fecha")
    public boolean isProximaDosisValida() {
        return fecha == null || proximaDosis == null || proximaDosis.isAfter(fecha);
    }

    @AssertTrue(message = "La vacuna no es para la especie de la mascota")
    public boolean isVacunaDeLaEspecie() {
        return mascota == null || vacuna == null || mascota.getEspecie() == vacuna.getEspecie();
    }

    /** "Vencida" si la próxima dosis ya pasó; si no, "Vigente". */
    public String getEstadoDosis() {
        if (proximaDosis == null) {
            return "Sin refuerzo";
        }
        return proximaDosis.isBefore(LocalDate.now()) ? "Vencida" : "Vigente";
    }

    public boolean isVencida() {
        return "Vencida".equals(getEstadoDosis());
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }

    public Vacuna getVacuna() {
        return vacuna;
    }

    public void setVacuna(Vacuna vacuna) {
        this.vacuna = vacuna;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDate getProximaDosis() {
        return proximaDosis;
    }

    public void setProximaDosis(LocalDate proximaDosis) {
        this.proximaDosis = proximaDosis;
    }
}
