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

        campoQuantidadeInicial = criarCampoTexto("Quantidade inicial");
        campoQuantidadeMinima = criarCampoTexto("Quantidade mínima");

        campoObservacoes = new TextArea();
        campoObservacoes.setPromptText("Observações");
        campoObservacoes.setPrefRowCount(3);
        estilizarTextArea(campoObservacoes);

        // Grid para organizar os campos
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // Adiciona os campos ao grid
        adicionarCampoAoGrid(grid, "Código:", campoId, 0);
        adicionarCampoAoGrid(grid, "Descrição:", campoDescricao, 1);
        adicionarCampoAoGrid(grid, "Data de Compra:", campoDataCompra, 2);
        adicionarCampoAoGrid(grid, "Peso (g):", campoPeso, 3);
        adicionarCampoAoGrid(grid, "Largura (cm):", campoLargura, 4);
        adicionarCampoAoGrid(grid, "Comprimento (cm):", campoComprimento, 5);
        adicionarCampoAoGrid(grid, "Tipo:", campoTipo, 6);
        adicionarCampoAoGrid(grid, "Quantidade Inicial:", campoQuantidadeInicial, 7);
        adicionarCampoAoGrid(grid, "Quantidade Mínima:", campoQuantidadeMinima, 8);
        adicionarCampoAoGrid(grid, "Observações:", campoObservacoes, 9);

        // Botões
        HBox botoesBox = new HBox(15);
        botoesBox.setAlignment(Pos.CENTER);

        Button btnSalvar = new Button("Salvar Equipamento");
        btnSalvar.setStyle(
                "-fx-background-color: #1a237e; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-weight: bold;"
        );

        Button btnLimpar = new Button("Limpar");
        btnLimpar.setStyle(
                "-fx-background-color: #e0e0e0; " +
                        "-fx-text-fill: #424242; " +
                        "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-weight: bold;"
        );

        botoesBox.getChildren().addAll(btnSalvar, btnLimpar);

        // Eventos
        campoTipo.setOnAction(e -> atualizarCamposQuantidade());
        btnSalvar.setOnAction(e -> salvarEquipamento());
        btnLimpar.setOnAction(e -> limparFormulario());

        // Adiciona todos os componentes ao formulário
        formContainer.getChildren().addAll(grid, botoesBox);
        getChildren().addAll(header, formContainer);
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

    private void estilizarComboBox(ComboBox<String> campo) {
        campo.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px;"
        );
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

    private void atualizarCamposQuantidade() {
        boolean isConsumivel = "Consumível".equals(campoTipo.getValue());
        campoQuantidadeMinima.setDisable(!isConsumivel);
        if (!isConsumivel) {
            campoQuantidadeMinima.clear();
        }
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
                    "comprimento, quantidadeAtual, quantidadeEstoque, tipo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, campoId.getText());
            stmt.setString(2, campoDescricao.getText());
            stmt.setDate(3, Date.valueOf(campoDataCompra.getValue()));
            stmt.setDouble(4, Double.parseDouble(campoPeso.getText()));
            stmt.setDouble(5, Double.parseDouble(campoLargura.getText()));
            stmt.setDouble(6, Double.parseDouble(campoComprimento.getText()));

            int quantidadeInicial = Integer.parseInt(campoQuantidadeInicial.getText());
            stmt.setInt(7, quantidadeInicial); // quantidadeAtual
            stmt.setInt(8, quantidadeInicial); // quantidadeEstoque
            stmt.setBoolean(9, "Consumível".equals(campoTipo.getValue()));

            stmt.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText("Equipamento registrado");
            alert.setContentText("O equipamento foi registrado com sucesso!");
            alert.showAndWait();

            limparFormulario();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao registrar equipamento");
            alert.setContentText("Não foi possível registrar o equipamento: " + e.getMessage());
            alert.showAndWait();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
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

        try {
            if (!campoQuantidadeInicial.getText().trim().isEmpty()) {
                Integer.parseInt(campoQuantidadeInicial.getText());
            }
        } catch (NumberFormatException e) {
            erros.append("- A quantidade inicial deve ser um número inteiro válido\n");
        }

        if ("Consumível".equals(campoTipo.getValue())) {
            try {
                if (!campoQuantidadeMinima.getText().trim().isEmpty()) {
                    Integer.parseInt(campoQuantidadeMinima.getText());
                }
            } catch (NumberFormatException e) {
                erros.append("- A quantidade mínima deve ser um número inteiro válido\n");
            }
        }

        if (erros.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Validação");
            alert.setHeaderText("Por favor, corrija os seguintes erros:");
            alert.setContentText(erros.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }

    private void limparFormulario() {
        campoId.clear();
        campoDescricao.clear();
        campoDataCompra.setValue(LocalDate.now());
        campoPeso.clear();
        campoLargura.clear();
        campoComprimento.clear();
        campoTipo.setValue("Emprestável");
        campoQuantidadeInicial.clear();
        campoQuantidadeMinima.clear();
        campoObservacoes.clear();
        atualizarCamposQuantidade();
    }
}