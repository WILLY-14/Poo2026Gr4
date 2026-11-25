package pe.edu.upeu.padronmascota.padronmascota.componentes;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Mensaje pequeño que aparece abajo de la ventana y se desvanece solo
 * (no hay que darle "Aceptar" como a un Alert).
 */
public class Notificacion {

    public enum Tipo { EXITO, ERROR, INFO }

    private Notificacion() {
    }

    public static void mostrar(Window ventana, String mensaje, Tipo tipo) {
        Label label = new Label(mensaje);
        label.getStyleClass().addAll("notificacion", "notificacion-" + tipo.name().toLowerCase());

        StackPane caja = new StackPane(label);
        caja.setPadding(new Insets(4));

        Popup popup = new Popup();
        popup.getContent().add(caja);
        popup.setAutoHide(true);
        popup.show(ventana);
        // Centrado horizontal, cerca del borde de abajo
        popup.setX(ventana.getX() + (ventana.getWidth() - caja.getWidth()) / 2);
        popup.setY(ventana.getY() + ventana.getHeight() - caja.getHeight() - 60);

        caja.setOpacity(0);
        FadeTransition entrar = new FadeTransition(Duration.millis(250), caja);
        entrar.setToValue(1);
        FadeTransition salir = new FadeTransition(Duration.millis(400), caja);
        salir.setToValue(0);
        SequentialTransition animacion = new SequentialTransition(entrar, new PauseTransition(Duration.seconds(2)), salir);
        animacion.setOnFinished(e -> popup.hide());
        animacion.play();
    }
}
