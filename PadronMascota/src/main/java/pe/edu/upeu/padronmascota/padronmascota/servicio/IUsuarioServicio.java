package pe.edu.upeu.padronmascota.padronmascota.servicio;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioServicio extends IServicioCrud<Usuario> {

    /** Devuelve el usuario si el username y la clave son correctos (y está Activo); si no, vacío. */
    Optional<Usuario> login(String username, String clave);
}
