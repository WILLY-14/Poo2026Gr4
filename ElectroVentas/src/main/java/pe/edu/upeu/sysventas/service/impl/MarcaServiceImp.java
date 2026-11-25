package pe.edu.upeu.sysventas.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.model.Marca;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.repository.MarcaRepository;
import pe.edu.upeu.sysventas.service.IMarcaService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MarcaServiceImp extends CrudGenericoServiceImp<Marca, Long>
        implements IMarcaService {

    private final MarcaRepository marcaRepository;
    @Override
    protected ICrudGenericoRepository<Marca, Long> getRepo() {
        return marcaRepository;
    }

    public List<ComboBoxOption> listarCombobox() {

        List<ComboBoxOption> listar = new ArrayList<>();
        for (Marca m : marcaRepository.findAll()) {
            ComboBoxOption cb = new ComboBoxOption();
            cb.setKey(String.valueOf(m.getIdMarca()));
            cb.setValue(m.getNombre());
            listar.add(cb);
        }
        return listar;
    }


    @Override
    public Marca save(Marca e) {
        if (marcaRepository.existsByNombre(e.getNombre(), null))
            throw new IllegalArgumentException("Ya existe una marca con el nombre '" + e.getNombre().trim() + "'");
        e.setNombre(e.getNombre().trim());
        return super.save(e);
    }

    @Override
    public Marca update(Long id, Marca e) {
        if (marcaRepository.existsByNombre(e.getNombre(), id))
            throw new IllegalArgumentException("Ya existe una marca con el nombre '" + e.getNombre().trim() + "'");
        e.setNombre(e.getNombre().trim());
        return super.update(id, e);
    }
}
