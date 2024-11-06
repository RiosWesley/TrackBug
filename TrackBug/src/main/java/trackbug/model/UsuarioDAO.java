package trackbug.model;

import trackbug.Forms.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public static boolean autenticar(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ? AND ativo = true";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password); // Em produção, usar hash da senha

            rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Erro ao autenticar usuário: " + e.getMessage());
            return false;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    public static Usuario buscarPorUsername(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Usuario usuario = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM usuarios WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setUsername(rs.getString("username"));
                usuario.setPassword(rs.getString("password"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setNivelAcesso(rs.getInt("nivel_acesso"));
                usuario.setAtivo(rs.getBoolean("ativo"));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuário: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }

        return usuario;
    }

    public static void criarUsuario(Usuario usuario) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "INSERT INTO usuarios (username, password, nome, email, nivel_acesso, ativo) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getPassword());
            stmt.setString(3, usuario.getNome());
            stmt.setString(4, usuario.getEmail());
            stmt.setInt(5, usuario.getNivelAcesso());
            stmt.setBoolean(6, usuario.isAtivo());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao criar usuário: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }
    public static List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM usuarios ORDER BY nome";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setUsername(rs.getString("username"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setNivelAcesso(rs.getInt("nivel_acesso"));
                usuario.setAtivo(rs.getBoolean("ativo"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }

        return usuarios;
    }

    public static void atualizarStatus(Usuario usuario) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "UPDATE usuarios SET ativo = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setBoolean(1, usuario.isAtivo());
            stmt.setInt(2, usuario.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status do usuário: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    public static void atualizarUsuario(Usuario usuario) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "UPDATE usuarios SET nome = ?, email = ?, nivel_acesso = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setInt(3, usuario.getNivelAcesso());
            stmt.setInt(4, usuario.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }
}
