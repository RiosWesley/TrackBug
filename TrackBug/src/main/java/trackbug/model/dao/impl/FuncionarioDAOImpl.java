// File: src/main/java/trackbug/model/dao/impl/FuncionarioDAOImpl.java
package trackbug.model.dao.impl;

import trackbug.model.dao.interfaces.FuncionarioDAO;
import trackbug.model.entity.Funcionario;
import trackbug.Forms.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAOImpl implements FuncionarioDAO {

    @Override
    public void criar(Funcionario funcionario) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "INSERT INTO funcionarios (id, nome, funcao, dt) VALUES (?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, funcionario.getId());
            stmt.setString(2, funcionario.getNome());
            stmt.setString(3, funcionario.getFuncao());
            stmt.setString(4, funcionario.getDataAdmissao());

            stmt.executeUpdate();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    @Override
    public Funcionario buscarPorId(String id) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Funcionario funcionario = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM funcionarios WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                funcionario = mapearResultSet(rs);
            }

            return funcionario;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public List<Funcionario> listarTodos() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Funcionario> funcionarios = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM funcionarios ORDER BY nome";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                funcionarios.add(mapearResultSet(rs));
            }

            return funcionarios;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public void atualizar(Funcionario funcionario) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "UPDATE funcionarios SET nome = ?, funcao = ?, dt = ? WHERE id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getFuncao());
            stmt.setString(3, funcionario.getDataAdmissao());
            stmt.setString(4, funcionario.getId());

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

            // Primeiro, atualiza as observações dos empréstimos
            String sqlHistorico = "UPDATE emprestimos SET observacoes = " +
                    "CONCAT(IFNULL(observacoes, ''), ' [Funcionário excluído em: " +
                    java.time.LocalDateTime.now() + "]') " +
                    "WHERE idFuncionario = ?";
            stmt = conn.prepareStatement(sqlHistorico);
            stmt.setString(1, id);
            stmt.executeUpdate();

            // Depois, deleta o funcionário
            String sqlDelete = "DELETE FROM funcionarios WHERE id = ?";
            stmt = conn.prepareStatement(sqlDelete);
            stmt.setString(1, id);
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
            String sql = "SELECT COUNT(*) FROM emprestimos WHERE idFuncionario = ? AND ativo = true";
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

    private Funcionario mapearResultSet(ResultSet rs) throws SQLException {
        Funcionario funcionario = new Funcionario();
        funcionario.setID(rs.getString("id"));
        funcionario.setNome(rs.getString("nome"));
        funcionario.setFuncao(rs.getString("funcao"));
        funcionario.setDataAdmissao(rs.getString("dt"));
        return funcionario;
    }
}