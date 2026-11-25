package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.excepcion.NoEncontradoException;
import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Entidad;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.IRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;

import java.util.List;

/**
 * Implementación común para todos los servicios CRUD (HERENCIA).
 * Antes de guardar, revisa las anotaciones del modelo (@NotBlank, @Pattern, @Min...).
 * Cada hija solo dice cuál es su repositorio.
 */
public abstract class ServicioCrudBase<T extends Entidad> implements IServicioCrud<T> {

    protected abstract IRepositorio<T> repositorio();

    @Override
    public T registrar(T obj) {
        validar(obj);
        validarNegocio(obj);
        return repositorio().guardar(obj);
    }

    @Override
    public T modificar(int id, T obj) {
        obtener(id); // si no existe, lanza NoEncontradoException
        validar(obj);
        validarNegocio(obj);
        return repositorio().actualizar(obj);
    }

    @Override
    public void eliminar(int id) {
        obtener(id);
        repositorio().eliminar(id);
    }

    @Override
    public T obtener(int id) {
        return repositorio().buscarPorId(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el registro con id " + id));
    }

    @Override
    public List<T> listarTodos() {
        return repositorio().listar();
    }

    /** Si el objeto tiene errores, lanza ValidacionException con un mensaje por campo. */
    protected void validar(T obj) {
        Validador.validar(obj);
    }

    /**
     * Reglas que necesitan la base de datos (por ejemplo "DNI repetido").
     * Cada hija la sobrescribe si la necesita (POLIMORFISMO). Por defecto no hace nada.
     */
    protected void validarNegocio(T obj) {
    }

    /** Lanza el error de un solo campo, para que la pantalla lo marque en rojo. */
    protected static void errorEnCampo(String campo, String mensaje) {
        throw new ValidacionException(java.util.Map.of(campo, mensaje));
    }
}
