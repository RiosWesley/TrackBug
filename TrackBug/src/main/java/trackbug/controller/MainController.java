package trackbug.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import trackbug.Forms.*;
import trackbug.model.NivelAcesso;
import trackbug.Forms.SessionManager;

// Forms
import trackbug.Forms.RegistrarEquipamentoForm;
import trackbug.Forms.ListarEquipamentosForm;
import trackbug.Forms.ListarFuncionariosForm;
import trackbug.Forms.HistoricoEmprestimosForm;

public class MainController {
    @FXML
    private VBox areaPrincipal;

    @FXML
    private Label labelAdministracao;

    @FXML
    private Button btnGerenciarUsuarios;

    @FXML
    private void initialize() {
        verificarPermissoes();
        mostrarTelaBoasVindas();
    }

    private void verificarPermissoes() {
        boolean isAdmin = SessionManager.getUsuarioLogado().getNivelAcesso() == NivelAcesso.ADMIN.getNivel();
        labelAdministracao.setVisible(isAdmin);
        btnGerenciarUsuarios.setVisible(isAdmin);
    }

    @FXML
    private void mostrarEmprestimos() {
        areaPrincipal.getChildren().clear();
        areaPrincipal.getChildren().add(new EmprestimoForm());
    }

    @FXML
    private void mostrarDevolucao() {
        areaPrincipal.getChildren().clear();
        areaPrincipal.getChildren().add(new DevolucaoForm());
    }

    @FXML
    private void mostrarEmprestimosAtivos() {
        areaPrincipal.getChildren().clear();
        areaPrincipal.getChildren().add(new EmprestimosAtivosForm());
    }

    @FXML
    private void mostrarEmprestimosAtraso() {
        areaPrincipal.getChildren().clear();
        areaPrincipal.getChildren().add(new EmprestimosAtrasoForm());
    }

    @FXML
    private void mostrarRegistroEquipamento() {
        areaPrincipal.getChildren().clear();
        areaPrincipal.getChildren().add(new RegistrarEquipamentoForm());
    }

    @FXML
    private void mostrarListaEquipamentos() {
        areaPrincipal.getChildren().clear();
        areaPrincipal.getChildren().add(new ListarEquipamentosForm());
    }

    @FXML
    private void mostrarCadastroFuncionario() {
        areaPrincipal.getChildren().clear();
        areaPrincipal.getChildren().add(new CadastrarFuncionarioForm());
    }

    @FXML
    private void mostrarListaFuncionarios() {
        areaPrincipal.getChildren().clear();
        areaPrincipal.getChildren().add(new ListarFuncionariosForm());
    }

    @FXML
    private void mostrarHistorico() {
        areaPrincipal.getChildren().clear();
        areaPrincipal.getChildren().add(new HistoricoEmprestimosForm());
    }

    @FXML
    private void mostrarGerenciamentoUsuarios() {
        areaPrincipal.getChildren().clear();
        areaPrincipal.getChildren().add(new GerenciamentoPermissoesForm());
    }

    private void mostrarTelaBoasVindas() {
        VBox welcomeBox = new VBox(15);
        welcomeBox.setAlignment(Pos.CENTER);

        Label bemVindo = new Label("Bem-vindo ao Sistema de Gerenciamento de Estoque.");
        bemVindo.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #00B393;" +
                        "-fx-font-family: 'Segoe UI';"
        );

        Label instrucoes = new Label("Selecione uma opção no menu lateral para começar.");
        instrucoes.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #757575;" +
                        "-fx-font-family: 'Segoe UI';"
        );

        welcomeBox.getChildren().addAll(bemVindo, instrucoes);
        areaPrincipal.getChildren().add(welcomeBox);
    }
    @FXML
    private void mostrarHistoricoAvarias() {
        areaPrincipal.getChildren().clear();
        areaPrincipal.getChildren().add(new HistoricoAvariasForm());
    }

    @FXML
    private void mostrarHistoricoAlteracoes() {
        areaPrincipal.getChildren().clear();
        areaPrincipal.getChildren().add(new HistoricoAlteracoesForm());
    }
    @FXML
    private void mostrarDeletarFuncionarios() {
        areaPrincipal.getChildren().clear();
        areaPrincipal.getChildren().add(new ListarFuncionariosForm());
    }
}