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
        setSpacing(30);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: white;");

        // Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        Label titulo = new Label("Registrar Novo Equipamento");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1a237e; -fx-font-family: 'Segoe UI';");
        Label subtitulo = new Label("Preencha os dados do novo equipamento");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575; -fx-font-family: 'Segoe UI';");
        header.getChildren().addAll(titulo, subtitulo);

        // Container do formulário
        VBox formContainer = new VBox(20);
        formContainer.setMaxWidth(600);
        formContainer.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20px; -fx-background-radius: 5px;");

        // Inicialização dos campos
        campoId = criarCampoTexto("Código do equipamento");
        campoDescricao = criarCampoTexto("Descrição do equipamento");
        campoDataCompra = new DatePicker(LocalDate.now());
        campoDataCompra.setPromptText("Data de compra");
        estilizarDatePicker(campoDataCompra);

        campoPeso = criarCampoTexto("Peso (g)");
        campoLargura = criarCampoTexto("Largura (cm)");
        campoComprimento = criarCampoTexto("Comprimento (cm)");

        campoTipo = new ComboBox<>();
        campoTipo.getItems().addAll("Emprestável", "Consumível");
        campoTipo.setValue("Emprestável");
        estilizarComboBox(campoTipo);

        campoTipoUso = new ComboBox<>();
        campoTipoUso.getItems().addAll("Reutilizável", "Uso Único");
        campoTipoUso.setValue("Reutilizável");
        estilizarComboBox(campoTipoUso);

        checkBoxMedidas = new CheckBox("Incluir peso e dimensões");
        checkBoxMedidas.setSelected(false);
        checkBoxMedidas.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");

        campoQuantidadeInicial = criarCampoTexto("Quantidade inicial");
        campoQuantidadeMinima = criarCampoTexto("Quantidade mínima");

        campoObservacoes = new TextArea();
        campoObservacoes.setPromptText("Observações");
        campoObservacoes.setPrefRowCount(3);
        estilizarTextArea(campoObservacoes);

        // Container para campos de medidas
        VBox medidasContainer = new VBox(10);
        medidasContainer.getChildren().addAll(
                criarCampoComLabel("Peso (g):", campoPeso),
                criarCampoComLabel("Largura (cm):", campoLargura),
                criarCampoComLabel("Comprimento (cm):", campoComprimento)
        );
        medidasContainer.visibleProperty().bind(checkBoxMedidas.selectedProperty());
        medidasContainer.managedProperty().bind(checkBoxMedidas.selectedProperty());

        // Grid para organizar os campos
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // Adiciona os campos ao grid
        int row = 0;
        adicionarCampoAoGrid(grid, "Código:", campoId, row++);
        adicionarCampoAoGrid(grid, "Descrição:", campoDescricao, row++);
        adicionarCampoAoGrid(grid, "Data de Compra:", campoDataCompra, row++);
        adicionarCampoAoGrid(grid, "Tipo de Item:", campoTipo, row++);
        adicionarCampoAoGrid(grid, "Tipo de Uso:", campoTipoUso, row++);
        adicionarCampoAoGrid(grid, "Quantidade Inicial:", campoQuantidadeInicial, row++);
        adicionarCampoAoGrid(grid, "Quantidade Mínima:", campoQuantidadeMinima, row++);
        adicionarCampoAoGrid(grid, "Observações:", campoObservacoes, row++);

        grid.add(checkBoxMedidas, 0, row++, 2, 1);
        grid.add(medidasContainer, 0, row, 2, 1);

        // Botões
        HBox botoesBox = new HBox(15);
        botoesBox.setAlignment(Pos.CENTER);
        Button btnSalvar = criarBotao("Salvar Equipamento", true);
        Button btnLimpar = criarBotao("Limpar", false);
        botoesBox.getChildren().addAll(btnSalvar, btnLimpar);

        // Eventos
        btnSalvar.setOnAction(e -> salvarEquipamento());
        btnLimpar.setOnAction(e -> limparFormulario());

        // Adiciona todos os componentes ao formulário
        formContainer.getChildren().addAll(grid, botoesBox);
        getChildren().addAll(header, formContainer);
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
                    "comprimento, quantidadeAtual, quantidadeEstoque, tipo, tipo_uso) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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