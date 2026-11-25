package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoCita;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Cita;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Veterinario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Tabla cita (la "Consulta cita" del Excel). */
public class CitaRepositorio extends RepositorioBase<Cita> implements IRepositorio<Cita> {

    private static final String SELECT = "SELECT c.id_cita, c.fecha, c.hora, c.motivo, c.id_estado_cita, "
            + "m.id_mascota, m.nombre AS mascota, m.id_especie, p.id_propietario, p.nombre AS propietario, "
            + "v.id_veterinario, v.nombre AS veterinario, v.telefono AS telefono_vet "
            + "FROM cita c "
            + "JOIN mascota m ON m.id_mascota = c.id_mascota "
            + "JOIN propietario p ON p.id_propietario = m.id_propietario "
            + "JOIN veterinario v ON v.id_veterinario = c.id_veterinario ";

    @Override
    protected Cita mapear(ResultSet rs) throws SQLException {
        Cita c = new Cita();
        c.setId(rs.getInt("id_cita"));
        c.setFecha(fecha(rs, "fecha"));
        c.setHora(rs.getObject("hora", LocalTime.class));
        c.setMotivo(rs.getString("motivo"));
        c.setEstado(EstadoCita.desdeId(rs.getInt("id_estado_cita")));
        c.setMascota(MascotaRepositorio.corta(rs.getInt("id_mascota"), rs.getString("mascota"), rs.getInt("id_especie"),
                rs.getInt("id_propietario"), rs.getString("propietario")));
        c.setVeterinario(new Veterinario(rs.getInt("id_veterinario"), rs.getString("veterinario"), rs.getString("telefono_vet")));
        return c;
    }

    @Override
    public Cita guardar(Cita c) {
        c.setId(insertar("INSERT INTO cita (id_mascota, id_veterinario, fecha, hora, motivo, id_estado_cita) VALUES (?, ?, ?, ?, ?, ?)",
                c.getMascota().getId(), c.getVeterinario().getId(), c.getFecha(), c.getHora(), c.getMotivo(), c.getEstado().getId()));
        return c;
    }

    @Override
    public Cita actualizar(Cita c) {
        ejecutar("UPDATE cita SET id_mascota = ?, id_veterinario = ?, fecha = ?, hora = ?, motivo = ?, id_estado_cita = ? WHERE id_cita = ?",
                c.getMascota().getId(), c.getVeterinario().getId(), c.getFecha(), c.getHora(), c.getMotivo(), c.getEstado().getId(), c.getId());
        return c;
    }

    @Override
    public void eliminar(int id) {
        enTransaccion(cn -> {
            ejecutarEn(cn, "UPDATE atencion SET id_cita = NULL WHERE id_cita = ?", id);
            ejecutarEn(cn, "DELETE FROM cita WHERE id_cita = ?", id);
            return null;
        });
    }

    public void cambiarEstado(int id, EstadoCita estado) {
        ejecutar("UPDATE cita SET id_estado_cita = ? WHERE id_cita = ?", estado.getId(), id);
    }

    @Override
    public Optional<Cita> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE c.id_cita = ?", id);
    }

    @Override
    public List<Cita> listar() {
        return buscar(null, null);
    }

    /** texto busca en mascota, veterinario o motivo. estado null = Todos. */
    public List<Cita> buscar(String texto, EstadoCita estado) {
        StringBuilder sql = new StringBuilder(SELECT).append("WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        if (texto != null && !texto.isBlank()) {
            sql.append("AND (LOWER(m.nombre) LIKE ? OR LOWER(v.nombre) LIKE ? OR LOWER(c.motivo) LIKE ?) ");
            String like = "%" + texto.trim().toLowerCase() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (estado != null) {
            sql.append("AND c.id_estado_cita = ? ");
            params.add(estado.getId());
        }
        sql.append("ORDER BY c.fecha DESC, c.hora DESC");
        return consultar(sql.toString(), params.toArray());
    }

    /** ¿duplicada? El mismo doctor ya tiene otra cita (no cancelada) a esa fecha y hora. */
    public boolean horarioOcupado(Cita c) {
        return contarFilas("SELECT COUNT(*) FROM cita WHERE id_veterinario = ? AND fecha = ? AND hora = ? AND id_cita <> ?",
                c.getVeterinario().getId(), c.getFecha(), c.getHora(), c.getId()) > 0;
    }

    public int contarAtenciones(int idCita) {
        return contarFilas("SELECT COUNT(*) FROM atencion WHERE id_cita = ?", idCita);
    }
}
