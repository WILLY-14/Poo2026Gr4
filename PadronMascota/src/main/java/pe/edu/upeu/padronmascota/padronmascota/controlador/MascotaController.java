package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Combos;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Dialogo;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Formato;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Notificacion;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Sugerencias;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.dto.OpcionCombo;
import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoVacunacion;
import pe.edu.upeu.padronmascota.padronmascota.enums.Sexo;
import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Color;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Mascota;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Raza;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IColorServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IMascotaServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IPropietarioServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IRazaServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Padrón de mascotas. Raza y color se ESCRIBEN (con sugerencias); si son nuevos se agregan solos a su catálogo.
 * "Eliminar" es un borrado lógico: la mascota queda inactiva (activo = No).
 */
public class MascotaController extends CrudController<Mascota> {

    @FXML private TextField txtNombre;
    @FXML private ComboBox<Especie> cboEspecie;
    @FXML private TextField txtRaza;
    @FXML private TextField txtColor;
    @FXML private ComboBox<Sexo> cboSexo;
    @FXML private TextField txtEdad;
    @FXML private DatePicker dtpFechaRegistro;
    @FXML private TextField txtPropietario;   // se escribe nombre o DNI, con sugerencias
    @FXML private javafx.scene.control.Label lblDniProp;
    @FXML private TextField txtDniProp;       // solo para un propietario nuevo
    @FXML private javafx.scene.control.Label lblTelProp;
    @FXML private TextField txtTelProp;       // solo para un propietario nuevo
    @FXML private ComboBox<EstadoVacunacion> cboEstado;
    @FXML private TextArea txtObservaciones;
    @FXML private ComboBox<OpcionCombo<Especie>> cboFiltroEspecie;
    @FXML private ComboBox<OpcionCombo<EstadoVacunacion>> cboFiltroEstado;
    @FXML private CheckBox chkInactivas;

    private final IMascotaServicio mascotaServicio;
    private final IPropietarioServicio propietarioServicio;
    private final IRazaServicio razaServicio;
    private final IColorServicio colorServicio;

    public MascotaController(IMascotaServicio mascotaServicio, IPropietarioServicio propietarioServicio,
                             IRazaServicio razaServicio, IColorServicio colorServicio) {
        this.mascotaServicio = mascotaServicio;
        this.propietarioServicio = propietarioServicio;
        this.razaServicio = razaServicio;
        this.colorServicio = colorServicio;
    }

