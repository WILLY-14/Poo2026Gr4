package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoVacunacion;
import pe.edu.upeu.padronmascota.padronmascota.enums.Sexo;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Color;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Mascota;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Raza;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Tabla mascota con JOIN a raza, color y propietario (la "Consulta mascota" del Excel). */
public class MascotaRepositorio extends RepositorioBase<Mascota> implements IRepositorio<Mascota> {

    private static final String SELECT =
            "SELECT m.id_mascota, m.nombre, m.id_especie, m.id_raza, r.nombre AS raza, m.id_color, c.nombre AS color, "
            + "m.sexo, m.edad_aprox, m.fecha_registro, m.id_estado, m.activo, m.observaciones, "
            + "p.id_propietario, p.nombre AS propietario, p.telefono, p.dni "
            + "FROM mascota m "
            + "LEFT JOIN raza r ON r.id_raza = m.id_raza "      // LEFT: la mascota "por completar" aún no tiene raza
            + "LEFT JOIN color c ON c.id_color = m.id_color "
            + "JOIN propietario p ON p.id_propietario = m.id_propietario ";

    @Override
    protected Mascota mapear(ResultSet rs) throws SQLException {
        Mascota m = new Mascota();
        m.setId(rs.getInt("id_mascota"));
        m.setNombre(rs.getString("nombre"));
        m.setEspecie(Especie.desdeId(rs.getInt("id_especie")));
        Integer idRaza = enteroONull(rs, "id_raza");
        m.setRaza(idRaza == null ? null : new Raza(idRaza, rs.getString("raza"), m.getEspecie()));
        Integer idColor = enteroONull(rs, "id_color");
        m.setColor(idColor == null ? null : new Color(idColor, rs.getString("color")));
        String sexo = rs.getString("sexo");
        m.setSexo(sexo == null ? null : Sexo.desdeNombre(sexo));
        m.setEdadAprox(rs.getInt("edad_aprox"));
        m.setFechaRegistro(fecha(rs, "fecha_registro"));
        m.setEstado(EstadoVacunacion.desdeId(rs.getInt("id_estado")));
        m.setActivo(rs.getBoolean("activo"));
        m.setObservaciones(rs.getString("observaciones"));
        Propietario p = new Propietario(rs.getInt("id_propietario"), rs.getString("propietario"), rs.getString("telefono"));
        p.setDni(rs.getString("dni"));
        m.setPropietario(p);
        return m;
    }

    /** Una mascota "corta" (solo lo necesario) para las tablas de citas, atenciones y vacunas. */
    static Mascota corta(int id, String nombre, int idEspecie, int idPropietario, String propietario) {
        Mascota m = new Mascota();
        m.setId(id);
        m.setNombre(nombre);
        m.setEspecie(Especie.desdeId(idEspecie));
        m.setPropietario(new Propietario(idPropietario, propietario, null));
        return m;
    }

    @Override
    public Mascota guardar(Mascota m) {
        m.setId(insertar("INSERT INTO mascota (nombre, id_especie, id_raza, id_color, sexo, edad_aprox, fecha_registro, "
                        + "id_propietario, id_estado, activo, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                m.getNombre(), m.getEspecie().getId(), idDe(m.getRaza()), idDe(m.getColor()),
                m.getSexo() == null ? null : m.getSexo().getNombre(),
                m.getEdadAprox(), m.getFechaRegistro(), m.getPropietario().getId(), m.getEstado().getId(),
                m.isActivo(), m.getObservaciones()));
        return m;
    }

    @Override
    public Mascota actualizar(Mascota m) {
        ejecutar("UPDATE mascota SET nombre = ?, id_especie = ?, id_raza = ?, id_color = ?, sexo = ?, edad_aprox = ?, "
                        + "fecha_registro = ?, id_propietario = ?, id_estado = ?, activo = ?, observaciones = ? WHERE id_mascota = ?",
                m.getNombre(), m.getEspecie().getId(), idDe(m.getRaza()), idDe(m.getColor()),
                m.getSexo() == null ? null : m.getSexo().getNombre(),
                m.getEdadAprox(), m.getFechaRegistro(), m.getPropietario().getId(), m.getEstado().getId(),
                m.isActivo(), m.getObservaciones(), m.getId());
        return m;
    }

    private static Integer idDe(pe.edu.upeu.padronmascota.padronmascota.modelo.Entidad e) {
        return e == null ? null : e.getId();
    }

