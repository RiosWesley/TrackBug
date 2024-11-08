// File: src/main/java/trackbug/model/dao/impl/LogEquipamentoDAOImpl.java
package trackbug.model.dao.impl;

import trackbug.model.dao.interfaces.LogEquipamentoDAO;
import trackbug.model.entity.LogEquipamento;
import trackbug.Forms.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogEquipamentoDAOImpl implements LogEquipamentoDAO {

    @Override
    public void registrar(LogEquipamento log) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "INSERT INTO log_equipamentos (id_equipamento, descricao, acao, " +
                    "data_acao, detalhes) VALUES (?, ?, ?, NOW(), ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, log.getIdEquipamento());
            stmt.setString(2, log.getDescricao());
            stmt.setString(3, log.getAcao());
            stmt.setString(4, log.getDetalhes());

            stmt.executeUpdate();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    @Override
    public List<LogEquipamento> listarPorEquipamento(String idEquipamento) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<LogEquipamento> logs = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM log_equipamentos WHERE id_equipamento = ? " +
                    "ORDER BY data_acao DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, idEquipamento);
            rs = stmt.executeQuery();

            while (rs.next()) {
                logs.add(mapearResultSet(rs));
            }

            return logs;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public List<LogEquipamento> listarTodos() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<LogEquipamento> logs = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM log_equipamentos ORDER BY data_acao DESC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                logs.add(mapearResultSet(rs));
            }

            return logs;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private LogEquipamento mapearResultSet(ResultSet rs) throws SQLException {
        LogEquipamento log = new LogEquipamento();
        log.setId(rs.getInt("id"));
        log.setIdEquipamento(rs.getString("id_equipamento"));
        log.setDescricao(rs.getString("descricao"));
        log.setAcao(rs.getString("acao"));
        log.setDataAcao(rs.getTimestamp("data_acao").toLocalDateTime());
        log.setDetalhes(rs.getString("detalhes"));
        return log;
    }
}