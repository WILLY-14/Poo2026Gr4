package pe.edu.upeu.sisventas.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sisventas.model.Marca;
import pe.edu.upeu.sisventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sisventas.repository.MarcaRepository;
import pe.edu.upeu.sisventas.service.IMarcaService;

@RequiredArgsConstructor
public class MarcaServiceImp extends CrudGenericoServiceImp<Marca, Long> implements IMarcaService {

    private final MarcaRepository marcaRepository;

    @Override
    protected ICrudGenericoRepository<Marca, Long> getRepo() {
        return marcaRepository;
    }
}