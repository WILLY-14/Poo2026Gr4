package pe.edu.upeu.padronmascota.padronmascota;

import pe.edu.upeu.padronmascota.padronmascota.componentes.GestorVentana;
import pe.edu.upeu.padronmascota.padronmascota.config.DatabaseConfig;
import pe.edu.upeu.padronmascota.padronmascota.controlador.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;

/** La aplicación JavaFX: abre primero el login. */
public class PadronMascotaApp extends Application {

    @Override
    public void start(Stage stage) {
        GestorVentana.setStage(stage);
        // Ícono de la ventana y de la barra de tareas (huellita)
        var icono = PadronMascotaApp.class.getResourceAsStream("/icono.png");
        if (icono != null) {
            stage.getIcons().add(new javafx.scene.image.Image(icono));
        }
        LoginController.mostrarLogin();   // primero el login
        stage.show();
    }

    @Override
    public void stop() {
        DatabaseConfig.shutdown(); // cierra las conexiones al salir
    }
}
