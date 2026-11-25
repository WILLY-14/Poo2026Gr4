package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Combos;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Dialogo;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Formato;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Notificacion;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Sugerencias;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IPropietarioServicio;
import pe.edu.upeu.padronmascota.padronmascota.dto.OpcionCombo;
import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoCita;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Cita;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Mascota;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Veterinario;
import pe.edu.upeu.padronmascota.padronmascota.servicio.ICitaServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IMascotaServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IVeterinarioServicio;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Agenda de citas. Un veterinario no puede tener dos citas a la misma fecha y hora. */
public class CitaController extends CrudController<Cita> {

    @FXML private TextField txtMascota;
    @FXML private Label lblEspecieNueva;
    @FXML private ComboBox<Especie> cboEspecieNueva;
    @FXML private Label lblDueno;
    @FXML private TextField txtDueno;
    @FXML private Label lblDniDueno;
    @FXML private TextField txtDniDueno;
    @FXML private Label lblTelDueno;
    @FXML private TextField txtTelDueno;
    @FXML private Label lblNotaNueva;
    @FXML private ComboBox<Veterinario> cboVeterinario;   // solo los doctores registrados en Veterinarios
    @FXML private DatePicker dtpFecha;
    @FXML private TextField txtHora;
    @FXML private TextField txtMotivo;
    @FXML private ComboBox<OpcionCombo<EstadoCita>> cboFiltroEstado;

    private final ICitaServicio citaServicio;
    private final IMascotaServicio mascotaServicio;
    private final IVeterinarioServicio veterinarioServicio;
    private final IPropietarioServicio propietarioServicio;

    public CitaController(ICitaServicio citaServicio, IMascotaServicio mascotaServicio,
                          IVeterinarioServicio veterinarioServicio, IPropietarioServicio propietarioServicio) {
        this.citaServicio = citaServicio;
        this.mascotaServicio = mascotaServicio;
        this.veterinarioServicio = veterinarioServicio;
        this.propietarioServicio = propietarioServicio;
    }

