package trackbug.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import trackbug.model.entity.Equipamento;
import trackbug.model.service.EquipamentoService;
import trackbug.util.AlertHelper;
import trackbug.util.ValidationHelper;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RegistrarEquipamentoController implements Initializable {

    @FXML private TextField codigoField;
    @FXML private TextField descricaoField;
    @FXML private DatePicker dataCompraField;
    @FXML private ComboBox<String> tipoItemCombo;
    @FXML private ComboBox<String> tipoUsoCombo;
    @FXML private TextField quantidadeInicialField;
    @FXML private TextField quantidadeMinimaField;
    @FXML private CheckBox medidasCheckBox;
    @FXML private VBox medidasContainer;
    @FXML private TextField pesoField;
    @FXML private TextField larguraField;
    @FXML private TextField comprimentoField;

    private final EquipamentoService equipamentoService;

    public RegistrarEquipamentoController() {
        this.equipamentoService = new EquipamentoService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCampos();
        configurarValidacoes();
        configurarBindings();
    }

    @FXML
    private void salvarEquipamento() {
        try {
            if (validarCampos()) {
                Equipamento equipamento = criarEquipamento();
                equipamentoService.salvar(equipamento);

                AlertHelper.showSuccess("Equipamento registrado com sucesso!");
                limparFormulario();
            }
        } catch (Exception e) {
            AlertHelper.showError("Erro ao registrar equipamento", e.getMessage());
        }
    }

    private Equipamento criarEquipamento() {
        Equipamento equipamento = new Equipamento();
        equipamento.setId(codigoField.getText().trim());
        equipamento.setDescricao(descricaoField.getText().trim());
        equipamento.setDataCompra(dataCompraField.getValue());
        equipamento.setTipo("Consumível".equals(tipoItemCombo.getValue()));
        equipamento.setTipoUso(tipoUsoCombo.getValue());

        int quantidadeInicial = Integer.parseInt(quantidadeInicialField.getText());
        equipamento.setQuantidadeAtual(quantidadeInicial);
        equipamento.setQuantidadeEstoque(quantidadeInicial);
        equipamento.setQuantidadeMinima(Integer.parseInt(quantidadeMinimaField.getText()));

        if (medidasCheckBox.isSelected()) {
            if (!ValidationHelper.isNullOrEmpty(pesoField.getText())) {
                equipamento.setPeso(Double.parseDouble(pesoField.getText()));
            }
            if (!ValidationHelper.isNullOrEmpty(larguraField.getText())) {
                equipamento.setLargura(Double.parseDouble(larguraField.getText()));
            }
            if (!ValidationHelper.isNullOrEmpty(comprimentoField.getText())) {
                equipamento.setComprimento(Double.parseDouble(comprimentoField.getText()));
            }
        }

        return equipamento;
    }

    private boolean validarCampos() {
        try {
            StringBuilder erros = new StringBuilder();

            if (ValidationHelper.isNullOrEmpty(codigoField.getText())) {
                erros.append("O código é obrigatório\n");
            } else if (equipamentoService.existePorId(codigoField.getText().trim())) {
                erros.append("Já existe um equipamento com este código\n");
            }

            if (ValidationHelper.isNullOrEmpty(descricaoField.getText())) {
                erros.append("A descrição é obrigatória\n");
            }

            if (dataCompraField.getValue() == null) {
                erros.append("A data de compra é obrigatória\n");
            } else if (dataCompraField.getValue().isAfter(LocalDate.now())) {
                erros.append("A data de compra não pode ser futura\n");
            }

            if (tipoItemCombo.getValue() == null) {
                erros.append("O tipo de item é obrigatório\n");
            }

            if (tipoUsoCombo.getValue() == null) {
                erros.append("O tipo de uso é obrigatório\n");
            }

            if (ValidationHelper.isNullOrEmpty(quantidadeInicialField.getText())) {
                erros.append("A quantidade inicial é obrigatória\n");
            }

            if (ValidationHelper.isNullOrEmpty(quantidadeMinimaField.getText())) {
                erros.append("A quantidade mínima é obrigatória\n");
            }

            if (medidasCheckBox.isSelected()) {
                if (!ValidationHelper.isNullOrEmpty(pesoField.getText()) &&
                        !ValidationHelper.isNumeric(pesoField.getText())) {
                    erros.append("O peso deve ser um número válido\n");
                }
                if (!ValidationHelper.isNullOrEmpty(larguraField.getText()) &&
                        !ValidationHelper.isNumeric(larguraField.getText())) {
                    erros.append("A largura deve ser um número válido\n");
                }
                if (!ValidationHelper.isNullOrEmpty(comprimentoField.getText()) &&
                        !ValidationHelper.isNumeric(comprimentoField.getText())) {
                    erros.append("O comprimento deve ser um número válido\n");
                }
            }

            if (erros.length() > 0) {
                AlertHelper.showWarning("Campos inválidos", erros.toString());
                return false;
            }

            return true;
        } catch (Exception e) {
            AlertHelper.showError("Erro ao validar campos", e.getMessage());
            return false;
        }
    }

    private void configurarCampos() {
        tipoItemCombo.setItems(FXCollections.observableArrayList("Emprestável", "Consumível"));
        tipoUsoCombo.setItems(FXCollections.observableArrayList("Reutilizável", "Uso Único"));

        dataCompraField.setValue(LocalDate.now());

        // Configurar formatação e validação dos campos
        codigoField.textProperty().addListener((obs, old, novo) -> {
            if (novo != null) {
                codigoField.setText(novo.toUpperCase().replaceAll("[^A-Z0-9]", ""));
            }
        });

        quantidadeInicialField.textProperty().addListener((obs, old, novo) -> {
            if (!novo.matches("\\d*")) {
                quantidadeInicialField.setText(novo.replaceAll("[^\\d]", ""));
            }
        });

        quantidadeMinimaField.textProperty().addListener((obs, old, novo) -> {
            if (!novo.matches("\\d*")) {
                quantidadeMinimaField.setText(novo.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void configurarValidacoes() {
        // Validação para campos numéricos decimais
        configurarCampoDecimal(pesoField);
        configurarCampoDecimal(larguraField);
        configurarCampoDecimal(comprimentoField);
    }

    private void configurarCampoDecimal(TextField campo) {
        campo.textProperty().addListener((obs, old, novo) -> {
            if (!novo.matches("\\d*(\\.\\d*)?")) {
                campo.setText(old);
            }
        });
    }

    private void configurarBindings() {
        medidasContainer.visibleProperty().bind(medidasCheckBox.selectedProperty());
        medidasContainer.managedProperty().bind(medidasCheckBox.selectedProperty());
    }

    @FXML
    private void limparFormulario() {
        codigoField.clear();
        descricaoField.clear();
        dataCompraField.setValue(LocalDate.now());
        tipoItemCombo.setValue(null);
        tipoUsoCombo.setValue(null);
        quantidadeInicialField.clear();
        quantidadeMinimaField.clear();
        medidasCheckBox.setSelected(false);
        pesoField.clear();
        larguraField.clear();
        comprimentoField.clear();
    }
}