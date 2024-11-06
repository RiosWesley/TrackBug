module trackbug {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires javafx.graphics;
    requires javafx.base;

    opens trackbug to javafx.fxml;
    opens trackbug.controller to javafx.fxml;
    exports trackbug;
    exports trackbug.controller;
    exports trackbug.model;
    opens trackbug.model to javafx.fxml;
    exports trackbug.Forms;
    opens trackbug.Forms to javafx.fxml;
}