package trackbug;

import javafx.animation.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    private static Stage primaryStage;
    private static StackPane mainContainer;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        mainContainer = new StackPane();
        Scene scene = new Scene(mainContainer, 1200, 600);

        // Configurações iniciais
        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
        Image icon = new Image(getClass().getResourceAsStream("/images/icon.png"));

        // Configurar stage
        primaryStage.setTitle("TrackBug");
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        // Carregar tela de login
        carregarTelaLogin();

        primaryStage.show();
    }

    private void carregarTelaLogin() throws Exception {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        mainContainer.getChildren().add(loginRoot);

        // Animação de entrada
        fadeInNode(loginRoot);
    }

    public static void carregarTelaPrincipal() {
        try {
            Parent mainRoot = FXMLLoader.load(Main.class.getResource("/fxml/main.fxml"));

            // Configurar fade out da tela atual
            Node currentScreen = mainContainer.getChildren().get(0);
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentScreen);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            // Após fade out, remove tela atual e adiciona nova
            fadeOut.setOnFinished(e -> {
                mainContainer.getChildren().clear();
                mainContainer.getChildren().add(mainRoot);

                // Animação de entrada para nova tela
                fadeInNode(mainRoot);

                // Atualizar título
                primaryStage.setTitle("TrackBug - Sistema de Gerenciamento");
            });

            fadeOut.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fadeInNode(Node node) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(400), node);
        scaleIn.setFromX(0.95);
        scaleIn.setFromY(0.95);
        scaleIn.setToX(1);
        scaleIn.setToY(1);

        ParallelTransition parallelTransition = new ParallelTransition(fadeIn, scaleIn);
        parallelTransition.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}