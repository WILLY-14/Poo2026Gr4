package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IPropietarioServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Map;

/** Dueños de las mascotas: DNI (8 dígitos, único), nombre, teléfono, dirección y email. */
public class PropietarioController extends CrudController<Propietario> {

    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtEmail;

    private final IPropietarioServicio propietarioServicio;

    public PropietarioController(IPropietarioServicio propietarioServicio) {
        this.propietarioServicio = propietarioServicio;
    }

    @Override
    protected IServicioCrud<Propietario> servicio() {
        return propietarioServicio;
    }

    @Override
    protected String prefijo() {
        return "propietario";
    }

    @Override
    protected void columnas(TablaConAcciones<Propietario> t) {
        t.columna(Idioma.t("comun.id"), Propietario::getId, 40)
                .columna(Idioma.t("propietario.dni"), Propietario::getDni, 80)
                .columna(Idioma.t("comun.nombre"), Propietario::getNombre, 170)
                .columna(Idioma.t("comun.telefono"), Propietario::getTelefono, 85)
                .columna(Idioma.t("propietario.direccion"), Propietario::getDireccion, 170)
                .columna(Idioma.t("propietario.email"), Propietario::getEmail, 170)
                .columna(Idioma.t("propietario.mascotas"), Propietario::getNumeroMascotas, 80);
    }

    @Override
    protected List<Propietario> buscarEnServicio(String texto) {
        return propietarioServicio.buscar(texto);
    }

    @Override
    protected Propietario leerFormulario() {
        Propietario p = new Propietario();
        p.setDni(txtDni.getText());
        p.setNombre(txtNombre.getText());
        p.setTelefono(txtTelefono.getText());
        p.setDireccion(txtDireccion.getText());
        p.setEmail(txtEmail.getText());
        return p;
    }

    @Override
    protected void llenarFormulario(Propietario p) {
        txtDni.setText(p.getDni());
        txtNombre.setText(p.getNombre());
        txtTelefono.setText(p.getTelefono());
        txtDireccion.setText(p.getDireccion());
        txtEmail.setText(p.getEmail());
        txtDni.requestFocus();
    }

    @Override
    protected void limpiarFormulario() {
        txtDni.clear();
        txtNombre.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtEmail.clear();
    }

    @Override
    protected Map<String, Control> campos() {
        return Map.of("dni", txtDni, "nombre", txtNombre, "telefono", txtTelefono,
                "direccion", txtDireccion, "email", txtEmail);
    }
}
