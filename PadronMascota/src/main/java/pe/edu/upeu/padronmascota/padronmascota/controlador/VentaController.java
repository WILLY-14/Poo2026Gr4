package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.CampoBuscable;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioVeterinarioServicio;
import pe.edu.upeu.padronmascota.padronmascota.modelo.ServicioVeterinario;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Refrescable;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Dialogo;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Formato;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Impresion;
import pe.edu.upeu.padronmascota.padronmascota.componentes.MarcadorError;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Notificacion;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.dto.OpcionCombo;
import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoVenta;
import pe.edu.upeu.padronmascota.padronmascota.enums.MetodoPago;
import pe.edu.upeu.padronmascota.padronmascota.enums.TipoComprobante;
import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Atencion;
import pe.edu.upeu.padronmascota.padronmascota.modelo.DetalleVenta;
import pe.edu.upeu.padronmascota.padronmascota.modelo.ItemVenta;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Producto;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Venta;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IAtencionServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IPropietarioServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IVentaServicio;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Venta de accesorios, productos y servicios.
 * Izquierda: la venta que se está armando (carrito). Derecha: el historial con Ver y Anular.
 */
public class VentaController implements Refrescable {

    // ----- Nueva venta -----
    @FXML private TextField txtCliente;
    private CampoBuscable<Propietario> cboCliente;   // se escribe, con sugerencias
    @FXML private ComboBox<TipoComprobante> cboComprobante;
    @FXML private ComboBox<MetodoPago> cboMetodoPago;
    @FXML private Label lblRuc;
    @FXML private TextField txtRuc;
    @FXML private Label lblPendientes;   // "Se cargó lo pendiente: 1 consulta, 1 vacuna"
    @FXML private TextField txtItem;
    private CampoBuscable<ItemVenta> cboItem;   // se escribe, con sugerencias
    @FXML private TextField txtCantidad;
    @FXML private TextField txtPrecio;
    @FXML private Label lblDniCliente;
    @FXML private TextField txtDniCliente;
    @FXML private Label lblTelCliente;
    @FXML private TextField txtTelCliente;
    @FXML private TableView<DetalleVenta> tblDetalle;
    @FXML private Label lblTotalVenta;

    // ----- Historial -----
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<OpcionCombo<EstadoVenta>> cboFiltroEstado;
    @FXML private TableView<Venta> tblVentas;
    @FXML private Label lblTotal;

    private final IVentaServicio ventaServicio;
    private final IPropietarioServicio propietarioServicio;
    private final IAtencionServicio atencionServicio;
    private final IServicioVeterinarioServicio servicioVetServicio;
    private final MarcadorError errores = new MarcadorError();
    private Venta venta;   // la venta que se está armando

    public VentaController(IVentaServicio ventaServicio, IPropietarioServicio propietarioServicio,
                           IAtencionServicio atencionServicio, IServicioVeterinarioServicio servicioVetServicio) {
        this.servicioVetServicio = servicioVetServicio;
        this.ventaServicio = ventaServicio;
        this.propietarioServicio = propietarioServicio;
        this.atencionServicio = atencionServicio;
    }

