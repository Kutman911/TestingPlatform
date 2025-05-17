module com.project.testing_platform {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.desktop;

    opens com.project.app to javafx.fxml;
    exports com.project.app;
    exports com.project.controller;
    opens com.project.controller to javafx.fxml;

    opens com.project.controller.student to javafx.fxml, javafx.base;
    opens com.project.controller.teacher to javafx.fxml;
    opens com.project.controller.admin to javafx.fxml;
    opens com.project.controller.manager to javafx.fxml;
    opens com.project.controller.common to javafx.fxml;


    opens com.project.view to javafx.fxml;
    opens com.project.view.admin   to javafx.fxml;
    opens com.project.view.teacher to javafx.fxml;
    opens com.project.view.manager to javafx.fxml;
    opens com.project.view.student to javafx.fxml;
    opens com.project.view.common to javafx.fxml;

    opens com.project.model to javafx.base;


}