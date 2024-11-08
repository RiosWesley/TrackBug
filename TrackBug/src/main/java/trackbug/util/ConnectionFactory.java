package trackbug.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class ConnectionFactory {
    private static final String CONFIG_FILE = "config.properties";
    private static Properties props = new Properties();

    static {
        try {
            // Tenta carregar as configurações do arquivo
            props.load(new FileInputStream(CONFIG_FILE));
        } catch (IOException e) {
            // Se não encontrar o arquivo, usa valores default
            props.setProperty("db.url", "jdbc:mysql://localhost:3306/trackbug");
            props.setProperty("db.user", "root");
            props.setProperty("db.password", "");
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password")
            );
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL não encontrado", e);
        }
    }

    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }

    public static void closeConnection(Connection conn, PreparedStatement stmt) {
        closeConnection(conn);
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar statement: " + e.getMessage());
        }
    }

    public static void closeConnection(Connection conn, PreparedStatement stmt, ResultSet rs) {
        closeConnection(conn, stmt);
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar resultset: " + e.getMessage());
        }
    }
}