    @FXML
    private void initialize() {
        cboComprobante.setItems(FXCollections.observableArrayList(TipoComprobante.values()));
        cboMetodoPago.setItems(FXCollections.observableArrayList(MetodoPago.values()));
        cboCliente = new CampoBuscable<>(txtCliente, propietarioServicio::listarTodos,
                p -> p.getDni() == null ? p.getNombre() : p.getNombre() + " - " + p.getDni());
        cboItem = new CampoBuscable<>(txtItem, ventaServicio::itemsVendibles, this::textoItem);
        // Al reconocer un producto o servicio se pone su precio, pero se puede cambiar
        cboItem.valueProperty().addListener((o, antes, item) -> {
            if (item != null && item.getPrecio() != null) {
                txtPrecio.setText(item.getPrecio().toPlainString());
            }
        });
        txtCliente.textProperty().addListener((o, a, b) -> {
            boolean nuevo = txtCliente.getText() != null && !txtCliente.getText().isBlank() && cboCliente.getValue() == null;
            for (javafx.scene.Node n : List.of(lblDniCliente, txtDniCliente, lblTelCliente, txtTelCliente)) {
                n.setVisible(nuevo);
                n.setManaged(nuevo);
            }
        });
        // Al abrir los combos se recargan (por si registraste algo en otra pestaña)
        // Venta.id_atencion es opcional: solo se ofrecen las atenciones de las mascotas del cliente
        // Al reconocer al cliente se carga SOLO lo que debe: consultas y vacunas sin cobrar
        cboCliente.valueProperty().addListener((obs, antes, ahora) -> cargarPendientes(ahora));
        // Factura pide RUC
        cboComprobante.valueProperty().addListener((o, a, tipo) -> {
            boolean factura = tipo == TipoComprobante.FACTURA;
            lblRuc.setVisible(factura);
            lblRuc.setManaged(factura);
            txtRuc.setVisible(factura);
            txtRuc.setManaged(factura);
        });

        new TablaConAcciones<>(tblDetalle)
                .columna(Idioma.t("venta.item"), DetalleVenta::getDescripcion, 170)
                .columna(Idioma.t("venta.cantidad"), DetalleVenta::getCantidad, 45)
                .columna(Idioma.t("comun.precio"), d -> Formato.dinero(d.getPrecio()), 70)
                .columna(Idioma.t("venta.subtotal"), d -> Formato.dinero(d.getSubtotal()), 75)
                .acciones(Idioma.t("venta.quitar"), this::quitar, null, null);

        cboFiltroEstado.getItems().add(new OpcionCombo<>(null, Idioma.t("comun.todos")));
        for (EstadoVenta e : EstadoVenta.values()) {
            cboFiltroEstado.getItems().add(new OpcionCombo<>(e, e.getNombre()));
        }
        cboFiltroEstado.getSelectionModel().selectFirst();
        txtBuscar.textProperty().addListener((obs, antes, ahora) -> buscar());

        new TablaConAcciones<>(tblVentas)
                .columna(Idioma.t("venta.numero"), Venta::getNumeroComprobante, 100)
                .columna(Idioma.t("venta.fecha"), v -> Formato.fechaHora(v.getFechaHora()), 140)
                .columna(Idioma.t("venta.cliente"), v -> v.getPropietario().getNombre(), 150)
                .columna(Idioma.t("venta.mascotaAtencion"), v -> v.getAtencion() == null ? "-" : v.getAtencion().getMascota().getNombre(), 85)
                .columna(Idioma.t("venta.metodo"), Venta::getMetodoPago, 95)
                .columna(Idioma.t("venta.registradoPor"), v -> v.getUsuario().getUsername(), 85)
                .columna(Idioma.t("venta.estado"), Venta::getEstado, 70)
                .columna(Idioma.t("venta.total"), v -> Formato.dinero(v.getTotal()), 80)
                .acciones(Idioma.t("venta.comprobanteBtn"), this::ver, Idioma.t("venta.anular"), this::anular);
        nuevaVenta();
        buscar();
    }

    // ===================== Nueva venta =====================

    @FXML
    private void nuevaVenta() {
        venta = new Venta();
        cboCliente.setValue(null);
        cboComprobante.setValue(TipoComprobante.BOLETA);
        cboMetodoPago.setValue(MetodoPago.EFECTIVO);
        cboItem.setValue(null);
        txtCantidad.setText("1");
        txtPrecio.clear();
        txtDniCliente.clear();
        txtTelCliente.clear();
        txtRuc.clear();
        lblPendientes.setText(Idioma.t("venta.escribeCliente"));
        errores.limpiar();
        refrescarCarrito();
    }