    @Override
    protected void configurar() {
        Combos.llenar(cboEspecie, Arrays.asList(Especie.values()));
        Combos.llenar(cboSexo, Arrays.asList(Sexo.values()));
        Combos.llenar(cboEstado, Arrays.asList(EstadoVacunacion.values()));
        Combos.filtro(cboFiltroEspecie, Idioma.t("comun.todas"), Arrays.asList(Especie.values()), Especie::getNombre);
        Combos.filtro(cboFiltroEstado, Idioma.t("comun.todos"), Arrays.asList(EstadoVacunacion.values()), EstadoVacunacion::getNombre);
        // Raza: sugiere las razas de la especie elegida que contienen lo escrito
        Sugerencias.en(txtRaza, t -> cboEspecie.getValue() == null ? List.of()
                : razaServicio.buscar(t, cboEspecie.getValue()).stream().map(Raza::getNombre).toList());
        // Color: sugiere los colores que contienen lo escrito
        Sugerencias.en(txtColor, t -> colorServicio.buscar(t).stream().map(Color::getNombre).toList());
        // Propietario: sugiere por nombre o DNI ("María Quispe - 45879632")
        Sugerencias.en(txtPropietario, t -> propietarioServicio.listarTodos().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(t) || (p.getDni() != null && p.getDni().contains(t)))
                .map(MascotaController::etiqueta).toList());
        txtPropietario.textProperty().addListener((obs, antes, ahora) -> mostrarCamposPropietarioNuevo());
    }

    /** Busca la raza escrita en la especie; si no existe la registra. null si falta algo (y marca el error). */
    private Raza razaEscrita(Especie especie) {
        String nombre = txtRaza.getText() == null ? "" : txtRaza.getText().trim();
        if (nombre.isEmpty() || especie == null) {
            return null;   // la validación de Mascota avisa "Elige la raza" / "Elige la especie"
        }
        Raza r = razaServicio.deEspecie(especie).stream()
                .filter(x -> x.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null);
        if (r != null) {
            return r;
        }
        Raza nueva = new Raza();
        nueva.setNombre(nombre);
        nueva.setEspecie(especie);
        try {
            return razaServicio.registrar(nueva);   // raza nueva: se agrega al catálogo
        } catch (ValidacionException e) {
            errores.marcar(txtRaza, e.getErrores().values().iterator().next());
        } catch (RuntimeException e) {
            errores.marcar(txtRaza, mensajeAmigable(e));
        }
        return null;
    }

    /** Igual que la raza: busca el color escrito y si no existe lo registra. */
    private Color colorEscrito() {
        String nombre = txtColor.getText() == null ? "" : txtColor.getText().trim();
        if (nombre.isEmpty()) {
            return null;
        }
        Color c = colorServicio.listarTodos().stream()
                .filter(x -> x.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null);
        if (c != null) {
            return c;
        }
        Color nuevo = new Color();
        nuevo.setNombre(nombre);
        try {
            return colorServicio.registrar(nuevo);
        } catch (ValidacionException e) {
            errores.marcar(txtColor, e.getErrores().values().iterator().next());
        } catch (RuntimeException e) {
            errores.marcar(txtColor, mensajeAmigable(e));
        }
        return null;
    }

    /** Cómo se ve un propietario en el campo y en las sugerencias. */
    private static String etiqueta(Propietario p) {
        return p.getDni() == null ? p.getNombre() : p.getNombre() + " - " + p.getDni();
    }

    /** Busca el propietario escrito: por "nombre - dni", por DNI o por nombre exacto. null si es nuevo. */
    private Propietario propietarioPorTexto(String texto) {
        String t = texto.trim();
        List<Propietario> todos = propietarioServicio.listarTodos();
        return todos.stream().filter(p -> etiqueta(p).equalsIgnoreCase(t)).findFirst()
                .or(() -> todos.stream().filter(p -> t.equals(p.getDni())).findFirst())
                .or(() -> todos.stream().filter(p -> p.getNombre().equalsIgnoreCase(t)).findFirst())
                .orElse(null);
    }

    /** Si el propietario escrito no existe, aparecen DNI y Teléfono para registrarlo. */
    private void mostrarCamposPropietarioNuevo() {
        String t = txtPropietario.getText() == null ? "" : txtPropietario.getText().trim();
        boolean nuevo = !t.isEmpty() && propietarioPorTexto(t) == null;
        for (javafx.scene.Node n : List.of(lblDniProp, txtDniProp, lblTelProp, txtTelProp)) {
            n.setVisible(nuevo);
            n.setManaged(nuevo);
        }
    }

    /** Devuelve el propietario escrito; si es nuevo lo registra con el DNI y teléfono escritos. */
    private Propietario propietarioEscrito() {
        String t = txtPropietario.getText() == null ? "" : txtPropietario.getText().trim();
        if (t.isEmpty()) {
            return null;   // la validación de Mascota avisa "Elige el propietario"
        }
        Propietario p = propietarioPorTexto(t);
        if (p != null) {
            return p;
        }
        Propietario nuevo = new Propietario();
        nuevo.setNombre(t);
        nuevo.setDni(txtDniProp.getText() == null ? "" : txtDniProp.getText().trim());
        nuevo.setTelefono(txtTelProp.getText() == null ? "" : txtTelProp.getText().trim());
        try {
            return propietarioServicio.registrar(nuevo);
        } catch (ValidacionException e) {
            e.getErrores().forEach((campo, msg) -> errores.marcar(
                    "dni".equals(campo) ? txtDniProp : "telefono".equals(campo) ? txtTelProp : txtPropietario, msg));
        } catch (RuntimeException e) {
            errores.marcar(txtPropietario, mensajeAmigable(e));
        }
        return null;
    }

    @Override
    protected IServicioCrud<Mascota> servicio() {
        return mascotaServicio;
    }

    @Override
    protected String prefijo() {
        return "mascota";
    }

    @Override
    protected void columnas(TablaConAcciones<Mascota> t) {
        t.columna(Idioma.t("comun.id"), Mascota::getId, 40)
                .columna(Idioma.t("comun.nombre"), Mascota::getNombre, 90)
                .columna(Idioma.t("mascota.especie"), Mascota::getEspecie, 65)
                .columna(Idioma.t("mascota.raza"), Mascota::getRaza, 100)
                .columna(Idioma.t("mascota.color"), Mascota::getColor, 95)
                .columna(Idioma.t("mascota.sexo"), Mascota::getSexo, 65)
                .columna(Idioma.t("mascota.edadCorta"), Mascota::getEdadAprox, 45)
                .columna(Idioma.t("mascota.registro"), m -> Formato.fecha(m.getFechaRegistro()), 100)
                .columna(Idioma.t("mascota.propietario"), m -> m.getPropietario().getNombre(), 150)
                .columna(Idioma.t("mascota.vacunacion"), Mascota::getEstado, 80)
                .columna(Idioma.t("mascota.activo"), m -> m.isActivo() ? Idioma.t("comun.si") : Idioma.t("comun.no"), 50);
    }

    /** Las inactivas tienen el botón "Reactivar" en vez de "Dar de baja". */
    @Override
    protected void acciones(TablaConAcciones<Mascota> t) {
        t.acciones(Idioma.t("comun.editar"), this::editar, Idioma.t("mascota.baja"), this::bajaOReactivar,
                Idioma.t("comun.eliminar"), this::borrarDefinitivo);
    }

    @Override
    protected List<Mascota> buscarEnServicio(String texto) {
        boolean soloActivas = chkInactivas == null || !chkInactivas.isSelected();
        return mascotaServicio.buscar(texto, Combos.valorFiltro(cboFiltroEspecie), Combos.valorFiltro(cboFiltroEstado), soloActivas);
    }

    private void bajaOReactivar(Mascota m) {
        if (m.isActivo()) {
            eliminar(m);   // CrudController pide confirmación y el servicio la marca inactiva
            return;
        }
        try {
            mascotaServicio.reactivar(m.getId());
            avisar(Idioma.t("mascota.reactivada"), Notificacion.Tipo.EXITO);
            buscar();
        } catch (RuntimeException e) {
            Dialogo.error(e.getMessage());
        }
    }

    /** Borra la mascota de verdad, después de avisar qué se borra con ella. */
    private void borrarDefinitivo(Mascota m) {
        int[] h = mascotaServicio.historialQueSeBorra(m.getId());
        if (!Dialogo.confirmar(Idioma.t("mascota.confirmarBorrar", m.getNombre(), h[0], h[1], h[2], h[3], h[4]))) {
            return;
        }
        try {
            mascotaServicio.eliminarDefinitivo(m.getId());
            if (enEdicion != null && enEdicion.getId() == m.getId()) {
                nuevo();
            }
            avisar(Idioma.t("mascota.borrada", m.getNombre()), Notificacion.Tipo.INFO);
            buscar();
        } catch (IllegalStateException e) {
            Dialogo.error(e.getMessage());
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.eliminar", mensajeAmigable(e)));
        }
    }

    @Override
    protected Mascota leerFormulario() {
        Mascota m = new Mascota();
        m.setNombre(txtNombre.getText());
        m.setEspecie(cboEspecie.getValue());
        m.setRaza(razaEscrita(cboEspecie.getValue()));
        m.setColor(colorEscrito());
        m.setSexo(cboSexo.getValue());
        m.setEdadAprox(leerEntero(txtEdad));
        m.setFechaRegistro(dtpFechaRegistro.getValue());
        m.setPropietario(propietarioEscrito());
        m.setEstado(cboEstado.getValue());
        m.setObservaciones(txtObservaciones.getText());
        m.setActivo(enEdicion == null || enEdicion.isActivo());
        return m;
    }

    @Override
    protected void llenarFormulario(Mascota m) {
        txtNombre.setText(m.getNombre());
        cboEspecie.setValue(m.getEspecie());
        Sugerencias.ponerTexto(txtRaza, m.getRaza() == null ? "" : m.getRaza().getNombre());
        Sugerencias.ponerTexto(txtColor, m.getColor() == null ? "" : m.getColor().getNombre());
        cboSexo.setValue(m.getSexo());
        txtEdad.setText(String.valueOf(m.getEdadAprox()));
        dtpFechaRegistro.setValue(m.getFechaRegistro());
        Propietario dueno = m.getPropietario() == null ? null : propietarioServicio.obtener(m.getPropietario().getId());
        Sugerencias.ponerTexto(txtPropietario, dueno == null ? "" : etiqueta(dueno));
        cboEstado.setValue(m.getEstado());
        txtObservaciones.setText(m.getObservaciones());
        txtNombre.requestFocus();
    }

    @Override
    protected void limpiarFormulario() {
        txtNombre.clear();
        cboEspecie.setValue(null);
        txtRaza.clear();
        txtColor.clear();
        cboSexo.setValue(null);
        txtEdad.clear();
        dtpFechaRegistro.setValue(LocalDate.now());
        txtPropietario.clear();
        txtDniProp.clear();
        txtTelProp.clear();
        cboEstado.setValue(null);
        txtObservaciones.clear();
    }

    @Override
    protected Map<String, Control> campos() {
        return Map.ofEntries(
                Map.entry("nombre", txtNombre), Map.entry("especie", cboEspecie), Map.entry("raza", txtRaza),
                Map.entry("razaDeLaEspecie", txtRaza), Map.entry("color", txtColor), Map.entry("sexo", cboSexo),
                Map.entry("edadAprox", txtEdad), Map.entry("fechaRegistro", dtpFechaRegistro),
                Map.entry("propietario", txtPropietario), Map.entry("estado", cboEstado),
                Map.entry("observaciones", txtObservaciones));
    }
}
