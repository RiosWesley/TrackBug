package trackbug.Forms;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import trackbug.model.NivelAcesso;
import trackbug.model.entity.Usuario;
import trackbug.model.entity.UsuarioDAO;

public class RegistroUsuarioForm extends VBox {
    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private TextField nomeField;
    private TextField emailField;
    private ComboBox<NivelAcesso> nivelAcessoCombo;
    private Label mensagemErro;
    private Usuario usuarioExistente;
    private VBox senhaContainer;

    // Construtor para novo usuário
    public RegistroUsuarioForm() {
        this(null);
    }

    // Construtor para edição de usuário existente
    public RegistroUsuarioForm(Usuario usuario) {
        this.usuarioExistente = usuario;

        setSpacing(15);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: white;");

        // Cabeçalho
        Label titulo = new Label(usuario == null ? "Registrar Novo Usuário" : "Editar Usuário");
        titulo.setStyle(
                "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #1a237e; " +
                        "-fx-font-family: 'Segoe UI';"
        );

        Label subtitulo = new Label(usuario == null ?
                "Preencha os dados do novo usuário" :
                "Atualize os dados do usuário");
        subtitulo.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-text-fill: #757575; " +
                        "-fx-font-family: 'Segoe UI';"
        );

        // Mensagem de erro
        mensagemErro = new Label();
        mensagemErro.setTextFill(Color.RED);
        mensagemErro.setVisible(false);

        // Campos do formulário
        VBox form = new VBox(15);
        form.setMaxWidth(400);
        form.setAlignment(Pos.CENTER);

        nomeField = criarCampo("Nome completo");
        emailField = criarCampo("E-mail");
        usernameField = criarCampo("Nome de usuário");

        // Container para campos de senha
        senhaContainer = new VBox(15);
        passwordField = criarPasswordField("Senha");
        confirmPasswordField = criarPasswordField("Confirme a senha");

        if (usuario != null) {
            // Em modo de edição, tornar campos específicos não editáveis
            usernameField.setEditable(false);
            usernameField.setStyle(usernameField.getStyle() + "-fx-background-color: #f5f5f5;");

            // Adicionar checkbox para alterar senha
            CheckBox alterarSenhaCheck = new CheckBox("Alterar senha");
            alterarSenhaCheck.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
            senhaContainer.getChildren().addAll(alterarSenhaCheck, passwordField, confirmPasswordField);

            // Inicialmente desabilitar campos de senha
            passwordField.setDisable(true);
            confirmPasswordField.setDisable(true);

            // Habilitar/desabilitar campos de senha baseado no checkbox
            alterarSenhaCheck.setOnAction(e -> {
                boolean habilitar = alterarSenhaCheck.isSelected();
                passwordField.setDisable(!habilitar);
                confirmPasswordField.setDisable(!habilitar);
                if (!habilitar) {
                    passwordField.clear();
                    confirmPasswordField.clear();
                }
            });
        } else {
            senhaContainer.getChildren().addAll(passwordField, confirmPasswordField);
        }

