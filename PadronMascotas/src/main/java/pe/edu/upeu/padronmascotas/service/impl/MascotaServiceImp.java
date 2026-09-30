package pe.edu.upeu.padronmascotas.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.padronmascotas.dto.ComboBoxOption;
import pe.edu.upeu.padronmascotas.enums.Especie;
import pe.edu.upeu.padronmascotas.model.Mascota;
import pe.edu.upeu.padronmascotas.repository.ICrudGenericoRepository;
import pe.edu.upeu.padronmascotas.repository.MascotaRepository;
import pe.edu.upeu.padronmascotas.service.IMascotaService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MascotaServiceImp extends CrudGenericoServiceImp<Mascota, Long> implements IMascotaService {

    private final MascotaRepository mascotaRepository;

    @Override
    protected ICrudGenericoRepository<Mascota, Long> getRepo() {
        return mascotaRepository;
    }

    // Llena el ComboBox con los valores del enum Especie (Perro, Gato, Otro)
    @Override
    public List<ComboBoxOption> listarEspecies() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (Especie e : Especie.values()) {
            ComboBoxOption cb = new ComboBoxOption();
            cb.setKey(e.name());
            cb.setValue(e.getDescripcion());
            listar.add(cb);
        }
        return listar;
    }
}
