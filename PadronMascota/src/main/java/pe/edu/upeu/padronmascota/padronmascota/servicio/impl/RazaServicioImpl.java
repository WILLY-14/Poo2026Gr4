package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Raza;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.IRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.RazaRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IRazaServicio;

import java.util.List;

public class RazaServicioImpl extends ServicioCrudBase<Raza> implements IRazaServicio {

    private final RazaRepositorio repo;

    public RazaServicioImpl(RazaRepositorio repo) {
        this.repo = repo;
    }

    @Override
    protected IRepositorio<Raza> repositorio() {
        return repo;
    }

    @Override
    public List<Raza> buscar(String texto, Especie especie) {
        return repo.buscar(texto, especie);
    }

    @Override
    public List<Raza> deEspecie(Especie especie) {
        return repo.buscar(null, especie);
    }

    @Override
    protected void validarNegocio(Raza r) {
        if (repo.existe(r.getNombre(), r.getEspecie(), r.getId())) {
            errorEnCampo("nombre", "Ya existe la raza " + r.getNombre() + " para " + r.getEspecie());
        }
    }

    @Override
    public void eliminar(int id) {
        List<String> usan = repo.mascotasQueLaUsan(id);
        if (!usan.isEmpty()) {
            Raza r = obtener(id);
            throw new IllegalStateException("No se puede borrar la raza " + r.getNombre() + " porque la tienen "
                    + usan.size() + " mascota(s): " + String.join(", ", usan) + ".\n\n"
                    + "Primero ve a Mascotas y cámbiales la raza (o borra esas mascotas). Luego ya podrás borrarla.");
        }
        super.eliminar(id);
    }
}
