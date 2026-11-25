package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoVacunacion;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Mascota;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;
import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.IRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.MascotaRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IMascotaServicio;

import java.util.List;

public class MascotaServicioImpl extends ServicioCrudBase<Mascota> implements IMascotaServicio {

    private final MascotaRepositorio repo;

    // El repositorio llega por el constructor: lo crea el ContenedorApp
    public MascotaServicioImpl(MascotaRepositorio repo) {
        this.repo = repo;
    }

    @Override
    protected IRepositorio<Mascota> repositorio() {
        return repo;
    }

    @Override
    public List<Mascota> buscar(String texto, Especie especie, EstadoVacunacion estado, boolean soloActivas) {
        return repo.buscar(texto, especie, estado, soloActivas);
    }

    /** Borrado lógico: la mascota no se borra, se marca activo = No (el repositorio hace el UPDATE). */
    @Override
    public void eliminar(int id) {
        super.eliminar(id);
    }

    @Override
    public int[] historialQueSeBorra(int id) {
        return repo.contarCitasYVacunas(id);
    }

    @Override
    public void eliminarDefinitivo(int id) {
        obtener(id);   // si no existe, avisa
        repo.borrarDefinitivo(id);   // borra también su historial (las ventas se conservan)
    }

    @Override
    public void reactivar(int id) {
        obtener(id);
        repo.reactivar(id);
    }

    @Override
    public List<Mascota> dePropietario(int idPropietario) {
        return repo.dePropietario(idPropietario);
    }

    @Override
    public Mascota registrarPorCompletar(String nombre, Especie especie, Propietario propietario) {
        java.util.Map<String, String> errores = new java.util.LinkedHashMap<>();
        if (nombre == null || nombre.isBlank()) {
            errores.put("mascota", "Escribe el nombre de la mascota");
        }
        if (especie == null) {
            errores.put("especie", "Elige la especie de la mascota nueva");
        }
        if (propietario == null) {
            errores.put("propietario", "Escribe el dueño de la mascota nueva");
        }
        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }
        Mascota m = new Mascota();
        m.setNombre(nombre.trim());
        m.setEspecie(especie);
        m.setPropietario(propietario);
        m.setEdadAprox(0);
        m.setFechaRegistro(java.time.LocalDate.now());
        m.setEstado(EstadoVacunacion.PENDIENTE);
        m.setObservaciones("POR COMPLETAR: registrada al sacar una cita (falta raza, color, sexo y edad)");
        return repo.guardar(m);   // sin validar raza/color/sexo: se completan después en Mascotas
    }
}
