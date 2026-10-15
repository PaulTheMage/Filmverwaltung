import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Datenbank {

    // Speichere die Adresse und den Namen der PostgreSQL-Datenbank.
    private static final String URL =
            "jdbc:postgresql://localhost:5432/Filmverwaltung";

    // Speichere den Benutzernamen für die Anmeldung an der Datenbank.
    private static final String BENUTZER = "postgres";

    // Speichere das Passwort für die Anmeldung an der Datenbank.
    private static final String PASSWORT = "bbq";


    // Stelle eine Verbindung zur PostgreSQL-Datenbank her.
    public Connection getConnection() throws SQLException {

        // Verwende die URL, den Benutzernamen und das Passwort,
        // um eine Datenbankverbindung aufzubauen.
        return DriverManager.getConnection(URL, BENUTZER, PASSWORT);
    }
}