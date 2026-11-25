package pe.edu.upeu.padronmascota.padronmascota.componentes;

import pe.edu.upeu.padronmascota.padronmascota.PadronMascotaApp;
import pe.edu.upeu.padronmascota.padronmascota.config.ContenedorApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Maneja la ventana (Stage): carga los FXML con el ContenedorApp y los textos
 * del idioma actual, y recuerda el tema (CSS) elegido.
 */
public class GestorVentana {

    private static final String BASE_CSS = "/css/base.css";

    private static Stage stage;
    private static String temaActual;   // null = tema claro

    private GestorVentana() {
    }

    public static void setStage(Stage s) {
        stage = s;
    }

    public static Stage getStage() {
        return stage;
    }

    public static String getTemaActual() {
        return temaActual;
    }

    /** Carga un FXML de la carpeta vistas. El ContenedorApp crea su controlador. */
    public static Parent cargar(String vista) {
        try {
            FXMLLoader loader = new FXMLLoader(PadronMascotaApp.class.getResource("vistas/" + vista));
            loader.setResources(Idioma.textos());   // para los text="%clave" del FXML
            loader.setControllerFactory(ContenedorApp.getInstancia()::crearControlador);
            Parent raiz = loader.load();
            // Guardamos el controlador en la vista para poder refrescarla al volver a su pestaña
            raiz.getProperties().put("controlador", loader.getController());
            return raiz;
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo abrir la vista " + vista, e);
        }
    }

    /** Muestra la ventana principal (grande). */
    public static void mostrar(String vista) {
        mostrar(vista, 1280, 760, true);
        stage.setMaximized(true); // la ventana principal ocupa toda la pantalla
    }

    /** Muestra una vista ocupando toda la ventana, con el tamaño indicado. */
    public static void mostrar(String vista, double ancho, double alto, boolean redimensionable) {
        Scene escena = new Scene(cargar(vista), ancho, alto);
        if (!redimensionable) {
            stage.setMaximized(false); // primero se quita el "maximizado" de la ventana principal
        }
        stage.setScene(escena);
        stage.setTitle(Idioma.t("app.titulo"));
        stage.setResizable(redimensionable);
        if (!redimensionable) {
            stage.sizeToScene();       // y la ventana vuelve al tamaño exacto del login (480 x 530)
            // Windows a veces aplica el "maximizado" un instante después: se revisa otra vez
            javafx.application.Platform.runLater(() -> {
                if (stage.isMaximized()) {
                    stage.setMaximized(false);
                }
                stage.sizeToScene();
                stage.centerOnScreen();
            });
        }
        stage.centerOnScreen();
        cambiarTema(temaActual);
    }

    /** temaCss = null deja solo el estilo base (claro). */
    public static void cambiarTema(String temaCss) {
        temaActual = temaCss;
        Scene escena = stage.getScene();
        escena.getStylesheets().setAll(GestorVentana.class.getResource(BASE_CSS).toExternalForm());
        if (temaCss != null) {
            escena.getStylesheets().add(GestorVentana.class.getResource(temaCss).toExternalForm());
        }
    }
}
