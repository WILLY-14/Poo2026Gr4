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
        Screen screen=Screen.getPrimary();
        Rectangle2D rectangle2D=screen.getVisualBounds();
        AppContext appContext=AppContext.getInstance();
        FXMLLoader fxmlLoader = new FXMLLoader(SysVentas.class.getResource("/view/login.fxml"));
        fxmlLoader.setControllerFactory(appContext::getBean);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(SysVentas.class.getResource("/css/style.css").toExternalForm());
        stage.setTitle("Hello!");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
