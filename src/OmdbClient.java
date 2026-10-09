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
    private static final String API_KEY = "f9a21e05";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public Film sucheFilm(String suchbegriff) throws Exception {
        String encoded = URLEncoder.encode(
                suchbegriff,
                StandardCharsets.UTF_8
        );

        String url = "https://www.omdbapi.com/?apikey="
                + API_KEY
                + "&t="
                + encoded
                + "&plot=full";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "OMDb API antwortet mit HTTP " + response.statusCode()
            );
        }

        String json = response.body();

        if ("False".equalsIgnoreCase(
                getJsonValue(json, "Response"))) {
            return null;
        }

        String titel = getJsonValue(json, "Title");

        if (titel == null || titel.isBlank()) {
            return null;
        }

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


private String getJsonValue(String json, String key) {
    String patternText =
            "\"" + Pattern.quote(key) + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"";

    Pattern pattern = Pattern.compile(patternText);
    Matcher matcher = pattern.matcher(json);

    if (!matcher.find()) {
        return "";
    }

    return matcher.group(1)
            .replace("\\\"", "\"")
            .replace("\\\\", "\\")
            .replace("\\/", "/")
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t");
}

private List<String> parseSchauspieler(String actors) {
    List<String> result = new ArrayList<>();

    if (actors == null || actors.isBlank() || "N/A".equalsIgnoreCase(actors)) {
        return result;
    }

    for (String actor : actors.split(",")) {
        String name = actor.trim();

        if (!name.isEmpty()) {
            result.add(name);
        }
    }

    return result;
}
}