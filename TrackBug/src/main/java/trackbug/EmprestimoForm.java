package trackbug;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class EmprestimoForm extends VBox {
    private ComboBox<String> funcionarioCombo;
    private ComboBox<String> equipamentoCombo;
    private DatePicker dataDevolucao;
    private TextArea observacoes;
    private TextField quantidadeField;
    private Map<String, String> funcionarioIds = new HashMap<>();
    private Map<String, String> equipamentoIds = new HashMap<>();
    private Label equipamentoInfoLabel;

    public EmprestimoForm() {
        setSpacing(30);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: white;");

        // Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        Label titulo = new Label("Registrar Novo Empréstimo");
        titulo.setStyle(
                "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #1a237e; " +
                        "-fx-font-family: 'Segoe UI';"
        );
        Label subtitulo = new Label("Preencha os dados do empréstimo abaixo");
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

        // Estilo padrão para labels
        String labelStyle = "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: #2c3e50;";

        // Inicializar e estilizar componentes
        funcionarioCombo = createStyledComboBox("Selecione o funcionário");
        equipamentoCombo = createStyledComboBox("Selecione o equipamento");
        dataDevolucao = createStyledDatePicker();
        quantidadeField = createStyledTextField("Quantidade");
        observacoes = createStyledTextArea();
        equipamentoInfoLabel = new Label();
        equipamentoInfoLabel.setStyle(
                "-fx-font-size: 12px; " +
                        "-fx-text-fill: #666666; " +
                        "-fx-font-family: 'Segoe UI';"
        );

        // Adicionar listener para o combo de equipamentos
        equipamentoCombo.setOnAction(e -> atualizarInfoEquipamento());

        // Adicionar componentes ao grid
        addLabelAndField(grid, "Funcionário:", funcionarioCombo, 0, labelStyle);
        addLabelAndField(grid, "Equipamento:", equipamentoCombo, 1, labelStyle);
        grid.add(equipamentoInfoLabel, 1, 2);
        addLabelAndField(grid, "Quantidade:", quantidadeField, 3, labelStyle);
        addLabelAndField(grid, "Data de Devolução:", dataDevolucao, 4, labelStyle);
        addLabelAndField(grid, "Observações:", observacoes, 5, labelStyle);

        // Botões
        HBox botoesBox = new HBox(15);
        botoesBox.setAlignment(Pos.CENTER);
        Button btnSalvar = createStyledButton("Registrar Empréstimo", "CONFIRMAR");
        Button btnCancelar = createStyledButton("Cancelar", "CANCELAR");
        botoesBox.getChildren().addAll(btnSalvar, btnCancelar);

        // Montar formulário
        formContainer.getChildren().addAll(grid, botoesBox);
        getChildren().addAll(header, formContainer);

        // Carregar dados
        carregarFuncionarios();
        carregarEquipamentos();

        // Ações dos botões
        btnSalvar.setOnAction(e -> {
            if (validarCampos()) {
                salvarEmprestimo();
            }
        });
        btnCancelar.setOnAction(e -> limparFormulario());
    }

    private ComboBox<String> createStyledComboBox(String prompt) {
        ComboBox<String> combo = new ComboBox<>();
        combo.setPromptText(prompt);
        combo.setPrefWidth(300);
        combo.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );
        return combo;
    }

    private DatePicker createStyledDatePicker() {
        DatePicker datePicker = new DatePicker();
        datePicker.setPrefWidth(300);
        datePicker.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );
        return datePicker;
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(300);
        field.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );
        return field;
    }

    private TextArea createStyledTextArea() {
        TextArea area = new TextArea();
        area.setPromptText("Observações sobre o empréstimo");
        area.setPrefRowCount(3);
        area.setPrefWidth(300);
        area.setWrapText(true);
        area.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );
        return area;
    }

    private Button createStyledButton(String text, String type) {
        Button btn = new Button(text);
        // Cria os estilos base como constantes finais
        final String baseStyle = "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; " +
                "-fx-padding: 10px 20px; -fx-cursor: hand; -fx-font-weight: bold;";

        final String confirmStyle = baseStyle + "-fx-background-color: #1a237e; -fx-text-fill: white;";
        final String cancelStyle = baseStyle + "-fx-background-color: #e0e0e0; -fx-text-fill: #424242;";
        final String confirmHoverStyle = baseStyle + "-fx-background-color: #283593; -fx-text-fill: white;";
        final String cancelHoverStyle = baseStyle + "-fx-background-color: #bdbdbd; -fx-text-fill: #424242;";

        // Aplica o estilo inicial
        if (type.equals("CONFIRMAR")) {
            btn.setStyle(confirmStyle);
            btn.setOnMouseEntered(e -> btn.setStyle(confirmHoverStyle));
            btn.setOnMouseExited(e -> btn.setStyle(confirmStyle));
        } else {
            btn.setStyle(cancelStyle);
            btn.setOnMouseEntered(e -> btn.setStyle(cancelHoverStyle));
            btn.setOnMouseExited(e -> btn.setStyle(cancelStyle));
        }

        return btn;
    }

    private void addLabelAndField(GridPane grid, String labelText, Control field, int row, String labelStyle) {
        Label label = new Label(labelText);
        label.setStyle(labelStyle);
        grid.add(label, 0, row);
        grid.add(field, 1, row);
        GridPane.setColumnSpan(field, 2);
    }

    private void carregarFuncionarios() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT id, nome FROM funcionarios ORDER BY nome";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            funcionarioCombo.getItems().clear();
            funcionarioIds.clear();

            while(rs.next()) {
                String nome = rs.getString("nome");
                String id = rs.getString("id");
                funcionarioCombo.getItems().add(nome);
                funcionarioIds.put(nome, id);
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar funcionários", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private void carregarEquipamentos() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT id, descricao FROM equipamentos WHERE status = 'Disponível' ORDER BY descricao";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            equipamentoCombo.getItems().clear();
            equipamentoIds.clear();

            while(rs.next()) {
                String descricao = rs.getString("descricao");
                String id = rs.getString("id");
                equipamentoCombo.getItems().add(descricao);
                equipamentoIds.put(descricao, id);
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar equipamentos", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private void salvarEmprestimo() {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "INSERT INTO emprestimos (idFuncionario, idEquipamento, dataSaida, " +
                    "dataRetornoPrevista, observacoes, ativo, quantidadeEmprestimo) " +
                    "VALUES (?, ?, NOW(), ?, ?, true, ?)";

            stmt = conn.prepareStatement(sql);

            String funcionarioId = funcionarioIds.get(funcionarioCombo.getValue());
            String equipamentoId = equipamentoIds.get(equipamentoCombo.getValue());
            stmt.setString(1, funcionarioId);
            stmt.setString(2, equipamentoId);
            stmt.setDate(3, java.sql.Date.valueOf(dataDevolucao.getValue()));
            stmt.setString(4, observacoes.getText());
            stmt.setInt(5, Integer.parseInt(quantidadeField.getText()));

            stmt.executeUpdate();

            // Atualizar o status do equipamento
            sql = "SELECT tipo, quantidadeAtual FROM equipamentos WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, equipamentoId);
            ResultSet rs = stmt.executeQuery();
            String opcao = "";
            int quantidadeAtual = 0;

            while(rs.next()){
                if(rs.getBoolean("tipo") == false && rs.getInt("quantidadeAtual") == Integer.parseInt(quantidadeField.getText())) {
                    quantidadeAtual = rs.getInt("quantidadeAtual") - Integer.parseInt(quantidadeField.getText());
                    opcao = "UPDATE equipamentos SET status = 'Emprestado', " +
                            "quantidadeAtual = ? WHERE id = ?";
                }else if (rs.getBoolean("tipo") == true && rs.getInt("quantidadeAtual") == Integer.parseInt(quantidadeField.getText())){
                    quantidadeAtual = rs.getInt("quantidadeAtual") - Integer.parseInt(quantidadeField.getText());
                    opcao = "UPDATE equipamentos SET status = 'Emprestado', " +
                            "quantidadeAtual = ?, quantidadeEstoque = quantidadeAtual WHERE id = ?";
                }
                else if (rs.getBoolean("tipo") == false && rs.getInt("quantidadeAtual") > 0){
                    quantidadeAtual = rs.getInt("quantidadeAtual") - Integer.parseInt(quantidadeField.getText());
                    opcao = "UPDATE equipamentos SET status = 'Disponível', " +
                            "quantidadeAtual = ?, quantidadeEstoque = quantidadeAtual WHERE id = ?";
                }
                else if (rs.getBoolean("tipo") == true && rs.getInt("quantidadeAtual") > 0){
                    quantidadeAtual = rs.getInt("quantidadeAtual") - Integer.parseInt(quantidadeField.getText());
                    opcao = "UPDATE equipamentos SET status = 'Disponível', " +
                            "quantidadeAtual = ?, quantidadeEstoque = quantidadeAtual WHERE id = ?";
                }
            }
            stmt = conn.prepareStatement(opcao);
            stmt.setInt(1, quantidadeAtual);
            stmt.setString(2, equipamentoId);
            stmt.executeUpdate();

            mostrarSucesso("Empréstimo registrado com sucesso!");
            limparFormulario();

        } catch (SQLException e) {
            mostrarErro("Erro ao registrar empréstimo", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    private boolean validarCampos() {
        if (funcionarioCombo.getValue() == null ||
                equipamentoCombo.getValue() == null ||
                quantidadeField.getText().isEmpty()) {

            mostrarAlerta("Campos obrigatórios", "Por favor, preencha todos os campos obrigatórios.");
            return false;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        conn = ConnectionFactory.getConnection();
        int quantAtual = 0;
        String equipamentoSelecionado = equipamentoCombo.getValue();
        try{
            String sql = "SELECT quantidadeAtual, tipo FROM equipamentos WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, equipamentoIds.get(equipamentoSelecionado));
            rs = stmt.executeQuery();
            if (rs.next()) {
                quantAtual = rs.getInt("quantidadeAtual");
                if(rs.getBoolean("tipo") == false) {
                    if (dataDevolucao.getValue() == null) {
                        mostrarAlerta("Campos obrigatórios", "Como é um item emprestável, é necessário uma data de retorno.");
                        return false;
                    }
                    if (dataDevolucao.getValue().isBefore(LocalDate.now())) {
                        mostrarAlerta("Data inválida", "A data de devolução não pode ser anterior à data atual.");
                        return false;
                    }
                }else {
                    dataDevolucao.setValue(LocalDate.now());
                }
            }

        } catch (SQLException e) {
            mostrarAlerta("ERRO", "NAQUILO MERMO");
            return false;
        } finally {
                ConnectionFactory.closeConnection(conn, stmt, rs);
        }

        try {
            int quantidade = Integer.parseInt(quantidadeField.getText());
            if (quantidade <= 0) {
                mostrarAlerta("Quantidade inválida", "A quantidade deve ser maior que zero.");
                return false;
            }else if(quantidade > quantAtual){
                mostrarAlerta("Quantidade inválida", "A quantidade deve ser menor ou igual a disponível.");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Quantidade inválida", "Por favor, insira um número válido para a quantidade.");
            return false;
        }

        return true;
    }

    private void limparFormulario() {
        funcionarioCombo.setValue(null);
        equipamentoCombo.setValue(null);
        dataDevolucao.setValue(null);
        quantidadeField.clear();
        observacoes.clear();
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

    private void atualizarInfoEquipamento() {
        String equipamentoSelecionado = equipamentoCombo.getValue();
        String equipamentoId = equipamentoIds.get(equipamentoSelecionado);
        if (equipamentoSelecionado != null) {
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = ConnectionFactory.getConnection();
                String sql = "SELECT tipo, quantidadeAtual, quantidadeEstoque FROM equipamentos WHERE id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, equipamentoId);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    int quantAtual = rs.getInt("quantidadeAtual");
                    int quantTotal = rs.getInt("quantidadeEstoque");
                    equipamentoInfoLabel.setText(String.format("Disponível: %d unidades (Total: %d)", quantAtual, quantTotal));
                    dataDevolucao.setDisable(rs.getBoolean("tipo"));
                    if(rs.getBoolean("tipo") == true){
                        dataDevolucao.setValue(null);
                    }
                }
            } catch (SQLException e) {
                equipamentoInfoLabel.setText("Erro ao carregar informações do equipamento");
            } finally {
                ConnectionFactory.closeConnection(conn, stmt, rs);
            }
        }

    }
}
