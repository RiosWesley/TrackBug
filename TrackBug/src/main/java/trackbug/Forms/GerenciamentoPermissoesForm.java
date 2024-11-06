package trackbug.Forms;

import trackbug.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Optional;

public class GerenciamentoPermissoesForm extends VBox {
    private TableView<Usuario> tabelaUsuarios;
    private TextField campoBusca;
    private ObservableList<Usuario> usuarios;

    public GerenciamentoPermissoesForm() {
        setSpacing(20);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: white;");

        // Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);

        Label titulo = new Label("Gerenciamento de Usuários e Permissões");
        titulo.setStyle(
                "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #1a237e; " +
                        "-fx-font-family: 'Segoe UI';"
        );

        Label subtitulo = new Label("Gerencie os usuários do sistema e suas permissões");
        subtitulo.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-text-fill: #757575; " +
                        "-fx-font-family: 'Segoe UI';"
        );

        header.getChildren().addAll(titulo, subtitulo);

        // Barra de ferramentas
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(10));
        toolbar.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5px;");

        campoBusca = new TextField();
        campoBusca.setPromptText("Buscar por nome, usuário ou email...");
        campoBusca.setPrefWidth(300);
        campoBusca.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );

        Button btnNovo = new Button("Novo Usuário");
        Button btnAtualizar = new Button("Atualizar Lista");

        estilizarBotao(btnNovo, true);
        estilizarBotao(btnAtualizar, false);

        toolbar.getChildren().addAll(campoBusca, btnNovo, btnAtualizar);

        // Tabela de usuários
        tabelaUsuarios = new TableView<>();
        tabelaUsuarios.setStyle("-fx-font-family: 'Segoe UI';");

        TableColumn<Usuario, String> colUsername = new TableColumn<>("Usuário");
        colUsername.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));

        TableColumn<Usuario, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNome()));

        TableColumn<Usuario, String> colEmail = new TableColumn<>("E-mail");
        colEmail.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<Usuario, String> colNivel = new TableColumn<>("Nível de Acesso");
        colNivel.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        NivelAcesso.fromNivel(data.getValue().getNivelAcesso()).getDescricao()
                ));

        TableColumn<Usuario, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().isAtivo() ? "Ativo" : "Inativo"
                ));

        colStatus.setCellFactory(column -> new TableCell<Usuario, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Ativo")) {
                        setStyle("-fx-text-fill: #2e7d32;"); // Verde
                    } else {
                        setStyle("-fx-text-fill: #c62828;"); // Vermelho
                    }
                }
            }
        });

        TableColumn<Usuario, Void> colAcoes = new TableColumn<>("Ações");
        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnAlterarStatus = new Button("Alterar Status");
            private final HBox box = new HBox(5);

            {
                btnEditar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    editarUsuario(usuario);
                });

                btnAlterarStatus.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    alterarStatusUsuario(usuario);
                });

                estilizarBotaoTabela(btnEditar, true);
                estilizarBotaoTabela(btnAlterarStatus, false);

                box.getChildren().addAll(btnEditar, btnAlterarStatus);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tabelaUsuarios.getColumns().addAll(
                colUsername, colNome, colEmail, colNivel, colStatus, colAcoes
        );

        tabelaUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabelaUsuarios, Priority.ALWAYS);

        // Eventos
        btnNovo.setOnAction(e -> mostrarFormularioRegistro());
        btnAtualizar.setOnAction(e -> carregarUsuarios());
        campoBusca.textProperty().addListener((obs, old, novo) -> filtrarUsuarios(novo));

        // Adiciona componentes ao layout
        getChildren().addAll(header, toolbar, tabelaUsuarios);

        // Carrega dados iniciais
        carregarUsuarios();
    }

    private void estilizarBotao(Button btn, boolean isPrimary) {
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
    }

    private void estilizarBotaoTabela(Button btn, boolean isPrimary) {
        String baseStyle =
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 12px; " +
                        "-fx-padding: 5px 10px; " +
                        "-fx-cursor: hand;";

        if (isPrimary) {
            btn.setStyle(baseStyle + "-fx-background-color: #1a237e; -fx-text-fill: white;");
        } else {
            btn.setStyle(baseStyle + "-fx-background-color: #e0e0e0; -fx-text-fill: #424242;");
        }
    }

    private void carregarUsuarios() {
        usuarios = FXCollections.observableArrayList(UsuarioDAO.listarTodos());
        tabelaUsuarios.setItems(usuarios);
    }

    private void filtrarUsuarios(String filtro) {
        if (usuarios == null) return;

        FilteredList<Usuario> dadosFiltrados = new FilteredList<>(usuarios);

        if (filtro == null || filtro.isEmpty()) {
            tabelaUsuarios.setItems(usuarios);
            return;
        }

        String filtroLowerCase = filtro.toLowerCase();

        dadosFiltrados.setPredicate(usuario ->
                usuario.getUsername().toLowerCase().contains(filtroLowerCase) ||
                        usuario.getNome().toLowerCase().contains(filtroLowerCase) ||
                        usuario.getEmail().toLowerCase().contains(filtroLowerCase)
        );

        tabelaUsuarios.setItems(dadosFiltrados);
    }

    private void mostrarFormularioRegistro() {
        Stage stage = new Stage();
        RegistroUsuarioForm registroForm = new RegistroUsuarioForm();
        Scene scene = new Scene(registroForm, 600, 700);
        stage.setScene(scene);
        stage.setTitle("Novo Usuário");
        stage.showAndWait();
        carregarUsuarios();
    }

    private void editarUsuario(Usuario usuario) {
        Stage stage = new Stage();
        RegistroUsuarioForm registroForm = new RegistroUsuarioForm(usuario);
        Scene scene = new Scene(registroForm, 600, 700);
        stage.setScene(scene);
        stage.setTitle("Editar Usuário");
        stage.showAndWait();
        carregarUsuarios();
    }

    private void alterarStatusUsuario(Usuario usuario) {
        String acao = usuario.isAtivo() ? "desativar" : "ativar";
        String status = usuario.isAtivo() ? "inativo" : "ativo";

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Alteração");
        confirmacao.setHeaderText("Deseja " + acao + " o usuário " + usuario.getUsername() + "?");
        confirmacao.setContentText("O usuário ficará " + status + " no sistema.");

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            usuario.setAtivo(!usuario.isAtivo());
            try {
                UsuarioDAO.atualizarStatus(usuario);
                carregarUsuarios();

                Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                sucesso.setTitle("Sucesso");
                sucesso.setHeaderText(null);
                sucesso.setContentText("Status do usuário alterado com sucesso!");
                sucesso.show();
            } catch (Exception e) {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro");
                erro.setHeaderText("Erro ao alterar status do usuário");
                erro.setContentText(e.getMessage());
                erro.show();
            }
        }
    }
}