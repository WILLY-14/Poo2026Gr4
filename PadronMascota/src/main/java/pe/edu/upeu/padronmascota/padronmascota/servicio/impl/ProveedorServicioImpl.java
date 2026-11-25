package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Proveedor;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.IRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.ProveedorRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IProveedorServicio;

import java.util.List;

public class ProveedorServicioImpl extends ServicioCrudBase<Proveedor> implements IProveedorServicio {

    private final ProveedorRepositorio repo;

    public ProveedorServicioImpl(ProveedorRepositorio repo) {
        this.repo = repo;
    }

    @Override
    protected IRepositorio<Proveedor> repositorio() {
        return repo;
    }

    @Override
    public List<Proveedor> buscar(String texto) {
        return repo.buscar(texto);
    }

    /** UNIQUE(ruc). */
    @Override
    protected void validarNegocio(Proveedor p) {
        if (repo.existeRuc(p.getRuc(), p.getId())) {
            errorEnCampo("ruc", "Ya existe un proveedor con el RUC " + p.getRuc());
        }
    }

    @Override
    public void eliminar(int id) {
        int compras = obtener(id).getNumeroCompras();
        if (compras > 0) {
            throw new IllegalStateException("No se puede eliminar: tiene " + compras + " compra(s)");
        }
        super.eliminar(id);
    }
}
