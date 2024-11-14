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
import trackbug.model.entity.Emprestimo;
import trackbug.model.service.EmprestimoService;
import trackbug.util.AlertHelper;
import trackbug.util.DateUtils;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HistoricoEmprestimosController implements Initializable {

    @FXML private DatePicker dataInicio;
    @FXML private DatePicker dataFim;
    @FXML private TextField funcionarioField;
    @FXML private TextField equipamentoField;
    @FXML private TableView<Emprestimo> tabelaHistorico;
    @FXML private TableColumn<Emprestimo, String> colunaData;
    @FXML private TableColumn<Emprestimo, String> colunaFuncionario;
    @FXML private TableColumn<Emprestimo, String> colunaEquipamento;
    @FXML private TableColumn<Emprestimo, Integer> colunaQtd;
    @FXML private TableColumn<Emprestimo, String> colunaPrevista;
    @FXML private TableColumn<Emprestimo, String> colunaEfetiva;
    @FXML private TableColumn<Emprestimo, String> colunaStatus;

    @FXML private Label totalEmprestimosLabel;
    @FXML private Label mediaAtrasoLabel;
    @FXML private Label taxaDevolucaoLabel;
    @FXML private Label itemMaisEmprestadoLabel;
    @FXML private VBox formContainer;;

    private final EmprestimoService emprestimoService;
    private ObservableList<Emprestimo> emprestimos;

    public HistoricoEmprestimosController() {
        this.emprestimoService = new EmprestimoService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColunas();
        configurarFiltros();
        carregarEmprestimos();
        addFadeInAnimation();
    }

    private void addFadeInAnimation() {
        FadeTransition ft = new FadeTransition(Duration.millis(500), formContainer);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    private void configurarColunas() {
        colunaData.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        DateUtils.formatarDataHora(data.getValue().getDataSaida())
                ));

        colunaFuncionario.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getNomeFuncionario()
                ));

        colunaEquipamento.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDescricaoEquipamento()
                ));

        colunaQtd.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getQuantidadeEmprestimo()
                ).asObject());

        colunaPrevista.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        DateUtils.formatarDataHora(data.getValue().getDataRetornoPrevista())
                ));

        colunaEfetiva.setCellValueFactory(data -> {
            LocalDateTime dataRetorno = data.getValue().getDataRetornoEfetiva();
            return new javafx.beans.property.SimpleStringProperty(
                    dataRetorno != null ? DateUtils.formatarDataHora(dataRetorno) : "Pendente"
            );
        });

        colunaStatus.setCellValueFactory(data -> {
            String status;
            if (!data.getValue().isAtivo()) {
                LocalDateTime dataEfetiva = data.getValue().getDataRetornoEfetiva();
                LocalDateTime dataPrevista = data.getValue().getDataRetornoPrevista();
                if (dataEfetiva.isAfter(dataPrevista)) {
                    status = "Devolvido com atraso";
                } else {
                    status = "Devolvido no prazo";
                }
            } else {
                if (LocalDateTime.now().isAfter(data.getValue().getDataRetornoPrevista())) {
                    status = "Em atraso";
                } else {
                    status = "Em andamento";
                }
            }
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        // Estilizar coluna de status
        colunaStatus.setCellFactory(column -> new TableCell<Emprestimo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Devolvido no prazo":
                            setStyle("-fx-text-fill: #2e7d32;"); // Verde
                            break;
                        case "Em andamento":
                            setStyle("-fx-text-fill: #1976d2;"); // Azul
                            break;
                        case "Devolvido com atraso":
                        case "Em atraso":
                            setStyle("-fx-text-fill: #d32f2f;"); // Vermelho
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });
    }

    private void configurarFiltros() {
        // Configurar data inicial como primeiro dia do mês atual
        dataInicio.setValue(LocalDate.now().withDayOfMonth(1));
        // Configurar data final como dia atual
        dataFim.setValue(LocalDate.now());
    }

    @FXML
    private void pesquisar() {
        aplicarFiltros();
    }

    @FXML
    private void limparFiltros() {
        dataInicio.setValue(LocalDate.now().withDayOfMonth(1));
        dataFim.setValue(LocalDate.now());
        funcionarioField.clear();
        equipamentoField.clear();
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        if (emprestimos == null) return;

        FilteredList<Emprestimo> dadosFiltrados = new FilteredList<>(emprestimos);

        LocalDate dataInicioFiltro = dataInicio.getValue();
        LocalDate dataFimFiltro = dataFim.getValue();
        String funcionarioFiltro = funcionarioField.getText().toLowerCase();
        String equipamentoFiltro = equipamentoField.getText().toLowerCase();

        dadosFiltrados.setPredicate(emprestimo -> {
            LocalDateTime dataSaida = emprestimo.getDataSaida();
            LocalDate dataSaidaLocal = dataSaida.toLocalDate();

            boolean matchData = (dataInicioFiltro == null || !dataSaidaLocal.isBefore(dataInicioFiltro)) &&
                    (dataFimFiltro == null || !dataSaidaLocal.isAfter(dataFimFiltro));

            boolean matchFuncionario = funcionarioFiltro.isEmpty() ||
                    emprestimo.getNomeFuncionario().toLowerCase().contains(funcionarioFiltro);

            boolean matchEquipamento = equipamentoFiltro.isEmpty() ||
                    emprestimo.getDescricaoEquipamento().toLowerCase().contains(equipamentoFiltro);

            return matchData && matchFuncionario && matchEquipamento;
        });

        tabelaHistorico.setItems(dadosFiltrados);
        atualizarEstatisticas(dadosFiltrados);
    }

    private void carregarEmprestimos() {
        try {
            emprestimos = FXCollections.observableArrayList(
                    emprestimoService.listarTodos()
            );
            aplicarFiltros();
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar empréstimos", e.getMessage());
        }
    }

    private void atualizarEstatisticas(ObservableList<Emprestimo> lista) {
        // Total de empréstimos
        totalEmprestimosLabel.setText(String.valueOf(lista.size()));

        // Média de atraso
        double mediaAtraso = lista.stream()
                .filter(emp -> emp.getDataRetornoEfetiva() != null &&
                        emp.getDataRetornoEfetiva().isAfter(emp.getDataRetornoPrevista()))
                .mapToLong(emp -> ChronoUnit.DAYS.between(
                        emp.getDataRetornoPrevista(),
                        emp.getDataRetornoEfetiva()))
                .average()
                .orElse(0.0);
        mediaAtrasoLabel.setText(String.format("%.1f", mediaAtraso));

        // Taxa de devolução no prazo
        long devolvidos = lista.stream()
                .filter(emp -> emp.getDataRetornoEfetiva() != null)
                .count();
        long devolvidosNoPrazo = lista.stream()
                .filter(emp -> emp.getDataRetornoEfetiva() != null &&
                        !emp.getDataRetornoEfetiva().isAfter(emp.getDataRetornoPrevista()))
                .count();
        double taxaDevolucao = devolvidos > 0 ?
                (double) devolvidosNoPrazo / devolvidos * 100 : 0;
        taxaDevolucaoLabel.setText(String.format("%.1f%%", taxaDevolucao));

        // Item mais emprestado
        Map<String, Long> emprestimoPorEquipamento = lista.stream()
                .collect(Collectors.groupingBy(
                        Emprestimo::getDescricaoEquipamento,
                        Collectors.counting()
                ));

        String itemMaisEmprestado = emprestimoPorEquipamento.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        itemMaisEmprestadoLabel.setText(itemMaisEmprestado);
    }
}