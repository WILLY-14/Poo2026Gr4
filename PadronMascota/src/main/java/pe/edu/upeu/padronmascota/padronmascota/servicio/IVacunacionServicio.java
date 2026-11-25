package pe.edu.upeu.padronmascota.padronmascota.servicio;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Vacunacion;

import java.util.List;

/**
 * Vacunas aplicadas. registrar descuenta 1 dosis del stock (si la vacuna tiene producto).
 * Al modificar solo se corrigen las fechas.
 */
public interface IVacunacionServicio extends IServicioCrud<Vacunacion> {

    List<Vacunacion> buscar(String texto, boolean soloVencidas);
}
