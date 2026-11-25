package pe.edu.upeu.padronmascota.padronmascota.servicio;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Atencion;
import pe.edu.upeu.padronmascota.padronmascota.modelo.AtencionInsumo;

import java.util.List;

public interface IAtencionServicio {

    /** Guarda la atención con sus insumos (revisa stock) y marca la cita como Atendida. */
    Atencion registrar(Atencion a);

    void eliminar(int id);

    List<Atencion> buscar(String texto);

    List<AtencionInsumo> insumos(int idAtencion);

    /** Atenciones de las mascotas de un propietario (para la venta). */
    List<Atencion> dePropietario(int idPropietario);
}
