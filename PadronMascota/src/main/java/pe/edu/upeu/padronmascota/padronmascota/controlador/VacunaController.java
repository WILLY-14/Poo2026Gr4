package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Combos;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.enums.CategoriaProducto;
import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Producto;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Vacuna;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IProductoServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IVacunaServicio;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Catálogo de vacunas. El producto (opcional) es la dosis que se descuenta del stock al vacunar. */
public class VacunaController extends CrudController<Vacuna> {

    @FXML private TextField txtNombre;
    @FXML private ComboBox<Especie> cboEspecie;
    @FXML private ComboBox<Producto> cboProducto;

    private final IVacunaServicio vacunaServicio;
    private final IProductoServicio productoServicio;

    public VacunaController(IVacunaServicio vacunaServicio, IProductoServicio productoServicio) {
        this.vacunaServicio = vacunaServicio;
        this.productoServicio = productoServicio;
    }

    @Override
    protected void configurar() {
        Combos.llenar(cboEspecie, Arrays.asList(Especie.values()));
        // null al inicio = "sin producto"
        List<Producto> dosis = new ArrayList<>();
        dosis.add(null);
        dosis.addAll(productoServicio.buscar(null, CategoriaProducto.VACUNA));
        Combos.llenar(cboProducto, dosis);
    }

    @Override
    protected IServicioCrud<Vacuna> servicio() {
        return vacunaServicio;
    }

    @Override
    protected String prefijo() {
        return "vacuna";
    }

    @Override
    protected void columnas(TablaConAcciones<Vacuna> t) {
        t.columna(Idioma.t("comun.id"), Vacuna::getId, 45)
                .columna(Idioma.t("comun.nombre"), Vacuna::getNombre, 180)
                .columna(Idioma.t("mascota.especie"), Vacuna::getEspecie, 80)
                .columna(Idioma.t("vacuna.producto"), v -> v.getProducto() == null ? "-" : v.getProducto().getNombre(), 200);
    }

    @Override
    protected List<Vacuna> buscarEnServicio(String texto) {
        return vacunaServicio.buscar(texto);
    }

    @Override
    protected Vacuna leerFormulario() {
        Vacuna v = new Vacuna();
        v.setNombre(txtNombre.getText());
        v.setEspecie(cboEspecie.getValue());
        v.setProducto(cboProducto.getValue());
        return v;
    }

    @Override
    protected void llenarFormulario(Vacuna v) {
        txtNombre.setText(v.getNombre());
        cboEspecie.setValue(v.getEspecie());
        Combos.elegirPorId(cboProducto, v.getProducto());
        txtNombre.requestFocus();
    }

    @Override
    protected void limpiarFormulario() {
        txtNombre.clear();
        cboEspecie.setValue(null);
        cboProducto.setValue(null);
    }

    @Override
    protected Map<String, Control> campos() {
        return Map.of("nombre", txtNombre, "especie", cboEspecie);
    }
}
