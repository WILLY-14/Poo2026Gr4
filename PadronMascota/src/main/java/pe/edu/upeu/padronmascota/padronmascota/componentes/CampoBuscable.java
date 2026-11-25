package pe.edu.upeu.padronmascota.padronmascota.componentes;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Entidad;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Un TextField para ESCRIBIR algo que ya existe (mascota, veterinario, producto...),
 * con sugerencias mientras escribes. Reemplaza a los ComboBox de "Elegir".
 *
 * La lista se pide a la base CADA VEZ que escribes (fuente.get()), así nunca muestra
 * datos viejos o borrados. Tiene getValue / setValue / valueProperty como un ComboBox.
 *
 *   cboMascota = new CampoBuscable<>(txtMascota, () -> servicio.listarTodos(), m -> m.getNombre());
 */
public class CampoBuscable<T> {

    private final TextField campo;
    private final Supplier<List<T>> fuente;
    private final Function<T, String> etiqueta;
    private final ObjectProperty<T> valor = new SimpleObjectProperty<>();
    private boolean poniendo;

    public CampoBuscable(TextField campo, Supplier<List<T>> fuente, Function<T, String> etiqueta) {
        this.campo = campo;
        this.fuente = fuente;
        this.etiqueta = etiqueta;
        Sugerencias.en(campo, t -> fuente.get().stream()
                .map(etiqueta).filter(e -> e.toLowerCase().contains(t)).toList());
        // Cada vez que cambia lo escrito, se busca a quién corresponde (o null si a nadie)
        campo.textProperty().addListener((obs, antes, ahora) -> {
            if (!poniendo) {
                valor.set(buscar(ahora));
            }
        });
    }

    /**
     * Lo escrito corresponde a: el que tiene exactamente esa etiqueta; si no, el ÚNICO
     * cuya etiqueta empieza con lo escrito (ej. "Kiwi" encuentra "Kiwi - Rosa Flores").
     */
    private T buscar(String texto) {
        String t = texto == null ? "" : texto.trim().toLowerCase();
        if (t.isEmpty()) {
            return null;
        }
        List<T> todos = fuente.get();
        for (T x : todos) {
            if (etiqueta.apply(x).equalsIgnoreCase(t)) {
                return x;
            }
        }
        List<T> empiezan = todos.stream().filter(x -> {
            String e = etiqueta.apply(x).toLowerCase();
            return e.startsWith(t + " ") || e.startsWith(t + " -");
        }).toList();
        return empiezan.size() == 1 ? empiezan.get(0) : null;
    }

    public T getValue() {
        return valor.get();
    }

    /** Pone el objeto y su texto (sin abrir sugerencias). null deja el campo vacío. */
    public void setValue(T obj) {
        poniendo = true;
        Sugerencias.ponerTexto(campo, obj == null ? "" : etiqueta.apply(obj));
        poniendo = false;
        valor.set(obj);
    }

    /** Busca en la base el que tiene ese id (los objetos de la tabla no son los mismos). */
    public void elegirPorId(T obj) {
        setValue(obj == null ? null
                : fuente.get().stream().filter(x -> mismoId(x, obj)).findFirst().orElse(null));
    }

    private static boolean mismoId(Object a, Object b) {
        return a instanceof Entidad x && b instanceof Entidad y ? x.getId() == y.getId() : a.equals(b);
    }

    public ObjectProperty<T> valueProperty() {
        return valor;
    }

    public TextField getCampo() {
        return campo;
    }
}
