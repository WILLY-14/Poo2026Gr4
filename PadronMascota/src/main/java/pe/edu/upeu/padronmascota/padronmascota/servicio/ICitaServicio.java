package pe.edu.upeu.padronmascota.padronmascota.servicio;

import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoCita;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Cita;

import java.util.List;

public interface ICitaServicio extends IServicioCrud<Cita> {

    List<Cita> buscar(String texto, EstadoCita estado);

    void cancelar(int idCita);

    /** Citas pendientes de una mascota (para elegir la cita al registrar la atención). */
    List<Cita> pendientesDe(int idMascota);
}
