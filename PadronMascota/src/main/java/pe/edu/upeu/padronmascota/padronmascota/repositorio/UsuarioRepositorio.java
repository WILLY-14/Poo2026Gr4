package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.enums.Rol;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Usuario;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Veterinario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Tabla usuario con su rol y (si es doctor) su veterinario. */
public class UsuarioRepositorio extends RepositorioBase<Usuario> implements IRepositorio<Usuario> {

    private static final String SELECT = "SELECT u.id_usuario, u.username, u.clave, u.id_rol, u.estado, "
            + "v.id_veterinario, v.nombre AS veterinario, v.telefono "
            + "FROM usuario u LEFT JOIN veterinario v ON v.id_veterinario = u.id_veterinario ";

    @Override
    protected Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id_usuario"));
        u.setUsername(rs.getString("username"));
        u.setClave(rs.getString("clave"));
        u.setRol(Rol.desdeId(rs.getInt("id_rol")));
        u.setActivo("Activo".equals(rs.getString("estado")));
        if (rs.getObject("id_veterinario") != null) {
            u.setVeterinario(new Veterinario(rs.getInt("id_veterinario"), rs.getString("veterinario"), rs.getString("telefono")));
        }
        return u;
    }

    private static Integer idVeterinario(Usuario u) {
        return u.getVeterinario() == null ? null : u.getVeterinario().getId();
    }

    @Override
    public Usuario guardar(Usuario u) {
        u.setId(insertar("INSERT INTO usuario (username, clave, id_rol, id_veterinario, estado) VALUES (?, ?, ?, ?, ?)",
                u.getUsername(), u.getClave(), u.getRol().getId(), idVeterinario(u), u.getEstado()));
        return u;
    }

    @Override
    public Usuario actualizar(Usuario u) {
        ejecutar("UPDATE usuario SET username = ?, clave = ?, id_rol = ?, id_veterinario = ?, estado = ? WHERE id_usuario = ?",
                u.getUsername(), u.getClave(), u.getRol().getId(), idVeterinario(u), u.getEstado(), u.getId());
        return u;
    }

    @Override
    public void eliminar(int id) {
        ejecutar("DELETE FROM usuario WHERE id_usuario = ?", id);
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE u.id_usuario = ?", id);
    }

    @Override
    public List<Usuario> listar() {
        return consultar(SELECT + "ORDER BY u.id_usuario");
    }

    /** Para el login: solo usuarios activos. */
    public Optional<Usuario> buscarPorUsername(String username) {
        return consultarUno(SELECT + "WHERE u.username = ? AND u.estado = 'Activo'", username.trim().toLowerCase());
    }

    /** ¿username repetido? */
    public boolean existeUsername(String username, int idDistinto) {
        return contarFilas("SELECT COUNT(*) FROM usuario WHERE username = ? AND id_usuario <> ?", username, idDistinto) > 0;
    }

    /** Ventas + compras registradas por este usuario (si tiene, mejor desactivarlo que borrarlo). */
    public int contarMovimientos(int id) {
        return contarFilas("SELECT (SELECT COUNT(*) FROM venta WHERE id_usuario = ?) "
                + "+ (SELECT COUNT(*) FROM compra WHERE id_usuario = ?)", id, id);
    }

    public int contarAdminsActivos() {
        return contarFilas("SELECT COUNT(*) FROM usuario WHERE id_rol = 1 AND estado = 'Activo'");
    }
}
