package trackbug;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistoricoAvariasForm extends VBox {
    private TableView<RegistroAvaria> tabelaAvarias;
    private TextField equipamentoField;
    private final DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public class RegistroAvaria {
        private int id;
        private String idEquipamento;
        private int quantidade;
        private String descricao;
        private LocalDateTime data;

        // Getters e Setters atualizados
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getIdEquipamento() { return idEquipamento; }
        public void setIdEquipamento(String idEquipamento) { this.idEquipamento = idEquipamento; }

        public int getQuantidade() { return quantidade; }
        public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public LocalDateTime getData() { return data; }
        public void setData(LocalDateTime data) { this.data = data; }
    }

    public HistoricoAvariasForm() {
        setSpacing(30);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: white;");

        // Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        Label titulo = new Label("Histórico de Avarias");
        titulo.setStyle(
                "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #1a237e; " +
                        "-fx-font-family: 'Segoe UI';"
        );
        Label subtitulo = new Label("Consulte o histórico de avarias dos equipamentos");
        subtitulo.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-text-fill: #757575; " +
                        "-fx-font-family: 'Segoe UI';"
        );
        header.getChildren().addAll(titulo, subtitulo);

        // Barra de ferramentas
        HBox toolBar = new HBox(15);
        toolBar.setAlignment(Pos.CENTER_LEFT);
        toolBar.setPadding(new Insets(10));
        toolBar.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5px;");

        equipamentoField = new TextField();
        equipamentoField.setPromptText("Buscar por equipamento...");
        equipamentoField.setPrefWidth(300);
        equipamentoField.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );

        Button btnPesquisar = new Button("Pesquisar");
        btnPesquisar.setStyle(
                "-fx-background-color: #1a237e; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px;"
        );

        toolBar.getChildren().addAll(equipamentoField, btnPesquisar);

        // Tabela de avarias
        tabelaAvarias = new TableView<>();
        tabelaAvarias.setStyle("-fx-font-family: 'Segoe UI';");

        TableColumn<RegistroAvaria, String> colunaEquipamento = new TableColumn<>("Equipamento");
        colunaEquipamento.setCellValueFactory(data -> {
            String nomeEquipamento = buscarNomeEquipamento(data.getValue().getIdEquipamento());
            return new javafx.beans.property.SimpleStringProperty(nomeEquipamento);
        });

        TableColumn<RegistroAvaria, String> colunaData = new TableColumn<>("Data do Registro");
        colunaData.setCellValueFactory(data -> {
            if (data.getValue().getData() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getData().format(formatador)
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        TableColumn<RegistroAvaria, Number> colunaQuantidade = new TableColumn<>("Quantidade");
        colunaQuantidade.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getQuantidade())
        );
        colunaQuantidade.setStyle("-fx-alignment: CENTER;");

        TableColumn<RegistroAvaria, String> colunaDescricao = new TableColumn<>("Descrição");
        colunaDescricao.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDescricao())
        );

        tabelaAvarias.getColumns().addAll(
                colunaEquipamento,
                colunaData,
                colunaQuantidade,
                colunaDescricao
        );

        tabelaAvarias.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabelaAvarias, Priority.ALWAYS);

        // Eventos
        btnPesquisar.setOnAction(e -> carregarAvarias(equipamentoField.getText()));
        equipamentoField.setOnAction(e -> carregarAvarias(equipamentoField.getText()));

        // Adicionar componentes ao layout principal
        getChildren().addAll(header, toolBar, tabelaAvarias);

        // Carregar dados iniciais
        carregarAvarias("");
    }

    private void carregarAvarias(String filtro) {
        ObservableList<RegistroAvaria> avarias = FXCollections.observableArrayList();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            StringBuilder sql = new StringBuilder(
                    "SELECT a.*, e.descricao as nome_equipamento " +
                            "FROM avarias a " +
                            "JOIN equipamentos e ON a.id_equipamento = e.id " +
                            "WHERE 1=1"
            );

            if (!filtro.isEmpty()) {
                sql.append(" AND (e.descricao LIKE ? OR a.descricao LIKE ?)");
            }
            sql.append(" ORDER BY a.data DESC");

            stmt = conn.prepareStatement(sql.toString());

            if (!filtro.isEmpty()) {
                String searchTerm = "%" + filtro + "%";
                stmt.setString(1, searchTerm);
                stmt.setString(2, searchTerm);
            }

            rs = stmt.executeQuery();

            while (rs.next()) {
                RegistroAvaria avaria = new RegistroAvaria();
                avaria.setId(rs.getInt("id"));
                avaria.setIdEquipamento(rs.getString("id_equipamento"));
                avaria.setQuantidade(rs.getInt("quantidade"));

                Timestamp dataRegistro = rs.getTimestamp("data");
                if (dataRegistro != null) {
                    avaria.setData(dataRegistro.toLocalDateTime());
                }

                avaria.setDescricao(rs.getString("descricao"));
                avarias.add(avaria);
            }

            tabelaAvarias.setItems(avarias);

            if (avarias.isEmpty() && !filtro.isEmpty()) {
                mostrarMensagem("Nenhuma avaria encontrada para o filtro especificado.",
                        Alert.AlertType.INFORMATION);
            }

        } catch (SQLException e) {
            mostrarMensagem("Erro ao carregar avarias: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private String buscarNomeEquipamento(String id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement("SELECT descricao FROM equipamentos WHERE id = ?");
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

    private void mostrarMensagem(String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.ERROR ? "Erro" : "Informação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}