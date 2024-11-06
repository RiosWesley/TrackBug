package trackbug.Forms;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class CadastrarFuncionarioForm extends VBox {
    private TextField idField;
    private TextField nomeField;
    private TextField funcaoField;
    private DatePicker dataAdmissaoField;

    public CadastrarFuncionarioForm() {
        setSpacing(30);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: white;");

        // Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        Label titulo = new Label("Cadastrar Novo Funcionário");
        titulo.setStyle(
                "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #1a237e; " +
                        "-fx-font-family: 'Segoe UI';"
        );
        Label subtitulo = new Label("Preencha os dados do novo funcionário");
        subtitulo.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-text-fill: #757575; " +
                        "-fx-font-family: 'Segoe UI';"
        );
        header.getChildren().addAll(titulo, subtitulo);

        // Container principal
        VBox formContainer = new VBox(20);
        formContainer.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-padding: 20px; " +
                        "-fx-background-radius: 5px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        );
        formContainer.setMaxWidth(600);

        // Grid para os campos
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // Inicializar campos
        idField = criarCampoTexto("Digite o código do funcionário");
        nomeField = criarCampoTexto("Digite o nome completo");
        funcaoField = criarCampoTexto("Digite o cargo/função");
        dataAdmissaoField = new DatePicker(LocalDate.now());
        estilizarDatePicker(dataAdmissaoField);
        dataAdmissaoField.setPromptText("Selecione a data de admissão");

        // Adicionar campos ao grid
        adicionarCampoAoGrid(grid, "Código:", idField, 0);
        adicionarCampoAoGrid(grid, "Nome:", nomeField, 1);
        adicionarCampoAoGrid(grid, "Função:", funcaoField, 2);
        adicionarCampoAoGrid(grid, "Data de Admissão:", dataAdmissaoField, 3);

        // Botões
        HBox botoesBox = new HBox(15);
        botoesBox.setAlignment(Pos.CENTER);

        Button btnSalvar = criarBotao("Salvar", "CONFIRMAR");
        Button btnCancelar = criarBotao("Cancelar", "CANCELAR");

        botoesBox.getChildren().addAll(btnSalvar, btnCancelar);

        // Adicionar ao formulário
        formContainer.getChildren().addAll(grid, botoesBox);
        getChildren().addAll(header, formContainer);

        // Ações dos botões
        btnSalvar.setOnAction(e -> salvarFuncionario());
        btnCancelar.setOnAction(e -> limparFormulario());
    }

    private TextField criarCampoTexto(String prompt) {
        TextField campo = new TextField();
        campo.setPromptText(prompt);
        campo.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );
        return campo;
    }

    private void estilizarDatePicker(DatePicker campo) {
        campo.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px;"
        );
    }

    private void adicionarCampoAoGrid(GridPane grid, String labelText, Control campo, int linha) {
        Label label = new Label(labelText);
        label.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: #2c3e50;"
        );
        grid.add(label, 0, linha);
        grid.add(campo, 1, linha);
        GridPane.setColumnSpan(campo, 2);
    }

    private Button criarBotao(String texto, String tipo) {
        Button btn = new Button(texto);
        final String baseStyle = "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; " +
                "-fx-padding: 10px 20px; -fx-cursor: hand; -fx-font-weight: bold;";

        if (tipo.equals("CONFIRMAR")) {
            btn.setStyle(baseStyle + "-fx-background-color: #1a237e; -fx-text-fill: white;");
            btn.setOnMouseEntered(e ->
                    btn.setStyle(baseStyle + "-fx-background-color: #283593; -fx-text-fill: white;"));
            btn.setOnMouseExited(e ->
                    btn.setStyle(baseStyle + "-fx-background-color: #1a237e; -fx-text-fill: white;"));
        } else {
            btn.setStyle(baseStyle + "-fx-background-color: #e0e0e0; -fx-text-fill: #424242;");
            btn.setOnMouseEntered(e ->
                    btn.setStyle(baseStyle + "-fx-background-color: #bdbdbd; -fx-text-fill: #424242;"));
            btn.setOnMouseExited(e ->
                    btn.setStyle(baseStyle + "-fx-background-color: #e0e0e0; -fx-text-fill: #424242;"));
        }
        return btn;
    }

    private void salvarFuncionario() {
        if (!validarCampos()) {
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "INSERT INTO funcionarios (id, nome, funcao, dt) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, idField.getText());
            stmt.setString(2, nomeField.getText());
            stmt.setString(3, funcaoField.getText());
            stmt.setDate(4, Date.valueOf(dataAdmissaoField.getValue()));

            stmt.executeUpdate();

            mostrarSucesso("Funcionário cadastrado com sucesso!");
            limparFormulario();
        } catch (SQLException e) {
            mostrarErro("Erro ao cadastrar funcionário", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
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
        if (funcaoField.getText().trim().isEmpty()) {
            erros.append("- A função é obrigatória\n");
        }
        if (dataAdmissaoField.getValue() == null) {
            erros.append("- A data de admissão é obrigatória\n");
        }

        if (erros.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campos incompletos");
            alert.setHeaderText("Preencha todos os campos obrigatórios");
            alert.setContentText(erros.toString());
            alert.showAndWait();
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

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText("Operação realizada");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}