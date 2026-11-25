package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.CampoBuscable;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Refrescable;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Combos;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Dialogo;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Formato;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Impresion;
import pe.edu.upeu.padronmascota.padronmascota.componentes.MarcadorError;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Notificacion;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.enums.CategoriaProducto;
import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Atencion;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Mascota;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IMascotaServicio;
import pe.edu.upeu.padronmascota.padronmascota.modelo.DetalleReceta;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Producto;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Receta;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IAtencionServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IProductoServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IRecetaServicio;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** Recetas: lo que el doctor indica para casa (medicamento, dosis, frecuencia, días). No descuenta stock. */
public class RecetaController implements Refrescable {

    @FXML private TextField txtMascota;
    private CampoBuscable<Mascota> cboMascota;   // se escribe, con sugerencias
    @FXML private ComboBox<Atencion> cboAtencion;  // las atenciones de esa mascota
    @FXML private DatePicker dtpFecha;
    @FXML private TextArea txtIndicaciones;
    @FXML private TextField txtProducto;
    private CampoBuscable<Producto> cboProducto;   // se escribe, con sugerencias
    @FXML private TextField txtDosis;
    @FXML private TextField txtFrecuencia;
    @FXML private TextField txtDias;
    @FXML private TableView<DetalleReceta> tblLineas;
    @FXML private TextField txtBuscar;
    @FXML private TableView<Receta> tabla;
    @FXML private Label lblTotal;

    private final IRecetaServicio recetaServicio;
    private final IAtencionServicio atencionServicio;
    private final IProductoServicio productoServicio;
    private final IMascotaServicio mascotaServicio;
    private final MarcadorError errores = new MarcadorError();
    private Receta receta;

    public RecetaController(IRecetaServicio recetaServicio, IAtencionServicio atencionServicio,
                            IProductoServicio productoServicio, IMascotaServicio mascotaServicio) {
        this.mascotaServicio = mascotaServicio;
        this.recetaServicio = recetaServicio;
        this.atencionServicio = atencionServicio;
        this.productoServicio = productoServicio;
    }

