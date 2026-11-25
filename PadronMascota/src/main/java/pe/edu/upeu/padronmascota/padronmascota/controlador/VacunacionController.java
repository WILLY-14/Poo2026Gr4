package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.CampoBuscable;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IProductoServicio;
import pe.edu.upeu.padronmascota.padronmascota.enums.CategoriaProducto;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Producto;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IPropietarioServicio;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;
import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Notificacion;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Sugerencias;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Combos;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Formato;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Dialogo;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Impresion;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Mascota;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Vacuna;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Vacunacion;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IMascotaServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IVacunaServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IVacunacionServicio;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Vacunas aplicadas (la "Consulta vacunación" del Excel).
 * Al elegir la mascota, el combo de vacunas muestra solo las de su especie.
 * Dosis "Vencida" si la próxima dosis ya pasó.
 */
public class VacunacionController extends CrudController<Vacunacion> {

    @FXML private TextField txtMascota;
    private CampoBuscable<Mascota> cboMascota;   // se escribe, con sugerencias
    @FXML private TextField txtVacuna;
    private CampoBuscable<Producto> cboVacuna;   // sale de Productos (categoría Vacuna)
    @FXML private Label lblEspecieNueva;
    @FXML private ComboBox<Especie> cboEspecieNueva;
    @FXML private Label lblDueno;
    @FXML private TextField txtDueno;
    @FXML private Label lblDniDueno;
    @FXML private TextField txtDniDueno;
    @FXML private Label lblTelDueno;
    @FXML private TextField txtTelDueno;
    @FXML private Label lblNotaNueva;
    @FXML private DatePicker dtpFecha;
    @FXML private DatePicker dtpProxima;
    @FXML private CheckBox chkVencidas;

    private final IVacunacionServicio vacunacionServicio;
    private final IMascotaServicio mascotaServicio;
    private final IVacunaServicio vacunaServicio;
    private final IPropietarioServicio propietarioServicio;
    private final IProductoServicio productoServicio;

    public VacunacionController(IVacunacionServicio vacunacionServicio, IMascotaServicio mascotaServicio,
                                IVacunaServicio vacunaServicio, IPropietarioServicio propietarioServicio,
                                IProductoServicio productoServicio) {
        this.propietarioServicio = propietarioServicio;
        this.productoServicio = productoServicio;
        this.vacunacionServicio = vacunacionServicio;
        this.mascotaServicio = mascotaServicio;
        this.vacunaServicio = vacunaServicio;
    }

