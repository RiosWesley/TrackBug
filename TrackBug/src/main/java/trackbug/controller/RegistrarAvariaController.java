// src/main/java/trackbug/controller/RegistrarAvariaController.java
package trackbug.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import trackbug.model.entity.Avaria;
import trackbug.model.entity.Equipamento;
import trackbug.model.service.AvariaService;
import trackbug.util.AlertHelper;
import trackbug.util.ConnectionFactory;
import trackbug.util.ValidationHelper;

import java.net.URL;
import java.util.ResourceBundle;

public class RegistrarAvariaController implements Initializable {
    @FXML private Label equipamentoLabel;
    @FXML private Label quantidadeDisponivel;
    @FXML private TextField quantidadeField;
    @FXML private TextArea descricaoArea;

    private final AvariaService avariaService;
    private Equipamento equipamento;

    public RegistrarAvariaController() {
        this.avariaService = new AvariaService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCampos();
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
        equipamentoLabel.setText(equipamento.getDescricao());
        atualizarInfoQuantidade();
    }

    private void configurarCampos() {
        // Permitir apenas números na quantidade
        quantidadeField.textProperty().addListener((obs, old, novo) -> {
            if (!novo.matches("\\d*")) {
                quantidadeField.setText(old);
            }
            atualizarInfoQuantidade();
        });

        // Atualizar info de quantidade disponível
        quantidadeField.focusedProperty().addListener((obs, old, novo) -> {
            if (!novo) {
                atualizarInfoQuantidade();
            }
        });
    }

    private void atualizarInfoQuantidade() {
        if (equipamento != null) {
            int disponivel = equipamento.getQuantidadeAtual();
            int quantidade = parseQuantidade();

            if (quantidade > disponivel) {
                quantidadeDisponivel.setText(
                        String.format("Quantidade indisponível! Máximo: %d", disponivel));
                quantidadeDisponivel.getStyleClass().add("alerta");
            } else {
                quantidadeDisponivel.setText(
                        String.format("Quantidade disponível: %d", disponivel));
                quantidadeDisponivel.getStyleClass().remove("alerta");
            }
        }
    }

    private int parseQuantidade() {
        try {
            return Integer.parseInt(quantidadeField.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @FXML
    private void registrarAvaria() {
        try {
            if (!validarCampos()) {
                return;
            }

            Avaria avaria = new Avaria();
            avaria.setIdEquipamento(equipamento.getId());
            avaria.setQuantidade(parseQuantidade());
            avaria.setDescricao(descricaoArea.getText().trim());

            avariaService.registrarAvaria(avaria);
            ConnectionFactory.exportarBancoDeDados("BACKUP.2024");

            AlertHelper.showSuccess("Avaria registrada com sucesso!");
            fecharJanela();

        } catch (Exception e) {
            AlertHelper.showError("Erro ao registrar avaria", e.getMessage());
        }
    }

    private boolean validarCampos() {
        StringBuilder erros = new StringBuilder();

        if (ValidationHelper.isNullOrEmpty(quantidadeField.getText())) {
            erros.append("Quantidade é obrigatória\n");
        } else {
            int quantidade = parseQuantidade();
            if (quantidade <= 0) {
                erros.append("Quantidade deve ser maior que zero\n");
            }
            if (quantidade > equipamento.getQuantidadeAtual()) {
                erros.append("Quantidade maior que disponível\n");
            }
        }

        if (ValidationHelper.isNullOrEmpty(descricaoArea.getText())) {
            erros.append("Descrição é obrigatória\n");
        }

        if (erros.length() > 0) {
            AlertHelper.showWarning("Campos Inválidos", erros.toString());
            return false;
        }

        return true;
    }

    @FXML
    private void cancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = (Stage) quantidadeField.getScene().getWindow();
        stage.close();
    }
}