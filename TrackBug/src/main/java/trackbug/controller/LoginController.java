    package trackbug.controller;

    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.stage.Stage;
    import trackbug.Main;
    import trackbug.Forms.SessionManager;
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
        private ImageView leftImage;

        @FXML
        private ImageView rightImage;

        @FXML
        public void initialize() {
            try {
                // Carrega a imagem
                Image unifanImage = new Image(getClass().getResourceAsStream("/images/UNIFAN.png"));
                Image estadoImage = new Image(getClass().getResourceAsStream("/images/ESTADO.png"));
                // Define a imagem para ambos os ImageViews
                leftImage.setImage(unifanImage);
                rightImage.setImage(estadoImage);
            } catch (Exception e) {
                System.out.println("Erro ao carregar a imagem: " + e.getMessage());
                e.printStackTrace();
            }
        }

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