    @Override
    protected void configurar() {
        cboEspecieNueva.getItems().setAll(Especie.values());
        // Se escribe; las sugerencias se buscan en la base cada vez (siempre al día)
        Sugerencias.en(txtMascota, t -> mascotas().stream()
                .filter(m -> etiqueta(m).toLowerCase().contains(t)).map(CitaController::etiqueta).toList());
        Sugerencias.en(txtDueno, t -> propietarioServicio.listarTodos().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(t) || (p.getDni() != null && p.getDni().contains(t)))
                .map(CitaController::etiqueta).toList());
        // Se recarga cada vez que se abre: así salen los doctores recién registrados
        cboVeterinario.setOnShowing(e -> cargarVeterinarios());
        cargarVeterinarios();
        txtMascota.textProperty().addListener((o, a, b) -> mostrarCamposMascotaNueva());
        txtDueno.textProperty().addListener((o, a, b) -> mostrarCamposMascotaNueva());
        Combos.filtro(cboFiltroEstado, Idioma.t("comun.todos"), Arrays.asList(EstadoCita.values()), EstadoCita::getNombre);
    }

    @Override
    protected IServicioCrud<Cita> servicio() {
        return citaServicio;
    }

    @Override
    protected String prefijo() {
        return "cita";
    }

    @Override
    protected void columnas(TablaConAcciones<Cita> t) {
        t.columna(Idioma.t("comun.id"), Cita::getId, 40)
                .columna(Idioma.t("cita.fecha"), c -> Formato.fecha(c.getFecha()), 100)
                .columna(Idioma.t("cita.hora"), c -> Formato.hora(c.getHora()), 55)
                .columna(Idioma.t("cita.mascota"), c -> c.getMascota().getNombre(), 90)
                .columna(Idioma.t("mascota.propietario"), c -> c.getMascota().getPropietario().getNombre(), 150)
                .columna(Idioma.t("cita.veterinario"), c -> c.getVeterinario().getNombre(), 160)
                .columna(Idioma.t("cita.motivo"), Cita::getMotivo, 160)
                .columna(Idioma.t("cita.estado"), Cita::getEstado, 85);
    }

    @Override
    protected void acciones(TablaConAcciones<Cita> t) {
        t.acciones(Idioma.t("comun.editar"), this::editar, Idioma.t("cita.cancelar"), this::cancelar,
                Idioma.t("comun.eliminar"), this::eliminar);
    }

    @Override
    protected List<Cita> buscarEnServicio(String texto) {
        return citaServicio.buscar(texto, Combos.valorFiltro(cboFiltroEstado));
    }

    private void cancelar(Cita c) {
        if (!Dialogo.confirmar(Idioma.t("cita.confirmarCancelar", c.getMascota().getNombre(), Formato.fecha(c.getFecha())))) {
            return;
        }
        try {
            citaServicio.cancelar(c.getId());
            avisar(Idioma.t("cita.cancelada"), Notificacion.Tipo.INFO);
            buscar();
        } catch (RuntimeException e) {
            Dialogo.error(e.getMessage());
        }
    }

    // ---------- Mascota, dueño y veterinario escritos ----------

    private static String texto(TextField t) {
        return t.getText() == null ? "" : t.getText().trim();
    }

    private static void mostrar(boolean ver, javafx.scene.Node... nodos) {
        for (javafx.scene.Node n : nodos) {
            n.setVisible(ver);
            n.setManaged(ver);
        }
    }

    private List<Mascota> mascotas() {
        return mascotaServicio.buscar("", null, null, true);   // solo las activas
    }

    /** "Kiwi - Rosa Flores" (así se distinguen dos mascotas con el mismo nombre). */
    private static String etiqueta(Mascota m) {
        return m.getNombre() + " - " + m.getPropietario().getNombre();
    }

    private static String etiqueta(Propietario p) {
        return p.getDni() == null ? p.getNombre() : p.getNombre() + " - " + p.getDni();
    }

    /** Busca por "nombre - dueño"; si solo se escribió el nombre y hay una sola con ese nombre, también vale. */
    private Mascota mascotaPorTexto(String t) {
        if (t.isEmpty()) {
            return null;
        }
        List<Mascota> todas = mascotas();
        Mascota m = todas.stream().filter(x -> etiqueta(x).equalsIgnoreCase(t)).findFirst().orElse(null);
        if (m != null) {
            return m;
        }
        List<Mascota> mismoNombre = todas.stream().filter(x -> x.getNombre().equalsIgnoreCase(t)).toList();
        return mismoNombre.size() == 1 ? mismoNombre.get(0) : null;
    }

    private Propietario propietarioPorTexto(String t) {
        List<Propietario> todos = propietarioServicio.listarTodos();
        return todos.stream().filter(p -> etiqueta(p).equalsIgnoreCase(t)).findFirst()
                .or(() -> todos.stream().filter(p -> t.equals(p.getDni())).findFirst())
                .or(() -> todos.stream().filter(p -> p.getNombre().equalsIgnoreCase(t)).findFirst())
                .orElse(null);
    }

    /** Los veterinarios activos (los de la pestaña Veterinarios), sin perder el elegido. */
    private void cargarVeterinarios() {
        Veterinario elegido = cboVeterinario.getValue();
        Combos.llenar(cboVeterinario, veterinarioServicio.listarTodos());
        Combos.elegirPorId(cboVeterinario, elegido);
    }

    /** Mascota nueva: aparecen Especie y Dueño. Dueño nuevo: aparecen DNI y Teléfono. */
    private void mostrarCamposMascotaNueva() {
        String t = texto(txtMascota);
        boolean nueva = !t.isEmpty() && mascotaPorTexto(t) == null;
        mostrar(nueva, lblEspecieNueva, cboEspecieNueva, lblDueno, txtDueno, lblNotaNueva);
        String d = texto(txtDueno);
        mostrar(nueva && !d.isEmpty() && propietarioPorTexto(d) == null, lblDniDueno, txtDniDueno, lblTelDueno, txtTelDueno);
    }

    /** La mascota escrita; si es nueva la registra "por completar" (con su dueño, nuevo o no). */
    private Mascota mascotaEscrita() {
        String t = texto(txtMascota);
        if (t.isEmpty()) {
            return null;   // la validación de Cita avisa "Elige la mascota"
        }
        Mascota m = mascotaPorTexto(t);
        if (m != null) {
            return m;
        }
        if (errores.hayErrores()) {
            return null;   // no registramos nada si ya hay otro error en el formulario
        }
        Propietario dueno = duenoEscrito();
        if (dueno == null && !texto(txtDueno).isEmpty()) {
            return null;   // el error del dueño ya quedó marcado
        }
        try {
            Mascota nueva = mascotaServicio.registrarPorCompletar(t, cboEspecieNueva.getValue(), dueno);
            avisar(Idioma.t("cita.mascotaRegistrada", nueva.getNombre()), Notificacion.Tipo.INFO);
            Sugerencias.ponerTexto(txtMascota, nueva.getNombre() + " - " + dueno.getNombre());
            return nueva;
        } catch (ValidacionException e) {
            e.getErrores().forEach((campo, msg) -> errores.marcar(
                    "especie".equals(campo) ? cboEspecieNueva : "propietario".equals(campo) ? txtDueno : txtMascota, msg));
        } catch (RuntimeException e) {
            errores.marcar(txtMascota, mensajeAmigable(e));
        }
        return null;
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

    @Override
    protected Cita leerFormulario() {
        Cita c = new Cita();
        c.setFecha(dtpFecha.getValue());
        try {
            c.setHora(Formato.leerHora(txtHora.getText()));
        } catch (DateTimeParseException e) {
            errores.marcar(txtHora, Idioma.t("cita.horaInvalida"));
        }
        c.setMotivo(txtMotivo.getText());
        c.setVeterinario(cboVeterinario.getValue());
        // Al final, porque puede registrar una mascota nueva
        c.setMascota(mascotaEscrita());
        if (enEdicion != null) {
            c.setId(enEdicion.getId());      // para que no choque consigo misma al revisar el horario
            c.setEstado(enEdicion.getEstado());
        }
        return c;
    }

    @Override
    protected void llenarFormulario(Cita c) {
        Mascota m = mascotaServicio.obtener(c.getMascota().getId());
        Sugerencias.ponerTexto(txtMascota, etiqueta(m));
        cargarVeterinarios();
        Combos.elegirPorId(cboVeterinario, c.getVeterinario());
        mostrarCamposMascotaNueva();
        dtpFecha.setValue(c.getFecha());
        txtHora.setText(Formato.hora(c.getHora()));
        txtMotivo.setText(c.getMotivo());
    }

    @Override
    protected void limpiarFormulario() {
        Sugerencias.ponerTexto(txtMascota, "");
        cboVeterinario.setValue(null);
        Sugerencias.ponerTexto(txtDueno, "");
        cboEspecieNueva.setValue(null);
        txtDniDueno.clear();
        txtTelDueno.clear();
        mostrar(false, lblEspecieNueva, cboEspecieNueva, lblDueno, txtDueno, lblDniDueno, txtDniDueno,
                lblTelDueno, txtTelDueno, lblNotaNueva);
        dtpFecha.setValue(LocalDate.now());
        txtHora.clear();
        txtMotivo.clear();
    }

    @Override
    protected Map<String, Control> campos() {
        return Map.of("mascota", txtMascota, "veterinario", cboVeterinario, "fecha", dtpFecha,
                "hora", txtHora, "motivo", txtMotivo);
    }
}
