package pe.edu.upeu.padronmascota.padronmascota.servicio;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoVacunacion;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Mascota;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;

import java.util.List;

public interface IMascotaServicio extends IServicioCrud<Mascota> {

    List<Mascota> buscar(String texto, Especie especie, EstadoVacunacion estado, boolean soloActivas);

    /** Vuelve a activar una mascota dada de baja. */
    void reactivar(int id);

    /** [citas, vacunaciones] que se borrarían junto con la mascota. */
    int[] historialQueSeBorra(int id);

    /** Borra la mascota de verdad (con sus citas y vacunaciones). No se puede si tiene atenciones. */
    void eliminarDefinitivo(int id);

    List<Mascota> dePropietario(int idPropietario);

    /**
     * Registra una mascota con lo mínimo (nombre, especie y dueño) al sacar una cita.
     * Queda "por completar": raza, color, sexo y edad se llenan después en Mascotas.
     */
    Mascota registrarPorCompletar(String nombre, Especie especie, Propietario propietario);
}
