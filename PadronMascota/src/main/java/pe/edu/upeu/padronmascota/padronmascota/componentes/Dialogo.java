package pe.edu.upeu.padronmascota.padronmascota.componentes;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/** Ventanas de confirmación y error. */
public class Dialogo {

    private Dialogo() {
    }

    /** El diálogo usa los mismos colores (tema) que la ventana principal. */
    private static void estilo(Alert a) {
        javafx.stage.Stage principal = GestorVentana.getStage();
        if (principal != null && principal.getScene() != null) {
            a.initOwner(principal);
            a.getDialogPane().getStylesheets().setAll(principal.getScene().getStylesheets());
        }
    }

    public static boolean confirmar(String mensaje) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        a.setTitle(Idioma.t("dialogo.confirmar"));
        estilo(a);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    public static void error(String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR, mensaje);
        a.setHeaderText(null);
        a.setTitle(Idioma.t("dialogo.error"));
        estilo(a);
        a.showAndWait();
    }

    /** Ventana con texto en letra de ancho fijo (para que las columnas del detalle queden alineadas). */
    public static void detalle(String titulo, String texto) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        javafx.scene.control.Label contenido = new javafx.scene.control.Label(texto);
        contenido.setStyle("-fx-font-family: monospace;");
        a.getDialogPane().setContent(contenido);
        a.setResizable(true);
        estilo(a);
        a.showAndWait();
    }

    /** Ventana con información (por ejemplo, el detalle de una venta o una receta). */
    public static void info(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, mensaje);
        a.setHeaderText(titulo);
        a.setTitle(titulo);
        a.getDialogPane().setMinWidth(460);
        a.setResizable(true);
        estilo(a);
        a.showAndWait();
    }
}
