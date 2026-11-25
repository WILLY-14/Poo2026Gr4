package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.IRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.PropietarioRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IPropietarioServicio;

import java.util.List;

public class PropietarioServicioImpl extends ServicioCrudBase<Propietario> implements IPropietarioServicio {

    private final PropietarioRepositorio repo;

    public PropietarioServicioImpl(PropietarioRepositorio repo) {
        this.repo = repo;
    }

    @Override
    protected IRepositorio<Propietario> repositorio() {
        return repo;
    }

    @Override
    public List<Propietario> buscar(String texto) {
        return repo.buscar(texto);
    }

    /** dni: 8 dígitos y UNIQUE. */
    @Override
    protected void validarNegocio(Propietario p) {
        if (repo.existeDni(p.getDni(), p.getId())) {
            errorEnCampo("dni", "Ya existe un propietario con el DNI " + p.getDni());
        }
    }

    /**
     * Eliminar un propietario:
     *  - Si NO tiene ventas ni atenciones: se borra de verdad, junto con sus mascotas (y sus citas y vacunaciones).
     *  - Si tiene ventas o atenciones: se da de baja (él y sus mascotas dejan de salir) para no perder las cuentas.
     */
    @Override
    public void eliminar(int id) {
        obtener(id); // si no existe lanza NoEncontradoException
        if (repo.contarVentas(id) > 0 || repo.mascotasTienenAtenciones(id)) {
            repo.darDeBaja(id);
        } else {
            repo.borrarConMascotas(id);
        }
    }
}
