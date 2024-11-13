package trackbug;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega o ícone
        Image icon = new Image(getClass().getResourceAsStream("/images/icon.png"));

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(root, 1200, 600);

        // Carrega os estilosA
        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        primaryStage.setTitle("Login - TrackBug");
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Abre maximizado
        primaryStage.show();
    }

    public static void carregarTelaPrincipal(Stage stage) {
        try {
            Image icon = new Image(Main.class.getResourceAsStream("/images/icon.png"));
            Parent root = FXMLLoader.load(Main.class.getResource("/fxml/main.fxml"));
            Scene scene = new Scene(root, 1200, 800);

            // Carrega os estilos
            scene.getStylesheets().add(Main.class.getResource("/styles/styles.css").toExternalForm());

            stage.setTitle("TrackBug - Sistema de Gerenciamento");
            stage.getIcons().add(icon);
            stage.setScene(scene);
            stage.setMinWidth(1000);
            stage.setMinHeight(600);
            stage.setMaximized(true); // Abre maximizado
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}