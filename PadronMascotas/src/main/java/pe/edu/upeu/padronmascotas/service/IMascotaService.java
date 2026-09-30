package pe.edu.upeu.padronmascotas.service;

import pe.edu.upeu.padronmascotas.dto.ComboBoxOption;
import pe.edu.upeu.padronmascotas.model.Mascota;

import java.util.List;

public interface IMascotaService extends ICrudGenericoService<Mascota, Long> {
    List<ComboBoxOption> listarEspecies();
}
