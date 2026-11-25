package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Veterinario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Tabla veterinario. */
public class VeterinarioRepositorio extends RepositorioBase<Veterinario> implements IRepositorio<Veterinario> {

    private static final String SELECT = "SELECT v.id_veterinario, v.nombre, v.telefono, "
            + "(SELECT COUNT(*) FROM atencion a WHERE a.id_veterinario = v.id_veterinario) AS n_atenciones FROM veterinario v ";

    @Override
    protected Veterinario mapear(ResultSet rs) throws SQLException {
        Veterinario v = new Veterinario(rs.getInt("id_veterinario"), rs.getString("nombre"), rs.getString("telefono"));
        v.setNumeroAtenciones(rs.getInt("n_atenciones"));
        return v;
    }

    @Override
    public Veterinario guardar(Veterinario v) {
        v.setId(insertar("INSERT INTO veterinario (nombre, telefono) VALUES (?, ?)", v.getNombre(), v.getTelefono()));
        return v;
    }

    @Override
    public Veterinario actualizar(Veterinario v) {
        ejecutar("UPDATE veterinario SET nombre = ?, telefono = ? WHERE id_veterinario = ?", v.getNombre(), v.getTelefono(), v.getId());
        return v;
    }

    @Override
    public void eliminar(int id) {
        ejecutar("DELETE FROM veterinario WHERE id_veterinario = ?", id);
    }

    @Override
    public Optional<Veterinario> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE v.id_veterinario = ?", id);
    }

    @Override
    public List<Veterinario> listar() {
        return consultar(SELECT + "WHERE v.activo ORDER BY v.id_veterinario");   // solo los que siguen trabajando
    }

    public List<Veterinario> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return listar();
        }
        String like = "%" + texto.trim().toLowerCase() + "%";
        return consultar(SELECT + "WHERE v.activo AND (LOWER(v.nombre) LIKE ? OR v.telefono LIKE ?) ORDER BY v.id_veterinario", like, like);
    }

    /** Baja lógica: deja de salir en listas y combos, pero sus citas y atenciones se conservan.
     *  Los usuarios unidos a él quedan Inactivos (ya no pueden entrar). */
    public void darDeBaja(int id) {
        enTransaccion(cn -> {
            ejecutarEn(cn, "UPDATE veterinario SET activo = FALSE WHERE id_veterinario = ?", id);
            ejecutarEn(cn, "UPDATE usuario SET estado = 'Inactivo' WHERE id_veterinario = ?", id);
            return null;
        });
    }

    /** Citas + atenciones + usuarios unidos a este doctor (si hay, no se puede borrar). */
    public int contarUsos(int id) {
        return contarFilas("SELECT (SELECT COUNT(*) FROM cita WHERE id_veterinario = ?) "
                + "+ (SELECT COUNT(*) FROM atencion WHERE id_veterinario = ?) "
                + "+ (SELECT COUNT(*) FROM usuario WHERE id_veterinario = ?)", id, id, id);
    }
}
