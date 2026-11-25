package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Combos;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.enums.Rol;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Usuario;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Veterinario;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IUsuarioServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IVeterinarioServicio;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Usuarios del sistema (solo el Administrador). La clave se guarda como hash BCrypt. */
public class UsuarioController extends CrudController<Usuario> {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtClave;
    @FXML private ComboBox<Rol> cboRol;
    @FXML private ComboBox<Veterinario> cboVeterinario;   // solo los registrados en Veterinarios
    @FXML private CheckBox chkActivo;

    private final IUsuarioServicio usuarioServicio;
    private final IVeterinarioServicio veterinarioServicio;

    public UsuarioController(IUsuarioServicio usuarioServicio, IVeterinarioServicio veterinarioServicio) {
        this.usuarioServicio = usuarioServicio;
        this.veterinarioServicio = veterinarioServicio;
    }

    @Override
    protected void configurar() {
        Combos.llenar(cboRol, Arrays.asList(Rol.values()));
        // Se recarga cada vez que se abre (por si registraste un doctor en Veterinarios)
        cboVeterinario.setOnShowing(e -> cargarVeterinarios());
        cargarVeterinarios();
        // Si el rol deja de ser Veterinario, se borra el veterinario escrito
        cboRol.valueProperty().addListener((obs, antes, ahora) -> {
            if (ahora != Rol.VETERINARIO) {
                cboVeterinario.setValue(null);
            }
        });
        chkActivo.setText(Idioma.t("usuario.activoTexto"));
    }

    private void cargarVeterinarios() {
        Veterinario elegido = cboVeterinario.getValue();
        Combos.llenar(cboVeterinario, veterinarioServicio.listarTodos());
        Combos.elegirPorId(cboVeterinario, elegido);
    }

    @Override
    protected IServicioCrud<Usuario> servicio() {
        return usuarioServicio;
    }

    @Override
    protected String prefijo() {
        return "usuario";
    }

    @Override
    protected void columnas(TablaConAcciones<Usuario> t) {
        t.columna(Idioma.t("comun.id"), Usuario::getId, 40)
                .columna(Idioma.t("usuario.username"), Usuario::getUsername, 120)
                .columna(Idioma.t("usuario.rol"), Usuario::getRol, 110)
                .columna(Idioma.t("usuario.veterinario"), u -> u.getVeterinario() == null ? "-" : u.getVeterinario().getNombre(), 180)
                .columna(Idioma.t("usuario.estado"), Usuario::getEstado, 80);
    }

    @Override
    protected List<Usuario> buscarEnServicio(String texto) {
        String t = texto == null ? "" : texto.trim().toLowerCase();
        return usuarioServicio.listarTodos().stream()
                .filter(u -> t.isEmpty() || u.getUsername().contains(t) || u.getRol().getNombre().toLowerCase().contains(t))
                .toList();
    }

    @Override
    protected Usuario leerFormulario() {
        Usuario u = new Usuario();
        u.setUsername(txtUsername.getText());
        u.setClaveTexto(txtClave.getText());
        u.setRol(cboRol.getValue());
        u.setVeterinario(cboVeterinario.getValue());
        u.setActivo(chkActivo.isSelected());
        return u;
    }

    @Override
    protected void llenarFormulario(Usuario u) {
        txtUsername.setText(u.getUsername());
        txtClave.clear();   // vacía = no cambia la clave
        cboRol.setValue(u.getRol());
        cargarVeterinarios();
        Combos.elegirPorId(cboVeterinario, u.getVeterinario());
        chkActivo.setSelected(u.isActivo());
        txtUsername.requestFocus();
    }

    @Override
    protected void limpiarFormulario() {
        txtUsername.clear();
        txtClave.clear();
        cboRol.setValue(null);
        cboVeterinario.setValue(null);
        chkActivo.setSelected(true);
    }

    @Override
    protected Map<String, Control> campos() {
        return Map.of("username", txtUsername, "claveTexto", txtClave, "rol", cboRol,
                "veterinarioCorrecto", cboVeterinario);
    }
}
