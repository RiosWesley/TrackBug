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
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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

        // Configuração da tabela
        tabelaFuncionarios = new TableView<>();
        tabelaFuncionarios.setStyle("-fx-font-family: 'Segoe UI';");

        TableColumn<Funcionario, String> colunaId = new TableColumn<>("Código");
        colunaId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));

        TableColumn<Funcionario, String> colunaNome = new TableColumn<>("Nome");
        colunaNome.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNome()));

        TableColumn<Funcionario, String> colunaFuncao = new TableColumn<>("Função");
        colunaFuncao.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFuncao()));

        TableColumn<Funcionario, String> colunaData = new TableColumn<>("Data de Admissão");
        colunaData.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDataAdmissao()));

        tabelaFuncionarios.getColumns().addAll(colunaId, colunaNome, colunaFuncao, colunaData);

        // Adicionar coluna de ações
        configurarColunaAcoes();

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
        pesquisaField.textProperty().addListener((observable, oldValue, newValue) -> {
            carregarFuncionarios(newValue);
        });
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

    private void configurarColunaAcoes() {
        TableColumn<Funcionario, Void> colunaAcoes = new TableColumn<>("Ações");
        colunaAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnDeletar = new Button("Deletar");
            {
                btnDeletar.setStyle(
                        "-fx-background-color: #c62828; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-family: 'Segoe UI'; " +
                                "-fx-font-size: 12px; " +
                                "-fx-padding: 5px 10px;"
                );
                btnDeletar.setOnAction(e -> {
                    Funcionario funcionario = getTableView().getItems().get(getIndex());
                    verificarExclusao(funcionario);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDeletar);
            }
        });
        tabelaFuncionarios.getColumns().add(colunaAcoes);
    }

    private boolean verificarEmprestimosAtivos(Funcionario funcionario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT COUNT(*) FROM emprestimos WHERE idFuncionario = ? AND ativo = true";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, funcionario.getId());
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao verificar empréstimos", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
        return false;
    }

    private void executarDelecao(Funcionario funcionario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            // Primeiro adiciona observação aos empréstimos
            String sqlHistorico = "UPDATE emprestimos SET observacoes = CONCAT(IFNULL(observacoes, ''), ' [Funcionário excluído em: " +
                    java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) +
                    " - Nome: " + funcionario.getNome() + ", ID: " + funcionario.getId() + "]') " +
                    "WHERE idFuncionario = ?";
            stmt = conn.prepareStatement(sqlHistorico);
            stmt.setString(1, funcionario.getId());
            stmt.executeUpdate();

            // Agora podemos deletar o funcionário
            // A constraint SET NULL vai automaticamente setar NULL nos registros de empréstimos
            String sqlDelete = "DELETE FROM funcionarios WHERE id = ?";
            stmt = conn.prepareStatement(sqlDelete);
            stmt.setString(1, funcionario.getId());
            stmt.executeUpdate();

            conn.commit();
            mostrarSucesso("Funcionário excluído com sucesso!");
            carregarFuncionarios("");
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            mostrarErro("Erro ao excluir funcionário", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    private void verificarExclusao(Funcionario funcionario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT COUNT(*) FROM emprestimos WHERE idFuncionario = ? AND ativo = true";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, funcionario.getId());
            rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                mostrarAlerta("Não é possível excluir",
                        "Este funcionário possui empréstimos ativos. Finalize todos os empréstimos antes de excluí-lo.");
                return;
            }

            // Se não houver empréstimos ativos, mostra diálogo de confirmação
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusão");
            confirmacao.setHeaderText("Deseja realmente excluir o funcionário?");
            confirmacao.setContentText(
                    "Funcionário: " + funcionario.getNome() + "\n" +
                            "Código: " + funcionario.getId() + "\n\n" +
                            "O histórico de empréstimos será mantido, mas o funcionário será removido do sistema."
            );

            Optional<ButtonType> resultado = confirmacao.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                executarDelecao(funcionario);
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao verificar empréstimos", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
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