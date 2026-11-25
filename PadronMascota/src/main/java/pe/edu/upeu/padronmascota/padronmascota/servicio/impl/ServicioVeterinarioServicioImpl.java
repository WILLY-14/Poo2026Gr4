package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.modelo.ServicioVeterinario;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.IRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.ServicioVeterinarioRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioVeterinarioServicio;

import java.util.List;

public class ServicioVeterinarioServicioImpl extends ServicioCrudBase<ServicioVeterinario>
        implements IServicioVeterinarioServicio {

    private final ServicioVeterinarioRepositorio repo;

    public ServicioVeterinarioServicioImpl(ServicioVeterinarioRepositorio repo) {
        this.repo = repo;
    }

    @Override
    protected IRepositorio<ServicioVeterinario> repositorio() {
        return repo;
    }

    @Override
    public List<ServicioVeterinario> buscar(String texto) {
        return repo.buscar(texto);
    }

    @Override
    public void eliminar(int id) {
        int ventas = repo.contarVentas(id);
        if (ventas > 0) {
            throw new IllegalStateException("No se puede eliminar: aparece en " + ventas + " venta(s)");
        }
        super.eliminar(id);
    }
}
