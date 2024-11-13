package trackbug.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import trackbug.model.NivelAcesso;
import trackbug.util.SessionManager;
import trackbug.util.AlertHelper;

public class MainController {

    @FXML private VBox areaPrincipal;
    @FXML private Label labelAdministracao;
    @FXML private Button btnGerenciarUsuarios;
    @FXML private ScrollPane menuScrollPane;

    @FXML
    private void initialize() {
        verificarPermissoes();
        addMenuSlideInAnimation();
        mostrarDashboard();
    }
    private void addMenuSlideInAnimation() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), menuScrollPane);
        tt.setFromX(-200);
        tt.setToX(0);
        tt.play();
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