module pe.edu.upeu.sisventas {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires static lombok;
    requires jakarta.validation;
    requires jdk.jshell;

    opens pe.edu.upeu.sisventas to javafx.fxml;
    opens pe.edu.upeu.sisventas.controller to javafx.fxml;
    exports pe.edu.upeu.sisventas;
}