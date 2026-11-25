package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Refrescable;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Dialogo;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Formato;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.MarcadorError;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Notificacion;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import pe.edu.upeu.padronmascota.padronmascota.modelo.ServicioVeterinario;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioVeterinarioServicio;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Map;

/** Servicios de la veterinaria (consulta, vacuna, desparasitación...) y su precio. */
public class ServicioVeterinarioController implements Refrescable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<ServicioVeterinario> tblServicios;
    @FXML private Label lblTotal;
    @FXML private Label lblModo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;

    private final IServicioVeterinarioServicio servicioVet;
    private final MarcadorError errores = new MarcadorError();
    private ServicioVeterinario enEdicion;

    public ServicioVeterinarioController(IServicioVeterinarioServicio servicioVet) {
        this.servicioVet = servicioVet;
    }

    @FXML
    private void initialize() {
        txtBuscar.textProperty().addListener((obs, antes, ahora) -> buscar());
        new TablaConAcciones<>(tblServicios)
                .columna(Idioma.t("comun.id"), ServicioVeterinario::getId, 45)
                .columna(Idioma.t("comun.nombre"), ServicioVeterinario::getNombre, 260)
                .columna(Idioma.t("comun.precio"), s -> Formato.dinero(s.getPrecio()), 90)
                .acciones(this::editar, this::eliminar);
        nuevo();
        buscar();
    }

    @FXML
    private void buscar() {
        try {
            List<ServicioVeterinario> lista = servicioVet.buscar(txtBuscar.getText());
            tblServicios.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText(Idioma.t("servicio.total", lista.size()));
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.buscar", e.getMessage()));
        }
    }

    @FXML
    private void nuevo() {
        enEdicion = null;
        lblModo.setText(Idioma.t("servicio.nuevo"));
        txtNombre.clear();
        txtPrecio.clear();
        errores.limpiar();
    }

    @FXML
    private void guardar() {
        errores.limpiar();
        ServicioVeterinario s = new ServicioVeterinario();
        s.setNombre(txtNombre.getText());
        try {
            s.setPrecio(Formato.leerDinero(txtPrecio.getText()));
        } catch (NumberFormatException e) {
            errores.marcar(txtPrecio, Idioma.t("comun.numeroInvalido"));
            errores.enfocarPrimero();
            return;
        }
        try {
            if (enEdicion == null) {
                servicioVet.registrar(s);
                avisar(Idioma.t("servicio.registrado"), Notificacion.Tipo.EXITO);
            } else {
                s.setId(enEdicion.getId());
                servicioVet.modificar(s.getId(), s);
                avisar(Idioma.t("servicio.actualizado"), Notificacion.Tipo.EXITO);
            }
            nuevo();
            buscar();
        } catch (ValidacionException e) {
            Map<String, Control> campos = Map.of("nombre", txtNombre, "precio", txtPrecio);
            e.getErrores().forEach((campo, mensaje) -> {
                if (campos.containsKey(campo)) {
                    errores.marcar(campos.get(campo), mensaje);
                }
            });
            errores.enfocarPrimero();
            avisar(e.getErrores().values().iterator().next(), Notificacion.Tipo.ERROR);
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.guardar", e.getMessage()));
        }
    }

    private void editar(ServicioVeterinario s) {
        errores.limpiar();
        enEdicion = s;
        lblModo.setText(Idioma.t("comun.editando", s.getNombre()));
        txtNombre.setText(s.getNombre());
        txtPrecio.setText(s.getPrecio().toPlainString());
        txtNombre.requestFocus();
    }

    private void eliminar(ServicioVeterinario s) {
        if (!Dialogo.confirmar(Idioma.t("comun.confirmarEliminar", s.getNombre()))) {
            return;
        }
        try {
            servicioVet.eliminar(s.getId());
            avisar(Idioma.t("servicio.eliminado"), Notificacion.Tipo.INFO);
            if (enEdicion != null && enEdicion.getId() == s.getId()) {
                nuevo();
            }
            buscar();
        } catch (IllegalStateException e) {
            Dialogo.error(e.getMessage());
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.eliminar", e.getMessage()));
        }
    }

    private void avisar(String mensaje, Notificacion.Tipo tipo) {
        Notificacion.mostrar(tblServicios.getScene().getWindow(), mensaje, tipo);
    }

    /** Al volver a la pestaña se recargan los datos. */
    @Override
    public void refrescar() {
        buscar();
    }
}
