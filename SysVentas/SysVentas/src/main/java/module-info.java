module pe.edu.upeu.sysventas {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires static lombok;
    requires jakarta.validation;

    requires java.sql;
    requires org.slf4j;
    requires java.naming;
    requires com.zaxxer.hikari;
    requires java.prefs;

    opens pe.edu.upeu.sysventas to javafx.fxml;
    opens pe.edu.upeu.sysventas.controller to javafx.fxml;

    exports pe.edu.upeu.sysventas.model;
    opens pe.edu.upeu.sysventas.model;
    exports pe.edu.upeu.sysventas;
}