package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.UnidadMedida;

public class UnidadMedidaRepository extends AbstractJpaRepository<UnidadMedida, Long>{
    private long sequence=1;
    @Override
    protected Long getId(UnidadMedida entity) {
        return entity.getIdUnidad();
    }

    @Override
    protected void setId(UnidadMedida entity, Long id) {
        entity.setIdUnidad(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }

    public void seedData() {
        if (findAll().isEmpty()) {
            save(new UnidadMedida(generateId(), "Unidad"));
            save(new UnidadMedida(generateId(),"Kilo"));
        }
    }
}
