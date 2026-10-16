package pe.edu.upeu.padronmascotas.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import pe.edu.upeu.padronmascotas.config.AppContext;

import java.io.IOException;
import java.util.Map;

public class MainGuiController {

    @FXML
    TabPane tabPane;
    @FXML
    MenuItem menuMascota, menuSalir;

    @FXML
    public void initialize() {
        MenuItemListener miL = new MenuItemListener();
        menuMascota.setOnAction(miL::handle);
        menuSalir.setOnAction(miL::handle);

        miL.abrirTabPaneFXML("/view/main_mascota.fxml", "Padrón de Mascotas");
    }

    class MenuItemListener {

        Map<String, String[]> menuConfig = Map.of(
                "menuMascota", new String[]{"/view/main_mascota.fxml", "Padrón de Mascotas", "T"},
                "menuSalir", new String[]{"", "Salir", "C"}
        );

        public void handle(ActionEvent e) {
            String id = ((MenuItem) e.getSource()).getId();
            if (menuConfig.containsKey(id)) {
                String[] items = menuConfig.get(id);
                if (items[2].equals("C")) {
                    Platform.exit();
                    System.exit(0);
                } else {
                    abrirTabPaneFXML(items[0], items[1]);
                }
            }
        }

        private void abrirTabPaneFXML(String fxmlPath, String titulo) {
            try {
                AppContext context = AppContext.getInstance();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
                fxmlLoader.setControllerFactory(context::getBean);
                Parent root = fxmlLoader.load();

                ScrollPane scrollPane = new ScrollPane(root);
                scrollPane.setFitToWidth(true);
                scrollPane.setFitToHeight(true);
                Tab newTab = new Tab(titulo, scrollPane);
                tabPane.getTabs().clear();
                tabPane.getTabs().add(newTab);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}