package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.enums.CategoriaProducto;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Producto;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.IRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.ProductoRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IProductoServicio;

import java.util.List;

public class ProductoServicioImpl extends ServicioCrudBase<Producto> implements IProductoServicio {

    private final ProductoRepositorio repo;

    public ProductoServicioImpl(ProductoRepositorio repo) {
        this.repo = repo;
    }

    @Override
    protected IRepositorio<Producto> repositorio() {
        return repo;
    }

    @Override
    public List<Producto> buscar(String texto, CategoriaProducto categoria) {
        return repo.buscar(texto, categoria);
    }

    @Override
    protected void validarNegocio(Producto p) {
        if (repo.existeNombre(p.getNombre(), p.getId())) {
            errorEnCampo("nombre", "Ya existe el producto " + p.getNombre());
        }
    }

    @Override
    public void eliminar(int id) {
        int usos = repo.contarUsos(id);
        if (usos > 0) {
            throw new IllegalStateException("No se puede eliminar: aparece en " + usos + " compra(s), venta(s), atención(es) o receta(s)");
        }
        super.eliminar(id);
    }
}
