package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Proveedor;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IProveedorServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Map;

/** Proveedores (a quién se le compra). RUC de 11 dígitos y único. */
public class ProveedorController extends CrudController<Proveedor> {

    @FXML private TextField txtRuc;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;

    private final IProveedorServicio proveedorServicio;

    public ProveedorController(IProveedorServicio proveedorServicio) {
        this.proveedorServicio = proveedorServicio;
    }

    @Override
    protected IServicioCrud<Proveedor> servicio() {
        return proveedorServicio;
    }

    @Override
    protected String prefijo() {
        return "proveedor";
    }

    @Override
    protected void columnas(TablaConAcciones<Proveedor> t) {
        t.columna(Idioma.t("comun.id"), Proveedor::getId, 45)
                .columna(Idioma.t("proveedor.ruc"), Proveedor::getRuc, 110)
                .columna(Idioma.t("comun.nombre"), Proveedor::getNombre, 230)
                .columna(Idioma.t("comun.telefono"), Proveedor::getTelefono, 100)
                .columna(Idioma.t("proveedor.compras"), Proveedor::getNumeroCompras, 90);
    }

    @Override
    protected List<Proveedor> buscarEnServicio(String texto) {
        return proveedorServicio.buscar(texto);
    }

    @Override
    protected Proveedor leerFormulario() {
        Proveedor p = new Proveedor();
        p.setRuc(txtRuc.getText());
        p.setNombre(txtNombre.getText());
        p.setTelefono(txtTelefono.getText());
        return p;
    }

    @Override
    protected void llenarFormulario(Proveedor p) {
        txtRuc.setText(p.getRuc());
        txtNombre.setText(p.getNombre());
        txtTelefono.setText(p.getTelefono());
        txtRuc.requestFocus();
    }

    @Override
    protected void limpiarFormulario() {
        txtRuc.clear();
        txtNombre.clear();
        txtTelefono.clear();
    }

    @Override
    protected Map<String, Control> campos() {
        return Map.of("ruc", txtRuc, "nombre", txtNombre, "telefono", txtTelefono);
    }
}
