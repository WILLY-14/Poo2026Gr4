package pe.edu.upeu.algotirmogui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Label mensaje = new Label("Bienvenido a JavaFX");
        mensaje.setStyle("-fx-font-size: 18px;");
        TextField txtNombre=new TextField();
        Button boton = new Button("Saludar");
        //Deducir IGV
        Label msgMonto=new Label("Monto:");
        TextField txtMonto=new TextField();
        Button btnCalcular=new Button("Deducir IGV");
        Label resultado=new Label();
        btnCalcular.setOnAction(event -> {
            double resul=Double.parseDouble(txtMonto.getText())-Double.parseDouble(txtMonto.getText())/1.18;
            resultado.setText("pv:"+(Double.parseDouble(txtMonto.getText())/1.18)+ "  igv:"+resul);
        });
        HBox hb=new HBox(10, msgMonto, txtMonto, btnCalcular, resultado);
        //Fin D IGV
        boton.setOnAction(evento ->
                mensaje.setText("¡Hola "+txtNombre.getText()));
        VBox raiz = new VBox(15, mensaje, txtNombre, boton, hb);
        raiz.setAlignment(Pos.CENTER);
        raiz.setStyle("-fx-padding: 30;");
        Scene escena = new Scene(raiz, 400, 250);
        stage.setTitle("Hola JavaFX");
        stage.setScene(escena);
        stage.show();
    }

}
