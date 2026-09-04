module pe.edu.upeu.sisventas {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens pe.edu.upeu.sisventas to javafx.fxml;
    exports pe.edu.upeu.sisventas;
}