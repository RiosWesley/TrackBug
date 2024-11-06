package trackbug.Forms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ListarEquipamentosForm extends VBox {
    private TableView<Equipamento> tabelaEquipamentos;
    private TextField campoBusca;
    private ComboBox<String> filtroTipo;
    private ComboBox<String> filtroStatus;
    // Formatador para datas (sem hora)
    private final DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    // Formatador para data/hora (para logs e histórico)
    private final DateTimeFormatter formatadorDataHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


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
        filtroTipo.setItems(FXCollections.observableArrayList(
                "Todos",
                "Emprestáveis",
                "Consumíveis"
        ));
        filtroTipo.setValue("Todos");
        filtroTipo.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");

        filtroStatus = new ComboBox<>();
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
                        cellData.getValue().getDataCompra().format(formatadorData)
                )
        );

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

        TableColumn<Equipamento, String> colunaQuantidadeMinima = new TableColumn<>("Qtd. Mínima");
        colunaQuantidadeMinima.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(cellData.getValue().getQuantidadeMinima())));
        colunaQuantidadeMinima.setStyle("-fx-alignment: CENTER;");

        TableColumn<Equipamento, String> colunaTipo = new TableColumn<>("Tipo");
        colunaTipo.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().isTipo() ? "Consumível" : "Emprestável"));

        TableColumn<Equipamento, String> colunaStatus = new TableColumn<>("Status");
        colunaStatus.setCellValueFactory(cellData -> {
            Equipamento eq = cellData.getValue();
            String status;
            if (eq.isTipo()) {
                if (eq.getQuantidadeAtual() < eq.getQuantidadeMinima()) {
                    status = "Estoque baixo";
                } else {
                    status = "Disponível";
                }
            } else {
                if (eq.getQuantidadeAtual() < eq.getQuantidadeMinima()) {
                    status = "Estoque baixo";
                } else if (eq.getQuantidadeAtual() == eq.getQuantidadeEstoque()) {
                    status = "Disponível";
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

        tabelaEquipamentos.getColumns().addAll(
                colunaId,
                colunaDescricao,
                colunaDataCompra,
                colunaQuantidadeAtual,
                colunaQuantidadeEstoque,
                colunaQuantidadeMinima,
                colunaTipo,
                colunaStatus
        );

        // Adiciona a coluna de ações
        configurarColunaAcoes();

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

    private void configurarColunaAcoes() {
        TableColumn<Equipamento, Void> colunaAcoes = new TableColumn<>("Ações");
        colunaAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnDeletar = new Button("Deletar");
            private final Button btnAvaria = new Button("Registrar Avaria");

            {
                estilizarBotaoTabela(btnEditar, "primary");
                estilizarBotaoTabela(btnDeletar, "danger");
                estilizarBotaoTabela(btnAvaria, "warning");

                btnEditar.setOnAction(event -> {
                    Equipamento equipamento = getTableView().getItems().get(getIndex());
                    editarEquipamento(equipamento);
                });

                btnDeletar.setOnAction(event -> {
                    Equipamento equipamento = getTableView().getItems().get(getIndex());
                    deletarEquipamento(equipamento);
                });

                btnAvaria.setOnAction(event -> {
                    Equipamento equipamento = getTableView().getItems().get(getIndex());
                    registrarAvaria(equipamento);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5);
                    hbox.getChildren().addAll(btnEditar, btnDeletar, btnAvaria);
                    setGraphic(hbox);
                }
            }
        });

        tabelaEquipamentos.getColumns().add(colunaAcoes);
    }

    private void estilizarBotaoTabela(Button btn, String tipo) {
        String baseStyle = "-fx-font-family: 'Segoe UI'; -fx-font-size: 12px; -fx-padding: 5px 10px; -fx-cursor: hand;";

        switch (tipo.toLowerCase()) {
            case "primary":
                btn.setStyle(baseStyle + "-fx-background-color: #1a237e; -fx-text-fill: white;");
                btn.setOnMouseEntered(e -> btn.setStyle(baseStyle + "-fx-background-color: #283593; -fx-text-fill: white;"));
                btn.setOnMouseExited(e -> btn.setStyle(baseStyle + "-fx-background-color: #1a237e; -fx-text-fill: white;"));
                break;
            case "danger":
                btn.setStyle(baseStyle + "-fx-background-color: #c62828; -fx-text-fill: white;");
                btn.setOnMouseEntered(e -> btn.setStyle(baseStyle + "-fx-background-color: #d32f2f; -fx-text-fill: white;"));
                btn.setOnMouseExited(e -> btn.setStyle(baseStyle + "-fx-background-color: #c62828; -fx-text-fill: white;"));
                break;
            case "warning":
                btn.setStyle(baseStyle + "-fx-background-color: #f57c00; -fx-text-fill: white;");
                btn.setOnMouseEntered(e -> btn.setStyle(baseStyle + "-fx-background-color: #fb8c00; -fx-text-fill: white;"));
                btn.setOnMouseExited(e -> btn.setStyle(baseStyle + "-fx-background-color: #f57c00; -fx-text-fill: white;"));
                break;
            default:
                btn.setStyle(baseStyle + "-fx-background-color: #e0e0e0; -fx-text-fill: #424242;");
                btn.setOnMouseEntered(e -> btn.setStyle(baseStyle + "-fx-background-color: #bdbdbd; -fx-text-fill: #424242;"));
                btn.setOnMouseExited(e -> btn.setStyle(baseStyle + "-fx-background-color: #e0e0e0; -fx-text-fill: #424242;"));
        }
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
                equipamento.setQuantidadeMinima(rs.getInt("quantidadeMinima"));
                equipamento.setTipo(rs.getBoolean("tipo"));

                equipamentos.add(equipamento);
            }

            tabelaEquipamentos.setItems(equipamentos);
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar equipamentos",
                    "Não foi possível carregar a lista de equipamentos: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private void filtrarEquipamentos() {
        carregarEquipamentos();
        if (tabelaEquipamentos.getItems() == null) return;

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
                if (equipamento.getQuantidadeAtual() < equipamento.getQuantidadeMinima()) {
                    status = "Estoque baixo";
                } else {
                    status = "Disponível";
                }
            } else {
                if (equipamento.getQuantidadeAtual() < equipamento.getQuantidadeMinima()) {
                    status = "Estoque baixo";
                } else if (equipamento.getQuantidadeAtual() == equipamento.getQuantidadeEstoque()) {
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

    private void deletarEquipamento(Equipamento equipamento) {
        // Verifica empréstimos ativos
        if (verificarEmprestimosAtivos(equipamento)) {
            mostrarAlerta("Não é possível excluir",
                    "Este equipamento possui empréstimos ativos. Finalize todos os empréstimos antes de excluí-lo.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Deseja realmente excluir o equipamento?");
        confirmacao.setContentText(
                "Equipamento: " + equipamento.getDescricao() + "\n" +
                        "Código: " + equipamento.getId() + "\n\n" +
                        "Esta ação não poderá ser desfeita e todos os registros relacionados (avarias, logs) serão removidos."
        );

        Button btnSim = (Button) confirmacao.getDialogPane().lookupButton(ButtonType.OK);
        btnSim.setText("Sim, excluir");
        Button btnNao = (Button) confirmacao.getDialogPane().lookupButton(ButtonType.CANCEL);
        btnNao.setText("Não, cancelar");

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            executarDelecao(equipamento);
        }
    }

    private boolean verificarAvariasExistentes(Equipamento equipamento) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT COUNT(*) FROM avarias WHERE id_equipamento = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, equipamento.getId());
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            mostrarErro("Erro ao verificar avarias", e.getMessage());
            return true; // Por segurança, retorna true em caso de erro
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private void executarDelecao(Equipamento equipamento) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            // Deleta o equipamento (as deleções em cascata ocorrerão automaticamente)
            String sqlDelete = "DELETE FROM equipamentos WHERE id = ?";
            stmt = conn.prepareStatement(sqlDelete);
            stmt.setString(1, equipamento.getId());
            stmt.executeUpdate();

            conn.commit();
            mostrarSucesso("Equipamento excluído com sucesso!");
            carregarEquipamentos();
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            mostrarErro("Erro ao excluir equipamento", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    private boolean verificarEmprestimosAtivos(Equipamento equipamento) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT COUNT(*) FROM emprestimos WHERE idEquipamento = ? AND ativo = true";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, equipamento.getId());
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

    private void registrarAvaria(Equipamento equipamento) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Registrar Avaria");
        dialog.setHeaderText("Registrar avaria para: " + equipamento.getDescricao());

        ButtonType confirmarButtonType = new ButtonType("Registrar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmarButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField quantidadeField = new TextField();
        quantidadeField.setPromptText("Quantidade avariada");
        TextArea descricaoField = new TextArea();
        descricaoField.setPromptText("Descrição da avaria");
        descricaoField.setPrefRowCount(3);

        grid.add(new Label("Quantidade:"), 0, 0);
        grid.add(quantidadeField, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(descricaoField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmarButtonType) {
                try {
                    int quantidade = Integer.parseInt(quantidadeField.getText());
                    if (quantidade <= 0 || quantidade > equipamento.getQuantidadeAtual()) {
                        mostrarErro("Quantidade inválida",
                                "A quantidade deve ser maior que zero e menor que a quantidade atual.");
                        return null;
                    }
                    return quantidade + ";" + descricaoField.getText();
                } catch (NumberFormatException e) {
                    mostrarErro("Quantidade inválida", "Digite um número válido.");
                    return null;
                }
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(dados -> {
            String[] partes = dados.split(";");
            int quantidade = Integer.parseInt(partes[0]);
            String descricao = partes[1];
            registrarAvariaDB(equipamento, quantidade, descricao);
        });
    }

    private void registrarAvariaDB(Equipamento equipamento, int quantidade, String descricao) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            // Registrar avaria
            String sqlAvaria = "INSERT INTO avarias (id_equipamento, quantidade, descricao, data) VALUES (?, ?, ?, NOW())";
            stmt = conn.prepareStatement(sqlAvaria);
            stmt.setString(1, equipamento.getId());
            stmt.setInt(2, quantidade);
            stmt.setString(3, descricao);
            stmt.executeUpdate();

            // Atualizar quantidade do equipamento
            String sqlEquip = "UPDATE equipamentos SET quantidadeAtual = quantidadeAtual - ?, quantidadeEstoque = quantidadeEstoque - ? WHERE id = ?";
            stmt = conn.prepareStatement(sqlEquip);
            stmt.setInt(1, quantidade);
            stmt.setInt(2, quantidade);
            stmt.setString(3, equipamento.getId());
            stmt.executeUpdate();

            conn.commit();
            mostrarSucesso("Avaria registrada com sucesso!");
            carregarEquipamentos();
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            mostrarErro("Erro ao registrar avaria", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    private void editarEquipamento(Equipamento equipamento) {
        EditarEquipamentoForm formEdicao = new EditarEquipamentoForm(equipamento);
        formEdicao.mostrar();
        // Recarregar a lista após a edição
        carregarEquipamentos();
    }

    private void exportarLista() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportar Lista");
        alert.setHeaderText("Funcionalidade em desenvolvimento");
        alert.setContentText("A exportação da lista será implementada em breve.");
        alert.showAndWait();
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