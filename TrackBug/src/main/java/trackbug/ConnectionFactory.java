package trackbug;

import java.sql.*;

public class ConnectionFactory {

    // URL do banco de dados
    private static final String URL = "jdbc:mysql://localhost:3306/trackbug";
    // Usuário do banco de dados
    private static final String USER = "root";
    // Senha do banco de dados
    private static final String PASS = "root";
    // Driver JDBC para MySQL
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // Metodo para obter uma conexão com o banco de dados
    public static Connection getConnection() {
        try {
            // Carrega o driver JDBC
            Class.forName(DRIVER);
            System.out.println("Conexão com o banco de dados bem sucedida!");
            // Retorna a conexão
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            // Lança uma exceção em caso de erro na conexão
            e.printStackTrace();
        }
        return null;
    }

    // Metodo para fechar a conexão com o banco de dados
    public static void closeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            // Lança uma exceção em caso de erro ao fechar a conexão
            throw new RuntimeException("Erro ao fechar conexão com o Banco de Dados" + e);
        }
    }

    // Metodo para fechar a conexão e o PreparedStatement
    public static void closeConnection(Connection con, PreparedStatement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            // Lança uma exceção em caso de erro ao fechar a conexão ou o PreparedStatement
            throw new RuntimeException("Erro ao fechar conexão com o Banco de Dados" + e);
        }
    }

    // Metodo para fechar a conexão, o PreparedStatement e o ResultSet
    public static void closeConnection(Connection con, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            // Lança uma exceção em caso de erro ao fechar a conexão, o PreparedStatement ou o ResultSet
            throw new RuntimeException("Erro ao fechar o banco de dados " + e);
        }
    }
}
