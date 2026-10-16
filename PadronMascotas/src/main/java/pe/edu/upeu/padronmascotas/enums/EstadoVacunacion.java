package pe.edu.upeu.padronmascotas.enums;

import lombok.Getter;

@Getter
public enum EstadoVacunacion {
    AL_DIA("Al día"),
    PENDIENTE("Pendiente");

    String descripcion;
    EstadoVacunacion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}