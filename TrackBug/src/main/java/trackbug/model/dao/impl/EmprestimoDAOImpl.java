// File: src/main/java/trackbug/model/dao/impl/EmprestimoDAOImpl.java
package trackbug.model.dao.impl;

import trackbug.model.dao.interfaces.EmprestimoDAO;
import trackbug.model.entity.Emprestimo;
import trackbug.util.ConnectionFactory;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAOImpl implements EmprestimoDAO {

    @Override
    public void criar(Emprestimo emprestimo) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO emprestimos (idFuncionario, idEquipamento, dataSaida, " +
                    "dataRetornoPrevista, dataRetornoEfetiva, observacoes, ativo, quantidadeEmprestimo, " +
                    "tipoOperacao, usoUnico) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, emprestimo.getIdFuncionario());
            stmt.setString(2, emprestimo.getIdEquipamento());
            stmt.setTimestamp(3, Timestamp.valueOf(emprestimo.getDataSaida()));
            stmt.setTimestamp(4, Timestamp.valueOf(emprestimo.getDataRetornoPrevista()));
            if(emprestimo.isUsoUnico()) {
                stmt.setTimestamp(5, Timestamp.valueOf(emprestimo.getDataRetornoPrevista()));
            }else{
                stmt.setNull(5, Types.TIMESTAMP);
            }
            stmt.setString(6, emprestimo.getObservacoes());
            stmt.setBoolean(7, !emprestimo.isUsoUnico());
            stmt.setInt(8, emprestimo.getQuantidadeEmprestimo());
            stmt.setString(9, emprestimo.getTipoOperacao());
            stmt.setBoolean(10, emprestimo.isUsoUnico());

            stmt.executeUpdate();

            // Atualiza quantidade do equipamento
            atualizarQuantidadeEquipamento(conn, emprestimo.getIdEquipamento(),
                    emprestimo.getQuantidadeEmprestimo(), true);

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new Exception("Erro ao realizar rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    @Override
    public void registrarDevolucao(int id, LocalDateTime dataRetorno) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            // Primeiro, busca informações do empréstimo
            String sqlBusca = "SELECT idEquipamento, quantidadeEmprestimo FROM emprestimos WHERE id = ?";
            stmt = conn.prepareStatement(sqlBusca);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String idEquipamento = rs.getString("idEquipamento");
                int quantidade = rs.getInt("quantidadeEmprestimo");

                // Atualiza o empréstimo
                String sqlUpdate = "UPDATE emprestimos SET dataRetornoEfetiva = ?, ativo = false " +
                        "WHERE id = ?";
                stmt = conn.prepareStatement(sqlUpdate);
                stmt.setTimestamp(1, Timestamp.valueOf(dataRetorno));
                stmt.setInt(2, id);
                stmt.executeUpdate();

                // Atualiza quantidade do equipamento
                atualizarQuantidadeEquipamento(conn, idEquipamento, quantidade, false);

                conn.commit();
            }
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new Exception("Erro ao realizar rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private void atualizarQuantidadeEquipamento(Connection conn, String idEquipamento,
                                                int quantidade, boolean isEmprestimo) throws SQLException {
        boolean esgotado = false, tipo_uso = false;
        String sql = "UPDATE equipamentos SET quantidadeAtual = quantidadeAtual " +
                (isEmprestimo ? "- ?" : "+ ?") + " WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantidade);
            stmt.setString(2, idEquipamento);
            stmt.executeUpdate();
        }

        sql = "SELECT quantidadeAtual, tipo_uso FROM equipamentos WHERE id = ?";
        ResultSet rs = null;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idEquipamento);
            rs = stmt.executeQuery();
            if(rs.next()) {
                if(rs.getInt("quantidadeAtual") == 0){
                    esgotado = true;
                }
                if(rs.getString("tipo_uso").equals("Uso Único")){
                    tipo_uso = true;
                }
            }
        }

        if(esgotado == true) {
            sql = "UPDATE equipamentos SET status = 'Esgotado' WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, idEquipamento);
                stmt.executeUpdate();
            }
        }else{
            sql = "UPDATE equipamentos SET status = 'Disponível' WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, idEquipamento);
                stmt.executeUpdate();
            }
        }

        if(tipo_uso == true) {
            sql = "UPDATE equipamentos SET quantidadeEstoque = quantidadeEstoque" + (isEmprestimo ? "- ?" : "+ ?") +  " WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, quantidade);
                stmt.setString(2, idEquipamento);
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public Emprestimo buscarPorId(int id) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM emprestimos WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearResultSet(rs);
            }
            return null;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public List<Emprestimo> listarTodos() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Emprestimo> emprestimos = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT e.*, f.nome as nome_funcionario, eq.descricao as descricao_equipamento " +
                    "FROM emprestimos e " +
                    "JOIN funcionarios f ON e.idFuncionario = f.id " +
                    "JOIN equipamentos eq ON e.idEquipamento = eq.id " +
                    "ORDER BY e.dataSaida DESC";

            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                emprestimos.add(mapearResultSet(rs));
            }

            return emprestimos;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    public List<Emprestimo> listarAtivos() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Emprestimo> emprestimos = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT e.*, f.nome as nome_funcionario, eq.descricao as descricao_equipamento " +
                    "FROM emprestimos e " +
                    "JOIN funcionarios f ON e.idFuncionario = f.id " +
                    "JOIN equipamentos eq ON e.idEquipamento = eq.id " +
                    "WHERE e.ativo = true AND eq.tipo_uso = 'Reutilizável' " +
                    "ORDER BY e.dataSaida DESC";

            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                emprestimos.add(mapearResultSet(rs));
            }
            return emprestimos;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public List<Emprestimo> listarAtrasados() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Emprestimo> emprestimos = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT e.*, f.nome as nome_funcionario, eq.descricao as descricao_equipamento" +
                    " FROM emprestimos e " +
                    "JOIN equipamentos eq ON e.idEquipamento = eq.id " +
                    "JOIN funcionarios f ON e.idFuncionario = f.id " +
                    "WHERE e.ativo = true " +
                    "AND e.dataRetornoPrevista < NOW() " +
                    "AND eq.tipo_uso = 'Reutilizável' " +
                    "ORDER BY e.dataRetornoPrevista ASC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                emprestimos.add(mapearResultSet(rs));
            }

            return emprestimos;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public List<Emprestimo> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Emprestimo> emprestimos = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM emprestimos WHERE dataSaida BETWEEN ? AND ? " +
                    "ORDER BY dataSaida DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fim));
            rs = stmt.executeQuery();

            while (rs.next()) {
                emprestimos.add(mapearResultSet(rs));
            }

            return emprestimos;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public List<Emprestimo> buscarPorFuncionario(String idFuncionario) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Emprestimo> emprestimos = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT emp.*, f.nome as nome_funcionario, eq.descricao as descricao_equipamento " +
                    "FROM emprestimos emp " +
                    "JOIN funcionarios f ON emp.idFuncionario = f.id " +
                    "JOIN equipamentos eq ON emp.idEquipamento = eq.id " +
                    "WHERE emp.idFuncionario = ? " +
                    "ORDER BY emp.dataSaida DESC";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, idFuncionario);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Emprestimo emprestimo = mapearResultSet(rs);
                // Adicionando informações extras que podem ser úteis
                emprestimo.setNomeFuncionario(rs.getString("nome_funcionario"));
                emprestimo.setDescricaoEquipamento(rs.getString("descricao_equipamento"));
                emprestimos.add(emprestimo);
            }

            return emprestimos;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }
    @Override
    public List<Emprestimo> buscarPorEquipamento(String idEquipamento) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Emprestimo> emprestimos = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM emprestimos WHERE idEquipamento = ? " +
                    "ORDER BY dataSaida DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, idEquipamento);
            rs = stmt.executeQuery();

            while (rs.next()) {
                emprestimos.add(mapearResultSet(rs));
            }

            return emprestimos;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public void atualizar(Emprestimo emprestimo) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "UPDATE emprestimos SET idFuncionario = ?, idEquipamento = ?, " +
                    "dataSaida = ?, dataRetornoPrevista = ?, dataRetornoEfetiva = ?, " +
                    "observacoes = ?, ativo = ?, quantidadeEmprestimo = ?, " +
                    "tipoOperacao = ?, usoUnico = ? WHERE id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, emprestimo.getIdFuncionario());
            stmt.setString(2, emprestimo.getIdEquipamento());
            stmt.setTimestamp(3, Timestamp.valueOf(emprestimo.getDataSaida()));
            stmt.setTimestamp(4, Timestamp.valueOf(emprestimo.getDataRetornoPrevista()));
            stmt.setTimestamp(5, emprestimo.getDataRetornoEfetiva() != null ?
                    Timestamp.valueOf(emprestimo.getDataRetornoEfetiva()) : null);
            stmt.setString(6, emprestimo.getObservacoes());
            stmt.setBoolean(7, emprestimo.isAtivo());
            stmt.setInt(8, emprestimo.getQuantidadeEmprestimo());
            stmt.setString(9, emprestimo.getTipoOperacao());
            stmt.setBoolean(10, emprestimo.isUsoUnico());
            stmt.setInt(11, emprestimo.getId());

            stmt.executeUpdate();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    private Emprestimo mapearResultSet(ResultSet rs) throws SQLException {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(rs.getInt("id"));
        emprestimo.setIdFuncionario(rs.getString("idFuncionario"));
        emprestimo.setIdEquipamento(rs.getString("idEquipamento"));

        Timestamp dataSaida = rs.getTimestamp("dataSaida");
        if (dataSaida != null) {
            emprestimo.setDataSaida(dataSaida.toLocalDateTime());
        }

        Timestamp dataRetornoPrevista = rs.getTimestamp("dataRetornoPrevista");
        if (dataRetornoPrevista != null) {
            emprestimo.setDataRetornoPrevista(dataRetornoPrevista.toLocalDateTime());
        }

        Timestamp dataRetornoEfetiva = rs.getTimestamp("dataRetornoEfetiva");
        if (dataRetornoEfetiva != null) {
            emprestimo.setDataRetornoEfetiva(dataRetornoEfetiva.toLocalDateTime());
        }

        emprestimo.setObservacoes(rs.getString("observacoes"));
        emprestimo.setAtivo(rs.getBoolean("ativo"));
        emprestimo.setQuantidadeEmprestimo(rs.getInt("quantidadeEmprestimo"));
        emprestimo.setTipoOperacao(rs.getString("tipoOperacao"));
        emprestimo.setUsoUnico(rs.getBoolean("usoUnico"));

        // Tentar obter os campos adicionais se existirem
        try {
            String nomeFuncionario = rs.getString("nome_funcionario");
            String descricaoEquipamento = rs.getString("descricao_equipamento");

            if (nomeFuncionario != null) {
                emprestimo.setNomeFuncionario(nomeFuncionario);
            }
            if (descricaoEquipamento != null) {
                emprestimo.setDescricaoEquipamento(descricaoEquipamento);
            }
        } catch (SQLException e) {
            // Ignora se os campos não existirem no ResultSet
        }

        return emprestimo;
    }
}