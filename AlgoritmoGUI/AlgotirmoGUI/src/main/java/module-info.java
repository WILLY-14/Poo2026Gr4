module pe.edu.upeu.algotirmogui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens pe.edu.upeu.algotirmogui to javafx.fxml;
    exports pe.edu.upeu.algotirmogui;
}