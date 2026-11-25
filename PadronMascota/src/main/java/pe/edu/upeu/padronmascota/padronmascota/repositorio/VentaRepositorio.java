package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.config.DatabaseConfig;
import pe.edu.upeu.padronmascota.padronmascota.enums.CategoriaProducto;
import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoVenta;
import pe.edu.upeu.padronmascota.padronmascota.enums.MetodoPago;
import pe.edu.upeu.padronmascota.padronmascota.enums.TipoComprobante;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Atencion;
import pe.edu.upeu.padronmascota.padronmascota.modelo.DetalleVenta;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Mascota;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Usuario;
import pe.edu.upeu.padronmascota.padronmascota.modelo.ItemVenta;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Producto;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;
import pe.edu.upeu.padronmascota.padronmascota.modelo.ServicioVeterinario;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Ventas y su detalle. Registrar y anular usan una TRANSACCIÓN:
 * la venta, sus líneas y el stock se guardan juntos, o no se guarda nada.
 */
public class VentaRepositorio extends RepositorioBase<Venta> {

    private static final String SELECT =
            "SELECT v.id_venta, v.tipo_comprobante, v.numero_comprobante, v.fecha_hora, v.id_metodo_pago, "
            + "v.estado, v.total, v.ruc_cliente, v.id_atencion, a.fecha AS fecha_atencion, a.motivo, ma.nombre AS mascota_atencion, "
            + "p.id_propietario, p.nombre AS propietario, p.telefono, p.dni, u.id_usuario, u.username "
            + "FROM venta v JOIN propietario p ON p.id_propietario = v.id_propietario "
            + "JOIN usuario u ON u.id_usuario = v.id_usuario "
            + "LEFT JOIN atencion a ON a.id_atencion = v.id_atencion "
            + "LEFT JOIN mascota ma ON ma.id_mascota = a.id_mascota ";

    @Override
    protected Venta mapear(ResultSet rs) throws SQLException {
        Venta v = new Venta();
        v.setId(rs.getInt("id_venta"));
        v.setTipoComprobante(TipoComprobante.desdeNombre(rs.getString("tipo_comprobante")));
        v.setNumeroComprobante(rs.getString("numero_comprobante"));
        v.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        v.setMetodoPago(MetodoPago.desdeId(rs.getInt("id_metodo_pago")));
        v.setEstado(EstadoVenta.desdeNombre(rs.getString("estado")));
        v.setTotalGuardado(rs.getBigDecimal("total"));
        Propietario cliente = new Propietario(rs.getInt("id_propietario"), rs.getString("propietario"), rs.getString("telefono"));
        cliente.setDni(rs.getString("dni"));
        v.setPropietario(cliente);
        v.setRucCliente(rs.getString("ruc_cliente"));
        Usuario u = new Usuario();
        u.setId(rs.getInt("id_usuario"));
        u.setUsername(rs.getString("username"));
        v.setUsuario(u);
        if (rs.getObject("id_atencion") != null) {
            Atencion a = new Atencion();
            a.setId(rs.getInt("id_atencion"));
            a.setFecha(fecha(rs, "fecha_atencion"));
            a.setMotivo(rs.getString("motivo"));
            Mascota m = new Mascota();
            m.setNombre(rs.getString("mascota_atencion"));
            a.setMascota(m);
            v.setAtencion(a);
        }
        return v;
    }

