package trackbug;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class ListarEquipamentosForm extends VBox {
    private TableView<Equipamento> tabelaEquipamentos;
    private TextField campoBusca;
    private ComboBox<String> filtroTipo;
    private ComboBox<String> filtroStatus;
    private final DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ListarEquipamentosForm() {
        setSpacing(30);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: white;");

        // Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        Label titulo = new Label("Equipamentos Cadastrados");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1a237e; -fx-font-family: 'Segoe UI';");
        Label subtitulo = new Label("Visualize e gerencie os equipamentos do sistema");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575; -fx-font-family: 'Segoe UI';");
        header.getChildren().addAll(titulo, subtitulo);

        // Barra de ferramentas
        HBox toolBar = new HBox(15);
        toolBar.setAlignment(Pos.CENTER_LEFT);
        toolBar.setPadding(new Insets(10));
        toolBar.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5px;");

        campoBusca = new TextField();
        campoBusca.textProperty().addListener((obs, oldValue, newValue) -> filtrarEquipamentos());
        campoBusca.setPromptText("Buscar equipamento...");
        campoBusca.setPrefWidth(300);
        campoBusca.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );

        filtroTipo = new ComboBox<>();
        filtroTipo.setOnAction(e -> filtrarEquipamentos());
        filtroTipo.setItems(FXCollections.observableArrayList(
                "Todos",
                "Emprestáveis",
                "Consumíveis"
        ));
        filtroTipo.setValue("Todos");
        filtroTipo.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");

        filtroStatus = new ComboBox<>();
        filtroStatus.setOnAction(e -> filtrarEquipamentos());
        filtroStatus.setItems(FXCollections.observableArrayList(
                "Todos",
                "Disponível",
                "Em uso",
                "Estoque baixo"
        ));
        filtroStatus.setValue("Todos");
        filtroStatus.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");

        Button btnExportar = new Button("Exportar Lista");
        btnExportar.setStyle(
                "-fx-background-color: #1a237e; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-weight: bold;"
        );

        toolBar.getChildren().addAll(campoBusca, filtroTipo, filtroStatus, btnExportar);

        // Tabela de equipamentos
        tabelaEquipamentos = new TableView<>();
        tabelaEquipamentos.setStyle("-fx-font-family: 'Segoe UI';");

        TableColumn<Equipamento, String> colunaId = new TableColumn<>("Código");
        colunaId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));

        TableColumn<Equipamento, String> colunaDescricao = new TableColumn<>("Descrição");
        colunaDescricao.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescricao()));

        TableColumn<Equipamento, String> colunaDataCompra = new TableColumn<>("Data de Compra");
        colunaDataCompra.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDataCompra().format(formatador)));

        TableColumn<Equipamento, String> colunaPeso = new TableColumn<>("Peso (g)");
        colunaPeso.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("%.2f", cellData.getValue().getPeso())));

        TableColumn<Equipamento, String> colunaDimensoes = new TableColumn<>("Dimensões (cm)");
        colunaDimensoes.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("%.1f x %.1f",
                                cellData.getValue().getLargura(),
                                cellData.getValue().getComprimento())));

        TableColumn<Equipamento, String> colunaQuantidadeAtual = new TableColumn<>("Qtd. Atual");
        colunaQuantidadeAtual.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(cellData.getValue().getQuantidadeAtual())));
        colunaQuantidadeAtual.setStyle("-fx-alignment: CENTER;");

        TableColumn<Equipamento, String> colunaQuantidadeEstoque = new TableColumn<>("Qtd. Total");
        colunaQuantidadeEstoque.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(cellData.getValue().getQuantidadeEstoque())));
        colunaQuantidadeEstoque.setStyle("-fx-alignment: CENTER;");

        TableColumn<Equipamento, String> colunaTipo = new TableColumn<>("Tipo");
        colunaTipo.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().isTipo() ? "Consumível" : "Emprestável"));

        TableColumn<Equipamento, String> colunaStatus = new TableColumn<>("Status");
        colunaStatus.setCellValueFactory(cellData -> {
            Equipamento eq = cellData.getValue();
            String status;
            if (eq.isTipo()) { // Consumível
                if (eq.getQuantidadeAtual() == 0) {
                    status = "Estoque baixo";
                } else if (eq.getQuantidadeAtual() < eq.getQuantidadeEstoque() * 0.2) {
                    status = "Estoque baixo";
                } else {
                    status = "Disponível";
                }
            } else { // Emprestável
                if (eq.getQuantidadeAtual() == eq.getQuantidadeEstoque()) {
                    status = "Disponível";
                } else if (eq.getQuantidadeAtual() == 0) {
                    status = "Em uso";
                } else {
                    status = "Em uso";
                }
            }
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        colunaStatus.setCellFactory(column -> new TableCell<Equipamento, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Disponível":
                            setStyle("-fx-text-fill: #2e7d32;"); // Verde
                            break;
                        case "Em uso":
                            setStyle("-fx-text-fill: #1565c0;"); // Azul
                            break;
                        case "Estoque baixo":
                            setStyle("-fx-text-fill: #c62828;"); // Vermelho
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        TableColumn<Equipamento, Void> colunaAcoes = new TableColumn<>("Ações");
        colunaAcoes.setCellFactory(column -> new TableCell<Equipamento, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnHistorico = new Button("Histórico");

            {
                btnEditar.setStyle(
                        "-fx-background-color: #1a237e; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 12px; " +
                                "-fx-padding: 5px 10px;"
                );
                btnHistorico.setStyle(
                        "-fx-background-color: #757575; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 12px; " +
                                "-fx-padding: 5px 10px;"
                );

                btnEditar.setOnAction(event -> {
                    Equipamento equipamento = getTableView().getItems().get(getIndex());
                    editarEquipamento(equipamento);
                });

                btnHistorico.setOnAction(event -> {
                    Equipamento equipamento = getTableView().getItems().get(getIndex());
                    mostrarHistorico(equipamento);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5);
                    buttons.getChildren().addAll(btnEditar, btnHistorico);
                    setGraphic(buttons);
                }
            }
        });

        tabelaEquipamentos.getColumns().addAll(
                colunaId,
                colunaDescricao,
                colunaDataCompra,
                colunaPeso,
                colunaDimensoes,
                colunaQuantidadeAtual,
                colunaQuantidadeEstoque,
                colunaTipo,
                colunaStatus,
                colunaAcoes
        );

        // Configurações da tabela
        tabelaEquipamentos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabelaEquipamentos, Priority.ALWAYS);

        // Eventos
        campoBusca.textProperty().addListener((obs, old, novo) -> filtrarEquipamentos());
        filtroTipo.setOnAction(e -> filtrarEquipamentos());
        filtroStatus.setOnAction(e -> filtrarEquipamentos());
        btnExportar.setOnAction(e -> exportarLista());

        // Adiciona componentes ao layout principal
        getChildren().addAll(header, toolBar, tabelaEquipamentos);

        // Carrega dados iniciais
        carregarEquipamentos();
    }

    private void carregarEquipamentos() {
        ObservableList<Equipamento> equipamentos = FXCollections.observableArrayList();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM equipamentos ORDER BY descricao";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Equipamento equipamento = new Equipamento();
                equipamento.setId(rs.getString("id"));
                equipamento.setDescricao(rs.getString("descricao"));
                equipamento.setDataCompra(rs.getDate("dataCompra").toLocalDate());
                equipamento.setPeso(rs.getDouble("peso"));
                equipamento.setLargura(rs.getDouble("largura"));
                equipamento.setComprimento(rs.getDouble("comprimento"));
                equipamento.setQuantidadeAtual(rs.getInt("quantidadeAtual"));
                equipamento.setQuantidadeEstoque(rs.getInt("quantidadeEstoque"));
                equipamento.setTipo(rs.getBoolean("tipo"));

                equipamentos.add(equipamento);
            }

            tabelaEquipamentos.setItems(equipamentos);

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao carregar equipamentos");
            alert.setContentText("Não foi possível carregar a lista de equipamentos: " + e.getMessage());
            alert.showAndWait();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private void filtrarEquipamentos() {
        // Se a tabela ainda não foi inicializada, retorne
        if (tabelaEquipamentos.getItems() == null) return;

        // Obtém todos os equipamentos novamente se não houver filtros ativos
        if (campoBusca.getText().isEmpty() &&
                filtroTipo.getValue().equals("Todos") &&
                filtroStatus.getValue().equals("Todos")) {
            carregarEquipamentos();
            return;
        }

        // Caso contrário, aplica os filtros na lista atual
        FilteredList<Equipamento> dadosFiltrados = new FilteredList<>(tabelaEquipamentos.getItems());
        dadosFiltrados.setPredicate(equipamento -> {
            boolean matchBusca = campoBusca.getText().isEmpty() ||
                    equipamento.getDescricao().toLowerCase().contains(campoBusca.getText().toLowerCase()) ||
                    equipamento.getId().toLowerCase().contains(campoBusca.getText().toLowerCase());

            boolean matchTipo = filtroTipo.getValue().equals("Todos") ||
                    (filtroTipo.getValue().equals("Emprestáveis") && !equipamento.isTipo()) ||
                    (filtroTipo.getValue().equals("Consumíveis") && equipamento.isTipo());

            String status;
            if (equipamento.isTipo()) {
                if (equipamento.getQuantidadeAtual() == 0 ||
                        equipamento.getQuantidadeAtual() < equipamento.getQuantidadeEstoque() * 0.2) {
                    status = "Estoque baixo";
                } else {
                    status = "Disponível";
                }
            } else {
                if (equipamento.getQuantidadeAtual() == equipamento.getQuantidadeEstoque()) {
                    status = "Disponível";
                } else {
                    status = "Em uso";
                }
            }

            boolean matchStatus = filtroStatus.getValue().equals("Todos") ||
                    filtroStatus.getValue().equals(status);

            return matchBusca && matchTipo && matchStatus;
        });

        tabelaEquipamentos.setItems(dadosFiltrados);
    }
    private void editarEquipamento(Equipamento equipamento) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Editar Equipamento");
        alert.setHeaderText("Funcionalidade em desenvolvimento");
        alert.setContentText("A edição do equipamento " + equipamento.getDescricao() + " será implementada em breve.");
        alert.showAndWait();
    }

    private void mostrarHistorico(Equipamento equipamento) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Histórico do Equipamento");
        alert.setHeaderText("Funcionalidade em desenvolvimento");
        alert.setContentText("O histórico do equipamento " + equipamento.getDescricao() + " será implementado em breve.");
        alert.showAndWait();
    }

    private void exportarLista() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportar Lista");
        alert.setHeaderText("Funcionalidade em desenvolvimento");
        alert.setContentText("A exportação da lista será implementada em breve.");
        alert.showAndWait();
    }
}