    /** Borrado lógico: no se elimina la fila, se marca activo = FALSE. */
    @Override
    public void eliminar(int id) {
        ejecutar("UPDATE mascota SET activo = FALSE WHERE id_mascota = ?", id);
    }

    /** Cuántas atenciones tiene (si tiene, no se puede borrar de verdad: hay recetas, insumos o ventas detrás). */
    public int contarAtenciones(int id) {
        return contarFilas("SELECT COUNT(*) FROM atencion WHERE id_mascota = ?", id);
    }

    /** Lo que se borraría junto con la mascota: [citas, vacunaciones, atenciones, recetas, ventas ligadas]. */
    public int[] contarCitasYVacunas(int id) {
        String deSusAtenciones = "IN (SELECT id_atencion FROM atencion WHERE id_mascota = ?)";
        return new int[]{contarFilas("SELECT COUNT(*) FROM cita WHERE id_mascota = ?", id),
                contarFilas("SELECT COUNT(*) FROM vacunacion WHERE id_mascota = ?", id),
                contarAtenciones(id),
                contarFilas("SELECT COUNT(*) FROM receta WHERE id_atencion " + deSusAtenciones, id),
                contarFilas("SELECT COUNT(*) FROM venta WHERE id_atencion " + deSusAtenciones, id)};
    }

    /**
     * Borrado DEFINITIVO con todo su historial, en una sola transacción:
     * recetas (y su detalle), atenciones (y sus insumos), citas, vacunaciones y la mascota.
     * Las ventas NO se borran (es plata que ya entró): solo se les quita la atención.
     */
    public void borrarDefinitivo(int id) {
        enTransaccion(cn -> {
            String deSusAtenciones = "IN (SELECT id_atencion FROM atencion WHERE id_mascota = ?)";
            ejecutarEn(cn, "UPDATE venta SET id_atencion = NULL WHERE id_atencion " + deSusAtenciones, id);
            ejecutarEn(cn, "DELETE FROM detalle_receta WHERE id_receta IN (SELECT id_receta FROM receta WHERE id_atencion "
                    + deSusAtenciones + ")", id);
            ejecutarEn(cn, "DELETE FROM receta WHERE id_atencion " + deSusAtenciones, id);
            ejecutarEn(cn, "DELETE FROM atencion_insumo WHERE id_atencion " + deSusAtenciones, id);
            ejecutarEn(cn, "DELETE FROM atencion WHERE id_mascota = ?", id);
            ejecutarEn(cn, "DELETE FROM cita WHERE id_mascota = ?", id);
            ejecutarEn(cn, "DELETE FROM vacunacion WHERE id_mascota = ?", id);
            ejecutarEn(cn, "DELETE FROM mascota WHERE id_mascota = ?", id);
            return null;
        });
    }

    public void reactivar(int id) {
        ejecutar("UPDATE mascota SET activo = TRUE WHERE id_mascota = ?", id);
    }

    @Override
    public Optional<Mascota> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE m.id_mascota = ?", id);
    }

    /** Solo las activas (como el filtro del Excel). */
    @Override
    public List<Mascota> listar() {
        return consultar(SELECT + "WHERE m.activo ORDER BY m.nombre");
    }

    /**
     * Filtro de mascotas del Excel. especie / estado null = Todos.
     * soloActivas = false muestra también las dadas de baja.
     */
    public List<Mascota> buscar(String texto, Especie especie, EstadoVacunacion estado, boolean soloActivas) {
        StringBuilder sql = new StringBuilder(SELECT).append("WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        if (soloActivas) {
            sql.append("AND m.activo ");
        }
        if (texto != null && !texto.isBlank()) {
            sql.append("AND (LOWER(m.nombre) LIKE ? OR LOWER(r.nombre) LIKE ? OR LOWER(p.nombre) LIKE ?) ");
            String like = "%" + texto.trim().toLowerCase() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (especie != null) {
            sql.append("AND m.id_especie = ? ");
            params.add(especie.getId());
        }
        if (estado != null) {
            sql.append("AND m.id_estado = ? ");
            params.add(estado.getId());
        }
        sql.append("ORDER BY m.id_mascota");
        return consultar(sql.toString(), params.toArray());
    }

    /** Mascotas activas de un propietario (para el combo de Ventas). */
    public List<Mascota> dePropietario(int idPropietario) {
        return consultar(SELECT + "WHERE m.activo AND m.id_propietario = ? ORDER BY m.nombre", idPropietario);
    }
}
