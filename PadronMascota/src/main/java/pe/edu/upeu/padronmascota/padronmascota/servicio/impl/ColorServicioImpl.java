package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Color;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.ColorRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.IRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IColorServicio;

import java.util.List;

public class ColorServicioImpl extends ServicioCrudBase<Color> implements IColorServicio {

    private final ColorRepositorio repo;

    public ColorServicioImpl(ColorRepositorio repo) {
        this.repo = repo;
    }

    @Override
    protected IRepositorio<Color> repositorio() {
        return repo;
    }

    @Override
    public List<Color> buscar(String texto) {
        return repo.buscar(texto);
    }

    @Override
    protected void validarNegocio(Color c) {
        if (repo.existeNombre(c.getNombre(), c.getId())) {
            errorEnCampo("nombre", "Ya existe el color " + c.getNombre());
        }
    }

    @Override
    public void eliminar(int id) {
        List<String> usan = repo.mascotasQueLoUsan(id);
        if (!usan.isEmpty()) {
            Color c = obtener(id);
            throw new IllegalStateException("No se puede borrar el color " + c.getNombre() + " porque lo tienen "
                    + usan.size() + " mascota(s): " + String.join(", ", usan) + ".\n\n"
                    + "Primero ve a Mascotas y cámbiales el color (o borra esas mascotas). Luego ya podrás borrarlo.");
        }
        super.eliminar(id);
    }
}
