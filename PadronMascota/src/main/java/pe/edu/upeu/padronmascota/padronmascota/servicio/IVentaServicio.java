package pe.edu.upeu.padronmascota.padronmascota.servicio;

import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoVenta;
import pe.edu.upeu.padronmascota.padronmascota.modelo.DetalleVenta;
import pe.edu.upeu.padronmascota.padronmascota.modelo.ItemVenta;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Venta;

import java.util.List;

public interface IVentaServicio {

    /** Valida, pone fecha y usuario, guarda la venta y revisa el stock. */
    Venta registrar(Venta venta);

    /** Anula una venta pagada (deja de contar en el stock y en los totales). */
    void anular(int idVenta);

    List<Venta> buscar(String texto, EstadoVenta estado);

    List<DetalleVenta> detalles(int idVenta);

    /** Lo que se puede vender: productos con stock y todos los servicios. */
    List<ItemVenta> itemsVendibles();

    /** Consultas y vacunas del cliente que faltan cobrar. */
    List<DetalleVenta> pendientes(int idPropietario);
}
