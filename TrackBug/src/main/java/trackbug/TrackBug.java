package trackbug;

import java.util.Scanner;

import static trackbug.ConnectionFactory.getConnection;

public class TrackBug {
    // Declaração de um Scanner estático para capturar entradas do usuário
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Chama o metodo que estabelece conexao com o banco de dadosasdadasd
        getConnection();

        // Exibe o menu principal do sistema
        exibirmenu();
    }

    // Metodo responsável por exibir o menu e capturar as opções do usuário
    public static void exibirmenu() {
        while (true) { // Loop infinito para manter o menu ativo até o usuário escolher sair
            System.out.println("\n1. Registrar Empréstimo");
            System.out.println("2. Registrar Devolução");
            System.out.println("3. Listar empréstimos ativos");
            System.out.println("4. Listar empréstimos em atraso");
            System.out.println("5. Registrar Equipamento");
            System.out.println("6. Listar Equipamentos");
            System.out.println("7. Cadastrar Funcionários");
            System.out.println("8. Listar Funcionários");
            System.out.println("9. Histórico de empréstimos.");
            System.out.println("10. Sair");
            System.out.println("Escolha uma opção inserindo o número: ");

            // Captura a escolha do usuário
            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer do scanner após capturar o número

            // Avalia a opção do usuário e executa a ação correspondente
            switch(opcao) {
                case 1 -> {
                    // Registrar um novo empréstimo
                    Emprestimos emprestimo = new Emprestimos();
                    emprestimo.registrarEmprestimo(emprestimo);
                }
                case 2 -> {
                    // Registrar a devolução de um empréstimo
                    Emprestimos emprestimo = new Emprestimos();
                    emprestimo.registrarDevolucao(emprestimo);
                }
                case 3 -> {
                    // Listar todos os empréstimos ativos
                    Emprestimos emprestimo = new Emprestimos();
                    emprestimo.listarEmprestimosAtivos(emprestimo);
                }
                case 4 -> {
                    // Listar todos os empréstimos em atraso
                    Emprestimos emprestimo = new Emprestimos();
                    emprestimo.listarEmprestimosAtrasados(emprestimo);
                }
                case 5 -> {
                    // Registrar um novo equipamento
                    Equipamento equipamento = new Equipamento();
                    equipamento.registrarEquipamento(equipamento);
                }
                case 6 -> {
                    // Listar todos os equipamentos cadastrados
                    Equipamento equipamento = new Equipamento();
                    equipamento.listarEquipamentos(equipamento);
                }
                case 7 -> {
                    // Registrar um novo funcionário
                    Funcionario funcionario = new Funcionario();
                    funcionario.registrarFuncionario(funcionario);
                }
                case 8 -> {
                    // Listar todos os funcionários cadastrados
                    Funcionario funcionario = new Funcionario();
                    int i = funcionario.listarFuncionarios(funcionario);
                }
                case 9 -> {
                    // Listar todos os empréstimos já feitos.
                    Emprestimos emprestimo = new Emprestimos();
                    emprestimo.historicoDeEmprestimos(emprestimo);
                }
                case 10 -> {
                    // Sair do programa
                    System.out.println("Saindo do programa...");
                    scanner.close(); // Fecha o Scanner antes de sair
                    return; // Sai do loop e termina a execução do programa
                }
                default -> {
                    // Caso o usuário insira uma opção inválida
                    System.out.println("Opção Inválida!!");
                }
            }
        }
    }
}
