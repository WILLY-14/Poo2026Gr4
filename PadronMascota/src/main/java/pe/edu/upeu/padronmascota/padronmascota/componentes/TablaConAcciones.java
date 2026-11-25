package pe.edu.upeu.padronmascota.padronmascota.componentes;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Arma las columnas de un TableView sin escribir una por una en el FXML.
 * Cada columna recibe una función que saca el valor del objeto,
 * por ejemplo: m -> m.getEspecie().getNombre()
 */
public class TablaConAcciones<T> {

    private final TableView<T> tabla;

    public TablaConAcciones(TableView<T> tabla) {
        this.tabla = tabla;
        tabla.getColumns().clear();
        // Si las columnas entran, se estiran hasta llenar la tabla; si no entran,
        // aparece la barra horizontal en vez de cortar los textos con "..."
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabla.widthProperty().addListener((o, antes, ahora) -> ajustarPolitica());
        tabla.getColumns().addListener((javafx.collections.ListChangeListener<TableColumn<T, ?>>) c -> ajustarPolitica());
        tabla.setPlaceholder(new Label(Idioma.t("comun.sinDatos")));   // tabla vacía
    }

    /** Agrega una columna de datos. Devuelve this para poder encadenar llamadas. */
    public TablaConAcciones<T> columna(String titulo, Function<T, Object> valor, double ancho) {
        TableColumn<T, Object> col = new TableColumn<>(titulo);
        col.setCellValueFactory(fila -> new ReadOnlyObjectWrapper<>(valor.apply(fila.getValue())));
        double minimo = Math.max(ancho, anchoDelTitulo(titulo));
        col.setMinWidth(minimo);   // el título y los datos nunca se recortan
        col.setPrefWidth(minimo);
        tabla.getColumns().add(col);
        return this;
    }

    // ------------------------------------------------------------------
    //  Botones con ícono (el texto aparece al pasar el mouse)
    // ------------------------------------------------------------------

    /** Ancho de la columna Acciones según cuántos botones tiene (índice = cantidad). */
    private static final double[] ANCHO_ACCIONES = {0, 70, 106, 142};

    private static final String LAPIZ = "M3 17.3 V21 h3.7 L17.8 9.9 l-3.7 -3.7 z M20.7 7 a1 1 0 0 0 0 -1.4 l-2.3 -2.3 a1 1 0 0 0 -1.4 0 l-1.8 1.8 l3.7 3.7 z";
    private static final String PAPELERA = "M6 19 a2 2 0 0 0 2 2 h8 a2 2 0 0 0 2 -2 V7 H6 z M19 4 h-3.5 l-1 -1 h-5 l-1 1 H5 v2 h14 z";
    private static final String OJO = "M12 4.5 C7 4.5 2.7 7.6 1 12 c1.7 4.4 6 7.5 11 7.5 s9.3 -3.1 11 -7.5 C21.3 7.6 17 4.5 12 4.5 z M12 17 a5 5 0 1 1 0 -10 a5 5 0 0 1 0 10 z M12 9 a3 3 0 1 0 0 6 a3 3 0 0 0 0 -6 z";
    private static final String QUITAR = "M19 6.4 L17.6 5 L12 10.6 L6.4 5 L5 6.4 L10.6 12 L5 17.6 L6.4 19 L12 13.4 L17.6 19 L19 17.6 L13.4 12 z";
    private static final String PROHIBIDO = "M12 2 a10 10 0 1 0 0 20 a10 10 0 0 0 0 -20 z M4 12 c0 -1.8 0.6 -3.5 1.7 -4.9 l11.2 11.2 A8 8 0 0 1 4 12 z M18.3 16.9 L7.1 5.7 A8 8 0 0 1 18.3 16.9 z";
    private static final String ENCENDIDO = "M11 2 h2 v10 h-2 z M17.6 5.1 l-1.4 1.4 A7 7 0 1 1 7.8 6.5 L6.4 5.1 A9 9 0 1 0 17.6 5.1 z";
    private static final String IMPRESORA = "M19 8 H5 a3 3 0 0 0 -3 3 v6 h4 v4 h12 v-4 h4 v-6 a3 3 0 0 0 -3 -3 z M16 19 H8 v-5 h8 z M18 3 H6 v4 h12 z";
    private static final String CARNET = "M20 4 H4 a2 2 0 0 0 -2 2 v12 a2 2 0 0 0 2 2 h16 a2 2 0 0 0 2 -2 V6 a2 2 0 0 0 -2 -2 z M8 7 a2.5 2.5 0 1 1 0 5 a2.5 2.5 0 0 1 0 -5 z M12.5 17 h-9 c0 -2 2 -3.2 4.5 -3.2 s4.5 1.2 4.5 3.2 z M20 15 h-6 v-2 h6 z M20 11 h-6 V9 h6 z";
    private static final String RECIBO = "M18 2 H6 v20 l2 -1.5 l2 1.5 l2 -1.5 l2 1.5 l2 -1.5 l2 1.5 z M15 15 H9 v-2 h6 z M15 11 H9 V9 h6 z M15 7 H9 V5 h6 z";

    /** Ícono que corresponde al texto del botón (según la clave de idioma). */
    private static String icono(String texto) {
        // HashMap (no Map.of): en algún idioma dos textos podrían ser iguales
        java.util.Map<String, String> iconos = new java.util.HashMap<>();
        iconos.put(Idioma.t("venta.anular"), PROHIBIDO);
        iconos.put(Idioma.t("cita.cancelar"), PROHIBIDO);
        iconos.put(Idioma.t("mascota.baja"), ENCENDIDO);
        iconos.put(Idioma.t("impresion.imprimir"), IMPRESORA);
        iconos.put(Idioma.t("impresion.carnet"), CARNET);
        iconos.put(Idioma.t("venta.comprobanteBtn"), RECIBO);
        iconos.put(Idioma.t("venta.quitar"), QUITAR);
        iconos.put(Idioma.t("venta.ver"), OJO);
        iconos.put(Idioma.t("comun.eliminar"), PAPELERA);
        iconos.put(Idioma.t("comun.editar"), LAPIZ);
        return iconos.get(texto);
    }

