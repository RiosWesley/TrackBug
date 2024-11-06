package trackbug.Forms;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.format.DateTimeFormatter;

public class HistoricoAlteracoesForm extends VBox {
    private TableView<LogEquipamento> tabelaHistorico;
    private TextField campoBusca;
    private ComboBox<String> filtroAcao;
    private final DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Classe interna para representar o log
    public static class LogEquipamento {
        private final int id;
        private final String idEquipamento;
        private final String descricao;
        private final String acao;
        private final Timestamp dataAcao;
        private final String detalhes;

        public LogEquipamento(int id, String idEquipamento, String descricao,
                              String acao, Timestamp dataAcao, String detalhes) {
            this.id = id;
            this.idEquipamento = idEquipamento;
            this.descricao = descricao;
            this.acao = acao;
            this.dataAcao = dataAcao;
            this.detalhes = detalhes;
        }

        // Getters
        public int getId() { return id; }
        public String getIdEquipamento() { return idEquipamento; }
        public String getDescricao() { return descricao; }
        public String getAcao() { return acao; }
        public Timestamp getDataAcao() { return dataAcao; }
        public String getDetalhes() { return detalhes; }
    }

    public HistoricoAlteracoesForm() {
        setSpacing(30);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: white;");

        // Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        Label titulo = new Label("Histórico de Alterações");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1a237e; -fx-font-family: 'Segoe UI';");
        Label subtitulo = new Label("Visualize todas as alterações realizadas nos equipamentos");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575; -fx-font-family: 'Segoe UI';");
        header.getChildren().addAll(titulo, subtitulo);

        // Barra de ferramentas
        HBox toolBar = new HBox(15);
        toolBar.setAlignment(Pos.CENTER_LEFT);
        toolBar.setPadding(new Insets(10));
        toolBar.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5px;");

        campoBusca = new TextField();
        campoBusca.setPromptText("Buscar por equipamento ou descrição...");
        campoBusca.setPrefWidth(300);
        campoBusca.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );

        filtroAcao = new ComboBox<>();
        filtroAcao.setItems(FXCollections.observableArrayList(
                "Todas", "EDICAO", "EXCLUSAO", "AVARIA"
        ));
        filtroAcao.setValue("Todas");
        filtroAcao.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");

        Button btnAtualizar = new Button("Atualizar Lista");
        btnAtualizar.setStyle(
                "-fx-background-color: #1a237e; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px;"
        );

        toolBar.getChildren().addAll(campoBusca, filtroAcao, btnAtualizar);

        // Tabela de histórico
        tabelaHistorico = new TableView<>();
        tabelaHistorico.setStyle("-fx-font-family: 'Segoe UI';");

        // Modifique as criações de colunas para usar SimpleStringProperty diretamente:
        TableColumn<LogEquipamento, String> colunaData = new TableColumn<>("Data/Hora");
        colunaData.setCellValueFactory(data -> new SimpleStringProperty(
                formatador.format(data.getValue().getDataAcao().toLocalDateTime())
        ));

        TableColumn<LogEquipamento, String> colunaEquipamento = new TableColumn<>("Equipamento");
        colunaEquipamento.setCellValueFactory(data -> new SimpleStringProperty(
                buscarNomeEquipamento(data.getValue().getIdEquipamento())
        ));

        TableColumn<LogEquipamento, String> colunaDescricao = new TableColumn<>("Descrição");
        colunaDescricao.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDescricao()
        ));

        TableColumn<LogEquipamento, String> colunaAcao = new TableColumn<>("Ação");
        colunaAcao.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getAcao()
        ));

        TableColumn<LogEquipamento, String> colunaDetalhes = new TableColumn<>("Detalhes");
        colunaDetalhes.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDetalhes()
        ));

        tabelaHistorico.getColumns().addAll(
                colunaData,
                colunaEquipamento,
                colunaDescricao,
                colunaAcao,
                colunaDetalhes
        );

        // Configurações da tabela
        tabelaHistorico.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabelaHistorico, Priority.ALWAYS);

        // Eventos
        btnAtualizar.setOnAction(e -> carregarHistorico());
        campoBusca.textProperty().addListener((obs, old, novo) -> filtrarHistorico());
        filtroAcao.setOnAction(e -> filtrarHistorico());

        // Adicionar componentes ao layout principal
        getChildren().addAll(header, toolBar, tabelaHistorico);

        // Carregar dados iniciais
        carregarHistorico();
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

    private void carregarHistorico() {
        ObservableList<LogEquipamento> logs = FXCollections.observableArrayList();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM log_equipamentos ORDER BY data_acao DESC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                logs.add(new LogEquipamento(
                        rs.getInt("id"),
                        rs.getString("id_equipamento"),
                        rs.getString("descricao"),
                        rs.getString("acao"),
                        rs.getTimestamp("data_acao"),
                        rs.getString("detalhes")
                ));
            }

            tabelaHistorico.setItems(logs);
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar histórico", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private void filtrarHistorico() {
        String busca = campoBusca.getText().toLowerCase();
        String acao = filtroAcao.getValue();

        ObservableList<LogEquipamento> todosLogs = tabelaHistorico.getItems();
        ObservableList<LogEquipamento> logsFiltrados = FXCollections.observableArrayList();

        for (LogEquipamento log : todosLogs) {
            boolean matchBusca = busca.isEmpty() ||
                    buscarNomeEquipamento(log.getIdEquipamento()).toLowerCase().contains(busca) ||
                    log.getDescricao().toLowerCase().contains(busca) ||
                    log.getDetalhes().toLowerCase().contains(busca);

            boolean matchAcao = acao.equals("Todas") || log.getAcao().equals(acao);

            if (matchBusca && matchAcao) {
                logsFiltrados.add(log);
            }
        }

        tabelaHistorico.setItems(logsFiltrados);
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}