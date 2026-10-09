import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Datenbank {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/Filmverwaltung";

    private static final String BENUTZER = "postgres";

    private static final String PASSWORT = "bbq";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, BENUTZER, PASSWORT);
    }
}