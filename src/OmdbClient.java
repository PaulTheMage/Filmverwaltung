import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;

public class OmdbClient {
    private static final String API_KEY = "f9a21e05";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public Film sucheFilm(String suchbegriff) throws Exception {
        String encoded = URLEncoder.encode(
                suchbegriff,
                StandardCharsets.UTF_8
        );
    }
}
