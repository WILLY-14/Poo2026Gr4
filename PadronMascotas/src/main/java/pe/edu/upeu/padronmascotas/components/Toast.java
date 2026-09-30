package pe.edu.upeu.padronmascotas.components;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Toast {

    public static void showToast(Stage ownerStage, String message, int durationInMillis, double x, double y) {

        Label label = new Label(message);
        label.setStyle("-fx-background-color: #00FF99; -fx-text-fill: black; "
                + "-fx-padding: 10px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        label.setOpacity(0);


        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);


        StackPane pane = new StackPane(label);
        pane.setPadding(new Insets(10));
        pane.setAlignment(Pos.CENTER);
        popup.getContent().add(pane);


        popup.show(ownerStage, x, y);


        Timeline fadeIn = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(label.opacityProperty(), 1)));
        Timeline fadeOut = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(label.opacityProperty(), 0)));


        Timeline delay = new Timeline(new KeyFrame(Duration.millis(durationInMillis)));
        delay.setOnFinished(event -> fadeOut.play());


        fadeIn.play();
        fadeIn.setOnFinished(event -> delay.play());
        fadeOut.setOnFinished(event -> popup.hide());
    }
}

