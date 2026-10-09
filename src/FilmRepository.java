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

public int filmSpeichern(Film film) throws SQLException {
    String filmSql = """
                INSERT INTO filme (imdb_id, titel, jahr, genre, regisseur, plot, poster)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

    try (Connection connection = datenbank.getConnection()) {
        connection.setAutoCommit(false);

        try {
            int filmId;

            try (PreparedStatement statement =
                         connection.prepareStatement(filmSql)) {

                statement.setString(1, film.getImdbId());
                statement.setString(2, film.getTitel());
                statement.setString(3, film.getJahr());
                statement.setString(4, film.getGenre());
                statement.setString(5, film.getRegisseur());
                statement.setString(6, film.getPlot());
                statement.setString(7, film.getPoster());

                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {
                        throw new SQLException("Film konnte nicht gespeichert werden.");
                    }
                    filmId = result.getInt("id");
                }
            }

            String actorSql = """
                        INSERT INTO schauspieler (name)
                        VALUES (?)
                        ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name
                        RETURNING id
                        """;

            String linkSql = """
                        INSERT INTO film_schauspieler (film_id, schauspieler_id)
                        VALUES (?, ?)
                        ON CONFLICT DO NOTHING
                        """;

            if (film.getSchauspieler() != null) {
                for (String name : film.getSchauspieler()) {
                    if (name == null || name.isBlank()) {
                        continue;
                    }

                    int actorId;

                    try (PreparedStatement actorStatement =
                                 connection.prepareStatement(actorSql)) {
                        actorStatement.setString(1, name.trim());

                        try (ResultSet result = actorStatement.executeQuery()) {
                            if (!result.next()) {
                                throw new SQLException(
                                        "Schauspieler konnte nicht gespeichert werden.");
                            }
                            actorId = result.getInt("id");
                        }
                    }

                    try (PreparedStatement linkStatement =
                                 connection.prepareStatement(linkSql)) {
                        linkStatement.setInt(1, filmId);
                        linkStatement.setInt(2, actorId);
                        linkStatement.executeUpdate();
                    }
                }
            }

            connection.commit();
            film.setId(filmId);
            return filmId;

        } catch (Exception e) {
            connection.rollback();

            if (e instanceof SQLException) {
                throw (SQLException) e;
            }

            throw new SQLException("Film konnte nicht gespeichert werden.", e);
        }
    }

