package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.CampoBuscable;
import pe.edu.upeu.padronmascota.padronmascota.enums.CategoriaProducto;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Refrescable;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Combos;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Dialogo;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Formato;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.MarcadorError;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Notificacion;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Compra;
import pe.edu.upeu.padronmascota.padronmascota.modelo.DetalleCompra;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Producto;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Proveedor;
import pe.edu.upeu.padronmascota.padronmascota.servicio.ICompraServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IProductoServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IProveedorServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.impl.Validador;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** Compras a proveedores (solo productos). Al guardar, el stock sube solo. */
public class CompraController implements Refrescable {

    @FXML private ComboBox<Proveedor> cboProveedor;   // lista de los proveedores registrados
    @FXML private TextField txtFactura;
    @FXML private Label lblCategoriaNueva;
    @FXML private ComboBox<CategoriaProducto> cboCategoriaNueva;
    @FXML private Label lblPrecioNuevo;
    @FXML private TextField txtPrecioNuevo;
    @FXML private TextField txtProducto;
    private CampoBuscable<Producto> cboProducto;   // se escribe, con sugerencias
    @FXML private TextField txtCantidad;
    @FXML private TextField txtCosto;
    @FXML private TextField txtLote;
    @FXML private DatePicker dtpVencimiento;
    @FXML private TableView<DetalleCompra> tblLineas;
    @FXML private Label lblTotalDocumento;
    @FXML private TextField txtBuscar;
    @FXML private TableView<Compra> tabla;
    @FXML private Label lblTotal;

    private final ICompraServicio compraServicio;
    private final IProveedorServicio proveedorServicio;
    private final IProductoServicio productoServicio;
    private final MarcadorError errores = new MarcadorError();
    private Compra compra;

    public CompraController(ICompraServicio compraServicio, IProveedorServicio proveedorServicio,
                            IProductoServicio productoServicio) {
        this.compraServicio = compraServicio;
        this.proveedorServicio = proveedorServicio;
        this.productoServicio = productoServicio;
    }

    @FXML
    private void initialize() {
        prepararProveedores();
        cboProducto = new CampoBuscable<>(txtProducto, productoServicio::listarTodos, Producto::getNombre);
        cboCategoriaNueva.getItems().setAll(CategoriaProducto.values());
        // Si lo escrito no existe, aparecen los campos para registrarlo
        txtProducto.textProperty().addListener((o, a, b) -> mostrar(esNuevo(txtProducto, cboProducto),
                lblCategoriaNueva, cboCategoriaNueva, lblPrecioNuevo, txtPrecioNuevo));
        // Al elegir el producto, sugiere su último costo
        cboProducto.valueProperty().addListener((obs, antes, p) -> {
            if (p != null && p.getUltimoCosto() != null) {
                txtCosto.setText(p.getUltimoCosto().toPlainString());
            }
        });
        txtBuscar.textProperty().addListener((obs, antes, ahora) -> buscar());

        new TablaConAcciones<>(tblLineas)
                .columna(Idioma.t("compra.producto"), d -> d.getProducto().getNombre(), 130)
                .columna(Idioma.t("venta.cantidad"), DetalleCompra::getCantidad, 45)
                .columna(Idioma.t("compra.costo"), d -> Formato.dinero(d.getCostoUnitario()), 65)
                .columna(Idioma.t("venta.subtotal"), d -> Formato.dinero(d.getSubtotal()), 70)
                .columna(Idioma.t("compra.lote"), d -> d.getLote() == null ? "-" : d.getLote(), 70)
                .acciones(Idioma.t("venta.quitar"), this::quitar, null, null);

        new TablaConAcciones<>(tabla)
                .columna(Idioma.t("comun.id"), Compra::getId, 40)
                .columna(Idioma.t("venta.fecha"), c -> Formato.fechaHora(c.getFechaHora()), 140)
                .columna(Idioma.t("compra.proveedor"), c -> c.getProveedor().getNombre(), 210)
                .columna(Idioma.t("compra.factura"), Compra::getNumeroFactura, 100)
                .columna(Idioma.t("venta.registradoPor"), c -> c.getUsuario().getUsername(), 90)
                .columna(Idioma.t("compra.lineas"), Compra::getNumeroLineas, 60)
                .columna(Idioma.t("venta.total"), c -> Formato.dinero(c.getTotal()), 85)
                .acciones(Idioma.t("venta.ver"), this::ver, Idioma.t("comun.eliminar"), this::eliminar);
        nuevo();
        buscar();
    }

    private static boolean esNuevo(TextField campo, CampoBuscable<?> buscable) {
        return campo.getText() != null && !campo.getText().isBlank() && buscable.getValue() == null;
    }

