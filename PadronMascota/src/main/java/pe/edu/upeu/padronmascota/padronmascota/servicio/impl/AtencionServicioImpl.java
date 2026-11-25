package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoCita;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Atencion;
import pe.edu.upeu.padronmascota.padronmascota.modelo.AtencionInsumo;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Cita;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.AtencionRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.CitaRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IAtencionServicio;

import java.util.List;

public class AtencionServicioImpl implements IAtencionServicio {

    private final AtencionRepositorio repo;
    private final CitaRepositorio citaRepo;

    public AtencionServicioImpl(AtencionRepositorio repo, CitaRepositorio citaRepo) {
        this.repo = repo;
        this.citaRepo = citaRepo;
    }

    @Override
    public Atencion registrar(Atencion a) {
        Validador.validar(a);
        if (a.getCita() != null) {
            Cita cita = citaRepo.buscarPorId(a.getCita().getId())
                    .orElseThrow(() -> new IllegalStateException("No existe la cita"));
            if (cita.getEstado() != EstadoCita.PENDIENTE) {
                throw new IllegalStateException("La cita ya está " + cita.getEstado());
            }
            if (cita.getMascota().getId() != a.getMascota().getId()) {
                throw new IllegalStateException("La cita es de otra mascota (" + cita.getMascota().getNombre() + ")");
            }
        }
        return repo.registrar(a);   // transacción: atención + insumos (stock) + cita Atendida
    }

    @Override
    public void eliminar(int id) {
        repo.eliminar(id);   // borra también sus recetas e insumos; las ventas se quedan sin atención
    }

    @Override
    public List<Atencion> buscar(String texto) {
        return repo.buscar(texto);
    }

    @Override
    public List<AtencionInsumo> insumos(int idAtencion) {
        return repo.insumos(idAtencion);
    }

    @Override
    public List<Atencion> dePropietario(int idPropietario) {
        return repo.dePropietario(idPropietario);
    }
}
