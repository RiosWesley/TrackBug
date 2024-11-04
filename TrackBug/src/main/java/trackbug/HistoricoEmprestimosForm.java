package trackbug;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoricoEmprestimosForm extends VBox {
    private TableView<Emprestimos> tabelaHistorico;
    private DatePicker dataInicio;
    private DatePicker dataFim;
    private TextField funcionarioField;
    private TextField equipamentoField;
    private final DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public HistoricoEmprestimosForm() {
        setSpacing(30);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: white;");

        // Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        Label titulo = new Label("Histórico de Empréstimos");
        titulo.setStyle(
                "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #1a237e; " +
                        "-fx-font-family: 'Segoe UI';"
        );
        Label subtitulo = new Label("Consulte o histórico completo de empréstimos");
        subtitulo.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-text-fill: #757575; " +
                        "-fx-font-family: 'Segoe UI';"
        );
        header.getChildren().addAll(titulo, subtitulo);

        // Container dos filtros
        VBox filtrosContainer = new VBox(15);
        filtrosContainer.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-padding: 20px; " +
                        "-fx-background-radius: 5px;"
        );
        filtrosContainer.setMaxWidth(800);

        // Grid para os filtros
        GridPane gridFiltros = new GridPane();
        gridFiltros.setHgap(15);
        gridFiltros.setVgap(10);
        gridFiltros.setAlignment(Pos.CENTER);

        // Inicialização dos campos de filtro
        dataInicio = criarDatePicker("Data inicial");
        dataFim = criarDatePicker("Data final");
        funcionarioField = criarTextField("Nome do funcionário");
        equipamentoField = criarTextField("Descrição do equipamento");

        // Adiciona campos ao grid
        adicionarCampoAoGrid(gridFiltros, "Período:", dataInicio, 0, 0);
        adicionarCampoAoGrid(gridFiltros, "até", dataFim, 0, 2);
        adicionarCampoAoGrid(gridFiltros, "Funcionário:", funcionarioField, 1, 0);
        adicionarCampoAoGrid(gridFiltros, "Equipamento:", equipamentoField, 1, 2);

        // Botões de ação
        HBox botoesBox = new HBox(10);
        botoesBox.setAlignment(Pos.CENTER);
        Button btnPesquisar = criarBotao("Pesquisar", true);
        Button btnLimpar = criarBotao("Limpar Filtros", false);
        Button btnExportar = criarBotao("Exportar PDF", false);
        botoesBox.getChildren().addAll(btnPesquisar, btnLimpar, btnExportar);

        filtrosContainer.getChildren().addAll(gridFiltros, botoesBox);

        // Configuração da tabela
        tabelaHistorico = new TableView<>();
        tabelaHistorico.setStyle("-fx-font-family: 'Segoe UI';");

        TableColumn<Emprestimos, String> colunaData = new TableColumn<>("Data do Empréstimo");
        colunaData.setCellValueFactory(data -> {
            if (data.getValue() != null && data.getValue().getDataSaida() != null) {
                return new SimpleStringProperty(formatador.format(data.getValue().getDataSaida()));
            }
            return new SimpleStringProperty("");
        });

        TableColumn<Emprestimos, String> colunaFuncionario = new TableColumn<>("Funcionário");
        colunaFuncionario.setCellValueFactory(data -> {
            if (data.getValue() != null) {
                return new SimpleStringProperty(buscarNomeFuncionario(data.getValue().getIdFuncionario()));
            }
            return new SimpleStringProperty("");
        });

        TableColumn<Emprestimos, String> colunaEquipamento = new TableColumn<>("Equipamento");
        colunaEquipamento.setCellValueFactory(data -> {
            if (data.getValue() != null) {
                return new SimpleStringProperty(buscarDescricaoEquipamento(data.getValue().getIdEquipamento()));
            }
            return new SimpleStringProperty("");
        });

        TableColumn<Emprestimos, String> colunaQtd = new TableColumn<>("Quantidade");
        colunaQtd.setCellValueFactory(data -> {
            if (data.getValue() != null) {
                return new SimpleStringProperty(String.valueOf(data.getValue().getQuantidadeEmprestimo()));
            }
            return new SimpleStringProperty("");
        });

        TableColumn<Emprestimos, String> colunaPrevista = new TableColumn<>("Devolução Prevista");
        colunaPrevista.setCellValueFactory(data -> {
            if (data.getValue() != null && data.getValue().getDataRetornoPrevista() != null) {
                return new SimpleStringProperty(formatador.format(data.getValue().getDataRetornoPrevista()));
            }
            return new SimpleStringProperty("");
        });

        TableColumn<Emprestimos, String> colunaEfetiva = new TableColumn<>("Devolução Efetiva");
        colunaEfetiva.setCellValueFactory(data -> {
            if (data.getValue() != null && data.getValue().getDataRetornoEfetiva() != null) {
                return new SimpleStringProperty(formatador.format(data.getValue().getDataRetornoEfetiva()));
            }
            return new SimpleStringProperty("Pendente");
        });

        TableColumn<Emprestimos, String> colunaStatus = new TableColumn<>("Status");
        colunaStatus.setCellValueFactory(data -> {
            if (data.getValue() != null) {
                return new SimpleStringProperty(data.getValue().isAtivo() ? "Em andamento" : "Concluído");
            }
            return new SimpleStringProperty("");
        });

        tabelaHistorico.getColumns().addAll(
                colunaData,
                colunaFuncionario,
                colunaEquipamento,
                colunaQtd,
                colunaPrevista,
                colunaEfetiva,
                colunaStatus
        );

        // Configurações adicionais da tabela
        tabelaHistorico.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabelaHistorico, Priority.ALWAYS);

        // Eventos dos botões
        btnPesquisar.setOnAction(e -> pesquisarHistorico());
        btnLimpar.setOnAction(e -> limparFiltros());
        btnExportar.setOnAction(e -> exportarPDF());

        // Adiciona todos os componentes ao layout principal
        getChildren().addAll(header, filtrosContainer, tabelaHistorico);

        // Carrega dados iniciais
        carregarHistorico();
    }

    private DatePicker criarDatePicker(String prompt) {
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText(prompt);
        datePicker.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px;"
        );
        return datePicker;
    }

    private TextField criarTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );
        return field;
    }

    private Button criarBotao(String texto, boolean isPrimary) {
        Button btn = new Button(texto);
        String baseStyle =
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-weight: bold;";

        if (isPrimary) {
            btn.setStyle(baseStyle + "-fx-background-color: #1a237e; -fx-text-fill: white;");
        } else {
            btn.setStyle(baseStyle + "-fx-background-color: #e0e0e0; -fx-text-fill: #424242;");
        }
        return btn;
    }

    private void adicionarCampoAoGrid(GridPane grid, String labelText, Control campo, int linha, int coluna) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        grid.add(label, coluna, linha);
        grid.add(campo, coluna + 1, linha);
    }

    private void carregarHistorico() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql =
                    "SELECT e.*, f.nome as nome_funcionario, eq.descricao as descricao_equipamento " +
                            "FROM emprestimos e " +
                            "JOIN funcionarios f ON e.idFuncionario = f.id " +
                            "JOIN equipamentos eq ON e.idEquipamento = eq.id " +
                            "ORDER BY e.dataSaida DESC";

            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            ObservableList<Emprestimos> emprestimos = FXCollections.observableArrayList();

            while (rs.next()) {
                Emprestimos emp = criarEmprestimoDoResultSet(rs);
                emprestimos.add(emp);
            }

            tabelaHistorico.getItems().clear(); // Limpa os itens existentes
            tabelaHistorico.setItems(emprestimos);

        } catch (SQLException e) {
            mostrarErro("Erro ao carregar histórico", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private Emprestimos criarEmprestimoDoResultSet(ResultSet rs) throws SQLException {
        Emprestimos emp = new Emprestimos();
        emp.setId(rs.getInt("id"));
        emp.setIdFuncionario(rs.getString("idFuncionario"));
        emp.setIdEquipamento(rs.getString("idEquipamento"));

        // Verificação de nulos para dataSaida
        Timestamp dataSaida = rs.getTimestamp("dataSaida");
        if (dataSaida != null) {
            emp.setDataSaida(dataSaida.toLocalDateTime());
        }

        // Verificação de nulos para dataRetornoPrevista
        Timestamp dataRetornoPrevista = rs.getTimestamp("dataRetornoPrevista");
        if (dataRetornoPrevista != null) {
            emp.setDataRetornoPrevista(dataRetornoPrevista.toLocalDateTime());
        }

        // Verificação de nulos para dataRetornoEfetiva
        Timestamp dataRetornoEfetiva = rs.getTimestamp("dataRetornoEfetiva");
        if (dataRetornoEfetiva != null) {
            emp.setDataRetornoEfetiva(dataRetornoEfetiva.toLocalDateTime());
        }

        emp.setQuantidadeEmprestimo(rs.getInt("quantidadeEmprestimo"));
        emp.setAtivo(rs.getBoolean("ativo"));
        emp.setObservacoes(rs.getString("observacoes"));

        return emp;
    }

    private void pesquisarHistorico() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            StringBuilder sql = new StringBuilder(
                    "SELECT e.*, f.nome as nome_funcionario, eq.descricao as descricao_equipamento " +
                            "FROM emprestimos e " +
                            "JOIN funcionarios f ON e.idFuncionario = f.id " +
                            "JOIN equipamentos eq ON e.idEquipamento = eq.id " +
                            "WHERE 1=1"
            );

            List<Object> parametros = new ArrayList<>();

            if (dataInicio.getValue() != null) {
                sql.append(" AND DATE(e.dataSaida) >= ?");
                parametros.add(Date.valueOf(dataInicio.getValue()));
            }
            if (dataFim.getValue() != null) {
                sql.append(" AND DATE(e.dataSaida) <= ?");
                parametros.add(Date.valueOf(dataFim.getValue()));
            }
            if (!funcionarioField.getText().isEmpty()) {
                sql.append(" AND f.nome LIKE ?");
                parametros.add("%" + funcionarioField.getText() + "%");
            }
            if (!equipamentoField.getText().isEmpty()) {
                sql.append(" AND eq.descricao LIKE ?");
                parametros.add("%" + equipamentoField.getText() + "%");
            }

            sql.append(" ORDER BY e.dataSaida DESC");
            stmt = conn.prepareStatement(sql.toString());

            // Define os parâmetros
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            rs = stmt.executeQuery();
            ObservableList<Emprestimos> emprestimos = FXCollections.observableArrayList();

            while (rs.next()) {
                Emprestimos emp = criarEmprestimoDoResultSet(rs);
                emprestimos.add(emp);
            }

            tabelaHistorico.getItems().clear(); // Limpa os itens existentes
            tabelaHistorico.setItems(emprestimos);

        } catch (SQLException e) {
            mostrarErro("Erro ao pesquisar histórico", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private void limparFiltros() {
        dataInicio.setValue(null);
        dataFim.setValue(null);
        funcionarioField.clear();
        equipamentoField.clear();
        carregarHistorico();
    }

    private void exportarPDF() {
        // Implementar exportação para PDF
        mostrarInfo("Exportação para PDF", "Funcionalidade em desenvolvimento");
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private String buscarNomeFuncionario(String id) {
        if (id == null) return "";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT nome FROM funcionarios WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nome");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
        return id;
    }

    private String buscarDescricaoEquipamento(String id) {
        if (id == null) return "";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT descricao FROM equipamentos WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("descricao");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
        return id;
    }

}