package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.CampoBuscable;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Refrescable;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Combos;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Dialogo;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Formato;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.MarcadorError;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Notificacion;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Atencion;
import pe.edu.upeu.padronmascota.padronmascota.modelo.AtencionInsumo;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Cita;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Mascota;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Producto;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Veterinario;
import pe.edu.upeu.padronmascota.padronmascota.seguridad.Sesion;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IAtencionServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.ICitaServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IMascotaServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IProductoServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IVeterinarioServicio;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Atenciones del veterinario con sus insumos (jeringa, algodón...).
 * Al guardar: la atención, los insumos (descuentan stock) y la cita pasa a "Atendida", todo en una transacción.
 */
public class AtencionController implements Refrescable {

    @FXML private TextField txtMascota;
    private CampoBuscable<Mascota> cboMascota;   // se escribe, con sugerencias
    @FXML private ComboBox<Veterinario> cboVeterinario;
    @FXML private DatePicker dtpFecha;
    @FXML private TextField txtMotivo;
    @FXML private TextField txtPrecioConsulta;   // lo que cobra el doctor; sale solo en Ventas
    @FXML private ComboBox<Cita> cboCita;
    @FXML private ComboBox<pe.edu.upeu.padronmascota.padronmascota.modelo.ServicioVeterinario> cboServicio;
    @FXML private TextField txtProducto;
    private CampoBuscable<Producto> cboProducto;   // se escribe, con sugerencias
    @FXML private TextField txtCantidad;
    @FXML private TableView<AtencionInsumo> tblLineas;
    @FXML private TextField txtBuscar;
    @FXML private TableView<Atencion> tabla;
    @FXML private Label lblTotal;

    private final IAtencionServicio atencionServicio;
    private final IMascotaServicio mascotaServicio;
    private final IVeterinarioServicio veterinarioServicio;
    private final ICitaServicio citaServicio;
    private final IProductoServicio productoServicio;
    private final pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioVeterinarioServicio servicioVetServicio;
    private final MarcadorError errores = new MarcadorError();
    private Atencion atencion;   // la que se está armando

    public AtencionController(IAtencionServicio atencionServicio, IMascotaServicio mascotaServicio,
                              IVeterinarioServicio veterinarioServicio, ICitaServicio citaServicio,
                              IProductoServicio productoServicio,
                              pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioVeterinarioServicio servicioVetServicio) {
        this.atencionServicio = atencionServicio;
        this.mascotaServicio = mascotaServicio;
        this.veterinarioServicio = veterinarioServicio;
        this.citaServicio = citaServicio;
        this.productoServicio = productoServicio;
        this.servicioVetServicio = servicioVetServicio;
    }

    @FXML
    private void initialize() {
        cboMascota = new CampoBuscable<>(txtMascota, () -> mascotaServicio.buscar("", null, null, true),
                m -> m.getNombre() + " - " + m.getPropietario().getNombre());
        // Veterinario: lista de los registrados en Veterinarios; se recarga cada vez que la abres
        cargarVeterinarios();
        cboVeterinario.setOnShowing(e -> cargarVeterinarios());
        cboProducto = new CampoBuscable<>(txtProducto, productoServicio::listarTodos,
                p -> p.getNombre() + " (stock " + p.getStock() + ")");
        // La cita es opcional: se ofrecen las citas pendientes de la mascota elegida
        cboMascota.valueProperty().addListener((obs, antes, ahora) -> cargarCitas(ahora));
        // Al elegir una cita, se copian su veterinario y su motivo
        cboCita.valueProperty().addListener((obs, antes, cita) -> {
            if (cita != null) {
                Combos.elegirPorId(cboVeterinario, cita.getVeterinario());
                txtMotivo.setText(cita.getMotivo());
            }
        });
        prepararServicios();
        txtBuscar.textProperty().addListener((obs, antes, ahora) -> buscar());

        new TablaConAcciones<>(tblLineas)
                .columna(Idioma.t("atencion.insumo"), i -> i.getProducto().getNombre(), 230)
                .columna(Idioma.t("comun.cantidad"), AtencionInsumo::getCantidad, 70)
                .acciones(Idioma.t("venta.quitar"), this::quitar, null, null);

        new TablaConAcciones<>(tabla)
                .columna(Idioma.t("comun.id"), Atencion::getId, 40)
                .columna(Idioma.t("atencion.fecha"), a -> Formato.fecha(a.getFecha()), 100)
                .columna(Idioma.t("atencion.mascota"), a -> a.getMascota().getNombre(), 90)
                .columna(Idioma.t("mascota.propietario"), a -> a.getMascota().getPropietario().getNombre(), 150)
                .columna(Idioma.t("atencion.veterinario"), a -> a.getVeterinario().getNombre(), 150)
                .columna(Idioma.t("atencion.motivo"), Atencion::getMotivo, 170)
                .columna(Idioma.t("atencion.consulta"), a -> a.getPrecioConsulta() == null ? "-" : Formato.dinero(a.getPrecioConsulta()), 75)
                .columna(Idioma.t("atencion.cita"), a -> a.getCita() == null ? Idioma.t("atencion.sinCita") : "#" + a.getCita().getId(), 75)
                .acciones(Idioma.t("venta.ver"), this::ver, Idioma.t("comun.eliminar"), this::eliminar);
        nuevo();
        buscar();
    }

