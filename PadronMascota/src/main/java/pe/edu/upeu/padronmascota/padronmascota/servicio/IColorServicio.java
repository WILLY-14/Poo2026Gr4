package pe.edu.upeu.padronmascota.padronmascota.servicio;


import pe.edu.upeu.padronmascota.padronmascota.modelo.Color;

import java.util.List;

public interface IColorServicio extends IServicioCrud<Color> {

    List<Color> buscar(String texto);
}
