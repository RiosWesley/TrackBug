package trackbug.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.application.Platform;
import trackbug.model.service.DashboardService;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map;

public class DashboardController implements Initializable {
    @FXML private Label emprestimosAtivosLabel;
    @FXML private Label equipamentosLabel;
    @FXML private Label atrasosLabel;
    @FXML private Label funcionariosLabel;

    @FXML private Label emprestimosAtivosTendencia;
    @FXML private Label equipamentosTendencia;
    @FXML private Label atrasosTendencia;
    @FXML private Label funcionariosTendencia;

    @FXML private BarChart<String, Number> emprestimosPorMes;

    @FXML private TableView<EmprestimoRecente> emprestimosRecentes;
    @FXML private TableColumn<EmprestimoRecente, String> funcionarioColumn;
    @FXML private TableColumn<EmprestimoRecente, String> equipamentoColumn;
    @FXML private TableColumn<EmprestimoRecente, String> dataColumn;
    @FXML private TableColumn<EmprestimoRecente, String> statusColumn;

    @FXML private VBox alertaContainer;
    @FXML private ListView<String> alertasList;

    private final DashboardService dashboardService;
    private Timer updateTimer;
    private final DateTimeFormatter dateFormatter;

    public DashboardController() {
        this.dashboardService = new DashboardService();
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabela();
        iniciarAtualizacaoAutomatica();
        atualizarDashboard();
    }

    private void configurarTabela() {
        funcionarioColumn.setCellValueFactory(new PropertyValueFactory<>("funcionario"));
        equipamentoColumn.setCellValueFactory(new PropertyValueFactory<>("equipamento"));
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("dataDevolucao"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Configurar estilo para a coluna de status
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Em dia".equals(item)) {
                        setStyle("-fx-text-fill: #2e7d32;"); // Verde
                    } else {
                        setStyle("-fx-text-fill: #c62828;"); // Vermelho
                    }
                }
            }
        });
    }

    private void iniciarAtualizacaoAutomatica() {
        updateTimer = new Timer(true);
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> atualizarDashboard());
            }
        }, 0, 30000); // Atualizar a cada 30 segundos
    }

    private void atualizarDashboard() {
        try {
            // Atualizar métricas
            var metrics = dashboardService.getDashboardMetrics();

            // Atualizar labels
            emprestimosAtivosLabel.setText(String.valueOf(metrics.get("emprestimosAtivos")));
            equipamentosLabel.setText(String.valueOf(metrics.get("equipamentosCadastrados")));
            atrasosLabel.setText(String.valueOf(metrics.get("devolucoesPendentes")));
            funcionariosLabel.setText(String.valueOf(metrics.get("funcionariosComEmprestimos")));

            // Atualizar tendências
            @SuppressWarnings("unchecked")
            var tendencias = (Map<String, Object>) metrics.get("tendencias");
            atualizarTendencia(emprestimosAtivosTendencia, (Double) tendencias.get("emprestimos"));

            // Atualizar gráfico
            atualizarGrafico();

            // Atualizar tabela de empréstimos recentes
            atualizarTabelaEmprestimos();

            // Atualizar alertas
            atualizarAlertas();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizarTendencia(Label label, Double valor) {
        if (valor > 0) {
            label.setText("↑ +" + String.format("%.1f", valor) + "%");
            label.setStyle("-fx-text-fill: #2e7d32;"); // Verde
        } else {
            label.setText("↓ " + String.format("%.1f", valor) + "%");
            label.setStyle("-fx-text-fill: #c62828;"); // Vermelho
        }
    }

    private void atualizarGrafico() {
        try {
            XYChart.Series<String, Number> series = new XYChart.Series<>();

            var dadosGrafico = dashboardService.getEmprestimosPorMes();
            for (var dado : dadosGrafico) {
                series.getData().add(new XYChart.Data<>(
                        dado.get("mes").toString(),
                        ((Number) dado.get("quantidade")).intValue()
                ));
            }

            emprestimosPorMes.getData().clear();
            emprestimosPorMes.getData().add(series);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizarTabelaEmprestimos() {
        try {
            var emprestimos = dashboardService.getEmprestimosRecentes();
            var items = FXCollections.observableArrayList(
                    emprestimos.stream()
                            .map(emp -> new EmprestimoRecente(
                                    emp.get("funcionario").toString(),
                                    emp.get("equipamento").toString(),
                                    emp.get("dataPrevista").toString(),
                                    emp.get("status").toString()
                            ))
                            .toList()
            );
            emprestimosRecentes.setItems(items);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizarAlertas() {
        try {
            var metrics = dashboardService.getDashboardMetrics();
            // Convertendo Long para Integer de forma segura
            Long equipamentosBaixoEstoque = (Long) metrics.get("equipamentosBaixoEstoque");

            if (equipamentosBaixoEstoque != null && equipamentosBaixoEstoque > 0) {
                alertaContainer.setVisible(true);
                alertaContainer.setManaged(true);
                alertasList.getItems().setAll(
                        String.format("%d equipamento(s) com estoque abaixo do mínimo",
                                equipamentosBaixoEstoque)
                );
            } else {
                alertaContainer.setVisible(false);
                alertaContainer.setManaged(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cleanup() {
        if (updateTimer != null) {
            updateTimer.cancel();
        }
    }

    // Classe interna para os empréstimos recentes
    public static class EmprestimoRecente {
        private final String funcionario;
        private final String equipamento;
        private final String dataDevolucao;
        private final String status;

        public EmprestimoRecente(String funcionario, String equipamento,
                                 String dataDevolucao, String status) {
            this.funcionario = funcionario;
            this.equipamento = equipamento;
            this.dataDevolucao = dataDevolucao;
            this.status = status;
        }

        // Getters
        public String getFuncionario() { return funcionario; }
        public String getEquipamento() { return equipamento; }
        public String getDataDevolucao() { return dataDevolucao; }
        public String getStatus() { return status; }
    }
}