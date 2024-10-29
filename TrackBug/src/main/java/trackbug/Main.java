package trackbug;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private VBox areaPrincipal;

    @Override
    public void start(Stage primaryStage) {
        // Exibe a tela de login
        LoginForm loginForm = new LoginForm();
        Scene loginScene = new Scene(loginForm, 1000, 900);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Login - TrackBug");
        primaryStage.show();
    }

    public void carregarTelaPrincipal(Stage primaryStage) {
        // Layout principal
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f4f4;");

        // Menu lateral com gradiente e sombra
        VBox menuLateral = new VBox(5);
        menuLateral.setPadding(new Insets(15));
        menuLateral.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #1a237e, #0d47a1);" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 0);"
        );
        menuLateral.setPrefWidth(250);

        // Título do sistema com ícone
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 20, 0));

        Label titulo = new Label("TrackBug");
        titulo.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;"
        );

        Label subtitulo = new Label("Sistema de Gerenciamento");
        subtitulo.setStyle(
                "-fx-text-fill: #90caf9;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;"
        );

        headerBox.getChildren().addAll(titulo, subtitulo);

        // Seções do menu
        Label labelEmprestimos = criarLabelSecao("EMPRÉSTIMOS");
        Label labelEquipamentos = criarLabelSecao("EQUIPAMENTOS");
        Label labelFuncionarios = criarLabelSecao("FUNCIONÁRIOS");
        Label labelRelatorios = criarLabelSecao("RELATÓRIOS");

        // Botões
        Button btnEmprestimos = criarBotaoMenu("Registrar Empréstimo", "📋");
        Button btnDevolucao = criarBotaoMenu("Registrar Devolução", "↩");
        Button btnListarAtivos = criarBotaoMenu("Empréstimos Ativos", "📊");
        Button btnListarAtrasos = criarBotaoMenu("Empréstimos em Atraso", "⚠");

        Button btnRegistrarEquip = criarBotaoMenu("Registrar Equipamento", "📦");
        Button btnListarEquip = criarBotaoMenu("Listar Equipamentos", "📋");

        Button btnCadastrarFunc = criarBotaoMenu("Cadastrar Funcionários", "👤");
        Button btnListarFunc = criarBotaoMenu("Listar Funcionários", "👥");

        Button btnHistorico = criarBotaoMenu("Histórico", "📅");

        // Área principal
        areaPrincipal = new VBox(20);
        areaPrincipal.setAlignment(Pos.CENTER);
        areaPrincipal.setStyle(
                "-fx-background-color: white;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        );
        areaPrincipal.setPadding(new Insets(30));

        // Bem-vindo inicial
        VBox welcomeBox = new VBox(15);
        welcomeBox.setAlignment(Pos.CENTER);

        Label bemVindo = new Label("Bem-vindo ao Sistema de Gerenciamento");
        bemVindo.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1a237e;" +
                        "-fx-font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;"
        );

        Label instrucoes = new Label("Selecione uma opção no menu lateral para começar");
        instrucoes.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #757575;" +
                        "-fx-font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;"
        );

        welcomeBox.getChildren().addAll(bemVindo, instrucoes);
        areaPrincipal.getChildren().add(welcomeBox);

        // ScrollPane para a área principal
        ScrollPane scrollPane = new ScrollPane(areaPrincipal);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: #f4f4f4;");

        // Adiciona todos os elementos ao menu lateral
        menuLateral.getChildren().addAll(
                headerBox,
                labelEmprestimos,
                btnEmprestimos,
                btnDevolucao,
                btnListarAtivos,
                btnListarAtrasos,
                labelEquipamentos,
                btnRegistrarEquip,
                btnListarEquip,
                labelFuncionarios,
                btnCadastrarFunc,
                btnListarFunc,
                labelRelatorios,
                btnHistorico
        );

        // Event handlers dos botões
        btnEmprestimos.setOnAction(e -> {
            areaPrincipal.getChildren().clear();
            areaPrincipal.getChildren().add(new EmprestimoForm());
        });

        btnDevolucao.setOnAction(e -> {
            areaPrincipal.getChildren().clear();
            areaPrincipal.getChildren().add(new DevolucaoForm());
        });

        btnListarAtivos.setOnAction(e -> {
            areaPrincipal.getChildren().clear();
            areaPrincipal.getChildren().add(new EmprestimosAtivosForm());
        });

        btnListarAtrasos.setOnAction(e -> {
            areaPrincipal.getChildren().clear();
            areaPrincipal.getChildren().add(new EmprestimosAtrasoForm());
        });

        btnRegistrarEquip.setOnAction(e -> {
            areaPrincipal.getChildren().clear();
            areaPrincipal.getChildren().add(new RegistrarEquipamentoForm());
        });

        btnListarEquip.setOnAction(e -> {
            areaPrincipal.getChildren().clear();
            areaPrincipal.getChildren().add(new ListarEquipamentosForm());
        });

        btnCadastrarFunc.setOnAction(e -> {
            areaPrincipal.getChildren().clear();
            areaPrincipal.getChildren().add(new CadastrarFuncionarioForm());
        });

        btnListarFunc.setOnAction(e -> {
            areaPrincipal.getChildren().clear();
            areaPrincipal.getChildren().add(new ListarFuncionariosForm());
        });

        btnHistorico.setOnAction(e -> {
            areaPrincipal.getChildren().clear();
            areaPrincipal.getChildren().add(new HistoricoEmprestimosForm());
        });

        // Layout final
        root.setLeft(menuLateral);
        root.setCenter(scrollPane);

        // Configuração da janela
        Scene mainScene = new Scene(root, 1200, 800);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("TrackBug - Sistema de Gerenciamento");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    private Label criarLabelSecao(String texto) {
        Label label = new Label(texto);
        label.setStyle(
                "-fx-text-fill: #90caf9;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 15 0 5 0;" +
                        "-fx-font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;"
        );
        return label;
    }

    private Button criarBotaoMenu(String texto, String icone) {
        Button btn = new Button(icone + " " + texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-alignment: CENTER_LEFT;" +
                        "-fx-padding: 10px;" +
                        "-fx-font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;" +
                        "-fx-cursor: hand;"
        );

        // Efeito hover
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: rgba(255,255,255,0.1);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-alignment: CENTER_LEFT;" +
                        "-fx-padding: 10px;" +
                        "-fx-font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;" +
                        "-fx-cursor: hand;"
        ));

        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-alignment: CENTER_LEFT;" +
                        "-fx-padding: 10px;" +
                        "-fx-font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;" +
                        "-fx-cursor: hand;"
        ));

        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
