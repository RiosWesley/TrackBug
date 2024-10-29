package trackbug;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DevolucaoForm extends VBox {
    private ComboBox<Integer> emprestimoCombo;
    private TextArea detalhesEmprestimo;

    public DevolucaoForm() {
        setSpacing(30);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: white;");

        // Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        Label titulo = new Label("Registrar Devolução");
        titulo.setStyle(
                "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #1a237e; " +
                        "-fx-font-family: 'Segoe UI';"
        );
        Label subtitulo = new Label("Selecione o empréstimo para registrar a devolução");
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
        formContainer.setAlignment(Pos.TOP_CENTER);

        // Seleção do empréstimo
        VBox selecaoBox = new VBox(10);
        Label selecaoLabel = new Label("Selecione o Empréstimo:");
        selecaoLabel.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: #2c3e50;"
        );

        emprestimoCombo = new ComboBox<>();
        emprestimoCombo.setPromptText("Selecione o código do empréstimo");
        emprestimoCombo.setPrefWidth(300);
        emprestimoCombo.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );

        selecaoBox.getChildren().addAll(selecaoLabel, emprestimoCombo);

        // Card de detalhes
        VBox detalhesCard = new VBox(10);
        detalhesCard.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 15px; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );

        Label detalhesLabel = new Label("Detalhes do Empréstimo");
        detalhesLabel.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #2c3e50; " +
                        "-fx-font-family: 'Segoe UI';"
        );

        detalhesEmprestimo = new TextArea();
        detalhesEmprestimo.setEditable(false);
        detalhesEmprestimo.setPrefRowCount(8);
        detalhesEmprestimo.setPrefWidth(500);
        detalhesEmprestimo.setWrapText(true);
        detalhesEmprestimo.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: #f8f9fa; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );

        detalhesCard.getChildren().addAll(detalhesLabel, detalhesEmprestimo);

        // Botões
        HBox botoesBox = new HBox(15);
        botoesBox.setAlignment(Pos.CENTER);
        Button btnConfirmar = createStyledButton("Confirmar Devolução", "CONFIRMAR");
        Button btnCancelar = createStyledButton("Cancelar", "CANCELAR");
        botoesBox.getChildren().addAll(btnConfirmar, btnCancelar);

        // Adicionar componentes ao container
        formContainer.getChildren().addAll(selecaoBox, detalhesCard, botoesBox);

        // Adicionar container ao formulário
        getChildren().addAll(header, formContainer);

        // Carregar empréstimos ativos
        carregarEmprestimosAtivos();

        // Adicionar listener para quando um empréstimo for selecionado
        emprestimoCombo.setOnAction(e -> {
            Integer selectedId = emprestimoCombo.getValue();
            if (selectedId != null) {
                mostrarDetalhesEmprestimo(selectedId);
            }
        });

        // Ação dos botões
        btnConfirmar.setOnAction(e -> {
            if (emprestimoCombo.getValue() != null) {
                registrarDevolucao(emprestimoCombo.getValue());
            } else {
                mostrarAlerta("Selecione um empréstimo",
                        "Por favor, selecione um empréstimo para registrar a devolução.");
            }
        });

        btnCancelar.setOnAction(e -> limparFormulario());
    }

    private Button createStyledButton(String text, String type) {
        Button btn = new Button(text);
        final String baseStyle = "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; " +
                "-fx-padding: 10px 20px; -fx-cursor: hand; -fx-font-weight: bold;";

        final String confirmStyle = baseStyle + "-fx-background-color: #1a237e; -fx-text-fill: white;";
        final String cancelStyle = baseStyle + "-fx-background-color: #e0e0e0; -fx-text-fill: #424242;";
        final String confirmHoverStyle = baseStyle + "-fx-background-color: #283593; -fx-text-fill: white;";
        final String cancelHoverStyle = baseStyle + "-fx-background-color: #bdbdbd; -fx-text-fill: #424242;";

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

    private void carregarEmprestimosAtivos() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT id FROM emprestimos WHERE ativo = true ORDER BY id";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            emprestimoCombo.getItems().clear();
            while (rs.next()) {
                emprestimoCombo.getItems().add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar empréstimos", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private void mostrarDetalhesEmprestimo(int emprestimoId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT e.dataSaida, e.dataRetornoPrevista, e.observacoes, " +
                    "f.nome as nome_funcionario, eq.descricao as nome_equipamento, " +
                    "e.quantidadeEmprestimo " +
                    "FROM emprestimos e " +
                    "JOIN funcionarios f ON e.idFuncionario = f.id " +
                    "JOIN equipamentos eq ON e.idEquipamento = eq.id " +
                    "WHERE e.id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, emprestimoId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String detalhes = String.format(
                        "Funcionário: %s\n\n" +
                                "Equipamento: %s\n" +
                                "Quantidade: %d\n\n" +
                                "Data de Saída: %s\n" +
                                "Data Prevista de Retorno: %s\n\n" +
                                "Observações:\n%s",
                        rs.getString("nome_funcionario"),
                        rs.getString("nome_equipamento"),
                        rs.getInt("quantidadeEmprestimo"),
                        rs.getTimestamp("dataSaida").toString(),
                        rs.getTimestamp("dataRetornoPrevista").toString(),
                        rs.getString("observacoes")
                );
                detalhesEmprestimo.setText(detalhes);
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar detalhes", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private void registrarDevolucao(Integer emprestimoId) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();

            // Atualiza o status do empréstimo
            String sqlEmprestimo = "UPDATE emprestimos SET ativo = false, dataRetornoEfetiva = NOW() WHERE id = ?";
            stmt = conn.prepareStatement(sqlEmprestimo);
            stmt.setInt(1, emprestimoId);
            stmt.executeUpdate();

            // Atualiza o status e quantidade do equipamento
            String sqlEquipamento = "UPDATE equipamentos e " +
                    "JOIN emprestimos emp ON e.id = emp.idEquipamento " +
                    "SET e.status = 'Disponível', " +
                    "e.quantidadeAtual = e.quantidadeAtual + emp.quantidadeEmprestimo " +
                    "WHERE emp.id = ?";
            stmt = conn.prepareStatement(sqlEquipamento);
            stmt.setInt(1, emprestimoId);
            stmt.executeUpdate();

            mostrarSucesso("Devolução registrada com sucesso!");
            limparFormulario();
            carregarEmprestimosAtivos();

        } catch (SQLException e) {
            mostrarErro("Erro ao registrar devolução", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    private void limparFormulario() {
        emprestimoCombo.setValue(null);
        detalhesEmprestimo.clear();
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
}