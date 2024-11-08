package trackbug.model.entity;

public class Funcionario {
    public String id;
    public String nome;
    public String funcao;
    public String dataAdmissao;

    // Metodos setter
    public void setID(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public void setDataAdmissao(String dataAdmissao) {
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
    public String getDataAdmissao(){
        return dataAdmissao;
    }
}
