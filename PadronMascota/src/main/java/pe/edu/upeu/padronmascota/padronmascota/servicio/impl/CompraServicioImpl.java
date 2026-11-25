package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Compra;
import pe.edu.upeu.padronmascota.padronmascota.modelo.DetalleCompra;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.CompraRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.seguridad.Sesion;
import pe.edu.upeu.padronmascota.padronmascota.servicio.ICompraServicio;

import java.time.LocalDateTime;
import java.util.List;

public class CompraServicioImpl implements ICompraServicio {

    private final CompraRepositorio repo;

    public CompraServicioImpl(CompraRepositorio repo) {
        this.repo = repo;
    }

    @Override
    public Compra registrar(Compra c) {
        Validador.validar(c);
        if (!Sesion.activa()) {
            throw new IllegalStateException("Inicia sesión para registrar compras");
        }
        if (repo.existeFactura(c.getProveedor().getId(), c.getNumeroFactura())) {
            throw new IllegalStateException("La factura " + c.getNumeroFactura() + " de ese proveedor ya está registrada");
        }
        c.setFechaHora(LocalDateTime.now());
        c.setUsuario(Sesion.usuario());
        return repo.registrar(c);
    }

    @Override
    public void eliminar(int id) {
        repo.eliminar(id);   // si deja stock negativo, el repositorio hace rollback
    }

    @Override
    public List<Compra> buscar(String texto) {
        return repo.buscar(texto);
    }

    @Override
    public List<DetalleCompra> detalles(int idCompra) {
        return repo.detalles(idCompra);
    }
}
