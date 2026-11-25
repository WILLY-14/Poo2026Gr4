package pe.edu.upeu.padronmascota.padronmascota.componentes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.List;

/**
 * Hojas para imprimir (receta, carnet de vacunas).
 * Se arma la hoja con los ayudantes de abajo y se muestra una VISTA PREVIA con el botón Imprimir.
 * Para guardar en PDF: en la ventana de impresión elige la impresora "Microsoft Print to PDF".
 */
public final class Impresion {

    /** Ancho de una hoja A4 en puntos (lo que usa la impresora). */
    public static final double ANCHO = 560;

    private Impresion() {
    }

    // ------------------------------------------------------------------
    //  Ayudantes para armar la hoja (estilos en línea: la hoja siempre es blanca, aunque el tema sea oscuro)
    // ------------------------------------------------------------------

    /** Hoja en blanco con el encabezado de la veterinaria y el título del documento. */
    public static VBox hoja(String titulo, String subtitulo) {
        VBox hoja = new VBox(10);
        hoja.setPadding(new Insets(28, 32, 28, 32));
        hoja.setPrefWidth(ANCHO);
        hoja.setMaxWidth(ANCHO);
        hoja.setStyle("-fx-background-color: white;");

        Label nombre = texto(Idioma.t("app.titulo"), 20, true, "#1f6f5c");
        Label lema = texto(Idioma.t("impresion.lema"), 10, false, "#666666");
        VBox marca = new VBox(2, nombre, lema);
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        VBox doc = new VBox(2, texto(titulo, 15, true, "#222222"), texto(subtitulo, 11, false, "#444444"));
        doc.setAlignment(Pos.TOP_RIGHT);
        HBox cabecera = new HBox(marca, espacio, doc);
        hoja.getChildren().addAll(cabecera, linea());
        return hoja;
    }

    public static Label texto(String t, double tamano, boolean negrita, String color) {
        Label l = new Label(t == null ? "" : t);
        l.setWrapText(true);
        l.setStyle("-fx-font-size: " + tamano + "px; -fx-text-fill: " + color + ";"
                + (negrita ? " -fx-font-weight: bold;" : ""));
        return l;
    }

    public static Region linea() {
        Region r = new Region();
        r.setMinHeight(1.5);
        r.setMaxHeight(1.5);
        r.setStyle("-fx-background-color: #1f6f5c;");
        return r;
    }

    /** Título de una sección ("Paciente", "Medicamentos"...). */
    public static Label seccion(String t) {
        Label l = texto(t.toUpperCase(), 11, true, "#1f6f5c");
        l.setPadding(new Insets(6, 0, 0, 0));
        return l;
    }

    /** Pares "Dato: valor" en dos columnas. datos = {etiqueta1, valor1, etiqueta2, valor2, ...} */
    public static GridPane datos(String... datos) {
        GridPane g = new GridPane();
        g.setHgap(8);
        g.setVgap(3);
        for (int c = 0; c < 2; c++) {
            ColumnConstraints et = new ColumnConstraints(85);
            ColumnConstraints val = new ColumnConstraints();
            val.setPrefWidth(ANCHO / 2 - 110);
            g.getColumnConstraints().addAll(et, val);
        }
        for (int i = 0; i + 1 < datos.length; i += 2) {
            int n = i / 2;
            g.add(texto(datos[i] + ":", 11, true, "#444444"), (n % 2) * 2, n / 2);
            g.add(texto(datos[i + 1] == null || datos[i + 1].isBlank() ? "-" : datos[i + 1], 11, false, "#222222"),
                    (n % 2) * 2 + 1, n / 2);
        }
        return g;
    }

