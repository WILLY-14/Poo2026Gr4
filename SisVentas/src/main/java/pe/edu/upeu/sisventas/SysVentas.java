package pe.edu.upeu.sisventas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pe.edu.upeu.sisventas.config.AppContext;

import java.io.IOException;

public class SysVentas extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Screen screen=Screen.getPrimary();
        Rectangle2D rectangle2D=screen.getVisualBounds();
        AppContext appContext=AppContext.getInstance();
        FXMLLoader fxmlLoader = new FXMLLoader(SysVentas.class.getResource("/view/main_producto.fxml"));
        fxmlLoader.setControllerFactory(appContext::getBean);
        Scene scene = new Scene(fxmlLoader.load(),rectangle2D.getWidth(), rectangle2D.getHeight()-50);
        scene.getStylesheets().add(SysVentas.class.getResource("/css/style.css").toExternalForm());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}