package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.excepcion.NoEncontradoException;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Vacuna;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Vacunacion;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.VacunaRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.VacunacionRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IVacunacionServicio;

import java.util.List;

public class VacunacionServicioImpl implements IVacunacionServicio {

    private final VacunacionRepositorio repo;
    private final VacunaRepositorio vacunaRepo;

    public VacunacionServicioImpl(VacunacionRepositorio repo, VacunaRepositorio vacunaRepo) {
        this.repo = repo;
        this.vacunaRepo = vacunaRepo;
    }

    @Override
    public Vacunacion registrar(Vacunacion v) {
        Validador.validar(v);   // fechas y que la vacuna sea de la especie de la mascota
        Vacuna vacuna = vacunaRepo.buscarPorId(v.getVacuna().getId())
                .orElseThrow(() -> new IllegalStateException("No existe la vacuna"));
        Integer idDosis = vacuna.getProducto() == null ? null : vacuna.getProducto().getId();
        String nombreDosis = vacuna.getProducto() == null ? null : vacuna.getProducto().getNombre();
        return repo.registrar(v, idDosis, nombreDosis);   // revisa el stock de la dosis en la transacción
    }

    @Override
    public Vacunacion modificar(int id, Vacunacion v) {
        Vacunacion antes = obtener(id);
        if (antes.getMascota().getId() != v.getMascota().getId() || antes.getVacuna().getId() != v.getVacuna().getId()) {
            throw new IllegalStateException("Solo se pueden corregir las fechas. Para otra mascota o vacuna, elimina y registra de nuevo.");
        }
        Validador.validar(v);
        repo.actualizar(v);
        return v;
    }

    @Override
    public void eliminar(int id) {
        obtener(id);
        repo.eliminar(id);
    }

    @Override
    public Vacunacion obtener(int id) {
        return repo.buscarPorId(id).orElseThrow(() -> new NoEncontradoException("No existe la vacunación " + id));
    }

    @Override
    public List<Vacunacion> listarTodos() {
        return repo.buscar(null, false);
    }

    @Override
    public List<Vacunacion> buscar(String texto, boolean soloVencidas) {
        return repo.buscar(texto, soloVencidas);
    }
}
