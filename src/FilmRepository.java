import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FilmRepository {

    // Speichere die Datenbankkomponente, über die auf PostgreSQL zugegriffen wird.
    private final Datenbank datenbank;

    // Konstruktor: Übernimm die Datenbankkomponente und speichere sie im Repository.
    public FilmRepository(Datenbank datenbank) {
        this.datenbank = datenbank;
    }


    // Lösche einen Film anhand seiner Datenbank-ID.
    public void filmLoeschen(int filmId) throws SQLException {

        // Erstelle eine SQL-Abfrage, die den Film mit der angegebenen ID löscht.
        String sql = "DELETE FROM filme WHERE id = ?";

        // Stelle eine Datenbankverbindung her und bereite die SQL-Abfrage vor.
        try (Connection connection = datenbank.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Setze die Film-ID in den Platzhalter der SQL-Abfrage ein.
            statement.setInt(1, filmId);

            // Führe die Löschabfrage aus und ermittle die Anzahl der gelöschten Zeilen.
            int rows = statement.executeUpdate();

            // Prüfe, ob kein Film gelöscht wurde.
            if (rows == 0) {

                // Informiere den Benutzer, wenn kein Film mit dieser ID existiert.
                System.out.println("Kein Film mit dieser ID gefunden.");
            }
        }
    }


    // Speichere eine Bewertung mit Film-ID, Sterneanzahl und Kommentar.
    public void bewertungSpeichern(
            int filmId,
            int sterne,
            String kommentar) throws SQLException {

        // Erstelle eine SQL-Abfrage zum Einfügen einer neuen Bewertung.
        String sql = """
                INSERT INTO bewertungen (film_id, sterne, kommentar)
                VALUES (?, ?, ?)
                """;

        // Stelle eine Datenbankverbindung her und bereite die Abfrage vor.
        try (Connection connection = datenbank.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Setze die Film-ID, die Sterneanzahl und den Kommentar ein.
            statement.setInt(1, filmId);
            statement.setInt(2, sterne);
            statement.setString(3, kommentar);

            // Führe die SQL-Abfrage aus und speichere die Bewertung.
            statement.executeUpdate();
        }
    }


    // Rufe alle Bewertungen für einen bestimmten Film ab und gib sie als Liste zurück.
    public List<String> bewertungenFuerFilm(int filmId)
            throws SQLException {

        // Erstelle eine leere Liste für die formatierten Bewertungen.
        List<String> bewertungen = new ArrayList<>();

        // Wähle Sterne und Kommentare für den angegebenen Film aus.
        // Sortiere die Bewertungen nach Sterneanzahl, beginnend mit der höchsten.
        String sql = """
            SELECT sterne, kommentar
            FROM bewertungen
            WHERE film_id = ?
            ORDER BY sterne DESC
            """;

        // Stelle eine Datenbankverbindung her und bereite die Abfrage vor.
        try (Connection connection = datenbank.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Setze die Film-ID als Suchkriterium ein.
            statement.setInt(1, filmId);

            // Führe die Abfrage aus und gehe die gefundenen Bewertungen durch.
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {

                    // Lies die Sterneanzahl und den Kommentar der aktuellen Bewertung aus.
                    int sterne = result.getInt("sterne");
                    String kommentar = result.getString("kommentar");

                    // Erstelle eine Sternanzeige mit ausgefüllten und leeren Sternen.
                    String sterneAnzeige =
                            "★".repeat(sterne)
                                    + "☆".repeat(Math.max(0, 5 - sterne));

                    // Ersetze einen fehlenden oder leeren Kommentar durch einen Standardtext.
                    if (kommentar == null || kommentar.isBlank()) {
                        kommentar = "Kein Kommentar";
                    }

                    // Verbinde Sterneanzeige, Punktzahl und Kommentar zu einem Text.
                    String bewertung = sterneAnzeige
                            + " (" + sterne + "/5) – "
                            + kommentar;

                    // Füge die formatierte Bewertung zur Ergebnisliste hinzu.
                    bewertungen.add(bewertung);
                }
            }
        }

        // Gib die Liste aller gefundenen Bewertungen zurück.
        return bewertungen;
    }


    // Rufe alle Filme aus der Datenbank ab und gib sie als Liste zurück.
    public List<Film> alleFilme() throws SQLException {

        // Erstelle eine leere Liste für die gefundenen Filmobjekte.
        List<Film> filme = new ArrayList<>();

        // Wähle die Filminformationen und die zugehörigen Schauspielernamen aus.
        // Verknüpfe Filme mit der Zuordnungstabelle und der Schauspielertabelle.
        // Fasse die Schauspielernamen je Film zusammen.
        // Zeige auch Filme ohne Schauspieler an und sortiere die Ergebnisse nach Titel.
        String sql = """
                SELECT f.id,
                       f.imdb_id,
                       f.titel,
                       f.jahr,
                       f.genre,
                       f.regisseur,
                       f.plot,
                       COALESCE(
                           STRING_AGG(s.name, ', ' ORDER BY s.name),
                           ''
                       ) AS schauspieler
                FROM filme f
                LEFT JOIN film_schauspieler fs
                    ON fs.film_id = f.id
                LEFT JOIN schauspieler s
                    ON s.id = fs.schauspieler_id
                GROUP BY f.id
                ORDER BY f.titel
                """;

        // Stelle eine Datenbankverbindung her, bereite die Abfrage vor und führe sie aus.
        try (Connection connection = datenbank.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            // Gehe alle gefundenen Filme einzeln durch.
            while (result.next()) {

                // Erstelle für die aktuelle Ergebniszeile ein neues Filmobjekt.
                Film film = new Film(
                        result.getString("imdb_id"),
                        result.getString("titel"),
                        result.getString("jahr"),
                        result.getString("genre"),
                        result.getString("regisseur"),
                        result.getString("plot"),

                        // Wandle die zusammengefassten Schauspielernamen in eine Liste um.
                        parseSchauspieler(result.getString("schauspieler"))
                );

                // Übernimm die Datenbank-ID in das Filmobjekt.
                film.setId(result.getInt("id"));

                // Füge das fertige Filmobjekt zur Ergebnisliste hinzu.
                filme.add(film);
            }
        }

        // Gib die Liste aller Filme zurück.
        return filme;
    }


    // Wandle eine durch Kommas und Leerzeichen getrennte Zeichenkette in eine Liste um.
    private List<String> parseSchauspieler(String text) {

        // Erstelle eine leere Liste für die Schauspielernamen.
        List<String> result = new ArrayList<>();

        // Prüfe, ob kein Text mit Schauspielernamen vorhanden ist.
        if (text == null || text.isBlank()) {

            // Gib eine leere Liste zurück, wenn keine Namen vorhanden sind.
            return result;
        }

        // Teile den Text an den Kommas mit nachfolgendem Leerzeichen auf.
        for (String name : text.split(", ")) {

            // Füge jeden gefundenen Namen zur Liste hinzu.
            result.add(name);
        }

        // Gib die fertige Liste der Schauspielernamen zurück.
        return result;
    }


    // Speichere einen Film samt Schauspielern und gib seine neue Datenbank-ID zurück.
    public int filmSpeichern(Film film) throws SQLException {

        // Erstelle eine SQL-Abfrage, die einen Film einfügt und seine neue ID zurückgibt.
        String filmSql = """
                INSERT INTO filme (imdb_id, titel, jahr, genre, regisseur, plot)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        // Öffne eine Datenbankverbindung für den gesamten Speichervorgang.
        try (Connection connection = datenbank.getConnection()) {

            // Deaktiviere automatische Einzelbestätigungen,
            // damit alle Änderungen gemeinsam bestätigt oder zurückgerollt werden.
            connection.setAutoCommit(false);

            try {

                // Bereite eine Variable für die neue Film-ID vor.
                int filmId;

                // Bereite die SQL-Abfrage zum Speichern des Films vor.
                try (PreparedStatement statement =
                             connection.prepareStatement(filmSql)) {

                    // Übertrage die Filminformationen in die SQL-Platzhalter.
                    statement.setString(1, film.getImdbId());
                    statement.setString(2, film.getTitel());

                    // Wandle das Erscheinungsjahr in eine ganze Zahl um.
                    statement.setInt(3, Integer.parseInt(film.getJahr()));

                    statement.setString(4, film.getGenre());
                    statement.setString(5, film.getRegisseur());
                    statement.setString(6, film.getPlot());

                    // Führe die Abfrage aus und lies die zurückgegebene Film-ID aus.
                    try (ResultSet result = statement.executeQuery()) {

                        // Prüfe, ob PostgreSQL eine Ergebniszeile zurückgegeben hat.
                        if (!result.next()) {

                            // Melde einen Fehler, wenn keine Film-ID zurückgegeben wurde.
                            throw new SQLException("Film konnte nicht gespeichert werden.");
                        }

                        // Übernimm die neu vergebene Film-ID.
                        filmId = result.getInt("id");
                    }
                }

                // Erstelle eine SQL-Abfrage zum Speichern eines Schauspielers.
                // Existiert der Name bereits, verwende den vorhandenen Datensatz
                // und gib dessen ID zurück.
                String actorSql = """
                        INSERT INTO schauspieler (name)
                        VALUES (?)
                        ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name
                        RETURNING id
                        """;

                // Erstelle eine SQL-Abfrage, um Film und Schauspieler miteinander zu verknüpfen.
                // Überspringe eine Zuordnung, wenn sie bereits existiert.
                String linkSql = """
                        INSERT INTO film_schauspieler (film_id, schauspieler_id)
                        VALUES (?, ?)
                        ON CONFLICT DO NOTHING
                        """;

                // Prüfe, ob das Filmobjekt eine Schauspielerliste enthält.
                if (film.getSchauspieler() != null) {

                    // Gehe alle Schauspielernamen des Films einzeln durch.
                    for (String name : film.getSchauspieler()) {

                        // Überspringe fehlende oder leere Schauspielernamen.
                        if (name == null || name.isBlank()) {
                            continue;
                        }

                        // Bereite eine Variable für die Schauspieler-ID vor.
                        int actorId;

                        // Bereite die SQL-Abfrage zum Speichern oder Wiederfinden des Schauspielers vor.
                        try (PreparedStatement actorStatement =
                                     connection.prepareStatement(actorSql)) {

                            // Übertrage den bereinigten Namen in die SQL-Abfrage.
                            actorStatement.setString(1, name.trim());

                            // Führe die Abfrage aus und lies die Schauspieler-ID aus.
                            try (ResultSet result = actorStatement.executeQuery()) {

                                // Prüfe, ob PostgreSQL eine Schauspieler-ID zurückgegeben hat.
                                if (!result.next()) {

                                    // Melde einen Fehler, wenn keine Schauspieler-ID zurückkommt.
                                    throw new SQLException(
                                            "Schauspieler konnte nicht gespeichert werden.");
                                }

                                // Übernimm die ID des Schauspielers.
                                actorId = result.getInt("id");
                            }
                        }

                        // Bereite die Zuordnung zwischen Film und Schauspieler vor.
                        try (PreparedStatement linkStatement =
                                     connection.prepareStatement(linkSql)) {

                            // Setze die Film-ID und die Schauspieler-ID ein.
                            linkStatement.setInt(1, filmId);
                            linkStatement.setInt(2, actorId);

                            // Speichere die Zuordnung in der Verbindungstabelle.
                            linkStatement.executeUpdate();
                        }
                    }
                }

                // Bestätige alle Änderungen gemeinsam in der Datenbank.
                connection.commit();

                // Übertrage die neue Datenbank-ID in das Filmobjekt.
                film.setId(filmId);

                // Gib die neue Film-ID an den Aufrufer zurück.
                return filmId;

            } catch (Exception e) {

                // Mache alle Änderungen rückgängig, wenn beim Speichern ein Fehler auftritt.
                connection.rollback();

                // Gib SQL-Fehler unverändert an den Aufrufer weiter.
                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }

                // Wandle andere Fehler in einen SQL-Fehler mit Zusatzinformation um.
                throw new SQLException("Film konnte nicht gespeichert werden.", e);
            }
        }
    }
}