package trackbug.Forms;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import trackbug.model.entity.Equipamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditarEquipamentoForm extends VBox {
    private final TextField campoId;
    private final TextField campoDescricao;
    private final DatePicker campoDataCompra;
    private final TextField campoPeso;
    private final TextField campoLargura;
    private final TextField campoComprimento;
    private final ComboBox<String> campoTipo;
    private final TextField campoQuantidadeAtual;
    private final TextField campoQuantidadeMinima;
    private final ComboBox<String> campoTipoUso;
    private final CheckBox checkBoxMedidas;
    private final Equipamento equipamento;
    private final Stage stage;

    public EditarEquipamentoForm(Equipamento equipamento) {
        this.equipamento = equipamento;
        this.stage = new Stage();

        setSpacing(30);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: white;");

        // Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        Label titulo = new Label("Editar Equipamento");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1a237e; -fx-font-family: 'Segoe UI';");
        Label subtitulo = new Label("Atualize os dados do equipamento");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575; -fx-font-family: 'Segoe UI';");
        header.getChildren().addAll(titulo, subtitulo);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getConnection();

            String sql = "SELECT tipo_uso FROM equipamentos WHERE id = " + equipamento.getId();

            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()){
                equipamento.setTipoUso(rs.getString("tipo_uso"));
            }

        }catch (SQLException e){
            mostrarErro("ERRO", "CAUSA: " + e);
        }


        // Container do formulário
        VBox formContainer = new VBox(20);
        formContainer.setMaxWidth(600);
        formContainer.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20px; -fx-background-radius: 5px;");

        // Inicialização dos campos
        campoId = criarCampoTexto(equipamento.getId());
        campoId.setEditable(false);
        campoId.setStyle(campoId.getStyle() + "-fx-background-color: #f5f5f5;");

        campoDescricao = criarCampoTexto(equipamento.getDescricao());

        campoDataCompra = new DatePicker(equipamento.getDataCompra());
        estilizarDatePicker(campoDataCompra);

        Double peso = equipamento.getPeso();
        Double largura = equipamento.getLargura();
        Double comprimento = equipamento.getComprimento();

        campoPeso = criarCampoTexto(peso != null ? peso.toString() : "");
        campoLargura = criarCampoTexto(largura != null ? largura.toString() : "");
        campoComprimento = criarCampoTexto(comprimento != null ? comprimento.toString() : "");

        checkBoxMedidas = new CheckBox("Incluir peso e dimensões");
        checkBoxMedidas.setSelected(peso != null || largura != null || comprimento != null);
        checkBoxMedidas.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");

        campoTipo = new ComboBox<>();
        campoTipo.getItems().addAll("Emprestável", "Consumível");
        campoTipo.setValue(equipamento.isTipo() ? "Consumível" : "Emprestável");
        estilizarComboBox(campoTipo);

        campoTipoUso = new ComboBox<>();
        campoTipoUso.getItems().addAll("Reutilizável", "Uso Único");
        String tipoUso = equipamento.getTipoUso();
        campoTipoUso.setValue(tipoUso != null ? tipoUso  : "Reutilizável");
        estilizarComboBox(campoTipoUso);

        campoQuantidadeAtual = criarCampoTexto(String.valueOf(equipamento.getQuantidadeAtual()));
        campoQuantidadeMinima = criarCampoTexto(String.valueOf(equipamento.getQuantidadeMinima()));

        // Grid para organizar os campos
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // Adicionar campos ao grid
        int row = 0;
        adicionarCampoAoGrid(grid, "Código:", campoId, row++);
        adicionarCampoAoGrid(grid, "Descrição:", campoDescricao, row++);
        adicionarCampoAoGrid(grid, "Data de Compra:", campoDataCompra, row++);
        adicionarCampoAoGrid(grid, "Tipo de Item:", campoTipo, row++);
        adicionarCampoAoGrid(grid, "Tipo de Uso:", campoTipoUso, row++);
        adicionarCampoAoGrid(grid, "Quantidade Atual:", campoQuantidadeAtual, row++);
        adicionarCampoAoGrid(grid, "Quantidade Mínima:", campoQuantidadeMinima, row++);

        grid.add(checkBoxMedidas, 0, row++, 2, 1);

        // Container para medidas
        VBox medidasContainer = new VBox(10);
        medidasContainer.getChildren().addAll(
                criarCampoComLabel("Peso (g):", campoPeso),
                criarCampoComLabel("Largura (cm):", campoLargura),
                criarCampoComLabel("Comprimento (cm):", campoComprimento)
        );
        medidasContainer.visibleProperty().bind(checkBoxMedidas.selectedProperty());
        medidasContainer.managedProperty().bind(checkBoxMedidas.selectedProperty());
        grid.add(medidasContainer, 0, row, 2, 1);

        // Botões
        HBox botoesBox = new HBox(15);
        botoesBox.setAlignment(Pos.CENTER);
        Button btnSalvar = criarBotao("Salvar Alterações", true);
        Button btnCancelar = criarBotao("Cancelar", false);
        botoesBox.getChildren().addAll(btnSalvar, btnCancelar);

        // Eventos
        btnSalvar.setOnAction(e -> salvarAlteracoes());
        btnCancelar.setOnAction(e -> stage.close());

        // Montar formulário
        formContainer.getChildren().addAll(grid, botoesBox);
        getChildren().addAll(header, formContainer);

        // Configurar janela
        Scene scene = new Scene(this);
        stage.setScene(scene);
        stage.setTitle("Editar Equipamento");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(700);
        stage.setMinHeight(800);
    }

    private TextField criarCampoTexto(String valor) {
        TextField campo = new TextField(valor);
        campo.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );
        return campo;
    }

    private void estilizarDatePicker(DatePicker campo) {
        campo.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
    }

    private void estilizarComboBox(ComboBox<String> campo) {
        campo.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
    }

    private Button criarBotao(String texto, boolean isPrimary) {
        Button btn = new Button(texto);
        String baseStyle = "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; " +
                "-fx-padding: 10px 20px; -fx-cursor: hand; -fx-font-weight: bold;";
        if (isPrimary) {
            btn.setStyle(baseStyle + "-fx-background-color: #1a237e; -fx-text-fill: white;");
        } else {
            btn.setStyle(baseStyle + "-fx-background-color: #e0e0e0; -fx-text-fill: #424242;");
        }
        return btn;
    }

    private HBox criarCampoComLabel(String labelText, Control campo) {
        HBox container = new HBox(10);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        container.getChildren().addAll(label, campo);
        return container;
    }

    private void adicionarCampoAoGrid(GridPane grid, String labelText, Control campo, int linha) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        grid.add(label, 0, linha);
        grid.add(campo, 1, linha);
        GridPane.setHgrow(campo, Priority.ALWAYS);
    }

    private boolean validarCampos() {
        StringBuilder erros = new StringBuilder();

        if (campoDescricao.getText().trim().isEmpty()) {
            erros.append("- A descrição é obrigatória\n");
        }

        if (campoDataCompra.getValue() == null) {
            erros.append("- A data de compra é obrigatória\n");
        }

        try {
            Integer.parseInt(campoQuantidadeAtual.getText().trim());
        } catch (NumberFormatException e) {
            erros.append("- A quantidade atual deve ser um número válido\n");
        }

        try {
            Integer.parseInt(campoQuantidadeMinima.getText().trim());
        } catch (NumberFormatException e) {
            erros.append("- A quantidade mínima deve ser um número válido\n");
        }

        if (checkBoxMedidas.isSelected()) {
            try {
                if (!campoPeso.getText().trim().isEmpty()) {
                    Double.parseDouble(campoPeso.getText().trim());
                }
                if (!campoLargura.getText().trim().isEmpty()) {
                    Double.parseDouble(campoLargura.getText().trim());
                }
                if (!campoComprimento.getText().trim().isEmpty()) {
                    Double.parseDouble(campoComprimento.getText().trim());
                }
            } catch (NumberFormatException e) {
                erros.append("- As medidas devem ser números válidos\n");
            }
        }

        if (erros.length() > 0) {
            mostrarErro("Erro de Validação", erros.toString());
            return false;
        }

        return true;
    }

    private void salvarAlteracoes() {
        if (!validarCampos()) {
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); // Inicia transação

            // Cria uma cópia do equipamento antes das alterações
            Equipamento equipamentoAntigo = new Equipamento();
            equipamentoAntigo.setId(equipamento.getId());
            equipamentoAntigo.setDescricao(equipamento.getDescricao());
            equipamentoAntigo.setDataCompra(equipamento.getDataCompra());
            equipamentoAntigo.setPeso(equipamento.getPeso());
            equipamentoAntigo.setLargura(equipamento.getLargura());
            equipamentoAntigo.setComprimento(equipamento.getComprimento());
            equipamentoAntigo.setTipo(equipamento.isTipo());
            equipamentoAntigo.setQuantidadeAtual(equipamento.getQuantidadeAtual());
            equipamentoAntigo.setQuantidadeMinima(equipamento.getQuantidadeMinima());
            equipamentoAntigo.setTipoUso(equipamento.getTipoUso());

            String sql = "UPDATE equipamentos SET " +
                    "descricao = ?, dataCompra = ?, " +
                    "peso = ?, largura = ?, comprimento = ?, " +
                    "tipo = ?, quantidadeAtual = ?, quantidadeEstoque = ?," +
                    "quantidadeMinima = ?, tipo_uso = ?, status = ? " +
                    "WHERE id = ?";

            stmt = conn.prepareStatement(sql);
            int paramIndex = 1;

            stmt.setString(1, campoDescricao.getText().trim());
            stmt.setDate(2, java.sql.Date.valueOf(campoDataCompra.getValue()));

            if (checkBoxMedidas.isSelected()) {
                String pesoText = campoPeso.getText().trim();
                String larguraText = campoLargura.getText().trim();
                String comprimentoText = campoComprimento.getText().trim();

                stmt.setDouble(3, pesoText.isEmpty() ? 0 : Double.parseDouble(pesoText));
                stmt.setDouble(4, larguraText.isEmpty() ? 0 : Double.parseDouble(larguraText));
                stmt.setDouble(5, comprimentoText.isEmpty() ? 0 : Double.parseDouble(comprimentoText));
            } else {
                stmt.setNull(3, java.sql.Types.DOUBLE);
                stmt.setNull(4, java.sql.Types.DOUBLE);
                stmt.setNull(5, java.sql.Types.DOUBLE);
            }

            stmt.setBoolean(6, "Consumível".equals(campoTipo.getValue()));
            stmt.setInt(7, Integer.parseInt(campoQuantidadeAtual.getText().trim()));
            stmt.setInt(8, Integer.parseInt(campoQuantidadeAtual.getText().trim()));
            stmt.setInt(9, Integer.parseInt(campoQuantidadeMinima.getText().trim()));
            stmt.setString(10, campoTipoUso.getValue());
            if(equipamentoAntigo.getQuantidadeAtual() == 0 && !campoQuantidadeAtual.getText().equals("0")){
                stmt.setString(11, "Disponível");
            }else {
                stmt.setString(11, "Esgotado");
            }
            stmt.setString(12, campoId.getText());


            stmt.executeUpdate();

            // Registrar alterações no histórico
            registrarAlteracoes(equipamentoAntigo, conn);

            conn.commit(); // Confirma transação
            mostrarSucesso("Equipamento atualizado com sucesso!");
            stage.close();
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // Desfaz alterações em caso de erro
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            mostrarErro("Erro ao atualizar equipamento", e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
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
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public void mostrar() {
        stage.showAndWait();
    }

    private void registrarAlteracoes(Equipamento equipamentoAntigo, Connection conn) throws SQLException {
        StringBuilder detalhes = new StringBuilder();
        String usuarioNome = SessionManager.getUsuarioLogado().getNome();

        // Comparar descrição
        if (!equipamentoAntigo.getDescricao().equals(campoDescricao.getText().trim())) {
            detalhes.append("Descrição alterada de '")
                    .append(equipamentoAntigo.getDescricao())
                    .append("' para '")
                    .append(campoDescricao.getText().trim())
                    .append("'\n");
        }

        // Comparar quantidade
        int novaQuantidade = Integer.parseInt(campoQuantidadeAtual.getText().trim());
        if (equipamentoAntigo.getQuantidadeAtual() != novaQuantidade) {
            detalhes.append("Quantidade alterada de ")
                    .append(equipamentoAntigo.getQuantidadeAtual())
                    .append(" para ")
                    .append(novaQuantidade)
                    .append("\n");
        }

        // Comparar quantidade mínima
        int novaQuantidadeMinima = Integer.parseInt(campoQuantidadeMinima.getText().trim());
        if (equipamentoAntigo.getQuantidadeMinima() != novaQuantidadeMinima) {
            detalhes.append("Quantidade mínima alterada de ")
                    .append(equipamentoAntigo.getQuantidadeMinima())
                    .append(" para ")
                    .append(novaQuantidadeMinima)
                    .append("\n");
        }

        // Comparar tipo
        boolean novoTipo = "Consumível".equals(campoTipo.getValue());
        if (equipamentoAntigo.isTipo() != novoTipo) {
            detalhes.append("Tipo alterado de '")
                    .append(equipamentoAntigo.isTipo() ? "Consumível" : "Emprestável")
                    .append("' para '")
                    .append(novoTipo ? "Consumível" : "Emprestável")
                    .append("'\n");
        }

        // Medidas (com verificação segura de null)
        if (checkBoxMedidas.isSelected()) {
            // Verificar peso
            String pesoText = campoPeso.getText().trim();
            if (!pesoText.isEmpty()) {
                Double pesoNovo = Double.parseDouble(pesoText);
                Double pesoAntigo = equipamentoAntigo.getPeso();

                if (pesoAntigo == null || !pesoAntigo.equals(pesoNovo)) {
                    detalhes.append("Peso alterado de ")
                            .append(pesoAntigo != null ? String.format("%.2f", pesoAntigo) : "não definido")
                            .append(" para ")
                            .append(String.format("%.2f", pesoNovo))
                            .append("\n");
                }
            }

            // Verificar largura
            String larguraText = campoLargura.getText().trim();
            if (!larguraText.isEmpty()) {
                Double larguraNova = Double.parseDouble(larguraText);
                Double larguraAntiga = equipamentoAntigo.getLargura();

                if (larguraAntiga == null || !larguraAntiga.equals(larguraNova)) {
                    detalhes.append("Largura alterada de ")
                            .append(larguraAntiga != null ? String.format("%.2f", larguraAntiga) : "não definida")
                            .append(" para ")
                            .append(String.format("%.2f", larguraNova))
                            .append("\n");
                }
            }

            // Verificar comprimento
            String comprimentoText = campoComprimento.getText().trim();
            if (!comprimentoText.isEmpty()) {
                Double comprimentoNovo = Double.parseDouble(comprimentoText);
                Double comprimentoAntigo = equipamentoAntigo.getComprimento();

                if (comprimentoAntigo == null || !comprimentoAntigo.equals(comprimentoNovo)) {
                    detalhes.append("Comprimento alterado de ")
                            .append(comprimentoAntigo != null ? String.format("%.2f", comprimentoAntigo) : "não definido")
                            .append(" para ")
                            .append(String.format("%.2f", comprimentoNovo))
                            .append("\n");
                }
            }
        } else if (equipamentoAntigo.getPeso() != 0 ||
                equipamentoAntigo.getLargura() != 0 ||
                equipamentoAntigo.getComprimento() != 0) {
            detalhes.append("Medidas removidas\n");
        }

        // Registrar no log se houver alterações
        if (detalhes.length() > 0) {
            String sql = "INSERT INTO log_equipamentos (id_equipamento, descricao, acao, data_acao, detalhes) " +
                    "VALUES (?, ?, 'EDICAO', NOW(), ?)";

            try (PreparedStatement stmtLog = conn.prepareStatement(sql)) {
                stmtLog.setString(1, equipamentoAntigo.getId());
                stmtLog.setString(2, "Equipamento editado por " + usuarioNome);
                stmtLog.setString(3, detalhes.toString());
                stmtLog.executeUpdate();
            }
        }
    }

}