    @Override
    protected void configurar() {
        cboMascota = new CampoBuscable<>(txtMascota, () -> mascotaServicio.buscar("", null, null, true),
                m -> m.getNombre() + " - " + m.getPropietario().getNombre());
        // La vacuna se escribe y sale de Productos de categoría "Vacuna" (se descuenta 1 del stock)
        cboVacuna = new CampoBuscable<>(txtVacuna, () -> productoServicio.buscar(null, CategoriaProducto.VACUNA),
                p -> p.getNombre() + " (stock " + p.getStock() + ")");
        cboEspecieNueva.getItems().setAll(Especie.values());
        Sugerencias.en(txtDueno, t -> propietarioServicio.listarTodos().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(t) || (p.getDni() != null && p.getDni().contains(t)))
                .map(VacunacionController::etiqueta).toList());
        txtMascota.textProperty().addListener((o, a, b) -> mostrarCamposMascotaNueva());
        txtDueno.textProperty().addListener((o, a, b) -> mostrarCamposMascotaNueva());
        // Al elegir la fecha, sugiere la próxima dosis en 1 año
        dtpFecha.valueProperty().addListener((obs, antes, ahora) -> {
            if (ahora != null && enEdicion == null) {
                dtpProxima.setValue(ahora.plusYears(1));
            }
        });
    }

    @Override
    protected IServicioCrud<Vacunacion> servicio() {
        return vacunacionServicio;
    }

    @Override
    protected String prefijo() {
        return "vacunacion";
    }

    /** Editar, Carnet (imprimir todas las vacunas de esa mascota) y Eliminar. */
    @Override
    protected void acciones(TablaConAcciones<Vacunacion> t) {
        t.acciones(Idioma.t("comun.editar"), this::editar, Idioma.t("impresion.carnet"), this::imprimirCarnet,
                Idioma.t("comun.eliminar"), this::eliminar);
    }

    /** Carnet de vacunas: todas las vacunas de la mascota, con la PRÓXIMA DOSIS resaltada. */
    private void imprimirCarnet(Vacunacion v) {
        try {
            Mascota m = mascotaServicio.obtener(v.getMascota().getId());
            List<Vacunacion> suyas = vacunacionServicio.buscar("", false).stream()
                    .filter(x -> x.getMascota().getId() == m.getId())
                    .sorted(java.util.Comparator.comparing(Vacunacion::getFecha)).toList();
            javafx.scene.layout.VBox hoja = Impresion.hoja(Idioma.t("impresion.carnetTitulo"),
                    Idioma.t("impresion.emitido", Formato.fecha(LocalDate.now())));
            hoja.getChildren().addAll(
                    Impresion.seccion(Idioma.t("impresion.paciente")),
                    Impresion.datos(Idioma.t("atencion.mascota"), m.getNombre(),
                            Idioma.t("mascota.especie"), String.valueOf(m.getEspecie()),
                            Idioma.t("mascota.raza"), m.getRaza() == null ? "" : m.getRaza().getNombre(),
                            Idioma.t("mascota.sexo"), m.getSexo() == null ? "" : String.valueOf(m.getSexo()),
                            Idioma.t("mascota.propietario"), m.getPropietario().getNombre(),
                            Idioma.t("propietario.dni"), m.getPropietario().getDni()),
                    Impresion.seccion(Idioma.t("impresion.vacunasAplicadas")),
                    Impresion.tabla(List.of(Idioma.t("vacunacion.vacuna"), Idioma.t("vacunacion.fecha"),
                                    Idioma.t("vacunacion.proxima")),
                            new double[]{256, 120, 120},
                            suyas.stream().map(x -> List.of(x.getVacuna().getNombre(), Formato.fecha(x.getFecha()),
                                    x.getProximaDosis() == null ? "-" : Formato.fecha(x.getProximaDosis()))).toList(), 2));
            // Lo más importante para el dueño: cuándo debe volver
            suyas.stream().map(Vacunacion::getProximaDosis).filter(java.util.Objects::nonNull)
                    .filter(f -> !f.isBefore(LocalDate.now())).min(LocalDate::compareTo)
                    .ifPresent(f -> {
                        javafx.scene.control.Label aviso = Impresion.texto(
                                Idioma.t("impresion.vuelva", Formato.fecha(f)), 14, true, "#b3261e");
                        aviso.setStyle(aviso.getStyle() + " -fx-border-color: #b3261e; -fx-border-width: 1.5;"
                                + " -fx-padding: 8 12 8 12; -fx-background-color: #fdf1f0;");
                        hoja.getChildren().add(aviso);
                    });
            hoja.getChildren().add(Impresion.firma(Idioma.t("app.titulo")));
            Impresion.vistaPrevia(tabla.getScene().getWindow(), Idioma.t("impresion.carnetTitulo") + " - " + m.getNombre(), hoja);
        } catch (RuntimeException e) {
            Dialogo.error(e.getMessage());
        }
    }

    @Override
    protected void columnas(TablaConAcciones<Vacunacion> t) {
        t.columna(Idioma.t("comun.id"), Vacunacion::getId, 40)
                .columna(Idioma.t("vacunacion.mascota"), v -> v.getMascota().getNombre(), 90)
                .columna(Idioma.t("mascota.propietario"), v -> v.getMascota().getPropietario().getNombre(), 150)
                .columna(Idioma.t("vacunacion.vacuna"), v -> v.getVacuna().getNombre(), 140)
                .columna(Idioma.t("vacunacion.fecha"), v -> Formato.fecha(v.getFecha()), 100)
                .columna(Idioma.t("vacunacion.proxima"), v -> Formato.fecha(v.getProximaDosis()), 95)
                .columna(Idioma.t("vacunacion.dosis"), Vacunacion::getEstadoDosis, 80);
    }

    @Override
    protected List<Vacunacion> buscarEnServicio(String texto) {
        return vacunacionServicio.buscar(texto, chkVencidas != null && chkVencidas.isSelected());
    }

    @Override
    protected String textoTotal(List<Vacunacion> lista) {
        long vencidas = lista.stream().filter(Vacunacion::isVencida).count();
        return Idioma.t("vacunacion.total", lista.size(), vencidas);
    }

    // ---------- Mascota y vacuna escritas (si son nuevas se registran) ----------

    private static String texto(TextField t) {
        return t.getText() == null ? "" : t.getText().trim();
    }

    private static void mostrar(boolean ver, javafx.scene.Node... nodos) {
        for (javafx.scene.Node n : nodos) {
            n.setVisible(ver);
            n.setManaged(ver);
        }
    }

    private static String etiqueta(Propietario p) {
        return p.getDni() == null ? p.getNombre() : p.getNombre() + " - " + p.getDni();
    }

    /** Especie de la mascota escrita, o la elegida para la mascota nueva. */
    private Especie especie() {
        return cboMascota.getValue() != null ? cboMascota.getValue().getEspecie() : cboEspecieNueva.getValue();
    }

    private boolean mascotaNueva() {
        return !texto(txtMascota).isEmpty() && cboMascota.getValue() == null;
    }

    private Propietario propietarioPorTexto(String t) {
        List<Propietario> todos = propietarioServicio.listarTodos();
        return todos.stream().filter(p -> etiqueta(p).equalsIgnoreCase(t)).findFirst()
                .or(() -> todos.stream().filter(p -> t.equals(p.getDni())).findFirst())
                .or(() -> todos.stream().filter(p -> p.getNombre().equalsIgnoreCase(t)).findFirst())
                .orElse(null);
    }

    private void mostrarCamposMascotaNueva() {
        boolean nueva = mascotaNueva();
        mostrar(nueva, lblEspecieNueva, cboEspecieNueva, lblDueno, txtDueno, lblNotaNueva);
        String d = texto(txtDueno);
        mostrar(nueva && !d.isEmpty() && propietarioPorTexto(d) == null, lblDniDueno, txtDniDueno, lblTelDueno, txtTelDueno);
    }

    private Propietario duenoEscrito() {
        String d = texto(txtDueno);
        if (d.isEmpty()) {
            return null;
        }
        Propietario p = propietarioPorTexto(d);
        if (p != null) {
            return p;
        }
        Propietario nuevo = new Propietario();
        nuevo.setNombre(d);
        nuevo.setDni(texto(txtDniDueno));
        nuevo.setTelefono(texto(txtTelDueno));
        try {
            return propietarioServicio.registrar(nuevo);
        } catch (ValidacionException e) {
            e.getErrores().forEach((campo, msg) -> errores.marcar(
                    "dni".equals(campo) ? txtDniDueno : "telefono".equals(campo) ? txtTelDueno : txtDueno, msg));
        } catch (RuntimeException e) {
            errores.marcar(txtDueno, mensajeAmigable(e));
        }
        return null;
    }

    /** La mascota escrita; si vino solo a vacunarse y es nueva, se registra "por completar". */
    private Mascota mascotaEscrita() {
        if (!mascotaNueva()) {
            return cboMascota.getValue();
        }
        if (errores.hayErrores()) {
            return null;
        }
        Propietario dueno = duenoEscrito();
        if (dueno == null && !texto(txtDueno).isEmpty()) {
            return null;
        }
        try {
            Mascota nueva = mascotaServicio.registrarPorCompletar(texto(txtMascota), cboEspecieNueva.getValue(), dueno);
            cboMascota.setValue(nueva);
            avisar(Idioma.t("cita.mascotaRegistrada", nueva.getNombre()), Notificacion.Tipo.INFO);
            return nueva;
        } catch (ValidacionException e) {
            e.getErrores().forEach((campo, msg) -> errores.marcar(
                    "especie".equals(campo) ? cboEspecieNueva : "propietario".equals(campo) ? txtDueno : txtMascota, msg));
        } catch (RuntimeException e) {
            errores.marcar(txtMascota, mensajeAmigable(e));
        }
        return null;
    }

    /** La vacuna escrita: un producto de categoría Vacuna. Si no está en Productos, avisa que se compre primero. */
    private Vacuna vacunaEscrita(Mascota m) {
        String t = texto(txtVacuna);
        Producto p = cboVacuna.getValue();
        if (t.isEmpty() || m == null) {
            return null;   // la validación avisa "Elige la vacuna"
        }
        if (p == null) {
            errores.marcar(txtVacuna, Idioma.t("vacunacion.noEnProductos", t));
            return null;
        }
        try {
            return vacunaServicio.paraProducto(p, m.getEspecie());
        } catch (ValidacionException e) {
            errores.marcar(txtVacuna, e.getErrores().values().iterator().next());
        } catch (RuntimeException e) {
            errores.marcar(txtVacuna, mensajeAmigable(e));
        }
        return null;
    }

    @Override
    protected Vacunacion leerFormulario() {
        Vacunacion v = new Vacunacion();
        Mascota m = mascotaEscrita();
        v.setMascota(m);
        v.setVacuna(errores.hayErrores() ? null : vacunaEscrita(m));
        v.setFecha(dtpFecha.getValue());
        v.setProximaDosis(dtpProxima.getValue());
        return v;
    }

    @Override
    protected void llenarFormulario(Vacunacion v) {
        cboMascota.elegirPorId(v.getMascota());
        if (v.getVacuna().getProducto() != null) {
            cboVacuna.elegirPorId(v.getVacuna().getProducto());
        } else {
            Sugerencias.ponerTexto(txtVacuna, v.getVacuna().getNombre());   // vacuna antigua sin producto
        }
        dtpFecha.setValue(v.getFecha());
        dtpProxima.setValue(v.getProximaDosis());
    }

    @Override
    protected void limpiarFormulario() {
        cboMascota.setValue(null);
        cboVacuna.setValue(null);
        Sugerencias.ponerTexto(txtDueno, "");
        cboEspecieNueva.setValue(null);
        txtDniDueno.clear();
        txtTelDueno.clear();
        mostrar(false, lblEspecieNueva, cboEspecieNueva, lblDueno, txtDueno, lblDniDueno, txtDniDueno,
                lblTelDueno, txtTelDueno, lblNotaNueva);
        dtpFecha.setValue(LocalDate.now());
        dtpProxima.setValue(LocalDate.now().plusYears(1));
    }

    @Override
    protected Map<String, Control> campos() {
        return Map.of("mascota", cboMascota.getCampo(), "vacuna", cboVacuna.getCampo(), "vacunaDeLaEspecie", cboVacuna.getCampo(),
                "fecha", dtpFecha, "proximaDosis", dtpProxima, "proximaDosisValida", dtpProxima);
    }
}
