package pe.edu.upeu.sysventas.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.sysventas.enums.TipoDocumento;
import pe.edu.upeu.sysventas.model.Cliente;
import pe.edu.upeu.sysventas.service.IClienteService;

/** CRUD de clientes: interfaz -> servicio -> repositorio -> base de datos. */
public class ClienteController {
    private final IClienteService service;
    public ClienteController(IClienteService service) { this.service = service; }
    @FXML private TextField txtDocumento, txtNombres, txtRepresentante, txtDireccion, txtBuscar;
    @FXML private ComboBox<TipoDocumento> cbTipo;
    @FXML private TableView<Cliente> tabla;
    @FXML private TableColumn<Cliente,String> colDocumento, colNombre, colTipo, colDireccion, colRepresentante;
    @FXML private Label lblEstado;
    @FXML private Label lblPagina;
    @FXML private ComboBox<Integer> cbPorPagina;
    private String editando;
    private int pagina = 1, totalPaginas = 1;

    @FXML public void initialize() {
        cbTipo.setItems(FXCollections.observableArrayList(TipoDocumento.values()));
        cbTipo.getSelectionModel().select(TipoDocumento.DNI);
        colDocumento.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDniruc()));
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombres()));
        colTipo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTipoDocumento().name()));
        colDireccion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDireccion()));
        colRepresentante.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRepLegal()));

        // Resaltar en rojo todas las filas correspondientes a clientes con RUC.
        tabla.setRowFactory(tv -> new TableRow<Cliente>() {
            @Override
            protected void updateItem(Cliente cliente, boolean empty) {
                super.updateItem(cliente, empty);
                if (empty || cliente == null) {
                    setStyle("");
                } else if (cliente.getTipoDocumento() == TipoDocumento.RUC) {
                    setStyle("-fx-text-background-color: #b9706b;");
                } else {
                    setStyle("");
                }
            }
        });

        tabla.getSelectionModel().selectedItemProperty().addListener((o, antes, seleccionado) -> {
            if (seleccionado != null) editar(seleccionado);
        });
        cbPorPagina.setItems(FXCollections.observableArrayList(5, 10, 20, 50));
        cbPorPagina.setValue(10);
        cbPorPagina.setOnAction(e -> { pagina = 1; listar(); });
        txtBuscar.textProperty().addListener((o, antes, despues) -> { pagina = 1; listar(); });
        listar();
    }
    @FXML private void guardar() {
        String doc = txtDocumento.getText().trim(), nombre = txtNombres.getText().trim();
        TipoDocumento tipo = cbTipo.getValue();
        if (doc.isEmpty() || nombre.isEmpty() || tipo == null) { mensaje("El documento, el nombre y el tipo son obligatorios", true); return; }
        if (tipo == TipoDocumento.DNI && !doc.matches("\\d{8}")) { mensaje("El DNI debe contener 8 dígitos", true); return; }
        if (tipo == TipoDocumento.RUC && !doc.matches("\\d{11}")) { mensaje("El RUC debe contener 11 dígitos", true); return; }
        if (!doc.matches("[A-Za-z0-9]{5,12}")) { mensaje("El documento debe tener entre 5 y 12 caracteres alfanuméricos", true); return; }
        if (nombre.length() > 60 || txtRepresentante.getText().length() > 60 || txtDireccion.getText().length() > 60) { mensaje("Uno de los campos supera los 60 caracteres", true); return; }
        Cliente cliente = Cliente.builder().dniruc(doc).nombres(nombre).tipoDocumento(tipo)
                .repLegal(txtRepresentante.getText().trim()).direccion(txtDireccion.getText().trim()).build();
        try {
            if (editando == null) service.save(cliente);
            else { if (!editando.equals(doc)) { mensaje("El número de documento no se puede modificar",true); return; } service.update(editando, cliente); }
            limpiar(); listar(); mensaje("Cliente guardado correctamente", false);
        } catch (Exception ex) { mensaje("No se pudo guardar: " + ex.getMessage(), true); }
    }
    private void editar(Cliente c) {
        editando = c.getDniruc(); txtDocumento.setText(c.getDniruc()); txtDocumento.setDisable(true);
        txtNombres.setText(c.getNombres()); txtRepresentante.setText(c.getRepLegal());
        txtDireccion.setText(c.getDireccion()); cbTipo.setValue(c.getTipoDocumento());
    }
    @FXML private void eliminar() {
        Cliente c = tabla.getSelectionModel().getSelectedItem();
        if (c == null) { mensaje("Seleccione un cliente",true); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,"¿Eliminar al cliente " + c.getNombres() + "?",ButtonType.YES,ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
        try { service.delete(c.getDniruc()); limpiar(); listar(); mensaje("Cliente eliminado",false); }
        catch (Exception ex) { mensaje("No se puede eliminar; podría tener ventas asociadas. " + ex.getMessage(),true); }
    }
    @FXML private void limpiar() {
        editando = null; txtDocumento.setDisable(false); txtDocumento.clear(); txtNombres.clear();
        txtRepresentante.clear(); txtDireccion.clear(); cbTipo.setValue(TipoDocumento.DNI); tabla.getSelectionModel().clearSelection();
    }
    @FXML private void listar() {
        try {
            String filtro = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
            java.util.List<Cliente> filtrados = service.findAll().stream().filter(c ->
                 c.getDniruc().toLowerCase().contains(filtro) || c.getNombres().toLowerCase().contains(filtro)
                 || (c.getRepLegal() != null && c.getRepLegal().toLowerCase().contains(filtro))).toList();
            // Paginación: se muestra solo un bloque de registros por página.
            int porPagina = cbPorPagina.getValue() == null ? 10 : cbPorPagina.getValue();
            totalPaginas = Math.max(1, (int) Math.ceil(filtrados.size() / (double) porPagina));
            if (pagina > totalPaginas) pagina = totalPaginas;
            if (pagina < 1) pagina = 1;
            int desde = (pagina - 1) * porPagina, hasta = Math.min(desde + porPagina, filtrados.size());
            tabla.setItems(FXCollections.observableArrayList(filtrados.subList(desde, hasta)));
            lblPagina.setText("Página " + pagina + " de " + totalPaginas + "  (" + filtrados.size() + " registros)");
        } catch (Exception ex) { mensaje("Error al consultar: " + ex.getMessage(),true); }
    }
    @FXML private void primeraPagina() { pagina = 1; listar(); }
    @FXML private void paginaAnterior() { if (pagina > 1) { pagina--; listar(); } }
    @FXML private void paginaSiguiente() { if (pagina < totalPaginas) { pagina++; listar(); } }
    @FXML private void ultimaPagina() { pagina = totalPaginas; listar(); }
    private void mensaje(String t,boolean error) { lblEstado.setText(t); lblEstado.setStyle(error ? "-fx-text-fill:#a25d58" : "-fx-text-fill:#587f6b"); }
}
