package trackbugoriginal;

import trackbug.*;

public class Funcionario {
    private String codigo;
    private String nome;
    private String funcao;
    private String dataAdmissao;
    

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
}

