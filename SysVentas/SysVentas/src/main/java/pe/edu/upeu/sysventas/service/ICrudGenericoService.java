package pe.edu.upeu.sysventas.service;

import java.util.List;

public interface ICrudGenericoService<T, ID> {
    T save(T entity);
    T update(ID id, T entity);
    List<T> findAll();
    T findById(ID id);
    void delete(ID id);
}
