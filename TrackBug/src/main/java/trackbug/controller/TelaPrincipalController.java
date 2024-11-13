package trackbug.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import trackbug.model.entity.Emprestimo;
import trackbug.model.entity.Equipamento;
import trackbug.model.entity.Funcionario;
import trackbug.model.service.EmprestimoService;
import trackbug.model.service.EquipamentoService;
import trackbug.model.service.FuncionarioService;
import trackbug.util.AlertHelper;

import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TelaPrincipalController implements Initializable {

    @FXML private Label totalEmprestimosLabel;
    @FXML private Label totalFuncionariosLabel;
    @FXML private Label totalEquipamentosLabel;

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
        carregarEmprestimos();
    }

    private void carregarEmprestimos() {
        try {
            ObservableList<Emprestimo> emprestimos = FXCollections.observableArrayList(
                    emprestimoService.listarTodos()
            );
            ObservableList<Equipamento> equipamentos = FXCollections.observableArrayList(
                    equipamentoService.listarTodos()
            );
            ObservableList<Funcionario> funcionarios = FXCollections.observableArrayList(
                    funcionarioService.listarTodos()
            );
            atualizarEstatisticas(emprestimos, funcionarios, equipamentos);
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar empréstimos", e.getMessage());
        }
    }

    private void atualizarEstatisticas(ObservableList<Emprestimo> listaEmprestimos,
                                       ObservableList<Funcionario> listaFuncionarios,
                                       ObservableList<Equipamento> listaEquipamentos) {
        // Total de empréstimos
        totalFuncionariosLabel.setText(String.valueOf(listaFuncionarios.size()));
        totalEmprestimosLabel.setText(String.valueOf(listaEmprestimos.size()));
        totalEquipamentosLabel.setText(String.valueOf(listaEquipamentos.size()));
    }
}
