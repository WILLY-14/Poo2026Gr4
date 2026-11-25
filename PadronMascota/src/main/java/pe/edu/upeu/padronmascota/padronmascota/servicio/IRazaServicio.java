package pe.edu.upeu.padronmascota.padronmascota.servicio;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Raza;

import java.util.List;

public interface IRazaServicio extends IServicioCrud<Raza> {

    List<Raza> buscar(String texto, Especie especie);

    /** Las razas de una especie (para el combo de Mascotas). */
    List<Raza> deEspecie(Especie especie);
}
