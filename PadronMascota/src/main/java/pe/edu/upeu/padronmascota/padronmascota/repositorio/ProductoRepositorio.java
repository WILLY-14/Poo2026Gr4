package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.enums.CategoriaProducto;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Producto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Tabla producto + la vista v_stock_producto (comprados, vendidos, usados, último costo). */
public class ProductoRepositorio extends RepositorioBase<Producto> implements IRepositorio<Producto> {

    private static final String SELECT = "SELECT p.id_producto, p.nombre, p.id_categoria, p.precio, p.stock_minimo, "
            + "s.comprados, s.vendidos, s.usados, s.ultimo_costo "
            + "FROM producto p JOIN v_stock_producto s ON s.id_producto = p.id_producto ";

    @Override
    protected Producto mapear(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id_producto"));
        p.setNombre(rs.getString("nombre"));
        p.setCategoria(CategoriaProducto.desdeId(rs.getInt("id_categoria")));
        p.setPrecio(rs.getBigDecimal("precio"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        p.setComprados(rs.getInt("comprados"));
        p.setVendidos(rs.getInt("vendidos"));
        p.setUsados(rs.getInt("usados"));
        p.setUltimoCosto(rs.getBigDecimal("ultimo_costo"));
        return p;
    }

    @Override
    public Producto guardar(Producto p) {
        p.setId(insertar("INSERT INTO producto (nombre, id_categoria, precio, stock_minimo) VALUES (?, ?, ?, ?)",
                p.getNombre(), p.getCategoria().getId(), p.getPrecio(), p.getStockMinimo()));
        return p;
    }

    @Override
    public Producto actualizar(Producto p) {
        ejecutar("UPDATE producto SET nombre = ?, id_categoria = ?, precio = ?, stock_minimo = ? WHERE id_producto = ?",
                p.getNombre(), p.getCategoria().getId(), p.getPrecio(), p.getStockMinimo(), p.getId());
        return p;
    }

    @Override
    public void eliminar(int id) {
        ejecutar("DELETE FROM producto WHERE id_producto = ?", id);
    }

    @Override
    public Optional<Producto> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE p.id_producto = ?", id);
    }

    @Override
    public List<Producto> listar() {
        return consultar(SELECT + "ORDER BY p.id_producto");
    }

    /** categoria null = todas. */
    public List<Producto> buscar(String texto, CategoriaProducto categoria) {
        StringBuilder sql = new StringBuilder(SELECT).append("WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        if (texto != null && !texto.isBlank()) {
            sql.append("AND LOWER(p.nombre) LIKE ? ");
            params.add("%" + texto.trim().toLowerCase() + "%");
        }
        if (categoria != null) {
            sql.append("AND p.id_categoria = ? ");
            params.add(categoria.getId());
        }
        sql.append("ORDER BY p.id_producto");
        return consultar(sql.toString(), params.toArray());
    }

    public boolean existeNombre(String nombre, int idDistinto) {
        return contarFilas("SELECT COUNT(*) FROM producto WHERE LOWER(nombre) = ? AND id_producto <> ?",
                nombre.trim().toLowerCase(), idDistinto) > 0;
    }

    /** En cuántos movimientos aparece (compras, ventas, insumos, recetas, vacunas). Si hay, no se borra. */
    public int contarUsos(int id) {
        return contarFilas("SELECT (SELECT COUNT(*) FROM detalle_compra WHERE id_producto = ?) "
                + "+ (SELECT COUNT(*) FROM detalle_venta WHERE id_producto = ?) "
                + "+ (SELECT COUNT(*) FROM atencion_insumo WHERE id_producto = ?) "
                + "+ (SELECT COUNT(*) FROM detalle_receta WHERE id_producto = ?) "
                + "+ (SELECT COUNT(*) FROM vacuna WHERE id_producto = ?)", id, id, id, id, id);
    }
}
