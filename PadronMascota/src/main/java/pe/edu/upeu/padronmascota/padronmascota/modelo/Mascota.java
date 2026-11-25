package pe.edu.upeu.padronmascota.padronmascota.modelo;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoVacunacion;
import pe.edu.upeu.padronmascota.padronmascota.enums.Sexo;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Tabla mascota. COMPOSICIÓN: tiene una raza, un color y un propietario (objetos, no solo ids). */
public class Mascota extends Entidad {

    @NotBlank(message = "El nombre de la mascota es obligatorio")
    @Size(max = 60, message = "El nombre puede tener hasta 60 letras")
    private String nombre;

    @NotNull(message = "Elige la especie")
    private Especie especie;

    @NotNull(message = "Elige la raza")
    private Raza raza;

    @NotNull(message = "Elige el color")
    private Color color;

    @NotNull(message = "Elige el sexo")
    private Sexo sexo;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 40, message = "La edad máxima es 40 años")
    private Integer edadAprox;

    @NotNull(message = "La fecha de registro es obligatoria")
    @PastOrPresent(message = "La fecha de registro no puede ser futura")
    private LocalDate fechaRegistro;

    @NotNull(message = "Elige el propietario")
    private Propietario propietario;

    @NotNull(message = "Elige el estado de vacunación")
    private EstadoVacunacion estado;

    private boolean activo = true;   // borrado lógico: "eliminar" la marca como inactiva

    @Size(max = 255, message = "Las observaciones pueden tener hasta 255 letras")
    private String observaciones;

    /** La raza elegida debe ser de la misma especie (no existe un "Persa" perro). */
    @AssertTrue(message = "La raza no corresponde a la especie")
    public boolean isRazaDeLaEspecie() {
        return raza == null || especie == null || raza.getEspecie() == especie;
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

    public Raza getRaza() {
        return raza;
    }

    public void setRaza(Raza raza) {
        this.raza = raza;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public Integer getEdadAprox() {
        return edadAprox;
    }

    public void setEdadAprox(Integer edadAprox) {
        this.edadAprox = edadAprox;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }

    public EstadoVacunacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoVacunacion estado) {
        this.estado = estado;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = Propietario.vacioANull(observaciones);
    }

    /** En los combos se ve "Rocky (María Quispe Huamán)" para no confundir mascotas con el mismo nombre. */
    @Override
    public String toString() {
        return propietario == null || propietario.getNombre() == null ? nombre : nombre + " (" + propietario.getNombre() + ")";
    }
}
