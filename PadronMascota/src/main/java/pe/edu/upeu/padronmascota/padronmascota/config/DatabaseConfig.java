package pe.edu.upeu.padronmascota.padronmascota.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Configuración de la base de datos H2 (embebida, en archivo) con pool HikariCP,
 * igual idea que en SysVentas: no hay que instalar ningún servidor.
 *
 * <p>Al arrancar:
 * <ol>
 *   <li>Abre (o crea) el archivo {@code data/padron_mascota_pro.mv.db}.</li>
 *   <li>Ejecuta {@code db/schema.sql} (crea las 26 tablas si no existen).</li>
 *   <li>Si no hay propietarios, ejecuta {@code db/datos.sql} (datos del Excel).</li>
 * </ol>
 */
public final class DatabaseConfig {

    private static final String PROPERTIES_FILE = "application.properties";
    private static final String DEFAULT_URL = "jdbc:h2:file:./data/padron_mascota_pro;AUTO_SERVER=TRUE";

    private static volatile HikariDataSource dataSource;

    private DatabaseConfig() {
    }

    /** Abre el pool una sola vez y prepara las tablas. */
    public static synchronized void init() {
        if (dataSource != null) {
            return;
        }
        Properties props = loadProperties();
        String url = props.getProperty("db.url", DEFAULT_URL);
        // "./data/..." se toma desde la carpeta del programa (así el .exe siempre encuentra su base)
        String base = CarpetaApp.base().toString().replace('\\', '/');
        url = url.replace("jdbc:h2:file:./", "jdbc:h2:file:" + base + "/");

        HikariConfig config = new HikariConfig();
        config.setPoolName("PadronMascotaPool");
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl(url);
        config.setUsername(props.getProperty("db.username", "sa"));
        config.setPassword(props.getProperty("db.password", ""));
        // App de escritorio con un solo usuario: un pool pequeño es suficiente.
        config.setMaximumPoolSize(5);

        HikariDataSource ds;
        try {
            ds = new HikariDataSource(config);
        } catch (RuntimeException e) {
            throw new IllegalStateException("No fue posible abrir la base de datos H2: " + url, e);
        }
        try {
            ejecutarScript(ds, props.getProperty("db.ddl.script", "db/schema.sql"));
            // Datos de ejemplo del Excel: solo si en application.properties dice db.datosEjemplo=true
            boolean ejemplo = Boolean.parseBoolean(props.getProperty("db.datosEjemplo", "false"));
            if (ejemplo && contar(ds, "propietario") == 0) {
                ejecutarScript(ds, props.getProperty("db.datos.script", "db/datos.sql"));
            }
            reiniciarIdsVacios(ds);
        } catch (RuntimeException e) {
            ds.close();
            throw e;
        }
        dataSource = ds;
        System.out.println("H2 listo: " + url);
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            init();
        }
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    /** Cierra el pool al salir de la aplicación. */
    public static synchronized void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    // ------------------------------------------------------------------
    //  IDs y "empezar de cero"
    // ------------------------------------------------------------------

    /** Tablas fijas (las llenan los enums con MERGE en schema.sql): nunca se vacían. */
    private static final java.util.Set<String> CATALOGOS_FIJOS = java.util.Set.of(
            "ESPECIE", "ESTADO_VACUNACION", "ESTADO_CITA", "ROL", "METODO_PAGO", "CATEGORIA_PRODUCTO");

    /**
     * Si una tabla quedó vacía, su ID vuelve a empezar en 1.
     * (La base nunca reutiliza números por sí sola: sin esto, tras borrar 1..7 el siguiente sería 8.)
     */
    public static void reiniciarIdsVacios() {
        reiniciarIdsVacios(getDataSource());
    }

    private static void reiniciarIdsVacios(DataSource ds) {
        try (Connection cn = ds.getConnection(); Statement st = cn.createStatement()) {
            for (String[] t : columnasIdentity(cn)) {
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + t[0])) {
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        st.execute("ALTER TABLE " + t[0] + " ALTER COLUMN " + t[1] + " RESTART WITH 1");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reiniciando los IDs: " + e.getMessage(), e);
        }
    }

