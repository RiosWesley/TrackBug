package trackbug.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertHelper {

    public static void showError(String title, String content) {
        showAlert(AlertType.ERROR, title, content);
    }

    public static void showWarning(String title, String content) {
        showAlert(AlertType.WARNING, title, content);
    }

    public static void showSuccess(String content) {
        showAlert(AlertType.INFORMATION, "Sucesso", content);
    }

    private static void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(type == AlertType.INFORMATION ? "Sucesso" : title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}