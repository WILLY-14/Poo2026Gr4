package pe.edu.upeu.padronmascota.padronmascota.modelo;

import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoVenta;
import pe.edu.upeu.padronmascota.padronmascota.enums.MetodoPago;
import pe.edu.upeu.padronmascota.padronmascota.enums.TipoComprobante;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Una venta (tabla venta) con sus líneas (tabla detalle_venta).
 * COMPOSICIÓN: la venta TIENE un propietario, un método de pago y una lista de detalles.
 */
public class Venta extends Entidad {

    @NotNull(message = "Elige el tipo de comprobante")
    private TipoComprobante tipoComprobante;

    private String numeroComprobante;          // lo genera el repositorio: B001-000010

    @NotNull(message = "Elige el cliente (propietario)")
    private Propietario propietario;

    private Atencion atencion;                 // opcional: vacío = solo productos

    private LocalDateTime fechaHora;

    @NotNull(message = "Elige el método de pago")
    private MetodoPago metodoPago;

    private Usuario usuario;                   // quién registró la venta

    private EstadoVenta estado = EstadoVenta.PAGADA;

    private BigDecimal totalGuardado;

    private String rucCliente;                 // solo factura (11 dígitos)

    public String getRucCliente() {
        return rucCliente;
    }

    public void setRucCliente(String rucCliente) {
        this.rucCliente = rucCliente == null || rucCliente.isBlank() ? null : rucCliente.trim();
    }          // el total que vino de la BD (para el historial)

    @Valid
    @NotEmpty(message = "Agrega al menos un producto o servicio")
    private final List<DetalleVenta> detalles = new ArrayList<>();

    /** Agrega un item. Si ya estaba en la venta, suma la cantidad en la misma línea. */
    public void agregar(ItemVenta item, int cantidad) {
        agregar(item, cantidad, item.getPrecio());
    }

    /** Agrega con el precio que puso quien cobra (si ya está con el mismo precio, suma la cantidad). */
    public void agregar(ItemVenta item, int cantidad, java.math.BigDecimal precio) {
        for (DetalleVenta d : detalles) {
            if (!d.esPendiente() && d.getItem().esMismo(item) && d.getPrecio().compareTo(precio) == 0) {
                d.setCantidad(d.getCantidad() + cantidad);
                return;
            }
        }
        DetalleVenta nuevo = new DetalleVenta(item, cantidad);
        nuevo.setPrecio(precio);
        detalles.add(nuevo);
    }

    /** Cambia lo pendiente (consultas y vacunas sin cobrar) por lo del cliente nuevo. */
    public void ponerPendientes(List<DetalleVenta> pendientes) {
        detalles.removeIf(DetalleVenta::esPendiente);
        detalles.addAll(0, pendientes);
    }

    public void quitar(DetalleVenta detalle) {
        detalles.remove(detalle);
    }

    /** Suma de los subtotales. Si la venta vino de la BD sin detalles, usa el total guardado. */
    public BigDecimal getTotal() {
        if (detalles.isEmpty() && totalGuardado != null) {
            return totalGuardado;
        }
        return detalles.stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public TipoComprobante getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(TipoComprobante tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }

    public Atencion getAtencion() {
        return atencion;
    }

    public void setAtencion(Atencion atencion) {
        this.atencion = atencion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }

    public void setTotalGuardado(BigDecimal totalGuardado) {
        this.totalGuardado = totalGuardado;
    }
}
