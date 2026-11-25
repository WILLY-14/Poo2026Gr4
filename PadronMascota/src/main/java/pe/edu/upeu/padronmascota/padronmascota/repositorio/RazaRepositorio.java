package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Raza;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Tabla raza. Cada raza pertenece a una especie (FK id_especie). */
public class RazaRepositorio extends RepositorioBase<Raza> implements IRepositorio<Raza> {

    private static final String SELECT = "SELECT r.id_raza, r.nombre, r.id_especie, "
            + "(SELECT COUNT(*) FROM mascota m WHERE m.id_raza = r.id_raza AND m.activo) AS n_mascotas FROM raza r ";

    @Override
    protected Raza mapear(ResultSet rs) throws SQLException {
        Raza r = new Raza(rs.getInt("id_raza"), rs.getString("nombre"), Especie.desdeId(rs.getInt("id_especie")));
        r.setNumeroMascotas(rs.getInt("n_mascotas"));
        return r;
    }

    @Override
    public Raza guardar(Raza r) {
        r.setId(insertar("INSERT INTO raza (nombre, id_especie) VALUES (?, ?)", r.getNombre(), r.getEspecie().getId()));
        return r;
    }

    @Override
    public Raza actualizar(Raza r) {
        ejecutar("UPDATE raza SET nombre = ?, id_especie = ? WHERE id_raza = ?", r.getNombre(), r.getEspecie().getId(), r.getId());
        return r;
    }

    @Override
    public void eliminar(int id) {
        ejecutar("DELETE FROM raza WHERE id_raza = ?", id);
    }

    @Override
    public Optional<Raza> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE r.id_raza = ?", id);
    }

    @Override
    public List<Raza> listar() {
        return consultar(SELECT + "ORDER BY r.id_raza");
    }

    /** especie null = todas. */
    public List<Raza> buscar(String texto, Especie especie) {
        StringBuilder sql = new StringBuilder(SELECT).append("WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        if (texto != null && !texto.isBlank()) {
            sql.append("AND LOWER(r.nombre) LIKE ? ");
            params.add("%" + texto.trim().toLowerCase() + "%");
        }
        if (especie != null) {
            sql.append("AND r.id_especie = ? ");
            params.add(especie.getId());
        }
        sql.append("ORDER BY r.id_raza");
        return consultar(sql.toString(), params.toArray());
    }

    public int contarMascotas(int idRaza) {
        return contarFilas("SELECT COUNT(*) FROM mascota WHERE id_raza = ?", idRaza);
    }

    /** Nombres de las mascotas que tienen esta raza (las de baja dicen "(de baja)"). */
    public List<String> mascotasQueLaUsan(int idRaza) {
        return textos("SELECT nombre || CASE WHEN activo THEN '' ELSE ' (de baja)' END FROM mascota "
                + "WHERE id_raza = ? ORDER BY id_mascota", idRaza);
    }

    public boolean existe(String nombre, Especie especie, int idDistinto) {
        return contarFilas("SELECT COUNT(*) FROM raza WHERE LOWER(nombre) = ? AND id_especie = ? AND id_raza <> ?",
                nombre.trim().toLowerCase(), especie.getId(), idDistinto) > 0;
    }
}
