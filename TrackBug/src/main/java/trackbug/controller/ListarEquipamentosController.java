package trackbug.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import trackbug.model.entity.Equipamento;
import trackbug.model.service.EquipamentoService;
import trackbug.util.AlertHelper;
import trackbug.util.DateUtils;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListarEquipamentosController implements Initializable {

    @FXML private TextField campoBusca;
    @FXML private ComboBox<String> filtroTipo;
    @FXML private ComboBox<String> filtroStatus;
    @FXML private TableView<Equipamento> tabelaEquipamentos;
    @FXML private TableColumn<Equipamento, String> colunaId;
    @FXML private TableColumn<Equipamento, String> colunaDescricao;
    @FXML private TableColumn<Equipamento, String> colunaDataCompra;
    @FXML private TableColumn<Equipamento, Integer> colunaQuantidadeAtual;
    @FXML private TableColumn<Equipamento, Integer> colunaQuantidadeEstoque;
    @FXML private TableColumn<Equipamento, Integer> colunaQuantidadeMinima;
    @FXML private TableColumn<Equipamento, String> colunaTipo;
    @FXML private TableColumn<Equipamento, String> colunaStatus;
    @FXML private TableColumn<Equipamento, Void> colunaAcoes;
    @FXML private Label statusLabel;

    private final EquipamentoService equipamentoService;
    private ObservableList<Equipamento> equipamentos;

    public ListarEquipamentosController() {
        this.equipamentoService = new EquipamentoService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarFiltros();
        configurarColunas();
        configurarPesquisa();
        carregarEquipamentos();
    }

    private void configurarFiltros() {
        filtroTipo.setItems(FXCollections.observableArrayList(
                "Todos",
                "Emprestáveis",
                "Consumíveis"
        ));
        filtroTipo.setValue("Todos");

        filtroStatus.setItems(FXCollections.observableArrayList(
                "Todos",
                "Disponível",
                "Em uso",
                "Estoque baixo"
        ));
        filtroStatus.setValue("Todos");

        filtroTipo.setOnAction(e -> aplicarFiltros());
        filtroStatus.setOnAction(e -> aplicarFiltros());
    }

    private void configurarColunas() {
        colunaId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));

        colunaDescricao.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDescricao()));

        colunaDataCompra.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        DateUtils.formatarData(data.getValue().getDataCompra())
                ));

        colunaQuantidadeAtual.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getQuantidadeAtual()
                ).asObject());

        colunaQuantidadeEstoque.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getQuantidadeEstoque()
                ).asObject());

        colunaQuantidadeMinima.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getQuantidadeMinima()
                ).asObject());

        colunaTipo.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().isTipo() ? "Consumível" : "Emprestável"
                ));

        colunaStatus.setCellValueFactory(data -> {
            Equipamento eq = data.getValue();
            String status;
            if (eq.getQuantidadeAtual() < eq.getQuantidadeMinima()) {
                status = "Estoque baixo";
            } else if (eq.getQuantidadeAtual() == eq.getQuantidadeEstoque()) {
                status = "Disponível";
            } else {
                status = "Em uso";
            }
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        // Configurar coluna de ações
        colunaAcoes.setCellFactory(column -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnDeletar = new Button("Deletar");
            private final Button btnAvaria = new Button("Registrar Avaria");
            private final HBox box = new HBox(5);

            {
                btnEditar.getStyleClass().add("btn-edit");
                btnDeletar.getStyleClass().add("btn-delete");
                btnAvaria.getStyleClass().add("btn-warning");

                btnEditar.setOnAction(e -> editarEquipamento(getTableRow().getItem()));
                btnDeletar.setOnAction(e -> deletarEquipamento(getTableRow().getItem()));
                btnAvaria.setOnAction(e -> registrarAvaria(getTableRow().getItem()));

                box.getChildren().addAll(btnEditar, btnDeletar, btnAvaria);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // Estilizar coluna de status
        colunaStatus.setCellFactory(column -> new TableCell<Equipamento, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Disponível":
                            setStyle("-fx-text-fill: #2e7d32;"); // Verde
                            break;
                        case "Em uso":
                            setStyle("-fx-text-fill: #1565c0;"); // Azul
                            break;
                        case "Estoque baixo":
                            setStyle("-fx-text-fill: #c62828;"); // Vermelho
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });
    }

    private void configurarPesquisa() {
        campoBusca.textProperty().addListener((obs, old, novo) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        if (equipamentos == null) return;

        FilteredList<Equipamento> dadosFiltrados = new FilteredList<>(equipamentos);
        String textoBusca = campoBusca.getText().toLowerCase();
        String tipoSelecionado = filtroTipo.getValue();
        String statusSelecionado = filtroStatus.getValue();

        dadosFiltrados.setPredicate(equipamento -> {
            boolean matchBusca = textoBusca.isEmpty() ||
                    equipamento.getDescricao().toLowerCase().contains(textoBusca) ||
                    equipamento.getId().toLowerCase().contains(textoBusca);

            boolean matchTipo = tipoSelecionado.equals("Todos") ||
                    (tipoSelecionado.equals("Emprestáveis") && !equipamento.isTipo()) ||
                    (tipoSelecionado.equals("Consumíveis") && equipamento.isTipo());

            String status;
            if (equipamento.getQuantidadeAtual() < equipamento.getQuantidadeMinima()) {
                status = "Estoque baixo";
            } else if (equipamento.getQuantidadeAtual() == equipamento.getQuantidadeEstoque()) {
                status = "Disponível";
            } else {
                status = "Em uso";
            }

            boolean matchStatus = statusSelecionado.equals("Todos") ||
                    statusSelecionado.equals(status);

            return matchBusca && matchTipo && matchStatus;
        });

        tabelaEquipamentos.setItems(dadosFiltrados);
        atualizarStatusLabel();
    }

    private void carregarEquipamentos() {
        try {
            equipamentos = FXCollections.observableArrayList(
                    equipamentoService.listarTodos()
            );
            tabelaEquipamentos.setItems(equipamentos);
            atualizarStatusLabel();
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar equipamentos", e.getMessage());
        }
    }

    private void editarEquipamento(Equipamento equipamento) {
        try {
            equipamentoService.editar(equipamento);
            carregarEquipamentos();
            AlertHelper.showSuccess("Equipamento atualizado com sucesso!");
        } catch (Exception e) {
            AlertHelper.showError("Erro ao editar equipamento", e.getMessage());
        }
    }

    private void deletarEquipamento(Equipamento equipamento) {
        if (equipamentoService.possuiEmprestimosAtivos(equipamento.getId())) {
            AlertHelper.showWarning("Não é possível excluir",
                    "Este equipamento possui empréstimos ativos.");
            return;
        }

        Optional<ButtonType> result = AlertHelper.showConfirmation(
                "Confirmar Exclusão",
                "Deseja realmente excluir o equipamento?",
                "Esta ação não poderá ser desfeita."
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                equipamentoService.deletar(equipamento.getId());
                carregarEquipamentos();
                AlertHelper.showSuccess("Equipamento excluído com sucesso!");
            } catch (Exception e) {
                AlertHelper.showError("Erro ao excluir equipamento", e.getMessage());
            }
        }
    }

    private void registrarAvaria(Equipamento equipamento) {
        try {
            equipamentoService.registrarAvaria(equipamento);
            carregarEquipamentos();
            AlertHelper.showSuccess("Avaria registrada com sucesso!");
        } catch (Exception e) {
            AlertHelper.showError("Erro ao registrar avaria", e.getMessage());
        }
    }

    @FXML
    private void exportarLista() {
        AlertHelper.showInfo("Em desenvolvimento",
                "A funcionalidade de exportação será implementada em breve.");
    }

    private void atualizarStatusLabel() {
        int total = tabelaEquipamentos.getItems().size();
        long disponiveis = tabelaEquipamentos.getItems().stream()
                .filter(e -> e.getQuantidadeAtual() == e.getQuantidadeEstoque())
                .count();
        long emUso = tabelaEquipamentos.getItems().stream()
                .filter(e -> e.getQuantidadeAtual() < e.getQuantidadeEstoque() &&
                        e.getQuantidadeAtual() >= e.getQuantidadeMinima())
                .count();
        long estoqueBaixo = tabelaEquipamentos.getItems().stream()
                .filter(e -> e.getQuantidadeAtual() < e.getQuantidadeMinima())
                .count();

        statusLabel.setText(String.format(
                "Total: %d | Disponíveis: %d | Em uso: %d | Estoque baixo: %d",
                total, disponiveis, emUso, estoqueBaixo
        ));
    }
}