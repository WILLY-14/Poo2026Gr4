package pe.edu.upeu.padronmascota.padronmascota.servicio;

import pe.edu.upeu.padronmascota.padronmascota.modelo.DetalleReceta;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Receta;

import java.util.List;

public interface IRecetaServicio {

    Receta registrar(Receta r);

    void eliminar(int id);

    List<Receta> buscar(String texto);

    List<DetalleReceta> detalles(int idReceta);
}
