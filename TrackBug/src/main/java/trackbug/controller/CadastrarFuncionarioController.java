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
    }

    @FXML
    private void salvarFuncionario() {
        try {
            // Validar campos
            if (!validarCampos()) {
                return;
            }

            // Criar objeto funcionário
            Funcionario funcionario = new Funcionario();
            funcionario.setId(idField.getText().trim());
            funcionario.setNome(nomeField.getText().trim());
            funcionario.setFuncao(funcaoField.getText().trim());
            funcionario.setDataAdmissao(dataAdmissaoField.getValue().toString());

            // Salvar usando o service
            funcionarioService.cadastrarFuncionario(funcionario);

            // Mostrar mensagem de sucesso
            mostrarMensagem("Sucesso", "Funcionário cadastrado com sucesso!", Alert.AlertType.INFORMATION);

            // Limpar formulário
            limparFormulario();

        } catch (IllegalArgumentException e) {
            mostrarMensagem("Erro de Validação", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) {
            mostrarMensagem("Erro", "Erro ao cadastrar funcionário: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void cancelar() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }

    private boolean validarCampos() {
        StringBuilder erros = new StringBuilder();

        if (idField.getText().trim().isEmpty()) {
            erros.append("- O código do funcionário é obrigatório\n");
        }

        if (nomeField.getText().trim().isEmpty()) {
            erros.append("- O nome é obrigatório\n");
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

    private void limparFormulario() {
        idField.clear();
        nomeField.clear();
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