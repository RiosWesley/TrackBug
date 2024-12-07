package trackbug.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import trackbug.model.entity.Equipamento;
import trackbug.model.entity.LogEquipamento;
import trackbug.model.service.EquipamentoService;
import trackbug.model.service.LogEquipamentoService;
import trackbug.util.AlertHelper;
import trackbug.util.ConnectionFactory;
import trackbug.util.ValidationHelper;
import trackbug.util.SessionManager;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EditarEquipamentoController implements Initializable {

    @FXML private TextField codigoField;
    @FXML private TextField descricaoField;
    @FXML private DatePicker dataCompraField;
    @FXML private ComboBox<String> tipoItemCombo;
    @FXML private ComboBox<String> tipoUsoCombo;
    @FXML private TextField quantidadeAtualField;
    @FXML private TextField quantidadeMinimaField;
    @FXML private CheckBox medidasCheckBox;
    @FXML private VBox medidasContainer;
    @FXML private TextField pesoField;
    @FXML private TextField larguraField;
    @FXML private TextField comprimentoField;
    @FXML private Label mensagemErro;

    private final EquipamentoService equipamentoService;
    private final LogEquipamentoService logService;
    private Equipamento equipamentoOriginal;
    private Stage dialogStage;

    public EditarEquipamentoController() {
        this.equipamentoService = new EquipamentoService();
        this.logService = new LogEquipamentoService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCampos();
        configurarValidacoes();
        configurarBindings();
    }

    private void configurarCampos() {
        // Configurar ComboBoxes
        tipoItemCombo.setItems(FXCollections.observableArrayList(
                "Emprestável", "Consumível"
        ));

        tipoUsoCombo.setItems(FXCollections.observableArrayList(
                "Reutilizável", "Uso Único"
        ));

        // Configurar formatação de números
        configurarCampoNumerico(quantidadeAtualField);
        configurarCampoNumerico(quantidadeMinimaField);
        configurarCampoDecimal(pesoField);
        configurarCampoDecimal(larguraField);
        configurarCampoDecimal(comprimentoField);
    }

    private void configurarValidacoes() {
        // Validação em tempo real da descrição
        descricaoField.textProperty().addListener((obs, old, novo) -> {
            if (novo != null && novo.length() > 200) {
                descricaoField.setText(old);
            }
        });

        // Validação da data
        dataCompraField.valueProperty().addListener((obs, old, novo) -> {
            if (novo != null && novo.isAfter(LocalDate.now())) {
                dataCompraField.setValue(old);
                mostrarErro("Data inválida", "A data não pode ser futura");
            }
        });
    }

    private void configurarBindings() {
        // Vincular visibilidade do container de medidas ao checkbox
        medidasContainer.visibleProperty().bind(medidasCheckBox.selectedProperty());
        medidasContainer.managedProperty().bind(medidasCheckBox.selectedProperty());
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamentoOriginal = equipamento;

        // Preencher campos com dados do equipamento
        codigoField.setText(equipamento.getId());
        descricaoField.setText(equipamento.getDescricao());
        dataCompraField.setValue(equipamento.getDataCompra());
        tipoItemCombo.setValue(equipamento.isTipo() ? "Consumível" : "Emprestável");
        tipoUsoCombo.setValue(equipamento.getTipoUso());
        quantidadeAtualField.setText(String.valueOf(equipamento.getQuantidadeAtual()));
        quantidadeMinimaField.setText(String.valueOf(equipamento.getQuantidadeMinima()));

        // Configurar medidas
        boolean temMedidas = equipamento.getPeso() > 0 ||
                equipamento.getLargura() > 0 ||
                equipamento.getComprimento() > 0;

        medidasCheckBox.setSelected(temMedidas);
        if (temMedidas) {
            pesoField.setText(String.valueOf(equipamento.getPeso()));
            larguraField.setText(String.valueOf(equipamento.getLargura()));
            comprimentoField.setText(String.valueOf(equipamento.getComprimento()));
        } else {
            pesoField.clear();
            larguraField.clear();
            comprimentoField.clear();
        }
    }

    @FXML
    private void salvarAlteracoes() {
        if (!validarCampos()) {
            return;
        }

        try {
            Equipamento equipamentoAtualizado = criarEquipamentoAtualizado();
            equipamentoService.editar(equipamentoAtualizado);
            registrarLogAlteracoes(equipamentoAtualizado);
            ConnectionFactory.exportarBancoDeDados("BACKUP.2024");
            AlertHelper.showSuccess("Equipamento atualizado com sucesso!");
            fecharJanela();

        } catch (Exception e) {
            AlertHelper.showError("Erro ao atualizar equipamento", e.getMessage());
        }
    }

    private Equipamento criarEquipamentoAtualizado() {
        Equipamento equipamento = new Equipamento();
        equipamento.setId(codigoField.getText());
        equipamento.setDescricao(descricaoField.getText().trim());
        equipamento.setDataCompra(dataCompraField.getValue());
        equipamento.setTipo("Consumível".equals(tipoItemCombo.getValue()));
        equipamento.setTipoUso(tipoUsoCombo.getValue());
        equipamento.setQuantidadeAtual(Integer.parseInt(quantidadeAtualField.getText().trim()));
        equipamento.setQuantidadeEstoque(Integer.parseInt(quantidadeAtualField.getText().trim()));
        equipamento.setQuantidadeMinima(Integer.parseInt(quantidadeMinimaField.getText().trim()));

        // Inicializar medidas com 0
        equipamento.setPeso(0);
        equipamento.setLargura(0);
        equipamento.setComprimento(0);

        // Atualizar medidas se o checkbox estiver selecionado e os campos preenchidos
        if (medidasCheckBox.isSelected()) {
            String pesoText = pesoField.getText().trim();
            if (!pesoText.isEmpty()) {
                equipamento.setPeso(Double.parseDouble(pesoText));
            }

            String larguraText = larguraField.getText().trim();
            if (!larguraText.isEmpty()) {
                equipamento.setLargura(Double.parseDouble(larguraText));
            }

            String comprimentoText = comprimentoField.getText().trim();
            if (!comprimentoText.isEmpty()) {
                equipamento.setComprimento(Double.parseDouble(comprimentoText));
            }
        }

        return equipamento;
    }

    private void registrarLogAlteracoes(Equipamento equipamentoAtualizado) {
        try {
            StringBuilder detalhes = new StringBuilder();
            String usuarioNome = SessionManager.getUsuarioLogado().getNome();

            // Comparar alterações
            if (!equipamentoOriginal.getDescricao().equals(equipamentoAtualizado.getDescricao())) {
                detalhes.append("Descrição alterada de '")
                        .append(equipamentoOriginal.getDescricao())
                        .append("' para '")
                        .append(equipamentoAtualizado.getDescricao())
                        .append("'\n");
            }

            if (equipamentoOriginal.getQuantidadeAtual() != equipamentoAtualizado.getQuantidadeAtual()) {
                detalhes.append("Quantidade alterada de ")
                        .append(equipamentoOriginal.getQuantidadeAtual())
                        .append(" para ")
                        .append(equipamentoAtualizado.getQuantidadeAtual())
                        .append("\n");
            }

            // Se houver alterações, registrar no log
            if (detalhes.length() > 0) {
                LogEquipamento log = new LogEquipamento();
                log.setIdEquipamento(equipamentoAtualizado.getId());
                log.setAcao("EDICAO");
                log.setDescricao("Equipamento editado por " + usuarioNome);
                log.setDetalhes(detalhes.toString());

                logService.registrarLog(log);
            }
        } catch (Exception e) {
            // Apenas log do erro, não impede a atualização do equipamento
            System.err.println("Erro ao registrar log: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        StringBuilder erros = new StringBuilder();

        if (ValidationHelper.isNullOrEmpty(descricaoField.getText())) {
            erros.append("A descrição é obrigatória\n");
        }

        if (dataCompraField.getValue() == null) {
            erros.append("A data de compra é obrigatória\n");
        }

        try {
            int quantidadeAtual = Integer.parseInt(quantidadeAtualField.getText().trim());
            int quantidadeMinima = Integer.parseInt(quantidadeMinimaField.getText().trim());

            if (quantidadeAtual < 0) {
                erros.append("A quantidade atual não pode ser negativa\n");
            }
            if (quantidadeMinima < 0) {
                erros.append("A quantidade mínima não pode ser negativa\n");
            }
            if (quantidadeMinima > quantidadeAtual) {
                erros.append("A quantidade mínima não pode ser maior que a quantidade atual\n");
            }
        } catch (NumberFormatException e) {
            erros.append("As quantidades devem ser números válidos\n");
        }

        if (medidasCheckBox.isSelected()) {
            // Validar campos de medidas apenas se estiverem preenchidos
            String pesoText = pesoField.getText().trim();
            String larguraText = larguraField.getText().trim();
            String comprimentoText = comprimentoField.getText().trim();

            if (!pesoText.isEmpty()) {
                try {
                    double peso = Double.parseDouble(pesoText);
                    if (peso < 0) {
                        erros.append("O peso não pode ser negativo\n");
                    }
                } catch (NumberFormatException e) {
                    erros.append("Peso deve ser um número válido\n");
                }
            }

            if (!larguraText.isEmpty()) {
                try {
                    double largura = Double.parseDouble(larguraText);
                    if (largura < 0) {
                        erros.append("A largura não pode ser negativa\n");
                    }
                } catch (NumberFormatException e) {
                    erros.append("Largura deve ser um número válido\n");
                }
            }

            if (!comprimentoText.isEmpty()) {
                try {
                    double comprimento = Double.parseDouble(comprimentoText);
                    if (comprimento < 0) {
                        erros.append("O comprimento não pode ser negativo\n");
                    }
                } catch (NumberFormatException e) {
                    erros.append("Comprimento deve ser um número válido\n");
                }
            }
        }

        if (erros.length() > 0) {
            mostrarErro("Campos Inválidos", erros.toString());
            return false;
        }

        return true;
    }

    private boolean validarMedida(String valor, String campo) {
        if (!valor.isEmpty()) {
            try {
                double medida = Double.parseDouble(valor);
                return medida >= 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private void configurarCampoNumerico(TextField campo) {
        campo.textProperty().addListener((obs, old, novo) -> {
            if (!novo.matches("\\d*")) {
                campo.setText(novo.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void configurarCampoDecimal(TextField campo) {
        campo.textProperty().addListener((obs, old, novo) -> {
            if (!novo.matches("\\d*\\.?\\d*")) {
                campo.setText(old);
            }
        });
    }

    private void mostrarErro(String titulo, String mensagem) {
        mensagemErro.setText(mensagem);
        mensagemErro.setVisible(true);
    }

    @FXML
    private void cancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = (Stage) codigoField.getScene().getWindow();
        stage.close();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}