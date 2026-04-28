
import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/testdb";
        String username = "root";
        String password = "mutadak@1"; // change if different

        try {
            Connection con = DriverManager.getConnection(url, username, password);
            System.out.println("Connected successfully!");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
