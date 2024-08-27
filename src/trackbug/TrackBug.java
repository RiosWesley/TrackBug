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
            carregarFuncionarios();
            carregarEquipamentos();
            Emprestimos.carregarEmprestimos(listaEmprestimos);
            System.out.println("Emprestimos ativos carregados: " + listaEmprestimos.size());

            exibirmenu();
        }
    public static void carregarFuncionarios() {
    listafuncionarios = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader("funcionarios.txt"))) {
        String linha;
        Funcionario funcionario = null;

        while ((linha = reader.readLine()) != null) {
            if (linha.startsWith("Codigo: ")) {
                String codigo = linha.substring(8);
                String nome = reader.readLine().substring(6); 
                String funcao = reader.readLine().substring(8); 
                String dataAdmissao = reader.readLine().substring(17); 

                funcionario = new Funcionario(codigo, nome, funcao, dataAdmissao);
                listafuncionarios.add(funcionario);
                
                
                reader.readLine();
            }
        }
        System.out.println("Funcionarios carregados com sucesso!");
    } catch (IOException e) {
        System.out.println("Erro ao carregar os funcionários: " + e.getMessage());
    }
    }
   public static void carregarEquipamentos() {
    listaEquipamentos = new ArrayList<>();
    
    try (BufferedReader reader = new BufferedReader(new FileReader("equipamentos.txt"))) {
        String linha;
        
        while ((linha = reader.readLine()) != null) {
            if (linha.startsWith("Codigo: ")) {
                String codigo = linha.substring(8);
                String descricao = reader.readLine().substring(11);
                LocalDate dataCompra = LocalDate.parse(reader.readLine().substring(16));
                double peso = Double.parseDouble(reader.readLine().substring(6));
                double largura = Double.parseDouble(reader.readLine().substring(9));
                double comprimento = Double.parseDouble(reader.readLine().substring(12));
                String historicoManutencao = reader.readLine();
                String estadoConservacao = reader.readLine().substring(23);

                Equipamento equipamento = new Equipamento(codigo, descricao, dataCompra, peso, largura, comprimento, historicoManutencao, estadoConservacao);
                listaEquipamentos.add(equipamento);
                
                reader.readLine(); // Lê a linha de separador "================"
            }
        }
        System.out.println("Equipamentos carregados com sucesso!");
    } catch (IOException e) {
        System.out.println("Erro ao carregar os equipamentos: " + e.getMessage());
    } catch (NumberFormatException e) {
        System.out.println("Erro ao analisar um número: " + e.getMessage());
    } catch (DateTimeParseException e) {
        System.out.println("Erro ao analisar a data: " + e.getParsedString());
    }
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
                    registrarequipamento();
                    break;
                case 6:
                    listarEquipamentos();
                    break;
                case 7:
                    registrarFuncionario();
                    break;
                case 8:
                    listarfuncionarios();
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
   
    public static void listarfuncionarios(){

    System.out.println("Funcionarios cadastrados: \n");
    for (Funcionario f : listafuncionarios) {
        System.out.println("Código: " +f.getcodigo());
        System.out.println("Nome: " +f.getnome());
        System.out.println("Funcao: " +f.getfuncao());
        System.out.println("Data de Admissao: " +f.getdataAdmissao());
        System.out.println("==================");
    }
}
  





    
  
  
  

  






    public static void registrarFuncionario() {
    System.out.println("Digite o código do funcionário: ");
    String codigoFuncionario = scanner.nextLine();
    System.out.println("Digite o nome do funcionário: ");
    String nomeFuncionario = scanner.nextLine();
    System.out.println("Digite a função do funcionário: ");
    String funcaoFuncionario = scanner.nextLine();
    System.out.println("Digite a data de admissão do funcionário (yyyy-mm-dd): ");
    String dataAdmissaoFuncionario = scanner.nextLine();

    Funcionario novoFuncionario = new Funcionario(codigoFuncionario, nomeFuncionario, funcaoFuncionario, dataAdmissaoFuncionario);
    listafuncionarios.add(novoFuncionario);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter("funcionarios.txt", true))) {
        writer.write("Codigo: " + novoFuncionario.getcodigo() + "\n");
        writer.write("Nome: " + novoFuncionario.getnome() + "\n");
        writer.write("Funcao: " + novoFuncionario.getfuncao() + "\n");
        writer.write("Data de Admissao: " + novoFuncionario.getdataAdmissao() + "\n");
        writer.write("================\n");
    } catch (IOException e) {
        System.out.println("Erro ao gravar o funcionário no arquivo: " + e.getMessage());
    }

    System.out.println("Funcionário registrado com sucesso!");
}
    
    public static void registrarequipamento(){
        System.out.println("Digite o codigo do equipamento: ");
        String codigoEquipamento = scanner.nextLine();
        System.out.println("Digite a descricao do equipamento: ");
        String descricaoEquipamento = scanner.nextLine();
        System.out.println("Digite a data da compra do equipamento (yyyy-mm-dd): ");
        LocalDate dataCompraEquipamento = LocalDate.parse(scanner.nextLine());
        System.out.println("Digite o peso do equipamento (g): ");
        double pesoEquipamento = scanner.nextDouble();
        System.out.println("Digite a largura do equipamento (cm): ");
        double larguraEquipamento = scanner.nextDouble();
        System.out.println("Digite o comprimento do equipamento (cm): ");
        double comprimentoEquipamento = scanner.nextDouble();
        
        scanner.nextLine();
        
        System.out.println("Produto tem historico de manutencao?");
        String historicoManutencaoEquipamento = scanner.nextLine();
        System.out.println("Qual estado de conservacao do equipamento? ");
        String estadoConservacaoEquipamento = scanner.nextLine();
        
        Equipamento novoEquipamento = new Equipamento(codigoEquipamento, descricaoEquipamento, dataCompraEquipamento, pesoEquipamento, larguraEquipamento, comprimentoEquipamento, historicoManutencaoEquipamento, estadoConservacaoEquipamento);
        listaEquipamentos.add(novoEquipamento);
        
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("equipamentos.txt", true))){
        writer.write("Codigo: "+ novoEquipamento.getcodigo() + "\n");
        writer.write("Descricao: "+ novoEquipamento.getdescricao() + "\n");
        writer.write("Data da compra: "+ novoEquipamento.getdataCompra()+ "\n");
        writer.write("Peso: "+ novoEquipamento.getpeso()+"\n");
        writer.write("Largura: "+ novoEquipamento.getlargura()+"\n");
        writer.write("Comprimento: " + novoEquipamento.getcomprimento()+ "\n");
        writer.write("Historico de manutencao: "+ novoEquipamento.gethistoricoManutencao()+"\n");
        writer.write("Estado de conservacao: "+ novoEquipamento.getestadoConservacao()+"\n");
        writer.write("================================================\n");
    } catch (IOException e) {
    System.out.println("Erro ao gravar equipamento no arquivo: " + e.getMessage());
    }
    System.out.println("Equipamento registrado com sucesso!");
        
    }
    
    public static void listarEquipamentos(){
        System.out.println("Equipamentos Cadastrados: ");
        for (Equipamento e : listaEquipamentos ){
            System.out.println("Codigo: "+e.getcodigo());
            System.out.println("Descricao: "+e.getdescricao());
            System.out.println("Data da compra: "+e.getdataCompra());
            System.out.println("Peso: "+e.getpeso());
            System.out.println("Largura: "+e.getlargura());
            System.out.println("Comprimento: "+e.getcomprimento());
            System.out.println("Historico de manutencao: "+e.gethistoricoManutencao());
            System.out.println("Estado de conservacao: "+e.getestadoConservacao());
            System.out.println("========================================");
            }
    }
    public static Funcionario buscarFuncionarioPorCodigo(String codigo){
        for(Funcionario funcionario : listafuncionarios){
            if(funcionario.getcodigo().equals(codigo)){
                return funcionario;
            }
        }
        return null;
    }
    public static Equipamento buscarEquipamentoPorCodigo(String codigo){
        for(Equipamento equipamento : listaEquipamentos){
            if(equipamento.getcodigo().equals(codigo)){
                return equipamento;
            }
        }
        return null;
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


