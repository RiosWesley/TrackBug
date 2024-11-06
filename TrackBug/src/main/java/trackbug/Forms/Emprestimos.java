package trackbug.Forms;

import java.time.LocalDateTime;

public class Emprestimos {

    // Atributos da classe Emprestimos
    public int id;
    public String idFuncionario;
    public String idEquipamento;
    public LocalDateTime dataSaida;
    public LocalDateTime dataRetornoPrevista;
    public LocalDateTime dataRetornoEfetiva;
    public String observacoes;
    public boolean ativo;
    public int quantidadeEmprestimo;

    // Métodos setters para definir os valores dos atributos
    public void setId(int id) {
        this.id = id;
    }

    public void setIdFuncionario(String funcionario) {
        this.idFuncionario = funcionario;
    }

    public void setIdEquipamento(String equipamento) {
        this.idEquipamento = equipamento;
    }

    public void setDataSaida(LocalDateTime dataSaida) {
        this.dataSaida = dataSaida;
    }

    public void setDataRetornoPrevista(LocalDateTime dataRetornoPrevista) {
        this.dataRetornoPrevista = dataRetornoPrevista;
    }

    public void setDataRetornoEfetiva(LocalDateTime dataRetornoEfetiva) {
        this.dataRetornoEfetiva = dataRetornoEfetiva;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public void setQuantidadeEmprestimo(int quantidadeEmprestimo) {
        this.quantidadeEmprestimo = quantidadeEmprestimo;
    }

    // Métodos getters para obter os valores dos atributos
    public int getId() {
        return id;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public LocalDateTime getDataSaida() {
        return dataSaida;
    }

    public LocalDateTime getDataRetornoPrevista() {
        return dataRetornoPrevista;
    }

    public LocalDateTime getDataRetornoEfetiva() {
        return dataRetornoEfetiva;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public String getIdEquipamento() {
        return idEquipamento;
    }

    public String getIdFuncionario() {
        return idFuncionario;
    }

    public int getQuantidadeEmprestimo() {
        return quantidadeEmprestimo;
    }

}