    @FXML
    private void agregar() {
        errores.limpiar();
        String texto = txtItem.getText() == null ? "" : txtItem.getText().trim();
        if (texto.isEmpty()) {
            errores.marcar(cboItem.getCampo(), Idioma.t("venta.elegirItem"));
            avisar(errores.primerMensaje(), Notificacion.Tipo.ERROR);
            return;
        }
        Integer cantidad;
        try {
            cantidad = Formato.leerEntero(txtCantidad.getText());
        } catch (NumberFormatException e) {
            cantidad = null;
        }
        if (cantidad == null || cantidad < 1) {
            errores.marcar(txtCantidad, Idioma.t("venta.cantidadInvalida"));
            return;
        }
        // El precio lo escribe quien cobra (la consulta, el baño... a criterio del doctor)
        java.math.BigDecimal precio;
        try {
            precio = Formato.leerDinero(txtPrecio.getText());
        } catch (NumberFormatException e) {
            precio = null;
        }
        if (precio == null || precio.signum() < 0) {
            errores.marcar(txtPrecio, Idioma.t("venta.precioInvalido"));
            avisar(errores.primerMensaje(), Notificacion.Tipo.ERROR);
            return;
        }
        ItemVenta item = cboItem.getValue();
        if (item == null) {
            // Algo que no está registrado (ej. "Baño", "Consulta"): se registra como servicio, sin stock
            item = servicioNuevo(texto, precio);
            if (item == null) {
                return;
            }
        }
        // Aviso rápido de stock (la revisión de verdad la hace la base de datos al guardar)
        if (item instanceof Producto p) {
            int yaEnCarrito = venta.getDetalles().stream()
                    .filter(d -> d.getItem().esMismo(p)).mapToInt(DetalleVenta::getCantidad).sum();
            if (yaEnCarrito + cantidad > p.getStock()) {
                errores.marcar(txtCantidad, Idioma.t("venta.sinStock", p.getStock()));
                avisar(errores.primerMensaje(), Notificacion.Tipo.ERROR);
                return;
            }
        }
        venta.agregar(item, cantidad, precio);
        cboItem.setValue(null);
        txtCantidad.setText("1");
        txtPrecio.clear();
        refrescarCarrito();
        txtItem.requestFocus();
    }

    /** Registra un servicio nuevo con lo escrito (queda en Servicios para la próxima vez). */
    private ItemVenta servicioNuevo(String nombre, java.math.BigDecimal precio) {
        // Si ya se cobró antes con ese nombre (ej. "Baño"), se usa el mismo, con el precio de hoy
        for (ServicioVeterinario ya : servicioVetServicio.listarTodos()) {
            if (ya.getNombre().equalsIgnoreCase(nombre)) {
                return ya;
            }
        }
        ServicioVeterinario s = new ServicioVeterinario();
        s.setNombre(nombre);
        s.setPrecio(precio);
        try {
            return servicioVetServicio.registrar(s);
        } catch (ValidacionException e) {
            errores.marcar(cboItem.getCampo(), e.getErrores().values().iterator().next());
        } catch (RuntimeException e) {
            errores.marcar(cboItem.getCampo(), CrudController.mensajeAmigable(e));
        }
        avisar(errores.primerMensaje(), Notificacion.Tipo.ERROR);
        return null;
    }

    /** El cliente escrito; si es nuevo se registra con el DNI y teléfono escritos. */
    private Propietario clienteEscrito() {
        String t = txtCliente.getText() == null ? "" : txtCliente.getText().trim();
        if (cboCliente.getValue() != null || t.isEmpty()) {
            return cboCliente.getValue();
        }
        Propietario p = new Propietario();
        p.setNombre(t);
        p.setDni(txtDniCliente.getText() == null ? "" : txtDniCliente.getText().trim());
        p.setTelefono(txtTelCliente.getText() == null ? "" : txtTelCliente.getText().trim());
        try {
            Propietario nuevo = propietarioServicio.registrar(p);
            cboCliente.setValue(nuevo);
            return nuevo;
        } catch (ValidacionException e) {
            e.getErrores().forEach((campo, msg) -> errores.marcar(
                    "dni".equals(campo) ? txtDniCliente : "telefono".equals(campo) ? txtTelCliente : txtCliente, msg));
        } catch (RuntimeException e) {
            errores.marcar(txtCliente, CrudController.mensajeAmigable(e));
        }
        return null;
    }

    private void quitar(DetalleVenta d) {
        venta.quitar(d);
        refrescarCarrito();
    }

