package trackbug.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import trackbug.Main;
import trackbug.util.SessionManager;
import trackbug.model.entity.Usuario;
import trackbug.model.service.UsuarioService;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.util.Random;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label mensagemErro;
    @FXML private Button loginButton;
    @FXML private ImageView leftImage;
    @FXML private ImageView rightImage;
    @FXML private StackPane rootPane;
    private final UsuarioService usuarioService;

    public LoginController() {
        this.usuarioService = new UsuarioService();
    }

    @FXML
    public void initialize() {

        createAnimatedParticles();
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
    private void createAnimatedParticles() {
        Group particlesGroup = new Group();
        Random random = new Random();

        // Criar partículas
        for (int i = 0; i < 170; i++) {
            Circle particle = createParticle(random);
            animateParticle(particle, random);
            particlesGroup.getChildren().add(particle);
        }

        // Adicionar ao fundo
        rootPane.getChildren().add(0, particlesGroup);
    }

    private Circle createParticle(Random random) {
        Circle particle = new Circle(3);
        particle.setFill(Color.WHITE);
        particle.setOpacity(0.3);

        // Posição inicial aleatória
        particle.setCenterX(random.nextDouble() * 1000);
        particle.setCenterY(random.nextDouble() * 800);

        // Efeito de brilho
        Glow glow = new Glow(0.5);
        particle.setEffect(glow);

        return particle;
    }

    private void animateParticle(Circle particle, Random random) {
        // Criar animação de movimento
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(particle.centerXProperty(), particle.getCenterX()),
                        new KeyValue(particle.centerYProperty(), particle.getCenterY()),
                        new KeyValue(particle.opacityProperty(), 0.3)
                ),
                new KeyFrame(Duration.seconds(2 + random.nextDouble() * 2),
                        new KeyValue(particle.centerXProperty(), particle.getCenterX() + (-50 + random.nextDouble() * 100)),
                        new KeyValue(particle.centerYProperty(), particle.getCenterY() + (-50 + random.nextDouble() * 100)),
                        new KeyValue(particle.opacityProperty(), 0.1)
                )
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();
    }

    private void mostrarErro(String mensagem) {
        mensagemErro.setText(mensagem);
        mensagemErro.setVisible(true);
        passwordField.clear();
    }
}