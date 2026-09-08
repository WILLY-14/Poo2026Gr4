package pe.edu.upeu.sisventas.enums;

import lombok.Getter;

@Getter
public enum TipoProducto {
    PRODUCTO("Producto"),
    PREPARADO("Preparado"),
    SERVICIO("Servicio"),;

    String descripcion;
    TipoProducto(String descripcion) {
        this.descripcion = descripcion;
    }
}