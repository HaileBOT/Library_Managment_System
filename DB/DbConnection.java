package DB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    public static Connection connect() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/mylibrary";
        String user = "postgres";
        String password = "1996";
        return DriverManager.getConnection(url, user, password);
    }
    public static void main(String[] args) {
    try {
        Connection conn = connect();
        if (conn != null) {
            System.out.println("Connected to the database!");
            conn.close();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
};