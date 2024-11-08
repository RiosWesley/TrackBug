package trackbug.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import trackbug.model.entity.Funcionario;
import trackbug.model.service.FuncionarioService;
import trackbug.model.service.EmprestimoService;
import trackbug.util.AlertHelper;
import trackbug.util.DateUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListarFuncionariosController implements Initializable {

    @FXML private TextField pesquisaField;
    @FXML private TableView<Funcionario> tabelaFuncionarios;
    @FXML private TableColumn<Funcionario, String> colunaId;
    @FXML private TableColumn<Funcionario, String> colunaNome;
    @FXML private TableColumn<Funcionario, String> colunaFuncao;
    @FXML private TableColumn<Funcionario, String> colunaData;
    @FXML private TableColumn<Funcionario, Void> colunaAcoes;
    @FXML private Label statusLabel;

    private final FuncionarioService funcionarioService;
    private final EmprestimoService emprestimoService;
    private ObservableList<Funcionario> funcionarios;

    public ListarFuncionariosController() {
        this.funcionarioService = new FuncionarioService();
        this.emprestimoService = new EmprestimoService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColunas();
        configurarPesquisa();
        carregarFuncionarios();
    }

    private void configurarColunas() {
        // Configuração das colunas da tabela
        colunaId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));

        colunaNome.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNome()));

        colunaFuncao.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFuncao()));

        colunaData.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        DateUtils.formatarData(data.getValue().getDataAdmissao())));

        configurarColunaAcoes();
    }

    private void configurarColunaAcoes() {
        colunaAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnDeletar = new Button("Deletar");
            private final HBox container = new HBox(5, btnEditar, btnDeletar);

            {
                btnEditar.getStyleClass().add("btn-edit");
                btnDeletar.getStyleClass().add("btn-delete");

                btnEditar.setOnAction(e -> editarFuncionario(getTableRow().getItem()));
                btnDeletar.setOnAction(e -> deletarFuncionario(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void configurarPesquisa() {
        pesquisaField.textProperty().addListener((obs, old, novo) -> {
            if (funcionarios != null) {
                FilteredList<Funcionario> dadosFiltrados = new FilteredList<>(funcionarios);
                dadosFiltrados.setPredicate(funcionario -> {
                    if (novo == null || novo.isEmpty()) {
                        return true;
                    }
                    String filtroLowerCase = novo.toLowerCase();
                    return funcionario.getNome().toLowerCase().contains(filtroLowerCase) ||
                            funcionario.getFuncao().toLowerCase().contains(filtroLowerCase);
                });
                tabelaFuncionarios.setItems(dadosFiltrados);
                atualizarStatusLabel();
            }
        });
    }

    @FXML
    private void pesquisar() {
        // A pesquisa já é feita pelo listener do TextField
        // Este método existe para responder ao botão de pesquisa
    }

    @FXML
    private void atualizarLista() {
        pesquisaField.clear();
        carregarFuncionarios();
    }

    private void carregarFuncionarios() {
        try {
            funcionarios = FXCollections.observableArrayList(funcionarioService.listarTodos());
            tabelaFuncionarios.setItems(funcionarios);
            atualizarStatusLabel();
        } catch (Exception e) {
            AlertHelper.showError("Erro", "Erro ao carregar funcionários: " + e.getMessage());
        }
    }

    private void editarFuncionario(Funcionario funcionario) {
        if (funcionario != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/editar-funcionario.fxml"));
                VBox dialogContent = loader.load();

                EditarFuncionarioController controller = loader.getController();
                controller.setFuncionario(funcionario);

                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.initStyle(StageStyle.DECORATED);
                dialogStage.setResizable(false);

                Scene scene = new Scene(dialogContent);
                scene.getStylesheets().add(
                        getClass().getResource("/styles/styles.css").toExternalForm());

                dialogStage.setScene(scene);
                dialogStage.showAndWait();

                // Recarrega a lista após fechar o diálogo
                carregarFuncionarios();
            } catch (IOException e) {
                AlertHelper.showError("Erro",
                        "Erro ao abrir formulário de edição: " + e.getMessage());
            }
        }
    }

    private void deletarFuncionario(Funcionario funcionario) {
        if (funcionario != null) {
            try {
                if (emprestimoService.possuiEmprestimosAtivos(funcionario.getId())) {
                    AlertHelper.showWarning("Não é possível excluir",
                            "Este funcionário possui empréstimos ativos. " +
                                    "Finalize todos os empréstimos antes de excluí-lo.");
                    return;
                }

                Optional<ButtonType> result = AlertHelper.showConfirmation(
                        "Confirmar Exclusão",
                        "Deseja realmente excluir o funcionário?",
                        String.format("Funcionário: %s%nCódigo: %s%n%n" +
                                        "Esta ação não poderá ser desfeita.",
                                funcionario.getNome(), funcionario.getId())
                );

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    funcionarioService.deletar(funcionario.getId());
                    carregarFuncionarios();
                    AlertHelper.showSuccess("Funcionário excluído com sucesso!");
                }
            } catch (Exception e) {
                AlertHelper.showError("Erro",
                        "Erro ao excluir funcionário: " + e.getMessage());
            }
        }
    }

    private void atualizarStatusLabel() {
        int total = tabelaFuncionarios.getItems().size();
        statusLabel.setText(String.format("Total de funcionários: %d", total));
    }
}