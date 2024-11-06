package trackbug.Forms;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class EmprestimosAtrasoForm extends VBox {
    private TableView<Emprestimos> tabelaAtrasos;
    private ComboBox<String> filtroAtraso;
    private TextField campoBusca;
    private final DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public EmprestimosAtrasoForm() {
        setSpacing(30);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: white;");

        // Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        Label titulo = new Label("Empréstimos em Atraso");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1a237e; -fx-font-family: 'Segoe UI';");
        Label subtitulo = new Label("Monitore e gerencie os empréstimos atrasados");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575; -fx-font-family: 'Segoe UI';");
        header.getChildren().addAll(titulo, subtitulo);

        // Barra de ferramentas
        HBox toolBar = new HBox(15);
        toolBar.setAlignment(Pos.CENTER_LEFT);
        toolBar.setPadding(new Insets(10));
        toolBar.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5px;");

        // Filtro de período de atraso
        filtroAtraso = new ComboBox<>();
        filtroAtraso.setItems(FXCollections.observableArrayList(
                "Todos os atrasos",
                "Até 7 dias",
                "8 a 15 dias",
                "16 a 30 dias",
                "Mais de 30 dias"
        ));
        filtroAtraso.setValue("Todos os atrasos");
        filtroAtraso.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");

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

        Button btnNotificar = new Button("Notificar Selecionados");
        btnNotificar.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: #d32f2f; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-weight: bold;"
        );

        toolBar.getChildren().addAll(filtroAtraso, campoBusca, btnNotificar);

        // Tabela de empréstimos
        tabelaAtrasos = new TableView<>();
        tabelaAtrasos.setStyle("-fx-font-family: 'Segoe UI';");

        TableColumn<Emprestimos, CheckBox> colunaSeletor = new TableColumn<>("");
        colunaSeletor.setCellValueFactory(cellData -> {
            CheckBox checkBox = new CheckBox();
            return new javafx.beans.property.SimpleObjectProperty<>(checkBox);
        });
        colunaSeletor.setStyle("-fx-alignment: CENTER;");

        TableColumn<Emprestimos, String> colunaFuncionario = new TableColumn<>("Funcionário");
        colunaFuncionario.setCellValueFactory(cellData -> {
            String idFuncionario = cellData.getValue().getIdFuncionario();
            String nomeFuncionario = buscarNomeFuncionario(idFuncionario);
            return new javafx.beans.property.SimpleStringProperty(nomeFuncionario);
        });

        TableColumn<Emprestimos, String> colunaEquipamento = new TableColumn<>("Equipamento");
        colunaEquipamento.setCellValueFactory(cellData -> {
            String idEquipamento = cellData.getValue().getIdEquipamento();
            String nomeEquipamento = buscarNomeEquipamento(idEquipamento);
            return new javafx.beans.property.SimpleStringProperty(nomeEquipamento);
        });

        TableColumn<Emprestimos, String> colunaQuantidade = new TableColumn<>("Quantidade");
        colunaQuantidade.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(cellData.getValue().getQuantidadeEmprestimo())));

        TableColumn<Emprestimos, String> colunaDataPrevista = new TableColumn<>("Devolução Prevista");
        colunaDataPrevista.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDataRetornoPrevista().format(formatador)));

        TableColumn<Emprestimos, String> colunaDiasAtraso = new TableColumn<>("Dias em Atraso");
        colunaDiasAtraso.setCellValueFactory(cellData -> {
            long dias = ChronoUnit.DAYS.between(
                    cellData.getValue().getDataRetornoPrevista(),
                    LocalDateTime.now()
            );
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(dias));
        });
        colunaDiasAtraso.setStyle("-fx-alignment: CENTER;");

        tabelaAtrasos.getColumns().addAll(
                colunaSeletor,
                colunaFuncionario,
                colunaEquipamento,
                colunaQuantidade,
                colunaDataPrevista,
                colunaDiasAtraso
        );

        // Configurações da tabela
        tabelaAtrasos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabelaAtrasos, Priority.ALWAYS);

        // Eventos
        filtroAtraso.setOnAction(e -> carregarEmprestimosAtrasados());
        campoBusca.textProperty().addListener((obs, old, novo) -> filtrarEmprestimos(novo));


        // Adiciona componentes ao layout principal
        getChildren().addAll(header, toolBar, tabelaAtrasos);

        // Carrega dados iniciais
        carregarEmprestimosAtrasados();
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
        return idFuncionario;
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
        return idEquipamento;
    }

    private void carregarEmprestimosAtrasados() {
        ObservableList<Emprestimos> emprestimos = FXCollections.observableArrayList();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getConnection();
            // Modifica a query para excluir itens consumíveis
            String sql = "SELECT e.* FROM emprestimos e " +
                    "JOIN equipamentos eq ON e.idEquipamento = eq.id " +
                    "WHERE e.ativo = true " +
                    "AND e.dataRetornoPrevista < NOW() " +
                    "AND eq.tipo = false"; // tipo = false significa não consumível
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDateTime dataPrevista = rs.getTimestamp("dataRetornoPrevista").toLocalDateTime();
                long diasAtraso = ChronoUnit.DAYS.between(dataPrevista, LocalDateTime.now());

                // Aplicar filtro de período
                boolean incluirRegistro = false;
                String filtroSelecionado = filtroAtraso.getValue();
                switch (filtroSelecionado) {
                    case "Até 7 dias":
                        incluirRegistro = diasAtraso <= 7;
                        break;
                    case "8 a 15 dias":
                        incluirRegistro = diasAtraso > 7 && diasAtraso <= 15;
                        break;
                    case "16 a 30 dias":
                        incluirRegistro = diasAtraso > 15 && diasAtraso <= 30;
                        break;
                    case "Mais de 30 dias":
                        incluirRegistro = diasAtraso > 30;
                        break;
                    default: // "Todos os atrasos"
                        incluirRegistro = true;
                }

                if (incluirRegistro) {
                    Emprestimos emprestimo = new Emprestimos();
                    emprestimo.setId(rs.getInt("id"));
                    emprestimo.setIdFuncionario(rs.getString("idFuncionario"));
                    emprestimo.setIdEquipamento(rs.getString("idEquipamento"));
                    emprestimo.setDataSaida(rs.getTimestamp("dataSaida").toLocalDateTime());
                    emprestimo.setDataRetornoPrevista(dataPrevista);
                    emprestimo.setObservacoes(rs.getString("observacoes"));
                    emprestimo.setQuantidadeEmprestimo(rs.getInt("quantidadeEmprestimo"));
                    emprestimo.setAtivo(rs.getBoolean("ativo"));
                    emprestimos.add(emprestimo);
                }
            }
            tabelaAtrasos.setItems(emprestimos);
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar empréstimos em atraso",
                    "Não foi possível carregar a lista de empréstimos: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private void filtrarEmprestimos(String filtro) {
        if (tabelaAtrasos.getItems() == null) return;

        ObservableList<Emprestimos> todosDados = tabelaAtrasos.getItems();
        ObservableList<Emprestimos> dadosFiltrados = FXCollections.observableArrayList();

        String filtroLowerCase = filtro.toLowerCase();

        for (Emprestimos emprestimo : todosDados) {
            String nomeFuncionario = buscarNomeFuncionario(emprestimo.getIdFuncionario()).toLowerCase();
            String nomeEquipamento = buscarNomeEquipamento(emprestimo.getIdEquipamento()).toLowerCase();

            if (nomeFuncionario.contains(filtroLowerCase) || nomeEquipamento.contains(filtroLowerCase)) {
                dadosFiltrados.add(emprestimo);
            }
        }

        tabelaAtrasos.setItems(dadosFiltrados);
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