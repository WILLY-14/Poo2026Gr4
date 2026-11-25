package pe.edu.upeu.padronmascota.padronmascota.servicio;

import pe.edu.upeu.padronmascota.padronmascota.enums.CategoriaProducto;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Producto;

import java.util.List;

public interface IProductoServicio extends IServicioCrud<Producto> {

    List<Producto> buscar(String texto, CategoriaProducto categoria);
}
