package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Vacuna;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.IRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.VacunaRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IVacunaServicio;

import java.util.List;

public class VacunaServicioImpl extends ServicioCrudBase<Vacuna> implements IVacunaServicio {

    private final VacunaRepositorio repo;

    public VacunaServicioImpl(VacunaRepositorio repo) {
        this.repo = repo;
    }

    @Override
    protected IRepositorio<Vacuna> repositorio() {
        return repo;
    }

    @Override
    public List<Vacuna> buscar(String texto) {
        return repo.buscar(texto);
    }

    @Override
    public List<Vacuna> deEspecie(Especie especie) {
        return repo.deEspecie(especie);
    }

    @Override
    public void eliminar(int id) {
        int n = repo.contarAplicaciones(id);
        if (n > 0) {
            throw new IllegalStateException("No se puede eliminar: ya se aplicó " + n + " vez/veces");
        }
        super.eliminar(id);
    }

    @Override
    public Vacuna paraProducto(pe.edu.upeu.padronmascota.padronmascota.modelo.Producto producto, Especie especie) {
        List<Vacuna> deEsa = repo.deEspecie(especie);
        // 1) la que ya está unida a ese producto
        for (Vacuna v : deEsa) {
            if (v.getProducto() != null && v.getProducto().getId() == producto.getId()) {
                return v;
            }
        }
        // 2) una antigua con el mismo nombre pero sin producto: se le une el producto
        for (Vacuna v : deEsa) {
            if (v.getNombre().equalsIgnoreCase(producto.getNombre())) {
                v.setProducto(producto);
                return modificar(v.getId(), v);
            }
        }
        // 3) nueva
        Vacuna nueva = new Vacuna();
        nueva.setNombre(producto.getNombre().length() > 60 ? producto.getNombre().substring(0, 60) : producto.getNombre());
        nueva.setEspecie(especie);
        nueva.setProducto(producto);
        return registrar(nueva);
    }
}
