module trackbug {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.swt;
    requires javafx.web;
    requires jdk.jsobject;
    requires java.desktop;

    opens trackbug to javafx.fxml;
    opens trackbug.controller to javafx.fxml;
    opens trackbug.model to javafx.fxml;
    opens trackbug.model.entity to javafx.fxml;
    opens trackbug.model.dao.interfaces to javafx.fxml;
    opens trackbug.model.dao.impl to javafx.fxml;
    opens trackbug.util to javafx.fxml;

    exports trackbug;
    exports trackbug.controller;
    exports trackbug.model;
    exports trackbug.model.entity;
    exports trackbug.model.dao.interfaces;
    exports trackbug.model.dao.impl;
    exports trackbug.util;
}