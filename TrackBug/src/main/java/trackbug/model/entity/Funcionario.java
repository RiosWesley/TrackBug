package trackbug.model.entity;

import java.time.LocalDate;

public class Funcionario {
    private String id;
    private String nome;
    private String funcao;
    private LocalDate dataAdmissao;  // Alterado de String para LocalDate

    // Getters e Setters atualizados
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }
}