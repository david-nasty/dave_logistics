module com.example.nowyprojekt {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires java.persistence;
    requires java.naming;
    requires java.sql;

    opens projekt.Entities to org.hibernate.orm.core, javafx.base;
    opens projekt;

    exports projekt;
}