        nivelAcessoCombo = new ComboBox<>();
        nivelAcessoCombo.getItems().addAll(NivelAcesso.values());
        nivelAcessoCombo.setValue(NivelAcesso.USUARIO);
        nivelAcessoCombo.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px;"
        );

        // Preencher campos se for edição
        if (usuario != null) {
            nomeField.setText(usuario.getNome());
            emailField.setText(usuario.getEmail());
            usernameField.setText(usuario.getUsername());
            nivelAcessoCombo.setValue(NivelAcesso.fromNivel(usuario.getNivelAcesso()));
        }

        // Botões
        HBox botoesBox = new HBox(10);
        botoesBox.setAlignment(Pos.CENTER);

        Button btnSalvar = new Button(usuario == null ? "Registrar" : "Salvar Alterações");
        Button btnCancelar = new Button("Cancelar");

        estilizarBotao(btnSalvar, true);
        estilizarBotao(btnCancelar, false);

        botoesBox.getChildren().addAll(btnSalvar, btnCancelar);

        // Adiciona campos ao formulário
        form.getChildren().addAll(
                criarCampoComLabel("Nome completo:", nomeField),
                criarCampoComLabel("E-mail:", emailField),
                criarCampoComLabel("Nome de usuário:", usernameField),
                senhaContainer,
                criarCampoComLabel("Nível de acesso:", nivelAcessoCombo)
        );

        // Adiciona componentes ao layout principal
        getChildren().addAll(
                titulo,
                subtitulo,
                mensagemErro,
                form,
                botoesBox
        );

        // Eventos
        btnSalvar.setOnAction(e -> salvarUsuario());
        btnCancelar.setOnAction(e -> getScene().getWindow().hide());
    }

    private TextField criarCampo(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(400);
        field.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 8px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );
        return field;
    }

    private PasswordField criarPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setPrefWidth(400);
        field.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 8px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4px;"
        );
        return field;
    }

    private VBox criarCampoComLabel(String labelText, Control campo) {
        VBox container = new VBox(5);
        Label label = new Label(labelText);
        label.setStyle(
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: #424242;"
        );
        container.getChildren().addAll(label, campo);
        return container;
    }

    private void estilizarBotao(Button btn, boolean isPrimary) {
        String baseStyle =
                "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-font-weight: bold; ";

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

    private void salvarUsuario() {
        mensagemErro.setVisible(false);

        if (!validarCampos()) {
            return;
        }

        try {
            if (usuarioExistente == null) {
                // Criar novo usuário
                Usuario novoUsuario = new Usuario(
                        usernameField.getText().trim(),
                        passwordField.getText(),
                        nomeField.getText().trim(),
                        emailField.getText().trim(),
                        nivelAcessoCombo.getValue().getNivel()
                );
                UsuarioDAO.criarUsuario(novoUsuario);
            } else {
                // Atualizar usuário existente
                usuarioExistente.setNome(nomeField.getText().trim());
                usuarioExistente.setEmail(emailField.getText().trim());
                usuarioExistente.setNivelAcesso(nivelAcessoCombo.getValue().getNivel());

                // Atualizar senha apenas se os campos de senha estiverem preenchidos
                if (!passwordField.isDisabled() && !passwordField.getText().isEmpty()) {
                    usuarioExistente.setPassword(passwordField.getText());
                }

                UsuarioDAO.atualizarUsuario(usuarioExistente);
            }

            mostrarSucesso("Usuário " + (usuarioExistente == null ? "registrado" : "atualizado") + " com sucesso!");
            getScene().getWindow().hide();
        } catch (Exception e) {
            mostrarErro("Erro ao " + (usuarioExistente == null ? "registrar" : "atualizar") + " usuário: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (nomeField.getText().trim().isEmpty()) {
            mostrarErro("O nome é obrigatório");
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            mostrarErro("O e-mail é obrigatório");
            return false;
        }

        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mostrarErro("E-mail inválido");
            return false;
        }

        if (usuarioExistente == null && usernameField.getText().trim().isEmpty()) {
            mostrarErro("O nome de usuário é obrigatório");
            return false;
        }

        if ((usuarioExistente == null || !passwordField.isDisabled()) &&
                !passwordField.getText().isEmpty() && passwordField.getText().length() < 6) {
            mostrarErro("A senha deve ter pelo menos 6 caracteres");
            return false;
        }

        if ((usuarioExistente == null || !passwordField.isDisabled()) &&
                !passwordField.getText().equals(confirmPasswordField.getText())) {
            mostrarErro("As senhas não coincidem");
            return false;
        }

        if (nivelAcessoCombo.getValue() == null) {
            mostrarErro("Selecione um nível de acesso");
            return false;
        }

        return true;
    }

    private void mostrarErro(String mensagem) {
        mensagemErro.setText(mensagem);
        mensagemErro.setVisible(true);
    }

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}