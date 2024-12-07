package trackbug.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import trackbug.model.entity.Funcionario;
import trackbug.model.service.FuncionarioService;
import trackbug.util.AlertHelper;
import trackbug.util.ConnectionFactory;
import trackbug.util.ValidationHelper;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EditarFuncionarioController implements Initializable {

    @FXML private TextField codigoField;
    @FXML private TextField nomeField;
    @FXML private TextField funcaoField;
    @FXML private DatePicker dataAdmissaoField;
    @FXML private Label mensagemErro;

    private final FuncionarioService funcionarioService;
    private Funcionario funcionario;
    private boolean modoEdicao;

    public EditarFuncionarioController() {
        this.funcionarioService = new FuncionarioService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCampos();
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
        this.modoEdicao = funcionario != null;
        atualizarTitulo();
        preencherCampos();
    }

    @FXML
    private void salvar() {
        try {
            if (validarCampos()) {
                Funcionario func = modoEdicao ? funcionario : new Funcionario();
                func.setId(codigoField.getText());
                func.setNome(nomeField.getText());
                func.setFuncao(funcaoField.getText());
                func.setDataAdmissao(dataAdmissaoField.getValue());

                if (modoEdicao) {
                    funcionarioService.atualizar(func);
                    AlertHelper.showSuccess("Funcionário atualizado com sucesso!");
                } else {
                    funcionarioService.criar(func);
                    AlertHelper.showSuccess("Funcionário cadastrado com sucesso!");
                }
                ConnectionFactory.exportarBancoDeDados("BACKUP.2024");
                fecharJanela();
            }
        } catch (Exception e) {
            mostrarErro("Erro ao " + (modoEdicao ? "atualizar" : "cadastrar") +
                    " funcionário: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        try {
            StringBuilder erros = new StringBuilder();

            if (ValidationHelper.isNullOrEmpty(codigoField.getText())) {
                erros.append("O código é obrigatório\n");
            } else if (!modoEdicao && funcionarioService.existePorId(codigoField.getText())) {
                erros.append("Já existe um funcionário com este código\n");
            }

            if (ValidationHelper.isNullOrEmpty(nomeField.getText())) {
                erros.append("O nome é obrigatório\n");
            }

            if (ValidationHelper.isNullOrEmpty(funcaoField.getText())) {
                erros.append("A função é obrigatória\n");
            }

            if (dataAdmissaoField.getValue() == null) {
                erros.append("A data de admissão é obrigatória\n");
            } else if (dataAdmissaoField.getValue().isAfter(LocalDate.now())) {
                erros.append("A data de admissão não pode ser futura\n");
            }

            if (erros.length() > 0) {
                mostrarErro(erros.toString());
                return false;
            }

            return true;
        } catch (Exception e) {
            mostrarErro("Erro ao validar campos: " + e.getMessage());
            return false;
        }
    }

    private void configurarCampos() {
        // Impedir entrada de números no nome
        nomeField.textProperty().addListener((obs, old, novo) -> {
            if (novo != null && !novo.matches("\\sa-zA-ZÀ-ÿ\\s*")) {
                nomeField.setText(novo.replaceAll("[^\\sa-zA-ZÀ-ÿ\\s]", ""));
            }
        });

        // Formatar código automaticamente (apenas letras maiúsculas e números)
        codigoField.textProperty().addListener((obs, old, novo) -> {
            if (novo != null) {
                codigoField.setText(novo.toUpperCase().replaceAll("[^A-Z0-9]", ""));
            }
        });
    }

    private void atualizarTitulo() {
        if (modoEdicao) {
            // Atualizar labels para modo de edição
            mensagemErro.setText("Editar Funcionário");
        } else {
            // Atualizar labels para modo de criação
            mensagemErro.setText("Novo Funcionário");
        }
    }

    private void preencherCampos() {
        if (modoEdicao && funcionario != null) {
            codigoField.setText(funcionario.getId());
            nomeField.setText(funcionario.getNome());
            funcaoField.setText(funcionario.getFuncao());
            dataAdmissaoField.setValue(funcionario.getDataAdmissao());
        } else {
            dataAdmissaoField.setValue(LocalDate.now());
        }
    }

    @FXML
    private void cancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = (Stage) codigoField.getScene().getWindow();
        stage.close();
    }

    private void mostrarErro(String erro) {
        mensagemErro.setText(erro);
        mensagemErro.setVisible(true);
    }
}