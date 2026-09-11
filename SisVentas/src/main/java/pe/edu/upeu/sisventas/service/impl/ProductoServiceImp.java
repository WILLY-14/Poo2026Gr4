package pe.edu.upeu.sisventas.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sisventas.model.Producto;
import pe.edu.upeu.sisventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sisventas.repository.ProductoRepository;
import pe.edu.upeu.sisventas.service.IProductoService;


@RequiredArgsConstructor
public class ProductoServiceImp extends CrudGenericoServiceImp<Producto, Long> implements IProductoService {

    private final ProductoRepository productoRepository;

    @Override
    protected ICrudGenericoRepository<Producto, Long> getRepo() {
        return productoRepository;
    }
}
