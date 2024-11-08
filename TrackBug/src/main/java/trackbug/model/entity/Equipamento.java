package trackbug.model.entity;

import java.time.LocalDate;

public class Equipamento {

    private String id;
    private String descricao;
    private LocalDate dataCompra;
    private double peso;
    private double largura;
    private double comprimento;
    private int quantidadeAtual;
    private boolean tipo;
    private int quantidadeEstoque;
    private int quantidadeMinima;
    private  String tipoUso;

    // Getters
    public String getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public double getPeso() {
        return peso;
    }

    public double getLargura() {
        return largura;
    }

    public double getComprimento() {
        return comprimento;
    }

    public int getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public boolean isTipo() {
        return tipo;
    }

    public int getQuantidadeMinima() {
        return quantidadeMinima;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setDataCompra(LocalDate dataCompra) {
        this.dataCompra = dataCompra;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public void setLargura(double largura) {
        this.largura = largura;
    }

    public void setComprimento(double comprimento) {
        this.comprimento = comprimento;
    }

    public void setQuantidadeAtual(int quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public void setTipo(boolean tipo) {
        this.tipo = tipo;
    }

    public void setQuantidadeMinima(int quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public String getTipoUso() {
        return tipoUso;
    }

    public void setTipoUso(String tipoUso) {
        this.tipoUso = tipoUso;
    }
}
