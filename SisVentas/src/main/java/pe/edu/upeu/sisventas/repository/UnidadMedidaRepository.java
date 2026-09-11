package pe.edu.upeu.sisventas.repository;

import pe.edu.upeu.sisventas.model.UnidMedida;

public class UnidadMedidaRepository extends AbstractJpaRepository<UnidMedida, Long>{
    private long sequence = 1;
    @Override
    protected Long getId(UnidMedida entity) {
        return entity.getIdUnidad();
    }

    @Override
    protected void setId(UnidMedida entity, Long id) {
        entity.setIdUnidad(id);
    }

    @Override
    protected Long generateid() {
        return sequence++;
    }
}
