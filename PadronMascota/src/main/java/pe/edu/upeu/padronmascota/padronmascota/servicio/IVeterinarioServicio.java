package pe.edu.upeu.padronmascota.padronmascota.servicio;


import pe.edu.upeu.padronmascota.padronmascota.modelo.Veterinario;

import java.util.List;

public interface IVeterinarioServicio extends IServicioCrud<Veterinario> {

    List<Veterinario> buscar(String texto);
}
