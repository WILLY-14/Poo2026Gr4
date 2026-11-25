package pe.edu.upeu.padronmascota.padronmascota.componentes;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.function.Function;

/**
 * Autocompletar para un TextField: mientras escribes, abajo salen sugerencias.
 * Son solo ayuda: puedes escribir cualquier cosa (por ejemplo una raza nueva).
 *
 *   Sugerencias.en(txtRaza, texto -> nombresDeRazas(texto));
 */
public final class Sugerencias {

    private Sugerencias() {
    }

    /** buscar recibe lo escrito (en minúsculas) y devuelve los textos a sugerir. */
    public static void en(TextField campo, Function<String, List<String>> buscar) {
        ContextMenu menu = new ContextMenu();
        boolean[] eligiendo = {false};
        campo.textProperty().addListener((obs, antes, ahora) -> {
            if (eligiendo[0]) {
                return;
            }
            String t = ahora == null ? "" : ahora.trim().toLowerCase();
            menu.getItems().clear();
            if (!t.isEmpty()) {
                for (String texto : buscar.apply(t)) {
                    if (texto.equalsIgnoreCase(ahora.trim())) {
                        continue;   // ya está escrito completo
                    }
                    MenuItem item = new MenuItem(texto);
                    item.setOnAction(e -> {
                        eligiendo[0] = true;
                        campo.setText(texto);
                        campo.positionCaret(texto.length());
                        eligiendo[0] = false;
                    });
                    menu.getItems().add(item);
                }
            }
            if (menu.getItems().isEmpty() || !campo.isFocused()) {
                menu.hide();
            } else if (!menu.isShowing()) {
                menu.show(campo, Side.BOTTOM, 0, 0);
            }
        });
        campo.focusedProperty().addListener((obs, antes, enfocado) -> {
            if (!enfocado) {
                menu.hide();
            }
        });
    }

    /** Pone un texto sin abrir las sugerencias (al llenar el formulario para editar). */
    public static void ponerTexto(TextField campo, String texto) {
        campo.setText(texto == null ? "" : texto);
    }
}
