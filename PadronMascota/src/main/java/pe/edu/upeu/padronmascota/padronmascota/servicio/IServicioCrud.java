package pe.edu.upeu.padronmascota.padronmascota.servicio;

import java.util.List;

/**
 * Lo que cualquier servicio CRUD ofrece a las pantallas.
 * El controlador solo conoce esta interfaz, no sabe cómo está hecha por dentro.
 */
public interface IServicioCrud<T> {

    T registrar(T obj);

    T modificar(int id, T obj);

    void eliminar(int id);

    T obtener(int id);

    List<T> listarTodos();
}
