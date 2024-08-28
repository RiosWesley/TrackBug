package trackbug;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class TrackBug {
    private static Scanner scanner = new Scanner(System.in);
    private static Emprestimos emprestimoAtual; 
    private static List<Funcionario> listafuncionarios = new ArrayList <>();
    private static List<Equipamento> listaEquipamentos = new ArrayList <>();
    private static List<Emprestimos> listaEmprestimos = new ArrayList <>();
        public static void main(String[] args) {
            Funcionario.carregarFuncionarios(listafuncionarios);
            System.out.println("Funcionarios Carregados: "+ listafuncionarios.size());
            Equipamento.carregarEquipamentos(listaEquipamentos);
            System.out.println("Equipamentos Carregados: " + listaEquipamentos.size());
            Emprestimos.carregarEmprestimos(listaEmprestimos, listafuncionarios, listaEquipamentos);
            System.out.println("Emprestimos ativos carregados: " + listaEmprestimos.size());

            exibirmenu();
        }


    public static void exibirmenu(){
        while (true){
            System.out.println("\n1. Registrar Emprestimo");
            System.out.println("2. Registrar Devolucao");
            System.out.println("3. Listar emprestimos ativos");
            System.out.println("4. Listar emprestimos em atraso");
            System.out.println("5. Registrar Equipamento");
            System.out.println("6. Listar Equipamentos");
            System.out.println("7. Cadastrar Funcionarios");
            System.out.println("8. Listar Funcionarios");
            System.out.println("9. Sair");
            System.out.println("Escolha uma opcao inserindo o numero: ");
            
            int opcao = scanner.nextInt();
            scanner.nextLine();
            
            switch(opcao){
                case 1:
                    Emprestimos.registrarEmprestimo(listaEmprestimos, listafuncionarios, listaEquipamentos, scanner);
                    break;
                case 2:
                    Emprestimos.registrarDevolucao(listaEmprestimos, scanner);
                    break;
                case 3:
                    Emprestimos.listarEmprestimosAtivos(listaEmprestimos);
                    break;
                case 4:
                    System.out.println("Opcao em desenvolvimento!!");
                    break;
                case 5:
                    Equipamento.registrarequipamento(listaEquipamentos);
                    break;
                case 6:
                    Equipamento.listarEquipamentos(listaEquipamentos);
                    break;
                case 7:
                    Funcionario.registrarFuncionario(listafuncionarios);
                    break;
                case 8:
                    Funcionario.listarfuncionarios(listafuncionarios);
                    break;    
                case 9:
                    System.out.println("Saindo do programa...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opcao Invalida!!");
            }
        }
    }
   

    
    }


