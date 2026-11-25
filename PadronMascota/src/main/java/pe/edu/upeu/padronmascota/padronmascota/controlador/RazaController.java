package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Combos;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.dto.OpcionCombo;
import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Raza;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IRazaServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Catálogo de razas: cada raza es de una especie. */
public class RazaController extends CrudController<Raza> {

    @FXML private TextField txtNombre;
    @FXML private ComboBox<Especie> cboEspecie;
    @FXML private ComboBox<OpcionCombo<Especie>> cboFiltroEspecie;

    private final IRazaServicio razaServicio;

    public RazaController(IRazaServicio razaServicio) {
        this.razaServicio = razaServicio;
    }

    @Override
    protected void configurar() {
        Combos.llenar(cboEspecie, Arrays.asList(Especie.values()));
        Combos.filtro(cboFiltroEspecie, Idioma.t("comun.todas"), Arrays.asList(Especie.values()), Especie::getNombre);
    }

    @Override
    protected IServicioCrud<Raza> servicio() {
        return razaServicio;
    }

    @Override
    protected String prefijo() {
        return "raza";
    }

    @Override
    protected void columnas(TablaConAcciones<Raza> t) {
        t.columna(Idioma.t("comun.id"), Raza::getId, 45)
                .columna(Idioma.t("comun.nombre"), Raza::getNombre, 200)
                .columna(Idioma.t("mascota.especie"), Raza::getEspecie, 100)
                .columna(Idioma.t("color.mascotas"), Raza::getNumeroMascotas, 90);
    }

    @Override
    protected List<Raza> buscarEnServicio(String texto) {
        return razaServicio.buscar(texto, Combos.valorFiltro(cboFiltroEspecie));
    }

    @Override
    protected Raza leerFormulario() {
        Raza r = new Raza();
        r.setNombre(txtNombre.getText());
        r.setEspecie(cboEspecie.getValue());
        return r;
    }

    @Override
    protected void llenarFormulario(Raza r) {
        txtNombre.setText(r.getNombre());
        cboEspecie.setValue(r.getEspecie());
        txtNombre.requestFocus();
    }

    @Override
    protected void limpiarFormulario() {
        txtNombre.clear();
        cboEspecie.setValue(null);
    }

    @Override
    protected Map<String, Control> campos() {
        return Map.of("nombre", txtNombre, "especie", cboEspecie);
    }
}
