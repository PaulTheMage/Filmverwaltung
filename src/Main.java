import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Datenbank datenbank = new Datenbank();
        FilmRepository repository = new FilmRepository(datenbank);
        OmdbClient omdbClient = new OmdbClient();

        System.out.println("\u001B[38;2;190;120;255m\u001B[1m"
                + "=== Filmverwaltung ===" + "\u001B[0m");

        try {
            while (true) {
                System.out.println();
                System.out.println("\u001B[38;2;255;200;100m1. Filme ansehen\u001B[0m");
                System.out.println("\u001B[38;2;255;200;100m2. Film hinzufügen\u001B[0m");
                System.out.println("\u001B[38;2;255;200;100m3. Filmbewertung eintragen\u001B[0m");
                System.out.println("\u001B[38;2;255;200;100m4. Film löschen\u001B[0m");
                System.out.println("\u001B[38;2;255;200;100m5. Beenden\u001B[0m");
                System.out.print("\u001B[38;2;70;220;210m\u001B[1mAuswahl:\u001B[0m ");

                String auswahl = scanner.nextLine();

                switch (auswahl) {
                    case "1" -> filmeAnsehen(repository);
                    case "2" -> filmHinzufuegen(scanner, repository, omdbClient);
                    case "3" -> bewertungEintragen(scanner, repository);
                    case "4" -> filmLoeschen(scanner, repository);
                    case "5" -> {
                        System.out.println("Programm beendet.");
                        return;
                    }
                    default -> System.out.println("Ungültige Auswahl.");
                }
            }
        } finally {
            scanner.close();
        }
    }
    private static void filmLoeschen(
            Scanner scanner,
            FilmRepository repository) {

        try {
            filmeAnsehen(repository);

            System.out.print("\nFilm-ID zum Löschen: ");
            int filmId = Integer.parseInt(scanner.nextLine());

            System.out.print("Wirklich löschen? (j/n): ");
            if (scanner.nextLine().equalsIgnoreCase("j")) {
                repository.filmLoeschen(filmId);
                System.out.println("Film gelöscht.");
            } else {
                System.out.println("Löschen abgebrochen.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Bitte eine gültige Film-ID eingeben.");
        } catch (Exception e) {
            System.out.println("Fehler beim Löschen: " + e.getMessage());
        }
    }
    private static void bewertungEintragen(
            Scanner scanner,
            FilmRepository repository) {

        try {
            filmeAnsehen(repository);

            System.out.print("\nFilm-ID: ");
            int filmId = Integer.parseInt(scanner.nextLine());

            System.out.print("Bewertung (1-5): ");
            int sterne = Integer.parseInt(scanner.nextLine());

            if (sterne < 1 || sterne > 5) {
                System.out.println("Die Bewertung muss zwischen 1 und 5 liegen.");
                return;
            }

            System.out.print("Kommentar (optional): ");
            String kommentar = scanner.nextLine();

            repository.bewertungSpeichern(filmId, sterne, kommentar);
            System.out.println("Bewertung gespeichert.");

        } catch (NumberFormatException e) {
            System.out.println("Bitte eine gültige Zahl eingeben.");
        } catch (Exception e) {
            System.out.println("Fehler beim Speichern der Bewertung: " + e.getMessage());
        }
    }
    private static void filmeAnsehen(FilmRepository repository) {

        try {
            List<Film> filme = repository.alleFilme();

            if (filme.isEmpty()) {
                System.out.println("Keine Filme vorhanden.");
                return;
            }

            for (Film film : filme) {
                System.out.println();
                System.out.println(film);

                System.out.println("\u001B[38;2;70;220;210m"
                        + "  Bewertungen:"
                        + "\u001B[0m");

                List<String> bewertungen =
                        repository.bewertungenFuerFilm(film.getId());

                if (bewertungen.isEmpty()) {
                    System.out.println(
                            "  Noch keine Bewertungen vorhanden.");
                } else {
                    for (String bewertung : bewertungen) {
                        System.out.println("  " + bewertung);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(
                    "Fehler beim Laden der Filme: " + e.getMessage());
        }
    }

    private static void filmHinzufuegen(
            Scanner scanner,
            FilmRepository repository,
            OmdbClient omdbClient) {

        System.out.print("Filmname / Suchbegriff: ");
        String titel = scanner.nextLine().trim();

        if (titel.isEmpty()) {
            System.out.println("Bitte einen Filmtitel eingeben.");
            return;
        }

        try {
            Film film = omdbClient.sucheFilm(titel);

            if (film == null) {
                System.out.println("Film wurde bei OMDb nicht gefunden.");
                return;
            }

            System.out.println();
            System.out.println("Gefundener Film:");
            System.out.println(film);

            System.out.print("Film in Datenbank speichern? (j/n): ");
            if (scanner.nextLine().equalsIgnoreCase("j")) {
                int filmId = repository.filmSpeichern(film);
                System.out.println("Film gespeichert. ID: " + filmId);
            } else {
                System.out.println("Film wurde nicht gespeichert.");
            }

        } catch (Exception e) {
            System.out.println("Fehler beim Hinzufügen: " + e.getMessage());
        }
    }

}