    @FXML
    private void initialize() {
        // Se escribe la mascota y abajo salen sus atenciones (la más reciente queda elegida)
        cboMascota = new CampoBuscable<>(txtMascota, () -> mascotaServicio.buscar("", null, null, true),
                m -> m.getNombre() + " - " + m.getPropietario().getNombre());
        cboAtencion.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Atencion a) {
                return a == null ? "" : Formato.fecha(a.getFecha()) + " - " + a.getMotivo() + " (" + a.getVeterinario().getNombre() + ")";
            }
            @Override public Atencion fromString(String s) {
                return null;
            }
        });
        cboMascota.valueProperty().addListener((o, antes, m) -> cargarAtenciones(m));
        // Medicamento: texto libre. Sugiere los que ya existen; si es nuevo se registra solo
        cboProducto = new CampoBuscable<>(txtProducto, productoServicio::listarTodos, Producto::getNombre);
        txtBuscar.textProperty().addListener((obs, antes, ahora) -> buscar());

        new TablaConAcciones<>(tblLineas)
                .columna(Idioma.t("receta.medicamento"), d -> d.getProducto().getNombre(), 140)
                .columna(Idioma.t("receta.dosis"), DetalleReceta::getDosis, 80)
                .columna(Idioma.t("receta.frecuencia"), DetalleReceta::getFrecuencia, 90)
                .columna(Idioma.t("receta.dias"), DetalleReceta::getDias, 45)
                .acciones(Idioma.t("venta.quitar"), this::quitar, null, null);

        new TablaConAcciones<>(tabla)
                .columna(Idioma.t("comun.id"), Receta::getId, 40)
                .columna(Idioma.t("receta.fecha"), r -> Formato.fecha(r.getFecha()), 100)
                .columna(Idioma.t("atencion.mascota"), r -> r.getAtencion().getMascota().getNombre(), 90)
                .columna(Idioma.t("atencion.veterinario"), r -> r.getAtencion().getVeterinario().getNombre(), 150)
                .columna(Idioma.t("receta.indicaciones"), Receta::getIndicaciones, 260)
                .acciones(Idioma.t("venta.ver"), this::ver, Idioma.t("impresion.imprimir"), this::imprimir,
                        Idioma.t("comun.eliminar"), this::eliminar);
        nuevo();
        buscar();
    }

    /** Las atenciones de la mascota, de la más nueva a la más vieja; deja elegida la más nueva. */
    private void cargarAtenciones(Mascota m) {
        List<Atencion> lista = m == null ? List.of() : atencionServicio.buscar(null).stream()
                .filter(a -> a.getMascota().getId() == m.getId())
                .sorted(java.util.Comparator.comparing(Atencion::getFecha).thenComparing(Atencion::getId).reversed())
                .toList();
        Combos.llenar(cboAtencion, lista);
        cboAtencion.setValue(lista.isEmpty() ? null : lista.get(0));
        cboAtencion.setPromptText(m != null && lista.isEmpty()
                ? Idioma.t("receta.sinAtenciones", m.getNombre()) : Idioma.t("receta.atencionVacia"));
    }

    /** El medicamento escrito; si no existe se registra como Medicamento (precio 0, solo para la receta). */
    private Producto medicamentoEscrito() {
        String t = txtProducto.getText() == null ? "" : txtProducto.getText().trim();
        if (cboProducto.getValue() != null || t.isEmpty()) {
            return cboProducto.getValue();
        }
        Producto p = new Producto();
        p.setNombre(t);
        p.setCategoria(CategoriaProducto.MEDICAMENTO);
        p.setPrecio(java.math.BigDecimal.ZERO);
        p.setStockMinimo(0);
        try {
            Producto nuevo = productoServicio.registrar(p);
            cboProducto.setValue(nuevo);
            return nuevo;
        } catch (ValidacionException e) {
            errores.marcar(txtProducto, e.getErrores().values().iterator().next());
        } catch (RuntimeException e) {
            errores.marcar(txtProducto, CrudController.mensajeAmigable(e));
        }
        return null;
    }

    @FXML
    private void nuevo() {
        receta = new Receta();
        cboMascota.setValue(null);
        cboAtencion.setValue(null);
        dtpFecha.setValue(LocalDate.now());
        txtIndicaciones.clear();
        limpiarLinea();
        errores.limpiar();
        refrescarLineas();
    }

    private void limpiarLinea() {
        cboProducto.setValue(null);
        txtDosis.clear();
        txtFrecuencia.clear();
        txtDias.clear();
    }

    @FXML
    private void agregar() {
        errores.limpiar();
        Integer dias = null;
        try {
            dias = Formato.leerEntero(txtDias.getText());
        } catch (NumberFormatException e) {
            errores.marcar(txtDias, Idioma.t("comun.enteroInvalido"));
            return;
        }
        Producto medicamento = medicamentoEscrito();
        if (errores.hayErrores()) {
            errores.enfocarPrimero();
            return;
        }
        DetalleReceta d = new DetalleReceta(medicamento, txtDosis.getText(), txtFrecuencia.getText(), dias);
        try {
            pe.edu.upeu.padronmascota.padronmascota.servicio.impl.Validador.validar(d);   // @NotNull, @NotBlank, @Min(1) de la línea
        } catch (ValidacionException e) {
            Map<String, Control> campos = Map.of("producto", cboProducto.getCampo(), "dosis", txtDosis,
                    "frecuencia", txtFrecuencia, "dias", txtDias);
            e.getErrores().forEach((campo, mensaje) -> errores.marcar(campos.get(campo), mensaje));
            errores.enfocarPrimero();
            return;
        }
        receta.getDetalles().add(d);
        limpiarLinea();
        refrescarLineas();
    }

    private void quitar(DetalleReceta d) {
        receta.getDetalles().remove(d);
        refrescarLineas();
    }

    private void refrescarLineas() {
        tblLineas.setItems(FXCollections.observableArrayList(receta.getDetalles()));
    }

    @FXML
    private void guardar() {
        errores.limpiar();
        receta.setAtencion(cboAtencion.getValue());
        receta.setFecha(dtpFecha.getValue());
        receta.setIndicaciones(txtIndicaciones.getText());
        try {
            recetaServicio.registrar(receta);
            avisar(Idioma.t("receta.registrada"), Notificacion.Tipo.EXITO);
            nuevo();
            buscar();
        } catch (ValidacionException e) {
            Map<String, Control> campos = Map.of("atencion", cboMascota.getValue() == null ? cboMascota.getCampo() : cboAtencion, "fecha", dtpFecha,
                    "indicaciones", txtIndicaciones, "detalles", cboProducto.getCampo());
            e.getErrores().forEach((campo, mensaje) -> {
                if (campos.containsKey(campo)) {
                    errores.marcar(campos.get(campo), mensaje);
                }
            });
            errores.enfocarPrimero();
            avisar(e.getErrores().values().iterator().next(), Notificacion.Tipo.ERROR);
        } catch (IllegalStateException e) {
            Dialogo.error(e.getMessage());
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.guardar", CrudController.mensajeAmigable(e)));
        }
    }

    @FXML
    private void buscar() {
        try {
            List<Receta> lista = recetaServicio.buscar(txtBuscar.getText());
            tabla.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText(Idioma.t("receta.total", lista.size()));
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.buscar", e.getMessage()));
        }
    }

    private void ver(Receta r) {
        StringBuilder sb = new StringBuilder();
        sb.append(Idioma.t("receta.fecha")).append(": ").append(Formato.fecha(r.getFecha())).append('\n')
                .append(Idioma.t("atencion.mascota")).append(": ").append(r.getAtencion().getMascota().getNombre())
                .append(" (").append(r.getAtencion().getMascota().getPropietario().getNombre()).append(")\n")
                .append(Idioma.t("atencion.veterinario")).append(": ").append(r.getAtencion().getVeterinario().getNombre()).append("\n\n");
        for (DetalleReceta d : recetaServicio.detalles(r.getId())) {
            sb.append("• ").append(d.getProducto().getNombre()).append(": ").append(d.getDosis())
                    .append(", ").append(d.getFrecuencia()).append(", ")
                    .append(Idioma.t("receta.porDias", d.getDias())).append('\n');
        }
        if (r.getIndicaciones() != null) {
            sb.append('\n').append(Idioma.t("receta.indicaciones")).append(": ").append(r.getIndicaciones());
        }
        Dialogo.detalle(Idioma.t("receta.titulo", r.getId()), sb.toString());
    }

    /** Vista previa de la receta lista para imprimir (o guardar en PDF). */
    private void imprimir(Receta r) {
        try {
            Mascota m = mascotaServicio.obtener(r.getAtencion().getMascota().getId());
            String vet = r.getAtencion().getVeterinario().getNombre();
            javafx.scene.layout.VBox hoja = Impresion.hoja(Idioma.t("impresion.receta"),
                    Idioma.t("impresion.numeroFecha", r.getId(), Formato.fecha(r.getFecha())));
            hoja.getChildren().addAll(
                    Impresion.seccion(Idioma.t("impresion.paciente")),
                    Impresion.datos(Idioma.t("atencion.mascota"), m.getNombre(),
                            Idioma.t("mascota.especie"), String.valueOf(m.getEspecie()),
                            Idioma.t("mascota.raza"), m.getRaza() == null ? "" : m.getRaza().getNombre(),
                            Idioma.t("impresion.edad"), Idioma.t("impresion.anios", m.getEdadAprox()),
                            Idioma.t("mascota.propietario"), m.getPropietario().getNombre(),
                            Idioma.t("propietario.dni"), m.getPropietario().getDni()),
                    Impresion.seccion(Idioma.t("impresion.consulta")),
                    Impresion.datos(Idioma.t("atencion.veterinario"), vet,
                            Idioma.t("atencion.fecha"), Formato.fecha(r.getAtencion().getFecha()),
                            Idioma.t("impresion.diagnostico"), r.getAtencion().getMotivo()),
                    Impresion.seccion(Idioma.t("impresion.medicamentos")),
                    Impresion.tabla(List.of(Idioma.t("receta.medicamento"), Idioma.t("receta.dosis"),
                                    Idioma.t("receta.frecuencia"), Idioma.t("receta.dias")),
                            new double[]{200, 110, 130, 56},
                            recetaServicio.detalles(r.getId()).stream().map(d -> List.of(d.getProducto().getNombre(),
                                    d.getDosis(), d.getFrecuencia(), String.valueOf(d.getDias()))).toList(), -1));
            if (r.getIndicaciones() != null && !r.getIndicaciones().isBlank()) {
                hoja.getChildren().addAll(Impresion.seccion(Idioma.t("receta.indicaciones")),
                        Impresion.texto(r.getIndicaciones(), 11, false, "#222222"));
            }
            hoja.getChildren().add(Impresion.firma(vet));
            Impresion.vistaPrevia(tabla.getScene().getWindow(), Idioma.t("receta.titulo", r.getId()), hoja);
        } catch (RuntimeException e) {
            Dialogo.error(e.getMessage());
        }
    }

    private void eliminar(Receta r) {
        if (!Dialogo.confirmar(Idioma.t("receta.confirmarEliminar", r.getId()))) {
            return;
        }
        try {
            recetaServicio.eliminar(r.getId());
            avisar(Idioma.t("receta.eliminada"), Notificacion.Tipo.INFO);
            buscar();
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.eliminar", CrudController.mensajeAmigable(e)));
        }
    }

    private void avisar(String mensaje, Notificacion.Tipo tipo) {
        Notificacion.mostrar(tabla.getScene().getWindow(), mensaje, tipo);
    }

    /** Al volver a la pestaña se recargan los datos. */
    @Override
    public void refrescar() {
        buscar();
    }
}
