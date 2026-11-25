package pe.edu.upeu.padronmascota.padronmascota.servicio;

import pe.edu.upeu.padronmascota.padronmascota.dto.OpcionMenu;

import java.util.List;

public interface IMenuServicio {

    /** Las opciones que se dibujan en el MenuBar de la ventana principal. */
    List<OpcionMenu> opciones();
}
