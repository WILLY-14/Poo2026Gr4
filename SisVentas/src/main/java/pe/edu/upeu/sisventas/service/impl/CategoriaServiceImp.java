package pe.edu.upeu.sisventas.service.impl;

import pe.edu.upeu.sisventas.model.Categoria;
import pe.edu.upeu.sisventas.repository.CategoriaRepository;
import pe.edu.upeu.sisventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sisventas.service.ICategoriaService;

public class CategoriaServiceImp extends CrudGenericoServiceImp<Categoria, Long> implements ICategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImp(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    protected ICrudGenericoRepository<Categoria, Long> getRepo() {
        return categoriaRepository;
    }
}