    public Optional<Venta> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE v.id_venta = ?", id);
    }

    /** Historial. texto busca en el número o el cliente; estado null = Todos. */
    public List<Venta> buscar(String texto, EstadoVenta estado) {
        StringBuilder sql = new StringBuilder(SELECT).append("WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        if (texto != null && !texto.isBlank()) {
            sql.append("AND (LOWER(v.numero_comprobante) LIKE ? OR LOWER(p.nombre) LIKE ?) ");
            String like = "%" + texto.trim().toLowerCase() + "%";
            params.add(like);
            params.add(like);
        }
        if (estado != null) {
            sql.append("AND v.estado = ? ");
            params.add(estado.getNombre());
        }
        sql.append("ORDER BY v.fecha_hora DESC, v.id_venta DESC");
        return consultar(sql.toString(), params.toArray());
    }

    /** Las líneas de una venta, con su producto o servicio. */
    public List<DetalleVenta> detalles(int idVenta) {
        String sql = "SELECT d.cantidad, d.precio, d.descripcion, "
                + "d.id_producto, pr.nombre AS producto, pr.id_categoria, pr.stock_minimo, pr.precio AS precio_producto, "
                + "d.id_servicio, s.nombre AS servicio, s.precio AS precio_servicio "
                + "FROM detalle_venta d "
                + "LEFT JOIN producto pr ON pr.id_producto = d.id_producto "
                + "LEFT JOIN servicio s ON s.id_servicio = d.id_servicio "
                + "WHERE d.id_venta = ? ORDER BY d.id_detalle_venta";
        List<DetalleVenta> lista = new ArrayList<>();
        try (Connection cn = DatabaseConfig.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemVenta item;
                    if (rs.getObject("id_producto") != null) {
                        Producto p = new Producto();
                        p.setId(rs.getInt("id_producto"));
                        p.setNombre(rs.getString("producto"));
                        p.setCategoria(CategoriaProducto.desdeId(rs.getInt("id_categoria")));
                        p.setPrecio(rs.getBigDecimal("precio_producto"));
                        p.setStockMinimo(rs.getInt("stock_minimo"));
                        item = p;
                    } else {
                        ServicioVeterinario s = new ServicioVeterinario();
                        s.setId(rs.getInt("id_servicio"));
                        s.setNombre(rs.getString("servicio"));
                        s.setPrecio(rs.getBigDecimal("precio_servicio"));
                        item = s;
                    }
                    DetalleVenta d = new DetalleVenta(item, rs.getInt("cantidad"));
                    d.setPrecio(rs.getBigDecimal("precio"));   // el precio de ese día
                    d.setDescripcion(rs.getString("descripcion"));
                    lista.add(d);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer el detalle: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Guarda la venta completa en UNA transacción:
     * 1) número de comprobante, 2) venta, 3) detalles, 4) revisa el stock.
     * El stock no se guarda en una columna: la vista v_stock_producto ya resta lo vendido.
     * Si un producto queda con stock negativo, se deshace todo (rollback).
     */
    public Venta registrar(Venta v) {
        try {
            return enTransaccion(cn -> {
                v.setNumeroComprobante(siguienteNumero(cn, v.getTipoComprobante()));
                v.setId(insertarEn(cn, "INSERT INTO venta (tipo_comprobante, numero_comprobante, id_propietario, id_atencion, "
                                + "fecha_hora, id_metodo_pago, id_usuario, estado, total, ruc_cliente) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        v.getTipoComprobante().getNombre(), v.getNumeroComprobante(), v.getPropietario().getId(),
                        v.getAtencion() == null ? null : v.getAtencion().getId(), v.getFechaHora(),
                        v.getMetodoPago().getId(), v.getUsuario().getId(), v.getEstado().getNombre(), v.getTotal(),
                        v.getRucCliente()));
                for (DetalleVenta d : v.getDetalles()) {
                    ItemVenta item = d.getItem();
                    if (item instanceof ServicioVeterinario && item.getId() == 0) {
                        item.setId(servicioConsulta(cn, item));   // "Consulta": se registra la primera vez
                    }
                    insertarEn(cn, "INSERT INTO detalle_venta (id_venta, id_servicio, id_producto, cantidad, precio, subtotal, "
                                    + "ya_descontado, descripcion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                            v.getId(),
                            item instanceof ServicioVeterinario ? item.getId() : null,
                            item instanceof Producto ? item.getId() : null,
                            d.getCantidad(), d.getPrecio(), d.getSubtotal(), d.isYaDescontado(), d.getDescripcion());
                    if (item.controlaStock() && !d.isYaDescontado()) {   // POLIMORFISMO: solo los productos
                        verificarStock(cn, item.getId(), item.getNombre());
                    }
                    // Lo pendiente queda marcado como cobrado (no vuelve a salir)
                    if (d.getIdAtencion() != null) {
                        ejecutarEn(cn, "UPDATE atencion SET id_venta = ? WHERE id_atencion = ?", v.getId(), d.getIdAtencion());
                    }
                    if (d.getIdVacunacion() != null) {
                        ejecutarEn(cn, "UPDATE vacunacion SET id_venta = ? WHERE id_vacunacion = ?", v.getId(), d.getIdVacunacion());
                    }
                }
                return v;
            });
        } catch (RuntimeException e) {
            v.setId(0);
            v.setNumeroComprobante(null);
            throw e;
        }
    }

    /** Busca el servicio por nombre ("Consulta"); si no existe lo registra con ese precio. */
    private static int servicioConsulta(Connection cn, ItemVenta s) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("SELECT id_servicio FROM servicio WHERE LOWER(nombre) = LOWER(?)")) {
            ps.setString(1, s.getNombre());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return insertarEn(cn, "INSERT INTO servicio (nombre, precio) VALUES (?, ?)", s.getNombre(), s.getPrecio());
    }

    /** Sin cobrar = no tiene venta, o su venta se anuló. */
    private static final String SIN_COBRAR =
            " NOT EXISTS (SELECT 1 FROM venta vv WHERE vv.id_venta = %s.id_venta AND vv.estado = 'Pagada') ";

    /**
     * Lo que el cliente debe de sus mascotas (últimos 30 días): cada consulta con el precio que puso
     * el doctor y cada vacuna aplicada con su precio de Productos. Así la boleta sale completa de una vez.
     */
    public List<DetalleVenta> pendientes(int idPropietario) {
        List<DetalleVenta> lista = new ArrayList<>();
        String consultas = "SELECT a.id_atencion, a.precio_consulta, a.motivo, m.nombre AS mascota, "
                + "(SELECT id_servicio FROM servicio WHERE LOWER(nombre) = 'consulta') AS id_servicio "
                + "FROM atencion a JOIN mascota m ON m.id_mascota = a.id_mascota "
                + "WHERE m.id_propietario = ? AND a.precio_consulta IS NOT NULL AND a.fecha >= CURRENT_DATE - 30 AND "
                + String.format(SIN_COBRAR, "a") + "ORDER BY a.fecha, a.id_atencion";
        String vacunas = "SELECT va.id_vacunacion, m.nombre AS mascota, pr.id_producto, pr.nombre, pr.precio, pr.id_categoria "
                + "FROM vacunacion va JOIN vacuna vc ON vc.id_vacuna = va.id_vacuna "
                + "JOIN producto pr ON pr.id_producto = vc.id_producto "
                + "JOIN mascota m ON m.id_mascota = va.id_mascota "
                + "WHERE m.id_propietario = ? AND va.fecha >= CURRENT_DATE - 30 AND "
                + String.format(SIN_COBRAR, "va") + "ORDER BY va.fecha, va.id_vacunacion";
        try (Connection cn = DatabaseConfig.getConnection()) {
            try (PreparedStatement ps = cn.prepareStatement(consultas)) {
                ps.setInt(1, idPropietario);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ServicioVeterinario s = new ServicioVeterinario();
                        Integer idServicio = enteroONull(rs, "id_servicio");
                        s.setId(idServicio == null ? 0 : idServicio);   // 0 = se registra al cobrar
                        s.setNombre("Consulta");
                        s.setPrecio(rs.getBigDecimal("precio_consulta"));
                        DetalleVenta d = new DetalleVenta(s, 1);
                        d.setIdAtencion(rs.getInt("id_atencion"));
                        d.setDescripcion("Consulta - " + rs.getString("mascota") + " (" + rs.getString("motivo") + ")");
                        lista.add(d);
                    }
                }
            }
            try (PreparedStatement ps = cn.prepareStatement(vacunas)) {
                ps.setInt(1, idPropietario);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Producto p = new Producto();
                        p.setId(rs.getInt("id_producto"));
                        p.setNombre(rs.getString("nombre"));
                        p.setPrecio(rs.getBigDecimal("precio"));
                        p.setCategoria(CategoriaProducto.desdeId(rs.getInt("id_categoria")));
                        DetalleVenta d = new DetalleVenta(p, 1);
                        d.setIdVacunacion(rs.getInt("id_vacunacion"));
                        d.setYaDescontado(true);   // ya bajó del stock al vacunar
                        d.setDescripcion(p.getNombre() + " - " + rs.getString("mascota"));
                        lista.add(d);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer lo pendiente de cobro: " + e.getMessage(), e);
        }
        return lista;
    }

    /** Marca la venta como Anulada. Las anuladas no cuentan en el stock: los productos "vuelven" solos. */
    public void anular(int idVenta) {
        if (ejecutar("UPDATE venta SET estado = 'Anulada' WHERE id_venta = ? AND estado = 'Pagada'", idVenta) == 0) {
            throw new IllegalStateException("La venta ya estaba anulada");
        }
    }

    /** B001-000007: busca el último número de esa serie y le suma 1. */
    private String siguienteNumero(Connection cn, TipoComprobante tipo) throws SQLException {
        String serie = tipo.getSerie();
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT MAX(numero_comprobante) FROM venta WHERE numero_comprobante LIKE ?")) {
            ps.setString(1, serie + "-%");
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                String ultimo = rs.getString(1);
                int n = ultimo == null ? 1 : Integer.parseInt(ultimo.substring(serie.length() + 1)) + 1;
                return String.format("%s-%06d", serie, n);
            }
        }
    }

    public int contarPorPropietario(int idPropietario) {
        return contarFilas("SELECT COUNT(*) FROM venta WHERE id_propietario = ?", idPropietario);
    }
}
