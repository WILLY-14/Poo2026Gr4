package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Tabla propietario: solo SQL, sin reglas de negocio. */
public class PropietarioRepositorio extends RepositorioBase<Propietario> implements IRepositorio<Propietario> {

    private static final String SELECT = "SELECT p.id_propietario, p.dni, p.nombre, p.telefono, p.direccion, p.email, "
            + "(SELECT COUNT(*) FROM mascota m WHERE m.id_propietario = p.id_propietario AND m.activo) AS n_mascotas "
            + "FROM propietario p ";

    @Override
    protected Propietario mapear(ResultSet rs) throws SQLException {
        Propietario p = new Propietario(rs.getInt("id_propietario"), rs.getString("nombre"), rs.getString("telefono"));
        p.setDni(rs.getString("dni"));
        p.setDireccion(rs.getString("direccion"));
        p.setEmail(rs.getString("email"));
        p.setNumeroMascotas(rs.getInt("n_mascotas"));
        return p;
    }

    @Override
    public Propietario guardar(Propietario p) {
        p.setId(insertar("INSERT INTO propietario (dni, nombre, telefono, direccion, email) VALUES (?, ?, ?, ?, ?)",
                p.getDni(), p.getNombre(), p.getTelefono(), p.getDireccion(), p.getEmail()));
        return p;
    }

    @Override
    public Propietario actualizar(Propietario p) {
        ejecutar("UPDATE propietario SET dni = ?, nombre = ?, telefono = ?, direccion = ?, email = ? WHERE id_propietario = ?",
                p.getDni(), p.getNombre(), p.getTelefono(), p.getDireccion(), p.getEmail(), p.getId());
        return p;
    }

    @Override
    public void eliminar(int id) {
        ejecutar("DELETE FROM propietario WHERE id_propietario = ?", id);
    }

    @Override
    public Optional<Propietario> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE p.id_propietario = ?", id);
    }

    @Override
    public List<Propietario> listar() {
        return consultar(SELECT + "WHERE p.activo ORDER BY p.id_propietario");   // solo los activos, por ID
    }

    /** Busca por nombre, DNI o teléfono. */
    public List<Propietario> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return listar();
        }
        String like = "%" + texto.trim().toLowerCase() + "%";
        return consultar(SELECT + "WHERE p.activo AND (LOWER(p.nombre) LIKE ? OR p.dni LIKE ? OR p.telefono LIKE ?) ORDER BY p.id_propietario",
                like, like, like);
    }

    /** ¿dni repetido? (otro propietario con el mismo DNI) */
    public boolean existeDni(String dni, int idDistinto) {
        return contarFilas("SELECT COUNT(*) FROM propietario WHERE dni = ? AND id_propietario <> ?", dni, idDistinto) > 0;
    }

    /** Baja lógica: el propietario y sus mascotas dejan de salir, pero sus ventas y atenciones se conservan. */
    public void darDeBaja(int idPropietario) {
        enTransaccion(cn -> {
            ejecutarEn(cn, "UPDATE mascota SET activo = FALSE WHERE id_propietario = ?", idPropietario);
            ejecutarEn(cn, "UPDATE propietario SET activo = FALSE WHERE id_propietario = ?", idPropietario);
            return null;
        });
    }

    /** Mascotas que se pueden borrar junto con él: las que no tienen atenciones. */
    public boolean mascotasTienenAtenciones(int idPropietario) {
        return contarFilas("SELECT COUNT(*) FROM atencion a JOIN mascota m ON m.id_mascota = a.id_mascota "
                + "WHERE m.id_propietario = ?", idPropietario) > 0;
    }

    /** Borrado DEFINITIVO del propietario con sus mascotas (y las citas y vacunaciones de ellas). */
    public void borrarConMascotas(int idPropietario) {
        enTransaccion(cn -> {
            String deSusMascotas = "IN (SELECT id_mascota FROM mascota WHERE id_propietario = ?)";
            ejecutarEn(cn, "DELETE FROM cita WHERE id_mascota " + deSusMascotas, idPropietario);
            ejecutarEn(cn, "DELETE FROM vacunacion WHERE id_mascota " + deSusMascotas, idPropietario);
            ejecutarEn(cn, "DELETE FROM mascota WHERE id_propietario = ?", idPropietario);
            ejecutarEn(cn, "DELETE FROM propietario WHERE id_propietario = ?", idPropietario);
            return null;
        });
    }

    public int contarMascotas(int idPropietario) {
        return contarFilas("SELECT COUNT(*) FROM mascota WHERE id_propietario = ?", idPropietario);
    }

    public int contarVentas(int idPropietario) {
        return contarFilas("SELECT COUNT(*) FROM venta WHERE id_propietario = ?", idPropietario);
    }
}
