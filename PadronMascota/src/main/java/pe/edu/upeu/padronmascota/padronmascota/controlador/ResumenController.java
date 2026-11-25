package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Refrescable;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Dialogo;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Formato;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.dto.ResumenPadron;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IReporteServicio;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/** La tabla "Resumen" del Excel en tarjetas, con dos gráficos. */
public class ResumenController implements Refrescable {

    @FXML private FlowPane panelPadron;
    @FXML private FlowPane panelClinica;
    @FXML private FlowPane panelVentas;
    @FXML private FlowPane panelCompras;
    @FXML private PieChart graficoEspecies;
    @FXML private PieChart graficoVacunacion;

    private final IReporteServicio reporteServicio;

    public ResumenController(IReporteServicio reporteServicio) {
        this.reporteServicio = reporteServicio;
    }

    @FXML
    private void initialize() {
        actualizar();
    }

    @FXML
    private void actualizar() {
        try {
            ResumenPadron r = reporteServicio.resumen();
            llenar(panelPadron,
                    "resumen.mascotasActivas", r.mascotasActivas(), "resumen.perros", r.perros(),
                    "resumen.gatos", r.gatos(), "resumen.otros", r.otros(),
                    "resumen.alDia", r.vacunacionAlDia(), "resumen.pendiente", r.vacunacionPendiente(),
                    "resumen.edadPromedio", Idioma.t("resumen.anios", r.edadPromedio()),
                    "resumen.propietarios", r.propietarios());
            llenar(panelClinica,
                    "resumen.veterinarios", r.veterinarios(), "resumen.citasPendientes", r.citasPendientes(),
                    "resumen.citasAtendidas", r.citasAtendidas(), "resumen.atenciones", r.atenciones(),
                    "resumen.vacunasAplicadas", r.vacunasAplicadas(), "resumen.dosisVencidas", r.dosisVencidas(),
                    "resumen.recetas", r.recetas(), "resumen.insumos", r.insumosUsados());
            llenar(panelVentas,
                    "resumen.ventasPagadas", r.ventasPagadas(), "resumen.ventasAnuladas", r.ventasAnuladas(),
                    "resumen.totalVendido", Formato.dinero(r.totalVendido()),
                    "resumen.vendidoAnio", Formato.dinero(r.vendidoEsteAnio()));
            llenar(panelCompras,
                    "resumen.proveedores", r.proveedores(), "resumen.compras", r.compras(),
                    "resumen.totalComprado", Formato.dinero(r.totalComprado()),
                    "resumen.stockBajo", r.productosStockBajo(),
                    "resumen.lotesVencidos", r.lotesVencidos(), "resumen.lotesPorVencer", r.lotesPorVencer());

            graficoEspecies.setData(FXCollections.observableArrayList(
                    new PieChart.Data(Idioma.t("resumen.perros") + " (" + r.perros() + ")", r.perros()),
                    new PieChart.Data(Idioma.t("resumen.gatos") + " (" + r.gatos() + ")", r.gatos()),
                    new PieChart.Data(Idioma.t("resumen.otros") + " (" + r.otros() + ")", r.otros())));
            graficoVacunacion.setData(FXCollections.observableArrayList(
                    new PieChart.Data(Idioma.t("resumen.itemAlDia") + " (" + r.vacunacionAlDia() + ")", r.vacunacionAlDia()),
                    new PieChart.Data(Idioma.t("resumen.itemPendiente") + " (" + r.vacunacionPendiente() + ")", r.vacunacionPendiente())));
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.resumen", e.getMessage()));
        }
    }

    /** Recibe pares (clave de idioma, valor) y crea una tarjeta por cada uno. */
    private void llenar(FlowPane panel, Object... pares) {
        panel.getChildren().clear();
        for (int i = 0; i < pares.length; i += 2) {
            Label titulo = new Label(Idioma.t((String) pares[i]));
            titulo.getStyleClass().add("tarjeta-titulo");
            Label valor = new Label(String.valueOf(pares[i + 1]));
            valor.getStyleClass().add("tarjeta-valor");
            VBox tarjeta = new VBox(titulo, valor);
            tarjeta.getStyleClass().add("tarjeta");
            panel.getChildren().add(tarjeta);
        }
    }

    /** Al volver a la pestaña se recargan los datos. */
    @Override
    public void refrescar() {
        actualizar();
    }
}
