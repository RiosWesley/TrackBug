package trackbug.Forms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import trackbug.model.entity.Emprestimo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmprestimosAtivosForm extends VBox {
    private TableView<Emprestimo> tabelaEmprestimos;
    private TextField campoBusca;
    private Button btnAtualizar;
    private final DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public EmprestimosAtivosForm() {
        setSpacing(30);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: white;");

        // Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        Label titulo = new Label("Empréstimos Ativos");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1a237e; -fx-font-family: 'Segoe UI';");
        Label subtitulo = new Label("Visualize e gerencie os empréstimos ativos");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575; -fx-font-family: 'Segoe UI';");
        header.getChildren().addAll(titulo, subtitulo);

        // Barra de ferramentas
        HBox toolBar = new HBox(15);
        toolBar.setAlignment(Pos.CENTER_LEFT);
        toolBar.setPadding(new Insets(10));
        toolBar.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5px;");

        campoBusca = new TextField();
        campoBusca.setPromptText("Buscar por funcionário ou equipamento...");
        campoBusca.setPrefWidth(300);
        campoBusca.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );

        btnAtualizar = new Button("Atualizar Lista");
        btnAtualizar.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: #1a237e; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-weight: bold;"
        );

        toolBar.getChildren().addAll(campoBusca, btnAtualizar);

        // Tabela de empréstimos
        tabelaEmprestimos = new TableView<>();
        tabelaEmprestimos.setStyle("-fx-font-family: 'Segoe UI';");

        TableColumn<Emprestimo, String> colunaId = new TableColumn<>("Código");
        colunaId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getId())));

        TableColumn<Emprestimo, String> colunaFuncionario = new TableColumn<>("Funcionário");
        colunaFuncionario.setCellValueFactory(cellData -> {
            String idFuncionario = cellData.getValue().getIdFuncionario();
            String nomeFuncionario = buscarNomeFuncionario(idFuncionario);
            return new javafx.beans.property.SimpleStringProperty(nomeFuncionario);
        });

        TableColumn<Emprestimo, String> colunaEquipamento = new TableColumn<>("Equipamento");
        colunaEquipamento.setCellValueFactory(cellData -> {
            String idEquipamento = cellData.getValue().getIdEquipamento();
            String nomeEquipamento = buscarNomeEquipamento(idEquipamento);
            return new javafx.beans.property.SimpleStringProperty(nomeEquipamento);
        });

        TableColumn<Emprestimo, String> colunaQuantidade = new TableColumn<>("Quantidade");
        colunaQuantidade.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(cellData.getValue().getQuantidadeEmprestimo())));

        TableColumn<Emprestimo, String> colunaDataSaida = new TableColumn<>("Data de Saída");
        colunaDataSaida.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDataSaida().format(formatador)));

        TableColumn<Emprestimo, String> colunaDataPrevista = new TableColumn<>("Devolução Prevista");
        colunaDataPrevista.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDataRetornoPrevista().format(formatador)));

        TableColumn<Emprestimo, String> colunaStatus = new TableColumn<>("Status");
        colunaStatus.setCellValueFactory(cellData -> {
            LocalDateTime dataPrevista = cellData.getValue().getDataRetornoPrevista();
            String status = LocalDateTime.now().isAfter(dataPrevista) ? "Atrasado" : "Em dia";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        colunaStatus.setCellFactory(column -> new TableCell<Emprestimo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Atrasado")) {
                        setStyle("-fx-text-fill: #c62828;"); // Vermelho
                    } else {
                        setStyle("-fx-text-fill: #2e7d32;"); // Verde
                    }
                }
            }
        });

        tabelaEmprestimos.getColumns().addAll(
                colunaId,
                colunaFuncionario,
                colunaEquipamento,
                colunaQuantidade,
                colunaDataSaida,
                colunaDataPrevista,
                colunaStatus
        );

        // Configurações da tabela
        tabelaEmprestimos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabelaEmprestimos, Priority.ALWAYS);

        // Eventos
        btnAtualizar.setOnAction(e -> carregarEmprestimos());
        campoBusca.textProperty().addListener((obs, old, novo) -> filtrarEmprestimos(novo));

        // Adiciona componentes ao layout principal
        getChildren().addAll(header, toolBar, tabelaEmprestimos);

        // Carrega dados iniciais
        carregarEmprestimos();
    }

    private void carregarEmprestimos() {
        ObservableList<Emprestimo> emprestimos = FXCollections.observableArrayList();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection connEquip = null;
        PreparedStatement stmtEquip = null;
        ResultSet rsEquip = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM emprestimos WHERE ativo = true ORDER BY dataSaida DESC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            connEquip = ConnectionFactory.getConnection();
            String emprestaveis = "SELECT tipo FROM equipamentos WHERE id = ?";
            stmtEquip = connEquip.prepareStatement(emprestaveis);


            while (rs.next()) {

                stmtEquip.setString(1, rs.getString("idEquipamento"));
                rsEquip = stmtEquip.executeQuery();
                while(rsEquip.next()){
                    Emprestimo emprestimo = new Emprestimo();
                    if(rsEquip.getBoolean("tipo") == false) {
                        emprestimo.setId(rs.getInt("id"));
                        emprestimo.setIdFuncionario(rs.getString("idFuncionario"));
                        emprestimo.setIdEquipamento(rs.getString("idEquipamento"));
                        emprestimo.setDataSaida(rs.getTimestamp("dataSaida").toLocalDateTime());
                        emprestimo.setDataRetornoPrevista(rs.getTimestamp("dataRetornoPrevista").toLocalDateTime());
                        emprestimo.setObservacoes(rs.getString("observacoes"));
                        emprestimo.setQuantidadeEmprestimo(rs.getInt("quantidadeEmprestimo"));
                        emprestimo.setAtivo(rs.getBoolean("ativo"));

                        emprestimos.add(emprestimo);
                    }
                }

            }

            tabelaEmprestimos.setItems(emprestimos);

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao carregar empréstimos");
            alert.setContentText("Não foi possível carregar a lista de empréstimos: " + e.getMessage());
            alert.showAndWait();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private void filtrarEmprestimos(String filtro) {
        if (tabelaEmprestimos.getItems() == null) return;

        ObservableList<Emprestimo> todosDados = tabelaEmprestimos.getItems();
        ObservableList<Emprestimo> dadosFiltrados = FXCollections.observableArrayList();

        String filtroLowerCase = filtro.toLowerCase();

        for (Emprestimo emprestimo : todosDados) {
            if (emprestimo.getIdFuncionario().toLowerCase().contains(filtroLowerCase) ||
                    emprestimo.getIdEquipamento().toLowerCase().contains(filtroLowerCase)) {
                dadosFiltrados.add(emprestimo);
            }
        }

        tabelaEmprestimos.setItems(dadosFiltrados);
    }
    private String buscarNomeFuncionario(String idFuncionario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT nome FROM funcionarios WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, idFuncionario);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nome");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
        return idFuncionario; // Retorna o ID caso não encontre o nome
    }

    private String buscarNomeEquipamento(String idEquipamento) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT descricao FROM equipamentos WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, idEquipamento);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("descricao");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
        return idEquipamento; // Retorna o ID caso não encontre a descrição
    }
}