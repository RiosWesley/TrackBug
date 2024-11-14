package trackbug.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import trackbug.model.NivelAcesso;
import trackbug.util.SessionManager;
import trackbug.util.AlertHelper;

import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    @FXML private VBox areaPrincipal;
    @FXML private Label labelAdministracao;
    @FXML private Button btnGerenciarUsuarios;
    @FXML private ScrollPane menuScrollPane;
    @FXML private HBox menuContainer;
    @FXML private Label titleLabel;
    @FXML private Label logoLabel;

    private static final double EXPANDED_WIDTH = 250;
    private static final double COLLAPSED_WIDTH = 50; // Aumentei um pouco para melhor visibilidade
    private double currentWidth = EXPANDED_WIDTH;


    @FXML
    private void initialize() {
        verificarPermissoes();

        // Configuração inicial do menu
        menuContainer.setPrefWidth(EXPANDED_WIDTH);
        menuContainer.setMinWidth(COLLAPSED_WIDTH);

        menuScrollPane.setOnMouseEntered(e -> {
            if (currentWidth <= COLLAPSED_WIDTH) {
                expandMenu();
            }
        });

        menuScrollPane.setOnMouseExited(e -> collapseMenu());

        mostrarDashboard();
    }

    private void collapseMenu() {
        Timeline timeline = new Timeline();

        // Animar a largura do container
        KeyValue kvContainer = new KeyValue(menuContainer.prefWidthProperty(), COLLAPSED_WIDTH);

        // Animar a translação do ScrollPane
        KeyValue kvTranslate = new KeyValue(menuScrollPane.translateXProperty(), 0);

        KeyFrame kf = new KeyFrame(Duration.millis(300), kvContainer, kvTranslate);
        timeline.getKeyFrames().add(kf);

        timeline.setOnFinished(e -> {
            currentWidth = COLLAPSED_WIDTH;
            menuContainer.getStyleClass().add("menu-collapsed");
        });

        timeline.play();
    }

    private void expandMenu() {
        Timeline timeline = new Timeline();

        // Animar a largura do container
        KeyValue kvContainer = new KeyValue(menuContainer.prefWidthProperty(), EXPANDED_WIDTH);

        // Animar a translação do ScrollPane
        KeyValue kvTranslate = new KeyValue(menuScrollPane.translateXProperty(), 0);

        KeyFrame kf = new KeyFrame(Duration.millis(300), kvContainer, kvTranslate);
        timeline.getKeyFrames().add(kf);

        timeline.setOnFinished(e -> {
            currentWidth = EXPANDED_WIDTH;
            menuContainer.getStyleClass().remove("menu-collapsed");
        });

        timeline.play();
    }


    private void verificarPermissoes() {
        if (labelAdministracao != null && btnGerenciarUsuarios != null) {
            boolean isAdmin = SessionManager.getUsuarioLogado().getNivelAcesso() == NivelAcesso.ADMIN.getNivel();
            labelAdministracao.setVisible(isAdmin);
            btnGerenciarUsuarios.setVisible(isAdmin);
        }
    }

    private void carregarFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            areaPrincipal.getChildren().clear();
            areaPrincipal.getChildren().add(loader.load());
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar tela",
                    "Não foi possível carregar a tela solicitada: " + e.getMessage());
        }
    }
    @FXML
    private void mostrarDashboard() {
        carregarFXML("/fxml/dashboard.fxml");
    }
    @FXML
    private void mostrarEmprestimos() {
        carregarFXML("/fxml/emprestimo.fxml");
    }

    @FXML
    private void mostrarDevolucao() {
        carregarFXML("/fxml/devolucao.fxml");
    }

    @FXML
    private void mostrarEmprestimosAtivos() {
        carregarFXML("/fxml/emprestimos-ativos.fxml");
    }

    @FXML
    private void mostrarEmprestimosAtraso() {
        carregarFXML("/fxml/emprestimos-atraso.fxml");
    }

    @FXML
    private void mostrarRegistroEquipamento() {
        carregarFXML("/fxml/registrar-equipamento.fxml");
    }

    @FXML
    private void mostrarListaEquipamentos() {
        carregarFXML("/fxml/listar-equipamentos.fxml");
    }

    @FXML
    private void mostrarCadastroFuncionario() {
        carregarFXML("/fxml/cadastrar-funcionario.fxml");
    }

    @FXML
    private void mostrarListaFuncionarios() {
        carregarFXML("/fxml/listar-funcionarios.fxml");
    }

    @FXML
    private void mostrarHistorico() {
        carregarFXML("/fxml/historico-emprestimos.fxml");
    }

    @FXML
    private void mostrarGerenciamentoUsuarios() {
        carregarFXML("/fxml/gerenciar-permissoes.fxml");
    }

    @FXML
    private void mostrarHistoricoAvarias() {
        carregarFXML("/fxml/historico-avarias.fxml");
    }

    @FXML
    private void mostrarHistoricoAlteracoes() {
        carregarFXML("/fxml/historico-alteracoes.fxml");
    }


    @FXML
    private void logout() {
        try {
            SessionManager.limparSessao();
            // Aqui você pode adicionar a lógica para voltar para a tela de login
        } catch (Exception e) {
            AlertHelper.showError("Erro ao fazer logout", e.getMessage());
        }
    }
}