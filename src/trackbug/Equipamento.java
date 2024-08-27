
package trackbug;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Equipamento {

    
    private String codigo;
    private String descricao;
    private LocalDate dataCompra;
    private double peso;
    private double largura;
    private double comprimento;
    private String historicoManutencao;
    private String estadoConservacao;


    public Equipamento(String codigo, String descricao, LocalDate dataCompra, double peso, double largura, double comprimento, String historicoManutencao, String estadoConservacao){
        this.codigo=codigo;
        this.descricao=descricao;
        this.dataCompra=dataCompra;
        this.peso=peso;
        this.largura=largura;
        this.comprimento=comprimento;
        this.historicoManutencao=historicoManutencao;
        this.estadoConservacao=estadoConservacao;
    }

    public String getcodigo(){
        return codigo;
    }
    public String getdescricao(){
        return descricao;
    }
    public LocalDate getdataCompra(){
        return dataCompra;
    }
    public double getpeso(){
        return peso;
    }
    public double getlargura(){
        return largura;
    }
    public double getcomprimento(){
        return comprimento;
    }
    public String gethistoricoManutencao(){
        return historicoManutencao;
    }
    public String getestadoConservacao(){
        return estadoConservacao;
    }


    public void setcodigo(String codigo){
        this.codigo=codigo;
    }
    public void setdescricao(String descricao){
        this.descricao=descricao;
    }
    public void setdataCompra(LocalDate dataCompra){
        this.dataCompra=dataCompra;
    }
    public void setpeso(double peso){
        this.peso=peso;
    }
    public void setlargura(double largura){
        this.largura=largura;
    }
    public void setcomprimento(double comprimento){
        this.comprimento=comprimento;
    }
    public void sethistoricoManutencao(String historicoManutencao){
        this.historicoManutencao=historicoManutencao;
    }
    public void setestadoConservacao(String estadoConservacao){
        this.estadoConservacao=estadoConservacao;
    }
    
    public static void carregarEquipamentos(List<Equipamento> listaEquipamentos) {
    //listaEquipamentos = new ArrayList<>();
    
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
    
    public static void registrarequipamento(List<Equipamento> listaEquipamentos){
        Scanner scanner = new Scanner(System.in);
        
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
    
     public static void listarEquipamentos(List<Equipamento> listaEquipamentos){
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

    public static Equipamento buscarEquipamentoPorCodigo(String codigo, List<Equipamento> listaEquipamentos) {
        for (Equipamento equipamento : listaEquipamentos) {
            if (equipamento.getcodigo().equals(codigo)) {
                return equipamento;
            }
        }
        return null;
    }
        
        
    }
    
    
    
    
    
    
    
    
    
