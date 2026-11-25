package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Compra a un proveedor (tabla compra + detalle_compra). Solo productos, nunca servicios. */
public class Compra extends Entidad {

    @NotNull(message = "Elige el proveedor")
    private Proveedor proveedor;

    private LocalDateTime fechaHora;

    @NotBlank(message = "El número de factura es obligatorio")
    @Size(max = 20, message = "El número de factura puede tener hasta 20 letras")
    private String numeroFactura;

    private Usuario usuario;          // quién la registró
    private BigDecimal totalGuardado; // el total que vino de la BD
    private int numeroLineas;

    @Valid
    @NotEmpty(message = "Agrega al menos un producto")
    private final List<DetalleCompra> detalles = new ArrayList<>();

    /** Compra.total = suma de los subtotales de su detalle (no se escribe a mano). */
    public BigDecimal getTotal() {
        if (detalles.isEmpty() && totalGuardado != null) {
            return totalGuardado;
        }
        return detalles.stream().map(DetalleCompra::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<DetalleCompra> getDetalles() {
        return detalles;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura == null ? null : numeroFactura.trim().toUpperCase();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setTotalGuardado(BigDecimal totalGuardado) {
        this.totalGuardado = totalGuardado;
    }

    public int getNumeroLineas() {
        return detalles.isEmpty() ? numeroLineas : detalles.size();
    }

    public void setNumeroLineas(int numeroLineas) {
        this.numeroLineas = numeroLineas;
    }
}
