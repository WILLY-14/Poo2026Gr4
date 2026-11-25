package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Producto;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Vacuna;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Catálogo de vacunas (tabla vacuna). */
public class VacunaRepositorio extends RepositorioBase<Vacuna> implements IRepositorio<Vacuna> {

    private static final String SELECT = "SELECT v.id_vacuna, v.nombre, v.id_especie, v.id_producto, p.nombre AS producto "
            + "FROM vacuna v LEFT JOIN producto p ON p.id_producto = v.id_producto ";

    @Override
    protected Vacuna mapear(ResultSet rs) throws SQLException {
        Vacuna v = new Vacuna();
        v.setId(rs.getInt("id_vacuna"));
        v.setNombre(rs.getString("nombre"));
        v.setEspecie(Especie.desdeId(rs.getInt("id_especie")));
        if (rs.getObject("id_producto") != null) {
            Producto p = new Producto();
            p.setId(rs.getInt("id_producto"));
            p.setNombre(rs.getString("producto"));
            v.setProducto(p);
        }
        return v;
    }

    private static Integer idProducto(Vacuna v) {
        return v.getProducto() == null ? null : v.getProducto().getId();
    }

    @Override
    public Vacuna guardar(Vacuna v) {
        v.setId(insertar("INSERT INTO vacuna (nombre, id_especie, id_producto) VALUES (?, ?, ?)",
                v.getNombre(), v.getEspecie().getId(), idProducto(v)));
        return v;
    }

    @Override
    public Vacuna actualizar(Vacuna v) {
        ejecutar("UPDATE vacuna SET nombre = ?, id_especie = ?, id_producto = ? WHERE id_vacuna = ?",
                v.getNombre(), v.getEspecie().getId(), idProducto(v), v.getId());
        return v;
    }

    @Override
    public void eliminar(int id) {
        ejecutar("DELETE FROM vacuna WHERE id_vacuna = ?", id);
    }

    @Override
    public Optional<Vacuna> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE v.id_vacuna = ?", id);
    }

    @Override
    public List<Vacuna> listar() {
        return consultar(SELECT + "ORDER BY v.id_vacuna");
    }

    public List<Vacuna> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return listar();
        }
        return consultar(SELECT + "WHERE LOWER(v.nombre) LIKE ? ORDER BY v.id_vacuna", "%" + texto.trim().toLowerCase() + "%");
    }

    public List<Vacuna> deEspecie(Especie especie) {
        return consultar(SELECT + "WHERE v.id_especie = ? ORDER BY v.id_vacuna", especie.getId());
    }

    public int contarAplicaciones(int id) {
        return contarFilas("SELECT COUNT(*) FROM vacunacion WHERE id_vacuna = ?", id);
    }
}
