package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.config.DatabaseConfig;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Compra;
import pe.edu.upeu.padronmascota.padronmascota.modelo.DetalleCompra;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Producto;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Proveedor;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Tabla compra + detalle_compra. Al comprar, el stock sube solo (la vista suma lo comprado). */
public class CompraRepositorio extends RepositorioBase<Compra> {

    private static final String SELECT = "SELECT c.id_compra, c.fecha_hora, c.numero_factura, c.total, "
            + "pv.id_proveedor, pv.nombre AS proveedor, pv.ruc, u.id_usuario, u.username, "
            + "(SELECT COUNT(*) FROM detalle_compra d WHERE d.id_compra = c.id_compra) AS n_lineas "
            + "FROM compra c "
            + "JOIN proveedor pv ON pv.id_proveedor = c.id_proveedor "
            + "JOIN usuario u ON u.id_usuario = c.id_usuario ";

    @Override
    protected Compra mapear(ResultSet rs) throws SQLException {
        Compra c = new Compra();
        c.setId(rs.getInt("id_compra"));
        c.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        c.setNumeroFactura(rs.getString("numero_factura"));
        c.setTotalGuardado(rs.getBigDecimal("total"));
        c.setNumeroLineas(rs.getInt("n_lineas"));
        Proveedor p = new Proveedor();
        p.setId(rs.getInt("id_proveedor"));
        p.setNombre(rs.getString("proveedor"));
        p.setRuc(rs.getString("ruc"));
        c.setProveedor(p);
        Usuario u = new Usuario();
        u.setId(rs.getInt("id_usuario"));
        u.setUsername(rs.getString("username"));
        c.setUsuario(u);
        return c;
    }

    /** Guarda la compra y sus líneas en UNA transacción. */
    public Compra registrar(Compra c) {
        return enTransaccion(cn -> {
            c.setId(insertarEn(cn, "INSERT INTO compra (id_proveedor, fecha_hora, numero_factura, id_usuario, total) VALUES (?, ?, ?, ?, ?)",
                    c.getProveedor().getId(), c.getFechaHora(), c.getNumeroFactura(), c.getUsuario().getId(), c.getTotal()));
            for (DetalleCompra d : c.getDetalles()) {
                d.setId(insertarEn(cn, "INSERT INTO detalle_compra (id_compra, id_producto, cantidad, costo_unitario, lote, "
                                + "fecha_vencimiento, subtotal) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        c.getId(), d.getProducto().getId(), d.getCantidad(), d.getCostoUnitario(), d.getLote(),
                        d.getFechaVencimiento(), d.getSubtotal()));
            }
            return c;
        });
    }

    /** Borra la compra; si eso deja algún producto con stock negativo, se deshace (rollback). */
    public void eliminar(int id) {
        enTransaccion(cn -> {
            List<Integer> productos = new ArrayList<>();
            try (PreparedStatement ps = cn.prepareStatement("SELECT DISTINCT id_producto FROM detalle_compra WHERE id_compra = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        productos.add(rs.getInt(1));
                    }
                }
            }
            ejecutarEn(cn, "DELETE FROM detalle_compra WHERE id_compra = ?", id);
            ejecutarEn(cn, "DELETE FROM compra WHERE id_compra = ?", id);
            for (int idProducto : productos) {
                if (stockActual(cn, idProducto) < 0) {
                    throw new IllegalStateException("No se puede eliminar: esos productos ya se vendieron o usaron");
                }
            }
            return null;
        });
    }

    public Optional<Compra> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE c.id_compra = ?", id);
    }

    public List<Compra> buscar(String texto) {
        StringBuilder sql = new StringBuilder(SELECT);
        List<Object> params = new ArrayList<>();
        if (texto != null && !texto.isBlank()) {
            sql.append("WHERE LOWER(pv.nombre) LIKE ? OR LOWER(c.numero_factura) LIKE ? ");
            String like = "%" + texto.trim().toLowerCase() + "%";
            params.add(like);
            params.add(like);
        }
        sql.append("ORDER BY c.fecha_hora DESC, c.id_compra DESC");
        return consultar(sql.toString(), params.toArray());
    }

    public boolean existeFactura(int idProveedor, String numero) {
        return contarFilas("SELECT COUNT(*) FROM compra WHERE id_proveedor = ? AND numero_factura = ?", idProveedor, numero) > 0;
    }

    public List<DetalleCompra> detalles(int idCompra) {
        String sql = "SELECT d.cantidad, d.costo_unitario, d.lote, d.fecha_vencimiento, pr.id_producto, pr.nombre "
                + "FROM detalle_compra d JOIN producto pr ON pr.id_producto = d.id_producto "
                + "WHERE d.id_compra = ? ORDER BY d.id_detalle_compra";
        List<DetalleCompra> lista = new ArrayList<>();
        try (Connection cn = DatabaseConfig.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCompra);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setId(rs.getInt("id_producto"));
                    p.setNombre(rs.getString("nombre"));
                    lista.add(new DetalleCompra(p, rs.getInt("cantidad"), rs.getBigDecimal("costo_unitario"),
                            rs.getString("lote"), fecha(rs, "fecha_vencimiento")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer el detalle de la compra: " + e.getMessage(), e);
        }
        return lista;
    }
}
