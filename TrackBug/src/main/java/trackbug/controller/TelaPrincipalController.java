package trackbug.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import trackbug.model.entity.Emprestimo;
import trackbug.model.entity.Equipamento;
import trackbug.model.entity.Funcionario;
import trackbug.model.service.EmprestimoService;
import trackbug.model.service.EquipamentoService;
import trackbug.model.service.FuncionarioService;
import trackbug.util.AlertHelper;
import trackbug.util.DateUtils;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TelaPrincipalController implements Initializable {

    @FXML private Label totalEmprestimosAtivosLabel;
    @FXML private Label totalEmprestimosAtrasadosLabel;
    @FXML private Label totalEquipamentosEmprestadosLabel;
    @FXML private Label totalEquipamentosEstoqueLabel;
    @FXML private Label totalFuncionariosLabel;
    @FXML private Label totalEquipamentosLabel;
    @FXML private TableView<Emprestimo> tabelaAtrasos;
    @FXML private TableColumn<Emprestimo, String> colunaId;
    @FXML private TableColumn<Emprestimo, String> colunaFuncionario;
    @FXML private TableColumn<Emprestimo, String> colunaEquipamento;
    @FXML private TableColumn<Emprestimo, Integer> colunaQuantidade;
    @FXML private TableColumn<Emprestimo, String> colunaDataPrevista;
    @FXML private TableColumn<Emprestimo, Long> colunaDiasAtraso;
    @FXML private TableView<Equipamento> tabelaEstoqueBaixo;
    @FXML private TableColumn<Equipamento, String> colunaIdEstoque;
    @FXML private TableColumn<Equipamento, String> colunaEquipamentoEstoque;
    @FXML private TableColumn<Equipamento, Integer> colunaQuantidadeEstoque;
    @FXML private TableColumn<Equipamento, Integer> colunaQuantidadeMinima;
    @FXML private TableView<Emprestimo> tabelaEmprestimosDia;
    @FXML private TableColumn<Emprestimo, String> colunaIdDia;
    @FXML private TableColumn<Emprestimo, String> colunaFuncionarioDia;
    @FXML private TableColumn<Emprestimo, String> colunaEquipamentoDia;
    @FXML private TableColumn<Emprestimo, Integer> colunaQuantidadeDia;
    @FXML private TableColumn<Emprestimo, String> colunaDataSaidaDia;
    @FXML private TableColumn<Emprestimo, String> colunaDataPrevistaDia;
    @FXML private TableColumn<Emprestimo, String> colunaStatusDia;


    private final EmprestimoService emprestimoService;
    private final FuncionarioService funcionarioService;
    private final EquipamentoService equipamentoService;

    public TelaPrincipalController() {
        this.emprestimoService = new EmprestimoService();
        this.funcionarioService = new FuncionarioService();
        this.equipamentoService = new EquipamentoService();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColunasAtrasados();
        configurarColunasEstoqueBaixo();
        configurarColunasEmprestimosDoDia();
        carregarEmprestimos();
    }

    private void carregarEmprestimos() {
        try {
            ObservableList<Emprestimo> emprestimosAtivos = FXCollections.observableArrayList(
                    emprestimoService.listarEmprestimosAtivos()
            );
            ObservableList<Emprestimo> emprestimosAtrasados = FXCollections.observableArrayList(
                    emprestimoService.listarEmprestimosAtrasados()
            );
            ObservableList<Emprestimo> emprestimosDoDia = FXCollections.observableArrayList(
                    emprestimoService.listarEmprestimosDoDia()
            );
            ObservableList<Equipamento> equipamentos = FXCollections.observableArrayList(
                    equipamentoService.listarTodos()
            );
            ObservableList<Equipamento> equipamentosEmprestados = FXCollections.observableArrayList(
                    equipamentoService.listarEmprestados()
            );
            ObservableList<Equipamento> equipamentosEstoqueBaixo = FXCollections.observableArrayList(
                    equipamentoService.listarEstoqueBaixo()
            );
            ObservableList<Funcionario> funcionarios = FXCollections.observableArrayList(
                    funcionarioService.listarTodos()
            );
            tabelaAtrasos.setItems(emprestimosAtrasados);
            tabelaEstoqueBaixo.setItems(equipamentosEstoqueBaixo);
            tabelaEmprestimosDia.setItems(emprestimosDoDia);

            atualizarEstatisticas(emprestimosAtivos, emprestimosAtrasados, funcionarios, equipamentos, equipamentosEmprestados, equipamentosEstoqueBaixo);
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar empréstimos", e.getMessage());
        }
    }



    private void atualizarEstatisticas(ObservableList<Emprestimo> listaEmprestimosAtivos,
                                       ObservableList<Emprestimo> listaEmprestimosAtrasados,
                                       ObservableList<Funcionario> listaFuncionarios,
                                       ObservableList<Equipamento> listaEquipamentos,
                                       ObservableList<Equipamento> listaEquipamentosEmprestados,
                                       ObservableList<Equipamento> listaEquipamentosEstoque) {
        // Total de empréstimos
        totalFuncionariosLabel.setText(String.valueOf(listaFuncionarios.size()));
        totalEmprestimosAtivosLabel.setText(String.valueOf(listaEmprestimosAtivos.size()));
        totalEmprestimosAtrasadosLabel.setText(String.valueOf(listaEmprestimosAtrasados.size()));
        totalEquipamentosLabel.setText(String.valueOf(listaEquipamentos.size()));
        totalEquipamentosEmprestadosLabel.setText(String.valueOf(listaEquipamentosEmprestados.size()));
        totalEquipamentosEstoqueLabel.setText(String.valueOf(listaEquipamentosEstoque.size()));
    }

    private void configurarColunasAtrasados() {
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

    private void configurarColunasEstoqueBaixo() {
        colunaIdEstoque.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(data.getValue().getId())
                ));

        colunaEquipamentoEstoque.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDescricao()
                ));

        colunaQuantidade.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getQuantidadeEmprestimo()).asObject()
        );

        colunaQuantidadeEstoque.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getQuantidadeEstoque()).asObject()
        );

        colunaQuantidadeMinima.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getQuantidadeMinima()).asObject()
        );

    }

    private void configurarColunasEmprestimosDoDia() {
        colunaIdDia.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(data.getValue().getId())
                ));

        colunaFuncionarioDia.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getNomeFuncionario()
                ));

        colunaEquipamentoDia.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDescricaoEquipamento()
                ));

        colunaQuantidadeDia.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getQuantidadeEmprestimo()).asObject()
        );

        colunaDataSaidaDia.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        DateUtils.formatarDataHora(data.getValue().getDataSaida())
                ));

        colunaDataPrevistaDia.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        DateUtils.formatarDataHora(data.getValue().getDataRetornoPrevista())
                ));

        colunaStatusDia.setCellValueFactory(data -> {
            LocalDateTime dataPrevista = data.getValue().getDataRetornoPrevista();
            String status = LocalDateTime.now().isAfter(dataPrevista) ?
                    "Atrasado" : "Em dia";
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        // Configuração do estilo da coluna de status
        colunaStatusDia.setCellFactory(column -> new TableCell<Emprestimo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Em dia":
                            setStyle("-fx-text-fill: #2e7d32;"); // Verde
                            break;
                        case "Atrasado":
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

}
