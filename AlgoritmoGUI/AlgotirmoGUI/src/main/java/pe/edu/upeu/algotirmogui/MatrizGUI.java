package pe.edu.upeu.algotirmogui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MatrizGUI extends Application {

     GridPane grid;
     Label informacion;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
            Label titulo = new Label("Matriz -Formas  - David Mamani");
            Label lbnTam = new Label("Tamaño Matriz:");
            Spinner<Integer> spTam=new Spinner<>(2,30,5);

            Label lbnNumI = new Label("Numero de Inicio:");
            Spinner<Integer> spNumI=new Spinner<>(0,30,0);
            Button btnIniciar = new Button("Generar Matriz");
            HBox hb = new HBox(10,lbnTam,spTam,lbnNumI,spNumI, btnIniciar);
            grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            informacion=new Label("Mostrar Informacion");
            btnIniciar.setOnAction(e -> {
                matrizForma21(spTam.getValue(),spNumI.getValue());
            });
            matrizForma21(spTam.getValue(),spNumI.getValue());
            VBox vb = new VBox(10,titulo,hb,grid, informacion);
            ScrollPane scroll = new ScrollPane(vb);
            primaryStage.setScene(new Scene(scroll));
            primaryStage.show();
    }
    public void matrizForma21(int tm, int numI){
        grid.getChildren().clear();
        for(int c=0;c<tm;c++){//2
            if(c%2==0){//Amarillo
                for (int f=0;f<tm;f++){
                    Button b = new Button(String.valueOf(numI));
                    b.setMinSize(48,42);
                    b.setPrefSize(48,42);
                    grid.add(b,c,f);
                    numI++;
                }
            }else{//Verde
                for (int f=tm-1;f>=0;f--){
                    Button b = new Button(String.valueOf(numI));
                    b.setMinSize(48,42);
                    b.setPrefSize(48,42);
                    grid.add(b,c,f);
                    numI++;
                }
            }
        }
    }


}
