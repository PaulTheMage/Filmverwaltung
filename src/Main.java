import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Erstelle einen Scanner für Benutzereingaben.
        Scanner scanner = new Scanner(System.in);

        // Erstelle die Datenbankverbindung.
        Datenbank datenbank = new Datenbank();

        // Erstelle das Repository und übergib ihm die Datenbank.
        FilmRepository repository = new FilmRepository(datenbank);

        // Erstelle einen Client für die Online-Filmsuche.
        OmdbClient omdbClient = new OmdbClient();

        // Zeige die Überschrift des Programms an.
        System.out.println("\u001B[38;2;190;120;255m\u001B[1m"
                + "=== Filmverwaltung ===" + "\u001B[0m");

        try {

            // Wiederhole das Menü, bis der Benutzer das Programm beendet.
            while (true) {

                // Zeige alle verfügbaren Menüoptionen an.
                System.out.println();
                System.out.println("\u001B[38;2;255;200;100m1. Filme ansehen\u001B[0m");
                System.out.println("\u001B[38;2;255;200;100m2. Film hinzufügen\u001B[0m");
                System.out.println("\u001B[38;2;255;200;100m3. Filmbewertung eintragen\u001B[0m");
                System.out.println("\u001B[38;2;255;200;100m4. Film löschen\u001B[0m");
                System.out.println("\u001B[38;2;255;200;100m5. Beenden\u001B[0m");

                // Fordere den Benutzer zur Eingabe einer Auswahl auf.
                System.out.print("\u001B[38;2;70;220;210m\u001B[1mAuswahl:\u001B[0m ");

                // Lies die Auswahl des Benutzers ein.
                String auswahl = scanner.nextLine();

                // Prüfe die Auswahl und führe die passende Funktion aus.
                switch (auswahl) {

                    // Zeige alle gespeicherten Filme und ihre Bewertungen an.
                    case "1" -> filmeAnsehen(repository);

                    // Suche einen Film online und speichere ihn bei Bestätigung.
                    case "2" -> filmHinzufuegen(scanner, repository, omdbClient);

                    // Ermögliche dem Benutzer, einen Film zu bewerten.
                    case "3" -> bewertungEintragen(scanner, repository);

                    // Ermögliche dem Benutzer, einen Film zu löschen.
                    case "4" -> filmLoeschen(scanner, repository);

                    // Beende das Programm und verlasse die Schleife.
                    case "5" -> {
                        System.out.println("Programm beendet.");
                        return;
                    }

                    // Informiere den Benutzer über eine ungültige Auswahl.
                    default -> System.out.println("Ungültige Auswahl.");
                }
            }

        } finally {

            // Schließe den Scanner, wenn das Programm verlassen wird.
            scanner.close();
        }
    }

    private static void filmLoeschen(
            Scanner scanner,
            FilmRepository repository) {

        try {

            // Zeige die vorhandenen Filme an, damit der Benutzer eine ID auswählen kann.
            filmeAnsehen(repository);

            // Frage nach der ID des Films, der gelöscht werden soll.
            System.out.print("\nFilm-ID zum Löschen: ");

            // Lies die Film-ID ein und wandle sie in eine ganze Zahl um.
            int filmId = Integer.parseInt(scanner.nextLine());

            // Frage nach einer Bestätigung, bevor der Film gelöscht wird.
            System.out.print("Wirklich löschen? (j/n): ");

            // Prüfe die Antwort des Benutzers.
            if (scanner.nextLine().equalsIgnoreCase("j")) {

                // Veranlasse das Repository, den Film aus der Datenbank zu löschen.
                repository.filmLoeschen(filmId);

                // Bestätige den erfolgreichen Löschvorgang.
                System.out.println("Film gelöscht.");

            } else {

                // Breche den Löschvorgang ab, wenn der Benutzer nicht bestätigt.
                System.out.println("Löschen abgebrochen.");
            }

        } catch (NumberFormatException e) {

            // Melde, wenn keine gültige Zahl als Film-ID eingegeben wurde.
            System.out.println("Bitte eine gültige Film-ID eingeben.");

        } catch (Exception e) {

            // Melde andere Fehler, die beim Löschen auftreten.
            System.out.println("Fehler beim Löschen: " + e.getMessage());
        }
    }

    private static void bewertungEintragen(
            Scanner scanner,
            FilmRepository repository) {

        try {

            // Zeige die vorhandenen Filme an.
            filmeAnsehen(repository);

            // Frage nach der ID des Films, der bewertet werden soll.
            System.out.print("\nFilm-ID: ");

            // Lies die Film-ID ein.
            int filmId = Integer.parseInt(scanner.nextLine());

            // Frage nach einer Sternebewertung von 1 bis 5.
            System.out.print("Bewertung (1-5): ");

            // Lies die Sterneanzahl ein.
            int sterne = Integer.parseInt(scanner.nextLine());

            // Prüfe, ob die Sterneanzahl im erlaubten Bereich liegt.
            if (sterne < 1 || sterne > 5) {

                // Melde eine ungültige Sternebewertung.
                System.out.println("Die Bewertung muss zwischen 1 und 5 liegen.");

                // Beende die Funktion bei einer ungültigen Bewertung.
                return;
            }

            // Frage nach einem optionalen Kommentar zur Bewertung.
            System.out.print("Kommentar (optional): ");

            // Lies den Kommentar ein.
            String kommentar = scanner.nextLine();

            // Übergib Film-ID, Sterneanzahl und Kommentar an das Repository.
            // Das Repository speichert die Bewertung in der Datenbank.
            repository.bewertungSpeichern(filmId, sterne, kommentar);

            // Bestätige das Speichern der Bewertung.
            System.out.println("Bewertung gespeichert.");

        } catch (NumberFormatException e) {

            // Melde, wenn Film-ID oder Sterneanzahl keine gültige Zahl ist.
            System.out.println("Bitte eine gültige Zahl eingeben.");

        } catch (Exception e) {

            // Melde andere Fehler beim Speichern der Bewertung.
            System.out.println("Fehler beim Speichern der Bewertung: " + e.getMessage());
        }
    }

    private static void filmeAnsehen(FilmRepository repository) {

        try {

            // Fordere das Repository auf, alle Filme aus der Datenbank abzurufen.
            List<Film> filme = repository.alleFilme();

            // Prüfe, ob Filme vorhanden sind.
            if (filme.isEmpty()) {

                // Informiere den Benutzer, wenn keine Filme gespeichert sind.
                System.out.println("Keine Filme vorhanden.");

                // Beende die Funktion, da keine Filme angezeigt werden können.
                return;
            }

            // Gehe jeden Film in der Ergebnisliste einzeln durch.
            for (Film film : filme) {

                // Gib eine Leerzeile und die Filminformationen aus.
                System.out.println();
                System.out.println(film);

                // Zeige die Überschrift für die Bewertungen an.
                System.out.println("\u001B[38;2;70;220;210m"
                        + "  Bewertungen:"
                        + "\u001B[0m");

                // Rufe alle Bewertungen für die aktuelle Film-ID ab.
                List<String> bewertungen =
                        repository.bewertungenFuerFilm(film.getId());

                // Prüfe, ob für diesen Film Bewertungen existieren.
                if (bewertungen.isEmpty()) {

                    // Informiere den Benutzer, wenn noch keine Bewertungen vorliegen.
                    System.out.println(
                            "  Noch keine Bewertungen vorhanden.");

                } else {

                    // Gehe jede Bewertung durch und gib sie aus.
                    for (String bewertung : bewertungen) {
                        System.out.println("  " + bewertung);
                    }
                }
            }

        } catch (Exception e) {

            // Melde Fehler, die beim Abrufen oder Anzeigen der Filme auftreten.
            System.out.println(
                    "Fehler beim Laden der Filme: " + e.getMessage());
        }
    }

    private static void filmHinzufuegen(
            Scanner scanner,
            FilmRepository repository,
            OmdbClient omdbClient) {

        // Frage nach dem Titel oder einem Suchbegriff für den Film.
        System.out.print("Filmname / Suchbegriff: ");

        // Lies den Suchbegriff ein und entferne Leerzeichen am Anfang und Ende.
        String titel = scanner.nextLine().trim();

        // Prüfe, ob der Benutzer überhaupt einen Suchbegriff eingegeben hat.
        if (titel.isEmpty()) {

            // Fordere zur Eingabe eines Filmtitels auf.
            System.out.println("Bitte einen Filmtitel eingeben.");

            // Beende die Funktion bei einer leeren Eingabe.
            return;
        }

        try {

            // Suche den Film über die OMDb-Schnittstelle.
            Film film = omdbClient.sucheFilm(titel);

            // Prüfe, ob ein passender Film gefunden wurde.
            if (film == null) {

                // Informiere den Benutzer, wenn die Suche erfolglos war.
                System.out.println("Film wurde bei OMDb nicht gefunden.");

                // Beende die Funktion, da kein Film gespeichert werden kann.
                return;
            }

            // Zeige die gefundenen Filminformationen an.
            System.out.println();
            System.out.println("Gefundener Film:");
            System.out.println(film);

            // Frage, ob der gefundene Film in der Datenbank gespeichert werden soll.
            System.out.print("Film in Datenbank speichern? (j/n): ");

            // Prüfe die Antwort des Benutzers.
            if (scanner.nextLine().equalsIgnoreCase("j")) {

                // Übergib den Film an das Repository und speichere ihn.
                // Empfange die von der Datenbank vergebene Film-ID.
                int filmId = repository.filmSpeichern(film);

                // Bestätige das Speichern und zeige die Film-ID an.
                System.out.println("Film gespeichert. ID: " + filmId);

            } else {

                // Speichere den Film nicht, wenn der Benutzer ablehnt.
                System.out.println("Film wurde nicht gespeichert.");
            }

        } catch (Exception e) {

            // Melde Fehler bei der Online-Suche oder beim Speichern des Films.
            System.out.println("Fehler beim Hinzufügen: " + e.getMessage());
        }
    }
}