    /** Botón chico de la tabla: con ícono y el texto como ayuda; si no hay ícono, con texto. */
    private static Button boton(String texto, String clase) {
        Button b = new Button();
        b.getStyleClass().addAll("btn", "btn-chico", clase);
        String svg = icono(texto);
        if (svg == null) {
            b.setText(texto);
        } else {
            javafx.scene.shape.SVGPath forma = new javafx.scene.shape.SVGPath();
            forma.setContent(svg);
            forma.setScaleX(0.68);
            forma.setScaleY(0.68);
            b.setGraphic(new javafx.scene.Group(forma));
            b.getStyleClass().add("btn-icono");
        }
        javafx.scene.control.Tooltip ayuda = new javafx.scene.control.Tooltip(texto);
        ayuda.setShowDelay(javafx.util.Duration.millis(250));
        b.setTooltip(ayuda);
        return b;
    }

    /** Elige entre "llenar el ancho" o "permitir desplazarse" según si las columnas entran. */
    private void ajustarPolitica() {
        double minimos = 0;
        for (TableColumn<T, ?> c : tabla.getColumns()) {
            minimos += c.getMinWidth();
        }
        boolean entran = minimos + 20 <= tabla.getWidth(); // 20 = barra vertical y bordes
        tabla.setColumnResizePolicy(entran
                ? TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
                : TableView.UNCONSTRAINED_RESIZE_POLICY);
    }

    /** Ancho que necesita el título de la columna (en negrita) más su margen. */
    private static double anchoDelTitulo(String titulo) {
        javafx.scene.text.Text t = new javafx.scene.text.Text(titulo);
        t.setFont(javafx.scene.text.Font.font(null, javafx.scene.text.FontWeight.BOLD, 12));
        return Math.ceil(t.getLayoutBounds().getWidth()) + 22;
    }

    /**
     * Agrega la columna con los botones Editar y Eliminar.
     * Si alEliminar es null, no se muestra el botón Eliminar.
     */
    public TablaConAcciones<T> acciones(Consumer<T> alEditar, Consumer<T> alEliminar) {
        return acciones(Idioma.t("comun.editar"), alEditar, Idioma.t("comun.eliminar"), alEliminar);
    }

    /** Con un TERCER botón (rojo) a la derecha; el segundo queda gris. Ej.: Editar | Baja/Alta | Eliminar. */
    public TablaConAcciones<T> acciones(String texto1, Consumer<T> accion1, String texto2, Consumer<T> accion2,
                                        String texto3, Consumer<T> accion3) {
        TableColumn<T, Void> col = new TableColumn<>(Idioma.t("comun.acciones"));
        col.setPrefWidth(ANCHO_ACCIONES[3]);
        col.setMinWidth(ANCHO_ACCIONES[3]);
        col.setSortable(false);
        col.setCellFactory(c -> new TableCell<>() {
            private final Button b1 = boton(texto1, "btn-primario");
            private final Button b2 = boton(texto2, "btn-secundario");
            private final Button b3 = boton(texto3, "btn-peligro");
            private final HBox botones = new HBox(6, b1, b2, b3);
            { botones.setAlignment(javafx.geometry.Pos.CENTER_LEFT); }

            {
                b1.setOnAction(e -> accion1.accept(getTableRow().getItem()));
                b2.setOnAction(e -> accion2.accept(getTableRow().getItem()));
                b3.setOnAction(e -> accion3.accept(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean vacia) {
                super.updateItem(item, vacia);
                setGraphic(vacia || getTableRow().getItem() == null ? null : botones);
            }
        });
        tabla.getColumns().add(col);   // Acciones al final de la tabla
        return this;
    }

    /** Igual, pero con otros textos en los botones (por ejemplo "Ver" y "Anular"). */
    public TablaConAcciones<T> acciones(String textoEditar, Consumer<T> alEditar,
                                        String textoEliminar, Consumer<T> alEliminar) {
        TableColumn<T, Void> col = new TableColumn<>(Idioma.t("comun.acciones"));
        col.setPrefWidth(ANCHO_ACCIONES[alEliminar == null ? 1 : 2]);
        col.setMinWidth(col.getPrefWidth()); // los botones nunca se recortan
        col.setSortable(false);
        col.setCellFactory(c -> new TableCell<>() {
            private final Button btnEditar = boton(textoEditar, "btn-primario");
            private final Button btnEliminar = boton(textoEliminar, "btn-peligro");
            private final HBox botones = new HBox(6, btnEditar);
            { botones.setAlignment(javafx.geometry.Pos.CENTER_LEFT); }

            {
                btnEditar.setOnAction(e -> alEditar.accept(getTableRow().getItem()));
                if (alEliminar != null) {
                    btnEliminar.setOnAction(e -> alEliminar.accept(getTableRow().getItem()));
                    botones.getChildren().add(btnEliminar);
                }
            }

            @Override
            protected void updateItem(Void item, boolean vacia) {
                super.updateItem(item, vacia);
                setGraphic(vacia || getTableRow().getItem() == null ? null : botones);
            }
        });
        tabla.getColumns().add(col);   // Acciones al final de la tabla
        return this;
    }
}
