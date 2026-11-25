package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Veterinario;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IVeterinarioServicio;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Map;

/** Doctores de la veterinaria. Gracias a CrudController solo escribimos los campos y columnas. */
public class VeterinarioController extends CrudController<Veterinario> {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;

    private final IVeterinarioServicio veterinarioServicio;

    public VeterinarioController(IVeterinarioServicio veterinarioServicio) {
        this.veterinarioServicio = veterinarioServicio;
    }

    @Override
    protected IServicioCrud<Veterinario> servicio() {
        return veterinarioServicio;
    }

    @Override
    protected String prefijo() {
        return "veterinario";
    }

    @Override
    protected void columnas(TablaConAcciones<Veterinario> t) {
        t.columna(Idioma.t("comun.id"), Veterinario::getId, 45)
                .columna(Idioma.t("comun.nombre"), Veterinario::getNombre, 220)
                .columna(Idioma.t("comun.telefono"), Veterinario::getTelefono, 100)
                .columna(Idioma.t("veterinario.atenciones"), Veterinario::getNumeroAtenciones, 100);
    }

    @Override
    protected List<Veterinario> buscarEnServicio(String texto) {
        return veterinarioServicio.buscar(texto);
    }

    @Override
    protected Veterinario leerFormulario() {
        Veterinario v = new Veterinario();
        v.setNombre(txtNombre.getText());
        v.setTelefono(txtTelefono.getText());
        return v;
    }

    @Override
    protected void llenarFormulario(Veterinario v) {
        txtNombre.setText(v.getNombre());
        txtTelefono.setText(v.getTelefono());
        txtNombre.requestFocus();
    }

    @Override
    protected void limpiarFormulario() {
        txtNombre.clear();
        txtTelefono.clear();
    }

    @Override
    protected Map<String, Control> campos() {
        return Map.of("nombre", txtNombre, "telefono", txtTelefono);
    }
}
