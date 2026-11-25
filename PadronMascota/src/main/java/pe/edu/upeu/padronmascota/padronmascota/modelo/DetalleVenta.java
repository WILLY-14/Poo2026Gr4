package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** Una línea de la venta: qué se vendió, cuántos y a qué precio (tabla detalle_venta). */
public class DetalleVenta extends Entidad {

    // POLIMORFISMO: puede ser un Producto o un ServicioVeterinario
    @NotNull(message = "Elige un producto o servicio")
    private ItemVenta item;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;

    // Precio al momento de la venta (si luego cambia el precio del producto, la venta no cambia)
    @NotNull
    private BigDecimal precio;

    // Si la línea vino de lo pendiente del cliente (consulta o vacuna ya hecha)
    private Integer idAtencion;
    private Integer idVacunacion;
    private boolean yaDescontado;   // la vacuna ya bajó del stock al aplicarse
    private String descripcion;     // "Consulta - Kiwi" (solo para mostrar)

    public boolean esPendiente() {
        return idAtencion != null || idVacunacion != null;
    }

    public String getDescripcion() {
        return descripcion != null ? descripcion : item.getNombre();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(Integer idAtencion) {
        this.idAtencion = idAtencion;
    }

    public Integer getIdVacunacion() {
        return idVacunacion;
    }

    public void setIdVacunacion(Integer idVacunacion) {
        this.idVacunacion = idVacunacion;
    }

    public boolean isYaDescontado() {
        return yaDescontado;
    }

    public void setYaDescontado(boolean yaDescontado) {
        this.yaDescontado = yaDescontado;
    }

    public DetalleVenta() {
    }

    public DetalleVenta(ItemVenta item, int cantidad) {
        this.item = item;
        this.cantidad = cantidad;
        this.precio = item.getPrecio();
    }

    public BigDecimal getSubtotal() {
        if (precio == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }

    public ItemVenta getItem() {
        return item;
    }

    public void setItem(ItemVenta item) {
        this.item = item;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
