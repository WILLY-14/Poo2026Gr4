package pe.edu.upeu.padronmascota.padronmascota.servicio;


import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;

import java.util.List;

public interface IPropietarioServicio extends IServicioCrud<Propietario> {

    /** Busca por nombre, DNI o teléfono. */
    List<Propietario> buscar(String texto);
}
