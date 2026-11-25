package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Color;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IColorServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Map;

/** Catálogo de colores (tabla color). */
public class ColorController extends CrudController<Color> {

    @FXML private TextField txtNombre;

    private final IColorServicio colorServicio;

    public ColorController(IColorServicio colorServicio) {
        this.colorServicio = colorServicio;
    }

    @Override
    protected IServicioCrud<Color> servicio() {
        return colorServicio;
    }

    @Override
    protected String prefijo() {
        return "color";
    }

    @Override
    protected void columnas(TablaConAcciones<Color> t) {
        t.columna(Idioma.t("comun.id"), Color::getId, 45)
                .columna(Idioma.t("comun.nombre"), Color::getNombre, 200)
                .columna(Idioma.t("color.mascotas"), Color::getNumeroMascotas, 100);
    }

    @Override
    protected List<Color> buscarEnServicio(String texto) {
        return colorServicio.buscar(texto);
    }

    @Override
    protected Color leerFormulario() {
        Color c = new Color();
        c.setNombre(txtNombre.getText());
        return c;
    }

    @Override
    protected void llenarFormulario(Color c) {
        txtNombre.setText(c.getNombre());
        txtNombre.requestFocus();
    }

    @Override
    protected void limpiarFormulario() {
        txtNombre.clear();
    }

    @Override
    protected Map<String, Control> campos() {
        return Map.of("nombre", txtNombre);
    }
}
