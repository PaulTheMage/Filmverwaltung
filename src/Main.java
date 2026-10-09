import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner =  new Scanner(System.in);
        Datenbank datenbank = new Datenbank();
        FilmRepository repository = new FilmRepository(datenbank);
        OmdbClient omdbClient = new OmdbClient();

        System.out.println("=== Filmverwaltung ===");

        try {
            while (true) {
                System.out.println();
                System.out.println("1. Filme ansehen");
                System.out.println("2. Film hinzufügen");
                System.out.println("3. Filmbewertung eintragen");
                System.out.println("4. Film löschen");
                System.out.println("5. Beenden");
                System.out.print("Auswahl: ");

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
        }finally {
            scanner.close();
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
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Filme: " + e.getMessage());
        }
    }
