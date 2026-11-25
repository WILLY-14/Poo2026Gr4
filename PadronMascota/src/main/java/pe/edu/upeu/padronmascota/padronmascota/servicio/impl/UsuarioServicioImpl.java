package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.enums.Rol;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Usuario;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.IRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.UsuarioRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.seguridad.Clave;
import pe.edu.upeu.padronmascota.padronmascota.seguridad.Sesion;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IUsuarioServicio;

import java.util.Optional;

public class UsuarioServicioImpl extends ServicioCrudBase<Usuario> implements IUsuarioServicio {

    private final UsuarioRepositorio repo;

    public UsuarioServicioImpl(UsuarioRepositorio repo) {
        this.repo = repo;
    }

    @Override
    protected IRepositorio<Usuario> repositorio() {
        return repo;
    }

    @Override
    public Optional<Usuario> login(String username, String clave) {
        if (username == null || username.isBlank() || clave == null || clave.isEmpty()) {
            return Optional.empty();
        }
        // Buscamos al usuario y comparamos la clave con su hash BCrypt (nunca la clave en texto)
        return repo.buscarPorUsername(username)
                .filter(u -> Clave.coincide(clave, u.getClave()));
    }

    /**
     * - username UNIQUE.
     * - Usuario nuevo: la clave es obligatoria (mínimo 6). Al editar, vacía = deja la clave anterior.
     * - Si el rol no es Veterinario, no se une a ningún veterinario.
     */
    @Override
    protected void validarNegocio(Usuario u) {
        if (repo.existeUsername(u.getUsername(), u.getId())) {
            errorEnCampo("username", "Ya existe el usuario " + u.getUsername());
        }
        String clave = u.getClaveTexto();
        if (clave == null || clave.isEmpty()) {
            if (u.esNuevo()) {
                errorEnCampo("claveTexto", "La clave es obligatoria");
            }
            u.setClave(obtener(u.getId()).getClave());
        } else if (clave.length() < 6) {
            errorEnCampo("claveTexto", "La clave debe tener al menos 6 caracteres");
        } else {
            u.setClave(Clave.hash(clave));
        }
        if (u.getRol() != Rol.VETERINARIO) {
            u.setVeterinario(null);
        }
        if (!u.esNuevo()) {
            Usuario antes = obtener(u.getId());
            boolean dejaDeSerAdmin = antes.getRol() == Rol.ADMIN && antes.isActivo()
                    && (u.getRol() != Rol.ADMIN || !u.isActivo());
            if (dejaDeSerAdmin && repo.contarAdminsActivos() <= 1) {
                throw new IllegalStateException("Debe quedar al menos un Administrador activo");
            }
        }
    }

    @Override
    public void eliminar(int id) {
        Usuario u = obtener(id);
        if (Sesion.activa() && Sesion.usuario().getId() == id) {
            throw new IllegalStateException("No puedes eliminar el usuario con el que entraste");
        }
        if (u.getRol() == Rol.ADMIN && u.isActivo() && repo.contarAdminsActivos() <= 1) {
            throw new IllegalStateException("Debe quedar al menos un Administrador activo");
        }
        int movimientos = repo.contarMovimientos(id);
        if (movimientos > 0) {
            throw new IllegalStateException("No se puede eliminar: registró " + movimientos + " venta(s)/compra(s). Mejor ponlo Inactivo.");
        }
        super.eliminar(id);
    }
}
