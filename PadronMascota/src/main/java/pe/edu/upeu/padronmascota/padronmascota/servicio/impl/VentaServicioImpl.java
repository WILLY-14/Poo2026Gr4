package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoVenta;
import pe.edu.upeu.padronmascota.padronmascota.modelo.DetalleVenta;
import pe.edu.upeu.padronmascota.padronmascota.modelo.ItemVenta;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Producto;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Venta;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.ProductoRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.ServicioVeterinarioRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.VentaRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.seguridad.Sesion;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IVentaServicio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VentaServicioImpl implements IVentaServicio {

    private final VentaRepositorio ventaRepo;
    private final ProductoRepositorio productoRepo;
    private final ServicioVeterinarioRepositorio servicioRepo;

    public VentaServicioImpl(VentaRepositorio ventaRepo, ProductoRepositorio productoRepo,
                             ServicioVeterinarioRepositorio servicioRepo) {
        this.ventaRepo = ventaRepo;
        this.productoRepo = productoRepo;
        this.servicioRepo = servicioRepo;
    }

    @Override
    public Venta registrar(Venta venta) {
        Validador.validar(venta);   // @NotNull, @NotEmpty, y cada detalle (@Valid)
        if (!Sesion.activa()) {
            throw new IllegalStateException("Inicia sesión para registrar ventas");
        }
        venta.setFechaHora(LocalDateTime.now());
        venta.setUsuario(Sesion.usuario());
        venta.setEstado(EstadoVenta.PAGADA);
        if (venta.getTipoComprobante() == pe.edu.upeu.padronmascota.padronmascota.enums.TipoComprobante.FACTURA
                && (venta.getRucCliente() == null || !venta.getRucCliente().matches("\\d{11}"))) {
            throw new pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException(
                    java.util.Map.of("rucCliente", "Para factura escribe el RUC (11 dígitos)"));
        }
        if (venta.getAtencion() != null && venta.getAtencion().getMascota() != null
                && venta.getAtencion().getMascota().getPropietario() != null
                && venta.getAtencion().getMascota().getPropietario().getId() != venta.getPropietario().getId()) {
            throw new IllegalStateException("La atención elegida es de una mascota de otro propietario");
        }
        return ventaRepo.registrar(venta);   // el repositorio revisa el stock dentro de la transacción
    }

    @Override
    public void anular(int idVenta) {
        Venta v = ventaRepo.buscarPorId(idVenta)
                .orElseThrow(() -> new IllegalStateException("No existe la venta " + idVenta));
        if (v.getEstado() == EstadoVenta.ANULADA) {
            throw new IllegalStateException("La venta " + v.getNumeroComprobante() + " ya está anulada");
        }
        ventaRepo.anular(idVenta);
    }

    @Override
    public List<Venta> buscar(String texto, EstadoVenta estado) {
        return ventaRepo.buscar(texto, estado);
    }

    @Override
    public List<DetalleVenta> detalles(int idVenta) {
        return ventaRepo.detalles(idVenta);
    }

    @Override
    public List<DetalleVenta> pendientes(int idPropietario) {
        return ventaRepo.pendientes(idPropietario);
    }

    @Override
    public List<ItemVenta> itemsVendibles() {
        // POLIMORFISMO: en la misma lista van Productos y Servicios (los dos son ItemVenta)
        List<ItemVenta> items = new ArrayList<>();
        for (Producto p : productoRepo.listar()) {
            if (p.getStock() > 0) {
                items.add(p);
            }
        }
        // Los servicios (baño, consulta...) ya no se sugieren: su precio lo pone el doctor
        return items;
    }
}
