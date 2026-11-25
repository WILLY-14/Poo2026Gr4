package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Combos;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Formato;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.dto.OpcionCombo;
import pe.edu.upeu.padronmascota.padronmascota.enums.CategoriaProducto;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Producto;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IProductoServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Productos (la "Consulta stock" del Excel). El stock no se escribe:
 * se calcula con las compras, las ventas pagadas y lo usado en atenciones y vacunas.
 */
public class ProductoController extends CrudController<Producto> {

    @FXML private TextField txtNombre;
    @FXML private ComboBox<CategoriaProducto> cboCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStockMinimo;
    @FXML private ComboBox<OpcionCombo<CategoriaProducto>> cboFiltroCategoria;

    private final IProductoServicio productoServicio;

    public ProductoController(IProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }

    @Override
    protected void configurar() {
        Combos.llenar(cboCategoria, Arrays.asList(CategoriaProducto.values()));
        Combos.filtro(cboFiltroCategoria, Idioma.t("comun.todas"), Arrays.asList(CategoriaProducto.values()),
                CategoriaProducto::toString);
    }

    @Override
    protected IServicioCrud<Producto> servicio() {
        return productoServicio;
    }

    @Override
    protected String prefijo() {
        return "producto";
    }

    @Override
    protected void columnas(TablaConAcciones<Producto> t) {
        t.columna(Idioma.t("comun.id"), Producto::getId, 40)
                .columna(Idioma.t("comun.nombre"), Producto::getNombre, 150)
                .columna(Idioma.t("producto.categoria"), Producto::getCategoria, 125)
                .columna(Idioma.t("comun.precio"), p -> Formato.dinero(p.getPrecio()), 75)
                .columna(Idioma.t("producto.comprados"), Producto::getComprados, 75)
                .columna(Idioma.t("producto.vendidos"), Producto::getVendidos, 65)
                .columna(Idioma.t("producto.usados"), Producto::getUsados, 60)
                .columna(Idioma.t("producto.stock"), Producto::getStock, 55)
                .columna(Idioma.t("producto.stockMinimo"), Producto::getStockMinimo, 60)
                .columna(Idioma.t("producto.ultimoCosto"), p -> Formato.dinero(p.getUltimoCosto()), 80)
                .columna(Idioma.t("producto.margen"), p -> Formato.dinero(p.getMargen()), 70)
                .columna(Idioma.t("producto.alerta"), p -> p.stockBajo() ? Idioma.t("producto.stockBajo") : "OK", 85);
    }

    @Override
    protected List<Producto> buscarEnServicio(String texto) {
        return productoServicio.buscar(texto, Combos.valorFiltro(cboFiltroCategoria));
    }

    @Override
    protected String textoTotal(List<Producto> lista) {
        long bajos = lista.stream().filter(Producto::stockBajo).count();
        return Idioma.t("producto.total", lista.size(), bajos);
    }

    @Override
    protected Producto leerFormulario() {
        Producto p = new Producto();
        p.setNombre(txtNombre.getText());
        p.setCategoria(cboCategoria.getValue());
        p.setPrecio(leerDinero(txtPrecio));
        p.setStockMinimo(leerEntero(txtStockMinimo));
        return p;
    }

    @Override
    protected void llenarFormulario(Producto p) {
        txtNombre.setText(p.getNombre());
        cboCategoria.setValue(p.getCategoria());
        txtPrecio.setText(p.getPrecio().toPlainString());
        txtStockMinimo.setText(String.valueOf(p.getStockMinimo()));
        txtNombre.requestFocus();
    }

    @Override
    protected void limpiarFormulario() {
        txtNombre.clear();
        cboCategoria.setValue(null);
        txtPrecio.clear();
        txtStockMinimo.setText("0");
    }

    @Override
    protected Map<String, Control> campos() {
        return Map.of("nombre", txtNombre, "categoria", cboCategoria, "precio", txtPrecio, "stockMinimo", txtStockMinimo);
    }
}
