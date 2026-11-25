package pe.edu.upeu.padronmascota.padronmascota.componentes;

import pe.edu.upeu.padronmascota.padronmascota.dto.OpcionCombo;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

import java.util.Collection;
import java.util.function.Function;

/** Ayudas para llenar ComboBox sin repetir código en cada pantalla. */
public final class Combos {

    private Combos() {
    }

    /** Llena el combo con la lista (se ve el toString de cada objeto). */
    public static <T> void llenar(ComboBox<T> combo, Collection<T> items) {
        combo.setItems(FXCollections.observableArrayList(items));
    }

    /** Combo de filtro: primero "Todos" (valor null) y luego cada item. Deja elegido "Todos". */
    public static <T> void filtro(ComboBox<OpcionCombo<T>> combo, String textoTodos, Collection<T> items,
                                  Function<T, String> texto) {
        combo.getItems().clear();
        combo.getItems().add(new OpcionCombo<>(null, textoTodos));
        for (T item : items) {
            combo.getItems().add(new OpcionCombo<>(item, texto.apply(item)));
        }
        combo.getSelectionModel().selectFirst();
    }

    /** Valor elegido en un combo de filtro (null = Todos). */
    public static <T> T valorFiltro(ComboBox<OpcionCombo<T>> combo) {
        OpcionCombo<T> op = combo.getValue();
        return op == null ? null : op.getValor();
    }

    /** Elige en el combo el item con ese id (los objetos de la tabla no son los mismos del combo). */
    public static <T extends pe.edu.upeu.padronmascota.padronmascota.modelo.Entidad> void elegirPorId(ComboBox<T> combo, T obj) {
        combo.setValue(obj == null ? null
                : combo.getItems().stream().filter(x -> x.getId() == obj.getId()).findFirst().orElse(null));
    }
}
