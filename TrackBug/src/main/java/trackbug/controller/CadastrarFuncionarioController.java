package trackbug.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import trackbug.model.entity.Funcionario;
import trackbug.model.service.FuncionarioService;
import java.time.LocalDate;

public class CadastrarFuncionarioController {
    @FXML
    private TextField idField;
    @FXML
    private TextField nomeField;
    @FXML
    private TextField cpfField;
    @FXML
    private TextField funcaoField;
    @FXML
    private DatePicker dataAdmissaoField;

    private final FuncionarioService funcionarioService;

    public CadastrarFuncionarioController() {
        this.funcionarioService = new FuncionarioService();
    }

    @FXML
    private void initialize() {
        // Configurações iniciais
        dataAdmissaoField.setValue(LocalDate.now());

        // Configurar validações em tempo real
        idField.textProperty().addListener((obs, old, novo) -> {
            if (!novo.matches("[A-Za-z0-9-]*")) {
                idField.setText(old);
            }
        });

        cpfField.textProperty().addListener((obs, old, novo) -> {
            if (!novo.matches("[0-9-]*")) {
                cpfField.setText(old);
            }
            else if (novo.length() > 11) {
                cpfField.setText(old);
            }
        });
    }

    @FXML
    private void salvarFuncionario() {
        try {
            if (!validarCampos()) {
                return;
            }

            Funcionario funcionario = new Funcionario();
            funcionario.setId(idField.getText().trim());
            funcionario.setNome(nomeField.getText().trim());
            funcionario.setCpf(cpfField.getText().trim());
            funcionario.setFuncao(funcaoField.getText().trim());
            funcionario.setDataAdmissao(dataAdmissaoField.getValue()); // Agora passa o LocalDate diretamente

            funcionarioService.cadastrarFuncionario(funcionario);

            mostrarMensagem("Sucesso", "Funcionário cadastrado com sucesso!", Alert.AlertType.INFORMATION);
            limparFormulario();
        } catch (IllegalArgumentException e) {
            mostrarMensagem("Erro de Validação", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) {
            mostrarMensagem("Erro", "Erro ao cadastrar funcionário: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validarCampos() {
        StringBuilder erros = new StringBuilder();
        if (idField.getText().trim().isEmpty()) {
            erros.append("- O código do funcionário é obrigatório\n");
        }
        if (nomeField.getText().trim().isEmpty()) {
            erros.append("- O nome é obrigatório\n");
        }
        if (cpfField.getText().trim().isEmpty()) {
            erros.append("- O cpf é obrigatório\n");
        } else if (cpfField.getText().trim().length() < 11) {
            erros.append("- O CPF deve ter 11 dígitos\n");
        }
        if (funcaoField.getText().trim().isEmpty()) {
            erros.append("- A função é obrigatória\n");
        }
        if (dataAdmissaoField.getValue() == null) {
            erros.append("- A data de admissão é obrigatória\n");
        }

        if (erros.length() > 0) {
            mostrarMensagem("Campos Incompletos", erros.toString(), Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    @FXML
    private void cancelar() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }

    private void limparFormulario() {
        idField.clear();
        nomeField.clear();
        cpfField.clear();
        funcaoField.clear();
        dataAdmissaoField.setValue(LocalDate.now());
    }

    private void mostrarMensagem(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}