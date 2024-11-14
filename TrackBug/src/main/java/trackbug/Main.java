package trackbug;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // Carrega o ícone
        Image icon = new Image(getClass().getResourceAsStream("/images/icon.png"));

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(root, 1200, 600);

        // Carrega os estilos
        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        primaryStage.setTitle("Login - TrackBug");
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Abre maximizado
        primaryStage.show();
    }

    public static void carregarTelaPrincipal(Stage stage) {
        try {
            Parent mainRoot = FXMLLoader.load(Main.class.getResource("/fxml/main.fxml"));
            Scene scene = new Scene(mainRoot, 1200, 800);

            // Carrega os estilos
            scene.getStylesheets().add(Main.class.getResource("/styles/styles.css").toExternalForm());

            // Configurações da janela
            stage.setTitle("TrackBug - Sistema de Gerenciamento");
            stage.setScene(scene);
            stage.setMinWidth(1000);
            stage.setMinHeight(600);
            stage.setMaximized(true); // Abre maximizado

            // Transição suave usando FadeTransition
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(800), mainRoot);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.play();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
