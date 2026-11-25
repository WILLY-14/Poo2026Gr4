package pe.edu.upeu.padronmascota.padronmascota.servicio;

import pe.edu.upeu.padronmascota.padronmascota.modelo.ServicioVeterinario;

import java.util.List;

/** CRUD de los servicios que ofrece la veterinaria (consulta, vacuna...). */
public interface IServicioVeterinarioServicio extends IServicioCrud<ServicioVeterinario> {

    List<ServicioVeterinario> buscar(String texto);
}
