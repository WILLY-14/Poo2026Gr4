package pe.edu.upeu.sysventas.service.impl;


import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.model.Categoria;
import pe.edu.upeu.sysventas.model.Marca;
import pe.edu.upeu.sysventas.repository.CategoriaRepository;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.service.ICategoriaService;

import java.util.ArrayList;
import java.util.List;


public class CategoriaServiceImp extends CrudGenericoServiceImp<Categoria, Long>
        implements ICategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImp(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    protected ICrudGenericoRepository<Categoria, Long> getRepo() {
        return categoriaRepository;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (Categoria m : categoriaRepository.findAll()) {
            ComboBoxOption cb = new ComboBoxOption();
            cb.setKey(String.valueOf(m.getIdCategoria()));
            cb.setValue(m.getNombre());
            listar.add(cb);
        }
        return listar;
    }

    @Override
    public Categoria save(Categoria e) {
        if (categoriaRepository.existsByNombre(e.getNombre(), null))
            throw new IllegalArgumentException("Ya existe una categoría con el nombre '" + e.getNombre().trim() + "'");
        e.setNombre(e.getNombre().trim());
        return super.save(e);
    }

    @Override
    public Categoria update(Long id, Categoria e) {
        if (categoriaRepository.existsByNombre(e.getNombre(), id))
            throw new IllegalArgumentException("Ya existe una categoría con el nombre '" + e.getNombre().trim() + "'");
        e.setNombre(e.getNombre().trim());
        return super.update(id, e);
    }
}
