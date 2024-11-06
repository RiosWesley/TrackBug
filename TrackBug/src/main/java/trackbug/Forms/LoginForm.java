package trackbug.Forms;

import trackbug.Main;
import trackbug.model.Usuario;
import trackbug.model.UsuarioDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    private Label mensagemErro;

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

        // Mensagem de erro (inicialmente invisível)
        mensagemErro = new Label();
        mensagemErro.setTextFill(Color.web("#ff1744"));
        mensagemErro.setFont(Font.font("Segoe UI", 12));
        mensagemErro.setVisible(false);

        // Campo de nome de usuário
        usernameField = new TextField();
        usernameField.setPromptText("Usuário");
        usernameField.setStyle("-fx-font-size: 14px; -fx-background-radius: 5px; " +
                "-fx-padding: 10px; -fx-border-color: #90caf9; -fx-border-radius: 5px; " +
                "-fx-background-color: white;");
        usernameField.setPrefWidth(300);

        // Campo de senha
        passwordField = new PasswordField();
        passwordField.setPromptText("Senha");
        passwordField.setStyle("-fx-font-size: 14px; -fx-background-radius: 5px; " +
                "-fx-padding: 10px; -fx-border-color: #90caf9; -fx-border-radius: 5px; " +
                "-fx-background-color: white;");
        passwordField.setPrefWidth(300);

        // Botão de login com efeito de hover
        loginButton = new Button("Entrar");
        loginButton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        loginButton.setPrefWidth(300);
        loginButton.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; " +
                "-fx-padding: 10px 20px; -fx-background-radius: 5px;");

        // Efeitos de hover
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(
                "-fx-background-color: #283593; -fx-text-fill: white; " +
                        "-fx-padding: 10px 20px; -fx-background-radius: 5px;"
        ));

        loginButton.setOnMouseExited(e -> loginButton.setStyle(
                "-fx-background-color: #1a237e; -fx-text-fill: white; " +
                        "-fx-padding: 10px 20px; -fx-background-radius: 5px;"
        ));

        // Ação de login
        loginButton.setOnAction(e -> realizarLogin());

        // Permitir login ao pressionar Enter
        passwordField.setOnAction(e -> realizarLogin());

        // Adiciona os componentes ao layout
        getChildren().addAll(
                title,
                subtitle,
                mensagemErro,
                usernameField,
                passwordField,
                loginButton
        );
    }

    private void realizarLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validação básica
        if (username.isEmpty() || password.isEmpty()) {
            mostrarErro("Por favor, preencha todos os campos");
            return;
        }

        // Tenta autenticar
        if (UsuarioDAO.autenticar(username, password)) {
            // Busca dados completos do usuário
            Usuario usuario = UsuarioDAO.buscarPorUsername(username);
            if (usuario != null) {
                // Armazena usuário na sessão
                SessionManager.setUsuarioLogado(usuario);

                // Login bem sucedido
                Stage loginStage = (Stage) this.getScene().getWindow();
                loginStage.close();

                Main mainApp = new Main();
                Stage mainStage = new Stage();
                mainApp.carregarTelaPrincipal(mainStage);
            } else {
                mostrarErro("Erro ao carregar dados do usuário");
            }
        } else {
            mostrarErro("Usuário ou senha inválidos");
            passwordField.clear();
        }
    }

    private void mostrarErro(String mensagem) {
        mensagemErro.setText(mensagem);
        mensagemErro.setVisible(true);

        // Shake animation para feedback visual
        shakeNode(mensagemErro);
    }

    private void shakeNode(Label node) {
        double originalX = node.getTranslateX();
        int duration = 50; // Duração de cada movimento em ms

        Thread shakeThread = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    javafx.application.Platform.runLater(() ->
                            node.setTranslateX(originalX + 5));
                    Thread.sleep(duration);

                    javafx.application.Platform.runLater(() ->
                            node.setTranslateX(originalX - 5));
                    Thread.sleep(duration);
                }
                javafx.application.Platform.runLater(() ->
                        node.setTranslateX(originalX));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        shakeThread.start();
    }
}