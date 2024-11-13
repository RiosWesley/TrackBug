package trackbug.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import trackbug.model.entity.RegistroAvaria;
import trackbug.model.service.AvariaService;
import trackbug.model.service.EquipamentoService;
import trackbug.util.AlertHelper;
import trackbug.util.DateUtils;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HistoricoAvariasController implements Initializable {

    @FXML private TextField equipamentoField;
    @FXML private ComboBox<String> filtroPeriodo;
    @FXML private ComboBox<String> filtroGravidade;
    @FXML private TableView<RegistroAvaria> tabelaAvarias;
    @FXML private TableColumn<RegistroAvaria, String> colunaEquipamento;
    @FXML private TableColumn<RegistroAvaria, String> colunaData;
    @FXML private TableColumn<RegistroAvaria, Integer> colunaQuantidade;
    @FXML private TableColumn<RegistroAvaria, String> colunaGravidade;
    @FXML private TableColumn<RegistroAvaria, String> colunaDescricao;
    @FXML private TableColumn<RegistroAvaria, String> colunaStatus;
    @FXML private Label statusLabel;
    @FXML private Label totalAvariasLabel;
    @FXML private Label totalQuantidadeLabel;
    @FXML private Label periodoMaisCriticoLabel;
    @FXML private VBox formContainer;

    private final AvariaService avariaService;
    private final EquipamentoService equipamentoService;
    private ObservableList<RegistroAvaria> avarias;

    public HistoricoAvariasController() {
        this.avariaService = new AvariaService();
        this.equipamentoService = new EquipamentoService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarFiltros();
        configurarColunas();
        configurarPesquisa();
        carregarAvarias("");
        addFadeInAnimation();
    }

    private void addFadeInAnimation() {
        FadeTransition ft = new FadeTransition(Duration.millis(500), formContainer);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    private void configurarFiltros() {
        filtroPeriodo.setItems(FXCollections.observableArrayList(
                "Todos os períodos",
                "Último mês",
                "Últimos 3 meses",
                "Últimos 6 meses",
                "Último ano"
        ));
        filtroPeriodo.setValue("Todos os períodos");

        filtroGravidade.setItems(FXCollections.observableArrayList(
                "Todas",
                "Baixa",
                "Média",
                "Alta",
                "Crítica"
        ));
        filtroGravidade.setValue("Todas");

        filtroPeriodo.setOnAction(e -> aplicarFiltros());
        filtroGravidade.setOnAction(e -> aplicarFiltros());
    }

    private void configurarColunas() {
        colunaEquipamento.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        buscarNomeEquipamento(data.getValue().getIdEquipamento())
                ));

        colunaData.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        DateUtils.formatarDataHora(data.getValue().getData())
                ));

        colunaQuantidade.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getQuantidade()
                ).asObject());

        colunaGravidade.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getGravidade()
                ));

        colunaDescricao.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDescricao()
                ));

        colunaStatus.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getStatus()
                ));

        configurarEstiloColunaGravidade();
    }

    private void configurarEstiloColunaGravidade() {
        colunaGravidade.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(switch (item.toLowerCase()) {
                        case "baixa" -> "-fx-text-fill: #2e7d32;";
                        case "média" -> "-fx-text-fill: #f57c00;";
                        case "alta" -> "-fx-text-fill: #d32f2f;";
                        case "crítica" -> "-fx-text-fill: #c62828; -fx-font-weight: bold;";
                        default -> "";
                    });
                }
            }
        });
    }

    private void configurarPesquisa() {
        equipamentoField.textProperty().addListener((obs, old, novo) -> aplicarFiltros());
    }

    @FXML
    private void pesquisar() {
        carregarAvarias(equipamentoField.getText());
    }

    private void aplicarFiltros() {
        if (avarias == null) return;

        FilteredList<RegistroAvaria> dadosFiltrados = new FilteredList<>(avarias);
        String textoBusca = equipamentoField.getText().toLowerCase();
        String periodoSelecionado = filtroPeriodo.getValue();
        String gravidadeSelecionada = filtroGravidade.getValue();

        dadosFiltrados.setPredicate(avaria -> {
            boolean matchTexto = textoBusca.isEmpty() ||
                    buscarNomeEquipamento(avaria.getIdEquipamento()).toLowerCase().contains(textoBusca) ||
                    avaria.getDescricao().toLowerCase().contains(textoBusca);

            boolean matchPeriodo = "Todos os períodos".equals(periodoSelecionado) ||
                    verificarPeriodo(avaria.getData(), periodoSelecionado);

            boolean matchGravidade = "Todas".equals(gravidadeSelecionada) ||
                    gravidadeSelecionada.equals(avaria.getGravidade());

            return matchTexto && matchPeriodo && matchGravidade;
        });

        tabelaAvarias.setItems(dadosFiltrados);
        atualizarEstatisticas(dadosFiltrados);
    }

    private boolean verificarPeriodo(LocalDateTime data, String periodo) {
        if (data == null) return false;

        LocalDateTime agora = LocalDateTime.now();
        return switch (periodo) {
            case "Último mês" -> data.isAfter(agora.minusMonths(1));
            case "Últimos 3 meses" -> data.isAfter(agora.minusMonths(3));
            case "Últimos 6 meses" -> data.isAfter(agora.minusMonths(6));
            case "Último ano" -> data.isAfter(agora.minusYears(1));
            default -> true;
        };
    }

    private void carregarAvarias(String filtro) {
        try {
            List<RegistroAvaria> listaAvarias = avariaService.buscarTodos(filtro);
            avarias = FXCollections.observableArrayList(listaAvarias);
            tabelaAvarias.setItems(avarias);
            atualizarEstatisticas(avarias);
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar avarias", e.getMessage());
            e.printStackTrace(); // Remova em produção
        }
    }

    private void atualizarEstatisticas(ObservableList<RegistroAvaria> dadosFiltrados) {
        if (dadosFiltrados == null) return;

        // Total de avarias
        totalAvariasLabel.setText(String.valueOf(dadosFiltrados.size()));

        // Total de itens afetados
        int totalItens = dadosFiltrados.stream()
                .mapToInt(RegistroAvaria::getQuantidade)
                .sum();
        totalQuantidadeLabel.setText(String.valueOf(totalItens));

        // Período mais crítico
        calcularPeriodoMaisCritico(dadosFiltrados);

        // Status por gravidade
        atualizarStatusPorGravidade(dadosFiltrados);
    }

    private void calcularPeriodoMaisCritico(ObservableList<RegistroAvaria> dadosFiltrados) {
        if (dadosFiltrados.isEmpty()) {
            periodoMaisCriticoLabel.setText("N/A");
            return;
        }

        Map<YearMonth, Integer> avariasPorMes = dadosFiltrados.stream()
                .filter(a -> a.getData() != null)
                .collect(Collectors.groupingBy(
                        a -> YearMonth.from(a.getData()),
                        Collectors.summingInt(RegistroAvaria::getQuantidade)
                ));

        if (!avariasPorMes.isEmpty()) {
            YearMonth periodoMaisCritico = avariasPorMes.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(YearMonth.now());

            periodoMaisCriticoLabel.setText(
                    periodoMaisCritico.format(DateTimeFormatter.ofPattern("MMM/yyyy"))
            );
        }
    }

    private void atualizarStatusPorGravidade(ObservableList<RegistroAvaria> dadosFiltrados) {
        Map<String, Long> contagemPorGravidade = dadosFiltrados.stream()
                .collect(Collectors.groupingBy(
                        RegistroAvaria::getGravidade,
                        Collectors.counting()
                ));

        statusLabel.setText(String.format(
                "Total: %d | Baixa: %d | Média: %d | Alta: %d | Crítica: %d",
                dadosFiltrados.size(),
                contagemPorGravidade.getOrDefault("Baixa", 0L),
                contagemPorGravidade.getOrDefault("Média", 0L),
                contagemPorGravidade.getOrDefault("Alta", 0L),
                contagemPorGravidade.getOrDefault("Crítica", 0L)
        ));
    }

    private String buscarNomeEquipamento(String id) {
        try {
            return equipamentoService.buscarNomePorId(id);
        } catch (Exception e) {
            return "Equipamento não encontrado";
        }
    }
}