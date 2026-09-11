package pe.edu.upeu.sisventas.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sisventas.model.UnidMedida;
import pe.edu.upeu.sisventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sisventas.repository.UnidadMedidaRepository;
import pe.edu.upeu.sisventas.service.IUnidadMedidaService;

@RequiredArgsConstructor
public class UnidadMedidaServiceImp extends CrudGenericoServiceImp<UnidMedida, Long> implements IUnidadMedidaService {

    private final UnidadMedidaRepository unidadMedidaRepository;

    @Override
    protected ICrudGenericoRepository<UnidMedida, Long> getRepo() {
        return unidadMedidaRepository;
    }
}
