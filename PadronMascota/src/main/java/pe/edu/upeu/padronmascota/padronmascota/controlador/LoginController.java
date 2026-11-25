package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.GestorVentana;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Tema;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Usuario;
import pe.edu.upeu.padronmascota.padronmascota.seguridad.Sesion;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IUsuarioServicio;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Optional;

/**
 * Pantalla de inicio de sesión. Arriba tiene un circulito por cada tema
 * para cambiar los colores, y un combo para elegir el idioma.
 */
public class LoginController {

    @FXML private HBox cajaTemas;
    @FXML private ComboBox<String> cboIdioma;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private Label lblMensaje;

    private final IUsuarioServicio usuarioServicio;

    public LoginController(IUsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    /** Abre la pantalla de login (al iniciar y al cerrar sesión). */
    public static void mostrarLogin() {
        GestorVentana.mostrar("login.fxml", 480, 530, false);
    }

    @FXML
    private void initialize() {
        dibujarTemas();
        for (String[] idioma : Idioma.DISPONIBLES) {
            cboIdioma.getItems().add(idioma[1]);
            if (idioma[0].equals(Idioma.actual())) {
                cboIdioma.getSelectionModel().select(idioma[1]);
            }
        }
        cboIdioma.setOnAction(e -> cambiarIdioma());
        lblMensaje.setText("");
        Platform.runLater(txtUsuario::requestFocus);
    }

    private void dibujarTemas() {
        for (Tema tema : Tema.values()) {
            Circle c = new Circle(12, Color.web(tema.getColor()));
            c.getStyleClass().add("circulo-tema");
            c.setCursor(Cursor.HAND);
            Tooltip.install(c, new Tooltip(tema.nombre()));
            c.setOnMouseClicked(e -> tema.aplicar());
            cajaTemas.getChildren().add(c);
        }
    }

    private void cambiarIdioma() {
        int i = cboIdioma.getSelectionModel().getSelectedIndex();
        String codigo = Idioma.DISPONIBLES[i][0];
        if (!codigo.equals(Idioma.actual())) {
            Idioma.cambiar(codigo);
            mostrarLogin(); // recarga el login con los textos nuevos
        }
    }

    @FXML
    private void ingresar() {
        String usuario = txtUsuario.getText();
        String clave = txtClave.getText();
        if (usuario.isBlank() || clave.isEmpty()) {
            lblMensaje.setText(Idioma.t("login.vacio"));
            return;
        }
        Optional<Usuario> u;
        try {
            u = usuarioServicio.login(usuario, clave);
        } catch (RuntimeException e) {
            // Por ejemplo: la base está abierta en DBeaver o no se pudo crear
            e.printStackTrace();
            Throwable causa = e;
            while (causa.getCause() != null) {
                causa = causa.getCause();
            }
            lblMensaje.setText("Error de base de datos: " + causa.getMessage());
            return;
        }
        if (u.isEmpty()) {
            lblMensaje.setText(Idioma.t("login.error"));
            txtClave.clear();
            txtClave.requestFocus();
            return;
        }
        Sesion.iniciar(u.get());
        GestorVentana.mostrar("principal.fxml");
    }

    @FXML
    private void cerrar() {
        Platform.exit();
    }
}
