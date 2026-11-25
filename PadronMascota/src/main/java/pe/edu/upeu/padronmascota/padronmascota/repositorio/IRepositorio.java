package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import java.util.List;
import java.util.Optional;

/**
 * ABSTRACCIÓN: las operaciones que todo repositorio CRUD debe tener.
 * <T> es el tipo de objeto del modelo (Mascota, Propietario...).
 */
public interface IRepositorio<T> {

    T guardar(T obj);

    T actualizar(T obj);

    void eliminar(int id);

    Optional<T> buscarPorId(int id);

    List<T> listar();
}
