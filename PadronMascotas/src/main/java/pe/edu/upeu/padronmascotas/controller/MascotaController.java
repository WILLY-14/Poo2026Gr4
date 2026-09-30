package pe.edu.upeu.padronmascotas.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.padronmascotas.components.ColumnInfo;
import pe.edu.upeu.padronmascotas.components.TableViewHelper;
import pe.edu.upeu.padronmascotas.components.Toast;
import pe.edu.upeu.padronmascotas.components.ToltipCustom;
import pe.edu.upeu.padronmascotas.dto.ComboBoxOption;
import pe.edu.upeu.padronmascotas.enums.Especie;
import pe.edu.upeu.padronmascotas.enums.EstadoVacunacion;
import pe.edu.upeu.padronmascotas.model.Mascota;
import pe.edu.upeu.padronmascotas.service.IMascotaService;

import java.util.*;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class MascotaController {
    private final IMascotaService ms;


    @FXML TextField txtNombre, txtRaza, txtColor, txtPropietario, txtTelefono;
    @FXML Spinner<Integer> spEdad;
    @FXML ComboBox<ComboBoxOption> cbxEspecie;
    @FXML RadioButton rbAlDia, rbPendiente;


    @FXML TextField txtFiltroDato;
    @FXML ComboBox<String> cbxFiltroEspecie, cbxFiltroVacunacion;

    @FXML private TableView<Mascota> tableView;
    @FXML Label lbnMsg, lblTotal;
    @FXML private AnchorPane miContenedor;

    private Validator validator;
    private final ToggleGroup tgVacunacion = new ToggleGroup();

    private final ObservableList<Mascota> listarMascota = FXCollections.observableArrayList();
    private final FilteredList<Mascota> filtrados = new FilteredList<>(listarMascota, m -> true);
    private final SortedList<Mascota> ordenados = new SortedList<>(filtrados);
    Mascota formulario;
    Long idMascotaCE = 0L;   // 0 = mascota nueva; mayor a 0 = se está editando

    private final ToltipCustom ttc = new ToltipCustom();

    @FXML
    public void initialize() {
        cbxEspecie.getItems().addAll(ms.listarEspecies());
        rbAlDia.setToggleGroup(tgVacunacion);
        rbPendiente.setToggleGroup(tgVacunacion);


        spEdad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 40, 1));
        spEdad.setEditable(true);

        spEdad.focusedProperty().addListener((obs, antes, tieneFoco) -> { if (!tieneFoco) spEdad.increment(0); });


        txtTelefono.setTextFormatter(new TextFormatter<String>(cambio ->
                cambio.getControlNewText().matches("\\d{0,9}") ? cambio : null));


        cbxFiltroEspecie.getItems().add("Todas");
        for (Especie e : Especie.values()) cbxFiltroEspecie.getItems().add(e.getDescripcion());
        cbxFiltroEspecie.getSelectionModel().selectFirst();
        cbxFiltroVacunacion.getItems().add("Todos");
        for (EstadoVacunacion ev : EstadoVacunacion.values()) cbxFiltroVacunacion.getItems().add(ev.getDescripcion());
        cbxFiltroVacunacion.getSelectionModel().selectFirst();

        cbxFiltroEspecie.setOnAction(e -> aplicarFiltros());
        cbxFiltroVacunacion.setOnAction(e -> aplicarFiltros());
        txtFiltroDato.textProperty().addListener((obs, o, n) -> aplicarFiltros());

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        TableViewHelper<Mascota> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("idMascota", 50.0));
        columns.put("Nombre", new ColumnInfo("nombre", 110.0));
        columns.put("Especie", new ColumnInfo("especie", 80.0));
        columns.put("Raza", new ColumnInfo("raza", 120.0));
        columns.put("Color", new ColumnInfo("color", 100.0));
        columns.put("Edad (años)", new ColumnInfo("edadAprox", 80.0));
        columns.put("Propietario", new ColumnInfo("nombrePropietario", 170.0));
        columns.put("Teléfono", new ColumnInfo("telefonoPropietario", 100.0));
        columns.put("Vacunación", new ColumnInfo("estadoVacunacion", 90.0));

        Consumer<Mascota> updateAction = this::editForm;
        Consumer<Mascota> deleteAction = mascota -> {

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Está seguro de eliminar a la mascota \"" + mascota.getNombre() + "\"?",
                    ButtonType.YES, ButtonType.NO);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText(null);
            confirmacion.showAndWait().ifPresent(respuesta -> {
                if (respuesta == ButtonType.YES) {
                    ms.delete(mascota.getIdMascota());
                    mostrarToast("Se eliminó correctamente!!");
                    clearForm();
                    listar();
                }
            });
        };
        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, updateAction, deleteAction);
        tableView.setTableMenuButtonVisible(true);

        ordenados.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(ordenados);

        lblTotal.textProperty().bind(Bindings.size(filtrados).asString("Mostrando %d mascota(s)"));
        lbnMsg.setText("");
        listar();
    }

    public void listar() {
        try {
            listarMascota.setAll(ms.findAll());
            aplicarFiltros();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void aplicarFiltros() {
        String especie = cbxFiltroEspecie.getValue();
        String vacuna = cbxFiltroVacunacion.getValue();
        String texto = txtFiltroDato.getText() == null ? "" : txtFiltroDato.getText().toLowerCase().trim();

        filtrados.setPredicate(m ->
                (especie == null || especie.equals("Todas") || m.getEspecie().getDescripcion().equals(especie))
                && (vacuna == null || vacuna.equals("Todos") || m.getEstadoVacunacion().getDescripcion().equals(vacuna))
                && (texto.isEmpty()
                    || m.getNombre().toLowerCase().contains(texto)
                    || m.getNombrePropietario().toLowerCase().contains(texto)));
    }

    @FXML
    public void validarFormulario() {
        limpiarError();
        ComboBoxOption esp = cbxEspecie.getSelectionModel().getSelectedItem();
        EstadoVacunacion vacuna = rbAlDia.isSelected() ? EstadoVacunacion.AL_DIA
                : rbPendiente.isSelected() ? EstadoVacunacion.PENDIENTE : null;

        formulario = Mascota.builder()
                .nombre(txtNombre.getText().trim())
                .especie(esp == null ? null : Especie.valueOf(esp.getKey()))
                .raza(txtRaza.getText().trim())
                .color(txtColor.getText().trim())
                .edadAprox(spEdad.getValue())
                .nombrePropietario(txtPropietario.getText().trim())
                .telefonoPropietario(txtTelefono.getText().trim())
                .estadoVacunacion(vacuna)
                .build();

        Set<ConstraintViolation<Mascota>> errores = validator.validate(formulario);
        if (errores.isEmpty()) {
            procesarFormulario();
        } else {
            mostrarErroresValidacion(errores);
        }
    }

    private void procesarFormulario() {
        if (idMascotaCE > 0L) {
            formulario.setIdMascota(idMascotaCE);
            ms.update(idMascotaCE, formulario);
            mostrarToast("Se actualizó correctamente!!");
        } else {
            ms.save(formulario);
            mostrarToast("Se guardó correctamente!!");
        }
        clearForm();
        listar();
    }

    public void editForm(Mascota m) {
        txtNombre.setText(m.getNombre());
        cbxEspecie.getSelectionModel().select(
                cbxEspecie.getItems().stream()
                        .filter(o -> o.getKey().equals(m.getEspecie().name()))
                        .findFirst().orElse(null));
        txtRaza.setText(m.getRaza());
        txtColor.setText(m.getColor());
        spEdad.getValueFactory().setValue(m.getEdadAprox());
        txtPropietario.setText(m.getNombrePropietario());
        txtTelefono.setText(m.getTelefonoPropietario());
        rbAlDia.setSelected(m.getEstadoVacunacion() == EstadoVacunacion.AL_DIA);
        rbPendiente.setSelected(m.getEstadoVacunacion() == EstadoVacunacion.PENDIENTE);
        idMascotaCE = m.getIdMascota();
        limpiarError();
        lbnMsg.setText("Editando mascota N° " + idMascotaCE);
        lbnMsg.setStyle("-fx-text-fill: #1f3397; -fx-font-size: 14px;");
    }

    private void mostrarErroresValidacion(Set<ConstraintViolation<Mascota>> errores) {
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("nombre", txtNombre);
        campos.put("especie", cbxEspecie);
        campos.put("raza", txtRaza);
        campos.put("color", txtColor);
        campos.put("edadAprox", spEdad);
        campos.put("nombrePropietario", txtPropietario);
        campos.put("telefonoPropietario", txtTelefono);
        campos.put("estadoVacunacion", rbAlDia);

        String primerMensaje = null;
        for (String campo : campos.keySet()) {
            for (ConstraintViolation<Mascota> v : errores) {
                if (v.getPropertyPath().toString().equals(campo)) {
                    ttc.marcarError(campos.get(campo), v.getMessage());
                    if (primerMensaje == null) primerMensaje = v.getMessage();
                    break;
                }
            }
        }
        lbnMsg.setText(primerMensaje);
        lbnMsg.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
    }

    @FXML
    public void clearForm() {
        txtNombre.clear(); txtRaza.clear(); txtColor.clear();
        spEdad.getValueFactory().setValue(1);
        txtPropietario.clear(); txtTelefono.clear();
        cbxEspecie.getSelectionModel().clearSelection();
        tgVacunacion.selectToggle(null);
        idMascotaCE = 0L;
        lbnMsg.setText("");
        limpiarError();
    }

    public void limpiarError() {
        List.of(txtNombre, cbxEspecie, txtRaza, txtColor, spEdad, txtPropietario, txtTelefono, rbAlDia)
                .forEach(ttc::limpiarCampo);
    }

    private void mostrarToast(String mensaje) {
        Stage stage = (Stage) miContenedor.getScene().getWindow();
        double w = stage.getWidth() / 1.5, h = stage.getHeight() / 2;
        Toast.showToast(stage, mensaje, 2000, w, h);
    }
}
