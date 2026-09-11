package pe.edu.upeu.sisventas.repository;

import pe.edu.upeu.sisventas.model.Marca;

public class MarcaRepository extends AbstractJpaRepository<Marca,Long>{
    private long sequence = 1;
    @Override
    protected Long getId(Marca entity) {
        return entity.getIdMarca();
    }

    @Override
    protected void setId(Marca entity, Long id) {
        entity.setIdMarca(id);
    }

    @Override
    protected Long generateid() {
        return sequence++;
    }
}
