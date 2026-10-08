In SQl haben wir die Tabellen:
##### Filme:
    CREATE TABLE filme (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    imdb_id VARCHAR(20) UNIQUE,
    titel VARCHAR(255) NOT NULL,
    erscheinungsjahr INTEGER,
    genre VARCHAR(100),
    regisseur VARCHAR(255),
    laufzeit_minuten INTEGER
    );
##### Bewertungen:
    CREATE TABLE bewertungen (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    film_id BIGINT NOT NULL,
    bewertung NUMERIC(2,1) NOT NULL,
    kommentar TEXT,
    erstellt_am TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

       CONSTRAINT fk_bewertung_film
         FOREIGN KEY (film_id)
         REFERENCES filme(id)
          ON DELETE CASCADE,

      CONSTRAINT chk_bewertung
          CHECK (bewertung >= 0 AND bewertung <= 10)
    );
##### Schauspieler:
    CREATE TABLE schauspieler (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    vorname VARCHAR(100) NOT NULL,
    nachname VARCHAR(100) NOT NULL,
    geburtsdatum DATE
    );

##### In Java haben wir die Main.java mit dem Menü.
Wir haben eine Klasse mit dem API Call.
Wir haben eine Klasse für das Objekt Film.
Wir haben eine Klasse für die DatabaseConnection.
Dann haben wir eine Klasse für die SQL Operationen:
Methoden:  
    Film hinzufügen (Mit dem API Call): filmHinzufuegen
    Filme ansehen:                      filmeAnsehen
    Filmbewertung eintragen:            bewertungEintragen
    Film löschen:                       filmLoeschen

##### Pseudocode für die Applikation
Main.java:  
Scanner erstellen damit der Benutzer Eingaben machen kann  
Erstelle Menü   
Lese User Input  
1 = Filme anzeigen  
2 = Film hinzufügen  
3 = Bewertung eintragen  
4 = Film löschen  
5 = Programm beenden  
Sonst = Fehlermeldung anzeigen  
Scanner schließen  

##### Methode FilmeAnsehen
Fordert alle Filme von FilmRepository an  
Wenn keine Filme vorhanden zeige "Keine Filme vorhanden" am  
Für jeden Film gib aus:  
Film-ID  
Titel  
Erscheinungsjahr  
Genre  
Regisseur  
Schauspieler  
Beschreibung  
IMDB-ID  
Poster  

##### Methode FilmHinzufügen
Frage Benutzer nach Filmtitel  
Wenn Titel leer gib Fehlermeldung aus  
Suche Film über OMDb API  
Wenn Film nicht gefunden gib "Film wurde nicht gefunden" aus  
Zeige gefundene Filmdaten an  
Frage "Film in Datenbank speichern"  
Ja: Speichere Film in Datenbank  
Hole Film-ID  
Für jeden Schauspieler des Films  
Prüfe ob Schauspieler existiert  
Wenn nicht existiert: Erstelle Schauspieler  
Verknüpfe Schauspieler mit Film  
Gib "Film gespeichert aus"  
Sonst: Film nicht speichern  

##### Methode BewertungEintragen
Zeige alle Filme an  
Frage nach Film-ID  
Frage nach Bewertung  
Wenn Bewertung kleiner als 1 oder größer als 5 gibt Fehlermeldung aus  
Frage nach Kommentar  
Speichere Bewertung in Datenbank  
Gib "Bewertung gespeichert" aus  

##### Methode filmLoeschen
Zeige alle Filme an  
Frage nach Film-ID  
Frage "Wirklich löschen?"  
Ja: Lösche Film aus Datenbank  
Lösche verbundene Bewertungen  
Lösche Verknüpfungen zu Schauspielern  
Gib "Film gelöscht" aus  
Sonst: Löschen abbrechen  

