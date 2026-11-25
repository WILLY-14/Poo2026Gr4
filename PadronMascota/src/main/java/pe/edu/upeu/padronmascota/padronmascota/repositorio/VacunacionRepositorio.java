package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Vacuna;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Vacunacion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Tabla vacunacion (la "Consulta vacunación" del Excel).
 * Si la vacuna tiene producto, cada vacunación usa 1 dosis: se revisa el stock en una transacción.
 */
public class VacunacionRepositorio extends RepositorioBase<Vacunacion> {

    private static final String SELECT = "SELECT va.id_vacunacion, va.fecha, va.proxima_dosis, "
            + "m.id_mascota, m.nombre AS mascota, m.id_especie, p.id_propietario, p.nombre AS propietario, "
            + "vc.id_vacuna, vc.nombre AS vacuna, vc.id_especie AS especie_vacuna "
            + "FROM vacunacion va "
            + "JOIN mascota m ON m.id_mascota = va.id_mascota "
            + "JOIN propietario p ON p.id_propietario = m.id_propietario "
            + "JOIN vacuna vc ON vc.id_vacuna = va.id_vacuna ";

    @Override
    protected Vacunacion mapear(ResultSet rs) throws SQLException {
        Vacunacion v = new Vacunacion();
        v.setId(rs.getInt("id_vacunacion"));
        v.setFecha(fecha(rs, "fecha"));
        v.setProximaDosis(fecha(rs, "proxima_dosis"));
        v.setMascota(MascotaRepositorio.corta(rs.getInt("id_mascota"), rs.getString("mascota"), rs.getInt("id_especie"),
                rs.getInt("id_propietario"), rs.getString("propietario")));
        Vacuna vacuna = new Vacuna();
        vacuna.setId(rs.getInt("id_vacuna"));
        vacuna.setNombre(rs.getString("vacuna"));
        vacuna.setEspecie(v.getMascota().getEspecie());
        v.setVacuna(vacuna);
        return v;
    }

    /** Guarda la vacunación y revisa que haya stock de la dosis (si la vacuna tiene producto). */
    public Vacunacion registrar(Vacunacion v, Integer idProductoDosis, String nombreDosis) {
        return enTransaccion(cn -> {
            v.setId(insertarEn(cn, "INSERT INTO vacunacion (id_mascota, id_vacuna, fecha, proxima_dosis) VALUES (?, ?, ?, ?)",
                    v.getMascota().getId(), v.getVacuna().getId(), v.getFecha(), v.getProximaDosis()));
            if (idProductoDosis != null) {
                verificarStock(cn, idProductoDosis, nombreDosis);
            }
            return v;
        });
    }

    public void actualizar(Vacunacion v) {
        ejecutar("UPDATE vacunacion SET fecha = ?, proxima_dosis = ? WHERE id_vacunacion = ?",
                v.getFecha(), v.getProximaDosis(), v.getId());
    }

    public void eliminar(int id) {
        ejecutar("DELETE FROM vacunacion WHERE id_vacunacion = ?", id);
    }

    public Optional<Vacunacion> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE va.id_vacunacion = ?", id);
    }

    /** texto busca en mascota o vacuna. soloVencidas = las que ya pasaron su próxima dosis. */
    public List<Vacunacion> buscar(String texto, boolean soloVencidas) {
        StringBuilder sql = new StringBuilder(SELECT).append("WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        if (texto != null && !texto.isBlank()) {
            sql.append("AND (LOWER(m.nombre) LIKE ? OR LOWER(vc.nombre) LIKE ?) ");
            String like = "%" + texto.trim().toLowerCase() + "%";
            params.add(like);
            params.add(like);
        }
        if (soloVencidas) {
            sql.append("AND va.proxima_dosis < CURRENT_DATE ");
        }
        sql.append("ORDER BY va.fecha DESC, va.id_vacunacion DESC");
        return consultar(sql.toString(), params.toArray());
    }
}
