package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.config.DatabaseConfig;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Atencion;
import pe.edu.upeu.padronmascota.padronmascota.modelo.DetalleReceta;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Producto;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Receta;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Veterinario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Tabla receta + detalle_receta (en una transacción). La receta no descuenta stock. */
public class RecetaRepositorio extends RepositorioBase<Receta> {

    private static final String SELECT = "SELECT r.id_receta, r.fecha, r.indicaciones, "
            + "a.id_atencion, a.fecha AS fecha_atencion, a.motivo, "
            + "m.id_mascota, m.nombre AS mascota, m.id_especie, p.id_propietario, p.nombre AS propietario, "
            + "v.id_veterinario, v.nombre AS veterinario "
            + "FROM receta r "
            + "JOIN atencion a ON a.id_atencion = r.id_atencion "
            + "JOIN mascota m ON m.id_mascota = a.id_mascota "
            + "JOIN propietario p ON p.id_propietario = m.id_propietario "
            + "JOIN veterinario v ON v.id_veterinario = a.id_veterinario ";

    @Override
    protected Receta mapear(ResultSet rs) throws SQLException {
        Receta r = new Receta();
        r.setId(rs.getInt("id_receta"));
        r.setFecha(fecha(rs, "fecha"));
        r.setIndicaciones(rs.getString("indicaciones"));
        Atencion a = new Atencion();
        a.setId(rs.getInt("id_atencion"));
        a.setFecha(fecha(rs, "fecha_atencion"));
        a.setMotivo(rs.getString("motivo"));
        a.setMascota(MascotaRepositorio.corta(rs.getInt("id_mascota"), rs.getString("mascota"), rs.getInt("id_especie"),
                rs.getInt("id_propietario"), rs.getString("propietario")));
        a.setVeterinario(new Veterinario(rs.getInt("id_veterinario"), rs.getString("veterinario"), null));
        r.setAtencion(a);
        return r;
    }

    public Receta registrar(Receta r) {
        return enTransaccion(cn -> {
            r.setId(insertarEn(cn, "INSERT INTO receta (id_atencion, fecha, indicaciones) VALUES (?, ?, ?)",
                    r.getAtencion().getId(), r.getFecha(), r.getIndicaciones()));
            for (DetalleReceta d : r.getDetalles()) {
                d.setId(insertarEn(cn, "INSERT INTO detalle_receta (id_receta, id_producto, dosis, frecuencia, dias) VALUES (?, ?, ?, ?, ?)",
                        r.getId(), d.getProducto().getId(), d.getDosis(), d.getFrecuencia(), d.getDias()));
            }
            return r;
        });
    }

    /** Sus líneas se borran en cascada (ON DELETE CASCADE). */
    public void eliminar(int id) {
        ejecutar("DELETE FROM receta WHERE id_receta = ?", id);
    }

    public List<Receta> buscar(String texto) {
        StringBuilder sql = new StringBuilder(SELECT);
        List<Object> params = new ArrayList<>();
        if (texto != null && !texto.isBlank()) {
            sql.append("WHERE LOWER(m.nombre) LIKE ? OR LOWER(v.nombre) LIKE ? OR LOWER(r.indicaciones) LIKE ? ");
            String like = "%" + texto.trim().toLowerCase() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        sql.append("ORDER BY r.fecha DESC, r.id_receta DESC");
        return consultar(sql.toString(), params.toArray());
    }

    public List<DetalleReceta> detalles(int idReceta) {
        String sql = "SELECT d.dosis, d.frecuencia, d.dias, pr.id_producto, pr.nombre "
                + "FROM detalle_receta d JOIN producto pr ON pr.id_producto = d.id_producto "
                + "WHERE d.id_receta = ? ORDER BY d.id_detalle_receta";
        List<DetalleReceta> lista = new ArrayList<>();
        try (Connection cn = DatabaseConfig.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idReceta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setId(rs.getInt("id_producto"));
                    p.setNombre(rs.getString("nombre"));
                    lista.add(new DetalleReceta(p, rs.getString("dosis"), rs.getString("frecuencia"), rs.getInt("dias")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer la receta: " + e.getMessage(), e);
        }
        return lista;
    }
}
