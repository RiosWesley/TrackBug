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


        configurarValidacoes();
    }

    private void configurarValidacoes() {
        usernameField.textProperty().addListener((obs, old, novo) -> mensagemErro.setVisible(false));
        passwordField.textProperty().addListener((obs, old, novo) -> mensagemErro.setVisible(false));
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
            if (usuarioService.autenticar(username, password)) {
                Usuario usuario = usuarioService.buscarPorUsername(username);
                if (usuario != null) {
                    if (!usuario.isAtivo()) {
                        mostrarErro("Usuário está inativo. Contate o administrador.");
                        return;
                    }

                    SessionManager.setUsuarioLogado(usuario);

                    // Ao invés de criar nova Stage, chama o método sem parâmetros
                    Main.carregarTelaPrincipal();

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
        passwordField.clear();
    }
}