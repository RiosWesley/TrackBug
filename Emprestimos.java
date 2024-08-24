package trackbug;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Emprestimos{

    static Emprestimos registrarEmprestimo(Funcionario funcionario, Equipamento equipamento, int diasDeEmprestimo, String observacoes) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    private Funcionario funcionario;
    private Equipamento equipamento;
    private LocalDateTime dataSaida;
    private LocalDateTime dataRetornoPrevista;
    private LocalDateTime dataRetornoEfetiva;
    private String observacoes;

    public Emprestimos(Funcionario funcionario, Equipamento equipamento, LocalDateTime dataSaida, LocalDateTime dataRetornoPrevista, String observacoes) {
        this.dataSaida = dataSaida;
        this.dataRetornoPrevista = dataRetornoPrevista;
        this.dataRetornoEfetiva = null; // Vai ser definido quando o item retornar
        this.observacoes = observacoes;
        this.funcionario=funcionario;
        this.equipamento=equipamento;
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
    public static void registrarEmprestimo(){
        System.out.println("Sei la lek");
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
    public void salvarEmArquivo(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("RegistroEmprestimos.txt", true))){
           writer.write("Funcionario: " + funcionario.getnome());
           writer.write(", Equipamento: " + equipamento.getdescricao());
           writer.write(", Data de Saída: " + dataSaida);
           writer.write(", Data de Retorno Prevista: " + dataRetornoPrevista);
           writer.write(", Observacoes: " + observacoes);
           writer.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao salvar o emprestimo no arquivo: " + e.getMessage());
        }
    }
    
    

}

