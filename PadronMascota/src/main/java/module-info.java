module pe.edu.upeu.padronmascota.padronmascota {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.zaxxer.hikari;
    requires com.h2database;
    requires jakarta.validation;
    requires org.hibernate.validator;
    requires com.fasterxml.classmate; // la usa Hibernate Validator por dentro
    requires org.jboss.logging;       // la usa Hibernate Validator por dentro
    requires jbcrypt;                 // hash BCrypt de las claves

    // JavaFX necesita "abrir" los controladores para leer los @FXML
    opens pe.edu.upeu.padronmascota.padronmascota.controlador to javafx.fxml;
    // Hibernate Validator lee las anotaciones del modelo
    opens pe.edu.upeu.padronmascota.padronmascota.modelo;

    exports pe.edu.upeu.padronmascota.padronmascota;
}
