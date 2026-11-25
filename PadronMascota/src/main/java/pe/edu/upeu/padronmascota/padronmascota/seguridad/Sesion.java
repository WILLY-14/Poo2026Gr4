package pe.edu.upeu.padronmascota.padronmascota.seguridad;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Usuario;

/** Guarda quién inició sesión mientras el sistema está abierto. */
public final class Sesion {

    private static Usuario usuario;

    private Sesion() {
    }

    public static void iniciar(Usuario u) {
        usuario = u;
    }

    public static void cerrar() {
        usuario = null;
    }

    public static Usuario usuario() {
        return usuario;
    }

    public static boolean activa() {
        return usuario != null;
    }
}