    @FXML
    private void cobrar() {
        errores.limpiar();
        venta.setPropietario(clienteEscrito());
        if (errores.hayErrores()) {
            errores.enfocarPrimero();
            avisar(errores.primerMensaje(), Notificacion.Tipo.ERROR);
            return;
        }
        venta.setTipoComprobante(cboComprobante.getValue());
        venta.setMetodoPago(cboMetodoPago.getValue());
        venta.setRucCliente(txtRuc.getText());
        // La venta queda unida a la (primera) consulta que cobra
        venta.setAtencion(venta.getDetalles().stream().filter(d -> d.getIdAtencion() != null).findFirst().map(d -> {
            Atencion a = new Atencion();
            a.setId(d.getIdAtencion());
            return a;
        }).orElse(null));
        try {
            Venta guardada = ventaServicio.registrar(venta);
            avisar(Idioma.t("venta.registrada", guardada.getNumeroComprobante(), Formato.dinero(guardada.getTotal())),
                    Notificacion.Tipo.EXITO);
            List<DetalleVenta> lineas = List.copyOf(guardada.getDetalles());
            nuevaVenta();
            buscar();
            // Sale el comprobante para imprimir y entregar al cliente
            Impresion.vistaPrevia(tblVentas.getScene().getWindow(), guardada.getNumeroComprobante(),
                    hojaComprobante(guardada, lineas));
        } catch (ValidacionException e) {
            Map<String, Control> campos = Map.of("propietario", cboCliente.getCampo(), "tipoComprobante", cboComprobante,
                    "metodoPago", cboMetodoPago, "detalles", cboItem.getCampo(), "rucCliente", txtRuc);
            e.getErrores().forEach((campo, mensaje) -> {
                if (campos.containsKey(campo)) {
                    errores.marcar(campos.get(campo), mensaje);
                }
            });
            errores.enfocarPrimero();
            avisar(e.getErrores().values().iterator().next(), Notificacion.Tipo.ERROR);
        } catch (IllegalStateException e) {
            Dialogo.error(e.getMessage());   // por ejemplo: no hay stock suficiente
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.guardar", e.getMessage()));
        }
    }

    private void refrescarCarrito() {
        tblDetalle.setItems(FXCollections.observableArrayList(venta.getDetalles()));
        lblTotalVenta.setText(Idioma.t("venta.totalPagar", Formato.dinero(venta.getTotal())));
    }


    /** Pone en la venta las consultas y vacunas que el cliente aún no pagó. */
    private void cargarPendientes(Propietario cliente) {
        List<DetalleVenta> pendientes = cliente == null || cliente.getId() == 0 ? List.of()
                : ventaServicio.pendientes(cliente.getId());
        venta.ponerPendientes(pendientes);
        long consultas = pendientes.stream().filter(d -> d.getIdAtencion() != null).count();
        long vacunas = pendientes.size() - consultas;
        if (cliente == null) {
            lblPendientes.setText(Idioma.t("venta.escribeCliente"));
        } else if (pendientes.isEmpty()) {
            lblPendientes.setText(Idioma.t("venta.sinPendientes"));
        } else {
            lblPendientes.setText(Idioma.t("venta.pendientesCargados", consultas, vacunas));
        }
        refrescarCarrito();
    }


    /** "[Producto] Collar mediano · S/ 18.00 · stock 8"  /  "[Servicio] Consulta general · S/ 30.00" */
    /** "Collar rojo · S/ 25.00 · stock 4" (empieza con el nombre para que al escribirlo lo reconozca). */
    private String textoItem(ItemVenta i) {
        String texto = i.getNombre() + " · " + Formato.dinero(i.getPrecio());
        if (i instanceof Producto p) {
            texto += " · " + Idioma.t("venta.stockCorto", p.getStock());
        } else {
            texto += " · " + Idioma.t("venta.tipoServicio");
        }
        return texto;
    }

    // ===================== Historial =====================

