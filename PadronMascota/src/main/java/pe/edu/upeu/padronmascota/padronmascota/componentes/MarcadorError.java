package pe.edu.upeu.padronmascota.padronmascota.componentes;

import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Pinta de rojo los campos con error y les pone un tooltip con el mensaje.
 * Recuerda qué campos marcó para poder limpiarlos todos de una vez.
 */
public class MarcadorError {

    private final List<Control> marcados = new ArrayList<>();
    private final List<String> mensajes = new ArrayList<>();

    public void marcar(Control campo, String mensaje) {
        if (!campo.getStyleClass().contains("campo-error")) {
            campo.getStyleClass().add("campo-error");
        }
        Tooltip tip = new Tooltip(mensaje);
        tip.getStyleClass().add("tooltip-error");
        tip.setShowDelay(Duration.millis(100));
        campo.setTooltip(tip);
        marcados.add(campo);
        mensajes.add(mensaje);
    }

    /** El primer mensaje de error (para mostrarlo como aviso, no solo al pasar el mouse). */
    public String primerMensaje() {
        return mensajes.isEmpty() ? null : mensajes.get(0);
    }

    public void limpiar() {
        for (Control c : marcados) {
            c.getStyleClass().remove("campo-error");
            c.setTooltip(null);
        }
        marcados.clear();
        mensajes.clear();
    }

    public boolean hayErrores() {
        return !marcados.isEmpty();
    }

    /** Pone el cursor en el primer campo con error. */
    public void enfocarPrimero() {
        if (!marcados.isEmpty()) {
            marcados.get(0).requestFocus();
        }
    }
}
