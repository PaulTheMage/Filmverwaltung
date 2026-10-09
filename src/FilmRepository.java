import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FilmRepository {
    private final Datenbank datenbank;

    public FilmRepository(Datenbank datenbank) {

        this.datenbank = datenbank;
    }

    public List<Film> alleFilme() throws SQLException {
        List<Film> filme = new ArrayList<>();

        String sql = """
                SELECT f.id,
                       f.imdb_id,
                       f.titel,
                       f.jahr,
                       f.genre,
                       f.regisseur,
                       f.plot,
                       f.poster,
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

        try (Connection connection = datenbank.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                Film film = new Film(
                        result.getString("imdb_id"),
                        result.getString("titel"),
                        result.getString("jahr"),
                        result.getString("genre"),
                        result.getString("regisseur"),
                        result.getString("plot"),
                        result.getString("poster"),
                        parseSchauspieler(result.getString("schauspieler"))
                );

                film.setId(result.getInt("id"));
                filme.add(film);
            }
        }

        return filme;
    }
    private List<String> parseSchauspieler(String text) {
        List<String> result = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return result;
        }

        for (String name : text.split(", ")) {
            result.add(name);
        }

        return result;
    }
}