    /** Tabla simple con encabezado. resaltar[i] = true pinta esa columna en negrita/color (ej. próxima dosis). */
    public static GridPane tabla(List<String> titulos, double[] anchos, List<List<String>> filas, int columnaResaltada) {
        GridPane g = new GridPane();
        for (double a : anchos) {
            g.getColumnConstraints().add(new ColumnConstraints(a));
        }
        for (int c = 0; c < titulos.size(); c++) {
            Label l = texto(titulos.get(c), 11, true, "white");
            l.setMaxWidth(Double.MAX_VALUE);
            l.setPadding(new Insets(4, 6, 4, 6));
            l.setStyle(l.getStyle() + " -fx-background-color: #1f6f5c;");
            g.add(l, c, 0);
        }
        for (int f = 0; f < filas.size(); f++) {
            List<String> fila = filas.get(f);
            for (int c = 0; c < fila.size(); c++) {
                boolean resaltar = c == columnaResaltada;
                Label l = texto(fila.get(c), 11, resaltar, resaltar ? "#b3261e" : "#222222");
                l.setMaxWidth(Double.MAX_VALUE);
                l.setPadding(new Insets(4, 6, 4, 6));
                l.setStyle(l.getStyle() + " -fx-background-color: " + (f % 2 == 0 ? "#f3f7f6" : "white")
                        + "; -fx-border-color: transparent transparent #dddddd transparent;");
                g.add(l, c, f + 1);
            }
        }
        return g;
    }

    /** Línea para la firma y el sello del veterinario. */
    public static VBox firma(String nombre) {
        Region r = new Region();
        r.setMinHeight(1);
        r.setMaxHeight(1);
        r.setPrefWidth(200);
        r.setMaxWidth(200);
        r.setStyle("-fx-background-color: #333333;");
        VBox v = new VBox(3, r, texto(nombre, 11, true, "#222222"), texto(Idioma.t("impresion.firma"), 10, false, "#666666"));
        v.setAlignment(Pos.CENTER);
        v.setPadding(new Insets(50, 0, 0, 0));
        return v;
    }

    // ------------------------------------------------------------------
    //  Vista previa e impresión
    // ------------------------------------------------------------------

    /** Muestra la hoja en una ventana con los botones Imprimir y Cerrar. */
    public static void vistaPrevia(Window duena, String titulo, VBox hoja) {
        Stage st = new Stage();
        st.initOwner(duena);
        st.initModality(Modality.WINDOW_MODAL);
        st.setTitle(titulo);

        Button imprimir = new Button(Idioma.t("impresion.imprimir"));
        imprimir.getStyleClass().addAll("btn", "btn-exito");
        imprimir.setOnAction(e -> imprimir(st, hoja));
        Button cerrar = new Button(Idioma.t("impresion.cerrar"));
        cerrar.getStyleClass().addAll("btn", "btn-secundario");
        cerrar.setOnAction(e -> st.close());
        Label ayuda = new Label(Idioma.t("impresion.ayudaPdf"));
        ayuda.getStyleClass().add("texto-suave");
        ayuda.setWrapText(true);
        HBox botones = new HBox(8, imprimir, cerrar, ayuda);
        botones.setAlignment(Pos.CENTER_LEFT);
        botones.setPadding(new Insets(10));

        VBox centro = new VBox(hoja);
        centro.setAlignment(Pos.TOP_CENTER);
        centro.setPadding(new Insets(16));
        centro.setStyle("-fx-background-color: #9aa5a2;");
        ScrollPane scroll = new ScrollPane(centro);
        scroll.setFitToWidth(true);
        VBox raiz = new VBox(botones, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Scene escena = new Scene(raiz, ANCHO + 80, 720);
        if (duena != null && duena.getScene() != null) {
            escena.getStylesheets().addAll(duena.getScene().getStylesheets());
        }
        st.setScene(escena);
        st.show();
    }

    /** Abre la ventana de la impresora y manda la hoja (escalada al ancho de la página). */
    private static void imprimir(Stage st, Node hoja) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            Dialogo.error(Idioma.t("impresion.sinImpresora"));
            return;
        }
        if (!job.showPrintDialog(st)) {
            return;   // la persona canceló
        }
        PageLayout pagina = job.getJobSettings().getPageLayout();
        double escala = Math.min(1.0, pagina.getPrintableWidth() / hoja.getBoundsInParent().getWidth());
        Scale s = new Scale(escala, escala);
        hoja.getTransforms().add(s);
        boolean ok = job.printPage(hoja);
        hoja.getTransforms().remove(s);
        if (ok) {
            job.endJob();
            st.close();
        } else {
            Dialogo.error(Idioma.t("impresion.error"));
        }
    }
}
