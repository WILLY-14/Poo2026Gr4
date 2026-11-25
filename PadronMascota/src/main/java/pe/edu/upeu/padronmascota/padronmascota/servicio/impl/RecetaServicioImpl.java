package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.modelo.DetalleReceta;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Receta;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.RecetaRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IRecetaServicio;

import java.util.List;

public class RecetaServicioImpl implements IRecetaServicio {

    private final RecetaRepositorio repo;

    public RecetaServicioImpl(RecetaRepositorio repo) {
        this.repo = repo;
    }

    @Override
    public Receta registrar(Receta r) {
        Validador.validar(r);   // atención, fecha y al menos un medicamento (@Valid revisa cada línea)
        if (r.getFecha().isBefore(r.getAtencion().getFecha())) {
            throw new IllegalStateException("La receta no puede ser anterior a la atención (" + r.getAtencion().getFecha() + ")");
        }
        return repo.registrar(r);
    }

    @Override
    public void eliminar(int id) {
        repo.eliminar(id);
    }

    @Override
    public List<Receta> buscar(String texto) {
        return repo.buscar(texto);
    }

    @Override
    public List<DetalleReceta> detalles(int idReceta) {
        return repo.detalles(idReceta);
    }
}
