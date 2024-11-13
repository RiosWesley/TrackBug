package trackbug.model.dao.impl;

import trackbug.model.dao.interfaces.AvariaDAO;
import trackbug.model.entity.Avaria;
import trackbug.model.entity.RegistroAvaria;
import trackbug.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvariaDAOImpl implements AvariaDAO {
    @Override
    public void registrar(Avaria avaria) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO avarias (id_equipamento, quantidade, descricao, data) VALUES (?, ?, ?, NOW())";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, avaria.getIdEquipamento());
            stmt.setInt(2, avaria.getQuantidade());
            stmt.setString(3, avaria.getDescricao());
            stmt.executeUpdate();

            sql = "UPDATE equipamentos SET quantidadeAtual = quantidadeAtual - ?, quantidadeEstoque = quantidadeEstoque - ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, avaria.getQuantidade());
            stmt.setInt(2, avaria.getQuantidade());
            stmt.setString(3, avaria.getIdEquipamento());
            stmt.executeUpdate();

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
    public List<RegistroAvaria> listarTodas() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<RegistroAvaria> avarias = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM avarias ORDER BY data DESC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                avarias.add(mapearRegistroAvaria(rs));
            }

            return avarias;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public List<RegistroAvaria> listarPorEquipamento(String idEquipamento) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<RegistroAvaria> avarias = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM avarias WHERE id_equipamento = ? ORDER BY data DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, idEquipamento);
            rs = stmt.executeQuery();

            while (rs.next()) {
                avarias.add(mapearRegistroAvaria(rs));
            }

            return avarias;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private RegistroAvaria mapearRegistroAvaria(ResultSet rs) throws SQLException {
        RegistroAvaria avaria = new RegistroAvaria();
        avaria.setId(rs.getInt("id"));
        avaria.setIdEquipamento(rs.getString("id_equipamento"));
        avaria.setQuantidade(rs.getInt("quantidade"));
        avaria.setDescricao(rs.getString("descricao"));
        avaria.setData(rs.getTimestamp("data").toLocalDateTime());
        return avaria;
    }
}