package pe.edu.upeu.padronmascotas.enums;

import lombok.Getter;

@Getter
public enum Especie {
    PERRO("Perro"),
    GATO("Gato"),
    OTRO("Otro");

    String descripcion;
    Especie(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}