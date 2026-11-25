package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Proveedor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Tabla proveedor. */
public class ProveedorRepositorio extends RepositorioBase<Proveedor> implements IRepositorio<Proveedor> {

    private static final String SELECT = "SELECT p.id_proveedor, p.ruc, p.nombre, p.telefono, "
            + "(SELECT COUNT(*) FROM compra c WHERE c.id_proveedor = p.id_proveedor) AS n_compras FROM proveedor p ";

    @Override
    protected Proveedor mapear(ResultSet rs) throws SQLException {
        Proveedor p = new Proveedor();
        p.setId(rs.getInt("id_proveedor"));
        p.setRuc(rs.getString("ruc"));
        p.setNombre(rs.getString("nombre"));
        p.setTelefono(rs.getString("telefono"));
        p.setNumeroCompras(rs.getInt("n_compras"));
        return p;
    }

    @Override
    public Proveedor guardar(Proveedor p) {
        p.setId(insertar("INSERT INTO proveedor (ruc, nombre, telefono) VALUES (?, ?, ?)", p.getRuc(), p.getNombre(), p.getTelefono()));
        return p;
    }

    @Override
    public Proveedor actualizar(Proveedor p) {
        ejecutar("UPDATE proveedor SET ruc = ?, nombre = ?, telefono = ? WHERE id_proveedor = ?",
                p.getRuc(), p.getNombre(), p.getTelefono(), p.getId());
        return p;
    }

    @Override
    public void eliminar(int id) {
        ejecutar("DELETE FROM proveedor WHERE id_proveedor = ?", id);
    }

    @Override
    public Optional<Proveedor> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE p.id_proveedor = ?", id);
    }

    @Override
    public List<Proveedor> listar() {
        return consultar(SELECT + "ORDER BY p.id_proveedor");
    }

    public List<Proveedor> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return listar();
        }
        String like = "%" + texto.trim().toLowerCase() + "%";
        return consultar(SELECT + "WHERE LOWER(p.nombre) LIKE ? OR p.ruc LIKE ? ORDER BY p.id_proveedor", like, like);
    }

    /** ¿ruc repetido? */
    public boolean existeRuc(String ruc, int idDistinto) {
        return contarFilas("SELECT COUNT(*) FROM proveedor WHERE ruc = ? AND id_proveedor <> ?", ruc, idDistinto) > 0;
    }
}
