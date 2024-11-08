package trackbug.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import trackbug.model.NivelAcesso;
import trackbug.Forms.SessionManager;
import trackbug.util.AlertHelper;

public class MainController {

    @FXML private VBox areaPrincipal;
    @FXML private Label labelAdministracao;
    @FXML private Button btnGerenciarUsuarios;

    @FXML
    private void initialize() {
        verificarPermissoes();
        mostrarTelaBoasVindas();
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

    private void mostrarTelaBoasVindas() {
        VBox welcomeBox = new VBox(15);
        welcomeBox.setAlignment(Pos.CENTER);

        Label bemVindo = new Label("Bem-vindo ao Sistema de Gerenciamento de Equipamentos");
        bemVindo.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #00B393;" +
                        "-fx-font-family: 'Segoe UI';"
        );

        Label instrucoes = new Label("Selecione uma opção no menu lateral para começar");
        instrucoes.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #757575;" +
                        "-fx-font-family: 'Segoe UI';"
        );

        Label usuario = new Label("Usuário: " +
                SessionManager.getUsuarioLogado().getNome());
        usuario.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #424242;" +
                        "-fx-font-family: 'Segoe UI';"
        );

        welcomeBox.getChildren().addAll(bemVindo, instrucoes, usuario);
        areaPrincipal.getChildren().setAll(welcomeBox);
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