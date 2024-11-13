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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class EmprestimosAtrasoController implements Initializable {

    @FXML private ComboBox<String> filtroAtraso;
    @FXML private TextField campoBusca;
    @FXML private TableView<Emprestimo> tabelaAtrasos;
    @FXML private TableColumn<Emprestimo, String> colunaId;
    @FXML private TableColumn<Emprestimo, String> colunaFuncionario;
    @FXML private TableColumn<Emprestimo, String> colunaEquipamento;
    @FXML private TableColumn<Emprestimo, Integer> colunaQuantidade;
    @FXML private TableColumn<Emprestimo, String> colunaDataPrevista;
    @FXML private TableColumn<Emprestimo, Long> colunaDiasAtraso;
    @FXML private Label statusLabel;
    @FXML private VBox formContainer;

    private final EmprestimoService emprestimoService;
    private ObservableList<Emprestimo> emprestimos;

    public EmprestimosAtrasoController() {
        this.emprestimoService = new EmprestimoService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarFiltros();
        configurarColunas();
        configurarPesquisa();
        carregarEmprestimosAtrasados();
        addFadeInAnimation();
    }

    private void addFadeInAnimation() {
        FadeTransition ft = new FadeTransition(Duration.millis(500), formContainer);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }
    private void configurarFiltros() {
        filtroAtraso.setItems(FXCollections.observableArrayList(
                "Todos os atrasos",
                "Até 7 dias",
                "8 a 15 dias",
                "16 a 30 dias",
                "Mais de 30 dias"
        ));
        filtroAtraso.setValue("Todos os atrasos");
        filtroAtraso.setOnAction(e -> aplicarFiltros());
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
                        data.getValue().getQuantidadeEmprestimo()).asObject());

        colunaDataPrevista.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        DateUtils.formatarDataHora(data.getValue().getDataRetornoPrevista())));

        colunaDiasAtraso.setCellValueFactory(data -> {
            long dias = ChronoUnit.DAYS.between(
                    data.getValue().getDataRetornoPrevista(),
                    LocalDateTime.now()
            );
            return new javafx.beans.property.SimpleLongProperty(dias).asObject();
        });

        // Configurar estilo baseado nos dias de atraso
        colunaDiasAtraso.setCellFactory(column -> new TableCell<Emprestimo, Long>() {
            @Override
            protected void updateItem(Long dias, boolean empty) {
                super.updateItem(dias, empty);
                if (empty || dias == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(dias.toString());
                    getStyleClass().removeAll("atraso-leve", "atraso-medio", "atraso-grave");
                    if (dias <= 7) {
                        getStyleClass().add("atraso-leve");
                    } else if (dias <= 15) {
                        getStyleClass().add("atraso-medio");
                    } else {
                        getStyleClass().add("atraso-grave");
                    }
                }
            }
        });
    }

    private void configurarPesquisa() {
        campoBusca.textProperty().addListener((obs, old, novo) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        if (emprestimos == null) return;

        FilteredList<Emprestimo> dadosFiltrados = new FilteredList<>(emprestimos);

        String textoBusca = campoBusca.getText().toLowerCase();
        String filtroPeriodo = filtroAtraso.getValue();

        dadosFiltrados.setPredicate(emprestimo -> {
            boolean matchTexto = textoBusca.isEmpty() ||
                    emprestimo.getNomeFuncionario().toLowerCase().contains(textoBusca) ||
                    emprestimo.getDescricaoEquipamento().toLowerCase().contains(textoBusca);

            long diasAtraso = ChronoUnit.DAYS.between(
                    emprestimo.getDataRetornoPrevista(),
                    LocalDateTime.now()
            );

            boolean matchPeriodo = switch (filtroPeriodo) {
                case "Até 7 dias" -> diasAtraso <= 7;
                case "8 a 15 dias" -> diasAtraso > 7 && diasAtraso <= 15;
                case "16 a 30 dias" -> diasAtraso > 15 && diasAtraso <= 30;
                case "Mais de 30 dias" -> diasAtraso > 30;
                default -> true;
            };

            return matchTexto && matchPeriodo;
        });

        tabelaAtrasos.setItems(dadosFiltrados);
        atualizarStatusLabel();
    }

    private void carregarEmprestimosAtrasados() {
        try {
            emprestimos = FXCollections.observableArrayList(
                    emprestimoService.listarEmprestimosAtrasados()
            );
            tabelaAtrasos.setItems(emprestimos);
            atualizarStatusLabel();
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar empréstimos",
                    "Não foi possível carregar a lista de empréstimos em atraso: " +
                            e.getMessage());
        }
    }

    private void atualizarStatusLabel() {
        int total = tabelaAtrasos.getItems().size();
        statusLabel.setText(String.format("Total em atraso: %d", total));
    }
}