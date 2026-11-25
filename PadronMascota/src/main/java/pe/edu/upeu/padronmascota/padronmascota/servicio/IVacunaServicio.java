package pe.edu.upeu.padronmascota.padronmascota.servicio;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Vacuna;

import java.util.List;

public interface IVacunaServicio extends IServicioCrud<Vacuna> {

    List<Vacuna> buscar(String texto);

    /** Las vacunas de una especie (para el combo de Vacunación). */
    List<Vacuna> deEspecie(Especie especie);

    /**
     * La vacuna que corresponde a un producto de categoría Vacuna para esa especie.
     * Si no existe la crea (así ya no hace falta registrar vacunas aparte: salen de Productos).
     */
    Vacuna paraProducto(pe.edu.upeu.padronmascota.padronmascota.modelo.Producto producto, Especie especie);
}
