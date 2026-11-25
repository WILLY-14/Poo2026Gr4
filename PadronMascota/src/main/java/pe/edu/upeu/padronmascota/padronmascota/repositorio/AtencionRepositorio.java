package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.config.DatabaseConfig;
import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoCita;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Atencion;
import pe.edu.upeu.padronmascota.padronmascota.modelo.AtencionInsumo;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Cita;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Producto;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Veterinario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Tabla atencion + atencion_insumo. Registrar usa una TRANSACCIÓN:
 * atención, insumos (revisando stock) y la cita pasa a "Atendida", todo junto.
 */
public class AtencionRepositorio extends RepositorioBase<Atencion> {

    private static final String SELECT = "SELECT a.id_atencion, a.fecha, a.motivo, a.id_cita, a.precio_consulta, "
            + "m.id_mascota, m.nombre AS mascota, m.id_especie, p.id_propietario, p.nombre AS propietario, "
            + "v.id_veterinario, v.nombre AS veterinario, v.telefono AS telefono_vet "
            + "FROM atencion a "
            + "JOIN mascota m ON m.id_mascota = a.id_mascota "
            + "JOIN propietario p ON p.id_propietario = m.id_propietario "
            + "JOIN veterinario v ON v.id_veterinario = a.id_veterinario ";

    @Override
    protected Atencion mapear(ResultSet rs) throws SQLException {
        Atencion a = new Atencion();
        a.setId(rs.getInt("id_atencion"));
        a.setFecha(fecha(rs, "fecha"));
        a.setMotivo(rs.getString("motivo"));
        a.setPrecioConsulta(rs.getBigDecimal("precio_consulta"));
        a.setMascota(MascotaRepositorio.corta(rs.getInt("id_mascota"), rs.getString("mascota"), rs.getInt("id_especie"),
                rs.getInt("id_propietario"), rs.getString("propietario")));
        a.setVeterinario(new Veterinario(rs.getInt("id_veterinario"), rs.getString("veterinario"), rs.getString("telefono_vet")));
        Integer idCita = enteroONull(rs, "id_cita");
        if (idCita != null) {
            Cita c = new Cita();
            c.setId(idCita);
            a.setCita(c);
        }
        return a;
    }

    public Atencion registrar(Atencion a) {
        return enTransaccion(cn -> {
            a.setId(insertarEn(cn, "INSERT INTO atencion (id_mascota, id_veterinario, fecha, motivo, id_cita, precio_consulta) VALUES (?, ?, ?, ?, ?, ?)",
                    a.getMascota().getId(), a.getVeterinario().getId(), a.getFecha(), a.getMotivo(),
                    a.getCita() == null ? null : a.getCita().getId(), a.getPrecioConsulta()));
            for (AtencionInsumo i : a.getInsumos()) {
                i.setId(insertarEn(cn, "INSERT INTO atencion_insumo (id_atencion, id_producto, cantidad) VALUES (?, ?, ?)",
                        a.getId(), i.getProducto().getId(), i.getCantidad()));
                verificarStock(cn, i.getProducto().getId(), i.getProducto().getNombre());
            }
            if (a.getCita() != null) {
                ejecutarEn(cn, "UPDATE cita SET id_estado_cita = ? WHERE id_cita = ?", EstadoCita.ATENDIDA.getId(), a.getCita().getId());
            }
            return a;
        });
    }

    /** Borra la atención (sus insumos se borran en cascada) y la cita vuelve a "Pendiente". */
    public void eliminar(int id) {
        enTransaccion(cn -> {
            ejecutarEn(cn, "UPDATE cita SET id_estado_cita = ? WHERE id_cita = (SELECT id_cita FROM atencion WHERE id_atencion = ?)",
                    EstadoCita.PENDIENTE.getId(), id);
            // Las ventas se conservan (plata que ya entró): solo se les quita la atención
            ejecutarEn(cn, "UPDATE venta SET id_atencion = NULL WHERE id_atencion = ?", id);
            ejecutarEn(cn, "DELETE FROM detalle_receta WHERE id_receta IN (SELECT id_receta FROM receta WHERE id_atencion = ?)", id);
            ejecutarEn(cn, "DELETE FROM receta WHERE id_atencion = ?", id);
            ejecutarEn(cn, "DELETE FROM atencion_insumo WHERE id_atencion = ?", id);
            ejecutarEn(cn, "DELETE FROM atencion WHERE id_atencion = ?", id);
            return null;
        });
    }

    public Optional<Atencion> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE a.id_atencion = ?", id);
    }

    public List<Atencion> buscar(String texto) {
        StringBuilder sql = new StringBuilder(SELECT);
        List<Object> params = new ArrayList<>();
        if (texto != null && !texto.isBlank()) {
            sql.append("WHERE LOWER(m.nombre) LIKE ? OR LOWER(v.nombre) LIKE ? OR LOWER(a.motivo) LIKE ? ");
            String like = "%" + texto.trim().toLowerCase() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        sql.append("ORDER BY a.fecha DESC, a.id_atencion DESC");
        return consultar(sql.toString(), params.toArray());
    }

    /** Atenciones de las mascotas de un propietario (para unir una venta a su atención). */
    public List<Atencion> dePropietario(int idPropietario) {
        return consultar(SELECT + "WHERE p.id_propietario = ? ORDER BY a.fecha DESC", idPropietario);
    }

    public List<AtencionInsumo> insumos(int idAtencion) {
        String sql = "SELECT ai.id_atencion_insumo, ai.cantidad, pr.id_producto, pr.nombre "
                + "FROM atencion_insumo ai JOIN producto pr ON pr.id_producto = ai.id_producto "
                + "WHERE ai.id_atencion = ? ORDER BY ai.id_atencion_insumo";
        List<AtencionInsumo> lista = new ArrayList<>();
        try (Connection cn = DatabaseConfig.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idAtencion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setId(rs.getInt("id_producto"));
                    p.setNombre(rs.getString("nombre"));
                    AtencionInsumo i = new AtencionInsumo(p, rs.getInt("cantidad"));
                    i.setId(rs.getInt("id_atencion_insumo"));
                    lista.add(i);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer los insumos: " + e.getMessage(), e);
        }
        return lista;
    }

    /** Recetas + ventas unidas a la atención (si hay, no se puede borrar). */
    public int contarUsos(int id) {
        return contarFilas("SELECT (SELECT COUNT(*) FROM receta WHERE id_atencion = ?) "
                + "+ (SELECT COUNT(*) FROM venta WHERE id_atencion = ?)", id, id);
    }
}
