package trackbug.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import trackbug.model.entity.Emprestimo;
import trackbug.model.service.EmprestimoService;
import trackbug.model.service.FuncionarioService;
import trackbug.model.service.EquipamentoService;
import trackbug.util.ConnectionFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DevolucaoController {

    private int idEmprestimoSelecionado;

    @FXML
    private ComboBox<String> emprestimoCombo;

    @FXML
    private TextArea detalhesEmprestimo;

    private final EmprestimoService emprestimoService;
    private final FuncionarioService funcionarioService;
    private final EquipamentoService equipamentoService;
    private final DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DevolucaoController() {
        this.emprestimoService = new EmprestimoService();
        this.funcionarioService = new FuncionarioService();
        this.equipamentoService = new EquipamentoService();
    }

    @FXML
    private void initialize() {
        // Configurar listener para o ComboBox
        emprestimoCombo.setOnAction(e -> {
            String selectedValue = emprestimoCombo.getValue();
            if (selectedValue != null) {
                // Extrair o ID do valor selecionado e armazená-lo na variável
                idEmprestimoSelecionado = Integer.parseInt(selectedValue.split(" - ")[0]);
                mostrarDetalhesEmprestimo(idEmprestimoSelecionado);
            }
        });

        // Carregar empréstimos ativos
        carregarEmprestimosAtivos();
    }

    private void carregarEmprestimosAtivos() {
        try {
            List<Emprestimo> emprestimosAtivos = emprestimoService.listarEmprestimosAtivos();
            emprestimoCombo.setItems(FXCollections.observableArrayList(
                    emprestimosAtivos.stream()
                            .map(emprestimo -> emprestimo.getId() + " - " + emprestimo.getDescricaoEquipamento())
                            .toList()
            ));
        } catch (Exception e) {
            mostrarErro("Erro ao carregar empréstimos", e.getMessage());
        }
    }

    private void mostrarDetalhesEmprestimo(int emprestimoId) {
        try {
            Emprestimo emprestimo = emprestimoService.buscarPorId(emprestimoId);
            if (emprestimo == null) {
                detalhesEmprestimo.setText("Empréstimo não encontrado.");
                return;
            }

            String nomeFuncionario = funcionarioService.buscarPorId(emprestimo.getIdFuncionario()).getNome();
            String nomeEquipamento = equipamentoService.buscarPorId(emprestimo.getIdEquipamento()).getDescricao();

            String detalhes = String.format(
                    "Funcionário: %s\n\n" +
                            "Equipamento: %s\n" +
                            "Quantidade: %d\n\n" +
                            "Data de Saída: %s\n" +
                            "Data Prevista de Retorno: %s\n\n" +
                            "Observações:\n%s",
                    nomeFuncionario,
                    nomeEquipamento,
                    emprestimo.getQuantidadeEmprestimo(),
                    emprestimo.getDataSaida().format(formatador),
                    emprestimo.getDataRetornoPrevista().format(formatador),
                    emprestimo.getObservacoes()
            );

            detalhesEmprestimo.setText(detalhes);
        } catch (Exception e) {
            mostrarErro("Erro ao carregar detalhes", e.getMessage());
        }
    }

    @FXML
    private void confirmarDevolucao() {
        String emprestimoId = emprestimoCombo.getValue();
        if (emprestimoId == null) {
            mostrarAlerta("Selecione um empréstimo",
                    "Por favor, selecione um empréstimo para registrar a devolução.");
            return;
        }

        try {
            emprestimoService.registrarDevolucao(idEmprestimoSelecionado);
            mostrarSucesso("Devolução registrada com sucesso!");
            ConnectionFactory.exportarBancoDeDados("BACKUP.2024");
            limparFormulario();
            carregarEmprestimosAtivos();
        } catch (Exception e) {
            mostrarErro("Erro ao registrar devolução", e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        limparFormulario();
    }

    private void limparFormulario() {
        emprestimoCombo.setValue(null);
        detalhesEmprestimo.clear();
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText("Operação realizada");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}