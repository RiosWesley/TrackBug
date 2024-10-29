package trackbug;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListarFuncionariosForm extends VBox {
    private TableView<Funcionario> tabelaFuncionarios;
    private TextField pesquisaField;

    public ListarFuncionariosForm() {
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: white;");

        // Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        Label titulo = new Label("Lista de Funcionários");
        titulo.setStyle(
                "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #1a237e; " +
                        "-fx-font-family: 'Segoe UI';"
        );
        Label subtitulo = new Label("Visualize e gerencie os funcionários cadastrados");
        subtitulo.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-text-fill: #757575; " +
                        "-fx-font-family: 'Segoe UI';"
        );
        header.getChildren().addAll(titulo, subtitulo);

        // Campo de pesquisa
        HBox pesquisaBox = new HBox(10);
        pesquisaBox.setAlignment(Pos.CENTER);
        pesquisaField = new TextField();
        pesquisaField.setPromptText("Pesquisar por nome ou função...");
        pesquisaField.setPrefWidth(300);
        pesquisaField.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );

        Button btnPesquisar = new Button("Pesquisar");
        estilizarBotao(btnPesquisar, true);
        pesquisaBox.getChildren().addAll(pesquisaField, btnPesquisar);

        // Tabela de funcionários
        tabelaFuncionarios = new TableView<>();
        tabelaFuncionarios.setStyle("-fx-font-family: 'Segoe UI';");

        TableColumn<Funcionario, String> colunaId = new TableColumn<>("Código");
        colunaId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));

        TableColumn<Funcionario, String> colunaNome = new TableColumn<>("Nome");
        colunaNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));

        TableColumn<Funcionario, String> colunaFuncao = new TableColumn<>("Função");
        colunaFuncao.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFuncao()));

        TableColumn<Funcionario, String> colunaData = new TableColumn<>("Data de Admissão");
        colunaData.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDataAdmissao()));

        tabelaFuncionarios.getColumns().addAll(colunaId, colunaNome, colunaFuncao, colunaData);

        // Configurar a tabela para ocupar o espaço disponível
        VBox.setVgrow(tabelaFuncionarios, Priority.ALWAYS);
        tabelaFuncionarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Botão para atualizar lista
        Button btnAtualizar = new Button("Atualizar Lista");
        estilizarBotao(btnAtualizar, false);

        // Adicionar componentes ao formulário
        getChildren().addAll(header, pesquisaBox, tabelaFuncionarios, btnAtualizar);

        // Carregar dados iniciais
        carregarFuncionarios("");

        // Ações dos botões
        btnPesquisar.setOnAction(e -> carregarFuncionarios(pesquisaField.getText()));
        btnAtualizar.setOnAction(e -> {
            pesquisaField.clear();
            carregarFuncionarios("");
        });
    }

    private void estilizarBotao(Button btn, boolean isPrimary) {
        String baseStyle =
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; ";

        if (isPrimary) {
            btn.setStyle(baseStyle +
                    "-fx-background-color: #1a237e; " +
                    "-fx-text-fill: white;");
        } else {
            btn.setStyle(baseStyle +
                    "-fx-background-color: #e0e0e0; " +
                    "-fx-text-fill: #424242;");
        }
    }

    private void carregarFuncionarios(String filtro) {
        ObservableList<Funcionario> funcionarios = FXCollections.observableArrayList();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT id, nome, funcao, dt FROM funcionarios";
            if (!filtro.isEmpty()) {
                sql += " WHERE nome LIKE ? OR funcao LIKE ?";
            }
            sql += " ORDER BY nome";

            stmt = conn.prepareStatement(sql);
            if (!filtro.isEmpty()) {
                String searchTerm = "%" + filtro + "%";
                stmt.setString(1, searchTerm);
                stmt.setString(2, searchTerm);
            }

            rs = stmt.executeQuery();
            while (rs.next()) {
                Funcionario func = new Funcionario();
                func.setID(rs.getString("id"));
                func.setNome(rs.getString("nome"));
                func.setFuncao(rs.getString("funcao"));
                func.setDataAdmissao(rs.getString("dt"));
                funcionarios.add(func);
            }

            tabelaFuncionarios.setItems(funcionarios);

            if (funcionarios.isEmpty()) {
                mostrarMensagem("Nenhum funcionário encontrado", "", Alert.AlertType.INFORMATION);
            }

        } catch (SQLException e) {
            mostrarMensagem("Erro", "Erro ao carregar funcionários: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private void mostrarMensagem(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}