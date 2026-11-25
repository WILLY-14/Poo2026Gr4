package pe.edu.upeu.padronmascota.padronmascota.servicio;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Compra;
import pe.edu.upeu.padronmascota.padronmascota.modelo.DetalleCompra;

import java.util.List;

public interface ICompraServicio {

    /** Valida, pone fecha y usuario, y guarda la compra con sus líneas (el stock sube solo). */
    Compra registrar(Compra c);

    void eliminar(int id);

    List<Compra> buscar(String texto);

    List<DetalleCompra> detalles(int idCompra);
}
