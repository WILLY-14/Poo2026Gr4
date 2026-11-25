package pe.edu.upeu.padronmascota.padronmascota.servicio;


import pe.edu.upeu.padronmascota.padronmascota.modelo.Proveedor;

import java.util.List;

public interface IProveedorServicio extends IServicioCrud<Proveedor> {

    List<Proveedor> buscar(String texto);
}
