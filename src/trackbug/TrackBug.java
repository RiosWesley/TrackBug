package trackbug;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;


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
            Emprestimos.carregarEmprestimos(listaEmprestimos);
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
   
    
    





    
  
  
  

  






    
    
    
    
   
    
    
       // Funcionario funcionario = new Funcionario("123", "Joao", "Técnico", "2022-01-01");
        //Equipamento equipamento = new Equipamento("E001", "Smartphone", LocalDate.now(), 7, 20, 20, "Nenhuma Manutencao", "Bom");

       // Emprestimos novoEmprestimo = Emprestimos.registrarEmprestimo(funcionario, equipamento, 7, "Empréstimo para trabalho remoto");
       // System.out.println("Emprestimo registrado para o equipamento: " + novoEmprestimo.getequipamento().getdescricao());
       // System.out.println("Funcionario responsavel: " + novoEmprestimo.getfuncionario().getnome());
        //System.out.println("Data de saida: " + novoEmprestimo.getDataSaida());
        //System.out.println("Data prevista de retorno: " + novoEmprestimo.getDataRetornoPrevista());
    //}

    
    }


