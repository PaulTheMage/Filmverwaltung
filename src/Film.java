import java.util.List;

public class Film {

    // Speichert die eindeutige ID des Films in der Datenbank.
    private int id;

    // Speichert die eindeutige Kennung des Films bei IMDb.
    private String imdbId;

    // Speichert den Titel des Films.
    private String titel;

    // Speichert das Erscheinungsjahr des Films.
    private String jahr;

    // Speichert das Genre des Films.
    private String genre;

    // Speichert den Namen des Regisseurs.
    private String regisseur;

    // Speichert die Beschreibung der Handlung des Films.
    private String plot;

    // Speichert die Namen der Schauspieler in einer Liste.
    private List<String> schauspieler;


    // Konstruktor: Erzeugt einen Film und übernimmt seine Informationen.
    public Film(String imdbId, String titel, String jahr, String genre,
                String regisseur, String plot,
                List<String> schauspieler) {

        // Übernimm die übergebenen Werte und speichere sie im Filmobjekt.
        this.imdbId = imdbId;
        this.titel = titel;
        this.jahr = jahr;
        this.genre = genre;
        this.regisseur = regisseur;
        this.plot = plot;
        this.schauspieler = schauspieler;
    }


    // Getter: Gibt die Datenbank-ID des Films zurück.
    public int getId() {
        return id;
    }

    // Setter: Setzt die Datenbank-ID des Films.
    public void setId(int id) {
        this.id = id;
    }

    // Getter: Gibt die IMDb-ID zurück.
    public String getImdbId() {
        return imdbId;
    }

    // Getter: Gibt den Filmtitel zurück.
    public String getTitel() {
        return titel;
    }

    // Getter: Gibt das Erscheinungsjahr zurück.
    public String getJahr() {
        return jahr;
    }

    // Getter: Gibt das Genre zurück.
    public String getGenre() {
        return genre;
    }

    // Getter: Gibt den Namen des Regisseurs zurück.
    public String getRegisseur() {
        return regisseur;
    }

    // Getter: Gibt die Handlungsbeschreibung zurück.
    public String getPlot() {
        return plot;
    }

    // Getter: Gibt die Liste der Schauspieler zurück.
    public List<String> getSchauspieler() {
        return schauspieler;
    }


    // Wandelt die Filminformationen in einen lesbaren Text um.
    public String toString() {

        // Erstelle einen Text mit allen wichtigen Informationen zum Film.
        return "ID: " + id
                + "\nTitel: " + titel
                + "\nJahr: " + jahr
                + "\nGenre: " + genre
                + "\nRegisseur: " + regisseur

                // Verbinde alle Schauspielernamen durch Kommas.
                // Wenn keine Schauspielerliste vorhanden ist, verwende eine leere Liste.
                + "\nSchauspieler: " + String.join(
                ", ",
                schauspieler == null ? List.of() : schauspieler)

                + "\nBeschreibung: " + plot
                + "\nIMDb-ID: " + imdbId;
    }
}