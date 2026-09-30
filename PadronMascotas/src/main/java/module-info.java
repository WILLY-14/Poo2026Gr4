module pe.edu.upeu.padronmascotas {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires static lombok;
    requires jakarta.validation;

    opens pe.edu.upeu.padronmascotas to javafx.fxml;
    opens pe.edu.upeu.padronmascotas.controller to javafx.fxml;
    opens pe.edu.upeu.padronmascotas.model;
    exports pe.edu.upeu.padronmascotas;
}
