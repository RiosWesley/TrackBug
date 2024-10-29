package trackbug;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

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

    // Metodo para registrar um equipamento no banco de dados
    public void registrarEquipamento(Equipamento equipamento) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        String sql = "INSERT INTO equipamentos (id, descricao, dataCompra, peso, largura, comprimento, quantidadeAtual, quantidadeEstoque, tipo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            stmt = con.prepareStatement(sql);
            Scanner scanner = new Scanner(System.in);

            // Entrada de dados

            System.out.println("Digite o código do equipamento: ");
            equipamento.setId(scanner.nextLine());
            System.out.println("Digite a descrição do equipamento: ");
            equipamento.setDescricao(scanner.nextLine());
            System.out.println("Digite a data de compra do equipamento (yyyy-mm-dd): ");
            equipamento.setDataCompra(LocalDate.parse(scanner.nextLine()));
            System.out.println("Digite o peso do equipamento (g): ");
            equipamento.setPeso(scanner.nextDouble());
            System.out.println("Digite a largura do equipamento (cm): ");
            equipamento.setLargura(scanner.nextDouble());
            System.out.println("Digite o comprimento do equipamento (cm): ");
            equipamento.setComprimento(scanner.nextDouble());
            System.out.println("O item é emprestável ou consumível? Digite 0 para emprestável e 1 para consumível: ");
            int opcao = scanner.nextInt();
            boolean tipoEquip = false;
            do {
                if(opcao == 0){
                    equipamento.setTipo(tipoEquip);
                    System.out.println();
                }else if(opcao == 1){
                    tipoEquip = true;
                    equipamento.setTipo(tipoEquip);
                }else{
                    System.out.println("Digite um número válido! 0 ou 1: ");
                    opcao = scanner.nextInt();
                }
            }while(opcao != 0 && opcao != 1);
            System.out.println(equipamento.isTipo());

            System.out.println("Digite a quantidade inicial do item: ");
            equipamento.setQuantidadeEstoque(scanner.nextInt());


            scanner.nextLine(); // Limpar o buffer
            
            // Insere os dados na tabela do banco de dados.
            Date sqlDate = Date.valueOf(equipamento.getDataCompra());
            stmt.setString(1, equipamento.getId());
            stmt.setString(2, equipamento.getDescricao());
            stmt.setDate(3, sqlDate);
            stmt.setDouble(4, equipamento.getPeso());
            stmt.setDouble(5, equipamento.getLargura());
            stmt.setDouble(6, equipamento.getComprimento());
            if(equipamento.isTipo() == false){
                stmt.setInt(7, equipamento.getQuantidadeEstoque());
                stmt.setInt(8, equipamento.getQuantidadeEstoque());
            }else if(equipamento.isTipo() == true){
                System.out.println("Digite a quantidade mínima que o item pode ter: ");
                equipamento.setQuantidadeAtual(scanner.nextInt());
                stmt.setInt(7, equipamento.getQuantidadeAtual());
                stmt.setInt(8, equipamento.getQuantidadeEstoque());
            }
            stmt.setBoolean(9, equipamento.isTipo());

            stmt.executeUpdate();
            System.out.println("Equipamento registrado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao registrar o equipamento. CAUSA: " + e);
        } finally {
            ConnectionFactory.closeConnection(con, stmt);
        }
    }

    // Metodo para listar todos os equipamentos
    public void listarEquipamentos(Equipamento equipamento) {
        Scanner scanner = new Scanner(System.in);
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String estoqueTotal = "SELECT id, descricao, dataCompra, peso, largura, comprimento, quantidadeAtual, quantidadeEstoque, tipo FROM equipamentos";
        String estoqueConsumivel = "SELECT id, descricao, dataCompra, peso, largura, comprimento, quantidadeAtual, quantidadeEstoque, tipo FROM equipamentos WHERE tipo = true";
        String estoqueConsumivelBaixo = "SELECT id, descricao, dataCompra, peso, largura, comprimento, quantidadeAtual, quantidadeEstoque, tipo FROM equipamentos WHERE tipo = true AND quantidadeEstoque < quantidadeAtual";
        String estoqueConsumivelZero = "SELECT id, descricao, dataCompra, peso, largura, comprimento, quantidadeAtual, quantidadeEstoque, tipo FROM equipamentos WHERE tipo = true AND quantidadeAtual = 0";
        String estoqueEmprestimo = "SELECT id, descricao, dataCompra, peso, largura, comprimento, quantidadeAtual, quantidadeEstoque, tipo FROM equipamentos WHERE tipo = false";
        String estoqueEmprestimoCheio = "SELECT id, descricao, dataCompra, peso, largura, comprimento, quantidadeAtual, quantidadeEstoque, tipo FROM equipamentos WHERE quantidadeAtual = quantidadeEstoque AND tipo = false";
        String estoqueEmprestados = "SELECT id, descricao, dataCompra, peso, largura, comprimento, quantidadeAtual, quantidadeEstoque, tipo FROM equipamentos WHERE quantidadeAtual < quantidadeEstoque AND tipo = false";
        String estoqueEmprestimoZero = "SELECT id, descricao, dataCompra, peso, largura, comprimento, quantidadeAtual, quantidadeEstoque, tipo FROM equipamentos WHERE quantidadeAtual = 0 AND tipo = false";
        int registros = 0;

        try {
            System.out.println("\n1. Itens consumíveis e emprestáveis.");
            System.out.println("2. Itens consumíveis.");
            System.out.println("3. Itens consumíveis zerados.");
            System.out.println("4. Itens consumíveis de estoque baixo.");
            System.out.println("5. Itens emprestáveis.");
            System.out.println("6. Itens emprestáveis sem empréstimo efetivado.");
            System.out.println("7. Itens emprestados.");
            System.out.println("8. Itens que estão com todas as quantidades emprestadas");
            System.out.println("Escolha uma opção inserindo o número: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();
            switch(opcao){
                case 1 -> {
                    stmt = con.prepareStatement(estoqueTotal);
                }
                case 2 -> {
                    stmt = con.prepareStatement(estoqueConsumivel);
                }
                case 3 -> {
                    stmt = con.prepareStatement(estoqueConsumivelZero);
                }
                case 4 -> {
                    stmt = con.prepareStatement(estoqueConsumivelBaixo);
                }
                case 5 -> {
                    stmt = con.prepareStatement(estoqueEmprestimo);
                }
                case 6 -> {
                    stmt = con.prepareStatement(estoqueEmprestimoCheio);
                }
                case 7 -> {
                    stmt = con.prepareStatement(estoqueEmprestados);
                }
                case 8 -> {
                    stmt = con.prepareStatement(estoqueEmprestimoZero);
                }
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                //Pega os dados da tabela do banco, e insere em um objeto Equipamento
                equipamento.setId(rs.getString("id"));
                equipamento.setDescricao(rs.getString("descricao"));
                equipamento.setDataCompra(rs.getDate("dataCompra").toLocalDate());
                equipamento.setPeso(rs.getDouble("peso"));
                equipamento.setLargura(rs.getDouble("largura"));
                equipamento.setComprimento(rs.getDouble("comprimento"));
                equipamento.setQuantidadeAtual(rs.getInt("quantidadeAtual"));
                equipamento.setQuantidadeEstoque(rs.getInt("quantidadeEstoque"));
                equipamento.setTipo(rs.getBoolean("tipo"));
                // Exibir os dados do equipamento
                dadosEquipamentos(equipamento);

                registros++;
            }

            if (registros == 0) {
                System.out.println("Nenhum equipamento encontrado.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar os equipamentos. CAUSA: " + e);
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
    }

    // Exibir os dados do equipamento
    public static void dadosEquipamentos(Equipamento equipamento) {

        System.out.println("Código: " + equipamento.getId());
        System.out.println("Descrição: " + equipamento.getDescricao());
        System.out.println("Data de compra: " + equipamento.getDataCompra());
        System.out.println("Peso: " + equipamento.getPeso() + "g");
        System.out.println("Largura: " + equipamento.getLargura() + "cm");
        System.out.println("Comprimento: " + equipamento.getComprimento() + "cm");
        if(equipamento.isTipo() == true){
            System.out.println("Quantidade mínima exigida: " + equipamento.getQuantidadeAtual());
            System.out.println("Quantidade total: " + equipamento.getQuantidadeEstoque());
            System.out.println("Equipamento consumível");
        }else{
            System.out.println("Quantidade atual: " + equipamento.getQuantidadeAtual());
            System.out.println("Quantidade total: " + equipamento.getQuantidadeEstoque());
            System.out.println("Equipamento emprestável");
        }
        System.out.println("========================================");
    }

    // Metodo para buscar um equipamento pelo código, recebe como parametro o codigo do equipamento
    public static boolean buscarEquipamentoPorCodigo(String codigoEquipamento) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT id, descricao, dataCompra, peso, largura, comprimento, quantidadeAtual, quantidadeEstoque, tipo FROM equipamentos WHERE id = ?";
        boolean encontrado = false;

        try {
            stmt = con.prepareStatement(sql);
            //Setta o codigo do equipamento para o Prepare Statement
            stmt.setString(1, codigoEquipamento);
            rs = stmt.executeQuery();

            Equipamento equipamento = new Equipamento();
            if (rs.next()) {
                //Setta os dados ta tabela para objeto equipamento.
                equipamento.setId(rs.getString("id"));
                equipamento.setDescricao(rs.getString("descricao"));
                equipamento.setDataCompra(rs.getDate("dataCompra").toLocalDate());
                equipamento.setPeso(rs.getDouble("peso"));
                equipamento.setLargura(rs.getDouble("largura"));
                equipamento.setComprimento(rs.getDouble("comprimento"));
                equipamento.setQuantidadeAtual(rs.getInt("quantidadeAtual"));
                equipamento.setQuantidadeEstoque(rs.getInt("quantidadeEstoque"));
                equipamento.setTipo(rs.getBoolean("tipo"));

                //Printa as informacoes recebidas
                dadosEquipamentos(equipamento);

                encontrado = true;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar o equipamento. CAUSA: " + e);
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
        return encontrado;
    }

    public static boolean defineTipoDeSaida(String codigoEquipamento) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT tipo FROM equipamentos WHERE id = ?";
        boolean encontrado = false;

        try {
            stmt = con.prepareStatement(sql);
            //Setta o codigo do equipamento para o Prepare Statement
            stmt.setString(1, codigoEquipamento);
            rs = stmt.executeQuery();
            Equipamento equipamento = new Equipamento();

            if (rs.next()) {
                //Setta os dados ta tabela para objeto equipamento.
                if(rs.getBoolean("tipo") == true){
                    encontrado = true;
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar o equipamento. CAUSA: " + e);
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
        return encontrado;
    }

    public static boolean verificaQuantidade(int quantidadeSolicitada, String codigoEquipamento){
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT id, descricao, dataCompra, peso, largura, comprimento, quantidadeAtual, quantidadeEstoque, tipo FROM equipamentos WHERE id = ?";
        boolean encontrado = true;

        try {
            stmt = con.prepareStatement(sql);
            //Setta o codigo do equipamento para o Prepare Statement
            stmt.setString(1, codigoEquipamento);
            rs = stmt.executeQuery();

            if (rs.next()) {
                if(quantidadeSolicitada > rs.getInt("quantidadeAtual")){
                    encontrado = false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar o equipamento. CAUSA: " + e);
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
        return encontrado;
    }

    public static void atualizarQuantidadeSaida(int quantidadeSolicitada, String codigoEquipamento, boolean tipo){
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String buscarQuantidadeEmprestimo = "SELECT quantidadeAtual FROM equipamentos WHERE id = ?";
        String buscarQuantidadeConsumo = "SELECT quantidadeEstoque FROM equipamentos WHERE id = ?";
        String atualizarEstoqueEmprestimo = "UPDATE quantidadeAtual = ? FROM equipamentos WHERE id = ?";
        String atualizarEstoqueConsumo = "UPDATE quantidadeEstoque = ? FROM equipamentos WHERE id = ?";

        Equipamento equipamento = new Equipamento();
        try {
            if(tipo == false){
                stmt = con.prepareStatement(buscarQuantidadeEmprestimo);
            }else{
                stmt = con.prepareStatement(buscarQuantidadeConsumo);
            }
            //Setta o codigo do equipamento para o Prepare Statement
            stmt.setString(1, codigoEquipamento);
            rs = stmt.executeQuery();

            if (rs.next()) {
                if(tipo == false){
                    equipamento.setQuantidadeAtual(rs.getInt("quantidadeAtual") - quantidadeSolicitada);
                }else{
                    equipamento.setQuantidadeAtual(rs.getInt("quantidadeEstoque") - quantidadeSolicitada);
                }
            }

            if(tipo == false){
                stmt = con.prepareStatement(atualizarEstoqueEmprestimo);
            }else{
                stmt = con.prepareStatement(atualizarEstoqueConsumo);
            }

            stmt.setString(2, codigoEquipamento);
            stmt.setInt(1, equipamento.getQuantidadeAtual());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao buscar o equipamento. CAUSA: " + e);
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
        return;
    }

    public static void atualizarQuantidadeDevolucao(){
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String buscarEquipamento = "SELECT quantidadeEmprestimo, idEquipamento WHERE id = ?";
        String buscarTipo = "SELECT tipo WHERE id = ?";
        String buscarQuantidadeEmprestimo = "SELECT quantidadeAtual FROM equipamentos WHERE id = ?";
        String buscarQuantidadeConsumo = "SELECT quantidadeEstoque FROM equipamentos WHERE id = ?";
        String atualizarEstoqueEmprestimo = "UPDATE quantidadeAtual = ? FROM equipamentos WHERE id = ?";
        String atualizarEstoqueConsumo = "UPDATE quantidadeEstoque = ? FROM equipamentos WHERE id = ?";
        int quantidade = 0;
        String codigoEquipamento = "?";
        boolean tipo = false;

        Equipamento equipamento = new Equipamento();
        try {
            //Busca a quantidade solicitada no uso ou empréstimo e o id do equipamento envolvido na operação.
            stmt = con.prepareStatement(buscarEquipamento);
            rs = stmt.executeQuery();
            while(rs.next()){
                quantidade = rs.getInt("quantidadeEmprestimo");
                codigoEquipamento = rs.getString("idEquipamento");
            }

            //Busca o tipo do item, se ele é consumível ou emprestável.
            stmt = con.prepareStatement(buscarTipo);
            rs = stmt.executeQuery();
            while (rs.next()){
                tipo = rs.getBoolean("tipo");
            }

            //Define a operação, se é a devolução de um empréstimo ou itens consumíveis que não foram utilizados, para buscar o estoque atual do item.
            if(tipo == false){
                stmt = con.prepareStatement(buscarQuantidadeEmprestimo);

            }else{
                stmt = con.prepareStatement(buscarQuantidadeConsumo);
            }
            //Setta o codigo do equipamento.
            stmt.setString(1, codigoEquipamento);
            rs = stmt.executeQuery();

            //Define o novo valor do estoque, depois da devolução do item, se é a devolução de um empréstimo ou itens consumíveis que não foram utilizados.
            if (rs.next()) {
                if(tipo == false){
                    equipamento.setQuantidadeAtual(rs.getInt("quantidadeAtual") + quantidade);
                }else{
                    equipamento.setQuantidadeAtual(rs.getInt("quantidadeEstoque") + quantidade);
                }
            }

            //Atualiza o estoque do item consumível ou emprestável.
            if(tipo == false){
                stmt = con.prepareStatement(atualizarEstoqueEmprestimo);
            }else{
                stmt = con.prepareStatement(atualizarEstoqueConsumo);
            }
            stmt.setString(2, codigoEquipamento);
            stmt.setInt(1, equipamento.getQuantidadeAtual());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao buscar o equipamento. CAUSA: " + e);
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
        return;
    }
}