    @FXML
    private void buscar() {
        try {
            List<Venta> lista = ventaServicio.buscar(txtBuscar.getText(), cboFiltroEstado.getValue().getValor());
            tblVentas.setItems(FXCollections.observableArrayList(lista));
            BigDecimal pagado = lista.stream()
                    .filter(v -> v.getEstado() == EstadoVenta.PAGADA)
                    .map(Venta::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            lblTotal.setText(Idioma.t("venta.resumen", lista.size(), Formato.dinero(pagado)));
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.buscar", e.getMessage()));
        }
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscar.clear();
        cboFiltroEstado.getSelectionModel().selectFirst();
        buscar();
    }

    /** Muestra el comprobante de una venta del historial (para volver a imprimirlo). */
    private void ver(Venta v) {
        Impresion.vistaPrevia(tblVentas.getScene().getWindow(), v.getNumeroComprobante(),
                hojaComprobante(v, ventaServicio.detalles(v.getId())));
    }

    /** La boleta o factura: veterinaria, número, cliente, detalle, total (y IGV si es factura). */
    private javafx.scene.layout.VBox hojaComprobante(Venta v, List<DetalleVenta> lineas) {
        boolean factura = v.getTipoComprobante() == TipoComprobante.FACTURA;
        javafx.scene.layout.VBox hoja = Impresion.hoja(
                Idioma.t(factura ? "comprobante.factura" : "comprobante.boleta"), v.getNumeroComprobante());
        hoja.getChildren().addAll(
                Impresion.seccion(Idioma.t("comprobante.cliente")),
                Impresion.datos(
                        Idioma.t("venta.cliente"), v.getPropietario().getNombre(),
                        Idioma.t("venta.fecha"), Formato.fechaHora(v.getFechaHora()),
                        factura ? Idioma.t("venta.ruc") : Idioma.t("propietario.dni"),
                        factura ? v.getRucCliente() : v.getPropietario().getDni(),
                        Idioma.t("venta.metodoPago"), String.valueOf(v.getMetodoPago())),
                Impresion.seccion(Idioma.t("comprobante.detalle")));
        List<List<String>> filas = new java.util.ArrayList<>();
        for (DetalleVenta d : lineas) {
            filas.add(List.of(d.getDescripcion(), String.valueOf(d.getCantidad()),
                    Formato.dinero(d.getPrecio()), Formato.dinero(d.getSubtotal())));
        }
        hoja.getChildren().add(Impresion.tabla(
                List.of(Idioma.t("comprobante.concepto"), Idioma.t("venta.cantidad"), Idioma.t("comun.precio"),
                        Idioma.t("venta.subtotal")),
                new double[]{290, 50, 80, 76}, filas, -1));
        BigDecimal total = v.getTotal();
        List<String> resumen = new java.util.ArrayList<>();
        if (factura) {   // los precios ya incluyen IGV (18 %)
            BigDecimal base = total.divide(new BigDecimal("1.18"), 2, java.math.RoundingMode.HALF_UP);
            resumen.add(Idioma.t("comprobante.gravada", Formato.dinero(base)));
            resumen.add(Idioma.t("comprobante.igv", Formato.dinero(total.subtract(base))));
        }
        javafx.scene.layout.VBox totales = new javafx.scene.layout.VBox(3);
        totales.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        for (String r : resumen) {
            totales.getChildren().add(Impresion.texto(r, 11, false, "#444444"));
        }
        totales.getChildren().add(Impresion.texto(Idioma.t("venta.totalPagar", Formato.dinero(total)), 15, true, "#1f6f5c"));
        hoja.getChildren().addAll(totales, Impresion.texto(Idioma.t("comprobante.gracias"), 10, false, "#666666"));
        return hoja;
    }

    private void anular(Venta v) {
        if (!Dialogo.confirmar(Idioma.t("venta.confirmarAnular", v.getNumeroComprobante()))) {
            return;
        }
        try {
            ventaServicio.anular(v.getId());
            avisar(Idioma.t("venta.anulada"), Notificacion.Tipo.INFO);
            buscar();
        } catch (IllegalStateException e) {
            Dialogo.error(e.getMessage());
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.guardar", e.getMessage()));
        }
    }

    private void avisar(String mensaje, Notificacion.Tipo tipo) {
        Notificacion.mostrar(tblVentas.getScene().getWindow(), mensaje, tipo);
    }

    /** Al volver a la pestaña se recargan los datos. */
    @Override
    public void refrescar() {
        buscar();
    }
}
