package trackbug;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginForm extends VBox {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;

    public LoginForm() {
        setSpacing(15);
        setPadding(new Insets(30));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: linear-gradient(to bottom right, #1a237e, #0d47a1); " +
                "-fx-background-radius: 10px;");

        // Título estilizado
        Label title = new Label("Bem-vindo ao TrackBug");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Faça login para continuar");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setTextFill(Color.web("#90caf9"));

        // Campo de nome de usuário
        usernameField = new TextField();
        usernameField.setPromptText("Usuário");
        usernameField.setStyle("-fx-font-size: 14px; -fx-background-radius: 5px; " +
                "-fx-padding: 10px; -fx-border-color: #90caf9; -fx-border-radius: 5px;");

        // Campo de senha
        passwordField = new PasswordField();
        passwordField.setPromptText("Senha");
        passwordField.setStyle("-fx-font-size: 14px; -fx-background-radius: 5px; " +
                "-fx-padding: 10px; -fx-border-color: #90caf9; -fx-border-radius: 5px;");

        // Botão de login com efeito de hover
        loginButton = new Button("Entrar");
        loginButton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        loginButton.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; " +
                "-fx-padding: 10px 20px; -fx-background-radius: 5px;");
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #283593; -fx-text-fill: white; " +
                "-fx-padding: 10px 20px; -fx-background-radius: 5px;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; " +
                "-fx-padding: 10px 20px; -fx-background-radius: 5px;"));
        loginButton.setOnAction(e -> realizarLogin());

        // Adiciona os componentes ao layout
        getChildren().addAll(title, subtitle, usernameField, passwordField, loginButton);
    }

    private void realizarLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (verificarCredenciais(username, password)) {
            Stage loginStage = (Stage) this.getScene().getWindow();
            loginStage.close();

            Main mainApp = new Main();
            Stage mainStage = new Stage();
            mainApp.carregarTelaPrincipal(mainStage);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Login");
            alert.setHeaderText("Credenciais Inválidas");
            alert.setContentText("Verifique seu nome de usuário e senha.");
            alert.showAndWait();
        }
    }

    private boolean verificarCredenciais(String username, String password) {
        // Verifique as credenciais usando o banco de dados
        return username.equals("admin") && password.equals("1234");
    }

    public static void exibir(Stage stage) {
        LoginForm loginForm = new LoginForm();
        Scene scene = new Scene(loginForm, 1000, 900);
        stage.setScene(scene);
        stage.setTitle("Login - TrackBug");
        stage.show();
    }
}
