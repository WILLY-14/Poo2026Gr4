package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Una línea de la compra. lote y fecha_vencimiento son opcionales (los accesorios no vencen). */
public class DetalleCompra extends Entidad {

    public static final int DIAS_POR_VENCER = 60;

    @NotNull(message = "Elige el producto")
    private Producto producto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;

    @NotNull(message = "El costo es obligatorio")
    @DecimalMin(value = "0.00", message = "El costo no puede ser negativo")
    private BigDecimal costoUnitario;

    @Size(max = 30, message = "El lote puede tener hasta 30 letras")
    private String lote;

    private LocalDate fechaVencimiento;

    public DetalleCompra() {
    }

    public DetalleCompra(Producto producto, int cantidad, BigDecimal costoUnitario, String lote, LocalDate fechaVencimiento) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
        this.lote = Propietario.vacioANull(lote);
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getSubtotal() {
        if (costoUnitario == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return costoUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    /** VENCIDO, POR VENCER (60 días o menos) u OK. Sin fecha = no vence. */
    public String getEstadoLote() {
        if (fechaVencimiento == null) {
            return "-";
        }
        LocalDate hoy = LocalDate.now();
        if (fechaVencimiento.isBefore(hoy)) {
            return "VENCIDO";
        }
        return fechaVencimiento.isAfter(hoy.plusDays(DIAS_POR_VENCER)) ? "OK" : "POR VENCER";
    }

    public Producto getProducto() {
        return producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }

    public String getLote() {
        return lote;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
}
