package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoCita;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Cita;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.CitaRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.IRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.ICitaServicio;

import java.util.List;

public class CitaServicioImpl extends ServicioCrudBase<Cita> implements ICitaServicio {

    private final CitaRepositorio repo;

    public CitaServicioImpl(CitaRepositorio repo) {
        this.repo = repo;
    }

    @Override
    protected IRepositorio<Cita> repositorio() {
        return repo;
    }

    @Override
    public List<Cita> buscar(String texto, EstadoCita estado) {
        return repo.buscar(texto, estado);
    }

    /** UNIQUE(id_veterinario, fecha, hora): el doctor no puede tener dos citas a la misma hora. */
    @Override
    protected void validarNegocio(Cita c) {
        if (repo.horarioOcupado(c)) {
            errorEnCampo("hora", c.getVeterinario().getNombre() + " ya tiene una cita el " + c.getFecha() + " a las " + c.getHora());
        }
        if (!c.esNuevo() && obtener(c.getId()).getEstado() != EstadoCita.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden editar citas pendientes");
        }
    }

    @Override
    public void cancelar(int idCita) {
        Cita c = obtener(idCita);
        if (c.getEstado() != EstadoCita.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden cancelar citas pendientes (esta está " + c.getEstado() + ")");
        }
        repo.cambiarEstado(idCita, EstadoCita.CANCELADA);
    }

    @Override
    public List<Cita> pendientesDe(int idMascota) {
        return repo.buscar(null, EstadoCita.PENDIENTE).stream()
                .filter(c -> c.getMascota().getId() == idMascota)
                .toList();
    }

    @Override
    public void eliminar(int id) {
        obtener(id);
        repo.eliminar(id);   // si ya tiene atención, la atención se queda (sin cita) y la cita se borra
    }
}
