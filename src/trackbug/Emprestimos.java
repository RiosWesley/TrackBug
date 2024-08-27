package trackbug;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static trackbug.TrackBug.buscarEquipamentoPorCodigo;
import static trackbug.TrackBug.buscarFuncionarioPorCodigo;


public class Emprestimos{

    
    
    private Funcionario funcionario;
    private Equipamento equipamento;
    private LocalDateTime dataSaida;
    private LocalDateTime dataRetornoPrevista;
    private LocalDateTime dataRetornoEfetiva;
    private String observacoes;
    private boolean ativo;

    public Emprestimos(Funcionario funcionario, Equipamento equipamento, LocalDateTime dataSaida, LocalDateTime dataRetornoPrevista, String observacoes) {
        this.dataSaida = dataSaida;
        this.dataRetornoPrevista = dataRetornoPrevista;
        this.dataRetornoEfetiva = null; // Vai ser definido quando o item retornar
        this.observacoes = observacoes;
        this.funcionario=funcionario;
        this.equipamento=equipamento;
        this.ativo=true;
        
    }


    
    public LocalDateTime getDataSaida() {
        return dataSaida;
    }
    
    public void setDataSaida(LocalDateTime dataSaida) {
        this.dataSaida = dataSaida;
    }
    
    public LocalDateTime getDataRetornoPrevista() {
        return dataRetornoPrevista;
    }
    
    public void setDataRetornoPrevista(LocalDateTime dataRetornoPrevista) {
        this.dataRetornoPrevista = dataRetornoPrevista;
    }
    
    public LocalDateTime getDataRetornoEfetiva() {
        return dataRetornoEfetiva;
    }
    
    public void setDataRetornoEfetiva(LocalDateTime dataRetornoEfetiva) {
        this.dataRetornoEfetiva = dataRetornoEfetiva;
    }
    
    public String getobservacoes() {
        return observacoes;
    }
    
    public void setobservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Equipamento getequipamento(){
        return equipamento;
    }
    public void setequipamento(Equipamento equipamento){
        this.equipamento=equipamento;
    }
    public Funcionario getfuncionario(){
        return funcionario;
    }
    public void setfuncionario(Funcionario funcionario){
        this.funcionario=funcionario;
    }
    
    public boolean isAtivo(){
        return ativo;
    }

    
    public void registrarDevolucao(LocalDateTime dataRetornoEfetiva, String observacoes){
        this.dataRetornoEfetiva=dataRetornoEfetiva;
        this.observacoes=observacoes;
    }
    
    
    
    public boolean Atrasado(){
        if (dataRetornoEfetiva != null){
            return dataRetornoEfetiva.isAfter(dataRetornoPrevista);
        } else {
            return LocalDateTime.now().isAfter(dataRetornoPrevista);
        }
    }
    
    
     public static void carregarEmprestimos(List<Emprestimos> listaEmprestimos) {
    //listaEmprestimos = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader("emprestimos_ativos.txt"))) {
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

        System.out.println("Emprestimos carregados com sucesso!");

    } catch (IOException e) {
        System.out.println("Erro ao carregar os emprestimos: " + e.getMessage());
    } catch (DateTimeParseException e) {
        System.out.println("Erro ao analisar a data: " + e.getParsedString());
    }
}

      public static void registrarEmprestimo(List<Emprestimos> listaEmprestimos, List<Funcionario> listafuncionarios, List<Equipamento> listaEquipamentos, Scanner scanner) {
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
    
    System.out.println("Emprestimo registrado com sucesso!");
    System.out.println("Equipamento: " + novoEmprestimo.getequipamento().getcodigo() + " - " + novoEmprestimo.getequipamento().getdescricao());
    System.out.println("Funcionario: " + novoEmprestimo.getfuncionario().getnome());
    System.out.println("Data de saída: " + novoEmprestimo.getDataSaida());
    System.out.println("Data prevista de retorno: " + novoEmprestimo.getDataRetornoPrevista());
    
    // Registrar apenas o novo empréstimo no arquivo
    registrarEmprestimoEmArquivo(novoEmprestimo);
    
    // Atualizar o arquivo de empréstimos ativos
    salvarEmprestimosAtivos(listaEmprestimos);
}

     public static void registrarDevolucao(List<Emprestimos> listaEmprestimos, Scanner scanner) {
    System.out.println("Digite o código do funcionário: ");
    String codigoFuncionario = scanner.nextLine();
    System.out.println("Digite o código do equipamento: ");
    String codigoEquipamento = scanner.nextLine();

    Emprestimos emprestimoParaDevolver = null;

    for (Emprestimos e : listaEmprestimos) {
        if (e.getfuncionario().getcodigo().equals(codigoFuncionario) && 
            e.getequipamento().getcodigo().equals(codigoEquipamento) && 
            e.getDataRetornoEfetiva() == null) { // Verifica se o empréstimo ainda está ativo
            emprestimoParaDevolver = e;
            break;
        }
    }

    if (emprestimoParaDevolver != null) {
        emprestimoParaDevolver.setDataRetornoEfetiva(LocalDateTime.now());
        System.out.println("Empréstimo devolvido com sucesso!");

        // Atualiza o arquivo de empréstimos ativos
        salvarEmprestimosAtivos(listaEmprestimos);

        // Adiciona o empréstimo ao arquivo de histórico
        salvarHistoricoEmprestimos(emprestimoParaDevolver);
    } else {
        System.out.println("Empréstimo não encontrado ou já devolvido.");
    }
}
    
    public static void listarEmprestimosAtivos(List<Emprestimos> listaEmprestimos) {
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

    public static void salvarEmprestimosAtivos(List<Emprestimos> listaEmprestimos) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("emprestimos_ativos.txt"))) {
        for (Emprestimos emprestimo : listaEmprestimos) {
            if (emprestimo.getDataRetornoEfetiva() == null) { // Somente empréstimos ativos
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
            }
        }
    } catch (IOException e) {
        System.out.println("Erro ao salvar os empréstimos ativos no arquivo: " + e.getMessage());
    }
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
    
    public static void salvarHistoricoEmprestimos(Emprestimos emprestimo) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("historico_emprestimos.txt", true))) {
        writer.write("Emprestimo Concluido:");
        writer.newLine();
        writer.write("Funcionario: " + emprestimo.getfuncionario().getcodigo() + " - " + emprestimo.getfuncionario().getnome());
        writer.newLine();
        writer.write("Equipamento: " + emprestimo.getequipamento().getcodigo() + " - " + emprestimo.getequipamento().getdescricao());
        writer.newLine();
        writer.write("Data de saída: " + emprestimo.getDataSaida());
        writer.newLine();
        writer.write("Data prevista de retorno: " + emprestimo.getDataRetornoPrevista());
        writer.newLine();
        writer.write("Data de retorno efetiva: " + emprestimo.getDataRetornoEfetiva());
        writer.newLine();
        writer.write("Observações: " + emprestimo.getobservacoes());
        writer.newLine();
        writer.write("================================================");
        writer.newLine();
    } catch (IOException e) {
        System.out.println("Erro ao salvar o histórico de empréstimos: " + e.getMessage());
    }
}
    
    
    
}

