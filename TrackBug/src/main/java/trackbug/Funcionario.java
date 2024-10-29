package trackbug;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

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

    // Metodo para registrar um novo funcionário
    public void registrarFuncionario(Funcionario funcionario) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite o código do funcionário: ");
        funcionario.setID(scanner.nextLine());
        System.out.println("Digite o nome do funcionário: ");
        funcionario.setNome(scanner.nextLine());
        System.out.println("Digite a função do funcionário: ");
        funcionario.setFuncao(scanner.nextLine());
        System.out.println("Digite a data de admissão do funcionário (yyyy-mm-dd): ");
        funcionario.setDataAdmissao(scanner.nextLine());

        String sql = "INSERT INTO funcionarios (id, nome, funcao, dt) VALUES (?, ?, ?, ?)";

        try{
            stmt = con.prepareStatement(sql);
            //Insere os dados obtidos na tabela do banco de dados em seus respectivos parametros
            stmt.setString(1, funcionario.getId());
            stmt.setString(2, funcionario.getNome());
            stmt.setString(3, funcionario.getFuncao());
            stmt.setString(4, funcionario.getDataAdmissao());

            stmt.executeUpdate();
            System.out.println("Funcionário registrado com sucesso!");
        } catch(SQLException ex) {
            System.out.println("Erro ao salvar dados. " + ex);
        } finally {
            ConnectionFactory.closeConnection(con, stmt);
        }
    }

    // Metodo para listar todos os funcionários cadastrados
    public int listarFuncionarios(Funcionario funcionario) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT id, nome, funcao, dt FROM FUNCIONARIOS";
        int acho = 0;

        try {
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            System.out.println("Funcionários cadastrados: \n");
            while (rs.next()) {
                //Obtem os dados do banco atraves do get, e setta em funcionario.
                funcionario.setID(rs.getString("id"));
                funcionario.setNome(rs.getString("nome"));
                funcionario.setFuncao(rs.getString("funcao"));
                funcionario.setDataAdmissao(rs.getString("dt"));

                System.out.println("Código: " + funcionario.getId());
                System.out.println("Nome: " + funcionario.getNome());
                System.out.println("Função: " + funcionario.getFuncao());
                System.out.println("Data de Admissão: " + funcionario.getDataAdmissao());
                System.out.println("==================");
                acho++;
            }
            if (acho == 0) {
                System.out.println("\nNão há funcionários cadastrados.\n");
            }
        } catch(SQLException e) {
            System.out.println("Erro ao listar os funcionários. Causa: " + e);
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
        return acho;
    }

    // Metodo para buscar um funcionário pelo código
    public static boolean buscarFuncionarioPorCodigo(String codigoFuncionario) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT id, nome, funcao, dt FROM FUNCIONARIOS WHERE id = ?";
        boolean acho = false;
        Funcionario funcionario = new Funcionario();

        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, codigoFuncionario);
            rs = stmt.executeQuery();

            while (rs.next()) {

                //Obtem os dados do banco atraves do get, e setta em funcionario.
                funcionario.setID(rs.getString("id"));
                funcionario.setNome(rs.getString("nome"));
                funcionario.setFuncao(rs.getString("funcao"));
                funcionario.setDataAdmissao(rs.getString("dt"));

                
                System.out.println("Código: " + funcionario.getId());
                System.out.println("Nome: " + funcionario.getNome());
                System.out.println("Função: " + funcionario.getFuncao());
                System.out.println("Data de Admissão: " + funcionario.getDataAdmissao());
                if (codigoFuncionario.equals(funcionario.getId())) {
                    acho = true;
                }
            }
        } catch(SQLException e) {
            System.out.println("Erro ao buscar funcionário no banco de dados. CAUSA: " + e);
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }

        return acho;
    }
}
