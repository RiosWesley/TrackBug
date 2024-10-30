package trackbug.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import trackbug.Main;
import trackbug.SessionManager;
import trackbug.model.*;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label mensagemErro;

    @FXML
    private Button loginButton;

    @FXML
    private void realizarLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarErro("Por favor, preencha todos os campos");
            return;
        }

        if (UsuarioDAO.autenticar(username, password)) {
            Usuario usuario = UsuarioDAO.buscarPorUsername(username);
            if (usuario != null) {
                SessionManager.setUsuarioLogado(usuario);
                Stage loginStage = (Stage) loginButton.getScene().getWindow();
                loginStage.close();
                Main.carregarTelaPrincipal(new Stage());
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
    }
}