    private static void mostrar(boolean ver, javafx.scene.Node... nodos) {
        for (javafx.scene.Node n : nodos) {
            n.setVisible(ver);
            n.setManaged(ver);
        }
    }

    /** El producto escrito; si es nuevo (comida, collar...) lo registra con la categoría y el precio de venta. */
    private Producto productoEscrito() {
        if (cboProducto.getValue() != null || !esNuevo(txtProducto, cboProducto)) {
            return cboProducto.getValue();
        }
        Producto p = new Producto();
        p.setNombre(txtProducto.getText().trim());
        p.setCategoria(cboCategoriaNueva.getValue());
        p.setStockMinimo(0);
        try {
            p.setPrecio(Formato.leerDinero(txtPrecioNuevo.getText()));
        } catch (NumberFormatException e) {
            errores.marcar(txtPrecioNuevo, Idioma.t("comun.numeroInvalido"));
            return null;
        }
        // Sin precio de venta el producto se cobraría S/ 0.00 en Caja
        if (p.getPrecio() == null || p.getPrecio().signum() <= 0) {
            errores.marcar(txtPrecioNuevo, Idioma.t("compra.faltaPrecioVenta"));
            return null;
        }
        if (p.getCategoria() == null) {
            errores.marcar(cboCategoriaNueva, Idioma.t("compra.faltaCategoria"));
            return null;
        }
        try {
            Producto nuevo = productoServicio.registrar(p);
            cboProducto.setValue(nuevo);
            return nuevo;
        } catch (ValidacionException e) {
            e.getErrores().forEach((campo, msg) -> errores.marcar(
                    "categoria".equals(campo) ? cboCategoriaNueva : "precio".equals(campo) ? txtPrecioNuevo : txtProducto, msg));
        } catch (RuntimeException e) {
            errores.marcar(txtProducto, CrudController.mensajeAmigable(e));
        }
        return null;
    }

    // ------------------------------------------------------------------
    //  Proveedor: se elige de la lista (los registrados en Proveedores)
    // ------------------------------------------------------------------

    private void prepararProveedores() {
        // Cómo se ve cada proveedor en la lista: "Nombre · RUC 20123456789"
        cboProveedor.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Proveedor p) {
                return p == null ? "" : p.getNombre() + "  ·  RUC " + p.getRuc();
            }

