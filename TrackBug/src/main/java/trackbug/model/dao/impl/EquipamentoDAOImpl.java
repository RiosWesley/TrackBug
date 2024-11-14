// File: src/main/java/trackbug/model/dao/impl/EquipamentoDAOImpl.java
package trackbug.model.dao.impl;

import trackbug.model.dao.interfaces.EquipamentoDAO;
import trackbug.model.entity.Equipamento;
import trackbug.util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipamentoDAOImpl implements EquipamentoDAO {

    @Override
    public void criar(Equipamento equipamento) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "INSERT INTO equipamentos (id, descricao, dataCompra, peso, largura, " +
                    "comprimento, tipo, quantidadeAtual, quantidadeEstoque, quantidadeMinima, " +
                    "status, tipo_uso, usoUnico) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, equipamento.getId());
            stmt.setString(2, equipamento.getDescricao());
            stmt.setDate(3, Date.valueOf(equipamento.getDataCompra()));

            // Tratamento para valores nulos
            Double peso = equipamento.getPeso();
            if (peso != null) {
                stmt.setDouble(4, peso);
            } else {
                stmt.setNull(4, Types.DOUBLE);
            }

            Double largura = equipamento.getLargura();
            if (largura != null) {
                stmt.setDouble(5, largura);
            } else {
                stmt.setNull(5, Types.DOUBLE);
            }

            Double comprimento = equipamento.getComprimento();
            if (comprimento != null) {
                stmt.setDouble(6, comprimento);
            } else {
                stmt.setNull(6, Types.DOUBLE);
            }

            stmt.setBoolean(7, equipamento.isTipo());
            stmt.setInt(8, equipamento.getQuantidadeAtual());
            stmt.setInt(9, equipamento.getQuantidadeEstoque());
            stmt.setInt(10, equipamento.getQuantidadeMinima());
            stmt.setString(11, "Disponível");
            stmt.setString(12, equipamento.getTipoUso());
            stmt.setBoolean(13, false);

            stmt.executeUpdate();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    @Override
    public Equipamento buscarPorId(String id) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();

            String sql = """
                SELECT id, descricao, dataCompra, peso, largura, comprimento,
                       tipo, quantidadeAtual, quantidadeEstoque, quantidadeMinima,
                       tipo_uso, status
                FROM equipamentos 
                WHERE id = ?
            """;

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearResultSet(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new Exception("Erro ao buscar equipamento: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public List<Equipamento> listarTodos() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Equipamento> equipamentos = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM equipamentos ORDER BY descricao";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                equipamentos.add(mapearResultSet(rs));
            }

            return equipamentos;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public List<Equipamento> listarDisponiveis() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Equipamento> equipamentos = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM equipamentos WHERE status = 'Disponível' ORDER BY descricao";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                equipamentos.add(mapearResultSet(rs));
            }

            return equipamentos;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    public List<Equipamento> listarEmprestados() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Equipamento> equipamentos = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM equipamentos WHERE quantidadeAtual != quantidadeEstoque  ORDER BY descricao";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                equipamentos.add(mapearResultSet(rs));
            }

            return equipamentos;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    public List<Equipamento> listarEstoqueBaixo() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Equipamento> equipamentos = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM equipamentos WHERE quantidadeEstoque < quantidadeMinima  ORDER BY descricao";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                equipamentos.add(mapearResultSet(rs));
            }

            return equipamentos;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public void atualizar(Equipamento equipamento) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); // Inicia transação

            String sql = """
            UPDATE equipamentos 
            SET descricao = ?,
                dataCompra = ?,
                peso = ?,
                largura = ?,
                comprimento = ?,
                tipo = ?,
                quantidadeAtual = ?,
                quantidadeEstoque = ?,
                quantidadeMinima = ?,
                tipo_uso = ?,
                status = CASE 
                    WHEN ? = 0 THEN 'Esgotado'
                    ELSE 'Disponível'
                END
            WHERE id = ?
        """;

            stmt = conn.prepareStatement(sql);
            int index = 1;

            stmt.setString(index++, equipamento.getDescricao());
            stmt.setDate(index++, Date.valueOf(equipamento.getDataCompra()));
            stmt.setDouble(index++, equipamento.getPeso());
            stmt.setDouble(index++, equipamento.getLargura());
            stmt.setDouble(index++, equipamento.getComprimento());
            stmt.setBoolean(index++, equipamento.isTipo());
            stmt.setInt(index++, equipamento.getQuantidadeAtual());
            stmt.setInt(index++, equipamento.getQuantidadeEstoque());
            stmt.setInt(index++, equipamento.getQuantidadeMinima());
            stmt.setString(index++, equipamento.getTipoUso());
            // Parâmetro para o CASE do status
            stmt.setInt(index++, equipamento.getQuantidadeAtual());
            // ID do equipamento
            stmt.setString(index++, equipamento.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Falha ao atualizar equipamento: nenhum registro foi modificado.");
            }

            conn.commit();

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                throw new Exception("Erro ao realizar rollback: " + ex.getMessage());
            }
            throw new Exception("Erro ao atualizar equipamento: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    @Override
    public void deletar(String id) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "DELETE FROM equipamentos WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            stmt.executeUpdate();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    @Override
    public void atualizarQuantidade(String id, int quantidade) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            String sql = """
                        UPDATE equipamentos 
                        SET quantidadeAtual = ?,
                            status = CASE 
                                WHEN ? = 0 THEN 'Esgotado'
                                ELSE 'Disponível'
                            END
                        WHERE id = ?
                    """;

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quantidade);
            stmt.setInt(2, quantidade);
            stmt.setString(3, id);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Falha ao atualizar quantidade: equipamento não encontrado.");
            }

            conn.commit();

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                throw new Exception("Erro ao realizar rollback: " + ex.getMessage());
            }
            throw new Exception("Erro ao atualizar quantidade: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    @Override
    public boolean verificarEmprestimosAtivos(String id) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();

            String sql = """
                SELECT COUNT(*) as total 
                FROM emprestimos 
                WHERE idEquipamento = ? 
                AND ativo = true
            """;

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
            return false;

        } catch (SQLException e) {
            throw new Exception("Erro ao verificar empréstimos ativos: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }


    private Equipamento mapearResultSet(ResultSet rs) throws SQLException {
        Equipamento equipamento = new Equipamento();

        equipamento.setId(rs.getString("id"));
        equipamento.setDescricao(rs.getString("descricao"));
        equipamento.setDataCompra(rs.getDate("dataCompra").toLocalDate());
        equipamento.setPeso(rs.getDouble("peso"));
        equipamento.setLargura(rs.getDouble("largura"));
        equipamento.setComprimento(rs.getDouble("comprimento"));
        equipamento.setTipo(rs.getBoolean("tipo"));
        equipamento.setQuantidadeAtual(rs.getInt("quantidadeAtual"));
        equipamento.setQuantidadeEstoque(rs.getInt("quantidadeEstoque"));
        equipamento.setQuantidadeMinima(rs.getInt("quantidadeMinima"));
        equipamento.setTipoUso(rs.getString("tipo_uso"));

        return equipamento;
    }

    public String buscarNomePorId(String id) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT descricao FROM equipamentos WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("descricao");
            }
            return null;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }
}