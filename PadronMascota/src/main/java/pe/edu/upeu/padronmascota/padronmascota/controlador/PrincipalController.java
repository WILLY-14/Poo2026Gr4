package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Dialogo;
import pe.edu.upeu.padronmascota.padronmascota.componentes.GestorVentana;
import pe.edu.upeu.padronmascota.padronmascota.config.DatabaseConfig;
import pe.edu.upeu.padronmascota.padronmascota.enums.Rol;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Tema;
import pe.edu.upeu.padronmascota.padronmascota.dto.OpcionMenu;
import pe.edu.upeu.padronmascota.padronmascota.seguridad.Sesion;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IMenuServicio;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ventana principal: el MenuBar se arma con las opciones del IMenuServicio
 * y cada pantalla se abre en una pestaña. También tiene los menús Tema, Idioma y Sistema.
 */
public class PrincipalController {

    @FXML private MenuBar menuBar;
    @FXML private TabPane tabPane;
    @FXML private Label lblEstado;

    private final IMenuServicio menuServicio;

    public PrincipalController(IMenuServicio menuServicio) {
        this.menuServicio = menuServicio;
    }

    @FXML
    private void initialize() {
        armarMenus();
        abrirPestana(Idioma.t("opcion.resumen"), "resumen.fxml"); // Inicio: lo primero que se ve
        String quien = Sesion.activa() ? Sesion.usuario().getNombre() : "-";
        lblEstado.setText(Idioma.t("estado.barra", quien));
    }

    private void armarMenus() {
        menuBar.getMenus().clear();

        // Agrupamos las opciones por menú, respetando el orden. Las claves se traducen aquí.
        Map<String, Menu> menus = new LinkedHashMap<>();
        for (OpcionMenu op : menuServicio.opciones()) {
            Menu menu = menus.computeIfAbsent(op.getMenu(), clave -> new Menu(Idioma.t(clave)));
            String texto = Idioma.t(op.getTexto());
            MenuItem item = new MenuItem(texto);
            item.setOnAction(e -> abrirPestana(texto, op.getVista()));
            menu.getItems().add(item);
        }
        menuBar.getMenus().addAll(menus.values());
        menuBar.getMenus().add(menuSistema()); // Tema e Idioma van dentro de Sistema
    }

    private Menu menuTemas() {
        Menu menu = new Menu(Idioma.t("menu.tema"));
        ToggleGroup grupo = new ToggleGroup();
        for (Tema tema : Tema.values()) {
            RadioMenuItem item = new RadioMenuItem(tema.nombre());
            item.setToggleGroup(grupo);
            item.setSelected(tema == Tema.actual());
            item.setOnAction(e -> tema.aplicar());
            menu.getItems().add(item);
        }
        return menu;
    }

    private Menu menuIdiomas() {
        Menu menu = new Menu(Idioma.t("menu.idioma"));
        ToggleGroup grupo = new ToggleGroup();
        for (String[] idioma : Idioma.DISPONIBLES) {
            RadioMenuItem item = new RadioMenuItem(idioma[1]);
            item.setToggleGroup(grupo);
            item.setSelected(idioma[0].equals(Idioma.actual()));
            item.setOnAction(e -> {
                Idioma.cambiar(idioma[0]);
                GestorVentana.mostrar("principal.fxml"); // se vuelve a cargar con los textos nuevos
            });
            menu.getItems().add(item);
        }
        return menu;
    }

    private Menu menuSistema() {
        Menu menu = new Menu(Idioma.t("menu.sistema"));
        MenuItem cerrarSesion = new MenuItem(Idioma.t("sistema.cerrarSesion"));
        cerrarSesion.setOnAction(e -> {
            Sesion.cerrar();
            LoginController.mostrarLogin();
        });
        MenuItem salir = new MenuItem(Idioma.t("sistema.salir"));
        salir.setOnAction(e -> Platform.exit());
        menu.getItems().addAll(menuTemas(), menuIdiomas(), new SeparatorMenuItem(), cerrarSesion);
        // Solo el Administrador puede vaciar la base
        if (Sesion.activa() && Sesion.usuario().getRol() == Rol.ADMIN) {
            MenuItem vaciar = new MenuItem(Idioma.t("sistema.vaciarBase"));
            vaciar.setOnAction(e -> vaciarBase());
            menu.getItems().addAll(new SeparatorMenuItem(), vaciar);
        }
        menu.getItems().addAll(new SeparatorMenuItem(), salir);
        return menu;
    }

    /** Sistema > Empezar de cero: borra todos los registros (pide confirmar dos veces). */
    private void vaciarBase() {
        if (!Dialogo.confirmar(Idioma.t("sistema.vaciarPregunta1"))
                || !Dialogo.confirmar(Idioma.t("sistema.vaciarPregunta2"))) {
            return;
        }
        try {
            DatabaseConfig.borrarTodo(Sesion.usuario().getId());
            Dialogo.info(Idioma.t("sistema.vaciarBase"), Idioma.t("sistema.vaciarListo", Sesion.usuario().getUsername()));
            Sesion.cerrar();
            LoginController.mostrarLogin();
        } catch (RuntimeException e) {
            Dialogo.error(e.getMessage());
        }
    }

    /** Si la pestaña ya está abierta la selecciona; si no, la crea. */
    private void abrirPestana(String titulo, String vista) {
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(titulo)) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }
        javafx.scene.Parent contenido = GestorVentana.cargar(vista);
        Tab tab = new Tab(titulo, contenido);
        // Al volver a esta pestaña se recargan sus datos (si la pantalla es Refrescable)
        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected() && contenido.getProperties().get("controlador")
                    instanceof pe.edu.upeu.padronmascota.padronmascota.componentes.Refrescable r) {
                r.refrescar();
            }
        });
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }
}
