package trackbug.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
    private static final double EXPANDED_WIDTH = 250;
    private static final double COLLAPSED_WIDTH = 30;
    private double currentMenuWidth = EXPANDED_WIDTH;
    private List<Node> menuItems;

    @FXML
    private void initialize() {
        verificarPermissoes();
        addMenuSlideInAnimation();
        menuScrollPane.setOnMouseEntered(event -> expandMenu());
        menuScrollPane.setOnMouseExited(event -> collapseMenu());
        addMenuSlideInAnimation();
        menuItems = menuScrollPane.getChildrenUnmodifiable().stream()
                .filter(node -> node instanceof Button)
                .collect(Collectors.toList());
        mostrarDashboard();
    }
    private void addMenuSlideInAnimation() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), menuScrollPane);
        tt.setFromX(-200);
        tt.setToX(0);
        tt.play();
    }
    private void expandMenu() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), menuScrollPane);
        tt.setToX(0);
        tt.play();

        currentMenuWidth = EXPANDED_WIDTH;
        menuScrollPane.setPrefWidth(currentMenuWidth);

        // Remover a classe de menu retraído
        menuItems.forEach(item -> {
            item.getStyleClass().remove("menu-item-collapsed");
            item.getStyleClass().add("menu-item");
        });
    }

    private void collapseMenu() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), menuScrollPane);
        tt.setToX(-COLLAPSED_WIDTH + 50);
        tt.play();

        currentMenuWidth = COLLAPSED_WIDTH;
        menuScrollPane.setPrefWidth(currentMenuWidth);

        // Adicionar a classe de menu retraído
        menuItems.forEach(item -> {
            item.getStyleClass().add("menu-item-collapsed");
            item.getStyleClass().remove("menu-item");
        });
    }
    private void verificarPermissoes() {
        boolean isAdmin = SessionManager.getUsuarioLogado().getNivelAcesso() ==
                NivelAcesso.ADMIN.getNivel();
        labelAdministracao.setVisible(isAdmin);
        btnGerenciarUsuarios.setVisible(isAdmin);
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