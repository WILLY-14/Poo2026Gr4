package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Lo que el doctor usó en la atención (jeringa, algodón...). No se cobra aparte, pero descuenta stock. */
public class AtencionInsumo extends Entidad {

    @NotNull(message = "Elige el producto")
    private Producto producto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;

    public AtencionInsumo() {
    }

    public AtencionInsumo(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
