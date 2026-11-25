package pe.edu.upeu.padronmascota.padronmascota.componentes;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Textos de la aplicación en varios idiomas (internacionalización, "i18n").
 *
 * <p>Los textos están FUERA del código, en la carpeta {@code language/} del proyecto:
 * <ul>
 *   <li>{@code idiomas-es.properties}, {@code idiomas-en.properties}, {@code idiomas-fr.properties}:
 *       una línea {@code clave=texto} por cada texto.</li>
 *   <li>{@code PadronMascota.properties}: guarda el idioma elegido ({@code padronmascota.idioma=es}).</li>
 * </ul>
 * En el FXML se usa {@code text="%clave"} y en Java {@code Idioma.t("clave")}.
 */
public final class Idioma {

    private static final Path CARPETA = pe.edu.upeu.padronmascota.padronmascota.config.CarpetaApp.base().resolve("language");
    private static final Path CONFIG = CARPETA.resolve("PadronMascota.properties");
    private static final String CLAVE_IDIOMA = "padronmascota.idioma";

    /** Idiomas disponibles: código y nombre que se muestra. */
    public static final String[][] DISPONIBLES = {
            {"es", "Español"},
            {"en", "English"},
            {"fr", "Français"}
    };

    private static String actual;
    private static ResourceBundle textos;

    private Idioma() {
    }

    /** Código del idioma actual: es, en o fr. */
    public static String actual() {
        if (actual == null) {
            actual = leerConfig().getProperty(CLAVE_IDIOMA, "es");
        }
        return actual;
    }

    /** Los textos del idioma actual (el FXMLLoader los usa para los %clave). */
    public static ResourceBundle textos() {
        if (textos == null) {
            textos = cargar(actual());
        }
        return textos;
    }

    /** Texto traducido. Si la clave no existe, devuelve la clave para que se note. */
    public static String t(String clave) {
        try {
            return textos().getString(clave);
        } catch (MissingResourceException e) {
            return clave;
        }
    }

    /** Texto con datos: t("mascota.total", 5) con "mascota.total=%d mascota(s)". */
    public static String t(String clave, Object... datos) {
        return String.format(t(clave), datos);
    }

    /** Cambia el idioma y lo guarda para la próxima vez que se abra el sistema. */
    public static void cambiar(String codigo) {
        actual = codigo;
        textos = cargar(codigo);
        Properties config = leerConfig();
        config.setProperty(CLAVE_IDIOMA, codigo);
        try (Writer w = Files.newBufferedWriter(CONFIG, StandardCharsets.UTF_8)) {
            config.store(w, "Idioma del sistema Padron Mascota (es, en, fr)");
        } catch (IOException e) {
            System.out.println("No se pudo guardar el idioma: " + e.getMessage());
        }
    }

    private static ResourceBundle cargar(String codigo) {
        Path archivo = CARPETA.resolve("idiomas-" + codigo + ".properties");
        if (!Files.exists(archivo)) {
            throw new IllegalStateException("No se encontró " + archivo.toAbsolutePath()
                    + ". La carpeta language debe estar en la raíz del proyecto.");
        }
        try (Reader r = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            return new PropertyResourceBundle(r);
        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo " + archivo, e);
        }
    }

    private static Properties leerConfig() {
        Properties p = new Properties();
        if (Files.exists(CONFIG)) {
            try (Reader r = Files.newBufferedReader(CONFIG, StandardCharsets.UTF_8)) {
                p.load(r);
            } catch (IOException e) {
                System.out.println("No se pudo leer " + CONFIG + ": " + e.getMessage());
            }
        }
        return p;
    }
}
