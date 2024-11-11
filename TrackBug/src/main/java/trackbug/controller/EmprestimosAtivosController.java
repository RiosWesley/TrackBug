package trackbug.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.cell.PropertyValueFactory;
import trackbug.model.entity.Emprestimo;
import trackbug.model.service.EmprestimoService;
import trackbug.util.AlertHelper;
import trackbug.util.DateUtils;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class EmprestimosAtivosController implements Initializable {

    @FXML private TableView<Emprestimo> tabelaEmprestimos;
    @FXML private TableColumn<Emprestimo, String> colunaId;
    @FXML private TableColumn<Emprestimo, String> colunaFuncionario;
    @FXML private TableColumn<Emprestimo, String> colunaEquipamento;
    @FXML private TableColumn<Emprestimo, Integer> colunaQuantidade;
    @FXML private TableColumn<Emprestimo, String> colunaDataSaida;
    @FXML private TableColumn<Emprestimo, String> colunaDataPrevista;
    @FXML private TableColumn<Emprestimo, String> colunaStatus;
    @FXML private TextField campoBusca;
    @FXML private Label statusLabel;

    private final EmprestimoService emprestimoService;
    private ObservableList<Emprestimo> emprestimos;

    public EmprestimosAtivosController() {
        this.emprestimoService = new EmprestimoService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColunas();
        configurarPesquisa();
        carregarEmprestimos();
        configurarAlinhamentoCentral(colunaId);
        configurarAlinhamentoCentral(colunaFuncionario);
        configurarAlinhamentoCentral(colunaEquipamento);
        configurarAlinhamentoCentral(colunaQuantidade);
        configurarAlinhamentoCentral(colunaDataSaida);
        configurarAlinhamentoCentral(colunaDataPrevista);
        configurarAlinhamentoCentral(colunaStatus);
    }

    private void configurarColunas() {
        colunaId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(data.getValue().getId())
                ));

        colunaFuncionario.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getNomeFuncionario()
                ));

        colunaEquipamento.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDescricaoEquipamento()
                ));

        colunaQuantidade.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getQuantidadeEmprestimo()).asObject()
        );

        colunaDataSaida.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        DateUtils.formatarDataHora(data.getValue().getDataSaida())
                ));

        colunaDataPrevista.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        DateUtils.formatarDataHora(data.getValue().getDataRetornoPrevista())
                ));

        colunaStatus.setCellValueFactory(data -> {
            LocalDateTime dataPrevista = data.getValue().getDataRetornoPrevista();
            String status = LocalDateTime.now().isAfter(dataPrevista) ?
                    "Atrasado" : "Em dia";
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        // Configuração do estilo da coluna de status
        colunaStatus.setCellFactory(column -> new TableCell<Emprestimo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                    setStyle("-fx-padding: 0 10 0 10; " +
                            switch (item) {
                                case "Atrasado" -> "-fx-text-fill: #c62828;";
                                case "Em dia" -> "-fx-text-fill: #2e7d32;";
                                default -> "";
                            });
                }
            }
        });
    }

    private void configurarPesquisa() {
        campoBusca.textProperty().addListener((obs, old, novo) -> {
            if (emprestimos != null) {
                FilteredList<Emprestimo> dadosFiltrados = new FilteredList<>(emprestimos);
                dadosFiltrados.setPredicate(emprestimo -> {
                    if (novo == null || novo.isEmpty()) {
                        return true;
                    }
                    String filtroLowerCase = novo.toLowerCase();
                    return emprestimo.getNomeFuncionario().toLowerCase().contains(filtroLowerCase) ||
                            emprestimo.getDescricaoEquipamento().toLowerCase().contains(filtroLowerCase);
                });
                tabelaEmprestimos.setItems(dadosFiltrados);
                atualizarStatusLabel();
            }
        });
    }

    @FXML
    private void atualizarLista() {
        campoBusca.clear();
        carregarEmprestimos();
    }

    private void carregarEmprestimos() {
        try {
            emprestimos = FXCollections.observableArrayList(
                    emprestimoService.listarEmprestimosAtivos()
            );
            tabelaEmprestimos.setItems(emprestimos);
            atualizarStatusLabel();
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar empréstimos", e.getMessage());
        }
    }

    private void atualizarStatusLabel() {
        int total = tabelaEmprestimos.getItems().size();
        int atrasados = 0;
        for (Emprestimo emp : tabelaEmprestimos.getItems()) {
            if (LocalDateTime.now().isAfter(emp.getDataRetornoPrevista())) {
                atrasados++;
            }
        }
        statusLabel.setText(String.format(
                "Total de empréstimos ativos: %d | Atrasados: %d", total, atrasados
        ));
    }

    private <T> void configurarAlinhamentoCentral(TableColumn<Emprestimo, T> coluna) {
        coluna.setCellFactory(tc -> new TableCell<Emprestimo, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.toString());
                    setAlignment(Pos.CENTER);
                    setStyle("-fx-padding: 0 10 0 10;"); // Adiciona um pequeno padding horizontal
                }
            }
        });
    }
}