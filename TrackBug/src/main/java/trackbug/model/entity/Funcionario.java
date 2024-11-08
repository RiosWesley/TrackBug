package trackbug.model.entity;

import java.time.LocalDate;

public class Funcionario {
    public String id;
    public String nome;
    public String funcao;
    public LocalDate dataAdmissao;

    // Metodos setter
    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    // Métodos getter
    public String getId(){
        return id;
    }
    public String getNome(){
        return nome;
    }
    public String getFuncao(){
        return funcao;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }
}
