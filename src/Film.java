import java.util.List;

public class Film {

    private int id;
    private String imdbId;
    private String titel;
    private String jahr;
    private String genre;
    private String regisseur;
    private String plot;
    private String poster;
    private List<String> schauspieler;

    public Film(String imdbId, String titel, String jahr, String genre,
                String regisseur, String plot, String poster,
                List<String> schauspieler) {
        this.imdbId = imdbId;
        this.titel = titel;
        this.jahr = jahr;
        this.genre = genre;
        this.regisseur = regisseur;
        this.plot = plot;
        this.poster = poster;
        this.schauspieler = schauspieler;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getTitel() {
        return titel;
    }

    public String getJahr() {
        return jahr;
    }

    public String getGenre() {
        return genre;
    }

    public String getRegisseur() {
        return regisseur;
    }

    public String getPlot() {
        return plot;
    }

    public String getPoster() {
        return poster;
    }

    public List<String> getSchauspieler() {
        return schauspieler;
    }

    public String toString() {
        return "ID: " + id
                + "\nTitel: " + titel
                + "\nJahr: " + jahr
                + "\nGenre: " + genre
                + "\nRegisseur: " + regisseur
                + "\nSchauspieler: " + String.join(", ", schauspieler == null ? List.of() : schauspieler)
                + "\nBeschreibung: " + plot
                + "\nIMDb-ID: " + imdbId
                + "\nPoster: " + poster;
    }
}
