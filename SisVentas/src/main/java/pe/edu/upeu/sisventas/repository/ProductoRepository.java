package pe.edu.upeu.sisventas.repository;


import pe.edu.upeu.sisventas.model.Producto;

public class ProductoRepository extends AbstractJpaRepository<Producto, Long>{
    private long sequence =1;
    @Override
    protected Long getId(Producto entity) {
        return entity.getIdProducto();
    }

    @Override
    protected void setId(Producto entity, Long id) {
        entity.setIdProducto(id);
    }

    @Override
    protected Long generateid() {
        return sequence++;
    }
}
