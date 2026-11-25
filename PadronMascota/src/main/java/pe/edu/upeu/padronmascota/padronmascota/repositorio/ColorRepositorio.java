package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Color;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Tabla color (catálogo editable). */
public class ColorRepositorio extends RepositorioBase<Color> implements IRepositorio<Color> {

    private static final String SELECT = "SELECT c.id_color, c.nombre, "
            + "(SELECT COUNT(*) FROM mascota m WHERE m.id_color = c.id_color AND m.activo) AS n_mascotas FROM color c ";

    @Override
    protected Color mapear(ResultSet rs) throws SQLException {
        Color c = new Color(rs.getInt("id_color"), rs.getString("nombre"));
        c.setNumeroMascotas(rs.getInt("n_mascotas"));
        return c;
    }

    @Override
    public Color guardar(Color c) {
        c.setId(insertar("INSERT INTO color (nombre) VALUES (?)", c.getNombre()));
        return c;
    }

    @Override
    public Color actualizar(Color c) {
        ejecutar("UPDATE color SET nombre = ? WHERE id_color = ?", c.getNombre(), c.getId());
        return c;
    }

    @Override
    public void eliminar(int id) {
        ejecutar("DELETE FROM color WHERE id_color = ?", id);
    }

    @Override
    public Optional<Color> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE c.id_color = ?", id);
    }

    @Override
    public List<Color> listar() {
        return consultar(SELECT + "ORDER BY c.id_color");
    }

    public List<Color> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return listar();
        }
        return consultar(SELECT + "WHERE LOWER(c.nombre) LIKE ? ORDER BY c.id_color", "%" + texto.trim().toLowerCase() + "%");
    }

    /** Cuenta TODAS las mascotas (también las dadas de baja): si hay alguna, no se puede borrar. */
    public int contarMascotas(int idColor) {
        return contarFilas("SELECT COUNT(*) FROM mascota WHERE id_color = ?", idColor);
    }

    /** Nombres de las mascotas que tienen este color (las de baja dicen "(de baja)"). */
    public List<String> mascotasQueLoUsan(int idColor) {
        return textos("SELECT nombre || CASE WHEN activo THEN '' ELSE ' (de baja)' END FROM mascota "
                + "WHERE id_color = ? ORDER BY id_mascota", idColor);
    }

    public boolean existeNombre(String nombre, int idDistinto) {
        return contarFilas("SELECT COUNT(*) FROM color WHERE LOWER(nombre) = ? AND id_color <> ?",
                nombre.trim().toLowerCase(), idDistinto) > 0;
    }
}
