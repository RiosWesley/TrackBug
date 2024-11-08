package trackbug.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.text.Text;
import trackbug.model.entity.LogEquipamento;
import trackbug.model.service.LogEquipamentoService;
import trackbug.model.service.EquipamentoService;
import trackbug.util.AlertHelper;
import trackbug.util.DateUtils;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HistoricoAlteracoesController implements Initializable {

    @FXML private ComboBox<String> filtroAcao;
    @FXML private TextField campoBusca;
    @FXML private TableView<LogEquipamento> tabelaHistorico;
    @FXML private TableColumn<LogEquipamento, String> colunaData;
    @FXML private TableColumn<LogEquipamento, String> colunaEquipamento;
    @FXML private TableColumn<LogEquipamento, String> colunaDescricao;
    @FXML private TableColumn<LogEquipamento, String> colunaAcao;
    @FXML private TableColumn<LogEquipamento, String> colunaDetalhes;
    @FXML private Label statusLabel;

    private final LogEquipamentoService logService;
    private final EquipamentoService equipamentoService;
    private ObservableList<LogEquipamento> registrosLog;

    public HistoricoAlteracoesController() {
        this.logService = new LogEquipamentoService();
        this.equipamentoService = new EquipamentoService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarFiltros();
        configurarColunas();
        configurarPesquisa();
        carregarHistorico();
    }

    private void configurarFiltros() {
        filtroAcao.setItems(FXCollections.observableArrayList(
                "Todas",
                "EDICAO",
                "EXCLUSAO",
                "AVARIA"
        ));
        filtroAcao.setValue("Todas");
        filtroAcao.setOnAction(e -> aplicarFiltros());
    }

    private void configurarColunas() {
        colunaData.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        DateUtils.formatarDataHora(
                                data.getValue().getDataAcao().toLocalDateTime()
                        )
                ));

        colunaEquipamento.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        buscarNomeEquipamento(data.getValue().getIdEquipamento())
                ));

        colunaDescricao.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDescricao()
                ));

        colunaAcao.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getAcao()
                ));

        colunaDetalhes.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDetalhes()
                ));

        // Configurar wrap text para coluna de detalhes
        colunaDetalhes.setCellFactory(tc -> {
            TableCell<LogEquipamento, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(colunaDetalhes.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });

        // Estilizar coluna de ação
        colunaAcao.setCellFactory(column -> new TableCell<LogEquipamento, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "EDICAO":
                            setStyle("-fx-text-fill: #1976d2;"); // Azul
                            break;
                        case "EXCLUSAO":
                            setStyle("-fx-text-fill: #d32f2f;"); // Vermelho
                            break;
                        case "AVARIA":
                            setStyle("-fx-text-fill: #f57c00;"); // Laranja
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
        if (registrosLog == null) return;

        FilteredList<LogEquipamento> dadosFiltrados = new FilteredList<>(registrosLog);
        String textoBusca = campoBusca.getText().toLowerCase();
        String acaoSelecionada = filtroAcao.getValue();

        dadosFiltrados.setPredicate(log -> {
            boolean matchTexto = textoBusca.isEmpty() ||
                    buscarNomeEquipamento(log.getIdEquipamento()).toLowerCase().contains(textoBusca) ||
                    log.getDescricao().toLowerCase().contains(textoBusca) ||
                    log.getDetalhes().toLowerCase().contains(textoBusca);

            boolean matchAcao = "Todas".equals(acaoSelecionada) ||
                    log.getAcao().equals(acaoSelecionada);

            return matchTexto && matchAcao;
        });

        tabelaHistorico.setItems(dadosFiltrados);
        atualizarStatusLabel();
    }

    @FXML
    private void atualizarLista() {
        campoBusca.clear();
        filtroAcao.setValue("Todas");
        carregarHistorico();
    }

    private void carregarHistorico() {
        try {
            registrosLog = FXCollections.observableArrayList(
                    logService.buscarTodos()
            );
            tabelaHistorico.setItems(registrosLog);
            atualizarStatusLabel();
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar histórico", e.getMessage());
        }
    }

    private String buscarNomeEquipamento(String id) {
        try {
            return equipamentoService.buscarNomePorId(id);
        } catch (Exception e) {
            return id;
        }
    }

    private void atualizarStatusLabel() {
        int total = tabelaHistorico.getItems().size();
        Map<String, Long> contadorPorAcao = tabelaHistorico.getItems().stream()
                .collect(Collectors.groupingBy(
                        LogEquipamento::getAcao,
                        Collectors.counting()
                ));

        statusLabel.setText(String.format(
                "Total: %d | Edições: %d | Exclusões: %d | Avarias: %d",
                total,
                contadorPorAcao.getOrDefault("EDICAO", 0L),
                contadorPorAcao.getOrDefault("EXCLUSAO", 0L),
                contadorPorAcao.getOrDefault("AVARIA", 0L)
        ));
    }
}