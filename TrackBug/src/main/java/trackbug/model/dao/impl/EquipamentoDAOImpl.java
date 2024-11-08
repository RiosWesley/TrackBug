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
            stmt.setDouble(4, equipamento.getPeso() != null ? equipamento.getPeso() : 0);
            stmt.setDouble(5, equipamento.getLargura() != null ? equipamento.getLargura() : 0);
            stmt.setDouble(6, equipamento.getComprimento() != null ? equipamento.getComprimento() : 0);
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
        Equipamento equipamento = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM equipamentos WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                equipamento = mapearResultSet(rs);
            }

            return equipamento;
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

    @Override
    public void atualizar(Equipamento equipamento) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "UPDATE equipamentos SET descricao = ?, dataCompra = ?, " +
                    "peso = ?, largura = ?, comprimento = ?, tipo = ?, " +
                    "quantidadeAtual = ?, quantidadeEstoque = ?, quantidadeMinima = ?, " +
                    "tipo_uso = ? WHERE id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, equipamento.getDescricao());
            stmt.setDate(2, Date.valueOf(equipamento.getDataCompra()));
            stmt.setDouble(3, equipamento.getPeso() != null ? equipamento.getPeso() : 0);
            stmt.setDouble(4, equipamento.getLargura() != null ? equipamento.getLargura() : 0);
            stmt.setDouble(5, equipamento.getComprimento() != null ? equipamento.getComprimento() : 0);
            stmt.setBoolean(6, equipamento.isTipo());
            stmt.setInt(7, equipamento.getQuantidadeAtual());
            stmt.setInt(8, equipamento.getQuantidadeEstoque());
            stmt.setInt(9, equipamento.getQuantidadeMinima());
            stmt.setString(10, equipamento.getTipoUso());
            stmt.setString(11, equipamento.getId());

            stmt.executeUpdate();
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
            String sql = "UPDATE equipamentos SET quantidadeAtual = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quantidade);
            stmt.setString(2, id);
            stmt.executeUpdate();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    @Override
    public void atualizarStatus(String id, String status) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "UPDATE equipamentos SET status = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setString(2, id);
            stmt.executeUpdate();
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
            String sql = "SELECT COUNT(*) FROM emprestimos WHERE idEquipamento = ? AND ativo = true";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
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
}