            @Override
            public Proveedor fromString(String texto) {
                return null;
            }
        });
        cboProveedor.setPlaceholder(new Label(Idioma.t("compra.sinProveedores")));
        cboProveedor.setVisibleRowCount(10);
        cargarProveedores();
    }

    /** Vuelve a leer los proveedores de la base (por si registraron uno nuevo) sin perder el elegido. */
    private void cargarProveedores() {
        Proveedor elegido = cboProveedor.getValue();
        List<Proveedor> lista = proveedorServicio.listarTodos().stream()
                .sorted(java.util.Comparator.comparing(Proveedor::getNombre, String.CASE_INSENSITIVE_ORDER))
                .toList();
        cboProveedor.getItems().setAll(lista);
        if (elegido != null) {
            cboProveedor.setValue(lista.stream().filter(p -> p.getId() == elegido.getId()).findFirst().orElse(null));
        }
    }

    @FXML
    private void nuevo() {
        compra = new Compra();
        cboProveedor.setValue(null);
        txtFactura.clear();
        limpiarLinea();
        errores.limpiar();
        refrescarLineas();
    }

    private void limpiarLinea() {
        cboProducto.setValue(null);
        cboCategoriaNueva.setValue(null);
        txtPrecioNuevo.clear();
        txtCantidad.setText("1");
        txtCosto.clear();
        txtLote.clear();
        dtpVencimiento.setValue(null);
    }

    @FXML
    private void agregar() {
        errores.limpiar();
        Integer cantidad = null;
        BigDecimal costo = null;
        try {
            cantidad = Formato.leerEntero(txtCantidad.getText());
        } catch (NumberFormatException e) {
            errores.marcar(txtCantidad, Idioma.t("comun.enteroInvalido"));
        }
        try {
            costo = Formato.leerDinero(txtCosto.getText());
        } catch (NumberFormatException e) {
            errores.marcar(txtCosto, Idioma.t("comun.numeroInvalido"));
        }
        Producto producto = errores.hayErrores() ? null : productoEscrito();
        if (errores.hayErrores()) {
            errores.enfocarPrimero();
            return;
        }
        DetalleCompra d = new DetalleCompra(producto, cantidad == null ? 0 : cantidad, costo,
                txtLote.getText(), dtpVencimiento.getValue());
        try {
            Validador.validar(d);
        } catch (ValidacionException e) {
            Map<String, Control> campos = Map.of("producto", cboProducto.getCampo(), "cantidad", txtCantidad,
                    "costoUnitario", txtCosto, "lote", txtLote);
            e.getErrores().forEach((campo, mensaje) -> {
                if (campos.containsKey(campo)) {
                    errores.marcar(campos.get(campo), mensaje);
                }
            });
            errores.enfocarPrimero();
            return;
        }
        compra.getDetalles().add(d);
        limpiarLinea();
        refrescarLineas();
    }

    private void quitar(DetalleCompra d) {
        compra.getDetalles().remove(d);
        refrescarLineas();
    }

    private void refrescarLineas() {
        tblLineas.setItems(FXCollections.observableArrayList(compra.getDetalles()));
        lblTotalDocumento.setText(Idioma.t("venta.totalPagar", Formato.dinero(compra.getTotal())));
    }

    @FXML
    private void guardar() {
        errores.limpiar();
        if (cboProveedor.getValue() == null) {
            errores.marcar(cboProveedor, Idioma.t("compra.eligeProveedor"));
        }
        compra.setProveedor(cboProveedor.getValue());
        if (errores.hayErrores()) {
            errores.enfocarPrimero();
            return;
        }
        compra.setNumeroFactura(txtFactura.getText());
        try {
            Compra c = compraServicio.registrar(compra);
            avisar(Idioma.t("compra.registrada", c.getNumeroFactura(), Formato.dinero(c.getTotal())), Notificacion.Tipo.EXITO);
            nuevo();
            buscar();
        } catch (ValidacionException e) {
            Map<String, Control> campos = Map.of("proveedor", cboProveedor, "numeroFactura", txtFactura, "detalles", cboProducto.getCampo());
            e.getErrores().forEach((campo, mensaje) -> {
                if (campos.containsKey(campo)) {
                    errores.marcar(campos.get(campo), mensaje);
                }
            });
            errores.enfocarPrimero();
            avisar(e.getErrores().values().iterator().next(), Notificacion.Tipo.ERROR);
        } catch (IllegalStateException e) {
            Dialogo.error(e.getMessage());
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.guardar", CrudController.mensajeAmigable(e)));
        }
    }

    @FXML
    private void buscar() {
        try {
            List<Compra> lista = compraServicio.buscar(txtBuscar.getText());
            tabla.setItems(FXCollections.observableArrayList(lista));
            BigDecimal total = lista.stream().map(Compra::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            lblTotal.setText(Idioma.t("compra.total", lista.size(), Formato.dinero(total)));
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.buscar", e.getMessage()));
        }
    }

    private void ver(Compra c) {
        StringBuilder sb = new StringBuilder();
        sb.append(Idioma.t("compra.factura")).append(' ').append(c.getNumeroFactura())
                .append("   ").append(Formato.fechaHora(c.getFechaHora())).append('\n')
                .append(Idioma.t("compra.proveedor")).append(": ").append(c.getProveedor().getNombre())
                .append(" (RUC ").append(c.getProveedor().getRuc()).append(")\n\n");
        for (DetalleCompra d : compraServicio.detalles(c.getId())) {
            sb.append(String.format("%4d x %-30s %10s  %-10s %s%n", d.getCantidad(), d.getProducto().getNombre(),
                    Formato.dinero(d.getSubtotal()), d.getLote() == null ? "" : d.getLote(),
                    d.getFechaVencimiento() == null ? "" : Formato.fecha(d.getFechaVencimiento()) + " " + d.getEstadoLote()));
        }
        sb.append('\n').append(Idioma.t("venta.totalPagar", Formato.dinero(c.getTotal())));
        Dialogo.detalle(c.getNumeroFactura(), sb.toString());
    }

    private void eliminar(Compra c) {
        if (!Dialogo.confirmar(Idioma.t("compra.confirmarEliminar", c.getNumeroFactura()))) {
            return;
        }
        try {
            compraServicio.eliminar(c.getId());
            avisar(Idioma.t("compra.eliminada"), Notificacion.Tipo.INFO);
            buscar();
        } catch (IllegalStateException e) {
            Dialogo.error(e.getMessage());
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.eliminar", CrudController.mensajeAmigable(e)));
        }
    }

    private void avisar(String mensaje, Notificacion.Tipo tipo) {
        Notificacion.mostrar(tabla.getScene().getWindow(), mensaje, tipo);
    }

    /** Al volver a la pestaña se recargan los datos. */
    @Override
    public void refrescar() {
        cargarProveedores();   // aparecen los proveedores recién registrados
        buscar();
    }
}
