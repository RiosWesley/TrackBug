package trackbug;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Scanner;

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

    // Metodo para registrar um empréstimo
    @SuppressWarnings("empty-statement")
    public void registrarEmprestimo(Emprestimos emprestimo) {
        Connection con = ConnectionFactory.getConnection(); // Obtém uma conexão com o banco de dados
        PreparedStatement stmt = null; // Declaração SQL preparada
        String sql = "INSERT INTO emprestimos (idFuncionario, idEquipamento, observacoes, dataSaida, dataRetornoPrevista, ativo, dataRetornoEfetiva, quantidadeEmprestimo) values (?, ?, ?, ?, ?, ?, null, ?)";
        ResultSet rs = null; // Resultado da consulta SQL
        String buscarEquipamento = "SELECT descricao FROM equipamentos WHERE id = ?";
        String buscaFuncionario = "SELECT nome FROM funcionarios WHERE id = ?";
        int diasDeEmprestimo = 0;
        
        try {
            stmt = con.prepareStatement(sql); // Prepara a declaração SQL
            Scanner scanner = new Scanner(System.in); // Scanner para entrada do usuário

            // Verifica se existem funcionários no banco de dados
            Funcionario funcionario = new Funcionario();
            if(funcionario.listarFuncionarios(funcionario) == 0){
                return;
            }
            // Solicita o código do funcionário e define no objeto emprestimo
            System.out.println("Digite o codigo do funcionario: ");
            emprestimo.setIdFuncionario(scanner.nextLine());

            // Verifica se o funcionário existe no banco de dados
            if (Funcionario.buscarFuncionarioPorCodigo(emprestimo.getIdFuncionario()) == true) {
                System.out.println("Funcionário encontrado");
            } else {
                System.out.println("Não foi encontrado esse funcionário na nossa base de dados.");
                return;
            }
            
            System.out.println("\n");
            Equipamento equipamento = new Equipamento();
            equipamento.listarEquipamentos(equipamento);
            System.out.println("\n");

            // Solicita o código do equipamento e define no objeto emprestimo
            System.out.println("Digite o codigo do equipamento a ser emprestado: ");
            emprestimo.setIdEquipamento(scanner.nextLine());
            
            // Verifica se o equipamento existe no banco de dados
            if (Equipamento.buscarEquipamentoPorCodigo(emprestimo.getIdEquipamento()) == true) {
                System.out.println("Equipamento encontrado.");
            } else {
                System.out.println("Não foi encontrado esse equipamento na nossa base de dados.");
                return;
            }

            //Define a quantidade emprestada.
            System.out.println("Digite a quantidade que será emprestada.");
            emprestimo.setQuantidadeEmprestimo(scanner.nextInt());

            //Verifica se a quantidade existe no estoque.
            if (Equipamento.verificaQuantidade(emprestimo.getQuantidadeEmprestimo(), emprestimo.getIdEquipamento())== true) {
                System.out.println("Quantidade disponível.\n");
            } else {
                System.out.println("Não há essa quantidade do item no estoque.");
                return;
            }

            // Solicita o número de dias de empréstimo
            if(Equipamento.defineTipoDeSaida(emprestimo.getIdEquipamento()) == false){
                System.out.println("Digite o numero de dias de emprestimo: ");
                diasDeEmprestimo = scanner.nextInt();
            }
            scanner.nextLine();  // Limpar o buffer do scanner

            // Solicita observações sobre o empréstimo
            System.out.println("Digite observacoes sobre o emprestismo (motivo do emprestimo...): ");
            emprestimo.setObservacoes(scanner.nextLine());

            // Calcula as datas de saída e retorno previstas
            emprestimo.setDataSaida(LocalDateTime.now());
            emprestimo.setDataRetornoPrevista(emprestimo.getDataSaida().plusDays(diasDeEmprestimo));

            // Converte as datas de saída e retorno previstas para o formato Timestamp
            Timestamp sqlDataSaida = Timestamp.valueOf(emprestimo.getDataSaida());
            Timestamp sqlDataRetornoPrevista = Timestamp.valueOf(emprestimo.getDataRetornoPrevista());

            // Define os parâmetros da declaração SQL preparada com os valores do objeto emprestimo
            stmt.setString(1, emprestimo.getIdFuncionario());
            stmt.setString(2, emprestimo.getIdEquipamento());
            stmt.setString(3, emprestimo.getObservacoes());
            stmt.setTimestamp(4, sqlDataSaida);
            stmt.setTimestamp(5, sqlDataRetornoPrevista);
            stmt.setBoolean(6, true);
            stmt.setInt(8, emprestimo.getQuantidadeEmprestimo());

            // Executa a atualização no banco de dados
            stmt.executeUpdate();

            Equipamento.atualizarQuantidadeSaida(emprestimo.getQuantidadeEmprestimo(), emprestimo.getIdEquipamento(), Equipamento.defineTipoDeSaida(emprestimo.getIdEquipamento()));

            System.out.println("Emprestimo registrado com sucesso!");

            // Prepara a declaração SQL para buscar a descrição do equipamento
            stmt = con.prepareStatement(buscarEquipamento);
            stmt.setString(1, emprestimo.getIdEquipamento());
            rs = stmt.executeQuery();

            // Exibe a descrição do equipamento
            while (rs.next()) {
                String descricao = rs.getString("descricao");
                System.out.println("Equipamento: " + emprestimo.getIdEquipamento() + " - " + descricao);
            }


            // Prepara a declaração SQL para buscar o nome do funcionário
            stmt = con.prepareStatement(buscaFuncionario);
            stmt.setString(1, emprestimo.getIdFuncionario());
            rs = stmt.executeQuery();

            // Exibe o nome do funcionário
            while (rs.next()) {
                String nome = rs.getString("nome");
                System.out.println("Funcionario: " + nome);
            }

            // Exibe as datas de saída e retorno previstas do equipamento
            System.out.println("Data de saída do equipamento: " + emprestimo.getDataSaida());
            System.out.println("Data de retorno prevista: " + emprestimo.getDataRetornoPrevista());

        } catch (SQLException e) {
            // Trata possíveis exceções de SQL
            System.out.println("Erro ao inserir dados no Banco de Dados. CAUSA: " + e);
        } finally {
            // Fecha a conexão com o banco de dados
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
    }

    //Metodo para Registrar Devolucao, recebendo como parametro objeto do tipo Emprestimos.
    public void registrarDevolucao(Emprestimos emprestimo) {
        int acho = 0, opcao = 0;

        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "UPDATE emprestimos SET ativo = ?, dataRetornoEfetiva = ? WHERE id = ? AND ativo = true";

        Scanner scanner = new Scanner(System.in);

        try {
            stmt = con.prepareStatement(sql);

                    // Define a data de retorno efetiva como o momento atual
                    emprestimo.setDataRetornoEfetiva(LocalDateTime.now());
                    Timestamp sqlDataRetornoEfetiva = Timestamp.valueOf(emprestimo.getDataRetornoEfetiva());
                    stmt.setTimestamp(2, sqlDataRetornoEfetiva);
                    
                    Equipamento equipamento = new Equipamento();
                    emprestimo.listarEmprestimosAtivos(emprestimo);

                    // Solicita o código do equipamento e define no objeto emprestimo
                    System.out.println("Digite o código do empréstimo a ser devolvido: ");
                    emprestimo.setId(scanner.nextInt());

                    if(Emprestimos.verificaEmprestimoPorCodigo(emprestimo.getId()) == false){
                        System.out.println("Código de empréstimo não encontrado");
                        return;
                    }
                    stmt.setInt(3, emprestimo.getId());
                    Emprestimos.listaEmprestimoPorCodigo(emprestimo.getId());
                    System.out.println("\nConfirma a devolução do(s) item(ns)? Digite 1 para sim e 0 para não: ");
                    while(opcao != 0 && opcao != 1){
                        if(opcao == 0) {
                            System.out.println("Devolução abortada.");
                            return;
                        }else if(opcao == 1){
                            Equipamento.atualizarQuantidadeDevolucao();
                            stmt.setBoolean(1, false);
                            System.out.println("Empréstimo devolvido com sucesso!");
                        }
                    }
                    stmt.setBoolean(1, false);
                    
                    stmt.executeUpdate();


                    acho++;

            if (acho == 0) {
                System.out.println("Não foi achado um empréstimo com esse código de funcionário e equipamento ativo.");
            }

        } catch (SQLException ex) {
            System.out.println("Erro ao verificar a situação do empréstimo. CAUSA: " + ex);
        } finally {
            ConnectionFactory.closeConnection(con, stmt);
        }
    }

    public void historicoDeEmprestimos(Emprestimos emprestimo) {
        Connection con = ConnectionFactory.getConnection(); // Obtém uma conexão com o banco de dados
        PreparedStatement stmt = null; // Declaração SQL preparada
        ResultSet rs = null; // Resultado da consulta SQL
        String sql = "SELECT idEquipamento, idFuncionario, dataSaida, dataRetornoPrevista, observacoes, id FROM emprestimos";
        String orderEquipamento = "SELECT descricao FROM equipamentos WHERE id = ?";
        String orderFuncionario = "SELECT nome FROM funcionarios WHERE id = ?";
        int acho = 0; // Contador para verificar se há empréstimos ativos

        try {
            System.out.println("Histórico de Emprestimos:");
            stmt = con.prepareStatement(sql); // Prepara a declaração SQL
            rs = stmt.executeQuery(); // Executa a consulta SQL

            while (rs.next()) {

                Timestamp dataSaida = rs.getTimestamp("dataSaida");
                Timestamp dataRetornoPrevista = rs.getTimestamp("dataRetornoPrevista");
                emprestimo.setObservacoes(rs.getString("observacoes"));
                // Converte as datas de saída e retorno previstas para LocalDateTime
                emprestimo.setDataSaida(dataSaida.toLocalDateTime());
                emprestimo.setDataRetornoPrevista(dataRetornoPrevista.toLocalDateTime());

                // Prepara as declarações SQL para buscar a descrição do equipamento e o nome do funcionário
                stmt = con.prepareStatement(orderEquipamento);

                // Define o ID do equipamento no objeto emprestimo e na declaração SQL
                emprestimo.setIdEquipamento(rs.getString("idEquipamento"));
                stmt.setString(1, emprestimo.getIdEquipamento());
                rs = stmt.executeQuery();

                // Exibe a descrição do equipamento
                while (rs.next()) {
                    String descricao = rs.getString("descricao");
                    System.out.println("Equipamento: " + emprestimo.getIdEquipamento() + " - " + descricao);
                }

                stmt = con.prepareStatement(orderFuncionario);

                // Define o ID do funcionário no objeto emprestimo e na declaração SQL
                emprestimo.setIdFuncionario(rs.getString("idFuncionario"));
                stmt.setString(1, emprestimo.getIdFuncionario());
                rs = stmt.executeQuery();

                // Exibe o nome do funcionário
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    System.out.println("Funcionario: " + emprestimo.getIdFuncionario() + " - " + nome);
                }

                // Exibe as informações do empréstimo
                System.out.println("Data de Saida: " + emprestimo.getDataSaida());
                System.out.println("Data Prevista de Retorno: " + emprestimo.getDataRetornoPrevista());
                System.out.println("Observacoes: " + emprestimo.getObservacoes());
                System.out.println("--------------------------------------------------");

                acho++;
            }
            if (acho == 0) {
                System.out.println("\nNão há emprestimos registrados.\n");
            }

        } catch (SQLException e) {
            // Trata possíveis exceções de SQL
            System.out.println("Erro ao listar emprestimos. CAUSA: " + e);
        } finally {
            // Fecha a conexão com o banco de dados
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
    }

    //Metodo para listar os Emprestimos Ativos, recebendo como paramentro um obejto do tipo Emprestimos.
    public void listarEmprestimosAtivos(Emprestimos emprestimo) {

        Connection con = ConnectionFactory.getConnection(); // Obtém uma conexão com o banco de dados
        PreparedStatement stmt = null; // Declaração SQL preparada
        ResultSet rs = null; // Resultado da consulta SQL
        String sql = "SELECT idEquipamento, idFuncionario, dataSaida, dataRetornoPrevista, observacoes, id FROM emprestimos WHERE ativo = true";
        String orderEquipamento = "SELECT descricao FROM equipamentos WHERE id = ?";
        String orderFuncionario = "SELECT nome FROM funcionarios WHERE id = ?";
        int acho = 0; // Contador para verificar se há empréstimos ativos

        try {
            System.out.println("Emprestimos Ativos:");
            stmt = con.prepareStatement(sql); // Prepara a declaração SQL
            rs = stmt.executeQuery(); // Executa a consulta SQL

            while (rs.next()) {

                Timestamp dataSaida = rs.getTimestamp("dataSaida");
                Timestamp dataRetornoPrevista = rs.getTimestamp("dataRetornoPrevista");
                emprestimo.setObservacoes(rs.getString("observacoes"));
                // Converte as datas de saída e retorno previstas para LocalDateTime
                emprestimo.setDataSaida(dataSaida.toLocalDateTime());
                emprestimo.setDataRetornoPrevista(dataRetornoPrevista.toLocalDateTime());

                // Prepara as declarações SQL para buscar a descrição do equipamento e o nome do funcionário
                stmt = con.prepareStatement(orderEquipamento);

                // Define o ID do equipamento no objeto emprestimo e na declaração SQL
                emprestimo.setIdEquipamento(rs.getString("idEquipamento"));
                stmt.setString(1, emprestimo.getIdEquipamento());
                rs = stmt.executeQuery();

                // Exibe a descrição do equipamento
                while (rs.next()) {
                    String descricao = rs.getString("descricao");
                    System.out.println("Equipamento: " + emprestimo.getIdEquipamento() + " - " + descricao);
                }

                stmt = con.prepareStatement(orderFuncionario);

                // Define o ID do funcionário no objeto emprestimo e na declaração SQL
                emprestimo.setIdFuncionario(rs.getString("idFuncionario"));
                stmt.setString(1, emprestimo.getIdFuncionario());
                rs = stmt.executeQuery();

                // Exibe o nome do funcionário
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    System.out.println("Funcionario: " + emprestimo.getIdFuncionario() + " - " + nome);
                }

                // Exibe as informações do empréstimo
                System.out.println("Data de Saida: " + emprestimo.getDataSaida());
                System.out.println("Data Prevista de Retorno: " + emprestimo.getDataRetornoPrevista());
                System.out.println("Observacoes: " + emprestimo.getObservacoes());
                System.out.println("--------------------------------------------------");

                acho++;
            }
            if (acho == 0) {
                System.out.println("\nNão há emprestimos ativos.\n");
            }

        } catch (SQLException e) {
            // Trata possíveis exceções de SQL
            System.out.println("Erro ao listar emprestimos ativos. CAUSA: " + e);
        } finally {
            // Fecha a conexão com o banco de dados
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
    }

    public void listarEmprestimosAtrasados(Emprestimos emprestimo) {
        Connection con = ConnectionFactory.getConnection(); // Obtém uma conexão com o banco de dados
        PreparedStatement stmt = null; // Declaração SQL preparada
        ResultSet rs = null; // Resultado da consulta SQL
        String sql = "SELECT idEquipamento, idFuncionario, dataSaida, dataRetornoPrevista, dataRetornoEfetiva, observacoes FROM emprestimos WHERE ativo = true";
        String orderEquipamento = "SELECT descricao FROM equipamentos WHERE id = ?";
        String orderFuncionario = "SELECT nome FROM funcionarios WHERE id = ?";
        int acho = 0; // Contador para verificar se há empréstimos atrasados

            try{

                stmt = con.prepareStatement(sql); // Prepara a declaração SQL
                rs = stmt.executeQuery(); // Executa a consulta SQL

                while(rs.next()){
                    // Converte as datas de saída, retorno prevista e retorno efetiva para Timestamp
                    Timestamp dataSaida = rs.getTimestamp("dataSaida");
                    Timestamp dataRetornoPrevista = rs.getTimestamp("dataRetornoPrevista");
                    Timestamp dataRetornoEfetiva = rs.getTimestamp("dataRetornoEfetiva");


                    // Define as datas no objeto emprestimo
                    emprestimo.setDataSaida(dataSaida.toLocalDateTime());
                    emprestimo.setDataRetornoPrevista(dataRetornoPrevista.toLocalDateTime());
                    // Verifica se o empréstimo está atrasado
                    if(dataRetornoEfetiva == null && LocalDateTime.now().isAfter(emprestimo.getDataRetornoPrevista())){
                        System.out.println("Emprestimos Atrasados:");

                        if(LocalDateTime.now().isAfter(emprestimo.getDataRetornoPrevista()) == true){

                            // Prepara as declarações SQL para buscar a descrição do equipamento e o nome do funcionário
                            PreparedStatement stmt2 = con.prepareStatement(orderEquipamento);
                            PreparedStatement stmt3 = con.prepareStatement(orderFuncionario);

                            // Define o ID do equipamento no objeto emprestimo e na declaração SQL
                            emprestimo.setIdEquipamento(rs.getString("idEquipamento"));
                            stmt2.setString(1, emprestimo.getIdEquipamento());
                            ResultSet rs2 = stmt2.executeQuery();

                            // Exibe a descrição do equipamento
                            while(rs2.next()){
                                String descricao = rs2.getString("descricao");
                                System.out.println("Equipamento: " + emprestimo.getIdEquipamento() + " - " + descricao);
                            }
                            // Define o ID do funcionário no objeto emprestimo e na declaração SQL
                            emprestimo.setIdFuncionario(rs.getString("idFuncionario"));
                            stmt3.setString(1, emprestimo.getIdFuncionario());
                            ResultSet rs3 = stmt3.executeQuery();

                            // Exibe o nome do funcionário
                            while(rs3.next()){
                                String nome = rs3.getString("nome");
                                System.out.println("Funcionario: " + emprestimo.getIdFuncionario() + " - " + nome);
                            }

                            // Define as observações no objeto emprestimo
                            emprestimo.setObservacoes(rs.getString("observacoes"));

                            // Exibe as informações do empréstimo
                            System.out.println("Data de Saida: " + emprestimo.getDataSaida());
                            System.out.println("Data Prevista de Retorno: " + emprestimo.getDataRetornoPrevista());
                            System.out.println("Observacoes: " + emprestimo.getObservacoes());
                            System.out.println("--------------------------------------------------");
                            acho++;
                        }
                    }else if(dataRetornoEfetiva == null && LocalDateTime.now().isBefore(emprestimo.getDataRetornoPrevista())){
                        System.out.println("\n\nNão há empréstimos atrasados\n");

                    }else{
                        System.out.println("Emprestimos Atrasados:");
                        LocalDateTime javaDataRetornoEfetiva = dataRetornoEfetiva.toLocalDateTime();
                        if(javaDataRetornoEfetiva.isAfter(emprestimo.getDataRetornoPrevista()) == true){

                            PreparedStatement stmt2 = con.prepareStatement(orderEquipamento);
                            PreparedStatement stmt3 = con.prepareStatement(orderFuncionario);

                            // Obtém o ID do equipamento do ResultSet e o define no objeto emprestimo
                            emprestimo.setIdEquipamento(rs.getString("idEquipamento"));
                            stmt2.setString(1, emprestimo.getIdEquipamento());
                            ResultSet rs2 = stmt2.executeQuery();

                            while(rs2.next()){
                                String descricao = rs2.getString("descricao");
                                System.out.println("Equipamento: " + emprestimo.getIdEquipamento() + " - " + descricao);
                            }
                            // Obtém o ID do funcionário do ResultSet e o define no objeto emprestimo
                            emprestimo.setIdFuncionario(rs.getString("idFuncionario"));
                            stmt3.setString(1, emprestimo.getIdFuncionario());
                            ResultSet rs3 = stmt3.executeQuery();

                            while(rs3.next()){
                                String nome = rs3.getString("nome");
                                System.out.println("Funcionario: " + emprestimo.getIdFuncionario() + " - " + nome);
                            }

                            // Obtém as observações do ResultSet e as define no objeto emprestimo
                            emprestimo.setObservacoes(rs.getString("observacoes"));
                            // Imprime as informações do empréstimo
                            System.out.println("Data de Saida: " + emprestimo.getDataSaida());
                            System.out.println("Data Prevista de Retorno: " + emprestimo.getDataRetornoPrevista());
                            System.out.println("Observacoes: " + emprestimo.getObservacoes());
                            System.out.println("--------------------------------------------------");
                            acho++;
                        }

                    }
                }
                // Verifica se nenhum empréstimo foi encontrado
                if(acho == 0){
                    System.out.println("\nNão há emprestimos atrasados.\n");
                }

            }catch(SQLException e){
                    System.out.println("Erro ao listar emprestimos ativos. CAUSA: " + e);
            }finally{
                        ConnectionFactory.closeConnection(con, stmt, rs);
            }
    }
    
    public static boolean verificaEmprestimoPorCodigo(int codigoEquipamento) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT id FROM emprestimos WHERE id = ?";
        boolean encontrado = false;

        try {
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, codigoEquipamento);
            rs = stmt.executeQuery();

            if(rs.next()) {
                        encontrado = true;
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar o emprestimo. CAUSA: " + e);
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }

        return encontrado;
    }

    public static void listaEmprestimoPorCodigo(int codigoEquipamento) {
        Connection con = ConnectionFactory.getConnection(); // Obtém uma conexão com o banco de dados
        PreparedStatement stmt = null; // Declaração SQL preparada
        ResultSet rs = null; // Resultado da consulta SQL
        String sql = "SELECT idEquipamento, idFuncionario, dataSaida, dataRetornoPrevista, observacoes, id FROM emprestimos WHERE id = ?";
        String orderEquipamento = "SELECT descricao FROM equipamentos WHERE id = ?";
        String orderFuncionario = "SELECT nome FROM funcionarios WHERE id = ?";
        int acho = 0; // Contador para verificar se há empréstimos ativos

        Emprestimos emprestimo = new Emprestimos();
        try {
            System.out.println("Emprestimo:");
            stmt = con.prepareStatement(sql); // Prepara a declaração SQL
            stmt.setInt(1, codigoEquipamento);
            rs = stmt.executeQuery(); // Executa a consulta SQL

            while(rs.next()) {

                Timestamp dataSaida = rs.getTimestamp("dataSaida");
                Timestamp dataRetornoPrevista = rs.getTimestamp("dataRetornoPrevista");
                emprestimo.setObservacoes(rs.getString("observacoes"));
                // Converte as datas de saída e retorno previstas para LocalDateTime
                emprestimo.setDataSaida(dataSaida.toLocalDateTime());
                emprestimo.setDataRetornoPrevista(dataRetornoPrevista.toLocalDateTime());

                // Prepara as declarações SQL para buscar a descrição do equipamento e o nome do funcionário
                stmt = con.prepareStatement(orderEquipamento);

                // Define o ID do equipamento no objeto emprestimo e na declaração SQL
                emprestimo.setIdEquipamento(rs.getString("idEquipamento"));
                stmt.setString(1, emprestimo.getIdEquipamento());
                rs = stmt.executeQuery();

                // Exibe a descrição do equipamento
                while (rs.next()) {
                    String descricao = rs.getString("descricao");
                    System.out.println("Equipamento: " + emprestimo.getIdEquipamento() + " - " + descricao);
                }

                stmt = con.prepareStatement(orderFuncionario);

                // Define o ID do funcionário no objeto emprestimo e na declaração SQL
                emprestimo.setIdFuncionario(rs.getString("idFuncionario"));
                stmt.setString(1, emprestimo.getIdFuncionario());
                rs = stmt.executeQuery();

                // Exibe o nome do funcionário
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    System.out.println("Funcionario: " + emprestimo.getIdFuncionario() + " - " + nome);
                }

                // Exibe as informações do empréstimo
                System.out.println("Data de Saida: " + emprestimo.getDataSaida());
                System.out.println("Data Prevista de Retorno: " + emprestimo.getDataRetornoPrevista());
                System.out.println("Observacoes: " + emprestimo.getObservacoes());
                System.out.println("--------------------------------------------------");

                acho++;
            }
            if (acho == 0) {
                System.out.println("\nNão há emprestimos registrados.\n");
            }

        } catch (SQLException e) {
            // Trata possíveis exceções de SQL
            System.out.println("Erro ao listar emprestimos. CAUSA: " + e);
        } finally {
            // Fecha a conexão com o banco de dados
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
        return;
    }

}



