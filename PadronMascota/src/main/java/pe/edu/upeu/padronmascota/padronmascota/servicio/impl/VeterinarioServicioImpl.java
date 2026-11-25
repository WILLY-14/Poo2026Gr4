package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Veterinario;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.IRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.VeterinarioRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IVeterinarioServicio;

import java.util.List;

public class VeterinarioServicioImpl extends ServicioCrudBase<Veterinario> implements IVeterinarioServicio {

    private final VeterinarioRepositorio repo;

    public VeterinarioServicioImpl(VeterinarioRepositorio repo) {
        this.repo = repo;
    }

    @Override
    protected IRepositorio<Veterinario> repositorio() {
        return repo;
    }

    @Override
    public List<Veterinario> buscar(String texto) {
        return repo.buscar(texto);
    }

    @Override
    public void eliminar(int id) {
        obtener(id); // si no existe lanza NoEncontradoException
        if (repo.contarUsos(id) > 0) {
            repo.darDeBaja(id);   // tiene historial: no se borra, se da de baja
        } else {
            super.eliminar(id);   // nunca trabajó: se borra de verdad
        }
    }
}
