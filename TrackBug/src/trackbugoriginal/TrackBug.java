package trackbugoriginal;


import trackbug.*;
import java.time.LocalDate;

public class TrackBug {

    public static void main(String[] args) {
       
    Funcionario funcionario = new Funcionario("123", "Joao", "Técnico", "2022-01-01");
    Equipamento equipamento = new Equipamento("E001", "Smartphone", LocalDate.now(), 7, 20, 20, "Nenhuma Manutencao", "Bom");

    Emprestimos novoEmprestimo = Emprestimos.registrarEmprestimo(funcionario, equipamento, 15, "Empréstimo para trabalho remoto");
    System.out.println("Emprestimo registrado para o equipamento: " + novoEmprestimo.getequipamento().getdescricao());
    System.out.println("Funcionario responsavel: " + novoEmprestimo.getfuncionario().getnome());
    System.out.println("Data de saida: " + novoEmprestimo.getDataSaida());
    System.out.println("Data prevista de retorno: " + novoEmprestimo.getDataRetornoPrevista());
}
}