    private void cargarVeterinarios() {
        Veterinario elegido = cboVeterinario.getValue();
        Combos.llenar(cboVeterinario, veterinarioServicio.listarTodos());
        Combos.elegirPorId(cboVeterinario, elegido);
    }

    private void cargarCitas(Mascota m) {
        List<Cita> citas = new ArrayList<>();
        citas.add(null);   // "sin cita"
        if (m != null) {
            citas.addAll(citaServicio.pendientesDe(m.getId()));
        }
        Combos.llenar(cboCita, citas);
        cboCita.setValue(null);
    }

    // ------------------------------------------------------------------
    //  Servicio (consulta, operación, baño...): sale de Inventario > Servicios
    // ------------------------------------------------------------------

    private boolean recargandoServicios;   // al recargar la lista no se toca el precio escrito

    private void prepararServicios() {
        cboServicio.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(pe.edu.upeu.padronmascota.padronmascota.modelo.ServicioVeterinario s) {
                return s == null ? "" : s.getNombre() + "  ·  " + Formato.dinero(s.getPrecio());
            }

            @Override
            public pe.edu.upeu.padronmascota.padronmascota.modelo.ServicioVeterinario fromString(String texto) {
                return null;
            }
        });
        cboServicio.setPlaceholder(new Label(Idioma.t("atencion.sinServicios")));
        cboServicio.setVisibleRowCount(10);
        cargarServicios();
        cboServicio.setOnShowing(e -> cargarServicios());   // aparecen los recién registrados
        // Al elegir el servicio se pone su precio (se puede cambiar) y, si está vacío, el motivo
        cboServicio.valueProperty().addListener((obs, antes, s) -> {
            if (s == null || recargandoServicios) {
                return;
            }
            txtPrecioConsulta.setText(s.getPrecio() == null ? "" : s.getPrecio().toPlainString());
            if (txtMotivo.getText() == null || txtMotivo.getText().isBlank()) {
                txtMotivo.setText(s.getNombre());
            }
        });
    }

    private void cargarServicios() {
        var elegido = cboServicio.getValue();
        var lista = servicioVetServicio.listarTodos().stream()
                .sorted(java.util.Comparator.comparing(
                        pe.edu.upeu.padronmascota.padronmascota.modelo.ServicioVeterinario::getNombre,
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
        recargandoServicios = true;
        cboServicio.getItems().setAll(lista);
        if (elegido != null) {
            cboServicio.setValue(lista.stream().filter(x -> x.getId() == elegido.getId()).findFirst().orElse(null));
        }
        recargandoServicios = false;
    }

    @FXML
    private void nuevo() {
        atencion = new Atencion();
        cboMascota.setValue(null);
        cboCita.setValue(null);
        dtpFecha.setValue(LocalDate.now());
        txtMotivo.clear();
        txtPrecioConsulta.clear();
        cboServicio.setValue(null);
        cboProducto.setValue(null);
        txtCantidad.setText("1");
        // Si entró un veterinario, se elige solo
        Veterinario yo = Sesion.activa() ? Sesion.usuario().getVeterinario() : null;
        Combos.elegirPorId(cboVeterinario, yo);
        errores.limpiar();
        refrescarLineas();
    }

    @FXML
    private void agregar() {
        errores.limpiar();
        Producto p = cboProducto.getValue();
        if (p == null) {
            errores.marcar(cboProducto.getCampo(), txtProducto.getText() == null || txtProducto.getText().isBlank()
                    ? Idioma.t("atencion.elegirInsumo") : Idioma.t("atencion.insumoNoExiste", txtProducto.getText().trim()));
            avisar(errores.primerMensaje(), Notificacion.Tipo.ERROR);
            return;
        }
        Integer cantidad = leerCantidad(txtCantidad);
        if (cantidad == null) {
            return;
        }
        int yaUsado = atencion.getInsumos().stream().filter(i -> i.getProducto().getId() == p.getId())
                .mapToInt(AtencionInsumo::getCantidad).sum();
        if (yaUsado + cantidad > p.getStock()) {   // aviso rápido; la BD lo vuelve a revisar al guardar
            errores.marcar(txtCantidad, Idioma.t("venta.sinStock", p.getStock()));
            return;
        }
        atencion.agregarInsumo(p, cantidad);
        cboProducto.setValue(null);
        txtCantidad.setText("1");
        refrescarLineas();
    }

    private Integer leerCantidad(TextField campo) {
        try {
            Integer n = Formato.leerEntero(campo.getText());
            if (n != null && n >= 1) {
                return n;
            }
        } catch (NumberFormatException ignorado) {
            // se marca abajo
        }
        errores.marcar(campo, Idioma.t("venta.cantidadInvalida"));
        return null;
    }

    private void quitar(AtencionInsumo i) {
        atencion.getInsumos().remove(i);
        refrescarLineas();
    }

    private void refrescarLineas() {
        tblLineas.setItems(FXCollections.observableArrayList(atencion.getInsumos()));
    }

    @FXML
    private void guardar() {
        errores.limpiar();
        atencion.setMascota(cboMascota.getValue());
        atencion.setVeterinario(cboVeterinario.getValue());
        atencion.setFecha(dtpFecha.getValue());
        atencion.setMotivo(txtMotivo.getText());
        atencion.setCita(cboCita.getValue());
        try {
            atencion.setPrecioConsulta(txtPrecioConsulta.getText() == null || txtPrecioConsulta.getText().isBlank()
                    ? null : pe.edu.upeu.padronmascota.padronmascota.componentes.Formato.leerDinero(txtPrecioConsulta.getText()));
        } catch (NumberFormatException e) {
            errores.marcar(txtPrecioConsulta, Idioma.t("venta.precioInvalido"));
            avisar(errores.primerMensaje(), Notificacion.Tipo.ERROR);
            return;
        }
        try {
            atencionServicio.registrar(atencion);
            avisar(Idioma.t("atencion.registrada"), Notificacion.Tipo.EXITO);
            nuevo();
            buscar();
        } catch (ValidacionException e) {
            Map<String, Control> campos = Map.of("mascota", cboMascota.getCampo(), "veterinario", cboVeterinario,
                    "fecha", dtpFecha, "motivo", txtMotivo, "precioConsulta", txtPrecioConsulta);
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
            List<Atencion> lista = atencionServicio.buscar(txtBuscar.getText());
            tabla.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText(Idioma.t("atencion.total", lista.size()));
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.buscar", e.getMessage()));
        }
    }

    private void ver(Atencion a) {
        StringBuilder sb = new StringBuilder();
        sb.append(Idioma.t("atencion.fecha")).append(": ").append(Formato.fecha(a.getFecha())).append('\n')
                .append(Idioma.t("atencion.mascota")).append(": ").append(a.getMascota().getNombre())
                .append(" (").append(a.getMascota().getPropietario().getNombre()).append(")\n")
                .append(Idioma.t("atencion.veterinario")).append(": ").append(a.getVeterinario().getNombre()).append('\n')
                .append(Idioma.t("atencion.motivo")).append(": ").append(a.getMotivo()).append("\n\n")
                .append(Idioma.t("atencion.insumosUsados")).append(":\n");
        List<AtencionInsumo> insumos = atencionServicio.insumos(a.getId());
        if (insumos.isEmpty()) {
            sb.append("  -\n");
        }
        for (AtencionInsumo i : insumos) {
            sb.append(String.format("%4d x %s%n", i.getCantidad(), i.getProducto().getNombre()));
        }
        Dialogo.detalle(Idioma.t("atencion.titulo", a.getId()), sb.toString());
    }

    private void eliminar(Atencion a) {
        if (!Dialogo.confirmar(Idioma.t("atencion.confirmarEliminar", a.getMascota().getNombre(), Formato.fecha(a.getFecha())))) {
            return;
        }
        try {
            atencionServicio.eliminar(a.getId());
            avisar(Idioma.t("atencion.eliminada"), Notificacion.Tipo.INFO);
            buscar();
        } catch (IllegalStateException e) {
            Dialogo.error(e.getMessage());
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
