package trackbug;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class RegistrarEquipamentoForm extends VBox {
    private TextField campoId;
    private TextField campoDescricao;
    private DatePicker campoDataCompra;
    private TextField campoPeso;
    private TextField campoLargura;
    private TextField campoComprimento;
    private ComboBox<String> campoTipo;
    private TextField campoQuantidadeInicial;
    private TextField campoQuantidadeMinima;
    private TextArea campoObservacoes;
    private ComboBox<String> campoTipoUso;
    private CheckBox checkBoxMedidas;

    public RegistrarEquipamentoForm() {
        // Configuração do ScrollPane para garantir responsividade
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Container principal com padding
        VBox mainContainer = new VBox();
        mainContainer.getStyleClass().add("form-container");
        mainContainer.setSpacing(30);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setAlignment(Pos.TOP_CENTER);

        // Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("form-header");

        Label titulo = new Label("Registrar Novo Equipamento");
        titulo.getStyleClass().add("form-title");

        Label subtitulo = new Label("Preencha os dados do novo equipamento");
        subtitulo.getStyleClass().add("form-subtitle");

        header.getChildren().addAll(titulo, subtitulo);

        // Grid para campos do formulário
        GridPane grid = new GridPane();
        grid.getStyleClass().add("form-grid");
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // Configuração das colunas do grid para responsividade
        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setMinWidth(100);
        labelCol.setPrefWidth(150);
        labelCol.setHgrow(Priority.NEVER);

        ColumnConstraints fieldCol = new ColumnConstraints();
        fieldCol.setHgrow(Priority.ALWAYS);
        fieldCol.setMinWidth(200);

        grid.getColumnConstraints().addAll(labelCol, fieldCol);

        // Inicialização dos campos
        initializeFields();

        // Adiciona campos ao grid
        int row = 0;
        addFormField(grid, "Código:", campoId, row++);
        addFormField(grid, "Descrição:", campoDescricao, row++);
        addFormField(grid, "Data de Compra:", campoDataCompra, row++);
        addFormField(grid, "Tipo de Item:", campoTipo, row++);
        addFormField(grid, "Tipo de Uso:", campoTipoUso, row++);
        addFormField(grid, "Quantidade Inicial:", campoQuantidadeInicial, row++);
        addFormField(grid, "Quantidade Mínima:", campoQuantidadeMinima, row++);
        addFormField(grid, "Observações:", campoObservacoes, row++);

        // Checkbox para medidas
        HBox checkboxContainer = new HBox(10);
        checkboxContainer.setAlignment(Pos.CENTER_LEFT);
        checkboxContainer.getChildren().add(checkBoxMedidas);
        grid.add(checkboxContainer, 0, row++, 2, 1);

        // Container para campos de medidas
        VBox medidasContainer = new VBox(10);
        medidasContainer.visibleProperty().bind(checkBoxMedidas.selectedProperty());
        medidasContainer.managedProperty().bind(checkBoxMedidas.selectedProperty());

        GridPane medidasGrid = new GridPane();
        medidasGrid.setHgap(15);
        medidasGrid.setVgap(10);

        addFormField(medidasGrid, "Peso (g):", campoPeso, 0);
        addFormField(medidasGrid, "Largura (cm):", campoLargura, 1);
        addFormField(medidasGrid, "Comprimento (cm):", campoComprimento, 2);

        medidasContainer.getChildren().add(medidasGrid);
        grid.add(medidasContainer, 0, row++, 2, 1);

        // Botões
        HBox botoesBox = new HBox(15);
        botoesBox.setAlignment(Pos.CENTER);

        Button btnSalvar = new Button("Salvar Equipamento");
        btnSalvar.getStyleClass().addAll("btn-primary");
        btnSalvar.setOnAction(e -> salvarEquipamento());

        Button btnLimpar = new Button("Limpar");
        btnLimpar.getStyleClass().addAll("btn-secondary");
        btnLimpar.setOnAction(e -> limparFormulario());

        botoesBox.getChildren().addAll(btnSalvar, btnLimpar);

        // Monta o layout final
        mainContainer.getChildren().addAll(header, grid, botoesBox);
        scrollPane.setContent(mainContainer);
        getChildren().add(scrollPane);
    }

    private void initializeFields() {
        campoId = createStyledTextField("Código do equipamento");
        campoDescricao = createStyledTextField("Descrição do equipamento");
        campoDataCompra = new DatePicker(LocalDate.now());
        campoDataCompra.getStyleClass().add("form-field");

        campoTipo = new ComboBox<>();
        campoTipo.getItems().addAll("Emprestável", "Consumível");
        campoTipo.setValue("Emprestável");
        campoTipo.getStyleClass().add("form-field");

        campoTipoUso = new ComboBox<>();
        campoTipoUso.getItems().addAll("Reutilizável", "Uso Único");
        campoTipoUso.setValue("Reutilizável");
        campoTipoUso.getStyleClass().add("form-field");

        campoQuantidadeInicial = createStyledTextField("Quantidade inicial");
        campoQuantidadeMinima = createStyledTextField("Quantidade mínima");

        campoObservacoes = new TextArea();
        campoObservacoes.setPromptText("Observações");
        campoObservacoes.setPrefRowCount(3);
        campoObservacoes.getStyleClass().add("form-field");

        checkBoxMedidas = new CheckBox("Incluir peso e dimensões");
        checkBoxMedidas.getStyleClass().add("form-checkbox");

        campoPeso = createStyledTextField("Peso");
        campoLargura = createStyledTextField("Largura");
        campoComprimento = createStyledTextField("Comprimento");
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("form-field");
        return field;
    }

    private void addFormField(GridPane grid, String label, Control field, int row) {
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("form-label");
        grid.add(labelNode, 0, row);
        grid.add(field, 1, row);
        GridPane.setFillWidth(field, true);
    }

    private HBox criarCampoComLabel(String labelText, Control campo) {
        HBox container = new HBox(10);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        container.getChildren().addAll(label, campo);
        return container;
    }

    private Button criarBotao(String texto, boolean isPrimary) {
        Button btn = new Button(texto);
        String baseStyle = "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; " +
                "-fx-padding: 10px 20px; -fx-cursor: hand; -fx-font-weight: bold;";

        if (isPrimary) {
            btn.setStyle(baseStyle + "-fx-background-color: #1a237e; -fx-text-fill: white;");
        } else {
            btn.setStyle(baseStyle + "-fx-background-color: #e0e0e0; -fx-text-fill: #424242;");
        }

        return btn;
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
        campo.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
    }

    private void estilizarComboBox(ComboBox<String> campo) {
        campo.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
    }

    private void estilizarTextArea(TextArea campo) {
        campo.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );
    }

    private void adicionarCampoAoGrid(GridPane grid, String label, Control campo, int linha) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        grid.add(labelNode, 0, linha);
        grid.add(campo, 1, linha);
        GridPane.setHgrow(campo, Priority.ALWAYS);
    }

    private boolean validarCampos() {
        StringBuilder erros = new StringBuilder();

        if (campoId.getText().trim().isEmpty()) {
            erros.append("- O código do equipamento é obrigatório\n");
        }
        if (campoDescricao.getText().trim().isEmpty()) {
            erros.append("- A descrição é obrigatória\n");
        }
        if (campoDataCompra.getValue() == null) {
            erros.append("- A data de compra é obrigatória\n");
        }
        if (campoQuantidadeInicial.getText().trim().isEmpty()) {
            erros.append("- A quantidade inicial é obrigatória\n");
        }

        // Validar medidas apenas se o checkbox estiver selecionado
        if (checkBoxMedidas.isSelected()) {
            try {
                if (!campoPeso.getText().trim().isEmpty()) {
                    Double.parseDouble(campoPeso.getText());
                }
            } catch (NumberFormatException e) {
                erros.append("- O peso deve ser um número válido\n");
            }

            try {
                if (!campoLargura.getText().trim().isEmpty()) {
                    Double.parseDouble(campoLargura.getText());
                }
            } catch (NumberFormatException e) {
                erros.append("- A largura deve ser um número válido\n");
            }

            try {
                if (!campoComprimento.getText().trim().isEmpty()) {
                    Double.parseDouble(campoComprimento.getText());
                }
            } catch (NumberFormatException e) {
                erros.append("- O comprimento deve ser um número válido\n");
            }
        }

        if (erros.length() > 0) {
            mostrarErro("Erro de Validação", erros.toString());
            return false;
        }
        return true;
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

    private void salvarEquipamento() {
        if (!validarCampos()) {
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "INSERT INTO equipamentos (id, descricao, dataCompra, peso, largura, " +
                    "comprimento, quantidadeAtual, quantidadeEstoque, tipo, tipo_uso, quantidadeMinima) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, campoId.getText());
            stmt.setString(2, campoDescricao.getText());
            stmt.setDate(3, Date.valueOf(campoDataCompra.getValue()));

            // Definir valores de medidas
            if (checkBoxMedidas.isSelected()) {
                stmt.setDouble(4, Double.parseDouble(campoPeso.getText()));
                stmt.setDouble(5, Double.parseDouble(campoLargura.getText()));
                stmt.setDouble(6, Double.parseDouble(campoComprimento.getText()));
            } else {
                stmt.setNull(4, java.sql.Types.DOUBLE);
                stmt.setNull(5, java.sql.Types.DOUBLE);
                stmt.setNull(6, java.sql.Types.DOUBLE);
            }

            int quantidadeInicial = Integer.parseInt(campoQuantidadeInicial.getText());
            stmt.setInt(7, quantidadeInicial);
            stmt.setInt(8, quantidadeInicial);
            stmt.setBoolean(9, "Consumível".equals(campoTipo.getValue()));
            stmt.setString(10, campoTipoUso.getValue());
            stmt.setInt(11, Integer.parseInt(campoQuantidadeMinima.getText()));

            stmt.executeUpdate();

            mostrarSucesso("Equipamento registrado com sucesso!");
            limparFormulario();
        } catch (SQLException e) {
            mostrarErro("Erro ao registrar equipamento", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    private void limparFormulario() {
        campoId.clear();
        campoDescricao.clear();
        campoDataCompra.setValue(LocalDate.now());
        campoPeso.clear();
        campoLargura.clear();
        campoComprimento.clear();
        campoTipo.setValue("Emprestável");
        campoTipoUso.setValue("Reutilizável");
        campoQuantidadeInicial.clear();
        campoQuantidadeMinima.clear();
        campoObservacoes.clear();
        checkBoxMedidas.setSelected(false);
    }
}