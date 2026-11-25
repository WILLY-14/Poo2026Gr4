package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Refrescable;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Dialogo;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.MarcadorError;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Notificacion;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Entidad;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Pantalla CRUD genérica: formulario a la izquierda, buscador y tabla a la derecha.
 * TEMPLATE METHOD + HERENCIA: aquí está todo lo que se repite (guardar, editar, eliminar,
 * marcar errores, avisos) y cada hija solo dice SUS campos y SUS columnas.
 *
 * <p>El FXML de la hija debe tener: txtBuscar, tabla, lblTotal y lblModo.
 * Los textos salen de language/ con el prefijo de la hija: veterinario.nuevo, veterinario.total...
 */
public abstract class CrudController<T extends Entidad> implements Refrescable {

    @FXML protected TextField txtBuscar;
    @FXML protected TableView<T> tabla;
    @FXML protected Label lblTotal;
    @FXML protected Label lblModo;

    protected final MarcadorError errores = new MarcadorError();
    protected T enEdicion;   // null = registrando uno nuevo

    // ---------- Lo que cada hija tiene que decir ----------

    protected abstract IServicioCrud<T> servicio();

    /** Prefijo de los textos en language/: "veterinario", "color"... */
    protected abstract String prefijo();

    protected abstract void columnas(TablaConAcciones<T> tabla);

    protected abstract List<T> buscarEnServicio(String texto);

    /** Arma el objeto con lo escrito. Si un campo no se puede leer, lo marca con errores.marcar(...). */
    protected abstract T leerFormulario();

    protected abstract void llenarFormulario(T obj);

    protected abstract void limpiarFormulario();

    /** nombre del atributo del modelo -> campo de la pantalla (para pintar los errores). */
    protected abstract Map<String, Control> campos();

    // ---------- Ganchos opcionales ----------

    /** Llenar combos, listeners... Se llama antes de armar la tabla. */
    protected void configurar() {
    }

    /** Botones de la tabla. Por defecto Editar y Eliminar. */
    protected void acciones(TablaConAcciones<T> t) {
        t.acciones(this::editar, this::eliminar);
    }

    protected String textoTotal(List<T> lista) {
        return Idioma.t(prefijo() + ".total", lista.size());
    }

    // ---------- El flujo común ----------

    @FXML
    protected void initialize() {
        configurar();
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, antes, ahora) -> buscar());
        }
        TablaConAcciones<T> t = new TablaConAcciones<>(tabla);
        columnas(t);
        acciones(t);
        nuevo();
        buscar();
    }

    @FXML
    protected void buscar() {
        try {
            List<T> lista = buscarEnServicio(txtBuscar == null ? null : txtBuscar.getText());
            tabla.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText(textoTotal(lista));
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.buscar", e.getMessage()));
        }
    }

    /** Al volver a la pestaña se recarga la tabla (los conteos pueden haber cambiado en otra pantalla). */
    @Override
    public void refrescar() {
        buscar();
    }

    @FXML
    protected void nuevo() {
        enEdicion = null;
        lblModo.setText(Idioma.t(prefijo() + ".nuevo"));
        errores.limpiar();
        limpiarFormulario();
    }

    @FXML
    protected void guardar() {
        errores.limpiar();
        T obj = leerFormulario();
        if (obj == null || errores.hayErrores()) {
            errores.enfocarPrimero();
            if (errores.primerMensaje() != null) {
                avisar(errores.primerMensaje(), Notificacion.Tipo.ERROR);
            }
            return;
        }
        try {
            if (enEdicion == null) {
                servicio().registrar(obj);
                avisar(Idioma.t(prefijo() + ".registrado"), Notificacion.Tipo.EXITO);
            } else {
                obj.setId(enEdicion.getId());
                servicio().modificar(obj.getId(), obj);
                avisar(Idioma.t(prefijo() + ".actualizado"), Notificacion.Tipo.EXITO);
            }
            nuevo();
            buscar();
        } catch (ValidacionException e) {
            mostrarErrores(e);
        } catch (IllegalStateException e) {
            Dialogo.error(e.getMessage());
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.guardar", mensajeAmigable(e)));
        }
    }

    protected void editar(T obj) {
        errores.limpiar();
        enEdicion = obj;
        lblModo.setText(Idioma.t("comun.editando", obj.toString()));
        llenarFormulario(obj);
    }

    protected void eliminar(T obj) {
        // Si la pantalla tiene su propio texto (ej. propietario.confirmarEliminar) se usa ese
        String clave = prefijo() + ".confirmarEliminar";
        String pregunta = Idioma.t(clave).equals(clave)
                ? Idioma.t("comun.confirmarEliminar", obj.toString())
                : Idioma.t(clave, obj.toString());
        if (!Dialogo.confirmar(pregunta)) {
            return;
        }
        try {
            servicio().eliminar(obj.getId());
            avisar(Idioma.t(prefijo() + ".eliminado"), Notificacion.Tipo.INFO);
            if (enEdicion != null && enEdicion.getId() == obj.getId()) {
                nuevo();
            }
            buscar();
        } catch (IllegalStateException e) {
            Dialogo.error(e.getMessage());
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.eliminar", mensajeAmigable(e)));
        }
    }

    // ---------- Ayudas para las hijas ----------

    /** Pinta en rojo cada campo con error y muestra el primer mensaje. */
    protected void mostrarErrores(ValidacionException e) {
        Map<String, Control> campos = campos();
        e.getErrores().forEach((campo, mensaje) -> {
            if (campos.containsKey(campo)) {
                errores.marcar(campos.get(campo), mensaje);
            }
        });
        errores.enfocarPrimero();
        avisar(e.getErrores().values().iterator().next(), Notificacion.Tipo.ERROR);
    }

    /** Lee un entero; si no es número, marca el campo y devuelve null. Vacío = null (lo valida el modelo). */
    protected Integer leerEntero(TextField campo) {
        String texto = campo.getText();
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException ex) {
            errores.marcar(campo, Idioma.t("comun.enteroInvalido"));
            return null;
        }
    }

    /** Lee dinero ("12.50" o "12,50"); si no es número, marca el campo y devuelve null. */
    protected BigDecimal leerDinero(TextField campo) {
        String texto = campo.getText();
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(texto.trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            errores.marcar(campo, Idioma.t("comun.numeroInvalido"));
            return null;
        }
    }

    protected void avisar(String mensaje, Notificacion.Tipo tipo) {
        Notificacion.mostrar(tabla.getScene().getWindow(), mensaje, tipo);
    }

    /** Traduce los errores de H2 más comunes a algo que el usuario entienda. */
    static String mensajeAmigable(RuntimeException e) {
        String m = String.valueOf(e.getMessage());
        if (m.contains("Unique index") || m.contains("primary key violation")) {
            return Idioma.t("error.duplicado");
        }
        if (m.contains("Referential integrity")) {
            return Idioma.t("error.enUso");
        }
        if (m.contains("Check constraint")) {
            return Idioma.t("error.datoInvalido");
        }
        return m;
    }
}
