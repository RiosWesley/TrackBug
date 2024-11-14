package trackbug.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import trackbug.Main;
import trackbug.util.SessionManager;
import trackbug.model.entity.Usuario;
import trackbug.model.service.UsuarioService;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label mensagemErro;
    @FXML private Button loginButton;
    @FXML private ImageView leftImage;
    @FXML private ImageView rightImage;

    private final UsuarioService usuarioService;

    public LoginController() {
        this.usuarioService = new UsuarioService();
    }

    @FXML
    public void initialize() {
        // Configurar validações dos campos
        configurarValidacoes();
    }

    private void configurarValidacoes() {
        // Adicionar listener para limpar mensagem de erro quando o usuário começa a digitar
        usernameField.textProperty().addListener((obs, old, novo) -> mensagemErro.setVisible(false));
        passwordField.textProperty().addListener((obs, old, novo) -> mensagemErro.setVisible(false));

        // Configurar ação de enter nos campos
        passwordField.setOnAction(event -> realizarLogin());
    }

    @FXML
    private void realizarLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarErro("Por favor, preencha todos os campos");
            return;
        }

        try {
            // Tenta autenticar o usuário
            if (usuarioService.autenticar(username, password)) {
                Usuario usuario = usuarioService.buscarPorUsername(username);
                if (usuario != null) {
                    // Verifica se o usuário está ativo
                    if (!usuario.isAtivo()) {
                        mostrarErro("Usuário está inativo. Contate o administrador.");
                        return;
                    }

                    // Login bem sucedido
                    SessionManager.setUsuarioLogado(usuario);

                    // Fecha a tela de login
                    Stage loginStage = (Stage) loginButton.getScene().getWindow();
                    loginStage.close();

                    // Abre a tela principal
                    Main.carregarTelaPrincipal(new Stage());
                } else {
                    mostrarErro("Erro ao carregar dados do usuário");
                }
            } else {
                mostrarErro("Usuário ou senha inválidos");
                passwordField.clear();
            }
        } catch (Exception e) {
            mostrarErro("Erro ao realizar login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarErro(String mensagem) {
        mensagemErro.setText(mensagem);
        mensagemErro.setVisible(true);
        passwordField.clear(); // Limpa a senha por segurança
    }
}