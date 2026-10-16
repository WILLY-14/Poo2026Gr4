package pe.edu.upeu.sysventas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pe.edu.upeu.sysventas.config.AppContext;

import java.io.IOException;

public class SysVentas extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        AppContext appContext=AppContext.getInstance();
        Screen screen=Screen.getPrimary();
        FXMLLoader fxmlLoader = new FXMLLoader(SysVentas.class.getResource("/view/login.fxml"));
        fxmlLoader.setControllerFactory(appContext::getBean);
        Rectangle2D dimension=screen.getBounds();

        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setTitle("SysVentas Login!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
