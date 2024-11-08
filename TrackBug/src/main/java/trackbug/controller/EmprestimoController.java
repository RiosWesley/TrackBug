package trackbug.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import trackbug.model.entity.Equipamento;
import trackbug.model.entity.Funcionario;
import trackbug.model.entity.Emprestimo;
import trackbug.model.service.EmprestimoService;
import trackbug.model.service.EquipamentoService;
import trackbug.model.service.FuncionarioService;
import trackbug.util.AlertHelper;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class EmprestimoController implements Initializable {

    @FXML private ComboBox<Funcionario> funcionarioCombo;
    @FXML private ComboBox<Equipamento> equipamentoCombo;
    @FXML private TextField quantidadeField;
    @FXML private DatePicker dataDevolucao;
    @FXML private TextArea observacoes;
    @FXML private Label equipamentoInfoLabel;

    private final EmprestimoService emprestimoService;
    private final EquipamentoService equipamentoService;
    private final FuncionarioService funcionarioService;

    public EmprestimoController() {
        this.emprestimoService = new EmprestimoService();
        this.equipamentoService = new EquipamentoService();
        this.funcionarioService = new FuncionarioService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarComboBoxes();
        carregarDados();
        configurarListeners();
    }

    private void configurarComboBoxes() {
        // Configurar exibição dos itens nos ComboBoxes
        funcionarioCombo.setConverter(new StringConverter<Funcionario>() {
            @Override
            public String toString(Funcionario f) {
                return f != null ? f.getNome() : "";
            }

            @Override
            public Funcionario fromString(String string) {
                return null;
            }
        });

        equipamentoCombo.setConverter(new StringConverter<Equipamento>() {
            @Override
            public String toString(Equipamento e) {
                return e != null ? e.getDescricao() : "";
            }

            @Override
            public Equipamento fromString(String string) {
                return null;
            }
        });
    }

    private void carregarDados() {
        try {
            ObservableList<Funcionario> funcionarios =
                    FXCollections.observableArrayList(funcionarioService.listarTodos());
            funcionarioCombo.setItems(funcionarios);

            ObservableList<Equipamento> equipamentos =
                    FXCollections.observableArrayList(equipamentoService.listarDisponiveis());
            equipamentoCombo.setItems(equipamentos);
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar dados",
                    "Não foi possível carregar os dados necessários: " + e.getMessage());
        }
    }

    private void configurarListeners() {
        equipamentoCombo.setOnAction(e -> atualizarInfoEquipamento());

        // Listener para validação de quantidade
        quantidadeField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                quantidadeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void atualizarInfoEquipamento() {
        Equipamento equipamento = equipamentoCombo.getValue();
        if (equipamento != null) {
            equipamentoInfoLabel.setText(String.format(
                    "Disponível: %d unidades (Total: %d) - %s",
                    equipamento.getQuantidadeAtual(),
                    equipamento.getQuantidadeEstoque(),
                    equipamento.getTipoUso()
            ));

            // Desabilitar dataDevolucao para itens de uso único
            dataDevolucao.setDisable("Uso Único".equals(equipamento.getTipoUso()));
            if (dataDevolucao.isDisabled()) {
                dataDevolucao.setValue(LocalDate.now());
            }
        }
    }

    @FXML
    private void registrarEmprestimo() {
        if (!validarCampos()) {
            return;
        }

        try {
            Emprestimo emprestimo = criarEmprestimo();
            emprestimoService.realizarEmprestimo(emprestimo);

            AlertHelper.showSuccess("Empréstimo registrado com sucesso!");
            limparFormulario();
            carregarDados(); // Recarrega os dados para atualizar as quantidades
        } catch (Exception e) {
            AlertHelper.showError("Erro ao registrar empréstimo", e.getMessage());
        }
    }

    private Emprestimo criarEmprestimo() {
        Emprestimo emprestimo = new Emprestimo();

        emprestimo.setIdFuncionario(funcionarioCombo.getValue().getId());
        emprestimo.setIdEquipamento(equipamentoCombo.getValue().getId());
        emprestimo.setQuantidadeEmprestimo(Integer.parseInt(quantidadeField.getText()));

        LocalDateTime dataHoraDevolucao = dataDevolucao.getValue().atTime(LocalTime.now());
        emprestimo.setDataRetornoPrevista(dataHoraDevolucao);

        emprestimo.setObservacoes(observacoes.getText());
        emprestimo.setAtivo(true);
        emprestimo.setDataSaida(LocalDateTime.now());

        // Define o tipo de operação como SAIDA
        emprestimo.setTipoOperacao("SAIDA");

        // Define se é uso único baseado no equipamento
        emprestimo.setUsoUnico(equipamentoCombo.getValue().getTipoUso().equals("Uso Único"));

        return emprestimo;
    }

    private boolean validarCampos() {
        // Implementação da validação dos campos
        if (funcionarioCombo.getValue() == null || equipamentoCombo.getValue() == null ||
                quantidadeField.getText().isEmpty()) {
            AlertHelper.showWarning("Campos obrigatórios",
                    "Por favor, preencha todos os campos obrigatórios.");
            return false;
        }

        // Validação específica para equipamentos reutilizáveis
        Equipamento equipamento = equipamentoCombo.getValue();
        if (!equipamento.getTipoUso().equals("Uso Único")) {
            if (dataDevolucao.getValue() == null) {
                AlertHelper.showWarning("Data obrigatória",
                        "Para itens emprestáveis, é necessário informar a data de devolução.");
                return false;
            }
            if (dataDevolucao.getValue().isBefore(LocalDate.now())) {
                AlertHelper.showWarning("Data inválida",
                        "A data de devolução não pode ser anterior à data atual.");
                return false;
            }
        }

        // Validação da quantidade
        try {
            int quantidade = Integer.parseInt(quantidadeField.getText());
            if (quantidade <= 0) {
                AlertHelper.showWarning("Quantidade inválida",
                        "A quantidade deve ser maior que zero.");
                return false;
            }
            if (quantidade > equipamento.getQuantidadeAtual()) {
                AlertHelper.showWarning("Quantidade indisponível",
                        "A quantidade solicitada é maior que a disponível.");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Quantidade inválida",
                    "Por favor, insira um número válido para a quantidade.");
            return false;
        }

        return true;
    }



    @FXML
    private void limparFormulario() {
        funcionarioCombo.setValue(null);
        equipamentoCombo.setValue(null);
        quantidadeField.clear();
        dataDevolucao.setValue(null);
        observacoes.clear();
        equipamentoInfoLabel.setText("");
    }
}