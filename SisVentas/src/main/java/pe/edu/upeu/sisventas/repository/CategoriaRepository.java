package pe.edu.upeu.sisventas.repository;

import pe.edu.upeu.sisventas.model.Categoria;

public class CategoriaRepository extends AbstractJpaRepository<Categoria, Long> {
    private long sequence = 1;
    @Override
    protected Long getId(Categoria entity) {
        return entity.getIdCategoria();
    }

    @Override
    protected void setId(Categoria entity, Long id) {
        entity.setIdCategoria(id);
    }

    @Override
    protected Long generateid() {
        return sequence++;
    }
}
