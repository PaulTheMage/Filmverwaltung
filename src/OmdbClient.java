import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OmdbClient {

    // Speichere den API-Schlüssel für den Zugriff auf die OMDb-Schnittstelle.
    private static final String API_KEY = "f9a21e05";

    // Erstelle einen HTTP-Client, um Anfragen an die OMDb-Schnittstelle zu senden.
    private final HttpClient httpClient = HttpClient.newHttpClient();


    // Suche anhand eines Suchbegriffs nach einem Film und gib die Filminformationen zurück.
    public Film sucheFilm(String suchbegriff) throws Exception {

        // Wandle den Suchbegriff in ein URL-kompatibles Format um.
        String encoded = URLEncoder.encode(
                suchbegriff,
                StandardCharsets.UTF_8
        );

        // Erstelle die URL für die OMDb-Anfrage.
        // Übermittle den API-Schlüssel, den Suchbegriff und die vollständige Handlung.
        String url = "https://www.omdbapi.com/?apikey="
                + API_KEY
                + "&t="
                + encoded
                + "&plot=full";

        // Erstelle eine HTTP-GET-Anfrage an die OMDb-Schnittstelle.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // Sende die Anfrage und empfange die Antwort als Text.
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        // Prüfe, ob die Anfrage erfolgreich beantwortet wurde.
        if (response.statusCode() != 200) {

            // Melde einen Fehler, wenn der HTTP-Status nicht 200 ist.
            throw new RuntimeException(
                    "OMDb API antwortet mit HTTP " + response.statusCode()
            );
        }

        // Speichere den Antworttext der Schnittstelle.
        String json = response.body();

        // Prüfe, ob OMDb meldet, dass kein Film gefunden wurde.
        if ("False".equalsIgnoreCase(
                getJsonValue(json, "Response"))) {

            // Gib keinen Film zurück, wenn die Suche erfolglos war.
            return null;
        }

        // Lies den Filmtitel aus der JSON-Antwort aus.
        String titel = getJsonValue(json, "Title");

        // Prüfe, ob ein gültiger Filmtitel vorhanden ist.
        if (titel == null || titel.isBlank()) {

            // Gib keinen Film zurück, wenn kein Titel vorhanden ist.
            return null;
        }

        // Erstelle ein Filmobjekt mit den erhaltenen Informationen.
        // Lies die einzelnen Werte aus der JSON-Antwort aus.
        // Wandle die Schauspielernamen in eine Liste um.
        return new Film(
                getJsonValue(json, "imdbID"),
                titel,
                getJsonValue(json, "Year"),
                getJsonValue(json, "Genre"),
                getJsonValue(json, "Director"),
                getJsonValue(json, "Plot"),
                parseSchauspieler(getJsonValue(json, "Actors"))
        );
    }


    // Suche in der JSON-Antwort nach dem Wert eines bestimmten Schlüssels.
    private String getJsonValue(String json, String key) {

        // Erstelle ein Suchmuster, das zum angegebenen JSON-Schlüssel passt.
        // Suche den zugehörigen Textwert hinter dem Schlüssel.
        String patternText =
                "\"" + Pattern.quote(key) + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"";

        // Erstelle das Suchmuster.
        Pattern pattern = Pattern.compile(patternText);

        // Wende das Suchmuster auf die JSON-Antwort an.
        Matcher matcher = pattern.matcher(json);

        // Prüfe, ob ein passender Schlüssel mit einem Textwert gefunden wurde.
        if (!matcher.find()) {

            // Gib einen leeren Text zurück, wenn kein passender Wert gefunden wurde.
            return "";
        }

        // Lies den gefundenen Wert aus und ersetze bestimmte Escape-Zeichen.
        // Wandle maskierte Anführungszeichen, Backslashes und Steuerzeichen um.
        return matcher.group(1)
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\/", "/")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }


    // Wandle eine kommaseparierte Liste von Schauspielernamen in eine Liste um.
    private List<String> parseSchauspieler(String actors) {

        // Erstelle eine leere Liste für die Schauspielernamen.
        List<String> result = new ArrayList<>();

        // Prüfe, ob keine Schauspieler angegeben wurden.
        if (actors == null
                || actors.isBlank()
                || "N/A".equalsIgnoreCase(actors)) {

            // Gib eine leere Liste zurück, wenn keine Namen vorhanden sind.
            return result;
        }

        // Teile den Text an den Kommas auf und gehe jeden Namen einzeln durch.
        for (String actor : actors.split(",")) {

            // Entferne überflüssige Leerzeichen am Anfang und Ende des Namens.
            String name = actor.trim();

            // Prüfe, ob der Name nach dem Entfernen der Leerzeichen noch Inhalt hat.
            if (!name.isEmpty()) {

                // Füge den gültigen Namen zur Ergebnisliste hinzu.
                result.add(name);
            }
        }

        // Gib die fertige Liste mit den Schauspielernamen zurück.
        return result;
    }
}