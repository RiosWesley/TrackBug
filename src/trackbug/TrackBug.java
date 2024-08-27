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
            carregarEmprestimos();
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
        System.out.println("Funcionários carregados com sucesso!");
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

    public static void carregarEmprestimos() {
    listaEmprestimos = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader("emprestimos.txt"))) {
        String linha;

        while ((linha = reader.readLine()) != null) {
            if (linha.startsWith("Emprestimos Ativos:")) {
                String funcionarioLinha = reader.readLine();
                String equipamentoLinha = reader.readLine();
                String dataSaidaLinha = reader.readLine();
                String dataRetornoPrevistaLinha = reader.readLine();
                String observacoesLinha = reader.readLine();

                String funcionarioCodigo = funcionarioLinha.substring(12, funcionarioLinha.indexOf(" - ")).trim(); 
                String equipamentoCodigo = equipamentoLinha.substring(12, equipamentoLinha.indexOf(" - ")).trim(); 
                LocalDateTime dataSaida = LocalDateTime.parse(dataSaidaLinha.substring(15).trim()); 
                LocalDateTime dataRetornoPrevista = LocalDateTime.parse(dataRetornoPrevistaLinha.substring(26).trim());
                String observacoes = observacoesLinha.substring(13).trim(); 

                Funcionario funcionario = buscarFuncionarioPorCodigo(funcionarioCodigo);
                Equipamento equipamento = buscarEquipamentoPorCodigo(equipamentoCodigo);

                if (funcionario != null && equipamento != null) {
                    Emprestimos emprestimo = new Emprestimos(funcionario, equipamento, dataSaida, dataRetornoPrevista, observacoes);
                    listaEmprestimos.add(emprestimo);
                }

                // Lê a linha de separador "================"
                reader.readLine();
            }
        }

        System.out.println("Empréstimos carregados com sucesso!");

    } catch (IOException e) {
        System.out.println("Erro ao carregar os empréstimos: " + e.getMessage());
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
                    registrarEmprestimo();
                    break;
                case 2:
                    registrarDevolucao();
                    break;
                case 3:
                    listarEmprestimosAtivos();
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
    public static void listarEmprestimosAtivos() {
    System.out.println("Emprstimos Ativos:");

    for (Emprestimos emprestimo : listaEmprestimos) {
        if (emprestimo.getDataRetornoEfetiva() == null) {
            System.out.println("Equipamento: " + emprestimo.getequipamento().getcodigo() + " - " + emprestimo.getequipamento().getdescricao());
            System.out.println("Funcionario: " + emprestimo.getfuncionario().getcodigo()+ " - " + emprestimo.getfuncionario().getnome());
            System.out.println("Data de Saida: " + emprestimo.getDataSaida());
            System.out.println("Data Prevista de Retorno: " + emprestimo.getDataRetornoPrevista());
            System.out.println("Observacoes: " + emprestimo.getobservacoes());
            System.out.println("--------------------------------------------------");
        }
    }
}


 public static void registrarEmprestimo() {
    System.out.println("Digite o codigo do funcionario: ");
    String codigoFuncionario = scanner.nextLine();
    Funcionario funcionario = buscarFuncionarioPorCodigo(codigoFuncionario);
    if (funcionario == null) {
        System.out.println("Funcionario nao encontrado!");
        return;
    }
    
    System.out.println("Digite o codigo do equipamento a ser emprestado: ");
    String codigoEquipamento = scanner.nextLine();
    Equipamento equipamento = buscarEquipamentoPorCodigo(codigoEquipamento);
    if (equipamento == null) {
        System.out.println("Equipamento nao encontrado!");
        return;
    }
    
    System.out.println("Digite o numero de dias de emprestimo: ");
    int diasDeEmprestimo = scanner.nextInt();
    scanner.nextLine();  // Limpar o buffer do scanner
    
    System.out.println("Digite observacoes sobre o emprestismo (motivo do emprestimo...): ");
    String observacoes = scanner.nextLine();
    
    // Calcular as datas de saída e retorno previstas
    LocalDateTime dataSaida = LocalDateTime.now();
    LocalDateTime dataRetornoPrevista = dataSaida.plusDays(diasDeEmprestimo);
    
    // Criar o objeto Emprestimos usando os objetos Funcionario e Equipamento
    Emprestimos novoEmprestimo = new Emprestimos(funcionario, equipamento, dataSaida, dataRetornoPrevista, observacoes);
    listaEmprestimos.add(novoEmprestimo);
    
    System.out.println("Empréstimo registrado com sucesso!");
    System.out.println("Equipamento: " + novoEmprestimo.getequipamento().getcodigo() + " - " + novoEmprestimo.getequipamento().getdescricao());
    System.out.println("Funcionário: " + novoEmprestimo.getfuncionario().getnome());
    System.out.println("Data de saída: " + novoEmprestimo.getDataSaida());
    System.out.println("Data prevista de retorno: " + novoEmprestimo.getDataRetornoPrevista());
    
    // Registrar apenas o novo empréstimo no arquivo
    registrarEmprestimoEmArquivo(novoEmprestimo);
}



    
  public static void registrarEmprestimoEmArquivo(Emprestimos emprestimo) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("emprestimos.txt", true))) {
        writer.write("Emprestimos Ativos: ");
        writer.newLine();
        writer.write("Funcionario: " + emprestimo.getfuncionario().getcodigo() + " - " + emprestimo.getfuncionario().getnome());
        writer.newLine();
        writer.write("Equipamento: " + emprestimo.getequipamento().getcodigo() + " - " + emprestimo.getequipamento().getdescricao());
        writer.newLine();
        writer.write("Data de saída: " + emprestimo.getDataSaida());
        writer.newLine();
        writer.write("Data prevista de retorno: " + emprestimo.getDataRetornoPrevista());
        writer.newLine();
        writer.write("Observações: " + emprestimo.getobservacoes());
        writer.newLine();
        writer.write("================================================");
        writer.newLine();
    } catch (IOException e) {
        System.out.println("Erro ao registrar o empréstimo no arquivo: " + e.getMessage());
    }
}
  
  public static void registrarDevolucao() {
    System.out.println("Digite o código do funcionário: ");
    String codigoFuncionario = scanner.nextLine();
    Funcionario funcionario = buscarFuncionarioPorCodigo(codigoFuncionario);
    if (funcionario == null) {
        System.out.println("Funcionário não encontrado!");
        return;
    }

    System.out.println("Digite o código do equipamento: ");
    String codigoEquipamento = scanner.nextLine();
    Equipamento equipamento = buscarEquipamentoPorCodigo(codigoEquipamento);
    if (equipamento == null) {
        System.out.println("Equipamento não encontrado!");
        return;
    }

    // Verifica se existe um empréstimo ativo para esse equipamento
    Emprestimos emprestimo = listaEmprestimos.stream()
        .filter(e -> e.getequipamento().getcodigo().equals(codigoEquipamento) && e.getDataRetornoEfetiva() == null)
        .findFirst()
        .orElse(null);

    if (emprestimo == null) {
        System.out.println("Nenhum empréstimo ativo encontrado para este equipamento.");
        return;
    }

    // Registra a devolução com a data atual do sistema
    LocalDateTime dataAtual = LocalDateTime.now();
    emprestimo.registrarDevolucao(dataAtual, "Devolução realizada.");

    System.out.println("Devolução registrada com sucesso!");
    System.out.println("Data da devolução: " + dataAtual);

    // Atualiza o arquivo de empréstimos após registrar a devolução
    registrarEmprestimoEmArquivo(emprestimo);
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


