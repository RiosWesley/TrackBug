package trackbug;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class Funcionario {
    private String codigo;
    private String nome;
    private String funcao;
    private String dataAdmissao;
    private String listaFuncionarios;
    

    public Funcionario(String codigo, String nome, String funcao, String dataAdmissao){
        this.codigo=codigo;
        this.nome=nome;
        this.funcao=funcao;
        this.dataAdmissao=dataAdmissao;
        }


    public String getcodigo(){
        return codigo;
    }    
    public String getnome(){
        return nome;
    }
    public String getfuncao(){
        return funcao;
    }
    public String getdataAdmissao(){
        return dataAdmissao;
    }
    
    public void setcodigo(String codigo){
        this.codigo=codigo;
    }
    public void setnome(String nome){
        this.nome=nome;
    }
    public void setfuncao(String funcao){
        this.funcao=funcao;
    }
    public void setdataAdmissao(String dataAdmissao){
        this.dataAdmissao=dataAdmissao;
    }
    @Override
     public String toString() {
        return "Codigo: " + codigo + ", Nome: " + nome + ", Funcao: " + funcao + ", Data de Admissao: " + dataAdmissao;
    }
     
     public static void registrarFuncionario(List<Funcionario> listafuncionarios) {
         Scanner scanner = new Scanner(System.in);
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
     
     
     public static void listarfuncionarios(List<Funcionario> listafuncionarios){

    System.out.println("Funcionarios cadastrados: \n");
    for (Funcionario f : listafuncionarios) {
        System.out.println("Código: " +f.getcodigo());
        System.out.println("Nome: " +f.getnome());
        System.out.println("Funcao: " +f.getfuncao());
        System.out.println("Data de Admissao: " +f.getdataAdmissao());
        System.out.println("==================");
    }
}
     public static Funcionario buscarFuncionarioPorCodigo(String codigo, List<Funcionario> listafuncionarios){
        for(Funcionario funcionario : listafuncionarios){
            if(funcionario.getcodigo().equals(codigo)){
                return funcionario;
            }
        }
        return null;
    }
     
      public static void carregarFuncionarios(List<Funcionario> listafuncionarios) {
   // listafuncionarios = new ArrayList<>();

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
     
}