    /** [tabla, columna] de cada ID autonumérico de la base. */
    private static java.util.List<String[]> columnasIdentity(Connection cn) throws SQLException {
        java.util.List<String[]> lista = new java.util.ArrayList<>();
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT c.TABLE_NAME, c.COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS c "
                     + "JOIN INFORMATION_SCHEMA.TABLES t ON t.TABLE_SCHEMA = c.TABLE_SCHEMA AND t.TABLE_NAME = c.TABLE_NAME "
                     + "WHERE c.TABLE_SCHEMA = 'PUBLIC' AND c.IS_IDENTITY = 'YES' AND t.TABLE_TYPE = 'BASE TABLE'")) {
            while (rs.next()) {
                lista.add(new String[]{rs.getString(1), rs.getString(2)});
            }
        }
        return lista;
    }

    /**
     * EMPEZAR DE CERO: borra todos los registros (mascotas, propietarios, ventas, compras, razas,
     * colores, productos...) y deja los IDs en 1. Solo se conservan las tablas fijas y el usuario
     * que está usando el sistema, para poder seguir entrando con su misma clave.
     */
    public static void borrarTodo(int idUsuarioQueQueda) {
        try (Connection cn = getConnection(); Statement st = cn.createStatement()) {
            cn.setAutoCommit(false);
            try {
                st.execute("SET REFERENTIAL_INTEGRITY FALSE");
                java.util.List<String> tablas = new java.util.ArrayList<>();
                try (ResultSet rs = st.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
                        + "WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_TYPE = 'BASE TABLE'")) {
                    while (rs.next()) {
                        tablas.add(rs.getString(1));
                    }
                }
                for (String t : tablas) {
                    if (CATALOGOS_FIJOS.contains(t)) {
                        continue;
                    }
                    if (t.equals("USUARIO")) {
                        try (PreparedStatement ps = cn.prepareStatement("DELETE FROM usuario WHERE id_usuario <> ?")) {
                            ps.setInt(1, idUsuarioQueQueda);
                            ps.executeUpdate();
                        }
                        st.executeUpdate("UPDATE usuario SET id_veterinario = NULL");
                    } else {
                        st.executeUpdate("DELETE FROM " + t);
                    }
                }
                cn.commit();
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            } finally {
                st.execute("SET REFERENTIAL_INTEGRITY TRUE");
                cn.setAutoCommit(true);
            }
            // El usuario que queda conserva su ID; los demás vuelven a 1
            for (String[] t : columnasIdentity(cn)) {
                int siguiente = 1;
                try (ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(" + t[1] + "), 0) + 1 FROM " + t[0])) {
                    rs.next();
                    siguiente = rs.getInt(1);
                }
                st.execute("ALTER TABLE " + t[0] + " ALTER COLUMN " + t[1] + " RESTART WITH " + siguiente);
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo vaciar la base: " + e.getMessage(), e);
        }
    }

    /** Cuántas filas tiene una tabla (para saber si hay que cargar datos de ejemplo). */
    private static int contar(DataSource ds, String tabla) {
        try (Connection cn = ds.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabla)) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new IllegalStateException("Error revisando la tabla " + tabla, e);
        }
    }

    /** Lee un .sql de resources y lo ejecuta sentencia por sentencia (separadas por ;). */
    private static void ejecutarScript(DataSource ds, String script) {
        try (InputStream in = DatabaseConfig.class.getResourceAsStream("/" + script)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró el script " + script);
            }
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8)
                    .lines()
                    .filter(linea -> !linea.strip().startsWith("--"))
                    .collect(Collectors.joining("\n"));

            // TRANSACCIÓN: o se ejecuta todo el script, o no se guarda nada
            try (Connection cn = ds.getConnection(); Statement st = cn.createStatement()) {
                cn.setAutoCommit(false);
                try {
                    for (String sentencia : sql.split(";")) {
                        if (!sentencia.isBlank()) {
                            st.execute(sentencia);
                        }
                    }
                    cn.commit();
                } catch (SQLException e) {
                    cn.rollback();
                    throw e;
                }
            }
        } catch (IOException | SQLException e) {
            throw new IllegalStateException("Error ejecutando el script " + script, e);
        }
    }

    private static Properties loadProperties() {
        try (InputStream in = DatabaseConfig.class.getResourceAsStream("/" + PROPERTIES_FILE)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró " + PROPERTIES_FILE);
            }
            Properties props = new Properties();
            props.load(in);
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo " + PROPERTIES_FILE, e);
        }
    }

    // Prueba rápida: dale Run a este main para saber si conecta
    public static void main(String[] args) {
        try (Connection cn = getConnection()) {
            System.out.println("Conexión exitosa a: " + cn.getMetaData().getURL());
        } catch (SQLException | RuntimeException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        } finally {
            shutdown();
        }
    }
}
