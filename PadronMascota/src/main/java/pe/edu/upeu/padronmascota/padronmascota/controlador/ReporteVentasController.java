package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Refrescable;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Combos;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Dialogo;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Formato;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.dto.FilaReporte;
import pe.edu.upeu.padronmascota.padronmascota.dto.OpcionCombo;
import pe.edu.upeu.padronmascota.padronmascota.enums.MetodoPago;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IReporteServicio;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Reporte de ventas pagadas: por año, por mes de un año, por día y por rango de fechas + método de pago. */
public class ReporteVentasController implements Refrescable {

    @FXML private TableView<FilaReporte> tblAnio;
    @FXML private ComboBox<Integer> cboAnio;
    @FXML private Label lblTotalAnio;
    @FXML private TableView<FilaReporte> tblMes;
    @FXML private BarChart<String, Number> graficoMes;
    @FXML private TableView<FilaReporte> tblDia;
    @FXML private DatePicker dtpDesde;
    @FXML private DatePicker dtpHasta;
    @FXML private ComboBox<OpcionCombo<MetodoPago>> cboMetodo;
    @FXML private Label lblRango;
    @FXML private TableView<FilaReporte> tblRango;

    private final IReporteServicio reporteServicio;

    public ReporteVentasController(IReporteServicio reporteServicio) {
        this.reporteServicio = reporteServicio;
    }

    @FXML
    private void initialize() {
        columnas(tblAnio, "reporte.anio");
        columnas(tblMes, "reporte.mes");
        columnas(tblDia, "reporte.dia");
        columnas(tblRango, "venta.metodoPago");
        Combos.filtro(cboMetodo, Idioma.t("comun.todos"), Arrays.asList(MetodoPago.values()), MetodoPago::toString);
        LocalDate hoy = LocalDate.now();
        dtpDesde.setValue(hoy.withDayOfMonth(1));
        dtpHasta.setValue(hoy);
        actualizar();
    }

    private void columnas(TableView<FilaReporte> tabla, String claveEtiqueta) {
        new TablaConAcciones<>(tabla)
                .columna(Idioma.t(claveEtiqueta), FilaReporte::etiqueta, 130)
                .columna(Idioma.t("reporte.numVentas"), FilaReporte::cantidad, 90)
                .columna(Idioma.t("venta.total"), f -> Formato.dinero(f.total()), 100);
    }

    @FXML
    private void actualizar() {
        try {
            tblAnio.setItems(FXCollections.observableArrayList(reporteServicio.ventasPorAnio()));
            tblDia.setItems(FXCollections.observableArrayList(reporteServicio.ventasPorDia().stream()
                    .map(f -> new FilaReporte(Formato.fecha(LocalDate.parse(f.etiqueta())), f.cantidad(), f.total()))
                    .toList()));
            List<Integer> anios = reporteServicio.aniosConVentas();
            Integer elegido = cboAnio.getValue();
            cboAnio.setItems(FXCollections.observableArrayList(anios));
            if (elegido != null && anios.contains(elegido)) {
                cboAnio.setValue(elegido);
            } else if (!anios.isEmpty()) {
                cboAnio.setValue(anios.contains(LocalDate.now().getYear()) ? LocalDate.now().getYear() : anios.get(anios.size() - 1));
            }
            cargarMeses();
            calcularRango();
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.resumen", e.getMessage()));
        }
    }

    @FXML
    private void cargarMeses() {
        Integer anio = cboAnio.getValue();
        if (anio == null) {
            return;
        }
        Locale idioma = Locale.forLanguageTag(Idioma.actual());
        List<FilaReporte> meses = reporteServicio.ventasPorMes(anio).stream()
                .map(f -> new FilaReporte(nombreMes(Integer.parseInt(f.etiqueta()), idioma), f.cantidad(), f.total()))
                .toList();
        tblMes.setItems(FXCollections.observableArrayList(meses));
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (FilaReporte f : meses) {
            serie.getData().add(new XYChart.Data<>(f.etiqueta().substring(0, 3), f.total()));
        }
        graficoMes.getData().setAll(List.of(serie));
        int n = meses.stream().mapToInt(FilaReporte::cantidad).sum();
        BigDecimal total = meses.stream().map(FilaReporte::total).reduce(BigDecimal.ZERO, BigDecimal::add);
        lblTotalAnio.setText(Idioma.t("reporte.totalAnio", n, Formato.dinero(total)));
    }

    private static String nombreMes(int mes, Locale idioma) {
        String nombre = Month.of(mes).getDisplayName(TextStyle.FULL, idioma);
        return Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1);
    }

    @FXML
    private void calcularRango() {
        try {
            List<FilaReporte> filas = reporteServicio.ventasPorRango(dtpDesde.getValue(), dtpHasta.getValue(),
                    Combos.valorFiltro(cboMetodo));
            tblRango.setItems(FXCollections.observableArrayList(filas));
            int n = filas.stream().mapToInt(FilaReporte::cantidad).sum();
            BigDecimal total = filas.stream().map(FilaReporte::total).reduce(BigDecimal.ZERO, BigDecimal::add);
            lblRango.setText(Idioma.t("reporte.resultadoRango", n, Formato.dinero(total)));
        } catch (IllegalStateException e) {
            Dialogo.error(e.getMessage());
        }
    }

    /** Al volver a la pestaña se recargan los datos. */
    @Override
    public void refrescar() {
        actualizar();
    }
}
