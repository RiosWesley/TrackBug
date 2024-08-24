
package trackbugoriginal;
import trackbug.*;
import java.time.LocalDate;

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
}