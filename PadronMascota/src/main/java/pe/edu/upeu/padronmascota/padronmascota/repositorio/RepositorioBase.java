package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Clase padre de todos los repositorios (HERENCIA).
 * Tiene el código JDBC que se repetiría en cada uno: abrir conexión,
 * poner los "?" y recorrer el ResultSet. Las hijas solo escriben su SQL.
 */
public abstract class RepositorioBase<T> {

    /** Cada hija dice cómo convertir una fila en su objeto. */
    protected abstract T mapear(ResultSet rs) throws SQLException;

    /** SELECT que devuelve varias filas. */
    protected List<T> consultar(String sql, Object... params) {
        try (Connection cn = DatabaseConfig.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ponerParametros(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar: " + e.getMessage(), e);
        }
    }

    /** SELECT que devuelve una sola fila, o vacío si no hay. */
    protected Optional<T> consultarUno(String sql, Object... params) {
        List<T> lista = consultar(sql, params);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    /** INSERT que devuelve el id que generó la base de datos (IDENTITY). */
    protected int insertar(String sql, Object... params) {
        try (Connection cn = DatabaseConfig.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ponerParametros(ps, params);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                }
                throw new SQLException("La base de datos no devolvió el id generado");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar: " + e.getMessage(), e);
        }
    }

    /** UPDATE o DELETE. Devuelve cuántas filas cambió. */
    protected int ejecutar(String sql, Object... params) {
        try (Connection cn = DatabaseConfig.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ponerParametros(ps, params);
            int filas = ps.executeUpdate();
            if (filas > 0 && sql.stripLeading().toUpperCase().startsWith("DELETE")) {
                DatabaseConfig.reiniciarIdsVacios();   // si la tabla quedó vacía, el próximo ID es 1
            }
            return filas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al ejecutar: " + e.getMessage(), e);
        }
    }

    /** SELECT de una sola columna de texto (ej. nombres): devuelve la lista. */
    protected List<String> textos(String sql, Object... params) {
        try (Connection cn = DatabaseConfig.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ponerParametros(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<String> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(rs.getString(1));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar: " + e.getMessage(), e);
        }
    }

    /** SELECT COUNT(*) ... : devuelve el número. */
    protected int contarFilas(String sql, Object... params) {
        try (Connection cn = DatabaseConfig.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ponerParametros(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar: " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------
    //  TRANSACCIONES: varias sentencias que se guardan todas juntas o ninguna
    // ------------------------------------------------------------------

    /** Lo que se hace dentro de la transacción (recibe la conexión abierta). */
    @FunctionalInterface
    protected interface TrabajoSql<R> {
        R ejecutar(Connection cn) throws SQLException;
    }

    /**
     * Abre una conexión, apaga el auto-commit, ejecuta el trabajo y hace commit.
     * Si algo falla (SQL o una regla del negocio), hace rollback: no se guarda nada.
     */
    protected <R> R enTransaccion(TrabajoSql<R> trabajo) {
        try (Connection cn = DatabaseConfig.getConnection()) {
            cn.setAutoCommit(false);
            try {
                R resultado = trabajo.ejecutar(cn);
                cn.commit();
                DatabaseConfig.reiniciarIdsVacios();   // por si la transacción borró y dejó tablas vacías
                return resultado;
            } catch (SQLException | RuntimeException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en la transacción: " + e.getMessage(), e);
        }
    }

    /** INSERT dentro de una transacción. Devuelve el id generado. */
    protected static int insertarEn(Connection cn, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ponerParametros(ps, params);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                claves.next();
                return claves.getInt(1);
            }
        }
    }

    /** UPDATE o DELETE dentro de una transacción. */
    protected static int ejecutarEn(Connection cn, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ponerParametros(ps, params);
            return ps.executeUpdate();
        }
    }

    /**
     * Stock actual de un producto según la vista v_stock_producto
     * (comprados - vendidos - usados). Dentro de la transacción ya cuenta lo que acabamos de insertar.
     */
    protected static int stockActual(Connection cn, int idProducto) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT comprados - vendidos - usados FROM v_stock_producto WHERE id_producto = ?")) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** Si después de descontar el stock quedó negativo, lanza el error (y la transacción hace rollback). */
    protected static void verificarStock(Connection cn, int idProducto, String nombre) throws SQLException {
        int stock = stockActual(cn, idProducto);
        if (stock < 0) {
            throw new IllegalStateException("No hay stock suficiente de " + nombre + " (faltan " + (-stock) + ")");
        }
    }

    /** LocalDate desde una columna DATE (null si está vacía). */
    protected static java.time.LocalDate fecha(ResultSet rs, String columna) throws SQLException {
        return rs.getObject(columna, java.time.LocalDate.class);
    }

    /** Integer desde una columna que puede ser NULL (getInt devolvería 0). */
    protected static Integer enteroONull(ResultSet rs, String columna) throws SQLException {
        int valor = rs.getInt(columna);
        return rs.wasNull() ? null : valor;
    }

    // Reemplaza cada "?" en orden. En JDBC el primer "?" es el 1, no el 0.
    private static void ponerParametros(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}
